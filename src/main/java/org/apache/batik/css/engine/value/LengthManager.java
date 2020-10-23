// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.CSSContext;
import org.w3c.dom.Element;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;

public abstract class LengthManager extends AbstractValueManager
{
    static final double SQRT2;
    protected static final int HORIZONTAL_ORIENTATION = 0;
    protected static final int VERTICAL_ORIENTATION = 1;
    protected static final int BOTH_ORIENTATION = 2;
    
    @Override
    public Value createValue(final LexicalUnit lu, final CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 15: {
                return new FloatValue((short)3, lu.getFloatValue());
            }
            case 16: {
                return new FloatValue((short)4, lu.getFloatValue());
            }
            case 17: {
                return new FloatValue((short)5, lu.getFloatValue());
            }
            case 19: {
                return new FloatValue((short)6, lu.getFloatValue());
            }
            case 20: {
                return new FloatValue((short)7, lu.getFloatValue());
            }
            case 18: {
                return new FloatValue((short)8, lu.getFloatValue());
            }
            case 21: {
                return new FloatValue((short)9, lu.getFloatValue());
            }
            case 22: {
                return new FloatValue((short)10, lu.getFloatValue());
            }
            case 13: {
                return new FloatValue((short)1, (float)lu.getIntegerValue());
            }
            case 14: {
                return new FloatValue((short)1, lu.getFloatValue());
            }
            case 23: {
                return new FloatValue((short)2, lu.getFloatValue());
            }
            default: {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
        }
    }
    
    @Override
    public Value createFloatValue(final short type, final float floatValue) throws DOMException {
        switch (type) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10: {
                return new FloatValue(type, floatValue);
            }
            default: {
                throw this.createInvalidFloatTypeDOMException(type);
            }
        }
    }
    
    @Override
    public Value computeValue(final CSSStylableElement elt, final String pseudo, final CSSEngine engine, final int idx, final StyleMap sm, final Value value) {
        if (value.getCssValueType() != 1) {
            return value;
        }
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
                sm.putFontSizeRelative(idx, true);
                final float v = value.getFloatValue();
                final int fsidx = engine.getFontSizeIndex();
                final float fs = engine.getComputedStyle(elt, pseudo, fsidx).getFloatValue();
                return new FloatValue((short)1, v * fs);
            }
            case 4: {
                sm.putFontSizeRelative(idx, true);
                final float v = value.getFloatValue();
                final int fsidx = engine.getFontSizeIndex();
                final float fs = engine.getComputedStyle(elt, pseudo, fsidx).getFloatValue();
                return new FloatValue((short)1, v * fs * 0.5f);
            }
            case 2: {
                final CSSContext ctx = engine.getCSSContext();
                float fs = 0.0f;
                switch (this.getOrientation()) {
                    case 0: {
                        sm.putBlockWidthRelative(idx, true);
                        fs = value.getFloatValue() * ctx.getBlockWidth(elt) / 100.0f;
                        break;
                    }
                    case 1: {
                        sm.putBlockHeightRelative(idx, true);
                        fs = value.getFloatValue() * ctx.getBlockHeight(elt) / 100.0f;
                        break;
                    }
                    default: {
                        sm.putBlockWidthRelative(idx, true);
                        sm.putBlockHeightRelative(idx, true);
                        final double w = ctx.getBlockWidth(elt);
                        final double h = ctx.getBlockHeight(elt);
                        fs = (float)(value.getFloatValue() * (Math.sqrt(w * w + h * h) / LengthManager.SQRT2) / 100.0);
                        break;
                    }
                }
                return new FloatValue((short)1, fs);
            }
            default: {
                return value;
            }
        }
    }
    
    protected abstract int getOrientation();
    
    static {
        SQRT2 = Math.sqrt(2.0);
    }
}
