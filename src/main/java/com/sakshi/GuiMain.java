package com.sakshi;

import com.sakshi.exporter.CsvExporter;
import com.sakshi.service.GenerationResult;
import com.sakshi.service.GenerationService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Desktop interface for generating, reviewing, and exporting test cases. */
public class GuiMain extends JFrame {
    private static final Color NAVY = new Color(23, 37, 84);
    private static final Color BLUE = new Color(37, 99, 235);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color SURFACE = new Color(248, 250, 252);

    private final JTextField filePathField = new JTextField();
    private final JTextArea userStoryArea = new JTextArea();
    private final JTextArea jsonArea = new JTextArea();
    private final JTable table = new JTable();
    private final JLabel statusLabel = new JLabel("Ready");
    private final JLabel outputLabel = new JLabel("Choose a user story file to begin");
    private final JProgressBar progressBar = new JProgressBar();
    private final JButton browseButton = new JButton("Browse");
    private final JButton generateButton = new JButton("Generate test cases");
    private final JButton clearButton = new JButton("Clear");
    private final JButton copyJsonButton = new JButton("Copy JSON");

    public GuiMain() {
        super("AI Test Case Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 680));
        setSize(1180, 760);
        setLocationRelativeTo(null);
        initLookAndFeel();
        buildUi();
        bindActions();
    }

    private void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // The default look and feel is a safe fallback.
        }
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(BorderFactory.createEmptyBorder(18, 22, 14, 22));
        root.setBackground(SURFACE);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildContent(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 12));
        header.setOpaque(false);

        JLabel title = new JLabel("AI Test Case Generator");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        title.setForeground(NAVY);

        JLabel subtitle = new JLabel("Turn a user story into validated positive, negative, boundary, and edge cases.");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);
        header.add(titlePanel, BorderLayout.WEST);

        JPanel filePanel = new JPanel(new GridBagLayout());
        filePanel.setOpaque(false);
        GridBagConstraints labelGbc = new GridBagConstraints();
        labelGbc.insets = new Insets(0, 0, 0, 8);
        labelGbc.anchor = GridBagConstraints.WEST;
        filePanel.add(new JLabel("User story file"), labelGbc);

        filePathField.setColumns(34);
        filePathField.setToolTipText("Path to a file containing Feature, User Story, and Expected Behavior sections");
        GridBagConstraints fieldGbc = new GridBagConstraints();
        fieldGbc.weightx = 1;
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;
        fieldGbc.insets = new Insets(0, 0, 0, 8);
        filePanel.add(filePathField, fieldGbc);

        styleButton(browseButton, false);
        filePanel.add(browseButton, new GridBagConstraints());
        header.add(filePanel, BorderLayout.SOUTH);
        return header;
    }

    private JSplitPane buildContent() {
        userStoryArea.setEditable(false);
        userStoryArea.setLineWrap(true);
        userStoryArea.setWrapStyleWord(true);
        userStoryArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        userStoryArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = titledPanel("User story preview", new JScrollPane(userStoryArea));
        inputPanel.setPreferredSize(new Dimension(360, 420));

        jsonArea.setEditable(false);
        jsonArea.setLineWrap(false);
        jsonArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        jsonArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        table.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Scenario", "Steps", "Expected result", "Type"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.setRowHeight(28);
        table.getTableHeader().setReorderingAllowed(false);

        JPanel jsonPanel = titledPanel("JSON preview", new JScrollPane(jsonArea));
        JPanel tablePanel = titledPanel("CSV / table preview", new JScrollPane(table));
        JTabbedPane results = new JTabbedPane();
        results.addTab("JSON", jsonPanel);
        results.addTab("Test cases", tablePanel);
        results.setBorder(BorderFactory.createEmptyBorder());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputPanel, results);
        split.setResizeWeight(0.30);
        split.setBorder(BorderFactory.createEmptyBorder());
        return split;
    }

    private JPanel titledPanel(String title, java.awt.Component content) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        label.setForeground(NAVY);
        panel.add(label, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(12, 8));
        footer.setOpaque(false);

        JPanel statusPanel = new JPanel(new BorderLayout(8, 0));
        statusPanel.setOpaque(false);
        statusLabel.setForeground(NAVY);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(130, 8));
        statusPanel.add(progressBar, BorderLayout.CENTER);
        footer.add(statusPanel, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        styleButton(copyJsonButton, false);
        styleButton(clearButton, false);
        styleButton(generateButton, true);
        actions.add(copyJsonButton);
        actions.add(clearButton);
        actions.add(generateButton);
        footer.add(actions, BorderLayout.EAST);

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setOpaque(false);
        outputLabel.setForeground(MUTED);
        outputLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        outputPanel.add(outputLabel, BorderLayout.WEST);
        footer.add(outputPanel, BorderLayout.SOUTH);
        return footer;
    }

    private void styleButton(JButton button, boolean primary) {
        button.setFocusPainted(false);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        button.setMargin(new Insets(8, 14, 8, 14));
        if (primary) {
            button.setBackground(BLUE);
            button.setForeground(Color.WHITE);
        }
    }

    private void bindActions() {
        browseButton.addActionListener(event -> chooseFile());
        filePathField.addActionListener(event -> loadUserStory(filePathField.getText().trim()));
        generateButton.addActionListener(event -> generate());
        clearButton.addActionListener(event -> clear());
        copyJsonButton.addActionListener(event -> copyJson());
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select user story");
        chooser.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(chooser.getSelectedFile().getAbsolutePath());
            loadUserStory(filePathField.getText());
        }
    }

    private void loadUserStory(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return;
        }
        try {
            userStoryArea.setText(Files.readString(Path.of(filePath)));
            statusLabel.setText("User story loaded");
            outputLabel.setText("Ready to generate");
        } catch (IOException | RuntimeException ex) {
            userStoryArea.setText("");
            showError("Unable to read the selected file: " + ex.getMessage());
        }
    }

    private void generate() {
        String filePath = filePathField.getText().trim();
        if (filePath.isBlank()) {
            showError("Select a user story file before generating test cases.");
            return;
        }

        setBusy(true);
        statusLabel.setText("Generating and validating test cases…");
        outputLabel.setText("The AI request is in progress");

        SwingWorker<GenerationResult, Void> worker = new SwingWorker<>() {
            @Override
            protected GenerationResult doInBackground() {
                return GenerationService.generate(filePath);
            }

            @Override
            protected void done() {
                try {
                    displayResult(get());
                } catch (Exception ex) {
                    displayFailure("Generation failed: " + rootMessage(ex));
                } finally {
                    setBusy(false);
                }
            }
        };
        worker.execute();
    }

    private void displayResult(GenerationResult result) {
        if (!result.success()) {
            displayFailure(result.message());
            return;
        }

        jsonArea.setText(result.json());
        jsonArea.setCaretPosition(0);
        populateTable(result.response());
        statusLabel.setText(result.message());
        outputLabel.setText("JSON: " + result.jsonPath() + "    CSV: " + result.csvPath());
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private void displayFailure(String message) {
        statusLabel.setText("Generation failed");
        outputLabel.setText(message);
        showError(message);
    }

    private void populateTable(com.sakshi.model.AIResponse response) {
        List<String[]> rows = CsvExporter.toRows(response);
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Scenario", "Steps", "Expected result", "Type"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (String[] row : rows) {
            model.addRow(row);
        }
        table.setModel(model);
        table.setSelectionModel(new DefaultListSelectionModel());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void copyJson() {
        if (jsonArea.getText().isBlank()) {
            showError("There is no generated JSON to copy yet.");
            return;
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(jsonArea.getText()), null);
        statusLabel.setText("JSON copied to clipboard");
    }

    private void clear() {
        filePathField.setText("");
        userStoryArea.setText("");
        jsonArea.setText("");
        table.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Scenario", "Steps", "Expected result", "Type"}));
        statusLabel.setText("Ready");
        outputLabel.setText("Choose a user story file to begin");
    }

    private void setBusy(boolean busy) {
        filePathField.setEnabled(!busy);
        browseButton.setEnabled(!busy);
        generateButton.setEnabled(!busy);
        clearButton.setEnabled(!busy);
        copyJsonButton.setEnabled(!busy);
        progressBar.setVisible(busy);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Generation error", JOptionPane.ERROR_MESSAGE);
    }

    private String rootMessage(Exception exception) {
        Throwable cause = exception;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuiMain().setVisible(true));
    }
}
