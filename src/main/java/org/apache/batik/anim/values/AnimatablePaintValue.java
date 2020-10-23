// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatablePaintValue extends AnimatableColorValue
{
    public static final int PAINT_NONE = 0;
    public static final int PAINT_CURRENT_COLOR = 1;
    public static final int PAINT_COLOR = 2;
    public static final int PAINT_URI = 3;
    public static final int PAINT_URI_NONE = 4;
    public static final int PAINT_URI_CURRENT_COLOR = 5;
    public static final int PAINT_URI_COLOR = 6;
    public static final int PAINT_INHERIT = 7;
    protected int paintType;
    protected String uri;
    
    protected AnimatablePaintValue(final AnimationTarget target) {
        super(target);
    }
    
    protected AnimatablePaintValue(final AnimationTarget target, final float r, final float g, final float b) {
        super(target, r, g, b);
    }
    
    public static AnimatablePaintValue createNonePaintValue(final AnimationTarget target) {
        final AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.paintType = 0;
        return v;
    }
    
    public static AnimatablePaintValue createCurrentColorPaintValue(final AnimationTarget target) {
        final AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.paintType = 1;
        return v;
    }
    
    public static AnimatablePaintValue createColorPaintValue(final AnimationTarget target, final float r, final float g, final float b) {
        final AnimatablePaintValue v = new AnimatablePaintValue(target, r, g, b);
        v.paintType = 2;
        return v;
    }
    
    public static AnimatablePaintValue createURIPaintValue(final AnimationTarget target, final String uri) {
        final AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.uri = uri;
        v.paintType = 3;
        return v;
    }
    
    public static AnimatablePaintValue createURINonePaintValue(final AnimationTarget target, final String uri) {
        final AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.uri = uri;
        v.paintType = 4;
        return v;
    }
    
    public static AnimatablePaintValue createURICurrentColorPaintValue(final AnimationTarget target, final String uri) {
        final AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.uri = uri;
        v.paintType = 5;
        return v;
    }
    
    public static AnimatablePaintValue createURIColorPaintValue(final AnimationTarget target, final String uri, final float r, final float g, final float b) {
        final AnimatablePaintValue v = new AnimatablePaintValue(target, r, g, b);
        v.uri = uri;
        v.paintType = 6;
        return v;
    }
    
    public static AnimatablePaintValue createInheritPaintValue(final AnimationTarget target) {
        final AnimatablePaintValue v = new AnimatablePaintValue(target);
        v.paintType = 7;
        return v;
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        AnimatablePaintValue res;
        if (result == null) {
            res = new AnimatablePaintValue(this.target);
        }
        else {
            res = (AnimatablePaintValue)result;
        }
        if (this.paintType == 2) {
            boolean canInterpolate = true;
            if (to != null) {
                final AnimatablePaintValue toPaint = (AnimatablePaintValue)to;
                canInterpolate = (toPaint.paintType == 2);
            }
            if (accumulation != null) {
                final AnimatablePaintValue accPaint = (AnimatablePaintValue)accumulation;
                canInterpolate = (canInterpolate && accPaint.paintType == 2);
            }
            if (canInterpolate) {
                res.paintType = 2;
                return super.interpolate(res, to, interpolation, accumulation, multiplier);
            }
        }
        int newPaintType;
        String newURI;
        float newRed;
        float newGreen;
        float newBlue;
        if (to != null && interpolation >= 0.5) {
            final AnimatablePaintValue toValue = (AnimatablePaintValue)to;
            newPaintType = toValue.paintType;
            newURI = toValue.uri;
            newRed = toValue.red;
            newGreen = toValue.green;
            newBlue = toValue.blue;
        }
        else {
            newPaintType = this.paintType;
            newURI = this.uri;
            newRed = this.red;
            newGreen = this.green;
            newBlue = this.blue;
        }
        if (res.paintType != newPaintType || res.uri == null || !res.uri.equals(newURI) || res.red != newRed || res.green != newGreen || res.blue != newBlue) {
            res.paintType = newPaintType;
            res.uri = newURI;
            res.red = newRed;
            res.green = newGreen;
            res.blue = newBlue;
            res.hasChanged = true;
        }
        return res;
    }
    
    public int getPaintType() {
        return this.paintType;
    }
    
    public String getURI() {
        return this.uri;
    }
    
    @Override
    public boolean canPace() {
        return false;
    }
    
    @Override
    public float distanceTo(final AnimatableValue other) {
        return 0.0f;
    }
    
    @Override
    public AnimatableValue getZeroValue() {
        return createColorPaintValue(this.target, 0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public String getCssText() {
        switch (this.paintType) {
            case 0: {
                return "none";
            }
            case 1: {
                return "currentColor";
            }
            case 2: {
                return super.getCssText();
            }
            case 3: {
                return "url(" + this.uri + ")";
            }
            case 4: {
                return "url(" + this.uri + ") none";
            }
            case 5: {
                return "url(" + this.uri + ") currentColor";
            }
            case 6: {
                return "url(" + this.uri + ") " + super.getCssText();
            }
            default: {
                return "inherit";
            }
        }
    }
}
