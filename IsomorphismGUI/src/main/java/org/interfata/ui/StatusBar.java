package org.interfata.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Status bar
 * Display only this: "Status: <status>"
 */
public class StatusBar extends JLabel {
    final GraphPanel graphPanel;
    protected String message;

    public StatusBar(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        // padding
        setBorder(new EmptyBorder(5, 10, 5, 10)); // Insets: top, left, bottom, right

        this.setMessage("Ready");
    }

    public void setMessage(String message) {
        this.message = message;
        setText("Status: " + message);
    }

    public void setMessage(String message, Color color) {
        this.message = message;
        setForeground(color);
        setText("Status: " + message);
    }
}
