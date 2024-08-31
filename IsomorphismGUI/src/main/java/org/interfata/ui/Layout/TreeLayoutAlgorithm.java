package org.interfata.ui.Layout;

import org.graph4j.Graph;
import org.interfata.ui.models.GraphModel;
import org.interfata.ui.models.VertexModel;
import org.graph4j.traverse.BFSIterator;
import org.graph4j.traverse.SearchNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeLayoutAlgorithm {
    public static void layout(GraphModel gm, boolean init) {
        int w = gm.getWidth();
        int h = gm.getHeight();

        Graph<String, String> g = gm.getGraph();
        Map<Integer, VertexModel> vertexToModelMap = gm.getVertexToModelMap();

        BFSIterator bfsIterator = new BFSIterator(g);
        Map<Integer, List<Integer>> mapLevels = new HashMap<>();
        int maxLevel = 0;
        int level;

        while(bfsIterator.hasNext()){
            SearchNode searchNode = bfsIterator.next();
            int node = searchNode.vertex();

            level = searchNode.level();

            // if the level is not yet in the list, add it
            if (!mapLevels.containsKey(level)) {
                mapLevels.put(level, new ArrayList<>());
            }

            // update the maximum level
            if (level > maxLevel) {
                maxLevel = level;
            }

            // add the node to the list of nodes on the current level
            mapLevels.get(level).add(node);
        }

        int spacingBetweenLevels = h / (maxLevel + 1); // Add 1 to include space for level 0
        int horizontalSpacing = 75; // Example horizontal spacing between vertices

        // Iterate through levels and compute positions
        for (int l = 0; l <= maxLevel; l++) {
            List<Integer> verticesAtLevel = mapLevels.get(l);
            int n = verticesAtLevel.size();
            // Compute vertical position for vertices at this level
            int y = (l + 1) * spacingBetweenLevels - spacingBetweenLevels / 2; // Example: equidistant vertical spacing

            int startX = (w - ((n-1) * horizontalSpacing)) / 2; // Center vertices at this level
            // Iterate through vertices at this level and assign positions
            for (int vertex : verticesAtLevel) {
                VertexModel vm = vertexToModelMap.get(vertex);

                if (init)
                    vm.setInitialPosition(startX, y);
                else
                    vm.setPosition(startX, y);

                startX += horizontalSpacing;
            }

            horizontalSpacing += 25;
        }
    }
}
