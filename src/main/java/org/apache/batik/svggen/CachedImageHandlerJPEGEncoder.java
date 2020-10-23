// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.io.IOException;
import org.apache.batik.ext.awt.image.spi.ImageWriter;
import java.awt.image.RenderedImage;
import org.apache.batik.ext.awt.image.spi.ImageWriterParams;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import java.io.OutputStream;
import java.awt.image.BufferedImage;

public class CachedImageHandlerJPEGEncoder extends DefaultCachedImageHandler
{
    public static final String CACHED_JPEG_PREFIX = "jpegImage";
    public static final String CACHED_JPEG_SUFFIX = ".jpg";
    protected String refPrefix;
    
    public CachedImageHandlerJPEGEncoder(final String imageDir, final String urlRoot) throws SVGGraphics2DIOException {
        this.refPrefix = "";
        this.refPrefix = urlRoot + "/";
        this.setImageCacher(new ImageCacher.External(imageDir, "jpegImage", ".jpg"));
    }
    
    @Override
    public void encodeImage(final BufferedImage buf, final OutputStream os) throws IOException {
        final ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor("image/jpeg");
        final ImageWriterParams params = new ImageWriterParams();
        params.setJPEGQuality(1.0f, false);
        writer.writeImage(buf, os, params);
    }
    
    @Override
    public int getBufferedImageType() {
        return 1;
    }
    
    @Override
    public String getRefPrefix() {
        return this.refPrefix;
    }
}
