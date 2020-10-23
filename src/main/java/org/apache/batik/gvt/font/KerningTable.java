// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

public class KerningTable
{
    private Kern[] entries;
    
    public KerningTable(final Kern[] entries) {
        this.entries = entries;
    }
    
    public float getKerningValue(final int glyphCode1, final int glyphCode2, final String glyphUnicode1, final String glyphUnicode2) {
        for (final Kern entry : this.entries) {
            if (entry.matchesFirstGlyph(glyphCode1, glyphUnicode1) && entry.matchesSecondGlyph(glyphCode2, glyphUnicode2)) {
                return entry.getAdjustValue();
            }
        }
        return 0.0f;
    }
}
