package org.interfata.ui.Layout;

import org.interfata.ui.models.GraphModel;
import org.interfata.ui.models.VertexModel;
import org.graph4j.util.IntArrays;

import java.util.List;
import java.util.Map;

public class GridLayoutAlgorithm {
    public static void layout(GraphModel gm, boolean init) {
        int width = gm.getWidth();
        int height = gm.getHeight();

        Map<Integer, VertexModel> vertexToModelMap = gm.getVertexToModelMap();

        int n = gm.getGraph().numVertices();

        if (n == 0) return;

        int rows = (int) Math.sqrt(n);
        int cols = (int) Math.ceil((double) n / rows);

        int minDistance = 50;
        List<Integer> vertices = IntArrays.asList(gm.getGraph().vertices());

        int startX = (width / 2 - cols * minDistance / 2);
        int endX = (width / 2 + cols * minDistance / 2);

        int startY = (height / 2 - rows * minDistance / 2);
        int endY = (height / 2 + rows * minDistance / 2);

        int index = 0;
        boolean done = false;
        for (int i = startX; i < endX && !done; i += minDistance) {
            for (int j = startY; j < endY && !done; j += minDistance) {
                int vertex = vertices.get(index);

                VertexModel vm = vertexToModelMap.get(vertex);

                if (init)
                    vm.setInitialPosition(i, j);
                else
                    vm.setPosition(i, j);

                index++;
                if (index >= n) {
                    done = true;
                }
            }
        }
    }
}
