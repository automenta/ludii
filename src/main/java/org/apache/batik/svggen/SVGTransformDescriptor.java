// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SVGTransformDescriptor implements SVGDescriptor, SVGSyntax
{
    private String transform;
    
    public SVGTransformDescriptor(final String transform) {
        this.transform = transform;
    }
    
    @Override
    public Map getAttributeMap(Map attrMap) {
        if (attrMap == null) {
            attrMap = new HashMap<String, String>();
        }
        attrMap.put("transform", this.transform);
        return attrMap;
    }
    
    @Override
    public List getDefinitionSet(List defSet) {
        if (defSet == null) {
            defSet = new LinkedList();
        }
        return defSet;
    }
}
