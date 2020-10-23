// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class LookupList
{
    private int lookupCount;
    private int[] lookupOffsets;
    private Lookup[] lookups;
    
    public LookupList(final RandomAccessFile raf, final int offset, final LookupSubtableFactory factory) throws IOException {
        raf.seek(offset);
        this.lookupCount = raf.readUnsignedShort();
        this.lookupOffsets = new int[this.lookupCount];
        this.lookups = new Lookup[this.lookupCount];
        for (int i = 0; i < this.lookupCount; ++i) {
            this.lookupOffsets[i] = raf.readUnsignedShort();
        }
        for (int i = 0; i < this.lookupCount; ++i) {
            this.lookups[i] = new Lookup(factory, raf, offset + this.lookupOffsets[i]);
        }
    }
    
    public Lookup getLookup(final Feature feature, final int index) {
        if (feature.getLookupCount() > index) {
            final int i = feature.getLookupListIndex(index);
            return this.lookups[i];
        }
        return null;
    }
}
