// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.w3c.dom.svg.SVGFitToViewBox;
import java.awt.Shape;
import org.apache.batik.ext.awt.geom.SegmentList;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;
import org.apache.batik.dom.events.AbstractEvent;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.Node;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.svg.SVGMotionAnimatableElement;
import org.apache.batik.dom.svg.AbstractSVGTransformList;
import org.apache.batik.anim.dom.SVGOMAnimatedTransformList;
import java.awt.geom.AffineTransform;
import org.w3c.dom.svg.SVGTransformable;
import org.w3c.dom.Element;
import java.awt.geom.Rectangle2D;
import java.lang.ref.SoftReference;
import org.apache.batik.parser.UnitProcessor;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.dom.svg.SVGContext;

public abstract class AbstractGraphicsNodeBridge extends AnimatableSVGBridge implements SVGContext, BridgeUpdateHandler, GraphicsNodeBridge, ErrorConstants
{
    protected GraphicsNode node;
    protected boolean isSVG12;
    protected UnitProcessor.Context unitContext;
    protected SoftReference bboxShape;
    protected Rectangle2D bbox;
    
    protected AbstractGraphicsNodeBridge() {
        this.bboxShape = null;
        this.bbox = null;
    }
    
    @Override
    public GraphicsNode createGraphicsNode(final BridgeContext ctx, final Element e) {
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }
        final GraphicsNode node = this.instantiateGraphicsNode();
        this.setTransform(node, e, ctx);
        node.setVisible(CSSUtilities.convertVisibility(e));
        this.associateSVGContext(ctx, e, node);
        return node;
    }
    
    protected abstract GraphicsNode instantiateGraphicsNode();
    
    @Override
    public void buildGraphicsNode(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        node.setComposite(CSSUtilities.convertOpacity(e));
        node.setFilter(CSSUtilities.convertFilter(e, node, ctx));
        node.setMask(CSSUtilities.convertMask(e, node, ctx));
        node.setClip(CSSUtilities.convertClipPath(e, node, ctx));
        node.setPointerEventType(CSSUtilities.convertPointerEvents(e));
        this.initializeDynamicSupport(ctx, e, node);
    }
    
    @Override
    public boolean getDisplay(final Element e) {
        return CSSUtilities.convertDisplay(e);
    }
    
    protected AffineTransform computeTransform(final SVGTransformable te, final BridgeContext ctx) {
        try {
            final AffineTransform at = new AffineTransform();
            final SVGOMAnimatedTransformList atl = (SVGOMAnimatedTransformList)te.getTransform();
            if (atl.isSpecified()) {
                atl.check();
                final AbstractSVGTransformList tl = (AbstractSVGTransformList)te.getTransform().getAnimVal();
                at.concatenate(tl.getAffineTransform());
            }
            if (this.e instanceof SVGMotionAnimatableElement) {
                final SVGMotionAnimatableElement mae = (SVGMotionAnimatableElement)this.e;
                final AffineTransform mat = mae.getMotionTransform();
                if (mat != null) {
                    at.concatenate(mat);
                }
            }
            return at;
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }
    
    protected void setTransform(final GraphicsNode n, final Element e, final BridgeContext ctx) {
        n.setTransform(this.computeTransform((SVGTransformable)e, ctx));
    }
    
    protected void associateSVGContext(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        this.e = e;
        this.node = node;
        this.ctx = ctx;
        this.unitContext = org.apache.batik.bridge.UnitProcessor.createContext(ctx, e);
        this.isSVG12 = ctx.isSVG12();
        ((SVGOMElement)e).setSVGContext(this);
    }
    
    protected void initializeDynamicSupport(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        if (ctx.isInteractive()) {
            ctx.bind(e, node);
        }
    }
    
    @Override
    public void handleDOMAttrModifiedEvent(final MutationEvent evt) {
    }
    
    protected void handleGeometryChanged() {
        this.node.setFilter(CSSUtilities.convertFilter(this.e, this.node, this.ctx));
        this.node.setMask(CSSUtilities.convertMask(this.e, this.node, this.ctx));
        this.node.setClip(CSSUtilities.convertClipPath(this.e, this.node, this.ctx));
        if (this.isSVG12) {
            if (!"use".equals(this.e.getLocalName())) {
                this.fireShapeChangeEvent();
            }
            this.fireBBoxChangeEvent();
        }
    }
    
    protected void fireShapeChangeEvent() {
        final DocumentEvent d = (DocumentEvent)this.e.getOwnerDocument();
        final AbstractEvent evt = (AbstractEvent)d.createEvent("SVGEvents");
        evt.initEventNS("http://www.w3.org/2000/svg", "shapechange", true, false);
        try {
            ((EventTarget)this.e).dispatchEvent(evt);
        }
        catch (RuntimeException ex) {
            this.ctx.getUserAgent().displayError(ex);
        }
    }
    
    @Override
    public void handleDOMNodeInsertedEvent(final MutationEvent evt) {
        if (evt.getTarget() instanceof Element) {
            final Element e2 = (Element)evt.getTarget();
            final Bridge b = this.ctx.getBridge(e2);
            if (b instanceof GenericBridge) {
                ((GenericBridge)b).handleElement(this.ctx, e2);
            }
        }
    }
    
    @Override
    public void handleDOMNodeRemovedEvent(final MutationEvent evt) {
        final Node parent = this.e.getParentNode();
        if (parent instanceof SVGOMElement) {
            final SVGContext bridge = ((SVGOMElement)parent).getSVGContext();
            if (bridge instanceof SVGSwitchElementBridge) {
                ((SVGSwitchElementBridge)bridge).handleChildElementRemoved(this.e);
                return;
            }
        }
        final CompositeGraphicsNode gn = this.node.getParent();
        gn.remove(this.node);
        disposeTree(this.e);
    }
    
    @Override
    public void handleDOMCharacterDataModified(final MutationEvent evt) {
    }
    
    @Override
    public void dispose() {
        final SVGOMElement elt = (SVGOMElement)this.e;
        elt.setSVGContext(null);
        this.ctx.unbind(this.e);
        this.bboxShape = null;
    }
    
    protected static void disposeTree(final Node node) {
        disposeTree(node, true);
    }
    
    protected static void disposeTree(final Node node, final boolean removeContext) {
        if (node instanceof SVGOMElement) {
            final SVGOMElement elt = (SVGOMElement)node;
            final SVGContext ctx = elt.getSVGContext();
            if (ctx instanceof BridgeUpdateHandler) {
                final BridgeUpdateHandler h = (BridgeUpdateHandler)ctx;
                if (removeContext) {
                    elt.setSVGContext(null);
                }
                h.dispose();
            }
        }
        for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling()) {
            disposeTree(n, removeContext);
        }
    }
    
    @Override
    public void handleCSSEngineEvent(final CSSEngineEvent evt) {
        try {
            final SVGCSSEngine eng = (SVGCSSEngine)evt.getSource();
            final int[] arr$;
            final int[] properties = arr$ = evt.getProperties();
            for (final int idx : arr$) {
                this.handleCSSPropertyChanged(idx);
                final String pn = eng.getPropertyName(idx);
                this.fireBaseAttributeListeners(pn);
            }
        }
        catch (Exception ex) {
            this.ctx.getUserAgent().displayError(ex);
        }
    }
    
    protected void handleCSSPropertyChanged(final int property) {
        switch (property) {
            case 57: {
                this.node.setVisible(CSSUtilities.convertVisibility(this.e));
                break;
            }
            case 38: {
                this.node.setComposite(CSSUtilities.convertOpacity(this.e));
                break;
            }
            case 18: {
                this.node.setFilter(CSSUtilities.convertFilter(this.e, this.node, this.ctx));
                break;
            }
            case 37: {
                this.node.setMask(CSSUtilities.convertMask(this.e, this.node, this.ctx));
                break;
            }
            case 3: {
                this.node.setClip(CSSUtilities.convertClipPath(this.e, this.node, this.ctx));
                break;
            }
            case 40: {
                this.node.setPointerEventType(CSSUtilities.convertPointerEvents(this.e));
                break;
            }
            case 12: {
                if (!this.getDisplay(this.e)) {
                    final CompositeGraphicsNode parent = this.node.getParent();
                    parent.remove(this.node);
                    disposeTree(this.e, false);
                    break;
                }
                break;
            }
        }
    }
    
    @Override
    public void handleAnimatedAttributeChanged(final AnimatedLiveAttributeValue alav) {
        if (alav.getNamespaceURI() == null && alav.getLocalName().equals("transform")) {
            this.setTransform(this.node, this.e, this.ctx);
            this.handleGeometryChanged();
        }
    }
    
    @Override
    public void handleOtherAnimationChanged(final String type) {
        if (type.equals("motion")) {
            this.setTransform(this.node, this.e, this.ctx);
            this.handleGeometryChanged();
        }
    }
    
    protected void checkBBoxChange() {
        if (this.e != null) {
            this.fireBBoxChangeEvent();
        }
    }
    
    protected void fireBBoxChangeEvent() {
        final DocumentEvent d = (DocumentEvent)this.e.getOwnerDocument();
        final AbstractEvent evt = (AbstractEvent)d.createEvent("SVGEvents");
        evt.initEventNS("http://www.w3.org/2000/svg", "RenderedBBoxChange", true, false);
        try {
            ((EventTarget)this.e).dispatchEvent(evt);
        }
        catch (RuntimeException ex) {
            this.ctx.getUserAgent().displayError(ex);
        }
    }
    
    @Override
    public float getPixelUnitToMillimeter() {
        return this.ctx.getUserAgent().getPixelUnitToMillimeter();
    }
    
    @Override
    public float getPixelToMM() {
        return this.getPixelUnitToMillimeter();
    }
    
    @Override
    public Rectangle2D getBBox() {
        if (this.node == null) {
            return null;
        }
        final Shape s = this.node.getOutline();
        if (this.bboxShape != null && s == this.bboxShape.get()) {
            return this.bbox;
        }
        this.bboxShape = new SoftReference((T)s);
        this.bbox = null;
        if (s == null) {
            return this.bbox;
        }
        final SegmentList sl = new SegmentList(s);
        return this.bbox = sl.getBounds2D();
    }
    
    @Override
    public AffineTransform getCTM() {
        GraphicsNode gn = this.node;
        final AffineTransform ctm = new AffineTransform();
        Element elt = this.e;
        while (elt != null) {
            if (elt instanceof SVGFitToViewBox) {
                AffineTransform at;
                if (gn instanceof CanvasGraphicsNode) {
                    at = ((CanvasGraphicsNode)gn).getViewingTransform();
                }
                else {
                    at = gn.getTransform();
                }
                if (at != null) {
                    ctm.preConcatenate(at);
                    break;
                }
                break;
            }
            else {
                final AffineTransform at = gn.getTransform();
                if (at != null) {
                    ctm.preConcatenate(at);
                }
                elt = CSSEngine.getParentCSSStylableElement(elt);
                gn = gn.getParent();
            }
        }
        return ctm;
    }
    
    @Override
    public AffineTransform getScreenTransform() {
        return this.ctx.getUserAgent().getTransform();
    }
    
    @Override
    public void setScreenTransform(final AffineTransform at) {
        this.ctx.getUserAgent().setTransform(at);
    }
    
    @Override
    public AffineTransform getGlobalTransform() {
        return this.node.getGlobalTransform();
    }
    
    @Override
    public float getViewportWidth() {
        return this.ctx.getBlockWidth(this.e);
    }
    
    @Override
    public float getViewportHeight() {
        return this.ctx.getBlockHeight(this.e);
    }
    
    @Override
    public float getFontSize() {
        return CSSUtilities.getComputedStyle(this.e, 22).getFloatValue();
    }
}
