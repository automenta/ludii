// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value;

import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;

public abstract class IdentifierManager extends AbstractValueManager
{
    @Override
    public Value createValue(final LexicalUnit lu, final CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return ValueConstants.INHERIT_VALUE;
            }
            case 35: {
                final String s = lu.getStringValue().toLowerCase().intern();
                final Object v = this.getIdentifiers().get(s);
                if (v == null) {
                    throw this.createInvalidIdentifierDOMException(lu.getStringValue());
                }
                return (Value)v;
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
        final Object v = this.getIdentifiers().get(value.toLowerCase().intern());
        if (v == null) {
            throw this.createInvalidIdentifierDOMException(value);
        }
        return (Value)v;
    }
    
    public abstract StringMap getIdentifiers();
}
