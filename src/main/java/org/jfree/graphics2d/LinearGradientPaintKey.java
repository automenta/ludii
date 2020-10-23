// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d;

import java.awt.*;
import java.util.Arrays;

public class LinearGradientPaintKey
{
    private final LinearGradientPaint paint;
    
    public LinearGradientPaintKey(final LinearGradientPaint lgp) {
        Args.nullNotPermitted(lgp, "lgp");
        this.paint = lgp;
    }
    
    public LinearGradientPaint getPaint() {
        return this.paint;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LinearGradientPaint)) {
            return false;
        }
        final LinearGradientPaint that = (LinearGradientPaint)obj;
        return this.paint.getStartPoint().equals(that.getStartPoint()) && this.paint.getEndPoint().equals(that.getEndPoint()) && Arrays.equals(this.paint.getColors(), that.getColors()) && Arrays.equals(this.paint.getFractions(), that.getFractions());
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.paint.getStartPoint().hashCode();
        hash = 47 * hash + this.paint.getEndPoint().hashCode();
        hash = 47 * hash + Arrays.hashCode(this.paint.getColors());
        hash = 47 * hash + Arrays.hashCode(this.paint.getFractions());
        return hash;
    }
}
