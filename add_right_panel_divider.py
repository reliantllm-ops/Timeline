with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add variable to track last right panel width
old1 = '''    private int lastSpreadsheetDividerLocation = 250;'''

new1 = '''    private int lastSpreadsheetDividerLocation = 250;
    private int lastRightPanelWidth = 290;
    private boolean rightPanelCollapsed = false;'''

content = content.replace(old1, new1)

# Replace the right panel setup with a split pane approach
old2 = '''        rightPanel = new CollapsiblePanel("Right Panel", rightTabbedWrapper, false);
        rightPanel.setHeaderVisible(false);

        // Create collapse button with beveled left edge, no right border
        JButton rightPanelCollapseBtn = new JButton("\\u25B6") {
            private Color bgColor = new Color(220, 220, 220);

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arc = 10;

                // Create shape with rounded left edge only
                java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
                path.moveTo(arc, 0);
                path.lineTo(w, 0);
                path.lineTo(w, h);
                path.lineTo(arc, h);
                path.quadTo(0, h, 0, h - arc);
                path.lineTo(0, arc);
                path.quadTo(0, 0, arc, 0);
                path.closePath();

                // Fill background
                g2d.setColor(bgColor);
                g2d.fill(path);

                // Draw border on left, top, bottom
                g2d.setColor(new Color(150, 150, 150));
                g2d.drawLine(arc, 0, w - 1, 0);  // top
                g2d.drawLine(arc, h - 1, w - 1, h - 1);  // bottom
                // Left curved edge
                g2d.draw(new java.awt.geom.QuadCurve2D.Float(arc, 0, 0, 0, 0, arc));
                g2d.drawLine(0, arc, 0, h - arc);
                g2d.draw(new java.awt.geom.QuadCurve2D.Float(0, h - arc, 0, h, arc, h - 1));
                // Right border - same color as right panel border
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawLine(w - 1, 0, w - 1, h - 1);

                // Draw arrow
                g2d.setColor(getForeground());
                FontMetrics fm = g2d.getFontMetrics();
                String text = getText();
                int textX = (w - fm.stringWidth(text)) / 2;
                int textY = (h + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(text, textX, textY);

                g2d.dispose();
            }

            @Override
            public void setBackground(Color c) {
                super.setBackground(c);
                this.bgColor = c;
                repaint();
            }
        };
        rightPanelCollapseBtn.setPreferredSize(new Dimension(20, 60));
        rightPanelCollapseBtn.setBounds(270, 5, 20, 60);
        rightPanelCollapseBtn.setBackground(new Color(220, 220, 220));
        rightPanelCollapseBtn.setForeground(Color.DARK_GRAY);
        rightPanelCollapseBtn.setBorderPainted(false);
        rightPanelCollapseBtn.setContentAreaFilled(false);
        rightPanelCollapseBtn.setFocusPainted(false);
        rightPanelCollapseBtn.setToolTipText("Collapse Panel");
        rightPanelCollapseBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                rightPanelCollapseBtn.setBackground(new Color(180, 180, 180));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                rightPanelCollapseBtn.setBackground(new Color(220, 220, 220));
            }
        });

        // Wrapper panel to hold rightPanel and manage sizing
        JPanel rightPanelWrapper = new JPanel(new BorderLayout());
        rightPanelWrapper.setPreferredSize(new Dimension(290, 600));
        rightPanelWrapper.add(rightPanel, BorderLayout.CENTER);

        // Use JLayeredPane to overlay the button on top of the right panel
        JLayeredPane rightLayeredPane = new JLayeredPane() {
            @Override
            public Dimension getPreferredSize() {
                return rightPanelWrapper.getPreferredSize();
            }
        };
        rightLayeredPane.setLayout(null);  // Use absolute positioning

        // Add wrapper to the default layer
        rightPanelWrapper.setBounds(0, 0, 290, 600);
        rightLayeredPane.add(rightPanelWrapper, JLayeredPane.DEFAULT_LAYER);

        // Add button to a higher layer so it appears on top
        rightLayeredPane.add(rightPanelCollapseBtn, JLayeredPane.PALETTE_LAYER);

        rightPanelCollapseBtn.addActionListener(e -> {
            rightPanel.toggleCollapse();
            // Update button arrow direction and wrapper size based on collapsed state
            if (rightPanel.isCollapsed()) {
                rightPanelCollapseBtn.setText("\\u25C0");  // Left arrow when collapsed (to expand)
                rightPanelCollapseBtn.setToolTipText("Expand Panel");
                rightPanelWrapper.setPreferredSize(new Dimension(30, 600));
                rightPanelWrapper.setBounds(0, 0, 30, rightLayeredPane.getHeight());
                rightPanelCollapseBtn.setBounds(5, 5, 20, 60);
            } else {
                rightPanelCollapseBtn.setText("\\u25B6");  // Right arrow when expanded (to collapse)
                rightPanelCollapseBtn.setToolTipText("Collapse Panel");
                rightPanelWrapper.setPreferredSize(new Dimension(290, 600));
                rightPanelWrapper.setBounds(0, 0, 290, rightLayeredPane.getHeight());
                rightPanelCollapseBtn.setBounds(269, 5, 20, 60);
            }
            rightPanelWrapper.revalidate();
            rightLayeredPane.revalidate();
            Timeline2.this.revalidate();
            Timeline2.this.repaint();
        });

        // Listen for resize to update bounds
        rightLayeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                rightPanelWrapper.setBounds(0, 0, rightLayeredPane.getWidth(), rightLayeredPane.getHeight());
                // Keep button at top right inside the panel
                if (!rightPanel.isCollapsed()) {
                    rightPanelCollapseBtn.setBounds(rightLayeredPane.getWidth() - 21, 5, 20, 60);
                } else {
                    rightPanelCollapseBtn.setBounds(5, 5, 20, 60);
                }
            }
        });

        add(rightLayeredPane, BorderLayout.EAST);'''

new2 = '''        rightPanel = new CollapsiblePanel("Right Panel", rightTabbedWrapper, false);
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
        };
        rightDividerPanel.setPreferredSize(new Dimension(12, 0));
        rightDividerPanel.setBackground(new Color(235, 235, 235));
        rightDividerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightDividerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
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

        add(rightPanelWrapper, BorderLayout.EAST);'''

content = content.replace(old2, new2)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - added 3-dot collapsible divider to right panel")
