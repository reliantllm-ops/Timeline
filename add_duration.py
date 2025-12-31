with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add Duration to dropdown options
content = content.replace(
    'String[] opts = {"(none)", "Name", "Start Date", "End Date", "Center Text"};',
    'String[] opts = {"(none)", "Name", "Start Date", "End Date", "Duration", "Center Text"};'
)

# Add Duration handling in the Apply button action
content = content.replace(
    '''                            } else if ("Center Text".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).centerText;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).centerText;
                            }''',
    '''                            } else if ("Duration".equals(sel)) {
                                if (item instanceof TimelineTask) {
                                    try {
                                        LocalDate start = LocalDate.parse(((TimelineTask) item).startDate, DATE_FORMAT);
                                        LocalDate end = LocalDate.parse(((TimelineTask) item).endDate, DATE_FORMAT);
                                        val = String.valueOf(java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1);
                                    } catch (Exception ex) { val = ""; }
                                } else { val = "1"; }
                            } else if ("Center Text".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).centerText;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).centerText;
                            }'''
)

# Add Duration handling in updateSpreadsheet
content = content.replace(
    '''                } else if ("Center Text".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).centerText;
                    else if (item instanceof TimelineMilestone) rowData[col] = ((TimelineMilestone) item).centerText;
                    else rowData[col] = "";
                } else {''',
    '''                } else if ("Duration".equals(header)) {
                    if (item instanceof TimelineTask) {
                        try {
                            LocalDate start = LocalDate.parse(((TimelineTask) item).startDate, DATE_FORMAT);
                            LocalDate end = LocalDate.parse(((TimelineTask) item).endDate, DATE_FORMAT);
                            rowData[col] = String.valueOf(java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1);
                        } catch (Exception ex) { rowData[col] = ""; }
                    } else { rowData[col] = "1"; }
                } else if ("Center Text".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).centerText;
                    else if (item instanceof TimelineMilestone) rowData[col] = ((TimelineMilestone) item).centerText;
                    else rowData[col] = "";
                } else {'''
)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - added Duration column option")
