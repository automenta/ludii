// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.AbstractValueManager;

public abstract class GlyphOrientationManager extends AbstractValueManager
{
    @Override
    public boolean isInheritedProperty() {
        return true;
    }
    
    @Override
    public boolean isAnimatableProperty() {
        return false;
    }
    
    @Override
    public boolean isAdditiveProperty() {
        return false;
    }
    
    @Override
    public int getPropertyType() {
        return 5;
    }
    
    @Override
    public Value createValue(final LexicalUnit lu, final CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVGValueConstants.INHERIT_VALUE;
            }
            case 28: {
                return new FloatValue((short)11, lu.getFloatValue());
            }
            case 29: {
                return new FloatValue((short)13, lu.getFloatValue());
            }
            case 30: {
                return new FloatValue((short)12, lu.getFloatValue());
            }
            case 13: {
                final int n = lu.getIntegerValue();
                return new FloatValue((short)11, (float)n);
            }
            case 14: {
                final float n2 = lu.getFloatValue();
                return new FloatValue((short)11, n2);
            }
            default: {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
        }
    }
    
    @Override
    public Value createFloatValue(final short type, final float floatValue) throws DOMException {
        switch (type) {
            case 11:
            case 12:
            case 13: {
                return new FloatValue(type, floatValue);
            }
            default: {
                throw this.createInvalidFloatValueDOMException(floatValue);
            }
        }
    }
}
