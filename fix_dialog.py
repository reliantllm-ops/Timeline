with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add more top padding to the columns panel
old = 'colsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));'
new = 'colsPanel.setBorder(BorderFactory.createEmptyBorder(30, 15, 15, 15));'
content = content.replace(old, new)

# Make dialog taller
old = 'sp.setPreferredSize(new Dimension(280, 300));'
new = 'sp.setPreferredSize(new Dimension(280, 350));'
content = content.replace(old, new)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done")
