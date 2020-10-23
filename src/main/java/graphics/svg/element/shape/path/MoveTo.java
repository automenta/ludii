// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape.path;

import graphics.svg.SVGParser;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class MoveTo extends PathOp
{
    private double x;
    private double y;
    
    public MoveTo() {
        super('M');
        this.x = 0.0;
        this.y = 0.0;
    }
    
    public double x() {
        return this.x;
    }
    
    public double y() {
        return this.y;
    }
    
    @Override
    public PathOp newInstance() {
        return new MoveTo();
    }
    
    @Override
    public Rectangle2D.Double bounds() {
        return new Rectangle2D.Double(this.x, this.y, 0.0, 0.0);
    }
    
    @Override
    public boolean load(final String expr) {
        this.label = expr.charAt(0);
        int c = 1;
        final Double resultX = SVGParser.extractDoubleAt(expr, c);
        if (resultX == null) {
            System.out.println("* Failed to read X from " + expr + ".");
            return false;
        }
        this.x = resultX;
        while (c < expr.length() && SVGParser.isNumeric(expr.charAt(c))) {
            ++c;
        }
        while (c < expr.length() && !SVGParser.isNumeric(expr.charAt(c))) {
            ++c;
        }
        final Double resultY = SVGParser.extractDoubleAt(expr, c);
        if (resultY == null) {
            System.out.println("* Failed to read Y from " + expr + ".");
            return false;
        }
        this.y = resultY;
        return true;
    }
    
    @Override
    public int expectedNumValues() {
        return 2;
    }
    
    @Override
    public void setValues(final List<Double> values, final Point2D[] current) {
        this.x = values.get(0);
        this.y = values.get(1);
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
        sb.append(this.label + ": x=" + this.x + ", y=" + this.y);
        return sb.toString();
    }
    
    @Override
    public void apply(final GeneralPath path, final double x0, final double y0) {
        if (this.absolute()) {
            path.moveTo(x0 + this.x, y0 + this.y);
        }
        else {
            final Point2D pt = path.getCurrentPoint();
            path.moveTo(pt.getX() + this.x, pt.getY() + this.y);
        }
    }
}
