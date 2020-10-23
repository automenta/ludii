// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d.svg;

import org.jfree.graphics2d.Args;

import java.util.HashMap;
import java.util.Map;

public class StandardFontMapper implements FontMapper
{
    private final Map<String, String> alternates;
    
    public StandardFontMapper() {
        (this.alternates = new HashMap<>()).put("Dialog", "sans-serif");
        this.alternates.put("DialogInput", "monospace");
        this.alternates.put("SansSerif", "sans-serif");
        this.alternates.put("Serif", "serif");
        this.alternates.put("Monospaced", "monospace");
    }
    
    public String get(final String family) {
        Args.nullNotPermitted(family, "family");
        return this.alternates.get(family);
    }
    
    public void put(final String family, final String alternate) {
        Args.nullNotPermitted(family, "family");
        this.alternates.put(family, alternate);
    }
    
    @Override
    public String mapFont(final String family) {
        Args.nullNotPermitted(family, "family");
        final String alternate = this.alternates.get(family);
        if (alternate != null) {
            return alternate;
        }
        return family;
    }
}
