// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Ligature
{
    private int ligGlyph;
    private int compCount;
    private int[] components;
    
    public Ligature(final RandomAccessFile raf) throws IOException {
        this.ligGlyph = raf.readUnsignedShort();
        this.compCount = raf.readUnsignedShort();
        this.components = new int[this.compCount - 1];
        for (int i = 0; i < this.compCount - 1; ++i) {
            this.components[i] = raf.readUnsignedShort();
        }
    }
    
    public int getGlyphCount() {
        return this.compCount;
    }
    
    public int getGlyphId(final int i) {
        return (i == 0) ? this.ligGlyph : this.components[i - 1];
    }
}
