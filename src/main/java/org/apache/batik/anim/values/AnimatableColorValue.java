// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatableColorValue extends AnimatableValue
{
    protected float red;
    protected float green;
    protected float blue;
    
    protected AnimatableColorValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableColorValue(final AnimationTarget target, final float r, final float g, final float b) {
        super(target);
        this.red = r;
        this.green = g;
        this.blue = b;
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        AnimatableColorValue res;
        if (result == null) {
            res = new AnimatableColorValue(this.target);
        }
        else {
            res = (AnimatableColorValue)result;
        }
        final float oldRed = res.red;
        final float oldGreen = res.green;
        final float oldBlue = res.blue;
        res.red = this.red;
        res.green = this.green;
        res.blue = this.blue;
        final AnimatableColorValue toColor = (AnimatableColorValue)to;
        final AnimatableColorValue accColor = (AnimatableColorValue)accumulation;
        if (to != null) {
            final AnimatableColorValue animatableColorValue = res;
            animatableColorValue.red += interpolation * (toColor.red - res.red);
            final AnimatableColorValue animatableColorValue2 = res;
            animatableColorValue2.green += interpolation * (toColor.green - res.green);
            final AnimatableColorValue animatableColorValue3 = res;
            animatableColorValue3.blue += interpolation * (toColor.blue - res.blue);
        }
        if (accumulation != null) {
            final AnimatableColorValue animatableColorValue4 = res;
            animatableColorValue4.red += multiplier * accColor.red;
            final AnimatableColorValue animatableColorValue5 = res;
            animatableColorValue5.green += multiplier * accColor.green;
            final AnimatableColorValue animatableColorValue6 = res;
            animatableColorValue6.blue += multiplier * accColor.blue;
        }
        if (res.red != oldRed || res.green != oldGreen || res.blue != oldBlue) {
            res.hasChanged = true;
        }
        return res;
    }
    
    @Override
    public boolean canPace() {
        return true;
    }
    
    @Override
    public float distanceTo(final AnimatableValue other) {
        final AnimatableColorValue o = (AnimatableColorValue)other;
        final float dr = this.red - o.red;
        final float dg = this.green - o.green;
        final float db = this.blue - o.blue;
        return (float)Math.sqrt(dr * dr + dg * dg + db * db);
    }
    
    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatableColorValue(this.target, 0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public String getCssText() {
        return "rgb(" + Math.round(this.red * 255.0f) + ',' + Math.round(this.green * 255.0f) + ',' + Math.round(this.blue * 255.0f) + ')';
    }
}
