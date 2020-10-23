// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt;

import java.awt.geom.AffineTransform;
import java.awt.Color;
import java.awt.Paint;

public abstract class MultipleGradientPaint implements Paint
{
    protected int transparency;
    protected float[] fractions;
    protected Color[] colors;
    protected AffineTransform gradientTransform;
    protected CycleMethodEnum cycleMethod;
    protected ColorSpaceEnum colorSpace;
    public static final CycleMethodEnum NO_CYCLE;
    public static final CycleMethodEnum REFLECT;
    public static final CycleMethodEnum REPEAT;
    public static final ColorSpaceEnum SRGB;
    public static final ColorSpaceEnum LINEAR_RGB;
    
    public MultipleGradientPaint(final float[] fractions, final Color[] colors, final CycleMethodEnum cycleMethod, final ColorSpaceEnum colorSpace, final AffineTransform gradientTransform) {
        if (fractions == null) {
            throw new IllegalArgumentException("Fractions array cannot be null");
        }
        if (colors == null) {
            throw new IllegalArgumentException("Colors array cannot be null");
        }
        if (fractions.length != colors.length) {
            throw new IllegalArgumentException("Colors and fractions must have equal size");
        }
        if (colors.length < 2) {
            throw new IllegalArgumentException("User must specify at least 2 colors");
        }
        if (colorSpace != MultipleGradientPaint.LINEAR_RGB && colorSpace != MultipleGradientPaint.SRGB) {
            throw new IllegalArgumentException("Invalid colorspace for interpolation.");
        }
        if (cycleMethod != MultipleGradientPaint.NO_CYCLE && cycleMethod != MultipleGradientPaint.REFLECT && cycleMethod != MultipleGradientPaint.REPEAT) {
            throw new IllegalArgumentException("Invalid cycle method.");
        }
        if (gradientTransform == null) {
            throw new IllegalArgumentException("Gradient transform cannot be null.");
        }
        System.arraycopy(fractions, 0, this.fractions = new float[fractions.length], 0, fractions.length);
        System.arraycopy(colors, 0, this.colors = new Color[colors.length], 0, colors.length);
        this.colorSpace = colorSpace;
        this.cycleMethod = cycleMethod;
        this.gradientTransform = (AffineTransform)gradientTransform.clone();
        boolean opaque = true;
        for (final Color color : colors) {
            opaque = (opaque && color.getAlpha() == 255);
        }
        if (opaque) {
            this.transparency = 1;
        }
        else {
            this.transparency = 3;
        }
    }
    
    public Color[] getColors() {
        final Color[] colors = new Color[this.colors.length];
        System.arraycopy(this.colors, 0, colors, 0, this.colors.length);
        return colors;
    }
    
    public float[] getFractions() {
        final float[] fractions = new float[this.fractions.length];
        System.arraycopy(this.fractions, 0, fractions, 0, this.fractions.length);
        return fractions;
    }
    
    @Override
    public int getTransparency() {
        return this.transparency;
    }
    
    public CycleMethodEnum getCycleMethod() {
        return this.cycleMethod;
    }
    
    public ColorSpaceEnum getColorSpace() {
        return this.colorSpace;
    }
    
    public AffineTransform getTransform() {
        return (AffineTransform)this.gradientTransform.clone();
    }
    
    static {
        NO_CYCLE = new CycleMethodEnum();
        REFLECT = new CycleMethodEnum();
        REPEAT = new CycleMethodEnum();
        SRGB = new ColorSpaceEnum();
        LINEAR_RGB = new ColorSpaceEnum();
    }
    
    public static class ColorSpaceEnum
    {
    }
    
    public static class CycleMethodEnum
    {
    }
}
