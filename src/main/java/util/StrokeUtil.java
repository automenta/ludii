// 
// Decompiled by Procyon v0.5.36
// 

package util;

import metadata.graphics.util.LineStyle;

import java.awt.*;

public class StrokeUtil
{
    public static BasicStroke getStrokeFromStyle(final LineStyle lineStyle, final BasicStroke strokeThin, final BasicStroke strokeThick) {
        switch (lineStyle) {
            case Hidden -> {
                return new BasicStroke(0.0f);
            }
            case Thick -> {
                return strokeThick;
            }
            case ThickDashed -> {
                return getDashedStroke(strokeThick.getLineWidth());
            }
            case ThickDotted -> {
                return getDottedStroke(strokeThick.getLineWidth());
            }
            case Thin -> {
                return strokeThick;
            }
            case ThinDashed -> {
                return getDashedStroke(strokeThin.getLineWidth());
            }
            case ThinDotted -> {
                return getDottedStroke(strokeThin.getLineWidth());
            }
            default -> {
                return null;
            }
        }
    }
    
    public static BasicStroke getDashedStroke(final float strokeWidth) {
        final float[] dash = { strokeWidth * 3.0f, strokeWidth * 3.0f };
        return new BasicStroke(strokeWidth, 0, 1, 0.0f, dash, 0.0f);
    }
    
    public static BasicStroke getDottedStroke(final float strokeWidth) {
        final float[] dash = { 0.0f, strokeWidth * 2.5f };
        return new BasicStroke(strokeWidth, 1, 1, 0.0f, dash, 0.0f);
    }
}
