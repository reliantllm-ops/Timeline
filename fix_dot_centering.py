with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Fix left divider dots - make dots smaller (3px) for 8px width
old1 = '''                        // Draw 3 dots
                        int dotSize = 4;
                        int spacing = 6;
                        int totalHeight = dotSize * 3 + spacing * 2;
                        int startY = (h - totalHeight) / 2;

                        g2d.setColor(new Color(120, 120, 120));
                        for (int i = 0; i < 3; i++) {
                            int y = startY + i * (dotSize + spacing);
                            g2d.fillOval((w - dotSize) / 2, y, dotSize, dotSize);
                        }'''

new1 = '''                        // Draw 3 dots
                        int dotSize = 3;
                        int spacing = 5;
                        int totalHeight = dotSize * 3 + spacing * 2;
                        int startY = (h - totalHeight) / 2;

                        g2d.setColor(new Color(120, 120, 120));
                        for (int i = 0; i < 3; i++) {
                            int y = startY + i * (dotSize + spacing);
                            int x = (w - dotSize) / 2;
                            g2d.fillOval(x, y, dotSize, dotSize);
                        }'''

content = content.replace(old1, new1)

# Fix right divider dots
old2 = '''                int w = getWidth();
                int h = getHeight();
                int dotSize = 4;
                int spacing = 6;
                int totalHeight = dotSize * 3 + spacing * 2;
                int startY = (h - totalHeight) / 2;

                g2d.setColor(new Color(120, 120, 120));
                for (int i = 0; i < 3; i++) {
                    int y = startY + i * (dotSize + spacing);
                    g2d.fillOval((w - dotSize) / 2, y, dotSize, dotSize);
                }'''

new2 = '''                int w = getWidth();
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
                }'''

content = content.replace(old2, new2)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - dots are now centered in 8px dividers")
