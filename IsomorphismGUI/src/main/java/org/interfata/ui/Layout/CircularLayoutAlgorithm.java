package org.interfata.ui.Layout;

import org.graph4j.Graph;
import org.interfata.ui.models.GraphModel;
import org.interfata.ui.models.VertexModel;

import java.util.Map;

public class CircularLayoutAlgorithm {
    public static void layout(GraphModel gm, boolean init) {
        // figure out the radius of the circle, so that all the vertices fit
        // into the drawable area
        // calculate the x and y coordinates of each vertex
        int w = gm.getWidth();
        int h = gm.getHeight();

        Graph<?, ?> g = gm.getGraph();
        Map<Integer, VertexModel> vertexToModelMap = gm.getVertexToModelMap();

        int n = g.numVertices();
        // if the minimum distance between two consecutive vertices is d,
        // then the circumference of the circle is n * d
        // so, the radius of the circle is n * d / (2 * pi)
        // find the best d, in order to fit all the vertices in the drawable area
        // or if d is too small, then increase the radius
        double minDist = 100;

        int r = 100;
        while (true) {
            double d = 2 * Math.PI * r / n;

            if (d >= minDist) {
                break;
            } else {
                r += 25;
            }
        }

        double angleStep = 2 * Math.PI / n;

        int i = 0;
        for (int v : g.vertices()) {
            double x = r * Math.cos(angleStep * i) + (double) w / 2;
            double y = r * Math.sin(angleStep * i) + (double) h / 2;

            VertexModel vm = vertexToModelMap.get(v);

            if (init)
                vm.setInitialPosition((int) x, (int) y);
            else
                vm.setPosition((int) x, (int) y);

            i++;
        }
    }
}
