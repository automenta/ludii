// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;

public class SVGFontDescriptor implements SVGDescriptor, SVGSyntax
{
    private Element def;
    private String fontSize;
    private String fontWeight;
    private String fontStyle;
    private String fontFamily;
    
    public SVGFontDescriptor(final String fontSize, final String fontWeight, final String fontStyle, final String fontFamily, final Element def) {
        if (fontSize == null || fontWeight == null || fontStyle == null || fontFamily == null) {
            throw new SVGGraphics2DRuntimeException("none of the font description parameters should be null");
        }
        this.fontSize = fontSize;
        this.fontWeight = fontWeight;
        this.fontStyle = fontStyle;
        this.fontFamily = fontFamily;
        this.def = def;
    }
    
    @Override
    public Map getAttributeMap(Map attrMap) {
        if (attrMap == null) {
            attrMap = new HashMap<String, String>();
        }
        attrMap.put("font-size", this.fontSize);
        attrMap.put("font-weight", this.fontWeight);
        attrMap.put("font-style", this.fontStyle);
        attrMap.put("font-family", this.fontFamily);
        return attrMap;
    }
    
    public Element getDef() {
        return this.def;
    }
    
    @Override
    public List getDefinitionSet(List defSet) {
        if (defSet == null) {
            defSet = new LinkedList<Element>();
        }
        if (this.def != null && !defSet.contains(this.def)) {
            defSet.add(this.def);
        }
        return defSet;
    }
}
