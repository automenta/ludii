// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.ByteArrayInputStream;

public class GlyfSimpleDescript extends GlyfDescript
{
    private int[] endPtsOfContours;
    private byte[] flags;
    private short[] xCoordinates;
    private short[] yCoordinates;
    private int count;
    
    public GlyfSimpleDescript(final GlyfTable parentTable, final short numberOfContours, final ByteArrayInputStream bais) {
        super(parentTable, numberOfContours, bais);
        this.endPtsOfContours = new int[numberOfContours];
        for (int i = 0; i < numberOfContours; ++i) {
            this.endPtsOfContours[i] = (bais.read() << 8 | bais.read());
        }
        this.count = this.endPtsOfContours[numberOfContours - 1] + 1;
        this.flags = new byte[this.count];
        this.xCoordinates = new short[this.count];
        this.yCoordinates = new short[this.count];
        final int instructionCount = bais.read() << 8 | bais.read();
        this.readInstructions(bais, instructionCount);
        this.readFlags(this.count, bais);
        this.readCoords(this.count, bais);
    }
    
    @Override
    public int getEndPtOfContours(final int i) {
        return this.endPtsOfContours[i];
    }
    
    @Override
    public byte getFlags(final int i) {
        return this.flags[i];
    }
    
    @Override
    public short getXCoordinate(final int i) {
        return this.xCoordinates[i];
    }
    
    @Override
    public short getYCoordinate(final int i) {
        return this.yCoordinates[i];
    }
    
    @Override
    public boolean isComposite() {
        return false;
    }
    
    @Override
    public int getPointCount() {
        return this.count;
    }
    
    @Override
    public int getContourCount() {
        return this.getNumberOfContours();
    }
    
    private void readCoords(final int count, final ByteArrayInputStream bais) {
        short x = 0;
        short y = 0;
        for (int i = 0; i < count; ++i) {
            if ((this.flags[i] & 0x10) != 0x0) {
                if ((this.flags[i] & 0x2) != 0x0) {
                    x += (short)bais.read();
                }
            }
            else if ((this.flags[i] & 0x2) != 0x0) {
                x += (short)(-(short)bais.read());
            }
            else {
                x += (short)(bais.read() << 8 | bais.read());
            }
            this.xCoordinates[i] = x;
        }
        for (int i = 0; i < count; ++i) {
            if ((this.flags[i] & 0x20) != 0x0) {
                if ((this.flags[i] & 0x4) != 0x0) {
                    y += (short)bais.read();
                }
            }
            else if ((this.flags[i] & 0x4) != 0x0) {
                y += (short)(-(short)bais.read());
            }
            else {
                y += (short)(bais.read() << 8 | bais.read());
            }
            this.yCoordinates[i] = y;
        }
    }
    
    private void readFlags(final int flagCount, final ByteArrayInputStream bais) {
        try {
            for (int index = 0; index < flagCount; ++index) {
                this.flags[index] = (byte)bais.read();
                if ((this.flags[index] & 0x8) != 0x0) {
                    final int repeats = bais.read();
                    for (int i = 1; i <= repeats; ++i) {
                        this.flags[index + i] = this.flags[index];
                    }
                    index += repeats;
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("error: array index out of bounds");
        }
    }
}
