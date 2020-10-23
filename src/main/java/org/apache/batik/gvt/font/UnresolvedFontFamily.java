// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

import java.util.Map;
import java.text.AttributedCharacterIterator;

public class UnresolvedFontFamily implements GVTFontFamily
{
    protected GVTFontFace fontFace;
    
    public UnresolvedFontFamily(final GVTFontFace fontFace) {
        this.fontFace = fontFace;
    }
    
    public UnresolvedFontFamily(final String familyName) {
        this(new GVTFontFace(familyName));
    }
    
    @Override
    public GVTFontFace getFontFace() {
        return this.fontFace;
    }
    
    @Override
    public String getFamilyName() {
        return this.fontFace.getFamilyName();
    }
    
    @Override
    public GVTFont deriveFont(final float size, final AttributedCharacterIterator aci) {
        return null;
    }
    
    @Override
    public GVTFont deriveFont(final float size, final Map attrs) {
        return null;
    }
    
    @Override
    public boolean isComplex() {
        return false;
    }
}
