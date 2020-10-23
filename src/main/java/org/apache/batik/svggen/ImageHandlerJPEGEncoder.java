// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.Dimension;
import org.apache.batik.ext.awt.image.spi.ImageWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.awt.image.RenderedImage;
import org.apache.batik.ext.awt.image.spi.ImageWriterParams;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import java.io.FileOutputStream;
import java.io.File;
import java.awt.image.BufferedImage;

public class ImageHandlerJPEGEncoder extends AbstractImageHandlerEncoder
{
    public ImageHandlerJPEGEncoder(final String imageDir, final String urlRoot) throws SVGGraphics2DIOException {
        super(imageDir, urlRoot);
    }
    
    @Override
    public final String getSuffix() {
        return ".jpg";
    }
    
    @Override
    public final String getPrefix() {
        return "jpegImage";
    }
    
    @Override
    public void encodeImage(final BufferedImage buf, final File imageFile) throws SVGGraphics2DIOException {
        try {
            final OutputStream os = new FileOutputStream(imageFile);
            try {
                final ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor("image/jpeg");
                final ImageWriterParams params = new ImageWriterParams();
                params.setJPEGQuality(1.0f, false);
                writer.writeImage(buf, os, params);
            }
            finally {
                os.close();
            }
        }
        catch (IOException e) {
            throw new SVGGraphics2DIOException("could not write image File " + imageFile.getName());
        }
    }
    
    @Override
    public BufferedImage buildBufferedImage(final Dimension size) {
        return new BufferedImage(size.width, size.height, 1);
    }
}
