// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Shape;
import java.awt.image.renderable.RenderContext;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.util.Vector;
import java.util.Map;
import java.awt.geom.Rectangle2D;

public class DeferRable implements Filter
{
    volatile Filter src;
    Rectangle2D bounds;
    Map props;
    
    public synchronized Filter getSource() {
        while (this.src == null) {
            try {
                this.wait();
            }
            catch (InterruptedException ie) {}
        }
        return this.src;
    }
    
    public synchronized void setSource(final Filter src) {
        if (this.src != null) {
            return;
        }
        this.src = src;
        this.bounds = src.getBounds2D();
        this.notifyAll();
    }
    
    public synchronized void setBounds(final Rectangle2D bounds) {
        if (this.bounds != null) {
            return;
        }
        this.bounds = bounds;
        this.notifyAll();
    }
    
    public synchronized void setProperties(final Map props) {
        this.props = props;
        this.notifyAll();
    }
    
    @Override
    public long getTimeStamp() {
        return this.getSource().getTimeStamp();
    }
    
    @Override
    public Vector getSources() {
        return this.getSource().getSources();
    }
    
    @Override
    public boolean isDynamic() {
        return this.getSource().isDynamic();
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        synchronized (this) {
            while (this.src == null && this.bounds == null) {
                try {
                    this.wait();
                }
                catch (InterruptedException ie) {}
            }
        }
        if (this.src != null) {
            return this.src.getBounds2D();
        }
        return this.bounds;
    }
    
    @Override
    public float getMinX() {
        return (float)this.getBounds2D().getX();
    }
    
    @Override
    public float getMinY() {
        return (float)this.getBounds2D().getY();
    }
    
    @Override
    public float getWidth() {
        return (float)this.getBounds2D().getWidth();
    }
    
    @Override
    public float getHeight() {
        return (float)this.getBounds2D().getHeight();
    }
    
    @Override
    public Object getProperty(final String name) {
        synchronized (this) {
            while (this.src == null && this.props == null) {
                try {
                    this.wait();
                }
                catch (InterruptedException ie) {}
            }
        }
        if (this.src != null) {
            return this.src.getProperty(name);
        }
        return this.props.get(name);
    }
    
    @Override
    public String[] getPropertyNames() {
        synchronized (this) {
            while (this.src == null && this.props == null) {
                try {
                    this.wait();
                }
                catch (InterruptedException ie) {}
            }
        }
        if (this.src != null) {
            return this.src.getPropertyNames();
        }
        final String[] ret = new String[this.props.size()];
        this.props.keySet().toArray(ret);
        return ret;
    }
    
    @Override
    public RenderedImage createDefaultRendering() {
        return this.getSource().createDefaultRendering();
    }
    
    @Override
    public RenderedImage createScaledRendering(final int w, final int h, final RenderingHints hints) {
        return this.getSource().createScaledRendering(w, h, hints);
    }
    
    @Override
    public RenderedImage createRendering(final RenderContext rc) {
        return this.getSource().createRendering(rc);
    }
    
    @Override
    public Shape getDependencyRegion(final int srcIndex, final Rectangle2D outputRgn) {
        return this.getSource().getDependencyRegion(srcIndex, outputRgn);
    }
    
    @Override
    public Shape getDirtyRegion(final int srcIndex, final Rectangle2D inputRgn) {
        return this.getSource().getDirtyRegion(srcIndex, inputRgn);
    }
}
