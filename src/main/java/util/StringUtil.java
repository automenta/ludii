// 
// Decompiled by Procyon v0.5.36
// 

package util;

import topology.TopologyElement;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class StringUtil
{
    public static void drawStringAtPoint(final Graphics2D g2d, final String string, final Point2D drawPosn, final boolean withOutline) {
        final Rectangle2D rect = g2d.getFont().getStringBounds(string, g2d.getFontRenderContext());
        int posnX = 0;
        int posnY = 0;
        posnX = (int)(drawPosn.getX() - rect.getWidth() / 2.0);
        posnY = (int)(drawPosn.getY() + rect.getHeight() / 2.7);
        if (!withOutline) {
            g2d.drawString(string, posnX, posnY);
        }
        else {
            drawStringWithOutline(g2d, string, posnX, posnY);
        }
    }
    
    public static void drawStringAtSite(final Graphics2D g2d, final String string, final TopologyElement graphElement, final Point2D drawPosn, final boolean withOutline) {
        final Rectangle2D rect = g2d.getFont().getStringBounds(string, g2d.getFontRenderContext());
        int posnX = 0;
        int posnY = 0;
        if (graphElement.layer() > 1) {
            posnX = (int)(drawPosn.getX() - rect.getWidth() / 2.0 + graphElement.layer() / 2 * rect.getWidth() + 5.0);
            posnY = (int)(drawPosn.getY() + rect.getHeight() / 2.7);
        }
        else {
            posnX = (int)(drawPosn.getX() - rect.getWidth() / 2.0);
            posnY = (int)(drawPosn.getY() + rect.getHeight() / 2.7);
        }
        if (!withOutline) {
            g2d.drawString(string, posnX, posnY);
        }
        else {
            drawStringWithOutline(g2d, string, posnX, posnY);
        }
    }
    
    private static void drawStringWithOutline(final Graphics2D g2d, final String string, final int posnX, final int posnY) {
        final Color originalFontColour = g2d.getColor();
        final Graphics2D g2dNew = (Graphics2D)g2d.create();
        g2dNew.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2dNew.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2dNew.translate(posnX, posnY);
        g2dNew.setColor(Color.WHITE);
        final FontRenderContext frc = g2d.getFontRenderContext();
        final TextLayout tl = new TextLayout(string, g2d.getFont(), frc);
        final Shape shape = tl.getOutline(null);
        g2dNew.setStroke(new BasicStroke((float)(g2d.getFont().getSize() / 5)));
        g2dNew.draw(shape);
        g2dNew.draw(shape);
        g2dNew.setColor(originalFontColour);
        g2dNew.fill(shape);
    }
}
