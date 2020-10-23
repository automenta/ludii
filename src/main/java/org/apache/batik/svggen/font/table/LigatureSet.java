// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class LigatureSet
{
    private int ligatureCount;
    private int[] ligatureOffsets;
    private Ligature[] ligatures;
    
    public LigatureSet(final RandomAccessFile raf, final int offset) throws IOException {
        raf.seek(offset);
        this.ligatureCount = raf.readUnsignedShort();
        this.ligatureOffsets = new int[this.ligatureCount];
        this.ligatures = new Ligature[this.ligatureCount];
        for (int i = 0; i < this.ligatureCount; ++i) {
            this.ligatureOffsets[i] = raf.readUnsignedShort();
        }
        for (int i = 0; i < this.ligatureCount; ++i) {
            raf.seek(offset + this.ligatureOffsets[i]);
            this.ligatures[i] = new Ligature(raf);
        }
    }
}
