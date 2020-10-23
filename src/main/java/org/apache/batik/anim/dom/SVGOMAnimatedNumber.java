// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.anim.values.AnimatableNumberValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.DOMException;
import org.w3c.dom.Attr;
import org.w3c.dom.svg.SVGAnimatedNumber;

public class SVGOMAnimatedNumber extends AbstractSVGAnimatedValue implements SVGAnimatedNumber
{
    protected float defaultValue;
    protected boolean allowPercentage;
    protected boolean valid;
    protected float baseVal;
    protected float animVal;
    protected boolean changing;
    
    public SVGOMAnimatedNumber(final AbstractElement elt, final String ns, final String ln, final float val) {
        this(elt, ns, ln, val, false);
    }
    
    public SVGOMAnimatedNumber(final AbstractElement elt, final String ns, final String ln, final float val, final boolean allowPercentage) {
        super(elt, ns, ln);
        this.defaultValue = val;
        this.allowPercentage = allowPercentage;
    }
    
    @Override
    public float getBaseVal() {
        if (!this.valid) {
            this.update();
        }
        return this.baseVal;
    }
    
    protected void update() {
        final Attr attr = this.element.getAttributeNodeNS(this.namespaceURI, this.localName);
        if (attr == null) {
            this.baseVal = this.defaultValue;
        }
        else {
            final String v = attr.getValue();
            final int len = v.length();
            if (this.allowPercentage && len > 1 && v.charAt(len - 1) == '%') {
                this.baseVal = 0.01f * Float.parseFloat(v.substring(0, len - 1));
            }
            else {
                this.baseVal = Float.parseFloat(v);
            }
        }
        this.valid = true;
    }
    
    @Override
    public void setBaseVal(final float baseVal) throws DOMException {
        try {
            this.baseVal = baseVal;
            this.valid = true;
            this.changing = true;
            this.element.setAttributeNS(this.namespaceURI, this.localName, String.valueOf(baseVal));
        }
        finally {
            this.changing = false;
        }
    }
    
    @Override
    public float getAnimVal() {
        if (this.hasAnimVal) {
            return this.animVal;
        }
        if (!this.valid) {
            this.update();
        }
        return this.baseVal;
    }
    
    @Override
    public AnimatableValue getUnderlyingValue(final AnimationTarget target) {
        return new AnimatableNumberValue(target, this.getBaseVal());
    }
    
    @Override
    protected void updateAnimatedValue(final AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        }
        else {
            this.hasAnimVal = true;
            this.animVal = ((AnimatableNumberValue)val).getValue();
        }
        this.fireAnimatedAttributeListeners();
    }
    
    @Override
    public void attrAdded(final Attr node, final String newv) {
        if (!this.changing) {
            this.valid = false;
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }
    
    @Override
    public void attrModified(final Attr node, final String oldv, final String newv) {
        if (!this.changing) {
            this.valid = false;
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }
    
    @Override
    public void attrRemoved(final Attr node, final String oldv) {
        if (!this.changing) {
            this.valid = false;
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }
}
