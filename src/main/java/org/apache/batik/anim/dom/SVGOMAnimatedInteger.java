// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.anim.values.AnimatableIntegerValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.DOMException;
import org.w3c.dom.Attr;
import org.w3c.dom.svg.SVGAnimatedInteger;

public class SVGOMAnimatedInteger extends AbstractSVGAnimatedValue implements SVGAnimatedInteger
{
    protected int defaultValue;
    protected boolean valid;
    protected int baseVal;
    protected int animVal;
    protected boolean changing;
    
    public SVGOMAnimatedInteger(final AbstractElement elt, final String ns, final String ln, final int val) {
        super(elt, ns, ln);
        this.defaultValue = val;
    }
    
    @Override
    public int getBaseVal() {
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
            this.baseVal = Integer.parseInt(attr.getValue());
        }
        this.valid = true;
    }
    
    @Override
    public void setBaseVal(final int baseVal) throws DOMException {
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
    public int getAnimVal() {
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
        return new AnimatableIntegerValue(target, this.getBaseVal());
    }
    
    @Override
    protected void updateAnimatedValue(final AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        }
        else {
            this.hasAnimVal = true;
            this.animVal = ((AnimatableIntegerValue)val).getValue();
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
