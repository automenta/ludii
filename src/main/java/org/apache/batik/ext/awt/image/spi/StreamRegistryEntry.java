// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.spi;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.util.ParsedURL;
import java.io.StreamCorruptedException;
import java.io.InputStream;

public interface StreamRegistryEntry extends RegistryEntry
{
    int getReadlimit();
    
    boolean isCompatibleStream(final InputStream p0) throws StreamCorruptedException;
    
    Filter handleStream(final InputStream p0, final ParsedURL p1, final boolean p2);
}
