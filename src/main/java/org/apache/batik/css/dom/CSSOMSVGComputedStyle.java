// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.dom;

import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGColorManager;
import org.apache.batik.css.engine.value.svg.SVGPaintManager;
import org.w3c.dom.css.CSSValue;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.CSSEngine;

public class CSSOMSVGComputedStyle extends CSSOMComputedStyle
{
    public CSSOMSVGComputedStyle(final CSSEngine e, final CSSStylableElement elt, final String pseudoElt) {
        super(e, elt, pseudoElt);
    }
    
    @Override
    protected CSSValue createCSSValue(final int idx) {
        if (idx > 59) {
            if (this.cssEngine.getValueManagers()[idx] instanceof SVGPaintManager) {
                return new ComputedCSSPaintValue(idx);
            }
            if (this.cssEngine.getValueManagers()[idx] instanceof SVGColorManager) {
                return new ComputedCSSColorValue(idx);
            }
        }
        else {
            switch (idx) {
                case 15:
                case 45: {
                    return new ComputedCSSPaintValue(idx);
                }
                case 19:
                case 33:
                case 43: {
                    return new ComputedCSSColorValue(idx);
                }
            }
        }
        return super.createCSSValue(idx);
    }
    
    protected class ComputedCSSColorValue extends CSSOMSVGColor implements ValueProvider
    {
        protected int index;
        
        public ComputedCSSColorValue(final int idx) {
            super(null);
            this.valueProvider = this;
            this.index = idx;
        }
        
        @Override
        public Value getValue() {
            return CSSOMSVGComputedStyle.this.cssEngine.getComputedStyle(CSSOMSVGComputedStyle.this.element, CSSOMSVGComputedStyle.this.pseudoElement, this.index);
        }
    }
    
    public class ComputedCSSPaintValue extends CSSOMSVGPaint implements ValueProvider
    {
        protected int index;
        
        public ComputedCSSPaintValue(final int idx) {
            super(null);
            this.valueProvider = this;
            this.index = idx;
        }
        
        @Override
        public Value getValue() {
            return CSSOMSVGComputedStyle.this.cssEngine.getComputedStyle(CSSOMSVGComputedStyle.this.element, CSSOMSVGComputedStyle.this.pseudoElement, this.index);
        }
    }
}
