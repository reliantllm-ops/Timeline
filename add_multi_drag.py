with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add fields to track multi-drag state
old1 = '''        private boolean isMoveDragging = false;'''

new1 = '''        private boolean isMoveDragging = false;
        private boolean isMultiDragging = false;
        private int multiDragStartX = 0;
        private int multiDragStartY = 0;
        private java.util.Map<Integer, String> multiDragTaskOriginalStarts = new java.util.HashMap<>();
        private java.util.Map<Integer, String> multiDragTaskOriginalEnds = new java.util.HashMap<>();
        private java.util.Map<Integer, Integer> multiDragTaskOriginalY = new java.util.HashMap<>();
        private java.util.Map<Integer, String> multiDragMilestoneOriginalDates = new java.util.HashMap<>();
        private java.util.Map<Integer, Integer> multiDragMilestoneOriginalY = new java.util.HashMap<>();'''

content = content.replace(old1, new1)

# Modify the task click handling to start multi-drag when clicking on a selected task in multi-selection
old2 = '''                            if (x >= x1 && x <= x1 + barWidth) {
                                selectTask(taskIdx, ctrlDown);
                                isMoveDragging = true;
                                moveDragTaskIndex = taskIdx;
                                moveDragStartX = x;
                                moveDragStartY = y;
                                moveDragOriginalStartDate = task.startDate;
                                moveDragOriginalEndDate = task.endDate;
                                moveDragOriginalYPosition = taskY;
                                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                                return;
                            }'''

new2 = '''                            if (x >= x1 && x <= x1 + barWidth) {
                                // Check if we're clicking on an already-selected item with multi-selection
                                int totalSelected = selectedTaskIndices.size() + selectedMilestoneIndices.size();
                                if (selectedMilestoneIndex >= 0 && !selectedMilestoneIndices.contains(selectedMilestoneIndex)) {
                                    totalSelected++;
                                }
                                boolean isAlreadySelected = selectedTaskIndices.contains(taskIdx);

                                if (totalSelected > 1 && isAlreadySelected && !ctrlDown) {
                                    // Start multi-drag
                                    startMultiDrag(x, y);
                                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                                    return;
                                }

                                selectTask(taskIdx, ctrlDown);
                                isMoveDragging = true;
                                moveDragTaskIndex = taskIdx;
                                moveDragStartX = x;
                                moveDragStartY = y;
                                moveDragOriginalStartDate = task.startDate;
                                moveDragOriginalEndDate = task.endDate;
                                moveDragOriginalYPosition = taskY;
                                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                                return;
                            }'''

content = content.replace(old2, new2)

# Modify the milestone click handling to start multi-drag when clicking on a selected milestone in multi-selection
old3 = '''                        if (x >= mx - halfW - boxPadding && x <= mx + halfW + boxPadding &&
                            y >= my - halfH - boxPadding && y <= my + halfH + boxPadding) {
                            selectMilestone(milestoneIdx, ctrlDown);
                            isMilestoneDragging = true;
                            milestoneDragIndex = milestoneIdx;
                            milestoneDragStartX = x;
                            milestoneDragStartY = y;
                            milestoneDragOriginalDate = milestone.date;
                            milestoneDragOriginalYPosition = my;
                            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                            return;
                        }'''

new3 = '''                        if (x >= mx - halfW - boxPadding && x <= mx + halfW + boxPadding &&
                            y >= my - halfH - boxPadding && y <= my + halfH + boxPadding) {
                            // Check if we're clicking on an already-selected item with multi-selection
                            int totalSelected = selectedTaskIndices.size() + selectedMilestoneIndices.size();
                            if (selectedMilestoneIndex >= 0 && !selectedMilestoneIndices.contains(selectedMilestoneIndex)) {
                                totalSelected++;
                            }
                            boolean isAlreadySelected = selectedMilestoneIndices.contains(milestoneIdx) || milestoneIdx == selectedMilestoneIndex;

                            if (totalSelected > 1 && isAlreadySelected && !ctrlDown) {
                                // Start multi-drag
                                startMultiDrag(x, y);
                                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                                return;
                            }

                            selectMilestone(milestoneIdx, ctrlDown);
                            isMilestoneDragging = true;
                            milestoneDragIndex = milestoneIdx;
                            milestoneDragStartX = x;
                            milestoneDragStartY = y;
                            milestoneDragOriginalDate = milestone.date;
                            milestoneDragOriginalYPosition = my;
                            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                            return;
                        }'''

content = content.replace(old3, new3)

# Add multi-drag handling to mouseDragged
old4 = '''                    if (isMoveDragging) {
                        handleMoveDrag(e.getX(), e.getY());
                    }'''

new4 = '''                    if (isMultiDragging) {
                        handleMultiDrag(e.getX(), e.getY());
                    }
                    if (isMoveDragging) {
                        handleMoveDrag(e.getX(), e.getY());
                    }'''

content = content.replace(old4, new4)

# Add multi-drag state saving
old5 = '''                    if (!dragStateSaved && (isDragging || isMoveDragging || isHeightDragging || isMilestoneDragging || isMilestoneResizing)) {'''

new5 = '''                    if (!dragStateSaved && (isDragging || isMoveDragging || isHeightDragging || isMilestoneDragging || isMilestoneResizing || isMultiDragging)) {'''

content = content.replace(old5, new5)

