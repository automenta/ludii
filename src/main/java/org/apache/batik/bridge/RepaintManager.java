// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.awt.geom.AffineTransform;
import java.util.List;
import org.apache.batik.ext.awt.geom.RectListManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.batik.gvt.renderer.ImageRenderer;

public class RepaintManager
{
    static final int COPY_OVERHEAD = 10000;
    static final int COPY_LINE_OVERHEAD = 10;
    protected ImageRenderer renderer;
    
    public RepaintManager(final ImageRenderer r) {
        this.renderer = r;
    }
    
    public Collection updateRendering(final Collection areas) throws InterruptedException {
        this.renderer.flush(areas);
        final List rects = new ArrayList(areas.size());
        final AffineTransform at = this.renderer.getTransform();
        for (final Object area : areas) {
            Shape s = (Shape)area;
            s = at.createTransformedShape(s);
            final Rectangle2D r2d = s.getBounds2D();
            final int x0 = (int)Math.floor(r2d.getX());
            final int y0 = (int)Math.floor(r2d.getY());
            final int x2 = (int)Math.ceil(r2d.getX() + r2d.getWidth());
            final int y2 = (int)Math.ceil(r2d.getY() + r2d.getHeight());
            final Rectangle r = new Rectangle(x0 - 1, y0 - 1, x2 - x0 + 3, y2 - y0 + 3);
            rects.add(r);
        }
        RectListManager devRLM = null;
        try {
            devRLM = new RectListManager(rects);
            devRLM.mergeRects(10000, 10);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.renderer.repaint(devRLM);
        return devRLM;
    }
    
    public void setupRenderer(final AffineTransform u2d, final boolean dbr, final Shape aoi, final int width, final int height) {
        this.renderer.setTransform(u2d);
        this.renderer.setDoubleBuffered(dbr);
        this.renderer.updateOffScreen(width, height);
        this.renderer.clearOffScreen();
    }
    
    public BufferedImage getOffScreen() {
        return this.renderer.getOffScreen();
    }
}
