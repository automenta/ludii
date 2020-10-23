// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.gvt.font.SVGGVTGlyphVector;
import org.apache.batik.gvt.font.Glyph;
import org.apache.batik.gvt.text.TextPaintInfo;
import org.apache.batik.gvt.font.GVTGlyphVector;
import java.awt.font.FontRenderContext;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import org.apache.batik.gvt.font.Kern;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.gvt.font.GVTLineMetrics;
import org.apache.batik.gvt.font.KerningTable;
import org.w3c.dom.Element;
import org.apache.batik.gvt.font.GVTFontFace;
import java.text.AttributedCharacterIterator;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.gvt.font.GVTFont;

public final class SVGGVTFont implements GVTFont, SVGConstants
{
    public static final AttributedCharacterIterator.Attribute PAINT_INFO;
    private float fontSize;
    private GVTFontFace fontFace;
    private String[] glyphUnicodes;
    private String[] glyphNames;
    private String[] glyphLangs;
    private String[] glyphOrientations;
    private String[] glyphForms;
    private Element[] glyphElements;
    private Element[] hkernElements;
    private Element[] vkernElements;
    private BridgeContext ctx;
    private Element textElement;
    private Element missingGlyphElement;
    private KerningTable hKerningTable;
    private KerningTable vKerningTable;
    private String language;
    private String orientation;
    private float scale;
    private GVTLineMetrics lineMetrics;
    
    public SVGGVTFont(final float fontSize, final GVTFontFace fontFace, final String[] glyphUnicodes, final String[] glyphNames, final String[] glyphLangs, final String[] glyphOrientations, final String[] glyphForms, final BridgeContext ctx, final Element[] glyphElements, final Element missingGlyphElement, final Element[] hkernElements, final Element[] vkernElements, final Element textElement) {
        this.lineMetrics = null;
        this.fontFace = fontFace;
        this.fontSize = fontSize;
        this.glyphUnicodes = glyphUnicodes;
        this.glyphNames = glyphNames;
        this.glyphLangs = glyphLangs;
        this.glyphOrientations = glyphOrientations;
        this.glyphForms = glyphForms;
        this.ctx = ctx;
        this.glyphElements = glyphElements;
        this.missingGlyphElement = missingGlyphElement;
        this.hkernElements = hkernElements;
        this.vkernElements = vkernElements;
        this.scale = fontSize / fontFace.getUnitsPerEm();
        this.textElement = textElement;
        this.language = XMLSupport.getXMLLang(textElement);
        final Value v = CSSUtilities.getComputedStyle(textElement, 59);
        if (v.getStringValue().startsWith("tb")) {
            this.orientation = "v";
        }
        else {
            this.orientation = "h";
        }
        this.createKerningTables();
    }
    
    private void createKerningTables() {
        final Kern[] hEntries = new Kern[this.hkernElements.length];
        for (int i = 0; i < this.hkernElements.length; ++i) {
            final Element hkernElement = this.hkernElements[i];
            final SVGHKernElementBridge hkernBridge = (SVGHKernElementBridge)this.ctx.getBridge(hkernElement);
            final Kern hkern = hkernBridge.createKern(this.ctx, hkernElement, this);
            hEntries[i] = hkern;
        }
        this.hKerningTable = new KerningTable(hEntries);
        final Kern[] vEntries = new Kern[this.vkernElements.length];
        for (int j = 0; j < this.vkernElements.length; ++j) {
            final Element vkernElement = this.vkernElements[j];
            final SVGVKernElementBridge vkernBridge = (SVGVKernElementBridge)this.ctx.getBridge(vkernElement);
            final Kern vkern = vkernBridge.createKern(this.ctx, vkernElement, this);
            vEntries[j] = vkern;
        }
        this.vKerningTable = new KerningTable(vEntries);
    }
    
    @Override
    public float getHKern(final int glyphCode1, final int glyphCode2) {
        if (glyphCode1 < 0 || glyphCode1 >= this.glyphUnicodes.length || glyphCode2 < 0 || glyphCode2 >= this.glyphUnicodes.length) {
            return 0.0f;
        }
        final float ret = this.hKerningTable.getKerningValue(glyphCode1, glyphCode2, this.glyphUnicodes[glyphCode1], this.glyphUnicodes[glyphCode2]);
        return ret * this.scale;
    }
    
    @Override
    public float getVKern(final int glyphCode1, final int glyphCode2) {
        if (glyphCode1 < 0 || glyphCode1 >= this.glyphUnicodes.length || glyphCode2 < 0 || glyphCode2 >= this.glyphUnicodes.length) {
            return 0.0f;
        }
        final float ret = this.vKerningTable.getKerningValue(glyphCode1, glyphCode2, this.glyphUnicodes[glyphCode1], this.glyphUnicodes[glyphCode2]);
        return ret * this.scale;
    }
    
