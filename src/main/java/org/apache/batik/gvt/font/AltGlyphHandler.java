// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

import java.text.AttributedCharacterIterator;
import java.awt.font.FontRenderContext;

public interface AltGlyphHandler
{
    GVTGlyphVector createGlyphVector(final FontRenderContext p0, final float p1, final AttributedCharacterIterator p2);
}
