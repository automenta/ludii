// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.spi;

import java.util.Iterator;
import org.apache.batik.util.Service;
import java.util.HashMap;
import java.util.Map;

public final class ImageWriterRegistry
{
    private static ImageWriterRegistry instance;
    private final Map imageWriterMap;
    
    private ImageWriterRegistry() {
        this.imageWriterMap = new HashMap();
        this.setup();
    }
    
    public static ImageWriterRegistry getInstance() {
        synchronized (ImageWriterRegistry.class) {
            if (ImageWriterRegistry.instance == null) {
                ImageWriterRegistry.instance = new ImageWriterRegistry();
            }
            return ImageWriterRegistry.instance;
        }
    }
    
    private void setup() {
        final Iterator iter = Service.providers(ImageWriter.class);
        while (iter.hasNext()) {
            final ImageWriter writer = iter.next();
            this.register(writer);
        }
    }
    
    public void register(final ImageWriter writer) {
        this.imageWriterMap.put(writer.getMIMEType(), writer);
    }
    
    public ImageWriter getWriterFor(final String mime) {
        return this.imageWriterMap.get(mime);
    }
}
