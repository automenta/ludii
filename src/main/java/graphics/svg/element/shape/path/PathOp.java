// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape.path;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public abstract class PathOp
{
    protected char label;
    
    public PathOp(final char label) {
        this.label = label;
    }
    
    public boolean absolute() {
        return this.label == Character.toUpperCase(this.label);
    }
    
    public Rectangle2D bounds() {
        return null;
    }
    
    public char label() {
        return this.label;
    }
    
    public void setLabel(final char ch) {
        this.label = ch;
    }
    
    public boolean matchesLabel(final char ch) {
        return Character.toUpperCase(ch) == Character.toUpperCase(this.label);
    }
    
    public boolean isMoveTo() {
        return Character.toLowerCase(this.label) == 'm';
    }
    
    public abstract int expectedNumValues();
    
    public abstract PathOp newInstance();
    
    public abstract boolean load(final String p0);
    
    public abstract void setValues(final List<Double> p0, final Point2D[] p1);
    
    public abstract void getPoints(final List<Point2D> p0);
    
    public abstract void apply(final GeneralPath p0, final double p1, final double p2);
}
