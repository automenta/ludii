// 
// Decompiled by Procyon v0.5.36
// 

package graphics;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

public class ImageProcessing
{
    public static void floodFillDepth(final BufferedImage img, final int x, final int y, final int minWidth, final int minHeight, final int maxWidth, final int maxHeight, final int[] rgbaTarget, final int[] rgbaReplacement) {
        if (x < minWidth || y < minHeight || x >= maxWidth || y >= maxHeight) {
            return;
        }
        final WritableRaster raster = img.getRaster();
        final int[] rgba = { 0, 0, 0, 255 };
        raster.getPixel(x, y, rgba);
        if (rgba[0] == rgbaReplacement[0] && rgba[1] == rgbaReplacement[1] && rgba[2] == rgbaReplacement[2] && rgba[3] == rgbaReplacement[3]) {
            return;
        }
        if (rgba[0] == rgbaTarget[0] && rgba[1] == rgbaTarget[1] && rgba[2] == rgbaTarget[2] && rgba[3] == rgbaTarget[3]) {
            return;
        }
        raster.setPixel(x, y, rgbaReplacement);
        final int[][] off = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };
        for (int a = 0; a < 4; ++a) {
            floodFillDepth(img, x + off[a][0], y + off[a][1], minWidth, minHeight, maxWidth, maxHeight, rgbaTarget, rgbaReplacement);
        }
    }
    
    public static ArrayList<Point> floodFillBreadth(final BufferedImage img, final ArrayList<Point> pointsToCheck, final int x, final int y, final int width, final int height, final int[] rgbaTarget, final int[] rgbaReplacement) {
        final WritableRaster raster = img.getRaster();
        final int[] rgba = { 0, 0, 0, 255 };
        raster.getPixel(x, y, rgba);
        raster.setPixel(x, y, rgbaReplacement);
        pointsToCheck.remove(0);
        final int[][] off = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };
        for (int a = 0; a < 4; ++a) {
            if (x + off[a][0] >= 0 && y + off[a][1] >= 0 && x + off[a][0] < width) {
                if (y + off[a][1] < height) {
                    raster.getPixel(x + off[a][0], y + off[a][1], rgba);
                    if (rgba[0] != rgbaReplacement[0] || rgba[1] != rgbaReplacement[1] || rgba[2] != rgbaReplacement[2] || rgba[3] != rgbaReplacement[3]) {
                        if (rgba[0] != rgbaTarget[0] || rgba[1] != rgbaTarget[1] || rgba[2] != rgbaTarget[2] || rgba[3] != rgbaTarget[3]) {
                            boolean inPointsToCheck = false;
                            final Point pointToAdd = new Point(x + off[a][0], y + off[a][1]);
                            for (int i = 0; i < pointsToCheck.size(); ++i) {
                                if (pointToAdd.x == pointsToCheck.get(i).x && pointToAdd.y == pointsToCheck.get(i).y) {
                                    inPointsToCheck = true;
                                }
                            }
                            if (!inPointsToCheck) {
                                pointsToCheck.add(new Point(x + off[a][0], y + off[a][1]));
                            }
                        }
                    }
                }
            }
        }
        return pointsToCheck;
    }
    
    public static void contractImage(final BufferedImage img, final int width, final int height) {
        final int[][] off = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };
        final WritableRaster raster = img.getRaster();
        final int[] rgba = { 0, 0, 0, 255 };
        final int[] rgbaOff = { 0, 0, 0, 0 };
        final boolean[][] border = new boolean[width][height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                raster.getPixel(x, y, rgba);
                if (rgba[0] != 0 || rgba[1] != 0 || rgba[2] != 0 || rgba[3] != 0) {
                    for (int a = 0; a < 4; ++a) {
                        final int xx = x + off[a][0];
                        final int yy = y + off[a][1];
                        if (xx >= 0 && yy >= 0 && xx < width && yy < height) {
                            raster.getPixel(xx, yy, rgba);
                            if (rgba[0] == 0 && rgba[1] == 0 && rgba[2] == 0 && rgba[3] == 0) {
                                border[x][y] = true;
                            }
                        }
                    }
                }
            }
        }
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (border[x][y]) {
                    raster.setPixel(x, y, rgbaOff);
                }
            }
        }
    }
    
    public static void makeMask(final BufferedImage img, final int width, final int height, final int[] rgbaMask) {
        final WritableRaster raster = img.getRaster();
        final int[] rgba = { 0, 0, 0, 255 };
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                raster.getPixel(x, y, rgba);
                if (rgba[0] != 0 || rgba[1] != 0 || rgba[2] != 0 || rgba[3] != 0) {
                    raster.setPixel(x, y, rgbaMask);
                }
            }
        }
    }
    
    public static BufferedImage resize(final BufferedImage img, final int newW, final int newH) {
        final Image tmp = img.getScaledInstance(newW, newH, 4);
        final BufferedImage dimg = new BufferedImage(newW, newH, 2);
        final Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return dimg;
    }
    
    public static void ballImage(final Graphics2D g2d, final int x0, final int y0, final int r, final Color baseColour) {
        final float[] dist = { 0.0f, 0.25f, 0.4f, 1.0f };
        final Color[] colors = { Color.WHITE, baseColour, baseColour, Color.BLACK };
        RadialGradientPaint rgp = new RadialGradientPaint(new Point(x0 + r * 2 / 3, y0 + r * 2 / 3), (float)(r * 2), dist, colors);
        g2d.setPaint(rgp);
        g2d.fill(new Ellipse2D.Double(x0, y0, r * 2, r * 2));
        final float[] dist2 = { 0.0f, 0.35f, 1.0f };
        final Color[] colors2 = { new Color(0, 0, 0, 0), new Color(0, 0, 0, 0), Color.BLACK };
        rgp = new RadialGradientPaint(new Point(x0 + r, y0 + r), (float)(r * 2), dist2, colors2);
        g2d.setPaint(rgp);
        g2d.fill(new Ellipse2D.Double(x0, y0, r * 2, r * 2));
    }
    
    public static void ringImage(final Graphics2D g2d, final int x0, final int y0, final int imageSize, final Color baseColour) {
        final int r = (int)(0.425 * imageSize);
        final int off = (imageSize - 2 * r) / 2;
        final float swO = 0.15f * imageSize;
        final float swI = 0.075f * imageSize;
        final Shape circle = new Ellipse2D.Double(x0 + off, y0 + off, r * 2 - 1, r * 2 - 1);
        g2d.setStroke(new BasicStroke(swO, 1, 1));
        g2d.setColor(Color.black);
        g2d.draw(circle);
        g2d.setStroke(new BasicStroke(swI, 1, 1));
        g2d.setColor(baseColour);
        g2d.draw(circle);
    }
    
    public static void chocolateImage(final Graphics2D g2d, final int imageSize, final int numSides, final Color baseColour) {
        if (numSides != 4) {
            System.out.println("** Only four sided chocolate pieces supported.");
        }
        final int offO = (int)(0.125 * imageSize);
        final int offI = (int)(0.2 * imageSize);
        final Point[][] pts = new Point[4][2];
        g2d.setColor(baseColour);
        g2d.fillRect(0, 0, imageSize, imageSize);
        pts[0][0] = new Point(offO, imageSize - 1 - offO);
        pts[0][1] = new Point(offI, imageSize - 1 - offI);
        pts[1][0] = new Point(offO, offO);
        pts[1][1] = new Point(offI, offI);
        pts[2][0] = new Point(imageSize - 1 - offO, offO);
        pts[2][1] = new Point(imageSize - 1 - offI, offI);
        pts[3][0] = new Point(imageSize - 1 - offO, imageSize - 1 - offO);
        pts[3][1] = new Point(imageSize - 1 - offI, imageSize - 1 - offI);
        g2d.setColor(baseColour);
        g2d.fillRect(0, 0, imageSize, imageSize);
        GeneralPath path = new GeneralPath();
        path.moveTo((float)pts[0][0].x, (float)pts[0][0].y);
        path.lineTo((float)pts[1][0].x, (float)pts[1][0].y);
        path.lineTo((float)pts[2][0].x, (float)pts[2][0].y);
        path.lineTo((float)pts[2][1].x, (float)pts[2][1].y);
        path.lineTo((float)pts[1][1].x, (float)pts[1][1].y);
        path.lineTo((float)pts[0][1].x, (float)pts[0][1].y);
        path.closePath();
        g2d.setColor(new Color(255, 230, 200, 100));
        g2d.fill(path);
        path = new GeneralPath();
        path.moveTo((float)pts[0][0].x, (float)pts[0][0].y);
        path.lineTo((float)pts[3][0].x, (float)pts[3][0].y);
        path.lineTo((float)pts[2][0].x, (float)pts[2][0].y);
        path.lineTo((float)pts[2][1].x, (float)pts[2][1].y);
        path.lineTo((float)pts[3][1].x, (float)pts[3][1].y);
        path.lineTo((float)pts[0][1].x, (float)pts[0][1].y);
        path.closePath();
        g2d.setColor(new Color(50, 40, 20, 100));
        g2d.fill(path);
    }
    
    public static BufferedImage pillImage(final double pieceScale, final int dim, final int r, final Color baseColour) {
        final int diameter = r;
        final int sz_master = 2 * diameter;
        final int off = diameter / 2;
        final Color clr_off = new Color(0.0f, 0.0f, 0.0f, 0.0f);
        final Color clr_off_w = new Color(255, 255, 255, 1);
        final Color clr_on = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        final BufferedImage img_master = new BufferedImage(sz_master, sz_master, 2);
        final Graphics2D g2d_master = (Graphics2D)img_master.getGraphics();
        g2d_master.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d_master.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int r2 = 0;
        int g0 = 0;
        int b0 = 0;
        int r3 = 0;
        int g2 = 0;
        int b2 = 0;
        int r_hi = 0;
        int g_hi = 0;
        int b_hi = 0;
        final int blur_amount_hi = 2;
        r2 = Math.max(0, baseColour.getRed() - 75);
        g0 = Math.max(0, baseColour.getGreen() - 75);
        b0 = Math.max(0, baseColour.getBlue() - 75);
        r3 = baseColour.getRed();
        g2 = baseColour.getGreen();
        b2 = baseColour.getBlue();
        r_hi = Math.min(255, baseColour.getRed() + 255);
        g_hi = Math.min(255, baseColour.getGreen() + 250);
        b_hi = Math.min(255, baseColour.getBlue() + 240);
        final Color clr_hi = new Color(r_hi, g_hi, b_hi);
        final WritableRaster raster = img_master.getRaster();
        final int[] rgba = { 0, 0, 0, 255 };
        final int dr = r3 - r2;
        final int dg = g2 - g0;
        final int db = b2 - b0;
        final double radius = diameter / 2.0;
        final int cx = sz_master / 2 - 1;
        final int cy = sz_master / 2 - 1;
        for (int x = 0; x < sz_master; ++x) {
            for (int y = 0; y < sz_master; ++y) {
                final double dx = x - cx;
                final double dy = y - cy;
                final double dist = Math.sqrt(dx * dx + dy * dy) / radius;
                double t = 1.0 - dist;
                if (dist < 0.8) {
                    t = 1.0;
                }
                else {
                    t = 1.0 - (dist - 0.8) * 5.0;
                }
                t = Math.pow(t, 0.5);
                rgba[0] = r2 + (int)(dr * t + 0.5);
                rgba[1] = g0 + (int)(dg * t + 0.5);
                rgba[2] = b0 + (int)(db * t + 0.5);
                raster.setPixel(x, y, rgba);
            }
        }
        BufferedImage img_hi = new BufferedImage(sz_master, sz_master, 2);
        final Graphics2D g2d_hi = (Graphics2D)img_hi.getGraphics();
        g2d_hi.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d_hi.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d_hi.setPaint(clr_off_w);
        g2d_hi.fillRect(0, 0, sz_master, sz_master);
        g2d_hi.setPaint(clr_hi);
        final float rHi = 0.41f * diameter;
        final float rHole = 0.575f * diameter;
        final float offHole = 0.14f * diameter;
        final int x2 = cx;
        final int y2 = cy;
        final Shape hi = new Ellipse2D.Float(x2 - rHi, y2 - rHi, 2.0f * rHi, 2.0f * rHi);
        final Area areaHi = new Area(hi);
        final Shape hole = new Ellipse2D.Float(x2 - rHole + offHole, y2 - rHole + offHole, 2.0f * rHole, 2.0f * rHole);
        final Area areaHole = new Area(hole);
        areaHi.subtract(areaHole);
        g2d_hi.fill(areaHi);
        img_hi = Filters.gaussianBlurFilter(2, true).filter(img_hi, null);
        img_hi = Filters.gaussianBlurFilter(2, false).filter(img_hi, null);
        g2d_master.drawImage(img_hi, 0, 0, null);
        final BufferedImage img_mask = new BufferedImage(sz_master, sz_master, 2);
        final Graphics2D g2d_mask = (Graphics2D)img_mask.getGraphics();
        g2d_mask.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d_mask.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d_mask.setPaint(clr_off);
        g2d_mask.fillRect(0, 0, sz_master, sz_master);
        g2d_mask.setPaint(clr_on);
        g2d_mask.fillOval(off, off, diameter, diameter);
        g2d_master.setComposite(AlphaComposite.getInstance(6, 1.0f));
        g2d_master.drawImage(img_mask, 0, 0, null);
        final BufferedImage imgFinal = new BufferedImage(dim, dim, 2);
        final Graphics2D g2dFinal = (Graphics2D)imgFinal.getGraphics();
        g2dFinal.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2dFinal.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2dFinal.drawImage(img_master, (int)(0.0 + dim * (1.0 - pieceScale) / 2.0), (int)(0.0 + dim * (1.0 - pieceScale) / 2.0), (int)(diameter + dim * (1.0 - pieceScale) / 2.0), (int)(diameter + dim * (1.0 - pieceScale) / 2.0), off - 1, off - 1, off + diameter + 1, off + diameter + 1, null);
        return imgFinal;
    }
}
