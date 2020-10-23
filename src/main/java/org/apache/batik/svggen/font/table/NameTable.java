// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class NameTable implements Table
{
    private short formatSelector;
    private short numberOfNameRecords;
    private short stringStorageOffset;
    private NameRecord[] records;
    
    protected NameTable(final DirectoryEntry de, final RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        this.formatSelector = raf.readShort();
        this.numberOfNameRecords = raf.readShort();
        this.stringStorageOffset = raf.readShort();
        this.records = new NameRecord[this.numberOfNameRecords];
        for (int i = 0; i < this.numberOfNameRecords; ++i) {
            this.records[i] = new NameRecord(raf);
        }
        for (int i = 0; i < this.numberOfNameRecords; ++i) {
            this.records[i].loadString(raf, de.getOffset() + this.stringStorageOffset);
        }
    }
    
    public String getRecord(final short nameId) {
        for (int i = 0; i < this.numberOfNameRecords; ++i) {
            if (this.records[i].getNameId() == nameId) {
                return this.records[i].getRecordString();
            }
        }
        return "";
    }
    
    @Override
    public int getType() {
        return 1851878757;
    }
}
