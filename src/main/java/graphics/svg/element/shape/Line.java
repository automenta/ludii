// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape;

import graphics.svg.SVGParser;
import graphics.svg.element.Element;

import java.awt.*;

public class Line extends Shape
{
    private double x1;
    private double y1;
    private double x2;
    private double y2;
    
    public Line() {
        super("line");
        this.x1 = 0.0;
        this.y1 = 0.0;
        this.x2 = 0.0;
        this.y2 = 0.0;
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
    
    @Override
    public Element newInstance() {
        return new Line();
    }
    
    @Override
    public void setBounds() {
        final double x = Math.min(this.x1, this.x2);
        final double y = Math.min(this.y1, this.y2);
        final double width = Math.max(this.x1, this.x2) - x;
        final double height = Math.max(this.y1, this.y2) - y;
        this.bounds.setRect(x, y, width, height);
    }
    
    @Override
    public boolean load(final String expr) {
        final boolean okay = true;
        if (!super.load(expr)) {
            return false;
        }
        if (expr.contains(" x1=")) {
            final Double result = SVGParser.extractDouble(expr, " x1=");
            if (result == null) {
                return false;
            }
            this.x1 = result;
        }
        if (expr.contains(" y1=")) {
            final Double result = SVGParser.extractDouble(expr, " y1=");
            if (result == null) {
                return false;
            }
            this.y1 = result;
        }
        if (expr.contains(" x2=")) {
            final Double result = SVGParser.extractDouble(expr, " x2=");
            if (result == null) {
                return false;
            }
            this.x2 = result;
        }
        if (expr.contains(" y2=")) {
            final Double result = SVGParser.extractDouble(expr, " y2=");
            if (result == null) {
                return false;
            }
            this.y2 = result;
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.label()).append(": fill=").append(this.style.fill()).append(", stroke=").append(this.style.stroke()).append(", strokeWidth=").append(this.style.strokeWidth());
        sb.append(" : x1=").append(this.x1).append(", y1=").append(this.y1).append(", x2=").append(this.x2).append(", y2=").append(this.y2);
        return sb.toString();
    }
    
    @Override
    public void render(final Graphics2D g2d, final double x0, final double y0, final Color footprintColour, final Color fillColour, final Color strokeColour) {
    }
    
    @Override
    public Element newOne() {
        return new Line();
    }
}
