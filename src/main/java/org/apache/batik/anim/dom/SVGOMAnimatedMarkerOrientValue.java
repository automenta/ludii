// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.svg.SVGAngle;
import org.w3c.dom.DOMException;
import org.apache.batik.dom.svg.SVGOMAngle;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedAngle;
import org.w3c.dom.Attr;
import org.apache.batik.anim.values.AnimatableValue;

public class SVGOMAnimatedMarkerOrientValue extends AbstractSVGAnimatedValue
{
    protected boolean valid;
    protected AnimatedAngle animatedAngle;
    protected AnimatedEnumeration animatedEnumeration;
    protected BaseSVGAngle baseAngleVal;
    protected short baseEnumerationVal;
    protected AnimSVGAngle animAngleVal;
    protected short animEnumerationVal;
    protected boolean changing;
    
    public SVGOMAnimatedMarkerOrientValue(final AbstractElement elt, final String ns, final String ln) {
        super(elt, ns, ln);
        this.animatedAngle = new AnimatedAngle();
        this.animatedEnumeration = new AnimatedEnumeration();
    }
    
    @Override
    protected void updateAnimatedValue(final AnimatableValue val) {
        throw new UnsupportedOperationException("Animation of marker orient value is not implemented");
    }
    
    @Override
    public AnimatableValue getUnderlyingValue(final AnimationTarget target) {
        throw new UnsupportedOperationException("Animation of marker orient value is not implemented");
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
    
    public void setAnimatedValueToAngle(final short unitType, final float value) {
        this.hasAnimVal = true;
        this.animAngleVal.setAnimatedValue(unitType, value);
        this.animEnumerationVal = 2;
        this.fireAnimatedAttributeListeners();
    }
    
    public void setAnimatedValueToAuto() {
        this.hasAnimVal = true;
        this.animAngleVal.setAnimatedValue(1, 0.0f);
        this.animEnumerationVal = 1;
        this.fireAnimatedAttributeListeners();
    }
    
    public void resetAnimatedValue() {
        this.hasAnimVal = false;
        this.fireAnimatedAttributeListeners();
    }
    
    public SVGAnimatedAngle getAnimatedAngle() {
        return this.animatedAngle;
    }
    
    public SVGAnimatedEnumeration getAnimatedEnumeration() {
        return this.animatedEnumeration;
    }
    
    protected class BaseSVGAngle extends SVGOMAngle
    {
        public void invalidate() {
            SVGOMAnimatedMarkerOrientValue.this.valid = false;
        }
        
        @Override
        protected void reset() {
            try {
                SVGOMAnimatedMarkerOrientValue.this.changing = true;
                SVGOMAnimatedMarkerOrientValue.this.valid = true;
                String value;
                if (SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal == 2) {
                    value = this.getValueAsString();
                }
                else {
                    if (SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal != 1) {
                        return;
                    }
                    value = "auto";
                }
                SVGOMAnimatedMarkerOrientValue.this.element.setAttributeNS(SVGOMAnimatedMarkerOrientValue.this.namespaceURI, SVGOMAnimatedMarkerOrientValue.this.localName, value);
            }
            finally {
                SVGOMAnimatedMarkerOrientValue.this.changing = false;
            }
        }
        
        @Override
        protected void revalidate() {
            if (!SVGOMAnimatedMarkerOrientValue.this.valid) {
                final Attr attr = SVGOMAnimatedMarkerOrientValue.this.element.getAttributeNodeNS(SVGOMAnimatedMarkerOrientValue.this.namespaceURI, SVGOMAnimatedMarkerOrientValue.this.localName);
                if (attr == null) {
                    this.setUnitType((short)1);
                    this.value = 0.0f;
                }
                else {
                    this.parse(attr.getValue());
                }
                SVGOMAnimatedMarkerOrientValue.this.valid = true;
            }
        }
        
        @Override
        protected void parse(final String s) {
            if (s.equals("auto")) {
                this.setUnitType((short)1);
                this.value = 0.0f;
                SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal = 1;
            }
            else {
                super.parse(s);
                if (this.getUnitType() == 0) {
                    SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal = 0;
                }
                else {
                    SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal = 2;
                }
            }
        }
    }
    
    protected class AnimSVGAngle extends SVGOMAngle
    {
        @Override
        public short getUnitType() {
            if (SVGOMAnimatedMarkerOrientValue.this.hasAnimVal) {
                return super.getUnitType();
            }
            return SVGOMAnimatedMarkerOrientValue.this.animatedAngle.getBaseVal().getUnitType();
        }
        
        @Override
        public float getValue() {
            if (SVGOMAnimatedMarkerOrientValue.this.hasAnimVal) {
                return super.getValue();
            }
            return SVGOMAnimatedMarkerOrientValue.this.animatedAngle.getBaseVal().getValue();
        }
        
        @Override
        public float getValueInSpecifiedUnits() {
            if (SVGOMAnimatedMarkerOrientValue.this.hasAnimVal) {
                return super.getValueInSpecifiedUnits();
            }
            return SVGOMAnimatedMarkerOrientValue.this.animatedAngle.getBaseVal().getValueInSpecifiedUnits();
        }
        
        @Override
        public String getValueAsString() {
            if (SVGOMAnimatedMarkerOrientValue.this.hasAnimVal) {
                return super.getValueAsString();
            }
            return SVGOMAnimatedMarkerOrientValue.this.animatedAngle.getBaseVal().getValueAsString();
        }
        
        @Override
        public void setValue(final float value) throws DOMException {
            throw SVGOMAnimatedMarkerOrientValue.this.element.createDOMException((short)7, "readonly.angle", null);
        }
        
        @Override
        public void setValueInSpecifiedUnits(final float value) throws DOMException {
            throw SVGOMAnimatedMarkerOrientValue.this.element.createDOMException((short)7, "readonly.angle", null);
        }
        
        @Override
        public void setValueAsString(final String value) throws DOMException {
            throw SVGOMAnimatedMarkerOrientValue.this.element.createDOMException((short)7, "readonly.angle", null);
        }
        
        @Override
        public void newValueSpecifiedUnits(final short unit, final float value) {
            throw SVGOMAnimatedMarkerOrientValue.this.element.createDOMException((short)7, "readonly.angle", null);
        }
        
        @Override
        public void convertToSpecifiedUnits(final short unit) {
            throw SVGOMAnimatedMarkerOrientValue.this.element.createDOMException((short)7, "readonly.angle", null);
        }
        
        protected void setAnimatedValue(final int type, final float val) {
            super.newValueSpecifiedUnits((short)type, val);
        }
    }
    
    protected class AnimatedAngle implements SVGAnimatedAngle
    {
        @Override
        public SVGAngle getBaseVal() {
            if (SVGOMAnimatedMarkerOrientValue.this.baseAngleVal == null) {
                SVGOMAnimatedMarkerOrientValue.this.baseAngleVal = new BaseSVGAngle();
            }
            return SVGOMAnimatedMarkerOrientValue.this.baseAngleVal;
        }
        
        @Override
        public SVGAngle getAnimVal() {
            if (SVGOMAnimatedMarkerOrientValue.this.animAngleVal == null) {
                SVGOMAnimatedMarkerOrientValue.this.animAngleVal = new AnimSVGAngle();
            }
            return SVGOMAnimatedMarkerOrientValue.this.animAngleVal;
        }
    }
    
    protected class AnimatedEnumeration implements SVGAnimatedEnumeration
    {
        @Override
        public short getBaseVal() {
            if (SVGOMAnimatedMarkerOrientValue.this.baseAngleVal == null) {
                SVGOMAnimatedMarkerOrientValue.this.baseAngleVal = new BaseSVGAngle();
            }
            SVGOMAnimatedMarkerOrientValue.this.baseAngleVal.revalidate();
            return SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal;
        }
        
        @Override
        public void setBaseVal(final short baseVal) throws DOMException {
            if (baseVal == 1) {
                SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal = baseVal;
                if (SVGOMAnimatedMarkerOrientValue.this.baseAngleVal == null) {
                    SVGOMAnimatedMarkerOrientValue.this.baseAngleVal = new BaseSVGAngle();
                }
                SVGOMAnimatedMarkerOrientValue.this.baseAngleVal.setUnitType((short)1);
                SVGOMAnimatedMarkerOrientValue.this.baseAngleVal.setValue(0.0f);
                SVGOMAnimatedMarkerOrientValue.this.baseAngleVal.reset();
            }
            else if (baseVal == 2) {
                SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal = baseVal;
                if (SVGOMAnimatedMarkerOrientValue.this.baseAngleVal == null) {
                    SVGOMAnimatedMarkerOrientValue.this.baseAngleVal = new BaseSVGAngle();
                }
                SVGOMAnimatedMarkerOrientValue.this.baseAngleVal.reset();
            }
        }
        
        @Override
        public short getAnimVal() {
            if (SVGOMAnimatedMarkerOrientValue.this.hasAnimVal) {
                return SVGOMAnimatedMarkerOrientValue.this.animEnumerationVal;
            }
            if (SVGOMAnimatedMarkerOrientValue.this.baseAngleVal == null) {
                SVGOMAnimatedMarkerOrientValue.this.baseAngleVal = new BaseSVGAngle();
            }
            SVGOMAnimatedMarkerOrientValue.this.baseAngleVal.revalidate();
            return SVGOMAnimatedMarkerOrientValue.this.baseEnumerationVal;
        }
    }
}
