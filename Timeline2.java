import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Timeline2 extends JFrame {

    // Data storage
    private ArrayList<TimelineEvent> events = new ArrayList<>();
    private ArrayList<TimelineTask> tasks = new ArrayList<>();
    private ArrayList<TimelineMilestone> milestones = new ArrayList<>();
    private ArrayList<Object> layerOrder = new ArrayList<>(); // Unified list of tasks and milestones for z-ordering
    private Set<Integer> selectedTaskIndices = new HashSet<>(); // Multi-select for tasks
    private int selectedMilestoneIndex = -1;

    // Panel colors - Settings
    private Color settingsInteriorColor = new Color(250, 250, 250);
    private Color settingsOutlineColor = new Color(200, 200, 200);
    private Color settingsHeaderColor = new Color(70, 130, 180);
    private Color settingsHeaderTextColor = Color.WHITE;
    private Color settingsLabelColor = Color.BLACK;
    private Color settingsFieldBgColor = Color.WHITE;
    private Color settingsButtonBgColor = new Color(240, 240, 240);
    private Color settingsButtonTextColor = Color.BLACK;
    // Panel colors - Timeline
    private Color timelineInteriorColor = Color.WHITE;
    private Color timelineOutlineColor = new Color(200, 200, 200);
    private Color timelineLineColor = new Color(70, 130, 180);
    private Color timelineDateTextColor = Color.BLACK;
    private Color timelineGridColor = new Color(220, 220, 220);
    private Color timelineEventColor = new Color(220, 53, 69);
    // Panel colors - Layers
    private Color layersInteriorColor = new Color(250, 250, 250);
    private Color layersOutlineColor = new Color(200, 200, 200);
    private Color layersHeaderColor = new Color(70, 130, 180);
    private Color layersHeaderTextColor = Color.WHITE;
    private Color layersListBgColor = Color.WHITE;
    private Color layersItemTextColor = Color.BLACK;
    private Color layersSelectedBgColor = new Color(200, 200, 200);
    private Color layersDragHandleColor = new Color(150, 150, 150);
    // Panel colors - Format
    private Color formatInteriorColor = new Color(250, 250, 250);
    private Color formatOutlineColor = new Color(200, 200, 200);
    private Color formatHeaderColor = new Color(70, 130, 180);
    private Color formatLabelColor = Color.BLACK;
    private Color formatSeparatorColor = new Color(200, 200, 200);
    private Color formatResizeHandleColor = new Color(230, 230, 230);

    // UI Components
    private TimelineDisplayPanel timelineDisplayPanel;
    private JTextField startDateField, endDateField;
    private CollapsiblePanel leftPanel;
    private CollapsiblePanel rightPanel;
    private LayersPanel layersPanel;
    private JPanel formatPanel;
    private JTextField taskNameField, taskStartField, taskEndField;
    private JLabel formatTitleLabel;
    private JButton duplicateTaskBtn;
    private JButton fillColorBtn, outlineColorBtn, textColorBtn;
    // Notes tab fields
    private JTextField note1Field, note2Field, note3Field, note4Field, note5Field;
    private JSpinner outlineThicknessSpinner, taskHeightSpinner, fontSizeSpinner;
    private JToggleButton boldBtn, italicBtn;
    private JTextField centerTextField;
    private JSpinner centerXOffsetSpinner, centerYOffsetSpinner;
    // Front text controls
    private JTextField frontTextField;
    private JSpinner frontFontSizeSpinner;
    private JToggleButton frontBoldBtn, frontItalicBtn;
    private JButton frontTextColorBtn;
    private JSpinner frontXOffsetSpinner, frontYOffsetSpinner;
    // Above text controls
    private JTextField aboveTextField;
    private JSpinner aboveFontSizeSpinner;
    private JToggleButton aboveBoldBtn, aboveItalicBtn;
    private JButton aboveTextColorBtn;
    private JSpinner aboveXOffsetSpinner, aboveYOffsetSpinner;
    // Underneath text controls
    private JTextField underneathTextField;
    private JSpinner underneathFontSizeSpinner;
    private JToggleButton underneathBoldBtn, underneathItalicBtn;
    private JButton underneathTextColorBtn;
    private JSpinner underneathXOffsetSpinner, underneathYOffsetSpinner;
    // Behind text controls
    private JTextField behindTextField;
    private JSpinner behindFontSizeSpinner;
    private JToggleButton behindBoldBtn, behindItalicBtn;
    private JButton behindTextColorBtn;
    private JSpinner behindXOffsetSpinner, behindYOffsetSpinner;
    // Milestone controls
    private JTextField milestoneNameField, milestoneDateField;
    private JSpinner milestoneWidthSpinner, milestoneHeightSpinner;
    private JButton milestoneFillColorBtn, milestoneOutlineColorBtn;
    private JSpinner milestoneOutlineThicknessSpinner;
    // Row 1 switcher (task vs milestone)
    private JPanel row1Container;
    private CardLayout row1CardLayout;
    // Timeline background color
    private JButton timelineBgColorBtn;

    // Constants
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Color[] TASK_COLORS = {
        new Color(76, 175, 80),   // Green
        new Color(33, 150, 243),  // Blue
        new Color(255, 152, 0),   // Orange
        new Color(156, 39, 176),  // Purple
        new Color(244, 67, 54),   // Red
        new Color(0, 188, 212),   // Cyan
        new Color(255, 193, 7),   // Amber
        new Color(121, 85, 72)    // Brown
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Timeline2().setVisible(true));
    }

    public Timeline2() {
        setTitle("Timeline Creator V3");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        // Menu bar
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem importItem = new JMenuItem("Import...");
        importItem.addActionListener(e -> importFromExcel());
        fileMenu.add(importItem);
        JMenuItem exportItem = new JMenuItem("Export...");
        exportItem.addActionListener(e -> exportToExcel());
        fileMenu.add(exportItem);
        menuBar.add(fileMenu);

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem preferencesItem = new JMenuItem("Preferences");
        preferencesItem.addActionListener(e -> showPreferencesDialog());
        editMenu.add(preferencesItem);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);

        // Left panel - Settings
        leftPanel = new CollapsiblePanel("Settings", createSettingsPanel(), true);
        add(leftPanel, BorderLayout.WEST);

        // Center - Timeline display with New Task button
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Top toolbar with New Task, New Milestone, and Clear All buttons
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        toolbarPanel.setBackground(new Color(245, 245, 245));
        JButton newTaskBtn = new JButton("+ New Task");
        newTaskBtn.addActionListener(e -> addNewTask());
        toolbarPanel.add(newTaskBtn);
        JButton newMilestoneBtn = new JButton("+ New Milestone");
        newMilestoneBtn.addActionListener(e -> showMilestoneShapeMenu(newMilestoneBtn));
        toolbarPanel.add(newMilestoneBtn);
        duplicateTaskBtn = new JButton("Duplicate");
        duplicateTaskBtn.setEnabled(false);
        duplicateTaskBtn.setToolTipText("Duplicate selected task(s)");
        duplicateTaskBtn.addActionListener(e -> duplicateSelectedTasks());
        toolbarPanel.add(duplicateTaskBtn);
        JButton clearAllBtn = new JButton("Clear All");
        clearAllBtn.addActionListener(e -> clearAll());
        toolbarPanel.add(clearAllBtn);
        centerPanel.add(toolbarPanel, BorderLayout.NORTH);

        timelineDisplayPanel = new TimelineDisplayPanel();
        JScrollPane scrollPane = new JScrollPane(timelineDisplayPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Timeline"));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Right panel - Layers (collapsible)
        layersPanel = new LayersPanel();
        rightPanel = new CollapsiblePanel("Layers", layersPanel, false);
        add(rightPanel, BorderLayout.EAST);

        // Bottom - Format panel
        formatPanel = createFormatPanel();
        add(formatPanel, BorderLayout.SOUTH);

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        refreshTimeline();
    }

    private JPanel createFormatPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.setPreferredSize(new Dimension(0, 250));
        panel.setBackground(new Color(250, 250, 250));

        // Header bar with minimize/maximize toggle
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setPreferredSize(new Dimension(0, 20));
        headerBar.setBackground(new Color(70, 130, 180));

        JLabel formatTitle = new JLabel("  Format");
        formatTitle.setForeground(Color.WHITE);
        formatTitle.setFont(new Font("Arial", Font.BOLD, 11));
        headerBar.add(formatTitle, BorderLayout.WEST);

        // Minimize/maximize button
        JButton toggleBtn = new JButton("\u25BC");  // Down arrow (expanded)
        toggleBtn.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 10));
        toggleBtn.setMargin(new Insets(0, 5, 0, 5));
        toggleBtn.setFocusPainted(false);
        toggleBtn.setToolTipText("Minimize");

        // Content wrapper to show/hide
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        final boolean[] isExpanded = {true};
        toggleBtn.addActionListener(e -> {
            isExpanded[0] = !isExpanded[0];
            if (isExpanded[0]) {
                panel.setPreferredSize(new Dimension(0, 250));
                toggleBtn.setText("\u25BC");  // Down arrow
                toggleBtn.setToolTipText("Minimize");
                contentWrapper.setVisible(true);
            } else {
                panel.setPreferredSize(new Dimension(0, 20));
                toggleBtn.setText("\u25B2");  // Up arrow
                toggleBtn.setToolTipText("Expand");
                contentWrapper.setVisible(false);
            }
            panel.revalidate();
            Timeline2.this.revalidate();
            Timeline2.this.repaint();
        });
        headerBar.add(toggleBtn, BorderLayout.EAST);

        panel.add(headerBar, BorderLayout.NORTH);

        // Main content panel with rows
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Row 1: CardLayout container for task/milestone fields
        row1CardLayout = new CardLayout();
        row1Container = new JPanel(row1CardLayout);
        row1Container.setOpaque(false);
        row1Container.setMinimumSize(new Dimension(1200, 30));
        row1Container.setPreferredSize(new Dimension(1200, 30));

        // Task row panel
        JPanel taskRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        taskRow.setOpaque(false);

        formatTitleLabel = new JLabel("No task selected");
        formatTitleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        taskRow.add(formatTitleLabel);

        taskRow.add(Box.createHorizontalStrut(10));
        taskRow.add(new JLabel("Name:"));
        taskNameField = new JTextField(10);
        taskNameField.setEnabled(false);
        taskNameField.addActionListener(e -> updateSelectedTaskName());
        taskNameField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateSelectedTaskName(); }
        });
        taskRow.add(taskNameField);

        taskRow.add(Box.createHorizontalStrut(10));
        taskRow.add(new JLabel("Start:"));
        taskStartField = new JTextField(8);
        taskStartField.setEnabled(false);
        taskStartField.addActionListener(e -> updateSelectedTaskDates());
        taskStartField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateSelectedTaskDates(); }
        });
        taskRow.add(taskStartField);

        taskRow.add(Box.createHorizontalStrut(10));
        taskRow.add(new JLabel("End:"));
        taskEndField = new JTextField(8);
        taskEndField.setEnabled(false);
        taskEndField.addActionListener(e -> updateSelectedTaskDates());
        taskEndField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateSelectedTaskDates(); }
        });
        taskRow.add(taskEndField);

        taskRow.add(Box.createHorizontalStrut(15));
        taskRow.add(new JLabel("Fill:"));
        fillColorBtn = new JButton();
        fillColorBtn.setPreferredSize(new Dimension(30, 25));
        fillColorBtn.setEnabled(false);
        fillColorBtn.setToolTipText("Click to change fill color");
        fillColorBtn.addActionListener(e -> chooseFillColor());
        taskRow.add(fillColorBtn);

        taskRow.add(Box.createHorizontalStrut(10));
        taskRow.add(new JLabel("Outline:"));
        outlineColorBtn = new JButton();
        outlineColorBtn.setPreferredSize(new Dimension(30, 25));
        outlineColorBtn.setEnabled(false);
        outlineColorBtn.setToolTipText("Click to change outline color");
        outlineColorBtn.addActionListener(e -> chooseOutlineColor());
        taskRow.add(outlineColorBtn);

        taskRow.add(Box.createHorizontalStrut(10));
        taskRow.add(new JLabel("Thickness:"));
        outlineThicknessSpinner = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));
        outlineThicknessSpinner.setPreferredSize(new Dimension(50, 25));
        outlineThicknessSpinner.setEnabled(false);
        outlineThicknessSpinner.setToolTipText("Outline thickness (0-10)");
        outlineThicknessSpinner.addChangeListener(e -> updateOutlineThickness());
        taskRow.add(outlineThicknessSpinner);

        taskRow.add(Box.createHorizontalStrut(10));
        taskRow.add(new JLabel("Height:"));
        taskHeightSpinner = new JSpinner(new SpinnerNumberModel(25, 10, 100, 5));
        taskHeightSpinner.setPreferredSize(new Dimension(55, 25));
        taskHeightSpinner.setEnabled(false);
        taskHeightSpinner.setToolTipText("Task bar height (10-100)");
        taskHeightSpinner.addChangeListener(e -> updateTaskHeight());
        taskRow.add(taskHeightSpinner);

        row1Container.add(taskRow, "task");

        // Milestone row panel
        JPanel milestoneRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        milestoneRow.setOpaque(false);

        JLabel milestoneTitleLabel = new JLabel("Milestone selected");
        milestoneTitleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        milestoneRow.add(milestoneTitleLabel);

        milestoneRow.add(Box.createHorizontalStrut(10));
        milestoneRow.add(new JLabel("Name:"));
        milestoneNameField = new JTextField(10);
        milestoneNameField.setEnabled(false);
        milestoneNameField.addActionListener(e -> updateSelectedMilestoneName());
        milestoneNameField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateSelectedMilestoneName(); }
        });
        milestoneRow.add(milestoneNameField);

        milestoneRow.add(Box.createHorizontalStrut(10));
        milestoneRow.add(new JLabel("Date:"));
        milestoneDateField = new JTextField(8);
        milestoneDateField.setEnabled(false);
        milestoneDateField.addActionListener(e -> updateSelectedMilestoneDate());
        milestoneDateField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateSelectedMilestoneDate(); }
        });
        milestoneRow.add(milestoneDateField);

        milestoneRow.add(Box.createHorizontalStrut(15));
        milestoneRow.add(new JLabel("Fill:"));
        milestoneFillColorBtn = new JButton();
        milestoneFillColorBtn.setPreferredSize(new Dimension(30, 25));
        milestoneFillColorBtn.setEnabled(false);
        milestoneFillColorBtn.setToolTipText("Click to change fill color");
        milestoneFillColorBtn.addActionListener(e -> chooseMilestoneFillColor());
        milestoneRow.add(milestoneFillColorBtn);

        milestoneRow.add(Box.createHorizontalStrut(10));
        milestoneRow.add(new JLabel("Outline:"));
        milestoneOutlineColorBtn = new JButton();
        milestoneOutlineColorBtn.setPreferredSize(new Dimension(30, 25));
        milestoneOutlineColorBtn.setEnabled(false);
        milestoneOutlineColorBtn.setToolTipText("Click to change outline color");
        milestoneOutlineColorBtn.addActionListener(e -> chooseMilestoneOutlineColor());
        milestoneRow.add(milestoneOutlineColorBtn);

        milestoneRow.add(Box.createHorizontalStrut(10));
        milestoneRow.add(new JLabel("Thickness:"));
        milestoneOutlineThicknessSpinner = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));
        milestoneOutlineThicknessSpinner.setPreferredSize(new Dimension(50, 25));
        milestoneOutlineThicknessSpinner.setEnabled(false);
        milestoneOutlineThicknessSpinner.addChangeListener(e -> updateMilestoneOutlineThickness());
        milestoneRow.add(milestoneOutlineThicknessSpinner);

        milestoneRow.add(Box.createHorizontalStrut(10));
        milestoneRow.add(new JLabel("Height:"));
        milestoneHeightSpinner = new JSpinner(new SpinnerNumberModel(20, 10, 100, 5));
        milestoneHeightSpinner.setPreferredSize(new Dimension(55, 25));
        milestoneHeightSpinner.setEnabled(false);
        milestoneHeightSpinner.addChangeListener(e -> updateMilestoneHeight());
        milestoneRow.add(milestoneHeightSpinner);

        milestoneRow.add(Box.createHorizontalStrut(10));
        milestoneRow.add(new JLabel("Width:"));
        milestoneWidthSpinner = new JSpinner(new SpinnerNumberModel(20, 10, 100, 5));
        milestoneWidthSpinner.setPreferredSize(new Dimension(55, 25));
        milestoneWidthSpinner.setEnabled(false);
        milestoneWidthSpinner.addChangeListener(e -> updateMilestoneWidth());
        milestoneRow.add(milestoneWidthSpinner);

        row1Container.add(milestoneRow, "milestone");

        // Show task row by default
        row1CardLayout.show(row1Container, "task");

        contentPanel.add(row1Container);

        // Separator between rows with 5px spacing above and 3px below
        contentPanel.add(Box.createVerticalStrut(5));
        JPanel separatorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        separatorPanel.setOpaque(false);
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setPreferredSize(new Dimension(1200, 2));
        separatorPanel.add(Box.createHorizontalStrut(10));
        separatorPanel.add(separator);
        contentPanel.add(separatorPanel);
        contentPanel.add(Box.createVerticalStrut(3));

        // Row 2: Front text fields (text in front of task bar)
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        row2.setOpaque(false);
        row2.setMinimumSize(new Dimension(800, 30));
        row2.setPreferredSize(new Dimension(800, 30));

        JLabel frontLabel = new JLabel("Front Text:");
        frontLabel.setPreferredSize(new Dimension(105, 20));
        row2.add(frontLabel);
        frontTextField = new JTextField(12);
        frontTextField.setEnabled(false);
        frontTextField.setToolTipText("Text displayed in front of the task bar");
        frontTextField.addActionListener(e -> updateFrontText());
        frontTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateFrontText(); }
        });
        row2.add(frontTextField);

        row2.add(Box.createHorizontalStrut(10));
        frontFontSizeSpinner = new JSpinner(new SpinnerNumberModel(10, 8, 24, 1));
        frontFontSizeSpinner.setPreferredSize(new Dimension(50, 25));
        frontFontSizeSpinner.setEnabled(false);
        frontFontSizeSpinner.setToolTipText("Front text font size (8-24)");
        frontFontSizeSpinner.addChangeListener(e -> updateFrontFontSize());
        row2.add(frontFontSizeSpinner);

        row2.add(Box.createHorizontalStrut(5));

        // MS Word style Bold button for front text
        frontBoldBtn = new JToggleButton("B") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(150, 150, 150));
                    g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                } else if (getModel().isRollover() && isEnabled()) {
                    g2d.setColor(new Color(230, 230, 230));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(180, 180, 180));
                    g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                } else {
                    g2d.setColor(getBackground());
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                g2d.setColor(isEnabled() ? Color.BLACK : Color.GRAY);
                g2d.setFont(new Font("Times New Roman", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("B")) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString("B", x, y);
            }
        };
        frontBoldBtn.setPreferredSize(new Dimension(28, 25));
        frontBoldBtn.setEnabled(false);
        frontBoldBtn.setToolTipText("Bold front text");
        frontBoldBtn.setContentAreaFilled(false);
        frontBoldBtn.setBorderPainted(false);
        frontBoldBtn.setFocusPainted(false);
        frontBoldBtn.addActionListener(e -> updateFrontFontBold());
        row2.add(frontBoldBtn);

        row2.add(Box.createHorizontalStrut(2));

        // MS Word style Italic button for front text
        frontItalicBtn = new JToggleButton("I") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(150, 150, 150));
                    g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                } else if (getModel().isRollover() && isEnabled()) {
                    g2d.setColor(new Color(230, 230, 230));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(180, 180, 180));
                    g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                } else {
                    g2d.setColor(getBackground());
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                g2d.setColor(isEnabled() ? Color.BLACK : Color.GRAY);
                g2d.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("I")) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString("I", x, y);
            }
        };
        frontItalicBtn.setPreferredSize(new Dimension(28, 25));
        frontItalicBtn.setEnabled(false);
        frontItalicBtn.setToolTipText("Italic front text");
        frontItalicBtn.setContentAreaFilled(false);
        frontItalicBtn.setBorderPainted(false);
        frontItalicBtn.setFocusPainted(false);
        frontItalicBtn.addActionListener(e -> updateFrontFontItalic());
        row2.add(frontItalicBtn);

        row2.add(Box.createHorizontalStrut(5));
        frontTextColorBtn = new JButton();
        frontTextColorBtn.setPreferredSize(new Dimension(30, 25));
        frontTextColorBtn.setEnabled(false);
        frontTextColorBtn.setToolTipText("Click to change front text color");
        frontTextColorBtn.addActionListener(e -> chooseFrontTextColor());
        row2.add(frontTextColorBtn);

        row2.add(Box.createHorizontalStrut(10));
        row2.add(new JLabel("X:"));
        frontXOffsetSpinner = new JSpinner(new SpinnerNumberModel(0, -500, 500, 1));
        frontXOffsetSpinner.setPreferredSize(new Dimension(50, 25));
        frontXOffsetSpinner.setEnabled(false);
        frontXOffsetSpinner.setToolTipText("X offset from default position");
        frontXOffsetSpinner.addChangeListener(e -> updateFrontXOffset());
        row2.add(frontXOffsetSpinner);
        row2.add(new JLabel("Y:"));
        frontYOffsetSpinner = new JSpinner(new SpinnerNumberModel(0, -500, 500, 1));
        frontYOffsetSpinner.setPreferredSize(new Dimension(50, 25));
        frontYOffsetSpinner.setEnabled(false);
        frontYOffsetSpinner.setToolTipText("Y offset from default position");
        frontYOffsetSpinner.addChangeListener(e -> updateFrontYOffset());
        row2.add(frontYOffsetSpinner);

        contentPanel.add(row2);

        // Row 3: Center text fields (text on the task bar)
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        row3.setOpaque(false);
        row3.setMinimumSize(new Dimension(800, 30));
        row3.setPreferredSize(new Dimension(800, 30));

        JLabel centerLabel = new JLabel("Center Text:");
        centerLabel.setPreferredSize(new Dimension(105, 20));
        row3.add(centerLabel);
        centerTextField = new JTextField(12);
        centerTextField.setEnabled(false);
        centerTextField.setToolTipText("Text displayed on the task bar");
        centerTextField.addActionListener(e -> updateCenterText());
        centerTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateCenterText(); }
        });
        row3.add(centerTextField);

        row3.add(Box.createHorizontalStrut(10));
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(11, 8, 24, 1));
        fontSizeSpinner.setPreferredSize(new Dimension(50, 25));
        fontSizeSpinner.setEnabled(false);
        fontSizeSpinner.setToolTipText("Center text font size (8-24)");
        fontSizeSpinner.addChangeListener(e -> updateFontSize());
        row3.add(fontSizeSpinner);

        row3.add(Box.createHorizontalStrut(5));

        // MS Word style Bold button
        boldBtn = new JToggleButton("B") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(150, 150, 150));
                    g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                } else if (getModel().isRollover() && isEnabled()) {
                    g2d.setColor(new Color(230, 230, 230));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(180, 180, 180));
                    g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                } else {
                    g2d.setColor(getBackground());
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                g2d.setColor(isEnabled() ? Color.BLACK : Color.GRAY);
                g2d.setFont(new Font("Times New Roman", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("B")) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString("B", x, y);
            }
        };
        boldBtn.setPreferredSize(new Dimension(28, 25));
        boldBtn.setEnabled(false);
        boldBtn.setToolTipText("Bold (Ctrl+B)");
        boldBtn.setContentAreaFilled(false);
        boldBtn.setBorderPainted(false);
        boldBtn.setFocusPainted(false);
        boldBtn.addActionListener(e -> updateFontBold());
        row3.add(boldBtn);

        row3.add(Box.createHorizontalStrut(2));

        // MS Word style Italic button
        italicBtn = new JToggleButton("I") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(150, 150, 150));
                    g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                } else if (getModel().isRollover() && isEnabled()) {
                    g2d.setColor(new Color(230, 230, 230));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(180, 180, 180));
                    g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                } else {
                    g2d.setColor(getBackground());
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                g2d.setColor(isEnabled() ? Color.BLACK : Color.GRAY);
                g2d.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("I")) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString("I", x, y);
            }
        };
        italicBtn.setPreferredSize(new Dimension(28, 25));
        italicBtn.setEnabled(false);
        italicBtn.setToolTipText("Italic (Ctrl+I)");
        italicBtn.setContentAreaFilled(false);
        italicBtn.setBorderPainted(false);
        italicBtn.setFocusPainted(false);
        italicBtn.addActionListener(e -> updateFontItalic());
        row3.add(italicBtn);

        row3.add(Box.createHorizontalStrut(5));
        textColorBtn = new JButton();
        textColorBtn.setPreferredSize(new Dimension(30, 25));
        textColorBtn.setEnabled(false);
        textColorBtn.setToolTipText("Click to change center text color");
        textColorBtn.addActionListener(e -> chooseTextColor());
        row3.add(textColorBtn);

        row3.add(Box.createHorizontalStrut(10));
        row3.add(new JLabel("X:"));
        centerXOffsetSpinner = new JSpinner(new SpinnerNumberModel(0, -500, 500, 1));
        centerXOffsetSpinner.setPreferredSize(new Dimension(50, 25));
        centerXOffsetSpinner.setEnabled(false);
        centerXOffsetSpinner.setToolTipText("X offset from default position");
        centerXOffsetSpinner.addChangeListener(e -> updateCenterXOffset());
        row3.add(centerXOffsetSpinner);
        row3.add(new JLabel("Y:"));
        centerYOffsetSpinner = new JSpinner(new SpinnerNumberModel(0, -500, 500, 1));
        centerYOffsetSpinner.setPreferredSize(new Dimension(50, 25));
        centerYOffsetSpinner.setEnabled(false);
        centerYOffsetSpinner.setToolTipText("Y offset from default position");
        centerYOffsetSpinner.addChangeListener(e -> updateCenterYOffset());
        row3.add(centerYOffsetSpinner);

        contentPanel.add(row3);

        // Row 4: Above text fields
        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        row4.setOpaque(false);
        row4.setMinimumSize(new Dimension(800, 30));
        row4.setPreferredSize(new Dimension(800, 30));
        JLabel aboveLabel = new JLabel("Above Text:");
        aboveLabel.setPreferredSize(new Dimension(105, 20));
        row4.add(aboveLabel);
        aboveTextField = new JTextField(12);
        aboveTextField.setEnabled(false);
        aboveTextField.setToolTipText("Text displayed above the task bar");
        aboveTextField.addActionListener(e -> updateAboveText());
        aboveTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateAboveText(); }
        });
        row4.add(aboveTextField);
        row4.add(Box.createHorizontalStrut(10));
        aboveFontSizeSpinner = new JSpinner(new SpinnerNumberModel(10, 8, 24, 1));
        aboveFontSizeSpinner.setPreferredSize(new Dimension(50, 25));
        aboveFontSizeSpinner.setEnabled(false);
        aboveFontSizeSpinner.setToolTipText("Above text font size");
        aboveFontSizeSpinner.addChangeListener(e -> updateAboveFontSize());
        row4.add(aboveFontSizeSpinner);
        row4.add(Box.createHorizontalStrut(5));
        aboveBoldBtn = createWordStyleButton("B", true, false);
        aboveBoldBtn.addActionListener(e -> updateAboveFontBold());
        row4.add(aboveBoldBtn);
        row4.add(Box.createHorizontalStrut(2));
        aboveItalicBtn = createWordStyleButton("I", false, true);
        aboveItalicBtn.addActionListener(e -> updateAboveFontItalic());
        row4.add(aboveItalicBtn);
        row4.add(Box.createHorizontalStrut(5));
        aboveTextColorBtn = new JButton();
        aboveTextColorBtn.setPreferredSize(new Dimension(30, 25));
        aboveTextColorBtn.setEnabled(false);
        aboveTextColorBtn.setToolTipText("Above text color");
        aboveTextColorBtn.addActionListener(e -> chooseAboveTextColor());
        row4.add(aboveTextColorBtn);
        row4.add(Box.createHorizontalStrut(10));
        row4.add(new JLabel("X:"));
        aboveXOffsetSpinner = new JSpinner(new SpinnerNumberModel(0, -500, 500, 1));
        aboveXOffsetSpinner.setPreferredSize(new Dimension(50, 25));
        aboveXOffsetSpinner.setEnabled(false);
        aboveXOffsetSpinner.setToolTipText("X offset from default position");
        aboveXOffsetSpinner.addChangeListener(e -> updateAboveXOffset());
        row4.add(aboveXOffsetSpinner);
        row4.add(new JLabel("Y:"));
        aboveYOffsetSpinner = new JSpinner(new SpinnerNumberModel(0, -500, 500, 1));
        aboveYOffsetSpinner.setPreferredSize(new Dimension(50, 25));
        aboveYOffsetSpinner.setEnabled(false);
        aboveYOffsetSpinner.setToolTipText("Y offset from default position");
        aboveYOffsetSpinner.addChangeListener(e -> updateAboveYOffset());
        row4.add(aboveYOffsetSpinner);
        contentPanel.add(row4);

        // Row 5: Underneath text fields
        JPanel row5 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        row5.setOpaque(false);
        row5.setMinimumSize(new Dimension(800, 30));
        row5.setPreferredSize(new Dimension(800, 30));
        JLabel underneathLabel = new JLabel("Underneath Text:");
        underneathLabel.setPreferredSize(new Dimension(105, 20));
        row5.add(underneathLabel);
        underneathTextField = new JTextField(12);
        underneathTextField.setEnabled(false);
        underneathTextField.setToolTipText("Text displayed below the task bar");
        underneathTextField.addActionListener(e -> updateUnderneathText());
        underneathTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateUnderneathText(); }
        });
        row5.add(underneathTextField);
        row5.add(Box.createHorizontalStrut(10));
        underneathFontSizeSpinner = new JSpinner(new SpinnerNumberModel(10, 8, 24, 1));
        underneathFontSizeSpinner.setPreferredSize(new Dimension(50, 25));
        underneathFontSizeSpinner.setEnabled(false);
        underneathFontSizeSpinner.setToolTipText("Underneath text font size");
        underneathFontSizeSpinner.addChangeListener(e -> updateUnderneathFontSize());
        row5.add(underneathFontSizeSpinner);
        row5.add(Box.createHorizontalStrut(5));
        underneathBoldBtn = createWordStyleButton("B", true, false);
        underneathBoldBtn.addActionListener(e -> updateUnderneathFontBold());
        row5.add(underneathBoldBtn);
        row5.add(Box.createHorizontalStrut(2));
        underneathItalicBtn = createWordStyleButton("I", false, true);
        underneathItalicBtn.addActionListener(e -> updateUnderneathFontItalic());
        row5.add(underneathItalicBtn);
        row5.add(Box.createHorizontalStrut(5));
        underneathTextColorBtn = new JButton();
        underneathTextColorBtn.setPreferredSize(new Dimension(30, 25));
        underneathTextColorBtn.setEnabled(false);
        underneathTextColorBtn.setToolTipText("Underneath text color");
        underneathTextColorBtn.addActionListener(e -> chooseUnderneathTextColor());
        row5.add(underneathTextColorBtn);
        row5.add(Box.createHorizontalStrut(10));
        row5.add(new JLabel("X:"));
        underneathXOffsetSpinner = new JSpinner(new SpinnerNumberModel(0, -500, 500, 1));
        underneathXOffsetSpinner.setPreferredSize(new Dimension(50, 25));
        underneathXOffsetSpinner.setEnabled(false);
        underneathXOffsetSpinner.setToolTipText("X offset from default position");
        underneathXOffsetSpinner.addChangeListener(e -> updateUnderneathXOffset());
        row5.add(underneathXOffsetSpinner);
        row5.add(new JLabel("Y:"));
        underneathYOffsetSpinner = new JSpinner(new SpinnerNumberModel(0, -500, 500, 1));
        underneathYOffsetSpinner.setPreferredSize(new Dimension(50, 25));
        underneathYOffsetSpinner.setEnabled(false);
        underneathYOffsetSpinner.setToolTipText("Y offset from default position");
        underneathYOffsetSpinner.addChangeListener(e -> updateUnderneathYOffset());
        row5.add(underneathYOffsetSpinner);
        contentPanel.add(row5);

        // Row 6: Behind text fields
        JPanel row6 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        row6.setOpaque(false);
        row6.setMinimumSize(new Dimension(800, 30));
        row6.setPreferredSize(new Dimension(800, 30));
        JLabel behindLabel = new JLabel("Behind Text:");
        behindLabel.setPreferredSize(new Dimension(105, 20));
        row6.add(behindLabel);
        behindTextField = new JTextField(12);
        behindTextField.setEnabled(false);
        behindTextField.setToolTipText("Text displayed behind the task bar");
        behindTextField.addActionListener(e -> updateBehindText());
        behindTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateBehindText(); }
        });
        row6.add(behindTextField);
        row6.add(Box.createHorizontalStrut(10));
        behindFontSizeSpinner = new JSpinner(new SpinnerNumberModel(10, 8, 24, 1));
        behindFontSizeSpinner.setPreferredSize(new Dimension(50, 25));
        behindFontSizeSpinner.setEnabled(false);
        behindFontSizeSpinner.setToolTipText("Behind text font size");
        behindFontSizeSpinner.addChangeListener(e -> updateBehindFontSize());
        row6.add(behindFontSizeSpinner);
        row6.add(Box.createHorizontalStrut(5));
        behindBoldBtn = createWordStyleButton("B", true, false);
        behindBoldBtn.addActionListener(e -> updateBehindFontBold());
        row6.add(behindBoldBtn);
        row6.add(Box.createHorizontalStrut(2));
        behindItalicBtn = createWordStyleButton("I", false, true);
        behindItalicBtn.addActionListener(e -> updateBehindFontItalic());
        row6.add(behindItalicBtn);
        row6.add(Box.createHorizontalStrut(5));
        behindTextColorBtn = new JButton();
        behindTextColorBtn.setPreferredSize(new Dimension(30, 25));
        behindTextColorBtn.setEnabled(false);
        behindTextColorBtn.setToolTipText("Behind text color");
        behindTextColorBtn.addActionListener(e -> chooseBehindTextColor());
        row6.add(behindTextColorBtn);
        row6.add(Box.createHorizontalStrut(10));
        row6.add(new JLabel("X:"));
        behindXOffsetSpinner = new JSpinner(new SpinnerNumberModel(0, -500, 500, 1));
        behindXOffsetSpinner.setPreferredSize(new Dimension(50, 25));
        behindXOffsetSpinner.setEnabled(false);
        behindXOffsetSpinner.setToolTipText("X offset from default position");
        behindXOffsetSpinner.addChangeListener(e -> updateBehindXOffset());
        row6.add(behindXOffsetSpinner);
        row6.add(new JLabel("Y:"));
        behindYOffsetSpinner = new JSpinner(new SpinnerNumberModel(0, -500, 500, 1));
        behindYOffsetSpinner.setPreferredSize(new Dimension(50, 25));
        behindYOffsetSpinner.setEnabled(false);
        behindYOffsetSpinner.setToolTipText("Y offset from default position");
        behindYOffsetSpinner.addChangeListener(e -> updateBehindYOffset());
        row6.add(behindYOffsetSpinner);
        contentPanel.add(row6);
        contentPanel.add(Box.createVerticalStrut(3));

        // Create Notes panel with labels in first row, inputs in second row
        JPanel notesPanel = new JPanel();
        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));
        notesPanel.setOpaque(false);
        notesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Row 1: Labels
        JPanel labelsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        labelsRow.setOpaque(false);
        JLabel note1Label = new JLabel("Note 1");
        note1Label.setPreferredSize(new Dimension(150, 20));
        note1Label.setHorizontalAlignment(SwingConstants.CENTER);
        labelsRow.add(note1Label);
        JLabel note2Label = new JLabel("Note 2");
        note2Label.setPreferredSize(new Dimension(150, 20));
        note2Label.setHorizontalAlignment(SwingConstants.CENTER);
        labelsRow.add(note2Label);
        JLabel note3Label = new JLabel("Note 3");
        note3Label.setPreferredSize(new Dimension(150, 20));
        note3Label.setHorizontalAlignment(SwingConstants.CENTER);
        labelsRow.add(note3Label);
        JLabel note4Label = new JLabel("Note 4");
        note4Label.setPreferredSize(new Dimension(150, 20));
        note4Label.setHorizontalAlignment(SwingConstants.CENTER);
        labelsRow.add(note4Label);
        JLabel note5Label = new JLabel("Note 5");
        note5Label.setPreferredSize(new Dimension(150, 20));
        note5Label.setHorizontalAlignment(SwingConstants.CENTER);
        labelsRow.add(note5Label);
        notesPanel.add(labelsRow);

        // Row 2: Input fields
        JPanel inputsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        inputsRow.setOpaque(false);
        note1Field = new JTextField(12);
        note1Field.setEnabled(false);
        note1Field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateNotes(); }
        });
        inputsRow.add(note1Field);
        note2Field = new JTextField(12);
        note2Field.setEnabled(false);
        note2Field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateNotes(); }
        });
        inputsRow.add(note2Field);
        note3Field = new JTextField(12);
        note3Field.setEnabled(false);
        note3Field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateNotes(); }
        });
        inputsRow.add(note3Field);
        note4Field = new JTextField(12);
        note4Field.setEnabled(false);
        note4Field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateNotes(); }
        });
        inputsRow.add(note4Field);
        note5Field = new JTextField(12);
        note5Field.setEnabled(false);
        note5Field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateNotes(); }
        });
        inputsRow.add(note5Field);
        notesPanel.add(inputsRow);

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 11));
        tabbedPane.addTab("Format", contentPanel);
        tabbedPane.addTab("Notes", notesPanel);

        contentWrapper.add(tabbedPane, BorderLayout.CENTER);
        panel.add(contentWrapper, BorderLayout.CENTER);

        return panel;
    }

    // Helper to create MS Word style toggle button
    private JToggleButton createWordStyleButton(String text, boolean isBold, boolean isItalic) {
        JToggleButton btn = new JToggleButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(150, 150, 150));
                    g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                } else if (getModel().isRollover() && isEnabled()) {
                    g2d.setColor(new Color(230, 230, 230));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(180, 180, 180));
                    g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                } else {
                    g2d.setColor(getBackground());
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                g2d.setColor(isEnabled() ? Color.BLACK : Color.GRAY);
                int style = (isBold ? Font.BOLD : 0) | (isItalic ? Font.ITALIC : 0);
                if (style == 0) style = Font.BOLD;
                g2d.setFont(new Font("Times New Roman", style, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(text, x, y);
            }
        };
        btn.setPreferredSize(new Dimension(28, 25));
        btn.setEnabled(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    private void chooseFillColor() {
        if (selectedTaskIndices.isEmpty()) return;
        Color currentColor = Color.BLUE;
        if (selectedTaskIndices.size() == 1) {
            int idx = selectedTaskIndices.iterator().next();
            TimelineTask t = tasks.get(idx);
            currentColor = t.fillColor != null ? t.fillColor : TASK_COLORS[idx % TASK_COLORS.length];
        }
        Color newColor = JColorChooser.showDialog(this, "Choose Fill Color", currentColor);
        if (newColor != null) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).fillColor = newColor;
            }
            fillColorBtn.setBackground(newColor);
            refreshTimeline();
        }
    }

    private void chooseOutlineColor() {
        if (selectedTaskIndices.isEmpty()) return;
        Color currentColor = Color.DARK_GRAY;
        if (selectedTaskIndices.size() == 1) {
            int idx = selectedTaskIndices.iterator().next();
            TimelineTask t = tasks.get(idx);
            Color fill = t.fillColor != null ? t.fillColor : TASK_COLORS[idx % TASK_COLORS.length];
            currentColor = t.outlineColor != null ? t.outlineColor : fill.darker();
        }
        Color newColor = JColorChooser.showDialog(this, "Choose Outline Color", currentColor);
        if (newColor != null) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).outlineColor = newColor;
            }
            outlineColorBtn.setBackground(newColor);
            refreshTimeline();
        }
    }

    private void updateOutlineThickness() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) outlineThicknessSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).outlineThickness = value;
        }
        refreshTimeline();
    }

    private void updateTaskHeight() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) taskHeightSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).height = value;
        }
        refreshTimeline();
    }

    private void updateCenterText() {
        if (selectedTaskIndices.isEmpty()) return;
        String text = centerTextField.getText();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).centerText = text;
        }
        refreshTimeline();
    }

    private void updateFontSize() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) fontSizeSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).fontSize = value;
        }
        refreshTimeline();
    }

    private void updateFontBold() {
        if (selectedTaskIndices.isEmpty()) return;
        boolean value = boldBtn.isSelected();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).fontBold = value;
        }
        refreshTimeline();
    }

    private void updateFontItalic() {
        if (selectedTaskIndices.isEmpty()) return;
        boolean value = italicBtn.isSelected();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).fontItalic = value;
        }
        refreshTimeline();
    }

    private void chooseTextColor() {
        if (selectedTaskIndices.isEmpty()) return;
        Color currentColor = Color.WHITE;
        if (selectedTaskIndices.size() == 1) {
            TimelineTask t = tasks.get(selectedTaskIndices.iterator().next());
            currentColor = t.textColor != null ? t.textColor : Color.WHITE;
        }
        Color newColor = JColorChooser.showDialog(this, "Choose Text Color", currentColor);
        if (newColor != null) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).textColor = newColor;
            }
            textColorBtn.setBackground(newColor);
            refreshTimeline();
        }
    }

    private void updateCenterXOffset() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) centerXOffsetSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).centerTextXOffset = value;
        }
        refreshTimeline();
    }

    private void updateCenterYOffset() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) centerYOffsetSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).centerTextYOffset = value;
        }
        refreshTimeline();
    }

    // Front text update methods
    private void updateFrontText() {
        if (selectedTaskIndices.isEmpty()) return;
        String text = frontTextField.getText();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).frontText = text;
        }
        refreshTimeline();
    }

    private void updateFrontFontSize() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) frontFontSizeSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).frontFontSize = value;
        }
        refreshTimeline();
    }

    private void updateFrontFontBold() {
        if (selectedTaskIndices.isEmpty()) return;
        boolean value = frontBoldBtn.isSelected();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).frontFontBold = value;
        }
        refreshTimeline();
    }

    private void updateFrontFontItalic() {
        if (selectedTaskIndices.isEmpty()) return;
        boolean value = frontItalicBtn.isSelected();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).frontFontItalic = value;
        }
        refreshTimeline();
    }

    private void chooseFrontTextColor() {
        if (selectedTaskIndices.isEmpty()) return;
        Color currentColor = Color.BLACK;
        if (selectedTaskIndices.size() == 1) {
            TimelineTask t = tasks.get(selectedTaskIndices.iterator().next());
            currentColor = t.frontTextColor != null ? t.frontTextColor : Color.BLACK;
        }
        Color newColor = JColorChooser.showDialog(this, "Choose Front Text Color", currentColor);
        if (newColor != null) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).frontTextColor = newColor;
            }
            frontTextColorBtn.setBackground(newColor);
            refreshTimeline();
        }
    }

    private void updateFrontXOffset() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) frontXOffsetSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).frontTextXOffset = value;
        }
        refreshTimeline();
    }

    private void updateFrontYOffset() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) frontYOffsetSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).frontTextYOffset = value;
        }
        refreshTimeline();
    }

    // Above text update methods
    private void updateAboveText() {
        if (selectedTaskIndices.isEmpty()) return;
        String text = aboveTextField.getText();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).aboveText = text;
        }
        refreshTimeline();
    }

    private void updateAboveFontSize() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) aboveFontSizeSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).aboveFontSize = value;
        }
        refreshTimeline();
    }

    private void updateAboveFontBold() {
        if (selectedTaskIndices.isEmpty()) return;
        boolean value = aboveBoldBtn.isSelected();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).aboveFontBold = value;
        }
        refreshTimeline();
    }

    private void updateAboveFontItalic() {
        if (selectedTaskIndices.isEmpty()) return;
        boolean value = aboveItalicBtn.isSelected();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).aboveFontItalic = value;
        }
        refreshTimeline();
    }

    private void chooseAboveTextColor() {
        if (selectedTaskIndices.isEmpty()) return;
        Color currentColor = Color.BLACK;
        if (selectedTaskIndices.size() == 1) {
            TimelineTask t = tasks.get(selectedTaskIndices.iterator().next());
            currentColor = t.aboveTextColor != null ? t.aboveTextColor : Color.BLACK;
        }
        Color newColor = JColorChooser.showDialog(this, "Choose Above Text Color", currentColor);
        if (newColor != null) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).aboveTextColor = newColor;
            }
            aboveTextColorBtn.setBackground(newColor);
            refreshTimeline();
        }
    }

    private void updateAboveXOffset() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) aboveXOffsetSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).aboveTextXOffset = value;
        }
        refreshTimeline();
    }

    private void updateAboveYOffset() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) aboveYOffsetSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).aboveTextYOffset = value;
        }
        refreshTimeline();
    }

    // Underneath text update methods
    private void updateUnderneathText() {
        if (selectedTaskIndices.isEmpty()) return;
        String text = underneathTextField.getText();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).underneathText = text;
        }
        refreshTimeline();
    }

    private void updateUnderneathFontSize() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) underneathFontSizeSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).underneathFontSize = value;
        }
        refreshTimeline();
    }

    private void updateUnderneathFontBold() {
        if (selectedTaskIndices.isEmpty()) return;
        boolean value = underneathBoldBtn.isSelected();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).underneathFontBold = value;
        }
        refreshTimeline();
    }

    private void updateUnderneathFontItalic() {
        if (selectedTaskIndices.isEmpty()) return;
        boolean value = underneathItalicBtn.isSelected();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).underneathFontItalic = value;
        }
        refreshTimeline();
    }

    private void chooseUnderneathTextColor() {
        if (selectedTaskIndices.isEmpty()) return;
        Color currentColor = Color.BLACK;
        if (selectedTaskIndices.size() == 1) {
            TimelineTask t = tasks.get(selectedTaskIndices.iterator().next());
            currentColor = t.underneathTextColor != null ? t.underneathTextColor : Color.BLACK;
        }
        Color newColor = JColorChooser.showDialog(this, "Choose Underneath Text Color", currentColor);
        if (newColor != null) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).underneathTextColor = newColor;
            }
            underneathTextColorBtn.setBackground(newColor);
            refreshTimeline();
        }
    }

    private void updateUnderneathXOffset() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) underneathXOffsetSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).underneathTextXOffset = value;
        }
        refreshTimeline();
    }

    private void updateUnderneathYOffset() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) underneathYOffsetSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).underneathTextYOffset = value;
        }
        refreshTimeline();
    }

    // Behind text update methods
    private void updateBehindText() {
        if (selectedTaskIndices.isEmpty()) return;
        String text = behindTextField.getText();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).behindText = text;
        }
        refreshTimeline();
    }

    private void updateBehindFontSize() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) behindFontSizeSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).behindFontSize = value;
        }
        refreshTimeline();
    }

    private void updateBehindFontBold() {
        if (selectedTaskIndices.isEmpty()) return;
        boolean value = behindBoldBtn.isSelected();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).behindFontBold = value;
        }
        refreshTimeline();
    }

    private void updateBehindFontItalic() {
        if (selectedTaskIndices.isEmpty()) return;
        boolean value = behindItalicBtn.isSelected();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).behindFontItalic = value;
        }
        refreshTimeline();
    }

    private void chooseBehindTextColor() {
        if (selectedTaskIndices.isEmpty()) return;
        Color currentColor = new Color(150, 150, 150);
        if (selectedTaskIndices.size() == 1) {
            TimelineTask t = tasks.get(selectedTaskIndices.iterator().next());
            currentColor = t.behindTextColor != null ? t.behindTextColor : new Color(150, 150, 150);
        }
        Color newColor = JColorChooser.showDialog(this, "Choose Behind Text Color", currentColor);
        if (newColor != null) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).behindTextColor = newColor;
            }
            behindTextColorBtn.setBackground(newColor);
            refreshTimeline();
        }
    }

    private void updateBehindXOffset() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) behindXOffsetSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).behindTextXOffset = value;
        }
        refreshTimeline();
    }

    private void updateBehindYOffset() {
        if (selectedTaskIndices.isEmpty()) return;
        int value = (Integer) behindYOffsetSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).behindTextYOffset = value;
        }
        refreshTimeline();
    }

    private void updateNotes() {
        if (selectedTaskIndices.isEmpty()) return;
        for (int idx : selectedTaskIndices) {
            TimelineTask task = tasks.get(idx);
            task.note1 = note1Field.getText();
            task.note2 = note2Field.getText();
            task.note3 = note3Field.getText();
            task.note4 = note4Field.getText();
            task.note5 = note5Field.getText();
        }
    }

    // Milestone update methods
    private void updateSelectedMilestoneName() {
        if (selectedMilestoneIndex < 0 || selectedMilestoneIndex >= milestones.size()) return;
        String newName = milestoneNameField.getText().trim();
        if (!newName.isEmpty()) {
            milestones.get(selectedMilestoneIndex).name = newName;
            milestones.get(selectedMilestoneIndex).labelText = newName;
            refreshTimeline();
        }
    }

    private void updateSelectedMilestoneDate() {
        if (selectedMilestoneIndex < 0 || selectedMilestoneIndex >= milestones.size()) return;
        String newDate = milestoneDateField.getText().trim();
        try {
            LocalDate.parse(newDate, DATE_FORMAT);
            milestones.get(selectedMilestoneIndex).date = newDate;
            refreshTimeline();
        } catch (Exception e) {
            showWarning("Invalid date format. Use YYYY-MM-DD.");
        }
    }

    private void chooseMilestoneFillColor() {
        if (selectedMilestoneIndex < 0 || selectedMilestoneIndex >= milestones.size()) return;
        TimelineMilestone milestone = milestones.get(selectedMilestoneIndex);
        Color newColor = JColorChooser.showDialog(this, "Choose Fill Color", milestone.fillColor);
        if (newColor != null) {
            milestone.fillColor = newColor;
            milestoneFillColorBtn.setBackground(newColor);
            refreshTimeline();
        }
    }

    private void chooseMilestoneOutlineColor() {
        if (selectedMilestoneIndex < 0 || selectedMilestoneIndex >= milestones.size()) return;
        TimelineMilestone milestone = milestones.get(selectedMilestoneIndex);
        Color newColor = JColorChooser.showDialog(this, "Choose Outline Color", milestone.outlineColor);
        if (newColor != null) {
            milestone.outlineColor = newColor;
            milestoneOutlineColorBtn.setBackground(newColor);
            refreshTimeline();
        }
    }

    private void chooseTimelineBackgroundColor() {
        Color newColor = JColorChooser.showDialog(this, "Choose Background Color", timelineInteriorColor);
        if (newColor != null) {
            timelineInteriorColor = newColor;
            timelineBgColorBtn.setBackground(newColor);
            refreshTimeline();
        }
    }

    private void updateMilestoneOutlineThickness() {
        if (selectedMilestoneIndex < 0 || selectedMilestoneIndex >= milestones.size()) return;
        milestones.get(selectedMilestoneIndex).outlineThickness = (Integer) milestoneOutlineThicknessSpinner.getValue();
        refreshTimeline();
    }

    private void updateMilestoneHeight() {
        if (selectedMilestoneIndex < 0 || selectedMilestoneIndex >= milestones.size()) return;
        milestones.get(selectedMilestoneIndex).height = (Integer) milestoneHeightSpinner.getValue();
        refreshTimeline();
    }

    private void updateMilestoneWidth() {
        if (selectedMilestoneIndex < 0 || selectedMilestoneIndex >= milestones.size()) return;
        milestones.get(selectedMilestoneIndex).width = (Integer) milestoneWidthSpinner.getValue();
        refreshTimeline();
    }

    void selectTask(int index, boolean ctrlDown) {
        // Deselect milestone when task is selected
        if (index >= 0) {
            selectedMilestoneIndex = -1;
            row1CardLayout.show(row1Container, "task");
        }

        // Handle multi-select with Ctrl key
        if (ctrlDown && index >= 0) {
            // Toggle selection
            if (selectedTaskIndices.contains(index)) {
                selectedTaskIndices.remove(index);
            } else {
                selectedTaskIndices.add(index);
            }
        } else {
            // Single select - clear previous and select new
            selectedTaskIndices.clear();
            if (index >= 0) {
                selectedTaskIndices.add(index);
            }
        }

        updateFormatPanelForSelection();
        timelineDisplayPanel.repaint();
        if (layersPanel != null) {
            // For layers panel, show the first selected task
            if (!selectedTaskIndices.isEmpty()) {
                int firstSelected = selectedTaskIndices.iterator().next();
                TimelineTask task = tasks.get(firstSelected);
                int layerIndex = layerOrder.indexOf(task);
                layersPanel.setSelectedLayer(layerIndex);
            } else {
                layersPanel.setSelectedLayer(-1);
            }
        }
    }

    // Overload for calls without ctrlDown parameter
    void selectTask(int index) {
        selectTask(index, false);
    }

    // Update format panel based on current selection
    private void updateFormatPanelForSelection() {
        if (selectedTaskIndices.isEmpty()) {
            // No selection
            formatTitleLabel.setText("No task selected");
            formatTitleLabel.setForeground(Color.BLACK);
            duplicateTaskBtn.setEnabled(false);
            setFormatFieldsEnabled(false);
            clearFormatFields();
        } else if (selectedTaskIndices.size() == 1) {
            // Single selection - show task values
            int index = selectedTaskIndices.iterator().next();
            TimelineTask task = tasks.get(index);
            Color defaultColor = TASK_COLORS[index % TASK_COLORS.length];
            Color fillColor = task.fillColor != null ? task.fillColor : defaultColor;
            Color outlineColor = task.outlineColor != null ? task.outlineColor : fillColor.darker();

            formatTitleLabel.setText("Selected: " + task.name);
            formatTitleLabel.setForeground(fillColor.darker());
            duplicateTaskBtn.setEnabled(true);
            taskNameField.setText(task.name);
            taskStartField.setText(task.startDate);
            taskEndField.setText(task.endDate);

            fillColorBtn.setBackground(fillColor);
            outlineColorBtn.setBackground(outlineColor);
            outlineThicknessSpinner.setValue(task.outlineThickness);
            taskHeightSpinner.setValue(task.height);

            centerTextField.setText(task.centerText);
            fontSizeSpinner.setValue(task.fontSize);
            boldBtn.setSelected(task.fontBold);
            italicBtn.setSelected(task.fontItalic);
            textColorBtn.setBackground(task.textColor != null ? task.textColor : Color.BLACK);

            frontTextField.setText(task.frontText);
            frontFontSizeSpinner.setValue(task.frontFontSize);
            frontBoldBtn.setSelected(task.frontFontBold);
            frontItalicBtn.setSelected(task.frontFontItalic);
            frontTextColorBtn.setBackground(task.frontTextColor != null ? task.frontTextColor : Color.BLACK);
            frontXOffsetSpinner.setValue(task.frontTextXOffset);
            frontYOffsetSpinner.setValue(task.frontTextYOffset);

            centerXOffsetSpinner.setValue(task.centerTextXOffset);
            centerYOffsetSpinner.setValue(task.centerTextYOffset);

            aboveTextField.setText(task.aboveText);
            aboveFontSizeSpinner.setValue(task.aboveFontSize);
            aboveBoldBtn.setSelected(task.aboveFontBold);
            aboveItalicBtn.setSelected(task.aboveFontItalic);
            aboveTextColorBtn.setBackground(task.aboveTextColor != null ? task.aboveTextColor : Color.BLACK);
            aboveXOffsetSpinner.setValue(task.aboveTextXOffset);
            aboveYOffsetSpinner.setValue(task.aboveTextYOffset);

            underneathTextField.setText(task.underneathText);
            underneathFontSizeSpinner.setValue(task.underneathFontSize);
            underneathBoldBtn.setSelected(task.underneathFontBold);
            underneathItalicBtn.setSelected(task.underneathFontItalic);
            underneathTextColorBtn.setBackground(task.underneathTextColor != null ? task.underneathTextColor : Color.BLACK);
            underneathXOffsetSpinner.setValue(task.underneathTextXOffset);
            underneathYOffsetSpinner.setValue(task.underneathTextYOffset);

            behindTextField.setText(task.behindText);
            behindFontSizeSpinner.setValue(task.behindFontSize);
            behindBoldBtn.setSelected(task.behindFontBold);
            behindItalicBtn.setSelected(task.behindFontItalic);
            behindTextColorBtn.setBackground(task.behindTextColor != null ? task.behindTextColor : new Color(150, 150, 150));
            behindXOffsetSpinner.setValue(task.behindTextXOffset);
            behindYOffsetSpinner.setValue(task.behindTextYOffset);

            // Notes
            note1Field.setText(task.note1);
            note2Field.setText(task.note2);
            note3Field.setText(task.note3);
            note4Field.setText(task.note4);
            note5Field.setText(task.note5);

            setFormatFieldsEnabled(true);
        } else {
            // Multiple selection - show blank fields for batch editing
            formatTitleLabel.setText(selectedTaskIndices.size() + " tasks selected");
            formatTitleLabel.setForeground(Color.BLUE);
            duplicateTaskBtn.setEnabled(true);

            // Clear text fields but keep them enabled for batch input
            taskNameField.setText("");
            taskStartField.setText("");
            taskEndField.setText("");
            centerTextField.setText("");
            frontTextField.setText("");
            aboveTextField.setText("");
            underneathTextField.setText("");
            behindTextField.setText("");

            // Reset spinners to default but keep enabled
            outlineThicknessSpinner.setValue(2);
            taskHeightSpinner.setValue(25);
            fontSizeSpinner.setValue(11);
            frontFontSizeSpinner.setValue(10);
            aboveFontSizeSpinner.setValue(10);
            underneathFontSizeSpinner.setValue(10);
            behindFontSizeSpinner.setValue(10);
            // Reset offset spinners
            frontXOffsetSpinner.setValue(0);
            frontYOffsetSpinner.setValue(0);
            centerXOffsetSpinner.setValue(0);
            centerYOffsetSpinner.setValue(0);
            aboveXOffsetSpinner.setValue(0);
            aboveYOffsetSpinner.setValue(0);
            underneathXOffsetSpinner.setValue(0);
            underneathYOffsetSpinner.setValue(0);
            behindXOffsetSpinner.setValue(0);
            behindYOffsetSpinner.setValue(0);

            // Reset toggle buttons
            boldBtn.setSelected(false);
            italicBtn.setSelected(false);
            frontBoldBtn.setSelected(false);
            frontItalicBtn.setSelected(false);
            aboveBoldBtn.setSelected(false);
            aboveItalicBtn.setSelected(false);
            underneathBoldBtn.setSelected(false);
            underneathItalicBtn.setSelected(false);
            behindBoldBtn.setSelected(false);
            behindItalicBtn.setSelected(false);

            // Set color buttons to null/gray
            fillColorBtn.setBackground(Color.GRAY);
            outlineColorBtn.setBackground(Color.GRAY);
            textColorBtn.setBackground(Color.GRAY);
            frontTextColorBtn.setBackground(Color.GRAY);
            aboveTextColorBtn.setBackground(Color.GRAY);
            underneathTextColorBtn.setBackground(Color.GRAY);
            behindTextColorBtn.setBackground(Color.GRAY);

            // Clear notes
            note1Field.setText("");
            note2Field.setText("");
            note3Field.setText("");
            note4Field.setText("");
            note5Field.setText("");

            setFormatFieldsEnabled(true);
        }
    }

    private void setFormatFieldsEnabled(boolean enabled) {
        taskNameField.setEnabled(enabled);
        taskStartField.setEnabled(enabled);
        taskEndField.setEnabled(enabled);
        fillColorBtn.setEnabled(enabled);
        outlineColorBtn.setEnabled(enabled);
        outlineThicknessSpinner.setEnabled(enabled);
        taskHeightSpinner.setEnabled(enabled);
        centerTextField.setEnabled(enabled);
        fontSizeSpinner.setEnabled(enabled);
        boldBtn.setEnabled(enabled);
        italicBtn.setEnabled(enabled);
        textColorBtn.setEnabled(enabled);
        frontTextField.setEnabled(enabled);
        frontFontSizeSpinner.setEnabled(enabled);
        frontBoldBtn.setEnabled(enabled);
        frontItalicBtn.setEnabled(enabled);
        frontTextColorBtn.setEnabled(enabled);
        frontXOffsetSpinner.setEnabled(enabled);
        frontYOffsetSpinner.setEnabled(enabled);
        centerXOffsetSpinner.setEnabled(enabled);
        centerYOffsetSpinner.setEnabled(enabled);
        aboveTextField.setEnabled(enabled);
        aboveFontSizeSpinner.setEnabled(enabled);
        aboveBoldBtn.setEnabled(enabled);
        aboveItalicBtn.setEnabled(enabled);
        aboveTextColorBtn.setEnabled(enabled);
        aboveXOffsetSpinner.setEnabled(enabled);
        aboveYOffsetSpinner.setEnabled(enabled);
        underneathTextField.setEnabled(enabled);
        underneathFontSizeSpinner.setEnabled(enabled);
        underneathBoldBtn.setEnabled(enabled);
        underneathItalicBtn.setEnabled(enabled);
        underneathTextColorBtn.setEnabled(enabled);
        underneathXOffsetSpinner.setEnabled(enabled);
        underneathYOffsetSpinner.setEnabled(enabled);
        behindTextField.setEnabled(enabled);
        behindFontSizeSpinner.setEnabled(enabled);
        behindBoldBtn.setEnabled(enabled);
        behindItalicBtn.setEnabled(enabled);
        behindTextColorBtn.setEnabled(enabled);
        behindXOffsetSpinner.setEnabled(enabled);
        behindYOffsetSpinner.setEnabled(enabled);
        // Notes
        note1Field.setEnabled(enabled);
        note2Field.setEnabled(enabled);
        note3Field.setEnabled(enabled);
        note4Field.setEnabled(enabled);
        note5Field.setEnabled(enabled);
    }

    private void clearFormatFields() {
        taskNameField.setText("");
        taskStartField.setText("");
        taskEndField.setText("");
        fillColorBtn.setBackground(null);
        outlineColorBtn.setBackground(null);
        outlineThicknessSpinner.setValue(2);
        taskHeightSpinner.setValue(25);
        centerTextField.setText("");
        fontSizeSpinner.setValue(11);
        boldBtn.setSelected(false);
        italicBtn.setSelected(false);
        textColorBtn.setBackground(null);
        frontTextField.setText("");
        frontFontSizeSpinner.setValue(10);
        frontBoldBtn.setSelected(false);
        frontItalicBtn.setSelected(false);
        frontTextColorBtn.setBackground(null);
        frontXOffsetSpinner.setValue(0);
        frontYOffsetSpinner.setValue(0);
        centerXOffsetSpinner.setValue(0);
        centerYOffsetSpinner.setValue(0);
        aboveTextField.setText("");
        aboveFontSizeSpinner.setValue(10);
        aboveBoldBtn.setSelected(false);
        aboveItalicBtn.setSelected(false);
        aboveTextColorBtn.setBackground(null);
        aboveXOffsetSpinner.setValue(0);
        aboveYOffsetSpinner.setValue(0);
        underneathTextField.setText("");
        underneathFontSizeSpinner.setValue(10);
        underneathBoldBtn.setSelected(false);
        underneathItalicBtn.setSelected(false);
        underneathTextColorBtn.setBackground(null);
        underneathXOffsetSpinner.setValue(0);
        underneathYOffsetSpinner.setValue(0);
        behindTextField.setText("");
        behindFontSizeSpinner.setValue(10);
        behindBoldBtn.setSelected(false);
        behindItalicBtn.setSelected(false);
        behindTextColorBtn.setBackground(null);
        behindXOffsetSpinner.setValue(0);
        behindYOffsetSpinner.setValue(0);
        // Notes
        note1Field.setText("");
        note2Field.setText("");
        note3Field.setText("");
        note4Field.setText("");
        note5Field.setText("");
    }

    void selectMilestone(int index) {
        selectedMilestoneIndex = index;
        // Deselect tasks when milestone is selected and switch to milestone row view
        if (index >= 0) {
            selectedTaskIndices.clear();
            row1CardLayout.show(row1Container, "milestone");
        }

        if (index >= 0 && index < milestones.size()) {
            TimelineMilestone milestone = milestones.get(index);
            milestoneNameField.setText(milestone.name);
            milestoneNameField.setEnabled(true);
            milestoneDateField.setText(milestone.date);
            milestoneDateField.setEnabled(true);
            milestoneFillColorBtn.setBackground(milestone.fillColor);
            milestoneFillColorBtn.setEnabled(true);
            milestoneOutlineColorBtn.setBackground(milestone.outlineColor);
            milestoneOutlineColorBtn.setEnabled(true);
            milestoneOutlineThicknessSpinner.setValue(milestone.outlineThickness);
            milestoneOutlineThicknessSpinner.setEnabled(true);
            milestoneHeightSpinner.setValue(milestone.height);
            milestoneHeightSpinner.setEnabled(true);
            milestoneWidthSpinner.setValue(milestone.width);
            milestoneWidthSpinner.setEnabled(true);
        } else {
            // Show task row when no milestone is selected
            row1CardLayout.show(row1Container, "task");
        }
        timelineDisplayPanel.repaint();
        if (layersPanel != null) {
            // Find the correct index in layerOrder for this milestone
            if (index >= 0 && index < milestones.size()) {
                TimelineMilestone milestone = milestones.get(index);
                int layerIndex = layerOrder.indexOf(milestone);
                layersPanel.setSelectedLayer(layerIndex);
            } else {
                layersPanel.setSelectedLayer(-1);
            }
        }
    }

    private void duplicateSelectedTasks() {
        if (selectedTaskIndices.isEmpty()) return;

        // Collect tasks to duplicate (copy indices to avoid modification during iteration)
        java.util.List<Integer> indicesToDuplicate = new java.util.ArrayList<>(selectedTaskIndices);
        java.util.List<TimelineTask> newTasks = new java.util.ArrayList<>();

        for (int idx : indicesToDuplicate) {
            TimelineTask original = tasks.get(idx);
            TimelineTask copy = new TimelineTask(original.name + " (copy)", original.startDate, original.endDate);

            // Copy all properties
            copy.centerText = original.centerText;
            copy.fillColor = original.fillColor;
            copy.outlineColor = original.outlineColor;
            copy.outlineThickness = original.outlineThickness;
            copy.height = original.height;
            copy.yPosition = original.yPosition >= 0 ? original.yPosition + original.height + 10 : -1;
            copy.fontSize = original.fontSize;
            copy.fontBold = original.fontBold;
            copy.fontItalic = original.fontItalic;
            copy.textColor = original.textColor;
            copy.centerTextXOffset = original.centerTextXOffset;
            copy.centerTextYOffset = original.centerTextYOffset;
            // Front text
            copy.frontText = original.frontText;
            copy.frontFontSize = original.frontFontSize;
            copy.frontFontBold = original.frontFontBold;
            copy.frontFontItalic = original.frontFontItalic;
            copy.frontTextColor = original.frontTextColor;
            copy.frontTextXOffset = original.frontTextXOffset;
            copy.frontTextYOffset = original.frontTextYOffset;
            // Above text
            copy.aboveText = original.aboveText;
            copy.aboveFontSize = original.aboveFontSize;
            copy.aboveFontBold = original.aboveFontBold;
            copy.aboveFontItalic = original.aboveFontItalic;
            copy.aboveTextColor = original.aboveTextColor;
            copy.aboveTextXOffset = original.aboveTextXOffset;
            copy.aboveTextYOffset = original.aboveTextYOffset;
            // Underneath text
            copy.underneathText = original.underneathText;
            copy.underneathFontSize = original.underneathFontSize;
            copy.underneathFontBold = original.underneathFontBold;
            copy.underneathFontItalic = original.underneathFontItalic;
            copy.underneathTextColor = original.underneathTextColor;
            copy.underneathTextXOffset = original.underneathTextXOffset;
            copy.underneathTextYOffset = original.underneathTextYOffset;
            // Behind text
            copy.behindText = original.behindText;
            copy.behindFontSize = original.behindFontSize;
            copy.behindFontBold = original.behindFontBold;
            copy.behindFontItalic = original.behindFontItalic;
            copy.behindTextColor = original.behindTextColor;
            copy.behindTextXOffset = original.behindTextXOffset;
            copy.behindTextYOffset = original.behindTextYOffset;
            // Notes
            copy.note1 = original.note1;
            copy.note2 = original.note2;
            copy.note3 = original.note3;
            copy.note4 = original.note4;
            copy.note5 = original.note5;

            newTasks.add(copy);
        }

        // Add all new tasks and update layer order
        for (TimelineTask newTask : newTasks) {
            tasks.add(newTask);
            layerOrder.add(0, newTask); // Add to top of layer order
        }

        // Select the new tasks
        selectedTaskIndices.clear();
        for (TimelineTask newTask : newTasks) {
            selectedTaskIndices.add(tasks.indexOf(newTask));
        }

        updateFormatPanelForSelection();
        refreshTimeline();
    }

    private void updateSelectedTaskName() {
        if (selectedTaskIndices.size() == 1) {
            int index = selectedTaskIndices.iterator().next();
            String newName = taskNameField.getText().trim();
            if (!newName.isEmpty()) {
                tasks.get(index).name = newName;
                formatTitleLabel.setText("Selected: " + newName);
                refreshTimeline();
            }
        }
    }

    private void updateSelectedTaskDates() {
        if (selectedTaskIndices.isEmpty()) return;
        String newStart = taskStartField.getText().trim();
        String newEnd = taskEndField.getText().trim();
        try {
            LocalDate.parse(newStart, DATE_FORMAT);
            LocalDate.parse(newEnd, DATE_FORMAT);
            for (int idx : selectedTaskIndices) {
                TimelineTask task = tasks.get(idx);
                task.startDate = newStart;
                task.endDate = newEnd;
            }
            refreshTimeline();
        } catch (Exception ex) {
            // Invalid date, revert for single selection
            if (selectedTaskIndices.size() == 1) {
                TimelineTask task = tasks.get(selectedTaskIndices.iterator().next());
                taskStartField.setText(task.startDate);
                taskEndField.setText(task.endDate);
            }
        }
    }

    private void addNewTask() {
        int taskIndex = tasks.size();
        String taskName = "Task " + (taskIndex + 1);
        String startDate = LocalDate.now().format(DATE_FORMAT);
        String endDate = LocalDate.now().plusWeeks(2).format(DATE_FORMAT);

        TimelineTask task = new TimelineTask(taskName, startDate, endDate);
        // Assign a default color at creation so it stays with the task when reordered
        task.fillColor = TASK_COLORS[taskIndex % TASK_COLORS.length];
        tasks.add(task);
        layerOrder.add(0, task); // Add to top of layer order
        refreshTimeline();
    }

    private void showMilestoneShapeMenu(JButton button) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));

        String[] shapes = {"Diamond", "Circle", "Triangle", "Star", "Square", "Hexagon"};
        int buttonWidth = button.getWidth();

        for (String shape : shapes) {
            JMenuItem item = new JMenuItem(shape) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // Draw shape icon on the left
                    int cy = getHeight() / 2;
                    int size = 10;
                    g2d.setColor(new Color(80, 80, 80));
                    drawMilestoneShape(g2d, shape.toLowerCase(), 20, cy, size, size, true);
                    g2d.setColor(Color.BLACK);
                    drawMilestoneShape(g2d, shape.toLowerCase(), 20, cy, size, size, false);
                }
            };
            item.setPreferredSize(new Dimension(buttonWidth, 28));
            item.setBorder(BorderFactory.createEmptyBorder(4, 35, 4, 10));
            item.addActionListener(e -> addNewMilestone(shape.toLowerCase()));
            popup.add(item);
        }

        // Show popup directly under the button
        popup.show(button, 0, button.getHeight());
    }

    private void addNewMilestone(String shape) {
        int milestoneIndex = milestones.size();
        String name = "Milestone " + (milestoneIndex + 1);
        String date = LocalDate.now().plusWeeks(1).format(DATE_FORMAT);

        TimelineMilestone milestone = new TimelineMilestone(name, date, shape);
        milestone.fillColor = TASK_COLORS[(milestoneIndex + 3) % TASK_COLORS.length];
        milestones.add(milestone);
        layerOrder.add(0, milestone); // Add to top of layer order
        refreshTimeline();
    }

    private void drawMilestoneShape(Graphics2D g2d, String shape, int cx, int cy, int w, int h, boolean fill) {
        int[] xPoints, yPoints;
        switch (shape) {
            case "diamond":
                xPoints = new int[]{cx, cx + w, cx, cx - w};
                yPoints = new int[]{cy - h, cy, cy + h, cy};
                if (fill) g2d.fillPolygon(xPoints, yPoints, 4);
                else g2d.drawPolygon(xPoints, yPoints, 4);
                break;
            case "circle":
                if (fill) g2d.fillOval(cx - w, cy - h, w * 2, h * 2);
                else g2d.drawOval(cx - w, cy - h, w * 2, h * 2);
                break;
            case "triangle":
                xPoints = new int[]{cx, cx + w, cx - w};
                yPoints = new int[]{cy - h, cy + h, cy + h};
                if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                else g2d.drawPolygon(xPoints, yPoints, 3);
                break;
            case "star":
                drawStar(g2d, cx, cy, w, h, fill);
                break;
            case "square":
                if (fill) g2d.fillRect(cx - w, cy - h, w * 2, h * 2);
                else g2d.drawRect(cx - w, cy - h, w * 2, h * 2);
                break;
            case "hexagon":
                int hw = w * 2 / 3;
                xPoints = new int[]{cx - w, cx - hw, cx + hw, cx + w, cx + hw, cx - hw};
                yPoints = new int[]{cy, cy - h, cy - h, cy, cy + h, cy + h};
                if (fill) g2d.fillPolygon(xPoints, yPoints, 6);
                else g2d.drawPolygon(xPoints, yPoints, 6);
                break;
        }
    }

    private void drawStar(Graphics2D g2d, int cx, int cy, int outerR, int innerR, boolean fill) {
        int[] xPoints = new int[10];
        int[] yPoints = new int[10];
        int inner = innerR / 2;
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 2 + i * Math.PI / 5;
            int r = (i % 2 == 0) ? outerR : inner;
            xPoints[i] = cx + (int)(r * Math.cos(angle));
            yPoints[i] = cy - (int)(r * Math.sin(angle));
        }
        if (fill) g2d.fillPolygon(xPoints, yPoints, 10);
        else g2d.drawPolygon(xPoints, yPoints, 10);
    }

    private void deleteTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            // Update selection indices
            Set<Integer> newSelection = new HashSet<>();
            for (int idx : selectedTaskIndices) {
                if (idx == index) continue; // Remove deleted index
                if (idx > index) {
                    newSelection.add(idx - 1); // Shift down
                } else {
                    newSelection.add(idx);
                }
            }
            selectedTaskIndices = newSelection;
            updateFormatPanelForSelection();
            refreshTimeline();
        }
    }

    void updateFormatPanelDates(int index) {
        if (index >= 0 && index < tasks.size() && selectedTaskIndices.contains(index)) {
            TimelineTask task = tasks.get(index);
            taskStartField.setText(task.startDate);
            taskEndField.setText(task.endDate);
        }
    }

    void updateFormatPanelMilestoneDate(int index) {
        if (index >= 0 && index < milestones.size() && index == selectedMilestoneIndex) {
            TimelineMilestone milestone = milestones.get(index);
            milestoneDateField.setText(milestone.date);
        }
    }

    void moveTask(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex >= tasks.size() || toIndex < 0 || toIndex >= tasks.size()) return;
        if (fromIndex == toIndex) return;

        TimelineTask task = tasks.remove(fromIndex);
        tasks.add(toIndex, task);

        // Update selected indices
        Set<Integer> newSelection = new HashSet<>();
        for (int idx : selectedTaskIndices) {
            if (idx == fromIndex) {
                newSelection.add(toIndex);
            } else if (fromIndex < idx && toIndex >= idx) {
                newSelection.add(idx - 1);
            } else if (fromIndex > idx && toIndex <= idx) {
                newSelection.add(idx + 1);
            } else {
                newSelection.add(idx);
            }
        }
        selectedTaskIndices = newSelection;

        refreshTimeline();
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 250, 250));

        // Timeline Range Section
        addSectionHeader(panel, "Timeline Range");

        // Date range - labels above fields, side by side
        JPanel dateContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dateContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateContainer.setOpaque(false);
        dateContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // Start date column
        JPanel startCol = new JPanel();
        startCol.setLayout(new BoxLayout(startCol, BoxLayout.Y_AXIS));
        startCol.setOpaque(false);
        JLabel startLabel = new JLabel("Start");
        startLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        startCol.add(startLabel);
        startDateField = new JTextField(LocalDate.now().format(DATE_FORMAT), 8);
        startDateField.addActionListener(e -> refreshTimeline());
        startCol.add(startDateField);
        dateContainer.add(startCol);

        // End date column
        JPanel endCol = new JPanel();
        endCol.setLayout(new BoxLayout(endCol, BoxLayout.Y_AXIS));
        endCol.setOpaque(false);
        JLabel endLabel = new JLabel("End");
        endLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        endCol.add(endLabel);
        endDateField = new JTextField(LocalDate.now().plusMonths(3).format(DATE_FORMAT), 8);
        endDateField.addActionListener(e -> refreshTimeline());
        endCol.add(endDateField);
        dateContainer.add(endCol);

        panel.add(dateContainer);

        // Dual-handle range slider for date range
        LocalDate sliderBaseDate = LocalDate.now().minusYears(2).minusMonths(6);
        int totalDays = 365 * 5;  // 5 year span
        int startDayOffset = (int) java.time.temporal.ChronoUnit.DAYS.between(sliderBaseDate, LocalDate.now());
        int endDayOffset = (int) java.time.temporal.ChronoUnit.DAYS.between(sliderBaseDate, LocalDate.now().plusMonths(3));

        RangeSlider rangeSlider = new RangeSlider(0, totalDays, startDayOffset, endDayOffset);
        rangeSlider.setMaximumSize(new Dimension(210, 25));
        rangeSlider.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Connect range slider to date fields
        rangeSlider.addChangeListener(e -> {
            LocalDate startDate = sliderBaseDate.plusDays(rangeSlider.getLowValue());
            LocalDate endDate = sliderBaseDate.plusDays(rangeSlider.getHighValue());
            startDateField.setText(startDate.format(DATE_FORMAT));
            endDateField.setText(endDate.format(DATE_FORMAT));
            if (!rangeSlider.getValueIsAdjusting()) refreshTimeline();
        });

        // Connect date fields to range slider
        startDateField.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(startDateField.getText().trim(), DATE_FORMAT);
                int days = (int) java.time.temporal.ChronoUnit.DAYS.between(sliderBaseDate, date);
                rangeSlider.setLowValue(Math.max(0, Math.min(rangeSlider.getHighValue(), days)));
            } catch (Exception ex) {}
        });
        endDateField.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(endDateField.getText().trim(), DATE_FORMAT);
                int days = (int) java.time.temporal.ChronoUnit.DAYS.between(sliderBaseDate, date);
                rangeSlider.setHighValue(Math.max(rangeSlider.getLowValue(), Math.min(totalDays, days)));
            } catch (Exception ex) {}
        });

        panel.add(rangeSlider);
        panel.add(Box.createVerticalStrut(10));

        // Timeline Appearance Section
        addSectionHeader(panel, "Appearance");

        JPanel bgColorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        bgColorRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        bgColorRow.setOpaque(false);
        bgColorRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel bgLabel = new JLabel("Background:");
        bgLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        bgColorRow.add(bgLabel);

        timelineBgColorBtn = new JButton();
        timelineBgColorBtn.setPreferredSize(new Dimension(30, 20));
        timelineBgColorBtn.setBackground(timelineInteriorColor);
        timelineBgColorBtn.setToolTipText("Click to change timeline background color");
        timelineBgColorBtn.addActionListener(e -> chooseTimelineBackgroundColor());
        bgColorRow.add(timelineBgColorBtn);

        panel.add(bgColorRow);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    // Helper methods for building UI
    private void addSectionHeader(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
    }

    private void addLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(3));
    }

    private JTextField addTextField(JPanel panel, String defaultValue) {
        JTextField field = new JTextField(defaultValue);
        field.setMaximumSize(new Dimension(170, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));
        return field;
    }

    private void clearAll() {
        events.clear();
        tasks.clear();
        milestones.clear();
        layerOrder.clear();
        selectTask(-1);
        selectMilestone(-1);
        refreshTimeline();
    }

    private void refreshTimeline() {
        try {
            LocalDate startDate = LocalDate.parse(startDateField.getText().trim(), DATE_FORMAT);
            LocalDate endDate = LocalDate.parse(endDateField.getText().trim(), DATE_FORMAT);

            if (endDate.isBefore(startDate)) {
                showWarning("End date must be after start date.");
                return;
            }

            timelineDisplayPanel.updateTimeline(startDate, endDate, events, tasks, milestones);
            layersPanel.refreshLayers();
        } catch (Exception e) {
            showWarning("Please enter valid dates in YYYY-MM-DD format.");
        }
    }

    private void showPreferencesDialog() {
        JDialog dialog = new JDialog(this, "Preferences", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);

        JTabbedPane tabbedPane = new JTabbedPane();

        // General Tab - overview table
        JPanel generalTab = new JPanel(new GridLayout(5, 5, 5, 5));
        generalTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        generalTab.add(new JLabel(""));
        generalTab.add(createCenteredLabel("Interior"));
        generalTab.add(createCenteredLabel("Outline"));
        generalTab.add(createCenteredLabel("Header"));
        generalTab.add(createCenteredLabel("Head Text"));
        generalTab.add(new JLabel("Settings"));
        generalTab.add(createColorBtn(dialog, settingsInteriorColor, c -> settingsInteriorColor = c));
        generalTab.add(createColorBtn(dialog, settingsOutlineColor, c -> settingsOutlineColor = c));
        generalTab.add(createColorBtn(dialog, settingsHeaderColor, c -> settingsHeaderColor = c));
        generalTab.add(createColorBtn(dialog, settingsHeaderTextColor, c -> settingsHeaderTextColor = c));
        generalTab.add(new JLabel("Timeline"));
        generalTab.add(createColorBtn(dialog, timelineInteriorColor, c -> timelineInteriorColor = c));
        generalTab.add(createColorBtn(dialog, timelineOutlineColor, c -> timelineOutlineColor = c));
        generalTab.add(new JLabel(""));
        generalTab.add(new JLabel(""));
        generalTab.add(new JLabel("Layers"));
        generalTab.add(createColorBtn(dialog, layersInteriorColor, c -> layersInteriorColor = c));
        generalTab.add(createColorBtn(dialog, layersOutlineColor, c -> layersOutlineColor = c));
        generalTab.add(createColorBtn(dialog, layersHeaderColor, c -> layersHeaderColor = c));
        generalTab.add(createColorBtn(dialog, layersHeaderTextColor, c -> layersHeaderTextColor = c));
        generalTab.add(new JLabel("Format"));
        generalTab.add(createColorBtn(dialog, formatInteriorColor, c -> formatInteriorColor = c));
        generalTab.add(createColorBtn(dialog, formatOutlineColor, c -> formatOutlineColor = c));
        generalTab.add(createColorBtn(dialog, formatHeaderColor, c -> formatHeaderColor = c));
        generalTab.add(new JLabel(""));
        tabbedPane.addTab("General", generalTab);

        // Settings Tab
        JPanel settingsTab = new JPanel(new GridLayout(8, 2, 10, 8));
        settingsTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addColorRow(settingsTab, dialog, "Interior:", settingsInteriorColor, c -> settingsInteriorColor = c);
        addColorRow(settingsTab, dialog, "Outline:", settingsOutlineColor, c -> settingsOutlineColor = c);
        addColorRow(settingsTab, dialog, "Header Background:", settingsHeaderColor, c -> settingsHeaderColor = c);
        addColorRow(settingsTab, dialog, "Header Text:", settingsHeaderTextColor, c -> settingsHeaderTextColor = c);
        addColorRow(settingsTab, dialog, "Labels:", settingsLabelColor, c -> settingsLabelColor = c);
        addColorRow(settingsTab, dialog, "Field Background:", settingsFieldBgColor, c -> settingsFieldBgColor = c);
        addColorRow(settingsTab, dialog, "Button Background:", settingsButtonBgColor, c -> settingsButtonBgColor = c);
        addColorRow(settingsTab, dialog, "Button Text:", settingsButtonTextColor, c -> settingsButtonTextColor = c);
        tabbedPane.addTab("Settings", settingsTab);

        // Timeline Tab
        JPanel timelineTab = new JPanel(new GridLayout(6, 2, 10, 8));
        timelineTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addColorRow(timelineTab, dialog, "Interior:", timelineInteriorColor, c -> timelineInteriorColor = c);
        addColorRow(timelineTab, dialog, "Outline:", timelineOutlineColor, c -> timelineOutlineColor = c);
        addColorRow(timelineTab, dialog, "Timeline Line:", timelineLineColor, c -> timelineLineColor = c);
        addColorRow(timelineTab, dialog, "Date Text:", timelineDateTextColor, c -> timelineDateTextColor = c);
        addColorRow(timelineTab, dialog, "Grid Lines:", timelineGridColor, c -> timelineGridColor = c);
        addColorRow(timelineTab, dialog, "Event Markers:", timelineEventColor, c -> timelineEventColor = c);
        tabbedPane.addTab("Timeline", timelineTab);

        // Layers Tab
        JPanel layersTab = new JPanel(new GridLayout(8, 2, 10, 8));
        layersTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addColorRow(layersTab, dialog, "Interior:", layersInteriorColor, c -> layersInteriorColor = c);
        addColorRow(layersTab, dialog, "Outline:", layersOutlineColor, c -> layersOutlineColor = c);
        addColorRow(layersTab, dialog, "Header Background:", layersHeaderColor, c -> layersHeaderColor = c);
        addColorRow(layersTab, dialog, "Header Text:", layersHeaderTextColor, c -> layersHeaderTextColor = c);
        addColorRow(layersTab, dialog, "List Background:", layersListBgColor, c -> layersListBgColor = c);
        addColorRow(layersTab, dialog, "Item Text:", layersItemTextColor, c -> layersItemTextColor = c);
        addColorRow(layersTab, dialog, "Selected Background:", layersSelectedBgColor, c -> layersSelectedBgColor = c);
        addColorRow(layersTab, dialog, "Drag Handle:", layersDragHandleColor, c -> layersDragHandleColor = c);
        tabbedPane.addTab("Layers", layersTab);

        // Format Tab
        JPanel formatTab = new JPanel(new GridLayout(6, 2, 10, 8));
        formatTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addColorRow(formatTab, dialog, "Interior:", formatInteriorColor, c -> formatInteriorColor = c);
        addColorRow(formatTab, dialog, "Outline:", formatOutlineColor, c -> formatOutlineColor = c);
        addColorRow(formatTab, dialog, "Header/Title:", formatHeaderColor, c -> formatHeaderColor = c);
        addColorRow(formatTab, dialog, "Labels:", formatLabelColor, c -> formatLabelColor = c);
        addColorRow(formatTab, dialog, "Separator:", formatSeparatorColor, c -> formatSeparatorColor = c);
        addColorRow(formatTab, dialog, "Resize Handle:", formatResizeHandleColor, c -> formatResizeHandleColor = c);
        tabbedPane.addTab("Format", formatTab);

        dialog.add(tabbedPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyBtn = new JButton("Apply");
        applyBtn.addActionListener(e -> {
            applyPanelColors();
            dialog.dispose();
        });
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonsPanel.add(applyBtn);
        buttonsPanel.add(cancelBtn);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void addColorRow(JPanel panel, JDialog dialog, String label, Color initialColor, java.util.function.Consumer<Color> setter) {
        panel.add(new JLabel(label));
        panel.add(createColorBtn(dialog, initialColor, setter));
    }

    private JLabel createCenteredLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        return label;
    }

    private JButton createColorBtn(JDialog dialog, Color initialColor, java.util.function.Consumer<Color> setter) {
        JButton btn = new JButton();
        btn.setBackground(initialColor);
        btn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(dialog, "Choose Color", btn.getBackground());
            if (c != null) {
                setter.accept(c);
                btn.setBackground(c);
            }
        });
        return btn;
    }

    private void applyPanelColors() {
        // Apply settings panel colors
        if (leftPanel != null) {
            leftPanel.applyColors(settingsInteriorColor, settingsOutlineColor, settingsHeaderColor, settingsHeaderTextColor);
        }
        // Apply timeline panel colors
        if (timelineDisplayPanel != null) {
            timelineDisplayPanel.setBackground(timelineInteriorColor);
        }
        // Apply layers panel colors
        if (rightPanel != null) {
            rightPanel.applyColors(layersInteriorColor, layersOutlineColor, layersHeaderColor, layersHeaderTextColor);
        }
        // Apply format panel colors
        if (formatPanel != null) {
            formatPanel.setBackground(formatInteriorColor);
            applyColorToChildren(formatPanel, formatInteriorColor);
        }
        repaint();
    }

    private void applyColorToChildren(JPanel panel, Color color) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                if (p.isOpaque()) {
                    p.setBackground(color);
                }
                applyColorToChildren(p, color);
            }
        }
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    // Export timeline data to Excel .xlsx file with multiple sheets
    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Timeline Data");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        fileChooser.setSelectedFile(new File("timeline_export.xlsx"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }

            try {
                createExcelFile(file);
                JOptionPane.showMessageDialog(this,
                        "Exported " + tasks.size() + " tasks and " + milestones.size() + " milestones to:\n" + file.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error exporting file: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createExcelFile(File file) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {
            // [Content_Types].xml
            zos.putNextEntry(new ZipEntry("[Content_Types].xml"));
            zos.write(getContentTypesXml().getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            // _rels/.rels
            zos.putNextEntry(new ZipEntry("_rels/.rels"));
            zos.write(getRelsXml().getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            // xl/workbook.xml
            zos.putNextEntry(new ZipEntry("xl/workbook.xml"));
            zos.write(getWorkbookXml().getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            // xl/_rels/workbook.xml.rels
            zos.putNextEntry(new ZipEntry("xl/_rels/workbook.xml.rels"));
            zos.write(getWorkbookRelsXml().getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            // xl/styles.xml
            zos.putNextEntry(new ZipEntry("xl/styles.xml"));
            zos.write(getStylesXml().getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            // Sheet 1: Basic Data
            zos.putNextEntry(new ZipEntry("xl/worksheets/sheet1.xml"));
            zos.write(getSheet1Xml().getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            // Sheet 2: Format Data
            zos.putNextEntry(new ZipEntry("xl/worksheets/sheet2.xml"));
            zos.write(getSheet2Xml().getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            // Sheet 3: Settings
            zos.putNextEntry(new ZipEntry("xl/worksheets/sheet3.xml"));
            zos.write(getSheet3Xml().getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
    }

    private String getContentTypesXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n" +
               "  <Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n" +
               "  <Default Extension=\"xml\" ContentType=\"application/xml\"/>\n" +
               "  <Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>\n" +
               "  <Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n" +
               "  <Override PartName=\"/xl/worksheets/sheet2.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n" +
               "  <Override PartName=\"/xl/worksheets/sheet3.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n" +
               "  <Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>\n" +
               "</Types>";
    }

    private String getRelsXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
               "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>\n" +
               "</Relationships>";
    }

    private String getWorkbookXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">\n" +
               "  <sheets>\n" +
               "    <sheet name=\"Basic Data\" sheetId=\"1\" r:id=\"rId1\"/>\n" +
               "    <sheet name=\"Format Data\" sheetId=\"2\" r:id=\"rId2\"/>\n" +
               "    <sheet name=\"Settings\" sheetId=\"3\" r:id=\"rId3\"/>\n" +
               "  </sheets>\n" +
               "</workbook>";
    }

    private String getWorkbookRelsXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
               "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>\n" +
               "  <Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet2.xml\"/>\n" +
               "  <Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet3.xml\"/>\n" +
               "  <Relationship Id=\"rId4\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>\n" +
               "</Relationships>";
    }

    private String getStylesXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n" +
               "  <fonts count=\"2\">\n" +
               "    <font><sz val=\"11\"/><name val=\"Calibri\"/></font>\n" +
               "    <font><b/><sz val=\"11\"/><name val=\"Calibri\"/></font>\n" +
               "  </fonts>\n" +
               "  <fills count=\"2\">\n" +
               "    <fill><patternFill patternType=\"none\"/></fill>\n" +
               "    <fill><patternFill patternType=\"gray125\"/></fill>\n" +
               "  </fills>\n" +
               "  <borders count=\"1\"><border/></borders>\n" +
               "  <cellStyleXfs count=\"1\"><xf/></cellStyleXfs>\n" +
               "  <cellXfs count=\"2\">\n" +
               "    <xf/>\n" +
               "    <xf fontId=\"1\" applyFont=\"1\"/>\n" +
               "  </cellXfs>\n" +
               "</styleSheet>";
    }

    // Sheet 1: Basic Data (Type, Name, Center Text, Start Date, End Date)
    private String getSheet1Xml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n");
        sb.append("  <sheetData>\n");

        // Header row
        sb.append("    <row r=\"1\">\n");
        sb.append("      <c r=\"A1\" t=\"inlineStr\" s=\"1\"><is><t>Type</t></is></c>\n");
        sb.append("      <c r=\"B1\" t=\"inlineStr\" s=\"1\"><is><t>Name</t></is></c>\n");
        sb.append("      <c r=\"C1\" t=\"inlineStr\" s=\"1\"><is><t>Center Text</t></is></c>\n");
        sb.append("      <c r=\"D1\" t=\"inlineStr\" s=\"1\"><is><t>Start Date</t></is></c>\n");
        sb.append("      <c r=\"E1\" t=\"inlineStr\" s=\"1\"><is><t>End Date</t></is></c>\n");
        sb.append("    </row>\n");

        int row = 2;
        // Tasks
        for (TimelineTask task : tasks) {
            sb.append("    <row r=\"").append(row).append("\">\n");
            sb.append("      <c r=\"A").append(row).append("\" t=\"inlineStr\"><is><t>Task</t></is></c>\n");
            sb.append("      <c r=\"B").append(row).append("\" t=\"inlineStr\"><is><t>").append(escapeXml(task.name)).append("</t></is></c>\n");
            sb.append("      <c r=\"C").append(row).append("\" t=\"inlineStr\"><is><t>").append(escapeXml(task.centerText)).append("</t></is></c>\n");
            sb.append("      <c r=\"D").append(row).append("\" t=\"inlineStr\"><is><t>").append(escapeXml(task.startDate)).append("</t></is></c>\n");
            sb.append("      <c r=\"E").append(row).append("\" t=\"inlineStr\"><is><t>").append(escapeXml(task.endDate)).append("</t></is></c>\n");
            sb.append("    </row>\n");
            row++;
        }
        // Milestones
        for (TimelineMilestone m : milestones) {
            sb.append("    <row r=\"").append(row).append("\">\n");
            sb.append("      <c r=\"A").append(row).append("\" t=\"inlineStr\"><is><t>Milestone</t></is></c>\n");
            sb.append("      <c r=\"B").append(row).append("\" t=\"inlineStr\"><is><t>").append(escapeXml(m.name)).append("</t></is></c>\n");
            sb.append("      <c r=\"C").append(row).append("\" t=\"inlineStr\"><is><t></t></is></c>\n");
            sb.append("      <c r=\"D").append(row).append("\" t=\"inlineStr\"><is><t>").append(escapeXml(m.date)).append("</t></is></c>\n");
            sb.append("      <c r=\"E").append(row).append("\" t=\"inlineStr\"><is><t>").append(escapeXml(m.date)).append("</t></is></c>\n");
            sb.append("    </row>\n");
            row++;
        }

        sb.append("  </sheetData>\n");
        sb.append("</worksheet>");
        return sb.toString();
    }

    // Sheet 2: Format Data (all task/milestone formatting properties)
    private String getSheet2Xml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n");
        sb.append("  <sheetData>\n");

        // Header row
        String[] headers = {"Type", "Name", "Layer Index", "Fill Color", "Outline Color", "Outline Thickness", "Height", "Y Position",
                "Font Size", "Font Bold", "Font Italic", "Text Color",
                "Front Text", "Front Font Size", "Front Bold", "Front Italic", "Front Text Color",
                "Above Text", "Above Font Size", "Above Bold", "Above Italic", "Above Text Color",
                "Underneath Text", "Underneath Font Size", "Underneath Bold", "Underneath Italic", "Underneath Text Color",
                "Behind Text", "Behind Font Size", "Behind Bold", "Behind Italic", "Behind Text Color",
                "Shape", "Width", "Label Text"};
        sb.append("    <row r=\"1\">\n");
        for (int i = 0; i < headers.length; i++) {
            sb.append("      <c r=\"").append(getExcelColumn(i)).append("1\" t=\"inlineStr\" s=\"1\"><is><t>").append(headers[i]).append("</t></is></c>\n");
        }
        sb.append("    </row>\n");

        int row = 2;
        // Tasks
        for (TimelineTask task : tasks) {
            int layerIdx = layerOrder.indexOf(task);
            String[] values = {
                "Task", escapeXml(task.name), String.valueOf(layerIdx),
                colorToHex(task.fillColor), colorToHex(task.outlineColor), String.valueOf(task.outlineThickness),
                String.valueOf(task.height), String.valueOf(task.yPosition), String.valueOf(task.fontSize),
                String.valueOf(task.fontBold), String.valueOf(task.fontItalic), colorToHex(task.textColor),
                escapeXml(task.frontText), String.valueOf(task.frontFontSize), String.valueOf(task.frontFontBold),
                String.valueOf(task.frontFontItalic), colorToHex(task.frontTextColor),
                escapeXml(task.aboveText), String.valueOf(task.aboveFontSize), String.valueOf(task.aboveFontBold),
                String.valueOf(task.aboveFontItalic), colorToHex(task.aboveTextColor),
                escapeXml(task.underneathText), String.valueOf(task.underneathFontSize), String.valueOf(task.underneathFontBold),
                String.valueOf(task.underneathFontItalic), colorToHex(task.underneathTextColor),
                escapeXml(task.behindText), String.valueOf(task.behindFontSize), String.valueOf(task.behindFontBold),
                String.valueOf(task.behindFontItalic), colorToHex(task.behindTextColor),
                "", "", "" // Shape, Width, Label Text (not applicable for tasks)
            };
            sb.append("    <row r=\"").append(row).append("\">\n");
            for (int i = 0; i < values.length; i++) {
                sb.append("      <c r=\"").append(getExcelColumn(i)).append(row).append("\" t=\"inlineStr\"><is><t>").append(values[i]).append("</t></is></c>\n");
            }
            sb.append("    </row>\n");
            row++;
        }
        // Milestones
        for (TimelineMilestone m : milestones) {
            int layerIdx = layerOrder.indexOf(m);
            String[] values = {
                "Milestone", escapeXml(m.name), String.valueOf(layerIdx),
                colorToHex(m.fillColor), colorToHex(m.outlineColor), String.valueOf(m.outlineThickness),
                String.valueOf(m.height), String.valueOf(m.yPosition), String.valueOf(m.fontSize),
                String.valueOf(m.fontBold), String.valueOf(m.fontItalic), colorToHex(m.textColor),
                "", "", "", "", "", // Front text fields (not applicable)
                "", "", "", "", "", // Above text fields (not applicable)
                "", "", "", "", "", // Underneath text fields (not applicable)
                "", "", "", "", "", // Behind text fields (not applicable)
                escapeXml(m.shape), String.valueOf(m.width), escapeXml(m.labelText)
            };
            sb.append("    <row r=\"").append(row).append("\">\n");
            for (int i = 0; i < values.length; i++) {
                sb.append("      <c r=\"").append(getExcelColumn(i)).append(row).append("\" t=\"inlineStr\"><is><t>").append(values[i]).append("</t></is></c>\n");
            }
            sb.append("    </row>\n");
            row++;
        }

        sb.append("  </sheetData>\n");
        sb.append("</worksheet>");
        return sb.toString();
    }

    // Sheet 3: Settings
    private String getSheet3Xml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n");
        sb.append("  <sheetData>\n");

        // Header row
        sb.append("    <row r=\"1\">\n");
        sb.append("      <c r=\"A1\" t=\"inlineStr\" s=\"1\"><is><t>Setting</t></is></c>\n");
        sb.append("      <c r=\"B1\" t=\"inlineStr\" s=\"1\"><is><t>Value</t></is></c>\n");
        sb.append("    </row>\n");

        int row = 2;
        // Timeline date range
        row = addSettingRow(sb, row, "Timeline Start Date", startDateField.getText());
        row = addSettingRow(sb, row, "Timeline End Date", endDateField.getText());
        // Timeline colors
        row = addSettingRow(sb, row, "Timeline Background Color", colorToHex(timelineInteriorColor));
        row = addSettingRow(sb, row, "Timeline Outline Color", colorToHex(timelineOutlineColor));
        row = addSettingRow(sb, row, "Timeline Line Color", colorToHex(timelineLineColor));
        row = addSettingRow(sb, row, "Timeline Date Text Color", colorToHex(timelineDateTextColor));
        row = addSettingRow(sb, row, "Timeline Grid Color", colorToHex(timelineGridColor));
        row = addSettingRow(sb, row, "Timeline Event Color", colorToHex(timelineEventColor));
        // Settings panel colors
        row = addSettingRow(sb, row, "Settings Interior Color", colorToHex(settingsInteriorColor));
        row = addSettingRow(sb, row, "Settings Outline Color", colorToHex(settingsOutlineColor));
        row = addSettingRow(sb, row, "Settings Header Color", colorToHex(settingsHeaderColor));
        row = addSettingRow(sb, row, "Settings Header Text Color", colorToHex(settingsHeaderTextColor));
        row = addSettingRow(sb, row, "Settings Label Color", colorToHex(settingsLabelColor));
        row = addSettingRow(sb, row, "Settings Field Background Color", colorToHex(settingsFieldBgColor));
        row = addSettingRow(sb, row, "Settings Button Background Color", colorToHex(settingsButtonBgColor));
        row = addSettingRow(sb, row, "Settings Button Text Color", colorToHex(settingsButtonTextColor));
        // Layers panel colors
        row = addSettingRow(sb, row, "Layers Interior Color", colorToHex(layersInteriorColor));
        row = addSettingRow(sb, row, "Layers Outline Color", colorToHex(layersOutlineColor));
        row = addSettingRow(sb, row, "Layers Header Color", colorToHex(layersHeaderColor));
        row = addSettingRow(sb, row, "Layers Header Text Color", colorToHex(layersHeaderTextColor));
        row = addSettingRow(sb, row, "Layers List Background Color", colorToHex(layersListBgColor));
        row = addSettingRow(sb, row, "Layers Item Text Color", colorToHex(layersItemTextColor));
        row = addSettingRow(sb, row, "Layers Selected Background Color", colorToHex(layersSelectedBgColor));
        row = addSettingRow(sb, row, "Layers Drag Handle Color", colorToHex(layersDragHandleColor));
        // Format panel colors
        row = addSettingRow(sb, row, "Format Interior Color", colorToHex(formatInteriorColor));
        row = addSettingRow(sb, row, "Format Outline Color", colorToHex(formatOutlineColor));
        row = addSettingRow(sb, row, "Format Header Color", colorToHex(formatHeaderColor));
        row = addSettingRow(sb, row, "Format Label Color", colorToHex(formatLabelColor));
        row = addSettingRow(sb, row, "Format Separator Color", colorToHex(formatSeparatorColor));
        addSettingRow(sb, row, "Format Resize Handle Color", colorToHex(formatResizeHandleColor));

        sb.append("  </sheetData>\n");
        sb.append("</worksheet>");
        return sb.toString();
    }

    private int addSettingRow(StringBuilder sb, int row, String setting, String value) {
        sb.append("    <row r=\"").append(row).append("\">\n");
        sb.append("      <c r=\"A").append(row).append("\" t=\"inlineStr\"><is><t>").append(escapeXml(setting)).append("</t></is></c>\n");
        sb.append("      <c r=\"B").append(row).append("\" t=\"inlineStr\"><is><t>").append(escapeXml(value)).append("</t></is></c>\n");
        sb.append("    </row>\n");
        return row + 1;
    }

    private String colorToHex(Color c) {
        if (c == null) return "";
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    private Color hexToColor(String hex) {
        if (hex == null || hex.isEmpty() || !hex.startsWith("#")) return null;
        try {
            return new Color(
                Integer.parseInt(hex.substring(1, 3), 16),
                Integer.parseInt(hex.substring(3, 5), 16),
                Integer.parseInt(hex.substring(5, 7), 16)
            );
        } catch (Exception e) {
            return null;
        }
    }

    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }

    // Convert column index (0-based) to Excel column letters (A, B, ..., Z, AA, AB, ...)
    private String getExcelColumn(int index) {
        StringBuilder sb = new StringBuilder();
        index++; // Convert to 1-based
        while (index > 0) {
            index--;
            sb.insert(0, (char) ('A' + (index % 26)));
            index /= 26;
        }
        return sb.toString();
    }

    // Import timeline data from Excel .xlsx file
    private void importFromExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Timeline Data");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            int choice = JOptionPane.showOptionDialog(this,
                    "Do you want to replace existing items or add to them?",
                    "Import Options",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Replace All", "Add to Existing", "Cancel"},
                    "Add to Existing");

            if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) return;
            boolean replaceAll = (choice == 0);

            try {
                Map<String, String> sheets = readExcelSheets(file);
                int[] counts = importFromSheets(sheets, replaceAll);

                layersPanel.refreshLayers();
                refreshTimeline();

                JOptionPane.showMessageDialog(this,
                        "Imported " + counts[0] + " tasks and " + counts[1] + " milestones.",
                        "Import Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error importing file: " + ex.getMessage(),
                        "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Map<String, String> readExcelSheets(File file) throws IOException {
        Map<String, String> sheets = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().startsWith("xl/worksheets/sheet")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                    sheets.put(entry.getName(), baos.toString(StandardCharsets.UTF_8.name()));
                }
                zis.closeEntry();
            }
        }
        return sheets;
    }

    private int[] importFromSheets(Map<String, String> sheets, boolean replaceAll) {
        if (replaceAll) {
            tasks.clear();
            milestones.clear();
            layerOrder.clear();
            selectedTaskIndices.clear();
            selectedMilestoneIndex = -1;
        }

        int tasksImported = 0, milestonesImported = 0;

        // Parse Sheet 1 (Basic Data) and Sheet 2 (Format Data)
        String sheet1 = sheets.get("xl/worksheets/sheet1.xml");
        String sheet2 = sheets.get("xl/worksheets/sheet2.xml");
        String sheet3 = sheets.get("xl/worksheets/sheet3.xml");

        if (sheet1 != null) {
            ArrayList<String[]> rows = parseSheetRows(sheet1);
            ArrayList<String[]> formatRows = sheet2 != null ? parseSheetRows(sheet2) : new ArrayList<>();

            // Create a map of name to format data
            Map<String, String[]> formatMap = new HashMap<>();
            for (int i = 1; i < formatRows.size(); i++) {
                String[] fr = formatRows.get(i);
                if (fr.length > 1) formatMap.put(fr[1], fr);
            }

            for (int i = 1; i < rows.size(); i++) {
                String[] r = rows.get(i);
                if (r.length < 5) continue;

                String type = r[0];
                String name = r[1];
                String centerText = r.length > 2 ? r[2] : "";
                String startDate = r.length > 3 ? r[3] : "";
                String endDate = r.length > 4 ? r[4] : "";

                String[] fmt = formatMap.get(name);

                if ("Task".equalsIgnoreCase(type)) {
                    TimelineTask task = new TimelineTask(name, startDate, endDate);
                    task.centerText = centerText.isEmpty() ? name : centerText;
                    task.fillColor = TASK_COLORS[tasks.size() % TASK_COLORS.length];

                    // Apply format data if available
                    if (fmt != null && fmt.length > 10) {
                        task.fillColor = hexToColor(fmt[3]) != null ? hexToColor(fmt[3]) : task.fillColor;
                        task.outlineColor = hexToColor(fmt[4]);
                        task.outlineThickness = parseIntSafe(fmt[5], 2);
                        task.height = parseIntSafe(fmt[6], 25);
                        task.yPosition = parseIntSafe(fmt[7], -1);
                        task.fontSize = parseIntSafe(fmt[8], 11);
                        task.fontBold = "true".equalsIgnoreCase(fmt[9]);
                        task.fontItalic = "true".equalsIgnoreCase(fmt[10]);
                        task.textColor = hexToColor(fmt[11]) != null ? hexToColor(fmt[11]) : Color.BLACK;
                        if (fmt.length > 16) {
                            task.frontText = fmt[12];
                            task.frontFontSize = parseIntSafe(fmt[13], 10);
                            task.frontFontBold = "true".equalsIgnoreCase(fmt[14]);
                            task.frontFontItalic = "true".equalsIgnoreCase(fmt[15]);
                            task.frontTextColor = hexToColor(fmt[16]) != null ? hexToColor(fmt[16]) : Color.BLACK;
                        }
                        if (fmt.length > 21) {
                            task.aboveText = fmt[17];
                            task.aboveFontSize = parseIntSafe(fmt[18], 10);
                            task.aboveFontBold = "true".equalsIgnoreCase(fmt[19]);
                            task.aboveFontItalic = "true".equalsIgnoreCase(fmt[20]);
                            task.aboveTextColor = hexToColor(fmt[21]) != null ? hexToColor(fmt[21]) : Color.BLACK;
                        }
                        if (fmt.length > 26) {
                            task.underneathText = fmt[22];
                            task.underneathFontSize = parseIntSafe(fmt[23], 10);
                            task.underneathFontBold = "true".equalsIgnoreCase(fmt[24]);
                            task.underneathFontItalic = "true".equalsIgnoreCase(fmt[25]);
                            task.underneathTextColor = hexToColor(fmt[26]) != null ? hexToColor(fmt[26]) : Color.BLACK;
                        }
                        if (fmt.length > 31) {
                            task.behindText = fmt[27];
                            task.behindFontSize = parseIntSafe(fmt[28], 10);
                            task.behindFontBold = "true".equalsIgnoreCase(fmt[29]);
                            task.behindFontItalic = "true".equalsIgnoreCase(fmt[30]);
                            task.behindTextColor = hexToColor(fmt[31]) != null ? hexToColor(fmt[31]) : new Color(150, 150, 150);
                        }
                    }

                    tasks.add(task);
                    layerOrder.add(task);
                    tasksImported++;
                } else if ("Milestone".equalsIgnoreCase(type)) {
                    String shape = "diamond";
                    if (fmt != null && fmt.length > 32) {
                        shape = fmt[32].isEmpty() ? "diamond" : fmt[32];
                    }
                    TimelineMilestone m = new TimelineMilestone(name, startDate, shape);
                    m.fillColor = TASK_COLORS[(milestones.size() + 3) % TASK_COLORS.length];

                    if (fmt != null && fmt.length > 10) {
                        m.fillColor = hexToColor(fmt[3]) != null ? hexToColor(fmt[3]) : m.fillColor;
                        m.outlineColor = hexToColor(fmt[4]) != null ? hexToColor(fmt[4]) : Color.BLACK;
                        m.outlineThickness = parseIntSafe(fmt[5], 2);
                        m.height = parseIntSafe(fmt[6], 20);
                        m.yPosition = parseIntSafe(fmt[7], -1);
                        m.fontSize = parseIntSafe(fmt[8], 10);
                        m.fontBold = "true".equalsIgnoreCase(fmt[9]);
                        m.fontItalic = "true".equalsIgnoreCase(fmt[10]);
                        m.textColor = hexToColor(fmt[11]) != null ? hexToColor(fmt[11]) : Color.BLACK;
                        if (fmt.length > 33) m.width = parseIntSafe(fmt[33], 20);
                        if (fmt.length > 34) m.labelText = fmt[34];
                    }

                    milestones.add(m);
                    layerOrder.add(m);
                    milestonesImported++;
                }
            }
        }

        // Import settings from Sheet 3
        if (sheet3 != null) {
            ArrayList<String[]> settingRows = parseSheetRows(sheet3);
            for (int i = 1; i < settingRows.size(); i++) {
                String[] r = settingRows.get(i);
                if (r.length < 2) continue;
                String setting = r[0];
                String value = r[1];
                applySettingValue(setting, value);
            }
        }

        return new int[]{tasksImported, milestonesImported};
    }

    private void applySettingValue(String setting, String value) {
        switch (setting) {
            case "Timeline Start Date": startDateField.setText(value); break;
            case "Timeline End Date": endDateField.setText(value); break;
            case "Timeline Background Color": timelineInteriorColor = hexToColor(value) != null ? hexToColor(value) : timelineInteriorColor; break;
            case "Timeline Outline Color": timelineOutlineColor = hexToColor(value) != null ? hexToColor(value) : timelineOutlineColor; break;
            case "Timeline Line Color": timelineLineColor = hexToColor(value) != null ? hexToColor(value) : timelineLineColor; break;
            case "Timeline Date Text Color": timelineDateTextColor = hexToColor(value) != null ? hexToColor(value) : timelineDateTextColor; break;
            case "Timeline Grid Color": timelineGridColor = hexToColor(value) != null ? hexToColor(value) : timelineGridColor; break;
            case "Timeline Event Color": timelineEventColor = hexToColor(value) != null ? hexToColor(value) : timelineEventColor; break;
            case "Settings Interior Color": settingsInteriorColor = hexToColor(value) != null ? hexToColor(value) : settingsInteriorColor; break;
            case "Settings Outline Color": settingsOutlineColor = hexToColor(value) != null ? hexToColor(value) : settingsOutlineColor; break;
            case "Settings Header Color": settingsHeaderColor = hexToColor(value) != null ? hexToColor(value) : settingsHeaderColor; break;
            case "Settings Header Text Color": settingsHeaderTextColor = hexToColor(value) != null ? hexToColor(value) : settingsHeaderTextColor; break;
            case "Settings Label Color": settingsLabelColor = hexToColor(value) != null ? hexToColor(value) : settingsLabelColor; break;
            case "Settings Field Background Color": settingsFieldBgColor = hexToColor(value) != null ? hexToColor(value) : settingsFieldBgColor; break;
            case "Settings Button Background Color": settingsButtonBgColor = hexToColor(value) != null ? hexToColor(value) : settingsButtonBgColor; break;
            case "Settings Button Text Color": settingsButtonTextColor = hexToColor(value) != null ? hexToColor(value) : settingsButtonTextColor; break;
            case "Layers Interior Color": layersInteriorColor = hexToColor(value) != null ? hexToColor(value) : layersInteriorColor; break;
            case "Layers Outline Color": layersOutlineColor = hexToColor(value) != null ? hexToColor(value) : layersOutlineColor; break;
            case "Layers Header Color": layersHeaderColor = hexToColor(value) != null ? hexToColor(value) : layersHeaderColor; break;
            case "Layers Header Text Color": layersHeaderTextColor = hexToColor(value) != null ? hexToColor(value) : layersHeaderTextColor; break;
            case "Layers List Background Color": layersListBgColor = hexToColor(value) != null ? hexToColor(value) : layersListBgColor; break;
            case "Layers Item Text Color": layersItemTextColor = hexToColor(value) != null ? hexToColor(value) : layersItemTextColor; break;
            case "Layers Selected Background Color": layersSelectedBgColor = hexToColor(value) != null ? hexToColor(value) : layersSelectedBgColor; break;
            case "Layers Drag Handle Color": layersDragHandleColor = hexToColor(value) != null ? hexToColor(value) : layersDragHandleColor; break;
            case "Format Interior Color": formatInteriorColor = hexToColor(value) != null ? hexToColor(value) : formatInteriorColor; break;
            case "Format Outline Color": formatOutlineColor = hexToColor(value) != null ? hexToColor(value) : formatOutlineColor; break;
            case "Format Header Color": formatHeaderColor = hexToColor(value) != null ? hexToColor(value) : formatHeaderColor; break;
            case "Format Label Color": formatLabelColor = hexToColor(value) != null ? hexToColor(value) : formatLabelColor; break;
            case "Format Separator Color": formatSeparatorColor = hexToColor(value) != null ? hexToColor(value) : formatSeparatorColor; break;
            case "Format Resize Handle Color": formatResizeHandleColor = hexToColor(value) != null ? hexToColor(value) : formatResizeHandleColor; break;
        }
    }

    private ArrayList<String[]> parseSheetRows(String sheetXml) {
        ArrayList<String[]> rows = new ArrayList<>();
        int rowStart = 0;
        while ((rowStart = sheetXml.indexOf("<row ", rowStart)) != -1) {
            int rowEnd = sheetXml.indexOf("</row>", rowStart);
            if (rowEnd == -1) break;
            String rowXml = sheetXml.substring(rowStart, rowEnd);

            ArrayList<String> cells = new ArrayList<>();
            int cellStart = 0;
            while ((cellStart = rowXml.indexOf("<c ", cellStart)) != -1) {
                int cellEnd = rowXml.indexOf("</c>", cellStart);
                if (cellEnd == -1) {
                    cellEnd = rowXml.indexOf("/>", cellStart);
                    if (cellEnd == -1) break;
                    cellStart = cellEnd + 2;
                    cells.add("");
                    continue;
                }
                String cellXml = rowXml.substring(cellStart, cellEnd);

                // Extract value
                String value = "";
                int tStart = cellXml.indexOf("<t>");
                int tEnd = cellXml.indexOf("</t>");
                if (tStart != -1 && tEnd != -1) {
                    value = cellXml.substring(tStart + 3, tEnd);
                    value = value.replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'");
                } else {
                    int vStart = cellXml.indexOf("<v>");
                    int vEnd = cellXml.indexOf("</v>");
                    if (vStart != -1 && vEnd != -1) {
                        value = cellXml.substring(vStart + 3, vEnd);
                    }
                }
                cells.add(value);
                cellStart = cellEnd + 4;
            }
            rows.add(cells.toArray(new String[0]));
            rowStart = rowEnd + 6;
        }
        return rows;
    }

    private int parseIntSafe(String s, int defaultVal) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    // ==================== Inner Classes ====================

    // Range Slider with two handles
    class RangeSlider extends JPanel {
        private int min, max, lowValue, highValue;
        private boolean draggingLow = false, draggingHigh = false;
        private boolean valueIsAdjusting = false;
        private java.util.List<javax.swing.event.ChangeListener> changeListeners = new java.util.ArrayList<>();
        private static final int THUMB_WIDTH = 12;
        private static final int THUMB_HEIGHT = 16;
        private static final int TRACK_HEIGHT = 4;

        RangeSlider(int min, int max, int lowValue, int highValue) {
            this.min = min;
            this.max = max;
            this.lowValue = lowValue;
            this.highValue = highValue;
            setPreferredSize(new Dimension(200, 25));
            setOpaque(false);

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int lowX = valueToX(RangeSlider.this.lowValue);
                    int highX = valueToX(RangeSlider.this.highValue);
                    int mx = e.getX();

                    // Check which thumb is closer
                    if (Math.abs(mx - lowX) <= THUMB_WIDTH && Math.abs(mx - lowX) <= Math.abs(mx - highX)) {
                        draggingLow = true;
                    } else if (Math.abs(mx - highX) <= THUMB_WIDTH) {
                        draggingHigh = true;
                    }
                    valueIsAdjusting = true;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    draggingLow = false;
                    draggingHigh = false;
                    valueIsAdjusting = false;
                    fireChangeEvent();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    int newVal = xToValue(e.getX());
                    if (draggingLow) {
                        setLowValue(Math.min(newVal, RangeSlider.this.highValue));
                    } else if (draggingHigh) {
                        setHighValue(Math.max(newVal, RangeSlider.this.lowValue));
                    }
                }
            };
            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        }

        private int valueToX(int value) {
            int trackWidth = getWidth() - THUMB_WIDTH;
            return (int) ((value - min) / (double) (max - min) * trackWidth) + THUMB_WIDTH / 2;
        }

        private int xToValue(int x) {
            int trackWidth = getWidth() - THUMB_WIDTH;
            int val = (int) ((x - THUMB_WIDTH / 2) / (double) trackWidth * (max - min)) + min;
            return Math.max(min, Math.min(max, val));
        }

        public int getLowValue() { return lowValue; }
        public int getHighValue() { return highValue; }
        public boolean getValueIsAdjusting() { return valueIsAdjusting; }

        public void setLowValue(int v) {
            v = Math.max(min, Math.min(highValue, v));
            if (v != lowValue) {
                lowValue = v;
                repaint();
                fireChangeEvent();
            }
        }

        public void setHighValue(int v) {
            v = Math.max(lowValue, Math.min(max, v));
            if (v != highValue) {
                highValue = v;
                repaint();
                fireChangeEvent();
            }
        }

        public void addChangeListener(javax.swing.event.ChangeListener l) {
            changeListeners.add(l);
        }

        private void fireChangeEvent() {
            javax.swing.event.ChangeEvent e = new javax.swing.event.ChangeEvent(this);
            for (javax.swing.event.ChangeListener l : changeListeners) {
                l.stateChanged(e);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int trackY = h / 2 - TRACK_HEIGHT / 2;

            // Track background
            g2d.setColor(new Color(200, 200, 200));
            g2d.fillRoundRect(THUMB_WIDTH / 2, trackY, w - THUMB_WIDTH, TRACK_HEIGHT, 2, 2);

            // Selected range
            int lowX = valueToX(lowValue);
            int highX = valueToX(highValue);
            g2d.setColor(new Color(70, 130, 180));
            g2d.fillRect(lowX, trackY, highX - lowX, TRACK_HEIGHT);

            // Low thumb
            g2d.setColor(new Color(70, 130, 180));
            int[] xPointsLow = {lowX - THUMB_WIDTH / 2, lowX + THUMB_WIDTH / 2, lowX};
            int[] yPointsLow = {h / 2 - THUMB_HEIGHT / 2, h / 2 - THUMB_HEIGHT / 2, h / 2 + THUMB_HEIGHT / 2};
            g2d.fillPolygon(xPointsLow, yPointsLow, 3);
            g2d.setColor(new Color(50, 100, 150));
            g2d.drawPolygon(xPointsLow, yPointsLow, 3);

            // High thumb
            g2d.setColor(new Color(70, 130, 180));
            int[] xPointsHigh = {highX - THUMB_WIDTH / 2, highX + THUMB_WIDTH / 2, highX};
            int[] yPointsHigh = {h / 2 - THUMB_HEIGHT / 2, h / 2 - THUMB_HEIGHT / 2, h / 2 + THUMB_HEIGHT / 2};
            g2d.fillPolygon(xPointsHigh, yPointsHigh, 3);
            g2d.setColor(new Color(50, 100, 150));
            g2d.drawPolygon(xPointsHigh, yPointsHigh, 3);

            g2d.dispose();
        }
    }

    // Collapsible Panel
    class CollapsiblePanel extends JPanel {
        private JPanel content;
        private JPanel header;
        private JButton collapseBtn;
        private boolean collapsed = false;
        private boolean isLeft;
        private String title;
        private Dimension expandedSize = new Dimension(230, 600);

        CollapsiblePanel(String title, JPanel content, boolean isLeft) {
            this.title = title;
            this.content = content;
            this.isLeft = isLeft;

            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

            // Header
            header = new JPanel(new BorderLayout(5, 0));
            header.setBackground(new Color(70, 130, 180));
            header.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 5));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
            header.add(titleLabel, BorderLayout.CENTER);

            // Single collapse/expand button with arrow (direction depends on side)
            String collapseArrow = isLeft ? "\u25C0" : "\u25B6";  // Left arrow for left panel, right arrow for right panel
            collapseBtn = createHeaderButton(collapseArrow, "Collapse");
            collapseBtn.addActionListener(e -> toggleCollapse());
            header.add(collapseBtn, BorderLayout.EAST);
            add(header, BorderLayout.NORTH);

            // Content
            JPanel contentWrapper = new JPanel(new BorderLayout());
            contentWrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            contentWrapper.add(content, BorderLayout.CENTER);
            add(contentWrapper, BorderLayout.CENTER);

            setPreferredSize(expandedSize);
        }

        private JButton createHeaderButton(String text, String tooltip) {
            JButton btn = new JButton(text);
            btn.setToolTipText(tooltip);
            btn.setMargin(new Insets(0, 5, 0, 5));
            btn.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));
            return btn;
        }

        void toggleCollapse() {
            collapsed = !collapsed;

            if (collapsed) {
                setPreferredSize(new Dimension(30, 600));
                // When collapsed: show arrow pointing toward the panel to expand
                collapseBtn.setText(isLeft ? "\u25B6" : "\u25C0");  // Right arrow for left panel, left arrow for right panel
                collapseBtn.setToolTipText("Expand");
                for (Component c : getComponents()) {
                    if (c != header) c.setVisible(false);
                }
                header.setPreferredSize(new Dimension(30, 600));
            } else {
                setPreferredSize(expandedSize);
                // When expanded: show arrow pointing away from center to collapse
                collapseBtn.setText(isLeft ? "\u25C0" : "\u25B6");  // Left arrow for left panel, right arrow for right panel
                collapseBtn.setToolTipText("Collapse");
                for (Component c : getComponents()) {
                    c.setVisible(true);
                }
                header.setPreferredSize(null);
            }
            revalidate();
            Timeline2.this.revalidate();
            Timeline2.this.repaint();
        }

        void applyColors(Color interior, Color outline, Color headerBg, Color headerText) {
            setBackground(interior);
            setBorder(BorderFactory.createLineBorder(outline));
            header.setBackground(headerBg);
            for (Component c : header.getComponents()) {
                if (c instanceof JLabel) {
                    c.setForeground(headerText);
                }
            }
            // Apply interior color to content
            for (Component c : getComponents()) {
                if (c instanceof JPanel && c != header) {
                    applyInteriorColor((JPanel) c, interior);
                }
            }
        }

        private void applyInteriorColor(JPanel panel, Color color) {
            panel.setBackground(color);
            for (Component c : panel.getComponents()) {
                if (c instanceof JPanel) {
                    applyInteriorColor((JPanel) c, color);
                }
            }
        }
    }

    // Layers Panel for z-order control
    class LayersPanel extends JPanel {
        private DefaultListModel<String> listModel;
        private JList<String> layersList;
        private JScrollPane scrollPane;
        private boolean isDragging = false;
        private int dragOriginalIndex = -1;
        private int dropTargetIndex = -1;
        private int dragOffsetY = 0;
        private int floatingY = 0;
        private String draggedItemName = null;
        private Color draggedItemColor = null;

        LayersPanel() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            setPreferredSize(new Dimension(180, 400));

            // List with custom painting for floating item
            listModel = new DefaultListModel<>();
            layersList = new JList<String>(listModel) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    // Draw floating item on top (30% transparent = 70% opaque = alpha 178)
                    if (isDragging && draggedItemName != null) {
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        int cellHeight = getFixedCellHeight();
                        int width = getWidth() - 10;
                        int alpha = 178; // 70% opaque (30% transparent)

                        // Draw shadow
                        g2d.setColor(new Color(0, 0, 0, 20));
                        g2d.fillRoundRect(7, floatingY + 3, width, cellHeight - 2, 6, 6);

                        // Draw floating cell background (30% transparent)
                        g2d.setColor(new Color(200, 200, 200, alpha));
                        g2d.fillRoundRect(5, floatingY, width, cellHeight - 2, 6, 6);

                        // Draw border
                        g2d.setColor(new Color(150, 150, 150, alpha));
                        g2d.setStroke(new BasicStroke(1));
                        g2d.drawRoundRect(5, floatingY, width, cellHeight - 2, 6, 6);

                        // Draw color indicator
                        if (draggedItemColor != null) {
                            g2d.setColor(new Color(draggedItemColor.getRed(), draggedItemColor.getGreen(),
                                                   draggedItemColor.getBlue(), alpha));
                            g2d.fillRect(25, floatingY + 6, 14, 14);
                        }

                        // Draw drag handle
                        g2d.setColor(new Color(100, 100, 100, alpha));
                        g2d.setFont(new Font("Arial", Font.BOLD, 14));
                        g2d.drawString("\u2261", 10, floatingY + 18);

                        // Draw text
                        g2d.setColor(new Color(0, 0, 0, alpha));
                        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
                        g2d.drawString(draggedItemName, 45, floatingY + 17);
                    }
                }
            };
            layersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            layersList.setCellRenderer(new LayerCellRenderer());
            layersList.setFixedCellHeight(28);

            // Drag and drop support (handles selection too)
            setupDragAndDrop();

            scrollPane = new JScrollPane(layersList);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            add(scrollPane, BorderLayout.CENTER);

            // Buttons panel
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            buttonsPanel.setBackground(new Color(245, 245, 245));

            JButton moveUpBtn = new JButton("\u25B2");
            moveUpBtn.setToolTipText("Move layer up (to front)");
            moveUpBtn.setMargin(new Insets(2, 8, 2, 8));
            moveUpBtn.addActionListener(e -> moveLayerUp());
            buttonsPanel.add(moveUpBtn);

            JButton moveDownBtn = new JButton("\u25BC");
            moveDownBtn.setToolTipText("Move layer down (to back)");
            moveDownBtn.setMargin(new Insets(2, 8, 2, 8));
            moveDownBtn.addActionListener(e -> moveLayerDown());
            buttonsPanel.add(moveDownBtn);

            JButton deleteBtn = new JButton("\u2716");
            deleteBtn.setToolTipText("Delete layer");
            deleteBtn.setMargin(new Insets(2, 8, 2, 8));
            deleteBtn.addActionListener(e -> deleteSelectedLayer());
            buttonsPanel.add(deleteBtn);

            add(buttonsPanel, BorderLayout.SOUTH);
        }

        private void setupDragAndDrop() {
            layersList.setDragEnabled(false);

            MouseAdapter dragAdapter = new MouseAdapter() {
                private int dragStartY = -1;

                @Override
                public void mousePressed(MouseEvent e) {
                    int index = layersList.locationToIndex(e.getPoint());
                    if (index >= 0 && index < listModel.size()) {
                        dragOriginalIndex = index;
                        dropTargetIndex = index;
                        dragStartY = e.getY();
                        dragOffsetY = e.getY() - (index * layersList.getFixedCellHeight());
                        isDragging = false;
                        layersList.setSelectedIndex(index);

                        // Select the appropriate item type
                        Object item = layerOrder.get(index);
                        if (item instanceof TimelineTask) {
                            selectTask(tasks.indexOf(item));
                        } else if (item instanceof TimelineMilestone) {
                            selectMilestone(milestones.indexOf(item));
                        }

                        // Store dragged item info
                        draggedItemName = listModel.get(index);
                        if (item instanceof TimelineTask) {
                            TimelineTask task = (TimelineTask) item;
                            draggedItemColor = task.fillColor != null ? task.fillColor : TASK_COLORS[0];
                        } else if (item instanceof TimelineMilestone) {
                            TimelineMilestone milestone = (TimelineMilestone) item;
                            draggedItemColor = milestone.fillColor;
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (isDragging && dragOriginalIndex >= 0 && dropTargetIndex >= 0 && dragOriginalIndex != dropTargetIndex) {
                        // Commit the reorder to the actual tasks list
                        commitReorder(dragOriginalIndex, dropTargetIndex);
                        refreshLayers();
                        layersList.setSelectedIndex(dropTargetIndex);
                    }
                    dragOriginalIndex = -1;
                    dropTargetIndex = -1;
                    isDragging = false;
                    draggedItemName = null;
                    draggedItemColor = null;
                    layersList.setCursor(Cursor.getDefaultCursor());
                    layersList.repaint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (dragOriginalIndex < 0) return;

                    // Start dragging after moving a few pixels
                    if (!isDragging && Math.abs(e.getY() - dragStartY) > 3) {
                        isDragging = true;
                        layersList.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    }

                    if (isDragging) {
                        // Update floating position pixel by pixel
                        floatingY = e.getY() - dragOffsetY;

                        // Calculate drop target index
                        int cellHeight = layersList.getFixedCellHeight();
                        int centerY = floatingY + cellHeight / 2;
                        int newDropTarget = Math.max(0, Math.min(centerY / cellHeight, listModel.size() - 1));

                        if (newDropTarget != dropTargetIndex) {
                            dropTargetIndex = newDropTarget;
                        }

                        layersList.repaint();
                    }
                }
            };
            layersList.addMouseListener(dragAdapter);
            layersList.addMouseMotionListener(dragAdapter);
        }

        private void commitReorder(int fromIndex, int toIndex) {
            if (fromIndex < 0 || fromIndex >= layerOrder.size() || toIndex < 0 || toIndex >= layerOrder.size()) return;

            Object item = layerOrder.remove(fromIndex);
            layerOrder.add(toIndex, item);

            refreshTimeline();
        }

        void refreshLayers() {
            int selectedIndex = layersList.getSelectedIndex();
            listModel.clear();
            for (Object item : layerOrder) {
                if (item instanceof TimelineTask) {
                    listModel.addElement(((TimelineTask) item).name);
                } else if (item instanceof TimelineMilestone) {
                    listModel.addElement("\u25C6 " + ((TimelineMilestone) item).name); // Diamond prefix for milestones
                }
            }
            if (selectedIndex >= 0 && selectedIndex < listModel.size()) {
                layersList.setSelectedIndex(selectedIndex);
            }
        }

        void setSelectedLayer(int index) {
            if (index >= 0 && index < listModel.size()) {
                layersList.setSelectedIndex(index);
            } else {
                layersList.clearSelection();
            }
        }

        private void moveLayerUp() {
            int index = layersList.getSelectedIndex();
            if (index > 0) {
                commitReorder(index, index - 1);
                refreshLayers();
                layersList.setSelectedIndex(index - 1);
            }
        }

        private void moveLayerDown() {
            int index = layersList.getSelectedIndex();
            if (index >= 0 && index < listModel.size() - 1) {
                commitReorder(index, index + 1);
                refreshLayers();
                layersList.setSelectedIndex(index + 1);
            }
        }

        private void deleteSelectedLayer() {
            int index = layersList.getSelectedIndex();
            if (index >= 0 && index < layerOrder.size()) {
                Object item = layerOrder.get(index);
                layerOrder.remove(index);
                if (item instanceof TimelineTask) {
                    tasks.remove(item);
                    selectTask(-1);
                } else if (item instanceof TimelineMilestone) {
                    milestones.remove(item);
                    selectMilestone(-1);
                }
                refreshTimeline();
            }
        }

        void applyColors(Color interior, Color outline, Color headerBg, Color headerText) {
            setBackground(interior);
            if (layersList != null) {
                layersList.setBackground(interior);
            }
        }

        // Custom cell renderer for layers
        class LayerCellRenderer extends JPanel implements ListCellRenderer<String> {
            private JLabel nameLabel;
            private JPanel colorIndicator;
            private JLabel dragHandleLabel;

            LayerCellRenderer() {
                setLayout(new BorderLayout(5, 0));
                setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

                // Drag handle on left
                dragHandleLabel = new JLabel("\u2261");
                dragHandleLabel.setFont(new Font("Arial", Font.BOLD, 14));
                dragHandleLabel.setForeground(new Color(150, 150, 150));
                add(dragHandleLabel, BorderLayout.WEST);

                // Color indicator
                colorIndicator = new JPanel();
                colorIndicator.setPreferredSize(new Dimension(14, 14));
                JPanel colorWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                colorWrapper.setOpaque(false);
                colorWrapper.add(colorIndicator);

                nameLabel = new JLabel();
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                colorWrapper.add(nameLabel);
                add(colorWrapper, BorderLayout.CENTER);
            }

            @Override
            public Component getListCellRendererComponent(JList<? extends String> list, String value,
                    int index, boolean isSelected, boolean cellHasFocus) {

                nameLabel.setText(value);

                // Get item color from layerOrder
                Color itemColor = TASK_COLORS[index % TASK_COLORS.length];
                if (index >= 0 && index < layerOrder.size()) {
                    Object item = layerOrder.get(index);
                    if (item instanceof TimelineTask) {
                        TimelineTask task = (TimelineTask) item;
                        itemColor = task.fillColor != null ? task.fillColor : TASK_COLORS[0];
                    } else if (item instanceof TimelineMilestone) {
                        TimelineMilestone milestone = (TimelineMilestone) item;
                        itemColor = milestone.fillColor;
                    }
                }
                colorIndicator.setBackground(itemColor);

                // When dragging, show drop indicator line
                if (isDragging && dragOriginalIndex >= 0) {
                    // Keep all items in normal white state
                    setBackground(Color.WHITE);
                    nameLabel.setForeground(Color.BLACK);
                    dragHandleLabel.setForeground(new Color(150, 150, 150));

                    // Show drop indicator line at target position
                    if (index == dropTargetIndex && dropTargetIndex != dragOriginalIndex) {
                        setBorder(BorderFactory.createMatteBorder(
                            dropTargetIndex < dragOriginalIndex ? 3 : 0, 0,
                            dropTargetIndex > dragOriginalIndex ? 3 : 0, 0,
                            new Color(70, 130, 180)));
                    } else {
                        setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                    }
                } else {
                    // Normal (not dragging) - grey highlight for selection
                    if (isSelected) {
                        setBackground(new Color(200, 200, 200));
                        nameLabel.setForeground(Color.BLACK);
                        dragHandleLabel.setForeground(new Color(100, 100, 100));
                    } else {
                        setBackground(Color.WHITE);
                        nameLabel.setForeground(Color.BLACK);
                        dragHandleLabel.setForeground(new Color(150, 150, 150));
                    }
                    setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                }

                return this;
            }
        }
    }

    // Data classes
    static class TimelineEvent {
        String title, date, description;
        TimelineEvent(String title, String date, String description) {
            this.title = title;
            this.date = date;
            this.description = description;
        }
    }

    static class TimelineTask {
        String name, startDate, endDate;
        String centerText = "";    // text displayed on the task bar
        Color fillColor = null;    // null means use default
        Color outlineColor = null; // null means use default (darker fill)
        int outlineThickness = 2;  // default thickness
        int height = 25;           // default height
        int yPosition = -1;        // Y position on timeline (-1 means auto-calculate)
        // Center text formatting properties
        int fontSize = 11;         // default font size
        boolean fontBold = false;  // default not bold
        boolean fontItalic = false; // default not italic
        Color textColor = Color.BLACK;    // default black
        int centerTextXOffset = 0; // X offset from default position
        int centerTextYOffset = 0; // Y offset from default position
        // Front text properties (text in front of task bar)
        String frontText = "";
        int frontFontSize = 10;
        boolean frontFontBold = false;
        boolean frontFontItalic = false;
        Color frontTextColor = Color.BLACK;
        int frontTextXOffset = 0;
        int frontTextYOffset = 0;
        // Above text properties (text above task bar)
        String aboveText = "";
        int aboveFontSize = 10;
        boolean aboveFontBold = false;
        boolean aboveFontItalic = false;
        Color aboveTextColor = Color.BLACK;
        int aboveTextXOffset = 0;
        int aboveTextYOffset = 0;
        // Underneath text properties (text below task bar)
        String underneathText = "";
        int underneathFontSize = 10;
        boolean underneathFontBold = false;
        boolean underneathFontItalic = false;
        Color underneathTextColor = Color.BLACK;
        int underneathTextXOffset = 0;
        int underneathTextYOffset = 0;
        // Behind text properties (text behind task bar)
        String behindText = "";
        int behindFontSize = 10;
        boolean behindFontBold = false;
        boolean behindFontItalic = false;
        Color behindTextColor = new Color(150, 150, 150);
        int behindTextXOffset = 0;
        int behindTextYOffset = 0;
        // Notes
        String note1 = "";
        String note2 = "";
        String note3 = "";
        String note4 = "";
        String note5 = "";
        TimelineTask(String name, String startDate, String endDate) {
            this.name = name;
            this.centerText = name; // default center text to name
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    static class TimelineMilestone {
        String name;
        String date;
        String shape;  // "diamond", "circle", "triangle", "star", "square", "hexagon"
        int width = 20;
        int height = 20;
        int yPosition = -1;
        Color fillColor = new Color(255, 193, 7);  // default gold/yellow
        Color outlineColor = Color.BLACK;
        int outlineThickness = 2;
        // Text properties
        String labelText = "";
        int fontSize = 10;
        boolean fontBold = false;
        boolean fontItalic = false;
        Color textColor = Color.BLACK;

        TimelineMilestone(String name, String date, String shape) {
            this.name = name;
            this.labelText = name;
            this.date = date;
            this.shape = shape;
        }
    }

    // Timeline Display Panel
    class TimelineDisplayPanel extends JPanel {
        private LocalDate startDate, endDate;
        private ArrayList<TimelineEvent> events = new ArrayList<>();
        private ArrayList<TimelineTask> tasks = new ArrayList<>();
        private ArrayList<TimelineMilestone> milestones = new ArrayList<>();

        private static final int MARGIN_LEFT = 80, MARGIN_RIGHT = 50;
        private static final int DEFAULT_TASK_HEIGHT = 25, TASK_BAR_SPACING = 5;
        private static final int DRAG_HANDLE_WIDTH = 18;

        private int draggingTaskIndex = -1;
        private boolean draggingStart = false;
        private boolean isDragging = false;

        // Free movement dragging (move entire task)
        private boolean isMoveDragging = false;
        private int moveDragTaskIndex = -1;
        private int moveDragStartX = -1;
        private int moveDragStartY = -1;
        private String moveDragOriginalStartDate = null;
        private String moveDragOriginalEndDate = null;
        private int moveDragOriginalYPosition = -1;

        // Height dragging
        private boolean isHeightDragging = false;
        private int heightDragTaskIndex = -1;
        private boolean draggingTop = false;
        private int heightDragStartY = -1;
        private int heightDragOriginalHeight = -1;

        // Milestone dragging
        private boolean isMilestoneDragging = false;
        private int milestoneDragIndex = -1;
        private int milestoneDragStartX = -1;
        private int milestoneDragStartY = -1;
        private String milestoneDragOriginalDate = null;
        private int milestoneDragOriginalYPosition = -1;

        // Milestone resize handles
        private boolean isMilestoneResizing = false;
        private int resizeHandle = -1; // 0=top, 1=bottom, 2=left, 3=right, 4=top-left, 5=top-right, 6=bottom-left, 7=bottom-right
        private int resizeStartX = -1;
        private int resizeStartY = -1;
        private int resizeOriginalWidth = -1;
        private int resizeOriginalHeight = -1;

        TimelineDisplayPanel() {
            setBackground(Color.WHITE);
            setupMouseListeners();
            setupKeyListeners();
        }

        private void setupKeyListeners() {
            setFocusable(true);
            addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (selectedTaskIndices.isEmpty()) return;

                    int daysDelta = 0;
                    int yDelta = 0;

                    switch (e.getKeyCode()) {
                        case java.awt.event.KeyEvent.VK_LEFT:
                            daysDelta = -1;
                            break;
                        case java.awt.event.KeyEvent.VK_RIGHT:
                            daysDelta = 1;
                            break;
                        case java.awt.event.KeyEvent.VK_UP:
                            yDelta = -5;
                            break;
                        case java.awt.event.KeyEvent.VK_DOWN:
                            yDelta = 5;
                            break;
                        default:
                            return;
                    }

                    // Move all selected tasks
                    for (int idx : selectedTaskIndices) {
                        TimelineTask task = tasks.get(idx);

                        // Shift dates if left/right arrow
                        if (daysDelta != 0) {
                            try {
                                LocalDate start = LocalDate.parse(task.startDate, DATE_FORMAT);
                                LocalDate end = LocalDate.parse(task.endDate, DATE_FORMAT);
                                task.startDate = start.plusDays(daysDelta).format(DATE_FORMAT);
                                task.endDate = end.plusDays(daysDelta).format(DATE_FORMAT);
                            } catch (Exception ex) {}
                        }

                        // Shift Y position if up/down arrow
                        if (yDelta != 0) {
                            int currentY = task.yPosition >= 0 ? task.yPosition : getTaskY(idx);
                            int newY = Math.max(35, currentY + yDelta);
                            task.yPosition = newY;
                        }
                    }

                    // Update format panel if single selection
                    if (selectedTaskIndices.size() == 1) {
                        int idx = selectedTaskIndices.iterator().next();
                        updateFormatPanelDates(idx);
                    }

                    repaint();
                }
            });
        }

        private int getTaskHeight(int index) {
            if (index >= 0 && index < tasks.size()) {
                return tasks.get(index).height;
            }
            return DEFAULT_TASK_HEIGHT;
        }

        private int getTaskY(int index) {
            if (index >= 0 && index < tasks.size()) {
                TimelineTask task = tasks.get(index);
                if (task.yPosition >= 0) {
                    return task.yPosition;
                }
            }
            // Auto-calculate Y position for tasks without explicit position
            int y = 45;
            for (int i = 0; i < index && i < tasks.size(); i++) {
                if (tasks.get(i).yPosition < 0) {
                    y += tasks.get(i).height + TASK_BAR_SPACING;
                }
            }
            return y;
        }

        private int getTaskYForLayer(int layerIndex) {
            // Calculate Y position based on layer order
            Object item = layerOrder.get(layerIndex);
            if (item instanceof TimelineTask) {
                TimelineTask task = (TimelineTask) item;
                if (task.yPosition >= 0) {
                    return task.yPosition;
                }
            }
            // Auto-calculate Y position based on layer order
            int y = 45;
            for (int i = layerOrder.size() - 1; i > layerIndex; i--) {
                Object layerItem = layerOrder.get(i);
                if (layerItem instanceof TimelineTask) {
                    TimelineTask task = (TimelineTask) layerItem;
                    if (task.yPosition < 0) {
                        y += task.height + TASK_BAR_SPACING;
                    }
                }
            }
            return y;
        }

        private int getTotalTasksHeight() {
            int maxY = 45;
            // Calculate based on layerOrder for proper stacking
            for (int i = layerOrder.size() - 1; i >= 0; i--) {
                Object item = layerOrder.get(i);
                if (item instanceof TimelineTask) {
                    TimelineTask task = (TimelineTask) item;
                    int taskBottom;
                    if (task.yPosition >= 0) {
                        taskBottom = task.yPosition + task.height + TASK_BAR_SPACING;
                    } else {
                        taskBottom = maxY + task.height + TASK_BAR_SPACING;
                        maxY = taskBottom;
                    }
                    if (taskBottom > maxY) {
                        maxY = taskBottom;
                    }
                }
            }
            return maxY - 25; // Subtract initial offset
        }

        private void setupMouseListeners() {
            MouseAdapter adapter = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();
                    handleMousePressed(e.getX(), e.getY(), e.isControlDown());
                }
                public void mouseReleased(MouseEvent e) {
                    if (isDragging) {
                        isDragging = false;
                        draggingTaskIndex = -1;
                        setCursor(Cursor.getDefaultCursor());
                    }
                    if (isMoveDragging) {
                        isMoveDragging = false;
                        moveDragTaskIndex = -1;
                        moveDragOriginalStartDate = null;
                        moveDragOriginalEndDate = null;
                        setCursor(Cursor.getDefaultCursor());
                        refreshTimeline();
                    }
                    if (isHeightDragging) {
                        isHeightDragging = false;
                        heightDragTaskIndex = -1;
                        setCursor(Cursor.getDefaultCursor());
                    }
                    if (isMilestoneDragging) {
                        isMilestoneDragging = false;
                        milestoneDragIndex = -1;
                        milestoneDragOriginalDate = null;
                        setCursor(Cursor.getDefaultCursor());
                        refreshTimeline();
                    }
                    if (isMilestoneResizing) {
                        isMilestoneResizing = false;
                        resizeHandle = -1;
                        setCursor(Cursor.getDefaultCursor());
                        refreshTimeline();
                    }
                }
                public void mouseDragged(MouseEvent e) {
                    if (isDragging && draggingTaskIndex >= 0) {
                        handleDrag(e.getX());
                    }
                    if (isMoveDragging) {
                        handleMoveDrag(e.getX(), e.getY());
                    }
                    if (isHeightDragging) {
                        handleHeightDrag(e.getY());
                    }
                    if (isMilestoneDragging) {
                        handleMilestoneDrag(e.getX(), e.getY());
                    }
                    if (isMilestoneResizing) {
                        handleMilestoneResize(e.getX(), e.getY());
                    }
                }
                public void mouseMoved(MouseEvent e) {
                    updateCursor(e.getX(), e.getY());
                }
            };
            addMouseListener(adapter);
            addMouseMotionListener(adapter);
        }

        private void handleMousePressed(int x, int y, boolean ctrlDown) {
            if (startDate == null || endDate == null) return;

            int timelineX = MARGIN_LEFT;
            int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            if (totalDays <= 0) return;

            int tasksHeight = getTotalTasksHeight();
            int timelineY = 50 + tasksHeight;

            // Check items in layer order (front to back - index 0 is topmost)
            for (int layerIdx = 0; layerIdx < layerOrder.size(); layerIdx++) {
                Object item = layerOrder.get(layerIdx);

                if (item instanceof TimelineTask) {
                    TimelineTask task = (TimelineTask) item;
                    int taskIdx = tasks.indexOf(task);
                    int taskY = getTaskYForLayer(layerIdx);
                    int taskHeight = task.height;
                    boolean isSelected = selectedTaskIndices.contains(taskIdx);

                    try {
                        LocalDate taskStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                        LocalDate taskEnd = LocalDate.parse(task.endDate, DATE_FORMAT);

                        int x1 = getXForDate(taskStart, timelineX, timelineWidth, totalDays);
                        int x2 = getXForDate(taskEnd, timelineX, timelineWidth, totalDays);
                        int barWidth = Math.max(x2 - x1, 10);

                        // Check for height handles on selected task (top/bottom edges)
                        if (isSelected && x >= x1 && x <= x1 + barWidth) {
                            if (y >= taskY - 6 && y <= taskY + 6) {
                                isHeightDragging = true;
                                heightDragTaskIndex = taskIdx;
                                draggingTop = true;
                                heightDragStartY = y;
                                heightDragOriginalHeight = task.height;
                                setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                                return;
                            }
                            if (y >= taskY + taskHeight - 6 && y <= taskY + taskHeight + 6) {
                                isHeightDragging = true;
                                heightDragTaskIndex = taskIdx;
                                draggingTop = false;
                                heightDragStartY = y;
                                heightDragOriginalHeight = task.height;
                                setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                                return;
                            }
                        }

                        if (y >= taskY && y <= taskY + taskHeight) {
                            if (x >= x1 - DRAG_HANDLE_WIDTH && x <= x1 + DRAG_HANDLE_WIDTH) {
                                isDragging = true;
                                draggingTaskIndex = taskIdx;
                                draggingStart = true;
                                setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                                selectTask(taskIdx, ctrlDown);
                                return;
                            }
                            if (x >= x1 + barWidth - DRAG_HANDLE_WIDTH && x <= x1 + barWidth + DRAG_HANDLE_WIDTH) {
                                isDragging = true;
                                draggingTaskIndex = taskIdx;
                                draggingStart = false;
                                setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                                selectTask(taskIdx, ctrlDown);
                                return;
                            }
                            if (x >= x1 && x <= x1 + barWidth) {
                                selectTask(taskIdx, ctrlDown);
                                isMoveDragging = true;
                                moveDragTaskIndex = taskIdx;
                                moveDragStartX = x;
                                moveDragStartY = y;
                                moveDragOriginalStartDate = task.startDate;
                                moveDragOriginalEndDate = task.endDate;
                                moveDragOriginalYPosition = taskY;
                                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                                return;
                            }
                        }
                    } catch (Exception ex) {}

                } else if (item instanceof TimelineMilestone) {
                    TimelineMilestone milestone = (TimelineMilestone) item;
                    int milestoneIdx = milestones.indexOf(milestone);

                    try {
                        LocalDate milestoneDate = LocalDate.parse(milestone.date, DATE_FORMAT);
                        if (milestoneDate.isBefore(startDate) || milestoneDate.isAfter(endDate)) continue;

                        int mx = getXForDate(milestoneDate, timelineX, timelineWidth, totalDays);
                        int my = milestone.yPosition >= 0 ? milestone.yPosition : timelineY - milestone.height / 2 - 10;
                        int halfW = milestone.width / 2;
                        int halfH = milestone.height / 2;
                        int boxPadding = 6;

                        // Check for resize handles on selected milestone
                        if (milestoneIdx == selectedMilestoneIndex) {
                            int boxX = mx - halfW - boxPadding;
                            int boxY = my - halfH - boxPadding;
                            int boxW = milestone.width + boxPadding * 2;
                            int boxH = milestone.height + boxPadding * 2;
                            int handleSize = 6;

                            int handle = getResizeHandle(x, y, boxX, boxY, boxW, boxH, handleSize);
                            if (handle >= 0) {
                                isMilestoneResizing = true;
                                resizeHandle = handle;
                                resizeStartX = x;
                                resizeStartY = y;
                                resizeOriginalWidth = milestone.width;
                                resizeOriginalHeight = milestone.height;
                                setCursor(getResizeCursor(handle));
                                return;
                            }
                        }

                        if (x >= mx - halfW - boxPadding && x <= mx + halfW + boxPadding &&
                            y >= my - halfH - boxPadding && y <= my + halfH + boxPadding) {
                            selectMilestone(milestoneIdx);
                            isMilestoneDragging = true;
                            milestoneDragIndex = milestoneIdx;
                            milestoneDragStartX = x;
                            milestoneDragStartY = y;
                            milestoneDragOriginalDate = milestone.date;
                            milestoneDragOriginalYPosition = my;
                            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                            return;
                        }
                    } catch (Exception ex) {}
                }
            }
        }

        private int getResizeHandle(int x, int y, int boxX, int boxY, int boxW, int boxH, int handleSize) {
            int tolerance = handleSize / 2 + 2;
            // Top center
            if (Math.abs(x - (boxX + boxW/2)) <= tolerance && Math.abs(y - boxY) <= tolerance) return 0;
            // Bottom center
            if (Math.abs(x - (boxX + boxW/2)) <= tolerance && Math.abs(y - (boxY + boxH)) <= tolerance) return 1;
            // Left center
            if (Math.abs(x - boxX) <= tolerance && Math.abs(y - (boxY + boxH/2)) <= tolerance) return 2;
            // Right center
            if (Math.abs(x - (boxX + boxW)) <= tolerance && Math.abs(y - (boxY + boxH/2)) <= tolerance) return 3;
            // Top-left
            if (Math.abs(x - boxX) <= tolerance && Math.abs(y - boxY) <= tolerance) return 4;
            // Top-right
            if (Math.abs(x - (boxX + boxW)) <= tolerance && Math.abs(y - boxY) <= tolerance) return 5;
            // Bottom-left
            if (Math.abs(x - boxX) <= tolerance && Math.abs(y - (boxY + boxH)) <= tolerance) return 6;
            // Bottom-right
            if (Math.abs(x - (boxX + boxW)) <= tolerance && Math.abs(y - (boxY + boxH)) <= tolerance) return 7;
            return -1;
        }

        private Cursor getResizeCursor(int handle) {
            switch (handle) {
                case 0: return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
                case 1: return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
                case 2: return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
                case 3: return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
                case 4: return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
                case 5: return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
                case 6: return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
                case 7: return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
                default: return Cursor.getDefaultCursor();
            }
        }

        private void handleDrag(int x) {
            if (draggingTaskIndex < 0 || draggingTaskIndex >= tasks.size()) return;

            int timelineX = MARGIN_LEFT;
            int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            if (totalDays <= 0) return;

            LocalDate newDate = getDateForX(x, timelineX, timelineWidth, totalDays);
            if (newDate == null) return;

            TimelineTask task = tasks.get(draggingTaskIndex);
            try {
                LocalDate taskStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                LocalDate taskEnd = LocalDate.parse(task.endDate, DATE_FORMAT);

                if (draggingStart && newDate.isBefore(taskEnd)) {
                    task.startDate = newDate.format(DATE_FORMAT);
                    updateFormatPanelDates(draggingTaskIndex);
                    repaint();
                } else if (!draggingStart && newDate.isAfter(taskStart)) {
                    task.endDate = newDate.format(DATE_FORMAT);
                    updateFormatPanelDates(draggingTaskIndex);
                    repaint();
                }
            } catch (Exception ex) {}
        }

        private void handleMoveDrag(int x, int y) {
            if (moveDragTaskIndex < 0 || moveDragTaskIndex >= tasks.size()) return;

            int timelineX = MARGIN_LEFT;
            int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            if (totalDays <= 0) return;

            TimelineTask task = tasks.get(moveDragTaskIndex);

            // Calculate horizontal movement (date shift)
            int deltaX = x - moveDragStartX;
            try {
                LocalDate origStart = LocalDate.parse(moveDragOriginalStartDate, DATE_FORMAT);
                LocalDate origEnd = LocalDate.parse(moveDragOriginalEndDate, DATE_FORMAT);
                long duration = ChronoUnit.DAYS.between(origStart, origEnd);

                // Convert pixel delta to days
                double daysPerPixel = (double) totalDays / timelineWidth;
                long daysDelta = Math.round(deltaX * daysPerPixel);

                LocalDate newStart = origStart.plusDays(daysDelta);
                LocalDate newEnd = origEnd.plusDays(daysDelta);

                task.startDate = newStart.format(DATE_FORMAT);
                task.endDate = newEnd.format(DATE_FORMAT);
                updateFormatPanelDates(moveDragTaskIndex);
            } catch (Exception ex) {}

            // Calculate vertical movement (Y position)
            int deltaY = y - moveDragStartY;
            int newY = moveDragOriginalYPosition + deltaY;
            // Clamp Y position to valid range (minimum 35 to stay below title)
            newY = Math.max(35, newY);
            task.yPosition = newY;

            repaint();
        }

        private void handleHeightDrag(int y) {
            if (heightDragTaskIndex < 0 || heightDragTaskIndex >= tasks.size()) return;

            TimelineTask task = tasks.get(heightDragTaskIndex);
            int deltaY = y - heightDragStartY;
            int newHeight;

            if (draggingTop) {
                // Dragging top up makes bar taller, down makes it shorter
                newHeight = heightDragOriginalHeight - deltaY;
            } else {
                // Dragging bottom down makes bar taller, up makes it shorter
                newHeight = heightDragOriginalHeight + deltaY;
            }

            // Clamp height to valid range (10-100)
            newHeight = Math.max(10, Math.min(100, newHeight));

            if (task.height != newHeight) {
                task.height = newHeight;
                taskHeightSpinner.setValue(newHeight);
                repaint();
            }
        }

        private void handleMilestoneDrag(int x, int y) {
            if (milestoneDragIndex < 0 || milestoneDragIndex >= milestones.size()) return;

            int timelineX = MARGIN_LEFT;
            int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            if (totalDays <= 0) return;

            TimelineMilestone milestone = milestones.get(milestoneDragIndex);

            // Calculate horizontal movement (date shift)
            int deltaX = x - milestoneDragStartX;
            try {
                LocalDate origDate = LocalDate.parse(milestoneDragOriginalDate, DATE_FORMAT);

                // Convert pixel delta to days
                double daysPerPixel = (double) totalDays / timelineWidth;
                long daysDelta = Math.round(deltaX * daysPerPixel);

                LocalDate newDate = origDate.plusDays(daysDelta);

                // Clamp to timeline range
                if (newDate.isBefore(startDate)) newDate = startDate;
                if (newDate.isAfter(endDate)) newDate = endDate;

                milestone.date = newDate.format(DATE_FORMAT);
                updateFormatPanelMilestoneDate(milestoneDragIndex);
            } catch (Exception ex) {}

            // Calculate vertical movement (Y position)
            int deltaY = y - milestoneDragStartY;
            int newY = milestoneDragOriginalYPosition + deltaY;
            // Clamp Y position to valid range (minimum 35 to stay below title)
            newY = Math.max(35, newY);
            milestone.yPosition = newY;

            repaint();
        }

        private void handleMilestoneResize(int x, int y) {
            if (selectedMilestoneIndex < 0 || selectedMilestoneIndex >= milestones.size()) return;

            TimelineMilestone milestone = milestones.get(selectedMilestoneIndex);
            int deltaX = x - resizeStartX;
            int deltaY = y - resizeStartY;
            int minSize = 10;

            switch (resizeHandle) {
                case 0: // Top - adjust height
                    milestone.height = Math.max(minSize, resizeOriginalHeight - deltaY * 2);
                    break;
                case 1: // Bottom - adjust height
                    milestone.height = Math.max(minSize, resizeOriginalHeight + deltaY * 2);
                    break;
                case 2: // Left - adjust width
                    milestone.width = Math.max(minSize, resizeOriginalWidth - deltaX * 2);
                    break;
                case 3: // Right - adjust width
                    milestone.width = Math.max(minSize, resizeOriginalWidth + deltaX * 2);
                    break;
                case 4: // Top-left - adjust both
                    milestone.width = Math.max(minSize, resizeOriginalWidth - deltaX * 2);
                    milestone.height = Math.max(minSize, resizeOriginalHeight - deltaY * 2);
                    break;
                case 5: // Top-right - adjust both
                    milestone.width = Math.max(minSize, resizeOriginalWidth + deltaX * 2);
                    milestone.height = Math.max(minSize, resizeOriginalHeight - deltaY * 2);
                    break;
                case 6: // Bottom-left - adjust both
                    milestone.width = Math.max(minSize, resizeOriginalWidth - deltaX * 2);
                    milestone.height = Math.max(minSize, resizeOriginalHeight + deltaY * 2);
                    break;
                case 7: // Bottom-right - adjust both
                    milestone.width = Math.max(minSize, resizeOriginalWidth + deltaX * 2);
                    milestone.height = Math.max(minSize, resizeOriginalHeight + deltaY * 2);
                    break;
            }

            // Update the format panel spinners
            milestoneWidthSpinner.setValue(milestone.width);
            milestoneHeightSpinner.setValue(milestone.height);
            repaint();
        }

        private void updateCursor(int x, int y) {
            if (tasks.isEmpty() || startDate == null || endDate == null) {
                setCursor(Cursor.getDefaultCursor());
                return;
            }

            int timelineX = MARGIN_LEFT;
            int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            if (totalDays <= 0) return;

            for (int i = 0; i < tasks.size(); i++) {
                TimelineTask task = tasks.get(i);
                int taskY = getTaskY(i);
                int taskHeight = task.height;
                boolean isSelected = selectedTaskIndices.contains(i);
                try {
                    LocalDate taskStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                    LocalDate taskEnd = LocalDate.parse(task.endDate, DATE_FORMAT);

                    int x1 = getXForDate(taskStart, timelineX, timelineWidth, totalDays);
                    int x2 = getXForDate(taskEnd, timelineX, timelineWidth, totalDays);
                    int barWidth = Math.max(x2 - x1, 10);

                    // Check for top/bottom edges on selected task for height resize
                    if (isSelected && x >= x1 && x <= x1 + barWidth) {
                        if (y >= taskY - 6 && y <= taskY + 6) {
                            setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                            return;
                        }
                        if (y >= taskY + taskHeight - 6 && y <= taskY + taskHeight + 6) {
                            setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                            return;
                        }
                    }

                    if (y >= taskY && y <= taskY + taskHeight) {
                        if (x >= x1 - DRAG_HANDLE_WIDTH && x <= x1 + DRAG_HANDLE_WIDTH) {
                            setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                            return;
                        }
                        if (x >= x1 + barWidth - DRAG_HANDLE_WIDTH && x <= x1 + barWidth + DRAG_HANDLE_WIDTH) {
                            setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                            return;
                        }
                    }
                } catch (Exception ex) {}
            }
            setCursor(Cursor.getDefaultCursor());
        }

        private LocalDate getDateForX(int x, int timelineX, int timelineWidth, long totalDays) {
            x = Math.max(timelineX, Math.min(x, timelineX + timelineWidth));
            double ratio = (double) (x - timelineX) / timelineWidth;
            return startDate.plusDays(Math.round(ratio * totalDays));
        }

        void updateTimeline(LocalDate start, LocalDate end,
                           ArrayList<TimelineEvent> eventList,
                           ArrayList<TimelineTask> taskList,
                           ArrayList<TimelineMilestone> milestoneList) {
            this.startDate = start;
            this.endDate = end;
            this.events = new ArrayList<>(eventList);
            this.tasks = new ArrayList<>(taskList);
            this.milestones = new ArrayList<>(milestoneList);

            int tasksHeight = getTotalTasksHeight();
            int eventsHeight = events.size() * 90 + 100;
            setPreferredSize(new Dimension(600, 50 + tasksHeight + 70 + eventsHeight));
            revalidate();
            repaint();
        }

        private int getXForDate(LocalDate date, int timelineX, int timelineWidth, long totalDays) {
            long daysFromStart = ChronoUnit.DAYS.between(startDate, date);
            return timelineX + (int) ((double) daysFromStart / totalDays * timelineWidth);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Explicitly fill background with timelineInteriorColor
            g2d.setColor(timelineInteriorColor);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            if (startDate == null || endDate == null) return;

            int timelineX = MARGIN_LEFT;
            int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
            int tasksHeight = getTotalTasksHeight();
            int timelineY = 50 + tasksHeight;

            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            if (totalDays <= 0) totalDays = 1;

            // Title
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.setColor(new Color(50, 50, 50));
            g2d.drawString("Timeline: " + startDate.format(DATE_FORMAT) + " to " + endDate.format(DATE_FORMAT), MARGIN_LEFT, 25);

            // Draw items from layerOrder in reverse order so top layers appear in front
            for (int i = layerOrder.size() - 1; i >= 0; i--) {
                Object item = layerOrder.get(i);
                if (item instanceof TimelineTask) {
                    TimelineTask task = (TimelineTask) item;
                    int taskIndex = tasks.indexOf(task);
                    int taskY = getTaskYForLayer(i);
                    drawTaskBar(g2d, task, taskIndex, taskY, timelineX, timelineWidth, totalDays);
                } else if (item instanceof TimelineMilestone) {
                    TimelineMilestone milestone = (TimelineMilestone) item;
                    int milestoneIndex = milestones.indexOf(milestone);
                    drawMilestone(g2d, milestone, milestoneIndex, timelineX, timelineWidth, timelineY, totalDays);
                }
            }

            // Timeline line
            g2d.setStroke(new BasicStroke(3));
            g2d.setColor(new Color(70, 130, 180));
            g2d.drawLine(timelineX, timelineY, timelineX + timelineWidth, timelineY);

            // Date ticks
            drawDateTicks(g2d, timelineX, timelineWidth, timelineY, totalDays);

            // Events
            drawEvents(g2d, timelineX, timelineWidth, timelineY, totalDays);
        }

        private void drawTaskBar(Graphics2D g2d, TimelineTask task, int index, int y,
                                 int timelineX, int timelineWidth, long totalDays) {
            try {
                LocalDate taskStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                LocalDate taskEnd = LocalDate.parse(task.endDate, DATE_FORMAT);

                if (taskStart.isBefore(startDate)) taskStart = startDate;
                if (taskEnd.isAfter(endDate)) taskEnd = endDate;
                if (taskStart.isAfter(endDate) || taskEnd.isBefore(startDate)) return;

                int x1 = getXForDate(taskStart, timelineX, timelineWidth, totalDays);
                int x2 = getXForDate(taskEnd, timelineX, timelineWidth, totalDays);
                int barWidth = Math.max(x2 - x1, 10);

                // Get colors (custom or default)
                Color defaultColor = TASK_COLORS[index % TASK_COLORS.length];
                Color fillColor = task.fillColor != null ? task.fillColor : defaultColor;
                Color outlineColor = task.outlineColor != null ? task.outlineColor : fillColor.darker();
                boolean isSelected = selectedTaskIndices.contains(index);

                int taskHeight = task.height;

                // Selection box around task (drawn first, behind everything)
                if (isSelected) {
                    int boxPadding = 6;
                    // Selection box outline
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(x1 - boxPadding, y - boxPadding,
                                      barWidth + boxPadding * 2, taskHeight + boxPadding * 2, 10, 10);
                }

                // Draw behind text (behind the task bar)
                if (task.behindText != null && !task.behindText.isEmpty()) {
                    int behindFontStyle = Font.PLAIN;
                    if (task.behindFontBold) behindFontStyle |= Font.BOLD;
                    if (task.behindFontItalic) behindFontStyle |= Font.ITALIC;
                    g2d.setFont(new Font("Arial", behindFontStyle, task.behindFontSize));
                    g2d.setColor(task.behindTextColor != null ? task.behindTextColor : new Color(150, 150, 150));
                    FontMetrics behindFm = g2d.getFontMetrics();
                    int behindTextWidth = behindFm.stringWidth(task.behindText);
                    // Draw behind text to the right of the task bar with small gap
                    g2d.drawString(task.behindText, x1 + barWidth + 5 + task.behindTextXOffset,
                                   y + (taskHeight + behindFm.getAscent() - behindFm.getDescent()) / 2 + task.behindTextYOffset);
                }

                // Draw front text (in front of task bar)
                if (task.frontText != null && !task.frontText.isEmpty()) {
                    int frontFontStyle = Font.PLAIN;
                    if (task.frontFontBold) frontFontStyle |= Font.BOLD;
                    if (task.frontFontItalic) frontFontStyle |= Font.ITALIC;
                    g2d.setFont(new Font("Arial", frontFontStyle, task.frontFontSize));
                    g2d.setColor(task.frontTextColor != null ? task.frontTextColor : Color.BLACK);
                    FontMetrics frontFm = g2d.getFontMetrics();
                    int frontTextWidth = frontFm.stringWidth(task.frontText);
                    // Draw front text to the left of the task bar with small gap
                    g2d.drawString(task.frontText, x1 - frontTextWidth - 5 + task.frontTextXOffset,
                                   y + (taskHeight + frontFm.getAscent() - frontFm.getDescent()) / 2 + task.frontTextYOffset);
                }

                g2d.setColor(fillColor);
                g2d.fillRoundRect(x1, y, barWidth, taskHeight, 8, 8);
                int thickness = task.outlineThickness;
                if (thickness > 0) {
                    g2d.setColor(outlineColor);
                    g2d.setStroke(new BasicStroke(thickness));
                    g2d.drawRoundRect(x1, y, barWidth, taskHeight, 8, 8);
                }

                // Text - use custom formatting with centerText
                Color textColor = task.textColor != null ? task.textColor : Color.BLACK;
                g2d.setColor(textColor);
                int fontStyle = Font.PLAIN;
                if (task.fontBold) fontStyle |= Font.BOLD;
                if (task.fontItalic) fontStyle |= Font.ITALIC;
                g2d.setFont(new Font("Arial", fontStyle, task.fontSize));
                FontMetrics fm = g2d.getFontMetrics();
                String displayText = task.centerText != null && !task.centerText.isEmpty() ? task.centerText : task.name;
                int textWidth = fm.stringWidth(displayText);
                while (textWidth > barWidth - 10 && displayText.length() > 3) {
                    displayText = displayText.substring(0, displayText.length() - 4) + "...";
                    textWidth = fm.stringWidth(displayText);
                }
                if (textWidth <= barWidth - 6) {
                    g2d.drawString(displayText, x1 + (barWidth - textWidth) / 2 + task.centerTextXOffset,
                                   y + (taskHeight + fm.getAscent() - fm.getDescent()) / 2 + task.centerTextYOffset);
                }

                // Draw above text (above the task bar)
                if (task.aboveText != null && !task.aboveText.isEmpty()) {
                    int aboveFontStyle = Font.PLAIN;
                    if (task.aboveFontBold) aboveFontStyle |= Font.BOLD;
                    if (task.aboveFontItalic) aboveFontStyle |= Font.ITALIC;
                    g2d.setFont(new Font("Arial", aboveFontStyle, task.aboveFontSize));
                    g2d.setColor(task.aboveTextColor != null ? task.aboveTextColor : Color.BLACK);
                    FontMetrics aboveFm = g2d.getFontMetrics();
                    int aboveTextWidth = aboveFm.stringWidth(task.aboveText);
                    // Draw above text centered above the task bar
                    g2d.drawString(task.aboveText, x1 + (barWidth - aboveTextWidth) / 2 + task.aboveTextXOffset,
                                   y - 3 + task.aboveTextYOffset);
                }

                // Draw underneath text (below the task bar)
                if (task.underneathText != null && !task.underneathText.isEmpty()) {
                    int underneathFontStyle = Font.PLAIN;
                    if (task.underneathFontBold) underneathFontStyle |= Font.BOLD;
                    if (task.underneathFontItalic) underneathFontStyle |= Font.ITALIC;
                    g2d.setFont(new Font("Arial", underneathFontStyle, task.underneathFontSize));
                    g2d.setColor(task.underneathTextColor != null ? task.underneathTextColor : Color.BLACK);
                    FontMetrics underneathFm = g2d.getFontMetrics();
                    int underneathTextWidth = underneathFm.stringWidth(task.underneathText);
                    // Draw underneath text centered below the task bar
                    g2d.drawString(task.underneathText, x1 + (barWidth - underneathTextWidth) / 2 + task.underneathTextXOffset,
                                   y + taskHeight + underneathFm.getAscent() + 2 + task.underneathTextYOffset);
                }

                // Draw drag handles - only when selected, positioned outside the outline
                if (isSelected) {
                    int handleOffset = 8; // Distance outside the task bar

                    // Left handle (outside left edge)
                    g2d.setColor(new Color(255, 255, 255, 180));
                    g2d.fillRoundRect(x1 - handleOffset - 6, y + 4, 8, taskHeight - 8, 3, 3);
                    g2d.setColor(outlineColor);
                    g2d.drawLine(x1 - handleOffset - 4, y + 7, x1 - handleOffset - 4, y + taskHeight - 7);
                    g2d.drawLine(x1 - handleOffset - 1, y + 7, x1 - handleOffset - 1, y + taskHeight - 7);

                    // Right handle (outside right edge)
                    g2d.setColor(new Color(255, 255, 255, 180));
                    g2d.fillRoundRect(x1 + barWidth + handleOffset - 2, y + 4, 8, taskHeight - 8, 3, 3);
                    g2d.setColor(outlineColor);
                    g2d.drawLine(x1 + barWidth + handleOffset, y + 7, x1 + barWidth + handleOffset, y + taskHeight - 7);
                    g2d.drawLine(x1 + barWidth + handleOffset + 3, y + 7, x1 + barWidth + handleOffset + 3, y + taskHeight - 7);

                    // Top/Bottom handles for height adjustment
                    int handleWidth = Math.min(barWidth - 20, 40);
                    int handleX = x1 + (barWidth - handleWidth) / 2;
                    int vHandleOffset = 6; // Vertical offset outside task

                    // Top handle (outside top edge)
                    g2d.setColor(new Color(255, 255, 255, 180));
                    g2d.fillRoundRect(handleX, y - vHandleOffset - 4, handleWidth, 6, 3, 3);
                    g2d.setColor(outlineColor);
                    g2d.drawLine(handleX + 5, y - vHandleOffset - 2, handleX + handleWidth - 5, y - vHandleOffset - 2);
                    g2d.drawLine(handleX + 5, y - vHandleOffset, handleX + handleWidth - 5, y - vHandleOffset);

                    // Bottom handle (outside bottom edge)
                    g2d.setColor(new Color(255, 255, 255, 180));
                    g2d.fillRoundRect(handleX, y + taskHeight + vHandleOffset - 2, handleWidth, 6, 3, 3);
                    g2d.setColor(outlineColor);
                    g2d.drawLine(handleX + 5, y + taskHeight + vHandleOffset, handleX + handleWidth - 5, y + taskHeight + vHandleOffset);
                    g2d.drawLine(handleX + 5, y + taskHeight + vHandleOffset + 2, handleX + handleWidth - 5, y + taskHeight + vHandleOffset + 2);
                }

            } catch (Exception e) {}
        }

        private void drawMilestone(Graphics2D g2d, TimelineMilestone milestone, int index,
                                   int timelineX, int timelineWidth, int timelineY, long totalDays) {
            try {
                LocalDate milestoneDate = LocalDate.parse(milestone.date, DATE_FORMAT);
                if (milestoneDate.isBefore(startDate) || milestoneDate.isAfter(endDate)) return;

                int x = getXForDate(milestoneDate, timelineX, timelineWidth, totalDays);
                int y = milestone.yPosition >= 0 ? milestone.yPosition : timelineY - milestone.height / 2 - 10;
                boolean isSelected = (index == selectedMilestoneIndex);

                // Selection highlight (rounded box around shape, like tasks)
                if (isSelected) {
                    int boxPadding = 6;
                    int boxX = x - milestone.width / 2 - boxPadding;
                    int boxY = y - milestone.height / 2 - boxPadding;
                    int boxW = milestone.width + boxPadding * 2;
                    int boxH = milestone.height + boxPadding * 2;

                    // Selection box outline (white rounded rect like tasks)
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(boxX, boxY, boxW, boxH, 10, 10);

                    // Draw resize handles on the selection box edges
                    int handleSize = 6;
                    g2d.setColor(Color.WHITE);
                    // Corner handles
                    g2d.fillRect(boxX - handleSize/2, boxY - handleSize/2, handleSize, handleSize);
                    g2d.fillRect(boxX + boxW - handleSize/2, boxY - handleSize/2, handleSize, handleSize);
                    g2d.fillRect(boxX - handleSize/2, boxY + boxH - handleSize/2, handleSize, handleSize);
                    g2d.fillRect(boxX + boxW - handleSize/2, boxY + boxH - handleSize/2, handleSize, handleSize);
                    // Edge handles
                    g2d.fillRect(boxX + boxW/2 - handleSize/2, boxY - handleSize/2, handleSize, handleSize);
                    g2d.fillRect(boxX + boxW/2 - handleSize/2, boxY + boxH - handleSize/2, handleSize, handleSize);
                    g2d.fillRect(boxX - handleSize/2, boxY + boxH/2 - handleSize/2, handleSize, handleSize);
                    g2d.fillRect(boxX + boxW - handleSize/2, boxY + boxH/2 - handleSize/2, handleSize, handleSize);
                    // Handle outlines
                    g2d.setColor(milestone.outlineColor);
                    g2d.drawRect(boxX - handleSize/2, boxY - handleSize/2, handleSize, handleSize);
                    g2d.drawRect(boxX + boxW - handleSize/2, boxY - handleSize/2, handleSize, handleSize);
                    g2d.drawRect(boxX - handleSize/2, boxY + boxH - handleSize/2, handleSize, handleSize);
                    g2d.drawRect(boxX + boxW - handleSize/2, boxY + boxH - handleSize/2, handleSize, handleSize);
                    g2d.drawRect(boxX + boxW/2 - handleSize/2, boxY - handleSize/2, handleSize, handleSize);
                    g2d.drawRect(boxX + boxW/2 - handleSize/2, boxY + boxH - handleSize/2, handleSize, handleSize);
                    g2d.drawRect(boxX - handleSize/2, boxY + boxH/2 - handleSize/2, handleSize, handleSize);
                    g2d.drawRect(boxX + boxW - handleSize/2, boxY + boxH/2 - handleSize/2, handleSize, handleSize);
                }

                // Draw the milestone shape
                g2d.setColor(milestone.fillColor);
                drawMilestoneShapeOnPanel(g2d, milestone.shape, x, y, milestone.width, milestone.height, true);

                // Draw outline
                if (milestone.outlineThickness > 0) {
                    g2d.setColor(milestone.outlineColor);
                    g2d.setStroke(new BasicStroke(milestone.outlineThickness));
                    drawMilestoneShapeOnPanel(g2d, milestone.shape, x, y, milestone.width, milestone.height, false);
                }

                // Draw label text below milestone
                if (milestone.labelText != null && !milestone.labelText.isEmpty()) {
                    int fontStyle = Font.PLAIN;
                    if (milestone.fontBold) fontStyle |= Font.BOLD;
                    if (milestone.fontItalic) fontStyle |= Font.ITALIC;
                    g2d.setFont(new Font("Arial", fontStyle, milestone.fontSize));
                    g2d.setColor(milestone.textColor);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(milestone.labelText);
                    g2d.drawString(milestone.labelText, x - textWidth / 2, y + milestone.height / 2 + fm.getAscent() + 3);
                }
            } catch (Exception e) {}
        }

        private void drawMilestoneShapeOnPanel(Graphics2D g2d, String shape, int cx, int cy, int w, int h, boolean fill) {
            int halfW = w / 2;
            int halfH = h / 2;
            int[] xPoints, yPoints;

            switch (shape.toLowerCase()) {
                case "diamond":
                    xPoints = new int[]{cx, cx + halfW, cx, cx - halfW};
                    yPoints = new int[]{cy - halfH, cy, cy + halfH, cy};
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 4);
                    else g2d.drawPolygon(xPoints, yPoints, 4);
                    break;
                case "circle":
                    if (fill) g2d.fillOval(cx - halfW, cy - halfH, w, h);
                    else g2d.drawOval(cx - halfW, cy - halfH, w, h);
                    break;
                case "triangle":
                    xPoints = new int[]{cx, cx + halfW, cx - halfW};
                    yPoints = new int[]{cy - halfH, cy + halfH, cy + halfH};
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                    else g2d.drawPolygon(xPoints, yPoints, 3);
                    break;
                case "star":
                    drawStarOnPanel(g2d, cx, cy, halfW, halfW / 2, fill);
                    break;
                case "square":
                    if (fill) g2d.fillRect(cx - halfW, cy - halfH, w, h);
                    else g2d.drawRect(cx - halfW, cy - halfH, w, h);
                    break;
                case "hexagon":
                    int hexW = halfW;
                    int hexH = halfH;
                    xPoints = new int[]{cx - hexW / 2, cx + hexW / 2, cx + hexW, cx + hexW / 2, cx - hexW / 2, cx - hexW};
                    yPoints = new int[]{cy - hexH, cy - hexH, cy, cy + hexH, cy + hexH, cy};
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 6);
                    else g2d.drawPolygon(xPoints, yPoints, 6);
                    break;
            }
        }

        private void drawStarOnPanel(Graphics2D g2d, int cx, int cy, int outerR, int innerR, boolean fill) {
            int[] xPoints = new int[10];
            int[] yPoints = new int[10];
            double angle = -Math.PI / 2;
            for (int i = 0; i < 10; i++) {
                int r = (i % 2 == 0) ? outerR : innerR;
                xPoints[i] = cx + (int) (r * Math.cos(angle));
                yPoints[i] = cy + (int) (r * Math.sin(angle));
                angle += Math.PI / 5;
            }
            if (fill) g2d.fillPolygon(xPoints, yPoints, 10);
            else g2d.drawPolygon(xPoints, yPoints, 10);
        }

        private void drawDateTicks(Graphics2D g2d, int timelineX, int timelineWidth, int timelineY, long totalDays) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));

            // Determine if we should show months or years based on total days
            boolean showYears = totalDays > 730; // More than 2 years, show years
            DateTimeFormatter tickFormat = showYears ?
                DateTimeFormatter.ofPattern("yyyy") :
                DateTimeFormatter.ofPattern("MMM yyyy");

            // Find first tick: first of next month or first of next year
            LocalDate tick;
            if (showYears) {
                // Start at first of the year containing startDate, or next year if not Jan 1
                if (startDate.getDayOfYear() == 1) {
                    tick = startDate;
                } else {
                    tick = startDate.plusYears(1).withDayOfYear(1);
                }
            } else {
                // Start at first of the month containing startDate, or next month if not 1st
                if (startDate.getDayOfMonth() == 1) {
                    tick = startDate;
                } else {
                    tick = startDate.plusMonths(1).withDayOfMonth(1);
                }
            }

            // Draw ticks at first of each month or year
            while (!tick.isAfter(endDate)) {
                int x = getXForDate(tick, timelineX, timelineWidth, totalDays);

                g2d.setColor(new Color(70, 130, 180));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(x, timelineY, x, timelineY + 15);

                g2d.setColor(Color.DARK_GRAY);
                String dateStr = tick.format(tickFormat);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(dateStr, x - fm.stringWidth(dateStr) / 2, timelineY + 30);

                // Advance to next month or year
                if (showYears) {
                    tick = tick.plusYears(1);
                } else {
                    tick = tick.plusMonths(1);
                }
            }
        }

        private void drawEvents(Graphics2D g2d, int timelineX, int timelineWidth, int timelineY, long totalDays) {
            int eventY = timelineY + 50;
            boolean alternate = false;

            for (TimelineEvent event : events) {
                try {
                    LocalDate eventDate = LocalDate.parse(event.date, DATE_FORMAT);

                    if (!eventDate.isBefore(startDate) && !eventDate.isAfter(endDate)) {
                        int x = getXForDate(eventDate, timelineX, timelineWidth, totalDays);

                        // Connector line
                        g2d.setColor(new Color(70, 130, 180));
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawLine(x, timelineY + 35, x, eventY);

                        // Event dot
                        g2d.setColor(new Color(220, 20, 60));
                        g2d.fillOval(x - 6, timelineY - 6, 12, 12);

                        // Event card
                        drawEventCard(g2d, event, x, eventY, alternate);

                        eventY += 90;
                        alternate = !alternate;
                    }
                } catch (Exception e) {}
            }
        }

        private void drawEventCard(Graphics2D g2d, TimelineEvent event, int x, int y, boolean alternate) {
            int cardWidth = 150, cardHeight = 70;
            int cardX = alternate ? x - cardWidth - 10 : x + 10;

            g2d.setColor(new Color(240, 248, 255));
            g2d.fillRoundRect(cardX, y, cardWidth, cardHeight, 10, 10);

            g2d.setColor(new Color(70, 130, 180));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(cardX, y, cardWidth, cardHeight, 10, 10);

            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.setColor(new Color(70, 130, 180));
            g2d.drawString(event.date, cardX + 8, y + 15);

            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.setColor(Color.BLACK);
            g2d.drawString(truncate(event.title, 18), cardX + 8, y + 32);

            if (!event.description.isEmpty()) {
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawString(truncate(event.description, 22), cardX + 8, y + 50);
            }
        }

        private String truncate(String str, int maxLen) {
            return str.length() <= maxLen ? str : str.substring(0, maxLen - 3) + "...";
        }
    }
}
