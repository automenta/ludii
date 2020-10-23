// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class CoverageFormat1 extends Coverage
{
    private int glyphCount;
    private int[] glyphIds;
    
    protected CoverageFormat1(final RandomAccessFile raf) throws IOException {
        this.glyphCount = raf.readUnsignedShort();
        this.glyphIds = new int[this.glyphCount];
        for (int i = 0; i < this.glyphCount; ++i) {
            this.glyphIds[i] = raf.readUnsignedShort();
        }
    }
    
    @Override
    public int getFormat() {
        return 1;
    }
    
    @Override
    public int findGlyph(final int glyphId) {
        for (int i = 0; i < this.glyphCount; ++i) {
            if (this.glyphIds[i] == glyphId) {
                return i;
            }
        }
        return -1;
    }
}
