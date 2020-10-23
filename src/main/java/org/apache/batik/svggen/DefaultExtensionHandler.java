// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;
import java.awt.Composite;
import java.awt.Paint;

public class DefaultExtensionHandler implements ExtensionHandler
{
    @Override
    public SVGPaintDescriptor handlePaint(final Paint paint, final SVGGeneratorContext generatorContext) {
        return null;
    }
    
    @Override
    public SVGCompositeDescriptor handleComposite(final Composite composite, final SVGGeneratorContext generatorContext) {
        return null;
    }
    
    @Override
    public SVGFilterDescriptor handleFilter(final BufferedImageOp filter, final Rectangle filterRect, final SVGGeneratorContext generatorContext) {
        return null;
    }
}
