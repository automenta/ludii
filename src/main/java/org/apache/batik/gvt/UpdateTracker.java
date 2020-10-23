// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import java.awt.Rectangle;
import org.apache.batik.gvt.event.GraphicsNodeChangeEvent;
import org.apache.batik.ext.awt.image.renderable.Filter;
import java.util.Iterator;
import java.util.Set;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import org.apache.batik.gvt.event.GraphicsNodeChangeAdapter;

public class UpdateTracker extends GraphicsNodeChangeAdapter
{
    Map dirtyNodes;
    Map fromBounds;
    protected static Rectangle2D NULL_RECT;
    
    public UpdateTracker() {
        this.dirtyNodes = null;
        this.fromBounds = new HashMap();
    }
    
    public boolean hasChanged() {
        return this.dirtyNodes != null;
    }
    
    public List getDirtyAreas() {
        if (this.dirtyNodes == null) {
            return null;
        }
        final List ret = new LinkedList();
        final Set keys = this.dirtyNodes.keySet();
        for (final Object key : keys) {
            WeakReference gnWRef = (WeakReference)key;
            GraphicsNode gn = (GraphicsNode)gnWRef.get();
            if (gn == null) {
                continue;
            }
            AffineTransform oat = this.dirtyNodes.get(gnWRef);
            if (oat != null) {
                oat = new AffineTransform(oat);
            }
            final Rectangle2D srcORgn = this.fromBounds.remove(gnWRef);
            Rectangle2D srcNRgn = null;
            AffineTransform nat = null;
            if (!(srcORgn instanceof ChngSrcRect)) {
                srcNRgn = gn.getBounds();
                nat = gn.getTransform();
                if (nat != null) {
                    nat = new AffineTransform(nat);
                }
            }
            while (true) {
                gn = gn.getParent();
                if (gn == null) {
                    break;
                }
                final Filter f = gn.getFilter();
                if (f != null) {
                    srcNRgn = f.getBounds2D();
                    nat = null;
                }
                final AffineTransform at = gn.getTransform();
                gnWRef = gn.getWeakReference();
                AffineTransform poat = this.dirtyNodes.get(gnWRef);
                if (poat == null) {
                    poat = at;
                }
                if (poat != null) {
                    if (oat != null) {
                        oat.preConcatenate(poat);
                    }
                    else {
                        oat = new AffineTransform(poat);
                    }
                }
                if (at == null) {
                    continue;
                }
                if (nat != null) {
                    nat.preConcatenate(at);
                }
                else {
                    nat = new AffineTransform(at);
                }
            }
            if (gn != null) {
                continue;
            }
            Shape oRgn = srcORgn;
            if (oRgn != null && oRgn != UpdateTracker.NULL_RECT) {
                if (oat != null) {
                    oRgn = oat.createTransformedShape(srcORgn);
                }
                ret.add(oRgn);
            }
            if (srcNRgn == null) {
                continue;
            }
            Shape nRgn = srcNRgn;
            if (nat != null) {
                nRgn = nat.createTransformedShape(srcNRgn);
            }
            if (nRgn == null) {
                continue;
            }
            ret.add(nRgn);
        }
        this.fromBounds.clear();
        this.dirtyNodes.clear();
        return ret;
    }
    
    public Rectangle2D getNodeDirtyRegion(final GraphicsNode gn, AffineTransform at) {
        final WeakReference gnWRef = gn.getWeakReference();
        AffineTransform nat = this.dirtyNodes.get(gnWRef);
        if (nat == null) {
            nat = gn.getTransform();
        }
        if (nat != null) {
            at = new AffineTransform(at);
            at.concatenate(nat);
        }
        final Filter f = gn.getFilter();
        Rectangle2D ret = null;
        if (gn instanceof CompositeGraphicsNode) {
            final CompositeGraphicsNode cgn = (CompositeGraphicsNode)gn;
            for (final Object aCgn : cgn) {
                final GraphicsNode childGN = (GraphicsNode)aCgn;
                final Rectangle2D r2d = this.getNodeDirtyRegion(childGN, at);
                if (r2d != null) {
                    if (f != null) {
                        final Shape s = at.createTransformedShape(f.getBounds2D());
                        ret = s.getBounds2D();
                        break;
                    }
                    if (ret == null || ret == UpdateTracker.NULL_RECT) {
                        ret = r2d;
                    }
                    else {
                        ret.add(r2d);
                    }
                }
            }
        }
        else {
            ret = this.fromBounds.remove(gnWRef);
            if (ret == null) {
                if (f != null) {
                    ret = f.getBounds2D();
                }
                else {
                    ret = gn.getBounds();
                }
            }
            else if (ret == UpdateTracker.NULL_RECT) {
                ret = null;
            }
            if (ret != null) {
                ret = at.createTransformedShape(ret).getBounds2D();
            }
        }
        return ret;
    }
    
    public Rectangle2D getNodeDirtyRegion(final GraphicsNode gn) {
        return this.getNodeDirtyRegion(gn, new AffineTransform());
    }
    
    @Override
    public void changeStarted(final GraphicsNodeChangeEvent gnce) {
        final GraphicsNode gn = gnce.getGraphicsNode();
        final WeakReference gnWRef = gn.getWeakReference();
        boolean doPut = false;
        if (this.dirtyNodes == null) {
            this.dirtyNodes = new HashMap();
            doPut = true;
        }
        else if (!this.dirtyNodes.containsKey(gnWRef)) {
            doPut = true;
        }
        if (doPut) {
            AffineTransform at = gn.getTransform();
            if (at != null) {
                at = (AffineTransform)at.clone();
            }
            else {
                at = new AffineTransform();
            }
            this.dirtyNodes.put(gnWRef, at);
        }
        final GraphicsNode chngSrc = gnce.getChangeSrc();
        Rectangle2D rgn = null;
        if (chngSrc != null) {
            final Rectangle2D drgn = this.getNodeDirtyRegion(chngSrc);
            if (drgn != null) {
                rgn = new ChngSrcRect(drgn);
            }
        }
        else {
            rgn = gn.getBounds();
        }
        Rectangle2D r2d = this.fromBounds.remove(gnWRef);
        if (rgn != null) {
            if (r2d != null && r2d != UpdateTracker.NULL_RECT) {
                r2d.add(rgn);
            }
            else {
                r2d = rgn;
            }
        }
        if (r2d == null) {
            r2d = UpdateTracker.NULL_RECT;
        }
        this.fromBounds.put(gnWRef, r2d);
    }
    
    public void clear() {
        this.dirtyNodes = null;
    }
    
    static {
        UpdateTracker.NULL_RECT = new Rectangle();
    }
    
    static class ChngSrcRect extends Float
    {
        ChngSrcRect(final Rectangle2D r2d) {
            super((float)r2d.getX(), (float)r2d.getY(), (float)r2d.getWidth(), (float)r2d.getHeight());
        }
    }
}
