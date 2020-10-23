// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape.path;

import graphics.svg.SVGParser;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class CubicTo extends PathOp
{
    private double x1;
    private double y1;
    private double x2;
    private double y2;
    private double x;
    private double y;
    
    public CubicTo() {
        super('C');
        this.x1 = 0.0;
        this.y1 = 0.0;
        this.x2 = 0.0;
        this.y2 = 0.0;
        this.x = 0.0;
        this.y = 0.0;
    }
    
    public double x1() {
        return this.x1;
    }
    
    public double y1() {
        return this.y1;
    }
    
    public double x2() {
        return this.x2;
    }
    
    public double y2() {
        return this.y2;
    }
    
    public double x() {
        return this.x;
    }
    
    public double y() {
        return this.y;
    }
    
    @Override
    public PathOp newInstance() {
        return new CubicTo();
    }
    
    @Override
    public Rectangle2D.Double bounds() {
        final double x0 = Math.min(this.x1, Math.min(this.x2, this.x));
        final double y0 = Math.min(this.y1, Math.min(this.y2, this.y));
        final double width = Math.max(this.x1, Math.max(this.x2, this.x)) - x0;
        final double height = Math.max(this.y1, Math.max(this.y2, this.y)) - y0;
        return new Rectangle2D.Double(x0, y0, width, height);
    }
    
    @Override
    public boolean load(final String expr) {
        this.label = expr.charAt(0);
        int c = 1;
        final Double resultX1 = SVGParser.extractDoubleAt(expr, c);
        if (resultX1 == null) {
            System.out.println("* Failed to read X1 from " + expr + ".");
            return false;
        }
        this.x1 = resultX1;
        while (c < expr.length() && SVGParser.isNumeric(expr.charAt(c))) {
            ++c;
        }
        while (c < expr.length() && !SVGParser.isNumeric(expr.charAt(c))) {
            ++c;
        }
        final Double resultY1 = SVGParser.extractDoubleAt(expr, c);
        if (resultY1 == null) {
            System.out.println("* Failed to read Y1 from " + expr + ".");
            return false;
        }
        this.y1 = resultY1;
        while (c < expr.length() && SVGParser.isNumeric(expr.charAt(c))) {
            ++c;
        }
        while (c < expr.length() && !SVGParser.isNumeric(expr.charAt(c))) {
            ++c;
        }
        final Double resultX2 = SVGParser.extractDoubleAt(expr, c);
        if (resultX2 == null) {
            System.out.println("* Failed to read X2 from " + expr + ".");
            return false;
        }
        this.x2 = resultX2;
        while (c < expr.length() && SVGParser.isNumeric(expr.charAt(c))) {
            ++c;
        }
        while (c < expr.length() && !SVGParser.isNumeric(expr.charAt(c))) {
            ++c;
        }
        final Double resultY2 = SVGParser.extractDoubleAt(expr, c);
        if (resultY2 == null) {
            System.out.println("* Failed to read Y2 from " + expr + ".");
            return false;
        }
        this.y2 = resultY2;
        while (c < expr.length() && SVGParser.isNumeric(expr.charAt(c))) {
            ++c;
        }
        while (c < expr.length() && !SVGParser.isNumeric(expr.charAt(c))) {
            ++c;
        }
        final Double resultX3 = SVGParser.extractDoubleAt(expr, c);
        if (resultX3 == null) {
            System.out.println("* Failed to read X3 from " + expr + ".");
            return false;
        }
        this.x = resultX3;
        while (c < expr.length() && SVGParser.isNumeric(expr.charAt(c))) {
            ++c;
        }
        while (c < expr.length() && !SVGParser.isNumeric(expr.charAt(c))) {
            ++c;
        }
        final Double resultY3 = SVGParser.extractDoubleAt(expr, c);
        if (resultY3 == null) {
            System.out.println("* Failed to read Y3 from " + expr + ".");
            return false;
        }
        this.y = resultY3;
        return true;
    }
    
    @Override
    public int expectedNumValues() {
        return 6;
    }
    
    @Override
    public void setValues(final List<Double> values, final Point2D[] current) {
        this.x1 = values.get(0);
        this.y1 = values.get(1);
        this.x2 = values.get(2);
        this.y2 = values.get(3);
        this.x = values.get(4);
        this.y = values.get(5);
        current[0] = new Point2D.Double(this.x, this.y);
        current[1] = new Point2D.Double(this.x2, this.y2);
    }
    
    @Override
    public void getPoints(final List<Point2D> pts) {
        pts.add(new Point2D.Double(this.x1, this.y1));
        pts.add(new Point2D.Double(this.x2, this.y2));
        pts.add(new Point2D.Double(this.x, this.y));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.label + ": x1=" + this.x1 + ", y1=" + this.y1 + ", x2=" + this.x2 + ", y2=" + this.y2 + ", x=" + this.x + ", y=" + this.y);
        return sb.toString();
    }
    
    @Override
    public void apply(final GeneralPath path, final double x0, final double y0) {
        if (this.absolute()) {
            path.curveTo(x0 + this.x1, y0 + this.y1, x0 + this.x2, y0 + this.y2, x0 + this.x, y0 + this.y);
        }
        else {
            final Point2D pt = path.getCurrentPoint();
            path.curveTo(pt.getX() + this.x1, pt.getY() + this.y1, pt.getX() + this.x2, pt.getY() + this.y2, pt.getX() + this.x, pt.getY() + this.y);
        }
    }
}
