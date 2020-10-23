// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.spi;

import java.io.IOException;
import java.io.OutputStream;
import java.awt.image.RenderedImage;

public interface ImageWriter
{
    void writeImage(final RenderedImage p0, final OutputStream p1) throws IOException;
    
    void writeImage(final RenderedImage p0, final OutputStream p1, final ImageWriterParams p2) throws IOException;
    
    String getMIMEType();
}
