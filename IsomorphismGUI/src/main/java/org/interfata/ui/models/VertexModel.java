package org.interfata.ui.models;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * This is the class that models a vertex
 * It has position, color, radius and a label
 */
public class VertexModel {
    public static final Integer[] RADIUS_VALUES = {4, 8, 12, 16, 20, 24, 28 ,32, 36, 40};
    public static final int DEFAULT_RADIUS = 10;

    protected int vertexNumber;   // the corresponding vertex number
    protected Point p;    // the position of the vertex
    private final Point initialPosition;  // the initial position of the vertex
    protected int r = DEFAULT_RADIUS;  // the radius of the circle
    protected String label;
    protected Color color;

    public VertexModel(int vertexNumber, Point position, String label, Color color, Integer radius) {
        this.vertexNumber = vertexNumber;
        this.p = position;
        this.initialPosition = new Point(position.x, position.y);
        this.label = Objects.requireNonNullElse(label, "");
        this.color = color;
        if (radius != null)
            this.r = radius;
    }
    public VertexModel(int vertexNumber, Point position, String label, Color color) {
        this(vertexNumber, position, label, color, null);
    }

    public VertexModel(int vertexNumber, Point position) {
        this(vertexNumber, position, "", null);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setPosition(int x, int y) {
        p.x = x;
        p.y = y;
    }

    public void setInitialPosition(int x, int y) {
        initialPosition.x = x;
        initialPosition.y = y;

        setPosition(x, y);
    }

    public Point getPosition() {
        return p;
    }

    public Color getColor() {
        return color;
    }

    public int getVertexNumber() {
        return vertexNumber;
    }

    public void draw(Graphics g) {
        Color background = UIManager.getColor("Panel.background");

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));

        // Draw the string label
        String labelToDraw = vertexNumber + "";
        if (!label.isEmpty()) {
            labelToDraw = label;
        }

        // determine the radius such that the label fits inside the circle
        FontMetrics fm = g.getFontMetrics();
        int labelWidth = fm.stringWidth(labelToDraw);
        int labelHeight = fm.getHeight();
        int labelDiameter = Math.max(labelWidth, labelHeight);
        double r1 = Math.max(r, labelDiameter / 2 + 5);

        if (r1 > r) {
            r = (int) r1;
        }

        g.setColor(background);
        g.fillOval(p.x - r, p.y - r, r + r, r + r);

        if (color == null) {
            g.setColor(UIManager.getColor("Label.foreground"));
        }
        else {
            g.setColor(color);
        }
        g.drawOval(p.x - r, p.y - r, r + r, r + r);

        // get the width, height of the string label
        // Calculate the coordinates to center the label within the circle
        int labelX = p.x - labelWidth / 2;
        int labelY = p.y + labelHeight / 3; // Adjust the position based on the font metrics

        // Draw the string label
        g.drawString(labelToDraw, labelX, labelY);
    }

    public boolean isUnderCursor(int mx, int my) {
        int a = p.x - mx;
        int b = p.y - my;

        return a * a + b * b <= r * r;
    }

    public void move(int dx, int dy) {
        p.x += dx;
        p.y += dy;
    }

    public void zoom(double zoomFactor, int zoomCenterX, int zoomCenterY) {
        p.x = (int) (zoomFactor * (p.x - zoomCenterX) + zoomCenterX);
        p.y = (int) (zoomFactor * (p.y - zoomCenterY) + zoomCenterY);
    }

    public void setRadius(int radius) {
        if (radius < RADIUS_VALUES[0]) {
            r = RADIUS_VALUES[0];
        } else if (radius > RADIUS_VALUES[RADIUS_VALUES.length - 1]) {
            r = RADIUS_VALUES[RADIUS_VALUES.length - 1];
        } else {
            r = radius;
        }
    }

    public void reset() {
        this.color = null;
        this.r = DEFAULT_RADIUS;
        this.label = "";

        p.x = initialPosition.x;
        p.y = initialPosition.y;
    }

    @Override
    public String toString() {
        String color = this.color == null ? "null" : this.color.toString();
        return "(v=" + vertexNumber + "; " +  p.x + ", " + p.y + ")  label: " + label + " color: " + color + " radius: " + r;
    }
}
