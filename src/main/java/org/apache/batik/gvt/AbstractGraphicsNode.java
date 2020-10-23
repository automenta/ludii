// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import java.awt.geom.Point2D;
import org.apache.batik.util.HaltingThread;
import java.util.Iterator;
import org.apache.batik.gvt.event.GraphicsNodeChangeListener;
import java.util.List;
import java.awt.image.renderable.RenderableImage;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.Shape;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import org.apache.batik.gvt.filter.GraphicsNodeRable8Bit;
import org.apache.batik.gvt.filter.GraphicsNodeRable;
import java.util.Map;
import java.awt.geom.NoninvertibleTransformException;
import org.apache.batik.gvt.event.GraphicsNodeChangeEvent;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.filter.Mask;
import java.awt.RenderingHints;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import java.awt.Composite;
import java.awt.geom.AffineTransform;
import javax.swing.event.EventListenerList;

public abstract class AbstractGraphicsNode implements GraphicsNode
{
    protected EventListenerList listeners;
    protected AffineTransform transform;
    protected AffineTransform inverseTransform;
    protected Composite composite;
    protected boolean isVisible;
    protected ClipRable clip;
    protected RenderingHints hints;
    protected CompositeGraphicsNode parent;
    protected RootGraphicsNode root;
    protected Mask mask;
    protected Filter filter;
    protected int pointerEventType;
    protected WeakReference graphicsNodeRable;
    protected WeakReference enableBackgroundGraphicsNodeRable;
    protected WeakReference weakRef;
    private Rectangle2D bounds;
    protected GraphicsNodeChangeEvent changeStartedEvent;
    protected GraphicsNodeChangeEvent changeCompletedEvent;
    static double EPSILON;
    
    protected AbstractGraphicsNode() {
        this.isVisible = true;
        this.pointerEventType = 0;
        this.changeStartedEvent = null;
        this.changeCompletedEvent = null;
    }
    
    @Override
    public WeakReference getWeakReference() {
        if (this.weakRef == null) {
            this.weakRef = new WeakReference((T)this);
        }
        return this.weakRef;
    }
    
    @Override
    public int getPointerEventType() {
        return this.pointerEventType;
    }
    
    @Override
    public void setPointerEventType(final int pointerEventType) {
        this.pointerEventType = pointerEventType;
    }
    
