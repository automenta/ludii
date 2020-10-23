// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.Value;

public class GlyphOrientationVerticalManager extends GlyphOrientationManager
{
    @Override
    public String getPropertyName() {
        return "glyph-orientation-vertical";
    }
    
    @Override
    public Value getDefaultValue() {
        return SVGValueConstants.AUTO_VALUE;
    }
    
    @Override
    public Value createValue(final LexicalUnit lu, final CSSEngine engine) throws DOMException {
        if (lu.getLexicalUnitType() != 35) {
            return super.createValue(lu, engine);
        }
        if (lu.getStringValue().equalsIgnoreCase("auto")) {
            return SVGValueConstants.AUTO_VALUE;
        }
        throw this.createInvalidIdentifierDOMException(lu.getStringValue());
    }
    
    @Override
    public Value createStringValue(final short type, final String value, final CSSEngine engine) throws DOMException {
        if (type != 21) {
            throw this.createInvalidStringTypeDOMException(type);
        }
        if (value.equalsIgnoreCase("auto")) {
            return SVGValueConstants.AUTO_VALUE;
        }
        throw this.createInvalidIdentifierDOMException(value);
    }
}
