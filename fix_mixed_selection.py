with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Modify selectTask to not clear milestone selection when ctrlDown is true
old1 = '''    void selectTask(int index, boolean ctrlDown) {
        // Deselect milestone when task is selected
        if (index >= 0) {
            selectedMilestoneIndex = -1;
            selectedMilestoneIndices.clear();
            row1CardLayout.show(row1Container, "task");
        }

        // Handle multi-select with Ctrl key
        if (ctrlDown && index >= 0) {
            // Toggle selection
            if (selectedTaskIndices.contains(index)) {
                selectedTaskIndices.remove(index);
            } else {
                selectedTaskIndices.add(index);
            }
        } else {
            // Single select - clear previous and select new
            selectedTaskIndices.clear();
            if (index >= 0) {
                selectedTaskIndices.add(index);
            }
        }'''

new1 = '''    void selectTask(int index, boolean ctrlDown) {
        // Only deselect milestones when NOT using Ctrl (allows mixed selection)
        if (index >= 0 && !ctrlDown) {
            selectedMilestoneIndex = -1;
            selectedMilestoneIndices.clear();
        }
        if (index >= 0) {
            row1CardLayout.show(row1Container, "task");
        }

        // Handle multi-select with Ctrl key
        if (ctrlDown && index >= 0) {
            // Toggle selection
            if (selectedTaskIndices.contains(index)) {
                selectedTaskIndices.remove(index);
            } else {
                selectedTaskIndices.add(index);
            }
        } else {
            // Single select - clear previous and select new
            selectedTaskIndices.clear();
            if (index >= 0) {
                selectedTaskIndices.add(index);
            }
        }'''

content = content.replace(old1, new1)

# 2. Replace selectMilestone to add ctrlDown parameter version
old2 = '''    void selectMilestone(int index) {
        selectedMilestoneIndex = index;
        // Deselect tasks when milestone is selected and switch to milestone row view
        if (index >= 0) {
            selectedTaskIndices.clear();
            row1CardLayout.show(row1Container, "milestone");
        }'''

new2 = '''    void selectMilestone(int index, boolean ctrlDown) {
        // Handle multi-select with Ctrl key
        if (ctrlDown && index >= 0) {
            // Toggle selection in multi-select set
            if (selectedMilestoneIndices.contains(index)) {
                selectedMilestoneIndices.remove(index);
                if (selectedMilestoneIndex == index) {
                    selectedMilestoneIndex = selectedMilestoneIndices.isEmpty() ? -1 : selectedMilestoneIndices.iterator().next();
                }
            } else {
                selectedMilestoneIndices.add(index);
                selectedMilestoneIndex = index;
            }
            // Don't clear task selection when Ctrl is held
        } else {
            // Single select
            selectedMilestoneIndex = index;
            selectedMilestoneIndices.clear();
            if (index >= 0) {
                selectedMilestoneIndices.add(index);
                // Only deselect tasks when NOT using Ctrl
                selectedTaskIndices.clear();
            }
        }
        if (index >= 0) {
            row1CardLayout.show(row1Container, "milestone");
        }'''

content = content.replace(old2, new2)

# 3. Add overload for selectMilestone without ctrlDown - find the end of the method
old3 = '''        if (layersPanel != null) {
            // Find the correct index in layerOrder for this milestone
            if (index >= 0 && index < milestones.size()) {
                TimelineMilestone milestone = milestones.get(index);
                int layerIndex = layerOrder.indexOf(milestone);
                layersPanel.setSelectedLayer(layerIndex);
            } else {
                layersPanel.setSelectedLayer(-1);
            }
        }
    }

    private void duplicateSelectedTasks()'''

new3 = '''        if (layersPanel != null) {
            // Find the correct index in layerOrder for this milestone
            if (index >= 0 && index < milestones.size()) {
                TimelineMilestone milestone = milestones.get(index);
                int layerIndex = layerOrder.indexOf(milestone);
                layersPanel.setSelectedLayer(layerIndex);
            } else {
                layersPanel.setSelectedLayer(-1);
            }
        }
    }

    // Overload for calls without ctrlDown parameter
    void selectMilestone(int index) {
        selectMilestone(index, false);
    }

    private void duplicateSelectedTasks()'''

content = content.replace(old3, new3)

# 4. Update the call in handleMousePressed to pass ctrlDown
content = content.replace(
    'selectMilestone(milestoneIdx);',
    'selectMilestone(milestoneIdx, ctrlDown);'
)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - enabled mixed selection of tasks, milestones and shapes")
