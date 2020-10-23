// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.Attr;
import org.apache.batik.anim.values.AnimatableStringValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.Element;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedEnumeration;

public class SVGOMAnimatedEnumeration extends AbstractSVGAnimatedValue implements SVGAnimatedEnumeration
{
    protected String[] values;
    protected short defaultValue;
    protected boolean valid;
    protected short baseVal;
    protected short animVal;
    protected boolean changing;
    
    public SVGOMAnimatedEnumeration(final AbstractElement elt, final String ns, final String ln, final String[] val, final short def) {
        super(elt, ns, ln);
        this.values = val;
        this.defaultValue = def;
    }
    
    @Override
    public short getBaseVal() {
        if (!this.valid) {
            this.update();
        }
        return this.baseVal;
    }
    
    public String getBaseValAsString() {
        if (!this.valid) {
            this.update();
        }
        return this.values[this.baseVal];
    }
    
    protected void update() {
        final String val = this.element.getAttributeNS(this.namespaceURI, this.localName);
        if (val.length() == 0) {
            this.baseVal = this.defaultValue;
        }
        else {
            this.baseVal = this.getEnumerationNumber(val);
        }
        this.valid = true;
    }
    
    protected short getEnumerationNumber(final String s) {
        for (short i = 0; i < this.values.length; ++i) {
            if (s.equals(this.values[i])) {
                return i;
            }
        }
        return 0;
    }
    
    @Override
    public void setBaseVal(final short baseVal) throws DOMException {
        if (baseVal >= 0 && baseVal < this.values.length) {
            try {
                this.baseVal = baseVal;
                this.valid = true;
                this.changing = true;
                this.element.setAttributeNS(this.namespaceURI, this.localName, this.values[baseVal]);
            }
            finally {
                this.changing = false;
            }
        }
    }
    
    @Override
    public short getAnimVal() {
        if (this.hasAnimVal) {
            return this.animVal;
        }
        if (!this.valid) {
            this.update();
        }
        return this.baseVal;
    }
    
    public short getCheckedVal() {
        if (this.hasAnimVal) {
            return this.animVal;
        }
        if (!this.valid) {
            this.update();
        }
        if (this.baseVal == 0) {
            throw new LiveAttributeException(this.element, this.localName, (short)1, this.getBaseValAsString());
        }
        return this.baseVal;
    }
    
    @Override
    public AnimatableValue getUnderlyingValue(final AnimationTarget target) {
        return new AnimatableStringValue(target, this.getBaseValAsString());
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
    protected void updateAnimatedValue(final AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        }
        else {
            this.hasAnimVal = true;
            this.animVal = this.getEnumerationNumber(((AnimatableStringValue)val).getString());
            this.fireAnimatedAttributeListeners();
        }
        this.fireAnimatedAttributeListeners();
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
