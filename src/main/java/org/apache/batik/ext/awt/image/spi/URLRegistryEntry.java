// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.spi;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.util.ParsedURL;

public interface URLRegistryEntry extends RegistryEntry
{
    boolean isCompatibleURL(final ParsedURL p0);
    
    Filter handleURL(final ParsedURL p0, final boolean p1);
}
