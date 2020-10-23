// 
// Decompiled by Procyon v0.5.36
// 

package graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class UnicodeGraphics
{
    private static final int maxImageSize = 200;
    
    public static int maxImageSize() {
        return 200;
    }
    
    public static Font findFontForGlyph(final String glyph, final int fontSize) {
        final GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final String[] availableFontFamilyNames;
        final String[] envfonts = availableFontFamilyNames = gEnv.getAvailableFontFamilyNames();
        for (final String fontName : availableFontFamilyNames) {
            final Font font = new Font(fontName, 1, fontSize);
            if (font.canDisplayUpTo(glyph) == -1) {
                return font;
            }
        }
        return null;
    }
    
    public static BufferedImage floodFillPiece(final BufferedImage imgPieceSVG, final Color colour, final int width, final int height) {
        final Color fillColor = new Color(255, 250, 210);
        final int[][] rgbaMask = { new int[0], { 255, 250, 240, 255 }, { colour.getRed(), colour.getGreen(), colour.getBlue(), 255 } };
        final BufferedImage imgPiece = new BufferedImage(width, height, 2);
        final Graphics2D gfx = (Graphics2D)imgPiece.getGraphics();
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gfx.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        gfx.setPaint(fillColor);
        gfx.fillRect(0, 0, width, height);
        gfx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        gfx.setColor(new Color(0, 0, 0));
        gfx.drawImage(imgPieceSVG, null, 0, 0);
        final int[] rgbaTarget = { 0, 0, 0, 255 };
        final int[] rgbaReplacement = { 0, 0, 0, 0 };
        ArrayList<Point> pointsToCheck = new ArrayList<>();
        pointsToCheck.add(new Point(0, 0));
        while (pointsToCheck.size() > 0) {
            pointsToCheck = ImageProcessing.floodFillBreadth(imgPiece, pointsToCheck, pointsToCheck.get(0).x, pointsToCheck.get(0).y, width, height, rgbaTarget, rgbaReplacement);
        }
        ImageProcessing.makeMask(imgPiece, width, height, rgbaMask[2]);
        ImageProcessing.contractImage(imgPiece, width, height);
        gfx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gfx.drawImage(imgPieceSVG, 0, 0, null);
        return imgPiece;
    }
    
    public static BufferedImage createPieceImage(final int glyph, final char fallBack, final int who, final int width, final int height) {
        final Color fillColor = (who == 1) ? new Color(255, 250, 210) : new Color(127, 32, 0);
        final int[][] rgbaMask = { new int[0], { 255, 250, 240, 255 }, { 110, 32, 0, 255 } };
        final BufferedImage imgPiece = new BufferedImage(width, height, 2);
        final Graphics2D gfx = (Graphics2D)imgPiece.getGraphics();
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gfx.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        String display = String.valueOf(Character.toChars(glyph));
        display = display.concat(String.valueOf(Character.toChars(65038)));
        final int fontSize = (int)(0.9 * width);
        Font font = null;
        final Font preferredFont = new Font("Arial", 0, fontSize);
        if (preferredFont.canDisplayUpTo(display) != -1) {
            font = findFontForGlyph(display, fontSize);
        }
        else {
            font = preferredFont;
        }
        if (font == null) {
            font = preferredFont;
            display = String.valueOf(fallBack);
        }
        gfx.setFont(font);
        final int x = width / 2 - (int)(0.435 * width);
        final int y = height / 2 + (int)(0.275 * height);
        gfx.setPaint(fillColor);
        gfx.fillRect(0, 0, width, height);
        gfx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        gfx.setColor(new Color(0, 0, 0));
        gfx.drawString(display, x, y);
        final int[] rgbaTarget = { 0, 0, 0, 255 };
        final int[] rgbaReplacement = { 0, 0, 0, 0 };
        ArrayList<Point> pointsToCheck = new ArrayList<>();
        pointsToCheck.add(new Point(0, 0));
        while (pointsToCheck.size() > 0) {
            pointsToCheck = ImageProcessing.floodFillBreadth(imgPiece, pointsToCheck, pointsToCheck.get(0).x, pointsToCheck.get(0).y, width, height, rgbaTarget, rgbaReplacement);
        }
        ImageProcessing.makeMask(imgPiece, width, height, rgbaMask[who]);
        ImageProcessing.contractImage(imgPiece, width, height);
        gfx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gfx.drawString(display, x, y);
        return imgPiece;
    }
}
