// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DirectoryEntry
{
    private int tag;
    private int checksum;
    private int offset;
    private int length;
    private Table table;
    
    protected DirectoryEntry(final RandomAccessFile raf) throws IOException {
        this.table = null;
        this.tag = raf.readInt();
        this.checksum = raf.readInt();
        this.offset = raf.readInt();
        this.length = raf.readInt();
    }
    
    public int getChecksum() {
        return this.checksum;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public int getOffset() {
        return this.offset;
    }
    
    public int getTag() {
        return this.tag;
    }
    
    @Override
    public String toString() {
        return new StringBuffer().append((char)(this.tag >> 24 & 0xFF)).append((char)(this.tag >> 16 & 0xFF)).append((char)(this.tag >> 8 & 0xFF)).append((char)(this.tag & 0xFF)).append(", offset: ").append(this.offset).append(", length: ").append(this.length).append(", checksum: 0x").append(Integer.toHexString(this.checksum)).toString();
    }
}
