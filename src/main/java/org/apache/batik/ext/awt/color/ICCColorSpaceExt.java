// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.color;

import java.awt.color.ICC_Profile;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;

public class ICCColorSpaceExt extends ICC_ColorSpace
{
    public static final int PERCEPTUAL = 0;
    public static final int RELATIVE_COLORIMETRIC = 1;
    public static final int ABSOLUTE_COLORIMETRIC = 2;
    public static final int SATURATION = 3;
    public static final int AUTO = 4;
    static final ColorSpace sRGB;
    int intent;
    
    public ICCColorSpaceExt(final ICC_Profile p, final int intent) {
        super(p);
        switch (this.intent = intent) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4: {
                if (intent != 4) {
                    final byte[] hdr = p.getData(1751474532);
                    hdr[64] = (byte)intent;
                }
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public float[] intendedToRGB(final float[] values) {
        switch (this.intent) {
            case 2: {
                return this.absoluteColorimetricToRGB(values);
            }
            case 0:
            case 4: {
                return this.perceptualToRGB(values);
            }
            case 1: {
                return this.relativeColorimetricToRGB(values);
            }
            case 3: {
                return this.saturationToRGB(values);
            }
            default: {
                throw new RuntimeException("invalid intent:" + this.intent);
            }
        }
    }
    
    public float[] perceptualToRGB(final float[] values) {
        return this.toRGB(values);
    }
    
    public float[] relativeColorimetricToRGB(final float[] values) {
        final float[] ciexyz = this.toCIEXYZ(values);
        return ICCColorSpaceExt.sRGB.fromCIEXYZ(ciexyz);
    }
    
    public float[] absoluteColorimetricToRGB(final float[] values) {
        return this.perceptualToRGB(values);
    }
    
    public float[] saturationToRGB(final float[] values) {
        return this.perceptualToRGB(values);
    }
    
    static {
        sRGB = ColorSpace.getInstance(1000);
    }
}
