// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.DOMException;
import org.apache.batik.dom.svg.AbstractSVGPreserveAspectRatio;
import org.w3c.dom.Attr;
import org.apache.batik.anim.values.AnimatablePreserveAspectRatioValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.Element;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.w3c.dom.svg.SVGPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;

public class SVGOMAnimatedPreserveAspectRatio extends AbstractSVGAnimatedValue implements SVGAnimatedPreserveAspectRatio
{
    protected BaseSVGPARValue baseVal;
    protected AnimSVGPARValue animVal;
    protected boolean changing;
    
    public SVGOMAnimatedPreserveAspectRatio(final AbstractElement elt) {
        super(elt, null, "preserveAspectRatio");
    }
    
    @Override
    public SVGPreserveAspectRatio getBaseVal() {
        if (this.baseVal == null) {
            this.baseVal = new BaseSVGPARValue();
        }
        return this.baseVal;
    }
    
    @Override
    public SVGPreserveAspectRatio getAnimVal() {
        if (this.animVal == null) {
            this.animVal = new AnimSVGPARValue();
        }
        return this.animVal;
    }
    
    public void check() {
        if (!this.hasAnimVal) {
            if (this.baseVal == null) {
                this.baseVal = new BaseSVGPARValue();
            }
            if (this.baseVal.malformed) {
                throw new LiveAttributeException(this.element, this.localName, (short)1, this.baseVal.getValueAsString());
            }
        }
    }
    
    @Override
    public AnimatableValue getUnderlyingValue(final AnimationTarget target) {
        final SVGPreserveAspectRatio par = this.getBaseVal();
        return new AnimatablePreserveAspectRatioValue(target, par.getAlign(), par.getMeetOrSlice());
    }
    
    @Override
    protected void updateAnimatedValue(final AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        }
        else {
            this.hasAnimVal = true;
            if (this.animVal == null) {
                this.animVal = new AnimSVGPARValue();
            }
            final AnimatablePreserveAspectRatioValue animPAR = (AnimatablePreserveAspectRatioValue)val;
            this.animVal.setAnimatedValue(animPAR.getAlign(), animPAR.getMeetOrSlice());
        }
        this.fireAnimatedAttributeListeners();
    }
    
    @Override
    public void attrAdded(final Attr node, final String newv) {
        if (!this.changing && this.baseVal != null) {
            this.baseVal.invalidate();
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }
    
    @Override
    public void attrModified(final Attr node, final String oldv, final String newv) {
        if (!this.changing && this.baseVal != null) {
            this.baseVal.invalidate();
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }
    
    @Override
    public void attrRemoved(final Attr node, final String oldv) {
        if (!this.changing && this.baseVal != null) {
            this.baseVal.invalidate();
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }
    
    public class BaseSVGPARValue extends AbstractSVGPreserveAspectRatio
    {
        protected boolean malformed;
        
        public BaseSVGPARValue() {
            this.invalidate();
        }
        
        @Override
        protected DOMException createDOMException(final short type, final String key, final Object[] args) {
            return SVGOMAnimatedPreserveAspectRatio.this.element.createDOMException(type, key, args);
        }
        
        @Override
        protected void setAttributeValue(final String value) throws DOMException {
            try {
                SVGOMAnimatedPreserveAspectRatio.this.changing = true;
                SVGOMAnimatedPreserveAspectRatio.this.element.setAttributeNS(null, "preserveAspectRatio", value);
                this.malformed = false;
            }
            finally {
                SVGOMAnimatedPreserveAspectRatio.this.changing = false;
            }
        }
        
        protected void invalidate() {
            final String s = SVGOMAnimatedPreserveAspectRatio.this.element.getAttributeNS(null, "preserveAspectRatio");
            this.setValueAsString(s);
        }
    }
    
    public class AnimSVGPARValue extends AbstractSVGPreserveAspectRatio
    {
        @Override
        protected DOMException createDOMException(final short type, final String key, final Object[] args) {
            return SVGOMAnimatedPreserveAspectRatio.this.element.createDOMException(type, key, args);
        }
        
        @Override
        protected void setAttributeValue(final String value) throws DOMException {
        }
        
        @Override
        public short getAlign() {
            if (SVGOMAnimatedPreserveAspectRatio.this.hasAnimVal) {
                return super.getAlign();
            }
            return SVGOMAnimatedPreserveAspectRatio.this.getBaseVal().getAlign();
        }
        
        @Override
        public short getMeetOrSlice() {
            if (SVGOMAnimatedPreserveAspectRatio.this.hasAnimVal) {
                return super.getMeetOrSlice();
            }
            return SVGOMAnimatedPreserveAspectRatio.this.getBaseVal().getMeetOrSlice();
        }
        
        @Override
        public void setAlign(final short align) {
            throw SVGOMAnimatedPreserveAspectRatio.this.element.createDOMException((short)7, "readonly.preserve.aspect.ratio", null);
        }
        
        @Override
        public void setMeetOrSlice(final short meetOrSlice) {
            throw SVGOMAnimatedPreserveAspectRatio.this.element.createDOMException((short)7, "readonly.preserve.aspect.ratio", null);
        }
        
        protected void setAnimatedValue(final short align, final short meetOrSlice) {
            this.align = align;
            this.meetOrSlice = meetOrSlice;
        }
    }
}
