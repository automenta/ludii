// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.URIValue;
import org.apache.batik.css.engine.value.AbstractValueFactory;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.AbstractValueManager;

public class MarkerManager extends AbstractValueManager
{
    protected String property;
    
    public MarkerManager(final String prop) {
        this.property = prop;
    }
    
    @Override
    public boolean isInheritedProperty() {
        return true;
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
    public String getPropertyName() {
        return this.property;
    }
    
    @Override
    public Value getDefaultValue() {
        return ValueConstants.NONE_VALUE;
    }
    
    @Override
    public Value createValue(final LexicalUnit lu, final CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return ValueConstants.INHERIT_VALUE;
            }
            case 24: {
                return new URIValue(lu.getStringValue(), AbstractValueFactory.resolveURI(engine.getCSSBaseURI(), lu.getStringValue()));
            }
            case 35: {
                if (lu.getStringValue().equalsIgnoreCase("none")) {
                    return ValueConstants.NONE_VALUE;
                }
                break;
            }
        }
        throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }
    
    @Override
    public Value createStringValue(final short type, final String value, final CSSEngine engine) throws DOMException {
        switch (type) {
            case 21: {
                if (value.equalsIgnoreCase("none")) {
                    return ValueConstants.NONE_VALUE;
                }
                break;
            }
            case 20: {
                return new URIValue(value, AbstractValueFactory.resolveURI(engine.getCSSBaseURI(), value));
            }
        }
        throw this.createInvalidStringTypeDOMException(type);
    }
}
