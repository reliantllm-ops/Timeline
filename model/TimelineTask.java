package model;

import java.awt.Color;

public class TimelineTask {
    public String name, startDate, endDate;
    public String centerText = "";    // text displayed on the task bar
    public Color fillColor = null;    // null means use default
    public Color outlineColor = null; // null means use default (darker fill)
    public int outlineThickness = 2;  // default thickness
    public boolean bevelFill = false; // bevel effect on fill
    public int bevelDepth = 60;       // bevel intensity (0-100)
    public int bevelLightAngle = 135; // light angle in degrees (0-360, 135 = top-left)
    public int bevelHighlightOpacity = 80; // highlight opacity (0-255)
    public int bevelShadowOpacity = 60;    // shadow opacity (0-255)
    public String bevelStyle = "Inner Bevel";  // "Inner Bevel", "Outer Bevel", "Emboss", "Pillow Emboss"
    public String topBevel = "Circle";    // PowerPoint-style top bevel shape
    public String bottomBevel = "None";   // PowerPoint-style bottom bevel shape
    public String shadowType = "No Shadow"; // Shadow effect: No Shadow, Bottom Right, Bottom Left, Top Right, Top Left
    public int height = 25;           // default height
    public int yPosition = -1;        // Y position on timeline (-1 means auto-calculate)
    // Center text formatting properties
    public String fontFamily = "SansSerif";  // default font family
    public int fontSize = 11;         // default font size
    public boolean fontBold = false;  // default not bold
    public boolean fontItalic = false; // default not italic
    public Color textColor = Color.BLACK;    // default black
    public int centerTextXOffset = 0; // X offset from default position
    public int centerTextYOffset = 0; // Y offset from default position
    public boolean centerTextWrap = false; // wrap center text
    public boolean centerTextVisible = true; // center text visible
    // Front text properties (text in front of task bar)
    public String frontText = "";
    public String frontFontFamily = "SansSerif";
    public int frontFontSize = 10;
    public boolean frontFontBold = false;
    public boolean frontFontItalic = false;
    public Color frontTextColor = Color.BLACK;
    public int frontTextXOffset = 0;
    public int frontTextYOffset = 0;
    public boolean frontTextWrap = false; // wrap front text
    public boolean frontTextVisible = true; // front text visible
    // Above text properties (text above task bar)
    public String aboveText = "";
    public String aboveFontFamily = "SansSerif";
    public int aboveFontSize = 10;
    public boolean aboveFontBold = false;
    public boolean aboveFontItalic = false;
    public Color aboveTextColor = Color.BLACK;
    public int aboveTextXOffset = 0;
    public int aboveTextYOffset = 0;
    public boolean aboveTextWrap = false; // wrap above text
    public boolean aboveTextVisible = true; // above text visible
    // Underneath text properties (text below task bar)
    public String underneathText = "";
    public String underneathFontFamily = "SansSerif";
    public int underneathFontSize = 10;
    public boolean underneathFontBold = false;
    public boolean underneathFontItalic = false;
    public Color underneathTextColor = Color.BLACK;
    public int underneathTextXOffset = 0;
    public int underneathTextYOffset = 0;
    public boolean underneathTextWrap = false; // wrap underneath text
    public boolean underneathTextVisible = true; // underneath text visible
    // Behind text properties (text behind task bar)
    public String behindText = "";
    public String behindFontFamily = "SansSerif";
    public int behindFontSize = 10;
    public boolean behindFontBold = false;
    public boolean behindFontItalic = false;
    public Color behindTextColor = new Color(150, 150, 150);
    public int behindTextXOffset = 0;
    public int behindTextYOffset = 0;
    public boolean behindTextWrap = false; // wrap behind text
    public boolean behindTextVisible = true; // behind text visible
    // Notes
    public String note1 = "";
    public String note2 = "";
    public String note3 = "";
    public String note4 = "";
    public String note5 = "";

    public TimelineTask(String name, String startDate, String endDate) {
        this.name = name;
        this.centerText = ""; // leave center text blank by default
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public TimelineTask copy() {
        TimelineTask copy = new TimelineTask(name, startDate, endDate);
        copy.centerText = centerText;
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
        copy.height = height;
        copy.yPosition = yPosition;
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
        copy.note1 = note1;
        copy.note2 = note2;
        copy.note3 = note3;
        copy.note4 = note4;
        copy.note5 = note5;
        return copy;
    }
}
