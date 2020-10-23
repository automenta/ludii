// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class LigatureSubstFormat1 extends LigatureSubst
{
    private int coverageOffset;
    private int ligSetCount;
    private int[] ligatureSetOffsets;
    private Coverage coverage;
    private LigatureSet[] ligatureSets;
    
    protected LigatureSubstFormat1(final RandomAccessFile raf, final int offset) throws IOException {
        this.coverageOffset = raf.readUnsignedShort();
        this.ligSetCount = raf.readUnsignedShort();
        this.ligatureSetOffsets = new int[this.ligSetCount];
        this.ligatureSets = new LigatureSet[this.ligSetCount];
        for (int i = 0; i < this.ligSetCount; ++i) {
            this.ligatureSetOffsets[i] = raf.readUnsignedShort();
        }
        raf.seek(offset + this.coverageOffset);
        this.coverage = Coverage.read(raf);
        for (int i = 0; i < this.ligSetCount; ++i) {
            this.ligatureSets[i] = new LigatureSet(raf, offset + this.ligatureSetOffsets[i]);
        }
    }
    
    public int getFormat() {
        return 1;
    }
}
