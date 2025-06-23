import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*; 
import java.util.ArrayList;

public class CanvasPanel extends JPanel {
    ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<Furniture> furniture = new ArrayList<>();
    
    private Room selectedRoom;
    private Room highlightedRoom;
    public Rectangle boundary;

    public CanvasPanel() {
        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(10, 10));
        setLayout(null);

        // Add Mouse Listener for Drag and Drop functionality
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (boundary == null) {
                    JOptionPane.showMessageDialog(CanvasPanel.this, "Please set the boundary first.");
                    return;
                }
                for (Room room : rooms) {
                    if (room.getBounds().contains(e.getPoint())) {
                        selectedRoom = room;
                        selectedRoom.startDragging(e.getPoint());
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                for (Room room : rooms) {
                    room.setHighlighted(false); // Clear highlight for all rooms
                }
                selectedRoom = null; // Reset selection

                for (Room room : rooms) {
                    if (room.getBounds().contains(e.getPoint())) {
                        selectedRoom = room;
                        selectedRoom.setHighlighted(true);
                        break; // Stop checking other rooms after finding the selected one
                    }
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedRoom != null) {
                    if (!boundary.contains(selectedRoom.getBounds()) || checkOverlap(selectedRoom)) {
                        JOptionPane.showMessageDialog(CanvasPanel.this,
                                "Invalid placement! Room cannot be placed here.");
                        selectedRoom.snapBack();
                    }
                    selectedRoom = null;
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedRoom != null) {
                    selectedRoom.dragTo(e.getPoint());
                    repaint();
                }
            }
        });
    }

    //add furniture
    public void addFurniture(Furniture furn) 
    {
        furniture.add(furn);
        add(furn);
        //setComponentZOrder(furniture, 0);
        repaint();
    }

    // Method to remove furniture
    public void removeFurniture(Furniture furn) 
    {
        furniture.remove(furn);
        remove(furn);
        repaint();
    }

    public void repaintSelectedRoom() 
    {
        if (selectedRoom != null) {
            selectedRoom.repaint(); // Only repaint the selected room
        }
    }

    public void deleteSelectedRoom() {
        if (selectedRoom != null) {
            rooms.remove(selectedRoom);
            remove(selectedRoom);
            selectedRoom = null;
            repaint();
            JOptionPane.showMessageDialog(this, "Selected room deleted.");
        } else {
            JOptionPane.showMessageDialog(this, "No room selected to delete.");
        }
    }

    // Method to set boundary
    public void setBoundary(int width, int height) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int canvasWidth = (int) (screenSize.width * 0.75);
        int canvasHeight = (int) (screenSize.height * 0.75);
        if (width > canvasWidth || height > canvasHeight) {
            JOptionPane.showMessageDialog(CanvasPanel.this, "Boundary can't be bigger than canvas width");
        } else {
            boundary = new Rectangle(50, 50, width, height);
            repaint();
        }

    }

    public Room getSelectedRoom() {
        return selectedRoom;
    }

    // Method to add rooms within boundary
    public void addRoom(Room room) {
        if (boundary == null) {
            JOptionPane.showMessageDialog(this, "Please set the boundary first.");
            return;
        }
        if (!boundary.contains(room.getBounds())) {
            JOptionPane.showMessageDialog(this, "Room cant be placed outside boundary.");
        }
        if (checkOverlap(room)) {
            JOptionPane.showMessageDialog(this, "Rooms can't overlap.");
        } else if (boundary.contains(room.getBounds()) && checkOverlap(room) == false) {
            rooms.add(room);
            add(room);
            repaint();
        }
    }

    // Overlap Check
    private boolean checkOverlap(Room newRoom) {
        for (Room room : rooms) {
            if (room != newRoom && room.getBounds().intersects(newRoom.getBounds())) {
                return true;
            }
        }
        return false;
    }

    // Paint the boundary rectangle
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int gridCellSize = 50; // Size of each grid cell in pixels
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Draw the grid
        g.setColor(Color.WHITE); // Grid color
        for (int y = 0; y <= panelHeight; y += gridCellSize) {
            g.drawLine(0, y, panelWidth, y); // Horizontal lines
        }
        for (int x = 0; x <= panelWidth; x += gridCellSize) {
            g.drawLine(x, 0, x, panelHeight); // Vertical lines
        }

        // Draw the boundary (if it exists)
        if (boundary != null) {
            g.setColor(Color.BLACK); // Boundary color
            ((Graphics2D) g).draw(boundary); // Draw boundary rectangle
        }
    }

    // Save Plan to File
    public void savePlan(File file) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(boundary);
            out.writeObject(rooms);
            out.writeObject(furniture);
            JOptionPane.showMessageDialog(this, "Plan saved successfully!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving plan.");
        }
    }

    // Load Plan from File
    public void loadPlan(File file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            boundary = (Rectangle) in.readObject();
            rooms = (ArrayList<Room>) in.readObject();
            furniture = (ArrayList<Furniture>) in.readObject();
            removeAll();
            for (Room room : rooms)
                add(room);
            for (Furniture furn : furniture)
                add(furn);
            repaint();
            JOptionPane.showMessageDialog(this, "Plan loaded successfully!");
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error loading plan.");
        }
    }
}