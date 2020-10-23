// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.CSSContext;
import org.w3c.dom.Element;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.IdentifierManager;

public class FontWeightManager extends IdentifierManager
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
        return 28;
    }
    
    @Override
    public String getPropertyName() {
        return "font-weight";
    }
    
    @Override
    public Value getDefaultValue() {
        return ValueConstants.NORMAL_VALUE;
    }
    
    @Override
    public Value createValue(final LexicalUnit lu, final CSSEngine engine) throws DOMException {
        if (lu.getLexicalUnitType() != 13) {
            return super.createValue(lu, engine);
        }
        final int i = lu.getIntegerValue();
        switch (i) {
            case 100: {
                return ValueConstants.NUMBER_100;
            }
            case 200: {
                return ValueConstants.NUMBER_200;
            }
            case 300: {
                return ValueConstants.NUMBER_300;
            }
            case 400: {
                return ValueConstants.NUMBER_400;
            }
            case 500: {
                return ValueConstants.NUMBER_500;
            }
            case 600: {
                return ValueConstants.NUMBER_600;
            }
            case 700: {
                return ValueConstants.NUMBER_700;
            }
            case 800: {
                return ValueConstants.NUMBER_800;
            }
            case 900: {
                return ValueConstants.NUMBER_900;
            }
            default: {
                throw this.createInvalidFloatValueDOMException((float)i);
            }
        }
    }
    
    @Override
    public Value createFloatValue(final short type, final float floatValue) throws DOMException {
        if (type == 1) {
            final int i = (int)floatValue;
            if (floatValue == i) {
                switch (i) {
                    case 100: {
                        return ValueConstants.NUMBER_100;
                    }
                    case 200: {
                        return ValueConstants.NUMBER_200;
                    }
                    case 300: {
                        return ValueConstants.NUMBER_300;
                    }
                    case 400: {
                        return ValueConstants.NUMBER_400;
                    }
                    case 500: {
                        return ValueConstants.NUMBER_500;
                    }
                    case 600: {
                        return ValueConstants.NUMBER_600;
                    }
                    case 700: {
                        return ValueConstants.NUMBER_700;
                    }
                    case 800: {
                        return ValueConstants.NUMBER_800;
                    }
                    case 900: {
                        return ValueConstants.NUMBER_900;
                    }
                }
            }
        }
        throw this.createInvalidFloatValueDOMException(floatValue);
    }
    
    @Override
    public Value computeValue(final CSSStylableElement elt, final String pseudo, final CSSEngine engine, final int idx, final StyleMap sm, final Value value) {
        if (value == ValueConstants.BOLDER_VALUE) {
            sm.putParentRelative(idx, true);
            final CSSContext ctx = engine.getCSSContext();
            final CSSStylableElement p = CSSEngine.getParentCSSStylableElement(elt);
            float fw;
            if (p == null) {
                fw = 400.0f;
            }
            else {
                final Value v = engine.getComputedStyle(p, pseudo, idx);
                fw = v.getFloatValue();
            }
            return this.createFontWeight(ctx.getBolderFontWeight(fw));
        }
        if (value == ValueConstants.LIGHTER_VALUE) {
            sm.putParentRelative(idx, true);
            final CSSContext ctx = engine.getCSSContext();
            final CSSStylableElement p = CSSEngine.getParentCSSStylableElement(elt);
            float fw;
            if (p == null) {
                fw = 400.0f;
            }
            else {
                final Value v = engine.getComputedStyle(p, pseudo, idx);
                fw = v.getFloatValue();
            }
            return this.createFontWeight(ctx.getLighterFontWeight(fw));
        }
        if (value == ValueConstants.NORMAL_VALUE) {
            return ValueConstants.NUMBER_400;
        }
        if (value == ValueConstants.BOLD_VALUE) {
            return ValueConstants.NUMBER_700;
        }
        return value;
    }
    
    protected Value createFontWeight(final float f) {
        switch ((int)f) {
            case 100: {
                return ValueConstants.NUMBER_100;
            }
            case 200: {
                return ValueConstants.NUMBER_200;
            }
            case 300: {
                return ValueConstants.NUMBER_300;
            }
            case 400: {
                return ValueConstants.NUMBER_400;
            }
            case 500: {
                return ValueConstants.NUMBER_500;
            }
            case 600: {
                return ValueConstants.NUMBER_600;
            }
            case 700: {
                return ValueConstants.NUMBER_700;
            }
            case 800: {
                return ValueConstants.NUMBER_800;
            }
            default: {
                return ValueConstants.NUMBER_900;
            }
        }
    }
    
    @Override
    public StringMap getIdentifiers() {
        return FontWeightManager.values;
    }
    
    static {
        (values = new StringMap()).put("all", ValueConstants.ALL_VALUE);
        FontWeightManager.values.put("bold", ValueConstants.BOLD_VALUE);
        FontWeightManager.values.put("bolder", ValueConstants.BOLDER_VALUE);
        FontWeightManager.values.put("lighter", ValueConstants.LIGHTER_VALUE);
        FontWeightManager.values.put("normal", ValueConstants.NORMAL_VALUE);
    }
}
