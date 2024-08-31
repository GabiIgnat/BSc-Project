package org.interfata.GDF;

import org.interfata.GDF.models.GDFEdge;
import org.interfata.GDF.models.GDFGraph;
import org.interfata.GDF.models.GDFNode;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This class parses a GDF file
 * It reads the file and creates a GDFGraph object
 *
 * <p>
 * Lines starting with 'nodedef>' are nodes
 * Lines starting with 'edgedef>' are edges
 * Lines starting with '#' are comments
 * </p>
 */
public class GDFParser {
    private static void validateFile(String filePath) {
        if (!Files.exists(Paths.get(filePath))) {
            throw new IllegalArgumentException("File does not exist");
        }

        if (!filePath.endsWith(".gdf")) {
            throw new IllegalArgumentException("File is not a GDF file");
        }
    }

    public static GDFGraph parse(String filePath) throws IOException {
        validateFile(filePath);

        List<GDFNode> nodes = new ArrayList<>();
        List<GDFEdge> edges = new ArrayList<>();
        List<String> nodeAttr = new ArrayList<>();
        List<String> edgeAttr = new ArrayList<>();

        String[] lines = Files.readString(Paths.get(filePath)).split("\n");

        boolean isNodeDefinition = false;

        for(String line : lines) {
            if(line.startsWith("nodedef>")) {
                isNodeDefinition = true;
                nodeAttr = getAttributes(line);
                continue;
            }

            if(line.startsWith("edgedef>")) {
                isNodeDefinition = false;
                edgeAttr = getAttributes(line);
                continue;
            }

            if(line.startsWith("#") || line.isBlank()) {
                continue;
            }

            if(isNodeDefinition) {
                GDFNode node = parseNode(line, nodeAttr);

                nodes.add(node);
            } else {
                GDFEdge edge = parseEdge(line, edgeAttr);

                edges.add(edge);
            }
        }
        return new GDFGraph(nodes.toArray(new GDFNode[0]), edges.toArray(new GDFEdge[0]));
    }

    private static GDFNode parseNode(String line, List<String> nodeAttr) {
        line = replaceDoubleQuotesWithSingleQuotes(line);

        System.out.println("line: " + line);

        // the label is surrounded by single/double quotes if it contains a comma
        // the color is surrounded by single quotes because it contains commas '255,0,0'
        String[] parts = customSplit(line);

        for (String part : parts) {
            System.out.println("part: " + part);
        }

        // get the attributes in the order they appear in the node definition
        int index = nodeAttr.indexOf("name");
        int name = Integer.parseInt(parts[index].trim());

        index = nodeAttr.indexOf("label");
        String label = null;
        if (index != -1 && !parts[index].trim().isEmpty()) {
            label = removeQuotes(parts[index].trim());
        }

        index = nodeAttr.indexOf("x");
        Double x = null;
        if (index != -1 && !parts[index].trim().isEmpty()) {
            x = Double.parseDouble(parts[index].trim());
        }

        index = nodeAttr.indexOf("y");
        Double y = null;
        if (index != -1 && !parts[index].trim().isEmpty()) {
            y = Double.parseDouble(parts[index].trim());
        }

        index = nodeAttr.indexOf("color");
        String color = null;
        if (index != -1 && !parts[index].trim().isEmpty()) {
            color = removeQuotes(parts[index].trim());
        }

        index = nodeAttr.indexOf("width");
        Double width = null;
        if (index != -1 && !parts[index].trim().isEmpty()) {
            width = Double.parseDouble(parts[index].trim());
        }

        return new GDFNode(name, label, x, y, getColorFromString(color), width);
    }

    private static GDFEdge parseEdge(String line, List<String> edgeAttr) {
        line = replaceDoubleQuotesWithSingleQuotes(line);

        String[] parts = customSplit(line);

        // get the attributes in the order they appear in the edge definition
        int index = edgeAttr.indexOf("node1");
        int node1 = Integer.parseInt(parts[index].trim());

        index = edgeAttr.indexOf("node2");
        int node2 = Integer.parseInt(parts[index].trim());

        index = edgeAttr.indexOf("weight");
        Double weight = null;
        if (index != -1 && !parts[index].trim().isEmpty()) {
            weight = Double.parseDouble(parts[index].trim());
        }

        index = edgeAttr.indexOf("label");
        String label = null;
        if (index != -1 && !parts[index].trim().isEmpty()) {
            label = removeQuotes(parts[index].trim());
        }

        index = edgeAttr.indexOf("color");
        String color = null;
        if (index != -1 && !parts[index].trim().isEmpty()) {
            color = removeQuotes(parts[index].trim());
        }

        return new GDFEdge(node1, node2, weight, label, getColorFromString(color));
    }

