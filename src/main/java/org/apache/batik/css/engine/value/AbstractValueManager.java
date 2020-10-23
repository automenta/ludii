// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.dom.DOMException;

public abstract class AbstractValueManager extends AbstractValueFactory implements ValueManager
{
    @Override
    public Value createFloatValue(final short unitType, final float floatValue) throws DOMException {
        throw this.createDOMException();
    }
    
    @Override
    public Value createStringValue(final short type, final String value, final CSSEngine engine) throws DOMException {
        throw this.createDOMException();
    }
    
    @Override
    public Value computeValue(final CSSStylableElement elt, final String pseudo, final CSSEngine engine, final int idx, final StyleMap sm, final Value value) {
        if (value.getCssValueType() == 1 && value.getPrimitiveType() == 20) {
            return new URIValue(value.getStringValue(), value.getStringValue());
        }
        return value;
    }
}
