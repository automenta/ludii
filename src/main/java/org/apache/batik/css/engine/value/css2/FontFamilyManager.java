// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.CSSContext;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.StringValue;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.AbstractValueManager;

public class FontFamilyManager extends AbstractValueManager
{
    protected static final ListValue DEFAULT_VALUE;
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
        return 26;
    }
    
    @Override
    public String getPropertyName() {
        return "font-family";
    }
    
    @Override
    public Value getDefaultValue() {
        return FontFamilyManager.DEFAULT_VALUE;
    }
    
    @Override
    public Value createValue(LexicalUnit lu, final CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return ValueConstants.INHERIT_VALUE;
            }
            default: {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
            case 35:
            case 36: {
                final ListValue result = new ListValue();
                do {
                    switch (lu.getLexicalUnitType()) {
                        case 36: {
                            result.append(new StringValue((short)19, lu.getStringValue()));
                            lu = lu.getNextLexicalUnit();
                            break;
                        }
                        case 35: {
                            final StringBuffer sb = new StringBuffer(lu.getStringValue());
                            lu = lu.getNextLexicalUnit();
                            if (lu != null && this.isIdentOrNumber(lu)) {
                                do {
                                    sb.append(' ');
                                    switch (lu.getLexicalUnitType()) {
                                        case 35: {
                                            sb.append(lu.getStringValue());
                                            break;
                                        }
                                        case 13: {
                                            sb.append(Integer.toString(lu.getIntegerValue()));
                                            break;
                                        }
                                    }
                                    lu = lu.getNextLexicalUnit();
                                } while (lu != null && this.isIdentOrNumber(lu));
                                result.append(new StringValue((short)19, sb.toString()));
                                break;
                            }
                            final String id = sb.toString();
                            final String s = id.toLowerCase().intern();
                            final Value v = (Value)FontFamilyManager.values.get(s);
                            result.append((v != null) ? v : new StringValue((short)19, id));
                            break;
                        }
                    }
                    if (lu == null) {
                        return result;
                    }
                    if (lu.getLexicalUnitType() != 0) {
                        throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
                    }
                    lu = lu.getNextLexicalUnit();
                } while (lu != null);
                throw this.createMalformedLexicalUnitDOMException();
            }
        }
    }
    
    private boolean isIdentOrNumber(final LexicalUnit lu) {
        final short type = lu.getLexicalUnitType();
        switch (type) {
            case 13:
            case 35: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public Value computeValue(final CSSStylableElement elt, final String pseudo, final CSSEngine engine, final int idx, final StyleMap sm, Value value) {
        if (value == FontFamilyManager.DEFAULT_VALUE) {
            final CSSContext ctx = engine.getCSSContext();
            value = ctx.getDefaultFontFamily();
        }
        return value;
    }
    
    static {
        (DEFAULT_VALUE = new ListValue()).append(new StringValue((short)19, "Arial"));
        FontFamilyManager.DEFAULT_VALUE.append(new StringValue((short)19, "Helvetica"));
        FontFamilyManager.DEFAULT_VALUE.append(new StringValue((short)21, "sans-serif"));
        (values = new StringMap()).put("cursive", ValueConstants.CURSIVE_VALUE);
        FontFamilyManager.values.put("fantasy", ValueConstants.FANTASY_VALUE);
        FontFamilyManager.values.put("monospace", ValueConstants.MONOSPACE_VALUE);
        FontFamilyManager.values.put("serif", ValueConstants.SERIF_VALUE);
        FontFamilyManager.values.put("sans-serif", ValueConstants.SANS_SERIF_VALUE);
    }
}
