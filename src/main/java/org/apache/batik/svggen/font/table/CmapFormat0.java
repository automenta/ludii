// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class CmapFormat0 extends CmapFormat
{
    private int[] glyphIdArray;
    private int first;
    private int last;
    
    protected CmapFormat0(final RandomAccessFile raf) throws IOException {
        super(raf);
        this.glyphIdArray = new int[256];
        this.format = 0;
        this.first = -1;
        for (int i = 0; i < 256; ++i) {
            this.glyphIdArray[i] = raf.readUnsignedByte();
            if (this.glyphIdArray[i] > 0) {
                if (this.first == -1) {
                    this.first = i;
                }
                this.last = i;
            }
        }
    }
    
    @Override
    public int getFirst() {
        return this.first;
    }
    
    @Override
    public int getLast() {
        return this.last;
    }
    
    @Override
    public int mapCharCode(final int charCode) {
        if (0 <= charCode && charCode < 256) {
            return this.glyphIdArray[charCode];
        }
        return 0;
    }
}
