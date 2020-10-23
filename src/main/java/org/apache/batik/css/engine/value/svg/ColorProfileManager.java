// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.URIValue;
import org.apache.batik.css.engine.value.AbstractValueFactory;
import org.apache.batik.css.engine.value.StringValue;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.AbstractValueManager;

public class ColorProfileManager extends AbstractValueManager
{
    @Override
    public boolean isInheritedProperty() {
        return true;
    }
    
    @Override
    public String getPropertyName() {
        return "color-profile";
    }
    
    @Override
    public boolean isAnimatableProperty() {
        return true;
    }
    
    @Override
    public boolean isAdditiveProperty() {
        return false;
    }
    
    @Override
    public int getPropertyType() {
        return 20;
    }
    
    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.AUTO_VALUE;
    }
    
    @Override
    public Value createValue(final LexicalUnit lu, final CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVGValueConstants.INHERIT_VALUE;
            }
            case 35: {
                final String s = lu.getStringValue().toLowerCase();
                if (s.equals("auto")) {
                    return SVGValueConstants.AUTO_VALUE;
                }
                if (s.equals("srgb")) {
                    return SVGValueConstants.SRGB_VALUE;
                }
                return new StringValue((short)21, s);
            }
            case 24: {
                return new URIValue(lu.getStringValue(), AbstractValueFactory.resolveURI(engine.getCSSBaseURI(), lu.getStringValue()));
            }
            default: {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
        }
    }
    
    @Override
    public Value createStringValue(final short type, final String value, final CSSEngine engine) throws DOMException {
        switch (type) {
            case 21: {
                final String s = value.toLowerCase();
                if (s.equals("auto")) {
                    return SVGValueConstants.AUTO_VALUE;
                }
                if (s.equals("srgb")) {
                    return SVGValueConstants.SRGB_VALUE;
                }
                return new StringValue((short)21, s);
            }
            case 20: {
                return new URIValue(value, AbstractValueFactory.resolveURI(engine.getCSSBaseURI(), value));
            }
            default: {
                throw this.createInvalidStringTypeDOMException(type);
            }
        }
    }
}
