// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.geom;

public interface ExtendedPathIterator
{
    public static final int SEG_CLOSE = 4;
    public static final int SEG_MOVETO = 0;
    public static final int SEG_LINETO = 1;
    public static final int SEG_QUADTO = 2;
    public static final int SEG_CUBICTO = 3;
    public static final int SEG_ARCTO = 4321;
    public static final int WIND_EVEN_ODD = 0;
    public static final int WIND_NON_ZERO = 1;
    
    int currentSegment();
    
    int currentSegment(final double[] p0);
    
    int currentSegment(final float[] p0);
    
    int getWindingRule();
    
    boolean isDone();
    
    void next();
}
