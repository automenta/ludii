// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.apache.batik.anim.values.AnimatableBooleanValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.DOMException;
import org.w3c.dom.Attr;
import org.w3c.dom.svg.SVGAnimatedBoolean;

public class SVGOMAnimatedBoolean extends AbstractSVGAnimatedValue implements SVGAnimatedBoolean
{
    protected boolean defaultValue;
    protected boolean valid;
    protected boolean baseVal;
    protected boolean animVal;
    protected boolean changing;
    
    public SVGOMAnimatedBoolean(final AbstractElement elt, final String ns, final String ln, final boolean val) {
        super(elt, ns, ln);
        this.defaultValue = val;
    }
    
    @Override
    public boolean getBaseVal() {
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
            this.baseVal = attr.getValue().equals("true");
        }
        this.valid = true;
    }
    
    @Override
    public void setBaseVal(final boolean baseVal) throws DOMException {
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
    public boolean getAnimVal() {
        if (this.hasAnimVal) {
            return this.animVal;
        }
        if (!this.valid) {
            this.update();
        }
        return this.baseVal;
    }
    
    public void setAnimatedValue(final boolean animVal) {
        this.hasAnimVal = true;
        this.animVal = animVal;
        this.fireAnimatedAttributeListeners();
    }
    
    @Override
    protected void updateAnimatedValue(final AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        }
        else {
            this.hasAnimVal = true;
            this.animVal = ((AnimatableBooleanValue)val).getValue();
        }
        this.fireAnimatedAttributeListeners();
    }
    
    @Override
    public AnimatableValue getUnderlyingValue(final AnimationTarget target) {
        return new AnimatableBooleanValue(target, this.getBaseVal());
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
