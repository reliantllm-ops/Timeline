with open('Timeline2.java', 'r') as f:
    content = f.read()

old = '''        // Add toolbar at top of spreadsheet with Select Columns button
        JPanel spreadsheetToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        spreadsheetToolbar.setBackground(new Color(240, 240, 240));
        JButton selectColumnsBtn = new JButton("Select Columns");
        selectColumnsBtn.setMargin(new Insets(2, 8, 2, 8));

        // Column selection dropdowns panel (initially hidden)
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

new = '''        // Add toolbar at top of spreadsheet with Select Columns button
        JPanel spreadsheetToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        spreadsheetToolbar.setBackground(new Color(240, 240, 240));
        JButton selectColumnsBtn = new JButton("Select Columns");
        selectColumnsBtn.setMargin(new Insets(2, 8, 2, 8));

        selectColumnsBtn.addActionListener(e -> {
            // Show dialog with column dropdowns
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(spreadsheetPanel), "Select Column Types", true);
            dialog.setLayout(new BorderLayout());

            JPanel columnsPanel = new JPanel(new GridLayout(0, 2, 10, 5));
            columnsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            String[] columnOptions = {"(none)", "Name", "Start Date", "End Date", "Center Text"};
            JComboBox<String>[] combos = new JComboBox[spreadsheetTable.getColumnCount()];

            for (int col = 0; col < spreadsheetTable.getColumnCount(); col++) {
                columnsPanel.add(new JLabel("Column " + (col + 1) + ":"));
                combos[col] = new JComboBox<>(columnOptions);
                String currentHeader = spreadsheetTable.getColumnModel().getColumn(col).getHeaderValue().toString();
                for (String opt : columnOptions) {
                    if (opt.equals(currentHeader)) combos[col].setSelectedItem(opt);
                }
                columnsPanel.add(combos[col]);
            }

            JScrollPane scrollPane = new JScrollPane(columnsPanel);
            scrollPane.setPreferredSize(new Dimension(300, 300));
            dialog.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton applyBtn = new JButton("Apply");
            applyBtn.addActionListener(ev -> {
                for (int col = 0; col < combos.length; col++) {
                    String selected = (String) combos[col].getSelectedItem();
                    if (selected == null || "(none)".equals(selected)) continue;

                    spreadsheetTable.getColumnModel().getColumn(col).setHeaderValue(selected);

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
                                Object existing = spreadsheetTableModel.getValueAt(row, col);
                                value = existing != null ? existing.toString() : "";
                            }
                            spreadsheetTableModel.setValueAt(value, row, col);
                        }
                    }

                    if ("Center Text".equals(selected)) {
                        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
                        centerRenderer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                        spreadsheetTable.getColumnModel().getColumn(col).setCellRenderer(centerRenderer);
                    }
                }
                spreadsheetTable.getTableHeader().repaint();
                spreadsheetTable.repaint();
                dialog.dispose();
            });
            buttonPanel.add(applyBtn);

            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(ev -> dialog.dispose());
            buttonPanel.add(cancelBtn);

            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setLocationRelativeTo(spreadsheetPanel);
            dialog.setVisible(true);
        });

        spreadsheetToolbar.add(selectColumnsBtn);
        spreadsheetPanel.add(spreadsheetToolbar, BorderLayout.NORTH);'''

content = content.replace(old, new)

with open('Timeline2.java', 'w') as f:
    f.write(content)

print("Done")
