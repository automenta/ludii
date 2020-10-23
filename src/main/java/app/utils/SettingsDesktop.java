// 
// Decompiled by Procyon v0.5.36
// 

package app.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SettingsDesktop
{
    public static int defaultWidth;
    public static int defaultHeight;
    public static int defaultX;
    public static int defaultY;
    public static boolean frameMaximised;
    public static boolean cursorTooltipDev;
    public static BufferedImage jumpingMoveSavedImage;
    public static int tabFontSize;
    public static int editorFontSize;
    public static String moveFormat;
    public static boolean darkMode;
    
    static {
        SettingsDesktop.defaultWidth = (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.75);
        SettingsDesktop.defaultHeight = (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.75);
        SettingsDesktop.defaultX = -1;
        SettingsDesktop.defaultY = -1;
        SettingsDesktop.frameMaximised = false;
        SettingsDesktop.cursorTooltipDev = false;
        SettingsDesktop.jumpingMoveSavedImage = null;
        SettingsDesktop.tabFontSize = 13;
        SettingsDesktop.editorFontSize = 13;
        SettingsDesktop.moveFormat = "Move";
        SettingsDesktop.darkMode = false;
    }
}
