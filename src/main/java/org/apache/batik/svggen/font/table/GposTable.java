// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class GposTable implements Table
{
    protected GposTable(final DirectoryEntry de, final RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        raf.readInt();
        raf.readInt();
        raf.readInt();
        raf.readInt();
    }
    
    @Override
    public int getType() {
        return 1196445523;
    }
    
    @Override
    public String toString() {
        return "GPOS";
    }
}
