// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.flow;

import java.awt.Shape;

public class RegionInfo
{
    private Shape shape;
    private float verticalAlignment;
    
    public RegionInfo(final Shape s, final float verticalAlignment) {
        this.shape = s;
        this.verticalAlignment = verticalAlignment;
    }
    
    public Shape getShape() {
        return this.shape;
    }
    
    public void setShape(final Shape s) {
        this.shape = s;
    }
    
    public float getVerticalAlignment() {
        return this.verticalAlignment;
    }
    
    public void setVerticalAlignment(final float verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }
}
