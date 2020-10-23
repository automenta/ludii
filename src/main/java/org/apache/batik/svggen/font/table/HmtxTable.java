// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class HmtxTable implements Table
{
    private byte[] buf;
    private int[] hMetrics;
    private short[] leftSideBearing;
    
    protected HmtxTable(final DirectoryEntry de, final RandomAccessFile raf) throws IOException {
        this.buf = null;
        this.hMetrics = null;
        this.leftSideBearing = null;
        raf.seek(de.getOffset());
        raf.read(this.buf = new byte[de.getLength()]);
    }
    
    public void init(final int numberOfHMetrics, final int lsbCount) {
        if (this.buf == null) {
            return;
        }
        this.hMetrics = new int[numberOfHMetrics];
        final ByteArrayInputStream bais = new ByteArrayInputStream(this.buf);
        for (int i = 0; i < numberOfHMetrics; ++i) {
            this.hMetrics[i] = (bais.read() << 24 | bais.read() << 16 | bais.read() << 8 | bais.read());
        }
        if (lsbCount > 0) {
            this.leftSideBearing = new short[lsbCount];
            for (int i = 0; i < lsbCount; ++i) {
                this.leftSideBearing[i] = (short)(bais.read() << 8 | bais.read());
            }
        }
        this.buf = null;
    }
    
    public int getAdvanceWidth(final int i) {
        if (this.hMetrics == null) {
            return 0;
        }
        if (i < this.hMetrics.length) {
            return this.hMetrics[i] >> 16;
        }
        return this.hMetrics[this.hMetrics.length - 1] >> 16;
    }
    
    public short getLeftSideBearing(final int i) {
        if (this.hMetrics == null) {
            return 0;
        }
        if (i < this.hMetrics.length) {
            return (short)(this.hMetrics[i] & 0xFFFF);
        }
        return this.leftSideBearing[i - this.hMetrics.length];
    }
    
    @Override
    public int getType() {
        return 1752003704;
    }
}
