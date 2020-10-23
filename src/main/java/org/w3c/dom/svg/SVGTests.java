// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

public interface SVGTests
{
    SVGStringList getRequiredFeatures();
    
    SVGStringList getRequiredExtensions();
    
    SVGStringList getSystemLanguage();
    
    boolean hasExtension(final String p0);
}
