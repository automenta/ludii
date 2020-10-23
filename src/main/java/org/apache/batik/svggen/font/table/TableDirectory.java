// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class TableDirectory
{
    private int version;
    private short numTables;
    private short searchRange;
    private short entrySelector;
    private short rangeShift;
    private DirectoryEntry[] entries;
    
    public TableDirectory(final RandomAccessFile raf) throws IOException {
        this.version = 0;
        this.numTables = 0;
        this.searchRange = 0;
        this.entrySelector = 0;
        this.rangeShift = 0;
        this.version = raf.readInt();
        this.numTables = raf.readShort();
        this.searchRange = raf.readShort();
        this.entrySelector = raf.readShort();
        this.rangeShift = raf.readShort();
        this.entries = new DirectoryEntry[this.numTables];
        for (int i = 0; i < this.numTables; ++i) {
            this.entries[i] = new DirectoryEntry(raf);
        }
        boolean modified = true;
        while (modified) {
            modified = false;
            for (int j = 0; j < this.numTables - 1; ++j) {
                if (this.entries[j].getOffset() > this.entries[j + 1].getOffset()) {
                    final DirectoryEntry temp = this.entries[j];
                    this.entries[j] = this.entries[j + 1];
                    this.entries[j + 1] = temp;
                    modified = true;
                }
            }
        }
    }
    
    public DirectoryEntry getEntry(final int index) {
        return this.entries[index];
    }
    
    public DirectoryEntry getEntryByTag(final int tag) {
        for (int i = 0; i < this.numTables; ++i) {
            if (this.entries[i].getTag() == tag) {
                return this.entries[i];
            }
        }
        return null;
    }
    
    public short getEntrySelector() {
        return this.entrySelector;
    }
    
    public short getNumTables() {
        return this.numTables;
    }
    
    public short getRangeShift() {
        return this.rangeShift;
    }
    
    public short getSearchRange() {
        return this.searchRange;
    }
    
    public int getVersion() {
        return this.version;
    }
}
