// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.Dimension;
import org.apache.batik.ext.awt.image.spi.ImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.batik.util.Base64EncoderStream;
import java.io.ByteArrayOutputStream;
import java.awt.image.renderable.RenderableImage;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.ImageObserver;
import org.w3c.dom.Element;
import java.awt.Image;

public class ImageHandlerBase64Encoder extends DefaultImageHandler
{
    public void handleHREF(final Image image, final Element imageElement, final SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        if (image == null) {
            throw new SVGGraphics2DRuntimeException("image should not be null");
        }
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        if (width == 0 || height == 0) {
            this.handleEmptyImage(imageElement);
        }
        else if (image instanceof RenderedImage) {
            this.handleHREF((RenderedImage)image, imageElement, generatorContext);
        }
        else {
            final BufferedImage buf = new BufferedImage(width, height, 2);
            final Graphics2D g = buf.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            this.handleHREF((RenderedImage)buf, imageElement, generatorContext);
        }
    }
    
    public void handleHREF(final RenderableImage image, final Element imageElement, final SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        if (image == null) {
            throw new SVGGraphics2DRuntimeException("image should not be null");
        }
        final RenderedImage r = image.createDefaultRendering();
        if (r == null) {
            this.handleEmptyImage(imageElement);
        }
        else {
            this.handleHREF(r, imageElement, generatorContext);
        }
    }
    
    protected void handleEmptyImage(final Element imageElement) {
        imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", "data:image/png;base64,");
        imageElement.setAttributeNS(null, "width", "0");
        imageElement.setAttributeNS(null, "height", "0");
    }
    
    public void handleHREF(final RenderedImage image, final Element imageElement, final SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final Base64EncoderStream b64Encoder = new Base64EncoderStream(os);
        try {
            this.encodeImage(image, b64Encoder);
            b64Encoder.close();
        }
        catch (IOException e) {
            throw new SVGGraphics2DIOException("unexpected exception", e);
        }
        imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", "data:image/png;base64," + os.toString());
    }
    
    public void encodeImage(final RenderedImage buf, final OutputStream os) throws SVGGraphics2DIOException {
        try {
            final ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor("image/png");
            writer.writeImage(buf, os);
        }
        catch (IOException e) {
            throw new SVGGraphics2DIOException("unexpected exception");
        }
    }
    
    public BufferedImage buildBufferedImage(final Dimension size) {
        return new BufferedImage(size.width, size.height, 2);
    }
}
