// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.CSSContext;
import org.w3c.dom.Element;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.LengthManager;

public class FontSizeManager extends LengthManager
{
    protected static final StringMap values;
    
    public StringMap getIdentifiers() {
        return FontSizeManager.values;
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
    public String getPropertyName() {
        return "font-size";
    }
    
    @Override
    public int getPropertyType() {
        return 39;
    }
    
    @Override
    public Value getDefaultValue() {
        return ValueConstants.MEDIUM_VALUE;
    }
    
    @Override
    public Value createValue(final LexicalUnit lu, final CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return ValueConstants.INHERIT_VALUE;
            }
            case 35: {
                final String s = lu.getStringValue().toLowerCase().intern();
                final Object v = FontSizeManager.values.get(s);
                if (v == null) {
                    throw this.createInvalidIdentifierDOMException(s);
                }
                return (Value)v;
            }
            default: {
                return super.createValue(lu, engine);
            }
        }
    }
    
    @Override
    public Value createStringValue(final short type, final String value, final CSSEngine engine) throws DOMException {
        if (type != 21) {
            throw this.createInvalidStringTypeDOMException(type);
        }
        final Object v = FontSizeManager.values.get(value.toLowerCase().intern());
        if (v == null) {
            throw this.createInvalidIdentifierDOMException(value);
        }
        return (Value)v;
    }
    
    @Override
    public Value computeValue(final CSSStylableElement elt, final String pseudo, final CSSEngine engine, final int idx, final StyleMap sm, final Value value) {
        float scale = 1.0f;
        boolean doParentRelative = false;
        switch (value.getPrimitiveType()) {
            case 1:
            case 5: {
                return value;
            }
            case 7: {
                final CSSContext ctx = engine.getCSSContext();
                final float v = value.getFloatValue();
                return new FloatValue((short)1, v / ctx.getPixelUnitToMillimeter());
            }
            case 6: {
                final CSSContext ctx = engine.getCSSContext();
                final float v = value.getFloatValue();
                return new FloatValue((short)1, v * 10.0f / ctx.getPixelUnitToMillimeter());
            }
            case 8: {
                final CSSContext ctx = engine.getCSSContext();
                final float v = value.getFloatValue();
                return new FloatValue((short)1, v * 25.4f / ctx.getPixelUnitToMillimeter());
            }
            case 9: {
                final CSSContext ctx = engine.getCSSContext();
                final float v = value.getFloatValue();
                return new FloatValue((short)1, v * 25.4f / (72.0f * ctx.getPixelUnitToMillimeter()));
            }
            case 10: {
                final CSSContext ctx = engine.getCSSContext();
                final float v = value.getFloatValue();
                return new FloatValue((short)1, v * 25.4f / (6.0f * ctx.getPixelUnitToMillimeter()));
            }
            case 3: {
                doParentRelative = true;
                scale = value.getFloatValue();
                break;
            }
            case 4: {
                doParentRelative = true;
                scale = value.getFloatValue() * 0.5f;
                break;
            }
            case 2: {
                doParentRelative = true;
                scale = value.getFloatValue() * 0.01f;
                break;
            }
        }
        if (value == ValueConstants.LARGER_VALUE) {
            doParentRelative = true;
            scale = 1.2f;
        }
        else if (value == ValueConstants.SMALLER_VALUE) {
            doParentRelative = true;
            scale = 0.8333333f;
        }
        if (doParentRelative) {
            sm.putParentRelative(idx, true);
            final CSSStylableElement p = CSSEngine.getParentCSSStylableElement(elt);
            float fs;
            if (p == null) {
                final CSSContext ctx2 = engine.getCSSContext();
                fs = ctx2.getMediumFontSize();
            }
            else {
                fs = engine.getComputedStyle(p, null, idx).getFloatValue();
            }
            return new FloatValue((short)1, fs * scale);
        }
        final CSSContext ctx = engine.getCSSContext();
        float fs = ctx.getMediumFontSize();
        final String s = value.getStringValue();
        Label_0655: {
            switch (s.charAt(0)) {
                case 'm': {
                    break;
                }
                case 's': {
                    fs /= (float)1.2;
                    break;
                }
                case 'l': {
                    fs *= (float)1.2;
                    break;
                }
                default: {
                    switch (s.charAt(1)) {
                        case 'x': {
                            switch (s.charAt(3)) {
                                case 's': {
                                    fs = (float)(fs / 1.2 / 1.2 / 1.2);
                                    break Label_0655;
                                }
                                default: {
                                    fs = (float)(fs * 1.2 * 1.2 * 1.2);
                                    break Label_0655;
                                }
                            }
                            break;
                        }
                        default: {
                            switch (s.charAt(2)) {
                                case 's': {
                                    fs = (float)(fs / 1.2 / 1.2);
                                    break Label_0655;
                                }
                                default: {
                                    fs = (float)(fs * 1.2 * 1.2);
                                    break Label_0655;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return new FloatValue((short)1, fs);
    }
    
    @Override
    protected int getOrientation() {
        return 1;
    }
    
    static {
        (values = new StringMap()).put("all", ValueConstants.ALL_VALUE);
        FontSizeManager.values.put("large", ValueConstants.LARGE_VALUE);
        FontSizeManager.values.put("larger", ValueConstants.LARGER_VALUE);
        FontSizeManager.values.put("medium", ValueConstants.MEDIUM_VALUE);
        FontSizeManager.values.put("small", ValueConstants.SMALL_VALUE);
        FontSizeManager.values.put("smaller", ValueConstants.SMALLER_VALUE);
        FontSizeManager.values.put("x-large", ValueConstants.X_LARGE_VALUE);
        FontSizeManager.values.put("x-small", ValueConstants.X_SMALL_VALUE);
        FontSizeManager.values.put("xx-large", ValueConstants.XX_LARGE_VALUE);
        FontSizeManager.values.put("xx-small", ValueConstants.XX_SMALL_VALUE);
    }
}
