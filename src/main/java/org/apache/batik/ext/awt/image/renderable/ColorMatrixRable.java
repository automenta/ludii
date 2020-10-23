// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

public interface ColorMatrixRable extends FilterColorInterpolation
{
    public static final int TYPE_MATRIX = 0;
    public static final int TYPE_SATURATE = 1;
    public static final int TYPE_HUE_ROTATE = 2;
    public static final int TYPE_LUMINANCE_TO_ALPHA = 3;
    
    Filter getSource();
    
    void setSource(final Filter p0);
    
    int getType();
    
    float[][] getMatrix();
}
