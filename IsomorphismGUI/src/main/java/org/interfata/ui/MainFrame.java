package org.interfata.ui;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * The main frame of the application
 * It has a menu bar at top, a panel in the left for displaying the mapping of the isomorphic graphs and 2 drawing panel in the center and right
 */
public class MainFrame extends JFrame {
    public MainFrame() {
        super("Graph Isomorphism Tester");

        // set logo icon
//        try {
//            ImageIcon icon = new ImageIcon("logo5.png");
//            setIconImage(icon.getImage());
//        } catch (Exception e) {
//            System.out.println("Failed to set logo icon");
//        }

        init();
    }

    private void init() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1600, 800));

        GraphPanel graphPanel1 = new GraphPanel(this);
        GraphPanel graphPanel2 = new GraphPanel(this);

        graphPanel1.setBorder(new TitledBorder("Graph 1"));
        graphPanel2.setBorder(new TitledBorder("Graph 2"));

        TestPanel testPanel = new TestPanel(this, graphPanel1, graphPanel2);
        testPanel.setBorder(new TitledBorder("Test Isomorphism"));

        CustomMenuBar menuBar = new CustomMenuBar(this, graphPanel1, graphPanel2);
        setJMenuBar(menuBar);

        // Create a new GridBagLayout instance for the main frame
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        // Create GridBagConstraints for the components
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        add(testPanel, c);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.45;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        add(graphPanel1, c);

        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.45;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(10, 10, 10, 10);
        add(graphPanel2, c);

        pack();
    }

    public void setDarkTheme() {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            System.out.println("Failed to set dark theme");
        }
    }

    public void setLightTheme() {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            System.out.println("Failed to set light theme");
        }
    }
}
