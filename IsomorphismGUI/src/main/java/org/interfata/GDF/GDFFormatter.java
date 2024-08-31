package org.interfata.GDF;

import org.interfata.GDF.models.GDFEdge;
import org.interfata.GDF.models.GDFGraph;
import org.interfata.GDF.models.GDFNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GDFFormatter {
    public static void save(GDFGraph gdfGraph, String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("nodedef>name INTEGER,label VARCHAR,x DOUBLE,y DOUBLE,color VARCHAR,width DOUBLE\n");
        for (GDFNode node : gdfGraph.nodes()) {
            sb.append(node.toString()).append("\n");
        }

        sb.append("edgedef>node1 INTEGER,node2 INTEGER,weight DOUBLE,label VARCHAR,color VARCHAR\n");
        for (GDFEdge edge : gdfGraph.edges()) {
            if (edge == null) {
                continue;
            }
            sb.append(edge).append("\n");
        }

        Files.write(Paths.get(filePath), sb.toString().getBytes());
    }
}
