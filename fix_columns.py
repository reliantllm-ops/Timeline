import re

with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

old = '''        // Column selection dropdowns panel (initially hidden)
        JPanel columnSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        columnSelectPanel.setVisible(false);
        String[] columnOptions = {"", "Name", "Start Date", "End Date", "Center Text"};
        JComboBox<String>[] columnCombos = new JComboBox[spreadsheetTable.getColumnCount()];

        for (int col = 0; col < spreadsheetTable.getColumnCount(); col++) {
            final int colIndex = col;
            columnCombos[col] = new JComboBox<>(columnOptions);
            columnCombos[col].setPreferredSize(new Dimension(90, 22));
            columnCombos[col].addActionListener(ev -> {
                String selected = (String) columnCombos[colIndex].getSelectedItem();
                if (selected == null || selected.isEmpty()) return;

                // Update column header
                spreadsheetTable.getColumnModel().getColumn(colIndex).setHeaderValue(selected);
                spreadsheetTable.getTableHeader().repaint();

                // Fill column with corresponding data
                for (int row = 0; row < spreadsheetTableModel.getRowCount(); row++) {
                    if (row < spreadsheetRowOrder.size()) {
                        Object item = spreadsheetRowOrder.get(row);
                        String value = "";
                        if ("Name".equals(selected)) {
                            if (item instanceof TimelineTask) value = ((TimelineTask) item).name;
                            else if (item instanceof TimelineMilestone) value = ((TimelineMilestone) item).name;
                        } else if ("Start Date".equals(selected)) {
                            if (item instanceof TimelineTask) value = ((TimelineTask) item).startDate;
                            else if (item instanceof TimelineMilestone) value = ((TimelineMilestone) item).date;
                        } else if ("End Date".equals(selected)) {
                            if (item instanceof TimelineTask) value = ((TimelineTask) item).endDate;
                        } else if ("Center Text".equals(selected)) {
                            // Keep existing value, just center it
                            Object existing = spreadsheetTableModel.getValueAt(row, colIndex);
                            value = existing != null ? existing.toString() : "";
                        }
                        spreadsheetTableModel.setValueAt(value, row, colIndex);
                    }
                }

                // Center align if Center Text selected
                if ("Center Text".equals(selected)) {
                    javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
                    centerRenderer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                    spreadsheetTable.getColumnModel().getColumn(colIndex).setCellRenderer(centerRenderer);
                }
                spreadsheetTable.repaint();
            });
            columnSelectPanel.add(columnCombos[col]);
        }

        selectColumnsBtn.addActionListener(e -> {
            columnSelectPanel.setVisible(!columnSelectPanel.isVisible());
            spreadsheetPanel.revalidate();
        });
        spreadsheetToolbar.add(selectColumnsBtn);
        spreadsheetToolbar.add(columnSelectPanel);
        spreadsheetPanel.add(spreadsheetToolbar, BorderLayout.NORTH);'''

new = '''        selectColumnsBtn.addActionListener(e -> {
            JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(spreadsheetPanel), "Select Column Types", true);
            dlg.setLayout(new BorderLayout());
            JPanel colsPanel = new JPanel(new GridLayout(0, 2, 10, 5));
            colsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            String[] opts = {"(none)", "Name", "Start Date", "End Date", "Center Text"};
            int colCount = spreadsheetTable.getColumnCount();
            JComboBox[] cbs = new JComboBox[colCount];
            for (int c = 0; c < colCount; c++) {
                colsPanel.add(new JLabel("Column " + (c + 1) + ":"));
                cbs[c] = new JComboBox<>(opts);
                String hdr = spreadsheetTable.getColumnModel().getColumn(c).getHeaderValue().toString();
                for (String o : opts) if (o.equals(hdr)) cbs[c].setSelectedItem(o);
                colsPanel.add(cbs[c]);
            }
            JScrollPane sp = new JScrollPane(colsPanel);
            sp.setPreferredSize(new Dimension(280, 300));
            dlg.add(sp, BorderLayout.CENTER);
            JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton applyB = new JButton("Apply");
            applyB.addActionListener(ev -> {
                for (int c = 0; c < cbs.length; c++) {
                    String sel = (String) cbs[c].getSelectedItem();
                    if (sel == null || "(none)".equals(sel)) continue;
                    spreadsheetTable.getColumnModel().getColumn(c).setHeaderValue(sel);
                    for (int r = 0; r < spreadsheetTableModel.getRowCount(); r++) {
                        if (r < spreadsheetRowOrder.size()) {
                            Object item = spreadsheetRowOrder.get(r);
                            String val = "";
                            if ("Name".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).name;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).name;
                            } else if ("Start Date".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).startDate;
                                else if (item instanceof TimelineMilestone) val = ((TimelineMilestone) item).date;
                            } else if ("End Date".equals(sel)) {
                                if (item instanceof TimelineTask) val = ((TimelineTask) item).endDate;
                            } else if ("Center Text".equals(sel)) {
                                Object ex = spreadsheetTableModel.getValueAt(r, c);
                                val = ex != null ? ex.toString() : "";
                            }
                            spreadsheetTableModel.setValueAt(val, r, c);
                        }
                    }
                    if ("Center Text".equals(sel)) {
                        javax.swing.table.DefaultTableCellRenderer cr = new javax.swing.table.DefaultTableCellRenderer();
                        cr.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                        spreadsheetTable.getColumnModel().getColumn(c).setCellRenderer(cr);
                    }
                }
                spreadsheetTable.getTableHeader().repaint();
                spreadsheetTable.repaint();
                dlg.dispose();
            });
            btnPnl.add(applyB);
            JButton canB = new JButton("Cancel");
            canB.addActionListener(ev -> dlg.dispose());
            btnPnl.add(canB);
            dlg.add(btnPnl, BorderLayout.SOUTH);
            dlg.pack();
            dlg.setLocationRelativeTo(spreadsheetPanel);
            dlg.setVisible(true);
        });
        spreadsheetToolbar.add(selectColumnsBtn);
        spreadsheetPanel.add(spreadsheetToolbar, BorderLayout.NORTH);'''

content = content.replace(old, new)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done")
