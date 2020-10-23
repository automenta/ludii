// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.dom;

import org.w3c.dom.DOMException;
import org.apache.batik.parser.NumberListHandler;
import org.apache.batik.parser.ParseException;
import org.w3c.dom.Element;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.parser.DefaultNumberListHandler;
import org.apache.batik.parser.NumberListParser;
import org.apache.batik.dom.svg.SVGOMRect;
import org.w3c.dom.Attr;
import org.apache.batik.anim.values.AnimatableRectValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGAnimatedRect;

public class SVGOMAnimatedRect extends AbstractSVGAnimatedValue implements SVGAnimatedRect
{
    protected BaseSVGRect baseVal;
    protected AnimSVGRect animVal;
    protected boolean changing;
    protected String defaultValue;
    
    public SVGOMAnimatedRect(final AbstractElement elt, final String ns, final String ln, final String def) {
        super(elt, ns, ln);
        this.defaultValue = def;
    }
    
    @Override
    public SVGRect getBaseVal() {
        if (this.baseVal == null) {
            this.baseVal = new BaseSVGRect();
        }
        return this.baseVal;
    }
    
    @Override
    public SVGRect getAnimVal() {
        if (this.animVal == null) {
            this.animVal = new AnimSVGRect();
        }
        return this.animVal;
    }
    
    @Override
    protected void updateAnimatedValue(final AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        }
        else {
            this.hasAnimVal = true;
            final AnimatableRectValue animRect = (AnimatableRectValue)val;
            if (this.animVal == null) {
                this.animVal = new AnimSVGRect();
            }
            this.animVal.setAnimatedValue(animRect.getX(), animRect.getY(), animRect.getWidth(), animRect.getHeight());
        }
        this.fireAnimatedAttributeListeners();
    }
    
    @Override
    public AnimatableValue getUnderlyingValue(final AnimationTarget target) {
        final SVGRect r = this.getBaseVal();
        return new AnimatableRectValue(target, r.getX(), r.getY(), r.getWidth(), r.getHeight());
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
    
    protected class BaseSVGRect extends SVGOMRect
    {
        protected boolean valid;
        
        public void invalidate() {
            this.valid = false;
        }
        
        protected void reset() {
            try {
                SVGOMAnimatedRect.this.changing = true;
                SVGOMAnimatedRect.this.element.setAttributeNS(SVGOMAnimatedRect.this.namespaceURI, SVGOMAnimatedRect.this.localName, Float.toString(this.x) + ' ' + this.y + ' ' + this.w + ' ' + this.h);
            }
            finally {
                SVGOMAnimatedRect.this.changing = false;
            }
        }
        
        protected void revalidate() {
            if (this.valid) {
                return;
            }
            final Attr attr = SVGOMAnimatedRect.this.element.getAttributeNodeNS(SVGOMAnimatedRect.this.namespaceURI, SVGOMAnimatedRect.this.localName);
            final String s = (attr == null) ? SVGOMAnimatedRect.this.defaultValue : attr.getValue();
            final float[] numbers = new float[4];
            final NumberListParser p = new NumberListParser();
            p.setNumberListHandler(new DefaultNumberListHandler() {
                protected int count;
                
                @Override
                public void endNumberList() {
                    if (this.count != 4) {
                        throw new LiveAttributeException(SVGOMAnimatedRect.this.element, SVGOMAnimatedRect.this.localName, (short)1, s);
                    }
                }
                
                @Override
                public void numberValue(final float v) throws ParseException {
                    if (this.count < 4) {
                        numbers[this.count] = v;
                    }
                    if (v < 0.0f && (this.count == 2 || this.count == 3)) {
                        throw new LiveAttributeException(SVGOMAnimatedRect.this.element, SVGOMAnimatedRect.this.localName, (short)1, s);
                    }
                    ++this.count;
                }
            });
            p.parse(s);
            this.x = numbers[0];
            this.y = numbers[1];
            this.w = numbers[2];
            this.h = numbers[3];
            this.valid = true;
        }
        
        @Override
        public float getX() {
            this.revalidate();
            return this.x;
        }
        
        @Override
        public void setX(final float x) throws DOMException {
            this.x = x;
            this.reset();
        }
        
        @Override
        public float getY() {
            this.revalidate();
            return this.y;
        }
        
        @Override
        public void setY(final float y) throws DOMException {
            this.y = y;
            this.reset();
        }
        
        @Override
        public float getWidth() {
            this.revalidate();
            return this.w;
        }
        
        @Override
        public void setWidth(final float width) throws DOMException {
            this.w = width;
            this.reset();
        }
        
        @Override
        public float getHeight() {
            this.revalidate();
            return this.h;
        }
        
        @Override
        public void setHeight(final float height) throws DOMException {
            this.h = height;
            this.reset();
        }
    }
    
    protected class AnimSVGRect extends SVGOMRect
    {
        @Override
        public float getX() {
            if (SVGOMAnimatedRect.this.hasAnimVal) {
                return super.getX();
            }
            return SVGOMAnimatedRect.this.getBaseVal().getX();
        }
        
        @Override
        public float getY() {
            if (SVGOMAnimatedRect.this.hasAnimVal) {
                return super.getY();
            }
            return SVGOMAnimatedRect.this.getBaseVal().getY();
        }
        
        @Override
        public float getWidth() {
            if (SVGOMAnimatedRect.this.hasAnimVal) {
                return super.getWidth();
            }
            return SVGOMAnimatedRect.this.getBaseVal().getWidth();
        }
        
        @Override
        public float getHeight() {
            if (SVGOMAnimatedRect.this.hasAnimVal) {
                return super.getHeight();
            }
            return SVGOMAnimatedRect.this.getBaseVal().getHeight();
        }
        
        @Override
        public void setX(final float value) throws DOMException {
            throw SVGOMAnimatedRect.this.element.createDOMException((short)7, "readonly.length", null);
        }
        
        @Override
        public void setY(final float value) throws DOMException {
            throw SVGOMAnimatedRect.this.element.createDOMException((short)7, "readonly.length", null);
        }
        
        @Override
        public void setWidth(final float value) throws DOMException {
            throw SVGOMAnimatedRect.this.element.createDOMException((short)7, "readonly.length", null);
        }
        
        @Override
        public void setHeight(final float value) throws DOMException {
            throw SVGOMAnimatedRect.this.element.createDOMException((short)7, "readonly.length", null);
        }
        
        protected void setAnimatedValue(final float x, final float y, final float w, final float h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}
