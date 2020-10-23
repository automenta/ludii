// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class KernSubtableFormat2 extends KernSubtable
{
    private int rowWidth;
    private int leftClassTable;
    private int rightClassTable;
    private int array;
    
    protected KernSubtableFormat2(final RandomAccessFile raf) throws IOException {
        this.rowWidth = raf.readUnsignedShort();
        this.leftClassTable = raf.readUnsignedShort();
        this.rightClassTable = raf.readUnsignedShort();
        this.array = raf.readUnsignedShort();
    }
    
    @Override
    public int getKerningPairCount() {
        return 0;
    }
    
    @Override
    public KerningPair getKerningPair(final int i) {
        return null;
    }
}
