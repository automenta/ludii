// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class LigatureSubst extends LookupSubtable
{
    public static LigatureSubst read(final RandomAccessFile raf, final int offset) throws IOException {
        LigatureSubst ls = null;
        raf.seek(offset);
        final int format = raf.readUnsignedShort();
        if (format == 1) {
            ls = new LigatureSubstFormat1(raf, offset);
        }
        return ls;
    }
}
