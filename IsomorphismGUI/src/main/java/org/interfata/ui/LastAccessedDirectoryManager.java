package org.interfata.ui;

import java.io.*;
import java.util.Scanner;

public class LastAccessedDirectoryManager {
    private static final String LAST_DIRECTORY_FILE = "lastAccessedDirectory.txt";
    private static String lastDirectory = null;

    public static String loadLastDirectory() {
        if (lastDirectory != null) {
            return lastDirectory;
        }

        try (Scanner scanner = new Scanner(new File(LAST_DIRECTORY_FILE))) {
            if (scanner.hasNextLine()) {
                lastDirectory = scanner.nextLine();
            } else {
                lastDirectory = System.getProperty("user.home");
            }
        } catch (FileNotFoundException e) {
            lastDirectory = System.getProperty("user.home");
        }
        return lastDirectory;
    }

    public static void saveLastDirectory(String directory) {
        lastDirectory = directory;

        try (PrintWriter writer = new PrintWriter(new FileWriter(LAST_DIRECTORY_FILE, false))) {
            writer.println(directory);
        } catch (IOException e) {
            System.out.println("Error saving last directory");
        }
    }
}
