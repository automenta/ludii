// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d;

import java.awt.*;

public final class GradientPaintKey
{
    private final GradientPaint paint;
    
    public GradientPaintKey(final GradientPaint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.paint = paint;
    }
    
    public GradientPaint getPaint() {
        return this.paint;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GradientPaintKey)) {
            return false;
        }
        final GradientPaintKey that = (GradientPaintKey)obj;
        final GradientPaint thisGP = this.paint;
        final GradientPaint thatGP = that.getPaint();
        return thisGP.getColor1().equals(thatGP.getColor1()) && thisGP.getColor2().equals(thatGP.getColor2()) && thisGP.getPoint1().equals(thatGP.getPoint1()) && thisGP.getPoint2().equals(thatGP.getPoint2()) && thisGP.getTransparency() == thatGP.getTransparency() && thisGP.isCyclic() == thatGP.isCyclic();
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.paint.getPoint1().hashCode();
        hash = 47 * hash + this.paint.getPoint2().hashCode();
        hash = 47 * hash + this.paint.getColor1().hashCode();
        hash = 47 * hash + this.paint.getColor2().hashCode();
        return hash;
    }
}
