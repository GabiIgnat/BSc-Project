package org.interfata.main;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.interfata.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception e) {
            System.out.println("Failed to initialize LaF");
        }

        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    }
}