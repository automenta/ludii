// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.HashSet;
import org.apache.batik.dom.svg.SVGContext;
import java.util.Set;
import java.util.Collection;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.gvt.ShapeNode;
import java.util.ArrayList;
import java.util.List;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.w3c.dom.Node;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.w3c.dom.svg.SVGRect;
import java.awt.RenderingHints;
import org.apache.batik.ext.awt.image.renderable.Filter;
import java.awt.Shape;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.apache.batik.anim.dom.SVGOMAnimatedRect;
import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.Element;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.dom.svg.SVGSVGContext;

public class SVGSVGElementBridge extends SVGGElementBridge implements SVGSVGContext
{
    @Override
    public String getLocalName() {
        return "svg";
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGSVGElementBridge();
    }
    
    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return new CanvasGraphicsNode();
    }
    
    @Override
    public GraphicsNode createGraphicsNode(final BridgeContext ctx, final Element e) {
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }
        final CanvasGraphicsNode cgn = (CanvasGraphicsNode)this.instantiateGraphicsNode();
        this.associateSVGContext(ctx, e, cgn);
        try {
            final SVGDocument doc = (SVGDocument)e.getOwnerDocument();
            final SVGOMSVGElement se = (SVGOMSVGElement)e;
            final boolean isOutermost = doc.getRootElement() == e;
            float x = 0.0f;
            float y = 0.0f;
            if (!isOutermost) {
                final AbstractSVGAnimatedLength _x = (AbstractSVGAnimatedLength)se.getX();
                x = _x.getCheckedValue();
                final AbstractSVGAnimatedLength _y = (AbstractSVGAnimatedLength)se.getY();
                y = _y.getCheckedValue();
            }
            final AbstractSVGAnimatedLength _width = (AbstractSVGAnimatedLength)se.getWidth();
            final float w = _width.getCheckedValue();
            final AbstractSVGAnimatedLength _height = (AbstractSVGAnimatedLength)se.getHeight();
            final float h = _height.getCheckedValue();
            cgn.setVisible(CSSUtilities.convertVisibility(e));
            final SVGOMAnimatedRect vb = (SVGOMAnimatedRect)se.getViewBox();
            final SVGAnimatedPreserveAspectRatio par = se.getPreserveAspectRatio();
            final AffineTransform viewingTransform = ViewBox.getPreserveAspectRatioTransform(e, vb, par, w, h, ctx);
            float actualWidth = w;
            float actualHeight = h;
            try {
                final AffineTransform vtInv = viewingTransform.createInverse();
                actualWidth = (float)(w * vtInv.getScaleX());
                actualHeight = (float)(h * vtInv.getScaleY());
            }
            catch (NoninvertibleTransformException ex2) {}
            final AffineTransform positionTransform = AffineTransform.getTranslateInstance(x, y);
            if (!isOutermost) {
                cgn.setPositionTransform(positionTransform);
            }
            else if (doc == ctx.getDocument()) {
                final double dw = w;
                final double dh = h;
                ctx.setDocumentSize(new Dimension2D() {
                    double w = dw;
                    double h = dh;
                    
                    @Override
                    public double getWidth() {
                        return this.w;
                    }
                    
                    @Override
                    public double getHeight() {
                        return this.h;
                    }
                    
                    @Override
                    public void setSize(final double w, final double h) {
                        this.w = w;
                        this.h = h;
                    }
                });
            }
            cgn.setViewingTransform(viewingTransform);
            Shape clip = null;
            if (CSSUtilities.convertOverflow(e)) {
                final float[] offsets = CSSUtilities.convertClip(e);
                if (offsets == null) {
                    clip = new Rectangle2D.Float(x, y, w, h);
                }
                else {
                    clip = new Rectangle2D.Float(x + offsets[3], y + offsets[0], w - offsets[1] - offsets[3], h - offsets[2] - offsets[0]);
                }
            }
            if (clip != null) {
                try {
                    AffineTransform at = new AffineTransform(positionTransform);
                    at.concatenate(viewingTransform);
                    at = at.createInverse();
                    clip = at.createTransformedShape(clip);
                    final Filter filter = cgn.getGraphicsNodeRable(true);
                    cgn.setClip(new ClipRable8Bit(filter, clip));
                }
                catch (NoninvertibleTransformException ex3) {}
            }
            RenderingHints hints = null;
            hints = CSSUtilities.convertColorRendering(e, hints);
            if (hints != null) {
                cgn.setRenderingHints(hints);
            }
            final Rectangle2D r = CSSUtilities.convertEnableBackground(e);
            if (r != null) {
                cgn.setBackgroundEnable(r);
            }
            if (vb.isSpecified()) {
                final SVGRect vbr = vb.getAnimVal();
                actualWidth = vbr.getWidth();
                actualHeight = vbr.getHeight();
            }
            ctx.openViewport(e, new SVGSVGElementViewport(actualWidth, actualHeight));
            return cgn;
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }
    
    @Override
    public void buildGraphicsNode(final BridgeContext ctx, final Element e, final GraphicsNode node) {
        node.setComposite(CSSUtilities.convertOpacity(e));
        node.setFilter(CSSUtilities.convertFilter(e, node, ctx));
        node.setMask(CSSUtilities.convertMask(e, node, ctx));
        node.setPointerEventType(CSSUtilities.convertPointerEvents(e));
        this.initializeDynamicSupport(ctx, e, node);
        ctx.closeViewport(e);
    }
    
    @Override
    public void dispose() {
        this.ctx.removeViewport(this.e);
        super.dispose();
    }
    
    @Override
    public void handleAnimatedAttributeChanged(final AnimatedLiveAttributeValue alav) {
        try {
            boolean rebuild = false;
            if (alav.getNamespaceURI() == null) {
                final String ln = alav.getLocalName();
                if (ln.equals("width") || ln.equals("height")) {
                    rebuild = true;
                }
                else if (ln.equals("x") || ln.equals("y")) {
                    final SVGDocument doc = (SVGDocument)this.e.getOwnerDocument();
                    final SVGOMSVGElement se = (SVGOMSVGElement)this.e;
                    final boolean isOutermost = doc.getRootElement() == this.e;
                    if (!isOutermost) {
                        final AbstractSVGAnimatedLength _x = (AbstractSVGAnimatedLength)se.getX();
                        final float x = _x.getCheckedValue();
                        final AbstractSVGAnimatedLength _y = (AbstractSVGAnimatedLength)se.getY();
                        final float y = _y.getCheckedValue();
                        final AffineTransform positionTransform = AffineTransform.getTranslateInstance(x, y);
                        final CanvasGraphicsNode cgn = (CanvasGraphicsNode)this.node;
                        cgn.setPositionTransform(positionTransform);
                        return;
                    }
                }
                else if (ln.equals("viewBox") || ln.equals("preserveAspectRatio")) {
                    final SVGDocument doc = (SVGDocument)this.e.getOwnerDocument();
                    final SVGOMSVGElement se = (SVGOMSVGElement)this.e;
                    final boolean isOutermost = doc.getRootElement() == this.e;
                    float x2 = 0.0f;
                    float y2 = 0.0f;
                    if (!isOutermost) {
                        final AbstractSVGAnimatedLength _x2 = (AbstractSVGAnimatedLength)se.getX();
                        x2 = _x2.getCheckedValue();
                        final AbstractSVGAnimatedLength _y2 = (AbstractSVGAnimatedLength)se.getY();
                        y2 = _y2.getCheckedValue();
                    }
                    final AbstractSVGAnimatedLength _width = (AbstractSVGAnimatedLength)se.getWidth();
                    final float w = _width.getCheckedValue();
                    final AbstractSVGAnimatedLength _height = (AbstractSVGAnimatedLength)se.getHeight();
                    final float h = _height.getCheckedValue();
                    final CanvasGraphicsNode cgn2 = (CanvasGraphicsNode)this.node;
                    final SVGOMAnimatedRect vb = (SVGOMAnimatedRect)se.getViewBox();
                    final SVGAnimatedPreserveAspectRatio par = se.getPreserveAspectRatio();
                    final AffineTransform newVT = ViewBox.getPreserveAspectRatioTransform(this.e, vb, par, w, h, this.ctx);
                    final AffineTransform oldVT = cgn2.getViewingTransform();
                    if (newVT.getScaleX() != oldVT.getScaleX() || newVT.getScaleY() != oldVT.getScaleY() || newVT.getShearX() != oldVT.getShearX() || newVT.getShearY() != oldVT.getShearY()) {
                        rebuild = true;
                    }
                    else {
                        cgn2.setViewingTransform(newVT);
                        Shape clip = null;
                        if (CSSUtilities.convertOverflow(this.e)) {
                            final float[] offsets = CSSUtilities.convertClip(this.e);
                            if (offsets == null) {
                                clip = new Rectangle2D.Float(x2, y2, w, h);
                            }
                            else {
                                clip = new Rectangle2D.Float(x2 + offsets[3], y2 + offsets[0], w - offsets[1] - offsets[3], h - offsets[2] - offsets[0]);
                            }
                        }
                        if (clip != null) {
                            try {
                                AffineTransform at = cgn2.getPositionTransform();
                                if (at == null) {
                                    at = new AffineTransform();
                                }
                                else {
                                    at = new AffineTransform(at);
                                }
                                at.concatenate(newVT);
                                at = at.createInverse();
                                clip = at.createTransformedShape(clip);
                                final Filter filter = cgn2.getGraphicsNodeRable(true);
                                cgn2.setClip(new ClipRable8Bit(filter, clip));
                            }
                            catch (NoninvertibleTransformException ex2) {}
                        }
                    }
                }
                if (rebuild) {
                    final CompositeGraphicsNode gn = this.node.getParent();
                    gn.remove(this.node);
                    AbstractGraphicsNodeBridge.disposeTree(this.e, false);
                    this.handleElementAdded(gn, this.e.getParentNode(), this.e);
                    return;
                }
            }
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(this.ctx, ex);
        }
        super.handleAnimatedAttributeChanged(alav);
    }
    
    @Override
    public List getIntersectionList(final SVGRect svgRect, Element end) {
        final List ret = new ArrayList();
        final Rectangle2D rect = new Rectangle2D.Float(svgRect.getX(), svgRect.getY(), svgRect.getWidth(), svgRect.getHeight());
        final GraphicsNode svgGN = this.ctx.getGraphicsNode(this.e);
        if (svgGN == null) {
            return ret;
        }
        final Rectangle2D svgBounds = svgGN.getSensitiveBounds();
        if (svgBounds == null) {
            return ret;
        }
        if (!rect.intersects(svgBounds)) {
            return ret;
        }
        final Element base = this.e;
        AffineTransform ati = svgGN.getGlobalTransform();
        try {
            ati = ati.createInverse();
        }
        catch (NoninvertibleTransformException ex) {}
        Node next;
        for (next = base.getFirstChild(); next != null && !(next instanceof Element); next = next.getNextSibling()) {}
        if (next == null) {
            return ret;
        }
        Element curr = (Element)next;
        Set ancestors = null;
        if (end != null) {
            ancestors = this.getAncestors(end, base);
            if (ancestors == null) {
                end = null;
            }
        }
        while (curr != null) {
            final String nsURI = curr.getNamespaceURI();
            final String tag = curr.getLocalName();
            final boolean isGroup = "http://www.w3.org/2000/svg".equals(nsURI) && ("g".equals(tag) || "svg".equals(tag) || "a".equals(tag));
            final GraphicsNode gn = this.ctx.getGraphicsNode(curr);
            if (gn == null) {
                if (ancestors != null && ancestors.contains(curr)) {
                    break;
                }
                curr = this.getNext(curr, base, end);
            }
            else {
                final AffineTransform at = gn.getGlobalTransform();
                Rectangle2D gnBounds = gn.getSensitiveBounds();
                at.preConcatenate(ati);
                if (gnBounds != null) {
                    gnBounds = at.createTransformedShape(gnBounds).getBounds2D();
                }
                if (gnBounds == null || !rect.intersects(gnBounds)) {
                    if (ancestors != null && ancestors.contains(curr)) {
                        break;
                    }
                    curr = this.getNext(curr, base, end);
                }
                else {
                    if (isGroup) {
                        for (next = curr.getFirstChild(); next != null && !(next instanceof Element); next = next.getNextSibling()) {}
                        if (next != null) {
                            curr = (Element)next;
                            continue;
                        }
                    }
                    else {
                        if (curr == end) {
                            break;
                        }
                        if ("http://www.w3.org/2000/svg".equals(nsURI) && "use".equals(tag) && rect.contains(gnBounds)) {
                            ret.add(curr);
                        }
                        if (gn instanceof ShapeNode) {
                            final ShapeNode sn = (ShapeNode)gn;
                            Shape sensitive = sn.getSensitiveArea();
                            if (sensitive != null) {
                                sensitive = at.createTransformedShape(sensitive);
                                if (sensitive.intersects(rect)) {
                                    ret.add(curr);
                                }
                            }
                        }
                        else if (gn instanceof TextNode) {
                            final SVGOMElement svgElem = (SVGOMElement)curr;
                            final SVGTextElementBridge txtBridge = (SVGTextElementBridge)svgElem.getSVGContext();
                            final Set elems = txtBridge.getTextIntersectionSet(at, rect);
                            if (ancestors != null && ancestors.contains(curr)) {
                                this.filterChildren(curr, end, elems, ret);
                            }
                            else {
                                ret.addAll(elems);
                            }
                        }
                        else {
                            ret.add(curr);
                        }
                    }
                    curr = this.getNext(curr, base, end);
                }
            }
        }
        return ret;
    }
    
    @Override
    public List getEnclosureList(final SVGRect svgRect, Element end) {
        final List ret = new ArrayList();
        final Rectangle2D rect = new Rectangle2D.Float(svgRect.getX(), svgRect.getY(), svgRect.getWidth(), svgRect.getHeight());
        final GraphicsNode svgGN = this.ctx.getGraphicsNode(this.e);
        if (svgGN == null) {
            return ret;
        }
        final Rectangle2D svgBounds = svgGN.getSensitiveBounds();
        if (svgBounds == null) {
            return ret;
        }
        if (!rect.intersects(svgBounds)) {
            return ret;
        }
        final Element base = this.e;
        AffineTransform ati = svgGN.getGlobalTransform();
        try {
            ati = ati.createInverse();
        }
        catch (NoninvertibleTransformException ex) {}
        Node next;
        for (next = base.getFirstChild(); next != null && !(next instanceof Element); next = next.getNextSibling()) {}
        if (next == null) {
            return ret;
        }
        Element curr = (Element)next;
        Set ancestors = null;
        if (end != null) {
            ancestors = this.getAncestors(end, base);
            if (ancestors == null) {
                end = null;
            }
        }
        while (curr != null) {
            final String nsURI = curr.getNamespaceURI();
            final String tag = curr.getLocalName();
            final boolean isGroup = "http://www.w3.org/2000/svg".equals(nsURI) && ("g".equals(tag) || "svg".equals(tag) || "a".equals(tag));
            final GraphicsNode gn = this.ctx.getGraphicsNode(curr);
            if (gn == null) {
                if (ancestors != null && ancestors.contains(curr)) {
                    break;
                }
                curr = this.getNext(curr, base, end);
            }
            else {
                final AffineTransform at = gn.getGlobalTransform();
                Rectangle2D gnBounds = gn.getSensitiveBounds();
                at.preConcatenate(ati);
                if (gnBounds != null) {
                    gnBounds = at.createTransformedShape(gnBounds).getBounds2D();
                }
                if (gnBounds == null || !rect.intersects(gnBounds)) {
                    if (ancestors != null && ancestors.contains(curr)) {
                        break;
                    }
                    curr = this.getNext(curr, base, end);
                }
                else {
                    if (isGroup) {
                        for (next = curr.getFirstChild(); next != null && !(next instanceof Element); next = next.getNextSibling()) {}
                        if (next != null) {
                            curr = (Element)next;
                            continue;
                        }
                    }
                    else {
                        if (curr == end) {
                            break;
                        }
                        if ("http://www.w3.org/2000/svg".equals(nsURI) && "use".equals(tag)) {
                            if (rect.contains(gnBounds)) {
                                ret.add(curr);
                            }
                        }
                        else if (gn instanceof TextNode) {
                            final SVGOMElement svgElem = (SVGOMElement)curr;
                            final SVGTextElementBridge txtBridge = (SVGTextElementBridge)svgElem.getSVGContext();
                            final Set elems = txtBridge.getTextEnclosureSet(at, rect);
                            if (ancestors != null && ancestors.contains(curr)) {
                                this.filterChildren(curr, end, elems, ret);
                            }
                            else {
                                ret.addAll(elems);
                            }
                        }
                        else if (rect.contains(gnBounds)) {
                            ret.add(curr);
                        }
                    }
                    curr = this.getNext(curr, base, end);
                }
            }
        }
        return ret;
    }
    
    @Override
    public boolean checkIntersection(final Element element, final SVGRect svgRect) {
        final GraphicsNode svgGN = this.ctx.getGraphicsNode(this.e);
        if (svgGN == null) {
            return false;
        }
        final Rectangle2D rect = new Rectangle2D.Float(svgRect.getX(), svgRect.getY(), svgRect.getWidth(), svgRect.getHeight());
        AffineTransform ati = svgGN.getGlobalTransform();
        try {
            ati = ati.createInverse();
        }
        catch (NoninvertibleTransformException ex) {}
        SVGContext svgctx = null;
        if (element instanceof SVGOMElement) {
            svgctx = ((SVGOMElement)element).getSVGContext();
            if (svgctx instanceof SVGTextElementBridge || svgctx instanceof SVGTextElementBridge.AbstractTextChildSVGContext) {
                return SVGTextElementBridge.getTextIntersection(this.ctx, element, ati, rect, true);
            }
        }
        Rectangle2D gnBounds = null;
        final GraphicsNode gn = this.ctx.getGraphicsNode(element);
        if (gn != null) {
            gnBounds = gn.getSensitiveBounds();
        }
        if (gnBounds == null) {
            return false;
        }
        final AffineTransform at = gn.getGlobalTransform();
        at.preConcatenate(ati);
        gnBounds = at.createTransformedShape(gnBounds).getBounds2D();
        if (!rect.intersects(gnBounds)) {
            return false;
        }
        if (!(gn instanceof ShapeNode)) {
            return true;
        }
        final ShapeNode sn = (ShapeNode)gn;
        Shape sensitive = sn.getSensitiveArea();
        if (sensitive == null) {
            return false;
        }
        sensitive = at.createTransformedShape(sensitive);
        return sensitive.intersects(rect);
    }
    
    @Override
    public boolean checkEnclosure(final Element element, final SVGRect svgRect) {
        GraphicsNode gn = this.ctx.getGraphicsNode(element);
        Rectangle2D gnBounds = null;
        SVGContext svgctx = null;
        if (element instanceof SVGOMElement) {
            svgctx = ((SVGOMElement)element).getSVGContext();
            if (svgctx instanceof SVGTextElementBridge || svgctx instanceof SVGTextElementBridge.AbstractTextChildSVGContext) {
                gnBounds = SVGTextElementBridge.getTextBounds(this.ctx, element, true);
                for (Element p = (Element)element.getParentNode(); p != null && gn == null; gn = this.ctx.getGraphicsNode(p), p = (Element)p.getParentNode()) {}
            }
            else if (gn != null) {
                gnBounds = gn.getSensitiveBounds();
            }
        }
        else if (gn != null) {
            gnBounds = gn.getSensitiveBounds();
        }
        if (gnBounds == null) {
            return false;
        }
        final GraphicsNode svgGN = this.ctx.getGraphicsNode(this.e);
        if (svgGN == null) {
            return false;
        }
        final Rectangle2D rect = new Rectangle2D.Float(svgRect.getX(), svgRect.getY(), svgRect.getWidth(), svgRect.getHeight());
        AffineTransform ati = svgGN.getGlobalTransform();
        try {
            ati = ati.createInverse();
        }
        catch (NoninvertibleTransformException ex) {}
        final AffineTransform at = gn.getGlobalTransform();
        at.preConcatenate(ati);
        gnBounds = at.createTransformedShape(gnBounds).getBounds2D();
        return rect.contains(gnBounds);
    }
    
    public boolean filterChildren(final Element curr, final Element end, final Set elems, final List ret) {
        for (Node child = curr.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element && this.filterChildren((Element)child, end, elems, ret)) {
                return true;
            }
        }
        if (curr == end) {
            return true;
        }
        if (elems.contains(curr)) {
            ret.add(curr);
        }
        return false;
    }
    
    protected Set getAncestors(final Element end, final Element base) {
        final Set ret = new HashSet();
        Element p = end;
        do {
            ret.add(p);
            p = (Element)p.getParentNode();
        } while (p != null && p != base);
        if (p == null) {
            return null;
        }
        return ret;
    }
    
    protected Element getNext(Element curr, final Element base, final Element end) {
        Node next;
        for (next = curr.getNextSibling(); next != null; next = next.getNextSibling()) {
            if (next instanceof Element) {
                break;
            }
        }
        while (next == null) {
            curr = (Element)curr.getParentNode();
            if (curr == end || curr == base) {
                next = null;
                break;
            }
            for (next = curr.getNextSibling(); next != null; next = next.getNextSibling()) {
                if (next instanceof Element) {
                    break;
                }
            }
        }
        return (Element)next;
    }
    
    @Override
    public void deselectAll() {
        this.ctx.getUserAgent().deselectAll();
    }
    
    @Override
    public int suspendRedraw(final int max_wait_milliseconds) {
        final UpdateManager um = this.ctx.getUpdateManager();
        if (um != null) {
            return um.addRedrawSuspension(max_wait_milliseconds);
        }
        return -1;
    }
    
    @Override
    public boolean unsuspendRedraw(final int suspend_handle_id) {
        final UpdateManager um = this.ctx.getUpdateManager();
        return um != null && um.releaseRedrawSuspension(suspend_handle_id);
    }
    
    @Override
    public void unsuspendRedrawAll() {
        final UpdateManager um = this.ctx.getUpdateManager();
        if (um != null) {
            um.releaseAllRedrawSuspension();
        }
    }
    
    @Override
    public void forceRedraw() {
        final UpdateManager um = this.ctx.getUpdateManager();
        if (um != null) {
            um.forceRepaint();
        }
    }
    
    @Override
    public void pauseAnimations() {
        this.ctx.getAnimationEngine().pause();
    }
    
    @Override
    public void unpauseAnimations() {
        this.ctx.getAnimationEngine().unpause();
    }
    
    @Override
    public boolean animationsPaused() {
        return this.ctx.getAnimationEngine().isPaused();
    }
    
    @Override
    public float getCurrentTime() {
        return this.ctx.getAnimationEngine().getCurrentTime();
    }
    
    @Override
    public void setCurrentTime(final float t) {
        this.ctx.getAnimationEngine().setCurrentTime(t);
    }
    
    public static class SVGSVGElementViewport implements Viewport
    {
        private float width;
        private float height;
        
        public SVGSVGElementViewport(final float w, final float h) {
            this.width = w;
            this.height = h;
        }
        
        @Override
        public float getWidth() {
            return this.width;
        }
        
        @Override
        public float getHeight() {
            return this.height;
        }
    }
}
