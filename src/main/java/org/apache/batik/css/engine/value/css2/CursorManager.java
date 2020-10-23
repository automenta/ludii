// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.URIValue;
import org.apache.batik.css.engine.value.AbstractValueFactory;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.AbstractValueManager;

public class CursorManager extends AbstractValueManager
{
    protected static final StringMap values;
    
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
        return 21;
    }
    
    @Override
    public String getPropertyName() {
        return "cursor";
    }
    
    @Override
    public Value getDefaultValue() {
        return ValueConstants.AUTO_VALUE;
    }
    
    @Override
    public Value createValue(LexicalUnit lu, final CSSEngine engine) throws DOMException {
        final ListValue result = new ListValue();
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return ValueConstants.INHERIT_VALUE;
            }
            case 24: {
                do {
                    result.append(new URIValue(lu.getStringValue(), AbstractValueFactory.resolveURI(engine.getCSSBaseURI(), lu.getStringValue())));
                    lu = lu.getNextLexicalUnit();
                    if (lu == null) {
                        throw this.createMalformedLexicalUnitDOMException();
                    }
                    if (lu.getLexicalUnitType() != 0) {
                        throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
                    }
                    lu = lu.getNextLexicalUnit();
                    if (lu == null) {
                        throw this.createMalformedLexicalUnitDOMException();
                    }
                } while (lu.getLexicalUnitType() == 24);
                if (lu.getLexicalUnitType() != 35) {
                    throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
                }
            }
            case 35: {
                final String s = lu.getStringValue().toLowerCase().intern();
                final Object v = CursorManager.values.get(s);
                if (v == null) {
                    throw this.createInvalidIdentifierDOMException(lu.getStringValue());
                }
                result.append((Value)v);
                lu = lu.getNextLexicalUnit();
                break;
            }
        }
        if (lu != null) {
            throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
        }
        return result;
    }
    
    @Override
    public Value computeValue(final CSSStylableElement elt, final String pseudo, final CSSEngine engine, final int idx, final StyleMap sm, final Value value) {
        if (value.getCssValueType() == 2) {
            final ListValue lv = (ListValue)value;
            final int len = lv.getLength();
            final ListValue result = new ListValue(' ');
            for (int i = 0; i < len; ++i) {
                final Value v = lv.item(0);
                if (v.getPrimitiveType() == 20) {
                    result.append(new URIValue(v.getStringValue(), v.getStringValue()));
                }
                else {
                    result.append(v);
                }
            }
            return result;
        }
        return super.computeValue(elt, pseudo, engine, idx, sm, value);
    }
    
    static {
        (values = new StringMap()).put("auto", ValueConstants.AUTO_VALUE);
        CursorManager.values.put("crosshair", ValueConstants.CROSSHAIR_VALUE);
        CursorManager.values.put("default", ValueConstants.DEFAULT_VALUE);
        CursorManager.values.put("e-resize", ValueConstants.E_RESIZE_VALUE);
        CursorManager.values.put("help", ValueConstants.HELP_VALUE);
        CursorManager.values.put("move", ValueConstants.MOVE_VALUE);
        CursorManager.values.put("n-resize", ValueConstants.N_RESIZE_VALUE);
        CursorManager.values.put("ne-resize", ValueConstants.NE_RESIZE_VALUE);
        CursorManager.values.put("nw-resize", ValueConstants.NW_RESIZE_VALUE);
        CursorManager.values.put("pointer", ValueConstants.POINTER_VALUE);
        CursorManager.values.put("s-resize", ValueConstants.S_RESIZE_VALUE);
        CursorManager.values.put("se-resize", ValueConstants.SE_RESIZE_VALUE);
        CursorManager.values.put("sw-resize", ValueConstants.SW_RESIZE_VALUE);
        CursorManager.values.put("text", ValueConstants.TEXT_VALUE);
        CursorManager.values.put("w-resize", ValueConstants.W_RESIZE_VALUE);
        CursorManager.values.put("wait", ValueConstants.WAIT_VALUE);
    }
}
