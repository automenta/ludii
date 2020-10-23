// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;

public abstract class AbstractColorManager extends IdentifierManager
{
    protected static final StringMap values;
    protected static final StringMap computedValues;
    
    @Override
    public Value createValue(LexicalUnit lu, final CSSEngine engine) throws DOMException {
        if (lu.getLexicalUnitType() == 27) {
            lu = lu.getParameters();
            final Value red = this.createColorComponent(lu);
            lu = lu.getNextLexicalUnit().getNextLexicalUnit();
            final Value green = this.createColorComponent(lu);
            lu = lu.getNextLexicalUnit().getNextLexicalUnit();
            final Value blue = this.createColorComponent(lu);
            return this.createRGBColor(red, green, blue);
        }
        return super.createValue(lu, engine);
    }
    
    @Override
    public Value computeValue(final CSSStylableElement elt, final String pseudo, final CSSEngine engine, final int idx, final StyleMap sm, final Value value) {
        if (value.getPrimitiveType() != 21) {
            return super.computeValue(elt, pseudo, engine, idx, sm, value);
        }
        final String ident = value.getStringValue();
        final Value v = (Value)AbstractColorManager.computedValues.get(ident);
        if (v != null) {
            return v;
        }
        if (AbstractColorManager.values.get(ident) == null) {
            throw new IllegalStateException("Not a system-color:" + ident);
        }
        return engine.getCSSContext().getSystemColor(ident);
    }
    
    protected Value createRGBColor(final Value r, final Value g, final Value b) {
        return new RGBColorValue(r, g, b);
    }
    
