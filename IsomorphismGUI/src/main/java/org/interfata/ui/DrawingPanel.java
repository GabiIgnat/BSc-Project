package org.interfata.ui;

import org.graph4j.Graph;
import org.interfata.ui.Layout.CircularLayoutAlgorithm;
import org.interfata.ui.Layout.GridLayoutAlgorithm;
import org.interfata.ui.Layout.RandomLayoutAlgorithm;
import org.interfata.ui.Layout.TreeLayoutAlgorithm;
import org.interfata.ui.models.EdgeModel;
import org.interfata.ui.models.GraphModel;
import org.interfata.ui.models.VertexModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DrawingPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private static final int MAX_ZOOM_IN = 40;
    private static final int MAX_ZOOM_OUT = 40;
    private final StatusBar status;
    private final OperationsBar toolBar;
    private final GraphModel graphModel;
    private VertexModel vertexUnderCursor;
    private EdgeModel edgeUnderCursor;
    private int mouseX, mouseY;
    private boolean mouseLeftButton = false;
    private boolean mouseRightButton = false;
    private VertexModel newEdgeSource;
    private boolean chooseTarget = false;
    private int numZoomIn = 0;
    private int numZoomOut = 0;

    public DrawingPanel(StatusBar status, OperationsBar toolBar) {
        this.status = status;
        this.toolBar = toolBar;

        graphModel = new GraphModel(getWidth(), getHeight());
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

        // add resize listener
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                graphModel.setSize(getWidth(), getHeight());
                repaint();
            }
        });

        init();
    }

    private void init() {
        setPreferredSize(new Dimension(600, 800));
        setBorder(BorderFactory.createEtchedBorder());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // set antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphModel.draw(g2d);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseLeftButton = true;

            switch (toolBar.getOperation()) {
                case ADD:
                    if (chooseTarget)
                        finalizeAddEdge();
                    else if (vertexUnderCursor == null) {
                        createNewVertex(e.getX(), e.getY());
                    } else {
                        initializeAddEdge(vertexUnderCursor);
                    }
                    break;
                case DELETE:
                    if (vertexUnderCursor != null) {
                        removeVertex(vertexUnderCursor);
                    } else if (edgeUnderCursor != null) {
                        removeEdge(edgeUnderCursor);
                    }
                    break;
            }
        }

        if (e.getButton() == MouseEvent.BUTTON3) {
            mouseRightButton = true;
        }

        setMouseCursor(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseLeftButton = false;

            if (toolBar.getOperation() == Operation.EDIT) {
                if (vertexUnderCursor != null) {
                    editVertexPopupMenu(e, vertexUnderCursor);
                } else if (edgeUnderCursor != null) {
                    editEdgePopupMenu(e, edgeUnderCursor);
                }
            }
        }

        if (e.getButton() == MouseEvent.BUTTON3) {
            mouseRightButton = false;
        }

        setMouseCursor(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseRightButton) {
            moveGraphDrag(e.getX(), e.getY());
        } else {
            setMouseCursor(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        setMouseCursor(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int zoomCenterX = e.getX();
        int zoomCenterY = e.getY();
        double zoomFactor = 1.0;

        if (e.getWheelRotation() < 0) {
            // Zoom in
            if (numZoomIn < MAX_ZOOM_IN) {
                numZoomIn++;
                numZoomOut--;
                zoomFactor = 1.1;
            }
        } else {
            // Zoom out
            if (numZoomOut < MAX_ZOOM_OUT) {
                numZoomOut++;
                numZoomIn--;
                zoomFactor = 0.9;
            }
        }

        zoomGraph(zoomFactor, zoomCenterX, zoomCenterY);
    }

    public void setMouseCursor(MouseEvent e) {
        if (e != null) {
            vertexUnderCursor = graphModel.findVertexUnderCursor(e.getX(), e.getY());

            if (vertexUnderCursor == null) {
                edgeUnderCursor = graphModel.findEdgeUnderCursor(e.getX(), e.getY());
            }

            mouseX = e.getX();
            mouseY = e.getY();
        }

        int mouseCursor;
        if (vertexUnderCursor != null) {
            mouseCursor = Cursor.HAND_CURSOR;
        } else if (edgeUnderCursor != null) {
            mouseCursor = Cursor.CROSSHAIR_CURSOR;
        } else if (chooseTarget) {
            mouseCursor = Cursor.WAIT_CURSOR;
        } else if (mouseRightButton) {
            mouseCursor = Cursor.MOVE_CURSOR;
        } else {
            mouseCursor = Cursor.DEFAULT_CURSOR;
        }

        setCursor(Cursor.getPredefinedCursor(mouseCursor));
    }

    private void moveGraphDrag(int mx, int my) {
        int dx = mx - mouseX;
        int dy = my - mouseY;

        String msg;

        if (vertexUnderCursor != null) {
            vertexUnderCursor.move(dx, dy);
            msg = "Vertex moved successfully";
        } else if (edgeUnderCursor != null) {
            edgeUnderCursor.move(dx, dy);
            msg = "Edge moved successfully";
        } else {
            graphModel.move(dx, dy);
            msg = "Graph moved successfully";
        }

        mouseX = mx;
        mouseY = my;

        status.setMessage(msg, Color.BLUE);

        repaint();
    }

    private void zoomGraph(double zoomFactor, int zoomCenterX, int zoomCenterY) {
        graphModel.zoom(zoomFactor, zoomCenterX, zoomCenterY);

        status.setMessage("Graph zoomed successfully", Color.BLUE);

        repaint();
    }

    private void createNewVertex(int mx, int my) {
        int v = graphModel.addVertex(mx, my);

        status.setMessage("Vertex " + v + " added successfully", Color.GREEN);
        repaint();
    }

    private void removeVertex(VertexModel vm) {
        int v = graphModel.removeVertex(vm);

        status.setMessage("Vertex " + v + " removed successfully", Color.BLUE);
        repaint();
    }

    private void initializeAddEdge(VertexModel vm) {
        if (vertexUnderCursor != null) {
            newEdgeSource = vm;
            vertexUnderCursor = null;
            chooseTarget = true;

            status.setMessage("Choose target vertex", Color.BLUE);
        }
    }

    private void finalizeAddEdge() {
        if (chooseTarget) {
            if (vertexUnderCursor != null) {
                System.out.println("[info] vertexUnderCursor: " + vertexUnderCursor);
                if (vertexUnderCursor.equals(newEdgeSource)) {
                    status.setMessage("Cannot connect a vertex to itself", Color.gray);
                } else {
                    int source = newEdgeSource.getVertexNumber();
                    int target = vertexUnderCursor.getVertexNumber();
                    String msg = graphModel.addEdge(source, target);

                    if (msg != null) {
                        status.setMessage(msg, Color.RED);
                    } else {
                        status.setMessage("Edge {" + source + ", " + target + "} added successfully", Color.GREEN);
                    }

                    repaint();
                }
            } else {
                status.setMessage("Cancelled adding edge", Color.BLUE);
            }

            chooseTarget = false;
        }
    }

    private void removeEdge(EdgeModel em) {
        String edge = graphModel.removeEdge(em);

        status.setMessage("Edge " + edge + " removed successfully", Color.BLUE);

        repaint();
    }

    private void changeVertexRadius(VertexModel vm) {
        int radius = (Integer) JOptionPane.showInputDialog(this, "Choose radius:", "Edit vertex", JOptionPane.QUESTION_MESSAGE, null, VertexModel.RADIUS_VALUES, VertexModel.RADIUS_VALUES[0]);

        vm.setRadius(radius);
        status.setMessage("Vertex " + vm.getVertexNumber() + " radius changed successfully to " + radius, Color.BLUE);

        repaint();
    }

    private void changeVertexColor(VertexModel vm) {
        Color color = JColorChooser.showDialog(this, "Choose new color", vm.getColor());

        vm.setColor(color);
        status.setMessage("Vertex " + vm.getVertexNumber() + " color changed successfully", Color.BLUE);

        repaint();
    }

    private void changeVertexLabel(VertexModel vm) {
        String label = JOptionPane.showInputDialog(this, "Input text:", "Edit vertex", JOptionPane.QUESTION_MESSAGE);

        graphModel.setVertexLabel(vm, label);
        status.setMessage("Vertex " + vm.getVertexNumber() + " label changed successfully to '" + label + "'", Color.BLUE);

        repaint();
    }

    private void editVertexPopupMenu(MouseEvent e, VertexModel vm){
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem changeNodeRadiusMenuItem = new JMenuItem("Change vertex size");
        popupMenu.add(changeNodeRadiusMenuItem);
        changeNodeRadiusMenuItem.addActionListener((action)-> changeVertexRadius(vm));

        popupMenu.addSeparator();
        JMenuItem changeNodeColorMenuItem = new JMenuItem("Change vertex color");
        popupMenu.add(changeNodeColorMenuItem);
        changeNodeColorMenuItem.addActionListener((action)-> changeVertexColor(vm));

        popupMenu.addSeparator();
        JMenuItem changeLabelMenuItem = new JMenuItem("Change vertex label");
        popupMenu.add(changeLabelMenuItem);
        changeLabelMenuItem.addActionListener((action)-> changeVertexLabel(vm));

        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void changeEdgeWeight(EdgeModel em) {
        Double weight = (Double) JOptionPane.showInputDialog(this, "Choose weight:",
                "Edit edge", JOptionPane.QUESTION_MESSAGE, null, EdgeModel.WEIGHT_VALUES, EdgeModel.WEIGHT_VALUES[0]);

        graphModel.setEdgeWeight(em, weight);
        String edge = "{" + em.getSource().getVertexNumber() + ", " + em.getTarget().getVertexNumber() + "}";
        status.setMessage("Edge " + edge + " weight changed successfully to " + weight, Color.BLUE);

        repaint();
    }

    private void changeEdgeColor(EdgeModel em){
        Color color = JColorChooser.showDialog(this, "Choose color", Color.BLACK);

        em.setColor(color);
        String edge = "{" + em.getSource().getVertexNumber() + ", " + em.getTarget().getVertexNumber() + "}";
        status.setMessage("Edge " + edge + " color changed successfully", Color.BLUE);

        repaint();
    }

    private void changeEdgeLabel(EdgeModel em) {
        String label = JOptionPane.showInputDialog(this, "Input text:", "Edit edge", JOptionPane.QUESTION_MESSAGE);

        graphModel.setEdgeLabel(em, label);
        String edge = "{" + em.getSource().getVertexNumber() + ", " + em.getTarget().getVertexNumber() + "}";
        status.setMessage("Edge " + edge + " label changed successfully to '" + label + "'", Color.BLUE);

        repaint();
    }

    private void editEdgePopupMenu(MouseEvent event, EdgeModel em) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem changeEdgeWeightMenuItem = new JMenuItem("Change edge weight");
        popupMenu.add(changeEdgeWeightMenuItem);
        changeEdgeWeightMenuItem.addActionListener((action)-> changeEdgeWeight(em));

        popupMenu.addSeparator();
        JMenuItem changeEdgeColorMenuItem = new JMenuItem("Change edge color");
        popupMenu.add(changeEdgeColorMenuItem);
        changeEdgeColorMenuItem.addActionListener((action)-> changeEdgeColor(em));

        popupMenu.addSeparator();
        JMenuItem changeLabelMenuItem = new JMenuItem("Change edge label");
        popupMenu.add(changeLabelMenuItem);
        changeLabelMenuItem.addActionListener((action)-> changeEdgeLabel(em));

        popupMenu.show(event.getComponent(), event.getX(), event.getY());
    }

    public void reset() {
        graphModel.reset();

        // Reset to zoom 0
        numZoomIn = numZoomOut = 0;
        status.setMessage("Graph reset successfully(zoom, move, color, label)", Color.BLUE);

        repaint();
    }

    public void clear() {
        graphModel.clear();
        status.setMessage("Graph cleared successfully", Color.BLUE);

        repaint();
    }

    public void randomLayout() {
        RandomLayoutAlgorithm.layout(graphModel, false);
        status.setMessage("Graph layout changed to random", Color.BLUE);

        repaint();
    }

    public void circularLayout() {
        CircularLayoutAlgorithm.layout(graphModel, false);
        status.setMessage("Graph layout changed to circular", Color.BLUE);

        repaint();
    }

    public void gridLayout() {
        GridLayoutAlgorithm.layout(graphModel, false);
        status.setMessage("Graph layout changed to grid", Color.BLUE);

        repaint();
    }

    public void treeLayout() {
        TreeLayoutAlgorithm.layout(graphModel, false);
        status.setMessage("Graph layout changed to tree", Color.BLUE);

        repaint();
    }

    public void loadGraph(String jsonPath) {
        String msg = graphModel.loadGraph(jsonPath);

        if (msg != null) {
            status.setMessage(msg, Color.RED);
        } else {
            status.setMessage("Graph loaded successfully", Color.GREEN);
        }

        repaint();
    }

    public void saveGraph(String gdfPath) {
        String msg = graphModel.saveGraph(gdfPath);

        if (msg != null) {
            status.setMessage(msg, Color.RED);
        } else {
            status.setMessage("Graph saved successfully", Color.GREEN);
        }
    }

    public Graph<String, String> getGraph() {
        return graphModel.getGraph();
    }

    public GraphModel getGraphModel() {
        return graphModel;
    }

    public void setPosition(int vertexNumber, int x, int y) {
        graphModel.setVertexPosition(vertexNumber, x, y);

        repaint();
    }

    public void setVertexColor(int vertexNumber, Color color) {
        graphModel.setVertexColor(vertexNumber, color);

        repaint();
    }

    @Override
    public String toString() {
        return "DrawingPanel{" +
                "graphModel=" + graphModel +
                "} " + super.toString();
    }
}
