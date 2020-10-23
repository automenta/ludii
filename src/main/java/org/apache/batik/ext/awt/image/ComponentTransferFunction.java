// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image;

public interface ComponentTransferFunction
{
    public static final int IDENTITY = 0;
    public static final int TABLE = 1;
    public static final int DISCRETE = 2;
    public static final int LINEAR = 3;
    public static final int GAMMA = 4;
    
    int getType();
    
    float getSlope();
    
    float[] getTableValues();
    
    float getIntercept();
    
    float getAmplitude();
    
    float getExponent();
    
    float getOffset();
}
