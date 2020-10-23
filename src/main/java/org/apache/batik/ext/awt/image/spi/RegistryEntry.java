// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.spi;

import java.util.List;

public interface RegistryEntry
{
    List getStandardExtensions();
    
    List getMimeTypes();
    
    String getFormatName();
    
    float getPriority();
}
