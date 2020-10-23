// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Shape;
import java.util.Set;
import java.awt.image.renderable.RenderableImage;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.PadMode;
import java.awt.Rectangle;
import org.apache.batik.ext.awt.image.rendered.RenderedImageCachableRed;
import java.awt.image.renderable.RenderContext;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.util.Iterator;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public abstract class AbstractRable implements Filter
{
    protected Vector srcs;
    protected Map props;
    protected long stamp;
    
    protected AbstractRable() {
        this.props = new HashMap();
        this.stamp = 0L;
        this.srcs = new Vector();
    }
    
    protected AbstractRable(final Filter src) {
        this.props = new HashMap();
        this.stamp = 0L;
        this.init(src, null);
    }
    
    protected AbstractRable(final Filter src, final Map props) {
        this.props = new HashMap();
        this.stamp = 0L;
        this.init(src, props);
    }
    
    protected AbstractRable(final List srcs) {
        this(srcs, null);
    }
    
    protected AbstractRable(final List srcs, final Map props) {
        this.props = new HashMap();
        this.stamp = 0L;
        this.init(srcs, props);
    }
    
    public final void touch() {
        ++this.stamp;
    }
    
    @Override
    public long getTimeStamp() {
        return this.stamp;
    }
    
    protected void init(final Filter src) {
        this.touch();
        this.srcs = new Vector(1);
        if (src != null) {
            this.srcs.add(src);
        }
    }
    
    protected void init(final Filter src, final Map props) {
        this.init(src);
        if (props != null) {
            this.props.putAll(props);
        }
    }
    
    protected void init(final List srcs) {
        this.touch();
        this.srcs = new Vector(srcs);
    }
    
    protected void init(final List srcs, final Map props) {
        this.init(srcs);
        if (props != null) {
            this.props.putAll(props);
        }
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        Rectangle2D bounds = null;
        if (this.srcs.size() != 0) {
            final Iterator i = this.srcs.iterator();
            Filter src = i.next();
            bounds = (Rectangle2D)src.getBounds2D().clone();
            while (i.hasNext()) {
                src = i.next();
                final Rectangle2D r = src.getBounds2D();
                Rectangle2D.union(bounds, r, bounds);
            }
        }
        return bounds;
    }
    
    @Override
    public Vector getSources() {
        return this.srcs;
    }
    
    @Override
    public RenderedImage createDefaultRendering() {
        return this.createScaledRendering(100, 100, null);
    }
    
    @Override
    public RenderedImage createScaledRendering(final int w, final int h, final RenderingHints hints) {
        final float sX = w / this.getWidth();
        final float sY = h / this.getHeight();
        final float scale = Math.min(sX, sY);
        final AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
        final RenderContext rc = new RenderContext(at, hints);
        final float dX = this.getWidth() * scale - w;
        final float dY = this.getHeight() * scale - h;
        final RenderedImage ri = this.createRendering(rc);
        final CachableRed cr = RenderedImageCachableRed.wrap(ri);
        return new PadRed(cr, new Rectangle((int)(dX / 2.0f), (int)(dY / 2.0f), w, h), PadMode.ZERO_PAD, null);
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
        Object ret = this.props.get(name);
        if (ret != null) {
            return ret;
        }
        for (final Object src : this.srcs) {
            final RenderableImage ri = (RenderableImage)src;
            ret = ri.getProperty(name);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }
    
    @Override
    public String[] getPropertyNames() {
        final Set keys = this.props.keySet();
        Iterator iter = keys.iterator();
        String[] ret = new String[keys.size()];
        int i = 0;
        while (iter.hasNext()) {
            ret[i++] = iter.next();
        }
        iter = this.srcs.iterator();
        while (iter.hasNext()) {
            final RenderableImage ri = iter.next();
            final String[] srcProps = ri.getPropertyNames();
            if (srcProps.length != 0) {
                final String[] tmp = new String[ret.length + srcProps.length];
                System.arraycopy(ret, 0, tmp, 0, ret.length);
                System.arraycopy(tmp, ret.length, srcProps, 0, srcProps.length);
                ret = tmp;
            }
        }
        return ret;
    }
    
    @Override
    public boolean isDynamic() {
        return false;
    }
    
    @Override
    public Shape getDependencyRegion(final int srcIndex, final Rectangle2D outputRgn) {
        if (srcIndex < 0 || srcIndex > this.srcs.size()) {
            throw new IndexOutOfBoundsException("Nonexistant source requested.");
        }
        final Rectangle2D srect = (Rectangle2D)outputRgn.clone();
        final Rectangle2D bounds = this.getBounds2D();
        if (!bounds.intersects(srect)) {
            return new Rectangle2D.Float();
        }
        Rectangle2D.intersect(srect, bounds, srect);
        return srect;
    }
    
    @Override
    public Shape getDirtyRegion(final int srcIndex, final Rectangle2D inputRgn) {
        if (srcIndex < 0 || srcIndex > this.srcs.size()) {
            throw new IndexOutOfBoundsException("Nonexistant source requested.");
        }
        final Rectangle2D drect = (Rectangle2D)inputRgn.clone();
        final Rectangle2D bounds = this.getBounds2D();
        if (!bounds.intersects(drect)) {
            return new Rectangle2D.Float();
        }
        Rectangle2D.intersect(drect, bounds, drect);
        return drect;
    }
}
