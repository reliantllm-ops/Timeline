import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Timeline2 extends JFrame {

    // Data storage
    private ArrayList<TimelineEvent> events = new ArrayList<>();
    private ArrayList<TimelineTask> tasks = new ArrayList<>();
    private ArrayList<TimelineMilestone> milestones = new ArrayList<>();
    private int selectedTaskIndex = -1;
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
    private LayersPanel layersPanel;
    private JPanel formatPanel;
    private JTextField taskNameField, taskStartField, taskEndField;
    private JLabel formatTitleLabel;
    private JButton fillColorBtn, outlineColorBtn, textColorBtn;
    private JSpinner outlineThicknessSpinner, taskHeightSpinner, fontSizeSpinner;
    private JToggleButton boldBtn, italicBtn;
    private JTextField centerTextField;
    // Front text controls
    private JTextField frontTextField;
    private JSpinner frontFontSizeSpinner;
    private JToggleButton frontBoldBtn, frontItalicBtn;
    private JButton frontTextColorBtn;
    // Above text controls
    private JTextField aboveTextField;
    private JSpinner aboveFontSizeSpinner;
    private JToggleButton aboveBoldBtn, aboveItalicBtn;
    private JButton aboveTextColorBtn;
    // Underneath text controls
    private JTextField underneathTextField;
    private JSpinner underneathFontSizeSpinner;
    private JToggleButton underneathBoldBtn, underneathItalicBtn;
    private JButton underneathTextColorBtn;
    // Behind text controls
    private JTextField behindTextField;
    private JSpinner behindFontSizeSpinner;
    private JToggleButton behindBoldBtn, behindItalicBtn;
    private JButton behindTextColorBtn;
    // Milestone controls
    private JTextField milestoneNameField, milestoneDateField;
    private JSpinner milestoneWidthSpinner, milestoneHeightSpinner;
    private JButton milestoneFillColorBtn, milestoneOutlineColorBtn;
    private JSpinner milestoneOutlineThicknessSpinner;

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
        newMilestoneBtn.addActionListener(e -> showMilestoneShapeDialog());
        toolbarPanel.add(newMilestoneBtn);
        JButton clearAllBtn = new JButton("Clear All");
        clearAllBtn.addActionListener(e -> clearAll());
        toolbarPanel.add(clearAllBtn);
        centerPanel.add(toolbarPanel, BorderLayout.NORTH);

        timelineDisplayPanel = new TimelineDisplayPanel();
        JScrollPane scrollPane = new JScrollPane(timelineDisplayPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Timeline"));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Right panel - Layers
        layersPanel = new LayersPanel();
        add(layersPanel, BorderLayout.EAST);

        // Bottom - Format panel
        formatPanel = createFormatPanel();
        add(formatPanel, BorderLayout.SOUTH);

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        refreshTimeline();
    }

    private JPanel createFormatPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.setPreferredSize(new Dimension(0, 295));
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
                panel.setPreferredSize(new Dimension(0, 295));
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

        // Row 1: Title and main fields
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        row1.setOpaque(false);
        row1.setMinimumSize(new Dimension(1200, 30));
        row1.setPreferredSize(new Dimension(1200, 30));

        formatTitleLabel = new JLabel("No task selected");
        formatTitleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        row1.add(formatTitleLabel);

        row1.add(Box.createHorizontalStrut(10));
        row1.add(new JLabel("Name:"));
        taskNameField = new JTextField(10);
        taskNameField.setEnabled(false);
        taskNameField.addActionListener(e -> updateSelectedTaskName());
        taskNameField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateSelectedTaskName(); }
        });
        row1.add(taskNameField);

        row1.add(Box.createHorizontalStrut(10));
        row1.add(new JLabel("Start:"));
        taskStartField = new JTextField(8);
        taskStartField.setEnabled(false);
        taskStartField.addActionListener(e -> updateSelectedTaskDates());
        taskStartField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateSelectedTaskDates(); }
        });
        row1.add(taskStartField);

        row1.add(Box.createHorizontalStrut(10));
        row1.add(new JLabel("End:"));
        taskEndField = new JTextField(8);
        taskEndField.setEnabled(false);
        taskEndField.addActionListener(e -> updateSelectedTaskDates());
        taskEndField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateSelectedTaskDates(); }
        });
        row1.add(taskEndField);

        row1.add(Box.createHorizontalStrut(15));
        row1.add(new JLabel("Fill:"));
        fillColorBtn = new JButton();
        fillColorBtn.setPreferredSize(new Dimension(30, 25));
        fillColorBtn.setEnabled(false);
        fillColorBtn.setToolTipText("Click to change fill color");
        fillColorBtn.addActionListener(e -> chooseFillColor());
        row1.add(fillColorBtn);

        row1.add(Box.createHorizontalStrut(10));
        row1.add(new JLabel("Outline:"));
        outlineColorBtn = new JButton();
        outlineColorBtn.setPreferredSize(new Dimension(30, 25));
        outlineColorBtn.setEnabled(false);
        outlineColorBtn.setToolTipText("Click to change outline color");
        outlineColorBtn.addActionListener(e -> chooseOutlineColor());
        row1.add(outlineColorBtn);

        row1.add(Box.createHorizontalStrut(10));
        row1.add(new JLabel("Thickness:"));
        outlineThicknessSpinner = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));
        outlineThicknessSpinner.setPreferredSize(new Dimension(50, 25));
        outlineThicknessSpinner.setEnabled(false);
        outlineThicknessSpinner.setToolTipText("Outline thickness (0-10)");
        outlineThicknessSpinner.addChangeListener(e -> updateOutlineThickness());
        row1.add(outlineThicknessSpinner);

        row1.add(Box.createHorizontalStrut(10));
        row1.add(new JLabel("Height:"));
        taskHeightSpinner = new JSpinner(new SpinnerNumberModel(25, 10, 100, 5));
        taskHeightSpinner.setPreferredSize(new Dimension(55, 25));
        taskHeightSpinner.setEnabled(false);
        taskHeightSpinner.setToolTipText("Task bar height (10-100)");
        taskHeightSpinner.addChangeListener(e -> updateTaskHeight());
        row1.add(taskHeightSpinner);

        contentPanel.add(row1);

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
        contentPanel.add(row6);

        // Separator before milestone row
        contentPanel.add(Box.createVerticalStrut(5));
        JPanel separatorPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        separatorPanel2.setOpaque(false);
        JSeparator separator2 = new JSeparator(SwingConstants.HORIZONTAL);
        separator2.setPreferredSize(new Dimension(1200, 2));
        separatorPanel2.add(Box.createHorizontalStrut(10));
        separatorPanel2.add(separator2);
        contentPanel.add(separatorPanel2);
        contentPanel.add(Box.createVerticalStrut(3));

        // Row 7: Milestone fields
        JPanel row7 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        row7.setOpaque(false);
        row7.setMinimumSize(new Dimension(1200, 30));
        row7.setPreferredSize(new Dimension(1200, 30));
        JLabel milestoneLabel = new JLabel("Milestone:");
        milestoneLabel.setPreferredSize(new Dimension(60, 20));
        row7.add(milestoneLabel);

        row7.add(new JLabel("Name:"));
        milestoneNameField = new JTextField(10);
        milestoneNameField.setEnabled(false);
        milestoneNameField.addActionListener(e -> updateSelectedMilestoneName());
        milestoneNameField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateSelectedMilestoneName(); }
        });
        row7.add(milestoneNameField);

        row7.add(Box.createHorizontalStrut(10));
        row7.add(new JLabel("Date:"));
        milestoneDateField = new JTextField(8);
        milestoneDateField.setEnabled(false);
        milestoneDateField.addActionListener(e -> updateSelectedMilestoneDate());
        milestoneDateField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateSelectedMilestoneDate(); }
        });
        row7.add(milestoneDateField);

        row7.add(Box.createHorizontalStrut(15));
        row7.add(new JLabel("Fill:"));
        milestoneFillColorBtn = new JButton();
        milestoneFillColorBtn.setPreferredSize(new Dimension(30, 25));
        milestoneFillColorBtn.setEnabled(false);
        milestoneFillColorBtn.setToolTipText("Click to change fill color");
        milestoneFillColorBtn.addActionListener(e -> chooseMilestoneFillColor());
        row7.add(milestoneFillColorBtn);

        row7.add(Box.createHorizontalStrut(10));
        row7.add(new JLabel("Outline:"));
        milestoneOutlineColorBtn = new JButton();
        milestoneOutlineColorBtn.setPreferredSize(new Dimension(30, 25));
        milestoneOutlineColorBtn.setEnabled(false);
        milestoneOutlineColorBtn.setToolTipText("Click to change outline color");
        milestoneOutlineColorBtn.addActionListener(e -> chooseMilestoneOutlineColor());
        row7.add(milestoneOutlineColorBtn);

        row7.add(Box.createHorizontalStrut(10));
        row7.add(new JLabel("Thickness:"));
        milestoneOutlineThicknessSpinner = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));
        milestoneOutlineThicknessSpinner.setPreferredSize(new Dimension(50, 25));
        milestoneOutlineThicknessSpinner.setEnabled(false);
        milestoneOutlineThicknessSpinner.addChangeListener(e -> updateMilestoneOutlineThickness());
        row7.add(milestoneOutlineThicknessSpinner);

        row7.add(Box.createHorizontalStrut(10));
        row7.add(new JLabel("Height:"));
        milestoneHeightSpinner = new JSpinner(new SpinnerNumberModel(20, 10, 100, 5));
        milestoneHeightSpinner.setPreferredSize(new Dimension(55, 25));
        milestoneHeightSpinner.setEnabled(false);
        milestoneHeightSpinner.addChangeListener(e -> updateMilestoneHeight());
        row7.add(milestoneHeightSpinner);

        row7.add(Box.createHorizontalStrut(10));
        row7.add(new JLabel("Width:"));
        milestoneWidthSpinner = new JSpinner(new SpinnerNumberModel(20, 10, 100, 5));
        milestoneWidthSpinner.setPreferredSize(new Dimension(55, 25));
        milestoneWidthSpinner.setEnabled(false);
        milestoneWidthSpinner.addChangeListener(e -> updateMilestoneWidth());
        row7.add(milestoneWidthSpinner);

        contentPanel.add(row7);
        contentPanel.add(Box.createVerticalStrut(3));

        contentWrapper.add(contentPanel, BorderLayout.CENTER);
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
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        Color currentColor = task.fillColor != null ? task.fillColor : TASK_COLORS[selectedTaskIndex % TASK_COLORS.length];
        Color newColor = JColorChooser.showDialog(this, "Choose Fill Color", currentColor);
        if (newColor != null) {
            task.fillColor = newColor;
            fillColorBtn.setBackground(newColor);
            refreshTimeline();
        }
    }

    private void chooseOutlineColor() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        Color fillColor = task.fillColor != null ? task.fillColor : TASK_COLORS[selectedTaskIndex % TASK_COLORS.length];
        Color currentColor = task.outlineColor != null ? task.outlineColor : fillColor.darker();
        Color newColor = JColorChooser.showDialog(this, "Choose Outline Color", currentColor);
        if (newColor != null) {
            task.outlineColor = newColor;
            outlineColorBtn.setBackground(newColor);
            refreshTimeline();
        }
    }

    private void updateOutlineThickness() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.outlineThickness = (Integer) outlineThicknessSpinner.getValue();
        refreshTimeline();
    }

    private void updateTaskHeight() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.height = (Integer) taskHeightSpinner.getValue();
        refreshTimeline();
    }

    private void updateCenterText() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.centerText = centerTextField.getText();
        refreshTimeline();
    }

    private void updateFontSize() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.fontSize = (Integer) fontSizeSpinner.getValue();
        refreshTimeline();
    }

    private void updateFontBold() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.fontBold = boldBtn.isSelected();
        refreshTimeline();
    }

    private void updateFontItalic() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.fontItalic = italicBtn.isSelected();
        refreshTimeline();
    }

    private void chooseTextColor() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        Color currentColor = task.textColor != null ? task.textColor : Color.WHITE;
        Color newColor = JColorChooser.showDialog(this, "Choose Text Color", currentColor);
        if (newColor != null) {
            task.textColor = newColor;
            textColorBtn.setBackground(newColor);
            refreshTimeline();
        }
    }

    // Front text update methods
    private void updateFrontText() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.frontText = frontTextField.getText();
        refreshTimeline();
    }

    private void updateFrontFontSize() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.frontFontSize = (Integer) frontFontSizeSpinner.getValue();
        refreshTimeline();
    }

    private void updateFrontFontBold() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.frontFontBold = frontBoldBtn.isSelected();
        refreshTimeline();
    }

    private void updateFrontFontItalic() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.frontFontItalic = frontItalicBtn.isSelected();
        refreshTimeline();
    }

    private void chooseFrontTextColor() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        Color currentColor = task.frontTextColor != null ? task.frontTextColor : Color.BLACK;
        Color newColor = JColorChooser.showDialog(this, "Choose Front Text Color", currentColor);
        if (newColor != null) {
            task.frontTextColor = newColor;
            frontTextColorBtn.setBackground(newColor);
            refreshTimeline();
        }
    }

    // Above text update methods
    private void updateAboveText() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.aboveText = aboveTextField.getText();
        refreshTimeline();
    }

    private void updateAboveFontSize() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.aboveFontSize = (Integer) aboveFontSizeSpinner.getValue();
        refreshTimeline();
    }

    private void updateAboveFontBold() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.aboveFontBold = aboveBoldBtn.isSelected();
        refreshTimeline();
    }

    private void updateAboveFontItalic() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.aboveFontItalic = aboveItalicBtn.isSelected();
        refreshTimeline();
    }

    private void chooseAboveTextColor() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        Color currentColor = task.aboveTextColor != null ? task.aboveTextColor : Color.BLACK;
        Color newColor = JColorChooser.showDialog(this, "Choose Above Text Color", currentColor);
        if (newColor != null) {
            task.aboveTextColor = newColor;
            aboveTextColorBtn.setBackground(newColor);
            refreshTimeline();
        }
    }

    // Underneath text update methods
    private void updateUnderneathText() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.underneathText = underneathTextField.getText();
        refreshTimeline();
    }

    private void updateUnderneathFontSize() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.underneathFontSize = (Integer) underneathFontSizeSpinner.getValue();
        refreshTimeline();
    }

    private void updateUnderneathFontBold() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.underneathFontBold = underneathBoldBtn.isSelected();
        refreshTimeline();
    }

    private void updateUnderneathFontItalic() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.underneathFontItalic = underneathItalicBtn.isSelected();
        refreshTimeline();
    }

    private void chooseUnderneathTextColor() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        Color currentColor = task.underneathTextColor != null ? task.underneathTextColor : Color.BLACK;
        Color newColor = JColorChooser.showDialog(this, "Choose Underneath Text Color", currentColor);
        if (newColor != null) {
            task.underneathTextColor = newColor;
            underneathTextColorBtn.setBackground(newColor);
            refreshTimeline();
        }
    }

    // Behind text update methods
    private void updateBehindText() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.behindText = behindTextField.getText();
        refreshTimeline();
    }

    private void updateBehindFontSize() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.behindFontSize = (Integer) behindFontSizeSpinner.getValue();
        refreshTimeline();
    }

    private void updateBehindFontBold() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.behindFontBold = behindBoldBtn.isSelected();
        refreshTimeline();
    }

    private void updateBehindFontItalic() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        task.behindFontItalic = behindItalicBtn.isSelected();
        refreshTimeline();
    }

    private void chooseBehindTextColor() {
        if (selectedTaskIndex < 0 || selectedTaskIndex >= tasks.size()) return;
        TimelineTask task = tasks.get(selectedTaskIndex);
        Color currentColor = task.behindTextColor != null ? task.behindTextColor : new Color(150, 150, 150);
        Color newColor = JColorChooser.showDialog(this, "Choose Behind Text Color", currentColor);
        if (newColor != null) {
            task.behindTextColor = newColor;
            behindTextColorBtn.setBackground(newColor);
            refreshTimeline();
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

    void selectTask(int index) {
        selectedTaskIndex = index;
        // Deselect milestone when task is selected
        if (index >= 0) {
            selectedMilestoneIndex = -1;
            // Clear milestone fields
            milestoneNameField.setText("");
            milestoneNameField.setEnabled(false);
            milestoneDateField.setText("");
            milestoneDateField.setEnabled(false);
            milestoneFillColorBtn.setBackground(null);
            milestoneFillColorBtn.setEnabled(false);
            milestoneOutlineColorBtn.setBackground(null);
            milestoneOutlineColorBtn.setEnabled(false);
            milestoneOutlineThicknessSpinner.setValue(2);
            milestoneOutlineThicknessSpinner.setEnabled(false);
            milestoneHeightSpinner.setValue(20);
            milestoneHeightSpinner.setEnabled(false);
            milestoneWidthSpinner.setValue(20);
            milestoneWidthSpinner.setEnabled(false);
        }
        if (index >= 0 && index < tasks.size()) {
            TimelineTask task = tasks.get(index);
            Color defaultColor = TASK_COLORS[index % TASK_COLORS.length];
            Color fillColor = task.fillColor != null ? task.fillColor : defaultColor;
            Color outlineColor = task.outlineColor != null ? task.outlineColor : fillColor.darker();

            formatTitleLabel.setText("Selected: " + task.name);
            formatTitleLabel.setForeground(fillColor.darker());
            taskNameField.setText(task.name);
            taskNameField.setEnabled(true);
            taskStartField.setText(task.startDate);
            taskStartField.setEnabled(true);
            taskEndField.setText(task.endDate);
            taskEndField.setEnabled(true);

            // Update color buttons
            fillColorBtn.setBackground(fillColor);
            fillColorBtn.setEnabled(true);
            outlineColorBtn.setBackground(outlineColor);
            outlineColorBtn.setEnabled(true);
            outlineThicknessSpinner.setValue(task.outlineThickness);
            outlineThicknessSpinner.setEnabled(true);
            taskHeightSpinner.setValue(task.height);
            taskHeightSpinner.setEnabled(true);

            // Update text formatting controls
            centerTextField.setText(task.centerText);
            centerTextField.setEnabled(true);
            fontSizeSpinner.setValue(task.fontSize);
            fontSizeSpinner.setEnabled(true);
            boldBtn.setSelected(task.fontBold);
            boldBtn.setEnabled(true);
            italicBtn.setSelected(task.fontItalic);
            italicBtn.setEnabled(true);
            Color textColor = task.textColor != null ? task.textColor : Color.BLACK;
            textColorBtn.setBackground(textColor);
            textColorBtn.setEnabled(true);

            // Update front text controls
            frontTextField.setText(task.frontText);
            frontTextField.setEnabled(true);
            frontFontSizeSpinner.setValue(task.frontFontSize);
            frontFontSizeSpinner.setEnabled(true);
            frontBoldBtn.setSelected(task.frontFontBold);
            frontBoldBtn.setEnabled(true);
            frontItalicBtn.setSelected(task.frontFontItalic);
            frontItalicBtn.setEnabled(true);
            Color frontColor = task.frontTextColor != null ? task.frontTextColor : Color.BLACK;
            frontTextColorBtn.setBackground(frontColor);
            frontTextColorBtn.setEnabled(true);

            // Update above text controls
            aboveTextField.setText(task.aboveText);
            aboveTextField.setEnabled(true);
            aboveFontSizeSpinner.setValue(task.aboveFontSize);
            aboveFontSizeSpinner.setEnabled(true);
            aboveBoldBtn.setSelected(task.aboveFontBold);
            aboveBoldBtn.setEnabled(true);
            aboveItalicBtn.setSelected(task.aboveFontItalic);
            aboveItalicBtn.setEnabled(true);
            Color aboveColor = task.aboveTextColor != null ? task.aboveTextColor : Color.BLACK;
            aboveTextColorBtn.setBackground(aboveColor);
            aboveTextColorBtn.setEnabled(true);

            // Update underneath text controls
            underneathTextField.setText(task.underneathText);
            underneathTextField.setEnabled(true);
            underneathFontSizeSpinner.setValue(task.underneathFontSize);
            underneathFontSizeSpinner.setEnabled(true);
            underneathBoldBtn.setSelected(task.underneathFontBold);
            underneathBoldBtn.setEnabled(true);
            underneathItalicBtn.setSelected(task.underneathFontItalic);
            underneathItalicBtn.setEnabled(true);
            Color underneathColor = task.underneathTextColor != null ? task.underneathTextColor : Color.BLACK;
            underneathTextColorBtn.setBackground(underneathColor);
            underneathTextColorBtn.setEnabled(true);

            // Update behind text controls
            behindTextField.setText(task.behindText);
            behindTextField.setEnabled(true);
            behindFontSizeSpinner.setValue(task.behindFontSize);
            behindFontSizeSpinner.setEnabled(true);
            behindBoldBtn.setSelected(task.behindFontBold);
            behindBoldBtn.setEnabled(true);
            behindItalicBtn.setSelected(task.behindFontItalic);
            behindItalicBtn.setEnabled(true);
            Color behindColor = task.behindTextColor != null ? task.behindTextColor : new Color(150, 150, 150);
            behindTextColorBtn.setBackground(behindColor);
            behindTextColorBtn.setEnabled(true);
        } else {
            formatTitleLabel.setText("No task selected");
            formatTitleLabel.setForeground(Color.BLACK);
            taskNameField.setText("");
            taskNameField.setEnabled(false);
            taskStartField.setText("");
            taskStartField.setEnabled(false);
            taskEndField.setText("");
            taskEndField.setEnabled(false);
            fillColorBtn.setBackground(null);
            fillColorBtn.setEnabled(false);
            outlineColorBtn.setBackground(null);
            outlineColorBtn.setEnabled(false);
            outlineThicknessSpinner.setValue(2);
            outlineThicknessSpinner.setEnabled(false);
            taskHeightSpinner.setValue(25);
            taskHeightSpinner.setEnabled(false);

            // Reset text formatting controls
            centerTextField.setText("");
            centerTextField.setEnabled(false);
            fontSizeSpinner.setValue(11);
            fontSizeSpinner.setEnabled(false);
            boldBtn.setSelected(false);
            boldBtn.setEnabled(false);
            italicBtn.setSelected(false);
            italicBtn.setEnabled(false);
            textColorBtn.setBackground(null);
            textColorBtn.setEnabled(false);

            // Reset front text controls
            frontTextField.setText("");
            frontTextField.setEnabled(false);
            frontFontSizeSpinner.setValue(10);
            frontFontSizeSpinner.setEnabled(false);
            frontBoldBtn.setSelected(false);
            frontBoldBtn.setEnabled(false);
            frontItalicBtn.setSelected(false);
            frontItalicBtn.setEnabled(false);
            frontTextColorBtn.setBackground(null);
            frontTextColorBtn.setEnabled(false);

            // Reset above text controls
            aboveTextField.setText("");
            aboveTextField.setEnabled(false);
            aboveFontSizeSpinner.setValue(10);
            aboveFontSizeSpinner.setEnabled(false);
            aboveBoldBtn.setSelected(false);
            aboveBoldBtn.setEnabled(false);
            aboveItalicBtn.setSelected(false);
            aboveItalicBtn.setEnabled(false);
            aboveTextColorBtn.setBackground(null);
            aboveTextColorBtn.setEnabled(false);

            // Reset underneath text controls
            underneathTextField.setText("");
            underneathTextField.setEnabled(false);
            underneathFontSizeSpinner.setValue(10);
            underneathFontSizeSpinner.setEnabled(false);
            underneathBoldBtn.setSelected(false);
            underneathBoldBtn.setEnabled(false);
            underneathItalicBtn.setSelected(false);
            underneathItalicBtn.setEnabled(false);
            underneathTextColorBtn.setBackground(null);
            underneathTextColorBtn.setEnabled(false);

            // Reset behind text controls
            behindTextField.setText("");
            behindTextField.setEnabled(false);
            behindFontSizeSpinner.setValue(10);
            behindFontSizeSpinner.setEnabled(false);
            behindBoldBtn.setSelected(false);
            behindBoldBtn.setEnabled(false);
            behindItalicBtn.setSelected(false);
            behindItalicBtn.setEnabled(false);
            behindTextColorBtn.setBackground(null);
            behindTextColorBtn.setEnabled(false);
        }
        timelineDisplayPanel.repaint();
        if (layersPanel != null) {
            layersPanel.setSelectedLayer(index);
        }
    }

    void selectMilestone(int index) {
        selectedMilestoneIndex = index;
        // Deselect task when milestone is selected
        if (index >= 0) {
            selectedTaskIndex = -1;
            // Disable task fields
            selectTask(-1);
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
            milestoneNameField.setText("");
            milestoneNameField.setEnabled(false);
            milestoneDateField.setText("");
            milestoneDateField.setEnabled(false);
            milestoneFillColorBtn.setBackground(null);
            milestoneFillColorBtn.setEnabled(false);
            milestoneOutlineColorBtn.setBackground(null);
            milestoneOutlineColorBtn.setEnabled(false);
            milestoneOutlineThicknessSpinner.setValue(2);
            milestoneOutlineThicknessSpinner.setEnabled(false);
            milestoneHeightSpinner.setValue(20);
            milestoneHeightSpinner.setEnabled(false);
            milestoneWidthSpinner.setValue(20);
            milestoneWidthSpinner.setEnabled(false);
        }
        timelineDisplayPanel.repaint();
    }

    private void updateSelectedTaskName() {
        if (selectedTaskIndex >= 0 && selectedTaskIndex < tasks.size()) {
            String newName = taskNameField.getText().trim();
            if (!newName.isEmpty()) {
                tasks.get(selectedTaskIndex).name = newName;
                formatTitleLabel.setText("Selected: " + newName);
                refreshTimeline();
            }
        }
    }

    private void updateSelectedTaskDates() {
        if (selectedTaskIndex >= 0 && selectedTaskIndex < tasks.size()) {
            String newStart = taskStartField.getText().trim();
            String newEnd = taskEndField.getText().trim();
            TimelineTask task = tasks.get(selectedTaskIndex);
            try {
                LocalDate.parse(newStart, DATE_FORMAT);
                LocalDate.parse(newEnd, DATE_FORMAT);
                task.startDate = newStart;
                task.endDate = newEnd;
                refreshTimeline();
            } catch (Exception ex) {
                // Invalid date, revert
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
        refreshTimeline();
    }

    private void showMilestoneShapeDialog() {
        JDialog dialog = new JDialog(this, "Select Milestone Shape", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel shapesPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        shapesPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] shapes = {"Diamond", "Circle", "Triangle", "Star", "Square", "Hexagon"};
        for (String shape : shapes) {
            JButton btn = new JButton(shape) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2 + 8;
                    int size = 12;
                    g2d.setColor(new Color(255, 193, 7));
                    drawMilestoneShape(g2d, shape.toLowerCase(), cx, cy, size, size, true);
                    g2d.setColor(Color.BLACK);
                    drawMilestoneShape(g2d, shape.toLowerCase(), cx, cy, size, size, false);
                }
            };
            btn.setVerticalTextPosition(SwingConstants.TOP);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.addActionListener(e -> {
                addNewMilestone(shape.toLowerCase());
                dialog.dispose();
            });
            shapesPanel.add(btn);
        }

        dialog.add(shapesPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void addNewMilestone(String shape) {
        int milestoneIndex = milestones.size();
        String name = "Milestone " + (milestoneIndex + 1);
        String date = LocalDate.now().plusWeeks(1).format(DATE_FORMAT);

        TimelineMilestone milestone = new TimelineMilestone(name, date, shape);
        milestone.fillColor = TASK_COLORS[(milestoneIndex + 3) % TASK_COLORS.length];
        milestones.add(milestone);
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
            // Reset selection if deleted task was selected
            if (selectedTaskIndex == index) {
                selectTask(-1);
            } else if (selectedTaskIndex > index) {
                selectedTaskIndex--;
            }
            refreshTimeline();
        }
    }

    void updateFormatPanelDates(int index) {
        if (index >= 0 && index < tasks.size() && index == selectedTaskIndex) {
            TimelineTask task = tasks.get(index);
            taskStartField.setText(task.startDate);
            taskEndField.setText(task.endDate);
        }
    }

    void moveTask(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex >= tasks.size() || toIndex < 0 || toIndex >= tasks.size()) return;
        if (fromIndex == toIndex) return;

        TimelineTask task = tasks.remove(fromIndex);
        tasks.add(toIndex, task);

        // Update selected index if needed
        if (selectedTaskIndex == fromIndex) {
            selectedTaskIndex = toIndex;
        } else if (fromIndex < selectedTaskIndex && toIndex >= selectedTaskIndex) {
            selectedTaskIndex--;
        } else if (fromIndex > selectedTaskIndex && toIndex <= selectedTaskIndex) {
            selectedTaskIndex++;
        }

        refreshTimeline();
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 250, 250));

        // Timeline Range Section
        addSectionHeader(panel, "Timeline Range");

        // Base date for slider calculations (2.5 years before today)
        LocalDate sliderBaseDate = LocalDate.now().minusYears(2).minusMonths(6);
        int totalDays = 365 * 5;  // 5 year span

        addLabel(panel, "Start Date:");
        startDateField = new JTextField(LocalDate.now().format(DATE_FORMAT));
        startDateField.setMaximumSize(new Dimension(170, 30));
        startDateField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(startDateField);

        // Start date slider
        int startDayOffset = (int) java.time.temporal.ChronoUnit.DAYS.between(sliderBaseDate, LocalDate.now());
        JSlider startSlider = new JSlider(0, totalDays, startDayOffset);
        startSlider.setMaximumSize(new Dimension(170, 25));
        startSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        startSlider.addChangeListener(e -> {
            LocalDate newDate = sliderBaseDate.plusDays(startSlider.getValue());
            startDateField.setText(newDate.format(DATE_FORMAT));
            if (!startSlider.getValueIsAdjusting()) refreshTimeline();
        });
        startDateField.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(startDateField.getText().trim(), DATE_FORMAT);
                int days = (int) java.time.temporal.ChronoUnit.DAYS.between(sliderBaseDate, date);
                startSlider.setValue(Math.max(0, Math.min(totalDays, days)));
            } catch (Exception ex) {}
        });
        panel.add(startSlider);
        panel.add(Box.createVerticalStrut(10));

        addLabel(panel, "End Date:");
        endDateField = new JTextField(LocalDate.now().plusMonths(3).format(DATE_FORMAT));
        endDateField.setMaximumSize(new Dimension(170, 30));
        endDateField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(endDateField);

        // End date slider
        int endDayOffset = (int) java.time.temporal.ChronoUnit.DAYS.between(sliderBaseDate, LocalDate.now().plusMonths(3));
        JSlider endSlider = new JSlider(0, totalDays, endDayOffset);
        endSlider.setMaximumSize(new Dimension(170, 25));
        endSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        endSlider.addChangeListener(e -> {
            LocalDate newDate = sliderBaseDate.plusDays(endSlider.getValue());
            endDateField.setText(newDate.format(DATE_FORMAT));
            if (!endSlider.getValueIsAdjusting()) refreshTimeline();
        });
        endDateField.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(endDateField.getText().trim(), DATE_FORMAT);
                int days = (int) java.time.temporal.ChronoUnit.DAYS.between(sliderBaseDate, date);
                endSlider.setValue(Math.max(0, Math.min(totalDays, days)));
            } catch (Exception ex) {}
        });
        panel.add(endSlider);
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
        if (layersPanel != null) {
            layersPanel.applyColors(layersInteriorColor, layersOutlineColor, layersHeaderColor, layersHeaderTextColor);
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

    // ==================== Inner Classes ====================

    // Collapsible Panel
    class CollapsiblePanel extends JPanel {
        private JPanel content;
        private JPanel header;
        private JButton collapseBtn;
        private boolean collapsed = false;
        private boolean isLeft;
        private String title;
        private Dimension expandedSize = new Dimension(195, 600);

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

            // Single collapse/expand button with arrow
            collapseBtn = createHeaderButton("\u25C0", "Collapse");
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
                collapseBtn.setText("\u25B6");  // Right arrow to expand
                collapseBtn.setToolTipText("Expand");
                for (Component c : getComponents()) {
                    if (c != header) c.setVisible(false);
                }
                header.setPreferredSize(new Dimension(30, 600));
            } else {
                setPreferredSize(expandedSize);
                collapseBtn.setText("\u25C0");  // Left arrow to collapse
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
        private JPanel header;
        private JLabel titleLabel;
        private boolean isDragging = false;
        private int dragOriginalIndex = -1;
        private int dropTargetIndex = -1;
        private int dragOffsetY = 0;
        private int floatingY = 0;
        private String draggedItemName = null;
        private Color draggedItemColor = null;

        LayersPanel() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
            ));
            setPreferredSize(new Dimension(180, 400));

            // Header
            header = new JPanel(new BorderLayout());
            header.setBackground(new Color(70, 130, 180));
            header.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            titleLabel = new JLabel("Layers");
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
            header.add(titleLabel, BorderLayout.CENTER);

            JLabel helpLabel = new JLabel("(top = front)");
            helpLabel.setForeground(new Color(200, 220, 255));
            helpLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            header.add(helpLabel, BorderLayout.EAST);
            add(header, BorderLayout.NORTH);

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
                        selectTask(index);

                        // Store dragged item info
                        draggedItemName = listModel.get(index);
                        TimelineTask task = tasks.get(index);
                        draggedItemColor = task.fillColor != null ? task.fillColor : TASK_COLORS[index % TASK_COLORS.length];
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
            if (fromIndex < 0 || fromIndex >= tasks.size() || toIndex < 0 || toIndex >= tasks.size()) return;

            TimelineTask task = tasks.remove(fromIndex);
            tasks.add(toIndex, task);

            // Update selected index
            if (selectedTaskIndex == fromIndex) {
                selectedTaskIndex = toIndex;
            } else if (fromIndex < selectedTaskIndex && toIndex >= selectedTaskIndex) {
                selectedTaskIndex--;
            } else if (fromIndex > selectedTaskIndex && toIndex <= selectedTaskIndex) {
                selectedTaskIndex++;
            }

            refreshTimeline();
        }

        void refreshLayers() {
            int selectedIndex = layersList.getSelectedIndex();
            listModel.clear();
            for (TimelineTask task : tasks) {
                listModel.addElement(task.name);
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
            if (index >= 0) {
                deleteTask(index);
            }
        }

        void applyColors(Color interior, Color outline, Color headerBg, Color headerText) {
            setBackground(interior);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(outline),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
            ));
            header.setBackground(headerBg);
            titleLabel.setForeground(headerText);
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

                // Get task color - need to find the right task based on the value
                Color taskColor = TASK_COLORS[index % TASK_COLORS.length];
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).name.equals(value)) {
                        TimelineTask task = tasks.get(i);
                        taskColor = task.fillColor != null ? task.fillColor : TASK_COLORS[i % TASK_COLORS.length];
                        break;
                    }
                }
                colorIndicator.setBackground(taskColor);

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
        // Front text properties (text in front of task bar)
        String frontText = "";
        int frontFontSize = 10;
        boolean frontFontBold = false;
        boolean frontFontItalic = false;
        Color frontTextColor = Color.BLACK;
        // Above text properties (text above task bar)
        String aboveText = "";
        int aboveFontSize = 10;
        boolean aboveFontBold = false;
        boolean aboveFontItalic = false;
        Color aboveTextColor = Color.BLACK;
        // Underneath text properties (text below task bar)
        String underneathText = "";
        int underneathFontSize = 10;
        boolean underneathFontBold = false;
        boolean underneathFontItalic = false;
        Color underneathTextColor = Color.BLACK;
        // Behind text properties (text behind task bar)
        String behindText = "";
        int behindFontSize = 10;
        boolean behindFontBold = false;
        boolean behindFontItalic = false;
        Color behindTextColor = new Color(150, 150, 150);
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
        private static final int DRAG_HANDLE_WIDTH = 12;

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

        TimelineDisplayPanel() {
            setBackground(Color.WHITE);
            setupMouseListeners();
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

        private int getTotalTasksHeight() {
            int maxY = 45;
            for (TimelineTask task : tasks) {
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
            return maxY - 25; // Subtract initial offset
        }

        private void setupMouseListeners() {
            MouseAdapter adapter = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    handleMousePressed(e.getX(), e.getY());
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
                }
                public void mouseMoved(MouseEvent e) {
                    updateCursor(e.getX(), e.getY());
                }
            };
            addMouseListener(adapter);
            addMouseMotionListener(adapter);
        }

        private void handleMousePressed(int x, int y) {
            if (tasks.isEmpty() || startDate == null || endDate == null) return;

            int timelineX = MARGIN_LEFT;
            int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            if (totalDays <= 0) return;

            for (int i = 0; i < tasks.size(); i++) {
                TimelineTask task = tasks.get(i);
                int taskY = getTaskY(i);
                int taskHeight = task.height;
                boolean isSelected = (i == selectedTaskIndex);
                try {
                    LocalDate taskStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                    LocalDate taskEnd = LocalDate.parse(task.endDate, DATE_FORMAT);

                    int x1 = getXForDate(taskStart, timelineX, timelineWidth, totalDays);
                    int x2 = getXForDate(taskEnd, timelineX, timelineWidth, totalDays);
                    int barWidth = Math.max(x2 - x1, 10);

                    // Check for height handles on selected task (top/bottom edges)
                    if (isSelected && x >= x1 && x <= x1 + barWidth) {
                        // Check top edge
                        if (y >= taskY - 6 && y <= taskY + 6) {
                            isHeightDragging = true;
                            heightDragTaskIndex = i;
                            draggingTop = true;
                            heightDragStartY = y;
                            heightDragOriginalHeight = task.height;
                            setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                            return;
                        }
                        // Check bottom edge
                        if (y >= taskY + taskHeight - 6 && y <= taskY + taskHeight + 6) {
                            isHeightDragging = true;
                            heightDragTaskIndex = i;
                            draggingTop = false;
                            heightDragStartY = y;
                            heightDragOriginalHeight = task.height;
                            setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                            return;
                        }
                    }

                    if (y >= taskY && y <= taskY + taskHeight) {
                        // Check start edge for dragging
                        if (x >= x1 - DRAG_HANDLE_WIDTH && x <= x1 + DRAG_HANDLE_WIDTH) {
                            isDragging = true;
                            draggingTaskIndex = i;
                            draggingStart = true;
                            setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                            selectTask(i);
                            return;
                        }
                        // Check end edge for dragging
                        if (x >= x1 + barWidth - DRAG_HANDLE_WIDTH && x <= x1 + barWidth + DRAG_HANDLE_WIDTH) {
                            isDragging = true;
                            draggingTaskIndex = i;
                            draggingStart = false;
                            setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                            selectTask(i);
                            return;
                        }
                        // Click on middle of bar - select and start free movement drag
                        if (x >= x1 && x <= x1 + barWidth) {
                            selectTask(i);
                            isMoveDragging = true;
                            moveDragTaskIndex = i;
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
            }

            // Check for milestone clicks
            int tasksHeight = getTotalTasksHeight();
            int timelineY = 50 + tasksHeight;
            for (int i = 0; i < milestones.size(); i++) {
                TimelineMilestone milestone = milestones.get(i);
                try {
                    LocalDate milestoneDate = LocalDate.parse(milestone.date, DATE_FORMAT);
                    if (milestoneDate.isBefore(startDate) || milestoneDate.isAfter(endDate)) continue;

                    int mx = getXForDate(milestoneDate, timelineX, timelineWidth, totalDays);
                    int my = milestone.yPosition >= 0 ? milestone.yPosition : timelineY - milestone.height / 2 - 10;
                    int halfW = milestone.width / 2;
                    int halfH = milestone.height / 2;

                    // Check if click is within milestone bounds
                    if (x >= mx - halfW && x <= mx + halfW && y >= my - halfH && y <= my + halfH) {
                        selectMilestone(i);
                        return;
                    }
                } catch (Exception ex) {}
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
                boolean isSelected = (i == selectedTaskIndex);
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
            if (startDate == null || endDate == null) return;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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

            // Task bars (above timeline) - draw in reverse order so top layers appear in front
            for (int i = tasks.size() - 1; i >= 0; i--) {
                int taskY = getTaskY(i);
                drawTaskBar(g2d, tasks.get(i), i, taskY, timelineX, timelineWidth, totalDays);
            }

            // Milestones (above timeline, on same level as tasks)
            for (int i = 0; i < milestones.size(); i++) {
                drawMilestone(g2d, milestones.get(i), i, timelineX, timelineWidth, timelineY, totalDays);
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
                boolean isSelected = (index == selectedTaskIndex);

                int taskHeight = task.height;

                // Selection highlight (glow effect)
                if (isSelected) {
                    g2d.setColor(new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), 100));
                    g2d.fillRoundRect(x1 - 4, y - 4, barWidth + 8, taskHeight + 8, 12, 12);
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
                    g2d.drawString(task.behindText, x1 + barWidth + 5,
                                   y + (taskHeight + behindFm.getAscent() - behindFm.getDescent()) / 2);
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
                    g2d.drawString(task.frontText, x1 - frontTextWidth - 5,
                                   y + (taskHeight + frontFm.getAscent() - frontFm.getDescent()) / 2);
                }

                g2d.setColor(fillColor);
                g2d.fillRoundRect(x1, y, barWidth, taskHeight, 8, 8);
                int thickness = task.outlineThickness;
                if (thickness > 0) {
                    g2d.setColor(isSelected ? Color.WHITE : outlineColor);
                    g2d.setStroke(new BasicStroke(isSelected ? thickness + 2 : thickness));
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
                    g2d.drawString(displayText, x1 + (barWidth - textWidth) / 2,
                                   y + (taskHeight + fm.getAscent() - fm.getDescent()) / 2);
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
                    g2d.drawString(task.aboveText, x1 + (barWidth - aboveTextWidth) / 2,
                                   y - 3);
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
                    g2d.drawString(task.underneathText, x1 + (barWidth - underneathTextWidth) / 2,
                                   y + taskHeight + underneathFm.getAscent() + 2);
                }

                // Draw drag handles (grip lines) - only when selected
                if (isSelected) {
                    // Left/Right handles for date adjustment
                    g2d.setColor(new Color(255, 255, 255, 180));
                    g2d.fillRoundRect(x1, y + 4, 8, taskHeight - 8, 3, 3);
                    g2d.fillRoundRect(x1 + barWidth - 8, y + 4, 8, taskHeight - 8, 3, 3);

                    g2d.setColor(outlineColor);
                    // Left handle lines
                    g2d.drawLine(x1 + 2, y + 7, x1 + 2, y + taskHeight - 7);
                    g2d.drawLine(x1 + 5, y + 7, x1 + 5, y + taskHeight - 7);
                    // Right handle lines
                    g2d.drawLine(x1 + barWidth - 6, y + 7, x1 + barWidth - 6, y + taskHeight - 7);
                    g2d.drawLine(x1 + barWidth - 3, y + 7, x1 + barWidth - 3, y + taskHeight - 7);

                    // Top/Bottom handles for height adjustment
                    int handleWidth = Math.min(barWidth - 20, 40);
                    int handleX = x1 + (barWidth - handleWidth) / 2;

                    // Top handle
                    g2d.setColor(new Color(255, 255, 255, 180));
                    g2d.fillRoundRect(handleX, y - 2, handleWidth, 6, 3, 3);
                    g2d.setColor(outlineColor);
                    g2d.drawLine(handleX + 5, y, handleX + handleWidth - 5, y);
                    g2d.drawLine(handleX + 5, y + 2, handleX + handleWidth - 5, y + 2);

                    // Bottom handle
                    g2d.setColor(new Color(255, 255, 255, 180));
                    g2d.fillRoundRect(handleX, y + taskHeight - 4, handleWidth, 6, 3, 3);
                    g2d.setColor(outlineColor);
                    g2d.drawLine(handleX + 5, y + taskHeight - 2, handleX + handleWidth - 5, y + taskHeight - 2);
                    g2d.drawLine(handleX + 5, y + taskHeight, handleX + handleWidth - 5, y + taskHeight);
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

                // Selection highlight (glow effect)
                if (isSelected) {
                    g2d.setColor(new Color(milestone.fillColor.getRed(), milestone.fillColor.getGreen(),
                                           milestone.fillColor.getBlue(), 100));
                    drawMilestoneShapeOnPanel(g2d, milestone.shape, x, y, milestone.width + 8, milestone.height + 8, true);
                }

                // Draw the milestone shape
                g2d.setColor(milestone.fillColor);
                drawMilestoneShapeOnPanel(g2d, milestone.shape, x, y, milestone.width, milestone.height, true);

                // Draw outline
                if (milestone.outlineThickness > 0) {
                    g2d.setColor(isSelected ? Color.WHITE : milestone.outlineColor);
                    g2d.setStroke(new BasicStroke(isSelected ? milestone.outlineThickness + 2 : milestone.outlineThickness));
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
            int interval = totalDays <= 14 ? 1 : totalDays <= 60 ? 7 : totalDays <= 180 ? 14 : totalDays <= 365 ? 30 : 90;

            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            LocalDate tick = startDate;

            while (!tick.isAfter(endDate)) {
                int x = getXForDate(tick, timelineX, timelineWidth, totalDays);

                g2d.setColor(new Color(70, 130, 180));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(x, timelineY, x, timelineY + 15);

                g2d.setColor(Color.DARK_GRAY);
                String dateStr = tick.format(DATE_FORMAT);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(dateStr, x - fm.stringWidth(dateStr) / 2, timelineY + 30);

                tick = tick.plusDays(interval);
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
