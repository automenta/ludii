// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape;

import graphics.svg.element.Element;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Polyline extends Shape
{
    protected final List<Point2D.Double> points;
    
    public Polyline() {
        super("polyline");
        this.points = new ArrayList<>();
    }
    
    public Polyline(final String label) {
        super(label);
        this.points = new ArrayList<>();
    }
    
    public List<Point2D.Double> points() {
        return Collections.unmodifiableList(this.points);
    }
    
    @Override
    public Element newInstance() {
        return new Polyline();
    }
    
    @Override
    public void setBounds() {
        double x0 = 10000.0;
        double y0 = 10000.0;
        double x2 = -10000.0;
        double y2 = -10000.0;
        for (final Point2D.Double pt : this.points) {
            if (pt.x < x0) {
                x0 = pt.x;
            }
            if (pt.y < y0) {
                y0 = pt.y;
            }
            if (pt.x > x2) {
                x2 = pt.x;
            }
            if (pt.y > x2) {
                y2 = pt.y;
            }
        }
        this.bounds.setRect(x0, y0, x2 - x0, y2 - y0);
    }
    
    @Override
    public boolean load(final String expr) {
        final boolean okay = true;
        if (!super.load(expr)) {
            return false;
        }
        final int pos = expr.indexOf(" points=\"");
        int to;
        for (to = pos + 9; to < expr.length() && expr.charAt(to) != '\"'; ++to) {}
        if (to >= expr.length()) {
            System.out.println("* Failed to close points list in Polyline.");
            return false;
        }
        final String[] subs = expr.substring(pos + 9, to).split(" ");
        for (int n = 0; n < subs.length - 1; n += 2) {
            final double x = Double.parseDouble(subs[n]);
            final double y = Double.parseDouble(subs[n + 1]);
            this.points.add(new Point2D.Double(x, y));
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.label()).append(": fill=").append(this.style.fill()).append(", stroke=").append(this.style.stroke()).append(", strokeWidth=").append(this.style.strokeWidth());
        sb.append(" :");
        for (final Point2D.Double pt : this.points) {
            sb.append(" (").append(pt.x).append(",").append(pt.y).append(")");
        }
        return sb.toString();
    }
    
    @Override
    public void render(final Graphics2D g2d, final double x0, final double y0, final Color footprintColour, final Color fillColour, final Color strokeColour) {
    }
    
    @Override
    public Element newOne() {
        return new Polyline();
    }
}
