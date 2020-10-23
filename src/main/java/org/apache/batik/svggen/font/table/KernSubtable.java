// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class KernSubtable
{
    protected KernSubtable() {
    }
    
    public abstract int getKerningPairCount();
    
    public abstract KerningPair getKerningPair(final int p0);
    
    public static KernSubtable read(final RandomAccessFile raf) throws IOException {
        KernSubtable table = null;
        raf.readUnsignedShort();
        raf.readUnsignedShort();
        final int coverage = raf.readUnsignedShort();
        final int format = coverage >> 8;
        switch (format) {
            case 0: {
                table = new KernSubtableFormat0(raf);
                break;
            }
            case 2: {
                table = new KernSubtableFormat2(raf);
                break;
            }
        }
        return table;
    }
}
