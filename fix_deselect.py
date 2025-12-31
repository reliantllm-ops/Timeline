with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Fix deselection when clicking on blank area
old = '''            // No task or milestone was clicked - start selection box
            if (!ctrlDown) {
                // Clear selection when starting a new selection box (unless Ctrl is held)
                selectedTaskIndices.clear();
                selectedMilestoneIndices.clear();
                selectTask(-1);
            }'''

new = '''            // No task or milestone was clicked - start selection box
            if (!ctrlDown) {
                // Clear selection when starting a new selection box (unless Ctrl is held)
                selectedTaskIndices.clear();
                selectedMilestoneIndices.clear();
                selectedMilestoneIndex = -1;
                selectTask(-1);
                repaint();
            }'''

content = content.replace(old, new)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - clicking blank area now deselects milestones/shapes too")
