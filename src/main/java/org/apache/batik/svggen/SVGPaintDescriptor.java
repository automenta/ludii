// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;

public class SVGPaintDescriptor implements SVGDescriptor, SVGSyntax
{
    private Element def;
    private String paintValue;
    private String opacityValue;
    
    public SVGPaintDescriptor(final String paintValue, final String opacityValue) {
        this.paintValue = paintValue;
        this.opacityValue = opacityValue;
    }
    
    public SVGPaintDescriptor(final String paintValue, final String opacityValue, final Element def) {
        this(paintValue, opacityValue);
        this.def = def;
    }
    
    public String getPaintValue() {
        return this.paintValue;
    }
    
    public String getOpacityValue() {
        return this.opacityValue;
    }
    
    public Element getDef() {
        return this.def;
    }
    
    @Override
    public Map getAttributeMap(Map attrMap) {
        if (attrMap == null) {
            attrMap = new HashMap<String, String>();
        }
        attrMap.put("fill", this.paintValue);
        attrMap.put("stroke", this.paintValue);
        attrMap.put("fill-opacity", this.opacityValue);
        attrMap.put("stroke-opacity", this.opacityValue);
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