    public int[] getGlyphCodesForName(final String name) {
        final List glyphCodes = new ArrayList();
        for (int i = 0; i < this.glyphNames.length; ++i) {
            if (this.glyphNames[i] != null && this.glyphNames[i].equals(name)) {
                glyphCodes.add(i);
            }
        }
        final int[] glyphCodeArray = new int[glyphCodes.size()];
        for (int j = 0; j < glyphCodes.size(); ++j) {
            glyphCodeArray[j] = glyphCodes.get(j);
        }
        return glyphCodeArray;
    }
    
    public int[] getGlyphCodesForUnicode(final String unicode) {
        final List glyphCodes = new ArrayList();
        for (int i = 0; i < this.glyphUnicodes.length; ++i) {
            if (this.glyphUnicodes[i] != null && this.glyphUnicodes[i].equals(unicode)) {
                glyphCodes.add(i);
            }
        }
        final int[] glyphCodeArray = new int[glyphCodes.size()];
        for (int j = 0; j < glyphCodes.size(); ++j) {
            glyphCodeArray[j] = glyphCodes.get(j);
        }
        return glyphCodeArray;
    }
    
    private boolean languageMatches(final String glyphLang) {
        if (glyphLang == null || glyphLang.length() == 0) {
            return true;
        }
        final StringTokenizer st = new StringTokenizer(glyphLang, ",");
        while (st.hasMoreTokens()) {
            final String s = st.nextToken();
            if (s.equals(this.language) || (s.startsWith(this.language) && s.length() > this.language.length() && s.charAt(this.language.length()) == '-')) {
                return true;
            }
        }
        return false;
    }
    
    private boolean orientationMatches(final String glyphOrientation) {
        return glyphOrientation == null || glyphOrientation.length() == 0 || glyphOrientation.equals(this.orientation);
    }
    
