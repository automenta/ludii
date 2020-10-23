// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Attr;
import org.apache.batik.anim.values.AnimatableLengthValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.Element;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.w3c.dom.svg.SVGLength;
import org.apache.batik.dom.svg.LiveAttributeValue;
import org.w3c.dom.svg.SVGAnimatedLength;

public abstract class AbstractSVGAnimatedLength extends AbstractSVGAnimatedValue implements SVGAnimatedLength, LiveAttributeValue
{
    public static final short HORIZONTAL_LENGTH = 2;
    public static final short VERTICAL_LENGTH = 1;
    public static final short OTHER_LENGTH = 0;
    protected short direction;
    protected BaseSVGLength baseVal;
    protected AnimSVGLength animVal;
    protected boolean changing;
    protected boolean nonNegative;
    
    public AbstractSVGAnimatedLength(final AbstractElement elt, final String ns, final String ln, final short dir, final boolean nonneg) {
        super(elt, ns, ln);
        this.direction = dir;
        this.nonNegative = nonneg;
    }
    
    protected abstract String getDefaultValue();
    
    @Override
    public SVGLength getBaseVal() {
        if (this.baseVal == null) {
            this.baseVal = new BaseSVGLength(this.direction);
        }
        return this.baseVal;
    }
    
    @Override
    public SVGLength getAnimVal() {
        if (this.animVal == null) {
            this.animVal = new AnimSVGLength(this.direction);
        }
        return this.animVal;
    }
    
    public float getCheckedValue() {
        if (this.hasAnimVal) {
            if (this.animVal == null) {
                this.animVal = new AnimSVGLength(this.direction);
            }
            if (this.nonNegative && this.animVal.value < 0.0f) {
                throw new LiveAttributeException(this.element, this.localName, (short)2, this.animVal.getValueAsString());
            }
            return this.animVal.getValue();
        }
        else {
            if (this.baseVal == null) {
                this.baseVal = new BaseSVGLength(this.direction);
            }
            this.baseVal.revalidate();
            if (this.baseVal.missing) {
                throw new LiveAttributeException(this.element, this.localName, (short)0, null);
            }
            if (this.baseVal.unitType == 0) {
                throw new LiveAttributeException(this.element, this.localName, (short)1, this.baseVal.getValueAsString());
            }
            if (this.nonNegative && this.baseVal.value < 0.0f) {
                throw new LiveAttributeException(this.element, this.localName, (short)2, this.baseVal.getValueAsString());
            }
            return this.baseVal.getValue();
        }
    }
    
    @Override
    protected void updateAnimatedValue(final AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        }
        else {
            this.hasAnimVal = true;
            final AnimatableLengthValue animLength = (AnimatableLengthValue)val;
            if (this.animVal == null) {
                this.animVal = new AnimSVGLength(this.direction);
            }
            this.animVal.setAnimatedValue(animLength.getLengthType(), animLength.getLengthValue());
        }
        this.fireAnimatedAttributeListeners();
    }
    
    @Override
    public AnimatableValue getUnderlyingValue(final AnimationTarget target) {
        final SVGLength base = this.getBaseVal();
        return new AnimatableLengthValue(target, base.getUnitType(), base.getValueInSpecifiedUnits(), target.getPercentageInterpretation(this.getNamespaceURI(), this.getLocalName(), false));
    }
    
    @Override
    public void attrAdded(final Attr node, final String newv) {
        this.attrChanged();
    }
    
    @Override
    public void attrModified(final Attr node, final String oldv, final String newv) {
        this.attrChanged();
    }
    
    @Override
    public void attrRemoved(final Attr node, final String oldv) {
        this.attrChanged();
    }
    
    protected void attrChanged() {
        if (!this.changing && this.baseVal != null) {
            this.baseVal.invalidate();
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }
    
    protected class BaseSVGLength extends AbstractSVGLength
    {
        protected boolean valid;
        protected boolean missing;
        
        public BaseSVGLength(final short direction) {
            super(direction);
        }
        
        public void invalidate() {
            this.valid = false;
        }
        
        @Override
        protected void reset() {
            try {
                AbstractSVGAnimatedLength.this.changing = true;
                this.valid = true;
                final String value = this.getValueAsString();
                AbstractSVGAnimatedLength.this.element.setAttributeNS(AbstractSVGAnimatedLength.this.namespaceURI, AbstractSVGAnimatedLength.this.localName, value);
            }
            finally {
                AbstractSVGAnimatedLength.this.changing = false;
            }
        }
        
        @Override
        protected void revalidate() {
            if (this.valid) {
                return;
            }
            this.missing = false;
            this.valid = true;
            final Attr attr = AbstractSVGAnimatedLength.this.element.getAttributeNodeNS(AbstractSVGAnimatedLength.this.namespaceURI, AbstractSVGAnimatedLength.this.localName);
            String s;
            if (attr == null) {
                s = AbstractSVGAnimatedLength.this.getDefaultValue();
                if (s == null) {
                    this.missing = true;
                    return;
                }
            }
            else {
                s = attr.getValue();
            }
            this.parse(s);
        }
        
        @Override
        protected SVGOMElement getAssociatedElement() {
            return (SVGOMElement)AbstractSVGAnimatedLength.this.element;
        }
    }
    
    protected class AnimSVGLength extends AbstractSVGLength
    {
        public AnimSVGLength(final short direction) {
            super(direction);
        }
        
        @Override
        public short getUnitType() {
            if (AbstractSVGAnimatedLength.this.hasAnimVal) {
                return super.getUnitType();
            }
            return AbstractSVGAnimatedLength.this.getBaseVal().getUnitType();
        }
        
        @Override
        public float getValue() {
            if (AbstractSVGAnimatedLength.this.hasAnimVal) {
                return super.getValue();
            }
            return AbstractSVGAnimatedLength.this.getBaseVal().getValue();
        }
        
        @Override
        public float getValueInSpecifiedUnits() {
            if (AbstractSVGAnimatedLength.this.hasAnimVal) {
                return super.getValueInSpecifiedUnits();
            }
            return AbstractSVGAnimatedLength.this.getBaseVal().getValueInSpecifiedUnits();
        }
        
        @Override
        public String getValueAsString() {
            if (AbstractSVGAnimatedLength.this.hasAnimVal) {
                return super.getValueAsString();
            }
            return AbstractSVGAnimatedLength.this.getBaseVal().getValueAsString();
        }
        
        @Override
        public void setValue(final float value) throws DOMException {
            throw AbstractSVGAnimatedLength.this.element.createDOMException((short)7, "readonly.length", null);
        }
        
        @Override
        public void setValueInSpecifiedUnits(final float value) throws DOMException {
            throw AbstractSVGAnimatedLength.this.element.createDOMException((short)7, "readonly.length", null);
        }
        
        @Override
        public void setValueAsString(final String value) throws DOMException {
            throw AbstractSVGAnimatedLength.this.element.createDOMException((short)7, "readonly.length", null);
        }
        
        @Override
        public void newValueSpecifiedUnits(final short unit, final float value) {
            throw AbstractSVGAnimatedLength.this.element.createDOMException((short)7, "readonly.length", null);
        }
        
        @Override
        public void convertToSpecifiedUnits(final short unit) {
            throw AbstractSVGAnimatedLength.this.element.createDOMException((short)7, "readonly.length", null);
        }
        
        @Override
        protected SVGOMElement getAssociatedElement() {
            return (SVGOMElement)AbstractSVGAnimatedLength.this.element;
        }
        
        protected void setAnimatedValue(final int type, final float val) {
            super.newValueSpecifiedUnits((short)type, val);
        }
    }
}
