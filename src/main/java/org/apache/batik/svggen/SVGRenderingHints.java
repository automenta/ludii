// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.RenderingHints;
import org.apache.batik.ext.awt.g2d.GraphicContext;

public class SVGRenderingHints extends AbstractSVGConverter
{
    public SVGRenderingHints(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }
    
    @Override
    public SVGDescriptor toSVG(final GraphicContext gc) {
        return toSVG(gc.getRenderingHints());
    }
    
    public static SVGHintsDescriptor toSVG(final RenderingHints hints) {
        String colorInterpolation = "auto";
        String colorRendering = "auto";
        String textRendering = "auto";
        String shapeRendering = "auto";
        String imageRendering = "auto";
        if (hints != null) {
            final Object rendering = hints.get(RenderingHints.KEY_RENDERING);
            if (rendering == RenderingHints.VALUE_RENDER_DEFAULT) {
                colorInterpolation = "auto";
                colorRendering = "auto";
                textRendering = "auto";
                shapeRendering = "auto";
                imageRendering = "auto";
            }
            else if (rendering == RenderingHints.VALUE_RENDER_SPEED) {
                colorInterpolation = "sRGB";
                colorRendering = "optimizeSpeed";
                textRendering = "optimizeSpeed";
                shapeRendering = "geometricPrecision";
                imageRendering = "optimizeSpeed";
            }
            else if (rendering == RenderingHints.VALUE_RENDER_QUALITY) {
                colorInterpolation = "linearRGB";
                colorRendering = "optimizeQuality";
                textRendering = "optimizeQuality";
                shapeRendering = "geometricPrecision";
                imageRendering = "optimizeQuality";
            }
            final Object fractionalMetrics = hints.get(RenderingHints.KEY_FRACTIONALMETRICS);
            if (fractionalMetrics == RenderingHints.VALUE_FRACTIONALMETRICS_ON) {
                textRendering = "optimizeQuality";
                shapeRendering = "geometricPrecision";
            }
            else if (fractionalMetrics == RenderingHints.VALUE_FRACTIONALMETRICS_OFF) {
                textRendering = "optimizeSpeed";
                shapeRendering = "optimizeSpeed";
            }
            else if (fractionalMetrics == RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT) {
                textRendering = "auto";
                shapeRendering = "auto";
            }
            final Object antialiasing = hints.get(RenderingHints.KEY_ANTIALIASING);
            if (antialiasing == RenderingHints.VALUE_ANTIALIAS_ON) {
                textRendering = "optimizeLegibility";
                shapeRendering = "auto";
            }
            else if (antialiasing == RenderingHints.VALUE_ANTIALIAS_OFF) {
                textRendering = "geometricPrecision";
                shapeRendering = "crispEdges";
            }
            else if (antialiasing == RenderingHints.VALUE_ANTIALIAS_DEFAULT) {
                textRendering = "auto";
                shapeRendering = "auto";
            }
            final Object textAntialiasing = hints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
            if (textAntialiasing == RenderingHints.VALUE_TEXT_ANTIALIAS_ON) {
                textRendering = "geometricPrecision";
            }
            else if (textAntialiasing == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF) {
                textRendering = "optimizeSpeed";
            }
            else if (textAntialiasing == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT) {
                textRendering = "auto";
            }
            final Object colorRenderingHint = hints.get(RenderingHints.KEY_COLOR_RENDERING);
            if (colorRenderingHint == RenderingHints.VALUE_COLOR_RENDER_DEFAULT) {
                colorRendering = "auto";
            }
            else if (colorRenderingHint == RenderingHints.VALUE_COLOR_RENDER_QUALITY) {
                colorRendering = "optimizeQuality";
            }
            else if (colorRenderingHint == RenderingHints.VALUE_COLOR_RENDER_SPEED) {
                colorRendering = "optimizeSpeed";
            }
            final Object interpolation = hints.get(RenderingHints.KEY_INTERPOLATION);
            if (interpolation == RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR) {
                imageRendering = "optimizeSpeed";
            }
            else if (interpolation == RenderingHints.VALUE_INTERPOLATION_BICUBIC || interpolation == RenderingHints.VALUE_INTERPOLATION_BILINEAR) {
                imageRendering = "optimizeQuality";
            }
        }
        return new SVGHintsDescriptor(colorInterpolation, colorRendering, textRendering, shapeRendering, imageRendering);
    }
}
