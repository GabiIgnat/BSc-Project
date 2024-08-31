package org.interfata.ui;

import org.graph4j.Graph;
import org.interfata.ui.models.GraphModel;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * GraphPanel
 * Has: title on NORTH, graph on CENTER, buttons on EAST and status bar on SOUTH
 */
public class GraphPanel extends JPanel {
    final MainFrame frame;
    DrawingPanel canvas;
    OperationsBar toolBar;
    StatusBar status;

    public GraphPanel(MainFrame frame) {
        setPreferredSize(new Dimension(400, 800));

        this.frame = frame;

        setLayout(new BorderLayout());

        toolBar = new OperationsBar(this);
        status = new StatusBar(this);
        canvas = new DrawingPanel(status, toolBar);

        canvas.setBorder(new BevelBorder(BevelBorder.LOWERED));

        add(toolBar, BorderLayout.EAST);
        add(status, BorderLayout.SOUTH);
        add(canvas, BorderLayout.CENTER);
    }

    @Override
    public String toString() {
        return "GraphPanel{" +
                "canvas=" + canvas +
                "} " + super.toString();
    }

    public Graph<String, String> getGraph() {
        return canvas.getGraph();
    }

    public GraphModel getGraphModel() {
        return canvas.getGraphModel();
    }

    public void setPosition(int vertexNumber, int x, int y) {
        canvas.setPosition(vertexNumber, x, y);
    }

    public void setVertexColor(int vertexNumber, Color color) {
        canvas.setVertexColor(vertexNumber, color);
    }
}
