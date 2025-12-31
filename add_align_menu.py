with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add right-click popup check in mouseReleased
old = '''                public void mouseReleased(MouseEvent e) {
                    if (isDragging) {
                        isDragging = false;
                        draggingTaskIndex = -1;
                        setCursor(Cursor.getDefaultCursor());
                    }'''

new = '''                public void mouseReleased(MouseEvent e) {
                    // Check for right-click on multi-selection
                    if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                        int totalSelected = selectedTaskIndices.size() + selectedMilestoneIndices.size();
                        if (selectedMilestoneIndex >= 0 && !selectedMilestoneIndices.contains(selectedMilestoneIndex)) {
                            totalSelected++;
                        }
                        if (totalSelected > 1) {
                            showAlignmentPopup(e.getX(), e.getY());
                            return;
                        }
                    }
                    if (isDragging) {
                        isDragging = false;
                        draggingTaskIndex = -1;
                        setCursor(Cursor.getDefaultCursor());
                    }'''

content = content.replace(old, new)

# Add the showAlignmentPopup method and alignment methods after setupMouseListeners
old2 = '''        private void handleDrag(int x) {'''

new2 = '''        private void showAlignmentPopup(int x, int y) {
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
        }

        private void alignSelectedObjects(String alignment) {
            saveState();

            // Gather all selected objects with their bounds
            java.util.List<Object> selectedObjects = new java.util.ArrayList<>();
            java.util.List<int[]> bounds = new java.util.ArrayList<>(); // [x, y, width, height]

            // Add selected tasks
            for (int idx : selectedTaskIndices) {
                if (idx >= 0 && idx < tasks.size()) {
                    TimelineTask task = tasks.get(idx);
                    selectedObjects.add(task);
                    try {
                        LocalDate taskStart = LocalDate.parse(task.startDate, DATE_FORMAT);
                        LocalDate taskEnd = LocalDate.parse(task.endDate, DATE_FORMAT);
                        int timelineX = MARGIN_LEFT;
                        int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
                        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
                        int x1 = getXForDate(taskStart, timelineX, timelineWidth, totalDays);
                        int x2 = getXForDate(taskEnd, timelineX, timelineWidth, totalDays);
                        int y = task.yPosition >= 0 ? task.yPosition : 100;
                        bounds.add(new int[]{x1, y, x2 - x1, task.height});
                    } catch (Exception ex) {}
                }
            }

            // Add selected milestones
            for (int idx : selectedMilestoneIndices) {
                if (idx >= 0 && idx < milestones.size()) {
                    TimelineMilestone ms = milestones.get(idx);
                    selectedObjects.add(ms);
                    try {
                        LocalDate msDate = LocalDate.parse(ms.date, DATE_FORMAT);
                        int timelineX = MARGIN_LEFT;
                        int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
                        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
                        int mx = getXForDate(msDate, timelineX, timelineWidth, totalDays);
                        int my = ms.yPosition >= 0 ? ms.yPosition : 100;
                        bounds.add(new int[]{mx - ms.width/2, my - ms.height/2, ms.width, ms.height});
                    } catch (Exception ex) {}
                }
            }

            // Also add primary selected milestone if not in the set
            if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()
                && !selectedMilestoneIndices.contains(selectedMilestoneIndex)) {
                TimelineMilestone ms = milestones.get(selectedMilestoneIndex);
                selectedObjects.add(ms);
                try {
                    LocalDate msDate = LocalDate.parse(ms.date, DATE_FORMAT);
                    int timelineX = MARGIN_LEFT;
                    int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
                    long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
                    int mx = getXForDate(msDate, timelineX, timelineWidth, totalDays);
                    int my = ms.yPosition >= 0 ? ms.yPosition : 100;
                    bounds.add(new int[]{mx - ms.width/2, my - ms.height/2, ms.width, ms.height});
                } catch (Exception ex) {}
            }

            if (bounds.size() < 2) return;

            // Find alignment target based on alignment type
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
            }

            // Apply alignment
            int timelineX = MARGIN_LEFT;
            int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);

            for (int i = 0; i < selectedObjects.size(); i++) {
                Object obj = selectedObjects.get(i);
                int[] b = bounds.get(i);

                if (obj instanceof TimelineTask) {
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
                }
            }

            refreshTimeline();
        }

        private void handleDrag(int x) {'''

content = content.replace(old2, new2)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - added alignment popup menu for multi-selection")
