// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class CvtTable implements Table
{
    private short[] values;
    
    protected CvtTable(final DirectoryEntry de, final RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        final int len = de.getLength() / 2;
        this.values = new short[len];
        for (int i = 0; i < len; ++i) {
            this.values[i] = raf.readShort();
        }
    }
    
    @Override
    public int getType() {
        return 1668707360;
    }
    
    public short[] getValues() {
        return this.values;
    }
}
