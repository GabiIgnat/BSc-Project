package org.interfata.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Custom toolbar
 * Has buttons for: ADD, DELETE, EDIT, CONFIG
 */
public class OperationsBar extends JPanel {
    private final GraphPanel graphPanel;
    private Operation operation = Operation.NOTHING;
    private final JButton addButton;
    private final JButton layoutButton;

    public OperationsBar(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        setLayout(new BorderLayout()); // Set layout to BorderLayout

        layoutButton = new JButton("Layout");
        addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");
        JButton editButton = new JButton("Edit");
        JButton resetButton = new JButton("Reset");
        JButton clearButton = new JButton("Clear");

        // add function to the buttons
        layoutButton.addActionListener(this::layoutButtonAction);
        addButton.addActionListener(this::addButtonAction);
        deleteButton.addActionListener(this::deleteButtonAction);
        editButton.addActionListener(this::editButtonAction);
        resetButton.addActionListener(this::resetButtonAction);
        clearButton.addActionListener(this::clearButtonAction);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(layoutButton);
        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new GridBagLayout());
        add(bottomPanel, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding around each button

        gbc.gridy++;
        bottomPanel.add(addButton, gbc);

        gbc.gridy++;
        bottomPanel.add(deleteButton, gbc);

        gbc.gridy++;
        bottomPanel.add(editButton, gbc);

        gbc.gridy++;
        bottomPanel.add(resetButton, gbc);

        gbc.gridy++;
        bottomPanel.add(clearButton, gbc);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // Set focus to none
        layoutButton.setFocusable(false);
        graphPanel.requestFocusInWindow();
    }

    private void layoutButtonAction(ActionEvent actionEvent) {
        String[] options = {"Random", "Circular", "Grid", "Tree"};
        String selected = (String) JOptionPane.showInputDialog(this, "Select a layout", "Layout",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selected != null) {
            switch (selected) {
                case "Random":
                    graphPanel.canvas.randomLayout();
                    break;
                case "Circular":
                    graphPanel.canvas.circularLayout();
                    break;
                case "Grid":
                    graphPanel.canvas.gridLayout();
                    break;
                case "Tree":
                    graphPanel.canvas.treeLayout();
                    break;
            }
        }

        addButton.doClick();
        addButton.requestFocusInWindow();
    }

    private void addButtonAction(ActionEvent actionEvent) {
        operation = Operation.ADD;
    }

    private void deleteButtonAction(ActionEvent actionEvent) {
        operation = Operation.DELETE;
    }

    private void editButtonAction(ActionEvent actionEvent) {
        operation = Operation.EDIT;
    }

    private void resetButtonAction(ActionEvent actionEvent) {
        graphPanel.canvas.reset();
        operation = Operation.RESET;
    }

    private void clearButtonAction(ActionEvent actionEvent) {
        graphPanel.canvas.clear();
        operation = Operation.CLEAR;

        addButton.doClick();
        addButton.requestFocusInWindow();
    }

    public Operation getOperation() {
        return operation;
    }
}