# Add multi-drag cleanup to mouseReleased
old6 = '''                    if (isMoveDragging) {
                        isMoveDragging = false;
                        moveDragTaskIndex = -1;
                        moveDragOriginalStartDate = null;
                        moveDragOriginalEndDate = null;
                        setCursor(Cursor.getDefaultCursor());
                        refreshTimeline();
                    }'''

new6 = '''                    if (isMultiDragging) {
                        isMultiDragging = false;
                        multiDragTaskOriginalStarts.clear();
                        multiDragTaskOriginalEnds.clear();
                        multiDragTaskOriginalY.clear();
                        multiDragMilestoneOriginalDates.clear();
                        multiDragMilestoneOriginalY.clear();
                        setCursor(Cursor.getDefaultCursor());
                        refreshTimeline();
                    }
                    if (isMoveDragging) {
                        isMoveDragging = false;
                        moveDragTaskIndex = -1;
                        moveDragOriginalStartDate = null;
                        moveDragOriginalEndDate = null;
                        setCursor(Cursor.getDefaultCursor());
                        refreshTimeline();
                    }'''

content = content.replace(old6, new6)

# Add the startMultiDrag and handleMultiDrag methods
old7 = '''        private void handleMoveDrag(int x, int y) {'''

new7 = '''        private void startMultiDrag(int x, int y) {
            isMultiDragging = true;
            multiDragStartX = x;
            multiDragStartY = y;

            // Store original positions for all selected tasks
            multiDragTaskOriginalStarts.clear();
            multiDragTaskOriginalEnds.clear();
            multiDragTaskOriginalY.clear();
            for (int idx : selectedTaskIndices) {
                if (idx >= 0 && idx < tasks.size()) {
                    TimelineTask task = tasks.get(idx);
                    multiDragTaskOriginalStarts.put(idx, task.startDate);
                    multiDragTaskOriginalEnds.put(idx, task.endDate);
                    multiDragTaskOriginalY.put(idx, task.yPosition >= 0 ? task.yPosition : 100);
                }
            }

            // Store original positions for all selected milestones
            multiDragMilestoneOriginalDates.clear();
            multiDragMilestoneOriginalY.clear();
            for (int idx : selectedMilestoneIndices) {
                if (idx >= 0 && idx < milestones.size()) {
                    TimelineMilestone ms = milestones.get(idx);
                    multiDragMilestoneOriginalDates.put(idx, ms.date);
                    multiDragMilestoneOriginalY.put(idx, ms.yPosition >= 0 ? ms.yPosition : 100);
                }
            }
            // Also include primary selected milestone if not in the set
            if (selectedMilestoneIndex >= 0 && selectedMilestoneIndex < milestones.size()
                && !selectedMilestoneIndices.contains(selectedMilestoneIndex)) {
                TimelineMilestone ms = milestones.get(selectedMilestoneIndex);
                multiDragMilestoneOriginalDates.put(selectedMilestoneIndex, ms.date);
                multiDragMilestoneOriginalY.put(selectedMilestoneIndex, ms.yPosition >= 0 ? ms.yPosition : 100);
            }
        }

        private void handleMultiDrag(int x, int y) {
            int timelineX = MARGIN_LEFT;
            int timelineWidth = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            if (totalDays <= 0) return;

            int deltaX = x - multiDragStartX;
            int deltaY = y - multiDragStartY;
            double daysPerPixel = (double) totalDays / timelineWidth;
            long daysDelta = Math.round(deltaX * daysPerPixel);

            // Move all selected tasks
            for (java.util.Map.Entry<Integer, String> entry : multiDragTaskOriginalStarts.entrySet()) {
                int idx = entry.getKey();
                if (idx >= 0 && idx < tasks.size()) {
                    TimelineTask task = tasks.get(idx);
                    try {
                        LocalDate origStart = LocalDate.parse(entry.getValue(), DATE_FORMAT);
                        LocalDate origEnd = LocalDate.parse(multiDragTaskOriginalEnds.get(idx), DATE_FORMAT);
                        task.startDate = origStart.plusDays(daysDelta).format(DATE_FORMAT);
                        task.endDate = origEnd.plusDays(daysDelta).format(DATE_FORMAT);
                    } catch (Exception ex) {}

                    Integer origY = multiDragTaskOriginalY.get(idx);
                    if (origY != null) {
                        task.yPosition = Math.max(35, origY + deltaY);
                    }
                }
            }

            // Move all selected milestones
            for (java.util.Map.Entry<Integer, String> entry : multiDragMilestoneOriginalDates.entrySet()) {
                int idx = entry.getKey();
                if (idx >= 0 && idx < milestones.size()) {
                    TimelineMilestone ms = milestones.get(idx);
                    try {
                        LocalDate origDate = LocalDate.parse(entry.getValue(), DATE_FORMAT);
                        LocalDate newDate = origDate.plusDays(daysDelta);
                        if (newDate.isBefore(startDate)) newDate = startDate;
                        if (newDate.isAfter(endDate)) newDate = endDate;
                        ms.date = newDate.format(DATE_FORMAT);
                    } catch (Exception ex) {}

                    Integer origY = multiDragMilestoneOriginalY.get(idx);
                    if (origY != null) {
                        ms.yPosition = Math.max(35, origY + deltaY);
                    }
                }
            }

            repaint();
        }

        private void handleMoveDrag(int x, int y) {'''

content = content.replace(old7, new7)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - added multi-object drag support")