    protected Value createColorComponent(final LexicalUnit lu) throws DOMException {
        switch (lu.getLexicalUnitType()) {
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
                throw this.createInvalidRGBComponentUnitDOMException(lu.getLexicalUnitType());
            }
        }
    }
    
    @Override
    public StringMap getIdentifiers() {
        return AbstractColorManager.values;
    }
    
    private DOMException createInvalidRGBComponentUnitDOMException(final short type) {
        final Object[] p = { this.getPropertyName(), type };
        final String s = Messages.formatMessage("invalid.rgb.component.unit", p);
        return new DOMException((short)9, s);
    }
    
    static {
        (values = new StringMap()).put("aqua", ValueConstants.AQUA_VALUE);
        AbstractColorManager.values.put("black", ValueConstants.BLACK_VALUE);
        AbstractColorManager.values.put("blue", ValueConstants.BLUE_VALUE);
        AbstractColorManager.values.put("fuchsia", ValueConstants.FUCHSIA_VALUE);
        AbstractColorManager.values.put("gray", ValueConstants.GRAY_VALUE);
        AbstractColorManager.values.put("green", ValueConstants.GREEN_VALUE);
        AbstractColorManager.values.put("lime", ValueConstants.LIME_VALUE);
        AbstractColorManager.values.put("maroon", ValueConstants.MAROON_VALUE);
        AbstractColorManager.values.put("navy", ValueConstants.NAVY_VALUE);
        AbstractColorManager.values.put("olive", ValueConstants.OLIVE_VALUE);
        AbstractColorManager.values.put("purple", ValueConstants.PURPLE_VALUE);
        AbstractColorManager.values.put("red", ValueConstants.RED_VALUE);
        AbstractColorManager.values.put("silver", ValueConstants.SILVER_VALUE);
        AbstractColorManager.values.put("teal", ValueConstants.TEAL_VALUE);
        AbstractColorManager.values.put("white", ValueConstants.WHITE_VALUE);
        AbstractColorManager.values.put("yellow", ValueConstants.YELLOW_VALUE);
        AbstractColorManager.values.put("activeborder", ValueConstants.ACTIVEBORDER_VALUE);
        AbstractColorManager.values.put("activecaption", ValueConstants.ACTIVECAPTION_VALUE);
        AbstractColorManager.values.put("appworkspace", ValueConstants.APPWORKSPACE_VALUE);
        AbstractColorManager.values.put("background", ValueConstants.BACKGROUND_VALUE);
        AbstractColorManager.values.put("buttonface", ValueConstants.BUTTONFACE_VALUE);
        AbstractColorManager.values.put("buttonhighlight", ValueConstants.BUTTONHIGHLIGHT_VALUE);
        AbstractColorManager.values.put("buttonshadow", ValueConstants.BUTTONSHADOW_VALUE);
        AbstractColorManager.values.put("buttontext", ValueConstants.BUTTONTEXT_VALUE);
        AbstractColorManager.values.put("captiontext", ValueConstants.CAPTIONTEXT_VALUE);
        AbstractColorManager.values.put("graytext", ValueConstants.GRAYTEXT_VALUE);
        AbstractColorManager.values.put("highlight", ValueConstants.HIGHLIGHT_VALUE);
        AbstractColorManager.values.put("highlighttext", ValueConstants.HIGHLIGHTTEXT_VALUE);
        AbstractColorManager.values.put("inactiveborder", ValueConstants.INACTIVEBORDER_VALUE);
        AbstractColorManager.values.put("inactivecaption", ValueConstants.INACTIVECAPTION_VALUE);
        AbstractColorManager.values.put("inactivecaptiontext", ValueConstants.INACTIVECAPTIONTEXT_VALUE);
        AbstractColorManager.values.put("infobackground", ValueConstants.INFOBACKGROUND_VALUE);
        AbstractColorManager.values.put("infotext", ValueConstants.INFOTEXT_VALUE);
        AbstractColorManager.values.put("menu", ValueConstants.MENU_VALUE);
        AbstractColorManager.values.put("menutext", ValueConstants.MENUTEXT_VALUE);
        AbstractColorManager.values.put("scrollbar", ValueConstants.SCROLLBAR_VALUE);
        AbstractColorManager.values.put("threeddarkshadow", ValueConstants.THREEDDARKSHADOW_VALUE);
        AbstractColorManager.values.put("threedface", ValueConstants.THREEDFACE_VALUE);
        AbstractColorManager.values.put("threedhighlight", ValueConstants.THREEDHIGHLIGHT_VALUE);
        AbstractColorManager.values.put("threedlightshadow", ValueConstants.THREEDLIGHTSHADOW_VALUE);
        AbstractColorManager.values.put("threedshadow", ValueConstants.THREEDSHADOW_VALUE);
        AbstractColorManager.values.put("window", ValueConstants.WINDOW_VALUE);
        AbstractColorManager.values.put("windowframe", ValueConstants.WINDOWFRAME_VALUE);
        AbstractColorManager.values.put("windowtext", ValueConstants.WINDOWTEXT_VALUE);
        (computedValues = new StringMap()).put("black", ValueConstants.BLACK_RGB_VALUE);
        AbstractColorManager.computedValues.put("silver", ValueConstants.SILVER_RGB_VALUE);
        AbstractColorManager.computedValues.put("gray", ValueConstants.GRAY_RGB_VALUE);
        AbstractColorManager.computedValues.put("white", ValueConstants.WHITE_RGB_VALUE);
        AbstractColorManager.computedValues.put("maroon", ValueConstants.MAROON_RGB_VALUE);
        AbstractColorManager.computedValues.put("red", ValueConstants.RED_RGB_VALUE);
        AbstractColorManager.computedValues.put("purple", ValueConstants.PURPLE_RGB_VALUE);
        AbstractColorManager.computedValues.put("fuchsia", ValueConstants.FUCHSIA_RGB_VALUE);
        AbstractColorManager.computedValues.put("green", ValueConstants.GREEN_RGB_VALUE);
        AbstractColorManager.computedValues.put("lime", ValueConstants.LIME_RGB_VALUE);
        AbstractColorManager.computedValues.put("olive", ValueConstants.OLIVE_RGB_VALUE);
        AbstractColorManager.computedValues.put("yellow", ValueConstants.YELLOW_RGB_VALUE);
        AbstractColorManager.computedValues.put("navy", ValueConstants.NAVY_RGB_VALUE);
        AbstractColorManager.computedValues.put("blue", ValueConstants.BLUE_RGB_VALUE);
        AbstractColorManager.computedValues.put("teal", ValueConstants.TEAL_RGB_VALUE);
        AbstractColorManager.computedValues.put("aqua", ValueConstants.AQUA_RGB_VALUE);
    }
}
