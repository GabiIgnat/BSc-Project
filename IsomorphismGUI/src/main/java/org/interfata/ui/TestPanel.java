package org.interfata.ui;

import org.graph4j.Graph;
import org.graph4j.iso.IsomorphicGraphMapping;
import org.graph4j.iso.general.VF2SubGraphIsomorphism;
import org.interfata.ui.models.VertexModel;
import org.interfata.ui.models.Point;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

/**
 * Test Panel
 * Has: button 'TEST' at top, and at center will be displayed the result of the test(
 *     - the time it took to run the test
 *     - if isomorphism exists
 *     - the mapping between the two graphs
 */
public class TestPanel extends JPanel {
    final MainFrame frame;
    final GraphPanel graphPanel1;
    final GraphPanel graphPanel2;

    private final JLabel resultLabel;
    private final JPanel mappingPanel;
    private final JLabel mappingLabel;

    private final JButton colorButton;
    private boolean colorOrReset = true;
    private final Map<Integer, Color> vertexToOldColor1 = new java.util.HashMap<>();
    private final Map<Integer, Color> vertexToOldColor2 = new java.util.HashMap<>();

    private final java.util.List<String> mappings = new ArrayList<>();
    private IsomorphicGraphMapping mapping;

    public TestPanel(MainFrame frame, GraphPanel graphPanel1, GraphPanel graphPanel2) {
        this.frame = frame;
        this.graphPanel1 = graphPanel1;
        this.graphPanel2 = graphPanel2;

        JButton testButton = new JButton("Test");
        testButton.addActionListener(this::performIsomorphismTest);

        colorButton = new JButton("Color");
        colorButton.addActionListener(this::colorButtonAction);

        resultLabel = new JLabel("--");
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0.1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(10, 10, 10, 10);

        add(testButton, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0.1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(10, 10, 10, 10);

        add(colorButton, c);
        colorButton.setVisible(false);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 0.1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(10, 10, 10, 10);

        add(resultLabel, c);

        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 1;
        c.weighty = 0.7;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(10, 10, 10, 10);

        // ----------

        JButton exportButton = new JButton("Export Mapping");
        exportButton.addActionListener(this::exportMapping);

        mappingPanel = new JPanel(new BorderLayout());
        mappingLabel = new JLabel("");

        JScrollPane scrollPane = new JScrollPane(mappingLabel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(200, 200));

        mappingPanel.add(exportButton, BorderLayout.NORTH);
        mappingPanel.add(scrollPane, BorderLayout.CENTER);

        mappingPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        add(mappingPanel, c);

        mappingPanel.setVisible(false);
    }

    private void colorButtonAction(ActionEvent actionEvent) {
        if (colorOrReset) {
            colorGraphs();
        } else {
            resetColorGraphs();
        }
    }

    private void colorGraphs() {
        colorButton.setText("Reset colors");
        colorOrReset = false;

        Graph<String, String> graph1 = graphPanel1.getGraph();
        Map<Integer, VertexModel> vertexToModelMap1 = graphPanel1.getGraphModel().getVertexToModelMap();
        Map<Integer, VertexModel> vertexToModelMap2 = graphPanel2.getGraphModel().getVertexToModelMap();

        // generate n random colors
        int n = graph1.numVertices();
        Color[] colors = generateDistinctColors(n);

        vertexToOldColor1.clear();

        for (int i = 0; i < n; i++) {
            int v = graph1.vertexAt(i);
            VertexModel vm = vertexToModelMap1.get(v);
            Color oldColor = vm.getColor();

            vertexToOldColor1.put(v, oldColor);
            graphPanel1.setVertexColor(v, colors[i]);

            int v2 = mapping.getVertexCorrespondence(v, true);
            vm = vertexToModelMap2.get(v2);
            oldColor = vm.getColor();
            vertexToOldColor2.put(v2, oldColor);
            graphPanel2.setVertexColor(v2, colors[i]);
        }
    }

    private void resetColorGraphs() {
        colorButton.setText("Color");
        colorOrReset = true;

        Graph<String, String> graph1 = graphPanel1.getGraph();
        Graph<String, String> graph2 = graphPanel2.getGraph();

        for (int v : graph1.vertices()) {
            if (!vertexToOldColor1.containsKey(v)) {
                continue;
            }
            Color oldColor = vertexToOldColor1.get(v);
            graphPanel1.setVertexColor(v, oldColor);
        }

        for (int v : graph2.vertices()) {
            if (!vertexToOldColor2.containsKey(v)) {
                continue;
            }
            Color oldColor = vertexToOldColor2.get(v);
            graphPanel2.setVertexColor(v, oldColor);
        }
    }

    private void performIsomorphismTest(ActionEvent e) {
        resetColorGraphs();

        Graph<String, String> graph1 = graphPanel1.getGraph();
        Graph<String, String> graph2 = graphPanel2.getGraph();

        var vf2 = new VF2SubGraphIsomorphism(graph1, graph2);

        long startTime = System.currentTimeMillis();
        Optional<IsomorphicGraphMapping> mapping = vf2.getMapping();
        long timeTaken = System.currentTimeMillis() - startTime;
        boolean isIsomorphic = mapping.isPresent();

        System.out.println("Isomorphic: " + isIsomorphic);

        if (isIsomorphic) {
            this.mapping = mapping.get();

            this.mappings.clear();
            for (int v1 : graph1.vertices()) {
                int v2 = this.mapping.getVertexCorrespondence(v1, true);

                this.mappings.add(v1 +" -> " + v2);
            }

//            graphPanel1.canvas.circularLayout();

            // layout the second graph
//            Map<Integer, VertexModel> vertexToModelMap1 = graphPanel1.getGraphModel().getVertexToModelMap();

//            for (int v1 : graph1.vertices()) {
//                int v2 = mapping.get().getVertexCorrespondence(v1, true);
//
//                Point p = vertexToModelMap1.get(v1).getPosition();
//                int x = p.getX();
//                int y = p.getY();
//
//                graphPanel2.setPosition(v2, x, y);
//            }

            String msg = "<html> <h3> Graph1 is isomorphic with Graph2. </h3> <br> Time taken: " + timeTaken + " ms </html>";

            if (graph1.numVertices() < graph2.numVertices()) {
                msg = "<html> <h3> Graph1 is isomorphic with a subgraph of Graph2. </h3><br> Time taken: " + timeTaken + " ms </html>";
            }

            displayTestResult(msg, Color.GREEN);
            displayMapping();
        } else {
            String msg = "<html> <h3> The 2 graphs are not isomorphic. </h3> <br> Time taken: " + timeTaken + " ms </html>";

            displayTestResult(msg, Color.RED);
            removeMapping();
        }
    }

    private void displayMapping() {
        StringBuilder mappingText = new StringBuilder("<html> <ul>");

        for (String mapping : mappings) {
            mappingText.append("<li>").append(mapping).append("</li>");
        }
        mappingText.append("</ul></html>");

        mappingLabel.setText(mappingText.toString());
        mappingPanel.setVisible(true);

        colorButton.setVisible(true);
    }

    private void removeMapping() {
        mappingLabel.setText("");
        mappingPanel.setVisible(false);
        colorButton.setVisible(false);
    }

    private void displayTestResult(String message, Color color) {
        resultLabel.setForeground(color);
        resultLabel.setText(message);
    }

    private void exportMapping(ActionEvent e) {
        FileFilter txtFilter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
            }
            @Override
            public String getDescription() {
                return "TXT files (*.txt)";
            }
        };

