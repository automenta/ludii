// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image;

import java.awt.Color;

public class DistantLight extends AbstractLight
{
    private double azimuth;
    private double elevation;
    private double Lx;
    private double Ly;
    private double Lz;
    
    public double getAzimuth() {
        return this.azimuth;
    }
    
    public double getElevation() {
        return this.elevation;
    }
    
    public DistantLight(final double azimuth, final double elevation, final Color color) {
        super(color);
        this.azimuth = azimuth;
        this.elevation = elevation;
        this.Lx = Math.cos(Math.toRadians(azimuth)) * Math.cos(Math.toRadians(elevation));
        this.Ly = Math.sin(Math.toRadians(azimuth)) * Math.cos(Math.toRadians(elevation));
        this.Lz = Math.sin(Math.toRadians(elevation));
    }
    
    @Override
    public boolean isConstant() {
        return true;
    }
    
    @Override
    public void getLight(final double x, final double y, final double z, final double[] L) {
        L[0] = this.Lx;
        L[1] = this.Ly;
        L[2] = this.Lz;
    }
    
    @Override
    public double[][] getLightRow(final double x, final double y, final double dx, final int width, final double[][] z, final double[][] lightRow) {
        double[][] ret = lightRow;
        if (ret == null) {
            ret = new double[width][];
            final double[] CL = { this.Lx, this.Ly, this.Lz };
            for (int i = 0; i < width; ++i) {
                ret[i] = CL;
            }
        }
        else {
            final double lx = this.Lx;
            final double ly = this.Ly;
            final double lz = this.Lz;
            for (int j = 0; j < width; ++j) {
                ret[j][0] = lx;
                ret[j][1] = ly;
                ret[j][2] = lz;
            }
        }
        return ret;
    }
}
