// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

public abstract class SVGGraphicObjectConverter implements SVGSyntax
{
    protected SVGGeneratorContext generatorContext;
    
    public SVGGraphicObjectConverter(final SVGGeneratorContext generatorContext) {
        if (generatorContext == null) {
            throw new SVGGraphics2DRuntimeException("generatorContext should not be null");
        }
        this.generatorContext = generatorContext;
    }
    
    public final String doubleString(final double value) {
        return this.generatorContext.doubleString(value);
    }
}
