with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Update showAlignmentPopup to add more options
old = '''        private void showAlignmentPopup(int x, int y) {
            JPopupMenu popup = new JPopupMenu();

            JMenuItem alignLeft = new JMenuItem("Align Left");
            alignLeft.addActionListener(e -> alignSelectedObjects("left"));
            popup.add(alignLeft);

            JMenuItem alignRight = new JMenuItem("Align Right");
            alignRight.addActionListener(e -> alignSelectedObjects("right"));
            popup.add(alignRight);

            JMenuItem alignTop = new JMenuItem("Align Top");
            alignTop.addActionListener(e -> alignSelectedObjects("top"));
            popup.add(alignTop);

            JMenuItem alignBottom = new JMenuItem("Align Bottom");
            alignBottom.addActionListener(e -> alignSelectedObjects("bottom"));
            popup.add(alignBottom);

            popup.show(this, x, y);
        }'''

new = '''        private void showAlignmentPopup(int x, int y) {
            JPopupMenu popup = new JPopupMenu();

            JMenuItem alignLeft = new JMenuItem("Align Left");
            alignLeft.addActionListener(e -> alignSelectedObjects("left"));
            popup.add(alignLeft);

            JMenuItem alignRight = new JMenuItem("Align Right");
            alignRight.addActionListener(e -> alignSelectedObjects("right"));
            popup.add(alignRight);

            JMenuItem alignTop = new JMenuItem("Align Top");
            alignTop.addActionListener(e -> alignSelectedObjects("top"));
            popup.add(alignTop);

            JMenuItem alignBottom = new JMenuItem("Align Bottom");
            alignBottom.addActionListener(e -> alignSelectedObjects("bottom"));
            popup.add(alignBottom);

            JMenuItem alignCenter = new JMenuItem("Align Center");
            alignCenter.addActionListener(e -> alignSelectedObjects("center"));
            popup.add(alignCenter);

            popup.addSeparator();

            JMenuItem distHoriz = new JMenuItem("Distribute Horizontally");
            distHoriz.addActionListener(e -> alignSelectedObjects("distribute_h"));
            popup.add(distHoriz);

            JMenuItem distVert = new JMenuItem("Distribute Vertically");
            distVert.addActionListener(e -> alignSelectedObjects("distribute_v"));
            popup.add(distVert);

            popup.show(this, x, y);
        }'''

content = content.replace(old, new)

# Update alignSelectedObjects to handle new options
old2 = '''            // Find alignment target based on alignment type
            int targetValue = 0;
            switch (alignment) {
                case "left":
                    targetValue = Integer.MAX_VALUE;
                    for (int[] b : bounds) targetValue = Math.min(targetValue, b[0]);
                    break;
                case "right":
                    targetValue = Integer.MIN_VALUE;
                    for (int[] b : bounds) targetValue = Math.max(targetValue, b[0] + b[2]);
                    break;
                case "top":
                    targetValue = Integer.MAX_VALUE;
                    for (int[] b : bounds) targetValue = Math.min(targetValue, b[1]);
                    break;
                case "bottom":
                    targetValue = Integer.MIN_VALUE;
                    for (int[] b : bounds) targetValue = Math.max(targetValue, b[1] + b[3]);
                    break;
            }'''

new2 = '''            // Find alignment target based on alignment type
            int targetValue = 0;
            switch (alignment) {
                case "left":
                    targetValue = Integer.MAX_VALUE;
                    for (int[] b : bounds) targetValue = Math.min(targetValue, b[0]);
                    break;
                case "right":
                    targetValue = Integer.MIN_VALUE;
                    for (int[] b : bounds) targetValue = Math.max(targetValue, b[0] + b[2]);
                    break;
                case "top":
                    targetValue = Integer.MAX_VALUE;
                    for (int[] b : bounds) targetValue = Math.min(targetValue, b[1]);
                    break;
                case "bottom":
                    targetValue = Integer.MIN_VALUE;
                    for (int[] b : bounds) targetValue = Math.max(targetValue, b[1] + b[3]);
                    break;
                case "center":
                    // Find average center X
                    int sumCenterX = 0;
                    for (int[] b : bounds) sumCenterX += b[0] + b[2] / 2;
                    targetValue = sumCenterX / bounds.size();
                    break;
                case "distribute_h":
                case "distribute_v":
                    // Handle distribution separately
                    distributeSelectedObjects(alignment.equals("distribute_h"), selectedObjects, bounds);
                    return;
            }'''

content = content.replace(old2, new2)

