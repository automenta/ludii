// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.Cursor;
import org.w3c.dom.events.Event;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.w3c.dom.svg.SVGUseElement;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.RenderingHints;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.events.EventTarget;
import org.apache.batik.dom.events.NodeEventTarget;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.svg.SVGTransformable;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SVGOMUseShadowRoot;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.anim.dom.SVGOMAnimatedLength;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMUseElement;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGUseElementBridge extends AbstractGraphicsNodeBridge
{
    protected ReferencedElementMutationListener l;
    protected BridgeContext subCtx;
    
    @Override
    public String getLocalName() {
        return "use";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGUseElementBridge();
    }
    
    @Override
    public GraphicsNode createGraphicsNode(final BridgeContext ctx, final Element e) {
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }
        final CompositeGraphicsNode gn = this.buildCompositeGraphicsNode(ctx, e, null);
        this.associateSVGContext(ctx, e, gn);
        return gn;
    }
    
    public CompositeGraphicsNode buildCompositeGraphicsNode(final BridgeContext ctx, final Element e, CompositeGraphicsNode gn) {
        final SVGOMUseElement ue = (SVGOMUseElement)e;
        final String uri = ue.getHref().getAnimVal();
        if (uri.length() == 0) {
            throw new BridgeException(ctx, e, "attribute.missing", new Object[] { "xlink:href" });
        }
        final Element refElement = ctx.getReferencedElement(e, uri);
        final SVGOMDocument document = (SVGOMDocument)e.getOwnerDocument();
        final SVGOMDocument refDocument = (SVGOMDocument)refElement.getOwnerDocument();
        final boolean isLocal = refDocument == document;
        BridgeContext theCtx = ctx;
        this.subCtx = null;
        if (!isLocal) {
            this.subCtx = (BridgeContext)refDocument.getCSSEngine().getCSSContext();
            theCtx = this.subCtx;
        }
        Element localRefElement = (Element)document.importNode(refElement, true, true);
        if ("symbol".equals(localRefElement.getLocalName())) {
            final Element svgElement = document.createElementNS("http://www.w3.org/2000/svg", "svg");
            final NamedNodeMap attrs = localRefElement.getAttributes();
            for (int len = attrs.getLength(), i = 0; i < len; ++i) {
                final Attr attr = (Attr)attrs.item(i);
                svgElement.setAttributeNS(attr.getNamespaceURI(), attr.getName(), attr.getValue());
            }
            for (Node n = localRefElement.getFirstChild(); n != null; n = localRefElement.getFirstChild()) {
                svgElement.appendChild(n);
            }
            localRefElement = svgElement;
        }
        if ("svg".equals(localRefElement.getLocalName())) {
            try {
                SVGOMAnimatedLength al = (SVGOMAnimatedLength)ue.getWidth();
                if (al.isSpecified()) {
                    localRefElement.setAttributeNS(null, "width", al.getAnimVal().getValueAsString());
                }
                al = (SVGOMAnimatedLength)ue.getHeight();
                if (al.isSpecified()) {
                    localRefElement.setAttributeNS(null, "height", al.getAnimVal().getValueAsString());
                }
            }
            catch (LiveAttributeException ex) {
                throw new BridgeException(ctx, ex);
            }
        }
        final SVGOMUseShadowRoot root = new SVGOMUseShadowRoot(document, e, isLocal);
        root.appendChild(localRefElement);
        if (gn == null) {
            gn = new CompositeGraphicsNode();
            this.associateSVGContext(ctx, e, this.node);
        }
        else {
            for (int s = gn.size(), j = 0; j < s; ++j) {
                gn.remove(0);
            }
        }
        final Node oldRoot = ue.getCSSFirstChild();
        if (oldRoot != null) {
            AbstractGraphicsNodeBridge.disposeTree(oldRoot);
        }
        ue.setUseShadowTree(root);
        final Element g = localRefElement;
        CSSUtilities.computeStyleAndURIs(refElement, localRefElement, uri);
        final GVTBuilder builder = ctx.getGVTBuilder();
        final GraphicsNode refNode = builder.build(ctx, g);
        gn.getChildren().add(refNode);
        gn.setTransform(this.computeTransform((SVGTransformable)e, ctx));
        gn.setVisible(CSSUtilities.convertVisibility(e));
        RenderingHints hints = null;
        hints = CSSUtilities.convertColorRendering(e, hints);
        if (hints != null) {
            gn.setRenderingHints(hints);
        }
        final Rectangle2D r = CSSUtilities.convertEnableBackground(e);
        if (r != null) {
            gn.setBackgroundEnable(r);
        }
        if (this.l != null) {
            final NodeEventTarget target = this.l.target;
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.l, true);
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.l, true);
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.l, true);
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", this.l, true);
            this.l = null;
        }
        if (isLocal && ctx.isDynamic()) {
            this.l = new ReferencedElementMutationListener();
            final NodeEventTarget target = (NodeEventTarget)refElement;
            (this.l.target = target).addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.l, true, null);
            theCtx.storeEventListenerNS(target, "http://www.w3.org/2001/xml-events", "DOMAttrModified", this.l, true);
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.l, true, null);
            theCtx.storeEventListenerNS(target, "http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.l, true);
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.l, true, null);
            theCtx.storeEventListenerNS(target, "http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.l, true);
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", this.l, true, null);
            theCtx.storeEventListenerNS(target, "http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", this.l, true);
        }
        return gn;
    }
    
    @Override
    public void dispose() {
        if (this.l != null) {
            final NodeEventTarget target = this.l.target;
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.l, true);
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.l, true);
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.l, true);
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", this.l, true);
            this.l = null;
        }
        final SVGOMUseElement ue = (SVGOMUseElement)this.e;
        if (ue != null && ue.getCSSFirstChild() != null) {
            AbstractGraphicsNodeBridge.disposeTree(ue.getCSSFirstChild());
        }
        super.dispose();
        this.subCtx = null;
    }
    
    @Override
    protected AffineTransform computeTransform(final SVGTransformable e, final BridgeContext ctx) {
        final AffineTransform at = super.computeTransform(e, ctx);
        final SVGUseElement ue = (SVGUseElement)e;
        try {
            final AbstractSVGAnimatedLength _x = (AbstractSVGAnimatedLength)ue.getX();
            final float x = _x.getCheckedValue();
            final AbstractSVGAnimatedLength _y = (AbstractSVGAnimatedLength)ue.getY();
            final float y = _y.getCheckedValue();
            final AffineTransform xy = AffineTransform.getTranslateInstance(x, y);
            xy.preConcatenate(at);
            return xy;
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }
    
    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return null;
    }
    
    @Override
    public boolean isComposite() {
        return false;
    }
    
    @Override
    public void buildGraphicsNode(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        super.buildGraphicsNode(ctx, e, node);
        if (ctx.isInteractive()) {
            final NodeEventTarget target = (NodeEventTarget)e;
            final EventListener l = new CursorMouseOverListener(ctx);
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", l, false, null);
            ctx.storeEventListenerNS(target, "http://www.w3.org/2001/xml-events", "mouseover", l, false);
        }
    }
    
    @Override
    public void handleAnimatedAttributeChanged(final AnimatedLiveAttributeValue alav) {
        try {
            final String ns = alav.getNamespaceURI();
            final String ln = alav.getLocalName();
            if (ns == null) {
                if (ln.equals("x") || ln.equals("y") || ln.equals("transform")) {
                    this.node.setTransform(this.computeTransform((SVGTransformable)this.e, this.ctx));
                    this.handleGeometryChanged();
                }
                else if (ln.equals("width") || ln.equals("height")) {
                    this.buildCompositeGraphicsNode(this.ctx, this.e, (CompositeGraphicsNode)this.node);
                }
            }
            else if (ns.equals("http://www.w3.org/1999/xlink") && ln.equals("href")) {
                this.buildCompositeGraphicsNode(this.ctx, this.e, (CompositeGraphicsNode)this.node);
            }
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(this.ctx, ex);
        }
        super.handleAnimatedAttributeChanged(alav);
    }
    
    public static class CursorMouseOverListener implements EventListener
    {
        protected BridgeContext ctx;
        
        public CursorMouseOverListener(final BridgeContext ctx) {
            this.ctx = ctx;
        }
        
        @Override
        public void handleEvent(final Event evt) {
            final Element currentTarget = (Element)evt.getCurrentTarget();
            if (!CSSUtilities.isAutoCursor(currentTarget)) {
                final Cursor cursor = CSSUtilities.convertCursor(currentTarget, this.ctx);
                if (cursor != null) {
                    this.ctx.getUserAgent().setSVGCursor(cursor);
                }
            }
        }
    }
    
    protected class ReferencedElementMutationListener implements EventListener
    {
        protected NodeEventTarget target;
        
        @Override
        public void handleEvent(final Event evt) {
            SVGUseElementBridge.this.buildCompositeGraphicsNode(SVGUseElementBridge.this.ctx, SVGUseElementBridge.this.e, (CompositeGraphicsNode)SVGUseElementBridge.this.node);
        }
    }
}
