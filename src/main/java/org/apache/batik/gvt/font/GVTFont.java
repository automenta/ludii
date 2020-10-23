// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

import java.awt.font.FontRenderContext;
import java.text.CharacterIterator;

public interface GVTFont
{
    boolean canDisplay(final char p0);
    
    int canDisplayUpTo(final char[] p0, final int p1, final int p2);
    
    int canDisplayUpTo(final CharacterIterator p0, final int p1, final int p2);
    
    int canDisplayUpTo(final String p0);
    
    GVTGlyphVector createGlyphVector(final FontRenderContext p0, final char[] p1);
    
    GVTGlyphVector createGlyphVector(final FontRenderContext p0, final CharacterIterator p1);
    
    GVTGlyphVector createGlyphVector(final FontRenderContext p0, final int[] p1, final CharacterIterator p2);
    
    GVTGlyphVector createGlyphVector(final FontRenderContext p0, final String p1);
    
    GVTFont deriveFont(final float p0);
    
    String getFamilyName();
    
    GVTLineMetrics getLineMetrics(final char[] p0, final int p1, final int p2, final FontRenderContext p3);
    
    GVTLineMetrics getLineMetrics(final CharacterIterator p0, final int p1, final int p2, final FontRenderContext p3);
    
    GVTLineMetrics getLineMetrics(final String p0, final FontRenderContext p1);
    
    GVTLineMetrics getLineMetrics(final String p0, final int p1, final int p2, final FontRenderContext p3);
    
    float getSize();
    
    float getVKern(final int p0, final int p1);
    
    float getHKern(final int p0, final int p1);
    
    String toString();
}
