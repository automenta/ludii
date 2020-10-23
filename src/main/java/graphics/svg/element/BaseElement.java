// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element;

import java.awt.geom.Rectangle2D;

public abstract class BaseElement implements Element
{
    private final String label;
    private int filePos;
    protected final Style style;
    protected Rectangle2D.Double bounds;
    
    public BaseElement(final String label) {
        this.style = new Style();
        this.bounds = new Rectangle2D.Double();
        this.label = label;
    }
    
    @Override
    public String label() {
        return this.label;
    }
    
    @Override
    public int compare(final Element other) {
        return this.filePos - ((BaseElement)other).filePos;
    }
    
    public int filePos() {
        return this.filePos;
    }
    
    public void setFilePos(final int pos) {
        this.filePos = pos;
    }
    
    @Override
    public Style style() {
        return this.style;
    }
    
    public Rectangle2D.Double bounds() {
        return this.bounds;
    }
    
    public abstract void setBounds();
    
    public double strokeWidth() {
        return 0.0;
    }
}
