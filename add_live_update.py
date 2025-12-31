with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add updateSpreadsheet() after refreshTimeline() in updateCenterText
content = content.replace(
    '            milestones.get(selectedMilestoneIndex).centerText = text;\n        }\n        refreshTimeline();\n    }\n\n    private void updateFontFamily()',
    '            milestones.get(selectedMilestoneIndex).centerText = text;\n        }\n        refreshTimeline();\n        updateSpreadsheet();\n    }\n\n    private void updateFontFamily()'
)

# Add updateSpreadsheet() after refreshTimeline() in updateSelectedTaskName
content = content.replace(
    '                formatTitleLabel.setText("Selected: " + newName);\n                refreshTimeline();\n            }\n        }\n    }\n\n    private void updateSelectedTaskDates()',
    '                formatTitleLabel.setText("Selected: " + newName);\n                refreshTimeline();\n                updateSpreadsheet();\n            }\n        }\n    }\n\n    private void updateSelectedTaskDates()'
)

# Add updateSpreadsheet() after refreshTimeline() in updateSelectedTaskDates
content = content.replace(
    '                task.startDate = newStart;\n                task.endDate = newEnd;\n            }\n            refreshTimeline();\n        } catch (Exception ex) {',
    '                task.startDate = newStart;\n                task.endDate = newEnd;\n            }\n            refreshTimeline();\n            updateSpreadsheet();\n        } catch (Exception ex) {'
)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - added live spreadsheet updates")
