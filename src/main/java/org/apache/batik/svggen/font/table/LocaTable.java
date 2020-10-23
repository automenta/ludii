// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LocaTable implements Table
{
    private byte[] buf;
    private int[] offsets;
    private short factor;
    
    protected LocaTable(final DirectoryEntry de, final RandomAccessFile raf) throws IOException {
        this.buf = null;
        this.offsets = null;
        this.factor = 0;
        raf.seek(de.getOffset());
        raf.read(this.buf = new byte[de.getLength()]);
    }
    
    public void init(final int numGlyphs, final boolean shortEntries) {
        if (this.buf == null) {
            return;
        }
        this.offsets = new int[numGlyphs + 1];
        final ByteArrayInputStream bais = new ByteArrayInputStream(this.buf);
        if (shortEntries) {
            this.factor = 2;
            for (int i = 0; i <= numGlyphs; ++i) {
                this.offsets[i] = (bais.read() << 8 | bais.read());
            }
        }
        else {
            this.factor = 1;
            for (int i = 0; i <= numGlyphs; ++i) {
                this.offsets[i] = (bais.read() << 24 | bais.read() << 16 | bais.read() << 8 | bais.read());
            }
        }
        this.buf = null;
    }
    
    public int getOffset(final int i) {
        if (this.offsets == null) {
            return 0;
        }
        return this.offsets[i] * this.factor;
    }
    
    @Override
    public int getType() {
        return 1819239265;
    }
}
