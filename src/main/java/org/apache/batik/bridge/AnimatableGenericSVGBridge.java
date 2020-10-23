// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.w3c.dom.events.MutationEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.apache.batik.anim.dom.SVGOMElement;
import org.w3c.dom.Element;
import org.apache.batik.dom.svg.SVGContext;

public abstract class AnimatableGenericSVGBridge extends AnimatableSVGBridge implements GenericBridge, BridgeUpdateHandler, SVGContext
{
    @Override
    public void handleElement(final BridgeContext ctx, final Element e) {
        if (ctx.isDynamic()) {
            this.e = e;
            this.ctx = ctx;
            ((SVGOMElement)e).setSVGContext(this);
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
    
    @Override
    public void dispose() {
        ((SVGOMElement)this.e).setSVGContext(null);
    }
    
    @Override
    public void handleDOMNodeInsertedEvent(final MutationEvent evt) {
    }
    
    @Override
    public void handleDOMCharacterDataModified(final MutationEvent evt) {
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
}
