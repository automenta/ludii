// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class Coverage
{
    public abstract int getFormat();
    
    public abstract int findGlyph(final int p0);
    
    protected static Coverage read(final RandomAccessFile raf) throws IOException {
        Coverage c = null;
        final int format = raf.readUnsignedShort();
        if (format == 1) {
            c = new CoverageFormat1(raf);
        }
        else if (format == 2) {
            c = new CoverageFormat2(raf);
        }
        return c;
    }
}
