// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ClassDefFormat2 extends ClassDef
{
    private int classRangeCount;
    private RangeRecord[] classRangeRecords;
    
    public ClassDefFormat2(final RandomAccessFile raf) throws IOException {
        this.classRangeCount = raf.readUnsignedShort();
        this.classRangeRecords = new RangeRecord[this.classRangeCount];
        for (int i = 0; i < this.classRangeCount; ++i) {
            this.classRangeRecords[i] = new RangeRecord(raf);
        }
    }
    
    @Override
    public int getFormat() {
        return 2;
    }
}