# Add center case to the apply alignment section
old3 = '''                if (obj instanceof TimelineTask) {
                    TimelineTask task = (TimelineTask) obj;
                    switch (alignment) {
                        case "left":
                            // Move task so its left edge is at targetValue
                            LocalDate newStart = getDateForX(targetValue, timelineX, timelineWidth, totalDays);
                            if (newStart != null) {
                                try {
                                    LocalDate oldStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                                    LocalDate oldEnd = LocalDate.parse(task.endDate, DATE_FORMAT);
                                    long duration = ChronoUnit.DAYS.between(oldStart, oldEnd);
                                    task.startDate = newStart.format(DATE_FORMAT);
                                    task.endDate = newStart.plusDays(duration).format(DATE_FORMAT);
                                } catch (Exception ex) {}
                            }
                            break;
                        case "right":
                            // Move task so its right edge is at targetValue
                            LocalDate newEnd = getDateForX(targetValue, timelineX, timelineWidth, totalDays);
                            if (newEnd != null) {
                                try {
                                    LocalDate oldStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                                    LocalDate oldEnd = LocalDate.parse(task.endDate, DATE_FORMAT);
                                    long duration = ChronoUnit.DAYS.between(oldStart, oldEnd);
                                    task.endDate = newEnd.format(DATE_FORMAT);
                                    task.startDate = newEnd.minusDays(duration).format(DATE_FORMAT);
                                } catch (Exception ex) {}
                            }
                            break;
                        case "top":
                            task.yPosition = targetValue;
                            break;
                        case "bottom":
                            task.yPosition = targetValue - task.height;
                            break;
                    }
                } else if (obj instanceof TimelineMilestone) {
                    TimelineMilestone ms = (TimelineMilestone) obj;
                    switch (alignment) {
                        case "left":
                            // Move milestone so its left edge is at targetValue
                            LocalDate newDate = getDateForX(targetValue + ms.width/2, timelineX, timelineWidth, totalDays);
                            if (newDate != null) {
                                ms.date = newDate.format(DATE_FORMAT);
                            }
                            break;
                        case "right":
                            // Move milestone so its right edge is at targetValue
                            newDate = getDateForX(targetValue - ms.width/2, timelineX, timelineWidth, totalDays);
                            if (newDate != null) {
                                ms.date = newDate.format(DATE_FORMAT);
                            }
                            break;
                        case "top":
                            ms.yPosition = targetValue + ms.height/2;
                            break;
                        case "bottom":
                            ms.yPosition = targetValue - ms.height/2;
                            break;
                    }
                }'''

new3 = '''                if (obj instanceof TimelineTask) {
                    TimelineTask task = (TimelineTask) obj;
                    switch (alignment) {
                        case "left":
                            // Move task so its left edge is at targetValue
                            LocalDate newStart = getDateForX(targetValue, timelineX, timelineWidth, totalDays);
                            if (newStart != null) {
                                try {
                                    LocalDate oldStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                                    LocalDate oldEnd = LocalDate.parse(task.endDate, DATE_FORMAT);
                                    long duration = ChronoUnit.DAYS.between(oldStart, oldEnd);
                                    task.startDate = newStart.format(DATE_FORMAT);
                                    task.endDate = newStart.plusDays(duration).format(DATE_FORMAT);
                                } catch (Exception ex) {}
                            }
                            break;
                        case "right":
                            // Move task so its right edge is at targetValue
                            LocalDate newEnd = getDateForX(targetValue, timelineX, timelineWidth, totalDays);
                            if (newEnd != null) {
                                try {
                                    LocalDate oldStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                                    LocalDate oldEnd = LocalDate.parse(task.endDate, DATE_FORMAT);
                                    long duration = ChronoUnit.DAYS.between(oldStart, oldEnd);
                                    task.endDate = newEnd.format(DATE_FORMAT);
                                    task.startDate = newEnd.minusDays(duration).format(DATE_FORMAT);
                                } catch (Exception ex) {}
                            }
                            break;
                        case "top":
                            task.yPosition = targetValue;
                            break;
                        case "bottom":
                            task.yPosition = targetValue - task.height;
                            break;
                        case "center":
                            // Move task so its center is at targetValue
                            try {
                                LocalDate oldStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                                LocalDate oldEnd = LocalDate.parse(task.endDate, DATE_FORMAT);
                                long duration = ChronoUnit.DAYS.between(oldStart, oldEnd);
                                int taskWidth = b[2];
                                LocalDate newCenter = getDateForX(targetValue, timelineX, timelineWidth, totalDays);
                                if (newCenter != null) {
                                    LocalDate newStartDate = getDateForX(targetValue - taskWidth/2, timelineX, timelineWidth, totalDays);
                                    if (newStartDate != null) {
                                        task.startDate = newStartDate.format(DATE_FORMAT);
                                        task.endDate = newStartDate.plusDays(duration).format(DATE_FORMAT);
                                    }
                                }
                            } catch (Exception ex) {}
                            break;
                    }
                } else if (obj instanceof TimelineMilestone) {
                    TimelineMilestone ms = (TimelineMilestone) obj;
                    switch (alignment) {
                        case "left":
                            // Move milestone so its left edge is at targetValue
                            LocalDate newDate = getDateForX(targetValue + ms.width/2, timelineX, timelineWidth, totalDays);
                            if (newDate != null) {
                                ms.date = newDate.format(DATE_FORMAT);
                            }
                            break;
                        case "right":
                            // Move milestone so its right edge is at targetValue
                            newDate = getDateForX(targetValue - ms.width/2, timelineX, timelineWidth, totalDays);
                            if (newDate != null) {
                                ms.date = newDate.format(DATE_FORMAT);
                            }
                            break;
                        case "top":
                            ms.yPosition = targetValue + ms.height/2;
                            break;
                        case "bottom":
                            ms.yPosition = targetValue - ms.height/2;
                            break;
                        case "center":
                            // Move milestone so its center is at targetValue
                            newDate = getDateForX(targetValue, timelineX, timelineWidth, totalDays);
                            if (newDate != null) {
                                ms.date = newDate.format(DATE_FORMAT);
                            }
                            break;
                    }
                }'''

