package com.sakshi;

import com.sakshi.api.ApiServer;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;

/** Selects the desktop or command-line experience from one application entry point. */
public final class Launcher {
    private Launcher() {
    }

    public static void main(String[] args) {
        if (contains(args, "--server")) {
            try {
                ApiServer.start(serverPort(args));
            } catch (Exception ex) {
                System.err.println("Unable to start API server: " + ex.getMessage());
            }
            return;
        }
        if (contains(args, "--cli")) {
            Main.main(nonModeArguments(args));
            return;
        }
        if (contains(args, "--gui")) {
            launchGui();
            return;
        }

        if (GraphicsEnvironment.isHeadless()) {
            Main.main(args);
            return;
        }

        String[] options = {"Open GUI", "Use CLI", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "How would you like to generate test cases?",
                "AI Test Case Generator",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            launchGui();
        } else if (choice == 1) {
            Main.main(nonModeArguments(args));
        }
    }

    private static void launchGui() {
        SwingUtilities.invokeLater(() -> new GuiMain().setVisible(true));
    }

    private static boolean contains(String[] args, String value) {
        return Arrays.stream(args).anyMatch(value::equalsIgnoreCase);
    }

    private static int serverPort(String[] args) {
        for (int index = 0; index < args.length - 1; index++) {
            if ("--port".equalsIgnoreCase(args[index])) {
                try { return Integer.parseInt(args[index + 1]); } catch (NumberFormatException ignored) { return 8080; }
            }
        }
        return 8080;
    }

    private static String[] nonModeArguments(String[] args) {
        return Arrays.stream(args)
                .filter(arg -> !"--cli".equalsIgnoreCase(arg) && !"--gui".equalsIgnoreCase(arg))
                .toArray(String[]::new);
    }
}
