// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FpgmTable extends Program implements Table
{
    protected FpgmTable(final DirectoryEntry de, final RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        this.readInstructions(raf, de.getLength());
    }
    
    @Override
    public int getType() {
        return 1718642541;
    }
}
