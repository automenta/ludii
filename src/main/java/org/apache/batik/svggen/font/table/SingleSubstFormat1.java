// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class SingleSubstFormat1 extends SingleSubst
{
    private int coverageOffset;
    private short deltaGlyphID;
    private Coverage coverage;
    
    protected SingleSubstFormat1(final RandomAccessFile raf, final int offset) throws IOException {
        this.coverageOffset = raf.readUnsignedShort();
        this.deltaGlyphID = raf.readShort();
        raf.seek(offset + this.coverageOffset);
        this.coverage = Coverage.read(raf);
    }
    
    @Override
    public int getFormat() {
        return 1;
    }
    
    @Override
    public int substitute(final int glyphId) {
        final int i = this.coverage.findGlyph(glyphId);
        if (i > -1) {
            return glyphId + this.deltaGlyphID;
        }
        return glyphId;
    }
}
