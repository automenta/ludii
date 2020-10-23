// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image;

import java.awt.Color;

public abstract class AbstractLight implements Light
{
    private double[] color;
    
    public static final double sRGBToLsRGB(final double value) {
        if (value <= 0.003928) {
            return value / 12.92;
        }
        return Math.pow((value + 0.055) / 1.055, 2.4);
    }
    
    @Override
    public double[] getColor(final boolean linear) {
        final double[] ret = new double[3];
        if (linear) {
            ret[0] = sRGBToLsRGB(this.color[0]);
            ret[1] = sRGBToLsRGB(this.color[1]);
            ret[2] = sRGBToLsRGB(this.color[2]);
        }
        else {
            ret[0] = this.color[0];
            ret[1] = this.color[1];
            ret[2] = this.color[2];
        }
        return ret;
    }
    
    public AbstractLight(final Color color) {
        this.setColor(color);
    }
    
    @Override
    public void setColor(final Color newColor) {
        (this.color = new double[3])[0] = newColor.getRed() / 255.0;
        this.color[1] = newColor.getGreen() / 255.0;
        this.color[2] = newColor.getBlue() / 255.0;
    }
    
    @Override
    public boolean isConstant() {
        return true;
    }
    
    @Override
    public double[][][] getLightMap(final double x, double y, final double dx, final double dy, final int width, final int height, final double[][][] z) {
        final double[][][] L = new double[height][][];
        for (int i = 0; i < height; ++i) {
            L[i] = this.getLightRow(x, y, dx, width, z[i], null);
            y += dy;
        }
        return L;
    }
    
    @Override
    public double[][] getLightRow(double x, final double y, final double dx, final int width, final double[][] z, final double[][] lightRow) {
        double[][] ret = lightRow;
        if (ret == null) {
            ret = new double[width][3];
        }
        for (int i = 0; i < width; ++i) {
            this.getLight(x, y, z[i][3], ret[i]);
            x += dx;
        }
        return ret;
    }
}