content = content.replace(old3, new3)

# Add the distributeSelectedObjects method after alignSelectedObjects
old4 = '''        private void handleDrag(int x) {'''

new4 = '''        private void distributeSelectedObjects(boolean horizontal, java.util.List<Object> objects, java.util.List<int[]> bounds) {
            if (objects.size() < 3) return; // Need at least 3 items to distribute

            int timelineX = MARGIN_LEFT;
            int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);

            // Sort objects by position
            java.util.List<Integer> indices = new java.util.ArrayList<>();
            for (int i = 0; i < objects.size(); i++) indices.add(i);

            if (horizontal) {
                // Sort by X position (center)
                indices.sort((a, b) -> {
                    int centerA = bounds.get(a)[0] + bounds.get(a)[2] / 2;
                    int centerB = bounds.get(b)[0] + bounds.get(b)[2] / 2;
                    return Integer.compare(centerA, centerB);
                });

                // Get first and last center positions
                int firstCenter = bounds.get(indices.get(0))[0] + bounds.get(indices.get(0))[2] / 2;
                int lastCenter = bounds.get(indices.get(indices.size() - 1))[0] + bounds.get(indices.get(indices.size() - 1))[2] / 2;
                int totalSpan = lastCenter - firstCenter;
                int spacing = totalSpan / (indices.size() - 1);

                // Distribute middle items
                for (int i = 1; i < indices.size() - 1; i++) {
                    int idx = indices.get(i);
                    Object obj = objects.get(idx);
                    int[] b = bounds.get(idx);
                    int newCenterX = firstCenter + spacing * i;

                    if (obj instanceof TimelineTask) {
                        TimelineTask task = (TimelineTask) obj;
                        try {
                            LocalDate oldStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                            LocalDate oldEnd = LocalDate.parse(task.endDate, DATE_FORMAT);
                            long duration = ChronoUnit.DAYS.between(oldStart, oldEnd);
                            LocalDate newStart = getDateForX(newCenterX - b[2] / 2, timelineX, timelineWidth, totalDays);
                            if (newStart != null) {
                                task.startDate = newStart.format(DATE_FORMAT);
                                task.endDate = newStart.plusDays(duration).format(DATE_FORMAT);
                            }
                        } catch (Exception ex) {}
                    } else if (obj instanceof TimelineMilestone) {
                        TimelineMilestone ms = (TimelineMilestone) obj;
                        LocalDate newDate = getDateForX(newCenterX, timelineX, timelineWidth, totalDays);
                        if (newDate != null) {
                            ms.date = newDate.format(DATE_FORMAT);
                        }
                    }
                }
            } else {
                // Sort by Y position (center)
                indices.sort((a, b) -> {
                    int centerA = bounds.get(a)[1] + bounds.get(a)[3] / 2;
                    int centerB = bounds.get(b)[1] + bounds.get(b)[3] / 2;
                    return Integer.compare(centerA, centerB);
                });

                // Get first and last center positions
                int firstCenter = bounds.get(indices.get(0))[1] + bounds.get(indices.get(0))[3] / 2;
                int lastCenter = bounds.get(indices.get(indices.size() - 1))[1] + bounds.get(indices.get(indices.size() - 1))[3] / 2;
                int totalSpan = lastCenter - firstCenter;
                int spacing = totalSpan / (indices.size() - 1);

                // Distribute middle items
                for (int i = 1; i < indices.size() - 1; i++) {
                    int idx = indices.get(i);
                    Object obj = objects.get(idx);
                    int[] b = bounds.get(idx);
                    int newCenterY = firstCenter + spacing * i;

                    if (obj instanceof TimelineTask) {
                        TimelineTask task = (TimelineTask) obj;
                        task.yPosition = newCenterY - task.height / 2;
                    } else if (obj instanceof TimelineMilestone) {
                        TimelineMilestone ms = (TimelineMilestone) obj;
                        ms.yPosition = newCenterY;
                    }
                }
            }

            refreshTimeline();
        }

        private void handleDrag(int x) {'''

content = content.replace(old4, new4)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - added Align Center, Distribute Horizontally, Distribute Vertically")
