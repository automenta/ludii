// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class HeadTable implements Table
{
    private int versionNumber;
    private int fontRevision;
    private int checkSumAdjustment;
    private int magicNumber;
    private short flags;
    private short unitsPerEm;
    private long created;
    private long modified;
    private short xMin;
    private short yMin;
    private short xMax;
    private short yMax;
    private short macStyle;
    private short lowestRecPPEM;
    private short fontDirectionHint;
    private short indexToLocFormat;
    private short glyphDataFormat;
    
    protected HeadTable(final DirectoryEntry de, final RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        this.versionNumber = raf.readInt();
        this.fontRevision = raf.readInt();
        this.checkSumAdjustment = raf.readInt();
        this.magicNumber = raf.readInt();
        this.flags = raf.readShort();
        this.unitsPerEm = raf.readShort();
        this.created = raf.readLong();
        this.modified = raf.readLong();
        this.xMin = raf.readShort();
        this.yMin = raf.readShort();
        this.xMax = raf.readShort();
        this.yMax = raf.readShort();
        this.macStyle = raf.readShort();
        this.lowestRecPPEM = raf.readShort();
        this.fontDirectionHint = raf.readShort();
        this.indexToLocFormat = raf.readShort();
        this.glyphDataFormat = raf.readShort();
    }
    
    public int getCheckSumAdjustment() {
        return this.checkSumAdjustment;
    }
    
    public long getCreated() {
        return this.created;
    }
    
    public short getFlags() {
        return this.flags;
    }
    
    public short getFontDirectionHint() {
        return this.fontDirectionHint;
    }
    
    public int getFontRevision() {
        return this.fontRevision;
    }
    
    public short getGlyphDataFormat() {
        return this.glyphDataFormat;
    }
    
    public short getIndexToLocFormat() {
        return this.indexToLocFormat;
    }
    
    public short getLowestRecPPEM() {
        return this.lowestRecPPEM;
    }
    
    public short getMacStyle() {
        return this.macStyle;
    }
    
    public long getModified() {
        return this.modified;
    }
    
    @Override
    public int getType() {
        return 1751474532;
    }
    
    public short getUnitsPerEm() {
        return this.unitsPerEm;
    }
    
    public int getVersionNumber() {
        return this.versionNumber;
    }
    
    public short getXMax() {
        return this.xMax;
    }
    
    public short getXMin() {
        return this.xMin;
    }
    
    public short getYMax() {
        return this.yMax;
    }
    
    public short getYMin() {
        return this.yMin;
    }
    
    @Override
    public String toString() {
        return new StringBuffer().append("head\n\tversionNumber: ").append(this.versionNumber).append("\n\tfontRevision: ").append(this.fontRevision).append("\n\tcheckSumAdjustment: ").append(this.checkSumAdjustment).append("\n\tmagicNumber: ").append(this.magicNumber).append("\n\tflags: ").append(this.flags).append("\n\tunitsPerEm: ").append(this.unitsPerEm).append("\n\tcreated: ").append(this.created).append("\n\tmodified: ").append(this.modified).append("\n\txMin: ").append(this.xMin).append(", yMin: ").append(this.yMin).append("\n\txMax: ").append(this.xMax).append(", yMax: ").append(this.yMax).append("\n\tmacStyle: ").append(this.macStyle).append("\n\tlowestRecPPEM: ").append(this.lowestRecPPEM).append("\n\tfontDirectionHint: ").append(this.fontDirectionHint).append("\n\tindexToLocFormat: ").append(this.indexToLocFormat).append("\n\tglyphDataFormat: ").append(this.glyphDataFormat).toString();
    }
}
