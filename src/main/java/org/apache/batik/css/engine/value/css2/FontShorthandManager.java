// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.css2;

import java.util.HashSet;
import org.apache.batik.css.parser.CSSLexicalUnit;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.css.engine.value.IdentifierManager;
import org.apache.batik.css.engine.CSSEngine;
import java.util.Set;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.AbstractValueFactory;

public class FontShorthandManager extends AbstractValueFactory implements ShorthandManager
{
    static LexicalUnit NORMAL_LU;
    static LexicalUnit BOLD_LU;
    static LexicalUnit MEDIUM_LU;
    static LexicalUnit SZ_10PT_LU;
    static LexicalUnit SZ_8PT_LU;
    static LexicalUnit FONT_FAMILY_LU;
    protected static final Set values;
    
    @Override
    public String getPropertyName() {
        return "font";
    }
    
    @Override
    public boolean isAnimatableProperty() {
        return true;
    }
    
    @Override
    public boolean isAdditiveProperty() {
        return false;
    }
    
    public void handleSystemFont(final CSSEngine eng, final PropertyHandler ph, final String s, final boolean imp) {
        final LexicalUnit fontStyle = FontShorthandManager.NORMAL_LU;
        final LexicalUnit fontVariant = FontShorthandManager.NORMAL_LU;
        final LexicalUnit fontWeight = FontShorthandManager.NORMAL_LU;
        final LexicalUnit lineHeight = FontShorthandManager.NORMAL_LU;
        final LexicalUnit fontFamily = FontShorthandManager.FONT_FAMILY_LU;
        LexicalUnit fontSize;
        if (s.equals("small-caption")) {
            fontSize = FontShorthandManager.SZ_8PT_LU;
        }
        else {
            fontSize = FontShorthandManager.SZ_10PT_LU;
        }
        ph.property("font-family", fontFamily, imp);
        ph.property("font-style", fontStyle, imp);
        ph.property("font-variant", fontVariant, imp);
        ph.property("font-weight", fontWeight, imp);
        ph.property("font-size", fontSize, imp);
        ph.property("line-height", lineHeight, imp);
    }
    
