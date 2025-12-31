with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Fix mousePressed to skip selection handling on right-click
old = '''                public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();
                    handleMousePressed(e.getX(), e.getY(), e.isControlDown());
                }'''

new = '''                public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();
                    // Skip selection handling on right-click to preserve multi-selection
                    if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                        return;
                    }
                    handleMousePressed(e.getX(), e.getY(), e.isControlDown());
                }'''

content = content.replace(old, new)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - right-click no longer deselects items")
