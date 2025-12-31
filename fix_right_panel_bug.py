with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Fix the right panel click handler to not affect the left panel
# The issue is that revalidate() might be resetting the split pane
old = '''            @Override
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
            }'''

new = '''            @Override
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
            }'''

content = content.replace(old, new)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - fixed right panel not affecting left panel")
