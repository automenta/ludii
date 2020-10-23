// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import org.w3c.dom.Element;

public class SVGFilterDescriptor
{
    private Element def;
    private String filterValue;
    
    public SVGFilterDescriptor(final String filterValue) {
        this.filterValue = filterValue;
    }
    
    public SVGFilterDescriptor(final String filterValue, final Element def) {
        this(filterValue);
        this.def = def;
    }
    
    public String getFilterValue() {
        return this.filterValue;
    }
    
    public Element getDef() {
        return this.def;
    }
}
