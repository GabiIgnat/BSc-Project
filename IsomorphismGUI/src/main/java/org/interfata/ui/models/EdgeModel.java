package org.interfata.ui.models;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * This is the class that models an edge.
 * It has a source and a target vertex, a color, a weight and a label.
 */
public class EdgeModel {
    public static final Double[] WEIGHT_VALUES = {0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5 ,4.0, 4.5, 5.0};

    VertexModel S;  // source
    VertexModel T;  // target
    Color color;
    String label;
    Double weight;

    public EdgeModel(VertexModel S, VertexModel T, Color color, String label, Double weight) {
        this.S = S;
        this.T = T;
        this.color = color;
        this.label = Objects.requireNonNullElse(label, "");
        this.weight = Objects.requireNonNullElse(weight, 1.0);
    }

    public EdgeModel(VertexModel S, VertexModel T, String label, Double weight) {
        this(S, T, null, label, weight);
    }

    public EdgeModel(VertexModel S, VertexModel T) {
        this(S, T, "", 1.0);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public VertexModel getSource() {
        return S;
    }

    public VertexModel getTarget() {
        return T;
    }

    public void draw(Graphics g) {
        int xs = S.p.x;
        int ys = S.p.y;
        int xt = T.p.x;
        int yt = T.p.y;

        if (color == null) {
            g.setColor(UIManager.getColor("Label.foreground"));
        }
        else {
            g.setColor(color);
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke((float) (2 * weight)));


        g.drawLine(xs, ys, xt, yt);

        // draw label
        if (!label.isEmpty()) {
            int xm = (xs + xt) / 2;
            int ym = (ys + yt) / 2;

            // Draw label at the midpoint
            drawCenteredString(g, label, xm, ym);
        }
    }

    private void drawCenteredString(Graphics g, String text, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        int rectWidth = textWidth + 4;
        int rectHeight = textHeight + 4;

        int startX = x - textWidth / 2;
        int startY = y + textHeight / 3; // Adjusted for alignment at the baseline

        // Draw a rectangle around the text
        Color background = UIManager.getColor("Panel.background");
        g.setColor(background);
        g.fillRect(x - rectWidth / 2, y - rectHeight / 2, rectWidth, rectHeight);

        if (color == null)
            g.setColor(UIManager.getColor("Label.foreground"));
        else
            g.setColor(color);
        g.drawString(text, startX, startY);
    }

    public boolean isUnderCursor(int mx, int my) {
        int xs = S.p.x;
        int ys = S.p.y;
        int xt = T.p.x;
        int yt = T.p.y;

        if (mx < Math.min(xs, xt) ||
                mx > Math.max(xs, xt) ||
                my < Math.min(ys, yt) ||
                my > Math.max(ys, yt)) {
            return false;
        }

        double line_length = Math.sqrt((xt - xs) * (xt - xs) + (yt - ys) * (yt - ys));
        double distToLine = Math.abs((yt - ys) * mx - (xt - xs) * my + xt * ys - yt * xs) / line_length;

        return distToLine < 5;
    }

    public void move(int dx, int dy) {
        S.move(dx, dy);
        T.move(dx, dy);
    }

    public void reset() {
        color = null;
        label = "";
        weight = 1.0;
    }

    @Override
    public String toString() {
        String color = this.color == null ? "null" : this.color.toString();
        return "Edge from " + S.toString() + " to " + T.toString() + " with label " + label + " and color " + color;
    }
}