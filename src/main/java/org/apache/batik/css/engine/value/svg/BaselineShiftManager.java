// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.LengthManager;

public class BaselineShiftManager extends LengthManager
{
    protected static final StringMap values;
    
    @Override
    public boolean isInheritedProperty() {
        return false;
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
        return 40;
    }
    
    @Override
    public String getPropertyName() {
        return "baseline-shift";
    }
    
    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.BASELINE_VALUE;
    }
    
    @Override
    public Value createValue(final LexicalUnit lu, final CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVGValueConstants.INHERIT_VALUE;
            }
            case 35: {
                final Object v = BaselineShiftManager.values.get(lu.getStringValue().toLowerCase().intern());
                if (v == null) {
                    throw this.createInvalidIdentifierDOMException(lu.getStringValue());
                }
                return (Value)v;
            }
            default: {
                return super.createValue(lu, engine);
            }
        }
    }
    
    @Override
    public Value createStringValue(final short type, final String value, final CSSEngine engine) throws DOMException {
        if (type != 21) {
            throw this.createInvalidIdentifierDOMException(value);
        }
        final Object v = BaselineShiftManager.values.get(value.toLowerCase().intern());
        if (v == null) {
            throw this.createInvalidIdentifierDOMException(value);
        }
        return (Value)v;
    }
    
    @Override
    public Value computeValue(final CSSStylableElement elt, final String pseudo, final CSSEngine engine, final int idx, final StyleMap sm, final Value value) {
        if (value.getPrimitiveType() == 2) {
            sm.putLineHeightRelative(idx, true);
            final int fsi = engine.getLineHeightIndex();
            CSSStylableElement parent = (CSSStylableElement)elt.getParentNode();
            if (parent == null) {
                parent = elt;
            }
            final Value fs = engine.getComputedStyle(parent, pseudo, fsi);
            final float fsv = fs.getFloatValue();
            final float v = value.getFloatValue();
            return new FloatValue((short)1, fsv * v / 100.0f);
        }
        return super.computeValue(elt, pseudo, engine, idx, sm, value);
    }
    
    @Override
    protected int getOrientation() {
        return 2;
    }
    
    static {
        (values = new StringMap()).put("baseline", SVGValueConstants.BASELINE_VALUE);
        BaselineShiftManager.values.put("sub", SVGValueConstants.SUB_VALUE);
        BaselineShiftManager.values.put("super", SVGValueConstants.SUPER_VALUE);
    }
}
