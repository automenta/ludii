// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

import org.apache.batik.util.SVGConstants;

public class GVTFontFace implements SVGConstants
{
    protected String familyName;
    protected float unitsPerEm;
    protected String fontWeight;
    protected String fontStyle;
    protected String fontVariant;
    protected String fontStretch;
    protected float slope;
    protected String panose1;
    protected float ascent;
    protected float descent;
    protected float strikethroughPosition;
    protected float strikethroughThickness;
    protected float underlinePosition;
    protected float underlineThickness;
    protected float overlinePosition;
    protected float overlineThickness;
    
    public GVTFontFace(final String familyName, final float unitsPerEm, final String fontWeight, final String fontStyle, final String fontVariant, final String fontStretch, final float slope, final String panose1, final float ascent, final float descent, final float strikethroughPosition, final float strikethroughThickness, final float underlinePosition, final float underlineThickness, final float overlinePosition, final float overlineThickness) {
        this.familyName = familyName;
        this.unitsPerEm = unitsPerEm;
        this.fontWeight = fontWeight;
        this.fontStyle = fontStyle;
        this.fontVariant = fontVariant;
        this.fontStretch = fontStretch;
        this.slope = slope;
        this.panose1 = panose1;
        this.ascent = ascent;
        this.descent = descent;
        this.strikethroughPosition = strikethroughPosition;
        this.strikethroughThickness = strikethroughThickness;
        this.underlinePosition = underlinePosition;
        this.underlineThickness = underlineThickness;
        this.overlinePosition = overlinePosition;
        this.overlineThickness = overlineThickness;
    }
    
    public GVTFontFace(final String familyName) {
        this(familyName, 1000.0f, "all", "all", "normal", "normal", 0.0f, "0 0 0 0 0 0 0 0 0 0", 800.0f, 200.0f, 300.0f, 50.0f, -75.0f, 50.0f, 800.0f, 50.0f);
    }
    
    public String getFamilyName() {
        return this.familyName;
    }
    
    public boolean hasFamilyName(final String family) {
        String ffname = this.familyName;
        if (ffname.length() < family.length()) {
            return false;
        }
        ffname = ffname.toLowerCase();
        final int idx = ffname.indexOf(family.toLowerCase());
        if (idx == -1) {
            return false;
        }
        Label_0344: {
            if (ffname.length() > family.length()) {
                boolean quote = false;
                Label_0187: {
                    if (idx > 0) {
                        final char c = ffname.charAt(idx - 1);
                        switch (c) {
                            default: {
                                return false;
                            }
                            case ',': {
                                break;
                            }
                            case ' ': {
                                int i = idx - 2;
                                while (i >= 0) {
                                    switch (ffname.charAt(i)) {
                                        default: {
                                            return false;
                                        }
                                        case ' ': {
                                            --i;
                                            continue;
                                        }
                                        case '\"':
                                        case '\'': {
                                            quote = true;
                                            break Label_0187;
                                        }
                                    }
                                }
                                break;
                            }
                            case '\"':
                            case '\'': {
                                quote = true;
                                break;
                            }
                        }
                    }
                }
                if (idx + family.length() < ffname.length()) {
                    final char c = ffname.charAt(idx + family.length());
                    switch (c) {
                        default: {
                            return false;
                        }
                        case '\"':
                        case '\'': {
                            if (!quote) {
                                return false;
                            }
                            break;
                        }
                        case ',': {
                            break;
                        }
                        case ' ': {
                            int i = idx + family.length() + 1;
                            while (i < ffname.length()) {
                                switch (ffname.charAt(i)) {
                                    default: {
                                        return false;
                                    }
                                    case ' ': {
                                        ++i;
                                        continue;
                                    }
                                    case '\"':
                                    case '\'': {
                                        if (!quote) {
                                            return false;
                                        }
                                        break Label_0344;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public String getFontWeight() {
        return this.fontWeight;
    }
    
    public String getFontStyle() {
        return this.fontStyle;
    }
    
    public float getUnitsPerEm() {
        return this.unitsPerEm;
    }
    
    public float getAscent() {
        return this.ascent;
    }
    
    public float getDescent() {
        return this.descent;
    }
    
    public float getStrikethroughPosition() {
        return this.strikethroughPosition;
    }
    
    public float getStrikethroughThickness() {
        return this.strikethroughThickness;
    }
    
    public float getUnderlinePosition() {
        return this.underlinePosition;
    }
    
    public float getUnderlineThickness() {
        return this.underlineThickness;
    }
    
    public float getOverlinePosition() {
        return this.overlinePosition;
    }
    
    public float getOverlineThickness() {
        return this.overlineThickness;
    }
}
