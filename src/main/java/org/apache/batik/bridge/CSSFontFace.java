// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.StyleMap;
import java.util.LinkedList;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.FontFaceRule;
import org.apache.batik.css.engine.CSSEngine;
import java.util.List;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.util.SVGConstants;

public class CSSFontFace extends FontFace implements SVGConstants
{
    GVTFontFamily fontFamily;
    
    public CSSFontFace(final List srcs, final String familyName, final float unitsPerEm, final String fontWeight, final String fontStyle, final String fontVariant, final String fontStretch, final float slope, final String panose1, final float ascent, final float descent, final float strikethroughPosition, final float strikethroughThickness, final float underlinePosition, final float underlineThickness, final float overlinePosition, final float overlineThickness) {
        super(srcs, familyName, unitsPerEm, fontWeight, fontStyle, fontVariant, fontStretch, slope, panose1, ascent, descent, strikethroughPosition, strikethroughThickness, underlinePosition, underlineThickness, overlinePosition, overlineThickness);
        this.fontFamily = null;
    }
    
    protected CSSFontFace(final String familyName) {
        super(familyName);
        this.fontFamily = null;
    }
    
    public static CSSFontFace createCSSFontFace(final CSSEngine eng, final FontFaceRule ffr) {
        final StyleMap sm = ffr.getStyleMap();
        final String familyName = getStringProp(sm, eng, 21);
        final CSSFontFace ret = new CSSFontFace(familyName);
        Value v = sm.getValue(27);
        if (v != null) {
            ret.fontWeight = v.getCssText();
        }
        v = sm.getValue(25);
        if (v != null) {
            ret.fontStyle = v.getCssText();
        }
        v = sm.getValue(26);
        if (v != null) {
            ret.fontVariant = v.getCssText();
        }
        v = sm.getValue(24);
        if (v != null) {
            ret.fontStretch = v.getCssText();
        }
        v = sm.getValue(41);
        final ParsedURL base = ffr.getURL();
        if (v != null && v != ValueConstants.NONE_VALUE) {
            if (v.getCssValueType() == 1) {
                (ret.srcs = new LinkedList()).add(getSrcValue(v, base));
            }
            else if (v.getCssValueType() == 2) {
                ret.srcs = new LinkedList();
                for (int i = 0; i < v.getLength(); ++i) {
                    ret.srcs.add(getSrcValue(v.item(i), base));
                }
            }
        }
        return ret;
    }
    
    public static Object getSrcValue(final Value v, final ParsedURL base) {
        if (v.getCssValueType() != 1) {
            return null;
        }
        if (v.getPrimitiveType() == 20) {
            if (base != null) {
                return new ParsedURL(base, v.getStringValue());
            }
            return new ParsedURL(v.getStringValue());
        }
        else {
            if (v.getPrimitiveType() == 19) {
                return v.getStringValue();
            }
            return null;
        }
    }
    
    public static String getStringProp(final StyleMap sm, final CSSEngine eng, final int pidx) {
        Value v = sm.getValue(pidx);
        final ValueManager[] vms = eng.getValueManagers();
        if (v == null) {
            final ValueManager vm = vms[pidx];
            v = vm.getDefaultValue();
        }
        while (v.getCssValueType() == 2) {
            v = v.item(0);
        }
        return v.getStringValue();
    }
    
    public static float getFloatProp(final StyleMap sm, final CSSEngine eng, final int pidx) {
        Value v = sm.getValue(pidx);
        final ValueManager[] vms = eng.getValueManagers();
        if (v == null) {
            final ValueManager vm = vms[pidx];
            v = vm.getDefaultValue();
        }
        while (v.getCssValueType() == 2) {
            v = v.item(0);
        }
        return v.getFloatValue();
    }
    
    @Override
    public GVTFontFamily getFontFamily(final BridgeContext ctx) {
        if (this.fontFamily != null) {
            return this.fontFamily;
        }
        return this.fontFamily = super.getFontFamily(ctx);
    }
}
