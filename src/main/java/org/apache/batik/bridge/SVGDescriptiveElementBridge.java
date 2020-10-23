// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.w3c.dom.events.MutationEvent;
import org.apache.batik.anim.dom.SVGOMElement;
import org.w3c.dom.Element;
import org.apache.batik.dom.svg.SVGContext;

public abstract class SVGDescriptiveElementBridge extends AbstractSVGBridge implements GenericBridge, BridgeUpdateHandler, SVGContext
{
    Element theElt;
    Element parent;
    BridgeContext theCtx;
    
    @Override
    public void handleElement(final BridgeContext ctx, final Element e) {
        final UserAgent ua = ctx.getUserAgent();
        ua.handleElement(e, Boolean.TRUE);
        if (ctx.isDynamic()) {
            final SVGDescriptiveElementBridge b = (SVGDescriptiveElementBridge)this.getInstance();
            b.theElt = e;
            b.parent = (Element)e.getParentNode();
            b.theCtx = ctx;
            ((SVGOMElement)e).setSVGContext(b);
        }
    }
    
    @Override
    public void dispose() {
        final UserAgent ua = this.theCtx.getUserAgent();
        ((SVGOMElement)this.theElt).setSVGContext(null);
        ua.handleElement(this.theElt, this.parent);
        this.theElt = null;
        this.parent = null;
    }
    
    @Override
    public void handleDOMNodeInsertedEvent(final MutationEvent evt) {
        final UserAgent ua = this.theCtx.getUserAgent();
        ua.handleElement(this.theElt, Boolean.TRUE);
    }
    
    @Override
    public void handleDOMCharacterDataModified(final MutationEvent evt) {
        final UserAgent ua = this.theCtx.getUserAgent();
        ua.handleElement(this.theElt, Boolean.TRUE);
    }
    
    @Override
    public void handleDOMNodeRemovedEvent(final MutationEvent evt) {
        this.dispose();
    }
    
    @Override
    public void handleDOMAttrModifiedEvent(final MutationEvent evt) {
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
    public float getPixelUnitToMillimeter() {
        return this.theCtx.getUserAgent().getPixelUnitToMillimeter();
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
        return this.theCtx.getUserAgent().getTransform();
    }
    
    @Override
    public void setScreenTransform(final AffineTransform at) {
        this.theCtx.getUserAgent().setTransform(at);
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
        return this.theCtx.getBlockWidth(this.theElt);
    }
    
    @Override
    public float getViewportHeight() {
        return this.theCtx.getBlockHeight(this.theElt);
    }
    
    @Override
    public float getFontSize() {
        return 0.0f;
    }
}