        JFileChooser fileChooser = new JFileChooser(LastAccessedDirectoryManager.loadLastDirectory());
        fileChooser.setFileFilter(txtFilter);

        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String txtPath = fileChooser.getSelectedFile().getPath();
            // verify if the file is a valid TXT file
            if (!txtPath.endsWith(".txt")) {
                // if it doesn't have an extension at all, add .txt
                if (!txtPath.contains(".")) {
                    System.out.println("No extension found. Adding .txt");
                    txtPath = txtPath + ".txt";
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Only txt files are supported", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // if file already exists, ask for confirmation
            if (Files.exists(Path.of(txtPath))) {
                int result = JOptionPane.showConfirmDialog(this,
                        "File already exists. Do you want to overwrite it?",
                        "File exists",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            LastAccessedDirectoryManager.saveLastDirectory(fileChooser.getSelectedFile().getParent());

            saveMapping(txtPath);
        } else if (returnValue == JFileChooser.CANCEL_OPTION) {
            System.out.println("Export file canceled");
        }
    }

    private void saveMapping(String txtPath) {
        // write the mappingLabel text to the file
        System.out.println("Mapping saved to " + txtPath);

        StringBuilder mapping = new StringBuilder();
        for (String m : mappings) {
            mapping.append(m).append("\n");
        }

        try {
            Files.write(Path.of(txtPath), mapping.toString().getBytes());
        } catch (Exception e) {
            System.out.println("Error saving mapping");
        }
    }

    public static Color[] generateDistinctColors(int n) {
        Color[] colors = new Color[n];
        float hueIncrement = 360.0f / n;
        float saturation = 0.8f; // You can adjust saturation and brightness values
        float brightness = 0.9f; // to achieve desired color variations

        for (int i = 0; i < n; i++) {
            float hue = hueIncrement * i;
            colors[i] = Color.getHSBColor(hue / 360.0f, saturation, brightness);
        }

        return colors;
    }
}
