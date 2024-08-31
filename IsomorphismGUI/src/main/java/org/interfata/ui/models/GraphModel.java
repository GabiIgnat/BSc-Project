package org.interfata.ui.models;

import org.graph4j.Edge;
import org.graph4j.EdgeIterator;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.interfata.GDF.GDFFormatter;
import org.interfata.GDF.GDFParser;
import org.interfata.GDF.models.GDFEdge;
import org.interfata.GDF.models.GDFGraph;
import org.interfata.GDF.models.GDFNode;
import org.interfata.ui.Layout.CircularLayoutAlgorithm;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This is the model for the canvas
 * It represents a graph with nodes and edges
 */
public class GraphModel {
    private Graph<String, String> graph;
    private final Map<Integer, VertexModel> vertexToModelMap = new HashMap<>();
    private final Map<Edge<?>, EdgeModel> edgeToModelMap = new HashMap<>();
    private int width, height;

    public GraphModel(int w, int h) {
        width = w;
        height = h;
        graph = GraphBuilder.empty().estimatedNumVertices(1_000).buildGraph();
    }

    public Graph<String, String> getGraph() {
        return graph;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Map<Integer, VertexModel> getVertexToModelMap() {
        return vertexToModelMap;
    }

    public void draw(Graphics g) {
        for (Edge<?> e : graph.edges()) {
            EdgeModel em = edgeToModelMap.get(e);
            em.draw(g);
        }

        for (int v : graph.vertices()) {
            VertexModel vm = vertexToModelMap.get(v);
            vm.draw(g);
        }
    }

    public int addVertex(int x, int y) {
        int v = graph.addVertex();
        graph.setVertexLabel(v, "");

        VertexModel vertexModel = new VertexModel(v, new Point(x, y));
        vertexToModelMap.put(v, vertexModel);

        return v;
    }

    public String addEdge(int source, int target) {
        if (graph.containsEdge(source, target)) {
            return "Edge already exists";
        }
        graph.addEdge(source, target, "");

        VertexModel sourceModel = vertexToModelMap.get(source);
        VertexModel targetModel = vertexToModelMap.get(target);

        EdgeModel edgeModel = new EdgeModel(sourceModel, targetModel);
        Edge<?> edge = graph.edge(source, target);
        edgeToModelMap.put(edge, edgeModel);

        return null;
    }

    public void clear() {
        graph = GraphBuilder.empty().estimatedNumVertices(1_000).buildGraph();
    }

    public int removeVertex(VertexModel vertexUnderCursor) {
        int v = vertexUnderCursor.getVertexNumber();
        graph.removeVertex(v);

        return v;
    }

    public void setVertexLabel(VertexModel vm, String label) {
        int vertexNumber = vm.getVertexNumber();
        graph.setVertexLabel(vertexNumber, label);

        vm.setLabel(label);
    }

    public void setVertexPosition(int vertexNumber, int x, int y) {
        if (graph.containsVertex(vertexNumber)) {
            VertexModel vm = vertexToModelMap.get(vertexNumber);
            vm.setPosition(x, y);
        }
    }

    public void setVertexColor(int vertexNumber, Color color) {
        if (graph.containsVertex(vertexNumber)) {
            VertexModel vm = vertexToModelMap.get(vertexNumber);
            vm.setColor(color);
        }
    }

    public String removeEdge(EdgeModel edgeUnderCursor) {
        int source = edgeUnderCursor.S.getVertexNumber();
        int target = edgeUnderCursor.T.getVertexNumber();

        graph.removeEdge(source, target);

        return "{" + source + ", " + target + "}";
    }

    public void setEdgeLabel(EdgeModel em, String label) {
        int source = em.S.getVertexNumber();
        int target = em.T.getVertexNumber();

        graph.setEdgeLabel(source, target, label);

        em.setLabel(label);
    }

    public void setEdgeWeight(EdgeModel em, Double weight) {
        int source = em.S.getVertexNumber();
        int target = em.T.getVertexNumber();

        graph.setEdgeWeight(source, target, weight);

        em.setWeight(weight);
    }

    public VertexModel findVertexUnderCursor(int mx, int my) {
        for (int v : graph.vertices()) {
            VertexModel vm = vertexToModelMap.get(v);

            if (vm.isUnderCursor(mx, my)) {
                return vm;
            }
        }

        return null;
    }

    public EdgeModel findEdgeUnderCursor(int mx, int my) {
        EdgeIterator it = graph.edgeIterator();

        while (it.hasNext()) {
            Edge<?> e = it.next();
            EdgeModel em = edgeToModelMap.get(e);
            if (em.isUnderCursor(mx, my)) {
                return em;
            }
        }



        return null;
    }

    public void move(int dx, int dy) {
        for (int v : graph.vertices()) {
            VertexModel vm = vertexToModelMap.get(v);
            vm.move(dx, dy);
        }
    }

    public void zoom(double zoomFactor, int zoomCenterX, int zoomCenterY) {
        for (int v : graph.vertices()) {
            VertexModel vm = vertexToModelMap.get(v);
            vm.zoom(zoomFactor, zoomCenterX, zoomCenterY);
        }
    }

    public void reset() {
        for (int v : graph.vertices()) {
            VertexModel vm = vertexToModelMap.get(v);
            graph.setVertexLabel(v, "");
            vm.reset();
        }

        for (Edge<?> e : graph.edges()) {
            EdgeModel em = edgeToModelMap.get(e);
            graph.setEdgeLabel(e.source(), e.target(), "");
            em.reset();
        }
    }

    public String loadGraph(String filePath) {
        GDFGraph gdfGraph;
        try {
            gdfGraph = GDFParser.parse(filePath);
        } catch (IOException e) {
            return e.getMessage();
        }

        graph = GraphBuilder.empty().buildGraph();
        vertexToModelMap.clear();
        edgeToModelMap.clear();

        boolean allNodesHaveCoordinates = gdfGraph.allNodesHaveCoordinates();

        // Add vertices
        for (GDFNode node : gdfGraph.nodes()) {
            int vertexNumber = node.name();
            String label = node.label();
            int x = Objects.requireNonNullElse(node.x(), 0).intValue();
            int y = Objects.requireNonNullElse(node.y(), 0).intValue();
            Point position = new Point(x, y);
            Color color = node.color();
            Integer radius = node.width().intValue();

            if (!graph.containsVertex(vertexNumber)) {
                graph.addVertex(vertexNumber);

                if (label != null) {
                    graph.setVertexLabel(vertexNumber, label);
                }

                VertexModel vm = new VertexModel(vertexNumber, position, label, color, radius);
                vertexToModelMap.put(vertexNumber, vm);
            }
        }

        // Add edges
        for (GDFEdge edge : gdfGraph.edges()) {
            int node1 = edge.node1();
            int node2 = edge.node2();
            if (!graph.containsVertex(node1) || !graph.containsVertex(node2)) {
                return "Edge has a node that does not exist";
            }

            Double weight = edge.weight();
            String label = edge.label();
            Color color = edge.color();

            if (!graph.containsEdge(node1, node2)) {
                graph.addEdge(node1, node2, weight);
                if (label != null) {
                    graph.setEdgeLabel(node1, node2, label);
                }

                VertexModel source = vertexToModelMap.get(node1);
                VertexModel target = vertexToModelMap.get(node2);

                EdgeModel em = new EdgeModel(source, target, color, label, weight);
                edgeToModelMap.put(graph.edge(node1, node2), em);
            }
        }

        // If not all nodes have coordinates, layout the graph
        if (!allNodesHaveCoordinates) {
            CircularLayoutAlgorithm.layout(this, true);
        }

        return null;
    }

    public String saveGraph(String filePath) {
        // create a GDFGraph object
        GDFNode[] nodes = new GDFNode[graph.numVertices()];
        GDFEdge[] edges = new GDFEdge[(int) graph.numEdges()];

        int i = 0;
        for (int v : graph.vertices()) {
            VertexModel vm = vertexToModelMap.get(v);
            nodes[i++] = new GDFNode(v, vm.label, (double) vm.p.x, (double) vm.p.y, vm.getColor(), (double) vm.r);
        }

        i = 0;
        for (Edge<?> e : graph.edges()) {
            EdgeModel em = edgeToModelMap.get(e);
            int source = em.S.getVertexNumber();
            int target = em.T.getVertexNumber();
            edges[i++] = new GDFEdge(source, target, em.weight, em.label, em.color);
        }

        GDFGraph gdfGraph = new GDFGraph(nodes, edges);
        try {
            GDFFormatter.save(gdfGraph, filePath);
        } catch (IOException e) {
            return e.getMessage();
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vertices:\n");
        for (int v : graph.vertices()) {
            VertexModel vm = vertexToModelMap.get(v);
            sb.append(vm).append("\n");
        }

        sb.append("Edges:\n");
        for (Edge<?> e : graph.edges()) {
            EdgeModel em = edgeToModelMap.get(e);
            sb.append(em).append("\n");
        }

        return sb.toString();
    }
}
