// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RangeRecord
{
    private int start;
    private int end;
    private int startCoverageIndex;
    
    public RangeRecord(final RandomAccessFile raf) throws IOException {
        this.start = raf.readUnsignedShort();
        this.end = raf.readUnsignedShort();
        this.startCoverageIndex = raf.readUnsignedShort();
    }
    
    public boolean isInRange(final int glyphId) {
        return this.start <= glyphId && glyphId <= this.end;
    }
    
    public int getCoverageIndex(final int glyphId) {
        if (this.isInRange(glyphId)) {
            return this.startCoverageIndex + glyphId - this.start;
        }
        return -1;
    }
}
