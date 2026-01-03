import javax.swing.*;
import javax.swing.event.*;
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
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import com.formdev.flatlaf.FlatLightLaf;

public class Timeline2 extends JFrame {

    // Data storage
    private ArrayList<TimelineEvent> events = new ArrayList<>();
    private ArrayList<TimelineTask> tasks = new ArrayList<>();
    private ArrayList<TimelineMilestone> milestones = new ArrayList<>();
    private ArrayList<Object> layerOrder = new ArrayList<>(); // Unified list of tasks and milestones for z-ordering
    private Set<Integer> selectedTaskIndices = new HashSet<>(); // Multi-select for tasks
    private int selectedMilestoneIndex = -1;
    private Set<Integer> selectedMilestoneIndices = new HashSet<>(); // Multi-select for milestones

    // Panel colors - Settings (Timeline Tab)
    private Color settingsInteriorColor = Color.WHITE; // #FFFFFF
    private Color settingsInteriorColor2 = Color.WHITE; // #FFFFFF
    private boolean settingsUseGradient = false;
    private String settingsGradientDir = "Vertical";
    private double settingsGradientAngle = 90.0;
    private ArrayList<float[]> settingsGradientStops = new ArrayList<>();
    private Color settingsOutlineColor = new Color(0xC8, 0xC8, 0xC8); // #C8C8C8
    private boolean settingsBorderVisible = true;
    private Color settingsHeaderColor = new Color(0x46, 0x82, 0xB4); // #4682B4
    private Color settingsHeaderColor2 = new Color(0x46, 0x82, 0xB4); // #4682B4
    private boolean settingsHeaderUseGradient = false;
    private String settingsHeaderGradientDir = "Horizontal";
    private double settingsHeaderGradientAngle = 0.0;
    private ArrayList<float[]> settingsHeaderGradientStops = new ArrayList<>();
    private Color settingsHeaderTextColor = Color.WHITE; // #FFFFFF
    private Color settingsLabelColor = Color.BLACK; // #000000
    private Color settingsFieldBgColor = Color.WHITE; // #FFFFFF
    private Color settingsButtonBgColor = new Color(0xE6, 0xE6, 0xE6); // #E6E6E6
    private Color settingsButtonTextColor = Color.BLACK; // #000000
    // Panel colors - Timeline
    private Color timelineInteriorColor = Color.WHITE; // #FFFFFF
    private Color timelineInteriorColor2 = new Color(0xE6, 0xE6, 0xE6); // #E6E6E6
    private boolean timelineUseGradient = false;
    private String timelineGradientDir = "Vertical";
    private double timelineGradientAngle = 90.0;
    private ArrayList<float[]> timelineGradientStops = new ArrayList<>();
    private Color timelineOutlineColor = new Color(0xC8, 0xC8, 0xC8); // #C8C8C8
    private Color timelineLineColor = new Color(0x46, 0x82, 0xB4); // #4682B4
    private Color timelineDateTextColor = Color.BLACK; // #000000
    private Color timelineGridColor = new Color(0xDC, 0xDC, 0xDC); // #DCDCDC
    private Color timelineEventColor = new Color(0xDC, 0x35, 0x45); // #DC3545
    private boolean showTodayMark = true;
    // Panel colors - Layers
    private Color layersInteriorColor = new Color(0xE6, 0xE6, 0xE6); // #E6E6E6
    private Color layersInteriorColor2 = new Color(0xFA, 0xFA, 0xFA); // #FAFAFA
    private boolean layersUseGradient = false;
    private double layersGradientAngle = 90.0;
    private ArrayList<float[]> layersGradientStops = new ArrayList<>();
    private Color layersOutlineColor = new Color(0xC8, 0xC8, 0xC8); // #C8C8C8
    private boolean layersBorderVisible = true;
    private Color layersHeaderColor = new Color(0xCC, 0xCC, 0xCC); // #CCCCCC
    private Color layersHeaderColor2 = new Color(0x46, 0x82, 0xB4); // #4682B4
    private boolean layersHeaderUseGradient = false;
    private double layersHeaderGradientAngle = 0.0;
    private ArrayList<float[]> layersHeaderGradientStops = new ArrayList<>();
    private Color layersHeaderTextColor = Color.WHITE; // #FFFFFF
    private Color layersListBgColor = Color.WHITE; // #FFFFFF
    private Color layersListBgColor2 = Color.WHITE; // #FFFFFF
    private boolean layersListBgUseGradient = false;
    private double layersListBgGradientAngle = 90.0;
    private ArrayList<float[]> layersListBgGradientStops = new ArrayList<>();
    private Color layersItemTextColor = Color.BLACK; // #000000
    private Color layersSelectedBgColor = new Color(0xC8, 0xC8, 0xC8); // #C8C8C8
    private Color layersDragHandleColor = new Color(0x96, 0x96, 0x96); // #969696
    private Color layersTaskColor = new Color(0xF0, 0xF0, 0xF0); // #F0F0F0
    private Color layersTaskColor2 = new Color(0xE6, 0xE6, 0xE6); // #E6E6E6
    private boolean layersTaskUseGradient = false;
    private double layersTaskGradientAngle = 90.0;
    private ArrayList<float[]> layersTaskGradientStops = new ArrayList<>();
    // Panel colors - Format
    private Color formatInteriorColor = new Color(0xFA, 0xFA, 0xFA); // #FAFAFA
    private Color formatInteriorColor2 = new Color(0xFA, 0xFA, 0xFA); // #FAFAFA
    private boolean formatUseGradient = false;
    private double formatGradientAngle = 90.0;
    private ArrayList<float[]> formatGradientStops = new ArrayList<>();
    private Color formatOutlineColor = new Color(0xC8, 0xC8, 0xC8); // #C8C8C8
    private Color formatHeaderColor = new Color(0x46, 0x82, 0xB4); // #4682B4
    private Color formatHeaderColor2 = new Color(0x46, 0x82, 0xB4); // #4682B4
    private boolean formatHeaderUseGradient = false;
    private double formatHeaderGradientAngle = 0.0;
    private ArrayList<float[]> formatHeaderGradientStops = new ArrayList<>();
    // Panel colors - Toolbar
    private Color toolbarBgColor = new Color(0xE6, 0xE6, 0xE6); // #E6E6E6
    private Color toolbarBgColor2 = new Color(0xE6, 0xE6, 0xE6); // #E6E6E6
    private boolean toolbarUseGradient = false;
    private double toolbarGradientAngle = 90.0;
    private ArrayList<float[]> toolbarGradientStops = new ArrayList<>();
    private JPanel toolbarPanel;
    private JPanel spreadsheetPanel;
    private JTable spreadsheetTable;
    private javax.swing.table.DefaultTableModel spreadsheetTableModel;
    private java.util.Map<Object, String[]> spreadsheetData = new java.util.HashMap<>();
    private java.util.Map<Object, Integer> spreadsheetRowHeights = new java.util.HashMap<>();
    private java.util.Set<String> spreadsheetWordWrapCells = new java.util.HashSet<>(); // Tracks cells with word wrap enabled (key: "rowItem.hashCode()_col")
    private java.util.List<Object> spreadsheetRowOrder = new java.util.ArrayList<>();
    private Color spreadsheetSelectionColor = new Color(70, 130, 180); // Light blue highlight
    private Color spreadsheetSelectionTextColor = Color.WHITE; // Selected text color
    private Color spreadsheetUnselectedTextColor = Color.BLACK; // Unselected text color
    private Color spreadsheetUnselectedBgColor = Color.WHITE; // Unselected background color
    private JSplitPane centerSplitPane;
    private boolean spreadsheetVisible = false;
    private int lastSpreadsheetDividerLocation = 250;
    private int lastRightPanelWidth = 290;
    private boolean rightPanelCollapsed = false;
    private int spreadsheetPanelWidth = 300;
    private Color formatLabelColor = Color.BLACK; // #000000
    private Color formatSeparatorColor = new Color(0xC8, 0xC8, 0xC8); // #C8C8C8
    // Panel colors - Right Tabbed Pane
    private Color rightTabbedBgColor = new Color(0xF0, 0xF0, 0xF0); // #F0F0F0
    private Color rightTabbedFgColor = Color.BLACK; // #000000
    private Color rightTabbedSelectedBgColor = new Color(0x46, 0x82, 0xB4); // #4682B4
    private Color rightTabbedSelectedFgColor = Color.WHITE; // #FFFFFF
    private Color rightTabbedBorderColor = new Color(0x96, 0x96, 0x96); // #969696
    private boolean rightTabbedBorderVisible = false;
    private Color rightTabbedUnderlineColor = new Color(0x46, 0x82, 0xB4); // #4682B4
    private boolean rightTabbedUnderlineVisible = true;
    // Panel colors - General Tab
    private Color generalInteriorColor = Color.WHITE; // #FFFFFF
    private Color generalInteriorColor2 = new Color(0xE6, 0xE6, 0xE6); // #E6E6E6
    private boolean generalUseGradient = false;
    private double generalGradientAngle = 90.0;
    private ArrayList<float[]> generalGradientStops = new ArrayList<>();
    private Color generalOutlineColor = new Color(0xC8, 0xC8, 0xC8); // #C8C8C8
    private boolean generalBorderVisible = true;
    private Color formatResizeHandleColor = new Color(0xE6, 0xE6, 0xE6); // #E6E6E6
    private Color formatTabColor = new Color(0xDC, 0xDC, 0xDC); // #DCDCDC
    private Color formatTabColor2 = new Color(0xDC, 0xDC, 0xDC); // #DCDCDC
    private boolean formatTabUseGradient = false;
    private double formatTabGradientAngle = 90.0;
    private ArrayList<float[]> formatTabGradientStops = new ArrayList<>();
    private Color formatSelectedTabColor = Color.WHITE; // #FFFFFF
    private Color formatSelectedTabColor2 = Color.WHITE; // #FFFFFF
    private boolean formatSelectedTabUseGradient = false;
    private double formatSelectedTabGradientAngle = 90.0;
    private ArrayList<float[]> formatSelectedTabGradientStops = new ArrayList<>();
    private Color formatTabContentColor = new Color(0xE6, 0xE6, 0xE6); // #E6E6E6
    private Color formatTabContentColor2 = new Color(0xE6, 0xE6, 0xE6); // #E6E6E6
    private boolean formatTabContentUseGradient = false;
    private double formatTabContentGradientAngle = 90.0;
    private ArrayList<float[]> formatTabContentGradientStops = new ArrayList<>();

    // UI Components
    private TimelineDisplayPanel timelineDisplayPanel;
    private JTextField startDateField, endDateField;
    private CollapsiblePanel rightPanel;
    private LayersPanel layersPanel;
    private JTabbedPane rightTabbedPane;
    private JScrollPane generalScrollPane;
    private JScrollPane settingsScrollPane;
    private JPanel settingsPanel;
    private JPanel generalPanel;
    private JPanel formatPanel;
    private JPanel formatHeaderBar;
    private JTabbedPane formatTabbedPane;
    private JTextField taskNameField, taskStartField, taskEndField;
    private JLabel formatTitleLabel;
    private JButton duplicateTaskBtn;
    private JButton deleteTaskBtn;
    private JButton fillColorBtn, outlineColorBtn, textColorBtn;
    // Notes tab fields
    private JTextArea note1Area, note2Area, note3Area, note4Area, note5Area;
    private JSpinner outlineThicknessSpinner, taskHeightSpinner, fontSizeSpinner;
    private JCheckBox bevelFillCheckbox;
    private JButton bevelSettingsBtn;
    private JComboBox<String> fontFamilyCombo;
    private JToggleButton boldBtn, italicBtn;
    private JTextField centerTextField;
    private JSpinner centerXOffsetSpinner, centerYOffsetSpinner;
    private JCheckBox centerWrapCheckbox;
    private JCheckBox centerVisibleCheckbox;
    // Front text controls
    private JTextField frontTextField;
    private JComboBox<String> frontFontCombo;
    private JSpinner frontFontSizeSpinner;
    private JToggleButton frontBoldBtn, frontItalicBtn;
    private JButton frontTextColorBtn;
    private JSpinner frontXOffsetSpinner, frontYOffsetSpinner;
    private JCheckBox frontWrapCheckbox;
    private JCheckBox frontVisibleCheckbox;
    // Above text controls
    private JTextField aboveTextField;
    private JComboBox<String> aboveFontCombo;
    private JSpinner aboveFontSizeSpinner;
    private JToggleButton aboveBoldBtn, aboveItalicBtn;
    private JButton aboveTextColorBtn;
    private JSpinner aboveXOffsetSpinner, aboveYOffsetSpinner;
    private JCheckBox aboveWrapCheckbox;
    private JCheckBox aboveVisibleCheckbox;
    // Underneath text controls
    private JTextField underneathTextField;
    private JComboBox<String> underneathFontCombo;
    private JSpinner underneathFontSizeSpinner;
    private JToggleButton underneathBoldBtn, underneathItalicBtn;
    private JButton underneathTextColorBtn;
    private JSpinner underneathXOffsetSpinner, underneathYOffsetSpinner;
    private JCheckBox underneathWrapCheckbox;
    private JCheckBox underneathVisibleCheckbox;
    // Behind text controls
    private JTextField behindTextField;
    private JComboBox<String> behindFontCombo;
    private JSpinner behindFontSizeSpinner;
    private JToggleButton behindBoldBtn, behindItalicBtn;
    private JButton behindTextColorBtn;
    private JSpinner behindXOffsetSpinner, behindYOffsetSpinner;
    private JCheckBox behindWrapCheckbox;
    private JCheckBox behindVisibleCheckbox;
    // Milestone controls
    private JTextField milestoneNameField, milestoneDateField;
    private JSpinner milestoneWidthSpinner, milestoneHeightSpinner;
    private JButton milestoneFillColorBtn, milestoneOutlineColorBtn;
    private JSpinner milestoneOutlineThicknessSpinner;
    private JCheckBox milestoneBevelCheckbox;
    private JButton milestoneBevelSettingsBtn;
    // Row 1 switcher (task vs milestone)
    private JPanel row1Container;
    private CardLayout row1CardLayout;
    // Timeline background color
    private JButton timelineBgColorBtn;
    // Timeline axis settings
    private Color timelineAxisColor = new Color(70, 130, 180);
    private int timelineAxisThickness = 3;
    private JButton timelineAxisColorBtn;
    private JSpinner timelineAxisThicknessSpinner;
    private String timelineAxisPosition = "Bottom";
    private JComboBox<String> timelineAxisPositionCombo;
    private Color timelineAxisTickColor = new Color(70, 130, 180);
    private int timelineAxisTickWidth = 3;
    private int timelineAxisTickHeight = 15;
    private JPanel axisLineColorRow;
    private JPanel axisLineThicknessRow;
    // Extend ticks settings
    private boolean extendTicks = false;
    private Color extendTicksColor = new Color(200, 200, 200);
    private int extendTicksThickness = 1;
    private String extendTicksLineType = "Solid";
    private JCheckBox extendTicksCheckBox;
    private JButton extendTicksColorBtn;
    private JSpinner extendTicksThicknessSpinner;
    private JComboBox<String> extendTicksLineTypeCombo;
    private JPanel extendTicksOptionsPanel;
    private static final String[] LINE_TYPES = {"Solid", "Dashed", "Dotted", "Dash-Dot"};
    // Timeline axis date label settings
    private Color axisDateColor = Color.DARK_GRAY;
    private String axisDateFontFamily = "SansSerif";
    private int axisDateFontSize = 10;
    private boolean axisDateBold = false;
    private boolean axisDateItalic = false;
    private JButton axisDateColorBtn;
    private JComboBox<String> axisDateFontCombo;
    private JSpinner axisDateFontSizeSpinner;
    private JToggleButton axisDateBoldBtn, axisDateItalicBtn;
    // Font family options
    private static final String[] FONT_FAMILIES = {"SansSerif", "Serif", "Monospaced", "Arial", "Times New Roman", "Courier New", "Verdana", "Georgia", "Tahoma", "Calibri"};
    // Undo/Redo
    private java.util.Deque<TimelineState> undoStack = new java.util.ArrayDeque<>();
    private java.util.Deque<TimelineState> redoStack = new java.util.ArrayDeque<>();
    private JButton undoBtn, redoBtn;
    private static final int MAX_UNDO_LEVELS = 50;
    // Keyboard shortcuts (configurable)
    private int selectNextKey = KeyEvent.VK_TAB;
    private int selectNextModifiers = 0;
    private int deleteSelectedKey = KeyEvent.VK_DELETE;
    private int deleteSelectedModifiers = InputEvent.SHIFT_DOWN_MASK;
    private int duplicateKey = KeyEvent.VK_F1;
    private int duplicateModifiers = 0;

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
        try {
            // Disable FlatLaf native library to avoid Java warning
            System.setProperty("flatlaf.useNativeLibrary", "false");
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("TabbedPane.showContentSeparator", false);
            UIManager.put("TabbedPane.contentBorderInsets", new java.awt.Insets(0, 0, 0, 0));
            UIManager.put("TabbedPane.tabsOverlapBorder", true);
            UIManager.put("TabbedPane.hasFullBorder", false);
            UIManager.put("TabbedPane.background", new Color(230, 230, 230));
            UIManager.put("TabbedPane.contentAreaColor", new Color(230, 230, 230));
            UIManager.put("Panel.background", new Color(230, 230, 230));
            UIManager.put("ScrollPane.background", new Color(230, 230, 230));
            UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        JMenuItem exportGraphicItem = new JMenuItem("Export Graphic...");
        exportGraphicItem.addActionListener(e -> exportGraphic());
        fileMenu.add(exportGraphicItem);
        fileMenu.addSeparator();
        JMenuItem changeLogItem = new JMenuItem("Change Log");
        changeLogItem.addActionListener(e -> showChangeLog());
        fileMenu.add(changeLogItem);
        fileMenu.addSeparator();
        JMenuItem restartItem = new JMenuItem("Restart Program");
        restartItem.addActionListener(e -> restartProgram());
        fileMenu.add(restartItem);
        menuBar.add(fileMenu);

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        undoItem.addActionListener(e -> undo());
        editMenu.add(undoItem);
        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        redoItem.addActionListener(e -> redo());
        editMenu.add(redoItem);
        editMenu.addSeparator();
        JMenuItem preferencesItem = new JMenuItem("Preferences");
        preferencesItem.addActionListener(e -> showPreferencesDialog());
        editMenu.add(preferencesItem);
        JMenuItem skinsItem = new JMenuItem("Skins");
        skinsItem.addActionListener(e -> showSkinsDialog());
        editMenu.add(skinsItem);
        menuBar.add(editMenu);

        // View menu
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem spreadsheetItem = new JCheckBoxMenuItem("Spreadsheet", spreadsheetVisible);
        spreadsheetItem.addActionListener(e -> {
            spreadsheetVisible = spreadsheetItem.isSelected();
            toggleSpreadsheetPanel();
        });
        viewMenu.add(spreadsheetItem);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);

        // Center - Timeline display with New Task button
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Top toolbar with New Task, New Milestone, and Clear All buttons
        toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                if (toolbarUseGradient && toolbarGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();

                    float[] fractions = new float[toolbarGradientStops.size()];
                    Color[] colors = new Color[toolbarGradientStops.size()];
                    for (int i = 0; i < toolbarGradientStops.size(); i++) {
                        float[] stop = toolbarGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }

                    java.awt.LinearGradientPaint lgp = createAngledGradient(w, h, toolbarGradientAngle, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        toolbarPanel.setBackground(toolbarBgColor);
        JButton newTaskBtn = new JButton("+ New Task");
        newTaskBtn.addActionListener(e -> addNewTask());
        toolbarPanel.add(newTaskBtn);
        JButton newMilestoneBtn = new JButton("+ New Milestone");
        newMilestoneBtn.addActionListener(e -> showMilestoneShapeMenu(newMilestoneBtn));
        toolbarPanel.add(newMilestoneBtn);

        JButton addShapesBtn = new JButton("+ Add Shapes");
        addShapesBtn.addActionListener(e -> showShapesMenu(addShapesBtn));
        toolbarPanel.add(addShapesBtn);
        duplicateTaskBtn = new JButton("Duplicate");
        duplicateTaskBtn.setEnabled(false);
        duplicateTaskBtn.setToolTipText("Duplicate selected task(s)");
        duplicateTaskBtn.addActionListener(e -> duplicateSelectedTasks());
        toolbarPanel.add(duplicateTaskBtn);
        deleteTaskBtn = new JButton("Delete");
        deleteTaskBtn.setEnabled(false);
        deleteTaskBtn.setToolTipText("Delete selected task(s) or milestone");
        deleteTaskBtn.addActionListener(e -> deleteSelectedItems());
        toolbarPanel.add(deleteTaskBtn);
        JButton clearAllBtn = new JButton("Clear All");
        clearAllBtn.addActionListener(e -> clearAll());
        toolbarPanel.add(clearAllBtn);
        undoBtn = new JButton("Undo");
        undoBtn.setEnabled(false);
        undoBtn.setToolTipText("Undo last action (Ctrl+Z)");
        undoBtn.addActionListener(e -> undo());
        toolbarPanel.add(undoBtn);
        redoBtn = new JButton("Redo");
        redoBtn.setEnabled(false);
        redoBtn.setToolTipText("Redo last undone action (Ctrl+Y)");
        redoBtn.addActionListener(e -> redo());
        toolbarPanel.add(redoBtn);

        // Version label on the right
        JLabel versionLabel = new JLabel("v4.8  ");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        versionLabel.setForeground(Color.GRAY);

        // Wrapper panel to hold toolbar on left and version on right
        JPanel toolbarWrapper = new JPanel(new BorderLayout());
        toolbarWrapper.setBackground(toolbarBgColor);
        toolbarWrapper.add(toolbarPanel, BorderLayout.CENTER);
        toolbarWrapper.add(versionLabel, BorderLayout.EAST);

        // Toolbar at top spanning full width
        add(toolbarWrapper, BorderLayout.NORTH);

        timelineDisplayPanel = new TimelineDisplayPanel();
        JScrollPane scrollPane = new JScrollPane(timelineDisplayPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Create spreadsheet panel with task table (initially hidden)
        spreadsheetPanel = new JPanel(new BorderLayout());
        spreadsheetPanel.setBackground(Color.WHITE);
        spreadsheetPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        spreadsheetPanel.setPreferredSize(new Dimension(spreadsheetPanelWidth, 0));
        
        String[] columnNames = {"Name", "Start Date", "End Date", "", "", "", "", "", "", "", "", "", "", "", ""};
        spreadsheetTableModel = new javax.swing.table.DefaultTableModel(columnNames, 0);
        spreadsheetTable = new JTable(spreadsheetTableModel);
        spreadsheetTable.setRowHeight(22);
        spreadsheetTable.getTableHeader().setReorderingAllowed(false);
        spreadsheetTable.setShowGrid(true);
        spreadsheetTable.setGridColor(new Color(200, 200, 200));
        spreadsheetTable.setIntercellSpacing(new Dimension(1, 1));
        spreadsheetTable.setCellSelectionEnabled(true);
        spreadsheetTable.setRowSelectionAllowed(false);
        spreadsheetTable.setColumnSelectionAllowed(false);
        spreadsheetTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spreadsheetTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // Auto-fit columns to header text width
        FontMetrics fm = spreadsheetTable.getFontMetrics(spreadsheetTable.getFont());
        int nameWidth = fm.stringWidth("Name") + 20;
        int startDateWidth = fm.stringWidth("Start Date") + 16;
        int endDateWidth = fm.stringWidth("End Date") + 16;
        spreadsheetTable.getColumnModel().getColumn(0).setPreferredWidth(nameWidth);
        spreadsheetTable.getColumnModel().getColumn(1).setPreferredWidth(startDateWidth);
        spreadsheetTable.getColumnModel().getColumn(2).setPreferredWidth(endDateWidth);
        for (int i = 3; i < spreadsheetTable.getColumnCount(); i++) {
            spreadsheetTable.getColumnModel().getColumn(i).setPreferredWidth(100);
        }

        // Custom cell renderer to highlight selected tasks/milestones and support word wrap
        spreadsheetTable.setDefaultRenderer(Object.class, new javax.swing.table.TableCellRenderer() {
            private final javax.swing.table.DefaultTableCellRenderer defaultRenderer = new javax.swing.table.DefaultTableCellRenderer();
            private final JTextArea wrapRenderer = new JTextArea();
            {
                wrapRenderer.setLineWrap(true);
                wrapRenderer.setWrapStyleWord(true);
                wrapRenderer.setOpaque(true);
                wrapRenderer.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                // Determine colors based on selection state
                Color bgColor = spreadsheetUnselectedBgColor;
                Color fgColor = spreadsheetUnselectedTextColor;
                if (row < spreadsheetRowOrder.size()) {
                    Object item = spreadsheetRowOrder.get(row);
                    boolean isItemSelected = false;
                    if (item instanceof TimelineTask) {
                        int taskIndex = tasks.indexOf(item);
                        isItemSelected = selectedTaskIndices.contains(taskIndex);
                    } else if (item instanceof TimelineMilestone) {
                        int msIndex = milestones.indexOf(item);
                        isItemSelected = selectedMilestoneIndices.contains(msIndex) || msIndex == selectedMilestoneIndex;
                    }
                    if (isItemSelected) {
                        bgColor = spreadsheetSelectionColor;
                        fgColor = spreadsheetSelectionTextColor;
                    }
                }
                if (isSelected) {
                    bgColor = table.getSelectionBackground();
                    fgColor = table.getSelectionForeground();
                }

                // Check if word wrap is enabled for this cell
                String cellKey = row < spreadsheetRowOrder.size() ?
                    System.identityHashCode(spreadsheetRowOrder.get(row)) + "_" + column : "";
                boolean wordWrapEnabled = spreadsheetWordWrapCells.contains(cellKey);

                if (wordWrapEnabled) {
                    wrapRenderer.setText(value != null ? value.toString() : "");
                    wrapRenderer.setBackground(bgColor);
                    wrapRenderer.setForeground(fgColor);
                    wrapRenderer.setFont(table.getFont());

                    // Add focus border when cell is selected
                    if (hasFocus) {
                        wrapRenderer.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.BLACK, 1),
                            BorderFactory.createEmptyBorder(1, 1, 1, 1)));
                    } else {
                        wrapRenderer.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
                    }

                    // Calculate preferred height for word wrap
                    int colWidth = table.getColumnModel().getColumn(column).getWidth();
                    wrapRenderer.setSize(colWidth, Short.MAX_VALUE);
                    int preferredHeight = wrapRenderer.getPreferredSize().height + 4;
                    if (table.getRowHeight(row) < preferredHeight) {
                        table.setRowHeight(row, preferredHeight);
                    }
                    return wrapRenderer;
                } else {
                    Component c = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    c.setBackground(bgColor);
                    c.setForeground(fgColor);
                    return c;
                }
            }
        });

        // Custom cell editor using Metal L&F for visible cursor
        final boolean[] isEditing = {false};

        // Add key listener to table to intercept arrow keys during editing
        spreadsheetTable.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (spreadsheetTable.isEditing()) {
                    int key = e.getKeyCode();
                    if (key == java.awt.event.KeyEvent.VK_LEFT ||
                        key == java.awt.event.KeyEvent.VK_RIGHT ||
                        key == java.awt.event.KeyEvent.VK_HOME ||
                        key == java.awt.event.KeyEvent.VK_END) {

                        Component editor = spreadsheetTable.getEditorComponent();
                        if (editor instanceof JTextField) {
                            JTextField tf = (JTextField) editor;
                            int pos = tf.getCaretPosition();
                            if (key == java.awt.event.KeyEvent.VK_LEFT && pos > 0) {
                                tf.setCaretPosition(pos - 1);
                            } else if (key == java.awt.event.KeyEvent.VK_RIGHT && pos < tf.getText().length()) {
                                tf.setCaretPosition(pos + 1);
                            } else if (key == java.awt.event.KeyEvent.VK_HOME) {
                                tf.setCaretPosition(0);
                            } else if (key == java.awt.event.KeyEvent.VK_END) {
                                tf.setCaretPosition(tf.getText().length());
                            }
                            e.consume();
                        }
                    }
                }
            }
        });

        JTextField metalTextField = new JTextField();
        try {
            metalTextField.setUI(new javax.swing.plaf.metal.MetalTextFieldUI());
        } catch (Exception ex) {}
        metalTextField.setCaretColor(Color.BLACK);
        metalTextField.getCaret().setBlinkRate(500);
        metalTextField.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 3)); // Steel blue border for edit mode

        javax.swing.DefaultCellEditor metalEditor = new javax.swing.DefaultCellEditor(metalTextField) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                JTextField tf = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
                try {
                    tf.setUI(new javax.swing.plaf.metal.MetalTextFieldUI());
                } catch (Exception ex) {}
                tf.setCaretColor(Color.BLACK);
                tf.getCaret().setBlinkRate(500);
                tf.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 3)); // Steel blue border for edit mode
                SwingUtilities.invokeLater(() -> {
                    tf.setCaretPosition(tf.getText().length()); // Start at end of text
                    tf.getCaret().setVisible(true);
                });
                return tf;
            }
        };
        metalEditor.setClickCountToStart(2); // Double-click to edit
        spreadsheetTable.setDefaultEditor(Object.class, metalEditor);

        // Add row and column resizing functionality
        final int[] resizingRow = {-1};
        final int[] startY = {0};
        final int[] startHeight = {0};
        final int[] resizingCol = {-1};
        final int[] startX = {0};
        final int[] startWidth = {0};
        final int RESIZE_MARGIN = 5;

        spreadsheetTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = spreadsheetTable.rowAtPoint(e.getPoint());
                int col = spreadsheetTable.columnAtPoint(e.getPoint());

                // Check for column border first (right edge of current column or left edge detection)
                if (col >= 0) {
                    Rectangle colRect = spreadsheetTable.getCellRect(0, col, true);
                    int rightX = colRect.x + colRect.width;
                    if (Math.abs(e.getX() - rightX) <= RESIZE_MARGIN) {
                        spreadsheetTable.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                        return;
                    } else if (col > 0) {
                        Rectangle prevColRect = spreadsheetTable.getCellRect(0, col - 1, true);
                        int prevRightX = prevColRect.x + prevColRect.width;
                        if (Math.abs(e.getX() - prevRightX) <= RESIZE_MARGIN) {
                            spreadsheetTable.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                            return;
                        }
                    }
                }

                // Check for row border
                if (row >= 0) {
                    Rectangle rect = spreadsheetTable.getCellRect(row, 0, true);
                    int bottomY = rect.y + rect.height;
                    if (Math.abs(e.getY() - bottomY) <= RESIZE_MARGIN) {
                        spreadsheetTable.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                    } else if (row > 0) {
                        Rectangle prevRect = spreadsheetTable.getCellRect(row - 1, 0, true);
                        int prevBottomY = prevRect.y + prevRect.height;
                        if (Math.abs(e.getY() - prevBottomY) <= RESIZE_MARGIN) {
                            spreadsheetTable.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                        } else {
                            spreadsheetTable.setCursor(Cursor.getDefaultCursor());
                        }
                    } else {
                        spreadsheetTable.setCursor(Cursor.getDefaultCursor());
                    }
                } else {
                    spreadsheetTable.setCursor(Cursor.getDefaultCursor());
                }
            }

            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if (resizingCol[0] >= 0) {
                    int newWidth = startWidth[0] + (e.getX() - startX[0]);
                    if (newWidth >= 20) {
                        spreadsheetTable.getColumnModel().getColumn(resizingCol[0]).setPreferredWidth(newWidth);
                    }
                } else if (resizingRow[0] >= 0) {
                    int newHeight = startHeight[0] + (e.getY() - startY[0]);
                    if (newHeight >= 16) {
                        spreadsheetTable.setRowHeight(resizingRow[0], newHeight);
                    }
                }
            }
        });

        spreadsheetTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                int row = spreadsheetTable.rowAtPoint(e.getPoint());
                int col = spreadsheetTable.columnAtPoint(e.getPoint());

                // Check for column border first
                if (col >= 0) {
                    Rectangle colRect = spreadsheetTable.getCellRect(0, col, true);
                    int rightX = colRect.x + colRect.width;
                    if (Math.abs(e.getX() - rightX) <= RESIZE_MARGIN) {
                        resizingCol[0] = col;
                        startX[0] = e.getX();
                        startWidth[0] = spreadsheetTable.getColumnModel().getColumn(col).getWidth();
                        return;
                    } else if (col > 0) {
                        Rectangle prevColRect = spreadsheetTable.getCellRect(0, col - 1, true);
                        int prevRightX = prevColRect.x + prevColRect.width;
                        if (Math.abs(e.getX() - prevRightX) <= RESIZE_MARGIN) {
                            resizingCol[0] = col - 1;
                            startX[0] = e.getX();
                            startWidth[0] = spreadsheetTable.getColumnModel().getColumn(col - 1).getWidth();
                            return;
                        }
                    }
                }

                // Check for row border
                if (row >= 0) {
                    Rectangle rect = spreadsheetTable.getCellRect(row, 0, true);
                    int bottomY = rect.y + rect.height;
                    if (Math.abs(e.getY() - bottomY) <= RESIZE_MARGIN) {
                        resizingRow[0] = row;
                        startY[0] = e.getY();
                        startHeight[0] = spreadsheetTable.getRowHeight(row);
                    } else if (row > 0) {
                        Rectangle prevRect = spreadsheetTable.getCellRect(row - 1, 0, true);
                        int prevBottomY = prevRect.y + prevRect.height;
                        if (Math.abs(e.getY() - prevBottomY) <= RESIZE_MARGIN) {
                            resizingRow[0] = row - 1;
                            startY[0] = e.getY();
                            startHeight[0] = spreadsheetTable.getRowHeight(row - 1);
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                resizingRow[0] = -1;
                resizingCol[0] = -1;
                spreadsheetTable.setCursor(Cursor.getDefaultCursor());
            }
        });

        // Deselect spreadsheet entirely when clicking off it (even when editing a cell)
        spreadsheetTable.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                // Stop any cell editing in progress
                if (spreadsheetTable.isEditing()) {
                    spreadsheetTable.getCellEditor().stopCellEditing();
                }
                // Clear the selection
                spreadsheetTable.clearSelection();
            }
        });

        // Right-click context menu for word wrap toggle
        JPopupMenu cellPopupMenu = new JPopupMenu();
        JCheckBoxMenuItem wordWrapItem = new JCheckBoxMenuItem("Word Wrap");
        cellPopupMenu.add(wordWrapItem);

        final int[] popupRow = {-1};
        final int[] popupCol = {-1};

        wordWrapItem.addActionListener(e -> {
            if (popupRow[0] >= 0 && popupRow[0] < spreadsheetRowOrder.size() && popupCol[0] >= 0) {
                Object item = spreadsheetRowOrder.get(popupRow[0]);
                String cellKey = System.identityHashCode(item) + "_" + popupCol[0];
                if (wordWrapItem.isSelected()) {
                    spreadsheetWordWrapCells.add(cellKey);
                } else {
                    spreadsheetWordWrapCells.remove(cellKey);
                    // Reset row height to default when word wrap is disabled
                    spreadsheetTable.setRowHeight(popupRow[0], 22);
                }
                spreadsheetTable.repaint();
            }
        });

        spreadsheetTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showCellPopup(e);
                }
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showCellPopup(e);
                }
            }
            private void showCellPopup(java.awt.event.MouseEvent e) {
                int row = spreadsheetTable.rowAtPoint(e.getPoint());
                int col = spreadsheetTable.columnAtPoint(e.getPoint());
                if (row >= 0 && col >= 0) {
                    popupRow[0] = row;
                    popupCol[0] = col;
                    // Update checkbox state
                    if (row < spreadsheetRowOrder.size()) {
                        Object item = spreadsheetRowOrder.get(row);
                        String cellKey = System.identityHashCode(item) + "_" + col;
                        wordWrapItem.setSelected(spreadsheetWordWrapCells.contains(cellKey));
                    }
                    cellPopupMenu.show(spreadsheetTable, e.getX(), e.getY());
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(spreadsheetTable);
        
        // Add toolbar at top of spreadsheet with Select Columns button
        JPanel spreadsheetToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        spreadsheetToolbar.setBackground(new Color(240, 240, 240));
        JButton selectColumnsBtn = new JButton("Select Columns");
        selectColumnsBtn.setMargin(new Insets(2, 8, 2, 8));
        

        selectColumnsBtn.addActionListener(e -> {
            JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(spreadsheetPanel), "Select Column Types", true);
            dlg.setLayout(new BorderLayout());
            JPanel colsPanel = new JPanel(new GridLayout(0, 2, 10, 8));
            colsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            String[] opts = {"(none)", "Name", "Start Date", "End Date", "Duration", "Center Text", "Above Text", "Underneath Text", "Front Text", "Behind Text"};
            int colCount = spreadsheetTable.getColumnCount();
            JComboBox[] cbs = new JComboBox[colCount];
            for (int c = 0; c < colCount; c++) {
                colsPanel.add(new JLabel("Column " + (c + 1) + ":"));
                cbs[c] = new JComboBox<>(opts);
                String hdr = spreadsheetTable.getColumnModel().getColumn(c).getHeaderValue().toString();
                for (String o : opts) if (o.equals(hdr)) cbs[c].setSelectedItem(o);
                colsPanel.add(cbs[c]);
            }
            JScrollPane scrollP = new JScrollPane(colsPanel);
            scrollP.setPreferredSize(new Dimension(300, 400));
            scrollP.getVerticalScrollBar().setUnitIncrement(16);
            dlg.add(scrollP, BorderLayout.CENTER);
            JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton applyB = new JButton("Apply");
            applyB.addActionListener(ev -> {
                for (int c = 0; c < cbs.length; c++) {
                    String sel = (String) cbs[c].getSelectedItem();
                    if (sel == null || "(none)".equals(sel)) continue;
                    spreadsheetTable.getColumnModel().getColumn(c).setHeaderValue(sel);
                    for (int r = 0; r < spreadsheetTableModel.getRowCount(); r++) {
                        if (r < spreadsheetRowOrder.size()) {
                            Object item = spreadsheetRowOrder.get(r);
                            String val = "";
                            if ("Name".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).name;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).name;
                            } else if ("Start Date".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).startDate;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).date;
                            } else if ("End Date".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).endDate;
                            } else if ("Duration".equals(sel)) {
                                if (item instanceof TimelineTask) {
                                    try {
                                        LocalDate start = LocalDate.parse(((TimelineTask) item).startDate, DATE_FORMAT);
                                        LocalDate end = LocalDate.parse(((TimelineTask) item).endDate, DATE_FORMAT);
                                        val = String.valueOf(java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1);
                                    } catch (Exception ex) { val = ""; }
                                } else { val = "1"; }
                            } else if ("Center Text".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).centerText;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).centerText;
                            } else if ("Above Text".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).aboveText;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).aboveText;
                            } else if ("Underneath Text".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).underneathText;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).underneathText;
                            } else if ("Front Text".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).frontText;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).frontText;
                            } else if ("Behind Text".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).behindText;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).behindText;
                            }
                            spreadsheetTableModel.setValueAt(val, r, c);
                        }
                    }
                    if ("Center Text".equals(sel)) {
                        javax.swing.table.DefaultTableCellRenderer cr = new javax.swing.table.DefaultTableCellRenderer();
                        cr.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                        spreadsheetTable.getColumnModel().getColumn(c).setCellRenderer(cr);
                    }
                }
                spreadsheetTable.getTableHeader().repaint();
                spreadsheetTable.repaint();
                dlg.dispose();
            });
            btnPnl.add(applyB);
            JButton canB = new JButton("Cancel");
            canB.addActionListener(ev -> dlg.dispose());
            btnPnl.add(canB);
            dlg.add(btnPnl, BorderLayout.SOUTH);
            dlg.pack();
            dlg.setLocationRelativeTo(spreadsheetPanel);
            dlg.setVisible(true);
        });
        spreadsheetToolbar.add(selectColumnsBtn);

        // Export button
        JButton exportBtn = new JButton("Export");
        exportBtn.setMargin(new Insets(2, 8, 2, 8));
        exportBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Export Spreadsheet");
            fc.setSelectedFile(new java.io.File("spreadsheet.csv"));
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
            if (fc.showSaveDialog(spreadsheetPanel) == JFileChooser.APPROVE_OPTION) {
                try (java.io.PrintWriter pw = new java.io.PrintWriter(fc.getSelectedFile())) {
                    // Write headers
                    StringBuilder header = new StringBuilder();
                    for (int c = 0; c < spreadsheetTable.getColumnCount(); c++) {
                        if (c > 0) header.append(",");
                        String h = spreadsheetTable.getColumnModel().getColumn(c).getHeaderValue().toString();
                        header.append("\"").append(h.replace("\"", "\"\"")).append("\"");
                    }
                    pw.println(header);
                    // Write data
                    for (int r = 0; r < spreadsheetTableModel.getRowCount(); r++) {
                        StringBuilder row = new StringBuilder();
                        for (int c = 0; c < spreadsheetTableModel.getColumnCount(); c++) {
                            if (c > 0) row.append(",");
                            Object val = spreadsheetTableModel.getValueAt(r, c);
                            String s = val != null ? val.toString() : "";
                            row.append("\"").append(s.replace("\"", "\"\"")).append("\"");
                        }
                        pw.println(row);
                    }
                    JOptionPane.showMessageDialog(spreadsheetPanel, "Exported successfully!", "Export", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(spreadsheetPanel, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        spreadsheetToolbar.add(exportBtn);

        // Import button
        JButton importBtn = new JButton("Import");
        importBtn.setMargin(new Insets(2, 8, 2, 8));
        importBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Import Spreadsheet");
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
            if (fc.showOpenDialog(spreadsheetPanel) == JFileChooser.APPROVE_OPTION) {
                try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fc.getSelectedFile()))) {
                    String line;
                    int rowIdx = 0;
                    boolean firstLine = true;
                    while ((line = br.readLine()) != null) {
                        java.util.List<String> values = new java.util.ArrayList<>();
                        StringBuilder current = new StringBuilder();
                        boolean inQuotes = false;
                        for (int i = 0; i < line.length(); i++) {
                            char ch = line.charAt(i);
                            if (ch == '"') {
                                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                                    current.append('"');
                                    i++;
                                } else {
                                    inQuotes = !inQuotes;
                                }
                            } else if (ch == ',' && !inQuotes) {
                                values.add(current.toString());
                                current = new StringBuilder();
                            } else {
                                current.append(ch);
                            }
                        }
                        values.add(current.toString());

                        if (firstLine) {
                            // Set column headers
                            for (int c = 0; c < Math.min(values.size(), spreadsheetTable.getColumnCount()); c++) {
                                spreadsheetTable.getColumnModel().getColumn(c).setHeaderValue(values.get(c));
                            }
                            spreadsheetTable.getTableHeader().repaint();
                            firstLine = false;
                        } else {
                            // Set row data
                            if (rowIdx < spreadsheetTableModel.getRowCount()) {
                                for (int c = 0; c < Math.min(values.size(), spreadsheetTableModel.getColumnCount()); c++) {
                                    spreadsheetTableModel.setValueAt(values.get(c), rowIdx, c);
                                }
                            }
                            rowIdx++;
                        }
                    }
                    spreadsheetTable.repaint();
                    JOptionPane.showMessageDialog(spreadsheetPanel, "Imported successfully!", "Import", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(spreadsheetPanel, "Import failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        spreadsheetToolbar.add(importBtn);

        spreadsheetPanel.add(spreadsheetToolbar, BorderLayout.NORTH);

        spreadsheetPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Create split pane with spreadsheet on left, timeline on right
        centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spreadsheetPanel, scrollPane);
        centerSplitPane.setDividerLocation(0);
        centerSplitPane.setDividerSize(8);
        centerSplitPane.setContinuousLayout(true);

        // Custom UI for the divider with 3 dots and click-to-collapse
        centerSplitPane.setUI(new javax.swing.plaf.basic.BasicSplitPaneUI() {
            @Override
            public javax.swing.plaf.basic.BasicSplitPaneDivider createDefaultDivider() {
                return new javax.swing.plaf.basic.BasicSplitPaneDivider(this) {
                    private boolean isHovered = false;

                    @Override
                    public void paint(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        int w = getWidth();
                        int h = getHeight();

                        // Draw background with hover effect
                        if (isHovered) {
                            g2d.setColor(new Color(200, 200, 200));
                        } else {
                            g2d.setColor(new Color(235, 235, 235));
                        }
                        g2d.fillRect(0, 0, w, h);

                        // Draw 3 dots
                        int dotSize = 3;
                        int spacing = 5;
                        int totalHeight = dotSize * 3 + spacing * 2;
                        int startY = (h - totalHeight) / 2;

                        g2d.setColor(new Color(120, 120, 120));
                        for (int i = 0; i < 3; i++) {
                            int y = startY + i * (dotSize + spacing);
                            int x = (w - dotSize) / 2;
                            g2d.fillOval(x, y, dotSize, dotSize);
                        }
                    }

                    {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                        // Add mouse listener for click-to-collapse and hover effect
                        addMouseListener(new java.awt.event.MouseAdapter() {
                            @Override
                            public void mouseClicked(java.awt.event.MouseEvent e) {
                                // Single click toggles
                                if (centerSplitPane.getDividerLocation() <= 5) {
                                    // Currently collapsed - expand
                                    spreadsheetVisible = true;
                                    centerSplitPane.setDividerLocation(lastSpreadsheetDividerLocation > 50 ? lastSpreadsheetDividerLocation : 250);
                                    updateSpreadsheet();
                                } else {
                                    // Currently expanded - collapse
                                    lastSpreadsheetDividerLocation = centerSplitPane.getDividerLocation();
                                    spreadsheetVisible = false;
                                    centerSplitPane.setDividerLocation(0);
                                }
                            }

                            @Override
                            public void mouseEntered(java.awt.event.MouseEvent e) {
                                isHovered = true;
                                repaint();
                            }

                            @Override
                            public void mouseExited(java.awt.event.MouseEvent e) {
                                isHovered = false;
                                repaint();
                            }
                        });
                    }
                };
            }
        });

        centerPanel.add(centerSplitPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Right panel - Timeline Range at top, then tabs with Layers and Settings
        layersPanel = new LayersPanel();
        settingsPanel = createSettingsPanel();
        JPanel timelineRangePanel = createTimelineRangePanel();

        generalPanel = createGeneralPanel();
        rightTabbedPane = new JTabbedPane();

        // Create scroll panes with borders for tab content
        generalScrollPane = new JScrollPane(generalPanel);
        settingsScrollPane = new JScrollPane(settingsPanel);

        rightTabbedPane.addTab("Layers", layersPanel);
        rightTabbedPane.addTab("General", generalScrollPane);
        rightTabbedPane.addTab("Time Axis", settingsScrollPane);

        // Apply initial border and color settings
        applyTabContentBorders();
        applyRightTabbedPaneColors();

        JPanel rightTabbedWrapper = new JPanel(new BorderLayout());
        rightTabbedWrapper.add(timelineRangePanel, BorderLayout.NORTH);
        rightTabbedWrapper.add(rightTabbedPane, BorderLayout.CENTER);

        rightPanel = new CollapsiblePanel("Right Panel", rightTabbedWrapper, false);
        rightPanel.setHeaderVisible(false);

        // Create a divider panel with 3 dots for the right panel
        JPanel rightDividerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int dotSize = 3;
                int spacing = 5;
                int totalHeight = dotSize * 3 + spacing * 2;
                int startY = (h - totalHeight) / 2;

                g2d.setColor(new Color(120, 120, 120));
                for (int i = 0; i < 3; i++) {
                    int y = startY + i * (dotSize + spacing);
                    int x = (w - dotSize) / 2;
                    g2d.fillOval(x, y, dotSize, dotSize);
                }
            }
        };
        rightDividerPanel.setPreferredSize(new Dimension(8, 0));
        rightDividerPanel.setBackground(new Color(235, 235, 235));
        rightDividerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightDividerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Save current left panel state before toggle
                int savedDividerLocation = centerSplitPane.getDividerLocation();

                if (rightPanelCollapsed) {
                    // Expand
                    rightPanelCollapsed = false;
                    rightPanel.setVisible(true);
                    rightPanel.setPreferredSize(new Dimension(lastRightPanelWidth, 0));
                } else {
                    // Collapse
                    lastRightPanelWidth = rightPanel.getWidth();
                    rightPanelCollapsed = true;
                    rightPanel.setVisible(false);
                }
                Timeline2.this.revalidate();
                Timeline2.this.repaint();

                // Restore left panel state after revalidate
                centerSplitPane.setDividerLocation(savedDividerLocation);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                rightDividerPanel.setBackground(new Color(200, 200, 200));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                rightDividerPanel.setBackground(new Color(235, 235, 235));
            }
        });

        // Wrapper panel to hold divider and rightPanel
        JPanel rightPanelWrapper = new JPanel(new BorderLayout());
        rightPanelWrapper.add(rightDividerPanel, BorderLayout.WEST);
        rightPanelWrapper.add(rightPanel, BorderLayout.CENTER);

        add(rightPanelWrapper, BorderLayout.EAST);

        // Bottom - Format panel
        formatPanel = createFormatPanel();
        add(formatPanel, BorderLayout.SOUTH);

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        refreshTimeline();
    }

    private JPanel createFormatPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                if (formatUseGradient && formatGradientStops.size() >= 2) {
                    // Make children non-opaque so gradient shows through
                    for (Component c : getComponents()) {
                        if (c instanceof JPanel) {
                            setFormatChildrenOpaque((JPanel) c, false);
                        }
                    }

                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();

                    float[] fractions = new float[formatGradientStops.size()];
                    Color[] colors = new Color[formatGradientStops.size()];
                    for (int i = 0; i < formatGradientStops.size(); i++) {
                        float[] stop = formatGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }

                    java.awt.LinearGradientPaint lgp = createAngledGradient(w, h, formatGradientAngle, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.setPreferredSize(new Dimension(0, 250));
        panel.setBackground(new Color(230, 230, 230));

        // Content wrapper
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(2, 10, 5, 10));

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
        JPanel taskRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 2));
        taskRow.setOpaque(false);

        formatTitleLabel = new JLabel("No task selected");
        formatTitleLabel.setFont(new Font("Arial", Font.BOLD, 12));

        taskRow.add(new JLabel("Name:"));
        taskNameField = new JTextField(6);
        taskNameField.setPreferredSize(new Dimension(40, taskNameField.getPreferredSize().height));
        taskNameField.setEnabled(false);
        taskNameField.addActionListener(e -> updateSelectedTaskName());
        taskNameField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateSelectedTaskName(); }
        });
        taskRow.add(taskNameField);

        taskRow.add(new JLabel("Start:"));
        taskStartField = new JTextField(6);
        taskStartField.setEnabled(false);
        taskStartField.addActionListener(e -> updateSelectedTaskDates());
        taskStartField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateSelectedTaskDates(); }
        });
        taskRow.add(taskStartField);

        taskRow.add(new JLabel("End:"));
        taskEndField = new JTextField(6);
        taskEndField.setEnabled(false);
        taskEndField.addActionListener(e -> updateSelectedTaskDates());
        taskEndField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateSelectedTaskDates(); }
        });
        taskRow.add(taskEndField);

        taskRow.add(new JLabel("Fill:"));
        fillColorBtn = new JButton();
        fillColorBtn.setPreferredSize(new Dimension(30, 25));
        fillColorBtn.setEnabled(false);
        fillColorBtn.setToolTipText("Click to change fill color");
        fillColorBtn.addActionListener(e -> chooseFillColor());
        taskRow.add(fillColorBtn);

        taskRow.add(new JLabel("Outline:"));
        outlineColorBtn = new JButton();
        outlineColorBtn.setPreferredSize(new Dimension(30, 25));
        outlineColorBtn.setEnabled(false);
        outlineColorBtn.setToolTipText("Click to change outline color");
        outlineColorBtn.addActionListener(e -> chooseOutlineColor());
        taskRow.add(outlineColorBtn);

        taskRow.add(new JLabel("Thickness:"));
        outlineThicknessSpinner = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));
        outlineThicknessSpinner.setPreferredSize(new Dimension(50, 25));
        outlineThicknessSpinner.setEnabled(false);
        outlineThicknessSpinner.setToolTipText("Outline thickness (0-10)");
        outlineThicknessSpinner.addChangeListener(e -> updateOutlineThickness());
        taskRow.add(outlineThicknessSpinner);

        taskRow.add(new JLabel("H:"));
        taskHeightSpinner = new JSpinner(new SpinnerNumberModel(25, 10, 100, 5));
        taskHeightSpinner.setPreferredSize(new Dimension(55, 25));
        taskHeightSpinner.setEnabled(false);
        taskHeightSpinner.setToolTipText("Task bar height (10-100)");
        taskHeightSpinner.addChangeListener(e -> updateTaskHeight());
        taskRow.add(taskHeightSpinner);

        bevelFillCheckbox = new JCheckBox("Bevel");
        bevelFillCheckbox.setOpaque(false);
        bevelFillCheckbox.setEnabled(false);
        bevelFillCheckbox.setToolTipText("Apply bevel effect to fill");
        bevelFillCheckbox.addActionListener(e -> updateBevelFill());
        taskRow.add(bevelFillCheckbox);

        bevelSettingsBtn = new JButton("...");
        bevelSettingsBtn.setPreferredSize(new Dimension(30, 22));
        bevelSettingsBtn.setEnabled(false);
        bevelSettingsBtn.setToolTipText("Bevel settings");
        bevelSettingsBtn.addActionListener(e -> showBevelSettingsDialog());
        taskRow.add(bevelSettingsBtn);

        JLabel taskMoreLabel = new JLabel("More >");
        taskMoreLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        taskMoreLabel.setForeground(Color.BLACK);
        taskMoreLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        taskMoreLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        taskRow.add(taskMoreLabel);

        row1Container.add(taskRow, "task");

        // Milestone row panel
        JPanel milestoneRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 2));
        milestoneRow.setOpaque(false);

        milestoneRow.add(new JLabel("Name:"));
        milestoneNameField = new JTextField(6);
        milestoneNameField.setPreferredSize(new Dimension(40, milestoneNameField.getPreferredSize().height));
        milestoneNameField.setEnabled(false);
        milestoneNameField.addActionListener(e -> updateSelectedMilestoneName());
        milestoneNameField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateSelectedMilestoneName(); }
        });
        milestoneRow.add(milestoneNameField);

        milestoneRow.add(new JLabel("Date:"));
        milestoneDateField = new JTextField(6);
        milestoneDateField.setEnabled(false);
        milestoneDateField.addActionListener(e -> updateSelectedMilestoneDate());
        milestoneDateField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateSelectedMilestoneDate(); }
        });
        milestoneRow.add(milestoneDateField);

        milestoneRow.add(new JLabel("Fill:"));
        milestoneFillColorBtn = new JButton();
        milestoneFillColorBtn.setPreferredSize(new Dimension(30, 25));
        milestoneFillColorBtn.setEnabled(false);
        milestoneFillColorBtn.setToolTipText("Click to change fill color");
        milestoneFillColorBtn.addActionListener(e -> chooseMilestoneFillColor());
        milestoneRow.add(milestoneFillColorBtn);

        milestoneRow.add(new JLabel("Outline:"));
        milestoneOutlineColorBtn = new JButton();
        milestoneOutlineColorBtn.setPreferredSize(new Dimension(30, 25));
        milestoneOutlineColorBtn.setEnabled(false);
        milestoneOutlineColorBtn.setToolTipText("Click to change outline color");
        milestoneOutlineColorBtn.addActionListener(e -> chooseMilestoneOutlineColor());
        milestoneRow.add(milestoneOutlineColorBtn);

        milestoneRow.add(new JLabel("Thickness:"));
        milestoneOutlineThicknessSpinner = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));
        milestoneOutlineThicknessSpinner.setPreferredSize(new Dimension(50, 25));
        milestoneOutlineThicknessSpinner.setEnabled(false);
        milestoneOutlineThicknessSpinner.addChangeListener(e -> updateMilestoneOutlineThickness());
        milestoneRow.add(milestoneOutlineThicknessSpinner);

        milestoneRow.add(new JLabel("H:"));
        milestoneHeightSpinner = new JSpinner(new SpinnerNumberModel(20, 10, 100, 5));
        milestoneHeightSpinner.setPreferredSize(new Dimension(55, 25));
        milestoneHeightSpinner.setEnabled(false);
        milestoneHeightSpinner.addChangeListener(e -> updateMilestoneHeight());
        milestoneRow.add(milestoneHeightSpinner);

        milestoneRow.add(new JLabel("W:"));
        milestoneWidthSpinner = new JSpinner(new SpinnerNumberModel(20, 10, 100, 5));
        milestoneWidthSpinner.setPreferredSize(new Dimension(55, 25));
        milestoneWidthSpinner.setEnabled(false);
        milestoneWidthSpinner.addChangeListener(e -> updateMilestoneWidth());
        milestoneRow.add(milestoneWidthSpinner);

        milestoneBevelCheckbox = new JCheckBox("Bevel");
        milestoneBevelCheckbox.setOpaque(false);
        milestoneBevelCheckbox.setEnabled(false);
        milestoneBevelCheckbox.setToolTipText("Apply bevel effect to fill");
        milestoneBevelCheckbox.addActionListener(e -> updateMilestoneBevel());
        milestoneRow.add(milestoneBevelCheckbox);

        milestoneBevelSettingsBtn = new JButton("...");
        milestoneBevelSettingsBtn.setPreferredSize(new Dimension(30, 22));
        milestoneBevelSettingsBtn.setEnabled(false);
        milestoneBevelSettingsBtn.setToolTipText("Bevel settings");
        milestoneBevelSettingsBtn.addActionListener(e -> showBevelSettingsDialog());
        milestoneRow.add(milestoneBevelSettingsBtn);

        JLabel milestoneMoreLabel = new JLabel("More >");
        milestoneMoreLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        milestoneMoreLabel.setForeground(Color.BLACK);
        milestoneMoreLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        milestoneMoreLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        milestoneRow.add(milestoneMoreLabel);

        row1Container.add(milestoneRow, "milestone");

        // Show task row by default
        row1CardLayout.show(row1Container, "task");

        // Row 2: Front text fields (text in front of task bar)
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 2));
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
        frontFontCombo = new JComboBox<>(FONT_FAMILIES);
        frontFontCombo.setPreferredSize(new Dimension(100, 25));
        frontFontCombo.setMaximumSize(new Dimension(100, 25));
        frontFontCombo.setEnabled(false);
        frontFontCombo.setToolTipText("Front text font family");
        frontFontCombo.addActionListener(e -> updateFrontFontFamily());
        row2.add(frontFontCombo);

        row2.add(Box.createHorizontalStrut(5));
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
        row2.add(Box.createHorizontalStrut(5));
        frontWrapCheckbox = new JCheckBox("Wrap");
        frontWrapCheckbox.setOpaque(false);
        frontWrapCheckbox.setEnabled(false);
        frontWrapCheckbox.setToolTipText("Wrap text");
        frontWrapCheckbox.addActionListener(e -> updateFrontTextWrap());
        row2.add(frontWrapCheckbox);
        frontVisibleCheckbox = new JCheckBox("Visible");
        frontVisibleCheckbox.setOpaque(false);
        frontVisibleCheckbox.setEnabled(false);
        frontVisibleCheckbox.setSelected(true);
        frontVisibleCheckbox.setToolTipText("Show/hide front text");
        frontVisibleCheckbox.addActionListener(e -> updateFrontTextVisible());
        row2.add(frontVisibleCheckbox);

        contentPanel.add(row2);

        // Row 3: Center text fields (text on the task bar)
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 2));
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
        fontFamilyCombo = new JComboBox<>(FONT_FAMILIES);
        fontFamilyCombo.setPreferredSize(new Dimension(100, 25));
        fontFamilyCombo.setMaximumSize(new Dimension(100, 25));
        fontFamilyCombo.setEnabled(false);
        fontFamilyCombo.setToolTipText("Font family");
        fontFamilyCombo.addActionListener(e -> updateFontFamily());
        row3.add(fontFamilyCombo);

        row3.add(Box.createHorizontalStrut(5));
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
        row3.add(Box.createHorizontalStrut(5));
        centerWrapCheckbox = new JCheckBox("Wrap");
        centerWrapCheckbox.setOpaque(false);
        centerWrapCheckbox.setEnabled(false);
        centerWrapCheckbox.setToolTipText("Wrap text within task bar");
        centerWrapCheckbox.addActionListener(e -> updateCenterTextWrap());
        row3.add(centerWrapCheckbox);
        centerVisibleCheckbox = new JCheckBox("Visible");
        centerVisibleCheckbox.setOpaque(false);
        centerVisibleCheckbox.setEnabled(false);
        centerVisibleCheckbox.setSelected(true);
        centerVisibleCheckbox.setToolTipText("Show/hide center text");
        centerVisibleCheckbox.addActionListener(e -> updateCenterTextVisible());
        row3.add(centerVisibleCheckbox);

        contentPanel.add(row3);

        // Row 4: Above text fields
        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 2));
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
        aboveFontCombo = new JComboBox<>(FONT_FAMILIES);
        aboveFontCombo.setPreferredSize(new Dimension(100, 25));
        aboveFontCombo.setMaximumSize(new Dimension(100, 25));
        aboveFontCombo.setEnabled(false);
        aboveFontCombo.setToolTipText("Above text font family");
        aboveFontCombo.addActionListener(e -> updateAboveFontFamily());
        row4.add(aboveFontCombo);
        row4.add(Box.createHorizontalStrut(5));
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
        row4.add(Box.createHorizontalStrut(5));
        aboveWrapCheckbox = new JCheckBox("Wrap");
        aboveWrapCheckbox.setOpaque(false);
        aboveWrapCheckbox.setEnabled(false);
        aboveWrapCheckbox.setToolTipText("Wrap text");
        aboveWrapCheckbox.addActionListener(e -> updateAboveTextWrap());
        row4.add(aboveWrapCheckbox);
        aboveVisibleCheckbox = new JCheckBox("Visible");
        aboveVisibleCheckbox.setOpaque(false);
        aboveVisibleCheckbox.setEnabled(false);
        aboveVisibleCheckbox.setSelected(true);
        aboveVisibleCheckbox.setToolTipText("Show/hide above text");
        aboveVisibleCheckbox.addActionListener(e -> updateAboveTextVisible());
        row4.add(aboveVisibleCheckbox);
        contentPanel.add(row4);

        // Row 5: Underneath text fields
        JPanel row5 = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 2));
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
        underneathFontCombo = new JComboBox<>(FONT_FAMILIES);
        underneathFontCombo.setPreferredSize(new Dimension(100, 25));
        underneathFontCombo.setMaximumSize(new Dimension(100, 25));
        underneathFontCombo.setEnabled(false);
        underneathFontCombo.setToolTipText("Underneath text font family");
        underneathFontCombo.addActionListener(e -> updateUnderneathFontFamily());
        row5.add(underneathFontCombo);
        row5.add(Box.createHorizontalStrut(5));
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
        row5.add(Box.createHorizontalStrut(5));
        underneathWrapCheckbox = new JCheckBox("Wrap");
        underneathWrapCheckbox.setOpaque(false);
        underneathWrapCheckbox.setEnabled(false);
        underneathWrapCheckbox.setToolTipText("Wrap text");
        underneathWrapCheckbox.addActionListener(e -> updateUnderneathTextWrap());
        row5.add(underneathWrapCheckbox);
        underneathVisibleCheckbox = new JCheckBox("Visible");
        underneathVisibleCheckbox.setOpaque(false);
        underneathVisibleCheckbox.setEnabled(false);
        underneathVisibleCheckbox.setSelected(true);
        underneathVisibleCheckbox.setToolTipText("Show/hide underneath text");
        underneathVisibleCheckbox.addActionListener(e -> updateUnderneathTextVisible());
        row5.add(underneathVisibleCheckbox);
        contentPanel.add(row5);

        // Row 6: Behind text fields
        JPanel row6 = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 2));
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
        behindFontCombo = new JComboBox<>(FONT_FAMILIES);
        behindFontCombo.setPreferredSize(new Dimension(100, 25));
        behindFontCombo.setMaximumSize(new Dimension(100, 25));
        behindFontCombo.setEnabled(false);
        behindFontCombo.setToolTipText("Behind text font family");
        behindFontCombo.addActionListener(e -> updateBehindFontFamily());
        row6.add(behindFontCombo);
        row6.add(Box.createHorizontalStrut(5));
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
        row6.add(Box.createHorizontalStrut(5));
        behindWrapCheckbox = new JCheckBox("Wrap");
        behindWrapCheckbox.setOpaque(false);
        behindWrapCheckbox.setEnabled(false);
        behindWrapCheckbox.setToolTipText("Wrap text");
        behindWrapCheckbox.addActionListener(e -> updateBehindTextWrap());
        row6.add(behindWrapCheckbox);
        behindVisibleCheckbox = new JCheckBox("Visible");
        behindVisibleCheckbox.setOpaque(false);
        behindVisibleCheckbox.setEnabled(false);
        behindVisibleCheckbox.setSelected(true);
        behindVisibleCheckbox.setToolTipText("Show/hide behind text");
        behindVisibleCheckbox.addActionListener(e -> updateBehindTextVisible());
        row6.add(behindVisibleCheckbox);
        contentPanel.add(row6);
        contentPanel.add(Box.createVerticalStrut(3));

        // Create Notes panel with labels above large text areas with scroll bars
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setOpaque(false);
        notesPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Container for 5 columns
        JPanel columnsPanel = new JPanel(new GridLayout(1, 5, 5, 0));
        columnsPanel.setOpaque(false);

        // Note 1 column
        JPanel note1Panel = new JPanel(new BorderLayout(0, 2));
        note1Panel.setOpaque(false);
        note1Panel.add(new JLabel("Note 1"), BorderLayout.NORTH);
        note1Area = new JTextArea(6, 15);
        note1Area.setEnabled(false);
        note1Area.setLineWrap(true);
        note1Area.setWrapStyleWord(true);
        note1Area.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateNotes(); }
        });
        note1Panel.add(new JScrollPane(note1Area), BorderLayout.CENTER);
        columnsPanel.add(note1Panel);

        // Note 2 column
        JPanel note2Panel = new JPanel(new BorderLayout(0, 2));
        note2Panel.setOpaque(false);
        note2Panel.add(new JLabel("Note 2"), BorderLayout.NORTH);
        note2Area = new JTextArea(6, 15);
        note2Area.setEnabled(false);
        note2Area.setLineWrap(true);
        note2Area.setWrapStyleWord(true);
        note2Area.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateNotes(); }
        });
        note2Panel.add(new JScrollPane(note2Area), BorderLayout.CENTER);
        columnsPanel.add(note2Panel);

        // Note 3 column
        JPanel note3Panel = new JPanel(new BorderLayout(0, 2));
        note3Panel.setOpaque(false);
        note3Panel.add(new JLabel("Note 3"), BorderLayout.NORTH);
        note3Area = new JTextArea(6, 15);
        note3Area.setEnabled(false);
        note3Area.setLineWrap(true);
        note3Area.setWrapStyleWord(true);
        note3Area.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateNotes(); }
        });
        note3Panel.add(new JScrollPane(note3Area), BorderLayout.CENTER);
        columnsPanel.add(note3Panel);

        // Note 4 column
        JPanel note4Panel = new JPanel(new BorderLayout(0, 2));
        note4Panel.setOpaque(false);
        note4Panel.add(new JLabel("Note 4"), BorderLayout.NORTH);
        note4Area = new JTextArea(6, 15);
        note4Area.setEnabled(false);
        note4Area.setLineWrap(true);
        note4Area.setWrapStyleWord(true);
        note4Area.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateNotes(); }
        });
        note4Panel.add(new JScrollPane(note4Area), BorderLayout.CENTER);
        columnsPanel.add(note4Panel);

        // Note 5 column
        JPanel note5Panel = new JPanel(new BorderLayout(0, 2));
        note5Panel.setOpaque(false);
        note5Panel.add(new JLabel("Note 5"), BorderLayout.NORTH);
        note5Area = new JTextArea(6, 15);
        note5Area.setEnabled(false);
        note5Area.setLineWrap(true);
        note5Area.setWrapStyleWord(true);
        note5Area.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateNotes(); }
        });
        note5Panel.add(new JScrollPane(note5Area), BorderLayout.CENTER);
        columnsPanel.add(note5Panel);

        notesPanel.add(columnsPanel, BorderLayout.CENTER);

        // Create tabbed pane with custom tab colors and gradient support
        formatTabbedPane = new JTabbedPane();
        formatTabbedPane.setUI(new GradientTabbedPaneUI());
        formatTabbedPane.setFont(new Font("Arial", Font.PLAIN, 11));
        formatTabbedPane.addTab("Details", contentPanel);
        formatTabbedPane.addTab("Notes", notesPanel);
        formatTabbedPane.setBackground(formatTabColor);

        // Hide tabs by default, set collapsed panel size
        formatTabbedPane.setVisible(false);
        panel.setPreferredSize(new Dimension(0, 45));

        // Add mouse listeners to both More labels
        java.awt.event.MouseAdapter moreMouseAdapter = new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean visible = !formatTabbedPane.isVisible();
                formatTabbedPane.setVisible(visible);
                String text = visible ? "Less <" : "More >";
                taskMoreLabel.setText(text);
                milestoneMoreLabel.setText(text);
                panel.setPreferredSize(new Dimension(0, visible ? 230 : 45));
                panel.revalidate();
                Timeline2.this.revalidate();
                Timeline2.this.repaint();
            }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                ((JLabel)e.getSource()).setFont(new Font("Arial", Font.BOLD, 13));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                ((JLabel)e.getSource()).setFont(new Font("Arial", Font.PLAIN, 13));
            }
        };
        taskMoreLabel.addMouseListener(moreMouseAdapter);
        milestoneMoreLabel.addMouseListener(moreMouseAdapter);

        contentWrapper.add(row1Container, BorderLayout.NORTH);
        contentWrapper.add(formatTabbedPane, BorderLayout.CENTER);
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

    // Custom color chooser with alpha/transparency slider and live updates
    private Color showColorChooserWithAlpha(String title, Color initialColor, java.util.function.Consumer<Color> liveUpdate) {
        final Color[] result = {null};
        final Color startColor = initialColor != null ? initialColor : Color.WHITE;

        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new BorderLayout());

        JColorChooser colorChooser = new JColorChooser(new Color(startColor.getRed(), startColor.getGreen(), startColor.getBlue()));
        colorChooser.setPreviewPanel(new JPanel()); // Remove preview panel

        // Alpha slider panel
        JPanel alphaPanel = new JPanel(new BorderLayout(10, 5));
        alphaPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        JLabel alphaLabel = new JLabel("Transparency:");
        JSlider alphaSlider = new JSlider(0, 255, startColor.getAlpha());
        alphaSlider.setMajorTickSpacing(64);
        alphaSlider.setMinorTickSpacing(16);
        alphaSlider.setPaintTicks(true);
        alphaSlider.setPaintLabels(true);

        // Custom labels for the slider (0% = opaque, 100% = transparent)
        java.util.Hashtable<Integer, JLabel> labels = new java.util.Hashtable<>();
        labels.put(255, new JLabel("0%"));
        labels.put(191, new JLabel("25%"));
        labels.put(128, new JLabel("50%"));
        labels.put(64, new JLabel("75%"));
        labels.put(0, new JLabel("100%"));
        alphaSlider.setLabelTable(labels);

        JLabel alphaValue = new JLabel(String.format("%d%%", (int)((255 - startColor.getAlpha()) * 100.0 / 255)));
        alphaValue.setPreferredSize(new Dimension(45, 20));

        alphaPanel.add(alphaLabel, BorderLayout.WEST);
        alphaPanel.add(alphaSlider, BorderLayout.CENTER);
        alphaPanel.add(alphaValue, BorderLayout.EAST);

        // Live update handler
        Runnable doLiveUpdate = () -> {
            Color c = colorChooser.getColor();
            Color withAlpha = new Color(c.getRed(), c.getGreen(), c.getBlue(), alphaSlider.getValue());
            if (liveUpdate != null) {
                liveUpdate.accept(withAlpha);
            }
        };

        // Add listeners for live updates
        colorChooser.getSelectionModel().addChangeListener(e -> doLiveUpdate.run());
        alphaSlider.addChangeListener(e -> {
            alphaValue.setText(String.format("%d%%", (int)((255 - alphaSlider.getValue()) * 100.0 / 255)));
            doLiveUpdate.run();
        });

        // Layout
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(alphaPanel, BorderLayout.NORTH);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(e -> {
            Color c = colorChooser.getColor();
            result[0] = new Color(c.getRed(), c.getGreen(), c.getBlue(), alphaSlider.getValue());
            dialog.dispose();
        });
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> {
            // Restore original color
            if (liveUpdate != null) {
                liveUpdate.accept(startColor);
            }
            dialog.dispose();
        });
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(colorChooser, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return result[0];
    }

    private void chooseFillColor() {
        if (selectedTaskIndices.isEmpty()) return;
        Color currentColor = Color.BLUE;
        if (selectedTaskIndices.size() == 1) {
            int idx = selectedTaskIndices.iterator().next();
            TimelineTask t = tasks.get(idx);
            currentColor = t.fillColor != null ? t.fillColor : TASK_COLORS[idx % TASK_COLORS.length];
        }
        final Color originalColor = currentColor;
        saveState();
        Color newColor = showColorChooserWithAlpha("Choose Fill Color", currentColor, color -> {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).fillColor = color;
            }
            fillColorBtn.setBackground(color);
            timelineDisplayPanel.repaint();
        });
        if (newColor != null) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).fillColor = newColor;
            }
            fillColorBtn.setBackground(newColor);
            refreshTimeline();
        } else {
            undo(); // Cancel - restore original
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
        saveState();
        Color newColor = showColorChooserWithAlpha("Choose Outline Color", currentColor, color -> {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).outlineColor = color;
            }
            outlineColorBtn.setBackground(color);
            timelineDisplayPanel.repaint();
        });
        if (newColor != null) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).outlineColor = newColor;
            }
            outlineColorBtn.setBackground(newColor);
            refreshTimeline();
        } else {
            undo(); // Cancel - restore original
        }
    }

    private void updateOutlineThickness() {
        if (selectedTaskIndices.isEmpty()) return;
        saveState();
        int value = (Integer) outlineThicknessSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).outlineThickness = value;
        }
        refreshTimeline();
    }

    private void updateTaskHeight() {
        if (selectedTaskIndices.isEmpty()) return;
        saveState();
        int value = (Integer) taskHeightSpinner.getValue();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).height = value;
        }
        refreshTimeline();
    }

    private void updateBevelFill() {
        if (selectedTaskIndices.isEmpty()) return;
        saveState();
        boolean value = bevelFillCheckbox.isSelected();
        for (int idx : selectedTaskIndices) {
            tasks.get(idx).bevelFill = value;
        }
        refreshTimeline();
    }

    private void showBevelSettingsDialog() {
        if (selectedTaskIndices.isEmpty() && selectedMilestoneIndex < 0) return;

        // Save state for undo if user clicks OK
        saveState();

        // Store original values for cancel
        final java.util.Map<Integer, int[]> originalTaskValues = new java.util.HashMap<>();
        final java.util.Map<Integer, String[]> originalTaskStyles = new java.util.HashMap<>();
        for (int idx : selectedTaskIndices) {
            TimelineTask t = tasks.get(idx);
            originalTaskValues.put(idx, new int[]{t.bevelDepth, t.bevelLightAngle, t.bevelHighlightOpacity, t.bevelShadowOpacity});
            originalTaskStyles.put(idx, new String[]{t.bevelStyle, t.topBevel, t.bottomBevel});
        }
        final int[] originalMilestoneValues = selectedMilestoneIndex >= 0 ?
            new int[]{milestones.get(selectedMilestoneIndex).bevelDepth,
                      milestones.get(selectedMilestoneIndex).bevelLightAngle,
                      milestones.get(selectedMilestoneIndex).bevelHighlightOpacity,
                      milestones.get(selectedMilestoneIndex).bevelShadowOpacity} : null;
        final String[] originalMilestoneStyles = selectedMilestoneIndex >= 0 ?
            new String[]{milestones.get(selectedMilestoneIndex).bevelStyle,
                        milestones.get(selectedMilestoneIndex).topBevel,
                        milestones.get(selectedMilestoneIndex).bottomBevel} : null;

        JDialog dialog = new JDialog(this, "Bevel Settings", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(900, 380);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Get current values from first selected item
        int currentDepth = 60, currentAngle = 135, currentHighlight = 80, currentShadow = 60;
        String currentStyle = "Inner Bevel", currentTopBevel = "Circle", currentBottomBevel = "None";
        if (!selectedTaskIndices.isEmpty()) {
            TimelineTask task = tasks.get(selectedTaskIndices.iterator().next());
            currentDepth = task.bevelDepth;
            currentAngle = task.bevelLightAngle;
            currentHighlight = task.bevelHighlightOpacity;
            currentShadow = task.bevelShadowOpacity;
            currentStyle = task.bevelStyle;
            currentTopBevel = task.topBevel;
            currentBottomBevel = task.bottomBevel;
        } else if (selectedMilestoneIndex >= 0) {
            TimelineMilestone ms = milestones.get(selectedMilestoneIndex);
            currentDepth = ms.bevelDepth;
            currentAngle = ms.bevelLightAngle;
            currentHighlight = ms.bevelHighlightOpacity;
            currentShadow = ms.bevelShadowOpacity;
            currentStyle = ms.bevelStyle;
            currentTopBevel = ms.topBevel;
            currentBottomBevel = ms.bottomBevel;
        }

        // Create sliders first so they can be referenced in the lambda
        JSlider depthSlider = new JSlider(0, 100, currentDepth);
        JSlider angleSlider = new JSlider(0, 360, currentAngle);
        JSlider highlightSlider = new JSlider(0, 255, currentHighlight);
        JSlider shadowSlider = new JSlider(0, 255, currentShadow);

        // PowerPoint-style bevel shape options
        String[] bevelShapes = {"None", "Circle", "Relaxed Inset", "Cross", "Angle", "Soft Round",
                                "Convex", "Cool Slant", "Divot", "Riblet", "Hard Edge", "Art Deco"};

        // Bevel Style dropdown
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Style:"), gbc);
        String[] bevelStyles = {"Inner Bevel", "Outer Bevel", "Emboss", "Pillow Emboss"};
        JComboBox<String> styleCombo = new JComboBox<>(bevelStyles);
        styleCombo.setSelectedItem(currentStyle);
        styleCombo.setPreferredSize(new Dimension(150, 25));
        gbc.gridx = 1; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(styleCombo, gbc);

        // Top Bevel dropdown
        gbc.gridx = 2;
        mainPanel.add(new JLabel("  Top Bevel:"), gbc);
        JComboBox<String> topBevelCombo = new JComboBox<>(bevelShapes);
        topBevelCombo.setSelectedItem(currentTopBevel);
        topBevelCombo.setPreferredSize(new Dimension(130, 25));
        gbc.gridx = 3;
        mainPanel.add(topBevelCombo, gbc);

        // Bottom Bevel dropdown
        gbc.gridx = 4;
        mainPanel.add(new JLabel("  Bottom Bevel:"), gbc);
        JComboBox<String> bottomBevelCombo = new JComboBox<>(bevelShapes);
        bottomBevelCombo.setSelectedItem(currentBottomBevel);
        bottomBevelCombo.setPreferredSize(new Dimension(130, 25));
        gbc.gridx = 5;
        mainPanel.add(bottomBevelCombo, gbc);

        // Live update lambda
        Runnable updateLive = () -> {
            int depth = depthSlider.getValue();
            int angle = angleSlider.getValue();
            int highlight = highlightSlider.getValue();
            int shadow = shadowSlider.getValue();
            String style = (String) styleCombo.getSelectedItem();
            String topBevel = (String) topBevelCombo.getSelectedItem();
            String bottomBevel = (String) bottomBevelCombo.getSelectedItem();
            for (int idx : selectedTaskIndices) {
                TimelineTask task = tasks.get(idx);
                task.bevelDepth = depth;
                task.bevelLightAngle = angle;
                task.bevelHighlightOpacity = highlight;
                task.bevelShadowOpacity = shadow;
                task.bevelStyle = style;
                task.topBevel = topBevel;
                task.bottomBevel = bottomBevel;
            }
            if (selectedMilestoneIndex >= 0) {
                TimelineMilestone ms = milestones.get(selectedMilestoneIndex);
                ms.bevelDepth = depth;
                ms.bevelLightAngle = angle;
                ms.bevelHighlightOpacity = highlight;
                ms.bevelShadowOpacity = shadow;
                ms.bevelStyle = style;
                ms.topBevel = topBevel;
                ms.bottomBevel = bottomBevel;
            }
            timelineDisplayPanel.repaint();
        };

        styleCombo.addActionListener(e -> updateLive.run());
        topBevelCombo.addActionListener(e -> updateLive.run());
        bottomBevelCombo.addActionListener(e -> updateLive.run());

        // Depth slider (0-100)
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Depth:"), gbc);
        depthSlider.setPreferredSize(new Dimension(600, 45));
        depthSlider.setMajorTickSpacing(25);
        depthSlider.setPaintTicks(true);
        depthSlider.setPaintLabels(true);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(depthSlider, gbc);
        JLabel depthValue = new JLabel(String.valueOf(currentDepth));
        depthValue.setPreferredSize(new Dimension(40, 20));
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(depthValue, gbc);
        depthSlider.addChangeListener(e -> {
            depthValue.setText(String.valueOf(depthSlider.getValue()));
            updateLive.run();
        });

        // Light Angle (0-360)
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Light Angle:"), gbc);
        angleSlider.setPreferredSize(new Dimension(600, 45));
        angleSlider.setMajorTickSpacing(90);
        angleSlider.setPaintTicks(true);
        angleSlider.setPaintLabels(true);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(angleSlider, gbc);
        JLabel angleValue = new JLabel(currentAngle + "°");
        angleValue.setPreferredSize(new Dimension(40, 20));
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(angleValue, gbc);
        angleSlider.addChangeListener(e -> {
            angleValue.setText(angleSlider.getValue() + "°");
            updateLive.run();
        });

        // Highlight Opacity (0-255)
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Highlight:"), gbc);
        highlightSlider.setPreferredSize(new Dimension(600, 45));
        highlightSlider.setMajorTickSpacing(64);
        highlightSlider.setPaintTicks(true);
        highlightSlider.setPaintLabels(true);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(highlightSlider, gbc);
        JLabel highlightValue = new JLabel(String.valueOf(currentHighlight));
        highlightValue.setPreferredSize(new Dimension(40, 20));
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(highlightValue, gbc);
        highlightSlider.addChangeListener(e -> {
            highlightValue.setText(String.valueOf(highlightSlider.getValue()));
            updateLive.run();
        });

        // Shadow Opacity (0-255)
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Shadow:"), gbc);
        shadowSlider.setPreferredSize(new Dimension(600, 45));
        shadowSlider.setMajorTickSpacing(64);
        shadowSlider.setPaintTicks(true);
        shadowSlider.setPaintLabels(true);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(shadowSlider, gbc);
        JLabel shadowValue = new JLabel(String.valueOf(currentShadow));
        shadowValue.setPreferredSize(new Dimension(40, 20));
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(shadowValue, gbc);
        shadowSlider.addChangeListener(e -> {
            shadowValue.setText(String.valueOf(shadowSlider.getValue()));
            updateLive.run();
        });

        dialog.add(mainPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(e -> dialog.dispose());
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> {
            // Restore original values
            for (int idx : selectedTaskIndices) {
                TimelineTask task = tasks.get(idx);
                int[] vals = originalTaskValues.get(idx);
                String[] styles = originalTaskStyles.get(idx);
                task.bevelDepth = vals[0];
                task.bevelLightAngle = vals[1];
                task.bevelHighlightOpacity = vals[2];
                task.bevelShadowOpacity = vals[3];
                task.bevelStyle = styles[0];
                task.topBevel = styles[1];
                task.bottomBevel = styles[2];
            }
            if (selectedMilestoneIndex >= 0 && originalMilestoneValues != null && originalMilestoneStyles != null) {
                TimelineMilestone ms = milestones.get(selectedMilestoneIndex);
                ms.bevelDepth = originalMilestoneValues[0];
                ms.bevelLightAngle = originalMilestoneValues[1];
                ms.bevelHighlightOpacity = originalMilestoneValues[2];
                ms.bevelShadowOpacity = originalMilestoneValues[3];
                ms.bevelStyle = originalMilestoneStyles[0];
                ms.topBevel = originalMilestoneStyles[1];
                ms.bottomBevel = originalMilestoneStyles[2];
            }
            undo(); // Undo the saveState we did at the beginning
            timelineDisplayPanel.repaint();
            dialog.dispose();
        });
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void updateCenterText() {
        saveState();
        String text = centerTextField.getText();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).centerText = text;
            }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).centerText = text;
        }
        refreshTimeline();
        updateSpreadsheet();
    }

    private void updateFontFamily() {
        saveState();
        String value = (String) fontFamilyCombo.getSelectedItem();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).fontFamily = value;
            }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).fontFamily = value;
        }
        refreshTimeline();
    }

    private void updateFontSize() {
        saveState();
        int value = (Integer) fontSizeSpinner.getValue();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).fontSize = value;
            }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).fontSize = value;
        }
        refreshTimeline();
    }

    private void updateFontBold() {
        saveState();
        boolean value = boldBtn.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).fontBold = value;
            }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).fontBold = value;
        }
        refreshTimeline();
    }

    private void updateFontItalic() {
        saveState();
        boolean value = italicBtn.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).fontItalic = value;
            }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).fontItalic = value;
        }
        refreshTimeline();
    }

    private void chooseTextColor() {
        Color currentColor = Color.WHITE;
        if (!selectedTaskIndices.isEmpty()) {
            if (selectedTaskIndices.size() == 1) {
                TimelineTask t = tasks.get(selectedTaskIndices.iterator().next());
                currentColor = t.textColor != null ? t.textColor : Color.WHITE;
            }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            currentColor = milestones.get(selectedMilestoneIndex).textColor;
        } else {
            return;
        }
        saveState();
        Color newColor = showColorChooserWithAlpha("Choose Text Color", currentColor, color -> {
            if (!selectedTaskIndices.isEmpty()) {
                for (int idx : selectedTaskIndices) {
                    tasks.get(idx).textColor = color;
                }
            } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                milestones.get(selectedMilestoneIndex).textColor = color;
            }
            textColorBtn.setBackground(color);
            timelineDisplayPanel.repaint();
        });
        if (newColor != null) {
            if (!selectedTaskIndices.isEmpty()) {
                for (int idx : selectedTaskIndices) {
                    tasks.get(idx).textColor = newColor;
                }
            } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                milestones.get(selectedMilestoneIndex).textColor = newColor;
            }
            textColorBtn.setBackground(newColor);
            refreshTimeline();
        } else {
            undo();
        }
    }

    private void updateCenterXOffset() {
        saveState();
        int value = (Integer) centerXOffsetSpinner.getValue();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).centerTextXOffset = value;
            }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).centerTextXOffset = value;
        }
        refreshTimeline();
    }

    private void updateCenterYOffset() {
        saveState();
        int value = (Integer) centerYOffsetSpinner.getValue();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).centerTextYOffset = value;
            }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).centerTextYOffset = value;
        }
        refreshTimeline();
    }

    private void updateCenterTextWrap() {
        saveState();
        boolean wrap = centerWrapCheckbox.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).centerTextWrap = wrap;
            }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).centerTextWrap = wrap;
        }
        refreshTimeline();
    }

    private void updateCenterTextVisible() {
        saveState();
        boolean visible = centerVisibleCheckbox.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) {
                tasks.get(idx).centerTextVisible = visible;
            }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).centerTextVisible = visible;
        }
        refreshTimeline();
    }

    // Front text update methods
    private void updateFrontText() {
        saveState();
        String text = frontTextField.getText();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).frontText = text; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).frontText = text;
        }
        refreshTimeline();
    }

    private void updateFrontFontFamily() {
        saveState();
        String value = (String) frontFontCombo.getSelectedItem();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).frontFontFamily = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).frontFontFamily = value;
        }
        refreshTimeline();
    }

    private void updateFrontFontSize() {
        saveState();
        int value = (Integer) frontFontSizeSpinner.getValue();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).frontFontSize = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).frontFontSize = value;
        }
        refreshTimeline();
    }

    private void updateFrontFontBold() {
        saveState();
        boolean value = frontBoldBtn.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).frontFontBold = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).frontFontBold = value;
        }
        refreshTimeline();
    }

    private void updateFrontFontItalic() {
        saveState();
        boolean value = frontItalicBtn.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).frontFontItalic = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).frontFontItalic = value;
        }
        refreshTimeline();
    }

    private void chooseFrontTextColor() {
        Color currentColor = Color.BLACK;
        if (!selectedTaskIndices.isEmpty()) {
            if (selectedTaskIndices.size() == 1) {
                currentColor = tasks.get(selectedTaskIndices.iterator().next()).frontTextColor;
            }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            currentColor = milestones.get(selectedMilestoneIndex).frontTextColor;
        } else { return; }
        saveState();
        Color newColor = showColorChooserWithAlpha("Choose Front Text Color", currentColor, color -> {
            if (!selectedTaskIndices.isEmpty()) {
                for (int idx : selectedTaskIndices) { tasks.get(idx).frontTextColor = color; }
            } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                milestones.get(selectedMilestoneIndex).frontTextColor = color;
            }
            frontTextColorBtn.setBackground(color);
            timelineDisplayPanel.repaint();
        });
        if (newColor != null) {
            if (!selectedTaskIndices.isEmpty()) {
                for (int idx : selectedTaskIndices) { tasks.get(idx).frontTextColor = newColor; }
            } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                milestones.get(selectedMilestoneIndex).frontTextColor = newColor;
            }
            frontTextColorBtn.setBackground(newColor);
            refreshTimeline();
        } else { undo(); }
    }

    private void updateFrontXOffset() {
        saveState();
        int value = (Integer) frontXOffsetSpinner.getValue();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).frontTextXOffset = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).frontTextXOffset = value;
        }
        refreshTimeline();
    }

    private void updateFrontYOffset() {
        saveState();
        int value = (Integer) frontYOffsetSpinner.getValue();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).frontTextYOffset = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).frontTextYOffset = value;
        }
        refreshTimeline();
    }

    private void updateFrontTextWrap() {
        saveState();
        boolean wrap = frontWrapCheckbox.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).frontTextWrap = wrap; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).frontTextWrap = wrap;
        }
        refreshTimeline();
    }

    private void updateFrontTextVisible() {
        saveState();
        boolean visible = frontVisibleCheckbox.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).frontTextVisible = visible; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).frontTextVisible = visible;
        }
        refreshTimeline();
    }

    // Above text update methods
    private void updateAboveText() {
        saveState();
        String text = aboveTextField.getText();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).aboveText = text; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).aboveText = text;
        }
        refreshTimeline();
    }

    private void updateAboveFontFamily() {
        saveState();
        String value = (String) aboveFontCombo.getSelectedItem();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).aboveFontFamily = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).aboveFontFamily = value;
        }
        refreshTimeline();
    }

    private void updateAboveFontSize() {
        saveState();
        int value = (Integer) aboveFontSizeSpinner.getValue();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).aboveFontSize = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).aboveFontSize = value;
        }
        refreshTimeline();
    }

    private void updateAboveFontBold() {
        saveState();
        boolean value = aboveBoldBtn.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).aboveFontBold = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).aboveFontBold = value;
        }
        refreshTimeline();
    }

    private void updateAboveFontItalic() {
        saveState();
        boolean value = aboveItalicBtn.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).aboveFontItalic = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).aboveFontItalic = value;
        }
        refreshTimeline();
    }

    private void chooseAboveTextColor() {
        Color currentColor = Color.BLACK;
        if (!selectedTaskIndices.isEmpty()) {
            if (selectedTaskIndices.size() == 1) {
                TimelineTask t = tasks.get(selectedTaskIndices.iterator().next());
                currentColor = t.aboveTextColor != null ? t.aboveTextColor : Color.BLACK;
            }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            TimelineMilestone m = milestones.get(selectedMilestoneIndex);
            currentColor = m.aboveTextColor != null ? m.aboveTextColor : Color.BLACK;
        }
        saveState();
        Color newColor = showColorChooserWithAlpha("Choose Above Text Color", currentColor, color -> {
            if (!selectedTaskIndices.isEmpty()) {
                for (int idx : selectedTaskIndices) { tasks.get(idx).aboveTextColor = color; }
            } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                milestones.get(selectedMilestoneIndex).aboveTextColor = color;
            }
            aboveTextColorBtn.setBackground(color);
            timelineDisplayPanel.repaint();
        });
        if (newColor != null) {
            if (!selectedTaskIndices.isEmpty()) {
                for (int idx : selectedTaskIndices) { tasks.get(idx).aboveTextColor = newColor; }
            } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                milestones.get(selectedMilestoneIndex).aboveTextColor = newColor;
            }
            aboveTextColorBtn.setBackground(newColor);
            refreshTimeline();
        } else {
            undo();
        }
    }

    private void updateAboveXOffset() {
        saveState();
        int value = (Integer) aboveXOffsetSpinner.getValue();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).aboveTextXOffset = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).aboveTextXOffset = value;
        }
        refreshTimeline();
    }

    private void updateAboveYOffset() {
        saveState();
        int value = (Integer) aboveYOffsetSpinner.getValue();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).aboveTextYOffset = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).aboveTextYOffset = value;
        }
        refreshTimeline();
    }

    private void updateAboveTextWrap() {
        saveState();
        boolean wrap = aboveWrapCheckbox.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).aboveTextWrap = wrap; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).aboveTextWrap = wrap;
        }
        refreshTimeline();
    }

    private void updateAboveTextVisible() {
        saveState();
        boolean visible = aboveVisibleCheckbox.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).aboveTextVisible = visible; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).aboveTextVisible = visible;
        }
        refreshTimeline();
    }

    // Underneath text update methods
    private void updateUnderneathText() {
        saveState();
        String text = underneathTextField.getText();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).underneathText = text; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).underneathText = text;
        }
        refreshTimeline();
    }

    private void updateUnderneathFontFamily() {
        saveState();
        String value = (String) underneathFontCombo.getSelectedItem();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).underneathFontFamily = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).underneathFontFamily = value;
        }
        refreshTimeline();
    }

    private void updateUnderneathFontSize() {
        saveState();
        int value = (Integer) underneathFontSizeSpinner.getValue();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).underneathFontSize = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).underneathFontSize = value;
        }
        refreshTimeline();
    }

    private void updateUnderneathFontBold() {
        saveState();
        boolean value = underneathBoldBtn.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).underneathFontBold = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).underneathFontBold = value;
        }
        refreshTimeline();
    }

    private void updateUnderneathFontItalic() {
        saveState();
        boolean value = underneathItalicBtn.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).underneathFontItalic = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).underneathFontItalic = value;
        }
        refreshTimeline();
    }

    private void chooseUnderneathTextColor() {
        Color currentColor = Color.BLACK;
        if (!selectedTaskIndices.isEmpty()) {
            if (selectedTaskIndices.size() == 1) {
                TimelineTask t = tasks.get(selectedTaskIndices.iterator().next());
                currentColor = t.underneathTextColor != null ? t.underneathTextColor : Color.BLACK;
            }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            TimelineMilestone m = milestones.get(selectedMilestoneIndex);
            currentColor = m.underneathTextColor != null ? m.underneathTextColor : Color.BLACK;
        }
        saveState();
        Color newColor = showColorChooserWithAlpha("Choose Underneath Text Color", currentColor, color -> {
            if (!selectedTaskIndices.isEmpty()) {
                for (int idx : selectedTaskIndices) { tasks.get(idx).underneathTextColor = color; }
            } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                milestones.get(selectedMilestoneIndex).underneathTextColor = color;
            }
            underneathTextColorBtn.setBackground(color);
            timelineDisplayPanel.repaint();
        });
        if (newColor != null) {
            if (!selectedTaskIndices.isEmpty()) {
                for (int idx : selectedTaskIndices) { tasks.get(idx).underneathTextColor = newColor; }
            } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                milestones.get(selectedMilestoneIndex).underneathTextColor = newColor;
            }
            underneathTextColorBtn.setBackground(newColor);
            refreshTimeline();
        } else {
            undo();
        }
    }

    private void updateUnderneathXOffset() {
        saveState();
        int value = (Integer) underneathXOffsetSpinner.getValue();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).underneathTextXOffset = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).underneathTextXOffset = value;
        }
        refreshTimeline();
    }

    private void updateUnderneathYOffset() {
        saveState();
        int value = (Integer) underneathYOffsetSpinner.getValue();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).underneathTextYOffset = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).underneathTextYOffset = value;
        }
        refreshTimeline();
    }

    private void updateUnderneathTextWrap() {
        saveState();
        boolean wrap = underneathWrapCheckbox.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).underneathTextWrap = wrap; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).underneathTextWrap = wrap;
        }
        refreshTimeline();
    }

    private void updateUnderneathTextVisible() {
        saveState();
        boolean visible = underneathVisibleCheckbox.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).underneathTextVisible = visible; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).underneathTextVisible = visible;
        }
        refreshTimeline();
    }

    // Behind text update methods
    private void updateBehindText() {
        saveState();
        String text = behindTextField.getText();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).behindText = text; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).behindText = text;
        }
        refreshTimeline();
    }

    private void updateBehindFontFamily() {
        saveState();
        String value = (String) behindFontCombo.getSelectedItem();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).behindFontFamily = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).behindFontFamily = value;
        }
        refreshTimeline();
    }

    private void updateBehindFontSize() {
        saveState();
        int value = (Integer) behindFontSizeSpinner.getValue();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).behindFontSize = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).behindFontSize = value;
        }
        refreshTimeline();
    }

    private void updateBehindFontBold() {
        saveState();
        boolean value = behindBoldBtn.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).behindFontBold = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).behindFontBold = value;
        }
        refreshTimeline();
    }

    private void updateBehindFontItalic() {
        saveState();
        boolean value = behindItalicBtn.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).behindFontItalic = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).behindFontItalic = value;
        }
        refreshTimeline();
    }

    private void chooseBehindTextColor() {
        Color currentColor = new Color(150, 150, 150);
        if (!selectedTaskIndices.isEmpty()) {
            if (selectedTaskIndices.size() == 1) {
                TimelineTask t = tasks.get(selectedTaskIndices.iterator().next());
                currentColor = t.behindTextColor != null ? t.behindTextColor : new Color(150, 150, 150);
            }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            TimelineMilestone m = milestones.get(selectedMilestoneIndex);
            currentColor = m.behindTextColor != null ? m.behindTextColor : new Color(150, 150, 150);
        }
        saveState();
        Color newColor = showColorChooserWithAlpha("Choose Behind Text Color", currentColor, color -> {
            if (!selectedTaskIndices.isEmpty()) {
                for (int idx : selectedTaskIndices) { tasks.get(idx).behindTextColor = color; }
            } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                milestones.get(selectedMilestoneIndex).behindTextColor = color;
            }
            behindTextColorBtn.setBackground(color);
            timelineDisplayPanel.repaint();
        });
        if (newColor != null) {
            if (!selectedTaskIndices.isEmpty()) {
                for (int idx : selectedTaskIndices) { tasks.get(idx).behindTextColor = newColor; }
            } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                milestones.get(selectedMilestoneIndex).behindTextColor = newColor;
            }
            behindTextColorBtn.setBackground(newColor);
            refreshTimeline();
        } else {
            undo();
        }
    }

    private void updateBehindXOffset() {
        saveState();
        int value = (Integer) behindXOffsetSpinner.getValue();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).behindTextXOffset = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).behindTextXOffset = value;
        }
        refreshTimeline();
    }

    private void updateBehindYOffset() {
        saveState();
        int value = (Integer) behindYOffsetSpinner.getValue();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).behindTextYOffset = value; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).behindTextYOffset = value;
        }
        refreshTimeline();
    }

    private void updateBehindTextWrap() {
        saveState();
        boolean wrap = behindWrapCheckbox.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).behindTextWrap = wrap; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).behindTextWrap = wrap;
        }
        refreshTimeline();
    }

    private void updateBehindTextVisible() {
        saveState();
        boolean visible = behindVisibleCheckbox.isSelected();
        if (!selectedTaskIndices.isEmpty()) {
            for (int idx : selectedTaskIndices) { tasks.get(idx).behindTextVisible = visible; }
        } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
            milestones.get(selectedMilestoneIndex).behindTextVisible = visible;
        }
        refreshTimeline();
    }

    private void updateNotes() {
        if (selectedTaskIndices.isEmpty()) return;
        saveState();
        for (int idx : selectedTaskIndices) {
            TimelineTask task = tasks.get(idx);
            task.note1 = note1Area.getText();
            task.note2 = note2Area.getText();
            task.note3 = note3Area.getText();
            task.note4 = note4Area.getText();
            task.note5 = note5Area.getText();
        }
    }

    // Milestone update methods
    private void updateSelectedMilestoneName() {
        if (selectedMilestoneIndex < 0 || selectedMilestoneIndex >= milestones.size()) return;
        saveState();
        String newName = milestoneNameField.getText().trim();
        if (!newName.isEmpty()) {
            milestones.get(selectedMilestoneIndex).name = newName;
            milestones.get(selectedMilestoneIndex).labelText = newName;
            refreshTimeline();
        }
    }

    private void updateSelectedMilestoneDate() {
        if (selectedMilestoneIndex < 0 || selectedMilestoneIndex >= milestones.size()) return;
        saveState();
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
        saveState();
        Color newColor = showColorChooserWithAlpha("Choose Fill Color", milestone.fillColor, color -> {
            milestone.fillColor = color;
            milestoneFillColorBtn.setBackground(color);
            timelineDisplayPanel.repaint();
        });
        if (newColor != null) {
            milestone.fillColor = newColor;
            milestoneFillColorBtn.setBackground(newColor);
            refreshTimeline();
        } else {
            undo();
        }
    }

    private void chooseMilestoneOutlineColor() {
        if (selectedMilestoneIndex < 0 || selectedMilestoneIndex >= milestones.size()) return;
        TimelineMilestone milestone = milestones.get(selectedMilestoneIndex);
        saveState();
        Color newColor = showColorChooserWithAlpha("Choose Outline Color", milestone.outlineColor, color -> {
            milestone.outlineColor = color;
            milestoneOutlineColorBtn.setBackground(color);
            timelineDisplayPanel.repaint();
        });
        if (newColor != null) {
            milestone.outlineColor = newColor;
            milestoneOutlineColorBtn.setBackground(newColor);
            refreshTimeline();
        } else {
            undo();
        }
    }

    private void chooseTimelineBackgroundColor() {
        saveState();
        Color newColor = showColorChooserWithAlpha("Choose Background Color", timelineInteriorColor, color -> {
            timelineInteriorColor = color;
            timelineUseGradient = false; // Disable gradient when selecting solid color
            timelineBgColorBtn.setBackground(color);
            timelineDisplayPanel.repaint();
        });
        if (newColor != null) {
            timelineInteriorColor = newColor;
            timelineUseGradient = false; // Disable gradient when selecting solid color
            timelineBgColorBtn.setBackground(newColor);
            refreshTimeline();
        } else {
            undo();
        }
    }

    private void showGradientDialog() {
        showGradientDialog("background");
    }

    private void showGradientDialog(String type) {
        // Get current values based on type
        ArrayList<float[]> currentStops;
        Color currentColor1, currentColor2;
        String currentDir;
        double currentAngle;
        boolean currentUseGradient;

        if ("settings".equals(type)) {
            currentStops = settingsGradientStops;
            currentColor1 = settingsInteriorColor;
            currentColor2 = settingsInteriorColor2;
            currentDir = settingsGradientDir;
            currentAngle = settingsGradientAngle;
            currentUseGradient = settingsUseGradient;
        } else if ("settingsHeader".equals(type)) {
            currentStops = settingsHeaderGradientStops;
            currentColor1 = settingsHeaderColor;
            currentColor2 = settingsHeaderColor2;
            currentDir = settingsHeaderGradientDir;
            currentAngle = settingsHeaderGradientAngle;
            currentUseGradient = settingsHeaderUseGradient;
        } else if ("general".equals(type)) {
            currentStops = generalGradientStops;
            currentColor1 = generalInteriorColor;
            currentColor2 = generalInteriorColor2;
            currentDir = "Vertical";
            currentAngle = generalGradientAngle;
            currentUseGradient = generalUseGradient;
        } else if ("format".equals(type)) {
            currentStops = formatGradientStops;
            currentColor1 = formatInteriorColor;
            currentColor2 = formatInteriorColor2;
            currentDir = "Vertical";
            currentAngle = formatGradientAngle;
            currentUseGradient = formatUseGradient;
        } else if ("formatHeader".equals(type)) {
            currentStops = formatHeaderGradientStops;
            currentColor1 = formatHeaderColor;
            currentColor2 = formatHeaderColor2;
            currentDir = "Horizontal";
            currentAngle = formatHeaderGradientAngle;
            currentUseGradient = formatHeaderUseGradient;
        } else if ("formatTab".equals(type)) {
            currentStops = formatTabGradientStops;
            currentColor1 = formatTabColor;
            currentColor2 = formatTabColor2;
            currentDir = "Horizontal";
            currentAngle = formatTabGradientAngle;
            currentUseGradient = formatTabUseGradient;
        } else if ("formatSelectedTab".equals(type)) {
            currentStops = formatSelectedTabGradientStops;
            currentColor1 = formatSelectedTabColor;
            currentColor2 = formatSelectedTabColor2;
            currentDir = "Horizontal";
            currentAngle = formatSelectedTabGradientAngle;
            currentUseGradient = formatSelectedTabUseGradient;
        } else if ("formatTabContent".equals(type)) {
            currentStops = formatTabContentGradientStops;
            currentColor1 = formatTabContentColor;
            currentColor2 = formatTabContentColor2;
            currentDir = "Vertical";
            currentAngle = formatTabContentGradientAngle;
            currentUseGradient = formatTabContentUseGradient;
        } else if ("layers".equals(type)) {
            currentStops = layersGradientStops;
            currentColor1 = layersInteriorColor;
            currentColor2 = layersInteriorColor2;
            currentDir = "Vertical";
            currentAngle = layersGradientAngle;
            currentUseGradient = layersUseGradient;
        } else if ("layersHeader".equals(type)) {
            currentStops = layersHeaderGradientStops;
            currentColor1 = layersHeaderColor;
            currentColor2 = layersHeaderColor2;
            currentDir = "Horizontal";
            currentAngle = layersHeaderGradientAngle;
            currentUseGradient = layersHeaderUseGradient;
        } else if ("layersListBg".equals(type)) {
            currentStops = layersListBgGradientStops;
            currentColor1 = layersListBgColor;
            currentColor2 = layersListBgColor2;
            currentDir = "Vertical";
            currentAngle = layersListBgGradientAngle;
            currentUseGradient = layersListBgUseGradient;
            currentStops = layersTaskGradientStops;
            currentColor1 = layersTaskColor;
            currentColor2 = layersTaskColor2;
            currentDir = "Horizontal";
            currentAngle = layersTaskGradientAngle;
            currentUseGradient = layersTaskUseGradient;
        } else if ("toolbar".equals(type)) {
            currentStops = toolbarGradientStops;
            currentColor1 = toolbarBgColor;
            currentColor2 = toolbarBgColor2;
            currentDir = "Horizontal";
            currentAngle = toolbarGradientAngle;
            currentUseGradient = toolbarUseGradient;
        } else {
            currentStops = timelineGradientStops;
            currentColor1 = timelineInteriorColor;
            currentColor2 = timelineInteriorColor2;
            currentDir = timelineGradientDir;
            currentAngle = timelineGradientAngle;
            currentUseGradient = timelineUseGradient;
        }

        // Store original values for cancel
        final ArrayList<float[]> origStops = new ArrayList<>();
        for (float[] stop : currentStops) {
            origStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
        }
        final Color origColor1 = currentColor1;
        final Color origColor2 = currentColor2;
        final String origDir = currentDir;
        final double origAngle = currentAngle;
        final boolean origUseGradient = currentUseGradient;
        final String finalType = type;
        final double[] angleRef = {currentAngle}; // Mutable reference for angle

        JDialog dialog = new JDialog(this, "Gradient Settings", true);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Gradient stops list - load from saved stops or create default
        ArrayList<float[]> gradientStops = new ArrayList<>();
        if (currentStops.size() >= 2) {
            for (float[] stop : currentStops) {
                gradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
            }
        } else {
            gradientStops.add(new float[]{0f, currentColor1.getRed()/255f, currentColor1.getGreen()/255f,
                                           currentColor1.getBlue()/255f, currentColor1.getAlpha()/255f});
            gradientStops.add(new float[]{1f, currentColor2.getRed()/255f, currentColor2.getGreen()/255f,
                                           currentColor2.getBlue()/255f, currentColor2.getAlpha()/255f});
        }

        // "Gradient stops" label
        JLabel stopsLabel = new JLabel("Gradient stops");
        stopsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        stopsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(stopsLabel);
        mainPanel.add(Box.createVerticalStrut(3));

        // Track selected stop for color button
        final int[] selectedStopRef = {0};

        // Gradient slider panel - PowerPoint style
        JPanel sliderPanel = new JPanel() {
            int selectedStop = 0;
            int dragStop = -1;

            {
                setPreferredSize(new Dimension(280, 45));
                setBackground(Color.WHITE);

                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent e) {
                        int x = e.getX();
                        int y = e.getY();
                        int barX = 12;
                        int barW = getWidth() - 24;

                        // Check if clicking on stop marker (house shape at bottom)
                        for (int i = 0; i < gradientStops.size(); i++) {
                            float[] stop = gradientStops.get(i);
                            int stopX = (int)(stop[0] * barW) + barX;
                            if (Math.abs(x - stopX) < 8 && y >= 22) {
                                selectedStop = i;
                                selectedStopRef[0] = i;
                                dragStop = i;
                                repaint();
                                getParent().repaint();
                                return;
                            }
                        }

                        // Add new stop if clicking on gradient bar
                        if (y >= 5 && y <= 22) {
                            float pos = (x - barX) / (float)barW;
                            pos = Math.max(0.01f, Math.min(0.99f, pos));

                            Color interpColor = interpolateGradientColor(gradientStops, pos);
                            gradientStops.add(new float[]{pos, interpColor.getRed()/255f, interpColor.getGreen()/255f,
                                                           interpColor.getBlue()/255f, interpColor.getAlpha()/255f});
                            gradientStops.sort((a, b) -> Float.compare(a[0], b[0]));
                            for (int i = 0; i < gradientStops.size(); i++) {
                                if (Math.abs(gradientStops.get(i)[0] - pos) < 0.01f) {
                                    selectedStop = i;
                                    selectedStopRef[0] = i;
                                    break;
                                }
                            }
                            updateGradientFromStops(gradientStops, finalType);
                            repaint();
                            getParent().repaint();
                        }
                    }

                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent e) {
                        dragStop = -1;
                    }
                });

                addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                    @Override
                    public void mouseDragged(java.awt.event.MouseEvent e) {
                        if (dragStop >= 0 && dragStop < gradientStops.size()) {
                            int barX = 12;
                            int barW = getWidth() - 24;
                            float pos = (e.getX() - barX) / (float)barW;
                            // All stops can move within full range
                            pos = Math.max(0f, Math.min(1f, pos));

                            gradientStops.get(dragStop)[0] = pos;
                            // Sort and find new index after drag
                            gradientStops.sort((a, b) -> Float.compare(a[0], b[0]));
                            for (int i = 0; i < gradientStops.size(); i++) {
                                if (Math.abs(gradientStops.get(i)[0] - pos) < 0.001f) {
                                    dragStop = i;
                                    selectedStop = i;
                                    selectedStopRef[0] = i;
                                    break;
                                }
                            }
                            updateGradientFromStops(gradientStops, finalType);
                            repaint();
                        }
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int barX = 12;
                int barY = 5;
                int barW = getWidth() - 24;
                int barH = 18;

                // Draw gradient bar with border
                for (int x = 0; x < barW; x++) {
                    float pos = x / (float) barW;
                    Color c = interpolateGradientColor(gradientStops, pos);
                    g2d.setColor(c);
                    g2d.drawLine(x + barX, barY, x + barX, barY + barH);
                }
                g2d.setColor(new Color(160, 160, 160));
                g2d.drawRect(barX, barY, barW, barH);

                // Draw house-shaped stop markers (PowerPoint style)
                for (int i = 0; i < gradientStops.size(); i++) {
                    float[] stop = gradientStops.get(i);
                    int stopX = (int)(stop[0] * barW) + barX;
                    Color stopColor = new Color(stop[1], stop[2], stop[3], stop[4]);

                    // House shape: pentagon pointing up
                    int[] xPoints = {stopX - 6, stopX + 6, stopX + 6, stopX, stopX - 6};
                    int[] yPoints = {barY + barH + 18, barY + barH + 18, barY + barH + 6, barY + barH + 1, barY + barH + 6};

                    // Fill with stop color
                    g2d.setColor(stopColor);
                    g2d.fillPolygon(xPoints, yPoints, 5);

                    // Border - blue if selected, gray otherwise
                    if (i == selectedStop) {
                        g2d.setColor(new Color(0, 120, 215));
                        g2d.setStroke(new BasicStroke(2));
                    } else {
                        g2d.setColor(new Color(160, 160, 160));
                        g2d.setStroke(new BasicStroke(1));
                    }
                    g2d.drawPolygon(xPoints, yPoints, 5);
                }
            }
        };
        sliderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(sliderPanel);
        mainPanel.add(Box.createVerticalStrut(8));

        // Color row with button for selected stop
        JPanel colorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        colorRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        colorRow.setOpaque(false);

        JLabel colorLabel = new JLabel("Color");
        colorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        colorRow.add(colorLabel);

        JButton stopColorBtn = new JButton();
        stopColorBtn.setPreferredSize(new Dimension(40, 22));
        float[] firstStop = gradientStops.get(0);
        stopColorBtn.setBackground(new Color(firstStop[1], firstStop[2], firstStop[3], firstStop[4]));
        stopColorBtn.addActionListener(e -> {
            int idx = selectedStopRef[0];
            if (idx >= 0 && idx < gradientStops.size()) {
                float[] stop = gradientStops.get(idx);
                Color stopColor = new Color(stop[1], stop[2], stop[3], stop[4]);
                Color c = showColorChooserWithAlpha("Choose Stop Color", stopColor, color -> {
                    float[] s = gradientStops.get(selectedStopRef[0]);
                    s[1] = color.getRed()/255f;
                    s[2] = color.getGreen()/255f;
                    s[3] = color.getBlue()/255f;
                    s[4] = color.getAlpha()/255f;
                    stopColorBtn.setBackground(color);
                    updateGradientFromStops(gradientStops, finalType);
                    sliderPanel.repaint();
                });
                if (c != null) {
                    stop[1] = c.getRed()/255f;
                    stop[2] = c.getGreen()/255f;
                    stop[3] = c.getBlue()/255f;
                    stop[4] = c.getAlpha()/255f;
                    stopColorBtn.setBackground(c);
                    updateGradientFromStops(gradientStops, finalType);
                    sliderPanel.repaint();
                }
            }
        });
        colorRow.add(stopColorBtn);

        // Delete button - can delete any stop as long as at least 2 remain
        JButton deleteBtn = new JButton("X");
        deleteBtn.setPreferredSize(new Dimension(28, 22));
        deleteBtn.setFont(new Font("Arial", Font.BOLD, 10));
        deleteBtn.setMargin(new Insets(0, 0, 0, 0));
        deleteBtn.setToolTipText("Remove stop (minimum 2 required)");
        deleteBtn.addActionListener(e -> {
            int idx = selectedStopRef[0];
            if (gradientStops.size() > 2 && idx >= 0 && idx < gradientStops.size()) {
                gradientStops.remove(idx);
                selectedStopRef[0] = Math.min(idx, gradientStops.size() - 1);
                updateGradientFromStops(gradientStops, finalType);
                sliderPanel.repaint();
                // Update color button
                float[] newStop = gradientStops.get(selectedStopRef[0]);
                stopColorBtn.setBackground(new Color(newStop[1], newStop[2], newStop[3], newStop[4]));
            }
        });
        colorRow.add(deleteBtn);

        mainPanel.add(colorRow);
        mainPanel.add(Box.createVerticalStrut(10));

        // Angle row with wheel and spinner
        JPanel angleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        angleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel angleLabel = new JLabel("Angle:");
        angleRow.add(angleLabel);

        // Angle spinner
        SpinnerNumberModel angleModel = new SpinnerNumberModel(angleRef[0], 0.0, 360.0, 1.0);
        JSpinner angleSpinner = new JSpinner(angleModel);
        angleSpinner.setPreferredSize(new Dimension(70, 25));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(angleSpinner, "0.0");
        angleSpinner.setEditor(editor);

        // Angle wheel panel
        JPanel angleWheel = new JPanel() {
            private boolean dragging = false;
            {
                setPreferredSize(new Dimension(60, 60));
                setBackground(Color.WHITE);
                setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160)));

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        dragging = true;
                        updateAngleFromMouse(e.getX(), e.getY());
                    }
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        dragging = false;
                    }
                });

                addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        if (dragging) {
                            updateAngleFromMouse(e.getX(), e.getY());
                        }
                    }
                });
            }

            private void updateAngleFromMouse(int x, int y) {
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                double dx = x - cx;
                double dy = y - cy;
                double angle = Math.toDegrees(Math.atan2(dy, dx));
                if (angle < 0) angle += 360;
                angleRef[0] = angle;
                angleSpinner.setValue(angle);
                applyAngle(angle, finalType);
                repaint();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                int radius = Math.min(cx, cy) - 4;

                // Draw outer circle
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);

                // Draw gradient preview in circle
                for (int a = 0; a < 360; a += 10) {
                    double rad = Math.toRadians(a);
                    int x1 = cx + (int)(Math.cos(rad) * (radius - 8));
                    int y1 = cy + (int)(Math.sin(rad) * (radius - 8));
                    int x2 = cx + (int)(Math.cos(rad) * radius);
                    int y2 = cy + (int)(Math.sin(rad) * radius);
                    g2d.setColor(new Color(220, 220, 220));
                    g2d.drawLine(x1, y1, x2, y2);
                }

                // Draw angle indicator line
                double rad = Math.toRadians(angleRef[0]);
                int lineX = cx + (int)(Math.cos(rad) * radius);
                int lineY = cy + (int)(Math.sin(rad) * radius);
                g2d.setColor(new Color(0, 120, 215));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(cx, cy, lineX, lineY);

                // Draw center dot
                g2d.setColor(new Color(0, 120, 215));
                g2d.fillOval(cx - 4, cy - 4, 8, 8);

                // Draw arrow head
                double arrowAngle1 = rad + Math.toRadians(150);
                double arrowAngle2 = rad - Math.toRadians(150);
                int arrowLen = 8;
                int ax1 = lineX + (int)(Math.cos(arrowAngle1) * arrowLen);
                int ay1 = lineY + (int)(Math.sin(arrowAngle1) * arrowLen);
                int ax2 = lineX + (int)(Math.cos(arrowAngle2) * arrowLen);
                int ay2 = lineY + (int)(Math.sin(arrowAngle2) * arrowLen);
                g2d.drawLine(lineX, lineY, ax1, ay1);
                g2d.drawLine(lineX, lineY, ax2, ay2);
            }
        };

        angleSpinner.addChangeListener(e -> {
            double angle = (Double) angleSpinner.getValue();
            angleRef[0] = angle;
            applyAngle(angle, finalType);
            angleWheel.repaint();
        });

        angleRow.add(angleWheel);
        angleRow.add(angleSpinner);
        angleRow.add(new JLabel("°"));
        mainPanel.add(angleRow);
        mainPanel.add(Box.createVerticalStrut(15));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(e -> {
            if ("settings".equals(finalType)) {
                settingsUseGradient = true;
            } else if ("settingsHeader".equals(finalType)) {
                settingsHeaderUseGradient = true;
            } else if ("format".equals(finalType)) {
                formatUseGradient = true;
            } else if ("formatHeader".equals(finalType)) {
                formatHeaderUseGradient = true;
            } else {
                timelineUseGradient = true;
                timelineBgColorBtn.setBackground(timelineInteriorColor);
            }
            timelineDisplayPanel.repaint();
            if ("format".equals(finalType) || "formatHeader".equals(finalType)) {
                applyFormatPanelColors();
            }
            dialog.dispose();
        });
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> {
            // Restore original values
            if ("settings".equals(finalType)) {
                settingsGradientStops.clear();
                for (float[] stop : origStops) {
                    settingsGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                settingsInteriorColor = origColor1;
                settingsInteriorColor2 = origColor2;
                settingsGradientDir = origDir;
                settingsGradientAngle = origAngle;
                settingsUseGradient = origUseGradient;
            } else if ("settingsHeader".equals(finalType)) {
                settingsHeaderGradientStops.clear();
                for (float[] stop : origStops) {
                    settingsHeaderGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                settingsHeaderColor = origColor1;
                settingsHeaderColor2 = origColor2;
                settingsHeaderGradientDir = origDir;
                settingsHeaderGradientAngle = origAngle;
                settingsHeaderUseGradient = origUseGradient;
            } else if ("format".equals(finalType)) {
                formatGradientStops.clear();
                for (float[] stop : origStops) {
                    formatGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                formatInteriorColor = origColor1;
                formatInteriorColor2 = origColor2;
                formatGradientAngle = origAngle;
                formatUseGradient = origUseGradient;
            } else if ("formatHeader".equals(finalType)) {
                formatHeaderGradientStops.clear();
                for (float[] stop : origStops) {
                    formatHeaderGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                formatHeaderColor = origColor1;
                formatHeaderColor2 = origColor2;
                formatHeaderGradientAngle = origAngle;
                formatHeaderUseGradient = origUseGradient;
            } else {
                timelineGradientStops.clear();
                for (float[] stop : origStops) {
                    timelineGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                timelineInteriorColor = origColor1;
                timelineInteriorColor2 = origColor2;
                timelineGradientDir = origDir;
                timelineGradientAngle = origAngle;
                timelineUseGradient = origUseGradient;
            }
            timelineDisplayPanel.repaint();
            if ("format".equals(finalType) || "formatHeader".equals(finalType)) {
                applyFormatPanelColors();
            }
            dialog.dispose();
        });
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void applyAngle(double angle, String type) {
        if ("settings".equals(type)) {
            settingsGradientAngle = angle;
            settingsUseGradient = true;
        } else if ("settingsHeader".equals(type)) {
            settingsHeaderGradientAngle = angle;
            settingsHeaderUseGradient = true;
        } else if ("general".equals(type)) {
            generalGradientAngle = angle;
            generalUseGradient = true;
        } else if ("format".equals(type)) {
            formatGradientAngle = angle;
            formatUseGradient = true;
        } else if ("formatHeader".equals(type)) {
            formatHeaderGradientAngle = angle;
            formatHeaderUseGradient = true;
        } else if ("formatTab".equals(type)) {
            formatTabGradientAngle = angle;
            formatTabUseGradient = true;
        } else if ("formatSelectedTab".equals(type)) {
            formatSelectedTabGradientAngle = angle;
            formatSelectedTabUseGradient = true;
        } else if ("formatTabContent".equals(type)) {
            formatTabContentGradientAngle = angle;
            formatTabContentUseGradient = true;
        } else if ("layers".equals(type)) {
            layersGradientAngle = angle;
            layersUseGradient = true;
        } else if ("layersHeader".equals(type)) {
            layersHeaderGradientAngle = angle;
            layersHeaderUseGradient = true;
        } else if ("layersListBg".equals(type)) {
            layersListBgGradientAngle = angle;
            layersListBgUseGradient = true;
            layersTaskGradientAngle = angle;
            layersTaskUseGradient = true;
        } else if ("toolbar".equals(type)) {
            toolbarGradientAngle = angle;
            toolbarUseGradient = true;
            if (toolbarPanel != null) toolbarPanel.repaint();
        } else {
            timelineGradientAngle = angle;
            timelineUseGradient = true;
        }
        timelineDisplayPanel.repaint();
        if ("format".equals(type) || "formatHeader".equals(type) || "formatTab".equals(type) || "formatSelectedTab".equals(type) || "formatTabContent".equals(type)) {
            applyFormatPanelColors();
        }
        if ("layers".equals(type) || "layersHeader".equals(type)) {
            applyRightPanelColors();
        }
        if ("layersListBg".equals(type) || "layersTask".equals(type)) {
            applyLayersListColors();
        }
        if ("settings".equals(type) || "settingsHeader".equals(type)) {
            applySettingsPanelColors();
        }
        if ("general".equals(type)) {
            applyGeneralPanelColors();
        }
    }

    // Helper method to create LinearGradientPaint from angle
    private java.awt.LinearGradientPaint createAngledGradient(int w, int h, double angleDegrees, float[] fractions, Color[] colors) {
        double angleRad = Math.toRadians(angleDegrees);
        double cos = Math.cos(angleRad);
        double sin = Math.sin(angleRad);

        // Calculate the diagonal length to ensure gradient covers entire area
        double diagonal = Math.sqrt(w * w + h * h);

        // Center of the area
        double cx = w / 2.0;
        double cy = h / 2.0;

        // Calculate start and end points based on angle
        float startX = (float)(cx - cos * diagonal / 2);
        float startY = (float)(cy - sin * diagonal / 2);
        float endX = (float)(cx + cos * diagonal / 2);
        float endY = (float)(cy + sin * diagonal / 2);

        return new java.awt.LinearGradientPaint(startX, startY, endX, endY, fractions, colors);
    }

    private Color interpolateGradientColor(ArrayList<float[]> stops, float pos) {
        if (stops.isEmpty()) return Color.WHITE;
        if (pos <= stops.get(0)[0]) {
            float[] s = stops.get(0);
            return new Color(s[1], s[2], s[3], s[4]);
        }
        if (pos >= stops.get(stops.size()-1)[0]) {
            float[] s = stops.get(stops.size()-1);
            return new Color(s[1], s[2], s[3], s[4]);
        }

        for (int i = 0; i < stops.size() - 1; i++) {
            float[] s1 = stops.get(i);
            float[] s2 = stops.get(i + 1);
            if (pos >= s1[0] && pos <= s2[0]) {
                float t = (pos - s1[0]) / (s2[0] - s1[0]);
                float r = s1[1] + t * (s2[1] - s1[1]);
                float g = s1[2] + t * (s2[2] - s1[2]);
                float b = s1[3] + t * (s2[3] - s1[3]);
                float a = s1[4] + t * (s2[4] - s1[4]);
                return new Color(r, g, b, a);
            }
        }
        return Color.WHITE;
    }

    private void updateGradientFromStops(ArrayList<float[]> stops, String type) {
        if (stops.size() >= 2) {
            float[] first = stops.get(0);
            float[] last = stops.get(stops.size() - 1);
            Color color1 = new Color(first[1], first[2], first[3], first[4]);
            Color color2 = new Color(last[1], last[2], last[3], last[4]);

            if ("settings".equals(type)) {
                settingsGradientStops.clear();
                for (float[] stop : stops) {
                    settingsGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                settingsInteriorColor = color1;
                settingsInteriorColor2 = color2;
                settingsUseGradient = true;
                if (settingsPanel != null) {
                    settingsPanel.revalidate();
                    settingsPanel.repaint();
                }
            } else if ("settingsHeader".equals(type)) {
                settingsHeaderGradientStops.clear();
                for (float[] stop : stops) {
                    settingsHeaderGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                settingsHeaderColor = color1;
                settingsHeaderColor2 = color2;
                settingsHeaderUseGradient = true;
                if (settingsPanel != null) {
                    settingsPanel.revalidate();
                    settingsPanel.repaint();
                }
            } else if ("general".equals(type)) {
                generalGradientStops.clear();
                for (float[] stop : stops) {
                    generalGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                generalInteriorColor = color1;
                generalInteriorColor2 = color2;
                generalUseGradient = true;
                if (generalPanel != null) {
                    generalPanel.revalidate();
                    generalPanel.repaint();
                }
            } else if ("format".equals(type)) {
                formatGradientStops.clear();
                for (float[] stop : stops) {
                    formatGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                formatInteriorColor = color1;
                formatInteriorColor2 = color2;
                formatUseGradient = true;
                applyFormatPanelColors();
            } else if ("formatHeader".equals(type)) {
                formatHeaderGradientStops.clear();
                for (float[] stop : stops) {
                    formatHeaderGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                formatHeaderColor = color1;
                formatHeaderColor2 = color2;
                formatHeaderUseGradient = true;
                applyFormatPanelColors();
            } else if ("formatTab".equals(type)) {
                formatTabGradientStops.clear();
                for (float[] stop : stops) {
                    formatTabGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                formatTabColor = color1;
                formatTabColor2 = color2;
                formatTabUseGradient = true;
                applyFormatPanelColors();
            } else if ("formatSelectedTab".equals(type)) {
                formatSelectedTabGradientStops.clear();
                for (float[] stop : stops) {
                    formatSelectedTabGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                formatSelectedTabColor = color1;
                formatSelectedTabColor2 = color2;
                formatSelectedTabUseGradient = true;
                applyFormatPanelColors();
            } else if ("formatTabContent".equals(type)) {
                formatTabContentGradientStops.clear();
                for (float[] stop : stops) {
                    formatTabContentGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                formatTabContentColor = color1;
                formatTabContentColor2 = color2;
                formatTabContentUseGradient = true;
                applyFormatPanelColors();
            } else if ("layers".equals(type)) {
                layersGradientStops.clear();
                for (float[] stop : stops) {
                    layersGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                layersInteriorColor = color1;
                layersInteriorColor2 = color2;
                layersUseGradient = true;
                applyLayersPanelColors();
            } else if ("layersHeader".equals(type)) {
                layersHeaderGradientStops.clear();
                for (float[] stop : stops) {
                    layersHeaderGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                layersHeaderColor = color1;
                layersHeaderColor2 = color2;
                layersHeaderUseGradient = true;
                applyLayersPanelColors();
            } else if ("layersListBg".equals(type)) {
                layersListBgGradientStops.clear();
                for (float[] stop : stops) {
                    layersListBgGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                layersListBgColor = color1;
                layersListBgColor2 = color2;
                layersListBgUseGradient = true;
                applyLayersPanelColors();
                layersTaskGradientStops.clear();
                for (float[] stop : stops) {
                    layersTaskGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                layersTaskColor = color1;
                layersTaskColor2 = color2;
                layersTaskUseGradient = true;
                applyLayersPanelColors();
            } else if ("toolbar".equals(type)) {
                toolbarGradientStops.clear();
                for (float[] stop : stops) {
                    toolbarGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                toolbarBgColor = color1;
                toolbarBgColor2 = color2;
                toolbarUseGradient = true;
                if (toolbarPanel != null) toolbarPanel.repaint();
            } else {
                timelineGradientStops.clear();
                for (float[] stop : stops) {
                    timelineGradientStops.add(new float[]{stop[0], stop[1], stop[2], stop[3], stop[4]});
                }
                timelineInteriorColor = color1;
                timelineInteriorColor2 = color2;
                timelineUseGradient = true;
            }
            timelineDisplayPanel.repaint();
        }
    }

    private void chooseTimelineAxisColor() {
        saveState();
        Color newColor = showColorChooserWithAlpha("Choose Axis Color", timelineAxisColor, color -> {
            timelineAxisColor = color;
            timelineAxisColorBtn.setBackground(color);
            timelineDisplayPanel.repaint();
        });
        if (newColor != null) {
            timelineAxisColor = newColor;
            timelineAxisColorBtn.setBackground(newColor);
            refreshTimeline();
        } else {
            undo();
        }
    }

    private void chooseAxisDateColor() {
        saveState();
        Color newColor = showColorChooserWithAlpha("Choose Date Label Color", axisDateColor, color -> {
            axisDateColor = color;
            axisDateColorBtn.setBackground(color);
            timelineDisplayPanel.repaint();
        });
        if (newColor != null) {
            axisDateColor = newColor;
            axisDateColorBtn.setBackground(newColor);
            refreshTimeline();
        } else {
            undo();
        }
    }

    private void updateMilestoneOutlineThickness() {
        if (selectedMilestoneIndex < 0 || selectedMilestoneIndex >= milestones.size()) return;
        saveState();
        milestones.get(selectedMilestoneIndex).outlineThickness = (Integer) milestoneOutlineThicknessSpinner.getValue();
        refreshTimeline();
    }

    private void updateMilestoneHeight() {
        if (selectedMilestoneIndex < 0 || selectedMilestoneIndex >= milestones.size()) return;
        saveState();
        milestones.get(selectedMilestoneIndex).height = (Integer) milestoneHeightSpinner.getValue();
        refreshTimeline();
    }

    private void updateMilestoneWidth() {
        if (selectedMilestoneIndex < 0 || selectedMilestoneIndex >= milestones.size()) return;
        saveState();
        milestones.get(selectedMilestoneIndex).width = (Integer) milestoneWidthSpinner.getValue();
        refreshTimeline();
    }

    private void updateMilestoneBevel() {
        if (selectedMilestoneIndex < 0 || selectedMilestoneIndex >= milestones.size()) return;
        saveState();
        milestones.get(selectedMilestoneIndex).bevelFill = milestoneBevelCheckbox.isSelected();
        refreshTimeline();
    }

    void selectTask(int index, boolean ctrlDown) {
        // Only deselect milestones when NOT using Ctrl (allows mixed selection)
        if (index >= 0 && !ctrlDown) {
            selectedMilestoneIndex = -1;
            selectedMilestoneIndices.clear();
        }
        if (index >= 0) {
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
        if (spreadsheetTable != null) spreadsheetTable.repaint();
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
            deleteTaskBtn.setEnabled(false);
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
            deleteTaskBtn.setEnabled(true);
            taskNameField.setText(task.name);
            taskStartField.setText(task.startDate);
            taskEndField.setText(task.endDate);

            fillColorBtn.setBackground(fillColor);
            outlineColorBtn.setBackground(outlineColor);
            outlineThicknessSpinner.setValue(task.outlineThickness);
            taskHeightSpinner.setValue(task.height);
            bevelFillCheckbox.setSelected(task.bevelFill);

            centerTextField.setText(task.centerText);
            fontFamilyCombo.setSelectedItem(task.fontFamily);
            fontSizeSpinner.setValue(task.fontSize);
            boldBtn.setSelected(task.fontBold);
            italicBtn.setSelected(task.fontItalic);
            textColorBtn.setBackground(task.textColor != null ? task.textColor : Color.BLACK);

            frontTextField.setText(task.frontText);
            frontFontCombo.setSelectedItem(task.frontFontFamily);
            frontFontSizeSpinner.setValue(task.frontFontSize);
            frontBoldBtn.setSelected(task.frontFontBold);
            frontItalicBtn.setSelected(task.frontFontItalic);
            frontTextColorBtn.setBackground(task.frontTextColor != null ? task.frontTextColor : Color.BLACK);
            frontXOffsetSpinner.setValue(task.frontTextXOffset);
            frontYOffsetSpinner.setValue(task.frontTextYOffset);
            frontWrapCheckbox.setSelected(task.frontTextWrap);
            frontVisibleCheckbox.setSelected(task.frontTextVisible);

            centerXOffsetSpinner.setValue(task.centerTextXOffset);
            centerYOffsetSpinner.setValue(task.centerTextYOffset);
            centerWrapCheckbox.setSelected(task.centerTextWrap);
            centerVisibleCheckbox.setSelected(task.centerTextVisible);

            aboveTextField.setText(task.aboveText);
            aboveFontCombo.setSelectedItem(task.aboveFontFamily);
            aboveFontSizeSpinner.setValue(task.aboveFontSize);
            aboveBoldBtn.setSelected(task.aboveFontBold);
            aboveItalicBtn.setSelected(task.aboveFontItalic);
            aboveTextColorBtn.setBackground(task.aboveTextColor != null ? task.aboveTextColor : Color.BLACK);
            aboveXOffsetSpinner.setValue(task.aboveTextXOffset);
            aboveYOffsetSpinner.setValue(task.aboveTextYOffset);
            aboveWrapCheckbox.setSelected(task.aboveTextWrap);
            aboveVisibleCheckbox.setSelected(task.aboveTextVisible);

            underneathTextField.setText(task.underneathText);
            underneathFontCombo.setSelectedItem(task.underneathFontFamily);
            underneathFontSizeSpinner.setValue(task.underneathFontSize);
            underneathBoldBtn.setSelected(task.underneathFontBold);
            underneathItalicBtn.setSelected(task.underneathFontItalic);
            underneathTextColorBtn.setBackground(task.underneathTextColor != null ? task.underneathTextColor : Color.BLACK);
            underneathXOffsetSpinner.setValue(task.underneathTextXOffset);
            underneathYOffsetSpinner.setValue(task.underneathTextYOffset);
            underneathWrapCheckbox.setSelected(task.underneathTextWrap);
            underneathVisibleCheckbox.setSelected(task.underneathTextVisible);

            behindTextField.setText(task.behindText);
            behindFontCombo.setSelectedItem(task.behindFontFamily);
            behindFontSizeSpinner.setValue(task.behindFontSize);
            behindBoldBtn.setSelected(task.behindFontBold);
            behindItalicBtn.setSelected(task.behindFontItalic);
            behindTextColorBtn.setBackground(task.behindTextColor != null ? task.behindTextColor : new Color(150, 150, 150));
            behindXOffsetSpinner.setValue(task.behindTextXOffset);
            behindYOffsetSpinner.setValue(task.behindTextYOffset);
            behindWrapCheckbox.setSelected(task.behindTextWrap);
            behindVisibleCheckbox.setSelected(task.behindTextVisible);

            // Notes
            note1Area.setText(task.note1);
            note2Area.setText(task.note2);
            note3Area.setText(task.note3);
            note4Area.setText(task.note4);
            note5Area.setText(task.note5);

            setFormatFieldsEnabled(true);
        } else {
            // Multiple selection - show blank fields for batch editing
            formatTitleLabel.setText(selectedTaskIndices.size() + " tasks selected");
            formatTitleLabel.setForeground(Color.BLUE);
            duplicateTaskBtn.setEnabled(true);
            deleteTaskBtn.setEnabled(true);

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
            bevelFillCheckbox.setSelected(false);
            fontFamilyCombo.setSelectedIndex(0);
            fontSizeSpinner.setValue(11);
            frontFontCombo.setSelectedIndex(0);
            frontFontSizeSpinner.setValue(10);
            aboveFontCombo.setSelectedIndex(0);
            aboveFontSizeSpinner.setValue(10);
            underneathFontCombo.setSelectedIndex(0);
            underneathFontSizeSpinner.setValue(10);
            behindFontCombo.setSelectedIndex(0);
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
            note1Area.setText("");
            note2Area.setText("");
            note3Area.setText("");
            note4Area.setText("");
            note5Area.setText("");

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
        bevelFillCheckbox.setEnabled(enabled);
        bevelSettingsBtn.setEnabled(enabled);
        centerTextField.setEnabled(enabled);
        fontFamilyCombo.setEnabled(enabled);
        fontSizeSpinner.setEnabled(enabled);
        boldBtn.setEnabled(enabled);
        italicBtn.setEnabled(enabled);
        textColorBtn.setEnabled(enabled);
        frontTextField.setEnabled(enabled);
        frontFontCombo.setEnabled(enabled);
        frontFontSizeSpinner.setEnabled(enabled);
        frontBoldBtn.setEnabled(enabled);
        frontItalicBtn.setEnabled(enabled);
        frontTextColorBtn.setEnabled(enabled);
        frontXOffsetSpinner.setEnabled(enabled);
        frontYOffsetSpinner.setEnabled(enabled);
        frontWrapCheckbox.setEnabled(enabled);
        frontVisibleCheckbox.setEnabled(enabled);
        centerXOffsetSpinner.setEnabled(enabled);
        centerYOffsetSpinner.setEnabled(enabled);
        centerWrapCheckbox.setEnabled(enabled);
        centerVisibleCheckbox.setEnabled(enabled);
        aboveTextField.setEnabled(enabled);
        aboveFontCombo.setEnabled(enabled);
        aboveFontSizeSpinner.setEnabled(enabled);
        aboveBoldBtn.setEnabled(enabled);
        aboveItalicBtn.setEnabled(enabled);
        aboveTextColorBtn.setEnabled(enabled);
        aboveXOffsetSpinner.setEnabled(enabled);
        aboveYOffsetSpinner.setEnabled(enabled);
        aboveWrapCheckbox.setEnabled(enabled);
        aboveVisibleCheckbox.setEnabled(enabled);
        underneathTextField.setEnabled(enabled);
        underneathFontCombo.setEnabled(enabled);
        underneathFontSizeSpinner.setEnabled(enabled);
        underneathBoldBtn.setEnabled(enabled);
        underneathItalicBtn.setEnabled(enabled);
        underneathTextColorBtn.setEnabled(enabled);
        underneathXOffsetSpinner.setEnabled(enabled);
        underneathYOffsetSpinner.setEnabled(enabled);
        underneathWrapCheckbox.setEnabled(enabled);
        underneathVisibleCheckbox.setEnabled(enabled);
        behindTextField.setEnabled(enabled);
        behindFontCombo.setEnabled(enabled);
        behindFontSizeSpinner.setEnabled(enabled);
        behindBoldBtn.setEnabled(enabled);
        behindItalicBtn.setEnabled(enabled);
        behindTextColorBtn.setEnabled(enabled);
        behindXOffsetSpinner.setEnabled(enabled);
        behindYOffsetSpinner.setEnabled(enabled);
        behindWrapCheckbox.setEnabled(enabled);
        behindVisibleCheckbox.setEnabled(enabled);
        // Notes
        note1Area.setEnabled(enabled);
        note2Area.setEnabled(enabled);
        note3Area.setEnabled(enabled);
        note4Area.setEnabled(enabled);
        note5Area.setEnabled(enabled);
    }

    private void clearFormatFields() {
        taskNameField.setText("");
        taskStartField.setText("");
        taskEndField.setText("");
        fillColorBtn.setBackground(null);
        outlineColorBtn.setBackground(null);
        outlineThicknessSpinner.setValue(2);
        taskHeightSpinner.setValue(25);
        bevelFillCheckbox.setSelected(false);
        centerTextField.setText("");
        fontFamilyCombo.setSelectedIndex(0);
        fontSizeSpinner.setValue(11);
        boldBtn.setSelected(false);
        italicBtn.setSelected(false);
        textColorBtn.setBackground(null);
        frontTextField.setText("");
        frontFontCombo.setSelectedIndex(0);
        frontFontSizeSpinner.setValue(10);
        frontBoldBtn.setSelected(false);
        frontItalicBtn.setSelected(false);
        frontTextColorBtn.setBackground(null);
        frontXOffsetSpinner.setValue(0);
        frontYOffsetSpinner.setValue(0);
        centerXOffsetSpinner.setValue(0);
        centerYOffsetSpinner.setValue(0);
        aboveTextField.setText("");
        aboveFontCombo.setSelectedIndex(0);
        aboveFontSizeSpinner.setValue(10);
        aboveBoldBtn.setSelected(false);
        aboveItalicBtn.setSelected(false);
        aboveTextColorBtn.setBackground(null);
        aboveXOffsetSpinner.setValue(0);
        aboveYOffsetSpinner.setValue(0);
        underneathTextField.setText("");
        underneathFontCombo.setSelectedIndex(0);
        underneathFontSizeSpinner.setValue(10);
        underneathBoldBtn.setSelected(false);
        underneathItalicBtn.setSelected(false);
        underneathTextColorBtn.setBackground(null);
        underneathXOffsetSpinner.setValue(0);
        underneathYOffsetSpinner.setValue(0);
        behindTextField.setText("");
        behindFontCombo.setSelectedIndex(0);
        behindFontSizeSpinner.setValue(10);
        behindBoldBtn.setSelected(false);
        behindItalicBtn.setSelected(false);
        behindTextColorBtn.setBackground(null);
        behindXOffsetSpinner.setValue(0);
        behindYOffsetSpinner.setValue(0);
        // Notes
        note1Area.setText("");
        note2Area.setText("");
        note3Area.setText("");
        note4Area.setText("");
        note5Area.setText("");
    }

    void selectMilestone(int index, boolean ctrlDown) {
        // Handle multi-select with Ctrl key
        if (ctrlDown && index >= 0) {
            // Toggle selection in multi-select set
            if (selectedMilestoneIndices.contains(index)) {
                selectedMilestoneIndices.remove(index);
                if (selectedMilestoneIndex == index) {
                    selectedMilestoneIndex = selectedMilestoneIndices.isEmpty() ? -1 : selectedMilestoneIndices.iterator().next();
                }
            } else {
                selectedMilestoneIndices.add(index);
                selectedMilestoneIndex = index;
            }
            // Don't clear task selection when Ctrl is held
        } else {
            // Single select
            selectedMilestoneIndex = index;
            selectedMilestoneIndices.clear();
            if (index >= 0) {
                selectedMilestoneIndices.add(index);
                // Only deselect tasks when NOT using Ctrl
                selectedTaskIndices.clear();
            }
        }
        if (index >= 0) {
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
            milestoneBevelCheckbox.setSelected(milestone.bevelFill);
            milestoneBevelCheckbox.setEnabled(true);
            milestoneBevelSettingsBtn.setEnabled(true);

            // Populate text format fields (same as tasks)
            centerTextField.setText(milestone.centerText);
            fontFamilyCombo.setSelectedItem(milestone.fontFamily);
            fontSizeSpinner.setValue(milestone.fontSize);
            boldBtn.setSelected(milestone.fontBold);
            italicBtn.setSelected(milestone.fontItalic);
            textColorBtn.setBackground(milestone.textColor != null ? milestone.textColor : Color.BLACK);
            centerXOffsetSpinner.setValue(milestone.centerTextXOffset);
            centerYOffsetSpinner.setValue(milestone.centerTextYOffset);
            centerWrapCheckbox.setSelected(milestone.centerTextWrap);
            centerVisibleCheckbox.setSelected(milestone.centerTextVisible);

            frontTextField.setText(milestone.frontText);
            frontFontCombo.setSelectedItem(milestone.frontFontFamily);
            frontFontSizeSpinner.setValue(milestone.frontFontSize);
            frontBoldBtn.setSelected(milestone.frontFontBold);
            frontItalicBtn.setSelected(milestone.frontFontItalic);
            frontTextColorBtn.setBackground(milestone.frontTextColor != null ? milestone.frontTextColor : Color.BLACK);
            frontXOffsetSpinner.setValue(milestone.frontTextXOffset);
            frontYOffsetSpinner.setValue(milestone.frontTextYOffset);
            frontWrapCheckbox.setSelected(milestone.frontTextWrap);
            frontVisibleCheckbox.setSelected(milestone.frontTextVisible);

            aboveTextField.setText(milestone.aboveText);
            aboveFontCombo.setSelectedItem(milestone.aboveFontFamily);
            aboveFontSizeSpinner.setValue(milestone.aboveFontSize);
            aboveBoldBtn.setSelected(milestone.aboveFontBold);
            aboveItalicBtn.setSelected(milestone.aboveFontItalic);
            aboveTextColorBtn.setBackground(milestone.aboveTextColor != null ? milestone.aboveTextColor : Color.BLACK);
            aboveXOffsetSpinner.setValue(milestone.aboveTextXOffset);
            aboveYOffsetSpinner.setValue(milestone.aboveTextYOffset);
            aboveWrapCheckbox.setSelected(milestone.aboveTextWrap);
            aboveVisibleCheckbox.setSelected(milestone.aboveTextVisible);

            underneathTextField.setText(milestone.underneathText);
            underneathFontCombo.setSelectedItem(milestone.underneathFontFamily);
            underneathFontSizeSpinner.setValue(milestone.underneathFontSize);
            underneathBoldBtn.setSelected(milestone.underneathFontBold);
            underneathItalicBtn.setSelected(milestone.underneathFontItalic);
            underneathTextColorBtn.setBackground(milestone.underneathTextColor != null ? milestone.underneathTextColor : Color.BLACK);
            underneathXOffsetSpinner.setValue(milestone.underneathTextXOffset);
            underneathYOffsetSpinner.setValue(milestone.underneathTextYOffset);
            underneathWrapCheckbox.setSelected(milestone.underneathTextWrap);
            underneathVisibleCheckbox.setSelected(milestone.underneathTextVisible);

            behindTextField.setText(milestone.behindText);
            behindFontCombo.setSelectedItem(milestone.behindFontFamily);
            behindFontSizeSpinner.setValue(milestone.behindFontSize);
            behindBoldBtn.setSelected(milestone.behindFontBold);
            behindItalicBtn.setSelected(milestone.behindFontItalic);
            behindTextColorBtn.setBackground(milestone.behindTextColor != null ? milestone.behindTextColor : new Color(150, 150, 150));
            behindXOffsetSpinner.setValue(milestone.behindTextXOffset);
            behindYOffsetSpinner.setValue(milestone.behindTextYOffset);
            behindWrapCheckbox.setSelected(milestone.behindTextWrap);
            behindVisibleCheckbox.setSelected(milestone.behindTextVisible);

            setFormatFieldsEnabled(true);
        } else {
            // Show task row when no milestone is selected
            row1CardLayout.show(row1Container, "task");
        }
        timelineDisplayPanel.repaint();
        if (spreadsheetTable != null) spreadsheetTable.repaint();
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

    // Overload for calls without ctrlDown parameter
    void selectMilestone(int index) {
        selectMilestone(index, false);
    }

    private void duplicateSelectedTasks() {
        if (selectedTaskIndices.isEmpty()) return;
        saveState();

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
            copy.bevelFill = original.bevelFill;
            copy.bevelDepth = original.bevelDepth;
            copy.bevelLightAngle = original.bevelLightAngle;
            copy.bevelHighlightOpacity = original.bevelHighlightOpacity;
            copy.bevelShadowOpacity = original.bevelShadowOpacity;
            copy.bevelStyle = original.bevelStyle;
            copy.topBevel = original.topBevel;
            copy.bottomBevel = original.bottomBevel;
            copy.height = original.height;
            copy.yPosition = original.yPosition >= 0 ? original.yPosition + original.height + 10 : -1;
            copy.fontFamily = original.fontFamily;
            copy.fontSize = original.fontSize;
            copy.fontBold = original.fontBold;
            copy.fontItalic = original.fontItalic;
            copy.textColor = original.textColor;
            copy.centerTextXOffset = original.centerTextXOffset;
            copy.centerTextYOffset = original.centerTextYOffset;
            // Front text
            copy.frontText = original.frontText;
            copy.frontFontFamily = original.frontFontFamily;
            copy.frontFontSize = original.frontFontSize;
            copy.frontFontBold = original.frontFontBold;
            copy.frontFontItalic = original.frontFontItalic;
            copy.frontTextColor = original.frontTextColor;
            copy.frontTextXOffset = original.frontTextXOffset;
            copy.frontTextYOffset = original.frontTextYOffset;
            // Above text
            copy.aboveText = original.aboveText;
            copy.aboveFontFamily = original.aboveFontFamily;
            copy.aboveFontSize = original.aboveFontSize;
            copy.aboveFontBold = original.aboveFontBold;
            copy.aboveFontItalic = original.aboveFontItalic;
            copy.aboveTextColor = original.aboveTextColor;
            copy.aboveTextXOffset = original.aboveTextXOffset;
            copy.aboveTextYOffset = original.aboveTextYOffset;
            // Underneath text
            copy.underneathText = original.underneathText;
            copy.underneathFontFamily = original.underneathFontFamily;
            copy.underneathFontSize = original.underneathFontSize;
            copy.underneathFontBold = original.underneathFontBold;
            copy.underneathFontItalic = original.underneathFontItalic;
            copy.underneathTextColor = original.underneathTextColor;
            copy.underneathTextXOffset = original.underneathTextXOffset;
            copy.underneathTextYOffset = original.underneathTextYOffset;
            // Behind text
            copy.behindText = original.behindText;
            copy.behindFontFamily = original.behindFontFamily;
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

    private void deleteSelectedItems() {
        timelineDisplayPanel.deleteSelectedItem();
    }

    private void updateSelectedTaskName() {
        if (selectedTaskIndices.size() == 1) {
            int index = selectedTaskIndices.iterator().next();
            String newName = taskNameField.getText().trim();
            if (!newName.isEmpty()) {
                saveState();
                tasks.get(index).name = newName;
                formatTitleLabel.setText("Selected: " + newName);
                refreshTimeline();
                updateSpreadsheet();
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
            saveState();
            for (int idx : selectedTaskIndices) {
                TimelineTask task = tasks.get(idx);
                task.startDate = newStart;
                task.endDate = newEnd;
            }
            refreshTimeline();
            updateSpreadsheet();
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
        saveState();
        int taskIndex = tasks.size();
        String taskName = "Task " + (taskIndex + 1);
        // Start at the first month shown on the timeline axis
        LocalDate timelineStart;
        try {
            timelineStart = LocalDate.parse(startDateField.getText().trim(), DATE_FORMAT);
        } catch (Exception e) {
            timelineStart = LocalDate.now();
        }
        String startDate = timelineStart.format(DATE_FORMAT);
        String endDate = timelineStart.plusMonths(3).format(DATE_FORMAT);

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

    private void showShapesMenu(JButton button) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));

        String[] shapes = {"Rectangle", "Oval", "Arrow Right", "Arrow Left", "Arrow Up", "Arrow Down", "Pentagon", "Cross", "Heart", "Crescent"};
        int buttonWidth = button.getWidth();

        for (String shape : shapes) {
            JMenuItem item = new JMenuItem(shape) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int cy = getHeight() / 2;
                    int size = 10;
                    g2d.setColor(new Color(80, 80, 80));
                    drawMilestoneShape(g2d, shape.toLowerCase().replace(" ", "_"), 20, cy, size, size, true);
                    g2d.setColor(Color.BLACK);
                    drawMilestoneShape(g2d, shape.toLowerCase().replace(" ", "_"), 20, cy, size, size, false);
                }
            };
            item.setPreferredSize(new Dimension(buttonWidth, 28));
            item.setBorder(BorderFactory.createEmptyBorder(4, 35, 4, 10));
            item.addActionListener(e -> addNewShape(shape.toLowerCase().replace(" ", "_")));
            popup.add(item);
        }

        popup.show(button, 0, button.getHeight());
    }

    private void addNewShape(String shape) {
        saveState();
        int shapeIndex = milestones.size();
        String name = "Shape " + (shapeIndex + 1);
        LocalDate timelineStart;
        try {
            timelineStart = LocalDate.parse(startDateField.getText().trim(), DATE_FORMAT);
        } catch (Exception e) {
            timelineStart = LocalDate.now();
        }
        String date = timelineStart.format(DATE_FORMAT);

        TimelineMilestone shapeObj = new TimelineMilestone(name, date, shape);
        shapeObj.fillColor = TASK_COLORS[(shapeIndex + 5) % TASK_COLORS.length];
        milestones.add(shapeObj);
        layerOrder.add(0, shapeObj);
        refreshTimeline();
    }

    private void addNewMilestone(String shape) {
        saveState();
        int milestoneIndex = milestones.size();
        String name = "Milestone " + (milestoneIndex + 1);
        // Start at the first month shown on the timeline axis
        LocalDate timelineStart;
        try {
            timelineStart = LocalDate.parse(startDateField.getText().trim(), DATE_FORMAT);
        } catch (Exception e) {
            timelineStart = LocalDate.now();
        }
        String date = timelineStart.format(DATE_FORMAT);

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
            case "rectangle":
                if (fill) g2d.fillRect(cx - w, cy - h, w * 2, h * 2);
                else g2d.drawRect(cx - w, cy - h, w * 2, h * 2);
                break;
            case "oval":
                if (fill) g2d.fillOval(cx - w, cy - h, w * 2, h * 2);
                else g2d.drawOval(cx - w, cy - h, w * 2, h * 2);
                break;
            case "arrow_right":
                xPoints = new int[]{cx - w, cx + w, cx - w};
                yPoints = new int[]{cy - h, cy, cy + h};
                if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                else g2d.drawPolygon(xPoints, yPoints, 3);
                break;
            case "arrow_left":
                xPoints = new int[]{cx + w, cx - w, cx + w};
                yPoints = new int[]{cy - h, cy, cy + h};
                if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                else g2d.drawPolygon(xPoints, yPoints, 3);
                break;
            case "arrow_up":
                xPoints = new int[]{cx - w, cx, cx + w};
                yPoints = new int[]{cy + h, cy - h, cy + h};
                if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                else g2d.drawPolygon(xPoints, yPoints, 3);
                break;
            case "arrow_down":
                xPoints = new int[]{cx - w, cx, cx + w};
                yPoints = new int[]{cy - h, cy + h, cy - h};
                if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                else g2d.drawPolygon(xPoints, yPoints, 3);
                break;
            case "pentagon":
                xPoints = new int[5];
                yPoints = new int[5];
                for (int i = 0; i < 5; i++) {
                    xPoints[i] = cx + (int)(w * Math.cos(-Math.PI/2 + i * 2 * Math.PI / 5));
                    yPoints[i] = cy + (int)(h * Math.sin(-Math.PI/2 + i * 2 * Math.PI / 5));
                }
                if (fill) g2d.fillPolygon(xPoints, yPoints, 5);
                else g2d.drawPolygon(xPoints, yPoints, 5);
                break;
            case "cross":
                int cw = w / 3;
                int ch = h / 3;
                xPoints = new int[]{cx - cw, cx + cw, cx + cw, cx + w, cx + w, cx + cw, cx + cw, cx - cw, cx - cw, cx - w, cx - w, cx - cw};
                yPoints = new int[]{cy - h, cy - h, cy - ch, cy - ch, cy + ch, cy + ch, cy + h, cy + h, cy + ch, cy + ch, cy - ch, cy - ch};
                if (fill) g2d.fillPolygon(xPoints, yPoints, 12);
                else g2d.drawPolygon(xPoints, yPoints, 12);
                break;
            case "heart":
                java.awt.geom.Path2D.Double heart = new java.awt.geom.Path2D.Double();
                heart.moveTo(cx, cy + h);
                heart.curveTo(cx - w * 2, cy - h/2, cx - w, cy - h, cx, cy - h/3);
                heart.curveTo(cx + w, cy - h, cx + w * 2, cy - h/2, cx, cy + h);
                if (fill) g2d.fill(heart);
                else g2d.draw(heart);
                break;
            case "crescent":
                java.awt.geom.Area outer = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(cx - w, cy - h, w * 2, h * 2));
                java.awt.geom.Area inner = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(cx - w/2, cy - h, w * 2, h * 2));
                outer.subtract(inner);
                if (fill) g2d.fill(outer);
                else g2d.draw(outer);
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

    private JPanel createTimelineRangePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(230, 230, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 8, 15, 8));

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
        startDateField = new JTextField(LocalDate.now().withDayOfMonth(1).format(DATE_FORMAT), 8);
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
        endDateField = new JTextField(LocalDate.now().plusYears(1).withDayOfMonth(1).plusMonths(1).minusDays(1).format(DATE_FORMAT), 8);
        endDateField.addActionListener(e -> refreshTimeline());
        endCol.add(endDateField);
        dateContainer.add(endCol);

        panel.add(dateContainer);

        // Dual-handle range slider for date range - 2 years before and 3 years after today
        LocalDate sliderMinDate = LocalDate.now().minusYears(2);
        LocalDate sliderMaxDate = LocalDate.now().plusYears(3);

        // Helper to calculate slider base date and total days
        java.util.function.Supplier<LocalDate> getSliderBaseDate = () -> sliderMinDate;
        java.util.function.Supplier<Integer> getTotalDays = () -> (int) java.time.temporal.ChronoUnit.DAYS.between(sliderMinDate, sliderMaxDate);

        int startDayOffset = (int) java.time.temporal.ChronoUnit.DAYS.between(getSliderBaseDate.get(), LocalDate.now().withDayOfMonth(1));
        int endDayOffset = (int) java.time.temporal.ChronoUnit.DAYS.between(getSliderBaseDate.get(), LocalDate.now().plusYears(1).withDayOfMonth(1).plusMonths(1).minusDays(1));

        RangeSlider rangeSlider = new RangeSlider(0, getTotalDays.get(), startDayOffset, endDayOffset);
        rangeSlider.setMaximumSize(new Dimension(270, 25));
        rangeSlider.setPreferredSize(new Dimension(270, 25));
        rangeSlider.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));  // Center over spinners
        rangeSlider.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Connect range slider to date fields
        rangeSlider.addChangeListener(e -> {
            LocalDate startDate = getSliderBaseDate.get().plusDays(rangeSlider.getLowValue());
            LocalDate endDate = getSliderBaseDate.get().plusDays(rangeSlider.getHighValue());
            startDateField.setText(startDate.format(DATE_FORMAT));
            endDateField.setText(endDate.format(DATE_FORMAT));
            if (!rangeSlider.getValueIsAdjusting()) refreshTimeline();
        });

        // Connect date fields to range slider
        startDateField.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(startDateField.getText().trim(), DATE_FORMAT);
                int days = (int) java.time.temporal.ChronoUnit.DAYS.between(getSliderBaseDate.get(), date);
                rangeSlider.setLowValue(Math.max(0, Math.min(rangeSlider.getHighValue(), days)));
            } catch (Exception ex) {}
        });
        endDateField.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(endDateField.getText().trim(), DATE_FORMAT);
                int days = (int) java.time.temporal.ChronoUnit.DAYS.between(getSliderBaseDate.get(), date);
                rangeSlider.setHighValue(Math.max(rangeSlider.getLowValue(), Math.min(getTotalDays.get(), days)));
            } catch (Exception ex) {}
        });

        panel.add(rangeSlider);

        // Button row for Set to tasks buttons
        JPanel setToTasksBtnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        setToTasksBtnRow.setOpaque(false);
        setToTasksBtnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // "Set to tasks (loose)" button
        JButton setToTasksBtn = new JButton("Fit (loose)");
        setToTasksBtn.addActionListener(e -> {
            LocalDate earliest = null;
            LocalDate latest = null;

            // Find earliest and latest dates from tasks
            for (TimelineTask task : tasks) {
                try {
                    LocalDate start = LocalDate.parse(task.startDate, DATE_FORMAT);
                    LocalDate end = LocalDate.parse(task.endDate, DATE_FORMAT);
                    if (earliest == null || start.isBefore(earliest)) earliest = start;
                    if (latest == null || end.isAfter(latest)) latest = end;
                } catch (Exception ex) {}
            }

            // Find earliest and latest dates from milestones
            for (TimelineMilestone milestone : milestones) {
                try {
                    LocalDate date = LocalDate.parse(milestone.date, DATE_FORMAT);
                    if (earliest == null || date.isBefore(earliest)) earliest = date;
                    if (latest == null || date.isAfter(latest)) latest = date;
                } catch (Exception ex) {}
            }

            // Set range to first of the month of earliest and first of month after latest
            if (earliest != null && latest != null) {
                LocalDate newStart = earliest.withDayOfMonth(1);
                LocalDate newEnd = latest.plusMonths(1).withDayOfMonth(1);
                startDateField.setText(newStart.format(DATE_FORMAT));
                endDateField.setText(newEnd.format(DATE_FORMAT));

                int startDays = (int) java.time.temporal.ChronoUnit.DAYS.between(getSliderBaseDate.get(), newStart);
                int endDays = (int) java.time.temporal.ChronoUnit.DAYS.between(getSliderBaseDate.get(), newEnd);
                rangeSlider.setLowValue(Math.max(0, Math.min(getTotalDays.get(), startDays)));
                rangeSlider.setHighValue(Math.max(0, Math.min(getTotalDays.get(), endDays)));
                refreshTimeline();
            }
        });
        setToTasksBtnRow.add(setToTasksBtn);

        // "Set to tasks (tight)" button - exact dates
        JButton setToTasksTightBtn = new JButton("Fit (tight)");
        setToTasksTightBtn.addActionListener(e -> {
            LocalDate earliest = null;
            LocalDate latest = null;

            // Find earliest and latest dates from tasks
            for (TimelineTask task : tasks) {
                try {
                    LocalDate start = LocalDate.parse(task.startDate, DATE_FORMAT);
                    LocalDate end = LocalDate.parse(task.endDate, DATE_FORMAT);
                    if (earliest == null || start.isBefore(earliest)) earliest = start;
                    if (latest == null || end.isAfter(latest)) latest = end;
                } catch (Exception ex) {}
            }

            // Find earliest and latest dates from milestones
            for (TimelineMilestone milestone : milestones) {
                try {
                    LocalDate date = LocalDate.parse(milestone.date, DATE_FORMAT);
                    if (earliest == null || date.isBefore(earliest)) earliest = date;
                    if (latest == null || date.isAfter(latest)) latest = date;
                } catch (Exception ex) {}
            }

            // Set range to exact earliest and latest dates
            if (earliest != null && latest != null) {
                startDateField.setText(earliest.format(DATE_FORMAT));
                endDateField.setText(latest.format(DATE_FORMAT));

                int startDays = (int) java.time.temporal.ChronoUnit.DAYS.between(getSliderBaseDate.get(), earliest);
                int endDays = (int) java.time.temporal.ChronoUnit.DAYS.between(getSliderBaseDate.get(), latest);
                rangeSlider.setLowValue(Math.max(0, Math.min(getTotalDays.get(), startDays)));
                rangeSlider.setHighValue(Math.max(0, Math.min(getTotalDays.get(), endDays)));
                refreshTimeline();
            }
        });
        setToTasksBtnRow.add(setToTasksTightBtn);

        panel.add(setToTasksBtnRow);

        return panel;
    }

    private JPanel createGeneralPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (generalUseGradient && generalGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[generalGradientStops.size()];
                    Color[] colors = new Color[generalGradientStops.size()];
                    for (int i = 0; i < generalGradientStops.size(); i++) {
                        float[] stop = generalGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = createAngledGradient(w, h, generalGradientAngle, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(generalInteriorColor);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        panel.setMinimumSize(new Dimension(250, 400));


        // Add spacer row above Appearance header
        panel.add(Box.createVerticalStrut(15));

        // Appearance Section
        addSectionHeader(panel, "Appearance");

        JPanel bgColorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        bgColorRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        bgColorRow.setOpaque(false);
        bgColorRow.setMinimumSize(new Dimension(250, 25));
        bgColorRow.setPreferredSize(new Dimension(250, 25));
        bgColorRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel bgLabel = new JLabel("Background:");
        bgLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        bgColorRow.add(bgLabel);

        timelineBgColorBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (timelineUseGradient && timelineGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[timelineGradientStops.size()];
                    Color[] colors = new Color[timelineGradientStops.size()];
                    for (int i = 0; i < timelineGradientStops.size(); i++) {
                        float[] stop = timelineGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        timelineBgColorBtn.setPreferredSize(new Dimension(30, 20));
        timelineBgColorBtn.setBackground(timelineInteriorColor);
        timelineBgColorBtn.setToolTipText("Click to change timeline background color");
        timelineBgColorBtn.addActionListener(e -> chooseTimelineBackgroundColor());
        bgColorRow.add(timelineBgColorBtn);

        // Gradient dropdown arrow button with popup menu
        JButton gradientArrowBtn = new JButton("\u25BC");
        gradientArrowBtn.setPreferredSize(new Dimension(20, 20));
        gradientArrowBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        gradientArrowBtn.setMargin(new Insets(0, 0, 0, 0));
        gradientArrowBtn.setToolTipText("Gradient options");

        // Create popup menu
        JPopupMenu gradientMenu = new JPopupMenu();

        JMenuItem gradientMenuItem = new JMenuItem("Gradient...");
        gradientMenuItem.addActionListener(e -> showGradientDialog());
        gradientMenu.add(gradientMenuItem);

        gradientArrowBtn.addActionListener(e -> {
            gradientMenu.show(gradientArrowBtn, 0, gradientArrowBtn.getHeight());
        });

        bgColorRow.add(gradientArrowBtn);

        panel.add(bgColorRow);

        // Today Mark checkbox row
        JPanel todayMarkRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        todayMarkRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        todayMarkRow.setOpaque(false);
        todayMarkRow.setMinimumSize(new Dimension(250, 25));
        todayMarkRow.setPreferredSize(new Dimension(250, 25));
        todayMarkRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JCheckBox todayMarkCheckbox = new JCheckBox("Today Mark", showTodayMark);
        todayMarkCheckbox.setFont(new Font("Arial", Font.PLAIN, 11));
        todayMarkCheckbox.setOpaque(false);
        todayMarkCheckbox.addActionListener(e -> {
            showTodayMark = todayMarkCheckbox.isSelected();
            if (timelineDisplayPanel != null) timelineDisplayPanel.repaint();
        });
        todayMarkRow.add(todayMarkCheckbox);

        panel.add(todayMarkRow);

        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (settingsUseGradient && settingsGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[settingsGradientStops.size()];
                    Color[] colors = new Color[settingsGradientStops.size()];
                    for (int i = 0; i < settingsGradientStops.size(); i++) {
                        float[] stop = settingsGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = createAngledGradient(w, h, settingsGradientAngle, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(settingsInteriorColor);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        panel.setMinimumSize(new Dimension(250, 400));

        // Add spacer row above Timeline Axis header
        panel.add(Box.createVerticalStrut(15));

        // Timeline Axis section header
        addSectionHeader(panel, "Timeline Axis");

        // Axis position row
        JPanel axisPositionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        axisPositionRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        axisPositionRow.setOpaque(false);
        axisPositionRow.setMinimumSize(new Dimension(250, 25));
        axisPositionRow.setPreferredSize(new Dimension(250, 25));
        axisPositionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel axisPositionLabel = new JLabel("Position:");
        axisPositionLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        axisPositionRow.add(axisPositionLabel);

        timelineAxisPositionCombo = new JComboBox<>(new String[]{"Bottom", "Top"});
        timelineAxisPositionCombo.setPreferredSize(new Dimension(70, 20));
        timelineAxisPositionCombo.setSelectedItem(timelineAxisPosition);
        timelineAxisPositionCombo.addActionListener(e -> {
            timelineAxisPosition = (String) timelineAxisPositionCombo.getSelectedItem();
            if (timelineDisplayPanel != null) timelineDisplayPanel.repaint();
        });
        axisPositionRow.add(timelineAxisPositionCombo);

        panel.add(axisPositionRow);
        panel.add(Box.createVerticalStrut(1));

        // Timeline axis color row
        axisLineColorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        axisLineColorRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        axisLineColorRow.setOpaque(false);
        axisLineColorRow.setMinimumSize(new Dimension(250, 25));
        axisLineColorRow.setPreferredSize(new Dimension(250, 25));
        axisLineColorRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel axisColorLabel = new JLabel("Line Color:");
        axisColorLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        axisLineColorRow.add(axisColorLabel);

        timelineAxisColorBtn = new JButton();
        timelineAxisColorBtn.setPreferredSize(new Dimension(30, 20));
        timelineAxisColorBtn.setBackground(timelineAxisColor);
        timelineAxisColorBtn.setToolTipText("Click to change timeline axis color");
        timelineAxisColorBtn.addActionListener(e -> chooseTimelineAxisColor());
        axisLineColorRow.add(timelineAxisColorBtn);

        panel.add(axisLineColorRow);
        panel.add(Box.createVerticalStrut(1));

        // Timeline axis thickness row (Line style)
        axisLineThicknessRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        axisLineThicknessRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        axisLineThicknessRow.setOpaque(false);
        axisLineThicknessRow.setMinimumSize(new Dimension(250, 25));
        axisLineThicknessRow.setPreferredSize(new Dimension(250, 25));
        axisLineThicknessRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel axisThicknessLabel = new JLabel("Line Thickness:");
        axisThicknessLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        axisLineThicknessRow.add(axisThicknessLabel);

        timelineAxisThicknessSpinner = new JSpinner(new SpinnerNumberModel(timelineAxisThickness, 1, 10, 1));
        timelineAxisThicknessSpinner.setPreferredSize(new Dimension(50, 20));
        JSpinner.NumberEditor thicknessEditor = new JSpinner.NumberEditor(timelineAxisThicknessSpinner, "0");
        timelineAxisThicknessSpinner.setEditor(thicknessEditor);
        thicknessEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
        timelineAxisThicknessSpinner.addChangeListener(e -> {
            timelineAxisThickness = (Integer) timelineAxisThicknessSpinner.getValue();
            if (timelineDisplayPanel != null) timelineDisplayPanel.repaint();
        });
        axisLineThicknessRow.add(timelineAxisThicknessSpinner);

        panel.add(axisLineThicknessRow);
        panel.add(Box.createVerticalStrut(8));

        // Tick Marks section header
        addSectionHeader(panel, "Tick Marks");

        // Tick color row (moved from Bar-only section, now always visible)
        JPanel tickColorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tickColorRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        tickColorRow.setOpaque(false);
        tickColorRow.setMinimumSize(new Dimension(250, 25));
        tickColorRow.setPreferredSize(new Dimension(250, 25));
        tickColorRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel tickColorLabel = new JLabel("Tick Color:");
        tickColorLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        tickColorRow.add(tickColorLabel);

        JButton tickColorBtn = new JButton();
        tickColorBtn.setPreferredSize(new Dimension(30, 20));
        tickColorBtn.setBackground(timelineAxisTickColor);
        tickColorBtn.setToolTipText("Click to change tick color");
        tickColorBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Tick Color", timelineAxisTickColor);
            if (newColor != null) {
                timelineAxisTickColor = newColor;
                tickColorBtn.setBackground(timelineAxisTickColor);
                if (timelineDisplayPanel != null) timelineDisplayPanel.repaint();
            }
        });
        tickColorRow.add(tickColorBtn);

        panel.add(tickColorRow);
        panel.add(Box.createVerticalStrut(1));

        // Tick height row
        JPanel tickHeightRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tickHeightRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        tickHeightRow.setOpaque(false);
        tickHeightRow.setMinimumSize(new Dimension(250, 25));
        tickHeightRow.setPreferredSize(new Dimension(250, 25));
        tickHeightRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel tickHeightLbl = new JLabel("Tick Height:");
        tickHeightLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        tickHeightRow.add(tickHeightLbl);

        JSpinner tickHeightSpinner = new JSpinner(new SpinnerNumberModel(timelineAxisTickHeight, 5, 50, 1));
        tickHeightSpinner.setPreferredSize(new Dimension(50, 20));
        tickHeightSpinner.addChangeListener(e -> {
            timelineAxisTickHeight = (Integer) tickHeightSpinner.getValue();
            if (timelineDisplayPanel != null) timelineDisplayPanel.repaint();
        });
        tickHeightRow.add(tickHeightSpinner);

        panel.add(tickHeightRow);
        panel.add(Box.createVerticalStrut(1));

        // Tick width row
        JPanel tickWidthRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tickWidthRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        tickWidthRow.setOpaque(false);
        tickWidthRow.setMinimumSize(new Dimension(250, 25));
        tickWidthRow.setPreferredSize(new Dimension(250, 25));
        tickWidthRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel tickWidthLbl = new JLabel("Tick Width:");
        tickWidthLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        tickWidthRow.add(tickWidthLbl);

        JSpinner tickWidthSpinner = new JSpinner(new SpinnerNumberModel(timelineAxisTickWidth, 1, 10, 1));
        tickWidthSpinner.setPreferredSize(new Dimension(50, 20));
        tickWidthSpinner.addChangeListener(e -> {
            timelineAxisTickWidth = (Integer) tickWidthSpinner.getValue();
            if (timelineDisplayPanel != null) timelineDisplayPanel.repaint();
        });
        tickWidthRow.add(tickWidthSpinner);

        panel.add(tickWidthRow);
        panel.add(Box.createVerticalStrut(1));

        // Extend ticks checkbox row
        JPanel extendTicksRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        extendTicksRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        extendTicksRow.setOpaque(false);
        extendTicksRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        extendTicksCheckBox = new JCheckBox("Extend Ticks");
        extendTicksCheckBox.setFont(new Font("Arial", Font.PLAIN, 11));
        extendTicksCheckBox.setOpaque(false);
        extendTicksCheckBox.setSelected(extendTicks);
        extendTicksCheckBox.addActionListener(e -> {
            extendTicks = extendTicksCheckBox.isSelected();
            extendTicksOptionsPanel.setVisible(extendTicks);
            if (timelineDisplayPanel != null) timelineDisplayPanel.repaint();
        });
        extendTicksRow.add(extendTicksCheckBox);

        panel.add(extendTicksRow);

        // Extend ticks options panel
        extendTicksOptionsPanel = new JPanel();
        extendTicksOptionsPanel.setLayout(new BoxLayout(extendTicksOptionsPanel, BoxLayout.Y_AXIS));
        extendTicksOptionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        extendTicksOptionsPanel.setOpaque(false);
        extendTicksOptionsPanel.setVisible(extendTicks);

        // Extend ticks color row
        JPanel extendTicksColorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        extendTicksColorRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        extendTicksColorRow.setOpaque(false);
        extendTicksColorRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel extendTicksColorLabel = new JLabel("    Color:");
        extendTicksColorLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        extendTicksColorRow.add(extendTicksColorLabel);

        extendTicksColorBtn = new JButton();
        extendTicksColorBtn.setPreferredSize(new Dimension(30, 20));
        extendTicksColorBtn.setBackground(extendTicksColor);
        extendTicksColorBtn.setToolTipText("Click to change extended tick color");
        extendTicksColorBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Extended Tick Color", extendTicksColor);
            if (newColor != null) {
                extendTicksColor = newColor;
                extendTicksColorBtn.setBackground(extendTicksColor);
                if (timelineDisplayPanel != null) timelineDisplayPanel.repaint();
            }
        });
        extendTicksColorRow.add(extendTicksColorBtn);

        extendTicksOptionsPanel.add(extendTicksColorRow);

        // Extend ticks thickness row
        JPanel extendTicksThicknessRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        extendTicksThicknessRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        extendTicksThicknessRow.setOpaque(false);
        extendTicksThicknessRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel extendTicksThicknessLabel = new JLabel("    Thickness:");
        extendTicksThicknessLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        extendTicksThicknessRow.add(extendTicksThicknessLabel);

        extendTicksThicknessSpinner = new JSpinner(new SpinnerNumberModel(extendTicksThickness, 1, 5, 1));
        extendTicksThicknessSpinner.setPreferredSize(new Dimension(50, 20));
        extendTicksThicknessSpinner.addChangeListener(e -> {
            extendTicksThickness = (Integer) extendTicksThicknessSpinner.getValue();
            if (timelineDisplayPanel != null) timelineDisplayPanel.repaint();
        });
        extendTicksThicknessRow.add(extendTicksThicknessSpinner);

        extendTicksOptionsPanel.add(extendTicksThicknessRow);

        // Extend ticks line type row
        JPanel extendTicksLineTypeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        extendTicksLineTypeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        extendTicksLineTypeRow.setOpaque(false);
        extendTicksLineTypeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel extendTicksLineTypeLabel = new JLabel("    Line Type:");
        extendTicksLineTypeLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        extendTicksLineTypeRow.add(extendTicksLineTypeLabel);

        extendTicksLineTypeCombo = new JComboBox<>(LINE_TYPES);
        extendTicksLineTypeCombo.setPreferredSize(new Dimension(90, 20));
        extendTicksLineTypeCombo.setSelectedItem(extendTicksLineType);
        extendTicksLineTypeCombo.addActionListener(e -> {
            extendTicksLineType = (String) extendTicksLineTypeCombo.getSelectedItem();
            if (timelineDisplayPanel != null) timelineDisplayPanel.repaint();
        });
        extendTicksLineTypeRow.add(extendTicksLineTypeCombo);

        extendTicksOptionsPanel.add(extendTicksLineTypeRow);

        panel.add(extendTicksOptionsPanel);
        panel.add(Box.createVerticalStrut(8));

        // Date Labels section header
        addSectionHeader(panel, "Date Labels");

        // Date label color row
        JPanel dateLabelColorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dateLabelColorRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateLabelColorRow.setOpaque(false);
        dateLabelColorRow.setMinimumSize(new Dimension(250, 25));
        dateLabelColorRow.setPreferredSize(new Dimension(250, 25));
        dateLabelColorRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel dateLabelColorLabel = new JLabel("Color:");
        dateLabelColorLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        dateLabelColorRow.add(dateLabelColorLabel);

        axisDateColorBtn = new JButton();
        axisDateColorBtn.setPreferredSize(new Dimension(30, 20));
        axisDateColorBtn.setBackground(axisDateColor);
        axisDateColorBtn.setToolTipText("Click to change date label color");
        axisDateColorBtn.addActionListener(e -> chooseAxisDateColor());
        dateLabelColorRow.add(axisDateColorBtn);

        panel.add(dateLabelColorRow);
        panel.add(Box.createVerticalStrut(1));

        // Date label font family row
        JPanel dateLabelFontRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dateLabelFontRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateLabelFontRow.setOpaque(false);
        dateLabelFontRow.setMinimumSize(new Dimension(250, 25));
        dateLabelFontRow.setPreferredSize(new Dimension(250, 25));
        dateLabelFontRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel dateLabelFontLabel = new JLabel("Font:");
        dateLabelFontLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        dateLabelFontRow.add(dateLabelFontLabel);

        axisDateFontCombo = new JComboBox<>(FONT_FAMILIES);
        axisDateFontCombo.setPreferredSize(new Dimension(120, 20));
        axisDateFontCombo.addActionListener(e -> {
            axisDateFontFamily = (String) axisDateFontCombo.getSelectedItem();
            if (timelineDisplayPanel != null) timelineDisplayPanel.repaint();
        });
        dateLabelFontRow.add(axisDateFontCombo);

        panel.add(dateLabelFontRow);
        panel.add(Box.createVerticalStrut(1));

        // Date label font size row
        JPanel dateLabelSizeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dateLabelSizeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateLabelSizeRow.setOpaque(false);
        dateLabelSizeRow.setMinimumSize(new Dimension(250, 25));
        dateLabelSizeRow.setPreferredSize(new Dimension(250, 25));
        dateLabelSizeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel dateLabelSizeLabel = new JLabel("Size:");
        dateLabelSizeLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        dateLabelSizeRow.add(dateLabelSizeLabel);

        axisDateFontSizeSpinner = new JSpinner(new SpinnerNumberModel(axisDateFontSize, 6, 24, 1));
        axisDateFontSizeSpinner.setPreferredSize(new Dimension(50, 20));
        axisDateFontSizeSpinner.addChangeListener(e -> {
            axisDateFontSize = (Integer) axisDateFontSizeSpinner.getValue();
            if (timelineDisplayPanel != null) timelineDisplayPanel.repaint();
        });
        dateLabelSizeRow.add(axisDateFontSizeSpinner);

        panel.add(dateLabelSizeRow);
        panel.add(Box.createVerticalStrut(1));

        // Date label bold/italic row
        JPanel dateLabelStyleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dateLabelStyleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateLabelStyleRow.setOpaque(false);
        dateLabelStyleRow.setMinimumSize(new Dimension(250, 25));
        dateLabelStyleRow.setPreferredSize(new Dimension(250, 25));
        dateLabelStyleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel dateLabelStyleLabel = new JLabel("Style:");
        dateLabelStyleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        dateLabelStyleRow.add(dateLabelStyleLabel);

        // MS Word style Bold button
        axisDateBoldBtn = new JToggleButton("B") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(150, 150, 150));
                    g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(230, 230, 230));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(180, 180, 180));
                    g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                } else {
                    g2d.setColor(getBackground());
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Times New Roman", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("B")) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString("B", x, y);
            }
        };
        axisDateBoldBtn.setPreferredSize(new Dimension(28, 25));
        axisDateBoldBtn.setToolTipText("Bold");
        axisDateBoldBtn.setContentAreaFilled(false);
        axisDateBoldBtn.setBorderPainted(false);
        axisDateBoldBtn.setFocusPainted(false);
        axisDateBoldBtn.addActionListener(e -> {
            axisDateBold = axisDateBoldBtn.isSelected();
            if (timelineDisplayPanel != null) timelineDisplayPanel.repaint();
        });
        dateLabelStyleRow.add(axisDateBoldBtn);

        // MS Word style Italic button
        axisDateItalicBtn = new JToggleButton("I") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(150, 150, 150));
                    g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(230, 230, 230));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(180, 180, 180));
                    g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                } else {
                    g2d.setColor(getBackground());
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("I")) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString("I", x, y);
            }
        };
        axisDateItalicBtn.setPreferredSize(new Dimension(28, 25));
        axisDateItalicBtn.setToolTipText("Italic");
        axisDateItalicBtn.setContentAreaFilled(false);
        axisDateItalicBtn.setBorderPainted(false);
        axisDateItalicBtn.setFocusPainted(false);
        axisDateItalicBtn.addActionListener(e -> {
            axisDateItalic = axisDateItalicBtn.isSelected();
            if (timelineDisplayPanel != null) timelineDisplayPanel.repaint();
        });
        dateLabelStyleRow.add(axisDateItalicBtn);

        panel.add(dateLabelStyleRow);
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
        saveState();
        events.clear();
        tasks.clear();
        milestones.clear();
        layerOrder.clear();
        selectTask(-1);
        selectMilestone(-1);
        refreshTimeline();
    }

    private void saveState() {
        TimelineState state = new TimelineState(tasks, milestones, layerOrder, events);
        undoStack.push(state);
        if (undoStack.size() > MAX_UNDO_LEVELS) {
            undoStack.removeLast();
        }
        redoStack.clear();
        updateUndoRedoButtons();
    }

    private void undo() {
        if (undoStack.isEmpty()) return;
        // Save current state to redo stack
        TimelineState currentState = new TimelineState(tasks, milestones, layerOrder, events);
        redoStack.push(currentState);
        // Restore previous state
        TimelineState state = undoStack.pop();
        restoreState(state);
        updateUndoRedoButtons();
    }

    private void redo() {
        if (redoStack.isEmpty()) return;
        // Save current state to undo stack
        TimelineState currentState = new TimelineState(tasks, milestones, layerOrder, events);
        undoStack.push(currentState);
        // Restore redo state
        TimelineState state = redoStack.pop();
        restoreState(state);
        updateUndoRedoButtons();
    }

    private void restoreState(TimelineState state) {
        tasks.clear();
        tasks.addAll(state.tasks);
        milestones.clear();
        milestones.addAll(state.milestones);
        layerOrder.clear();
        layerOrder.addAll(state.layerOrder);
        events.clear();
        events.addAll(state.events);
        selectTask(-1);
        selectMilestone(-1);
        refreshTimeline();
    }

    private void updateUndoRedoButtons() {
        undoBtn.setEnabled(!undoStack.isEmpty());
        redoBtn.setEnabled(!redoStack.isEmpty());
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
            updateSpreadsheet();
        } catch (Exception e) {
            showWarning("Please enter valid dates in YYYY-MM-DD format.");
        }
    }

    private void toggleSpreadsheetPanel() {
        if (spreadsheetVisible) {
            updateSpreadsheet();
            centerSplitPane.setDividerLocation(lastSpreadsheetDividerLocation > 50 ? lastSpreadsheetDividerLocation : 250);
        } else {
            if (centerSplitPane.getDividerLocation() > 50) {
                lastSpreadsheetDividerLocation = centerSplitPane.getDividerLocation();
            }
            centerSplitPane.setDividerLocation(0);
        }
    }

    private void updateSpreadsheet() {
        if (spreadsheetTableModel == null) return;
        
        // Save current data from table to map
        saveSpreadsheetData();
        
        spreadsheetTableModel.setRowCount(0);
        spreadsheetRowOrder.clear();
        
        // Create list of items with their Y positions for sorting
        java.util.List<Object[]> itemsWithY = new java.util.ArrayList<>();
        
        for (int i = 0; i < layerOrder.size(); i++) {
            Object item = layerOrder.get(i);
            int yPos = 0;
            
            if (item instanceof TimelineTask) {
                TimelineTask task = (TimelineTask) item;
                if (task.yPosition >= 0) {
                    yPos = task.yPosition;
                } else {
                    yPos = "Top".equals(timelineAxisPosition) ? 100 : 45;
                    for (int j = layerOrder.size() - 1; j > i; j--) {
                        Object other = layerOrder.get(j);
                        if (other instanceof TimelineTask && ((TimelineTask) other).yPosition < 0) {
                            yPos += ((TimelineTask) other).height + 5;
                        }
                    }
                }
            } else if (item instanceof TimelineMilestone) {
                TimelineMilestone ms = (TimelineMilestone) item;
                yPos = ms.yPosition >= 0 ? ms.yPosition : 45;
            }
            
            itemsWithY.add(new Object[]{yPos, item});
        }
        
        // Sort by Y position (top to bottom)
        itemsWithY.sort((a, b) -> Integer.compare((Integer) a[0], (Integer) b[0]));
        
        // Add sorted items to table with preserved data
        for (Object[] entry : itemsWithY) {
            Object item = entry[1];
            String name = "";
            if (item instanceof TimelineTask) {
                name = ((TimelineTask) item).name;
            } else if (item instanceof TimelineMilestone) {
                name = ((TimelineMilestone) item).name;
            }
            
            spreadsheetRowOrder.add(item);
            // Build row data based on column headers
            Object[] rowData = new Object[spreadsheetTable.getColumnCount()];
            String[] savedData = spreadsheetData.get(item);
            for (int col = 0; col < spreadsheetTable.getColumnCount(); col++) {
                String header = spreadsheetTable.getColumnModel().getColumn(col).getHeaderValue().toString();
                if ("Name".equals(header)) {
                    rowData[col] = name;
                } else if ("Start Date".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).startDate;
                    else if (item instanceof TimelineMilestone) rowData[col] = ((TimelineMilestone) item).date;
                    else rowData[col] = "";
                } else if ("End Date".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).endDate;
                    else rowData[col] = "";
                } else if ("Duration".equals(header)) {
                    if (item instanceof TimelineTask) {
                        try {
                            LocalDate start = LocalDate.parse(((TimelineTask) item).startDate, DATE_FORMAT);
                            LocalDate end = LocalDate.parse(((TimelineTask) item).endDate, DATE_FORMAT);
                            rowData[col] = String.valueOf(java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1);
                        } catch (Exception ex) { rowData[col] = ""; }
                    } else { rowData[col] = "1"; }
                } else if ("Center Text".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).centerText;
                    else if (item instanceof TimelineMilestone) rowData[col] = ((TimelineMilestone) item).centerText;
                    else rowData[col] = "";
                } else if ("Above Text".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).aboveText;
                    else if (item instanceof TimelineMilestone) rowData[col] = ((TimelineMilestone) item).aboveText;
                    else rowData[col] = "";
                } else if ("Underneath Text".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).underneathText;
                    else if (item instanceof TimelineMilestone) rowData[col] = ((TimelineMilestone) item).underneathText;
                    else rowData[col] = "";
                } else if ("Front Text".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).frontText;
                    else if (item instanceof TimelineMilestone) rowData[col] = ((TimelineMilestone) item).frontText;
                    else rowData[col] = "";
                } else if ("Behind Text".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).behindText;
                    else if (item instanceof TimelineMilestone) rowData[col] = ((TimelineMilestone) item).behindText;
                    else rowData[col] = "";
                } else {
                    // Use saved data for other columns
                    if (savedData != null && col > 0 && col <= savedData.length) {
                        rowData[col] = savedData[col - 1];
                    } else {
                        rowData[col] = "";
                    }
                }
            }
            spreadsheetTableModel.addRow(rowData);
        }

        // Restore row heights
        for (int row = 0; row < spreadsheetRowOrder.size(); row++) {
            Object item = spreadsheetRowOrder.get(row);
            Integer savedHeight = spreadsheetRowHeights.get(item);
            if (savedHeight != null) {
                spreadsheetTable.setRowHeight(row, savedHeight);
            }
        }

        // Auto-fit first 3 columns to content width
        FontMetrics fm = spreadsheetTable.getFontMetrics(spreadsheetTable.getFont());
        String[] headers = {"Name", "Start Date", "End Date"};
        int[] padding = {20, 16, 16};
        for (int col = 0; col < 3; col++) {
            int maxWidth = fm.stringWidth(headers[col]) + padding[col];
            for (int row = 0; row < spreadsheetTableModel.getRowCount(); row++) {
                Object val = spreadsheetTableModel.getValueAt(row, col);
                if (val != null) {
                    int width = fm.stringWidth(val.toString()) + padding[col];
                    if (width > maxWidth) maxWidth = width;
                }
            }
            spreadsheetTable.getColumnModel().getColumn(col).setPreferredWidth(maxWidth);
        }
    }
    
    private void saveSpreadsheetData() {
        if (spreadsheetTableModel == null) return;
        
        // Save data for each row using the current spreadsheetRowOrder
        for (int row = 0; row < spreadsheetTableModel.getRowCount() && row < spreadsheetRowOrder.size(); row++) {
            Object item = spreadsheetRowOrder.get(row);
            String[] data = new String[4];
            for (int col = 1; col < 5; col++) {
                Object val = spreadsheetTableModel.getValueAt(row, col);
                data[col - 1] = val != null ? val.toString() : "";
            }
            spreadsheetData.put(item, data);
            spreadsheetRowHeights.put(item, spreadsheetTable.getRowHeight(row));
        }
    }

    private void showSkinsDialog() {
        JDialog dialog = new JDialog(this, "Skins", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        // === FORMAT TAB ===
        JPanel formatTab = new JPanel(new GridBagLayout());
        formatTab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(8, 8, 8, 8);
        fgbc.anchor = GridBagConstraints.WEST;
        fgbc.fill = GridBagConstraints.NONE;

        // === FORMAT HEADING ROW ===
        fgbc.gridx = 0; fgbc.gridy = 0;
        formatTab.add(new JLabel("Heading:"), fgbc);

        // Format Heading color button with gradient support
        JButton formatHeadingBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (formatHeaderUseGradient && formatHeaderGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[formatHeaderGradientStops.size()];
                    Color[] colors = new Color[formatHeaderGradientStops.size()];
                    for (int i = 0; i < formatHeaderGradientStops.size(); i++) {
                        float[] stop = formatHeaderGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        formatHeadingBtn.setPreferredSize(new Dimension(60, 25));
        formatHeadingBtn.setBackground(formatHeaderColor);
        final JButton finalFormatHeadingBtn = formatHeadingBtn;
        formatHeadingBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Format Heading Color", formatHeaderColor, color -> {
                formatHeaderColor = color;
                formatHeaderUseGradient = false;
                finalFormatHeadingBtn.setBackground(color);
                finalFormatHeadingBtn.repaint();
                applyFormatPanelColors();
            });
            if (newColor != null) {
                formatHeaderColor = newColor;
                formatHeaderUseGradient = false;
                finalFormatHeadingBtn.setBackground(newColor);
                finalFormatHeadingBtn.repaint();
                applyFormatPanelColors();
            }
        });
        fgbc.gridx = 1; fgbc.gridy = 0;
        formatTab.add(formatHeadingBtn, fgbc);

        // Format Heading gradient arrow button
        JButton formatHeadingGradientArrowBtn = new JButton("\u25BC");
        formatHeadingGradientArrowBtn.setPreferredSize(new Dimension(20, 25));
        formatHeadingGradientArrowBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        formatHeadingGradientArrowBtn.setMargin(new Insets(0, 0, 0, 0));
        formatHeadingGradientArrowBtn.setToolTipText("Gradient options");
        JPopupMenu formatHeadingGradientMenu = new JPopupMenu();
        JMenuItem formatHeadingGradientMenuItem = new JMenuItem("Gradient...");
        formatHeadingGradientMenuItem.addActionListener(ev -> {
            showGradientDialog("formatHeader");
            finalFormatHeadingBtn.repaint();
        });
        formatHeadingGradientMenu.add(formatHeadingGradientMenuItem);
        formatHeadingGradientArrowBtn.addActionListener(ev -> {
            formatHeadingGradientMenu.show(formatHeadingGradientArrowBtn, 0, formatHeadingGradientArrowBtn.getHeight());
        });
        fgbc.gridx = 2; fgbc.gridy = 0;
        formatTab.add(formatHeadingGradientArrowBtn, fgbc);

        // === FORMAT INTERIOR ROW ===
        fgbc.gridx = 0; fgbc.gridy = 1;
        formatTab.add(new JLabel("Interior:"), fgbc);

        // Format Interior color button with gradient support
        JButton formatInteriorBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (formatUseGradient && formatGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[formatGradientStops.size()];
                    Color[] colors = new Color[formatGradientStops.size()];
                    for (int i = 0; i < formatGradientStops.size(); i++) {
                        float[] stop = formatGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        formatInteriorBtn.setPreferredSize(new Dimension(60, 25));
        formatInteriorBtn.setBackground(formatInteriorColor);
        final JButton finalFormatInteriorBtn = formatInteriorBtn;
        formatInteriorBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Format Interior Color", formatInteriorColor, color -> {
                formatInteriorColor = color;
                formatUseGradient = false;
                finalFormatInteriorBtn.setBackground(color);
                finalFormatInteriorBtn.repaint();
                applyFormatPanelColors();
            });
            if (newColor != null) {
                formatInteriorColor = newColor;
                formatUseGradient = false;
                finalFormatInteriorBtn.setBackground(newColor);
                finalFormatInteriorBtn.repaint();
                applyFormatPanelColors();
            }
        });
        fgbc.gridx = 1; fgbc.gridy = 1;
        formatTab.add(formatInteriorBtn, fgbc);

        // Format Interior gradient arrow button
        JButton formatInteriorGradientArrowBtn = new JButton("\u25BC");
        formatInteriorGradientArrowBtn.setPreferredSize(new Dimension(20, 25));
        formatInteriorGradientArrowBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        formatInteriorGradientArrowBtn.setMargin(new Insets(0, 0, 0, 0));
        formatInteriorGradientArrowBtn.setToolTipText("Gradient options");
        JPopupMenu formatInteriorGradientMenu = new JPopupMenu();
        JMenuItem formatInteriorGradientMenuItem = new JMenuItem("Gradient...");
        formatInteriorGradientMenuItem.addActionListener(ev -> {
            showGradientDialog("format");
            finalFormatInteriorBtn.repaint();
        });
        formatInteriorGradientMenu.add(formatInteriorGradientMenuItem);
        formatInteriorGradientArrowBtn.addActionListener(ev -> {
            formatInteriorGradientMenu.show(formatInteriorGradientArrowBtn, 0, formatInteriorGradientArrowBtn.getHeight());
        });
        fgbc.gridx = 2; fgbc.gridy = 1;
        formatTab.add(formatInteriorGradientArrowBtn, fgbc);

        // === FORMAT SELECTED TAB ROW ===
        fgbc.gridx = 0; fgbc.gridy = 2;
        formatTab.add(new JLabel("Selected Tab:"), fgbc);

        // Format Selected Tab color button with gradient support
        JButton formatSelectedTabBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (formatSelectedTabUseGradient && formatSelectedTabGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[formatSelectedTabGradientStops.size()];
                    Color[] colors = new Color[formatSelectedTabGradientStops.size()];
                    for (int i = 0; i < formatSelectedTabGradientStops.size(); i++) {
                        float[] stop = formatSelectedTabGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        formatSelectedTabBtn.setPreferredSize(new Dimension(60, 25));
        formatSelectedTabBtn.setBackground(formatSelectedTabColor);
        final JButton finalFormatSelectedTabBtn = formatSelectedTabBtn;
        formatSelectedTabBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Selected Tab Color", formatSelectedTabColor, color -> {
                formatSelectedTabColor = color;
                formatSelectedTabUseGradient = false;
                finalFormatSelectedTabBtn.setBackground(color);
                finalFormatSelectedTabBtn.repaint();
                applyFormatPanelColors();
            });
            if (newColor != null) {
                formatSelectedTabColor = newColor;
                formatSelectedTabUseGradient = false;
                finalFormatSelectedTabBtn.setBackground(newColor);
                finalFormatSelectedTabBtn.repaint();
                applyFormatPanelColors();
            }
        });
        fgbc.gridx = 1; fgbc.gridy = 2;
        formatTab.add(formatSelectedTabBtn, fgbc);

        // Format Selected Tab gradient arrow button
        JButton formatSelectedTabGradientArrowBtn = new JButton("\u25BC");
        formatSelectedTabGradientArrowBtn.setPreferredSize(new Dimension(20, 25));
        formatSelectedTabGradientArrowBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        formatSelectedTabGradientArrowBtn.setMargin(new Insets(0, 0, 0, 0));
        formatSelectedTabGradientArrowBtn.setToolTipText("Gradient options");
        JPopupMenu formatSelectedTabGradientMenu = new JPopupMenu();
        JMenuItem formatSelectedTabGradientMenuItem = new JMenuItem("Gradient...");
        formatSelectedTabGradientMenuItem.addActionListener(ev -> {
            showGradientDialog("formatSelectedTab");
            finalFormatSelectedTabBtn.repaint();
        });
        formatSelectedTabGradientMenu.add(formatSelectedTabGradientMenuItem);
        formatSelectedTabGradientArrowBtn.addActionListener(ev -> {
            formatSelectedTabGradientMenu.show(formatSelectedTabGradientArrowBtn, 0, formatSelectedTabGradientArrowBtn.getHeight());
        });
        fgbc.gridx = 2; fgbc.gridy = 2;
        formatTab.add(formatSelectedTabGradientArrowBtn, fgbc);

        // === FORMAT DESELECTED TAB ROW ===
        fgbc.gridx = 0; fgbc.gridy = 3;
        formatTab.add(new JLabel("Deselected Tab:"), fgbc);

        // Format Deselected Tab color button with gradient support
        JButton formatTabsBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (formatTabUseGradient && formatTabGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[formatTabGradientStops.size()];
                    Color[] colors = new Color[formatTabGradientStops.size()];
                    for (int i = 0; i < formatTabGradientStops.size(); i++) {
                        float[] stop = formatTabGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        formatTabsBtn.setPreferredSize(new Dimension(60, 25));
        formatTabsBtn.setBackground(formatTabColor);
        final JButton finalFormatTabsBtn = formatTabsBtn;
        formatTabsBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Deselected Tab Color", formatTabColor, color -> {
                formatTabColor = color;
                formatTabUseGradient = false;
                finalFormatTabsBtn.setBackground(color);
                finalFormatTabsBtn.repaint();
                applyFormatPanelColors();
            });
            if (newColor != null) {
                formatTabColor = newColor;
                formatTabUseGradient = false;
                finalFormatTabsBtn.setBackground(newColor);
                finalFormatTabsBtn.repaint();
                applyFormatPanelColors();
            }
        });
        fgbc.gridx = 1; fgbc.gridy = 3;
        formatTab.add(formatTabsBtn, fgbc);

        // Format Deselected Tab gradient arrow button
        JButton formatTabsGradientArrowBtn = new JButton("\u25BC");
        formatTabsGradientArrowBtn.setPreferredSize(new Dimension(20, 25));
        formatTabsGradientArrowBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        formatTabsGradientArrowBtn.setMargin(new Insets(0, 0, 0, 0));
        formatTabsGradientArrowBtn.setToolTipText("Gradient options");
        JPopupMenu formatTabsGradientMenu = new JPopupMenu();
        JMenuItem formatTabsGradientMenuItem = new JMenuItem("Gradient...");
        formatTabsGradientMenuItem.addActionListener(ev -> {
            showGradientDialog("formatTab");
            finalFormatTabsBtn.repaint();
        });
        formatTabsGradientMenu.add(formatTabsGradientMenuItem);
        formatTabsGradientArrowBtn.addActionListener(ev -> {
            formatTabsGradientMenu.show(formatTabsGradientArrowBtn, 0, formatTabsGradientArrowBtn.getHeight());
        });
        fgbc.gridx = 2; fgbc.gridy = 3;
        formatTab.add(formatTabsGradientArrowBtn, fgbc);

        // === FORMAT TAB CONTENT ROW ===
        fgbc.gridx = 0; fgbc.gridy = 4;
        fgbc.gridwidth = 1;
        fgbc.weighty = 0;
        fgbc.fill = GridBagConstraints.NONE;
        formatTab.add(new JLabel("Tab Content:"), fgbc);

        // Format Tab Content color button with gradient support
        JButton formatTabContentBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (formatTabContentUseGradient && formatTabContentGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[formatTabContentGradientStops.size()];
                    Color[] colors = new Color[formatTabContentGradientStops.size()];
                    for (int i = 0; i < formatTabContentGradientStops.size(); i++) {
                        float[] stop = formatTabContentGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        formatTabContentBtn.setPreferredSize(new Dimension(60, 25));
        formatTabContentBtn.setBackground(formatTabContentColor);
        final JButton finalFormatTabContentBtn = formatTabContentBtn;
        formatTabContentBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Tab Content Color", formatTabContentColor, color -> {
                formatTabContentColor = color;
                formatTabContentUseGradient = false;
                finalFormatTabContentBtn.setBackground(color);
                finalFormatTabContentBtn.repaint();
                applyFormatPanelColors();
            });
            if (newColor != null) {
                formatTabContentColor = newColor;
                formatTabContentUseGradient = false;
                finalFormatTabContentBtn.setBackground(newColor);
                finalFormatTabContentBtn.repaint();
                applyFormatPanelColors();
            }
        });
        fgbc.gridx = 1; fgbc.gridy = 4;
        formatTab.add(formatTabContentBtn, fgbc);

        // Format Tab Content gradient arrow button
        JButton formatTabContentGradientArrowBtn = new JButton("\u25BC");
        formatTabContentGradientArrowBtn.setPreferredSize(new Dimension(20, 25));
        formatTabContentGradientArrowBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        formatTabContentGradientArrowBtn.setMargin(new Insets(0, 0, 0, 0));
        formatTabContentGradientArrowBtn.setToolTipText("Gradient options");
        JPopupMenu formatTabContentGradientMenu = new JPopupMenu();
        JMenuItem formatTabContentGradientMenuItem = new JMenuItem("Gradient...");
        formatTabContentGradientMenuItem.addActionListener(ev -> {
            showGradientDialog("formatTabContent");
            finalFormatTabContentBtn.repaint();
        });
        formatTabContentGradientMenu.add(formatTabContentGradientMenuItem);
        formatTabContentGradientArrowBtn.addActionListener(ev -> {
            formatTabContentGradientMenu.show(formatTabContentGradientArrowBtn, 0, formatTabContentGradientArrowBtn.getHeight());
        });
        fgbc.gridx = 2; fgbc.gridy = 4;
        formatTab.add(formatTabContentGradientArrowBtn, fgbc);

        // Add filler to push content to top
        fgbc.gridx = 0; fgbc.gridy = 5;
        fgbc.gridwidth = 3;
        fgbc.weighty = 1.0;
        fgbc.fill = GridBagConstraints.BOTH;
        formatTab.add(new JPanel(), fgbc);

        // === RIGHT PANEL TAB ===
        JPanel rightPanelSkinsTab = new JPanel(new GridBagLayout());
        rightPanelSkinsTab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints rpgbc = new GridBagConstraints();
        rpgbc.insets = new Insets(8, 8, 8, 8);
        rpgbc.anchor = GridBagConstraints.WEST;
        rpgbc.fill = GridBagConstraints.NONE;

        // === RIGHT PANEL HEADER ROW ===
        rpgbc.gridx = 0; rpgbc.gridy = 0;
        rightPanelSkinsTab.add(new JLabel("Header:"), rpgbc);

        JButton skinsLayersHeaderBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (layersHeaderUseGradient && layersHeaderGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[layersHeaderGradientStops.size()];
                    Color[] colors = new Color[layersHeaderGradientStops.size()];
                    for (int i = 0; i < layersHeaderGradientStops.size(); i++) {
                        float[] stop = layersHeaderGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        skinsLayersHeaderBtn.setPreferredSize(new Dimension(60, 25));
        skinsLayersHeaderBtn.setBackground(layersHeaderColor);
        final JButton finalSkinsLayersHeaderBtn = skinsLayersHeaderBtn;
        skinsLayersHeaderBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Right Panel Header Color", layersHeaderColor, color -> {
                layersHeaderColor = color;
                layersHeaderUseGradient = false;
                finalSkinsLayersHeaderBtn.setBackground(color);
                finalSkinsLayersHeaderBtn.repaint();
                applyLayersPanelColors();
            });
            if (newColor != null) {
                layersHeaderColor = newColor;
                layersHeaderUseGradient = false;
                finalSkinsLayersHeaderBtn.setBackground(newColor);
                finalSkinsLayersHeaderBtn.repaint();
                applyLayersPanelColors();
            }
        });
        rpgbc.gridx = 1; rpgbc.gridy = 0;
        rightPanelSkinsTab.add(skinsLayersHeaderBtn, rpgbc);

        JButton skinsLayersHeaderGradientBtn = new JButton("\u25BC");
        skinsLayersHeaderGradientBtn.setPreferredSize(new Dimension(20, 25));
        skinsLayersHeaderGradientBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        skinsLayersHeaderGradientBtn.setMargin(new Insets(0, 0, 0, 0));
        skinsLayersHeaderGradientBtn.setToolTipText("Gradient options");
        JPopupMenu skinsLayersHeaderGradientMenu = new JPopupMenu();
        JMenuItem skinsLayersHeaderGradientMenuItem = new JMenuItem("Gradient...");
        skinsLayersHeaderGradientMenuItem.addActionListener(ev -> {
            showGradientDialog("layersHeader");
            finalSkinsLayersHeaderBtn.repaint();
        });
        skinsLayersHeaderGradientMenu.add(skinsLayersHeaderGradientMenuItem);
        skinsLayersHeaderGradientBtn.addActionListener(ev -> {
            skinsLayersHeaderGradientMenu.show(skinsLayersHeaderGradientBtn, 0, skinsLayersHeaderGradientBtn.getHeight());
        });
        rpgbc.gridx = 2; rpgbc.gridy = 0;
        rightPanelSkinsTab.add(skinsLayersHeaderGradientBtn, rpgbc);

        // === RIGHT PANEL INTERIOR ROW ===
        rpgbc.gridx = 0; rpgbc.gridy = 1;
        rightPanelSkinsTab.add(new JLabel("Interior:"), rpgbc);

        JButton skinsLayersInteriorBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (layersUseGradient && layersGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[layersGradientStops.size()];
                    Color[] colors = new Color[layersGradientStops.size()];
                    for (int i = 0; i < layersGradientStops.size(); i++) {
                        float[] stop = layersGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        skinsLayersInteriorBtn.setPreferredSize(new Dimension(60, 25));
        skinsLayersInteriorBtn.setBackground(layersInteriorColor);
        final JButton finalSkinsLayersInteriorBtn = skinsLayersInteriorBtn;
        skinsLayersInteriorBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Right Panel Interior Color", layersInteriorColor, color -> {
                layersInteriorColor = color;
                layersUseGradient = false;
                finalSkinsLayersInteriorBtn.setBackground(color);
                finalSkinsLayersInteriorBtn.repaint();
                applyLayersPanelColors();
            });
            if (newColor != null) {
                layersInteriorColor = newColor;
                layersUseGradient = false;
                finalSkinsLayersInteriorBtn.setBackground(newColor);
                finalSkinsLayersInteriorBtn.repaint();
                applyLayersPanelColors();
            }
        });
        rpgbc.gridx = 1; rpgbc.gridy = 1;
        rightPanelSkinsTab.add(skinsLayersInteriorBtn, rpgbc);

        JButton skinsLayersInteriorGradientBtn = new JButton("\u25BC");
        skinsLayersInteriorGradientBtn.setPreferredSize(new Dimension(20, 25));
        skinsLayersInteriorGradientBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        skinsLayersInteriorGradientBtn.setMargin(new Insets(0, 0, 0, 0));
        skinsLayersInteriorGradientBtn.setToolTipText("Gradient options");
        JPopupMenu skinsLayersInteriorGradientMenu = new JPopupMenu();
        JMenuItem skinsLayersInteriorGradientMenuItem = new JMenuItem("Gradient...");
        skinsLayersInteriorGradientMenuItem.addActionListener(ev -> {
            showGradientDialog("layers");
            finalSkinsLayersInteriorBtn.repaint();
        });
        skinsLayersInteriorGradientMenu.add(skinsLayersInteriorGradientMenuItem);
        skinsLayersInteriorGradientBtn.addActionListener(ev -> {
            skinsLayersInteriorGradientMenu.show(skinsLayersInteriorGradientBtn, 0, skinsLayersInteriorGradientBtn.getHeight());
        });
        rpgbc.gridx = 2; rpgbc.gridy = 1;
        rightPanelSkinsTab.add(skinsLayersInteriorGradientBtn, rpgbc);

        // Separator row (border between tabs and content)
        rpgbc.gridx = 0; rpgbc.gridy = 2;
        rpgbc.gridwidth = 1;
        rightPanelSkinsTab.add(new JLabel("Separator:"), rpgbc);
        JButton separatorBtn = new JButton();
        separatorBtn.setPreferredSize(new Dimension(60, 25));
        separatorBtn.setBackground(rightTabbedBorderColor);
        separatorBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Separator Color", rightTabbedBorderColor, color -> {
                rightTabbedBorderColor = color;
                separatorBtn.setBackground(color);
                applyRightTabbedPaneColors();
            });
            if (newColor != null) {
                rightTabbedBorderColor = newColor;
                separatorBtn.setBackground(newColor);
                applyRightTabbedPaneColors();
            }
        });
        rpgbc.gridx = 1;
        rightPanelSkinsTab.add(separatorBtn, rpgbc);

        // Visible checkbox for separator
        JCheckBox separatorVisibleChk = new JCheckBox("Visible");
        separatorVisibleChk.setSelected(rightTabbedBorderVisible);
        separatorVisibleChk.addActionListener(e -> {
            rightTabbedBorderVisible = separatorVisibleChk.isSelected();
            applyRightTabbedPaneColors();
        });
        rpgbc.gridx = 2;
        rightPanelSkinsTab.add(separatorVisibleChk, rpgbc);

        // Add filler to push content to top
        rpgbc.gridx = 0; rpgbc.gridy = 3;
        rpgbc.gridwidth = 3;
        rpgbc.weighty = 1.0;
        rpgbc.fill = GridBagConstraints.BOTH;
        rightPanelSkinsTab.add(new JPanel(), rpgbc);

        // === LAYERS TAB ===
        JPanel skinsLayersTab = new JPanel(new GridBagLayout());
        skinsLayersTab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints slgbc = new GridBagConstraints();
        slgbc.insets = new Insets(8, 8, 8, 8);
        slgbc.anchor = GridBagConstraints.WEST;
        slgbc.fill = GridBagConstraints.NONE;

        // === LAYERS BACKGROUND ROW ===
        slgbc.gridx = 0; slgbc.gridy = 0;
        skinsLayersTab.add(new JLabel("Background:"), slgbc);

        JButton skinsLayersListBgBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (layersListBgUseGradient && layersListBgGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[layersListBgGradientStops.size()];
                    Color[] colors = new Color[layersListBgGradientStops.size()];
                    for (int i = 0; i < layersListBgGradientStops.size(); i++) {
                        float[] stop = layersListBgGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        skinsLayersListBgBtn.setPreferredSize(new Dimension(60, 25));
        skinsLayersListBgBtn.setBackground(layersListBgColor);
        final JButton finalSkinsLayersListBgBtn = skinsLayersListBgBtn;
        skinsLayersListBgBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Layers Background Color", layersListBgColor, color -> {
                layersListBgColor = color;
                layersListBgUseGradient = false;
                finalSkinsLayersListBgBtn.setBackground(color);
                finalSkinsLayersListBgBtn.repaint();
                applyLayersListColors();
            });
            if (newColor != null) {
                layersListBgColor = newColor;
                layersListBgUseGradient = false;
                finalSkinsLayersListBgBtn.setBackground(newColor);
                finalSkinsLayersListBgBtn.repaint();
                applyLayersListColors();
            }
        });
        slgbc.gridx = 1; slgbc.gridy = 0;
        skinsLayersTab.add(skinsLayersListBgBtn, slgbc);

        JButton skinsLayersListBgGradientBtn = new JButton("\u25BC");
        skinsLayersListBgGradientBtn.setPreferredSize(new Dimension(20, 25));
        skinsLayersListBgGradientBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        skinsLayersListBgGradientBtn.setMargin(new Insets(0, 0, 0, 0));
        skinsLayersListBgGradientBtn.setToolTipText("Gradient options");
        JPopupMenu skinsLayersListBgGradientMenu = new JPopupMenu();
        JMenuItem skinsLayersListBgGradientMenuItem = new JMenuItem("Gradient...");
        skinsLayersListBgGradientMenuItem.addActionListener(ev -> {
            showGradientDialog("layersListBg");
            finalSkinsLayersListBgBtn.repaint();
        });
        skinsLayersListBgGradientMenu.add(skinsLayersListBgGradientMenuItem);
        skinsLayersListBgGradientBtn.addActionListener(ev -> {
            skinsLayersListBgGradientMenu.show(skinsLayersListBgGradientBtn, 0, skinsLayersListBgGradientBtn.getHeight());
        });
        slgbc.gridx = 2; slgbc.gridy = 0;
        skinsLayersTab.add(skinsLayersListBgGradientBtn, slgbc);

        // === LAYERS TASK ROW ===
        slgbc.gridx = 0; slgbc.gridy = 1;
        slgbc.gridwidth = 1;
        slgbc.weighty = 0;
        slgbc.fill = GridBagConstraints.NONE;
        skinsLayersTab.add(new JLabel("Task:"), slgbc);

        JButton skinsLayersTaskBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (layersTaskUseGradient && layersTaskGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[layersTaskGradientStops.size()];
                    Color[] colors = new Color[layersTaskGradientStops.size()];
                    for (int i = 0; i < layersTaskGradientStops.size(); i++) {
                        float[] stop = layersTaskGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        skinsLayersTaskBtn.setPreferredSize(new Dimension(60, 25));
        skinsLayersTaskBtn.setBackground(layersTaskColor);
        final JButton finalSkinsLayersTaskBtn = skinsLayersTaskBtn;
        skinsLayersTaskBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Task Item Color", layersTaskColor, color -> {
                layersTaskColor = color;
                layersTaskUseGradient = false;
                finalSkinsLayersTaskBtn.setBackground(color);
                finalSkinsLayersTaskBtn.repaint();
                applyLayersListColors();
            });
            if (newColor != null) {
                layersTaskColor = newColor;
                layersTaskUseGradient = false;
                finalSkinsLayersTaskBtn.setBackground(newColor);
                finalSkinsLayersTaskBtn.repaint();
                applyLayersListColors();
            }
        });
        slgbc.gridx = 1; slgbc.gridy = 1;
        skinsLayersTab.add(skinsLayersTaskBtn, slgbc);

        JButton skinsLayersTaskGradientBtn = new JButton("\u25BC");
        skinsLayersTaskGradientBtn.setPreferredSize(new Dimension(20, 25));
        skinsLayersTaskGradientBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        skinsLayersTaskGradientBtn.setMargin(new Insets(0, 0, 0, 0));
        skinsLayersTaskGradientBtn.setToolTipText("Gradient options");
        JPopupMenu skinsLayersTaskGradientMenu = new JPopupMenu();
        JMenuItem skinsLayersTaskGradientMenuItem = new JMenuItem("Gradient...");
        skinsLayersTaskGradientMenuItem.addActionListener(ev -> {
            showGradientDialog("layersTask");
            finalSkinsLayersTaskBtn.repaint();
        });
        skinsLayersTaskGradientMenu.add(skinsLayersTaskGradientMenuItem);
        skinsLayersTaskGradientBtn.addActionListener(ev -> {
            skinsLayersTaskGradientMenu.show(skinsLayersTaskGradientBtn, 0, skinsLayersTaskGradientBtn.getHeight());
        });
        slgbc.gridx = 2; slgbc.gridy = 1;
        skinsLayersTab.add(skinsLayersTaskGradientBtn, slgbc);

        // Add filler to push content to top
        slgbc.gridx = 0; slgbc.gridy = 2;
        slgbc.gridwidth = 3;
        slgbc.weighty = 1.0;
        slgbc.fill = GridBagConstraints.BOTH;
        skinsLayersTab.add(new JPanel(), slgbc);

        // === TIME AXIS TAB ===
        JPanel timeAxisSkinsTab = new JPanel(new GridBagLayout());
        timeAxisSkinsTab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints tagbc = new GridBagConstraints();
        tagbc.insets = new Insets(8, 8, 8, 8);
        tagbc.anchor = GridBagConstraints.WEST;
        tagbc.fill = GridBagConstraints.NONE;

        // === TIME AXIS INTERIOR ROW ===
        tagbc.gridx = 0; tagbc.gridy = 0;
        timeAxisSkinsTab.add(new JLabel("Interior:"), tagbc);

        JButton skinsSettingsInteriorBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (settingsUseGradient && settingsGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[settingsGradientStops.size()];
                    Color[] colors = new Color[settingsGradientStops.size()];
                    for (int i = 0; i < settingsGradientStops.size(); i++) {
                        float[] stop = settingsGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        skinsSettingsInteriorBtn.setPreferredSize(new Dimension(60, 25));
        skinsSettingsInteriorBtn.setBackground(settingsInteriorColor);
        final JButton finalSkinsSettingsInteriorBtn = skinsSettingsInteriorBtn;
        skinsSettingsInteriorBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Time Axis Interior Color", settingsInteriorColor, color -> {
                settingsInteriorColor = color;
                settingsUseGradient = false;
                finalSkinsSettingsInteriorBtn.setBackground(color);
                finalSkinsSettingsInteriorBtn.repaint();
                applySettingsPanelColors();
            });
            if (newColor != null) {
                settingsInteriorColor = newColor;
                settingsUseGradient = false;
                finalSkinsSettingsInteriorBtn.setBackground(newColor);
                finalSkinsSettingsInteriorBtn.repaint();
                applySettingsPanelColors();
            }
        });
        tagbc.gridx = 1; tagbc.gridy = 0;
        timeAxisSkinsTab.add(skinsSettingsInteriorBtn, tagbc);

        JButton skinsSettingsInteriorGradientBtn = new JButton("\u25BC");
        skinsSettingsInteriorGradientBtn.setPreferredSize(new Dimension(20, 25));
        skinsSettingsInteriorGradientBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        skinsSettingsInteriorGradientBtn.setMargin(new Insets(0, 0, 0, 0));
        skinsSettingsInteriorGradientBtn.setToolTipText("Gradient options");
        JPopupMenu skinsSettingsInteriorGradientMenu = new JPopupMenu();
        JMenuItem skinsSettingsInteriorGradientMenuItem = new JMenuItem("Gradient...");
        skinsSettingsInteriorGradientMenuItem.addActionListener(ev -> {
            showGradientDialog("settings");
            finalSkinsSettingsInteriorBtn.repaint();
        });
        skinsSettingsInteriorGradientMenu.add(skinsSettingsInteriorGradientMenuItem);
        skinsSettingsInteriorGradientBtn.addActionListener(ev -> {
            skinsSettingsInteriorGradientMenu.show(skinsSettingsInteriorGradientBtn, 0, skinsSettingsInteriorGradientBtn.getHeight());
        });
        tagbc.gridx = 2; tagbc.gridy = 0;
        timeAxisSkinsTab.add(skinsSettingsInteriorGradientBtn, tagbc);

        // Add filler to push content to top
        tagbc.gridx = 0; tagbc.gridy = 1;
        tagbc.gridwidth = 3;
        tagbc.weighty = 1.0;
        tagbc.fill = GridBagConstraints.BOTH;
        timeAxisSkinsTab.add(new JPanel(), tagbc);

        // === TOOLBAR TAB ===
        JPanel toolbarSkinsTab = new JPanel(new GridBagLayout());
        toolbarSkinsTab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints tbgbc = new GridBagConstraints();
        tbgbc.insets = new Insets(8, 8, 8, 8);
        tbgbc.anchor = GridBagConstraints.WEST;
        tbgbc.gridx = 0; tbgbc.gridy = 0;
        toolbarSkinsTab.add(new JLabel("Background:"), tbgbc);
        JButton toolbarSkinsColorBtn = new JButton();
        toolbarSkinsColorBtn.setPreferredSize(new Dimension(60, 25));
        toolbarSkinsColorBtn.setBackground(toolbarBgColor);
        toolbarSkinsColorBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(dialog, "Choose Toolbar Background", toolbarBgColor);
            if (newColor != null) {
                toolbarBgColor = newColor;
                toolbarUseGradient = false;
                toolbarSkinsColorBtn.setBackground(newColor);
                if (toolbarPanel != null) toolbarPanel.setBackground(newColor);
            }
        });
        tbgbc.gridx = 1;
        toolbarSkinsTab.add(toolbarSkinsColorBtn, tbgbc);
        JButton toolbarSkinsGradBtn = new JButton("\u25BC");
        toolbarSkinsGradBtn.setPreferredSize(new Dimension(20, 25));
        toolbarSkinsGradBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        JPopupMenu toolbarSkinsGradMenu = new JPopupMenu();
        JMenuItem toolbarSkinsGradItem = new JMenuItem("Gradient...");
        toolbarSkinsGradItem.addActionListener(ev -> {
            showGradientDialog("toolbar");
            toolbarSkinsColorBtn.repaint();
            if (toolbarPanel != null) toolbarPanel.repaint();
        });
        toolbarSkinsGradMenu.add(toolbarSkinsGradItem);
        toolbarSkinsGradBtn.addActionListener(ev -> toolbarSkinsGradMenu.show(toolbarSkinsGradBtn, 0, toolbarSkinsGradBtn.getHeight()));
        tbgbc.gridx = 2;
        toolbarSkinsTab.add(toolbarSkinsGradBtn, tbgbc);
        tbgbc.gridx = 0; tbgbc.gridy = 1; tbgbc.gridwidth = 3; tbgbc.weighty = 1.0; tbgbc.fill = GridBagConstraints.BOTH;
        toolbarSkinsTab.add(new JPanel(), tbgbc);

        // === RIGHT TABBED PANE TAB ===
        JPanel rightTabbedSkinsTab = new JPanel(new GridBagLayout());
        rightTabbedSkinsTab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints rtgbc = new GridBagConstraints();
        rtgbc.insets = new Insets(8, 8, 8, 8);
        rtgbc.anchor = GridBagConstraints.WEST;
        rtgbc.fill = GridBagConstraints.NONE;

        // Background row
        rtgbc.gridx = 0; rtgbc.gridy = 0;
        rightTabbedSkinsTab.add(new JLabel("Background:"), rtgbc);
        JButton rtBgBtn = new JButton();
        rtBgBtn.setPreferredSize(new Dimension(60, 25));
        rtBgBtn.setBackground(rightTabbedBgColor);
        rtBgBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Tab Background Color", rightTabbedBgColor, color -> {
                rightTabbedBgColor = color;
                rtBgBtn.setBackground(color);
                applyRightTabbedPaneColors();
            });
            if (newColor != null) {
                rightTabbedBgColor = newColor;
                rtBgBtn.setBackground(newColor);
                applyRightTabbedPaneColors();
            }
        });
        rtgbc.gridx = 1;
        rightTabbedSkinsTab.add(rtBgBtn, rtgbc);

        // Foreground (text) row
        rtgbc.gridx = 0; rtgbc.gridy = 1;
        rightTabbedSkinsTab.add(new JLabel("Text:"), rtgbc);
        JButton rtFgBtn = new JButton();
        rtFgBtn.setPreferredSize(new Dimension(60, 25));
        rtFgBtn.setBackground(rightTabbedFgColor);
        rtFgBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Tab Text Color", rightTabbedFgColor, color -> {
                rightTabbedFgColor = color;
                rtFgBtn.setBackground(color);
                applyRightTabbedPaneColors();
            });
            if (newColor != null) {
                rightTabbedFgColor = newColor;
                rtFgBtn.setBackground(newColor);
                applyRightTabbedPaneColors();
            }
        });
        rtgbc.gridx = 1;
        rightTabbedSkinsTab.add(rtFgBtn, rtgbc);

        // Selected Background row
        rtgbc.gridx = 0; rtgbc.gridy = 2;
        rightTabbedSkinsTab.add(new JLabel("Selected Background:"), rtgbc);
        JButton rtSelBgBtn = new JButton();
        rtSelBgBtn.setPreferredSize(new Dimension(60, 25));
        rtSelBgBtn.setBackground(rightTabbedSelectedBgColor);
        rtSelBgBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Selected Tab Background", rightTabbedSelectedBgColor, color -> {
                rightTabbedSelectedBgColor = color;
                rtSelBgBtn.setBackground(color);
                applyRightTabbedPaneColors();
            });
            if (newColor != null) {
                rightTabbedSelectedBgColor = newColor;
                rtSelBgBtn.setBackground(newColor);
                applyRightTabbedPaneColors();
            }
        });
        rtgbc.gridx = 1;
        rightTabbedSkinsTab.add(rtSelBgBtn, rtgbc);

        // Selected Foreground row
        rtgbc.gridx = 0; rtgbc.gridy = 3;
        rightTabbedSkinsTab.add(new JLabel("Selected Text:"), rtgbc);
        JButton rtSelFgBtn = new JButton();
        rtSelFgBtn.setPreferredSize(new Dimension(60, 25));
        rtSelFgBtn.setBackground(rightTabbedSelectedFgColor);
        rtSelFgBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Selected Tab Text", rightTabbedSelectedFgColor, color -> {
                rightTabbedSelectedFgColor = color;
                rtSelFgBtn.setBackground(color);
                applyRightTabbedPaneColors();
            });
            if (newColor != null) {
                rightTabbedSelectedFgColor = newColor;
                rtSelFgBtn.setBackground(newColor);
                applyRightTabbedPaneColors();
            }
        });
        rtgbc.gridx = 1;
        rightTabbedSkinsTab.add(rtSelFgBtn, rtgbc);

        // Underline Color row with visibility checkbox
        rtgbc.gridx = 0; rtgbc.gridy = 4;
        rightTabbedSkinsTab.add(new JLabel("Underline:"), rtgbc);
        JButton rtUnderlineBtn = new JButton();
        rtUnderlineBtn.setPreferredSize(new Dimension(60, 25));
        rtUnderlineBtn.setBackground(rightTabbedUnderlineColor);
        rtUnderlineBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Tab Underline Color", rightTabbedUnderlineColor, color -> {
                rightTabbedUnderlineColor = color;
                rtUnderlineBtn.setBackground(color);
                applyRightTabbedPaneColors();
            });
            if (newColor != null) {
                rightTabbedUnderlineColor = newColor;
                rtUnderlineBtn.setBackground(newColor);
                applyRightTabbedPaneColors();
            }
        });
        rtgbc.gridx = 1;
        rightTabbedSkinsTab.add(rtUnderlineBtn, rtgbc);

        // Visible checkbox for underline
        JCheckBox rtUnderlineVisibleChk = new JCheckBox("Visible");
        rtUnderlineVisibleChk.setSelected(rightTabbedUnderlineVisible);
        rtUnderlineVisibleChk.addActionListener(e -> {
            rightTabbedUnderlineVisible = rtUnderlineVisibleChk.isSelected();
            applyRightTabbedPaneColors();
        });
        rtgbc.gridx = 2;
        rightTabbedSkinsTab.add(rtUnderlineVisibleChk, rtgbc);

        // Border row with visible checkbox (applies to all tab content panels)
        rtgbc.gridx = 0; rtgbc.gridy = 5;
        rtgbc.gridwidth = 1;
        rtgbc.weighty = 0;
        rtgbc.fill = GridBagConstraints.NONE;
        rightTabbedSkinsTab.add(new JLabel("Border:"), rtgbc);
        JButton rtBorderBtn = new JButton();
        rtBorderBtn.setPreferredSize(new Dimension(60, 25));
        rtBorderBtn.setBackground(settingsOutlineColor);
        rtBorderBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Border Color", settingsOutlineColor, color -> {
                settingsOutlineColor = color;
                layersOutlineColor = color;
                generalOutlineColor = color;
                rtBorderBtn.setBackground(color);
                applySettingsPanelColors();
                applyLayersPanelColors();
                applyGeneralPanelColors();
            });
            if (newColor != null) {
                settingsOutlineColor = newColor;
                layersOutlineColor = newColor;
                generalOutlineColor = newColor;
                rtBorderBtn.setBackground(newColor);
                applySettingsPanelColors();
                applyLayersPanelColors();
                applyGeneralPanelColors();
            }
        });
        rtgbc.gridx = 1;
        rightTabbedSkinsTab.add(rtBorderBtn, rtgbc);

        // Visible checkbox for border
        JCheckBox rtBorderVisibleChk = new JCheckBox("Visible");
        rtBorderVisibleChk.setSelected(settingsBorderVisible);
        rtBorderVisibleChk.addActionListener(e -> {
            settingsBorderVisible = rtBorderVisibleChk.isSelected();
            layersBorderVisible = rtBorderVisibleChk.isSelected();
            generalBorderVisible = rtBorderVisibleChk.isSelected();
            applySettingsPanelColors();
            applyLayersPanelColors();
            applyGeneralPanelColors();
        });
        rtgbc.gridx = 2;
        rightTabbedSkinsTab.add(rtBorderVisibleChk, rtgbc);

        // Filler
        rtgbc.gridx = 0; rtgbc.gridy = 6;
        rtgbc.gridwidth = 3;
        rtgbc.weighty = 1.0;
        rtgbc.fill = GridBagConstraints.BOTH;
        rightTabbedSkinsTab.add(new JPanel(), rtgbc);

        // === GENERAL TAB ===

        // === SPREADSHEET TAB ===
        JPanel spreadsheetSkinsTab = new JPanel(new GridBagLayout());
        spreadsheetSkinsTab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints ssgbc = new GridBagConstraints();
        ssgbc.insets = new Insets(8, 8, 8, 8);
        ssgbc.anchor = GridBagConstraints.WEST;
        ssgbc.fill = GridBagConstraints.NONE;

        // Selection Background row
        ssgbc.gridx = 0; ssgbc.gridy = 0;
        spreadsheetSkinsTab.add(new JLabel("Selected Background:"), ssgbc);
        JButton spreadsheetSelectionBtn = new JButton();
        spreadsheetSelectionBtn.setPreferredSize(new Dimension(60, 25));
        spreadsheetSelectionBtn.setBackground(spreadsheetSelectionColor);
        final JButton finalSpreadsheetSelectionBtn = spreadsheetSelectionBtn;
        spreadsheetSelectionBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Selected Background Color", spreadsheetSelectionColor, color -> {
                spreadsheetSelectionColor = color;
                finalSpreadsheetSelectionBtn.setBackground(color);
                finalSpreadsheetSelectionBtn.repaint();
                if (spreadsheetTable != null) spreadsheetTable.repaint();
            });
            if (newColor != null) {
                spreadsheetSelectionColor = newColor;
                finalSpreadsheetSelectionBtn.setBackground(newColor);
                finalSpreadsheetSelectionBtn.repaint();
                if (spreadsheetTable != null) spreadsheetTable.repaint();
            }
        });
        ssgbc.gridx = 1;
        spreadsheetSkinsTab.add(spreadsheetSelectionBtn, ssgbc);

        // Selection Text Color row
        ssgbc.gridx = 0; ssgbc.gridy = 1;
        spreadsheetSkinsTab.add(new JLabel("Selected Text:"), ssgbc);
        JButton spreadsheetTextColorBtn = new JButton();
        spreadsheetTextColorBtn.setPreferredSize(new Dimension(60, 25));
        spreadsheetTextColorBtn.setBackground(spreadsheetSelectionTextColor);
        final JButton finalSpreadsheetTextColorBtn = spreadsheetTextColorBtn;
        spreadsheetTextColorBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Selected Text Color", spreadsheetSelectionTextColor, color -> {
                spreadsheetSelectionTextColor = color;
                finalSpreadsheetTextColorBtn.setBackground(color);
                finalSpreadsheetTextColorBtn.repaint();
                if (spreadsheetTable != null) spreadsheetTable.repaint();
            });
            if (newColor != null) {
                spreadsheetSelectionTextColor = newColor;
                finalSpreadsheetTextColorBtn.setBackground(newColor);
                finalSpreadsheetTextColorBtn.repaint();
                if (spreadsheetTable != null) spreadsheetTable.repaint();
            }
        });
        ssgbc.gridx = 1;
        spreadsheetSkinsTab.add(spreadsheetTextColorBtn, ssgbc);

        // Unselected Background Color row
        ssgbc.gridx = 0; ssgbc.gridy = 2;
        spreadsheetSkinsTab.add(new JLabel("Unselected Background:"), ssgbc);
        JButton spreadsheetUnselectedBgBtn = new JButton();
        spreadsheetUnselectedBgBtn.setPreferredSize(new Dimension(60, 25));
        spreadsheetUnselectedBgBtn.setBackground(spreadsheetUnselectedBgColor);
        final JButton finalSpreadsheetUnselectedBgBtn = spreadsheetUnselectedBgBtn;
        spreadsheetUnselectedBgBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Unselected Background Color", spreadsheetUnselectedBgColor, color -> {
                spreadsheetUnselectedBgColor = color;
                finalSpreadsheetUnselectedBgBtn.setBackground(color);
                finalSpreadsheetUnselectedBgBtn.repaint();
                if (spreadsheetTable != null) spreadsheetTable.repaint();
            });
            if (newColor != null) {
                spreadsheetUnselectedBgColor = newColor;
                finalSpreadsheetUnselectedBgBtn.setBackground(newColor);
                finalSpreadsheetUnselectedBgBtn.repaint();
                if (spreadsheetTable != null) spreadsheetTable.repaint();
            }
        });
        ssgbc.gridx = 1;
        spreadsheetSkinsTab.add(spreadsheetUnselectedBgBtn, ssgbc);

        // Unselected Text Color row
        ssgbc.gridx = 0; ssgbc.gridy = 3;
        spreadsheetSkinsTab.add(new JLabel("Unselected Text:"), ssgbc);
        JButton spreadsheetUnselectedTextBtn = new JButton();
        spreadsheetUnselectedTextBtn.setPreferredSize(new Dimension(60, 25));
        spreadsheetUnselectedTextBtn.setBackground(spreadsheetUnselectedTextColor);
        final JButton finalSpreadsheetUnselectedTextBtn = spreadsheetUnselectedTextBtn;
        spreadsheetUnselectedTextBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Unselected Text Color", spreadsheetUnselectedTextColor, color -> {
                spreadsheetUnselectedTextColor = color;
                finalSpreadsheetUnselectedTextBtn.setBackground(color);
                finalSpreadsheetUnselectedTextBtn.repaint();
                if (spreadsheetTable != null) spreadsheetTable.repaint();
            });
            if (newColor != null) {
                spreadsheetUnselectedTextColor = newColor;
                finalSpreadsheetUnselectedTextBtn.setBackground(newColor);
                finalSpreadsheetUnselectedTextBtn.repaint();
                if (spreadsheetTable != null) spreadsheetTable.repaint();
            }
        });
        ssgbc.gridx = 1;
        spreadsheetSkinsTab.add(spreadsheetUnselectedTextBtn, ssgbc);

        // Panel Width
        ssgbc.gridx = 0; ssgbc.gridy = 4;
        ssgbc.gridwidth = 1;
        ssgbc.weighty = 0;
        ssgbc.fill = GridBagConstraints.NONE;
        spreadsheetSkinsTab.add(new JLabel("Panel Width (px):"), ssgbc);
        ssgbc.gridx = 1;
        JTextField panelWidthField = new JTextField(String.valueOf(spreadsheetPanelWidth), 6);
        panelWidthField.addActionListener(e -> {
            try {
                int newWidth = Integer.parseInt(panelWidthField.getText());
                if (newWidth > 50 && newWidth < 2000) {
                    spreadsheetPanelWidth = newWidth;
                    spreadsheetPanel.setPreferredSize(new Dimension(spreadsheetPanelWidth, 0));
                    centerSplitPane.resetToPreferredSizes();
                }
            } catch (NumberFormatException ex) {}
        });
        spreadsheetSkinsTab.add(panelWidthField, ssgbc);

        // Filler
        ssgbc.gridx = 0; ssgbc.gridy = 5;
        ssgbc.gridwidth = 2;
        ssgbc.weighty = 1.0;
        ssgbc.fill = GridBagConstraints.BOTH;
        spreadsheetSkinsTab.add(new JPanel(), ssgbc);

        JPanel generalSkinsTab = new JPanel(new GridBagLayout());
        generalSkinsTab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints ggbc = new GridBagConstraints();
        ggbc.insets = new Insets(8, 8, 8, 8);
        ggbc.anchor = GridBagConstraints.WEST;
        ggbc.fill = GridBagConstraints.NONE;

        // Spacer row
        ggbc.gridx = 0; ggbc.gridy = 0;
        ggbc.gridwidth = 3;
        generalSkinsTab.add(new JPanel(), ggbc);
        ggbc.gridwidth = 1;

        // Interior row
        ggbc.gridx = 0; ggbc.gridy = 1;
        generalSkinsTab.add(new JLabel("Interior:"), ggbc);
        JButton generalInteriorBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (generalUseGradient && generalGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[generalGradientStops.size()];
                    Color[] colors = new Color[generalGradientStops.size()];
                    for (int i = 0; i < generalGradientStops.size(); i++) {
                        float[] stop = generalGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        generalInteriorBtn.setPreferredSize(new Dimension(60, 25));
        generalInteriorBtn.setBackground(generalInteriorColor);
        final JButton finalGeneralInteriorBtn = generalInteriorBtn;
        generalInteriorBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose General Interior Color", generalInteriorColor, color -> {
                generalInteriorColor = color;
                generalUseGradient = false;
                finalGeneralInteriorBtn.setBackground(color);
                finalGeneralInteriorBtn.repaint();
                applyGeneralPanelColors();
            });
            if (newColor != null) {
                generalInteriorColor = newColor;
                generalUseGradient = false;
                finalGeneralInteriorBtn.setBackground(newColor);
                finalGeneralInteriorBtn.repaint();
                applyGeneralPanelColors();
            }
        });
        ggbc.gridx = 1;
        generalSkinsTab.add(generalInteriorBtn, ggbc);

        JButton generalInteriorGradientBtn = new JButton("\u25BC");
        generalInteriorGradientBtn.setPreferredSize(new Dimension(20, 25));
        generalInteriorGradientBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        generalInteriorGradientBtn.setMargin(new Insets(0, 0, 0, 0));
        generalInteriorGradientBtn.setToolTipText("Gradient options");
        JPopupMenu generalInteriorGradientMenu = new JPopupMenu();
        JMenuItem generalInteriorGradientMenuItem = new JMenuItem("Gradient...");
        generalInteriorGradientMenuItem.addActionListener(ev -> {
            showGradientDialog("general");
            finalGeneralInteriorBtn.repaint();
        });
        generalInteriorGradientMenu.add(generalInteriorGradientMenuItem);
        generalInteriorGradientBtn.addActionListener(ev -> {
            generalInteriorGradientMenu.show(generalInteriorGradientBtn, 0, generalInteriorGradientBtn.getHeight());
        });
        ggbc.gridx = 2;
        generalSkinsTab.add(generalInteriorGradientBtn, ggbc);

        // Filler
        ggbc.gridx = 0; ggbc.gridy = 2;
        ggbc.gridwidth = 3;
        ggbc.weighty = 1.0;
        ggbc.fill = GridBagConstraints.BOTH;
        generalSkinsTab.add(new JPanel(), ggbc);

        // Add all tabs in desired order: Toolbar, Format, Right Panel, rightTabbedPane, Layers, Timeline, General
        tabbedPane.addTab("Toolbar", toolbarSkinsTab);
        tabbedPane.addTab("Format", formatTab);
        tabbedPane.addTab("Right Panel", rightPanelSkinsTab);
        tabbedPane.addTab("rightTabbedPane", rightTabbedSkinsTab);
        tabbedPane.addTab("Layers", skinsLayersTab);
        tabbedPane.addTab("Timeline", timeAxisSkinsTab);
        tabbedPane.addTab("General", generalSkinsTab);
        tabbedPane.addTab("Spreadsheet", spreadsheetSkinsTab);

        dialog.add(tabbedPane, BorderLayout.CENTER);

        // Button panel with Export/Import on left, OK/Cancel on right
        JPanel buttonPanel = new JPanel(new BorderLayout());

        // Left side - Export/Import buttons
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton exportBtn = new JButton("Export...");
        exportBtn.addActionListener(e -> exportSkinsSettings(dialog));
        JButton importBtn = new JButton("Import...");
        importBtn.addActionListener(e -> importSkinsSettings(dialog));
        leftButtons.add(exportBtn);
        leftButtons.add(importBtn);
        buttonPanel.add(leftButtons, BorderLayout.WEST);

        // Right side - OK/Cancel buttons
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(e -> {
            if (settingsPanel != null) settingsPanel.repaint();
            dialog.dispose();
        });
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        rightButtons.add(okBtn);
        rightButtons.add(cancelBtn);
        buttonPanel.add(rightButtons, BorderLayout.EAST);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void exportSkinsSettings(JDialog parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Skins Settings");
        fileChooser.setSelectedFile(new java.io.File("skins_settings.txt"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));

        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new java.io.File(file.getAbsolutePath() + ".txt");
            }
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                writer.println("# Timeline Skins Settings Export");
                writer.println("# Generated: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
                writer.println();

                // Settings Panel
                writer.println("# Settings Panel (Timeline Tab)");
                writer.println("settingsInteriorColor=" + colorToHex(settingsInteriorColor));
                writer.println("settingsInteriorColor2=" + colorToHex(settingsInteriorColor2));
                writer.println("settingsUseGradient=" + settingsUseGradient);
                writer.println("settingsGradientAngle=" + settingsGradientAngle);
                writeGradientStops(writer, "settingsGradientStops", settingsGradientStops);
                writer.println("settingsOutlineColor=" + colorToHex(settingsOutlineColor));
                writer.println("settingsBorderVisible=" + settingsBorderVisible);
                writer.println("settingsHeaderColor=" + colorToHex(settingsHeaderColor));
                writer.println("settingsHeaderColor2=" + colorToHex(settingsHeaderColor2));
                writer.println("settingsHeaderUseGradient=" + settingsHeaderUseGradient);
                writer.println("settingsHeaderGradientAngle=" + settingsHeaderGradientAngle);
                writeGradientStops(writer, "settingsHeaderGradientStops", settingsHeaderGradientStops);
                writer.println("settingsHeaderTextColor=" + colorToHex(settingsHeaderTextColor));
                writer.println("settingsLabelColor=" + colorToHex(settingsLabelColor));
                writer.println("settingsFieldBgColor=" + colorToHex(settingsFieldBgColor));
                writer.println("settingsButtonBgColor=" + colorToHex(settingsButtonBgColor));
                writer.println("settingsButtonTextColor=" + colorToHex(settingsButtonTextColor));
                writer.println();

                // Timeline Panel
                writer.println("# Timeline Panel");
                writer.println("timelineInteriorColor=" + colorToHex(timelineInteriorColor));
                writer.println("timelineInteriorColor2=" + colorToHex(timelineInteriorColor2));
                writer.println("timelineUseGradient=" + timelineUseGradient);
                writer.println("timelineGradientAngle=" + timelineGradientAngle);
                writeGradientStops(writer, "timelineGradientStops", timelineGradientStops);
                writer.println("timelineOutlineColor=" + colorToHex(timelineOutlineColor));
                writer.println("timelineLineColor=" + colorToHex(timelineLineColor));
                writer.println("timelineDateTextColor=" + colorToHex(timelineDateTextColor));
                writer.println("timelineGridColor=" + colorToHex(timelineGridColor));
                writer.println("timelineEventColor=" + colorToHex(timelineEventColor));
                writer.println();

                // Layers Panel
                writer.println("# Layers Panel");
                writer.println("layersInteriorColor=" + colorToHex(layersInteriorColor));
                writer.println("layersInteriorColor2=" + colorToHex(layersInteriorColor2));
                writer.println("layersUseGradient=" + layersUseGradient);
                writer.println("layersGradientAngle=" + layersGradientAngle);
                writeGradientStops(writer, "layersGradientStops", layersGradientStops);
                writer.println("layersOutlineColor=" + colorToHex(layersOutlineColor));
                writer.println("layersBorderVisible=" + layersBorderVisible);
                writer.println("layersHeaderColor=" + colorToHex(layersHeaderColor));
                writer.println("layersHeaderColor2=" + colorToHex(layersHeaderColor2));
                writer.println("layersHeaderUseGradient=" + layersHeaderUseGradient);
                writer.println("layersHeaderGradientAngle=" + layersHeaderGradientAngle);
                writeGradientStops(writer, "layersHeaderGradientStops", layersHeaderGradientStops);
                writer.println("layersHeaderTextColor=" + colorToHex(layersHeaderTextColor));
                writer.println("layersListBgColor=" + colorToHex(layersListBgColor));
                writer.println("layersListBgColor2=" + colorToHex(layersListBgColor2));
                writer.println("layersListBgUseGradient=" + layersListBgUseGradient);
                writer.println("layersListBgGradientAngle=" + layersListBgGradientAngle);
                writeGradientStops(writer, "layersListBgGradientStops", layersListBgGradientStops);
                writer.println("layersItemTextColor=" + colorToHex(layersItemTextColor));
                writer.println("layersSelectedBgColor=" + colorToHex(layersSelectedBgColor));
                writer.println("layersDragHandleColor=" + colorToHex(layersDragHandleColor));
                writer.println("layersTaskColor=" + colorToHex(layersTaskColor));
                writer.println("layersTaskColor2=" + colorToHex(layersTaskColor2));
                writer.println("layersTaskUseGradient=" + layersTaskUseGradient);
                writer.println("layersTaskGradientAngle=" + layersTaskGradientAngle);
                writeGradientStops(writer, "layersTaskGradientStops", layersTaskGradientStops);
                writer.println();

                // Format Panel
                writer.println("# Format Panel");
                writer.println("formatInteriorColor=" + colorToHex(formatInteriorColor));
                writer.println("formatInteriorColor2=" + colorToHex(formatInteriorColor2));
                writer.println("formatUseGradient=" + formatUseGradient);
                writer.println("formatGradientAngle=" + formatGradientAngle);
                writeGradientStops(writer, "formatGradientStops", formatGradientStops);
                writer.println("formatOutlineColor=" + colorToHex(formatOutlineColor));
                writer.println("formatHeaderColor=" + colorToHex(formatHeaderColor));
                writer.println("formatHeaderColor2=" + colorToHex(formatHeaderColor2));
                writer.println("formatHeaderUseGradient=" + formatHeaderUseGradient);
                writer.println("formatHeaderGradientAngle=" + formatHeaderGradientAngle);
                writeGradientStops(writer, "formatHeaderGradientStops", formatHeaderGradientStops);
                writer.println("formatLabelColor=" + colorToHex(formatLabelColor));
                writer.println("formatSeparatorColor=" + colorToHex(formatSeparatorColor));
                writer.println("formatResizeHandleColor=" + colorToHex(formatResizeHandleColor));
                writer.println("formatTabColor=" + colorToHex(formatTabColor));
                writer.println("formatTabColor2=" + colorToHex(formatTabColor2));
                writer.println("formatTabUseGradient=" + formatTabUseGradient);
                writer.println("formatTabGradientAngle=" + formatTabGradientAngle);
                writeGradientStops(writer, "formatTabGradientStops", formatTabGradientStops);
                writer.println("formatSelectedTabColor=" + colorToHex(formatSelectedTabColor));
                writer.println("formatSelectedTabColor2=" + colorToHex(formatSelectedTabColor2));
                writer.println("formatSelectedTabUseGradient=" + formatSelectedTabUseGradient);
                writer.println("formatSelectedTabGradientAngle=" + formatSelectedTabGradientAngle);
                writeGradientStops(writer, "formatSelectedTabGradientStops", formatSelectedTabGradientStops);
                writer.println("formatTabContentColor=" + colorToHex(formatTabContentColor));
                writer.println("formatTabContentColor2=" + colorToHex(formatTabContentColor2));
                writer.println("formatTabContentUseGradient=" + formatTabContentUseGradient);
                writer.println("formatTabContentGradientAngle=" + formatTabContentGradientAngle);
                writeGradientStops(writer, "formatTabContentGradientStops", formatTabContentGradientStops);
                writer.println();

                // Toolbar
                writer.println("# Toolbar");
                writer.println("toolbarBgColor=" + colorToHex(toolbarBgColor));
                writer.println("toolbarBgColor2=" + colorToHex(toolbarBgColor2));
                writer.println("toolbarUseGradient=" + toolbarUseGradient);
                writer.println("toolbarGradientAngle=" + toolbarGradientAngle);
                writeGradientStops(writer, "toolbarGradientStops", toolbarGradientStops);
                writer.println();

                // Right Tabbed Pane
                writer.println("# Right Tabbed Pane");
                writer.println("rightTabbedBgColor=" + colorToHex(rightTabbedBgColor));
                writer.println("rightTabbedFgColor=" + colorToHex(rightTabbedFgColor));
                writer.println("rightTabbedSelectedBgColor=" + colorToHex(rightTabbedSelectedBgColor));
                writer.println("rightTabbedSelectedFgColor=" + colorToHex(rightTabbedSelectedFgColor));
                writer.println("rightTabbedBorderColor=" + colorToHex(rightTabbedBorderColor));
                writer.println("rightTabbedBorderVisible=" + rightTabbedBorderVisible);
                writer.println("rightTabbedUnderlineColor=" + colorToHex(rightTabbedUnderlineColor));
                writer.println("rightTabbedUnderlineVisible=" + rightTabbedUnderlineVisible);
                writer.println();

                // General Tab
                writer.println("# General Tab");
                writer.println("generalInteriorColor=" + colorToHex(generalInteriorColor));
                writer.println("generalInteriorColor2=" + colorToHex(generalInteriorColor2));
                writer.println("generalUseGradient=" + generalUseGradient);
                writer.println("generalGradientAngle=" + generalGradientAngle);
                writeGradientStops(writer, "generalGradientStops", generalGradientStops);
                writer.println("generalOutlineColor=" + colorToHex(generalOutlineColor));
                writer.println("generalBorderVisible=" + generalBorderVisible);

                JOptionPane.showMessageDialog(parent, "Skins settings exported to:\n" + file.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Error exporting settings: " + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void writeGradientStops(java.io.PrintWriter writer, String name, ArrayList<float[]> stops) {
        if (stops.isEmpty()) {
            writer.println(name + "=");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < stops.size(); i++) {
                float[] stop = stops.get(i);
                if (i > 0) sb.append(";");
                sb.append(String.format("%.3f,%.0f,%.0f,%.0f,%.0f", stop[0], stop[1], stop[2], stop[3], stop[4]));
            }
            writer.println(name + "=" + sb.toString());
        }
    }

    private void importSkinsSettings(JDialog parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Skins Settings");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));

        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;

                    int eqIndex = line.indexOf('=');
                    if (eqIndex > 0) {
                        String key = line.substring(0, eqIndex).trim();
                        String value = line.substring(eqIndex + 1).trim();
                        applySkinsSettingFromFile(key, value);
                    }
                }

                // Apply all changes
                applySettingsPanelColors();
                applyLayersPanelColors();
                applyGeneralPanelColors();
                applyRightTabbedPaneColors();
                if (toolbarPanel != null) toolbarPanel.repaint();
                if (timelineDisplayPanel != null) timelineDisplayPanel.repaint();

                JOptionPane.showMessageDialog(parent, "Skins settings imported successfully!\nClose and reopen Skins dialog to see updated color buttons.",
                    "Import Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Error importing settings: " + ex.getMessage(),
                    "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applySkinsSettingFromFile(String key, String value) {
        try {
            switch (key) {
                // Settings Panel
                case "settingsInteriorColor": settingsInteriorColor = hexToColor(value); break;
                case "settingsInteriorColor2": settingsInteriorColor2 = hexToColor(value); break;
                case "settingsUseGradient": settingsUseGradient = Boolean.parseBoolean(value); break;
                case "settingsGradientAngle": settingsGradientAngle = Double.parseDouble(value); break;
                case "settingsGradientStops": settingsGradientStops = parseGradientStops(value); break;
                case "settingsOutlineColor": settingsOutlineColor = hexToColor(value); break;
                case "settingsBorderVisible": settingsBorderVisible = Boolean.parseBoolean(value); break;
                case "settingsHeaderColor": settingsHeaderColor = hexToColor(value); break;
                case "settingsHeaderColor2": settingsHeaderColor2 = hexToColor(value); break;
                case "settingsHeaderUseGradient": settingsHeaderUseGradient = Boolean.parseBoolean(value); break;
                case "settingsHeaderGradientAngle": settingsHeaderGradientAngle = Double.parseDouble(value); break;
                case "settingsHeaderGradientStops": settingsHeaderGradientStops = parseGradientStops(value); break;
                case "settingsHeaderTextColor": settingsHeaderTextColor = hexToColor(value); break;
                case "settingsLabelColor": settingsLabelColor = hexToColor(value); break;
                case "settingsFieldBgColor": settingsFieldBgColor = hexToColor(value); break;
                case "settingsButtonBgColor": settingsButtonBgColor = hexToColor(value); break;
                case "settingsButtonTextColor": settingsButtonTextColor = hexToColor(value); break;

                // Timeline Panel
                case "timelineInteriorColor": timelineInteriorColor = hexToColor(value); break;
                case "timelineInteriorColor2": timelineInteriorColor2 = hexToColor(value); break;
                case "timelineUseGradient": timelineUseGradient = Boolean.parseBoolean(value); break;
                case "timelineGradientAngle": timelineGradientAngle = Double.parseDouble(value); break;
                case "timelineGradientStops": timelineGradientStops = parseGradientStops(value); break;
                case "timelineOutlineColor": timelineOutlineColor = hexToColor(value); break;
                case "timelineLineColor": timelineLineColor = hexToColor(value); break;
                case "timelineDateTextColor": timelineDateTextColor = hexToColor(value); break;
                case "timelineGridColor": timelineGridColor = hexToColor(value); break;
                case "timelineEventColor": timelineEventColor = hexToColor(value); break;

                // Layers Panel
                case "layersInteriorColor": layersInteriorColor = hexToColor(value); break;
                case "layersInteriorColor2": layersInteriorColor2 = hexToColor(value); break;
                case "layersUseGradient": layersUseGradient = Boolean.parseBoolean(value); break;
                case "layersGradientAngle": layersGradientAngle = Double.parseDouble(value); break;
                case "layersGradientStops": layersGradientStops = parseGradientStops(value); break;
                case "layersOutlineColor": layersOutlineColor = hexToColor(value); break;
                case "layersBorderVisible": layersBorderVisible = Boolean.parseBoolean(value); break;
                case "layersHeaderColor": layersHeaderColor = hexToColor(value); break;
                case "layersHeaderColor2": layersHeaderColor2 = hexToColor(value); break;
                case "layersHeaderUseGradient": layersHeaderUseGradient = Boolean.parseBoolean(value); break;
                case "layersHeaderGradientAngle": layersHeaderGradientAngle = Double.parseDouble(value); break;
                case "layersHeaderGradientStops": layersHeaderGradientStops = parseGradientStops(value); break;
                case "layersHeaderTextColor": layersHeaderTextColor = hexToColor(value); break;
                case "layersListBgColor": layersListBgColor = hexToColor(value); break;
                case "layersListBgColor2": layersListBgColor2 = hexToColor(value); break;
                case "layersListBgUseGradient": layersListBgUseGradient = Boolean.parseBoolean(value); break;
                case "layersListBgGradientAngle": layersListBgGradientAngle = Double.parseDouble(value); break;
                case "layersListBgGradientStops": layersListBgGradientStops = parseGradientStops(value); break;
                case "layersItemTextColor": layersItemTextColor = hexToColor(value); break;
                case "layersSelectedBgColor": layersSelectedBgColor = hexToColor(value); break;
                case "layersDragHandleColor": layersDragHandleColor = hexToColor(value); break;
                case "layersTaskColor": layersTaskColor = hexToColor(value); break;
                case "layersTaskColor2": layersTaskColor2 = hexToColor(value); break;
                case "layersTaskUseGradient": layersTaskUseGradient = Boolean.parseBoolean(value); break;
                case "layersTaskGradientAngle": layersTaskGradientAngle = Double.parseDouble(value); break;
                case "layersTaskGradientStops": layersTaskGradientStops = parseGradientStops(value); break;

                // Format Panel
                case "formatInteriorColor": formatInteriorColor = hexToColor(value); break;
                case "formatInteriorColor2": formatInteriorColor2 = hexToColor(value); break;
                case "formatUseGradient": formatUseGradient = Boolean.parseBoolean(value); break;
                case "formatGradientAngle": formatGradientAngle = Double.parseDouble(value); break;
                case "formatGradientStops": formatGradientStops = parseGradientStops(value); break;
                case "formatOutlineColor": formatOutlineColor = hexToColor(value); break;
                case "formatHeaderColor": formatHeaderColor = hexToColor(value); break;
                case "formatHeaderColor2": formatHeaderColor2 = hexToColor(value); break;
                case "formatHeaderUseGradient": formatHeaderUseGradient = Boolean.parseBoolean(value); break;
                case "formatHeaderGradientAngle": formatHeaderGradientAngle = Double.parseDouble(value); break;
                case "formatHeaderGradientStops": formatHeaderGradientStops = parseGradientStops(value); break;
                case "formatLabelColor": formatLabelColor = hexToColor(value); break;
                case "formatSeparatorColor": formatSeparatorColor = hexToColor(value); break;
                case "formatResizeHandleColor": formatResizeHandleColor = hexToColor(value); break;
                case "formatTabColor": formatTabColor = hexToColor(value); break;
                case "formatTabColor2": formatTabColor2 = hexToColor(value); break;
                case "formatTabUseGradient": formatTabUseGradient = Boolean.parseBoolean(value); break;
                case "formatTabGradientAngle": formatTabGradientAngle = Double.parseDouble(value); break;
                case "formatTabGradientStops": formatTabGradientStops = parseGradientStops(value); break;
                case "formatSelectedTabColor": formatSelectedTabColor = hexToColor(value); break;
                case "formatSelectedTabColor2": formatSelectedTabColor2 = hexToColor(value); break;
                case "formatSelectedTabUseGradient": formatSelectedTabUseGradient = Boolean.parseBoolean(value); break;
                case "formatSelectedTabGradientAngle": formatSelectedTabGradientAngle = Double.parseDouble(value); break;
                case "formatSelectedTabGradientStops": formatSelectedTabGradientStops = parseGradientStops(value); break;
                case "formatTabContentColor": formatTabContentColor = hexToColor(value); break;
                case "formatTabContentColor2": formatTabContentColor2 = hexToColor(value); break;
                case "formatTabContentUseGradient": formatTabContentUseGradient = Boolean.parseBoolean(value); break;
                case "formatTabContentGradientAngle": formatTabContentGradientAngle = Double.parseDouble(value); break;
                case "formatTabContentGradientStops": formatTabContentGradientStops = parseGradientStops(value); break;

                // Toolbar
                case "toolbarBgColor": toolbarBgColor = hexToColor(value); break;
                case "toolbarBgColor2": toolbarBgColor2 = hexToColor(value); break;
                case "toolbarUseGradient": toolbarUseGradient = Boolean.parseBoolean(value); break;
                case "toolbarGradientAngle": toolbarGradientAngle = Double.parseDouble(value); break;
                case "toolbarGradientStops": toolbarGradientStops = parseGradientStops(value); break;

                // Right Tabbed Pane
                case "rightTabbedBgColor": rightTabbedBgColor = hexToColor(value); break;
                case "rightTabbedFgColor": rightTabbedFgColor = hexToColor(value); break;
                case "rightTabbedSelectedBgColor": rightTabbedSelectedBgColor = hexToColor(value); break;
                case "rightTabbedSelectedFgColor": rightTabbedSelectedFgColor = hexToColor(value); break;
                case "rightTabbedBorderColor": rightTabbedBorderColor = hexToColor(value); break;
                case "rightTabbedBorderVisible": rightTabbedBorderVisible = Boolean.parseBoolean(value); break;
                case "rightTabbedUnderlineColor": rightTabbedUnderlineColor = hexToColor(value); break;
                case "rightTabbedUnderlineVisible": rightTabbedUnderlineVisible = Boolean.parseBoolean(value); break;

                // General Tab
                case "generalInteriorColor": generalInteriorColor = hexToColor(value); break;
                case "generalInteriorColor2": generalInteriorColor2 = hexToColor(value); break;
                case "generalUseGradient": generalUseGradient = Boolean.parseBoolean(value); break;
                case "generalGradientAngle": generalGradientAngle = Double.parseDouble(value); break;
                case "generalGradientStops": generalGradientStops = parseGradientStops(value); break;
                case "generalOutlineColor": generalOutlineColor = hexToColor(value); break;
                case "generalBorderVisible": generalBorderVisible = Boolean.parseBoolean(value); break;
            }
        } catch (Exception e) {
            // Silently ignore parse errors for individual settings
        }
    }

    private ArrayList<float[]> parseGradientStops(String value) {
        ArrayList<float[]> stops = new ArrayList<>();
        if (value == null || value.isEmpty()) return stops;

        String[] parts = value.split(";");
        for (String part : parts) {
            String[] values = part.split(",");
            if (values.length == 5) {
                try {
                    float[] stop = new float[5];
                    stop[0] = Float.parseFloat(values[0]);
                    stop[1] = Float.parseFloat(values[1]);
                    stop[2] = Float.parseFloat(values[2]);
                    stop[3] = Float.parseFloat(values[3]);
                    stop[4] = Float.parseFloat(values[4]);
                    stops.add(stop);
                } catch (NumberFormatException e) {
                    // Skip invalid entries
                }
            }
        }
        return stops;
    }

    private void showChangeLog() {
        JDialog dialog = new JDialog(this, "Change Log", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        String changeLog = """
            SPREADSHEET CHANGE LOG
            ======================

            Version 4 (Current)
            -------------------
            - Steel blue border around cell when in edit mode

            Version 3
            ---------
            - Simplified change log view with detailed option

            Version 2
            ---------
            - Added Change Log menu item in File menu
            - Added version label in top right of toolbar

            Version 1
            ---------
            - Double-click places cursor at clicked position in text
            - Single click selects cell, arrow keys navigate spreadsheet
            - Double click enters edit mode, arrow keys move cursor in text
            - Arrow keys move cursor within text when editing
            - Metal L&F text field editor for visible cursor
            - Disabled row/column selection highlighting
            - Right-click context menu with Word Wrap toggle
            - Word wrap cells auto-expand row height
            - Column resizing from anywhere in spreadsheet
            - Click outside spreadsheet stops editing and clears selection
            """;

        JTextArea textArea = new JTextArea(changeLog);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Top panel with detailed button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton detailedBtn = new JButton("Detailed Change Log");
        detailedBtn.addActionListener(e -> showDetailedChangeLog());
        topPanel.add(detailedBtn);
        dialog.add(topPanel, BorderLayout.NORTH);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(closeBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showDetailedChangeLog() {
        JDialog dialog = new JDialog(this, "Detailed Change Log", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this);

        String changeLog = """
            DETAILED SPREADSHEET CHANGE LOG
            ===============================

            Version 4.6 (Current)
            ---------------------
            Prompt: "keep trying to find a way to get into edit mode on double click"
            Changes:
            - Switched to mouseClicked instead of mousePressed
            - Simplified double-click handler

            Version 4.5
            -----------
            Prompt: "do a character then a backspace to trigger edit mode on double click try the letter 'a' then backspace"
            Changes:
            - Tried Document.insertString("a") then remove(1) - did not work

            Version 4.4
            -----------
            Prompt: "keep trying to find a way to get into edit mode on double click. what about space than backspace?"
            Changes:
            - Tried dispatching synthetic key events - did not work

            Version 4.3
            -----------
            Prompt: "keep trying to find a way to get into edit mode on double click. what about space than backspace?"
            Changes:
            - Tried setText with space then original text - did not work

            Version 4.2
            -----------
            Prompt: "can you make it thicker"
            Changes:
            - Increased edit mode border from 2px to 3px

            Version 4.1
            -----------
            Prompt: "can you make the box have a different color outline in edit mode"
            Changes:
            - Added steel blue border around cell when in edit mode

            Version 4
            ---------
            - Steel blue border (3px) around cell when in edit mode

            Version 3
            ---------
            Prompt: "make the change log window only list the changes that were made. Have a button at the top of this window called detailed change log that takes you to the detail you have in the current version."
            Changes:
            - Simplified main change log to show only changes
            - Added Detailed Change Log button for full details with prompts

            Version 2
            ---------
            Prompt: "make an option on the file window called change log. when you click on that open a window that describes the changes that were made and what my prompt to claude was that created those changes"
            Changes:
            - Added Change Log menu item in File menu
            - Created this change log dialog window

            Prompt: "can you make a version appear in the top right of the toolbar that increments each time we make a change. you can start with version 1"
            Changes:
            - Added version label (v1) in top right of toolbar

            Version 1
            ---------
            Prompt: "there was a version that would had the following functionality I think. when I double clicked it would place the cursor in that part of the text string I clicked on"
            Changes:
            - Double-click now places cursor at clicked position in text

            Prompt: "It works now, but the arrow keys no longer navigate the spreadsheet. When you mouse click a cell the first time it should select a cell but not the text in the cell, at this point the arrows should move you right left up down on the spreadsheet. if you click on a cell twice thats when your editing text and the arrows should move you back and forth in the text."
            Changes:
            - Single click selects cell, arrow keys navigate spreadsheet
            - Double click enters edit mode, arrow keys move cursor in text

            Prompt: "yes. but we still need to tweak some things" / "when editing text, if I hit the arrow key to move the cursor back in the text it moves to the next cell instead"
            Changes:
            - Arrow keys now move cursor within text when editing

            Prompt: "can you try again make a cursor visible when cell text is being edited"
            Changes:
            - Used Metal L&F for text field editor to show visible cursor

            Prompt: "can you get rid of the blue highlighting that goes across a whole line when you click on a cell"
            Changes:
            - Disabled row/column selection highlighting
            - Set to single cell selection mode only

            Prompt: "can you make it so I right click on a cell and it give me to turn word wrap on and off for that cell"
            Changes:
            - Added right-click context menu with Word Wrap toggle
            - Word wrap cells auto-expand row height
            - Word wrapped cells show focus border when selected

            Prompt: "can you make it so you can resize columns from anywhere like you can with row"
            Changes:
            - Added column resizing from anywhere in spreadsheet
            - Hover near column border shows resize cursor
            - Minimum column width: 20 pixels

            Prompt: "When you click off the spreadsheet deselect it entirely even if your editing a cell"
            Changes:
            - Added FocusListener to spreadsheet table
            - Clicking outside spreadsheet stops editing and clears selection
            """;

        JTextArea textArea = new JTextArea(changeLog);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(closeBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showPreferencesDialog() {
        JDialog dialog = new JDialog(this, "Preferences", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Settings Tab
        JPanel settingsTab = new JPanel(new GridLayout(8, 2, 10, 8));
        settingsTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Interior with gradient arrow
        settingsTab.add(new JLabel("Interior:"));
        JPanel settingsInteriorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton settingsInteriorBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (settingsUseGradient && settingsGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[settingsGradientStops.size()];
                    Color[] colors = new Color[settingsGradientStops.size()];
                    for (int i = 0; i < settingsGradientStops.size(); i++) {
                        float[] stop = settingsGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        settingsInteriorBtn.setPreferredSize(new Dimension(60, 25));
        settingsInteriorBtn.setBackground(settingsInteriorColor);
        final JButton finalSettingsInteriorBtn = settingsInteriorBtn;
        settingsInteriorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(dialog, "Choose Interior Color", settingsInteriorColor);
            if (c != null) {
                settingsInteriorColor = c;
                settingsUseGradient = false;
                finalSettingsInteriorBtn.setBackground(c);
                finalSettingsInteriorBtn.repaint();
            }
        });
        settingsInteriorPanel.add(settingsInteriorBtn);

        JButton settingsGradientArrowBtn = new JButton("\u25BC");
        settingsGradientArrowBtn.setPreferredSize(new Dimension(20, 25));
        settingsGradientArrowBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        settingsGradientArrowBtn.setMargin(new Insets(0, 0, 0, 0));
        settingsGradientArrowBtn.setToolTipText("Gradient options");
        JPopupMenu settingsGradientMenu = new JPopupMenu();
        JMenuItem settingsGradientMenuItem = new JMenuItem("Gradient...");
        settingsGradientMenuItem.addActionListener(ev -> showGradientDialog("settings"));
        settingsGradientMenu.add(settingsGradientMenuItem);
        settingsGradientArrowBtn.addActionListener(ev -> {
            settingsGradientMenu.show(settingsGradientArrowBtn, 0, settingsGradientArrowBtn.getHeight());
        });
        settingsInteriorPanel.add(settingsGradientArrowBtn);
        settingsTab.add(settingsInteriorPanel);
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

        // Layers Tab with gradient support
        JPanel layersTab = new JPanel(new GridBagLayout());
        layersTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints lgbc = new GridBagConstraints();
        lgbc.insets = new Insets(4, 4, 4, 4);
        lgbc.anchor = GridBagConstraints.WEST;
        lgbc.fill = GridBagConstraints.NONE;

        // === LAYERS HEADER ROW ===
        lgbc.gridx = 0; lgbc.gridy = 0;
        layersTab.add(new JLabel("Header:"), lgbc);

        JButton layersHeaderBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (layersHeaderUseGradient && layersHeaderGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[layersHeaderGradientStops.size()];
                    Color[] colors = new Color[layersHeaderGradientStops.size()];
                    for (int i = 0; i < layersHeaderGradientStops.size(); i++) {
                        float[] stop = layersHeaderGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        layersHeaderBtn.setPreferredSize(new Dimension(60, 25));
        layersHeaderBtn.setBackground(layersHeaderColor);
        final JButton finalLayersHeaderBtn = layersHeaderBtn;
        layersHeaderBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Layers Header Color", layersHeaderColor, color -> {
                layersHeaderColor = color;
                layersHeaderUseGradient = false;
                finalLayersHeaderBtn.setBackground(color);
                finalLayersHeaderBtn.repaint();
                applyLayersPanelColors();
            });
            if (newColor != null) {
                layersHeaderColor = newColor;
                layersHeaderUseGradient = false;
                finalLayersHeaderBtn.setBackground(newColor);
                finalLayersHeaderBtn.repaint();
                applyLayersPanelColors();
            }
        });
        lgbc.gridx = 1; lgbc.gridy = 0;
        layersTab.add(layersHeaderBtn, lgbc);

        JButton layersHeaderGradientArrowBtn = new JButton("\u25BC");
        layersHeaderGradientArrowBtn.setPreferredSize(new Dimension(20, 25));
        layersHeaderGradientArrowBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        layersHeaderGradientArrowBtn.setMargin(new Insets(0, 0, 0, 0));
        layersHeaderGradientArrowBtn.setToolTipText("Gradient options");
        JPopupMenu layersHeaderGradientMenu = new JPopupMenu();
        JMenuItem layersHeaderGradientMenuItem = new JMenuItem("Gradient...");
        layersHeaderGradientMenuItem.addActionListener(ev -> {
            showGradientDialog("layersHeader");
            finalLayersHeaderBtn.repaint();
        });
        layersHeaderGradientMenu.add(layersHeaderGradientMenuItem);
        layersHeaderGradientArrowBtn.addActionListener(ev -> {
            layersHeaderGradientMenu.show(layersHeaderGradientArrowBtn, 0, layersHeaderGradientArrowBtn.getHeight());
        });
        lgbc.gridx = 2; lgbc.gridy = 0;
        layersTab.add(layersHeaderGradientArrowBtn, lgbc);

        // === LAYERS INTERIOR ROW ===
        lgbc.gridx = 0; lgbc.gridy = 1;
        layersTab.add(new JLabel("Interior:"), lgbc);

        JButton layersInteriorBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (layersUseGradient && layersGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[layersGradientStops.size()];
                    Color[] colors = new Color[layersGradientStops.size()];
                    for (int i = 0; i < layersGradientStops.size(); i++) {
                        float[] stop = layersGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        layersInteriorBtn.setPreferredSize(new Dimension(60, 25));
        layersInteriorBtn.setBackground(layersInteriorColor);
        final JButton finalLayersInteriorBtn = layersInteriorBtn;
        layersInteriorBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Layers Interior Color", layersInteriorColor, color -> {
                layersInteriorColor = color;
                layersUseGradient = false;
                finalLayersInteriorBtn.setBackground(color);
                finalLayersInteriorBtn.repaint();
                applyLayersPanelColors();
            });
            if (newColor != null) {
                layersInteriorColor = newColor;
                layersUseGradient = false;
                finalLayersInteriorBtn.setBackground(newColor);
                finalLayersInteriorBtn.repaint();
                applyLayersPanelColors();
            }
        });
        lgbc.gridx = 1; lgbc.gridy = 1;
        layersTab.add(layersInteriorBtn, lgbc);

        JButton layersInteriorGradientArrowBtn = new JButton("\u25BC");
        layersInteriorGradientArrowBtn.setPreferredSize(new Dimension(20, 25));
        layersInteriorGradientArrowBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        layersInteriorGradientArrowBtn.setMargin(new Insets(0, 0, 0, 0));
        layersInteriorGradientArrowBtn.setToolTipText("Gradient options");
        JPopupMenu layersInteriorGradientMenu = new JPopupMenu();
        JMenuItem layersInteriorGradientMenuItem = new JMenuItem("Gradient...");
        layersInteriorGradientMenuItem.addActionListener(ev -> {
            showGradientDialog("layers");
            finalLayersInteriorBtn.repaint();
        });
        layersInteriorGradientMenu.add(layersInteriorGradientMenuItem);
        layersInteriorGradientArrowBtn.addActionListener(ev -> {
            layersInteriorGradientMenu.show(layersInteriorGradientArrowBtn, 0, layersInteriorGradientArrowBtn.getHeight());
        });
        lgbc.gridx = 2; lgbc.gridy = 1;
        layersTab.add(layersInteriorGradientArrowBtn, lgbc);

        // === LAYERS BACKGROUND (List) ROW ===
        lgbc.gridx = 0; lgbc.gridy = 2;
        layersTab.add(new JLabel("Background:"), lgbc);

        JButton layersListBgBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (layersListBgUseGradient && layersListBgGradientStops.size() >= 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    float[] fractions = new float[layersListBgGradientStops.size()];
                    Color[] colors = new Color[layersListBgGradientStops.size()];
                    for (int i = 0; i < layersListBgGradientStops.size(); i++) {
                        float[] stop = layersListBgGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }
                    java.awt.LinearGradientPaint lgp = new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        layersListBgBtn.setPreferredSize(new Dimension(60, 25));
        layersListBgBtn.setBackground(layersListBgColor);
        final JButton finalLayersListBgBtn = layersListBgBtn;
        layersListBgBtn.addActionListener(e -> {
            Color newColor = showColorChooserWithAlpha("Choose Layers Background Color", layersListBgColor, color -> {
                layersListBgColor = color;
                layersListBgUseGradient = false;
                finalLayersListBgBtn.setBackground(color);
                finalLayersListBgBtn.repaint();
                applyLayersListColors();
            });
            if (newColor != null) {
                layersListBgColor = newColor;
                layersListBgUseGradient = false;
                finalLayersListBgBtn.setBackground(newColor);
                finalLayersListBgBtn.repaint();
                applyLayersListColors();
            }
        });
        lgbc.gridx = 1; lgbc.gridy = 2;
        layersTab.add(layersListBgBtn, lgbc);

        JButton layersListBgGradientArrowBtn = new JButton("\u25BC");
        layersListBgGradientArrowBtn.setPreferredSize(new Dimension(20, 25));
        layersListBgGradientArrowBtn.setFont(new Font("Arial", Font.PLAIN, 8));
        layersListBgGradientArrowBtn.setMargin(new Insets(0, 0, 0, 0));
        layersListBgGradientArrowBtn.setToolTipText("Gradient options");
        JPopupMenu layersListBgGradientMenu = new JPopupMenu();
        JMenuItem layersListBgGradientMenuItem = new JMenuItem("Gradient...");
        layersListBgGradientMenuItem.addActionListener(ev -> {
            showGradientDialog("layersListBg");
            finalLayersListBgBtn.repaint();
        });
        layersListBgGradientMenu.add(layersListBgGradientMenuItem);
        layersListBgGradientArrowBtn.addActionListener(ev -> {
            layersListBgGradientMenu.show(layersListBgGradientArrowBtn, 0, layersListBgGradientArrowBtn.getHeight());
        });
        lgbc.gridx = 2; lgbc.gridy = 2;
        layersTab.add(layersListBgGradientArrowBtn, lgbc);

        // Add filler to push content to top
        lgbc.gridx = 0; lgbc.gridy = 3;
        lgbc.gridwidth = 3;
        lgbc.weighty = 1.0;
        lgbc.fill = GridBagConstraints.BOTH;
        layersTab.add(new JPanel(), lgbc);

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

        // Shortcuts Tab
        JPanel shortcutsTab = new JPanel(new GridBagLayout());
        shortcutsTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Key combo options
        String[] keyOptions = {"Tab", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12",
            "Delete", "Insert", "Home", "End", "Page Up", "Page Down", "Space", "Enter", "Escape"};
        String[] modifierOptions = {"None", "Shift", "Ctrl", "Alt", "Ctrl+Shift", "Ctrl+Alt", "Shift+Alt"};

        // Select Next shortcut
        gbc.gridx = 0; gbc.gridy = 0;
        shortcutsTab.add(new JLabel("Select Next:"), gbc);
        JComboBox<String> selectNextKeyCombo = new JComboBox<>(keyOptions);
        selectNextKeyCombo.setSelectedItem(getKeyName(selectNextKey));
        gbc.gridx = 1;
        shortcutsTab.add(selectNextKeyCombo, gbc);
        JComboBox<String> selectNextModCombo = new JComboBox<>(modifierOptions);
        selectNextModCombo.setSelectedItem(getModifierName(selectNextModifiers));
        gbc.gridx = 2;
        shortcutsTab.add(selectNextModCombo, gbc);

        // Delete Selected shortcut
        gbc.gridx = 0; gbc.gridy = 1;
        shortcutsTab.add(new JLabel("Delete Selected:"), gbc);
        JComboBox<String> deleteKeyCombo = new JComboBox<>(keyOptions);
        deleteKeyCombo.setSelectedItem(getKeyName(deleteSelectedKey));
        gbc.gridx = 1;
        shortcutsTab.add(deleteKeyCombo, gbc);
        JComboBox<String> deleteModCombo = new JComboBox<>(modifierOptions);
        deleteModCombo.setSelectedItem(getModifierName(deleteSelectedModifiers));
        gbc.gridx = 2;
        shortcutsTab.add(deleteModCombo, gbc);

        // Duplicate shortcut
        gbc.gridx = 0; gbc.gridy = 2;
        shortcutsTab.add(new JLabel("Duplicate Task:"), gbc);
        JComboBox<String> duplicateKeyCombo = new JComboBox<>(keyOptions);
        duplicateKeyCombo.setSelectedItem(getKeyName(duplicateKey));
        gbc.gridx = 1;
        shortcutsTab.add(duplicateKeyCombo, gbc);
        JComboBox<String> duplicateModCombo = new JComboBox<>(modifierOptions);
        duplicateModCombo.setSelectedItem(getModifierName(duplicateModifiers));
        gbc.gridx = 2;
        shortcutsTab.add(duplicateModCombo, gbc);

        // Apply shortcuts button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton applyShortcutsBtn = new JButton("Apply Shortcuts");
        applyShortcutsBtn.addActionListener(ev -> {
            selectNextKey = getKeyCode((String) selectNextKeyCombo.getSelectedItem());
            selectNextModifiers = getModifierCode((String) selectNextModCombo.getSelectedItem());
            deleteSelectedKey = getKeyCode((String) deleteKeyCombo.getSelectedItem());
            deleteSelectedModifiers = getModifierCode((String) deleteModCombo.getSelectedItem());
            duplicateKey = getKeyCode((String) duplicateKeyCombo.getSelectedItem());
            duplicateModifiers = getModifierCode((String) duplicateModCombo.getSelectedItem());
            JOptionPane.showMessageDialog(dialog, "Shortcuts updated successfully!");
        });
        shortcutsTab.add(applyShortcutsBtn, gbc);

        tabbedPane.addTab("Shortcuts", shortcutsTab);

        dialog.add(tabbedPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(e -> {
            applyPanelColors();
            dialog.dispose();
        });
        JButton applyBtn = new JButton("Apply");
        applyBtn.addActionListener(e -> {
            applyPanelColors();
        });
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonsPanel.add(okBtn);
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
            Color c = showColorChooserWithAlpha("Choose Color", btn.getBackground(), color -> {
                setter.accept(color);
                btn.setBackground(color);
                applyPanelColors();
            });
            if (c != null) {
                setter.accept(c);
                btn.setBackground(c);
                applyPanelColors();
            }
        });
        return btn;
    }

    private String getKeyName(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_TAB: return "Tab";
            case KeyEvent.VK_F1: return "F1";
            case KeyEvent.VK_F2: return "F2";
            case KeyEvent.VK_F3: return "F3";
            case KeyEvent.VK_F4: return "F4";
            case KeyEvent.VK_F5: return "F5";
            case KeyEvent.VK_F6: return "F6";
            case KeyEvent.VK_F7: return "F7";
            case KeyEvent.VK_F8: return "F8";
            case KeyEvent.VK_F9: return "F9";
            case KeyEvent.VK_F10: return "F10";
            case KeyEvent.VK_F11: return "F11";
            case KeyEvent.VK_F12: return "F12";
            case KeyEvent.VK_DELETE: return "Delete";
            case KeyEvent.VK_INSERT: return "Insert";
            case KeyEvent.VK_HOME: return "Home";
            case KeyEvent.VK_END: return "End";
            case KeyEvent.VK_PAGE_UP: return "Page Up";
            case KeyEvent.VK_PAGE_DOWN: return "Page Down";
            case KeyEvent.VK_SPACE: return "Space";
            case KeyEvent.VK_ENTER: return "Enter";
            case KeyEvent.VK_ESCAPE: return "Escape";
            default: return "Tab";
        }
    }

    private int getKeyCode(String keyName) {
        switch (keyName) {
            case "Tab": return KeyEvent.VK_TAB;
            case "F1": return KeyEvent.VK_F1;
            case "F2": return KeyEvent.VK_F2;
            case "F3": return KeyEvent.VK_F3;
            case "F4": return KeyEvent.VK_F4;
            case "F5": return KeyEvent.VK_F5;
            case "F6": return KeyEvent.VK_F6;
            case "F7": return KeyEvent.VK_F7;
            case "F8": return KeyEvent.VK_F8;
            case "F9": return KeyEvent.VK_F9;
            case "F10": return KeyEvent.VK_F10;
            case "F11": return KeyEvent.VK_F11;
            case "F12": return KeyEvent.VK_F12;
            case "Delete": return KeyEvent.VK_DELETE;
            case "Insert": return KeyEvent.VK_INSERT;
            case "Home": return KeyEvent.VK_HOME;
            case "End": return KeyEvent.VK_END;
            case "Page Up": return KeyEvent.VK_PAGE_UP;
            case "Page Down": return KeyEvent.VK_PAGE_DOWN;
            case "Space": return KeyEvent.VK_SPACE;
            case "Enter": return KeyEvent.VK_ENTER;
            case "Escape": return KeyEvent.VK_ESCAPE;
            default: return KeyEvent.VK_TAB;
        }
    }

    private String getModifierName(int modifiers) {
        if (modifiers == 0) return "None";
        if (modifiers == InputEvent.SHIFT_DOWN_MASK) return "Shift";
        if (modifiers == InputEvent.CTRL_DOWN_MASK) return "Ctrl";
        if (modifiers == InputEvent.ALT_DOWN_MASK) return "Alt";
        if (modifiers == (InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) return "Ctrl+Shift";
        if (modifiers == (InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK)) return "Ctrl+Alt";
        if (modifiers == (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK)) return "Shift+Alt";
        return "None";
    }

    private int getModifierCode(String modifierName) {
        switch (modifierName) {
            case "None": return 0;
            case "Shift": return InputEvent.SHIFT_DOWN_MASK;
            case "Ctrl": return InputEvent.CTRL_DOWN_MASK;
            case "Alt": return InputEvent.ALT_DOWN_MASK;
            case "Ctrl+Shift": return InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK;
            case "Ctrl+Alt": return InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK;
            case "Shift+Alt": return InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK;
            default: return 0;
        }
    }

    private void applyPanelColors() {
        // Apply settings panel colors
        if (settingsPanel != null) {
            settingsPanel.setBackground(settingsInteriorColor);
            settingsPanel.repaint();
        }
        // Apply timeline panel colors
        if (timelineDisplayPanel != null) {
            timelineDisplayPanel.setBackground(timelineInteriorColor);
            timelineDisplayPanel.repaint();
        }
        // Apply layers panel colors
        if (rightPanel != null) {
            rightPanel.applyColors(layersInteriorColor, layersOutlineColor, layersHeaderColor, layersHeaderTextColor);
        }
        // Apply format panel colors
        if (formatPanel != null) {
            formatPanel.setBackground(formatInteriorColor);
            applyColorToChildren(formatPanel, formatInteriorColor);
            formatPanel.repaint();
        }
        revalidate();
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

    private void applyFormatPanelColors() {
        if (formatPanel != null) {
            formatPanel.setBackground(formatInteriorColor);
            applyColorToChildren(formatPanel, formatInteriorColor);
            // Update format header bar background color
            if (formatHeaderBar != null) {
                formatHeaderBar.setBackground(formatHeaderColor);
                formatHeaderBar.repaint();
            }
            // Update format tabbed pane tab colors with custom gradient UI
            if (formatTabbedPane != null) {
                // Reinstall custom UI to pick up new colors
                formatTabbedPane.setUI(new GradientTabbedPaneUI());
                formatTabbedPane.setBackground(formatTabColor);
                // Update individual tab content panels
                for (int i = 0; i < formatTabbedPane.getTabCount(); i++) {
                    Component comp = formatTabbedPane.getComponentAt(i);
                    if (comp instanceof JPanel) {
                        ((JPanel) comp).setOpaque(false); // Let custom UI paint background
                    }
                }
                formatTabbedPane.repaint();
            }
            formatPanel.revalidate();
            formatPanel.repaint();
        }
    }

    private void applyTabContentBorders() {
        // Apply borders to all tab content in rightTabbedPane
        Color borderColor = settingsOutlineColor; // Use unified border color
        boolean visible = settingsBorderVisible;

        // Layers tab - direct panel
        if (layersPanel != null) {
            if (visible) {
                layersPanel.setBorder(BorderFactory.createLineBorder(borderColor));
            } else {
                layersPanel.setBorder(null);
            }
        }

        // General tab - scroll pane
        if (generalScrollPane != null) {
            if (visible) {
                generalScrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
            } else {
                generalScrollPane.setBorder(null);
            }
        }

        // Time Axis tab - scroll pane
        if (settingsScrollPane != null) {
            if (visible) {
                settingsScrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
            } else {
                settingsScrollPane.setBorder(null);
            }
        }
    }

    private void applyLayersPanelColors() {
        applyRightPanelColors();
        applyLayersListColors();
    }

    private void applyRightPanelColors() {
        if (rightPanel != null) {
            rightPanel.applyColors(layersInteriorColor, layersOutlineColor, layersHeaderColor, layersHeaderTextColor);
            rightPanel.repaint();
        }
    }

    private void applyLayersListColors() {
        if (layersPanel != null) {
            layersPanel.setListBackground(layersListBgColor);
            layersPanel.refreshList();
            layersPanel.repaint();
        }
        // Apply tab content borders (includes layers panel)
        applyTabContentBorders();
    }

    private void applySettingsPanelColors() {
        if (settingsPanel != null) {
            settingsPanel.setBackground(settingsInteriorColor);
            // Apply to all child components
            applyColorToChildren(settingsPanel, settingsInteriorColor);
            settingsPanel.repaint();
        }
        // Apply tab content borders (includes settings scroll pane)
        applyTabContentBorders();
    }

    private void applyGeneralPanelColors() {
        if (generalPanel != null) {
            generalPanel.setBackground(generalInteriorColor);
            // Apply to all child components
            applyColorToChildren(generalPanel, generalInteriorColor);
            generalPanel.repaint();
        }
        // Apply tab content borders (includes general scroll pane)
        applyTabContentBorders();
    }

    private void applyRightTabbedPaneColors() {
        if (rightTabbedPane != null) {
            // Set FlatLaf UI defaults
            UIManager.put("TabbedPane.selectedBackground", rightTabbedSelectedBgColor);
            UIManager.put("TabbedPane.selectedForeground", rightTabbedSelectedFgColor);

            // Underline properties - only apply when visible is checked
            if (rightTabbedUnderlineVisible) {
                UIManager.put("TabbedPane.underlineColor", rightTabbedUnderlineColor);
                UIManager.put("TabbedPane.tabUnderlineColor", rightTabbedUnderlineColor);
                UIManager.put("TabbedPane.contentAreaColor", rightTabbedUnderlineColor);
                UIManager.put("TabbedPane.focusColor", rightTabbedUnderlineColor);
                UIManager.put("TabbedPane.hoverColor", rightTabbedUnderlineColor);
            } else {
                // Reset to default/transparent when not visible
                UIManager.put("TabbedPane.underlineColor", null);
                UIManager.put("TabbedPane.tabUnderlineColor", null);
                UIManager.put("TabbedPane.contentAreaColor", null);
                UIManager.put("TabbedPane.focusColor", null);
                UIManager.put("TabbedPane.hoverColor", null);
            }

            // Force UI refresh to pick up new colors
            rightTabbedPane.updateUI();

            rightTabbedPane.setBackground(rightTabbedBgColor);
            rightTabbedPane.setForeground(rightTabbedFgColor);

            // Update border (separator)
            if (rightTabbedBorderVisible) {
                rightTabbedPane.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, rightTabbedBorderColor),
                    BorderFactory.createEmptyBorder(10, 0, 0, 0)));
            } else {
                rightTabbedPane.setBorder(BorderFactory.createEmptyBorder(11, 0, 0, 0));
            }

            // Update tab colors for each tab (background = unselected tab color)
            for (int i = 0; i < rightTabbedPane.getTabCount(); i++) {
                rightTabbedPane.setBackgroundAt(i, rightTabbedBgColor);
                rightTabbedPane.setForegroundAt(i, rightTabbedFgColor);
            }

            rightTabbedPane.revalidate();
            rightTabbedPane.repaint();
        }
    }


    /**
     * Apply colors to all panels and tabs in the application.
     * Call this method to update all UI colors at once.
     *
     * Panels/Tabs available:
     * - Toolbar: toolbarBgColor
     * - Settings/Time Axis: settingsInteriorColor, settingsHeaderColor
     * - Layers: layersInteriorColor, layersHeaderColor, layersListBgColor
     * - Format: formatInteriorColor, formatHeaderColor
     * - Timeline: timelineInteriorColor, timelineLineColor, timelineGridColor
     */
    private void applyAllPanelColors() {
        // Toolbar panel
        if (toolbarPanel != null) {
            toolbarPanel.setBackground(toolbarBgColor);
            toolbarPanel.repaint();
        }

        // Settings/Time Axis panel
        if (settingsPanel != null) {
            settingsPanel.setBackground(settingsInteriorColor);
            settingsPanel.repaint();
        }

        // Format panel
        applyFormatPanelColors();

        // Layers panel
        applyLayersPanelColors();

        // Timeline display
        if (timelineDisplayPanel != null) {
            timelineDisplayPanel.repaint();
        }
    }
    private void setFormatChildrenOpaque(JPanel panel, boolean opaque) {
        if (panel != formatHeaderBar) { // Don't affect the header bar
            panel.setOpaque(opaque);
        }
        for (Component c : panel.getComponents()) {
            if (c instanceof JPanel) {
                setFormatChildrenOpaque((JPanel) c, opaque);
            }
        }
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    // Export timeline as image file
    private void exportGraphic() {
        // Create resolution options dialog
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int currentWidth = timelineDisplayPanel.getWidth();
        int currentHeight = timelineDisplayPanel.getHeight();
        double aspectRatio = (double) currentHeight / currentWidth;

        // Preset dropdown
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Preset:"), gbc);

        String[] presetOptions = {"Low (1000px)", "Medium (2000px)", "High (4000px)", "Very High (6000px)", "Ultra (8000px)"};
        int[] presetWidths = {1000, 2000, 4000, 6000, 8000};
        JComboBox<String> presetCombo = new JComboBox<>(presetOptions);
        presetCombo.setSelectedIndex(2); // Default to High
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(presetCombo, gbc);

        // Width input field
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Width (px):"), gbc);

        JTextField widthField = new JTextField(String.valueOf(presetWidths[2]), 8);
        gbc.gridx = 1;
        panel.add(widthField, gbc);

        // Height input field
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Height (px):"), gbc);

        int initialHeight = (int) (presetWidths[2] * aspectRatio);
        JTextField heightField = new JTextField(String.valueOf(initialHeight), 8);
        gbc.gridx = 1;
        panel.add(heightField, gbc);

        // Lock proportions checkbox
        JCheckBox lockProportionsCheckbox = new JCheckBox("Lock proportions", true);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(lockProportionsCheckbox, gbc);

        // Format options
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        panel.add(new JLabel("Format:"), gbc);

        String[] formatOptions = {"PNG (Lossless)", "JPEG (Smaller file)"};
        JComboBox<String> formatCombo = new JComboBox<>(formatOptions);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(formatCombo, gbc);

        // Flag to prevent recursive updates
        final boolean[] updating = {false};

        // Width field listener - update height if locked
        widthField.addCaretListener(e -> {
            if (updating[0] || !lockProportionsCheckbox.isSelected()) return;
            try {
                int w = Integer.parseInt(widthField.getText().trim());
                if (w > 0) {
                    updating[0] = true;
                    int h = (int) (w * aspectRatio);
                    heightField.setText(String.valueOf(h));
                    updating[0] = false;
                }
            } catch (NumberFormatException ex) {}
        });

        // Height field listener - update width if locked
        heightField.addCaretListener(e -> {
            if (updating[0] || !lockProportionsCheckbox.isSelected()) return;
            try {
                int h = Integer.parseInt(heightField.getText().trim());
                if (h > 0) {
                    updating[0] = true;
                    int w = (int) (h / aspectRatio);
                    widthField.setText(String.valueOf(w));
                    updating[0] = false;
                }
            } catch (NumberFormatException ex) {}
        });

        // Preset dropdown populates width/height fields
        presetCombo.addActionListener(e -> {
            updating[0] = true;
            int selectedWidth = presetWidths[presetCombo.getSelectedIndex()];
            int selectedHeight = (int) (selectedWidth * aspectRatio);
            widthField.setText(String.valueOf(selectedWidth));
            heightField.setText(String.valueOf(selectedHeight));
            updating[0] = false;
        });

        int result = JOptionPane.showConfirmDialog(this, panel, "Export Graphic Options",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        // Parse width and height from fields
        int outputWidth, outputHeight;
        try {
            outputWidth = Integer.parseInt(widthField.getText().trim());
            outputHeight = Integer.parseInt(heightField.getText().trim());
            if (outputWidth < 100) outputWidth = 100;
            if (outputHeight < 100) outputHeight = 100;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid width or height value.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double scaleX = (double) outputWidth / currentWidth;
        double scaleY = (double) outputHeight / currentHeight;
        String format = formatCombo.getSelectedIndex() == 0 ? "png" : "jpg";
        String formatDesc = formatCombo.getSelectedIndex() == 0 ? "PNG Image (*.png)" : "JPEG Image (*.jpg)";

        // File chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Timeline Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter(formatDesc, format));
        fileChooser.setSelectedFile(new File("timeline_export." + format));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith("." + format)) {
                file = new File(file.getAbsolutePath() + "." + format);
            }

            try {
                // Create scaled image
                BufferedImage image = new BufferedImage(outputWidth, outputHeight,
                        format.equals("png") ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();

                // Set rendering hints for quality
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

                // Scale the graphics context
                g2d.scale(scaleX, scaleY);

                // Fill background for JPEG (no transparency)
                if (format.equals("jpg")) {
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(0, 0, currentWidth, currentHeight);
                }

                // Paint the timeline panel
                timelineDisplayPanel.paint(g2d);
                g2d.dispose();

                // Write to file
                ImageIO.write(image, format.toUpperCase(), file);

                JOptionPane.showMessageDialog(this,
                        "Exported timeline graphic to:\n" + file.getAbsolutePath() +
                        "\nSize: " + outputWidth + " x " + outputHeight + " pixels",
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error exporting graphic: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void restartProgram() {
        try {
            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            String classpath = System.getProperty("java.class.path");
            String className = Timeline2.class.getName();

            ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);
            builder.inheritIO();
            builder.start();

            System.exit(0);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error restarting program: " + ex.getMessage(),
                    "Restart Error", JOptionPane.ERROR_MESSAGE);
        }
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

            // Sheet 4: Notes
            zos.putNextEntry(new ZipEntry("xl/worksheets/sheet4.xml"));
            zos.write(getSheet4Xml().getBytes(StandardCharsets.UTF_8));
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
               "  <Override PartName=\"/xl/worksheets/sheet4.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n" +
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
               "    <sheet name=\"Notes\" sheetId=\"4\" r:id=\"rId4\"/>\n" +
               "  </sheets>\n" +
               "</workbook>";
    }

    private String getWorkbookRelsXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
               "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>\n" +
               "  <Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet2.xml\"/>\n" +
               "  <Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet3.xml\"/>\n" +
               "  <Relationship Id=\"rId4\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet4.xml\"/>\n" +
               "  <Relationship Id=\"rId5\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>\n" +
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
                "Font Size", "Font Bold", "Font Italic", "Text Color", "Center Text Wrap", "Center Text Visible",
                "Front Text", "Front Font Size", "Front Bold", "Front Italic", "Front Text Color", "Front Text Wrap", "Front Text Visible",
                "Above Text", "Above Font Size", "Above Bold", "Above Italic", "Above Text Color", "Above Text Wrap", "Above Text Visible",
                "Underneath Text", "Underneath Font Size", "Underneath Bold", "Underneath Italic", "Underneath Text Color", "Underneath Text Wrap", "Underneath Text Visible",
                "Behind Text", "Behind Font Size", "Behind Bold", "Behind Italic", "Behind Text Color", "Behind Text Wrap", "Behind Text Visible",
                "Shape", "Width", "Label Text", "Label Text Visible",
                "Bevel Fill", "Bevel Depth", "Bevel Light Angle", "Bevel Highlight Opacity", "Bevel Shadow Opacity",
                "Bevel Style", "Top Bevel", "Bottom Bevel"};
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
                String.valueOf(task.centerTextWrap), String.valueOf(task.centerTextVisible),
                escapeXml(task.frontText), String.valueOf(task.frontFontSize), String.valueOf(task.frontFontBold),
                String.valueOf(task.frontFontItalic), colorToHex(task.frontTextColor), String.valueOf(task.frontTextWrap), String.valueOf(task.frontTextVisible),
                escapeXml(task.aboveText), String.valueOf(task.aboveFontSize), String.valueOf(task.aboveFontBold),
                String.valueOf(task.aboveFontItalic), colorToHex(task.aboveTextColor), String.valueOf(task.aboveTextWrap), String.valueOf(task.aboveTextVisible),
                escapeXml(task.underneathText), String.valueOf(task.underneathFontSize), String.valueOf(task.underneathFontBold),
                String.valueOf(task.underneathFontItalic), colorToHex(task.underneathTextColor), String.valueOf(task.underneathTextWrap), String.valueOf(task.underneathTextVisible),
                escapeXml(task.behindText), String.valueOf(task.behindFontSize), String.valueOf(task.behindFontBold),
                String.valueOf(task.behindFontItalic), colorToHex(task.behindTextColor), String.valueOf(task.behindTextWrap), String.valueOf(task.behindTextVisible),
                "", "", "", "", // Shape, Width, Label Text, Label Text Visible (not applicable for tasks)
                String.valueOf(task.bevelFill), String.valueOf(task.bevelDepth), String.valueOf(task.bevelLightAngle),
                String.valueOf(task.bevelHighlightOpacity), String.valueOf(task.bevelShadowOpacity),
                escapeXml(task.bevelStyle), escapeXml(task.topBevel), escapeXml(task.bottomBevel)
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
                String.valueOf(m.centerTextWrap), String.valueOf(m.centerTextVisible),
                escapeXml(m.frontText), String.valueOf(m.frontFontSize), String.valueOf(m.frontFontBold),
                String.valueOf(m.frontFontItalic), colorToHex(m.frontTextColor), String.valueOf(m.frontTextWrap), String.valueOf(m.frontTextVisible),
                escapeXml(m.aboveText), String.valueOf(m.aboveFontSize), String.valueOf(m.aboveFontBold),
                String.valueOf(m.aboveFontItalic), colorToHex(m.aboveTextColor), String.valueOf(m.aboveTextWrap), String.valueOf(m.aboveTextVisible),
                escapeXml(m.underneathText), String.valueOf(m.underneathFontSize), String.valueOf(m.underneathFontBold),
                String.valueOf(m.underneathFontItalic), colorToHex(m.underneathTextColor), String.valueOf(m.underneathTextWrap), String.valueOf(m.underneathTextVisible),
                escapeXml(m.behindText), String.valueOf(m.behindFontSize), String.valueOf(m.behindFontBold),
                String.valueOf(m.behindFontItalic), colorToHex(m.behindTextColor), String.valueOf(m.behindTextWrap), String.valueOf(m.behindTextVisible),
                escapeXml(m.shape), String.valueOf(m.width), escapeXml(m.centerText), String.valueOf(m.centerTextVisible),
                String.valueOf(m.bevelFill), String.valueOf(m.bevelDepth), String.valueOf(m.bevelLightAngle),
                String.valueOf(m.bevelHighlightOpacity), String.valueOf(m.bevelShadowOpacity),
                escapeXml(m.bevelStyle), escapeXml(m.topBevel), escapeXml(m.bottomBevel)
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
        row = addSettingRow(sb, row, "Format Resize Handle Color", colorToHex(formatResizeHandleColor));
        // Extend ticks settings
        row = addSettingRow(sb, row, "Extend Ticks Enabled", String.valueOf(extendTicks));
        row = addSettingRow(sb, row, "Extend Ticks Color", colorToHex(extendTicksColor));
        row = addSettingRow(sb, row, "Extend Ticks Thickness", String.valueOf(extendTicksThickness));
        row = addSettingRow(sb, row, "Extend Ticks Line Type", extendTicksLineType);
        // Timeline axis settings
        row = addSettingRow(sb, row, "Timeline Axis Color", colorToHex(timelineAxisColor));
        row = addSettingRow(sb, row, "Timeline Axis Thickness", String.valueOf(timelineAxisThickness));
        // Timeline axis date label settings
        row = addSettingRow(sb, row, "Axis Date Color", colorToHex(axisDateColor));
        row = addSettingRow(sb, row, "Axis Date Font Family", axisDateFontFamily);
        row = addSettingRow(sb, row, "Axis Date Font Size", String.valueOf(axisDateFontSize));
        row = addSettingRow(sb, row, "Axis Date Bold", String.valueOf(axisDateBold));
        row = addSettingRow(sb, row, "Axis Date Italic", String.valueOf(axisDateItalic));

        sb.append("  </sheetData>\n");
        sb.append("</worksheet>");
        return sb.toString();
    }

    // Sheet 4: Notes (Type, Name, Note 1-5)
    private String getSheet4Xml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n");
        sb.append("  <sheetData>\n");

        // Header row
        sb.append("    <row r=\"1\">\n");
        sb.append("      <c r=\"A1\" t=\"inlineStr\" s=\"1\"><is><t>Type</t></is></c>\n");
        sb.append("      <c r=\"B1\" t=\"inlineStr\" s=\"1\"><is><t>Name</t></is></c>\n");
        sb.append("      <c r=\"C1\" t=\"inlineStr\" s=\"1\"><is><t>Note 1</t></is></c>\n");
        sb.append("      <c r=\"D1\" t=\"inlineStr\" s=\"1\"><is><t>Note 2</t></is></c>\n");
        sb.append("      <c r=\"E1\" t=\"inlineStr\" s=\"1\"><is><t>Note 3</t></is></c>\n");
        sb.append("      <c r=\"F1\" t=\"inlineStr\" s=\"1\"><is><t>Note 4</t></is></c>\n");
        sb.append("      <c r=\"G1\" t=\"inlineStr\" s=\"1\"><is><t>Note 5</t></is></c>\n");
        sb.append("    </row>\n");

        int row = 2;
        // Tasks with notes
        for (TimelineTask task : tasks) {
            sb.append("    <row r=\"").append(row).append("\">\n");
            sb.append("      <c r=\"A").append(row).append("\" t=\"inlineStr\"><is><t>Task</t></is></c>\n");
            sb.append("      <c r=\"B").append(row).append("\" t=\"inlineStr\"><is><t>").append(escapeXml(task.name)).append("</t></is></c>\n");
            sb.append("      <c r=\"C").append(row).append("\" t=\"inlineStr\"><is><t>").append(escapeXml(task.note1)).append("</t></is></c>\n");
            sb.append("      <c r=\"D").append(row).append("\" t=\"inlineStr\"><is><t>").append(escapeXml(task.note2)).append("</t></is></c>\n");
            sb.append("      <c r=\"E").append(row).append("\" t=\"inlineStr\"><is><t>").append(escapeXml(task.note3)).append("</t></is></c>\n");
            sb.append("      <c r=\"F").append(row).append("\" t=\"inlineStr\"><is><t>").append(escapeXml(task.note4)).append("</t></is></c>\n");
            sb.append("      <c r=\"G").append(row).append("\" t=\"inlineStr\"><is><t>").append(escapeXml(task.note5)).append("</t></is></c>\n");
            sb.append("    </row>\n");
            row++;
        }

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
            updateSpreadsheet();
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
            selectedMilestoneIndices.clear();
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
                    task.centerText = centerText; // Use exact value from import (constructor already defaults to name)
                    task.fillColor = TASK_COLORS[tasks.size() % TASK_COLORS.length];

                    // Apply format data if available
                    if (fmt != null && fmt.length > 10) {
                        // Determine if new format (with wrap columns) or old format
                        // New format has 40 columns, old format has 35
                        boolean newFormat = fmt.length > 37;

                        task.fillColor = hexToColor(fmt[3]) != null ? hexToColor(fmt[3]) : task.fillColor;
                        task.outlineColor = hexToColor(fmt[4]);
                        task.outlineThickness = parseIntSafe(fmt[5], 2);
                        task.height = parseIntSafe(fmt[6], 25);
                        task.yPosition = parseIntSafe(fmt[7], -1);
                        task.fontSize = parseIntSafe(fmt[8], 11);
                        task.fontBold = "true".equalsIgnoreCase(fmt[9]);
                        task.fontItalic = "true".equalsIgnoreCase(fmt[10]);
                        task.textColor = hexToColor(fmt[11]) != null ? hexToColor(fmt[11]) : Color.BLACK;

                        if (newFormat) {
                            // Check for newest format with visible columns (54 columns)
                            boolean hasVisibleColumns = fmt.length > 50;

                            if (hasVisibleColumns) {
                                // Newest format with visible columns
                                if (fmt.length > 12) task.centerTextWrap = "true".equalsIgnoreCase(fmt[12]);
                                if (fmt.length > 13) task.centerTextVisible = "true".equalsIgnoreCase(fmt[13]);
                                if (fmt.length > 20) {
                                    task.frontText = fmt[14];
                                    task.frontFontSize = parseIntSafe(fmt[15], 10);
                                    task.frontFontBold = "true".equalsIgnoreCase(fmt[16]);
                                    task.frontFontItalic = "true".equalsIgnoreCase(fmt[17]);
                                    task.frontTextColor = hexToColor(fmt[18]) != null ? hexToColor(fmt[18]) : Color.BLACK;
                                    task.frontTextWrap = "true".equalsIgnoreCase(fmt[19]);
                                    task.frontTextVisible = "true".equalsIgnoreCase(fmt[20]);
                                }
                                if (fmt.length > 27) {
                                    task.aboveText = fmt[21];
                                    task.aboveFontSize = parseIntSafe(fmt[22], 10);
                                    task.aboveFontBold = "true".equalsIgnoreCase(fmt[23]);
                                    task.aboveFontItalic = "true".equalsIgnoreCase(fmt[24]);
                                    task.aboveTextColor = hexToColor(fmt[25]) != null ? hexToColor(fmt[25]) : Color.BLACK;
                                    task.aboveTextWrap = "true".equalsIgnoreCase(fmt[26]);
                                    task.aboveTextVisible = "true".equalsIgnoreCase(fmt[27]);
                                }
                                if (fmt.length > 34) {
                                    task.underneathText = fmt[28];
                                    task.underneathFontSize = parseIntSafe(fmt[29], 10);
                                    task.underneathFontBold = "true".equalsIgnoreCase(fmt[30]);
                                    task.underneathFontItalic = "true".equalsIgnoreCase(fmt[31]);
                                    task.underneathTextColor = hexToColor(fmt[32]) != null ? hexToColor(fmt[32]) : Color.BLACK;
                                    task.underneathTextWrap = "true".equalsIgnoreCase(fmt[33]);
                                    task.underneathTextVisible = "true".equalsIgnoreCase(fmt[34]);
                                }
                                if (fmt.length > 41) {
                                    task.behindText = fmt[35];
                                    task.behindFontSize = parseIntSafe(fmt[36], 10);
                                    task.behindFontBold = "true".equalsIgnoreCase(fmt[37]);
                                    task.behindFontItalic = "true".equalsIgnoreCase(fmt[38]);
                                    task.behindTextColor = hexToColor(fmt[39]) != null ? hexToColor(fmt[39]) : new Color(150, 150, 150);
                                    task.behindTextWrap = "true".equalsIgnoreCase(fmt[40]);
                                    task.behindTextVisible = "true".equalsIgnoreCase(fmt[41]);
                                }
                                // Bevel settings (indices 46-53)
                                if (fmt.length > 53) {
                                    task.bevelFill = "true".equalsIgnoreCase(fmt[46]);
                                    task.bevelDepth = parseIntSafe(fmt[47], 60);
                                    task.bevelLightAngle = parseIntSafe(fmt[48], 135);
                                    task.bevelHighlightOpacity = parseIntSafe(fmt[49], 80);
                                    task.bevelShadowOpacity = parseIntSafe(fmt[50], 60);
                                    task.bevelStyle = fmt[51].isEmpty() ? "Inner Bevel" : fmt[51];
                                    task.topBevel = fmt[52].isEmpty() ? "Circle" : fmt[52];
                                    task.bottomBevel = fmt[53].isEmpty() ? "None" : fmt[53];
                                }
                            } else {
                                // Previous format with wrap but no visible columns
                                if (fmt.length > 12) task.centerTextWrap = "true".equalsIgnoreCase(fmt[12]);
                                if (fmt.length > 18) {
                                    task.frontText = fmt[13];
                                    task.frontFontSize = parseIntSafe(fmt[14], 10);
                                    task.frontFontBold = "true".equalsIgnoreCase(fmt[15]);
                                    task.frontFontItalic = "true".equalsIgnoreCase(fmt[16]);
                                    task.frontTextColor = hexToColor(fmt[17]) != null ? hexToColor(fmt[17]) : Color.BLACK;
                                    task.frontTextWrap = "true".equalsIgnoreCase(fmt[18]);
                                }
                                if (fmt.length > 24) {
                                    task.aboveText = fmt[19];
                                    task.aboveFontSize = parseIntSafe(fmt[20], 10);
                                    task.aboveFontBold = "true".equalsIgnoreCase(fmt[21]);
                                    task.aboveFontItalic = "true".equalsIgnoreCase(fmt[22]);
                                    task.aboveTextColor = hexToColor(fmt[23]) != null ? hexToColor(fmt[23]) : Color.BLACK;
                                    task.aboveTextWrap = "true".equalsIgnoreCase(fmt[24]);
                                }
                                if (fmt.length > 30) {
                                    task.underneathText = fmt[25];
                                    task.underneathFontSize = parseIntSafe(fmt[26], 10);
                                    task.underneathFontBold = "true".equalsIgnoreCase(fmt[27]);
                                    task.underneathFontItalic = "true".equalsIgnoreCase(fmt[28]);
                                    task.underneathTextColor = hexToColor(fmt[29]) != null ? hexToColor(fmt[29]) : Color.BLACK;
                                    task.underneathTextWrap = "true".equalsIgnoreCase(fmt[30]);
                                }
                                if (fmt.length > 36) {
                                    task.behindText = fmt[31];
                                    task.behindFontSize = parseIntSafe(fmt[32], 10);
                                    task.behindFontBold = "true".equalsIgnoreCase(fmt[33]);
                                    task.behindFontItalic = "true".equalsIgnoreCase(fmt[34]);
                                    task.behindTextColor = hexToColor(fmt[35]) != null ? hexToColor(fmt[35]) : new Color(150, 150, 150);
                                    task.behindTextWrap = "true".equalsIgnoreCase(fmt[36]);
                                }
                                // Bevel settings (indices 40-47)
                                if (fmt.length > 47) {
                                    task.bevelFill = "true".equalsIgnoreCase(fmt[40]);
                                    task.bevelDepth = parseIntSafe(fmt[41], 60);
                                    task.bevelLightAngle = parseIntSafe(fmt[42], 135);
                                    task.bevelHighlightOpacity = parseIntSafe(fmt[43], 80);
                                    task.bevelShadowOpacity = parseIntSafe(fmt[44], 60);
                                    task.bevelStyle = fmt[45].isEmpty() ? "Inner Bevel" : fmt[45];
                                    task.topBevel = fmt[46].isEmpty() ? "Circle" : fmt[46];
                                    task.bottomBevel = fmt[47].isEmpty() ? "None" : fmt[47];
                                }
                            }
                        } else {
                            // Old format without wrap columns
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
                    }

                    tasks.add(task);
                    layerOrder.add(task);
                    tasksImported++;
                } else if ("Milestone".equalsIgnoreCase(type)) {
                    // Determine format version
                    boolean newFormat = fmt != null && fmt.length > 37;
                    boolean hasVisibleColumns = fmt != null && fmt.length > 50;

                    String shape = "diamond";
                    if (hasVisibleColumns && fmt.length > 42) {
                        shape = fmt[42].isEmpty() ? "diamond" : fmt[42];
                    } else if (newFormat && fmt.length > 37) {
                        shape = fmt[37].isEmpty() ? "diamond" : fmt[37];
                    } else if (fmt != null && fmt.length > 32) {
                        // Old format: shape at index 32
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

                        if (hasVisibleColumns) {
                            // Newest format with visible columns
                            // Center text properties (indices 12-13)
                            if (fmt.length > 13) {
                                m.centerTextWrap = "true".equalsIgnoreCase(fmt[12]);
                                m.centerTextVisible = "true".equalsIgnoreCase(fmt[13]);
                            }
                            // Front text properties (indices 14-20)
                            if (fmt.length > 20) {
                                m.frontText = fmt[14];
                                m.frontFontSize = parseIntSafe(fmt[15], 10);
                                m.frontFontBold = "true".equalsIgnoreCase(fmt[16]);
                                m.frontFontItalic = "true".equalsIgnoreCase(fmt[17]);
                                m.frontTextColor = hexToColor(fmt[18]) != null ? hexToColor(fmt[18]) : Color.BLACK;
                                m.frontTextWrap = "true".equalsIgnoreCase(fmt[19]);
                                m.frontTextVisible = "true".equalsIgnoreCase(fmt[20]);
                            }
                            // Above text properties (indices 21-27)
                            if (fmt.length > 27) {
                                m.aboveText = fmt[21];
                                m.aboveFontSize = parseIntSafe(fmt[22], 10);
                                m.aboveFontBold = "true".equalsIgnoreCase(fmt[23]);
                                m.aboveFontItalic = "true".equalsIgnoreCase(fmt[24]);
                                m.aboveTextColor = hexToColor(fmt[25]) != null ? hexToColor(fmt[25]) : Color.BLACK;
                                m.aboveTextWrap = "true".equalsIgnoreCase(fmt[26]);
                                m.aboveTextVisible = "true".equalsIgnoreCase(fmt[27]);
                            }
                            // Underneath text properties (indices 28-34)
                            if (fmt.length > 34) {
                                m.underneathText = fmt[28];
                                m.underneathFontSize = parseIntSafe(fmt[29], 10);
                                m.underneathFontBold = "true".equalsIgnoreCase(fmt[30]);
                                m.underneathFontItalic = "true".equalsIgnoreCase(fmt[31]);
                                m.underneathTextColor = hexToColor(fmt[32]) != null ? hexToColor(fmt[32]) : Color.BLACK;
                                m.underneathTextWrap = "true".equalsIgnoreCase(fmt[33]);
                                m.underneathTextVisible = "true".equalsIgnoreCase(fmt[34]);
                            }
                            // Behind text properties (indices 35-41)
                            if (fmt.length > 41) {
                                m.behindText = fmt[35];
                                m.behindFontSize = parseIntSafe(fmt[36], 10);
                                m.behindFontBold = "true".equalsIgnoreCase(fmt[37]);
                                m.behindFontItalic = "true".equalsIgnoreCase(fmt[38]);
                                m.behindTextColor = hexToColor(fmt[39]) != null ? hexToColor(fmt[39]) : new Color(150, 150, 150);
                                m.behindTextWrap = "true".equalsIgnoreCase(fmt[40]);
                                m.behindTextVisible = "true".equalsIgnoreCase(fmt[41]);
                            }
                            // Shape, width, centerText (indices 42-45)
                            if (fmt.length > 43) m.width = parseIntSafe(fmt[43], 20);
                            if (fmt.length > 44) m.centerText = fmt[44];
                            if (fmt.length > 45) m.centerTextVisible = "true".equalsIgnoreCase(fmt[45]);
                            // Bevel settings (indices 46-53)
                            if (fmt.length > 53) {
                                m.bevelFill = "true".equalsIgnoreCase(fmt[46]);
                                m.bevelDepth = parseIntSafe(fmt[47], 60);
                                m.bevelLightAngle = parseIntSafe(fmt[48], 135);
                                m.bevelHighlightOpacity = parseIntSafe(fmt[49], 80);
                                m.bevelShadowOpacity = parseIntSafe(fmt[50], 60);
                                m.bevelStyle = fmt[51].isEmpty() ? "Inner Bevel" : fmt[51];
                                m.topBevel = fmt[52].isEmpty() ? "Circle" : fmt[52];
                                m.bottomBevel = fmt[53].isEmpty() ? "None" : fmt[53];
                            }
                        } else if (newFormat) {
                            // Previous format: width at 38, labelText at 39
                            if (fmt.length > 38) m.width = parseIntSafe(fmt[38], 20);
                            if (fmt.length > 39) m.labelText = fmt[39];
                            // Bevel settings (indices 40-47)
                            if (fmt.length > 47) {
                                m.bevelFill = "true".equalsIgnoreCase(fmt[40]);
                                m.bevelDepth = parseIntSafe(fmt[41], 60);
                                m.bevelLightAngle = parseIntSafe(fmt[42], 135);
                                m.bevelHighlightOpacity = parseIntSafe(fmt[43], 80);
                                m.bevelShadowOpacity = parseIntSafe(fmt[44], 60);
                                m.bevelStyle = fmt[45].isEmpty() ? "Inner Bevel" : fmt[45];
                                m.topBevel = fmt[46].isEmpty() ? "Circle" : fmt[46];
                                m.bottomBevel = fmt[47].isEmpty() ? "None" : fmt[47];
                            }
                        } else {
                            // Old format: width at 33, labelText at 34
                            if (fmt.length > 33) m.width = parseIntSafe(fmt[33], 20);
                            if (fmt.length > 34) m.labelText = fmt[34];
                        }
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

        // Import notes from Sheet 4
        String sheet4 = sheets.get("xl/worksheets/sheet4.xml");
        if (sheet4 != null) {
            ArrayList<String[]> notesRows = parseSheetRows(sheet4);
            // Create a map of task name to notes
            Map<String, String[]> notesMap = new HashMap<>();
            for (int i = 1; i < notesRows.size(); i++) {
                String[] r = notesRows.get(i);
                if (r.length >= 2 && "Task".equalsIgnoreCase(r[0])) {
                    notesMap.put(r[1], r);
                }
            }
            // Apply notes to tasks
            for (TimelineTask task : tasks) {
                String[] notes = notesMap.get(task.name);
                if (notes != null) {
                    task.note1 = notes.length > 2 ? notes[2] : "";
                    task.note2 = notes.length > 3 ? notes[3] : "";
                    task.note3 = notes.length > 4 ? notes[4] : "";
                    task.note4 = notes.length > 5 ? notes[5] : "";
                    task.note5 = notes.length > 6 ? notes[6] : "";
                }
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
            case "Extend Ticks Enabled":
                extendTicks = Boolean.parseBoolean(value);
                if (extendTicksCheckBox != null) extendTicksCheckBox.setSelected(extendTicks);
                if (extendTicksOptionsPanel != null) extendTicksOptionsPanel.setVisible(extendTicks);
                break;
            case "Extend Ticks Color":
                extendTicksColor = hexToColor(value) != null ? hexToColor(value) : extendTicksColor;
                if (extendTicksColorBtn != null) extendTicksColorBtn.setBackground(extendTicksColor);
                break;
            case "Extend Ticks Thickness":
                try { extendTicksThickness = Integer.parseInt(value); } catch (NumberFormatException e) {}
                if (extendTicksThicknessSpinner != null) extendTicksThicknessSpinner.setValue(extendTicksThickness);
                break;
            case "Extend Ticks Line Type":
                extendTicksLineType = value;
                if (extendTicksLineTypeCombo != null) extendTicksLineTypeCombo.setSelectedItem(extendTicksLineType);
                break;
            // Timeline axis settings
            case "Timeline Axis Color":
                timelineAxisColor = hexToColor(value) != null ? hexToColor(value) : timelineAxisColor;
                if (timelineAxisColorBtn != null) timelineAxisColorBtn.setBackground(timelineAxisColor);
                break;
            case "Timeline Axis Thickness":
                try { timelineAxisThickness = Integer.parseInt(value); } catch (NumberFormatException e) {}
                if (timelineAxisThicknessSpinner != null) timelineAxisThicknessSpinner.setValue(timelineAxisThickness);
                break;
            // Timeline axis date label settings
            case "Axis Date Color":
                axisDateColor = hexToColor(value) != null ? hexToColor(value) : axisDateColor;
                if (axisDateColorBtn != null) axisDateColorBtn.setBackground(axisDateColor);
                break;
            case "Axis Date Font Family":
                axisDateFontFamily = value;
                if (axisDateFontCombo != null) axisDateFontCombo.setSelectedItem(axisDateFontFamily);
                break;
            case "Axis Date Font Size":
                try { axisDateFontSize = Integer.parseInt(value); } catch (NumberFormatException e) {}
                if (axisDateFontSizeSpinner != null) axisDateFontSizeSpinner.setValue(axisDateFontSize);
                break;
            case "Axis Date Bold":
                axisDateBold = Boolean.parseBoolean(value);
                if (axisDateBoldBtn != null) axisDateBoldBtn.setSelected(axisDateBold);
                break;
            case "Axis Date Italic":
                axisDateItalic = Boolean.parseBoolean(value);
                if (axisDateItalicBtn != null) axisDateItalicBtn.setSelected(axisDateItalic);
                break;
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

        public void setMax(int newMax) {
            this.max = newMax;
            if (highValue > max) highValue = max;
            if (lowValue > max) lowValue = max;
            repaint();
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
        private Dimension expandedSize = new Dimension(290, 600);

        CollapsiblePanel(String title, JPanel content, boolean isLeft) {
            this.title = title;
            this.content = content;
            this.isLeft = isLeft;

            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

            // Header with gradient support
            header = new JPanel(new BorderLayout(5, 0)) {
                @Override
                protected void paintComponent(Graphics g) {
                    // Skip gradient when collapsed - fill with solid light gray
                    if (collapsed) {
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setColor(new Color(220, 220, 220));
                        g2d.fillRect(0, 0, getWidth(), getHeight());
                        return;
                    }

                    boolean useGradient = false;
                    ArrayList<float[]> stops = null;
                    double angle = 0;

                    if (isLeft && settingsHeaderUseGradient && settingsHeaderGradientStops.size() >= 2) {
                        useGradient = true;
                        stops = settingsHeaderGradientStops;
                        angle = settingsHeaderGradientAngle;
                    } else if (!isLeft && layersHeaderUseGradient && layersHeaderGradientStops.size() >= 2) {
                        useGradient = true;
                        stops = layersHeaderGradientStops;
                        angle = layersHeaderGradientAngle;
                    }

                    if (useGradient && stops != null) {
                        Graphics2D g2d = (Graphics2D) g;
                        int w = getWidth();
                        int h = getHeight();

                        float[] fractions = new float[stops.size()];
                        Color[] colors = new Color[stops.size()];
                        for (int i = 0; i < stops.size(); i++) {
                            float[] stop = stops.get(i);
                            fractions[i] = stop[0];
                            colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                        }

                        java.awt.LinearGradientPaint lgp = createAngledGradient(w, h, angle, fractions, colors);
                        g2d.setPaint(lgp);
                        g2d.fillRect(0, 0, w, h);
                    } else {
                        super.paintComponent(g);
                    }
                }
            };
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
            collapseBtn.setBackground(new Color(220, 220, 220));
            collapseBtn.setOpaque(true);
            collapseBtn.setForeground(Color.DARK_GRAY);
            collapseBtn.setBorderPainted(false);
            collapseBtn.setFocusPainted(false);
            collapseBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    collapseBtn.setBackground(new Color(180, 180, 180));
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    collapseBtn.setBackground(new Color(220, 220, 220));
                }
            });
            header.add(collapseBtn, BorderLayout.EAST);
            add(header, BorderLayout.NORTH);

            // Content
            JPanel contentWrapper = new JPanel(new BorderLayout());
            contentWrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            contentWrapper.add(content, BorderLayout.CENTER);
            add(contentWrapper, BorderLayout.CENTER);

            setPreferredSize(expandedSize);
        }

        void setHeaderVisible(boolean visible) {
            header.setVisible(visible);
            if (!visible) {
                remove(header);
            } else {
                add(header, BorderLayout.NORTH);
            }
            revalidate();
            repaint();
        }

        boolean isCollapsed() {
            return collapsed;
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
                setPreferredSize(new Dimension(30, getParent() != null ? getParent().getHeight() : 600));
                // When collapsed: show arrow pointing toward the panel to expand
                collapseBtn.setText(isLeft ? "\u25B6" : "\u25C0");  // Right arrow for left panel, left arrow for right panel
                collapseBtn.setToolTipText("Expand");
                for (Component c : getComponents()) {
                    if (c != header) c.setVisible(false);
                }
                // Hide title label when collapsed
                for (Component c : header.getComponents()) {
                    if (c instanceof JLabel) c.setVisible(false);
                }
                // Move button to CENTER so it fills the whole header
                header.remove(collapseBtn);
                header.add(collapseBtn, BorderLayout.CENTER);
                header.setPreferredSize(new Dimension(30, Integer.MAX_VALUE));
                // Use uniform light gray for entire collapsed panel
                Color collapsedGray = new Color(220, 220, 220);
                header.setBackground(collapsedGray);
                setBackground(collapsedGray);
                setBorder(BorderFactory.createLineBorder(collapsedGray));
                collapseBtn.setBackground(collapsedGray);
                collapseBtn.setForeground(Color.DARK_GRAY);
            } else {
                setPreferredSize(expandedSize);
                // When expanded: show arrow pointing away from center to collapse
                collapseBtn.setText(isLeft ? "\u25C0" : "\u25B6");  // Left arrow for left panel, right arrow for right panel
                collapseBtn.setToolTipText("Collapse");
                for (Component c : getComponents()) {
                    c.setVisible(true);
                }
                // Show title label when expanded
                for (Component c : header.getComponents()) {
                    if (c instanceof JLabel) c.setVisible(true);
                }
                // Move button back to EAST
                header.remove(collapseBtn);
                header.add(collapseBtn, BorderLayout.EAST);
                header.setPreferredSize(null);
                // Restore button to normal size
                collapseBtn.setPreferredSize(null);
                // Restore original colors when expanded
                header.setBackground(new Color(70, 130, 180));
                setBackground(new Color(250, 250, 250));
                setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
                collapseBtn.setBackground(new Color(220, 220, 220));
            }
            header.repaint();
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
            repaint();
        }

        private void applyInteriorColor(JPanel panel, Color color) {
            panel.setBackground(color);
            for (Component c : panel.getComponents()) {
                if (c instanceof JPanel) {
                    applyInteriorColor((JPanel) c, color);
                }
            }
        }

        private void setChildrenOpaque(JPanel panel, boolean opaque) {
            panel.setOpaque(opaque);
            for (Component c : panel.getComponents()) {
                if (c instanceof JPanel) {
                    setChildrenOpaque((JPanel) c, opaque);
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            boolean useGradient = false;
            ArrayList<float[]> stops = null;
            double angle = 0;

            if (isLeft && settingsUseGradient && settingsGradientStops.size() >= 2) {
                useGradient = true;
                stops = settingsGradientStops;
                angle = settingsGradientAngle;
            } else if (!isLeft && layersUseGradient && layersGradientStops.size() >= 2) {
                useGradient = true;
                stops = layersGradientStops;
                angle = layersGradientAngle;
            }

            if (useGradient && stops != null) {
                // Make children non-opaque so gradient shows through
                for (Component c : getComponents()) {
                    if (c instanceof JPanel && c != header) {
                        setChildrenOpaque((JPanel) c, false);
                    }
                }

                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                // Build arrays for LinearGradientPaint
                float[] fractions = new float[stops.size()];
                Color[] colors = new Color[stops.size()];
                for (int i = 0; i < stops.size(); i++) {
                    float[] stop = stops.get(i);
                    fractions[i] = stop[0];
                    colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                }

                java.awt.LinearGradientPaint lgp = createAngledGradient(w, h, angle, fractions, colors);
                g2d.setPaint(lgp);
                g2d.fillRect(0, 0, w, h);
            } else {
                super.paintComponent(g);
                // Restore children to opaque if not using gradient
                for (Component c : getComponents()) {
                    if (c instanceof JPanel && c != header) {
                        setChildrenOpaque((JPanel) c, true);
                    }
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

            // List with custom painting for floating item and gradient background
            listModel = new DefaultListModel<>();
            layersList = new JList<String>(listModel) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();

                    // Paint background (gradient or solid)
                    if (layersListBgUseGradient && layersListBgGradientStops.size() >= 2) {
                        // Build arrays for LinearGradientPaint
                        float[] fractions = new float[layersListBgGradientStops.size()];
                        Color[] colors = new Color[layersListBgGradientStops.size()];
                        for (int i = 0; i < layersListBgGradientStops.size(); i++) {
                            float[] stop = layersListBgGradientStops.get(i);
                            fractions[i] = stop[0];
                            colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                        }

                        java.awt.LinearGradientPaint lgp = createAngledGradient(w, h, layersListBgGradientAngle, fractions, colors);
                        g2d.setPaint(lgp);
                        g2d.fillRect(0, 0, w, h);
                    } else {
                        // Paint solid background
                        g2d.setColor(getBackground());
                        g2d.fillRect(0, 0, w, h);
                    }

                    // Let JList paint the cells
                    super.paintComponent(g);
                    // Draw floating item on top (30% transparent = 70% opaque = alpha 178)
                    if (isDragging && draggedItemName != null) {
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
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            add(scrollPane, BorderLayout.CENTER);

            // Buttons panel
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            buttonsPanel.setBackground(new Color(230, 230, 230));

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
            saveState();

            Object item = layerOrder.remove(fromIndex);
            layerOrder.add(toIndex, item);

            refreshTimeline();
        }

        void refreshLayers() {
            int selectedIndex = layersList.getSelectedIndex();
            listModel.clear();
            for (Object item : layerOrder) {
                if (item instanceof TimelineTask) {
                    TimelineTask task = (TimelineTask) item;
                    // Show task name with center text in parenthesis if it exists (limit to 10 chars)
                    String displayName = task.name;
                    if (task.centerText != null && !task.centerText.isEmpty()) {
                        String truncatedText = task.centerText.length() > 10
                            ? task.centerText.substring(0, 10) + "..."
                            : task.centerText;
                        displayName += " (" + truncatedText + ")";
                    }
                    listModel.addElement(displayName);
                } else if (item instanceof TimelineMilestone) {
                    TimelineMilestone m = (TimelineMilestone) item;
                    // Show milestone name with label text in parenthesis if it exists (limit to 10 chars)
                    String displayName = m.name;
                    if (m.labelText != null && !m.labelText.isEmpty()) {
                        String truncatedText = m.labelText.length() > 10
                            ? m.labelText.substring(0, 10) + "..."
                            : m.labelText;
                        displayName += " (" + truncatedText + ")";
                    }
                    listModel.addElement("\u25C6 " + displayName); // Diamond prefix for milestones
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
                saveState();
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
        }

        void setListBackground(Color color) {
            if (layersList != null) {
                layersList.setBackground(color);
                // Always set list opaque to false so our custom paintComponent can draw
                layersList.setOpaque(false);
                layersList.revalidate();
                layersList.repaint();
            }
            if (scrollPane != null) {
                scrollPane.getViewport().setBackground(color);
                scrollPane.getViewport().setOpaque(false);
                scrollPane.setOpaque(false);
                scrollPane.revalidate();
                scrollPane.repaint();
            }
        }

        void refreshList() {
            if (layersList != null) {
                layersList.repaint();
            }
        }

        // Custom cell renderer for layers
        class LayerCellRenderer extends JPanel implements ListCellRenderer<String> {
            private JLabel nameLabel;
            private JPanel colorIndicator;
            private JLabel dragHandleLabel;
            private boolean useGradient = false;

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
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                if (useGradient && layersTaskUseGradient && layersTaskGradientStops.size() >= 2) {
                    // Build arrays for LinearGradientPaint
                    float[] fractions = new float[layersTaskGradientStops.size()];
                    Color[] colors = new Color[layersTaskGradientStops.size()];
                    for (int i = 0; i < layersTaskGradientStops.size(); i++) {
                        float[] stop = layersTaskGradientStops.get(i);
                        fractions[i] = stop[0];
                        colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                    }

                    java.awt.LinearGradientPaint lgp = createAngledGradient(w, h, layersTaskGradientAngle, fractions, colors);
                    g2d.setPaint(lgp);
                    g2d.fillRect(0, 0, w, h);
                } else {
                    // Paint solid background
                    g2d.setColor(getBackground());
                    g2d.fillRect(0, 0, w, h);
                }
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
                    // Keep all items in task color state
                    useGradient = true; // Use gradient for task items while dragging
                    setBackground(layersTaskColor);
                    nameLabel.setForeground(layersItemTextColor);
                    dragHandleLabel.setForeground(layersDragHandleColor);

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
                    // Normal (not dragging) - highlight for selection
                    if (isSelected) {
                        useGradient = false; // Selected items use solid color
                        setBackground(layersSelectedBgColor);
                        nameLabel.setForeground(layersItemTextColor);
                        dragHandleLabel.setForeground(layersDragHandleColor);
                    } else {
                        useGradient = true; // Use gradient for non-selected task items
                        setBackground(layersTaskColor);
                        nameLabel.setForeground(layersItemTextColor);
                        dragHandleLabel.setForeground(layersDragHandleColor);
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

    // State class for undo/redo
    class TimelineState {
        ArrayList<TimelineTask> tasks;
        ArrayList<TimelineMilestone> milestones;
        ArrayList<Object> layerOrder;
        ArrayList<TimelineEvent> events;

        TimelineState(ArrayList<TimelineTask> tasks, ArrayList<TimelineMilestone> milestones,
                      ArrayList<Object> layerOrder, ArrayList<TimelineEvent> events) {
            // Deep copy tasks
            this.tasks = new ArrayList<>();
            for (TimelineTask t : tasks) {
                this.tasks.add(copyTask(t));
            }
            // Deep copy milestones
            this.milestones = new ArrayList<>();
            for (TimelineMilestone m : milestones) {
                this.milestones.add(copyMilestone(m));
            }
            // Deep copy layer order (references to new copies)
            this.layerOrder = new ArrayList<>();
            for (Object item : layerOrder) {
                if (item instanceof TimelineTask) {
                    int idx = tasks.indexOf(item);
                    if (idx >= 0) this.layerOrder.add(this.tasks.get(idx));
                } else if (item instanceof TimelineMilestone) {
                    int idx = milestones.indexOf(item);
                    if (idx >= 0) this.layerOrder.add(this.milestones.get(idx));
                }
            }
            // Deep copy events
            this.events = new ArrayList<>();
            for (TimelineEvent e : events) {
                this.events.add(new TimelineEvent(e.title, e.date, e.description));
            }
        }

        private TimelineTask copyTask(TimelineTask t) {
            TimelineTask copy = new TimelineTask(t.name, t.startDate, t.endDate);
            copy.centerText = t.centerText;
            copy.fillColor = t.fillColor;
            copy.outlineColor = t.outlineColor;
            copy.outlineThickness = t.outlineThickness;
            copy.bevelFill = t.bevelFill;
            copy.bevelDepth = t.bevelDepth;
            copy.bevelLightAngle = t.bevelLightAngle;
            copy.bevelHighlightOpacity = t.bevelHighlightOpacity;
            copy.bevelShadowOpacity = t.bevelShadowOpacity;
            copy.bevelStyle = t.bevelStyle;
            copy.topBevel = t.topBevel;
            copy.bottomBevel = t.bottomBevel;
            copy.height = t.height;
            copy.yPosition = t.yPosition;
            copy.fontFamily = t.fontFamily;
            copy.fontSize = t.fontSize;
            copy.fontBold = t.fontBold;
            copy.fontItalic = t.fontItalic;
            copy.textColor = t.textColor;
            copy.centerTextXOffset = t.centerTextXOffset;
            copy.centerTextYOffset = t.centerTextYOffset;
            copy.frontText = t.frontText;
            copy.frontFontFamily = t.frontFontFamily;
            copy.frontFontSize = t.frontFontSize;
            copy.frontFontBold = t.frontFontBold;
            copy.frontFontItalic = t.frontFontItalic;
            copy.frontTextColor = t.frontTextColor;
            copy.frontTextXOffset = t.frontTextXOffset;
            copy.frontTextYOffset = t.frontTextYOffset;
            copy.aboveText = t.aboveText;
            copy.aboveFontFamily = t.aboveFontFamily;
            copy.aboveFontSize = t.aboveFontSize;
            copy.aboveFontBold = t.aboveFontBold;
            copy.aboveFontItalic = t.aboveFontItalic;
            copy.aboveTextColor = t.aboveTextColor;
            copy.aboveTextXOffset = t.aboveTextXOffset;
            copy.aboveTextYOffset = t.aboveTextYOffset;
            copy.underneathText = t.underneathText;
            copy.underneathFontFamily = t.underneathFontFamily;
            copy.underneathFontSize = t.underneathFontSize;
            copy.underneathFontBold = t.underneathFontBold;
            copy.underneathFontItalic = t.underneathFontItalic;
            copy.underneathTextColor = t.underneathTextColor;
            copy.underneathTextXOffset = t.underneathTextXOffset;
            copy.underneathTextYOffset = t.underneathTextYOffset;
            copy.behindText = t.behindText;
            copy.behindFontFamily = t.behindFontFamily;
            copy.behindFontSize = t.behindFontSize;
            copy.behindFontBold = t.behindFontBold;
            copy.behindFontItalic = t.behindFontItalic;
            copy.behindTextColor = t.behindTextColor;
            copy.behindTextXOffset = t.behindTextXOffset;
            copy.behindTextYOffset = t.behindTextYOffset;
            copy.note1 = t.note1;
            copy.note2 = t.note2;
            copy.note3 = t.note3;
            copy.note4 = t.note4;
            copy.note5 = t.note5;
            return copy;
        }

        private TimelineMilestone copyMilestone(TimelineMilestone m) {
            TimelineMilestone copy = new TimelineMilestone(m.name, m.date, m.shape);
            copy.width = m.width;
            copy.height = m.height;
            copy.yPosition = m.yPosition;
            copy.fillColor = m.fillColor;
            copy.outlineColor = m.outlineColor;
            copy.outlineThickness = m.outlineThickness;
            copy.bevelFill = m.bevelFill;
            copy.bevelDepth = m.bevelDepth;
            copy.bevelLightAngle = m.bevelLightAngle;
            copy.bevelHighlightOpacity = m.bevelHighlightOpacity;
            copy.bevelShadowOpacity = m.bevelShadowOpacity;
            copy.bevelStyle = m.bevelStyle;
            copy.topBevel = m.topBevel;
            copy.bottomBevel = m.bottomBevel;
            copy.labelText = m.labelText;
            copy.fontFamily = m.fontFamily;
            copy.fontSize = m.fontSize;
            copy.fontBold = m.fontBold;
            copy.fontItalic = m.fontItalic;
            copy.textColor = m.textColor;
            return copy;
        }
    }

    static class TimelineTask {
        String name, startDate, endDate;
        String centerText = "";    // text displayed on the task bar
        Color fillColor = null;    // null means use default
        Color outlineColor = null; // null means use default (darker fill)
        int outlineThickness = 2;  // default thickness
        boolean bevelFill = false; // bevel effect on fill
        int bevelDepth = 60;       // bevel intensity (0-100)
        int bevelLightAngle = 135; // light angle in degrees (0-360, 135 = top-left)
        int bevelHighlightOpacity = 80; // highlight opacity (0-255)
        int bevelShadowOpacity = 60;    // shadow opacity (0-255)
        String bevelStyle = "Inner Bevel";  // "Inner Bevel", "Outer Bevel", "Emboss", "Pillow Emboss"
        String topBevel = "Circle";    // PowerPoint-style top bevel shape
        String bottomBevel = "None";   // PowerPoint-style bottom bevel shape
        int height = 25;           // default height
        int yPosition = -1;        // Y position on timeline (-1 means auto-calculate)
        // Center text formatting properties
        String fontFamily = "SansSerif";  // default font family
        int fontSize = 11;         // default font size
        boolean fontBold = false;  // default not bold
        boolean fontItalic = false; // default not italic
        Color textColor = Color.BLACK;    // default black
        int centerTextXOffset = 0; // X offset from default position
        int centerTextYOffset = 0; // Y offset from default position
        boolean centerTextWrap = false; // wrap center text
        boolean centerTextVisible = true; // center text visible
        // Front text properties (text in front of task bar)
        String frontText = "";
        String frontFontFamily = "SansSerif";
        int frontFontSize = 10;
        boolean frontFontBold = false;
        boolean frontFontItalic = false;
        Color frontTextColor = Color.BLACK;
        int frontTextXOffset = 0;
        int frontTextYOffset = 0;
        boolean frontTextWrap = false; // wrap front text
        boolean frontTextVisible = true; // front text visible
        // Above text properties (text above task bar)
        String aboveText = "";
        String aboveFontFamily = "SansSerif";
        int aboveFontSize = 10;
        boolean aboveFontBold = false;
        boolean aboveFontItalic = false;
        Color aboveTextColor = Color.BLACK;
        int aboveTextXOffset = 0;
        int aboveTextYOffset = 0;
        boolean aboveTextWrap = false; // wrap above text
        boolean aboveTextVisible = true; // above text visible
        // Underneath text properties (text below task bar)
        String underneathText = "";
        String underneathFontFamily = "SansSerif";
        int underneathFontSize = 10;
        boolean underneathFontBold = false;
        boolean underneathFontItalic = false;
        Color underneathTextColor = Color.BLACK;
        int underneathTextXOffset = 0;
        int underneathTextYOffset = 0;
        boolean underneathTextWrap = false; // wrap underneath text
        boolean underneathTextVisible = true; // underneath text visible
        // Behind text properties (text behind task bar)
        String behindText = "";
        String behindFontFamily = "SansSerif";
        int behindFontSize = 10;
        boolean behindFontBold = false;
        boolean behindFontItalic = false;
        Color behindTextColor = new Color(150, 150, 150);
        int behindTextXOffset = 0;
        int behindTextYOffset = 0;
        boolean behindTextWrap = false; // wrap behind text
        boolean behindTextVisible = true; // behind text visible
        // Notes
        String note1 = "";
        String note2 = "";
        String note3 = "";
        String note4 = "";
        String note5 = "";
        TimelineTask(String name, String startDate, String endDate) {
            this.name = name;
            this.centerText = ""; // leave center text blank by default
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
        boolean bevelFill = false; // bevel effect on fill
        int bevelDepth = 60;       // bevel intensity (0-100)
        int bevelLightAngle = 135; // light angle in degrees (0-360, 135 = top-left)
        int bevelHighlightOpacity = 80; // highlight opacity (0-255)
        int bevelShadowOpacity = 60;    // shadow opacity (0-255)
        String bevelStyle = "Inner Bevel";  // "Inner Bevel", "Outer Bevel", "Emboss", "Pillow Emboss"
        String topBevel = "Circle";    // PowerPoint-style top bevel shape
        String bottomBevel = "None";   // PowerPoint-style bottom bevel shape
        // Center text properties (text below milestone - same as labelText)
        String centerText = "";
        String fontFamily = "SansSerif";
        int fontSize = 10;
        boolean fontBold = false;
        boolean fontItalic = false;
        Color textColor = Color.BLACK;
        int centerTextXOffset = 0;
        int centerTextYOffset = 0;
        boolean centerTextWrap = false;
        boolean centerTextVisible = true;
        // Front text properties (text in front of milestone)
        String frontText = "";
        String frontFontFamily = "SansSerif";
        int frontFontSize = 10;
        boolean frontFontBold = false;
        boolean frontFontItalic = false;
        Color frontTextColor = Color.BLACK;
        int frontTextXOffset = 0;
        int frontTextYOffset = 0;
        boolean frontTextWrap = false;
        boolean frontTextVisible = true;
        // Above text properties (text above milestone)
        String aboveText = "";
        String aboveFontFamily = "SansSerif";
        int aboveFontSize = 10;
        boolean aboveFontBold = false;
        boolean aboveFontItalic = false;
        Color aboveTextColor = Color.BLACK;
        int aboveTextXOffset = 0;
        int aboveTextYOffset = 0;
        boolean aboveTextWrap = false;
        boolean aboveTextVisible = true;
        // Underneath text properties (text below milestone, further down)
        String underneathText = "";
        String underneathFontFamily = "SansSerif";
        int underneathFontSize = 10;
        boolean underneathFontBold = false;
        boolean underneathFontItalic = false;
        Color underneathTextColor = Color.BLACK;
        int underneathTextXOffset = 0;
        int underneathTextYOffset = 0;
        boolean underneathTextWrap = false;
        boolean underneathTextVisible = true;
        // Behind text properties (text behind/after milestone)
        String behindText = "";
        String behindFontFamily = "SansSerif";
        int behindFontSize = 10;
        boolean behindFontBold = false;
        boolean behindFontItalic = false;
        Color behindTextColor = new Color(150, 150, 150);
        int behindTextXOffset = 0;
        int behindTextYOffset = 0;
        boolean behindTextWrap = false;
        boolean behindTextVisible = true;
        // Legacy support
        String labelText = "";
        boolean labelTextWrap = false;
        boolean labelTextVisible = true;

        TimelineMilestone(String name, String date, String shape) {
            this.name = name;
            this.centerText = ""; // leave center text blank by default
            this.labelText = ""; // leave label text blank by default
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
        private boolean isMultiDragging = false;
        private int multiDragStartX = 0;
        private int multiDragStartY = 0;
        private java.util.Map<Integer, String> multiDragTaskOriginalStarts = new java.util.HashMap<>();
        private java.util.Map<Integer, String> multiDragTaskOriginalEnds = new java.util.HashMap<>();
        private java.util.Map<Integer, Integer> multiDragTaskOriginalY = new java.util.HashMap<>();
        private java.util.Map<Integer, String> multiDragMilestoneOriginalDates = new java.util.HashMap<>();
        private java.util.Map<Integer, Integer> multiDragMilestoneOriginalY = new java.util.HashMap<>();
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

        // Flag to save state once when drag starts
        private boolean dragStateSaved = false;

        // Selection box (rubber band selection)
        private boolean isSelectionBoxDragging = false;
        private int selectionBoxStartX = -1;
        private int selectionBoxStartY = -1;
        private int selectionBoxEndX = -1;
        private int selectionBoxEndY = -1;

        // Floating color bar and alignment window
        private JWindow activeColorBar = null;
        private JWindow activeAlignmentWindow = null;
        private int colorBarBottomY = 0;
        private int colorBarLeftX = 0;

        TimelineDisplayPanel() {
            setBackground(Color.WHITE);
            setupMouseListeners();
            setupKeyListeners();
        }

        private void setupKeyListeners() {
            setFocusable(true);
            // Disable Tab key focus traversal so Tab can be used as a shortcut
            setFocusTraversalKeysEnabled(false);
            addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {
                    int keyCode = e.getKeyCode();
                    int modifiers = e.getModifiersEx();

                    // Check for configurable shortcuts first
                    // Select next (Tab)
                    if (keyCode == selectNextKey && modifiers == selectNextModifiers) {
                        selectNextItem();
                        e.consume();
                        return;
                    }
                    // Delete selected (Shift+Delete)
                    if (keyCode == deleteSelectedKey && (modifiers & deleteSelectedModifiers) == deleteSelectedModifiers) {
                        deleteSelectedItem();
                        e.consume();
                        return;
                    }
                    // Duplicate (F1)
                    if (keyCode == duplicateKey && modifiers == duplicateModifiers) {
                        if (!selectedTaskIndices.isEmpty()) {
                            duplicateSelectedTasks();
                        }
                        e.consume();
                        return;
                    }

                    // Select all (Ctrl+A)
                    if (keyCode == java.awt.event.KeyEvent.VK_A && (modifiers & java.awt.event.InputEvent.CTRL_DOWN_MASK) != 0) {
                        selectAllItems();
                        e.consume();
                        return;
                    }

                    // Arrow key movement for selected tasks
                    if (selectedTaskIndices.isEmpty() && selectedMilestoneIndex < 0) return;

                    int daysDelta = 0;
                    int yDelta = 0;

                    switch (keyCode) {
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

                    saveState();
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

                    // Move selected milestone
                    if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                        TimelineMilestone ms = milestones.get(selectedMilestoneIndex);
                        if (daysDelta != 0) {
                            try {
                                LocalDate date = LocalDate.parse(ms.date, DATE_FORMAT);
                                ms.date = date.plusDays(daysDelta).format(DATE_FORMAT);
                            } catch (Exception ex) {}
                        }
                        if (yDelta != 0) {
                            ms.yPosition = Math.max(35, ms.yPosition + yDelta);
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

        private void selectNextItem() {
            // Combine tasks and milestones into a single list for navigation
            int totalItems = tasks.size() + milestones.size();
            if (totalItems == 0) return;

            int currentIndex = -1;

            // Find current selection
            if (selectedMilestoneIndex >= 0) {
                currentIndex = tasks.size() + selectedMilestoneIndex;
            } else if (!selectedTaskIndices.isEmpty()) {
                // Get the highest selected task index
                currentIndex = selectedTaskIndices.stream().max(Integer::compare).orElse(-1);
            }

            // Calculate next index
            int nextIndex = (currentIndex + 1) % totalItems;

            // Clear all selections
            selectedTaskIndices.clear();
            selectedMilestoneIndex = -1;
            selectedMilestoneIndices.clear();

            // Select next item
            if (nextIndex < tasks.size()) {
                selectedTaskIndices.add(nextIndex);
            } else {
                selectedMilestoneIndex = nextIndex - tasks.size();
            }

            updateFormatPanelForSelection();
            layersPanel.refreshLayers();
            updateSpreadsheet();
            repaint();
        }

        private void selectAllItems() {
            // Select all tasks
            selectedTaskIndices.clear();
            for (int i = 0; i < tasks.size(); i++) {
                selectedTaskIndices.add(i);
            }

            // Select all milestones
            selectedMilestoneIndices.clear();
            for (int i = 0; i < milestones.size(); i++) {
                selectedMilestoneIndices.add(i);
            }
            selectedMilestoneIndex = milestones.isEmpty() ? -1 : 0;

            updateFormatPanelForSelection();
            layersPanel.refreshLayers();
            updateSpreadsheet();
            repaint();
        }

        void deleteSelectedItem() {
            if (selectedTaskIndices.isEmpty() && selectedMilestoneIndex < 0 && selectedMilestoneIndices.isEmpty()) return;

            saveState();

            // Delete selected tasks
            if (!selectedTaskIndices.isEmpty()) {
                java.util.List<Integer> sortedIndices = new java.util.ArrayList<>(selectedTaskIndices);
                java.util.Collections.sort(sortedIndices, java.util.Collections.reverseOrder());
                for (int idx : sortedIndices) {
                    if (idx >= 0 && idx < tasks.size()) {
                        TimelineTask task = tasks.get(idx);
                        layerOrder.remove(task);
                        tasks.remove(idx);
                    }
                }
                selectedTaskIndices.clear();
            }

            // Delete selected milestones (multi-select)
            if (!selectedMilestoneIndices.isEmpty()) {
                java.util.List<Integer> sortedMilestoneIndices = new java.util.ArrayList<>(selectedMilestoneIndices);
                java.util.Collections.sort(sortedMilestoneIndices, java.util.Collections.reverseOrder());
                for (int idx : sortedMilestoneIndices) {
                    if (idx >= 0 && idx < milestones.size()) {
                        TimelineMilestone ms = milestones.get(idx);
                        layerOrder.remove(ms);
                        milestones.remove(idx);
                    }
                }
                selectedMilestoneIndices.clear();
                selectedMilestoneIndex = -1;
            } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                // Single milestone selection fallback
                TimelineMilestone ms = milestones.get(selectedMilestoneIndex);
                layerOrder.remove(ms);
                milestones.remove(selectedMilestoneIndex);
                selectedMilestoneIndex = -1;
            }

            clearFormatFields();
            layersPanel.refreshLayers();
            updateSpreadsheet();
            repaint();
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
            // When axis is at top, start tasks below the axis (at y=100)
            int y = "Top".equals(timelineAxisPosition) ? 100 : 45;
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

        // Lock all task positions so moving one doesn't affect others
        private void lockAllTaskPositions() {
            for (int i = 0; i < layerOrder.size(); i++) {
                Object item = layerOrder.get(i);
                if (item instanceof TimelineTask) {
                    TimelineTask task = (TimelineTask) item;
                    if (task.yPosition < 0) {
                        // Calculate and lock the current auto-position
                        task.yPosition = getTaskYForLayer(i);
                    }
                }
            }
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

        // Get the lowest Y position (bottom) of any task or milestone
        private int getLowestItemBottom() {
            int lowestBottom = 45; // Minimum position
            int autoY = 45;

            // Check all items in layer order
            for (int i = layerOrder.size() - 1; i >= 0; i--) {
                Object item = layerOrder.get(i);
                if (item instanceof TimelineTask) {
                    TimelineTask task = (TimelineTask) item;
                    int taskBottom;
                    if (task.yPosition >= 0) {
                        taskBottom = task.yPosition + task.height;
                    } else {
                        taskBottom = autoY + task.height;
                        autoY = taskBottom + TASK_BAR_SPACING;
                    }
                    if (taskBottom > lowestBottom) {
                        lowestBottom = taskBottom;
                    }
                } else if (item instanceof TimelineMilestone) {
                    TimelineMilestone milestone = (TimelineMilestone) item;
                    if (milestone.yPosition >= 0) {
                        int milestoneBottom = milestone.yPosition + milestone.height / 2;
                        if (milestoneBottom > lowestBottom) {
                            lowestBottom = milestoneBottom;
                        }
                    }
                }
            }
            return lowestBottom;
        }

        private int getHighestItemTop() {
            int highestTop = Integer.MAX_VALUE;
            int autoY = 100; // Starting Y for auto-positioned items when axis is at top

            // Check all items in layer order
            for (int i = layerOrder.size() - 1; i >= 0; i--) {
                Object item = layerOrder.get(i);
                if (item instanceof TimelineTask) {
                    TimelineTask task = (TimelineTask) item;
                    int taskTop;
                    if (task.yPosition >= 0) {
                        taskTop = task.yPosition;
                    } else {
                        taskTop = autoY;
                        autoY += task.height + TASK_BAR_SPACING;
                    }
                    if (taskTop < highestTop) {
                        highestTop = taskTop;
                    }
                } else if (item instanceof TimelineMilestone) {
                    TimelineMilestone milestone = (TimelineMilestone) item;
                    if (milestone.yPosition >= 0) {
                        int milestoneTop = milestone.yPosition - milestone.height / 2;
                        if (milestoneTop < highestTop) {
                            highestTop = milestoneTop;
                        }
                    }
                }
            }
            // Return a reasonable default if no items found
            return highestTop == Integer.MAX_VALUE ? 100 : highestTop;
        }

        private void setupMouseListeners() {
            MouseAdapter adapter = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();

                    // Close floating windows if left-clicking on timeline (not on windows)
                    if (e.getButton() == MouseEvent.BUTTON1 && !e.isPopupTrigger() && !SwingUtilities.isRightMouseButton(e)) {
                        if (activeColorBar != null || activeAlignmentWindow != null) {
                            Point clickScreen = e.getLocationOnScreen();
                            boolean onWindow = (activeColorBar != null && activeColorBar.getBounds().contains(clickScreen))
                                             || (activeAlignmentWindow != null && activeAlignmentWindow.getBounds().contains(clickScreen));
                            if (!onWindow) { closeFloatingWindows(); }
                        }
                    }

                    // Skip selection handling on right-click to preserve multi-selection
                    if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                        return;
                    }
                    handleMousePressed(e.getX(), e.getY(), e.isControlDown());
                }
                public void mouseReleased(MouseEvent e) {
                    // Check for right-click on multi-selection
                    if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                        int totalSelected = selectedTaskIndices.size() + selectedMilestoneIndices.size();
                        if (selectedMilestoneIndex >= 0 && !selectedMilestoneIndices.contains(selectedMilestoneIndex)) {
                            totalSelected++;
                        }
                        if (totalSelected >= 1 || selectedMilestoneIndex >= 0) {
                            if (totalSelected > 1) {
                                // Show color bar first, then alignment window
                                showFloatingColorBar(e.getX(), e.getY(), false);
                                showAlignmentPopup(e.getX(), e.getY());
                            } else {
                                showFloatingColorBar(e.getX(), e.getY(), true);
                            }
                            return;
                        }
                    }
                    if (isDragging) {
                        isDragging = false;
                        draggingTaskIndex = -1;
                        setCursor(Cursor.getDefaultCursor());
                    }
                    if (isMultiDragging) {
                        isMultiDragging = false;
                        multiDragTaskOriginalStarts.clear();
                        multiDragTaskOriginalEnds.clear();
                        multiDragTaskOriginalY.clear();
                        multiDragMilestoneOriginalDates.clear();
                        multiDragMilestoneOriginalY.clear();
                        setCursor(Cursor.getDefaultCursor());
                        refreshTimeline();
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
                    if (isSelectionBoxDragging) {
                        finishSelectionBox(e.isControlDown());
                        isSelectionBoxDragging = false;
                        selectionBoxStartX = -1;
                        selectionBoxStartY = -1;
                        selectionBoxEndX = -1;
                        selectionBoxEndY = -1;
                        repaint();
                    }
                    dragStateSaved = false; // Reset for next drag
                }
                public void mouseDragged(MouseEvent e) {
                    // Save state once at start of any drag
                    if (!dragStateSaved && (isDragging || isMoveDragging || isHeightDragging || isMilestoneDragging || isMilestoneResizing || isMultiDragging)) {
                        saveState();
                        // Lock all task positions before moving so other tasks don't shift
                        if (isMoveDragging) {
                            lockAllTaskPositions();
                        }
                        dragStateSaved = true;
                    }
                    if (isDragging && draggingTaskIndex >= 0) {
                        handleDrag(e.getX());
                    }
                    if (isMultiDragging) {
                        handleMultiDrag(e.getX(), e.getY());
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
                    if (isSelectionBoxDragging) {
                        selectionBoxEndX = e.getX();
                        selectionBoxEndY = e.getY();
                        repaint();
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
            int timelineY = "Top".equals(timelineAxisPosition) ? getHighestItemTop() - 65 : getLowestItemBottom() + 30;

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
                                // Check if we're clicking on an already-selected item with multi-selection
                                int totalSelected = selectedTaskIndices.size() + selectedMilestoneIndices.size();
                                if (selectedMilestoneIndex >= 0 && !selectedMilestoneIndices.contains(selectedMilestoneIndex)) {
                                    totalSelected++;
                                }
                                boolean isAlreadySelected = selectedTaskIndices.contains(taskIdx);

                                if (totalSelected > 1 && isAlreadySelected && !ctrlDown) {
                                    // Start multi-drag
                                    startMultiDrag(x, y);
                                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                                    return;
                                }

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
                        int my;
                        if (milestone.yPosition >= 0) {
                            my = milestone.yPosition;
                        } else if ("Top".equals(timelineAxisPosition)) {
                            my = timelineY + milestone.height / 2 + 20;
                        } else {
                            my = timelineY - milestone.height / 2 - 10;
                        }
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
                            // Check if we're clicking on an already-selected item with multi-selection
                            int totalSelected = selectedTaskIndices.size() + selectedMilestoneIndices.size();
                            if (selectedMilestoneIndex >= 0 && !selectedMilestoneIndices.contains(selectedMilestoneIndex)) {
                                totalSelected++;
                            }
                            boolean isAlreadySelected = selectedMilestoneIndices.contains(milestoneIdx) || milestoneIdx == selectedMilestoneIndex;

                            if (totalSelected > 1 && isAlreadySelected && !ctrlDown) {
                                // Start multi-drag
                                startMultiDrag(x, y);
                                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                                return;
                            }

                            selectMilestone(milestoneIdx, ctrlDown);
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

            // No task or milestone was clicked - start selection box
            if (!ctrlDown) {
                // Clear selection when starting a new selection box (unless Ctrl is held)
                selectedTaskIndices.clear();
                selectedMilestoneIndices.clear();
                selectedMilestoneIndex = -1;
                selectTask(-1);
                repaint();
            }
            isSelectionBoxDragging = true;
            selectionBoxStartX = x;
            selectionBoxStartY = y;
            selectionBoxEndX = x;
            selectionBoxEndY = y;
            repaint();
        }

        private void finishSelectionBox(boolean ctrlDown) {
            if (startDate == null || endDate == null) return;

            int timelineX = MARGIN_LEFT;
            int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            if (totalDays <= 0) return;

            int timelineY = "Top".equals(timelineAxisPosition) ? getHighestItemTop() - 65 : getLowestItemBottom() + 30;

            // Normalize selection box coordinates
            int boxLeft = Math.min(selectionBoxStartX, selectionBoxEndX);
            int boxRight = Math.max(selectionBoxStartX, selectionBoxEndX);
            int boxTop = Math.min(selectionBoxStartY, selectionBoxEndY);
            int boxBottom = Math.max(selectionBoxStartY, selectionBoxEndY);

            // If box is too small, don't select anything
            if (boxRight - boxLeft < 5 && boxBottom - boxTop < 5) return;

            // Check all items for intersection with selection box
            for (int layerIdx = 0; layerIdx < layerOrder.size(); layerIdx++) {
                Object item = layerOrder.get(layerIdx);

                if (item instanceof TimelineTask) {
                    TimelineTask task = (TimelineTask) item;
                    int taskIdx = tasks.indexOf(task);
                    int taskY = getTaskYForLayer(layerIdx);
                    int taskHeight = task.height;

                    try {
                        LocalDate taskStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                        LocalDate taskEnd = LocalDate.parse(task.endDate, DATE_FORMAT);

                        int x1 = getXForDate(taskStart, timelineX, timelineWidth, totalDays);
                        int x2 = getXForDate(taskEnd, timelineX, timelineWidth, totalDays);
                        int barWidth = Math.max(x2 - x1, 10);

                        // Check if task intersects with selection box
                        if (x1 + barWidth >= boxLeft && x1 <= boxRight &&
                            taskY + taskHeight >= boxTop && taskY <= boxBottom) {
                            if (!selectedTaskIndices.contains(taskIdx)) {
                                selectedTaskIndices.add(taskIdx);
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
                        int my;
                        if (milestone.yPosition >= 0) {
                            my = milestone.yPosition;
                        } else if ("Top".equals(timelineAxisPosition)) {
                            my = timelineY + milestone.height / 2 + 20;
                        } else {
                            my = timelineY - milestone.height / 2 - 10;
                        }
                        int halfW = milestone.width / 2;
                        int halfH = milestone.height / 2;

                        // Check if milestone intersects with selection box
                        if (mx + halfW >= boxLeft && mx - halfW <= boxRight &&
                            my + halfH >= boxTop && my - halfH <= boxBottom) {
                            if (!selectedMilestoneIndices.contains(milestoneIdx)) {
                                selectedMilestoneIndices.add(milestoneIdx);
                            }
                        }
                    } catch (Exception ex) {}
                }
            }

            // Update format panel for selection
            updateFormatPanelForSelection();
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

        private void showAlignmentPopup(int x, int y) {
            // Use JWindow instead of JPopupMenu to avoid focus issues with color bar
            JWindow alignWindow = new JWindow(SwingUtilities.getWindowAncestor(this));
            alignWindow.setLayout(new BorderLayout());

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            buttonPanel.setBackground(new Color(245, 245, 245));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

            java.util.function.Consumer<JButton> styleButton = btn -> {
                btn.setHorizontalAlignment(SwingConstants.LEFT);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setContentAreaFilled(true);
                btn.setOpaque(true);
                btn.setBackground(new Color(245, 245, 245));
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height));
                final Color normalFg = btn.getForeground();
                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        btn.setBackground(new Color(0, 120, 215));
                        btn.setForeground(Color.WHITE);
                    }
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        btn.setBackground(new Color(245, 245, 245));
                        btn.setForeground(normalFg);
                    }
                });
            };

            JButton alignLeft = new JButton("Align Left");
            styleButton.accept(alignLeft);
            alignLeft.addActionListener(e -> { closeFloatingWindows(); alignSelectedObjects("left"); });
            buttonPanel.add(alignLeft);

            JButton alignRight = new JButton("Align Right");
            styleButton.accept(alignRight);
            alignRight.addActionListener(e -> { closeFloatingWindows(); alignSelectedObjects("right"); });
            buttonPanel.add(alignRight);

            JButton alignTop = new JButton("Align Top");
            styleButton.accept(alignTop);
            alignTop.addActionListener(e -> { closeFloatingWindows(); alignSelectedObjects("top"); });
            buttonPanel.add(alignTop);

            JButton alignBottom = new JButton("Align Bottom");
            styleButton.accept(alignBottom);
            alignBottom.addActionListener(e -> { closeFloatingWindows(); alignSelectedObjects("bottom"); });
            buttonPanel.add(alignBottom);

            JButton alignCenter = new JButton("Align Center");
            styleButton.accept(alignCenter);
            alignCenter.addActionListener(e -> { closeFloatingWindows(); alignSelectedObjects("center"); });
            buttonPanel.add(alignCenter);

            JSeparator sep = new JSeparator();
            sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            buttonPanel.add(sep);

            JButton distHoriz = new JButton("Distribute Horizontally");
            styleButton.accept(distHoriz);
            distHoriz.addActionListener(e -> { closeFloatingWindows(); alignSelectedObjects("distribute_h"); });
            buttonPanel.add(distHoriz);

            JButton distVert = new JButton("Distribute Vertically");
            styleButton.accept(distVert);
            distVert.addActionListener(e -> { closeFloatingWindows(); alignSelectedObjects("distribute_v"); });
            buttonPanel.add(distVert);

            alignWindow.add(buttonPanel);
            alignWindow.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
            alignWindow.pack();

            int windowX = activeColorBar != null ? colorBarLeftX : (getLocationOnScreen().x + x);
            int windowY = activeColorBar != null ? (colorBarBottomY + 10) : (getLocationOnScreen().y + y);

            alignWindow.setLocation(windowX, windowY);
            alignWindow.setVisible(true);
            activeAlignmentWindow = alignWindow;
        }

        private void closeFloatingWindows() {
            if (activeColorBar != null) { activeColorBar.dispose(); activeColorBar = null; }
            if (activeAlignmentWindow != null) { activeAlignmentWindow.dispose(); activeAlignmentWindow = null; }
        }

        private void showFloatingColorBar(int x, int y, boolean autoClose) {
            JWindow colorBar = new JWindow(SwingUtilities.getWindowAncestor(this));
            colorBar.setLayout(new BorderLayout());

            Color currentFillColor = Color.GRAY;
            Color currentOutlineColor = Color.DARK_GRAY;

            if (!selectedTaskIndices.isEmpty()) {
                int idx = selectedTaskIndices.iterator().next();
                if (idx >= 0 && idx < tasks.size()) {
                    TimelineTask task = tasks.get(idx);
                    currentFillColor = task.fillColor != null ? task.fillColor : TASK_COLORS[idx % TASK_COLORS.length];
                    currentOutlineColor = task.outlineColor != null ? task.outlineColor : currentFillColor.darker();
                }
            } else if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                TimelineMilestone ms = milestones.get(selectedMilestoneIndex);
                currentFillColor = ms.fillColor;
                currentOutlineColor = ms.outlineColor;
            }

            // Load fill icon
            ImageIcon fillIcon = null;
            try {
                java.awt.Image img = javax.imageio.ImageIO.read(new java.io.File("colorbar_fill.png"));
                img = img.getScaledInstance(23, 23, java.awt.Image.SCALE_SMOOTH);
                fillIcon = new ImageIcon(img);
            } catch (Exception ex) { /* icon not found */ }

            // Create columns panel for proper centering
            JPanel columnsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
            columnsPanel.setOpaque(false);

            // Fill column (icon, color picker, label stacked vertically)
            JPanel fillColumn = new JPanel();
            fillColumn.setLayout(new BoxLayout(fillColumn, BoxLayout.Y_AXIS));
            fillColumn.setOpaque(false);
            if (fillIcon != null) {
                JLabel fillIconLabel = new JLabel(fillIcon);
                fillIconLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
                fillColumn.add(fillIconLabel);
                fillColumn.add(Box.createVerticalStrut(1));
            }

            JButton fillBtn = new JButton();
            fillBtn.setPreferredSize(new Dimension(24, 5));
            fillBtn.setMaximumSize(new Dimension(24, 5));
            fillBtn.setBackground(currentFillColor);
            fillBtn.setOpaque(true);
            fillBtn.setContentAreaFilled(true);
            fillBtn.setToolTipText("Fill Color");
            fillBtn.setBorder(null);
            fillBtn.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            final Color fillStartColor = currentFillColor;
            fillBtn.addActionListener(ev -> {
                activeColorBar = null;
                colorBar.dispose();
                if (activeAlignmentWindow != null) { activeAlignmentWindow.dispose(); activeAlignmentWindow = null; }

                saveState();
                Color newColor = showColorChooserWithAlpha("Choose Fill Color", fillStartColor, color -> {
                    for (int ti : selectedTaskIndices) { tasks.get(ti).fillColor = color; }
                    if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                        milestones.get(selectedMilestoneIndex).fillColor = color;
                    }
                    fillColorBtn.setBackground(color);
                    timelineDisplayPanel.repaint();
                });
                if (newColor != null) {
                    for (int ti : selectedTaskIndices) { tasks.get(ti).fillColor = newColor; }
                    if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                        milestones.get(selectedMilestoneIndex).fillColor = newColor;
                    }
                    fillColorBtn.setBackground(newColor);
                    refreshTimeline();
                } else { undo(); }
            });
            fillColumn.add(fillBtn);
            fillColumn.add(Box.createVerticalStrut(2));
            JLabel fillLabel = new JLabel("Fill");
            fillLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            fillLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            fillColumn.add(fillLabel);

            // Outline column (placeholder for icon, color picker, label)
            JPanel outlineColumn = new JPanel();
            outlineColumn.setLayout(new BoxLayout(outlineColumn, BoxLayout.Y_AXIS));
            outlineColumn.setOpaque(false);
            outlineColumn.add(Box.createVerticalStrut(24)); // placeholder for icon

            JButton outlineBtn = new JButton();
            outlineBtn.setPreferredSize(new Dimension(24, 5));
            outlineBtn.setMaximumSize(new Dimension(24, 5));
            outlineBtn.setBackground(currentOutlineColor);
            outlineBtn.setOpaque(true);
            outlineBtn.setContentAreaFilled(true);
            outlineBtn.setToolTipText("Outline Color");
            outlineBtn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
            outlineBtn.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            final Color outlineStartColor = currentOutlineColor;
            outlineBtn.addActionListener(ev -> {
                activeColorBar = null;
                colorBar.dispose();
                if (activeAlignmentWindow != null) { activeAlignmentWindow.dispose(); activeAlignmentWindow = null; }

                saveState();
                Color newColor = showColorChooserWithAlpha("Choose Outline Color", outlineStartColor, color -> {
                    for (int ti : selectedTaskIndices) { tasks.get(ti).outlineColor = color; }
                    if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                        milestones.get(selectedMilestoneIndex).outlineColor = color;
                    }
                    outlineColorBtn.setBackground(color);
                    timelineDisplayPanel.repaint();
                });
                if (newColor != null) {
                    for (int ti : selectedTaskIndices) { tasks.get(ti).outlineColor = newColor; }
                    if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()) {
                        milestones.get(selectedMilestoneIndex).outlineColor = newColor;
                    }
                    outlineColorBtn.setBackground(newColor);
                    refreshTimeline();
                } else { undo(); }
            });
            outlineColumn.add(outlineBtn);
            outlineColumn.add(Box.createVerticalStrut(2));
            JLabel outlineLabel = new JLabel("Outline");
            outlineLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            outlineLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            outlineColumn.add(outlineLabel);

            columnsPanel.add(fillColumn);
            columnsPanel.add(outlineColumn);

            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBackground(new Color(245, 245, 245));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            contentPanel.add(columnsPanel, BorderLayout.CENTER);

            colorBar.add(contentPanel);
            colorBar.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
            colorBar.pack();

            Point screenPos = getLocationOnScreen();
            int barX = screenPos.x + x + 10;
            int barY = screenPos.y + y - colorBar.getHeight() - 10;

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (barX + colorBar.getWidth() > screenSize.width) { barX = screenPos.x + x - colorBar.getWidth() - 10; }
            if (barY < 0) { barY = screenPos.y + y + 20; }

            colorBar.setLocation(barX, barY);
            colorBar.setVisible(true);
            activeColorBar = colorBar;
            colorBarLeftX = barX;
            colorBarBottomY = barY + colorBar.getHeight();

            if (autoClose) {
                colorBar.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
                    public void windowGainedFocus(java.awt.event.WindowEvent e) {}
                    public void windowLostFocus(java.awt.event.WindowEvent e) { colorBar.dispose(); }
                });
            }
        }

        private void alignSelectedObjects(String alignment) {
            saveState();

            // Gather all selected objects with their bounds
            java.util.List<Object> selectedObjects = new java.util.ArrayList<>();
            java.util.List<int[]> bounds = new java.util.ArrayList<>(); // [x, y, width, height]

            // Add selected tasks
            for (int idx : selectedTaskIndices) {
                if (idx >= 0 && idx < tasks.size()) {
                    TimelineTask task = tasks.get(idx);
                    selectedObjects.add(task);
                    try {
                        LocalDate taskStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                        LocalDate taskEnd = LocalDate.parse(task.endDate, DATE_FORMAT);
                        int timelineX = MARGIN_LEFT;
                        int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
                        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
                        int x1 = getXForDate(taskStart, timelineX, timelineWidth, totalDays);
                        int x2 = getXForDate(taskEnd, timelineX, timelineWidth, totalDays);
                        int y = task.yPosition >= 0 ? task.yPosition : 100;
                        bounds.add(new int[]{x1, y, x2 - x1, task.height});
                    } catch (Exception ex) {}
                }
            }

            // Add selected milestones
            for (int idx : selectedMilestoneIndices) {
                if (idx >= 0 && idx < milestones.size()) {
                    TimelineMilestone ms = milestones.get(idx);
                    selectedObjects.add(ms);
                    try {
                        LocalDate msDate = LocalDate.parse(ms.date, DATE_FORMAT);
                        int timelineX = MARGIN_LEFT;
                        int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
                        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
                        int mx = getXForDate(msDate, timelineX, timelineWidth, totalDays);
                        int my = ms.yPosition >= 0 ? ms.yPosition : 100;
                        bounds.add(new int[]{mx - ms.width/2, my - ms.height/2, ms.width, ms.height});
                    } catch (Exception ex) {}
                }
            }

            // Also add primary selected milestone if not in the set
            if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()
                && !selectedMilestoneIndices.contains(selectedMilestoneIndex)) {
                TimelineMilestone ms = milestones.get(selectedMilestoneIndex);
                selectedObjects.add(ms);
                try {
                    LocalDate msDate = LocalDate.parse(ms.date, DATE_FORMAT);
                    int timelineX = MARGIN_LEFT;
                    int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
                    long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
                    int mx = getXForDate(msDate, timelineX, timelineWidth, totalDays);
                    int my = ms.yPosition >= 0 ? ms.yPosition : 100;
                    bounds.add(new int[]{mx - ms.width/2, my - ms.height/2, ms.width, ms.height});
                } catch (Exception ex) {}
            }

            if (bounds.size() < 2) return;

            // Find alignment target based on alignment type
            int targetValue = 0;
            switch (alignment) {
                case "left":
                    targetValue = Integer.MAX_VALUE;
                    for (int[] b : bounds) targetValue = Math.min(targetValue, b[0]);
                    break;
                case "right":
                    targetValue = Integer.MIN_VALUE;
                    for (int[] b : bounds) targetValue = Math.max(targetValue, b[0] + b[2]);
                    break;
                case "top":
                    targetValue = Integer.MAX_VALUE;
                    for (int[] b : bounds) targetValue = Math.min(targetValue, b[1]);
                    break;
                case "bottom":
                    targetValue = Integer.MIN_VALUE;
                    for (int[] b : bounds) targetValue = Math.max(targetValue, b[1] + b[3]);
                    break;
                case "center":
                    // Find average center X
                    int sumCenterX = 0;
                    for (int[] b : bounds) sumCenterX += b[0] + b[2] / 2;
                    targetValue = sumCenterX / bounds.size();
                    break;
                case "distribute_h":
                case "distribute_v":
                    // Handle distribution separately
                    distributeSelectedObjects(alignment.equals("distribute_h"), selectedObjects, bounds);
                    return;
            }

            // Apply alignment
            int timelineX = MARGIN_LEFT;
            int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);

            for (int i = 0; i < selectedObjects.size(); i++) {
                Object obj = selectedObjects.get(i);
                int[] b = bounds.get(i);

                if (obj instanceof TimelineTask) {
                    TimelineTask task = (TimelineTask) obj;
                    switch (alignment) {
                        case "left":
                            // Move task so its left edge is at targetValue
                            LocalDate newStart = getDateForX(targetValue, timelineX, timelineWidth, totalDays);
                            if (newStart != null) {
                                try {
                                    LocalDate oldStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                                    LocalDate oldEnd = LocalDate.parse(task.endDate, DATE_FORMAT);
                                    long duration = ChronoUnit.DAYS.between(oldStart, oldEnd);
                                    task.startDate = newStart.format(DATE_FORMAT);
                                    task.endDate = newStart.plusDays(duration).format(DATE_FORMAT);
                                } catch (Exception ex) {}
                            }
                            break;
                        case "right":
                            // Move task so its right edge is at targetValue
                            LocalDate newEnd = getDateForX(targetValue, timelineX, timelineWidth, totalDays);
                            if (newEnd != null) {
                                try {
                                    LocalDate oldStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                                    LocalDate oldEnd = LocalDate.parse(task.endDate, DATE_FORMAT);
                                    long duration = ChronoUnit.DAYS.between(oldStart, oldEnd);
                                    task.endDate = newEnd.format(DATE_FORMAT);
                                    task.startDate = newEnd.minusDays(duration).format(DATE_FORMAT);
                                } catch (Exception ex) {}
                            }
                            break;
                        case "top":
                            task.yPosition = targetValue;
                            break;
                        case "bottom":
                            task.yPosition = targetValue - task.height;
                            break;
                        case "center":
                            // Move task so its center is at targetValue
                            try {
                                LocalDate oldStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                                LocalDate oldEnd = LocalDate.parse(task.endDate, DATE_FORMAT);
                                long duration = ChronoUnit.DAYS.between(oldStart, oldEnd);
                                int taskWidth = b[2];
                                LocalDate newCenter = getDateForX(targetValue, timelineX, timelineWidth, totalDays);
                                if (newCenter != null) {
                                    LocalDate newStartDate = getDateForX(targetValue - taskWidth/2, timelineX, timelineWidth, totalDays);
                                    if (newStartDate != null) {
                                        task.startDate = newStartDate.format(DATE_FORMAT);
                                        task.endDate = newStartDate.plusDays(duration).format(DATE_FORMAT);
                                    }
                                }
                            } catch (Exception ex) {}
                            break;
                    }
                } else if (obj instanceof TimelineMilestone) {
                    TimelineMilestone ms = (TimelineMilestone) obj;
                    switch (alignment) {
                        case "left":
                            // Move milestone so its left edge is at targetValue
                            LocalDate newDate = getDateForX(targetValue + ms.width/2, timelineX, timelineWidth, totalDays);
                            if (newDate != null) {
                                ms.date = newDate.format(DATE_FORMAT);
                            }
                            break;
                        case "right":
                            // Move milestone so its right edge is at targetValue
                            newDate = getDateForX(targetValue - ms.width/2, timelineX, timelineWidth, totalDays);
                            if (newDate != null) {
                                ms.date = newDate.format(DATE_FORMAT);
                            }
                            break;
                        case "top":
                            ms.yPosition = targetValue + ms.height/2;
                            break;
                        case "bottom":
                            ms.yPosition = targetValue - ms.height/2;
                            break;
                        case "center":
                            // Move milestone so its center is at targetValue
                            newDate = getDateForX(targetValue, timelineX, timelineWidth, totalDays);
                            if (newDate != null) {
                                ms.date = newDate.format(DATE_FORMAT);
                            }
                            break;
                    }
                }
            }

            refreshTimeline();
        }

        private void distributeSelectedObjects(boolean horizontal, java.util.List<Object> objects, java.util.List<int[]> bounds) {
            if (objects.size() < 3) return; // Need at least 3 items to distribute

            int timelineX = MARGIN_LEFT;
            int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);

            // Sort objects by position
            java.util.List<Integer> indices = new java.util.ArrayList<>();
            for (int i = 0; i < objects.size(); i++) indices.add(i);

            if (horizontal) {
                // Sort by X position (center)
                indices.sort((a, b) -> {
                    int centerA = bounds.get(a)[0] + bounds.get(a)[2] / 2;
                    int centerB = bounds.get(b)[0] + bounds.get(b)[2] / 2;
                    return Integer.compare(centerA, centerB);
                });

                // Get first and last center positions
                int firstCenter = bounds.get(indices.get(0))[0] + bounds.get(indices.get(0))[2] / 2;
                int lastCenter = bounds.get(indices.get(indices.size() - 1))[0] + bounds.get(indices.get(indices.size() - 1))[2] / 2;
                int totalSpan = lastCenter - firstCenter;
                int spacing = totalSpan / (indices.size() - 1);

                // Distribute middle items
                for (int i = 1; i < indices.size() - 1; i++) {
                    int idx = indices.get(i);
                    Object obj = objects.get(idx);
                    int[] b = bounds.get(idx);
                    int newCenterX = firstCenter + spacing * i;

                    if (obj instanceof TimelineTask) {
                        TimelineTask task = (TimelineTask) obj;
                        try {
                            LocalDate oldStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                            LocalDate oldEnd = LocalDate.parse(task.endDate, DATE_FORMAT);
                            long duration = ChronoUnit.DAYS.between(oldStart, oldEnd);
                            LocalDate newStart = getDateForX(newCenterX - b[2] / 2, timelineX, timelineWidth, totalDays);
                            if (newStart != null) {
                                task.startDate = newStart.format(DATE_FORMAT);
                                task.endDate = newStart.plusDays(duration).format(DATE_FORMAT);
                            }
                        } catch (Exception ex) {}
                    } else if (obj instanceof TimelineMilestone) {
                        TimelineMilestone ms = (TimelineMilestone) obj;
                        LocalDate newDate = getDateForX(newCenterX, timelineX, timelineWidth, totalDays);
                        if (newDate != null) {
                            ms.date = newDate.format(DATE_FORMAT);
                        }
                    }
                }
            } else {
                // Sort by Y position (center)
                indices.sort((a, b) -> {
                    int centerA = bounds.get(a)[1] + bounds.get(a)[3] / 2;
                    int centerB = bounds.get(b)[1] + bounds.get(b)[3] / 2;
                    return Integer.compare(centerA, centerB);
                });

                // Get first and last center positions
                int firstCenter = bounds.get(indices.get(0))[1] + bounds.get(indices.get(0))[3] / 2;
                int lastCenter = bounds.get(indices.get(indices.size() - 1))[1] + bounds.get(indices.get(indices.size() - 1))[3] / 2;
                int totalSpan = lastCenter - firstCenter;
                int spacing = totalSpan / (indices.size() - 1);

                // Distribute middle items
                for (int i = 1; i < indices.size() - 1; i++) {
                    int idx = indices.get(i);
                    Object obj = objects.get(idx);
                    int[] b = bounds.get(idx);
                    int newCenterY = firstCenter + spacing * i;

                    if (obj instanceof TimelineTask) {
                        TimelineTask task = (TimelineTask) obj;
                        task.yPosition = newCenterY - task.height / 2;
                    } else if (obj instanceof TimelineMilestone) {
                        TimelineMilestone ms = (TimelineMilestone) obj;
                        ms.yPosition = newCenterY;
                    }
                }
            }

            refreshTimeline();
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

        private void startMultiDrag(int x, int y) {
            isMultiDragging = true;
            multiDragStartX = x;
            multiDragStartY = y;

            // Store original positions for all selected tasks
            multiDragTaskOriginalStarts.clear();
            multiDragTaskOriginalEnds.clear();
            multiDragTaskOriginalY.clear();
            for (int idx : selectedTaskIndices) {
                if (idx >= 0 && idx < tasks.size()) {
                    TimelineTask task = tasks.get(idx);
                    multiDragTaskOriginalStarts.put(idx, task.startDate);
                    multiDragTaskOriginalEnds.put(idx, task.endDate);
                    multiDragTaskOriginalY.put(idx, task.yPosition >= 0 ? task.yPosition : 100);
                }
            }

            // Store original positions for all selected milestones
            multiDragMilestoneOriginalDates.clear();
            multiDragMilestoneOriginalY.clear();
            for (int idx : selectedMilestoneIndices) {
                if (idx >= 0 && idx < milestones.size()) {
                    TimelineMilestone ms = milestones.get(idx);
                    multiDragMilestoneOriginalDates.put(idx, ms.date);
                    multiDragMilestoneOriginalY.put(idx, ms.yPosition >= 0 ? ms.yPosition : 100);
                }
            }
            // Also include primary selected milestone if not in the set
            if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()
                && !selectedMilestoneIndices.contains(selectedMilestoneIndex)) {
                TimelineMilestone ms = milestones.get(selectedMilestoneIndex);
                multiDragMilestoneOriginalDates.put(selectedMilestoneIndex, ms.date);
                multiDragMilestoneOriginalY.put(selectedMilestoneIndex, ms.yPosition >= 0 ? ms.yPosition : 100);
            }
        }

        private void handleMultiDrag(int x, int y) {
            int timelineX = MARGIN_LEFT;
            int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            if (totalDays <= 0) return;

            int deltaX = x - multiDragStartX;
            int deltaY = y - multiDragStartY;
            double daysPerPixel = (double) totalDays / timelineWidth;
            long daysDelta = Math.round(deltaX * daysPerPixel);

            // Move all selected tasks
            for (java.util.Map.Entry<Integer, String> entry : multiDragTaskOriginalStarts.entrySet()) {
                int idx = entry.getKey();
                if (idx >= 0 && idx < tasks.size()) {
                    TimelineTask task = tasks.get(idx);
                    try {
                        LocalDate origStart = LocalDate.parse(entry.getValue(), DATE_FORMAT);
                        LocalDate origEnd = LocalDate.parse(multiDragTaskOriginalEnds.get(idx), DATE_FORMAT);
                        task.startDate = origStart.plusDays(daysDelta).format(DATE_FORMAT);
                        task.endDate = origEnd.plusDays(daysDelta).format(DATE_FORMAT);
                    } catch (Exception ex) {}

                    Integer origY = multiDragTaskOriginalY.get(idx);
                    if (origY != null) {
                        task.yPosition = Math.max(35, origY + deltaY);
                    }
                }
            }

            // Move all selected milestones
            for (java.util.Map.Entry<Integer, String> entry : multiDragMilestoneOriginalDates.entrySet()) {
                int idx = entry.getKey();
                if (idx >= 0 && idx < milestones.size()) {
                    TimelineMilestone ms = milestones.get(idx);
                    try {
                        LocalDate origDate = LocalDate.parse(entry.getValue(), DATE_FORMAT);
                        LocalDate newDate = origDate.plusDays(daysDelta);
                        if (newDate.isBefore(startDate)) newDate = startDate;
                        if (newDate.isAfter(endDate)) newDate = endDate;
                        ms.date = newDate.format(DATE_FORMAT);
                    } catch (Exception ex) {}

                    Integer origY = multiDragMilestoneOriginalY.get(idx);
                    if (origY != null) {
                        ms.yPosition = Math.max(35, origY + deltaY);
                    }
                }
            }

            repaint();
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

            int lowestItemBottom = getLowestItemBottom();
            int eventsHeight = events.size() * 90 + 100;
            setPreferredSize(new Dimension(600, lowestItemBottom + 100 + eventsHeight));
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

            // Fill background with gradient or solid color
            if (timelineUseGradient && timelineGradientStops.size() >= 2) {
                int w = getWidth();
                int h = getHeight();

                // Build arrays for LinearGradientPaint
                float[] fractions = new float[timelineGradientStops.size()];
                Color[] colors = new Color[timelineGradientStops.size()];
                for (int i = 0; i < timelineGradientStops.size(); i++) {
                    float[] stop = timelineGradientStops.get(i);
                    fractions[i] = stop[0];
                    colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                }

                java.awt.LinearGradientPaint lgp = createAngledGradient(w, h, timelineGradientAngle, fractions, colors);
                g2d.setPaint(lgp);
            } else if (timelineUseGradient) {
                // Fallback to 2-color gradient using angle
                int w = getWidth();
                int h = getHeight();
                float[] fractions = {0f, 1f};
                Color[] colors = {timelineInteriorColor, timelineInteriorColor2};
                java.awt.LinearGradientPaint lgp = createAngledGradient(w, h, timelineGradientAngle, fractions, colors);
                g2d.setPaint(lgp);
            } else {
                g2d.setColor(timelineInteriorColor);
            }
            g2d.fillRect(0, 0, getWidth(), getHeight());

            if (startDate == null || endDate == null) return;

            int timelineX = MARGIN_LEFT;
            int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
            int tasksHeight = getTotalTasksHeight();
            int timelineY;
            if ("Top".equals(timelineAxisPosition)) {
                timelineY = getHighestItemTop() - 65; // 65 pixels above highest item
            } else {
                timelineY = getLowestItemBottom() + 30; // 30 pixels below lowest item (Bottom)
            }

            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            if (totalDays <= 0) totalDays = 1;

            // Draw "today" arrow at top
            if (showTodayMark) {
            LocalDate today = LocalDate.now();
            if (!today.isBefore(startDate) && !today.isAfter(endDate)) {
                long todayOffset = ChronoUnit.DAYS.between(startDate, today);
                int todayX = timelineX + (int) (todayOffset * timelineWidth / totalDays);
                g2d.setColor(new Color(220, 50, 50));  // Red color
                // Draw "Today" text above arrow
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                FontMetrics fm = g2d.getFontMetrics();
                String todayText = "Today";
                int textWidth = fm.stringWidth(todayText);
                g2d.drawString(todayText, todayX - textWidth / 2, 33);
                // Draw down arrow
                int arrowY = 35;
                int arrowSize = 8;
                int[] xPoints = {todayX - arrowSize, todayX + arrowSize, todayX};
                int[] yPoints = {arrowY, arrowY, arrowY + arrowSize + 4};
                g2d.fillPolygon(xPoints, yPoints, 3);
            }
            }

            // Draw extended ticks behind tasks and milestones
            drawExtendedTicks(g2d, timelineX, timelineWidth, timelineY, totalDays);

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
            g2d.setColor(timelineAxisColor);
            g2d.setStroke(new BasicStroke(timelineAxisThickness));
            g2d.drawLine(timelineX, timelineY, timelineX + timelineWidth, timelineY);

            // Date ticks
            drawDateTicks(g2d, timelineX, timelineWidth, timelineY, totalDays);

            // Events
            drawEvents(g2d, timelineX, timelineWidth, timelineY, totalDays);

            // Draw selection box if dragging
            if (isSelectionBoxDragging && selectionBoxStartX >= 0) {
                int boxLeft = Math.min(selectionBoxStartX, selectionBoxEndX);
                int boxTop = Math.min(selectionBoxStartY, selectionBoxEndY);
                int boxWidth = Math.abs(selectionBoxEndX - selectionBoxStartX);
                int boxHeight = Math.abs(selectionBoxEndY - selectionBoxStartY);

                // Draw semi-transparent fill
                g2d.setColor(new Color(100, 149, 237, 50)); // Cornflower blue with transparency
                g2d.fillRect(boxLeft, boxTop, boxWidth, boxHeight);

                // Draw border
                g2d.setColor(new Color(100, 149, 237)); // Cornflower blue
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f, 3.0f}, 0.0f));
                g2d.drawRect(boxLeft, boxTop, boxWidth, boxHeight);
            }
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
                if (task.behindTextVisible && task.behindText != null && !task.behindText.isEmpty()) {
                    int behindFontStyle = Font.PLAIN;
                    if (task.behindFontBold) behindFontStyle |= Font.BOLD;
                    if (task.behindFontItalic) behindFontStyle |= Font.ITALIC;
                    g2d.setFont(new Font(task.behindFontFamily, behindFontStyle, task.behindFontSize));
                    g2d.setColor(task.behindTextColor != null ? task.behindTextColor : new Color(150, 150, 150));
                    FontMetrics behindFm = g2d.getFontMetrics();
                    if (task.behindTextWrap) {
                        drawWrappedText(g2d, task.behindText, x1 + barWidth + 5, y + taskHeight / 2, barWidth, task.behindTextXOffset, task.behindTextYOffset, false);
                    } else {
                        g2d.drawString(task.behindText, x1 + barWidth + 5 + task.behindTextXOffset,
                                       y + (taskHeight + behindFm.getAscent() - behindFm.getDescent()) / 2 + task.behindTextYOffset);
                    }
                }

                // Draw front text (in front of task bar)
                if (task.frontTextVisible && task.frontText != null && !task.frontText.isEmpty()) {
                    int frontFontStyle = Font.PLAIN;
                    if (task.frontFontBold) frontFontStyle |= Font.BOLD;
                    if (task.frontFontItalic) frontFontStyle |= Font.ITALIC;
                    g2d.setFont(new Font(task.frontFontFamily, frontFontStyle, task.frontFontSize));
                    g2d.setColor(task.frontTextColor != null ? task.frontTextColor : Color.BLACK);
                    FontMetrics frontFm = g2d.getFontMetrics();
                    if (task.frontTextWrap) {
                        // For wrapped front text, draw right-aligned to x1 - 5
                        int wrapWidth = barWidth;
                        drawWrappedText(g2d, task.frontText, x1 - wrapWidth - 5, y + taskHeight / 2, wrapWidth, task.frontTextXOffset, task.frontTextYOffset, false);
                    } else {
                        int frontTextWidth = frontFm.stringWidth(task.frontText);
                        g2d.drawString(task.frontText, x1 - frontTextWidth - 5 + task.frontTextXOffset,
                                       y + (taskHeight + frontFm.getAscent() - frontFm.getDescent()) / 2 + task.frontTextYOffset);
                    }
                }

                if (task.bevelFill) {
                    // Draw beveled fill effect using task's bevel settings
                    int depth = task.bevelDepth;
                    Color lighter = new Color(
                        Math.min(255, fillColor.getRed() + depth),
                        Math.min(255, fillColor.getGreen() + depth),
                        Math.min(255, fillColor.getBlue() + depth));
                    Color darker = new Color(
                        Math.max(0, fillColor.getRed() - depth),
                        Math.max(0, fillColor.getGreen() - depth),
                        Math.max(0, fillColor.getBlue() - depth));

                    String style = task.bevelStyle != null ? task.bevelStyle : "Inner Bevel";

                    if (style.equals("Pillow Emboss")) {
                        // Pillow emboss - inward pressed effect
                        g2d.setColor(fillColor);
                        g2d.fillRoundRect(x1, y, barWidth, taskHeight, 8, 8);
                        // Inner shadow (top-left)
                        g2d.setColor(new Color(0, 0, 0, task.bevelShadowOpacity));
                        g2d.setStroke(new BasicStroke(Math.max(1, depth / 20)));
                        g2d.drawLine(x1 + 4, y + 2, x1 + barWidth - 4, y + 2);
                        g2d.drawLine(x1 + 2, y + 4, x1 + 2, y + taskHeight - 4);
                        // Inner highlight (bottom-right)
                        g2d.setColor(new Color(255, 255, 255, task.bevelHighlightOpacity));
                        g2d.drawLine(x1 + 4, y + taskHeight - 2, x1 + barWidth - 4, y + taskHeight - 2);
                        g2d.drawLine(x1 + barWidth - 2, y + 4, x1 + barWidth - 2, y + taskHeight - 4);
                    } else if (style.equals("Outer Bevel")) {
                        // Outer bevel - raised effect with outer edges
                        double angleRad = Math.toRadians(task.bevelLightAngle);
                        float dx = (float) Math.cos(angleRad) * barWidth;
                        float dy = (float) Math.sin(angleRad) * taskHeight;
                        float startX = x1 + barWidth / 2f - dx / 2;
                        float startY = y + taskHeight / 2f - dy / 2;
                        float endX = x1 + barWidth / 2f + dx / 2;
                        float endY = y + taskHeight / 2f + dy / 2;
                        GradientPaint gradient = new GradientPaint(startX, startY, lighter, endX, endY, darker);
                        g2d.setPaint(gradient);
                        g2d.fillRoundRect(x1, y, barWidth, taskHeight, 8, 8);
                        // Outer highlight edge (thicker, outside feel)
                        int edgeWidth = Math.max(2, depth / 15);
                        g2d.setColor(new Color(255, 255, 255, task.bevelHighlightOpacity));
                        g2d.setStroke(new BasicStroke(edgeWidth));
                        g2d.drawLine(x1 - 1, y - 1, x1 + barWidth + 1, y - 1);
                        g2d.drawLine(x1 - 1, y - 1, x1 - 1, y + taskHeight + 1);
                        // Outer shadow edge
                        g2d.setColor(new Color(0, 0, 0, task.bevelShadowOpacity));
                        g2d.drawLine(x1 - 1, y + taskHeight + 1, x1 + barWidth + 1, y + taskHeight + 1);
                        g2d.drawLine(x1 + barWidth + 1, y - 1, x1 + barWidth + 1, y + taskHeight + 1);
                    } else if (style.equals("Emboss")) {
                        // Emboss - raised text/stamp effect
                        g2d.setColor(fillColor);
                        g2d.fillRoundRect(x1, y, barWidth, taskHeight, 8, 8);
                        int offset = Math.max(1, depth / 30);
                        // Shadow offset (bottom-right)
                        g2d.setColor(new Color(0, 0, 0, task.bevelShadowOpacity));
                        g2d.setStroke(new BasicStroke(Math.max(1, depth / 25)));
                        g2d.drawRoundRect(x1 + offset, y + offset, barWidth, taskHeight, 8, 8);
                        // Highlight offset (top-left)
                        g2d.setColor(new Color(255, 255, 255, task.bevelHighlightOpacity));
                        g2d.drawRoundRect(x1 - offset, y - offset, barWidth, taskHeight, 8, 8);
                    } else {
                        // Inner Bevel - default inward gradient bevel
                        double angleRad = Math.toRadians(task.bevelLightAngle);
                        float dx = (float) Math.cos(angleRad) * barWidth;
                        float dy = (float) Math.sin(angleRad) * taskHeight;
                        float startX = x1 + barWidth / 2f - dx / 2;
                        float startY = y + taskHeight / 2f - dy / 2;
                        float endX = x1 + barWidth / 2f + dx / 2;
                        float endY = y + taskHeight / 2f + dy / 2;
                        GradientPaint gradient = new GradientPaint(startX, startY, lighter, endX, endY, darker);
                        g2d.setPaint(gradient);
                        g2d.fillRoundRect(x1, y, barWidth, taskHeight, 8, 8);
                        // Add highlight edge using task's highlight opacity
                        g2d.setColor(new Color(255, 255, 255, task.bevelHighlightOpacity));
                        g2d.setStroke(new BasicStroke(1));
                        g2d.drawLine(x1 + 4, y + 1, x1 + barWidth - 4, y + 1);
                        g2d.drawLine(x1 + 1, y + 4, x1 + 1, y + taskHeight - 4);
                        // Add shadow edge using task's shadow opacity
                        g2d.setColor(new Color(0, 0, 0, task.bevelShadowOpacity));
                        g2d.drawLine(x1 + 4, y + taskHeight - 1, x1 + barWidth - 4, y + taskHeight - 1);
                        g2d.drawLine(x1 + barWidth - 1, y + 4, x1 + barWidth - 1, y + taskHeight - 4);
                    }

                    // Draw top bevel edge shape
                    String topBevel = task.topBevel != null ? task.topBevel : "Circle";
                    if (!topBevel.equals("None")) {
                        drawBevelEdge(g2d, topBevel, x1, y, barWidth, taskHeight, depth,
                                     task.bevelHighlightOpacity, task.bevelShadowOpacity, true, lighter, darker);
                    }

                    // Draw bottom bevel edge shape
                    String bottomBevel = task.bottomBevel != null ? task.bottomBevel : "None";
                    if (!bottomBevel.equals("None")) {
                        drawBevelEdge(g2d, bottomBevel, x1, y, barWidth, taskHeight, depth,
                                     task.bevelHighlightOpacity, task.bevelShadowOpacity, false, lighter, darker);
                    }
                } else {
                    g2d.setColor(fillColor);
                    g2d.fillRoundRect(x1, y, barWidth, taskHeight, 8, 8);
                }
                int thickness = task.outlineThickness;
                if (thickness > 0) {
                    g2d.setColor(outlineColor);
                    g2d.setStroke(new BasicStroke(thickness));
                    g2d.drawRoundRect(x1, y, barWidth, taskHeight, 8, 8);
                }

                // Text - use custom formatting with centerText
                Color textColor = task.textColor != null ? task.textColor : Color.BLACK;
                // Draw center text
                if (task.centerTextVisible && task.centerText != null && !task.centerText.isEmpty()) {
                    g2d.setColor(textColor);
                    int fontStyle = Font.PLAIN;
                    if (task.fontBold) fontStyle |= Font.BOLD;
                    if (task.fontItalic) fontStyle |= Font.ITALIC;
                    g2d.setFont(new Font(task.fontFamily, fontStyle, task.fontSize));
                    FontMetrics fm = g2d.getFontMetrics();
                    String displayText = task.centerText;

                    if (task.centerTextWrap) {
                        // Draw wrapped text centered in the task bar
                        drawWrappedText(g2d, displayText, x1, y + taskHeight / 2, barWidth - 6, task.centerTextXOffset, task.centerTextYOffset, true);
                    } else {
                        // Original non-wrapped behavior
                        int textWidth = fm.stringWidth(displayText);
                        while (textWidth > barWidth - 10 && displayText.length() > 3) {
                            displayText = displayText.substring(0, displayText.length() - 4) + "...";
                            textWidth = fm.stringWidth(displayText);
                        }
                        if (textWidth <= barWidth - 6) {
                            g2d.drawString(displayText, x1 + (barWidth - textWidth) / 2 + task.centerTextXOffset,
                                           y + (taskHeight + fm.getAscent() - fm.getDescent()) / 2 + task.centerTextYOffset);
                        }
                    }
                }

                // Draw above text (above the task bar)
                if (task.aboveTextVisible && task.aboveText != null && !task.aboveText.isEmpty()) {
                    int aboveFontStyle = Font.PLAIN;
                    if (task.aboveFontBold) aboveFontStyle |= Font.BOLD;
                    if (task.aboveFontItalic) aboveFontStyle |= Font.ITALIC;
                    g2d.setFont(new Font(task.aboveFontFamily, aboveFontStyle, task.aboveFontSize));
                    g2d.setColor(task.aboveTextColor != null ? task.aboveTextColor : Color.BLACK);
                    FontMetrics aboveFm = g2d.getFontMetrics();
                    if (task.aboveTextWrap) {
                        drawWrappedText(g2d, task.aboveText, x1, y - aboveFm.getHeight() / 2 - 3, barWidth, task.aboveTextXOffset, task.aboveTextYOffset, true);
                    } else {
                        int aboveTextWidth = aboveFm.stringWidth(task.aboveText);
                        g2d.drawString(task.aboveText, x1 + (barWidth - aboveTextWidth) / 2 + task.aboveTextXOffset,
                                       y - 3 + task.aboveTextYOffset);
                    }
                }

                // Draw underneath text (below the task bar)
                if (task.underneathTextVisible && task.underneathText != null && !task.underneathText.isEmpty()) {
                    int underneathFontStyle = Font.PLAIN;
                    if (task.underneathFontBold) underneathFontStyle |= Font.BOLD;
                    if (task.underneathFontItalic) underneathFontStyle |= Font.ITALIC;
                    g2d.setFont(new Font(task.underneathFontFamily, underneathFontStyle, task.underneathFontSize));
                    g2d.setColor(task.underneathTextColor != null ? task.underneathTextColor : Color.BLACK);
                    FontMetrics underneathFm = g2d.getFontMetrics();
                    if (task.underneathTextWrap) {
                        drawWrappedText(g2d, task.underneathText, x1, y + taskHeight + underneathFm.getHeight() / 2 + 2, barWidth, task.underneathTextXOffset, task.underneathTextYOffset, true);
                    } else {
                        int underneathTextWidth = underneathFm.stringWidth(task.underneathText);
                        g2d.drawString(task.underneathText, x1 + (barWidth - underneathTextWidth) / 2 + task.underneathTextXOffset,
                                       y + taskHeight + underneathFm.getAscent() + 2 + task.underneathTextYOffset);
                    }
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
                int y;
                if (milestone.yPosition >= 0) {
                    y = milestone.yPosition;
                } else if ("Top".equals(timelineAxisPosition)) {
                    y = timelineY + milestone.height / 2 + 20; // Below axis when at top
                } else {
                    y = timelineY - milestone.height / 2 - 10; // Above axis when at bottom
                }
                boolean isSelected = (index == selectedMilestoneIndex) || selectedMilestoneIndices.contains(index);

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
                if (milestone.bevelFill) {
                    // Draw beveled fill effect for milestone using milestone's bevel settings
                    Color fillColor = milestone.fillColor;
                    int depth = milestone.bevelDepth;
                    Color lighter = new Color(
                        Math.min(255, fillColor.getRed() + depth),
                        Math.min(255, fillColor.getGreen() + depth),
                        Math.min(255, fillColor.getBlue() + depth));
                    Color darker = new Color(
                        Math.max(0, fillColor.getRed() - depth),
                        Math.max(0, fillColor.getGreen() - depth),
                        Math.max(0, fillColor.getBlue() - depth));
                    // Calculate gradient direction based on light angle
                    double angleRad = Math.toRadians(milestone.bevelLightAngle);
                    float dx = (float) Math.cos(angleRad) * milestone.width;
                    float dy = (float) Math.sin(angleRad) * milestone.height;
                    float startX = x - dx / 2;
                    float startY = y - dy / 2;
                    float endX = x + dx / 2;
                    float endY = y + dy / 2;
                    GradientPaint gradient = new GradientPaint(startX, startY, lighter, endX, endY, darker);
                    g2d.setPaint(gradient);
                    drawMilestoneShapeOnPanel(g2d, milestone.shape, x, y, milestone.width, milestone.height, true);
                } else {
                    g2d.setColor(milestone.fillColor);
                    drawMilestoneShapeOnPanel(g2d, milestone.shape, x, y, milestone.width, milestone.height, true);
                }

                // Draw outline
                if (milestone.outlineThickness > 0) {
                    g2d.setColor(milestone.outlineColor);
                    g2d.setStroke(new BasicStroke(milestone.outlineThickness));
                    drawMilestoneShapeOnPanel(g2d, milestone.shape, x, y, milestone.width, milestone.height, false);
                }

                // Draw behind text (to the right of milestone)
                if (milestone.behindTextVisible && milestone.behindText != null && !milestone.behindText.isEmpty()) {
                    int behindFontStyle = Font.PLAIN;
                    if (milestone.behindFontBold) behindFontStyle |= Font.BOLD;
                    if (milestone.behindFontItalic) behindFontStyle |= Font.ITALIC;
                    g2d.setFont(new Font(milestone.behindFontFamily, behindFontStyle, milestone.behindFontSize));
                    g2d.setColor(milestone.behindTextColor != null ? milestone.behindTextColor : new Color(150, 150, 150));
                    FontMetrics behindFm = g2d.getFontMetrics();
                    if (milestone.behindTextWrap) {
                        drawWrappedText(g2d, milestone.behindText, x + milestone.width / 2 + 5, y, milestone.width, milestone.behindTextXOffset, milestone.behindTextYOffset, false);
                    } else {
                        g2d.drawString(milestone.behindText, x + milestone.width / 2 + 5 + milestone.behindTextXOffset,
                                       y + (behindFm.getAscent() - behindFm.getDescent()) / 2 + milestone.behindTextYOffset);
                    }
                }

                // Draw front text (to the left of milestone)
                if (milestone.frontTextVisible && milestone.frontText != null && !milestone.frontText.isEmpty()) {
                    int frontFontStyle = Font.PLAIN;
                    if (milestone.frontFontBold) frontFontStyle |= Font.BOLD;
                    if (milestone.frontFontItalic) frontFontStyle |= Font.ITALIC;
                    g2d.setFont(new Font(milestone.frontFontFamily, frontFontStyle, milestone.frontFontSize));
                    g2d.setColor(milestone.frontTextColor != null ? milestone.frontTextColor : Color.BLACK);
                    FontMetrics frontFm = g2d.getFontMetrics();
                    if (milestone.frontTextWrap) {
                        drawWrappedText(g2d, milestone.frontText, x - milestone.width / 2 - milestone.width - 5, y, milestone.width, milestone.frontTextXOffset, milestone.frontTextYOffset, false);
                    } else {
                        int frontTextWidth = frontFm.stringWidth(milestone.frontText);
                        g2d.drawString(milestone.frontText, x - milestone.width / 2 - frontTextWidth - 5 + milestone.frontTextXOffset,
                                       y + (frontFm.getAscent() - frontFm.getDescent()) / 2 + milestone.frontTextYOffset);
                    }
                }

                // Draw above text (above the milestone)
                if (milestone.aboveTextVisible && milestone.aboveText != null && !milestone.aboveText.isEmpty()) {
                    int aboveFontStyle = Font.PLAIN;
                    if (milestone.aboveFontBold) aboveFontStyle |= Font.BOLD;
                    if (milestone.aboveFontItalic) aboveFontStyle |= Font.ITALIC;
                    g2d.setFont(new Font(milestone.aboveFontFamily, aboveFontStyle, milestone.aboveFontSize));
                    g2d.setColor(milestone.aboveTextColor != null ? milestone.aboveTextColor : Color.BLACK);
                    FontMetrics aboveFm = g2d.getFontMetrics();
                    if (milestone.aboveTextWrap) {
                        drawWrappedText(g2d, milestone.aboveText, x - milestone.width / 2, y - milestone.height / 2 - aboveFm.getHeight() / 2 - 3, milestone.width, milestone.aboveTextXOffset, milestone.aboveTextYOffset, true);
                    } else {
                        int aboveTextWidth = aboveFm.stringWidth(milestone.aboveText);
                        g2d.drawString(milestone.aboveText, x - aboveTextWidth / 2 + milestone.aboveTextXOffset,
                                       y - milestone.height / 2 - 3 + milestone.aboveTextYOffset);
                    }
                }

                // Draw center/label text below milestone
                if (milestone.centerTextVisible && milestone.centerText != null && !milestone.centerText.isEmpty()) {
                    int fontStyle = Font.PLAIN;
                    if (milestone.fontBold) fontStyle |= Font.BOLD;
                    if (milestone.fontItalic) fontStyle |= Font.ITALIC;
                    g2d.setFont(new Font(milestone.fontFamily, fontStyle, milestone.fontSize));
                    g2d.setColor(milestone.textColor);
                    FontMetrics fm = g2d.getFontMetrics();
                    if (milestone.centerTextWrap) {
                        drawWrappedText(g2d, milestone.centerText, x - milestone.width / 2, y + milestone.height / 2 + fm.getAscent() + 3, milestone.width, milestone.centerTextXOffset, milestone.centerTextYOffset, true);
                    } else {
                        int textWidth = fm.stringWidth(milestone.centerText);
                        g2d.drawString(milestone.centerText, x - textWidth / 2 + milestone.centerTextXOffset, y + milestone.height / 2 + fm.getAscent() + 3 + milestone.centerTextYOffset);
                    }
                }

                // Draw underneath text (further below the milestone, below center text)
                if (milestone.underneathTextVisible && milestone.underneathText != null && !milestone.underneathText.isEmpty()) {
                    int underneathFontStyle = Font.PLAIN;
                    if (milestone.underneathFontBold) underneathFontStyle |= Font.BOLD;
                    if (milestone.underneathFontItalic) underneathFontStyle |= Font.ITALIC;
                    g2d.setFont(new Font(milestone.underneathFontFamily, underneathFontStyle, milestone.underneathFontSize));
                    g2d.setColor(milestone.underneathTextColor != null ? milestone.underneathTextColor : Color.BLACK);
                    FontMetrics underneathFm = g2d.getFontMetrics();
                    // Position underneath text below the center text area
                    int centerOffset = milestone.fontSize + 5; // Account for center text space
                    if (milestone.underneathTextWrap) {
                        drawWrappedText(g2d, milestone.underneathText, x - milestone.width / 2, y + milestone.height / 2 + centerOffset + underneathFm.getAscent() + 3, milestone.width, milestone.underneathTextXOffset, milestone.underneathTextYOffset, true);
                    } else {
                        int underneathTextWidth = underneathFm.stringWidth(milestone.underneathText);
                        g2d.drawString(milestone.underneathText, x - underneathTextWidth / 2 + milestone.underneathTextXOffset,
                                       y + milestone.height / 2 + centerOffset + underneathFm.getAscent() + 3 + milestone.underneathTextYOffset);
                    }
                }
            } catch (Exception e) {}
        }

        private void drawBevelEdge(Graphics2D g2d, String bevelType, int x, int y, int width, int height,
                                   int depth, int highlightOpacity, int shadowOpacity, boolean isTop,
                                   Color lighter, Color darker) {
            int edgeHeight = Math.max(2, Math.min(depth / 8, height / 4));
            int yPos = isTop ? y : y + height - edgeHeight;
            Color highlightColor = new Color(255, 255, 255, highlightOpacity);
            Color shadowColor = new Color(0, 0, 0, shadowOpacity);

            g2d.setStroke(new BasicStroke(1));

            switch (bevelType) {
                case "Circle":
                    // Smooth rounded edge
                    GradientPaint circleGrad = isTop ?
                        new GradientPaint(x, yPos, highlightColor, x, yPos + edgeHeight, new Color(255,255,255,0)) :
                        new GradientPaint(x, yPos, new Color(0,0,0,0), x, yPos + edgeHeight, shadowColor);
                    g2d.setPaint(circleGrad);
                    g2d.fillRoundRect(x + 2, yPos, width - 4, edgeHeight, 4, 4);
                    break;

                case "Relaxed Inset":
                    // Gentle inward curve
                    g2d.setColor(isTop ? highlightColor : shadowColor);
                    for (int i = 0; i < edgeHeight; i++) {
                        int alpha = isTop ? highlightOpacity - (i * highlightOpacity / edgeHeight) :
                                          (i * shadowOpacity / edgeHeight);
                        g2d.setColor(new Color(isTop ? 255 : 0, isTop ? 255 : 0, isTop ? 255 : 0, Math.max(0, Math.min(255, alpha))));
                        g2d.drawLine(x + 4 + i, yPos + i, x + width - 4 - i, yPos + i);
                    }
                    break;

                case "Cross":
                    // Cross pattern
                    g2d.setColor(isTop ? highlightColor : shadowColor);
                    int crossSize = edgeHeight / 2;
                    for (int i = x + 8; i < x + width - 8; i += crossSize * 2) {
                        g2d.drawLine(i, yPos, i + crossSize, yPos + edgeHeight);
                        g2d.drawLine(i + crossSize, yPos, i, yPos + edgeHeight);
                    }
                    break;

                case "Angle":
                    // Sharp angled edge
                    g2d.setColor(isTop ? highlightColor : shadowColor);
                    int[] xAngle = {x + 4, x + width - 4, x + width - 4 - edgeHeight, x + 4 + edgeHeight};
                    int[] yAngle = isTop ? new int[]{yPos, yPos, yPos + edgeHeight, yPos + edgeHeight} :
                                          new int[]{yPos + edgeHeight, yPos + edgeHeight, yPos, yPos};
                    g2d.fillPolygon(xAngle, yAngle, 4);
                    break;

                case "Soft Round":
                    // Soft rounded highlight
                    GradientPaint softGrad = isTop ?
                        new GradientPaint(x, yPos, lighter, x, yPos + edgeHeight * 2, new Color(lighter.getRed(), lighter.getGreen(), lighter.getBlue(), 0)) :
                        new GradientPaint(x, yPos - edgeHeight, new Color(darker.getRed(), darker.getGreen(), darker.getBlue(), 0), x, yPos + edgeHeight, darker);
                    g2d.setPaint(softGrad);
                    g2d.fillRect(x + 2, yPos, width - 4, edgeHeight);
                    break;

                case "Convex":
                    // Outward curving bulge
                    g2d.setColor(isTop ? highlightColor : shadowColor);
                    for (int i = 0; i < edgeHeight; i++) {
                        double curve = Math.sin(Math.PI * i / edgeHeight);
                        int offset = (int)(curve * edgeHeight / 3);
                        int alpha = (int)((isTop ? highlightOpacity : shadowOpacity) * (1 - (double)i / edgeHeight));
                        g2d.setColor(new Color(isTop ? 255 : 0, isTop ? 255 : 0, isTop ? 255 : 0, Math.max(0, Math.min(255, alpha))));
                        g2d.drawLine(x + 4, yPos + i - (isTop ? offset : -offset), x + width - 4, yPos + i - (isTop ? offset : -offset));
                    }
                    break;

                case "Cool Slant":
                    // Slanted edge
                    g2d.setColor(isTop ? highlightColor : shadowColor);
                    int slant = edgeHeight;
                    int[] xSlant = {x + 4, x + width - 4, x + width - 4, x + 4 + slant};
                    int[] ySlant = isTop ? new int[]{yPos, yPos, yPos + edgeHeight, yPos + edgeHeight} :
                                          new int[]{yPos, yPos, yPos + edgeHeight, yPos + edgeHeight};
                    g2d.fillPolygon(xSlant, ySlant, 4);
                    break;

                case "Divot":
                    // Indented groove
                    g2d.setColor(isTop ? shadowColor : highlightColor);
                    g2d.drawLine(x + 8, yPos + edgeHeight/2, x + width - 8, yPos + edgeHeight/2);
                    g2d.setColor(isTop ? highlightColor : shadowColor);
                    g2d.drawLine(x + 8, yPos + edgeHeight/2 + 1, x + width - 8, yPos + edgeHeight/2 + 1);
                    break;

                case "Riblet":
                    // Ribbed pattern
                    g2d.setColor(isTop ? highlightColor : shadowColor);
                    int ribSpacing = Math.max(3, edgeHeight);
                    for (int i = x + 6; i < x + width - 6; i += ribSpacing) {
                        g2d.drawLine(i, yPos, i, yPos + edgeHeight);
                    }
                    break;

                case "Hard Edge":
                    // Sharp hard line
                    g2d.setColor(isTop ? highlightColor : shadowColor);
                    g2d.setStroke(new BasicStroke(Math.max(2, edgeHeight / 2)));
                    g2d.drawLine(x + 4, yPos + edgeHeight/2, x + width - 4, yPos + edgeHeight/2);
                    break;

                case "Art Deco":
                    // Decorative stepped pattern
                    g2d.setColor(isTop ? highlightColor : shadowColor);
                    int stepWidth = Math.max(8, width / 10);
                    int steps = width / stepWidth;
                    for (int i = 0; i < steps; i++) {
                        int stepX = x + 4 + i * stepWidth;
                        int stepH = (i % 2 == 0) ? edgeHeight : edgeHeight / 2;
                        g2d.fillRect(stepX, isTop ? yPos : yPos + edgeHeight - stepH, stepWidth - 2, stepH);
                    }
                    break;
            }
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
                case "rectangle":
                    if (fill) g2d.fillRect(cx - halfW, cy - halfH, halfW * 2, halfH * 2);
                    else g2d.drawRect(cx - halfW, cy - halfH, halfW * 2, halfH * 2);
                    break;
                case "oval":
                    if (fill) g2d.fillOval(cx - halfW, cy - halfH, halfW * 2, halfH * 2);
                    else g2d.drawOval(cx - halfW, cy - halfH, halfW * 2, halfH * 2);
                    break;
                case "arrow_right":
                    xPoints = new int[]{cx - halfW, cx + halfW, cx - halfW};
                    yPoints = new int[]{cy - halfH, cy, cy + halfH};
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                    else g2d.drawPolygon(xPoints, yPoints, 3);
                    break;
                case "arrow_left":
                    xPoints = new int[]{cx + halfW, cx - halfW, cx + halfW};
                    yPoints = new int[]{cy - halfH, cy, cy + halfH};
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                    else g2d.drawPolygon(xPoints, yPoints, 3);
                    break;
                case "arrow_up":
                    xPoints = new int[]{cx - halfW, cx, cx + halfW};
                    yPoints = new int[]{cy + halfH, cy - halfH, cy + halfH};
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                    else g2d.drawPolygon(xPoints, yPoints, 3);
                    break;
                case "arrow_down":
                    xPoints = new int[]{cx - halfW, cx, cx + halfW};
                    yPoints = new int[]{cy - halfH, cy + halfH, cy - halfH};
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                    else g2d.drawPolygon(xPoints, yPoints, 3);
                    break;
                case "pentagon":
                    xPoints = new int[5];
                    yPoints = new int[5];
                    for (int i = 0; i < 5; i++) {
                        xPoints[i] = cx + (int)(halfW * Math.cos(-Math.PI/2 + i * 2 * Math.PI / 5));
                        yPoints[i] = cy + (int)(halfH * Math.sin(-Math.PI/2 + i * 2 * Math.PI / 5));
                    }
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 5);
                    else g2d.drawPolygon(xPoints, yPoints, 5);
                    break;
                case "cross":
                    int crossW = halfW / 3;
                    int crossH = halfH / 3;
                    xPoints = new int[]{cx - crossW, cx + crossW, cx + crossW, cx + halfW, cx + halfW, cx + crossW, cx + crossW, cx - crossW, cx - crossW, cx - halfW, cx - halfW, cx - crossW};
                    yPoints = new int[]{cy - halfH, cy - halfH, cy - crossH, cy - crossH, cy + crossH, cy + crossH, cy + halfH, cy + halfH, cy + crossH, cy + crossH, cy - crossH, cy - crossH};
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 12);
                    else g2d.drawPolygon(xPoints, yPoints, 12);
                    break;
                case "heart":
                    java.awt.geom.Path2D.Double heartShape = new java.awt.geom.Path2D.Double();
                    heartShape.moveTo(cx, cy + halfH);
                    heartShape.curveTo(cx - halfW * 2, cy - halfH/2, cx - halfW, cy - halfH, cx, cy - halfH/3);
                    heartShape.curveTo(cx + halfW, cy - halfH, cx + halfW * 2, cy - halfH/2, cx, cy + halfH);
                    if (fill) g2d.fill(heartShape);
                    else g2d.draw(heartShape);
                    break;
                case "crescent":
                    java.awt.geom.Area outerMoon = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(cx - halfW, cy - halfH, halfW * 2, halfH * 2));
                    java.awt.geom.Area innerMoon = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(cx - halfW/2, cy - halfH, halfW * 2, halfH * 2));
                    outerMoon.subtract(innerMoon);
                    if (fill) g2d.fill(outerMoon);
                    else g2d.draw(outerMoon);
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
            // Set font with user-defined style
            int fontStyle = Font.PLAIN;
            if (axisDateBold && axisDateItalic) fontStyle = Font.BOLD | Font.ITALIC;
            else if (axisDateBold) fontStyle = Font.BOLD;
            else if (axisDateItalic) fontStyle = Font.ITALIC;
            g2d.setFont(new Font(axisDateFontFamily, fontStyle, axisDateFontSize));

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

                // Use tick color and width settings for tick marks (both Line and Bar styles)
                g2d.setColor(timelineAxisTickColor);
                g2d.setStroke(new BasicStroke(timelineAxisTickWidth));
                g2d.drawLine(x, timelineY, x, timelineY + timelineAxisTickHeight);

                g2d.setColor(axisDateColor);
                String dateStr = tick.format(tickFormat);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(dateStr, x - fm.stringWidth(dateStr) / 2, timelineY + timelineAxisTickHeight + 15);

                // Advance to next month or year
                if (showYears) {
                    tick = tick.plusYears(1);
                } else {
                    tick = tick.plusMonths(1);
                }
            }
        }

        private BasicStroke getExtendTicksStroke() {
            float thickness = extendTicksThickness;
            switch (extendTicksLineType) {
                case "Dashed":
                    return new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f, 5.0f}, 0.0f);
                case "Dotted":
                    return new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{2.0f, 5.0f}, 0.0f);
                case "Dash-Dot":
                    return new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f, 5.0f, 2.0f, 5.0f}, 0.0f);
                case "Solid":
                default:
                    return new BasicStroke(thickness);
            }
        }

        // Helper method to draw wrapped text within a given width
        private void drawWrappedText(Graphics2D g2d, String text, int x, int y, int maxWidth, int xOffset, int yOffset, boolean centerHorizontally) {
            if (text == null || text.isEmpty()) return;
            FontMetrics fm = g2d.getFontMetrics();
            int lineHeight = fm.getHeight();

            // Split text into words
            String[] words = text.split(" ");
            java.util.List<String> lines = new java.util.ArrayList<>();
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                if (currentLine.length() == 0) {
                    currentLine.append(word);
                } else {
                    String testLine = currentLine + " " + word;
                    if (fm.stringWidth(testLine) <= maxWidth) {
                        currentLine.append(" ").append(word);
                    } else {
                        lines.add(currentLine.toString());
                        currentLine = new StringBuilder(word);
                    }
                }
            }
            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }

            // Draw each line
            int totalHeight = lines.size() * lineHeight;
            int startY = y - totalHeight / 2 + fm.getAscent() + yOffset;

            for (String line : lines) {
                int lineWidth = fm.stringWidth(line);
                int lineX = centerHorizontally ? x + (maxWidth - lineWidth) / 2 + xOffset : x + xOffset;
                g2d.drawString(line, lineX, startY);
                startY += lineHeight;
            }
        }

        private void drawExtendedTicks(Graphics2D g2d, int timelineX, int timelineWidth, int timelineY, long totalDays) {
            if (!extendTicks) return;

            long days = ChronoUnit.DAYS.between(startDate, endDate);
            boolean showYears = days > 365 * 2;

            LocalDate tick;
            if (showYears) {
                if (startDate.getDayOfYear() == 1) {
                    tick = startDate;
                } else {
                    tick = startDate.plusYears(1).withDayOfYear(1);
                }
            } else {
                if (startDate.getDayOfMonth() == 1) {
                    tick = startDate;
                } else {
                    tick = startDate.plusMonths(1).withDayOfMonth(1);
                }
            }

            g2d.setColor(extendTicksColor);
            g2d.setStroke(getExtendTicksStroke());

            while (!tick.isAfter(endDate)) {
                int x = getXForDate(tick, timelineX, timelineWidth, totalDays);
                g2d.drawLine(x, 0, x, timelineY);

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
                        g2d.setColor(timelineAxisColor);
                        g2d.setStroke(new BasicStroke(Math.max(1, timelineAxisThickness - 1)));
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

    // Custom TabbedPaneUI to support gradient tab backgrounds
    class GradientTabbedPaneUI extends javax.swing.plaf.basic.BasicTabbedPaneUI {
        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color color1, color2;
            boolean useGradient;
            double angle;
            ArrayList<float[]> stops;

            if (isSelected) {
                color1 = formatSelectedTabColor;
                color2 = formatSelectedTabColor2;
                useGradient = formatSelectedTabUseGradient;
                angle = formatSelectedTabGradientAngle;
                stops = formatSelectedTabGradientStops;
            } else {
                color1 = formatTabColor;
                color2 = formatTabColor2;
                useGradient = formatTabUseGradient;
                angle = formatTabGradientAngle;
                stops = formatTabGradientStops;
            }

            if (useGradient && stops.size() >= 2) {
                float[] fractions = new float[stops.size()];
                Color[] colors = new Color[stops.size()];
                for (int i = 0; i < stops.size(); i++) {
                    float[] stop = stops.get(i);
                    fractions[i] = stop[0];
                    colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                }
                java.awt.LinearGradientPaint lgp = createAngledGradient(w, h, angle, fractions, colors);
                // Translate to tab position
                g2d.translate(x, y);
                g2d.setPaint(lgp);
                g2d.fillRect(0, 0, w, h);
                g2d.translate(-x, -y);
            } else {
                g2d.setColor(color1);
                g2d.fillRect(x, y, w, h);
            }
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            // Paint content border with tab content color
            int width = tabPane.getWidth();
            int height = tabPane.getHeight();
            Insets insets = tabPane.getInsets();
            int x = insets.left;
            int y = insets.top;
            int w = width - insets.right - insets.left;
            int h = height - insets.top - insets.bottom;

            switch (tabPlacement) {
                case TOP:
                    y += calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                    h -= y - insets.top;
                    break;
                case BOTTOM:
                    h -= calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                    break;
                case LEFT:
                    x += calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                    w -= x - insets.left;
                    break;
                case RIGHT:
                    w -= calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                    break;
            }

            Graphics2D g2d = (Graphics2D) g;
            if (formatTabContentUseGradient && formatTabContentGradientStops.size() >= 2) {
                float[] fractions = new float[formatTabContentGradientStops.size()];
                Color[] colors = new Color[formatTabContentGradientStops.size()];
                for (int i = 0; i < formatTabContentGradientStops.size(); i++) {
                    float[] stop = formatTabContentGradientStops.get(i);
                    fractions[i] = stop[0];
                    colors[i] = new Color(stop[1], stop[2], stop[3], stop[4]);
                }
                java.awt.LinearGradientPaint lgp = createAngledGradient(w, h, formatTabContentGradientAngle, fractions, colors);
                g2d.translate(x, y);
                g2d.setPaint(lgp);
                g2d.fillRect(0, 0, w, h);
                g2d.translate(-x, -y);
            } else {
                g2d.setColor(formatTabContentColor);
                g2d.fillRect(x, y, w, h);
            }

            // Draw border
            g2d.setColor(Color.GRAY);
            g2d.drawRect(x, y, w - 1, h - 1);
        }
    }
}
