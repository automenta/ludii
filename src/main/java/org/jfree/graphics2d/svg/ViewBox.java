// 
// Decompiled by Procyon v0.5.36
// 

package org.jfree.graphics2d.svg;

public class ViewBox
{
    private final int minX;
    private final int minY;
    private final int width;
    private final int height;
    
    public ViewBox(final int minX, final int minY, final int width, final int height) {
        this.minX = minX;
        this.minY = minY;
        this.width = width;
        this.height = height;
    }
    
    public String valueStr() {
        return this.minX + " " + this.minY + " " + this.width + " " + this.height;
    }
}
