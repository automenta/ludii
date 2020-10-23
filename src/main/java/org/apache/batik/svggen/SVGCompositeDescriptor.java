// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;

public class SVGCompositeDescriptor implements SVGDescriptor, SVGSyntax
{
    private Element def;
    private String opacityValue;
    private String filterValue;
    
    public SVGCompositeDescriptor(final String opacityValue, final String filterValue) {
        this.opacityValue = opacityValue;
        this.filterValue = filterValue;
    }
    
    public SVGCompositeDescriptor(final String opacityValue, final String filterValue, final Element def) {
        this(opacityValue, filterValue);
        this.def = def;
    }
    
    public String getOpacityValue() {
        return this.opacityValue;
    }
    
    public String getFilterValue() {
        return this.filterValue;
    }
    
    public Element getDef() {
        return this.def;
    }
    
    @Override
    public Map getAttributeMap(Map attrMap) {
        if (attrMap == null) {
            attrMap = new HashMap<String, String>();
        }
        attrMap.put("opacity", this.opacityValue);
        attrMap.put("filter", this.filterValue);
        return attrMap;
    }
    
    @Override
    public List getDefinitionSet(List defSet) {
        if (defSet == null) {
            defSet = new LinkedList<Element>();
        }
        if (this.def != null) {
            defSet.add(this.def);
        }
        return defSet;
    }
}
