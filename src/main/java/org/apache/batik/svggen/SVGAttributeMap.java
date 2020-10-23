// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.HashMap;
import java.util.Map;

public class SVGAttributeMap
{
    private static Map attrMap;
    
    public static SVGAttribute get(final String attrName) {
        return SVGAttributeMap.attrMap.get(attrName);
    }
    
    static {
        SVGAttributeMap.attrMap = new HashMap();
    }
}
