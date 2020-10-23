// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Feature
{
    private int featureParams;
    private int lookupCount;
    private int[] lookupListIndex;
    
    protected Feature(final RandomAccessFile raf, final int offset) throws IOException {
        raf.seek(offset);
        this.featureParams = raf.readUnsignedShort();
        this.lookupCount = raf.readUnsignedShort();
        this.lookupListIndex = new int[this.lookupCount];
        for (int i = 0; i < this.lookupCount; ++i) {
            this.lookupListIndex[i] = raf.readUnsignedShort();
        }
    }
    
    public int getLookupCount() {
        return this.lookupCount;
    }
    
    public int getLookupListIndex(final int i) {
        return this.lookupListIndex[i];
    }
}
