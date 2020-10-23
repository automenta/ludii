// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class CoverageFormat2 extends Coverage
{
    private int rangeCount;
    private RangeRecord[] rangeRecords;
    
    protected CoverageFormat2(final RandomAccessFile raf) throws IOException {
        this.rangeCount = raf.readUnsignedShort();
        this.rangeRecords = new RangeRecord[this.rangeCount];
        for (int i = 0; i < this.rangeCount; ++i) {
            this.rangeRecords[i] = new RangeRecord(raf);
        }
    }
    
    @Override
    public int getFormat() {
        return 2;
    }
    
    @Override
    public int findGlyph(final int glyphId) {
        for (int i = 0; i < this.rangeCount; ++i) {
            final int n = this.rangeRecords[i].getCoverageIndex(glyphId);
            if (n > -1) {
                return n;
            }
        }
        return -1;
    }
}
