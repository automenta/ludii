// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.image.renderable.RenderableImage;
import java.awt.image.RenderedImage;
import java.awt.geom.AffineTransform;
import java.awt.Image;
import org.w3c.dom.Element;

public interface GenericImageHandler
{
    void setDOMTreeManager(final DOMTreeManager p0);
    
    Element createElement(final SVGGeneratorContext p0);
    
    AffineTransform handleImage(final Image p0, final Element p1, final int p2, final int p3, final int p4, final int p5, final SVGGeneratorContext p6);
    
    AffineTransform handleImage(final RenderedImage p0, final Element p1, final int p2, final int p3, final int p4, final int p5, final SVGGeneratorContext p6);
    
    AffineTransform handleImage(final RenderableImage p0, final Element p1, final double p2, final double p3, final double p4, final double p5, final SVGGeneratorContext p6);
}
