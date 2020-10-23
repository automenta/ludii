// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Attr;
import org.apache.batik.anim.values.AnimatableStringValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedString;

public class SVGOMAnimatedString extends AbstractSVGAnimatedValue implements SVGAnimatedString
{
    protected String animVal;
    
    public SVGOMAnimatedString(final AbstractElement elt, final String ns, final String ln) {
        super(elt, ns, ln);
    }
    
    @Override
    public String getBaseVal() {
        return this.element.getAttributeNS(this.namespaceURI, this.localName);
    }
    
    @Override
    public void setBaseVal(final String baseVal) throws DOMException {
        this.element.setAttributeNS(this.namespaceURI, this.localName, baseVal);
    }
    
    @Override
    public String getAnimVal() {
        if (this.hasAnimVal) {
            return this.animVal;
        }
        return this.element.getAttributeNS(this.namespaceURI, this.localName);
    }
    
    @Override
    public AnimatableValue getUnderlyingValue(final AnimationTarget target) {
        return new AnimatableStringValue(target, this.getBaseVal());
    }
    
    @Override
    protected void updateAnimatedValue(final AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        }
        else {
            this.hasAnimVal = true;
            this.animVal = ((AnimatableStringValue)val).getString();
        }
        this.fireAnimatedAttributeListeners();
    }
    
    @Override
    public void attrAdded(final Attr node, final String newv) {
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }
    
    @Override
    public void attrModified(final Attr node, final String oldv, final String newv) {
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }
    
    @Override
    public void attrRemoved(final Attr node, final String oldv) {
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }
}
