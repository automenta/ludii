// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg12;

import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.LengthManager;

public class LineHeightManager extends LengthManager
{
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
        return true;
    }
    
    @Override
    public int getPropertyType() {
        return 43;
    }
    
    @Override
    public String getPropertyName() {
        return "line-height";
    }
    
    @Override
    public Value getDefaultValue() {
        return SVG12ValueConstants.NORMAL_VALUE;
    }
    
    @Override
    public Value createValue(final LexicalUnit lu, final CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVG12ValueConstants.INHERIT_VALUE;
            }
            case 35: {
                final String s = lu.getStringValue().toLowerCase();
                if ("normal".equals(s)) {
                    return SVG12ValueConstants.NORMAL_VALUE;
                }
                throw this.createInvalidIdentifierDOMException(lu.getStringValue());
            }
            default: {
                return super.createValue(lu, engine);
            }
        }
    }
    
    @Override
    protected int getOrientation() {
        return 1;
    }
    
    @Override
    public Value computeValue(final CSSStylableElement elt, final String pseudo, final CSSEngine engine, final int idx, final StyleMap sm, final Value value) {
        if (value.getCssValueType() != 1) {
            return value;
        }
        switch (value.getPrimitiveType()) {
            case 1: {
                return new LineHeightValue((short)1, value.getFloatValue(), true);
            }
            case 2: {
                final float v = value.getFloatValue();
                final int fsidx = engine.getFontSizeIndex();
                final float fs = engine.getComputedStyle(elt, pseudo, fsidx).getFloatValue();
                return new FloatValue((short)1, v * fs * 0.01f);
            }
            default: {
                return super.computeValue(elt, pseudo, engine, idx, sm, value);
            }
        }
    }
}
