// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.AbstractValueManager;

public class OpacityManager extends AbstractValueManager
{
    protected boolean inherited;
    protected String property;
    
    public OpacityManager(final String prop, final boolean inherit) {
        this.property = prop;
        this.inherited = inherit;
    }
    
    @Override
    public boolean isInheritedProperty() {
        return this.inherited;
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
        return 25;
    }
    
    @Override
    public String getPropertyName() {
        return this.property;
    }
    
    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.NUMBER_1;
    }
    
    @Override
    public Value createValue(final LexicalUnit lu, final CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVGValueConstants.INHERIT_VALUE;
            }
            case 13: {
                return new FloatValue((short)1, (float)lu.getIntegerValue());
            }
            case 14: {
                return new FloatValue((short)1, lu.getFloatValue());
            }
            default: {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
        }
    }
    
    @Override
    public Value createFloatValue(final short type, final float floatValue) throws DOMException {
        if (type == 1) {
            return new FloatValue(type, floatValue);
        }
        throw this.createInvalidFloatTypeDOMException(type);
    }
}
