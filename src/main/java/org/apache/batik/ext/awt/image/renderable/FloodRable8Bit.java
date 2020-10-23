// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.rendered.CachableRed;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.rendered.FloodRed;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.Paint;

public class FloodRable8Bit extends AbstractRable implements FloodRable
{
    Paint floodPaint;
    Rectangle2D floodRegion;
    
    public FloodRable8Bit(final Rectangle2D floodRegion, final Paint floodPaint) {
        this.setFloodPaint(floodPaint);
        this.setFloodRegion(floodRegion);
    }
    
    @Override
    public void setFloodPaint(final Paint paint) {
        this.touch();
        if (paint == null) {
            this.floodPaint = new Color(0, 0, 0, 0);
        }
        else {
            this.floodPaint = paint;
        }
    }
    
    @Override
    public Paint getFloodPaint() {
        return this.floodPaint;
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return (Rectangle2D)this.floodRegion.clone();
    }
    
    @Override
    public Rectangle2D getFloodRegion() {
        return (Rectangle2D)this.floodRegion.clone();
    }
    
    @Override
    public void setFloodRegion(final Rectangle2D floodRegion) {
        if (floodRegion == null) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.floodRegion = floodRegion;
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext rc) {
        AffineTransform usr2dev = rc.getTransform();
        if (usr2dev == null) {
            usr2dev = new AffineTransform();
        }
        final Rectangle2D imageRect = this.getBounds2D();
        Shape aoi = rc.getAreaOfInterest();
        Rectangle2D userAOI;
        if (aoi == null) {
            aoi = imageRect;
            userAOI = imageRect;
        }
        else {
            userAOI = aoi.getBounds2D();
            if (!imageRect.intersects(userAOI)) {
                return null;
            }
            Rectangle2D.intersect(imageRect, userAOI, userAOI);
        }
        final Rectangle renderedArea = usr2dev.createTransformedShape(userAOI).getBounds();
        if (renderedArea.width <= 0 || renderedArea.height <= 0) {
            return null;
        }
        CachableRed cr = new FloodRed(renderedArea, this.getFloodPaint());
        cr = new PadRed(cr, renderedArea, PadMode.ZERO_PAD, null);
        return cr;
    }
}
