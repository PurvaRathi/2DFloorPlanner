import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class Furniture extends JLabel implements Serializable 
{
    private String type;
    private ImageIcon icon;
    private double rotationAngle = 0;
    private Point startDragPoint;
    private int initialX, initialY;
    private CanvasPanel canvasPanel;

    public Furniture(String type, String imagePath) {
        this.type = type;
        this.icon = new ImageIcon(imagePath);
        setIcon(new ImageIcon(icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        setSize(50, 50);
        setLayout(null);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Record initial position
                initialX = getX();
                initialY = getY();
                startDragPoint = new Point(e.getX(), e.getY());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Right-click to delete
                if (SwingUtilities.isRightMouseButton(e)) {
                    int response = JOptionPane.showConfirmDialog(
                        canvasPanel,
                        "Do you want to delete this furniture?",
                        "Delete Confirmation",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (response == JOptionPane.YES_OPTION) {
                        canvasPanel.removeFurniture(Furniture.this);
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Update position during drag
                int newX = getX() + e.getX() - startDragPoint.x;
                int newY = getY() + e.getY() - startDragPoint.y;
                setLocation(newX, newY);
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // Validate drop position
                if (!isWithinBoundary(getBounds())) {
                    // Snap back if out of bounds
                    setLocation(initialX, initialY);
                    JOptionPane.showMessageDialog(canvasPanel, "Invalid position! Furniture must stay within the boundary or room.");
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.rotate(rotationAngle, getWidth() / 2, getHeight() / 2);
        super.paintComponent(g2d);
        g2d.dispose();
    }

    private boolean isWithinBoundary(Rectangle furnitureBounds) {
        // Check if furniture is within the boundary
        if (canvasPanel.boundary != null && !canvasPanel.boundary.contains(furnitureBounds)) {
            return false;
        }
        // Check if furniture is within any room
        for (Room room : canvasPanel.rooms) {
            if (room.getBounds().contains(furnitureBounds)) {
                return true;
            }
        }
        return false;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public double getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(double rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public Point getStartDragPoint() {
        return startDragPoint;
    }

    public void setStartDragPoint(Point startDragPoint) {
        this.startDragPoint = startDragPoint;
    }

    public int getInitialX() {
        return initialX;
    }

    public void setInitialX(int initialX) {
        this.initialX = initialX;
    }

    public int getInitialY() {
        return initialY;
    }

    public void setInitialY(int initialY) {
        this.initialY = initialY;
    }

    public CanvasPanel getCanvasPanel() {
        return canvasPanel;
    }

    public void setCanvasPanel(CanvasPanel canvasPanel) {
        this.canvasPanel = canvasPanel;
    }
}