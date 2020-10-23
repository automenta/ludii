// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.URIValue;
import org.apache.batik.css.engine.value.AbstractValueFactory;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.AbstractValueManager;

public class FilterManager extends AbstractValueManager
{
    @Override
    public boolean isInheritedProperty() {
        return false;
    }
    
    @Override
    public String getPropertyName() {
        return "filter";
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
        return SVGValueConstants.NONE_VALUE;
    }
    
    @Override
    public Value createValue(final LexicalUnit lu, final CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVGValueConstants.INHERIT_VALUE;
            }
            case 24: {
                return new URIValue(lu.getStringValue(), AbstractValueFactory.resolveURI(engine.getCSSBaseURI(), lu.getStringValue()));
            }
            case 35: {
                if (lu.getStringValue().equalsIgnoreCase("none")) {
                    return SVGValueConstants.NONE_VALUE;
                }
                throw this.createInvalidIdentifierDOMException(lu.getStringValue());
            }
            default: {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
        }
    }
    
    @Override
    public Value createStringValue(final short type, final String value, final CSSEngine engine) throws DOMException {
        if (type == 21) {
            if (value.equalsIgnoreCase("none")) {
                return SVGValueConstants.NONE_VALUE;
            }
            throw this.createInvalidIdentifierDOMException(value);
        }
        else {
            if (type == 20) {
                return new URIValue(value, AbstractValueFactory.resolveURI(engine.getCSSBaseURI(), value));
            }
            throw this.createInvalidStringTypeDOMException(type);
        }
    }
}
