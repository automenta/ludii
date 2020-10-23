// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.events.EventTarget;
import java.util.Calendar;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGElement;
import org.apache.batik.anim.dom.AnimationTargetListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.w3c.dom.events.MutationEvent;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.dom.svg.SVGContext;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.dom.svg.SVGAnimationContext;

public abstract class SVGAnimationElementBridge extends AbstractSVGBridge implements GenericBridge, BridgeUpdateHandler, SVGAnimationContext, AnimatableElement
{
    protected SVGOMElement element;
    protected BridgeContext ctx;
    protected SVGAnimationEngine eng;
    protected TimedElement timedElement;
    protected AbstractAnimation animation;
    protected String attributeNamespaceURI;
    protected String attributeLocalName;
    protected short animationType;
    protected SVGOMElement targetElement;
    protected AnimationTarget animationTarget;
    
    public TimedElement getTimedElement() {
        return this.timedElement;
    }
    
    @Override
    public AnimatableValue getUnderlyingValue() {
        if (this.animationType == 0) {
            return this.animationTarget.getUnderlyingValue(this.attributeNamespaceURI, this.attributeLocalName);
        }
        return this.eng.getUnderlyingCSSValue(this.element, this.animationTarget, this.attributeLocalName);
    }
    
    @Override
    public void handleElement(final BridgeContext ctx, final Element e) {
        if (ctx.isDynamic() && BridgeContext.getSVGContext(e) == null) {
            final SVGAnimationElementBridge b = (SVGAnimationElementBridge)this.getInstance();
            b.element = (SVGOMElement)e;
            b.ctx = ctx;
            b.eng = ctx.getAnimationEngine();
            b.element.setSVGContext(b);
            if (b.eng.hasStarted()) {
                b.initializeAnimation();
                b.initializeTimedElement();
            }
            else {
                b.eng.addInitialBridge(b);
            }
        }
    }
    
    protected void initializeAnimation() {
        final String uri = XLinkSupport.getXLinkHref(this.element);
        Node t;
        if (uri.length() == 0) {
            t = this.element.getParentNode();
        }
        else {
            t = this.ctx.getReferencedElement(this.element, uri);
            if (t.getOwnerDocument() != this.element.getOwnerDocument()) {
                throw new BridgeException(this.ctx, this.element, "uri.badTarget", new Object[] { uri });
            }
        }
        this.animationTarget = null;
        if (t instanceof SVGOMElement) {
            this.targetElement = (SVGOMElement)t;
            this.animationTarget = this.targetElement;
        }
        if (this.animationTarget == null) {
            throw new BridgeException(this.ctx, this.element, "uri.badTarget", new Object[] { uri });
        }
        final String an = this.element.getAttributeNS(null, "attributeName");
        final int ci = an.indexOf(58);
        if (ci == -1) {
            if (this.element.hasProperty(an)) {
                this.animationType = 1;
                this.attributeLocalName = an;
            }
            else {
                this.animationType = 0;
                this.attributeLocalName = an;
            }
        }
        else {
            this.animationType = 0;
            final String prefix = an.substring(0, ci);
            this.attributeNamespaceURI = this.element.lookupNamespaceURI(prefix);
            this.attributeLocalName = an.substring(ci + 1);
        }
        if ((this.animationType == 1 && !this.targetElement.isPropertyAnimatable(this.attributeLocalName)) || (this.animationType == 0 && !this.targetElement.isAttributeAnimatable(this.attributeNamespaceURI, this.attributeLocalName))) {
            throw new BridgeException(this.ctx, this.element, "attribute.not.animatable", new Object[] { this.targetElement.getNodeName(), an });
        }
        int type;
        if (this.animationType == 1) {
            type = this.targetElement.getPropertyType(this.attributeLocalName);
        }
        else {
            type = this.targetElement.getAttributeType(this.attributeNamespaceURI, this.attributeLocalName);
        }
        if (!this.canAnimateType(type)) {
            throw new BridgeException(this.ctx, this.element, "type.not.animatable", new Object[] { this.targetElement.getNodeName(), an, this.element.getNodeName() });
        }
        this.timedElement = this.createTimedElement();
        this.animation = this.createAnimation(this.animationTarget);
        this.eng.addAnimation(this.animationTarget, this.animationType, this.attributeNamespaceURI, this.attributeLocalName, this.animation);
    }
    
    protected abstract boolean canAnimateType(final int p0);
    
    protected boolean checkValueType(final AnimatableValue v) {
        return true;
    }
    
    protected void initializeTimedElement() {
        this.initializeTimedElement(this.timedElement);
        this.timedElement.initialize();
    }
    
    protected TimedElement createTimedElement() {
        return new SVGTimedElement();
    }
    
    protected abstract AbstractAnimation createAnimation(final AnimationTarget p0);
    
    protected AnimatableValue parseAnimatableValue(final String an) {
        if (!this.element.hasAttributeNS(null, an)) {
            return null;
        }
        final String s = this.element.getAttributeNS(null, an);
        final AnimatableValue val = this.eng.parseAnimatableValue(this.element, this.animationTarget, this.attributeNamespaceURI, this.attributeLocalName, this.animationType == 1, s);
        if (!this.checkValueType(val)) {
            throw new BridgeException(this.ctx, this.element, "attribute.malformed", new Object[] { an, s });
        }
        return val;
    }
    