    @Override
    public void setTransform(final AffineTransform newTransform) {
        this.fireGraphicsNodeChangeStarted();
        this.transform = newTransform;
        Label_0056: {
            if (this.transform.getDeterminant() != 0.0) {
                try {
                    this.inverseTransform = this.transform.createInverse();
                    break Label_0056;
                }
                catch (NoninvertibleTransformException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            this.inverseTransform = this.transform;
        }
        if (this.parent != null) {
            this.parent.invalidateGeometryCache();
        }
        this.fireGraphicsNodeChangeCompleted();
    }
    
    @Override
    public AffineTransform getTransform() {
        return this.transform;
    }
    
    @Override
    public AffineTransform getInverseTransform() {
        return this.inverseTransform;
    }
    
    @Override
    public AffineTransform getGlobalTransform() {
        final AffineTransform ctm = new AffineTransform();
        for (GraphicsNode node = this; node != null; node = node.getParent()) {
            if (node.getTransform() != null) {
                ctm.preConcatenate(node.getTransform());
            }
        }
        return ctm;
    }
    
    @Override
    public void setComposite(final Composite newComposite) {
        this.fireGraphicsNodeChangeStarted();
        this.invalidateGeometryCache();
        this.composite = newComposite;
        this.fireGraphicsNodeChangeCompleted();
    }
    
    @Override
    public Composite getComposite() {
        return this.composite;
    }
    
    @Override
    public void setVisible(final boolean isVisible) {
        this.fireGraphicsNodeChangeStarted();
        this.isVisible = isVisible;
        this.invalidateGeometryCache();
        this.fireGraphicsNodeChangeCompleted();
    }
    
    @Override
    public boolean isVisible() {
        return this.isVisible;
    }
    
    @Override
    public void setClip(final ClipRable newClipper) {
        if (newClipper == null && this.clip == null) {
            return;
        }
        this.fireGraphicsNodeChangeStarted();
        this.invalidateGeometryCache();
        this.clip = newClipper;
        this.fireGraphicsNodeChangeCompleted();
    }
    
    @Override
    public ClipRable getClip() {
        return this.clip;
    }
    
    @Override
    public void setRenderingHint(final RenderingHints.Key key, final Object value) {
        this.fireGraphicsNodeChangeStarted();
        if (this.hints == null) {
            this.hints = new RenderingHints(key, value);
        }
        else {
            this.hints.put(key, value);
        }
        this.fireGraphicsNodeChangeCompleted();
    }
    
    @Override
    public void setRenderingHints(final Map hints) {
        this.fireGraphicsNodeChangeStarted();
        if (this.hints == null) {
            this.hints = new RenderingHints(hints);
        }
        else {
            this.hints.putAll(hints);
        }
        this.fireGraphicsNodeChangeCompleted();
    }
    
    @Override
    public void setRenderingHints(final RenderingHints newHints) {
        this.fireGraphicsNodeChangeStarted();
        this.hints = newHints;
        this.fireGraphicsNodeChangeCompleted();
    }
    
    @Override
    public RenderingHints getRenderingHints() {
        return this.hints;
    }
    
    @Override
    public void setMask(final Mask newMask) {
        if (newMask == null && this.mask == null) {
            return;
        }
        this.fireGraphicsNodeChangeStarted();
        this.invalidateGeometryCache();
        this.mask = newMask;
        this.fireGraphicsNodeChangeCompleted();
    }
    
    @Override
    public Mask getMask() {
        return this.mask;
    }
    
    @Override
    public void setFilter(final Filter newFilter) {
        if (newFilter == null && this.filter == null) {
            return;
        }
        this.fireGraphicsNodeChangeStarted();
        this.invalidateGeometryCache();
        this.filter = newFilter;
        this.fireGraphicsNodeChangeCompleted();
    }
    
    @Override
    public Filter getFilter() {
        return this.filter;
    }
    
    @Override
    public Filter getGraphicsNodeRable(final boolean createIfNeeded) {
        GraphicsNodeRable ret = null;
        if (this.graphicsNodeRable != null) {
            ret = (GraphicsNodeRable)this.graphicsNodeRable.get();
            if (ret != null) {
                return ret;
            }
        }
        if (createIfNeeded) {
            ret = new GraphicsNodeRable8Bit(this);
            this.graphicsNodeRable = new WeakReference((T)ret);
        }
        return ret;
    }
    
    @Override
    public Filter getEnableBackgroundGraphicsNodeRable(final boolean createIfNeeded) {
        GraphicsNodeRable ret = null;
        if (this.enableBackgroundGraphicsNodeRable != null) {
            ret = (GraphicsNodeRable)this.enableBackgroundGraphicsNodeRable.get();
            if (ret != null) {
                return ret;
            }
        }
        if (createIfNeeded) {
            ret = new GraphicsNodeRable8Bit(this);
            ret.setUsePrimitivePaint(false);
            this.enableBackgroundGraphicsNodeRable = new WeakReference((T)ret);
        }
        return ret;
    }
    
    @Override
    public void paint(Graphics2D g2d) {
        if (this.composite != null && this.composite instanceof AlphaComposite) {
            final AlphaComposite ac = (AlphaComposite)this.composite;
            if (ac.getAlpha() < 0.001) {
                return;
            }
        }
        final Rectangle2D bounds = this.getBounds();
        if (bounds == null) {
            return;
        }
        Composite defaultComposite = null;
        AffineTransform defaultTransform = null;
        RenderingHints defaultHints = null;
        Graphics2D baseG2d = null;
        if (this.clip != null) {
            baseG2d = g2d;
            g2d = (Graphics2D)g2d.create();
            if (this.hints != null) {
                g2d.addRenderingHints(this.hints);
            }
            if (this.transform != null) {
                g2d.transform(this.transform);
            }
            if (this.composite != null) {
                g2d.setComposite(this.composite);
            }
            g2d.clip(this.clip.getClipPath());
        }
        else {
            if (this.hints != null) {
                defaultHints = g2d.getRenderingHints();
                g2d.addRenderingHints(this.hints);
            }
            if (this.transform != null) {
                defaultTransform = g2d.getTransform();
                g2d.transform(this.transform);
            }
            if (this.composite != null) {
                defaultComposite = g2d.getComposite();
                g2d.setComposite(this.composite);
            }
        }
        final Shape curClip = g2d.getClip();
        g2d.setRenderingHint(RenderingHintsKeyExt.KEY_AREA_OF_INTEREST, curClip);
        boolean paintNeeded = true;
        final Shape g2dClip = curClip;
        if (g2dClip != null) {
            final Rectangle2D cb = g2dClip.getBounds2D();
            if (!bounds.intersects(cb.getX(), cb.getY(), cb.getWidth(), cb.getHeight())) {
                paintNeeded = false;
            }
        }
        if (paintNeeded) {
            boolean antialiasedClip = false;
            if (this.clip != null && this.clip.getUseAntialiasedClip()) {
                antialiasedClip = this.isAntialiasedClip(g2d.getTransform(), g2d.getRenderingHints(), this.clip.getClipPath());
            }
            boolean useOffscreen = this.isOffscreenBufferNeeded();
            useOffscreen |= antialiasedClip;
            if (!useOffscreen) {
                this.primitivePaint(g2d);
            }
            else {
                Filter filteredImage = null;
                if (this.filter == null) {
                    filteredImage = this.getGraphicsNodeRable(true);
                }
                else {
                    filteredImage = this.filter;
                }
                if (this.mask != null) {
                    if (this.mask.getSource() != filteredImage) {
                        this.mask.setSource(filteredImage);
                    }
                    filteredImage = this.mask;
                }
                if (this.clip != null && antialiasedClip) {
                    if (this.clip.getSource() != filteredImage) {
                        this.clip.setSource(filteredImage);
                    }
                    filteredImage = this.clip;
                }
                baseG2d = g2d;
                g2d = (Graphics2D)g2d.create();
                if (antialiasedClip) {
                    g2d.setClip(null);
                }
                final Rectangle2D filterBounds = filteredImage.getBounds2D();
                g2d.clip(filterBounds);
                GraphicsUtil.drawImage(g2d, filteredImage);
                g2d.dispose();
                g2d = baseG2d;
                baseG2d = null;
            }
        }
        if (baseG2d != null) {
            g2d.dispose();
        }
        else {
            if (defaultHints != null) {
                g2d.setRenderingHints(defaultHints);
            }
            if (defaultTransform != null) {
                g2d.setTransform(defaultTransform);
            }
            if (defaultComposite != null) {
                g2d.setComposite(defaultComposite);
            }
        }
    }
    
    private void traceFilter(final Filter filter, String prefix) {
        System.out.println(prefix + filter.getClass().getName());
        System.out.println(prefix + filter.getBounds2D());
        final List sources = filter.getSources();
        final int nSources = (sources != null) ? sources.size() : 0;
        prefix += "\t";
        for (int i = 0; i < nSources; ++i) {
            final Filter source = sources.get(i);
            this.traceFilter(source, prefix);
        }
        System.out.flush();
    }
    
    protected boolean isOffscreenBufferNeeded() {
        return this.filter != null || this.mask != null || (this.composite != null && !AlphaComposite.SrcOver.equals(this.composite));
    }
    
    protected boolean isAntialiasedClip(final AffineTransform usr2dev, final RenderingHints hints, final Shape clip) {
        if (clip == null) {
            return false;
        }
        final Object val = hints.get(RenderingHintsKeyExt.KEY_TRANSCODING);
        return val != "Printing" && val != "Vector" && (!(clip instanceof Rectangle2D) || usr2dev.getShearX() != 0.0 || usr2dev.getShearY() != 0.0);
    }
    
    public void fireGraphicsNodeChangeStarted(final GraphicsNode changeSrc) {
        if (this.changeStartedEvent == null) {
            this.changeStartedEvent = new GraphicsNodeChangeEvent(this, 9800);
        }
        this.changeStartedEvent.setChangeSrc(changeSrc);
        this.fireGraphicsNodeChangeStarted(this.changeStartedEvent);
        this.changeStartedEvent.setChangeSrc(null);
    }
    
    public void fireGraphicsNodeChangeStarted() {
        if (this.changeStartedEvent == null) {
            this.changeStartedEvent = new GraphicsNodeChangeEvent(this, 9800);
        }
        else {
            this.changeStartedEvent.setChangeSrc(null);
        }
        this.fireGraphicsNodeChangeStarted(this.changeStartedEvent);
    }
    
    public void fireGraphicsNodeChangeStarted(final GraphicsNodeChangeEvent changeStartedEvent) {
        final RootGraphicsNode rootGN = this.getRoot();
        if (rootGN == null) {
            return;
        }
        final List l = rootGN.getTreeGraphicsNodeChangeListeners();
        if (l == null) {
            return;
        }
        for (final GraphicsNodeChangeListener gncl : l) {
            gncl.changeStarted(changeStartedEvent);
        }
    }
    
    public void fireGraphicsNodeChangeCompleted() {
        if (this.changeCompletedEvent == null) {
            this.changeCompletedEvent = new GraphicsNodeChangeEvent(this, 9801);
        }
        final RootGraphicsNode rootGN = this.getRoot();
        if (rootGN == null) {
            return;
        }
        final List l = rootGN.getTreeGraphicsNodeChangeListeners();
        if (l == null) {
            return;
        }
        for (final GraphicsNodeChangeListener gncl : l) {
            gncl.changeCompleted(this.changeCompletedEvent);
        }
    }
    
    @Override
    public CompositeGraphicsNode getParent() {
        return this.parent;
    }
    
    @Override
    public RootGraphicsNode getRoot() {
        return this.root;
    }
    
    protected void setRoot(final RootGraphicsNode newRoot) {
        this.root = newRoot;
    }
    
    protected void setParent(final CompositeGraphicsNode newParent) {
        this.parent = newParent;
    }
    
    protected void invalidateGeometryCache() {
        if (this.parent != null) {
            this.parent.invalidateGeometryCache();
        }
        this.bounds = null;
    }
    
    @Override
    public Rectangle2D getBounds() {
        if (this.bounds == null) {
            if (this.filter == null) {
                this.bounds = this.getPrimitiveBounds();
            }
            else {
                this.bounds = this.filter.getBounds2D();
            }
            if (this.bounds != null) {
                if (this.clip != null) {
                    final Rectangle2D clipR = this.clip.getClipPath().getBounds2D();
                    if (clipR.intersects(this.bounds)) {
                        Rectangle2D.intersect(this.bounds, clipR, this.bounds);
                    }
                }
                if (this.mask != null) {
                    final Rectangle2D maskR = this.mask.getBounds2D();
                    if (maskR.intersects(this.bounds)) {
                        Rectangle2D.intersect(this.bounds, maskR, this.bounds);
                    }
                }
            }
            this.bounds = this.normalizeRectangle(this.bounds);
            if (HaltingThread.hasBeenHalted()) {
                this.invalidateGeometryCache();
            }
        }
        return this.bounds;
    }
    
    @Override
    public Rectangle2D getTransformedBounds(final AffineTransform txf) {
        AffineTransform t = txf;
        if (this.transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(this.transform);
        }
        Rectangle2D tBounds = null;
        if (this.filter == null) {
            tBounds = this.getTransformedPrimitiveBounds(txf);
        }
        else {
            tBounds = t.createTransformedShape(this.filter.getBounds2D()).getBounds2D();
        }
        if (tBounds != null) {
            if (this.clip != null) {
                Rectangle2D.intersect(tBounds, t.createTransformedShape(this.clip.getClipPath()).getBounds2D(), tBounds);
            }
            if (this.mask != null) {
                Rectangle2D.intersect(tBounds, t.createTransformedShape(this.mask.getBounds2D()).getBounds2D(), tBounds);
            }
        }
        return tBounds;
    }
    
    @Override
    public Rectangle2D getTransformedPrimitiveBounds(final AffineTransform txf) {
        final Rectangle2D tpBounds = this.getPrimitiveBounds();
        if (tpBounds == null) {
            return null;
        }
        AffineTransform t = txf;
        if (this.transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(this.transform);
        }
        return t.createTransformedShape(tpBounds).getBounds2D();
    }
    
    @Override
    public Rectangle2D getTransformedGeometryBounds(final AffineTransform txf) {
        final Rectangle2D tpBounds = this.getGeometryBounds();
        if (tpBounds == null) {
            return null;
        }
        AffineTransform t = txf;
        if (this.transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(this.transform);
        }
        return t.createTransformedShape(tpBounds).getBounds2D();
    }
    
    @Override
    public Rectangle2D getTransformedSensitiveBounds(final AffineTransform txf) {
        final Rectangle2D sBounds = this.getSensitiveBounds();
        if (sBounds == null) {
            return null;
        }
        AffineTransform t = txf;
        if (this.transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(this.transform);
        }
        return t.createTransformedShape(sBounds).getBounds2D();
    }
    
    @Override
    public boolean contains(final Point2D p) {
        final Rectangle2D b = this.getSensitiveBounds();
        if (b == null || !b.contains(p)) {
            return false;
        }
        switch (this.pointerEventType) {
            case 0:
            case 1:
            case 2:
            case 3: {
                return this.isVisible;
            }
            case 4:
            case 5:
            case 6:
            case 7: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean intersects(final Rectangle2D r) {
        final Rectangle2D b = this.getBounds();
        return b != null && b.intersects(r);
    }
    
    @Override
    public GraphicsNode nodeHitAt(final Point2D p) {
        return this.contains(p) ? this : null;
    }
    
    protected Rectangle2D normalizeRectangle(final Rectangle2D bounds) {
        if (bounds == null) {
            return null;
        }
        if (bounds.getWidth() < AbstractGraphicsNode.EPSILON) {
            if (bounds.getHeight() < AbstractGraphicsNode.EPSILON) {
                final AffineTransform gt = this.getGlobalTransform();
                final double det = Math.sqrt(gt.getDeterminant());
                return new Rectangle2D.Double(bounds.getX(), bounds.getY(), AbstractGraphicsNode.EPSILON / det, AbstractGraphicsNode.EPSILON / det);
            }
            double tmpW = bounds.getHeight() * AbstractGraphicsNode.EPSILON;
            if (tmpW < bounds.getWidth()) {
                tmpW = bounds.getWidth();
            }
            return new Rectangle2D.Double(bounds.getX(), bounds.getY(), tmpW, bounds.getHeight());
        }
        else {
            if (bounds.getHeight() < AbstractGraphicsNode.EPSILON) {
                double tmpH = bounds.getWidth() * AbstractGraphicsNode.EPSILON;
                if (tmpH < bounds.getHeight()) {
                    tmpH = bounds.getHeight();
                }
                return new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), tmpH);
            }
            return bounds;
        }
    }
    
    static {
        AbstractGraphicsNode.EPSILON = 1.0E-6;
    }
}
