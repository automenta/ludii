// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d.svg;

public enum PreserveAspectRatio
{
    NONE("none"), 
    XMIN_YMIN("xMinYMin"), 
    XMIN_YMID("xMinYMid"), 
    XMIN_YMAX("xMinYMax"), 
    XMID_YMIN("xMidYMin"), 
    XMID_YMID("xMidYMid"), 
    XMID_YMAX("xMidYMax"), 
    XMAX_YMIN("xMaxYMin"), 
    XMAX_YMID("xMaxYMid"), 
    XMAX_YMAX("xMaxYMax");
    
    private final String label;
    
    PreserveAspectRatio(final String label) {
        this.label = label;
    }
    
    @Override
    public String toString() {
        return this.label;
    }
}
