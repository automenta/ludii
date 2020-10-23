// 
// Decompiled by Procyon v0.5.36
// 

package util;

import java.awt.*;

public class ArrowUtil
{
    public static void drawArrow(final Graphics2D g2d, final int startX, final int startY, final int endX, final int endY, final int lineWidth, final int headWidth, final int headHeight) {
        final int dx = endX - startX;
        final int dy = endY - startY;
        final double D = Math.sqrt(dx * dx + dy * dy);
        double xn;
        double xm = xn = D - headHeight;
        double ym = headWidth;
        double yn = -headWidth;
        final double sin = dy / D;
        final double cos = dx / D;
        double x = xm * cos - ym * sin + startX;
        ym = xm * sin + ym * cos + startY;
        xm = x;
        x = xn * cos - yn * sin + startX;
        yn = xn * sin + yn * cos + startY;
        xn = x;
        final int[] xpoints = { endX, (int)xm, (int)xn };
        final int[] ypoints = { endY, (int)ym, (int)yn };
        final Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke((float)lineWidth, 1, 0));
        g2d.drawLine(startX, startY, (int)((xm + xn) / 2.0), (int)((ym + yn) / 2.0));
        g2d.setStroke(oldStroke);
        g2d.fillPolygon(xpoints, ypoints, 3);
    }
}
