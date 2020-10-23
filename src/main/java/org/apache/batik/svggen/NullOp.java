// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.awt.image.ColorModel;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

class NullOp implements BufferedImageOp
{
    @Override
    public BufferedImage filter(final BufferedImage src, final BufferedImage dest) {
        final Graphics2D g = dest.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return dest;
    }
    
    @Override
    public Rectangle2D getBounds2D(final BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }
    
    @Override
    public BufferedImage createCompatibleDestImage(final BufferedImage src, ColorModel destCM) {
        BufferedImage dest = null;
        if (destCM == null) {
            destCM = src.getColorModel();
        }
        dest = new BufferedImage(destCM, destCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), destCM.isAlphaPremultiplied(), null);
        return dest;
    }
    
    @Override
    public Point2D getPoint2D(final Point2D srcPt, Point2D destPt) {
        if (destPt == null) {
            destPt = new Point2D.Double();
        }
        destPt.setLocation(srcPt.getX(), srcPt.getY());
        return destPt;
    }
    
    @Override
    public RenderingHints getRenderingHints() {
        return null;
    }
}
