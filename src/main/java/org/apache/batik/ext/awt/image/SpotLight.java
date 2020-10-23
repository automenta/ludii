// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image;

import java.awt.Color;

public class SpotLight extends AbstractLight
{
    private double lightX;
    private double lightY;
    private double lightZ;
    private double pointAtX;
    private double pointAtY;
    private double pointAtZ;
    private double specularExponent;
    private double limitingConeAngle;
    private double limitingCos;
    private final double[] S;
    
    public double getLightX() {
        return this.lightX;
    }
    
    public double getLightY() {
        return this.lightY;
    }
    
    public double getLightZ() {
        return this.lightZ;
    }
    
    public double getPointAtX() {
        return this.pointAtX;
    }
    
    public double getPointAtY() {
        return this.pointAtY;
    }
    
    public double getPointAtZ() {
        return this.pointAtZ;
    }
    
    public double getSpecularExponent() {
        return this.specularExponent;
    }
    
    public double getLimitingConeAngle() {
        return this.limitingConeAngle;
    }
    
    public SpotLight(final double lightX, final double lightY, final double lightZ, final double pointAtX, final double pointAtY, final double pointAtZ, final double specularExponent, final double limitingConeAngle, final Color lightColor) {
        super(lightColor);
        this.S = new double[3];
        this.lightX = lightX;
        this.lightY = lightY;
        this.lightZ = lightZ;
        this.pointAtX = pointAtX;
        this.pointAtY = pointAtY;
        this.pointAtZ = pointAtZ;
        this.specularExponent = specularExponent;
        this.limitingConeAngle = limitingConeAngle;
        this.limitingCos = Math.cos(Math.toRadians(limitingConeAngle));
        this.S[0] = pointAtX - lightX;
        this.S[1] = pointAtY - lightY;
        this.S[2] = pointAtZ - lightZ;
        final double invNorm = 1.0 / Math.sqrt(this.S[0] * this.S[0] + this.S[1] * this.S[1] + this.S[2] * this.S[2]);
        final double[] s = this.S;
        final int n = 0;
        s[n] *= invNorm;
        final double[] s2 = this.S;
        final int n2 = 1;
        s2[n2] *= invNorm;
        final double[] s3 = this.S;
        final int n3 = 2;
        s3[n3] *= invNorm;
    }
    
    @Override
    public boolean isConstant() {
        return false;
    }
    
    public final double getLightBase(final double x, final double y, final double z, final double[] L) {
        double L2 = this.lightX - x;
        double L3 = this.lightY - y;
        double L4 = this.lightZ - z;
        final double invNorm = 1.0 / Math.sqrt(L2 * L2 + L3 * L3 + L4 * L4);
        L2 *= invNorm;
        L3 *= invNorm;
        L4 *= invNorm;
        final double LS = -(L2 * this.S[0] + L3 * this.S[1] + L4 * this.S[2]);
        L[0] = L2;
        L[1] = L3;
        L[2] = L4;
        if (LS <= this.limitingCos) {
            return 0.0;
        }
        double Iatt = this.limitingCos / LS;
        Iatt *= Iatt;
        Iatt *= Iatt;
        Iatt *= Iatt;
        Iatt *= Iatt;
        Iatt *= Iatt;
        Iatt *= Iatt;
        Iatt = 1.0 - Iatt;
        return Iatt * Math.pow(LS, this.specularExponent);
    }
    
    @Override
    public final void getLight(final double x, final double y, final double z, final double[] L) {
        final double s = this.getLightBase(x, y, z, L);
        final int n = 0;
        L[n] *= s;
        final int n2 = 1;
        L[n2] *= s;
        final int n3 = 2;
        L[n3] *= s;
    }
    
    public final void getLight4(final double x, final double y, final double z, final double[] L) {
        L[3] = this.getLightBase(x, y, z, L);
    }
    
    public double[][] getLightRow4(double x, final double y, final double dx, final int width, final double[][] z, final double[][] lightRow) {
        double[][] ret = lightRow;
        if (ret == null) {
            ret = new double[width][4];
        }
        for (int i = 0; i < width; ++i) {
            this.getLight4(x, y, z[i][3], ret[i]);
            x += dx;
        }
        return ret;
    }
}
