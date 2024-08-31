package org.interfata.GDF.models;

import java.util.Arrays;

/**
 * Represents a node in a GDF graph.
 * <p>
 * GDF format: <a href="https://gephi.org/users/supported-graph-formats/gdf-format/"></a>
 * <a href="http://graphexploration.cond.org/manual.pdf"></a>
 * </p>
 * DEFAULT columns for nodes:
 * name, visible, image, label, labelVisible, fixed, style, color, width, height, x, y
 * DEFAULT columns for edges:
 * node1, node2, visible, label, labelVisible, color, width, weight, directed
 * Example:
 * <p>
 * nodedef>id INT,label VARCHAR
 * 1,John
 * 2,Paul
 * 3,Ringo
 * edgedef>node1 INT,node2 INT,weight DOUBLE
 * 1,2,0.5
 * 2,3,0.1
 * 3,1,0.2
 * </p>
 *
 * <p>
 * nodedef> is the node definition, and edgedef> is the edge definition.
 * The node definition specifies the columns of the node table,
 * and the edge definition specifies the columns of the edge table.
 * </p>
 *
 * <p>
 * For the purpose of this project, we will consider only the following columns:
 * For nodes:
 *     <ul>
 *         <li>name INTEGER (the vertex number)</li>
 *         <li>label VARCHAR</li>
 *         <li>x INTEGER (the x coordinate)</li>
 *         <li>y INTEGER (the y coordinate)</li>
 *         <li>color VARCHAR (3 numbers separated by commas, RGB)</li>
 *         <li>width INTEGER (radius of the node)</li>
 *    </ul>
 * <p>
 *    For edges:
 *    <ul>
 *        <li>node1 INTEGER (the source vertex number)</li>
 *        <li>node2 INTEGER (the target vertex number)</li>
 *        <li>weight DOUBLE</li>
 *        <li>label VARCHAR</li>
 *        <li>color VARCHAR (3 numbers separated by commas, RGB)</li>
 *    </ul>
 * <p>
 *    All attributes are from the GDF format, except for the radius attribute.
 * </p>
 * <p>
 * If other columns are present, they are ignored.
 */
public record GDFGraph(GDFNode[] nodes, GDFEdge[] edges) {

    public boolean allNodesHaveCoordinates() {
        for (GDFNode node : nodes) {
            if (node.x() == null || node.y() == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "GDFGraph{" +
                "nodes=" + Arrays.toString(nodes) +
                ", edges=" + Arrays.toString(edges) +
                '}';
    }
}
