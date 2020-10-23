// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

import java.util.Map;
import java.text.AttributedCharacterIterator;

public interface GVTFontFamily
{
    String getFamilyName();
    
    GVTFontFace getFontFace();
    
    GVTFont deriveFont(final float p0, final AttributedCharacterIterator p1);
    
    GVTFont deriveFont(final float p0, final Map p1);
    
    boolean isComplex();
}
