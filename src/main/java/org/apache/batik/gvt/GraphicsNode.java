// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.filter.Mask;
import java.util.Map;
import java.awt.RenderingHints;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import java.awt.Composite;
import java.lang.ref.WeakReference;
import java.awt.geom.AffineTransform;

public interface GraphicsNode
{
    public static final int VISIBLE_PAINTED = 0;
    public static final int VISIBLE_FILL = 1;
    public static final int VISIBLE_STROKE = 2;
    public static final int VISIBLE = 3;
    public static final int PAINTED = 4;
    public static final int FILL = 5;
    public static final int STROKE = 6;
    public static final int ALL = 7;
    public static final int NONE = 8;
    public static final AffineTransform IDENTITY = new AffineTransform();
    
    WeakReference getWeakReference();
    
    int getPointerEventType();
    
    void setPointerEventType(final int p0);
    
    void setTransform(final AffineTransform p0);
    
    AffineTransform getTransform();
    
    AffineTransform getInverseTransform();
    
    AffineTransform getGlobalTransform();
    
    void setComposite(final Composite p0);
    
    Composite getComposite();
    
    void setVisible(final boolean p0);
    
    boolean isVisible();
    
    void setClip(final ClipRable p0);
    
    ClipRable getClip();
    
    void setRenderingHint(final RenderingHints.Key p0, final Object p1);
    
    void setRenderingHints(final Map p0);
    
    void setRenderingHints(final RenderingHints p0);
    
    RenderingHints getRenderingHints();
    
    void setMask(final Mask p0);
    
    Mask getMask();
    
    void setFilter(final Filter p0);
    
    Filter getFilter();
    
    Filter getGraphicsNodeRable(final boolean p0);
    
    Filter getEnableBackgroundGraphicsNodeRable(final boolean p0);
    
    void paint(final Graphics2D p0);
    
    void primitivePaint(final Graphics2D p0);
    
    CompositeGraphicsNode getParent();
    
    RootGraphicsNode getRoot();
    
    Rectangle2D getBounds();
    
    Rectangle2D getTransformedBounds(final AffineTransform p0);
    
    Rectangle2D getPrimitiveBounds();
    
    Rectangle2D getTransformedPrimitiveBounds(final AffineTransform p0);
    
    Rectangle2D getGeometryBounds();
    
    Rectangle2D getTransformedGeometryBounds(final AffineTransform p0);
    
    Rectangle2D getSensitiveBounds();
    
    Rectangle2D getTransformedSensitiveBounds(final AffineTransform p0);
    
    boolean contains(final Point2D p0);
    
    boolean intersects(final Rectangle2D p0);
    
    GraphicsNode nodeHitAt(final Point2D p0);
    
    Shape getOutline();
}
