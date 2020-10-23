// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.css2;

import org.w3c.dom.Element;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.IdentifierManager;

public class FontStretchManager extends IdentifierManager
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
        return 15;
    }
    
    @Override
    public String getPropertyName() {
        return "font-stretch";
    }
    
    @Override
    public Value getDefaultValue() {
        return ValueConstants.NORMAL_VALUE;
    }
    
    @Override
    public Value computeValue(final CSSStylableElement elt, final String pseudo, final CSSEngine engine, final int idx, final StyleMap sm, final Value value) {
        if (value == ValueConstants.NARROWER_VALUE) {
            sm.putParentRelative(idx, true);
            final CSSStylableElement p = CSSEngine.getParentCSSStylableElement(elt);
            if (p == null) {
                return ValueConstants.SEMI_CONDENSED_VALUE;
            }
            final Value v = engine.getComputedStyle(p, pseudo, idx);
            if (v == ValueConstants.NORMAL_VALUE) {
                return ValueConstants.SEMI_CONDENSED_VALUE;
            }
            if (v == ValueConstants.CONDENSED_VALUE) {
                return ValueConstants.EXTRA_CONDENSED_VALUE;
            }
            if (v == ValueConstants.EXPANDED_VALUE) {
                return ValueConstants.SEMI_EXPANDED_VALUE;
            }
            if (v == ValueConstants.SEMI_EXPANDED_VALUE) {
                return ValueConstants.NORMAL_VALUE;
            }
            if (v == ValueConstants.SEMI_CONDENSED_VALUE) {
                return ValueConstants.CONDENSED_VALUE;
            }
            if (v == ValueConstants.EXTRA_CONDENSED_VALUE) {
                return ValueConstants.ULTRA_CONDENSED_VALUE;
            }
            if (v == ValueConstants.EXTRA_EXPANDED_VALUE) {
                return ValueConstants.EXPANDED_VALUE;
            }
            if (v == ValueConstants.ULTRA_CONDENSED_VALUE) {
                return ValueConstants.ULTRA_CONDENSED_VALUE;
            }
            return ValueConstants.EXTRA_EXPANDED_VALUE;
        }
        else {
            if (value != ValueConstants.WIDER_VALUE) {
                return value;
            }
            sm.putParentRelative(idx, true);
            final CSSStylableElement p = CSSEngine.getParentCSSStylableElement(elt);
            if (p == null) {
                return ValueConstants.SEMI_CONDENSED_VALUE;
            }
            final Value v = engine.getComputedStyle(p, pseudo, idx);
            if (v == ValueConstants.NORMAL_VALUE) {
                return ValueConstants.SEMI_EXPANDED_VALUE;
            }
            if (v == ValueConstants.CONDENSED_VALUE) {
                return ValueConstants.SEMI_CONDENSED_VALUE;
            }
            if (v == ValueConstants.EXPANDED_VALUE) {
                return ValueConstants.EXTRA_EXPANDED_VALUE;
            }
            if (v == ValueConstants.SEMI_EXPANDED_VALUE) {
                return ValueConstants.EXPANDED_VALUE;
            }
            if (v == ValueConstants.SEMI_CONDENSED_VALUE) {
                return ValueConstants.NORMAL_VALUE;
            }
            if (v == ValueConstants.EXTRA_CONDENSED_VALUE) {
                return ValueConstants.CONDENSED_VALUE;
            }
            if (v == ValueConstants.EXTRA_EXPANDED_VALUE) {
                return ValueConstants.ULTRA_EXPANDED_VALUE;
            }
            if (v == ValueConstants.ULTRA_CONDENSED_VALUE) {
                return ValueConstants.EXTRA_CONDENSED_VALUE;
            }
            return ValueConstants.ULTRA_EXPANDED_VALUE;
        }
    }
    
    @Override
    public StringMap getIdentifiers() {
        return FontStretchManager.values;
    }
    
    static {
        (values = new StringMap()).put("all", ValueConstants.ALL_VALUE);
        FontStretchManager.values.put("condensed", ValueConstants.CONDENSED_VALUE);
        FontStretchManager.values.put("expanded", ValueConstants.EXPANDED_VALUE);
        FontStretchManager.values.put("extra-condensed", ValueConstants.EXTRA_CONDENSED_VALUE);
        FontStretchManager.values.put("extra-expanded", ValueConstants.EXTRA_EXPANDED_VALUE);
        FontStretchManager.values.put("narrower", ValueConstants.NARROWER_VALUE);
        FontStretchManager.values.put("normal", ValueConstants.NORMAL_VALUE);
        FontStretchManager.values.put("semi-condensed", ValueConstants.SEMI_CONDENSED_VALUE);
        FontStretchManager.values.put("semi-expanded", ValueConstants.SEMI_EXPANDED_VALUE);
        FontStretchManager.values.put("ultra-condensed", ValueConstants.ULTRA_CONDENSED_VALUE);
        FontStretchManager.values.put("ultra-expanded", ValueConstants.ULTRA_EXPANDED_VALUE);
        FontStretchManager.values.put("wider", ValueConstants.WIDER_VALUE);
    }
}
