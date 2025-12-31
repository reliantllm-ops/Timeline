with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

old = '''        spreadsheetToolbar.add(selectColumnsBtn);
        spreadsheetPanel.add(spreadsheetToolbar, BorderLayout.NORTH);'''

new = '''        spreadsheetToolbar.add(selectColumnsBtn);

        // Export button
        JButton exportBtn = new JButton("Export");
        exportBtn.setMargin(new Insets(2, 8, 2, 8));
        exportBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Export Spreadsheet");
            fc.setSelectedFile(new java.io.File("spreadsheet.csv"));
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
            if (fc.showSaveDialog(spreadsheetPanel) == JFileChooser.APPROVE_OPTION) {
                try (java.io.PrintWriter pw = new java.io.PrintWriter(fc.getSelectedFile())) {
                    // Write headers
                    StringBuilder header = new StringBuilder();
                    for (int c = 0; c < spreadsheetTable.getColumnCount(); c++) {
                        if (c > 0) header.append(",");
                        String h = spreadsheetTable.getColumnModel().getColumn(c).getHeaderValue().toString();
                        header.append("\\"").append(h.replace("\\"", "\\"\\"")).append("\\"");
                    }
                    pw.println(header);
                    // Write data
                    for (int r = 0; r < spreadsheetTableModel.getRowCount(); r++) {
                        StringBuilder row = new StringBuilder();
                        for (int c = 0; c < spreadsheetTableModel.getColumnCount(); c++) {
                            if (c > 0) row.append(",");
                            Object val = spreadsheetTableModel.getValueAt(r, c);
                            String s = val != null ? val.toString() : "";
                            row.append("\\"").append(s.replace("\\"", "\\"\\"")).append("\\"");
                        }
                        pw.println(row);
                    }
                    JOptionPane.showMessageDialog(spreadsheetPanel, "Exported successfully!", "Export", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(spreadsheetPanel, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        spreadsheetToolbar.add(exportBtn);

        // Import button
        JButton importBtn = new JButton("Import");
        importBtn.setMargin(new Insets(2, 8, 2, 8));
        importBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Import Spreadsheet");
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
            if (fc.showOpenDialog(spreadsheetPanel) == JFileChooser.APPROVE_OPTION) {
                try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fc.getSelectedFile()))) {
                    String line;
                    int rowIdx = 0;
                    boolean firstLine = true;
                    while ((line = br.readLine()) != null) {
                        java.util.List<String> values = new java.util.ArrayList<>();
                        StringBuilder current = new StringBuilder();
                        boolean inQuotes = false;
                        for (int i = 0; i < line.length(); i++) {
                            char ch = line.charAt(i);
                            if (ch == '"') {
                                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                                    current.append('"');
                                    i++;
                                } else {
                                    inQuotes = !inQuotes;
                                }
                            } else if (ch == ',' && !inQuotes) {
                                values.add(current.toString());
                                current = new StringBuilder();
                            } else {
                                current.append(ch);
                            }
                        }
                        values.add(current.toString());

                        if (firstLine) {
                            // Set column headers
                            for (int c = 0; c < Math.min(values.size(), spreadsheetTable.getColumnCount()); c++) {
                                spreadsheetTable.getColumnModel().getColumn(c).setHeaderValue(values.get(c));
                            }
                            spreadsheetTable.getTableHeader().repaint();
                            firstLine = false;
                        } else {
                            // Set row data
                            if (rowIdx < spreadsheetTableModel.getRowCount()) {
                                for (int c = 0; c < Math.min(values.size(), spreadsheetTableModel.getColumnCount()); c++) {
                                    spreadsheetTableModel.setValueAt(values.get(c), rowIdx, c);
                                }
                            }
                            rowIdx++;
                        }
                    }
                    spreadsheetTable.repaint();
                    JOptionPane.showMessageDialog(spreadsheetPanel, "Imported successfully!", "Import", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(spreadsheetPanel, "Import failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        spreadsheetToolbar.add(importBtn);

        spreadsheetPanel.add(spreadsheetToolbar, BorderLayout.NORTH);'''

content = content.replace(old, new)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - added Import and Export buttons")
