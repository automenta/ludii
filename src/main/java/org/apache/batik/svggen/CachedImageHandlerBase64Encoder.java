// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.io.IOException;
import org.apache.batik.ext.awt.image.spi.ImageWriter;
import java.awt.image.RenderedImage;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import org.apache.batik.util.Base64EncoderStream;
import java.io.OutputStream;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import org.w3c.dom.Element;

public class CachedImageHandlerBase64Encoder extends DefaultCachedImageHandler
{
    public CachedImageHandlerBase64Encoder() {
        this.setImageCacher(new ImageCacher.Embedded());
    }
    
    @Override
    public Element createElement(final SVGGeneratorContext generatorContext) {
        final Element imageElement = generatorContext.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "use");
        return imageElement;
    }
    
    @Override
    public String getRefPrefix() {
        return "";
    }
    
    @Override
    protected AffineTransform handleTransform(final Element imageElement, final double x, final double y, final double srcWidth, final double srcHeight, final double dstWidth, final double dstHeight, final SVGGeneratorContext generatorContext) {
        final AffineTransform af = new AffineTransform();
        final double hRatio = dstWidth / srcWidth;
        final double vRatio = dstHeight / srcHeight;
        af.translate(x, y);
        if (hRatio != 1.0 || vRatio != 1.0) {
            af.scale(hRatio, vRatio);
        }
        if (!af.isIdentity()) {
            return af;
        }
        return null;
    }
    
    @Override
    public void encodeImage(final BufferedImage buf, final OutputStream os) throws IOException {
        final Base64EncoderStream b64Encoder = new Base64EncoderStream(os);
        final ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor("image/png");
        writer.writeImage(buf, b64Encoder);
        b64Encoder.close();
    }
    
    @Override
    public int getBufferedImageType() {
        return 2;
    }
}
