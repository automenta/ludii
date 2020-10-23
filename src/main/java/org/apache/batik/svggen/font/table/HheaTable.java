// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class HheaTable implements Table
{
    private int version;
    private short ascender;
    private short descender;
    private short lineGap;
    private short advanceWidthMax;
    private short minLeftSideBearing;
    private short minRightSideBearing;
    private short xMaxExtent;
    private short caretSlopeRise;
    private short caretSlopeRun;
    private short metricDataFormat;
    private int numberOfHMetrics;
    
    protected HheaTable(final DirectoryEntry de, final RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        this.version = raf.readInt();
        this.ascender = raf.readShort();
        this.descender = raf.readShort();
        this.lineGap = raf.readShort();
        this.advanceWidthMax = raf.readShort();
        this.minLeftSideBearing = raf.readShort();
        this.minRightSideBearing = raf.readShort();
        this.xMaxExtent = raf.readShort();
        this.caretSlopeRise = raf.readShort();
        this.caretSlopeRun = raf.readShort();
        for (int i = 0; i < 5; ++i) {
            raf.readShort();
        }
        this.metricDataFormat = raf.readShort();
        this.numberOfHMetrics = raf.readUnsignedShort();
    }
    
    public short getAdvanceWidthMax() {
        return this.advanceWidthMax;
    }
    
    public short getAscender() {
        return this.ascender;
    }
    
    public short getCaretSlopeRise() {
        return this.caretSlopeRise;
    }
    
    public short getCaretSlopeRun() {
        return this.caretSlopeRun;
    }
    
    public short getDescender() {
        return this.descender;
    }
    
    public short getLineGap() {
        return this.lineGap;
    }
    
    public short getMetricDataFormat() {
        return this.metricDataFormat;
    }
    
    public short getMinLeftSideBearing() {
        return this.minLeftSideBearing;
    }
    
    public short getMinRightSideBearing() {
        return this.minRightSideBearing;
    }
    
    public int getNumberOfHMetrics() {
        return this.numberOfHMetrics;
    }
    
    @Override
    public int getType() {
        return 1751672161;
    }
    
    public short getXMaxExtent() {
        return this.xMaxExtent;
    }
}
