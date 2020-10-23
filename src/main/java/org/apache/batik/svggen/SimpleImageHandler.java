// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.image.renderable.RenderableImage;
import java.awt.image.RenderedImage;
import java.awt.image.ImageObserver;
import java.awt.geom.AffineTransform;
import java.awt.Image;
import org.w3c.dom.Element;

public class SimpleImageHandler implements GenericImageHandler, SVGSyntax, ErrorConstants
{
    static final String XLINK_NAMESPACE_URI = "http://www.w3.org/1999/xlink";
    protected ImageHandler imageHandler;
    
    public SimpleImageHandler(final ImageHandler imageHandler) {
        if (imageHandler == null) {
            throw new IllegalArgumentException();
        }
        this.imageHandler = imageHandler;
    }
    
    @Override
    public void setDOMTreeManager(final DOMTreeManager domTreeManager) {
    }
    
    @Override
    public Element createElement(final SVGGeneratorContext generatorContext) {
        final Element imageElement = generatorContext.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "image");
        return imageElement;
    }
    
    @Override
    public AffineTransform handleImage(final Image image, final Element imageElement, final int x, final int y, final int width, final int height, final SVGGeneratorContext generatorContext) {
        final int imageWidth = image.getWidth(null);
        final int imageHeight = image.getHeight(null);
        if (imageWidth == 0 || imageHeight == 0 || width == 0 || height == 0) {
            this.handleEmptyImage(imageElement);
        }
        else {
            this.imageHandler.handleImage(image, imageElement, generatorContext);
            this.setImageAttributes(imageElement, x, y, width, height, generatorContext);
        }
        return null;
    }
    
    @Override
    public AffineTransform handleImage(final RenderedImage image, final Element imageElement, final int x, final int y, final int width, final int height, final SVGGeneratorContext generatorContext) {
        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();
        if (imageWidth == 0 || imageHeight == 0 || width == 0 || height == 0) {
            this.handleEmptyImage(imageElement);
        }
        else {
            this.imageHandler.handleImage(image, imageElement, generatorContext);
            this.setImageAttributes(imageElement, x, y, width, height, generatorContext);
        }
        return null;
    }
    
    @Override
    public AffineTransform handleImage(final RenderableImage image, final Element imageElement, final double x, final double y, final double width, final double height, final SVGGeneratorContext generatorContext) {
        final double imageWidth = image.getWidth();
        final double imageHeight = image.getHeight();
        if (imageWidth == 0.0 || imageHeight == 0.0 || width == 0.0 || height == 0.0) {
            this.handleEmptyImage(imageElement);
        }
        else {
            this.imageHandler.handleImage(image, imageElement, generatorContext);
            this.setImageAttributes(imageElement, x, y, width, height, generatorContext);
        }
        return null;
    }
    
    protected void setImageAttributes(final Element imageElement, final double x, final double y, final double width, final double height, final SVGGeneratorContext generatorContext) {
        imageElement.setAttributeNS(null, "x", generatorContext.doubleString(x));
        imageElement.setAttributeNS(null, "y", generatorContext.doubleString(y));
        imageElement.setAttributeNS(null, "width", generatorContext.doubleString(width));
        imageElement.setAttributeNS(null, "height", generatorContext.doubleString(height));
        imageElement.setAttributeNS(null, "preserveAspectRatio", "none");
    }
    
    protected void handleEmptyImage(final Element imageElement) {
        imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", "");
        imageElement.setAttributeNS(null, "width", "0");
        imageElement.setAttributeNS(null, "height", "0");
    }
}
