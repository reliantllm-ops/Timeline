with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add a spacer row above Interior in General tab
old = '''        // Interior row
        ggbc.gridx = 0; ggbc.gridy = 0;
        generalSkinsTab.add(new JLabel("Interior:"), ggbc);'''

new = '''        // Spacer row
        ggbc.gridx = 0; ggbc.gridy = 0;
        ggbc.gridwidth = 3;
        generalSkinsTab.add(new JPanel(), ggbc);
        ggbc.gridwidth = 1;

        // Interior row
        ggbc.gridx = 0; ggbc.gridy = 1;
        generalSkinsTab.add(new JLabel("Interior:"), ggbc);'''

content = content.replace(old, new)

# Update filler row gridy
content = content.replace(
    '''        // Filler
        ggbc.gridx = 0; ggbc.gridy = 1;''',
    '''        // Filler
        ggbc.gridx = 0; ggbc.gridy = 2;'''
)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - added spacer row above Interior in General tab")
