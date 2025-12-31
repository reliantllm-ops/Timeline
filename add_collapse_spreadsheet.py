with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add a variable to track last divider location
old1 = '''    private JSplitPane centerSplitPane;
    private boolean spreadsheetVisible = false;'''

new1 = '''    private JSplitPane centerSplitPane;
    private boolean spreadsheetVisible = false;
    private int lastSpreadsheetDividerLocation = 250;'''

content = content.replace(old1, new1)

# Modify the split pane setup to add click-to-collapse on the divider
old2 = '''        // Create split pane with spreadsheet on left, timeline on right
        centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spreadsheetPanel, scrollPane);
        centerSplitPane.setDividerLocation(0);
        centerSplitPane.setDividerSize(5);
        centerSplitPane.setContinuousLayout(true);
        centerPanel.add(centerSplitPane, BorderLayout.CENTER);'''

new2 = '''        // Create split pane with spreadsheet on left, timeline on right
        centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spreadsheetPanel, scrollPane);
        centerSplitPane.setDividerLocation(0);
        centerSplitPane.setDividerSize(12);
        centerSplitPane.setContinuousLayout(true);

        // Custom UI for the divider with 3 dots and click-to-collapse
        centerSplitPane.setUI(new javax.swing.plaf.basic.BasicSplitPaneUI() {
            @Override
            public javax.swing.plaf.basic.BasicSplitPaneDivider createDefaultDivider() {
                return new javax.swing.plaf.basic.BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        super.paint(g);
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        int w = getWidth();
                        int h = getHeight();
                        int dotSize = 4;
                        int spacing = 6;
                        int totalHeight = dotSize * 3 + spacing * 2;
                        int startY = (h - totalHeight) / 2;

                        g2d.setColor(new Color(120, 120, 120));
                        for (int i = 0; i < 3; i++) {
                            int y = startY + i * (dotSize + spacing);
                            g2d.fillOval((w - dotSize) / 2, y, dotSize, dotSize);
                        }
                    }

                    {
                        // Add mouse listener for click-to-collapse
                        addMouseListener(new java.awt.event.MouseAdapter() {
                            private long lastClickTime = 0;

                            @Override
                            public void mouseClicked(java.awt.event.MouseEvent e) {
                                long now = System.currentTimeMillis();
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
                                lastClickTime = now;
                            }
                        });
                    }
                };
            }
        });

        centerPanel.add(centerSplitPane, BorderLayout.CENTER);'''

content = content.replace(old2, new2)

# Update toggleSpreadsheetPanel to use the saved location
old3 = '''    private void toggleSpreadsheetPanel() {
        if (spreadsheetVisible) {
            updateSpreadsheet();
            centerSplitPane.setDividerLocation(250);
        } else {
            centerSplitPane.setDividerLocation(0);
        }
    }'''

new3 = '''    private void toggleSpreadsheetPanel() {
        if (spreadsheetVisible) {
            updateSpreadsheet();
            centerSplitPane.setDividerLocation(lastSpreadsheetDividerLocation > 50 ? lastSpreadsheetDividerLocation : 250);
        } else {
            if (centerSplitPane.getDividerLocation() > 50) {
                lastSpreadsheetDividerLocation = centerSplitPane.getDividerLocation();
            }
            centerSplitPane.setDividerLocation(0);
        }
    }'''

content = content.replace(old3, new3)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - spreadsheet pane is now collapsible by clicking the 3 dots")
