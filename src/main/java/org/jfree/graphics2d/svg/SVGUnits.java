// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d.svg;

public enum SVGUnits
{
    EM("em"), 
    EX("ex"), 
    PX("px"), 
    PT("pt"), 
    PC("pc"), 
    CM("cm"), 
    MM("mm"), 
    IN("in");
    
    private final String label;
    
    SVGUnits(final String label) {
        this.label = label;
    }
    
    @Override
    public String toString() {
        return this.label;
    }
}
