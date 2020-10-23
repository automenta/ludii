// 
// Decompiled by Procyon v0.5.36
// 

package app.display.dialogs.util;

import app.DesktopApp;
import manager.utils.SettingsManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class DialogUtil
{
    public static void initialiseDialog(final JDialog dialog, final String title, final Rectangle bounds) {
        dialog.setModal(true);
        sharedInitialisation(dialog, title, bounds);
    }
    
    public static void initialiseSingletonDialog(final JDialog dialog, final String title, final Rectangle bounds) {
        if (SettingsManager.openDialog != null) {
            SettingsManager.openDialog.setVisible(false);
            SettingsManager.openDialog.dispose();
        }
        sharedInitialisation(SettingsManager.openDialog = dialog, title, bounds);
    }
    
    private static void sharedInitialisation(final JDialog dialog, final String title, final Rectangle bounds) {
        try {
            final URL resource = DesktopApp.frame().getClass().getResource("/ludii-logo-100x100.png");
            final BufferedImage image = ImageIO.read(resource);
            dialog.setIconImage(image);
            dialog.setTitle(title);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        dialog.setDefaultCloseOperation(2);
        if (bounds == null) {
            dialog.setLocationRelativeTo(DesktopApp.frame());
        }
        else {
            dialog.setLocation(bounds.getLocation());
            if (bounds.width > 0 && bounds.height > 0) {
                dialog.setSize(bounds.width, bounds.height);
            }
        }
        dialog.setVisible(true);
    }
    
    public static String getWrappedText(final Graphics graphics, final AbstractButton button, final String str) {
        final String STR_NEWLINE = "<br />";
        final FontRenderContext fontRenderContext = new FontRenderContext(new AffineTransform(), true, true);
        if (str != null) {
            final String text = str.replaceAll("<html><center>", "").replaceAll("</center></html>", "");
            final int width = button.getWidth();
            final Rectangle2D stringBounds = button.getFont().getStringBounds(text, fontRenderContext);
            if (!str.contains("<br />") && width - 5 < stringBounds.getWidth()) {
                String newStr;
                if (str.contains(" ")) {
                    final int lastIndex = str.lastIndexOf(" ");
                    newStr = str.substring(0, lastIndex) + "<br />" + str.substring(lastIndex);
                }
                else {
                    final int strLength = str.length() / 3 * 2;
                    newStr = str.substring(0, strLength) + "<br />" + str.substring(strLength);
                }
                return newStr;
            }
        }
        return str;
    }
}
