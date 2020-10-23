// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image;

import java.awt.Color;

public interface Light
{
    boolean isConstant();
    
    void getLight(final double p0, final double p1, final double p2, final double[] p3);
    
    double[][][] getLightMap(final double p0, final double p1, final double p2, final double p3, final int p4, final int p5, final double[][][] p6);
    
    double[][] getLightRow(final double p0, final double p1, final double p2, final int p3, final double[][] p4, final double[][] p5);
    
    double[] getColor(final boolean p0);
    
    void setColor(final Color p0);
}
