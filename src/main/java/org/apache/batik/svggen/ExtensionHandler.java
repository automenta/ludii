// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;
import java.awt.Composite;
import java.awt.Paint;

public interface ExtensionHandler
{
    SVGPaintDescriptor handlePaint(final Paint p0, final SVGGeneratorContext p1);
    
    SVGCompositeDescriptor handleComposite(final Composite p0, final SVGGeneratorContext p1);
    
    SVGFilterDescriptor handleFilter(final BufferedImageOp p0, final Rectangle p1, final SVGGeneratorContext p2);
}
