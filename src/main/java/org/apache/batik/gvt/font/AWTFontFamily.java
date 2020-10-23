// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import java.awt.Font;
import java.text.AttributedCharacterIterator;

public class AWTFontFamily implements GVTFontFamily
{
    public static final AttributedCharacterIterator.Attribute TEXT_COMPOUND_DELIMITER;
    protected GVTFontFace fontFace;
    protected Font font;
    
    public AWTFontFamily(final GVTFontFace fontFace) {
        this.fontFace = fontFace;
    }
    
    public AWTFontFamily(final String familyName) {
        this(new GVTFontFace(familyName));
    }
    
    public AWTFontFamily(final GVTFontFace fontFace, final Font font) {
        this.fontFace = fontFace;
        this.font = font;
    }
    
    @Override
    public String getFamilyName() {
        return this.fontFace.getFamilyName();
    }
    
    @Override
    public GVTFontFace getFontFace() {
        return this.fontFace;
    }
    
    @Override
    public GVTFont deriveFont(final float size, final AttributedCharacterIterator aci) {
        if (this.font != null) {
            return new AWTGVTFont(this.font, size);
        }
        return this.deriveFont(size, aci.getAttributes());
    }
    
    @Override
    public GVTFont deriveFont(final float size, final Map attrs) {
        if (this.font != null) {
            return new AWTGVTFont(this.font, size);
        }
        final Map fontAttributes = new HashMap(attrs);
        fontAttributes.put(TextAttribute.SIZE, size);
        fontAttributes.put(TextAttribute.FAMILY, this.fontFace.getFamilyName());
        fontAttributes.remove(AWTFontFamily.TEXT_COMPOUND_DELIMITER);
        return new AWTGVTFont(fontAttributes);
    }
    
    @Override
    public boolean isComplex() {
        return false;
    }
    
    static {
        TEXT_COMPOUND_DELIMITER = GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER;
    }
}
