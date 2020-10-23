// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.image.renderable.RenderableImage;
import java.awt.image.RenderedImage;
import java.awt.image.ImageObserver;
import org.w3c.dom.Element;
import java.awt.Image;
import org.apache.batik.constants.XMLConstants;

public class DefaultImageHandler implements ImageHandler, ErrorConstants, XMLConstants
{
    @Override
    public void handleImage(final Image image, final Element imageElement, final SVGGeneratorContext generatorContext) {
        imageElement.setAttributeNS(null, "width", String.valueOf(image.getWidth(null)));
        imageElement.setAttributeNS(null, "height", String.valueOf(image.getHeight(null)));
        try {
            this.handleHREF(image, imageElement, generatorContext);
        }
        catch (SVGGraphics2DIOException e) {
            try {
                generatorContext.errorHandler.handleError(e);
            }
            catch (SVGGraphics2DIOException io) {
                throw new SVGGraphics2DRuntimeException(io);
            }
        }
    }
    
    @Override
    public void handleImage(final RenderedImage image, final Element imageElement, final SVGGeneratorContext generatorContext) {
        imageElement.setAttributeNS(null, "width", String.valueOf(image.getWidth()));
        imageElement.setAttributeNS(null, "height", String.valueOf(image.getHeight()));
        try {
            this.handleHREF(image, imageElement, generatorContext);
        }
        catch (SVGGraphics2DIOException e) {
            try {
                generatorContext.errorHandler.handleError(e);
            }
            catch (SVGGraphics2DIOException io) {
                throw new SVGGraphics2DRuntimeException(io);
            }
        }
    }
    
    @Override
    public void handleImage(final RenderableImage image, final Element imageElement, final SVGGeneratorContext generatorContext) {
        imageElement.setAttributeNS(null, "width", String.valueOf(image.getWidth()));
        imageElement.setAttributeNS(null, "height", String.valueOf(image.getHeight()));
        try {
            this.handleHREF(image, imageElement, generatorContext);
        }
        catch (SVGGraphics2DIOException e) {
            try {
                generatorContext.errorHandler.handleError(e);
            }
            catch (SVGGraphics2DIOException io) {
                throw new SVGGraphics2DRuntimeException(io);
            }
        }
    }
    
    protected void handleHREF(final Image image, final Element imageElement, final SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", image.toString());
    }
    
    protected void handleHREF(final RenderedImage image, final Element imageElement, final SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", image.toString());
    }
    
    protected void handleHREF(final RenderableImage image, final Element imageElement, final SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", image.toString());
    }
}