    protected void initializeTimedElement(final TimedElement timedElement) {
        timedElement.parseAttributes(this.element.getAttributeNS(null, "begin"), this.element.getAttributeNS(null, "dur"), this.element.getAttributeNS(null, "end"), this.element.getAttributeNS(null, "min"), this.element.getAttributeNS(null, "max"), this.element.getAttributeNS(null, "repeatCount"), this.element.getAttributeNS(null, "repeatDur"), this.element.getAttributeNS(null, "fill"), this.element.getAttributeNS(null, "restart"));
    }
    
    @Override
    public void handleDOMAttrModifiedEvent(final MutationEvent evt) {
    }
    
    @Override
    public void handleDOMNodeInsertedEvent(final MutationEvent evt) {
    }
    
    @Override
    public void handleDOMNodeRemovedEvent(final MutationEvent evt) {
        this.element.setSVGContext(null);
        this.dispose();
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
        if (this.element.getSVGContext() == null) {
            this.eng.removeAnimation(this.animation);
            this.timedElement.deinitialize();
            this.timedElement = null;
            this.element = null;
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
        return this.ctx.getBlockWidth(this.element);
    }
    
    @Override
    public float getViewportHeight() {
        return this.ctx.getBlockHeight(this.element);
    }
    
    @Override
    public float getFontSize() {
        return 0.0f;
    }
    
    public float svgToUserSpace(final float v, final int type, final int pcInterp) {
        return 0.0f;
    }
    
    public void addTargetListener(final String pn, final AnimationTargetListener l) {
    }
    
    public void removeTargetListener(final String pn, final AnimationTargetListener l) {
    }
    
    @Override
    public SVGElement getTargetElement() {
        return this.targetElement;
    }
    
    @Override
    public float getStartTime() {
        return this.timedElement.getCurrentBeginTime();
    }
    
    @Override
    public float getCurrentTime() {
        return this.timedElement.getLastSampleTime();
    }
    
    @Override
    public float getSimpleDuration() {
        return this.timedElement.getSimpleDur();
    }
    
    @Override
    public float getHyperlinkBeginTime() {
        return this.timedElement.getHyperlinkBeginTime();
    }
    
    @Override
    public boolean beginElement() throws DOMException {
        this.timedElement.beginElement();
        return this.timedElement.canBegin();
    }
    
    @Override
    public boolean beginElementAt(final float offset) throws DOMException {
        this.timedElement.beginElement(offset);
        return true;
    }
    
    @Override
    public boolean endElement() throws DOMException {
        this.timedElement.endElement();
        return this.timedElement.canEnd();
    }
    
    @Override
    public boolean endElementAt(final float offset) throws DOMException {
        this.timedElement.endElement(offset);
        return true;
    }
    
    protected boolean isConstantAnimation() {
        return false;
    }
    
    protected class SVGTimedElement extends TimedElement
    {
        @Override
        public Element getElement() {
            return SVGAnimationElementBridge.this.element;
        }
        
        @Override
        protected void fireTimeEvent(final String eventType, final Calendar time, final int detail) {
            AnimationSupport.fireTimeEvent(SVGAnimationElementBridge.this.element, eventType, time, detail);
        }
        
        @Override
        protected void toActive(final float begin) {
            SVGAnimationElementBridge.this.eng.toActive(SVGAnimationElementBridge.this.animation, begin);
        }
        
        @Override
        protected void toInactive(final boolean stillActive, final boolean isFrozen) {
            SVGAnimationElementBridge.this.eng.toInactive(SVGAnimationElementBridge.this.animation, isFrozen);
        }
        
        @Override
        protected void removeFill() {
            SVGAnimationElementBridge.this.eng.removeFill(SVGAnimationElementBridge.this.animation);
        }
        
        @Override
        protected void sampledAt(final float simpleTime, final float simpleDur, final int repeatIteration) {
            SVGAnimationElementBridge.this.eng.sampledAt(SVGAnimationElementBridge.this.animation, simpleTime, simpleDur, repeatIteration);
        }
        
        @Override
        protected void sampledLastValue(final int repeatIteration) {
            SVGAnimationElementBridge.this.eng.sampledLastValue(SVGAnimationElementBridge.this.animation, repeatIteration);
        }
        
        @Override
        protected TimedElement getTimedElementById(final String id) {
            return AnimationSupport.getTimedElementById(id, SVGAnimationElementBridge.this.element);
        }
        
        @Override
        protected EventTarget getEventTargetById(final String id) {
            return AnimationSupport.getEventTargetById(id, SVGAnimationElementBridge.this.element);
        }
        
        @Override
        protected EventTarget getRootEventTarget() {
            return (EventTarget)SVGAnimationElementBridge.this.element.getOwnerDocument();
        }
        
        @Override
        protected EventTarget getAnimationEventTarget() {
            return SVGAnimationElementBridge.this.targetElement;
        }
        
        @Override
        public boolean isBefore(final TimedElement other) {
            final Element e = other.getElement();
            final int pos = SVGAnimationElementBridge.this.element.compareDocumentPosition(e);
            return (pos & 0x2) != 0x0;
        }
        
        @Override
        public String toString() {
            if (SVGAnimationElementBridge.this.element != null) {
                final String id = SVGAnimationElementBridge.this.element.getAttributeNS(null, "id");
                if (id.length() != 0) {
                    return id;
                }
            }
            return super.toString();
        }
        
        @Override
        protected boolean isConstantAnimation() {
            return SVGAnimationElementBridge.this.isConstantAnimation();
        }
    }
}
