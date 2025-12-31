with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

old = '''            spreadsheetRowOrder.add(item);
            String startDate = "";
            String endDate = "";
            if (item instanceof TimelineTask) {
                startDate = ((TimelineTask) item).startDate;
                endDate = ((TimelineTask) item).endDate;
            } else if (item instanceof TimelineMilestone) {
                startDate = ((TimelineMilestone) item).date;
            }
            String[] savedData = spreadsheetData.get(item);
            if (savedData != null) {
                spreadsheetTableModel.addRow(new Object[]{name, startDate, endDate, savedData[2], savedData[3]});
            } else {
                spreadsheetTableModel.addRow(new Object[]{name, startDate, endDate, "", ""});
            }'''

new = '''            spreadsheetRowOrder.add(item);
            // Build row data based on column headers
            Object[] rowData = new Object[spreadsheetTable.getColumnCount()];
            String[] savedData = spreadsheetData.get(item);
            for (int col = 0; col < spreadsheetTable.getColumnCount(); col++) {
                String header = spreadsheetTable.getColumnModel().getColumn(col).getHeaderValue().toString();
                if ("Name".equals(header)) {
                    rowData[col] = name;
                } else if ("Start Date".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).startDate;
                    else if (item instanceof TimelineMilestone) rowData[col] = ((TimelineMilestone) item).date;
                    else rowData[col] = "";
                } else if ("End Date".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).endDate;
                    else rowData[col] = "";
                } else if ("Center Text".equals(header)) {
                    if (item instanceof TimelineTask) rowData[col] = ((TimelineTask) item).centerText;
                    else if (item instanceof TimelineMilestone) rowData[col] = ((TimelineMilestone) item).centerText;
                    else rowData[col] = "";
                } else {
                    // Use saved data for other columns
                    if (savedData != null && col > 0 && col <= savedData.length) {
                        rowData[col] = savedData[col - 1];
                    } else {
                        rowData[col] = "";
                    }
                }
            }
            spreadsheetTableModel.addRow(rowData);'''

content = content.replace(old, new)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - fixed live update for all column types")
