with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Change left divider from 12 to 8
content = content.replace(
    'centerSplitPane.setDividerSize(12);',
    'centerSplitPane.setDividerSize(8);'
)

# Change right divider from 12 to 8
content = content.replace(
    'rightDividerPanel.setPreferredSize(new Dimension(12, 0));',
    'rightDividerPanel.setPreferredSize(new Dimension(8, 0));'
)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - dividers are now 8 pixels wide")
