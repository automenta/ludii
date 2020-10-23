// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class CmapFormat
{
    protected int format;
    protected int length;
    protected int version;
    
    protected CmapFormat(final RandomAccessFile raf) throws IOException {
        this.length = raf.readUnsignedShort();
        this.version = raf.readUnsignedShort();
    }
    
    protected static CmapFormat create(final int format, final RandomAccessFile raf) throws IOException {
        switch (format) {
            case 0: {
                return new CmapFormat0(raf);
            }
            case 2: {
                return new CmapFormat2(raf);
            }
            case 4: {
                return new CmapFormat4(raf);
            }
            case 6: {
                return new CmapFormat6(raf);
            }
            default: {
                return null;
            }
        }
    }
    
    public int getFormat() {
        return this.format;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public abstract int mapCharCode(final int p0);
    
    public abstract int getFirst();
    
    public abstract int getLast();
    
    @Override
    public String toString() {
        return new StringBuffer().append("format: ").append(this.format).append(", length: ").append(this.length).append(", version: ").append(this.version).toString();
    }
}
