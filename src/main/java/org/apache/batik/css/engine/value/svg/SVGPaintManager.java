// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.URIValue;
import org.apache.batik.css.engine.value.AbstractValueFactory;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.Value;

public class SVGPaintManager extends SVGColorManager
{
    public SVGPaintManager(final String prop) {
        super(prop);
    }
    
    public SVGPaintManager(final String prop, final Value v) {
        super(prop, v);
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
        return true;
    }
    
    @Override
    public int getPropertyType() {
        return 7;
    }
    
    @Override
    public Value createValue(LexicalUnit lu, final CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 35: {
                if (lu.getStringValue().equalsIgnoreCase("none")) {
                    return SVGValueConstants.NONE_VALUE;
                }
                break;
            }
            case 24: {
                final String value = lu.getStringValue();
                final String uri = AbstractValueFactory.resolveURI(engine.getCSSBaseURI(), value);
                lu = lu.getNextLexicalUnit();
                if (lu == null) {
                    return new URIValue(value, uri);
                }
                final ListValue result = new ListValue(' ');
                result.append(new URIValue(value, uri));
                if (lu.getLexicalUnitType() == 35 && lu.getStringValue().equalsIgnoreCase("none")) {
                    result.append(SVGValueConstants.NONE_VALUE);
                    return result;
                }
                final Value v = super.createValue(lu, engine);
                if (v.getCssValueType() == 3) {
                    final ListValue lv = (ListValue)v;
                    for (int i = 0; i < lv.getLength(); ++i) {
                        result.append(lv.item(i));
                    }
                }
                else {
                    result.append(v);
                }
                return result;
            }
        }
        return super.createValue(lu, engine);
    }
    
    @Override
    public Value computeValue(final CSSStylableElement elt, final String pseudo, final CSSEngine engine, final int idx, final StyleMap sm, final Value value) {
        if (value == SVGValueConstants.NONE_VALUE) {
            return value;
        }
        if (value.getCssValueType() == 2) {
            final ListValue lv = (ListValue)value;
            Value v = lv.item(0);
            if (v.getPrimitiveType() == 20) {
                v = lv.item(1);
                if (v == SVGValueConstants.NONE_VALUE) {
                    return value;
                }
                final Value t = super.computeValue(elt, pseudo, engine, idx, sm, v);
                if (t != v) {
                    final ListValue result = new ListValue(' ');
                    result.append(lv.item(0));
                    result.append(t);
                    if (lv.getLength() == 3) {
                        result.append(lv.item(1));
                    }
                    return result;
                }
                return value;
            }
        }
        return super.computeValue(elt, pseudo, engine, idx, sm, value);
    }
}
