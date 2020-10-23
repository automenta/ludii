// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.LengthManager;

public class KerningManager extends LengthManager
{
    @Override
    public boolean isInheritedProperty() {
        return true;
    }
    
    @Override
    public String getPropertyName() {
        return "kerning";
    }
    
    @Override
    public boolean isAnimatableProperty() {
        return true;
    }
    
    @Override
    public boolean isAdditiveProperty() {
        return true;
    }
    
    @Override
    public int getPropertyType() {
        return 41;
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
                if (lu.getStringValue().equalsIgnoreCase("auto")) {
                    return SVGValueConstants.AUTO_VALUE;
                }
                throw this.createInvalidIdentifierDOMException(lu.getStringValue());
            }
            default: {
                return super.createValue(lu, engine);
            }
        }
    }
    
    @Override
    public Value createStringValue(final short type, final String value, final CSSEngine engine) throws DOMException {
        if (type != 21) {
            throw this.createInvalidStringTypeDOMException(type);
        }
        if (value.equalsIgnoreCase("auto")) {
            return SVGValueConstants.AUTO_VALUE;
        }
        throw this.createInvalidIdentifierDOMException(value);
    }
    
    @Override
    protected int getOrientation() {
        return 0;
    }
}
