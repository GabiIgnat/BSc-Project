package org.interfata.ui.Layout;

import org.interfata.ui.models.GraphModel;
import org.interfata.ui.models.VertexModel;

import java.util.Map;

public class RandomLayoutAlgorithm extends LayoutAlgorithm {

    public static void layout(GraphModel gm, boolean init) {
        int width = gm.getWidth();
        int height = gm.getHeight();

        Map<Integer, VertexModel> vertexToModelMap = gm.getVertexToModelMap();
        for (int v : gm.getGraph().vertices()) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);

            VertexModel vm = vertexToModelMap.get(v);
            if (init)
                vm.setInitialPosition(x, y);
            else
                vm.setPosition(x, y);
        }
    }
}
