// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

public interface GlyphDescription
{
    int getEndPtOfContours(final int p0);
    
    byte getFlags(final int p0);
    
    short getXCoordinate(final int p0);
    
    short getYCoordinate(final int p0);
    
    short getXMaximum();
    
    short getXMinimum();
    
    short getYMaximum();
    
    short getYMinimum();
    
    boolean isComposite();
    
    int getPointCount();
    
    int getContourCount();
}
