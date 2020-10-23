// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape;

import graphics.svg.element.BaseElement;

import java.awt.geom.Rectangle2D;

public abstract class Shape extends BaseElement
{
    public Shape(final String label) {
        super(label);
    }
    
    @Override
    public Rectangle2D.Double bounds() {
        return this.bounds;
    }
    
    @Override
    public boolean load(final String expr) {
        return this.style.load(expr);
    }
    
    @Override
    public double strokeWidth() {
        return this.style.strokeWidth();
    }
}
