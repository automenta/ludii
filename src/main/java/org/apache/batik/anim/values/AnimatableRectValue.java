// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.values;

import org.apache.batik.anim.dom.AnimationTarget;

public class AnimatableRectValue extends AnimatableValue
{
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    
    protected AnimatableRectValue(final AnimationTarget target) {
        super(target);
    }
    
    public AnimatableRectValue(final AnimationTarget target, final float x, final float y, final float w, final float h) {
        super(target);
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }
    
    @Override
    public AnimatableValue interpolate(final AnimatableValue result, final AnimatableValue to, final float interpolation, final AnimatableValue accumulation, final int multiplier) {
        AnimatableRectValue res;
        if (result == null) {
            res = new AnimatableRectValue(this.target);
        }
        else {
            res = (AnimatableRectValue)result;
        }
        float newX = this.x;
        float newY = this.y;
        float newWidth = this.width;
        float newHeight = this.height;
        if (to != null) {
            final AnimatableRectValue toValue = (AnimatableRectValue)to;
            newX += interpolation * (toValue.x - this.x);
            newY += interpolation * (toValue.y - this.y);
            newWidth += interpolation * (toValue.width - this.width);
            newHeight += interpolation * (toValue.height - this.height);
        }
        if (accumulation != null && multiplier != 0) {
            final AnimatableRectValue accValue = (AnimatableRectValue)accumulation;
            newX += multiplier * accValue.x;
            newY += multiplier * accValue.y;
            newWidth += multiplier * accValue.width;
            newHeight += multiplier * accValue.height;
        }
        if (res.x != newX || res.y != newY || res.width != newWidth || res.height != newHeight) {
            res.x = newX;
            res.y = newY;
            res.width = newWidth;
            res.height = newHeight;
            res.hasChanged = true;
        }
        return res;
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public float getHeight() {
        return this.height;
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
        return new AnimatableRectValue(this.target, 0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public String toStringRep() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.x);
        sb.append(',');
        sb.append(this.y);
        sb.append(',');
        sb.append(this.width);
        sb.append(',');
        sb.append(this.height);
        return sb.toString();
    }
}
