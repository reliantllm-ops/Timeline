with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add the Add Shapes button after New Milestone button
old = '''        JButton newMilestoneBtn = new JButton("+ New Milestone");
        newMilestoneBtn.addActionListener(e -> showMilestoneShapeMenu(newMilestoneBtn));
        toolbarPanel.add(newMilestoneBtn);'''

new = '''        JButton newMilestoneBtn = new JButton("+ New Milestone");
        newMilestoneBtn.addActionListener(e -> showMilestoneShapeMenu(newMilestoneBtn));
        toolbarPanel.add(newMilestoneBtn);

        JButton addShapesBtn = new JButton("+ Add Shapes");
        addShapesBtn.addActionListener(e -> showShapesMenu(addShapesBtn));
        toolbarPanel.add(addShapesBtn);'''

content = content.replace(old, new)

# Add the showShapesMenu method after showMilestoneShapeMenu
old2 = '''    private void addNewMilestone(String shape) {'''

new2 = '''    private void showShapesMenu(JButton button) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));

        String[] shapes = {"Rectangle", "Oval", "Arrow Right", "Arrow Left", "Arrow Up", "Arrow Down", "Pentagon", "Cross", "Heart", "Crescent"};
        int buttonWidth = button.getWidth();

        for (String shape : shapes) {
            JMenuItem item = new JMenuItem(shape) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int cy = getHeight() / 2;
                    int size = 10;
                    g2d.setColor(new Color(80, 80, 80));
                    drawMilestoneShape(g2d, shape.toLowerCase().replace(" ", "_"), 20, cy, size, size, true);
                    g2d.setColor(Color.BLACK);
                    drawMilestoneShape(g2d, shape.toLowerCase().replace(" ", "_"), 20, cy, size, size, false);
                }
            };
            item.setPreferredSize(new Dimension(buttonWidth, 28));
            item.setBorder(BorderFactory.createEmptyBorder(4, 35, 4, 10));
            item.addActionListener(e -> addNewShape(shape.toLowerCase().replace(" ", "_")));
            popup.add(item);
        }

        popup.show(button, 0, button.getHeight());
    }

    private void addNewShape(String shape) {
        saveState();
        int shapeIndex = milestones.size();
        String name = "Shape " + (shapeIndex + 1);
        LocalDate timelineStart;
        try {
            timelineStart = LocalDate.parse(startDateField.getText().trim(), DATE_FORMAT);
        } catch (Exception e) {
            timelineStart = LocalDate.now();
        }
        String date = timelineStart.format(DATE_FORMAT);

        TimelineMilestone shapeObj = new TimelineMilestone(name, date, shape);
        shapeObj.fillColor = TASK_COLORS[(shapeIndex + 5) % TASK_COLORS.length];
        milestones.add(shapeObj);
        layerOrder.add(0, shapeObj);
        refreshTimeline();
    }

    private void addNewMilestone(String shape) {'''

content = content.replace(old2, new2)

# Add more shapes to drawMilestoneShape method
old3 = '''            case "hexagon":
                double angle = Math.PI / 6;
                xPoints = new int[6];
                yPoints = new int[6];
                for (int i = 0; i < 6; i++) {
                    xPoints[i] = cx + (int)(w * Math.cos(angle + i * Math.PI / 3));
                    yPoints[i] = cy + (int)(h * Math.sin(angle + i * Math.PI / 3));
                }
                if (fill) g2d.fillPolygon(xPoints, yPoints, 6);
                else g2d.drawPolygon(xPoints, yPoints, 6);
                break;
        }
    }'''

new3 = '''            case "hexagon":
                double angle = Math.PI / 6;
                xPoints = new int[6];
                yPoints = new int[6];
                for (int i = 0; i < 6; i++) {
                    xPoints[i] = cx + (int)(w * Math.cos(angle + i * Math.PI / 3));
                    yPoints[i] = cy + (int)(h * Math.sin(angle + i * Math.PI / 3));
                }
                if (fill) g2d.fillPolygon(xPoints, yPoints, 6);
                else g2d.drawPolygon(xPoints, yPoints, 6);
                break;
            case "rectangle":
                if (fill) g2d.fillRect(cx - w, cy - h/2, w * 2, h);
                else g2d.drawRect(cx - w, cy - h/2, w * 2, h);
                break;
            case "oval":
                if (fill) g2d.fillOval(cx - w, cy - h/2, w * 2, h);
                else g2d.drawOval(cx - w, cy - h/2, w * 2, h);
                break;
            case "arrow_right":
                xPoints = new int[]{cx - w, cx, cx - w};
                yPoints = new int[]{cy - h, cy, cy + h};
                if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                else g2d.drawPolygon(xPoints, yPoints, 3);
                break;
            case "arrow_left":
                xPoints = new int[]{cx + w, cx, cx + w};
                yPoints = new int[]{cy - h, cy, cy + h};
                if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                else g2d.drawPolygon(xPoints, yPoints, 3);
                break;
            case "arrow_up":
                xPoints = new int[]{cx - w, cx, cx + w};
                yPoints = new int[]{cy + h, cy - h, cy + h};
                if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                else g2d.drawPolygon(xPoints, yPoints, 3);
                break;
            case "arrow_down":
                xPoints = new int[]{cx - w, cx, cx + w};
                yPoints = new int[]{cy - h, cy + h, cy - h};
                if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                else g2d.drawPolygon(xPoints, yPoints, 3);
                break;
            case "pentagon":
                xPoints = new int[5];
                yPoints = new int[5];
                for (int i = 0; i < 5; i++) {
                    xPoints[i] = cx + (int)(w * Math.cos(-Math.PI/2 + i * 2 * Math.PI / 5));
                    yPoints[i] = cy + (int)(h * Math.sin(-Math.PI/2 + i * 2 * Math.PI / 5));
                }
                if (fill) g2d.fillPolygon(xPoints, yPoints, 5);
                else g2d.drawPolygon(xPoints, yPoints, 5);
                break;
            case "cross":
                int cw = w / 3;
                int ch = h / 3;
                xPoints = new int[]{cx - cw, cx + cw, cx + cw, cx + w, cx + w, cx + cw, cx + cw, cx - cw, cx - cw, cx - w, cx - w, cx - cw};
                yPoints = new int[]{cy - h, cy - h, cy - ch, cy - ch, cy + ch, cy + ch, cy + h, cy + h, cy + ch, cy + ch, cy - ch, cy - ch};
                if (fill) g2d.fillPolygon(xPoints, yPoints, 12);
                else g2d.drawPolygon(xPoints, yPoints, 12);
                break;
            case "heart":
                java.awt.geom.Path2D.Double heart = new java.awt.geom.Path2D.Double();
                heart.moveTo(cx, cy + h);
                heart.curveTo(cx - w * 2, cy - h/2, cx - w, cy - h, cx, cy - h/3);
                heart.curveTo(cx + w, cy - h, cx + w * 2, cy - h/2, cx, cy + h);
                if (fill) g2d.fill(heart);
                else g2d.draw(heart);
                break;
            case "crescent":
                java.awt.geom.Area outer = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(cx - w, cy - h, w * 2, h * 2));
                java.awt.geom.Area inner = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(cx - w/2, cy - h, w * 2, h * 2));
                outer.subtract(inner);
                if (fill) g2d.fill(outer);
                else g2d.draw(outer);
                break;
        }
    }'''

content = content.replace(old3, new3)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - added Add Shapes button with Rectangle, Oval, Arrows, Pentagon, Cross, Heart, Crescent")
