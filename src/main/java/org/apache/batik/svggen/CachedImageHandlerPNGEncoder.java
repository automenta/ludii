// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.io.IOException;
import org.apache.batik.ext.awt.image.spi.ImageWriter;
import java.awt.image.RenderedImage;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import java.io.OutputStream;
import java.awt.image.BufferedImage;

public class CachedImageHandlerPNGEncoder extends DefaultCachedImageHandler
{
    public static final String CACHED_PNG_PREFIX = "pngImage";
    public static final String CACHED_PNG_SUFFIX = ".png";
    protected String refPrefix;
    
    public CachedImageHandlerPNGEncoder(final String imageDir, final String urlRoot) throws SVGGraphics2DIOException {
        this.refPrefix = "";
        this.refPrefix = urlRoot + "/";
        this.setImageCacher(new ImageCacher.External(imageDir, "pngImage", ".png"));
    }
    
    @Override
    public void encodeImage(final BufferedImage buf, final OutputStream os) throws IOException {
        final ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor("image/png");
        writer.writeImage(buf, os);
    }
    
    @Override
    public int getBufferedImageType() {
        return 2;
    }
    
    @Override
    public String getRefPrefix() {
        return this.refPrefix;
    }
}
