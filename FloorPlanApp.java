import javax.swing.*;
import java.awt.*;
//import java.awt.event.*;
//import java.io.*;
//import java.util.ArrayList;

public class FloorPlanApp extends JFrame 
{
    private CanvasPanel canvasPanel;
    private ControlPanel controlPanel;

    public FloorPlanApp()
    {
        setTitle("2D Floor Plan Application");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.black);
        setLayout(null);

        // Create the Canvas and Control Panels
        canvasPanel = new CanvasPanel();
        controlPanel = new ControlPanel(canvasPanel);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int canvasWidth = (int) (screenSize.width * 0.75);
        int canvasHeight = (int) (screenSize.height * 0.75);
        canvasPanel.setBounds(300,50,canvasWidth,canvasHeight);
        controlPanel.setBounds(50,50,200,canvasHeight);

        // Adding Panels
        add(controlPanel);
        add(canvasPanel);

        pack();
        setVisible(true);
    }

    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> new FloorPlanApp());
    }    
}
