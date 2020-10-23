// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.LengthManager;

public class EnableBackgroundManager extends LengthManager
{
    protected int orientation;
    
    @Override
    public boolean isInheritedProperty() {
        return false;
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
        return 23;
    }
    
    @Override
    public String getPropertyName() {
        return "enable-background";
    }
    
    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.ACCUMULATE_VALUE;
    }
    
    @Override
    public Value createValue(LexicalUnit lu, final CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return SVGValueConstants.INHERIT_VALUE;
            }
            default: {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
            case 35: {
                final String id = lu.getStringValue().toLowerCase().intern();
                if (id == "accumulate") {
                    return SVGValueConstants.ACCUMULATE_VALUE;
                }
                if (id != "new") {
                    throw this.createInvalidIdentifierDOMException(id);
                }
                final ListValue result = new ListValue(' ');
                result.append(SVGValueConstants.NEW_VALUE);
                lu = lu.getNextLexicalUnit();
                if (lu == null) {
                    return result;
                }
                result.append(super.createValue(lu, engine));
                for (int i = 1; i < 4; ++i) {
                    lu = lu.getNextLexicalUnit();
                    if (lu == null) {
                        throw this.createMalformedLexicalUnitDOMException();
                    }
                    result.append(super.createValue(lu, engine));
                }
                return result;
            }
        }
    }
    
    @Override
    public Value createStringValue(final short type, final String value, final CSSEngine engine) {
        if (type != 21) {
            throw this.createInvalidStringTypeDOMException(type);
        }
        if (!value.equalsIgnoreCase("accumulate")) {
            throw this.createInvalidIdentifierDOMException(value);
        }
        return SVGValueConstants.ACCUMULATE_VALUE;
    }
    
    @Override
    public Value createFloatValue(final short unitType, final float floatValue) throws DOMException {
        throw this.createDOMException();
    }
    
    @Override
    public Value computeValue(final CSSStylableElement elt, final String pseudo, final CSSEngine engine, final int idx, final StyleMap sm, final Value value) {
        if (value.getCssValueType() == 2) {
            final ListValue lv = (ListValue)value;
            if (lv.getLength() == 5) {
                final Value lv2 = lv.item(1);
                this.orientation = 0;
                final Value v1 = super.computeValue(elt, pseudo, engine, idx, sm, lv2);
                final Value lv3 = lv.item(2);
                this.orientation = 1;
                final Value v2 = super.computeValue(elt, pseudo, engine, idx, sm, lv3);
                final Value lv4 = lv.item(3);
                this.orientation = 0;
                final Value v3 = super.computeValue(elt, pseudo, engine, idx, sm, lv4);
                final Value lv5 = lv.item(4);
                this.orientation = 1;
                final Value v4 = super.computeValue(elt, pseudo, engine, idx, sm, lv5);
                if (lv2 != v1 || lv3 != v2 || lv4 != v3 || lv5 != v4) {
                    final ListValue result = new ListValue(' ');
                    result.append(lv.item(0));
                    result.append(v1);
                    result.append(v2);
                    result.append(v3);
                    result.append(v4);
                    return result;
                }
            }
        }
        return value;
    }
    
    @Override
    protected int getOrientation() {
        return this.orientation;
    }
}
