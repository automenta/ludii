// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class KernTable implements Table
{
    private int version;
    private int nTables;
    private KernSubtable[] tables;
    
    protected KernTable(final DirectoryEntry de, final RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        this.version = raf.readUnsignedShort();
        this.nTables = raf.readUnsignedShort();
        this.tables = new KernSubtable[this.nTables];
        for (int i = 0; i < this.nTables; ++i) {
            this.tables[i] = KernSubtable.read(raf);
        }
    }
    
    public int getSubtableCount() {
        return this.nTables;
    }
    
    public KernSubtable getSubtable(final int i) {
        return this.tables[i];
    }
    
    @Override
    public int getType() {
        return 1801810542;
    }
}
