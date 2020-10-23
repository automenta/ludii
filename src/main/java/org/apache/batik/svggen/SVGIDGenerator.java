// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.HashMap;
import java.util.Map;

public class SVGIDGenerator
{
    private Map prefixMap;
    
    public SVGIDGenerator() {
        this.prefixMap = new HashMap();
    }
    
    public String generateID(final String prefix) {
        Integer maxId = this.prefixMap.get(prefix);
        if (maxId == null) {
            maxId = 0;
            this.prefixMap.put(prefix, maxId);
        }
        ++maxId;
        this.prefixMap.put(prefix, maxId);
        return prefix + maxId;
    }
}
