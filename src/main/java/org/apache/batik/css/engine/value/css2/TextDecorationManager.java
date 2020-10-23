// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.css2;

import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.AbstractValueManager;

public class TextDecorationManager extends AbstractValueManager
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
        return 18;
    }
    
    @Override
    public String getPropertyName() {
        return "text-decoration";
    }
    
    @Override
    public Value getDefaultValue() {
        return ValueConstants.NONE_VALUE;
    }
    
    @Override
    public Value createValue(LexicalUnit lu, final CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return ValueConstants.INHERIT_VALUE;
            }
            case 35: {
                if (lu.getStringValue().equalsIgnoreCase("none")) {
                    return ValueConstants.NONE_VALUE;
                }
                final ListValue lv = new ListValue(' ');
                while (lu.getLexicalUnitType() == 35) {
                    final String s = lu.getStringValue().toLowerCase().intern();
                    final Object obj = TextDecorationManager.values.get(s);
                    if (obj == null) {
                        throw this.createInvalidIdentifierDOMException(lu.getStringValue());
                    }
                    lv.append((Value)obj);
                    lu = lu.getNextLexicalUnit();
                    if (lu == null) {
                        return lv;
                    }
                }
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
            default: {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
        }
    }
    
    @Override
    public Value createStringValue(final short type, final String value, final CSSEngine engine) throws DOMException {
        if (type != 21) {
            throw this.createInvalidStringTypeDOMException(type);
        }
        if (!value.equalsIgnoreCase("none")) {
            throw this.createInvalidIdentifierDOMException(value);
        }
        return ValueConstants.NONE_VALUE;
    }
    
    static {
        (values = new StringMap()).put("blink", ValueConstants.BLINK_VALUE);
        TextDecorationManager.values.put("line-through", ValueConstants.LINE_THROUGH_VALUE);
        TextDecorationManager.values.put("overline", ValueConstants.OVERLINE_VALUE);
        TextDecorationManager.values.put("underline", ValueConstants.UNDERLINE_VALUE);
    }
}