    private static List<String> getAttributes(String line) {
        List<String> attributes = new ArrayList<>();
        String[] parts = line.split(">");
        String[] columns = parts[1].split(",");

        for(String column : columns) {
            String name = column.trim().split("\\s")[0].trim();
            attributes.add(name);
        }
        return attributes;
    }

    protected static String[] customSplit(String line) {
        List<String> words = new ArrayList<>();
        int i = 0;
        int n = line.length();

        StringBuilder word = new StringBuilder();

        while (i < n) {
            char c = line.charAt(i);

            if (c == '\'') {
                // Start of a quoted string
                int j = i + 1;
                while (true) {
                    if (j == n) {
                        // End of the string
                        break;
                    }
                    char d = line.charAt(j);
                    if (d == '\'') {
                        if (j == n - 1) {
                            // End of the string
                            break;
                        }
                        // End of the quoted string
                        if (line.charAt(j + 1) == ',') {
                            // Skip the comma after the quoted string
                            j++;
                            break;
                        }
                    }
                    word.append(d);
                    j++;
                }
                words.add("'" + word + "'");
                word.setLength(0); // Clear the StringBuilder for the next word
                i = j + 1; // Move to the next character after the quoted string
            } else if (c == ',') {
                // Empty field
                words.add("");
                i++;
            } else {
                // Not a quoted string
                int j = i;
                while (j < n && line.charAt(j) != ',') {
                    word.append(line.charAt(j));
                    j++;
                }
                words.add(word.toString());
                word.setLength(0); // Clear the StringBuilder for the next word
                i = j + 1; // Move to the next character after the word
            }
        }

        if (line.charAt(n - 1) == ',') {
            words.add("");
        }

        for (String w : words) {
            System.out.println("word:" + w);
        }

        return words.toArray(new String[0]);
    }

    /**
     * If a word is surrounded by double quotes, replace the double quotes with single quotes
     * Example: `1,"Hello, "world"",1` becomes `1,'Hello, "world"',1`
     * @param text: the line of text to process
     */
    protected static String replaceDoubleQuotesWithSingleQuotes(String text) {
        // find the longest string that begins with a double quote and ends with a double quote
        // check if it is inside a single quote string
        // if it is NOT inside a single quote string, replace the double quotes with single quotes
        String largestQuotedSubstring = findLargestQuotedSubstring(text);
        if (largestQuotedSubstring == null) {
            return text;
        }

        int length = largestQuotedSubstring.length();
        int startPosition = text.indexOf(largestQuotedSubstring);
        int endPosition = startPosition + length;

        String left = text.substring(0, startPosition);
        String right = text.substring(endPosition);
        String middle = "'" + largestQuotedSubstring.substring(1, length - 1) + "'";

        // if on the left/right are commas, OR the token is at the beginning/end of the string
        // then replace the double quotes with single quotes
        if ((startPosition == 0 || text.charAt(startPosition - 1) == ',') &&
                (endPosition == text.length() || text.charAt(endPosition) == ',')) {
            // Replace double quotes with single quotes
            text = left + middle + right;
        }

        return text;
    }

    public static String findLargestQuotedSubstring(String text) {
        int start = text.indexOf('"');
        int end = text.lastIndexOf('"');

        if (start == -1 || end == -1) {
            return null;
        }

        return text.substring(start, end + 1);
    }

    /**
     * Removes surrounding single/double quotes from a string
     * @param text the text to remove the single/double quotes from
     */
    static String removeQuotes(String text) {
        String result = text;
        if ((text.startsWith("'") && text.endsWith("'")) ||
                (text.startsWith("\"") && text.endsWith("\""))){
            result = text.substring(1, text.length() - 1);
        }
        return result;
    }

    private static Color getColorFromString(String color) {
        if (color == null) {
            System.out.println("color is null");
            return null;
        }

        String[] parts = color.split(",");

        int r = Integer.parseInt(parts[0].trim());
        int g = Integer.parseInt(parts[1].trim());
        int b = Integer.parseInt(parts[2].trim());

        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));

        return new Color(r, g, b);
    }
}
