package model;

import java.awt.Color;

public class TimelineMilestone {
    public String name;
    public String date;
    public String shape;  // "diamond", "circle", "triangle", "star", "square", "hexagon"
    public int width = 20;
    public int height = 20;
    public int yPosition = -1;
    public Color fillColor = new Color(255, 193, 7);  // default gold/yellow
    public Color outlineColor = Color.BLACK;
    public int outlineThickness = 2;
    public boolean bevelFill = false; // bevel effect on fill
    public int bevelDepth = 60;       // bevel intensity (0-100)
    public int bevelLightAngle = 135; // light angle in degrees (0-360, 135 = top-left)
    public int bevelHighlightOpacity = 80; // highlight opacity (0-255)
    public int bevelShadowOpacity = 60;    // shadow opacity (0-255)
    public String bevelStyle = "Inner Bevel";  // "Inner Bevel", "Outer Bevel", "Emboss", "Pillow Emboss"
    public String topBevel = "Circle";    // PowerPoint-style top bevel shape
    public String bottomBevel = "None";   // PowerPoint-style bottom bevel shape
    public String shadowType = "No Shadow"; // Shadow effect: No Shadow, Bottom Right, Bottom Left, Top Right, Top Left
    // Center text properties (text below milestone - same as labelText)
    public String centerText = "";
    public String fontFamily = "SansSerif";
    public int fontSize = 10;
    public boolean fontBold = false;
    public boolean fontItalic = false;
    public Color textColor = Color.BLACK;
    public int centerTextXOffset = 0;
    public int centerTextYOffset = 0;
    public boolean centerTextWrap = false;
    public boolean centerTextVisible = true;
    // Front text properties (text in front of milestone)
    public String frontText = "";
    public String frontFontFamily = "SansSerif";
    public int frontFontSize = 10;
    public boolean frontFontBold = false;
    public boolean frontFontItalic = false;
    public Color frontTextColor = Color.BLACK;
    public int frontTextXOffset = 0;
    public int frontTextYOffset = 0;
    public boolean frontTextWrap = false;
    public boolean frontTextVisible = true;
    // Above text properties (text above milestone)
    public String aboveText = "";
    public String aboveFontFamily = "SansSerif";
    public int aboveFontSize = 10;
    public boolean aboveFontBold = false;
    public boolean aboveFontItalic = false;
    public Color aboveTextColor = Color.BLACK;
    public int aboveTextXOffset = 0;
    public int aboveTextYOffset = 0;
    public boolean aboveTextWrap = false;
    public boolean aboveTextVisible = true;
    // Underneath text properties (text below milestone, further down)
    public String underneathText = "";
    public String underneathFontFamily = "SansSerif";
    public int underneathFontSize = 10;
    public boolean underneathFontBold = false;
    public boolean underneathFontItalic = false;
    public Color underneathTextColor = Color.BLACK;
    public int underneathTextXOffset = 0;
    public int underneathTextYOffset = 0;
    public boolean underneathTextWrap = false;
    public boolean underneathTextVisible = true;
    // Behind text properties (text behind/after milestone)
    public String behindText = "";
    public String behindFontFamily = "SansSerif";
    public int behindFontSize = 10;
    public boolean behindFontBold = false;
    public boolean behindFontItalic = false;
    public Color behindTextColor = new Color(150, 150, 150);
    public int behindTextXOffset = 0;
    public int behindTextYOffset = 0;
    public boolean behindTextWrap = false;
    public boolean behindTextVisible = true;
    // Legacy support
    public String labelText = "";
    public boolean labelTextWrap = false;
    public boolean labelTextVisible = true;

    public TimelineMilestone(String name, String date, String shape) {
        this.name = name;
        this.centerText = ""; // leave center text blank by default
        this.labelText = ""; // leave label text blank by default
        this.date = date;
        this.shape = shape;
    }

    public TimelineMilestone copy() {
        TimelineMilestone copy = new TimelineMilestone(name, date, shape);
        copy.width = width;
        copy.height = height;
        copy.yPosition = yPosition;
        copy.fillColor = fillColor;
        copy.outlineColor = outlineColor;
        copy.outlineThickness = outlineThickness;
        copy.bevelFill = bevelFill;
        copy.bevelDepth = bevelDepth;
        copy.bevelLightAngle = bevelLightAngle;
        copy.bevelHighlightOpacity = bevelHighlightOpacity;
        copy.bevelShadowOpacity = bevelShadowOpacity;
        copy.bevelStyle = bevelStyle;
        copy.topBevel = topBevel;
        copy.bottomBevel = bottomBevel;
        copy.shadowType = shadowType;
        copy.centerText = centerText;
        copy.fontFamily = fontFamily;
        copy.fontSize = fontSize;
        copy.fontBold = fontBold;
        copy.fontItalic = fontItalic;
        copy.textColor = textColor;
        copy.centerTextXOffset = centerTextXOffset;
        copy.centerTextYOffset = centerTextYOffset;
        copy.centerTextWrap = centerTextWrap;
        copy.centerTextVisible = centerTextVisible;
        copy.frontText = frontText;
        copy.frontFontFamily = frontFontFamily;
        copy.frontFontSize = frontFontSize;
        copy.frontFontBold = frontFontBold;
        copy.frontFontItalic = frontFontItalic;
        copy.frontTextColor = frontTextColor;
        copy.frontTextXOffset = frontTextXOffset;
        copy.frontTextYOffset = frontTextYOffset;
        copy.frontTextWrap = frontTextWrap;
        copy.frontTextVisible = frontTextVisible;
        copy.aboveText = aboveText;
        copy.aboveFontFamily = aboveFontFamily;
        copy.aboveFontSize = aboveFontSize;
        copy.aboveFontBold = aboveFontBold;
        copy.aboveFontItalic = aboveFontItalic;
        copy.aboveTextColor = aboveTextColor;
        copy.aboveTextXOffset = aboveTextXOffset;
        copy.aboveTextYOffset = aboveTextYOffset;
        copy.aboveTextWrap = aboveTextWrap;
        copy.aboveTextVisible = aboveTextVisible;
        copy.underneathText = underneathText;
        copy.underneathFontFamily = underneathFontFamily;
        copy.underneathFontSize = underneathFontSize;
        copy.underneathFontBold = underneathFontBold;
        copy.underneathFontItalic = underneathFontItalic;
        copy.underneathTextColor = underneathTextColor;
        copy.underneathTextXOffset = underneathTextXOffset;
        copy.underneathTextYOffset = underneathTextYOffset;
        copy.underneathTextWrap = underneathTextWrap;
        copy.underneathTextVisible = underneathTextVisible;
        copy.behindText = behindText;
        copy.behindFontFamily = behindFontFamily;
        copy.behindFontSize = behindFontSize;
        copy.behindFontBold = behindFontBold;
        copy.behindFontItalic = behindFontItalic;
        copy.behindTextColor = behindTextColor;
        copy.behindTextXOffset = behindTextXOffset;
        copy.behindTextYOffset = behindTextYOffset;
        copy.behindTextWrap = behindTextWrap;
        copy.behindTextVisible = behindTextVisible;
        copy.labelText = labelText;
        copy.labelTextWrap = labelTextWrap;
        copy.labelTextVisible = labelTextVisible;
        return copy;
    }
}
