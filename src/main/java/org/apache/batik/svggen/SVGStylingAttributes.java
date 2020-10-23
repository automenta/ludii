// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.util.HashSet;
import java.util.Set;

public class SVGStylingAttributes implements SVGSyntax
{
    static Set attrSet;
    public static final Set set;
    
    static {
        (SVGStylingAttributes.attrSet = new HashSet()).add("clip-path");
        SVGStylingAttributes.attrSet.add("color-interpolation");
        SVGStylingAttributes.attrSet.add("color-rendering");
        SVGStylingAttributes.attrSet.add("enable-background");
        SVGStylingAttributes.attrSet.add("fill");
        SVGStylingAttributes.attrSet.add("fill-opacity");
        SVGStylingAttributes.attrSet.add("fill-rule");
        SVGStylingAttributes.attrSet.add("filter");
        SVGStylingAttributes.attrSet.add("flood-color");
        SVGStylingAttributes.attrSet.add("flood-opacity");
        SVGStylingAttributes.attrSet.add("font-family");
        SVGStylingAttributes.attrSet.add("font-size");
        SVGStylingAttributes.attrSet.add("font-weight");
        SVGStylingAttributes.attrSet.add("font-style");
        SVGStylingAttributes.attrSet.add("image-rendering");
        SVGStylingAttributes.attrSet.add("mask");
        SVGStylingAttributes.attrSet.add("opacity");
        SVGStylingAttributes.attrSet.add("shape-rendering");
        SVGStylingAttributes.attrSet.add("stop-color");
        SVGStylingAttributes.attrSet.add("stop-opacity");
        SVGStylingAttributes.attrSet.add("stroke");
        SVGStylingAttributes.attrSet.add("stroke-opacity");
        SVGStylingAttributes.attrSet.add("stroke-dasharray");
        SVGStylingAttributes.attrSet.add("stroke-dashoffset");
        SVGStylingAttributes.attrSet.add("stroke-linecap");
        SVGStylingAttributes.attrSet.add("stroke-linejoin");
        SVGStylingAttributes.attrSet.add("stroke-miterlimit");
        SVGStylingAttributes.attrSet.add("stroke-width");
        SVGStylingAttributes.attrSet.add("text-rendering");
        set = SVGStylingAttributes.attrSet;
    }
}
