// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image;

import java.awt.Color;

public class PointLight extends AbstractLight
{
    private double lightX;
    private double lightY;
    private double lightZ;
    
    public double getLightX() {
        return this.lightX;
    }
    
    public double getLightY() {
        return this.lightY;
    }
    
    public double getLightZ() {
        return this.lightZ;
    }
    
    public PointLight(final double lightX, final double lightY, final double lightZ, final Color lightColor) {
        super(lightColor);
        this.lightX = lightX;
        this.lightY = lightY;
        this.lightZ = lightZ;
    }
    
    @Override
    public boolean isConstant() {
        return false;
    }
    
    @Override
    public final void getLight(final double x, final double y, final double z, final double[] L) {
        double L2 = this.lightX - x;
        double L3 = this.lightY - y;
        double L4 = this.lightZ - z;
        final double norm = Math.sqrt(L2 * L2 + L3 * L3 + L4 * L4);
        if (norm > 0.0) {
            final double invNorm = 1.0 / norm;
            L2 *= invNorm;
            L3 *= invNorm;
            L4 *= invNorm;
        }
        L[0] = L2;
        L[1] = L3;
        L[2] = L4;
    }
}
