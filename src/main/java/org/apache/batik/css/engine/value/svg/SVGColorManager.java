// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.value.svg12.DeviceColor;
import org.apache.batik.css.engine.value.svg12.CIELCHColor;
import org.apache.batik.css.engine.value.svg12.CIELabColor;
import org.apache.batik.css.engine.value.svg12.ICCNamedColor;
import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.Value;

public class SVGColorManager extends ColorManager
{
    protected String property;
    protected Value defaultValue;
    
    public SVGColorManager(final String prop) {
        this(prop, SVGValueConstants.BLACK_RGB_VALUE);
    }
    
    public SVGColorManager(final String prop, final Value v) {
        this.property = prop;
        this.defaultValue = v;
    }
    
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
        return true;
    }
    
    @Override
    public int getPropertyType() {
        return 6;
    }
    
    @Override
    public String getPropertyName() {
        return this.property;
    }
    
    @Override
    public Value getDefaultValue() {
        return this.defaultValue;
    }
    
    @Override
    public Value createValue(LexicalUnit lu, final CSSEngine engine) throws DOMException {
        if (lu.getLexicalUnitType() == 35 && lu.getStringValue().equalsIgnoreCase("currentcolor")) {
            return SVGValueConstants.CURRENTCOLOR_VALUE;
        }
        final Value v = super.createValue(lu, engine);
        lu = lu.getNextLexicalUnit();
        if (lu == null) {
            return v;
        }
        if (lu.getLexicalUnitType() != 41) {
            throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
        }
        final ListValue result = new ListValue(' ');
        result.append(v);
        final Value colorValue = this.parseColorFunction(lu, v);
        if (colorValue != null) {
            result.append(colorValue);
            return result;
        }
        return v;
    }
    
    private Value parseColorFunction(final LexicalUnit lu, final Value v) {
        final String functionName = lu.getFunctionName();
        if (functionName.equalsIgnoreCase("icc-color")) {
            return this.createICCColorValue(lu, v);
        }
        return this.parseColor12Function(lu, v);
    }
    
    private Value parseColor12Function(final LexicalUnit lu, final Value v) {
        final String functionName = lu.getFunctionName();
        if (functionName.equalsIgnoreCase("icc-named-color")) {
            return this.createICCNamedColorValue(lu, v);
        }
        if (functionName.equalsIgnoreCase("cielab")) {
            return this.createCIELabColorValue(lu, v);
        }
        if (functionName.equalsIgnoreCase("cielch")) {
            return this.createCIELCHColorValue(lu, v);
        }
        if (functionName.equalsIgnoreCase("device-cmyk")) {
            return this.createDeviceColorValue(lu, v, 4);
        }
        if (functionName.equalsIgnoreCase("device-rgb")) {
            return this.createDeviceColorValue(lu, v, 3);
        }
        if (functionName.equalsIgnoreCase("device-gray")) {
            return this.createDeviceColorValue(lu, v, 1);
        }
        if (functionName.equalsIgnoreCase("device-nchannel")) {
            return this.createDeviceColorValue(lu, v, 0);
        }
        return null;
    }
    
    private Value createICCColorValue(LexicalUnit lu, final Value v) {
        lu = lu.getParameters();
        this.expectIdent(lu);
        final ICCColor icc = new ICCColor(lu.getStringValue());
        for (lu = lu.getNextLexicalUnit(); lu != null; lu = lu.getNextLexicalUnit()) {
            this.expectComma(lu);
            lu = lu.getNextLexicalUnit();
            icc.append(this.getColorValue(lu));
        }
        return icc;
    }
    
    private Value createICCNamedColorValue(LexicalUnit lu, final Value v) {
        lu = lu.getParameters();
        this.expectIdent(lu);
        final String profileName = lu.getStringValue();
        lu = lu.getNextLexicalUnit();
        this.expectComma(lu);
        lu = lu.getNextLexicalUnit();
        this.expectIdent(lu);
        final String colorName = lu.getStringValue();
        final ICCNamedColor icc = new ICCNamedColor(profileName, colorName);
        lu = lu.getNextLexicalUnit();
        return icc;
    }
    
    private Value createCIELabColorValue(LexicalUnit lu, final Value v) {
        lu = lu.getParameters();
        final float l = this.getColorValue(lu);
        lu = lu.getNextLexicalUnit();
        this.expectComma(lu);
        lu = lu.getNextLexicalUnit();
        final float a = this.getColorValue(lu);
        lu = lu.getNextLexicalUnit();
        this.expectComma(lu);
        lu = lu.getNextLexicalUnit();
        final float b = this.getColorValue(lu);
        final CIELabColor icc = new CIELabColor(l, a, b);
        lu = lu.getNextLexicalUnit();
        return icc;
    }
    
    private Value createCIELCHColorValue(LexicalUnit lu, final Value v) {
        lu = lu.getParameters();
        final float l = this.getColorValue(lu);
        lu = lu.getNextLexicalUnit();
        this.expectComma(lu);
        lu = lu.getNextLexicalUnit();
        final float c = this.getColorValue(lu);
        lu = lu.getNextLexicalUnit();
        this.expectComma(lu);
        lu = lu.getNextLexicalUnit();
        final float h = this.getColorValue(lu);
        final CIELCHColor icc = new CIELCHColor(l, c, h);
        lu = lu.getNextLexicalUnit();
        return icc;
    }
    
    private Value createDeviceColorValue(LexicalUnit lu, final Value v, final int expectedComponents) {
        lu = lu.getParameters();
        final boolean nChannel = expectedComponents <= 0;
        final DeviceColor col = new DeviceColor(nChannel);
        col.append(this.getColorValue(lu));
        LexicalUnit lastUnit = lu;
        for (lu = lu.getNextLexicalUnit(); lu != null; lu = lu.getNextLexicalUnit()) {
            this.expectComma(lu);
            lu = lu.getNextLexicalUnit();
            col.append(this.getColorValue(lu));
            lastUnit = lu;
        }
        if (!nChannel && expectedComponents != col.getNumberOfColors()) {
            throw this.createInvalidLexicalUnitDOMException(lastUnit.getLexicalUnitType());
        }
        return col;
    }
    
    private void expectIdent(final LexicalUnit lu) {
        if (lu.getLexicalUnitType() != 35) {
            throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
        }
    }
    
    private void expectComma(final LexicalUnit lu) {
        if (lu.getLexicalUnitType() != 0) {
            throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
        }
    }
    
    private void expectNonNull(final LexicalUnit lu) {
        if (lu == null) {
            throw this.createInvalidLexicalUnitDOMException((short)(-1));
        }
    }
    
    @Override
    public Value computeValue(final CSSStylableElement elt, final String pseudo, final CSSEngine engine, final int idx, final StyleMap sm, final Value value) {
        if (value == SVGValueConstants.CURRENTCOLOR_VALUE) {
            sm.putColorRelative(idx, true);
            final int ci = engine.getColorIndex();
            return engine.getComputedStyle(elt, pseudo, ci);
        }
        if (value.getCssValueType() != 2) {
            return super.computeValue(elt, pseudo, engine, idx, sm, value);
        }
        final ListValue lv = (ListValue)value;
        final Value v = lv.item(0);
        final Value t = super.computeValue(elt, pseudo, engine, idx, sm, v);
        if (t != v) {
            final ListValue result = new ListValue(' ');
            result.append(t);
            result.append(lv.item(1));
            return result;
        }
        return value;
    }
    
    protected float getColorValue(final LexicalUnit lu) {
        this.expectNonNull(lu);
        switch (lu.getLexicalUnitType()) {
            case 13: {
                return (float)lu.getIntegerValue();
            }
            case 14: {
                return lu.getFloatValue();
            }
            default: {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
        }
    }
}
