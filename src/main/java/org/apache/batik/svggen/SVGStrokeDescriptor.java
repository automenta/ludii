// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SVGStrokeDescriptor implements SVGDescriptor, SVGSyntax
{
    private String strokeWidth;
    private String capStyle;
    private String joinStyle;
    private String miterLimit;
    private String dashArray;
    private String dashOffset;
    
    public SVGStrokeDescriptor(final String strokeWidth, final String capStyle, final String joinStyle, final String miterLimit, final String dashArray, final String dashOffset) {
        if (strokeWidth == null || capStyle == null || joinStyle == null || miterLimit == null || dashArray == null || dashOffset == null) {
            throw new SVGGraphics2DRuntimeException("none of the stroke description parameters should be null");
        }
        this.strokeWidth = strokeWidth;
        this.capStyle = capStyle;
        this.joinStyle = joinStyle;
        this.miterLimit = miterLimit;
        this.dashArray = dashArray;
        this.dashOffset = dashOffset;
    }
    
    String getStrokeWidth() {
        return this.strokeWidth;
    }
    
    String getCapStyle() {
        return this.capStyle;
    }
    
    String getJoinStyle() {
        return this.joinStyle;
    }
    
    String getMiterLimit() {
        return this.miterLimit;
    }
    
    String getDashArray() {
        return this.dashArray;
    }
    
    String getDashOffset() {
        return this.dashOffset;
    }
    
    @Override
    public Map getAttributeMap(Map attrMap) {
        if (attrMap == null) {
            attrMap = new HashMap<String, String>();
        }
        attrMap.put("stroke-width", this.strokeWidth);
        attrMap.put("stroke-linecap", this.capStyle);
        attrMap.put("stroke-linejoin", this.joinStyle);
        attrMap.put("stroke-miterlimit", this.miterLimit);
        attrMap.put("stroke-dasharray", this.dashArray);
        attrMap.put("stroke-dashoffset", this.dashOffset);
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
