// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape.path;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class Arc extends PathOp
{
    private double rx;
    private double ry;
    private double xAxis;
    private int largeArc;
    private int sweep;
    private double x;
    private double y;
    
    public Arc() {
        super('A');
        this.rx = 0.0;
        this.ry = 0.0;
        this.xAxis = 0.0;
        this.largeArc = 0;
        this.sweep = 0;
        this.x = 0.0;
        this.y = 0.0;
    }
    
    public double rx() {
        return this.rx;
    }
    
    public double ry() {
        return this.ry;
    }
    
    public double xAxis() {
        return this.xAxis;
    }
    
    public int largeArc() {
        return this.largeArc;
    }
    
    public int sweep() {
        return this.sweep;
    }
    
    public double x() {
        return this.x;
    }
    
    public double y() {
        return this.y;
    }
    
    @Override
    public PathOp newInstance() {
        return new Arc();
    }
    
    @Override
    public Rectangle2D.Double bounds() {
        final double x0 = this.x - this.rx;
        final double y0 = this.y - this.ry;
        final double width = 2.0 * this.rx;
        final double height = 2.0 * this.ry;
        return new Rectangle2D.Double(x0, y0, width, height);
    }
    
    @Override
    public boolean load(final String expr) {
        this.label = expr.charAt(0);
        return true;
    }
    
    @Override
    public int expectedNumValues() {
        return 7;
    }
    
    @Override
    public void setValues(final List<Double> values, final Point2D[] current) {
        this.rx = values.get(0);
        this.ry = values.get(1);
        this.xAxis = values.get(2);
        this.largeArc = (int)(double)values.get(3);
        this.sweep = (int)(double)values.get(4);
        this.x = values.get(5);
        this.y = values.get(6);
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
        sb.append(this.label + ": rx=" + this.rx + ", ry=" + this.ry + ", xAxis=" + this.xAxis + ", largeArc=" + this.largeArc + ", sweep=" + this.sweep + " +, x=" + this.x + ", y=" + this.y);
        return sb.toString();
    }
    
    @Override
    public void apply(final GeneralPath path, final double x0, final double y0) {
    }
}
