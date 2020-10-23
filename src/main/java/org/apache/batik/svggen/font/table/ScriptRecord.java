// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ScriptRecord
{
    private int tag;
    private int offset;
    
    protected ScriptRecord(final RandomAccessFile raf) throws IOException {
        this.tag = raf.readInt();
        this.offset = raf.readUnsignedShort();
    }
    
    public int getTag() {
        return this.tag;
    }
    
    public int getOffset() {
        return this.offset;
    }
}
