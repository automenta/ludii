// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.w3c.dom.Element;
import org.w3c.dom.events.MutationEvent;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Node;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.gvt.RootGraphicsNode;
import org.w3c.dom.Document;
import org.apache.batik.dom.svg.SVGContext;

public class SVGDocumentBridge implements DocumentBridge, BridgeUpdateHandler, SVGContext
{
    protected Document document;
    protected RootGraphicsNode node;
    protected BridgeContext ctx;
    
    @Override
    public String getNamespaceURI() {
        return null;
    }
    
    @Override
    public String getLocalName() {
        return null;
    }
    
    @Override
    public Bridge getInstance() {
        return new SVGDocumentBridge();
    }
    
    @Override
    public RootGraphicsNode createGraphicsNode(final BridgeContext ctx, final Document doc) {
        final RootGraphicsNode gn = new RootGraphicsNode();
        this.document = doc;
        this.node = gn;
        this.ctx = ctx;
        ((SVGOMDocument)doc).setSVGContext(this);
        return gn;
    }
    
    @Override
    public void buildGraphicsNode(final BridgeContext ctx, final Document doc, final RootGraphicsNode node) {
        if (ctx.isDynamic()) {
            ctx.bind(doc, node);
        }
    }
    
    @Override
    public void handleDOMAttrModifiedEvent(final MutationEvent evt) {
    }
    
    @Override
    public void handleDOMNodeInsertedEvent(final MutationEvent evt) {
        if (evt.getTarget() instanceof Element) {
            final Element childElt = (Element)evt.getTarget();
            final GVTBuilder builder = this.ctx.getGVTBuilder();
            final GraphicsNode childNode = builder.build(this.ctx, childElt);
            if (childNode == null) {
                return;
            }
            this.node.add(childNode);
        }
    }
    
    @Override
    public void handleDOMNodeRemovedEvent(final MutationEvent evt) {
    }
    
    @Override
    public void handleDOMCharacterDataModified(final MutationEvent evt) {
    }
    
    @Override
    public void handleCSSEngineEvent(final CSSEngineEvent evt) {
    }
    
    @Override
    public void handleAnimatedAttributeChanged(final AnimatedLiveAttributeValue alav) {
    }
    
    @Override
    public void handleOtherAnimationChanged(final String type) {
    }
    
    @Override
    public void dispose() {
        ((SVGOMDocument)this.document).setSVGContext(null);
        this.ctx.unbind(this.document);
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
        return null;
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
    public AffineTransform getCTM() {
        return null;
    }
    
    @Override
    public AffineTransform getGlobalTransform() {
        return null;
    }
    
    @Override
    public float getViewportWidth() {
        return 0.0f;
    }
    
    @Override
    public float getViewportHeight() {
        return 0.0f;
    }
    
    @Override
    public float getFontSize() {
        return 0.0f;
    }
}
