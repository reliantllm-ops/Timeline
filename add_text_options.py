with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add text options to dropdown
content = content.replace(
    'String[] opts = {"(none)", "Name", "Start Date", "End Date", "Duration", "Center Text"};',
    'String[] opts = {"(none)", "Name", "Start Date", "End Date", "Duration", "Center Text", "Above Text", "Underneath Text", "Front Text", "Behind Text"};'
)

# Add handling in the Apply button action for the new text options
content = content.replace(
    '''                            } else if ("Center Text".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).centerText;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).centerText;
                            }
                            spreadsheetTableModel.setValueAt(val, r, c);''',
    '''                            } else if ("Center Text".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).centerText;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).centerText;
                            } else if ("Above Text".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).aboveText;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).aboveText;
                            } else if ("Underneath Text".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).underneathText;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).underneathText;
                            } else if ("Front Text".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).frontText;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).frontText;
                            } else if ("Behind Text".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).behindText;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).behindText;
                            }
                            spreadsheetTableModel.setValueAt(val, r, c);'''
)

# Add handling in updateSpreadsheet for the new text options
content = content.replace(
    '''                } else if ("Center Text".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).centerText;
                    else if (item instanceof TimelineMilestone) rowData[col] = ((TimelineMilestone) item).centerText;
                    else rowData[col] = "";
                } else {''',
    '''                } else if ("Center Text".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).centerText;
                    else if (item instanceof TimelineMilestone) rowData[col] = ((TimelineMilestone) item).centerText;
                    else rowData[col] = "";
                } else if ("Above Text".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).aboveText;
                    else if (item instanceof TimelineMilestone) rowData[col] = ((TimelineMilestone) item).aboveText;
                    else rowData[col] = "";
                } else if ("Underneath Text".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).underneathText;
                    else if (item instanceof TimelineMilestone) rowData[col] = ((TimelineMilestone) item).underneathText;
                    else rowData[col] = "";
                } else if ("Front Text".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).frontText;
                    else if (item instanceof TimelineMilestone) rowData[col] = ((TimelineMilestone) item).frontText;
                    else rowData[col] = "";
                } else if ("Behind Text".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).behindText;
                    else if (item instanceof TimelineMilestone) rowData[col] = ((TimelineMilestone) item).behindText;
                    else rowData[col] = "";
                } else {'''
)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - added Above Text, Underneath Text, Front Text, Behind Text options")
