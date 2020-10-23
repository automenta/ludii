// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d;

import java.awt.*;
import java.util.Arrays;

public class RadialGradientPaintKey
{
    private final RadialGradientPaint paint;
    
    public RadialGradientPaintKey(final RadialGradientPaint rgp) {
        Args.nullNotPermitted(rgp, "rgp");
        this.paint = rgp;
    }
    
    public RadialGradientPaint getPaint() {
        return this.paint;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RadialGradientPaint)) {
            return false;
        }
        final RadialGradientPaint that = (RadialGradientPaint)obj;
        return this.paint.getCenterPoint().equals(that.getCenterPoint()) && this.paint.getFocusPoint().equals(that.getCenterPoint()) && Arrays.equals(this.paint.getColors(), that.getColors()) && Arrays.equals(this.paint.getFractions(), that.getFractions());
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.paint.getCenterPoint().hashCode();
        hash = 47 * hash + this.paint.getFocusPoint().hashCode();
        hash = 47 * hash + Float.floatToIntBits(this.paint.getRadius());
        hash = 47 * hash + Arrays.hashCode(this.paint.getColors());
        hash = 47 * hash + Arrays.hashCode(this.paint.getFractions());
        return hash;
    }
}
