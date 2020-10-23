// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.BasicStroke;
import org.apache.batik.ext.awt.g2d.GraphicContext;

public class SVGBasicStroke extends AbstractSVGConverter
{
    public SVGBasicStroke(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }
    
    @Override
    public SVGDescriptor toSVG(final GraphicContext gc) {
        if (gc.getStroke() instanceof BasicStroke) {
            return this.toSVG((BasicStroke)gc.getStroke());
        }
        return null;
    }
    
    public final SVGStrokeDescriptor toSVG(final BasicStroke stroke) {
        final String strokeWidth = this.doubleString(stroke.getLineWidth());
        final String capStyle = endCapToSVG(stroke.getEndCap());
        final String joinStyle = joinToSVG(stroke.getLineJoin());
        final String miterLimit = this.doubleString(stroke.getMiterLimit());
        final float[] array = stroke.getDashArray();
        String dashArray = null;
        if (array != null) {
            dashArray = this.dashArrayToSVG(array);
        }
        else {
            dashArray = "none";
        }
        final String dashOffset = this.doubleString(stroke.getDashPhase());
        return new SVGStrokeDescriptor(strokeWidth, capStyle, joinStyle, miterLimit, dashArray, dashOffset);
    }
    
    private final String dashArrayToSVG(final float[] dashArray) {
        final StringBuffer dashArrayBuf = new StringBuffer(dashArray.length * 8);
        if (dashArray.length > 0) {
            dashArrayBuf.append(this.doubleString(dashArray[0]));
        }
        for (int i = 1; i < dashArray.length; ++i) {
            dashArrayBuf.append(",");
            dashArrayBuf.append(this.doubleString(dashArray[i]));
        }
        return dashArrayBuf.toString();
    }
    
    private static String joinToSVG(final int lineJoin) {
        switch (lineJoin) {
            case 2: {
                return "bevel";
            }
            case 1: {
                return "round";
            }
            default: {
                return "miter";
            }
        }
    }
    
    private static String endCapToSVG(final int endCap) {
        switch (endCap) {
            case 0: {
                return "butt";
            }
            case 1: {
                return "round";
            }
            default: {
                return "square";
            }
        }
    }
}
