// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class ClassDef
{
    public abstract int getFormat();
    
    protected static ClassDef read(final RandomAccessFile raf) throws IOException {
        ClassDef c = null;
        final int format = raf.readUnsignedShort();
        if (format == 1) {
            c = new ClassDefFormat1(raf);
        }
        else if (format == 2) {
            c = new ClassDefFormat2(raf);
        }
        return c;
    }
}
