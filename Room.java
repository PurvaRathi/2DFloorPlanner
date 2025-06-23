import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class Room extends JPanel implements Serializable {
    private String type;
    private Point lastPosition;
    private ArrayList<Door> doors = new ArrayList<>();
    private ArrayList<Windows> windows = new ArrayList<>();
    private boolean isHighlighted = false;
    private int rotation = 0;
    int x, y, width, height;

    public Room(String type, int width, int height, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        setSize(width, height);
        setBackground(getRoomColor(type));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setLayout(null);
        setLocation(x, y);
    }

    public Shape getShape() {
        return new Rectangle(x, y, width, height);
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
        if (isHighlighted)
            setBorder(BorderFactory.createLineBorder(Color.RED, 3)); // Highlight with a WHITE border

        else
            setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Default border

        repaint();
    }

    public void rotate() {
        rotation = (rotation + 90) % 360;

        // Swap width and height
        int currentWidth = getWidth();
        int currentHeight = getHeight();
        setSize(currentHeight, currentWidth);
        // if(CanvasPanel.checkOverlap(new Room()))

        // Adjust position to rotate around the center
        setLocation(getX() + (currentWidth - currentHeight) / 2, getY() + (currentHeight - currentWidth) / 2);

        repaint();
    }

    private Color getRoomColor(String type) {
        switch (type) {
            case "Bedroom":
                return Color.GREEN;
            case "Bathroom":
                return Color.BLUE;
            case "Kitchen":
                return Color.RED;
            case "Dining Room":
                return Color.ORANGE;
            case "Living Room":
                return Color.YELLOW;
            case "Balcony":
                return Color.CYAN;
            default:
                return Color.GRAY;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw black border as wall
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(6)); // Make walls thicker
        drawWalls(g2d);

        g2d.setColor(Color.WHITE); // Doors appear as gaps
        g2d.setStroke(new BasicStroke(6));
        for (Door door : doors) {
            g2d.drawLine(door.x1, door.y1, door.x2, door.y2);
        }

        g2d.setColor(Color.WHITE); // Windows in blue dashed lines
        g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 10 }, 0));
        for (Windows window : windows) {
            g2d.drawLine(window.x1, window.y1, window.x2, window.y2);
        }
    }

    // Draw walls with gaps for doors and windows
    private void drawWalls(Graphics2D g2d) {
        int w = getWidth();
        int h = getHeight();

        // Top wall
        drawWallWithGaps(g2d, 0, 0, w, 0, false);

        // Right wall
        drawWallWithGaps(g2d, w - 1, 0, w - 1, h, true);

        // Bottom wall
        drawWallWithGaps(g2d, 0, h - 1, w, h - 1, false);

        // Left wall
        drawWallWithGaps(g2d, 0, 0, 0, h, true);
    }

    private void drawWallWithGaps(Graphics2D g2d, int x1, int y1, int x2, int y2, boolean vertical) {
        int gapStart, gapEnd;

        // Draw initial wall section (if no gaps exist, entire wall is drawn)
        g2d.drawLine(x1, y1, x2, y2);

        // Check for doors and windows along this wall
        for (Door door : doors) {
            if (vertical && door.x1 == x1 || vertical && door.x2 == x2) { // Vertical wall
                gapStart = Math.min(door.y1, door.y2);
                gapEnd = Math.max(door.y1, door.y2);
                g2d.clearRect(x1 - 3, gapStart, 6, gapEnd - gapStart);
                // repaint(); // Clear space for door
            } else if (!vertical && door.y1 == y1 || !vertical && door.y2 == y2) { // Horizontal wall
                gapStart = Math.min(door.x1, door.x2);
                gapEnd = Math.max(door.x1, door.x2);
                g2d.clearRect(gapStart, y1 - 3, gapEnd - gapStart, 6);
                // repaint();
            }
        }
        // doors.clear();
    }

    public void addDoor(Room room, int x1, int y1, int x2, int y2) {
        // check for overlap and add to the list of doors and repaint
        if (room == null) {
            JOptionPane.showMessageDialog(this, "No room selected.");
            return;
        }

        // Check if it's a Bedroom or Bathroom with a door outside
        if ((room.getType().equals("Bedroom") || room.getType().equals("Bathroom"))
                && isDoorLeadingOutside(room, x1, y1, x2, y2)) {
            JOptionPane.showMessageDialog(this, "Bedroom or Bathroom cannot have a door to the outside.");
            return;
        }

        // Check for overlapping doors/windows
        if (room.isOverlapWithExisting(x1, y1, x2, y2)) {
            JOptionPane.showMessageDialog(this, "Door cannot overlap with existing doors or windows.");
            return;
        }

        doors.add(new Door(x1, y1, x2, y2));
        repaint();

    }

    public void addWindow(Room room, int x1, int y1, int x2, int y2) {
        if (room == null) {
            JOptionPane.showMessageDialog(this, "No room selected.");
            return;
        }

        // Check if a window is placed between two rooms
        /*if (isBetweenTwoRooms(room, x1, y1, x2, y2)) {
            JOptionPane.showMessageDialog(this, "Windows cannot be placed between rooms.");
            return;
        }*/

        // Check for overlapping doors/windows
        if (room.isOverlapWithExisting(x1, y1, x2, y2)) {
            JOptionPane.showMessageDialog(this, "Window cannot overlap with existing doors or windows.");
            return;
        }
        windows.add(new Windows(x1, y1, x2, y2));
        repaint();
    }
    /*public boolean isBetweenTwoRooms(Room room, int x1, int y1, int x2, int y2) {
        for (Room otherRoom : rooms) {
            if (otherRoom != room && otherRoom.getBounds().intersects(new Rectangle(x1, y1, x2 - x1, y2 - y1))) {
                return true;
            }
        }
        return false;
    }*/

    public boolean isOverlapWithExisting(int x1, int y1, int x2, int y2) {
        Rectangle newElement = new Rectangle(x1, y1, x2 - x1, y2 - y1);
        for (Door door : doors) {
            if (door.intersects(newElement)) {
                return true;
            }
        }
        for (Windows window : windows) {
            if (window.intersects(newElement)) {
                return true;
            }
        }
        return false;
    }

    public String getType() {
        return type; // Assuming `type` is a field in Room storing room type
    }

    // In CanvasPanel
    

    private boolean isDoorLeadingOutside(Room room, int x1, int y1, int x2, int y2) {
        Rectangle boundary = new Rectangle(0, 0, getWidth(), getHeight());
        Rectangle doorRect = new Rectangle(x1, y1, x2 - x1, y2 - y1);
        return !boundary.contains(doorRect); // Door rectangle extends outside the canvas
    }

    public void startDragging(Point startPoint) {
        lastPosition = getLocation();
    }

    public void dragTo(Point newPoint) {
        setLocation(newPoint.x - getWidth() / 2, newPoint.y - getHeight() / 2);
    }

    public void snapBack() {
        setLocation(lastPosition);
    }
}
