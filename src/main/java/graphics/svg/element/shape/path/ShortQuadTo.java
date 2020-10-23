// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape.path;

import graphics.svg.SVGParser;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class ShortQuadTo extends PathOp
{
    private double x;
    private double y;
    private double x1;
    private double y1;
    
    public ShortQuadTo() {
        super('T');
        this.x = 0.0;
        this.y = 0.0;
        this.x1 = 0.0;
        this.y1 = 0.0;
    }
    
    public double x() {
        return this.x;
    }
    
    public double y() {
        return this.y;
    }
    
    @Override
    public PathOp newInstance() {
        return new ShortQuadTo();
    }
    
    @Override
    public Rectangle2D.Double bounds() {
        final double x0 = Math.min(this.x1, this.x);
        final double y0 = Math.min(this.y1, this.y);
        final double width = Math.max(this.x1, this.x) - x0;
        final double height = Math.max(this.y1, this.y) - y0;
        return new Rectangle2D.Double(x0, y0, width, height);
    }
    
    @Override
    public boolean load(final String expr) {
        this.label = expr.charAt(0);
        int c = 1;
        final Double resultX2 = SVGParser.extractDoubleAt(expr, c);
        if (resultX2 == null) {
            System.out.println("* Failed to read X2 from " + expr + ".");
            return false;
        }
        this.x = resultX2;
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
        this.y = resultY2;
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
        final double currentX = current[0].getX();
        final double currentY = current[0].getY();
        final double oldX = (current[1] == null) ? currentX : current[1].getX();
        final double oldY = (current[1] == null) ? currentY : current[1].getY();
        this.x1 = 2.0 * currentX - oldX;
        this.y1 = 2.0 * currentY - oldY;
        current[0] = new Point2D.Double(this.x, this.y);
        current[1] = new Point2D.Double(this.x1, this.y1);
    }
    
    @Override
    public void getPoints(final List<Point2D> pts) {
        pts.add(new Point2D.Double(this.x1, this.y1));
        pts.add(new Point2D.Double(this.x, this.y));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.label).append(": (x1=)=").append(this.x1).append(", (y1)=").append(this.y1).append(", x=").append(this.x).append(", y=").append(this.y);
        return sb.toString();
    }
    
    @Override
    public void apply(final GeneralPath path, final double x0, final double y0) {
        if (this.absolute()) {
            path.quadTo(x0 + this.x1, y0 + this.y1, x0 + this.x, y0 + this.y);
        }
        else {
            final Point2D pt = path.getCurrentPoint();
            path.quadTo(pt.getX() + this.x1, pt.getY() + this.y1, pt.getX() + this.x, pt.getY() + this.y);
        }
    }
}
