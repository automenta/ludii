// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.css2;

import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.URIValue;
import org.apache.batik.css.engine.value.AbstractValueFactory;
import org.apache.batik.css.engine.value.StringValue;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.IdentifierManager;

public class SrcManager extends IdentifierManager
{
    protected static final StringMap values;
    
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
        return 38;
    }
    
    @Override
    public String getPropertyName() {
        return "src";
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
            default: {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
            case 24:
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
                        case 24: {
                            final String uri = AbstractValueFactory.resolveURI(engine.getCSSBaseURI(), lu.getStringValue());
                            result.append(new URIValue(lu.getStringValue(), uri));
                            lu = lu.getNextLexicalUnit();
                            if (lu == null || lu.getLexicalUnitType() != 41) {
                                break;
                            }
                            if (!lu.getFunctionName().equalsIgnoreCase("format")) {
                                break;
                            }
                            lu = lu.getNextLexicalUnit();
                            break;
                        }
                        case 35: {
                            final StringBuffer sb = new StringBuffer(lu.getStringValue());
                            lu = lu.getNextLexicalUnit();
                            if (lu != null && lu.getLexicalUnitType() == 35) {
                                do {
                                    sb.append(' ');
                                    sb.append(lu.getStringValue());
                                    lu = lu.getNextLexicalUnit();
                                } while (lu != null && lu.getLexicalUnitType() == 35);
                                result.append(new StringValue((short)19, sb.toString()));
                                break;
                            }
                            final String id = sb.toString();
                            final String s = id.toLowerCase().intern();
                            final Value v = (Value)SrcManager.values.get(s);
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
    
    @Override
    public StringMap getIdentifiers() {
        return SrcManager.values;
    }
    
    static {
        (values = new StringMap()).put("none", ValueConstants.NONE_VALUE);
    }
}
