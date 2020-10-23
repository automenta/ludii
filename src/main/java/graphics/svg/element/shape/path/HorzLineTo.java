// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape.path;

import graphics.svg.SVGParser;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class HorzLineTo extends PathOp
{
    private double x;
    private double y;
    
    public HorzLineTo() {
        super('H');
        this.x = 0.0;
        this.y = 0.0;
    }
    
    public double x() {
        return this.x;
    }
    
    @Override
    public PathOp newInstance() {
        return new HorzLineTo();
    }
    
    @Override
    public Rectangle2D.Double bounds() {
        return new Rectangle2D.Double(this.x, this.y, 0.0, 0.0);
    }
    
    @Override
    public boolean load(final String expr) {
        this.label = expr.charAt(0);
        final int c = 1;
        final Double resultX = SVGParser.extractDoubleAt(expr, c);
        if (resultX == null) {
            System.out.println("* Failed to read X from " + expr + ".");
            return false;
        }
        this.x = resultX;
        return true;
    }
    
    @Override
    public int expectedNumValues() {
        return 1;
    }
    
    @Override
    public void setValues(final List<Double> values, final Point2D[] current) {
        this.x = values.get(0);
        this.y = current[0].getY();
        current[0] = new Point2D.Double(this.x, this.y);
        current[1] = null;
    }
    
    @Override
    public void getPoints(final List<Point2D> pts) {
        pts.add(new Point2D.Double(this.x, this.y));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.label).append(": x=").append(this.x).append(", (y)=").append(this.y);
        return sb.toString();
    }
    
    @Override
    public void apply(final GeneralPath path, final double x0, final double y0) {
        final Point2D pt = path.getCurrentPoint();
        if (this.absolute()) {
            path.moveTo(x0 + this.x, pt.getY());
        }
        else {
            path.moveTo(pt.getX() + this.x, pt.getY());
        }
    }
}