    private boolean formMatches(final String glyphUnicode, final String glyphForm, final AttributedCharacterIterator aci, final int currentIndex) {
        if (aci == null || glyphForm == null || glyphForm.length() == 0) {
            return true;
        }
        char c = aci.setIndex(currentIndex);
        final Integer form = (Integer)aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.ARABIC_FORM);
        if (form == null || form.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_NONE)) {
            return false;
        }
        if (glyphUnicode.length() > 1) {
            boolean matched = true;
            for (int j = 1; j < glyphUnicode.length(); ++j) {
                c = aci.next();
                if (glyphUnicode.charAt(j) != c) {
                    matched = false;
                    break;
                }
            }
            aci.setIndex(currentIndex);
            if (matched) {
                aci.setIndex(currentIndex + glyphUnicode.length() - 1);
                final Integer lastForm = (Integer)aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.ARABIC_FORM);
                aci.setIndex(currentIndex);
                if (form != null && lastForm != null) {
                    if (form.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_TERMINAL) && lastForm.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_INITIAL)) {
                        return glyphForm.equals("isolated");
                    }
                    if (form.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_TERMINAL)) {
                        return glyphForm.equals("terminal");
                    }
                    if (form.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_MEDIAL) && lastForm.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_MEDIAL)) {
                        return glyphForm.equals("medial");
                    }
                }
            }
        }
        if (form.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_ISOLATED)) {
            return glyphForm.equals("isolated");
        }
        if (form.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_TERMINAL)) {
            return glyphForm.equals("terminal");
        }
        if (form.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_INITIAL)) {
            return glyphForm.equals("initial");
        }
        return form.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_MEDIAL) && glyphForm.equals("medial");
    }
    
    public boolean canDisplayGivenName(final String name) {
        for (int i = 0; i < this.glyphNames.length; ++i) {
            if (this.glyphNames[i] != null && this.glyphNames[i].equals(name) && this.languageMatches(this.glyphLangs[i]) && this.orientationMatches(this.glyphOrientations[i])) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean canDisplay(final char c) {
        for (int i = 0; i < this.glyphUnicodes.length; ++i) {
            if (this.glyphUnicodes[i].indexOf(c) != -1 && this.languageMatches(this.glyphLangs[i]) && this.orientationMatches(this.glyphOrientations[i])) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int canDisplayUpTo(final char[] text, final int start, final int limit) {
        final StringCharacterIterator sci = new StringCharacterIterator(new String(text));
        return this.canDisplayUpTo(sci, start, limit);
    }
    
    @Override
    public int canDisplayUpTo(final CharacterIterator iter, final int start, final int limit) {
        AttributedCharacterIterator aci = null;
        if (iter instanceof AttributedCharacterIterator) {
            aci = (AttributedCharacterIterator)iter;
        }
        char c = iter.setIndex(start);
        for (int currentIndex = start; c != '\uffff' && currentIndex < limit; c = iter.next(), currentIndex = iter.getIndex()) {
            boolean foundMatchingGlyph = false;
            for (int i = 0; i < this.glyphUnicodes.length; ++i) {
                if (this.glyphUnicodes[i].indexOf(c) == 0 && this.languageMatches(this.glyphLangs[i]) && this.orientationMatches(this.glyphOrientations[i]) && this.formMatches(this.glyphUnicodes[i], this.glyphForms[i], aci, currentIndex)) {
                    if (this.glyphUnicodes[i].length() == 1) {
                        foundMatchingGlyph = true;
                        break;
                    }
                    boolean matched = true;
                    for (int j = 1; j < this.glyphUnicodes[i].length(); ++j) {
                        c = iter.next();
                        if (this.glyphUnicodes[i].charAt(j) != c) {
                            matched = false;
                            break;
                        }
                    }
                    if (matched) {
                        foundMatchingGlyph = true;
                        break;
                    }
                    c = iter.setIndex(currentIndex);
                }
            }
            if (!foundMatchingGlyph) {
                return currentIndex;
            }
        }
        return -1;
    }
    
    @Override
    public int canDisplayUpTo(final String str) {
        final StringCharacterIterator sci = new StringCharacterIterator(str);
        return this.canDisplayUpTo(sci, 0, str.length());
    }
    
    @Override
    public GVTGlyphVector createGlyphVector(final FontRenderContext frc, final char[] chars) {
        final StringCharacterIterator sci = new StringCharacterIterator(new String(chars));
        return this.createGlyphVector(frc, sci);
    }
    
    @Override
    public GVTGlyphVector createGlyphVector(final FontRenderContext frc, final CharacterIterator ci) {
        AttributedCharacterIterator aci = null;
        if (ci instanceof AttributedCharacterIterator) {
            aci = (AttributedCharacterIterator)ci;
        }
        final List glyphs = new ArrayList();
        for (char c = ci.first(); c != '\uffff'; c = ci.next()) {
            boolean foundMatchingGlyph = false;
            for (int i = 0; i < this.glyphUnicodes.length; ++i) {
                if (this.glyphUnicodes[i].indexOf(c) == 0 && this.languageMatches(this.glyphLangs[i]) && this.orientationMatches(this.glyphOrientations[i]) && this.formMatches(this.glyphUnicodes[i], this.glyphForms[i], aci, ci.getIndex())) {
                    if (this.glyphUnicodes[i].length() == 1) {
                        final Element glyphElement = this.glyphElements[i];
                        final SVGGlyphElementBridge glyphBridge = (SVGGlyphElementBridge)this.ctx.getBridge(glyphElement);
                        TextPaintInfo tpi = null;
                        if (aci != null) {
                            tpi = (TextPaintInfo)aci.getAttribute(SVGGVTFont.PAINT_INFO);
                        }
                        final Glyph glyph = glyphBridge.createGlyph(this.ctx, glyphElement, this.textElement, i, this.fontSize, this.fontFace, tpi);
                        glyphs.add(glyph);
                        foundMatchingGlyph = true;
                        break;
                    }
                    final int current = ci.getIndex();
                    boolean matched = true;
                    for (int j = 1; j < this.glyphUnicodes[i].length(); ++j) {
                        c = ci.next();
                        if (this.glyphUnicodes[i].charAt(j) != c) {
                            matched = false;
                            break;
                        }
                    }
                    if (matched) {
                        final Element glyphElement2 = this.glyphElements[i];
                        final SVGGlyphElementBridge glyphBridge2 = (SVGGlyphElementBridge)this.ctx.getBridge(glyphElement2);
                        TextPaintInfo tpi2 = null;
                        if (aci != null) {
                            aci.setIndex(ci.getIndex());
                            tpi2 = (TextPaintInfo)aci.getAttribute(SVGGVTFont.PAINT_INFO);
                        }
                        final Glyph glyph2 = glyphBridge2.createGlyph(this.ctx, glyphElement2, this.textElement, i, this.fontSize, this.fontFace, tpi2);
                        glyphs.add(glyph2);
                        foundMatchingGlyph = true;
                        break;
                    }
                    c = ci.setIndex(current);
                }
            }
            if (!foundMatchingGlyph) {
                final SVGGlyphElementBridge glyphBridge3 = (SVGGlyphElementBridge)this.ctx.getBridge(this.missingGlyphElement);
                TextPaintInfo tpi3 = null;
                if (aci != null) {
                    aci.setIndex(ci.getIndex());
                    tpi3 = (TextPaintInfo)aci.getAttribute(SVGGVTFont.PAINT_INFO);
                }
                final Glyph glyph3 = glyphBridge3.createGlyph(this.ctx, this.missingGlyphElement, this.textElement, -1, this.fontSize, this.fontFace, tpi3);
                glyphs.add(glyph3);
            }
        }
        final int numGlyphs = glyphs.size();
        final Glyph[] glyphArray = glyphs.toArray(new Glyph[numGlyphs]);
        return new SVGGVTGlyphVector(this, glyphArray, frc);
    }
    
    @Override
    public GVTGlyphVector createGlyphVector(final FontRenderContext frc, final int[] glyphCodes, final CharacterIterator ci) {
        final int nGlyphs = glyphCodes.length;
        final StringBuffer workBuff = new StringBuffer(nGlyphs);
        for (final int glyphCode : glyphCodes) {
            workBuff.append(this.glyphUnicodes[glyphCode]);
        }
        final StringCharacterIterator sci = new StringCharacterIterator(workBuff.toString());
        return this.createGlyphVector(frc, sci);
    }
    
    @Override
    public GVTGlyphVector createGlyphVector(final FontRenderContext frc, final String str) {
        final StringCharacterIterator sci = new StringCharacterIterator(str);
        return this.createGlyphVector(frc, sci);
    }
    
    @Override
    public GVTFont deriveFont(final float size) {
        return new SVGGVTFont(size, this.fontFace, this.glyphUnicodes, this.glyphNames, this.glyphLangs, this.glyphOrientations, this.glyphForms, this.ctx, this.glyphElements, this.missingGlyphElement, this.hkernElements, this.vkernElements, this.textElement);
    }
    
    @Override
    public String getFamilyName() {
        return this.fontFace.getFamilyName();
    }
    
    protected GVTLineMetrics getLineMetrics(final int beginIndex, final int limit) {
        if (this.lineMetrics != null) {
            return this.lineMetrics;
        }
        final float fontHeight = this.fontFace.getUnitsPerEm();
        final float scale = this.fontSize / fontHeight;
        final float ascent = this.fontFace.getAscent() * scale;
        final float descent = this.fontFace.getDescent() * scale;
        final float[] baselineOffsets = { 0.0f, (ascent + descent) / 2.0f - ascent, -ascent };
        final float stOffset = this.fontFace.getStrikethroughPosition() * -scale;
        final float stThickness = this.fontFace.getStrikethroughThickness() * scale;
        final float ulOffset = this.fontFace.getUnderlinePosition() * scale;
        final float ulThickness = this.fontFace.getUnderlineThickness() * scale;
        final float olOffset = this.fontFace.getOverlinePosition() * -scale;
        final float olThickness = this.fontFace.getOverlineThickness() * scale;
        return this.lineMetrics = new GVTLineMetrics(ascent, 0, baselineOffsets, descent, fontHeight, fontHeight, limit - beginIndex, stOffset, stThickness, ulOffset, ulThickness, olOffset, olThickness);
    }
    
    @Override
    public GVTLineMetrics getLineMetrics(final char[] chars, final int beginIndex, final int limit, final FontRenderContext frc) {
        return this.getLineMetrics(beginIndex, limit);
    }
    
    @Override
    public GVTLineMetrics getLineMetrics(final CharacterIterator ci, final int beginIndex, final int limit, final FontRenderContext frc) {
        return this.getLineMetrics(beginIndex, limit);
    }
    
    @Override
    public GVTLineMetrics getLineMetrics(final String str, final FontRenderContext frc) {
        final StringCharacterIterator sci = new StringCharacterIterator(str);
        return this.getLineMetrics(sci, 0, str.length(), frc);
    }
    
    @Override
    public GVTLineMetrics getLineMetrics(final String str, final int beginIndex, final int limit, final FontRenderContext frc) {
        final StringCharacterIterator sci = new StringCharacterIterator(str);
        return this.getLineMetrics(sci, beginIndex, limit, frc);
    }
    
    @Override
    public float getSize() {
        return this.fontSize;
    }
    
    @Override
    public String toString() {
        return this.fontFace.getFamilyName() + " " + this.fontFace.getFontWeight() + " " + this.fontFace.getFontStyle();
    }
    
    static {
        PAINT_INFO = GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO;
    }
}
