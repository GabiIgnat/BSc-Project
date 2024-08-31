package org.interfata.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Custom menu bar
 * Has: File, View, Help
 */
public class CustomMenuBar extends JMenuBar {

    final static String ABOUT_MESSAGE = """
            This is a simple graph editor.
            
            For import and export, only GDF format is supported.
            
            GDF is a standard format for representing graph data.
            @see https://gephi.org/users/supported-graph-formats/gdf-format/
            
            For this editor, we will consider only the following columns:
            e.g.
                # comment
                nodedef>name INTEGER,label VARCHAR,x DOUBLE,y DOUBLE,color VARCHAR, width DOUBLE
                1,John,2,3,'255,0,0',10
                2,Paul,3,6,'0,255,0',10
                3,'hello,world', , ,'0,0,255',3
                edgedef>node1 INTEGER,node2 INTEGER,weight DOUBLE,label VARCHAR,color VARCHAR
                1,2, 0.5,'edge1','255,0,0'
                2,3, ,'edge2','0,255,0'
            
            The required columns are: name, node1 and node2. The rest are optional.
            
            Also, you can add new attributes, but they will be ignored.
            
            Note:
                - the width attribute is the radius of the node.
                - The color is in RGB format, separated by commas. You must use single quotes.
                - The label is surrounded by single/double quotes if it contains a comma.    \s
                - In order to work with other editors, you should NOT use spaces after commas.
                - If you want to omit a value, use an empty string '', but don't forget about the comma.
            
            It only supports undirected graphs.
            """;
    final MainFrame frame;
    final GraphPanel graphPanel1;
    final GraphPanel graphPanel2;
    JMenu fileMenu = new JMenu("File");
    JMenu viewMenu = new JMenu("View");
    JMenu helpMenu = new JMenu("Help");

    JMenu importFile = new JMenu("Import file");
    JMenu exportFile = new JMenu("Export file");

    JMenuItem importGraph1 = new JMenuItem("Graph 1");
    JMenuItem importGraph2 = new JMenuItem("Graph 2");

    JMenuItem exportGraph1 = new JMenuItem("Graph 1");
    JMenuItem exportGraph2 = new JMenuItem("Graph 2");

    JMenuItem darkTheme = new JMenuItem("Dark Theme");
    JMenuItem lightTheme = new JMenuItem("Light Theme");

    JMenuItem aboutMenuItem = new JMenuItem("About");

    FileFilter gdfFilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            return f.getName().toLowerCase().endsWith(".gdf") || f.isDirectory();
        }
        @Override
        public String getDescription() {
            return "GDF files (*.gdf)";
        }
    };

    public CustomMenuBar(MainFrame frame, GraphPanel graphPanel1, GraphPanel graphPanel2) {
        this.frame = frame;
        this.graphPanel1 = graphPanel1;
        this.graphPanel2 = graphPanel2;

        importFile.add(importGraph1);
        importFile.add(importGraph2);

        exportFile.add(exportGraph1);
        exportFile.add(exportGraph2);

        // add menu items to the menus
        fileMenu.add(importFile);
        fileMenu.add(exportFile);

        viewMenu.add(darkTheme);
        viewMenu.add(lightTheme);

        helpMenu.add(aboutMenuItem);

        setBorder(new EmptyBorder(5, 10, 5, 10)); // Insets: top, left, bottom, right

        add(fileMenu);
        add(viewMenu);
        add(helpMenu);

        init();
    }

    private void init() {
        // add action listeners(functions) to the menu items
        darkTheme.addActionListener(actionEvent1 -> frame.setDarkTheme());
        lightTheme.addActionListener(actionEvent1 -> frame.setLightTheme());

        importGraph1.addActionListener(actionEvent -> importFile(graphPanel1));
        exportGraph1.addActionListener(actionEvent -> exportFile(graphPanel1));

        importGraph2.addActionListener(actionEvent -> importFile(graphPanel2));
        exportGraph2.addActionListener(actionEvent -> exportFile(graphPanel2));

        aboutMenuItem.addActionListener(actionEvent ->
                JOptionPane.showMessageDialog(this,
                        ABOUT_MESSAGE, "About",
                        JOptionPane.INFORMATION_MESSAGE));

    }

    private void importFile(GraphPanel graphPanel){
        JFileChooser fileChooser = new JFileChooser(LastAccessedDirectoryManager.loadLastDirectory());
        fileChooser.setFileFilter(gdfFilter);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getPath();
            // verify if the file is a valid JSON file
            if (!filePath.endsWith(".gdf")) {
                JOptionPane.showMessageDialog(this,
                        "Only GDF files are supported", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            LastAccessedDirectoryManager.saveLastDirectory(fileChooser.getSelectedFile().getParent());

            graphPanel.canvas.loadGraph(filePath);
        }
        else if (returnValue == JFileChooser.CANCEL_OPTION) {
            System.out.println("Import file canceled");
        }
    }

    private void exportFile(GraphPanel graphPanel){
        JFileChooser fileChooser = new JFileChooser(LastAccessedDirectoryManager.loadLastDirectory());
        fileChooser.setFileFilter(gdfFilter);

        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String gdfPath = fileChooser.getSelectedFile().getPath();
            // verify if the file is a valid GDF file
            if (!gdfPath.endsWith(".gdf")) {
                // if it doesn't have an extension at all, add .txt
                if (!gdfPath.contains(".")) {
                    System.out.println("No extension found. Adding .gdf");
                    gdfPath = gdfPath + ".gdf";
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Only gdf files are supported", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // if file already exists, ask for confirmation
            if (Files.exists(Path.of(gdfPath))) {
                int result = JOptionPane.showConfirmDialog(this,
                        "File already exists. Do you want to overwrite it?",
                        "File exists",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            LastAccessedDirectoryManager.saveLastDirectory(fileChooser.getSelectedFile().getParent());

            graphPanel.canvas.saveGraph(gdfPath);
        } else if (returnValue == JFileChooser.CANCEL_OPTION) {
            System.out.println("Export file canceled");
        }
    }
}
