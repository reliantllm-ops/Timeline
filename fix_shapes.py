with open('Timeline2.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add new shape cases after hexagon in drawMilestoneShape method
old = '''            case "hexagon":
                int hw = w * 2 / 3;
                xPoints = new int[]{cx - w, cx - hw, cx + hw, cx + w, cx + hw, cx - hw};
                yPoints = new int[]{cy, cy - h, cy - h, cy, cy + h, cy + h};
                if (fill) g2d.fillPolygon(xPoints, yPoints, 6);
                else g2d.drawPolygon(xPoints, yPoints, 6);
                break;
        }
    }'''

new = '''            case "hexagon":
                int hw = w * 2 / 3;
                xPoints = new int[]{cx - w, cx - hw, cx + hw, cx + w, cx + hw, cx - hw};
                yPoints = new int[]{cy, cy - h, cy - h, cy, cy + h, cy + h};
                if (fill) g2d.fillPolygon(xPoints, yPoints, 6);
                else g2d.drawPolygon(xPoints, yPoints, 6);
                break;
            case "rectangle":
                if (fill) g2d.fillRect(cx - w, cy - h, w * 2, h * 2);
                else g2d.drawRect(cx - w, cy - h, w * 2, h * 2);
                break;
            case "oval":
                if (fill) g2d.fillOval(cx - w, cy - h, w * 2, h * 2);
                else g2d.drawOval(cx - w, cy - h, w * 2, h * 2);
                break;
            case "arrow_right":
                xPoints = new int[]{cx - w, cx + w, cx - w};
                yPoints = new int[]{cy - h, cy, cy + h};
                if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                else g2d.drawPolygon(xPoints, yPoints, 3);
                break;
            case "arrow_left":
                xPoints = new int[]{cx + w, cx - w, cx + w};
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

content = content.replace(old, new)

# Also add to the second drawMilestoneShapeOnPanel method
old2 = '''                case "hexagon":
                    int hexW = halfW;
                    int hexH = halfH;
                    xPoints = new int[]{cx - hexW / 2, cx + hexW / 2, cx + hexW, cx + hexW / 2, cx - hexW / 2, cx - hexW};
                    yPoints = new int[]{cy - hexH, cy - hexH, cy, cy + hexH, cy + hexH, cy};
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 6);
                    else g2d.drawPolygon(xPoints, yPoints, 6);
                    break;
            }
        }'''

new2 = '''                case "hexagon":
                    int hexW = halfW;
                    int hexH = halfH;
                    xPoints = new int[]{cx - hexW / 2, cx + hexW / 2, cx + hexW, cx + hexW / 2, cx - hexW / 2, cx - hexW};
                    yPoints = new int[]{cy - hexH, cy - hexH, cy, cy + hexH, cy + hexH, cy};
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 6);
                    else g2d.drawPolygon(xPoints, yPoints, 6);
                    break;
                case "rectangle":
                    if (fill) g2d.fillRect(cx - halfW, cy - halfH, halfW * 2, halfH * 2);
                    else g2d.drawRect(cx - halfW, cy - halfH, halfW * 2, halfH * 2);
                    break;
                case "oval":
                    if (fill) g2d.fillOval(cx - halfW, cy - halfH, halfW * 2, halfH * 2);
                    else g2d.drawOval(cx - halfW, cy - halfH, halfW * 2, halfH * 2);
                    break;
                case "arrow_right":
                    xPoints = new int[]{cx - halfW, cx + halfW, cx - halfW};
                    yPoints = new int[]{cy - halfH, cy, cy + halfH};
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                    else g2d.drawPolygon(xPoints, yPoints, 3);
                    break;
                case "arrow_left":
                    xPoints = new int[]{cx + halfW, cx - halfW, cx + halfW};
                    yPoints = new int[]{cy - halfH, cy, cy + halfH};
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                    else g2d.drawPolygon(xPoints, yPoints, 3);
                    break;
                case "arrow_up":
                    xPoints = new int[]{cx - halfW, cx, cx + halfW};
                    yPoints = new int[]{cy + halfH, cy - halfH, cy + halfH};
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                    else g2d.drawPolygon(xPoints, yPoints, 3);
                    break;
                case "arrow_down":
                    xPoints = new int[]{cx - halfW, cx, cx + halfW};
                    yPoints = new int[]{cy - halfH, cy + halfH, cy - halfH};
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 3);
                    else g2d.drawPolygon(xPoints, yPoints, 3);
                    break;
                case "pentagon":
                    xPoints = new int[5];
                    yPoints = new int[5];
                    for (int i = 0; i < 5; i++) {
                        xPoints[i] = cx + (int)(halfW * Math.cos(-Math.PI/2 + i * 2 * Math.PI / 5));
                        yPoints[i] = cy + (int)(halfH * Math.sin(-Math.PI/2 + i * 2 * Math.PI / 5));
                    }
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 5);
                    else g2d.drawPolygon(xPoints, yPoints, 5);
                    break;
                case "cross":
                    int crossW = halfW / 3;
                    int crossH = halfH / 3;
                    xPoints = new int[]{cx - crossW, cx + crossW, cx + crossW, cx + halfW, cx + halfW, cx + crossW, cx + crossW, cx - crossW, cx - crossW, cx - halfW, cx - halfW, cx - crossW};
                    yPoints = new int[]{cy - halfH, cy - halfH, cy - crossH, cy - crossH, cy + crossH, cy + crossH, cy + halfH, cy + halfH, cy + crossH, cy + crossH, cy - crossH, cy - crossH};
                    if (fill) g2d.fillPolygon(xPoints, yPoints, 12);
                    else g2d.drawPolygon(xPoints, yPoints, 12);
                    break;
                case "heart":
                    java.awt.geom.Path2D.Double heartShape = new java.awt.geom.Path2D.Double();
                    heartShape.moveTo(cx, cy + halfH);
                    heartShape.curveTo(cx - halfW * 2, cy - halfH/2, cx - halfW, cy - halfH, cx, cy - halfH/3);
                    heartShape.curveTo(cx + halfW, cy - halfH, cx + halfW * 2, cy - halfH/2, cx, cy + halfH);
                    if (fill) g2d.fill(heartShape);
                    else g2d.draw(heartShape);
                    break;
                case "crescent":
                    java.awt.geom.Area outerMoon = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(cx - halfW, cy - halfH, halfW * 2, halfH * 2));
                    java.awt.geom.Area innerMoon = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(cx - halfW/2, cy - halfH, halfW * 2, halfH * 2));
                    outerMoon.subtract(innerMoon);
                    if (fill) g2d.fill(outerMoon);
                    else g2d.draw(outerMoon);
                    break;
            }
        }'''

content = content.replace(old2, new2)

with open('Timeline2.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done - added shape drawing code for all new shapes")
