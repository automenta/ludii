// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.image.renderable.RenderableImage;
import java.awt.image.RenderedImage;
import org.w3c.dom.Element;
import java.awt.Image;

public interface ImageHandler extends SVGSyntax
{
    void handleImage(final Image p0, final Element p1, final SVGGeneratorContext p2);
    
    void handleImage(final RenderedImage p0, final Element p1, final SVGGeneratorContext p2);
    
    void handleImage(final RenderableImage p0, final Element p1, final SVGGeneratorContext p2);
}
