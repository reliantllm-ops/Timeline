with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add hover effect to the left (spreadsheet) split pane divider
old = '''        // Custom UI for the divider with 3 dots and click-to-collapse
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
        });'''

new = '''        // Custom UI for the divider with 3 dots and click-to-collapse
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
        });'''

content = content.replace(old, new)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - added hover highlight to left panel divider")
