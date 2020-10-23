// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.io.ByteArrayInputStream;

public abstract class GlyfDescript extends Program implements GlyphDescription
{
    public static final byte onCurve = 1;
    public static final byte xShortVector = 2;
    public static final byte yShortVector = 4;
    public static final byte repeat = 8;
    public static final byte xDual = 16;
    public static final byte yDual = 32;
    protected GlyfTable parentTable;
    private int numberOfContours;
    private short xMin;
    private short yMin;
    private short xMax;
    private short yMax;
    
    protected GlyfDescript(final GlyfTable parentTable, final short numberOfContours, final ByteArrayInputStream bais) {
        this.parentTable = parentTable;
        this.numberOfContours = numberOfContours;
        this.xMin = (short)(bais.read() << 8 | bais.read());
        this.yMin = (short)(bais.read() << 8 | bais.read());
        this.xMax = (short)(bais.read() << 8 | bais.read());
        this.yMax = (short)(bais.read() << 8 | bais.read());
    }
    
    public void resolve() {
    }
    
    public int getNumberOfContours() {
        return this.numberOfContours;
    }
    
    @Override
    public short getXMaximum() {
        return this.xMax;
    }
    
    @Override
    public short getXMinimum() {
        return this.xMin;
    }
    
    @Override
    public short getYMaximum() {
        return this.yMax;
    }
    
    @Override
    public short getYMinimum() {
        return this.yMin;
    }
}
