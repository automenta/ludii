// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.dom;

import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGColorManager;
import org.apache.batik.css.engine.value.svg.SVGPaintManager;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSRule;
import org.apache.batik.css.engine.CSSEngine;

public class CSSOMSVGStyleDeclaration extends CSSOMStyleDeclaration
{
    protected CSSEngine cssEngine;
    
    public CSSOMSVGStyleDeclaration(final ValueProvider vp, final CSSRule parent, final CSSEngine eng) {
        super(vp, parent);
        this.cssEngine = eng;
    }
    
    @Override
    protected CSSValue createCSSValue(final String name) {
        final int idx = this.cssEngine.getPropertyIndex(name);
        if (idx > 59) {
            if (this.cssEngine.getValueManagers()[idx] instanceof SVGPaintManager) {
                return new StyleDeclarationPaintValue(name);
            }
            if (this.cssEngine.getValueManagers()[idx] instanceof SVGColorManager) {
                return new StyleDeclarationColorValue(name);
            }
        }
        else {
            switch (idx) {
                case 15:
                case 45: {
                    return new StyleDeclarationPaintValue(name);
                }
                case 19:
                case 33:
                case 43: {
                    return new StyleDeclarationColorValue(name);
                }
            }
        }
        return super.createCSSValue(name);
    }
    
    public class StyleDeclarationColorValue extends CSSOMSVGColor implements CSSOMSVGColor.ValueProvider
    {
        protected String property;
        
        public StyleDeclarationColorValue(final String prop) {
            super(null);
            ((CSSOMSVGColor)(this.valueProvider = this)).setModificationHandler(new AbstractModificationHandler() {
                @Override
                protected Value getValue() {
                    return StyleDeclarationColorValue.this.getValue();
                }
                
                @Override
                public void textChanged(final String text) throws DOMException {
                    if (StyleDeclarationColorValue.this.handler == null) {
                        throw new DOMException((short)7, "");
                    }
                    final String prio = CSSOMSVGStyleDeclaration.this.getPropertyPriority(StyleDeclarationColorValue.this.property);
                    CSSOMSVGStyleDeclaration.this.handler.propertyChanged(StyleDeclarationColorValue.this.property, text, prio);
                }
            });
            this.property = prop;
        }
        
        @Override
        public Value getValue() {
            return CSSOMSVGStyleDeclaration.this.valueProvider.getValue(this.property);
        }
    }
    
    public class StyleDeclarationPaintValue extends CSSOMSVGPaint implements CSSOMSVGColor.ValueProvider
    {
        protected String property;
        
        public StyleDeclarationPaintValue(final String prop) {
            super(null);
            ((CSSOMSVGPaint)(this.valueProvider = this)).setModificationHandler(new CSSOMSVGPaint.AbstractModificationHandler() {
                @Override
                protected Value getValue() {
                    return StyleDeclarationPaintValue.this.getValue();
                }
                
                @Override
                public void textChanged(final String text) throws DOMException {
                    if (StyleDeclarationPaintValue.this.handler == null) {
                        throw new DOMException((short)7, "");
                    }
                    final String prio = CSSOMSVGStyleDeclaration.this.getPropertyPriority(StyleDeclarationPaintValue.this.property);
                    CSSOMSVGStyleDeclaration.this.handler.propertyChanged(StyleDeclarationPaintValue.this.property, text, prio);
                }
            });
            this.property = prop;
        }
        
        @Override
        public Value getValue() {
            return CSSOMSVGStyleDeclaration.this.valueProvider.getValue(this.property);
        }
    }
}
