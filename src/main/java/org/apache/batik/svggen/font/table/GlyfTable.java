// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class GlyfTable implements Table
{
    private byte[] buf;
    private GlyfDescript[] descript;
    
    protected GlyfTable(final DirectoryEntry de, final RandomAccessFile raf) throws IOException {
        this.buf = null;
        raf.seek(de.getOffset());
        raf.read(this.buf = new byte[de.getLength()]);
    }
    
    public void init(final int numGlyphs, final LocaTable loca) {
        if (this.buf == null) {
            return;
        }
        this.descript = new GlyfDescript[numGlyphs];
        final ByteArrayInputStream bais = new ByteArrayInputStream(this.buf);
        for (int i = 0; i < numGlyphs; ++i) {
            final int len = loca.getOffset(i + 1) - loca.getOffset(i);
            if (len > 0) {
                bais.reset();
                bais.skip(loca.getOffset(i));
                final short numberOfContours = (short)(bais.read() << 8 | bais.read());
                if (numberOfContours >= 0) {
                    this.descript[i] = new GlyfSimpleDescript(this, numberOfContours, bais);
                }
                else {
                    this.descript[i] = new GlyfCompositeDescript(this, bais);
                }
            }
        }
        this.buf = null;
        for (int i = 0; i < numGlyphs; ++i) {
            if (this.descript[i] != null) {
                this.descript[i].resolve();
            }
        }
    }
    
    public GlyfDescript getDescription(final int i) {
        return this.descript[i];
    }
    
    @Override
    public int getType() {
        return 1735162214;
    }
}
