import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class ControlPanel extends JPanel {
    private CanvasPanel canvasPanel;
    // private Room selectedRoom;
    private ArrayList<Room> rooms = new ArrayList<>();
    int heightmax = 0;
    int x = 50, y = 50;

    public ControlPanel(CanvasPanel canvasPanel) {
        this.canvasPanel = canvasPanel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(250, getHeight()));
        setBackground(Color.DARK_GRAY);

        // Boundary Setting Section
        add(createBoundarySection());

        // Sections for each element category (Rooms, Windows & Doors, Furniture)
        add(createRoomSection());
        add(createSection("Doors"));
        add(createFurnitureSection());
        add(createWindowSection("Windows"));
        addSaveLoadDeleteRotateSection();
    }

    private JPanel createBoundarySection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.GRAY);

        JLabel label = new JLabel("Boundary", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.NORTH);

        JPanel dimensionPanel = new JPanel();
        dimensionPanel.setLayout(new GridLayout(2, 2));
        dimensionPanel.add(new JLabel("Width"));
        JTextField widthField = new JTextField("900");
        dimensionPanel.add(widthField);
        dimensionPanel.add(new JLabel("Height"));
        JTextField heightField = new JTextField("500");
        dimensionPanel.add(heightField);

        panel.add(dimensionPanel, BorderLayout.CENTER);

        JButton setBoundaryButton = new JButton("Set Boundary");
        setBoundaryButton.addActionListener(e -> {
            int width = Integer.parseInt(widthField.getText());
            int height = Integer.parseInt(heightField.getText());
            canvasPanel.setBoundary(width, height);
        });

        panel.add(setBoundaryButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createSection(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.GRAY);

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.NORTH);

        JButton addDoorButton = new JButton("Doors");
        String[] roomSide = { "Top", "Bottom", "Left", "Right" };
        JComboBox<String> doorDropdown = new JComboBox<>(roomSide);
        panel.add(doorDropdown, BorderLayout.NORTH);
        String[] placement = { "Left/Up", "Right/Down", "Center" };
        JComboBox<String> placementDropdown = new JComboBox<>(placement);
        panel.add(placementDropdown, BorderLayout.CENTER);
        addDoorButton.addActionListener(e -> {
            if (canvasPanel.getSelectedRoom() != null) {
                Room selectedRoom = canvasPanel.getSelectedRoom(); // Get the currently selected room
                Rectangle bounds = selectedRoom.getBounds(); // Get the room bounds
        
                int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
                String wall = (String) doorDropdown.getSelectedItem(); // Selected wall
                String place = (String) placementDropdown.getSelectedItem(); // Placement position
                // Adjust placement based on wall and position
                switch (wall) {
                    case "Top":
                        y1 = 0; // Top wall
                        y2 = y1;
                        if (place.equals("Left/Up")) {
                            x1 = 0;
                            x2 = x1 + 30;
                        } else if (place.equals("Right/Down")) {
                            x2 = bounds.width;
                            x1 = x2 - 30;
                        } else { // Center
                            x1 = (bounds.width / 2) - 15;
                            x2 = x1 + 30;
                        }
                        break;
        
                    case "Bottom":
                        y1 = bounds.height - 1; // Bottom wall
                        y2 = y1;
                        if (place.equals("Left/Up")) {
                            x1 = 0;
                            x2 = x1 + 30;
                        } else if (place.equals("Right/Down")) {
                            x2 = bounds.width;
                            x1 = x2 - 30;
                        } else { // Center
                            x1 = (bounds.width / 2) - 15;
                            x2 = x1 + 30;
                        }
                        break;
        
                    case "Left":
                        x1 = 0; // Left wall
                        x2 = 0;
                        if (place.equals("Left/Up")) {
                            y1 = 0;
                            y2 = y1 + 30;
                        } else if (place.equals("Right/Down")) {
                            y2 =  bounds.height;
                            y1 = y2 - 30;
                        } else { // Center
                            y1 = (bounds.height / 2) - 15;
                            y2 = y1 + 30;
                        }
                        break;
        
                    case "Right":
                        x1 = bounds.width - 1; // Right wall
                        x2 = x1;
                        if (place.equals("Left/Up")) {
                            y1 = 0;
                            y2 = y1 + 30;
                        } else if (place.equals("Right/Down")) {
                            y2 =  bounds.height;
                            y1 = y2 - 30;
                        } else { // Center
                            y1 = (bounds.height / 2) - 15;
                            y2 = y1 + 30;
                        }
                        break;
                }
        
                // Add the door to the selected room
                System.out.println(x1+" "+x2+" "+y1+" "+y2);
                selectedRoom.addDoor(selectedRoom,x1, y1, x2, y2);
                canvasPanel.repaintSelectedRoom();
                selectedRoom=null; // Trigger repaint to update the UI
                x1 = 0; y1 = 0; x2 = 0; y2 = 0;
            } else {
                JOptionPane.showMessageDialog(this, "No room selected to add door.");
            }
        });
        
        panel.add(addDoorButton,BorderLayout.SOUTH);
        return panel;
    }
    private JPanel createWindowSection(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.GRAY);

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.NORTH);

        

        JButton addWindowButton = new JButton("Windows");
        String[] roomSide = { "Top", "Bottom", "Left", "Right" };
        JComboBox<String> windowDropdown = new JComboBox<>(roomSide);
        panel.add(windowDropdown, BorderLayout.NORTH);
        String[] placement = { "Left/Up", "Right/Down", "Center" };
        JComboBox<String> placementDropdown = new JComboBox<>(placement);
        panel.add(placementDropdown, BorderLayout.CENTER);
        addWindowButton.addActionListener(e -> {
            if (canvasPanel.getSelectedRoom() != null) {
                Room selectedRoom = canvasPanel.getSelectedRoom(); // Get the currently selected room
                Rectangle bounds = selectedRoom.getBounds(); // Get the room bounds
        
                int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
                String wall = (String) windowDropdown.getSelectedItem(); // Selected wall
                String place = (String) placementDropdown.getSelectedItem(); // Placement position
                // Adjust placement based on wall and position
                switch (wall) {
                    case "Top":
                        y1 = 0; // Top wall
                        y2 = y1;
                        if (place.equals("Left/Up")) {
                            x1 = 0;
                            x2 = x1 + 30;
                        } else if (place.equals("Right/Down")) {
                            x2 = bounds.width;
                            x1 = x2 - 30;
                        } else { // Center
                            x1 = (bounds.width / 2) - 15;
                            x2 = x1 + 30;
                        }
                        break;
        
                    case "Bottom":
                        y1 = bounds.height - 1; // Bottom wall
                        y2 = y1;
                        if (place.equals("Left/Up")) {
                            x1 = 0;
                            x2 = x1 + 30;
                        } else if (place.equals("Right/Down")) {
                            x2 = bounds.width;
                            x1 = x2 - 30;
                        } else { // Center
                            x1 = (bounds.width / 2) - 15;
                            x2 = x1 + 30;
                        }
                        break;
        
                    case "Left":
                        x1 = 0; // Left wall
                        x2 = 0;
                        if (place.equals("Left/Up")) {
                            y1 = 0;
                            y2 = y1 + 30;
                        } else if (place.equals("Right/Down")) {
                            y2 =  bounds.height;
                            y1 = y2 - 30;
                        } else { // Center
                            y1 = (bounds.height / 2) - 15;
                            y2 = y1 + 30;
                        }
                        break;
        
                    case "Right":
                        x1 = bounds.width - 1; // Right wall
                        x2 = x1;
                        if (place.equals("Left/Up")) {
                            y1 = 0;
                            y2 = y1 + 30;
                        } else if (place.equals("Right/Down")) {
                            y2 =  bounds.height;
                            y1 = y2 - 30;
                        } else { // Center
                            y1 = (bounds.height / 2) - 15;
                            y2 = y1 + 30;
                        }
                        break;
                }
        
                // Add the door to the selected room
                
                selectedRoom.addWindow(selectedRoom,x1, y1, x2, y2);
                canvasPanel.repaintSelectedRoom();
                selectedRoom=null; // Trigger repaint to update the UI
                x1 = 0; y1 = 0; x2 = 0; y2 = 0;
            } else {
                JOptionPane.showMessageDialog(this, "No room selected to add window.");
            }
        });
        
        panel.add(addWindowButton,BorderLayout.SOUTH);
        return panel;
    }
    // Room Section with prompt for each room
    private JPanel createRoomSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.GRAY);

        JLabel label = new JLabel("Rooms", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.NORTH);

        String[] roomTypes = { "Living Room", "Dining Room", "Kitchen", "Bedroom", "Bathroom", "Balcony" };
        JComboBox<String> roomDropdown = new JComboBox<>(roomTypes);
        panel.add(roomDropdown, BorderLayout.CENTER);

        JButton addRoomButton = new JButton("Add Room");
        addRoomButton.addActionListener(e -> {
            if (canvasPanel.boundary != null) {
                String roomType = (String) roomDropdown.getSelectedItem();
                String widthStr = JOptionPane.showInputDialog(panel, "Enter width:");
                String heightStr = JOptionPane.showInputDialog(panel, "Enter height:");

                try {
                    int width = Integer.parseInt(widthStr);
                    int height = Integer.parseInt(heightStr);
                    System.out.println("heightmax = " + heightmax);
                    if (!rooms.isEmpty()) {
                        Room lastRoom = rooms.get(rooms.size() - 1);
                        x = lastRoom.getX() + lastRoom.getWidth(); // Next room position

                        if (x + width > canvasPanel.boundary.getWidth() + 50) {
                            x = 50; // Reset to the first column
                            y = y + heightmax; // Move to the next row
                            heightmax = 0; // Reset max height for the new row
                        }
                    }

                    Room room = new Room(roomType, width, height, x, y);
                    if (checkOverlap(room) == false && canvasPanel.boundary.contains(room.getBounds())) {
                        System.out.println("Adding room at: x=" + x + ", y=" + y);
                        if (height > heightmax) {
                            heightmax = height;
                        }
                        rooms.add(room);
                    }
                    canvasPanel.addRoom(room);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Invalid dimensions entered.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please set the boundary first.");
            }
        });

        panel.add(addRoomButton, BorderLayout.PAGE_END);

        return panel;
    }

    private boolean checkOverlap(Room newRoom) {
        for (Room room : rooms) {
            if (room != newRoom && room.getBounds().intersects(newRoom.getBounds())) {
                return true;
            }
        }
        return false;
    }

    private JPanel createFurnitureSection() {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.setBackground(Color.GRAY);

            JLabel label = new JLabel("Furniture", SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            panel.add(label, BorderLayout.NORTH);

            JButton addFurnitureButton = new JButton("Add Furniture");
            addFurnitureButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg"));
                int returnValue = fileChooser.showOpenDialog(this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    String type;
                    // Pass CanvasPanel reference
                    // Pass CanvasPanel reference
                 Furniture furniture = new Furniture("Custom Furniture", file.getAbsolutePath());
                    canvasPanel.addFurniture(furniture);
                    
                    
                }
            });

            panel.add(addFurnitureButton, BorderLayout.CENTER);
            return panel;
        }

    private JPanel addSaveLoadDeleteRotateSection() 
    {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2,2,25,25));
        panel.setBackground(Color.GRAY);
        
        JButton saveButton = new JButton("Save Plan");
        JButton loadButton = new JButton("Load Plan");
        JButton deleteRoomButton = new JButton("Delete Room");
        JButton rotateButton = new JButton("Rotate Room");

        saveButton.setFocusable(false);
        loadButton.setFocusable(false);
        deleteRoomButton.setFocusable(false);
        rotateButton.setFocusable(false);

        saveButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) 
            {
                canvasPanel.savePlan(fileChooser.getSelectedFile());
            }
        });

        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
            {
                canvasPanel.loadPlan(fileChooser.getSelectedFile());
            }
        });

        deleteRoomButton.addActionListener(e -> {
            canvasPanel.deleteSelectedRoom();
        });

        rotateButton.addActionListener(e -> {
            if (canvasPanel.getSelectedRoom() != null) 
            {
                canvasPanel.getSelectedRoom().rotate();
            } 
            else 
            {
                JOptionPane.showMessageDialog(this, "No room selected to rotate.");
            }
        });

        add(saveButton);
        add(loadButton);
        add(rotateButton);
        add(deleteRoomButton);

        return panel;
    }
}