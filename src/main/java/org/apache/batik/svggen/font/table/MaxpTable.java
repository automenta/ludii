// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class MaxpTable implements Table
{
    private int versionNumber;
    private int numGlyphs;
    private int maxPoints;
    private int maxContours;
    private int maxCompositePoints;
    private int maxCompositeContours;
    private int maxZones;
    private int maxTwilightPoints;
    private int maxStorage;
    private int maxFunctionDefs;
    private int maxInstructionDefs;
    private int maxStackElements;
    private int maxSizeOfInstructions;
    private int maxComponentElements;
    private int maxComponentDepth;
    
    protected MaxpTable(final DirectoryEntry de, final RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        this.versionNumber = raf.readInt();
        this.numGlyphs = raf.readUnsignedShort();
        this.maxPoints = raf.readUnsignedShort();
        this.maxContours = raf.readUnsignedShort();
        this.maxCompositePoints = raf.readUnsignedShort();
        this.maxCompositeContours = raf.readUnsignedShort();
        this.maxZones = raf.readUnsignedShort();
        this.maxTwilightPoints = raf.readUnsignedShort();
        this.maxStorage = raf.readUnsignedShort();
        this.maxFunctionDefs = raf.readUnsignedShort();
        this.maxInstructionDefs = raf.readUnsignedShort();
        this.maxStackElements = raf.readUnsignedShort();
        this.maxSizeOfInstructions = raf.readUnsignedShort();
        this.maxComponentElements = raf.readUnsignedShort();
        this.maxComponentDepth = raf.readUnsignedShort();
    }
    
    public int getMaxComponentDepth() {
        return this.maxComponentDepth;
    }
    
    public int getMaxComponentElements() {
        return this.maxComponentElements;
    }
    
    public int getMaxCompositeContours() {
        return this.maxCompositeContours;
    }
    
    public int getMaxCompositePoints() {
        return this.maxCompositePoints;
    }
    
    public int getMaxContours() {
        return this.maxContours;
    }
    
    public int getMaxFunctionDefs() {
        return this.maxFunctionDefs;
    }
    
    public int getMaxInstructionDefs() {
        return this.maxInstructionDefs;
    }
    
    public int getMaxPoints() {
        return this.maxPoints;
    }
    
    public int getMaxSizeOfInstructions() {
        return this.maxSizeOfInstructions;
    }
    
    public int getMaxStackElements() {
        return this.maxStackElements;
    }
    
    public int getMaxStorage() {
        return this.maxStorage;
    }
    
    public int getMaxTwilightPoints() {
        return this.maxTwilightPoints;
    }
    
    public int getMaxZones() {
        return this.maxZones;
    }
    
    public int getNumGlyphs() {
        return this.numGlyphs;
    }
    
    @Override
    public int getType() {
        return 1835104368;
    }
}