    @Override
    public void setValues(final CSSEngine eng, final PropertyHandler ph, LexicalUnit lu, final boolean imp) {
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return;
            }
            case 35: {
                final String s = lu.getStringValue().toLowerCase();
                if (FontShorthandManager.values.contains(s)) {
                    this.handleSystemFont(eng, ph, s, imp);
                    return;
                }
                break;
            }
        }
        LexicalUnit fontStyle = null;
        LexicalUnit fontVariant = null;
        LexicalUnit fontWeight = null;
        LexicalUnit fontSize = null;
        LexicalUnit lineHeight = null;
        LexicalUnit fontFamily = null;
        final ValueManager[] vMgrs = eng.getValueManagers();
        final int fst = eng.getPropertyIndex("font-style");
        final int fv = eng.getPropertyIndex("font-variant");
        final int fw = eng.getPropertyIndex("font-weight");
        final int fsz = eng.getPropertyIndex("font-size");
        final int lh = eng.getPropertyIndex("line-height");
        final IdentifierManager fstVM = (IdentifierManager)vMgrs[fst];
        final IdentifierManager fvVM = (IdentifierManager)vMgrs[fv];
        final IdentifierManager fwVM = (IdentifierManager)vMgrs[fw];
        final FontSizeManager fszVM = (FontSizeManager)vMgrs[fsz];
        final StringMap fstSM = fstVM.getIdentifiers();
        final StringMap fvSM = fvVM.getIdentifiers();
        final StringMap fwSM = fwVM.getIdentifiers();
        final StringMap fszSM = fszVM.getIdentifiers();
        boolean svwDone = false;
        LexicalUnit intLU = null;
        while (!svwDone && lu != null) {
            switch (lu.getLexicalUnitType()) {
                case 35: {
                    final String s2 = lu.getStringValue().toLowerCase().intern();
                    if (fontStyle == null && fstSM.get(s2) != null) {
                        fontStyle = lu;
                        if (intLU == null) {
                            break;
                        }
                        if (fontWeight == null) {
                            fontWeight = intLU;
                            intLU = null;
                            break;
                        }
                        throw this.createInvalidLexicalUnitDOMException(intLU.getLexicalUnitType());
                    }
                    else if (fontVariant == null && fvSM.get(s2) != null) {
                        fontVariant = lu;
                        if (intLU == null) {
                            break;
                        }
                        if (fontWeight == null) {
                            fontWeight = intLU;
                            intLU = null;
                            break;
                        }
                        throw this.createInvalidLexicalUnitDOMException(intLU.getLexicalUnitType());
                    }
                    else {
                        if (intLU == null && fontWeight == null && fwSM.get(s2) != null) {
                            fontWeight = lu;
                            break;
                        }
                        svwDone = true;
                        break;
                    }
                    break;
                }
                case 13: {
                    if (intLU == null && fontWeight == null) {
                        intLU = lu;
                        break;
                    }
                    svwDone = true;
                    break;
                }
                default: {
                    svwDone = true;
                    break;
                }
            }
            if (!svwDone) {
                lu = lu.getNextLexicalUnit();
            }
        }
        if (lu == null) {
            throw this.createMalformedLexicalUnitDOMException();
        }
        switch (lu.getLexicalUnitType()) {
            case 35: {
                final String s2 = lu.getStringValue().toLowerCase().intern();
                if (fszSM.get(s2) != null) {
                    fontSize = lu;
                    lu = lu.getNextLexicalUnit();
                }
                break;
            }
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23: {
                fontSize = lu;
                lu = lu.getNextLexicalUnit();
                break;
            }
        }
        if (fontSize == null) {
            if (intLU == null) {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
            fontSize = intLU;
            intLU = null;
        }
        if (intLU != null) {
            if (fontWeight != null) {
                throw this.createInvalidLexicalUnitDOMException(intLU.getLexicalUnitType());
            }
            fontWeight = intLU;
        }
        if (lu == null) {
            throw this.createMalformedLexicalUnitDOMException();
        }
        switch (lu.getLexicalUnitType()) {
            case 4: {
                lu = lu.getNextLexicalUnit();
                if (lu == null) {
                    throw this.createMalformedLexicalUnitDOMException();
                }
                lineHeight = lu;
                lu = lu.getNextLexicalUnit();
                break;
            }
        }
        if (lu == null) {
            throw this.createMalformedLexicalUnitDOMException();
        }
        fontFamily = lu;
        if (fontStyle == null) {
            fontStyle = FontShorthandManager.NORMAL_LU;
        }
        if (fontVariant == null) {
            fontVariant = FontShorthandManager.NORMAL_LU;
        }
        if (fontWeight == null) {
            fontWeight = FontShorthandManager.NORMAL_LU;
        }
        if (lineHeight == null) {
            lineHeight = FontShorthandManager.NORMAL_LU;
        }
        ph.property("font-family", fontFamily, imp);
        ph.property("font-style", fontStyle, imp);
        ph.property("font-variant", fontVariant, imp);
        ph.property("font-weight", fontWeight, imp);
        ph.property("font-size", fontSize, imp);
        if (lh != -1) {
            ph.property("line-height", lineHeight, imp);
        }
    }
    
    static {
        FontShorthandManager.NORMAL_LU = CSSLexicalUnit.createString((short)35, "normal", null);
        FontShorthandManager.BOLD_LU = CSSLexicalUnit.createString((short)35, "bold", null);
        FontShorthandManager.MEDIUM_LU = CSSLexicalUnit.createString((short)35, "medium", null);
        FontShorthandManager.SZ_10PT_LU = CSSLexicalUnit.createFloat((short)21, 10.0f, null);
        FontShorthandManager.SZ_8PT_LU = CSSLexicalUnit.createFloat((short)21, 8.0f, null);
        FontShorthandManager.FONT_FAMILY_LU = CSSLexicalUnit.createString((short)35, "Dialog", null);
        final LexicalUnit lu = CSSLexicalUnit.createString((short)35, "Helvetica", FontShorthandManager.FONT_FAMILY_LU);
        CSSLexicalUnit.createString((short)35, "sans-serif", lu);
        (values = new HashSet()).add("caption");
        FontShorthandManager.values.add("icon");
        FontShorthandManager.values.add("menu");
        FontShorthandManager.values.add("message-box");
        FontShorthandManager.values.add("small-caption");
        FontShorthandManager.values.add("status-bar");
    }
}
