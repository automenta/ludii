// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.HashSet;
import java.util.Set;

public class SVGAttribute
{
    private String name;
    private Set applicabilitySet;
    private boolean isSetInclusive;
    
    public SVGAttribute(Set applicabilitySet, final boolean isSetInclusive) {
        if (applicabilitySet == null) {
            applicabilitySet = new HashSet();
        }
        this.applicabilitySet = applicabilitySet;
        this.isSetInclusive = isSetInclusive;
    }
    
    public boolean appliesTo(final String tag) {
        final boolean tagInMap = this.applicabilitySet.contains(tag);
        if (this.isSetInclusive) {
            return tagInMap;
        }
        return !tagInMap;
    }
}
