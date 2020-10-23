// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape;

import graphics.svg.SVGParser;
import graphics.svg.element.Element;

import java.awt.*;

public class Circle extends Shape
{
    private double cx;
    private double cy;
    private double r;
    
    public Circle() {
        super("circle");
        this.cx = 0.0;
        this.cy = 0.0;
        this.r = 0.0;
    }
    
    public double cx() {
        return this.cx;
    }
    
    public double cy() {
        return this.cy;
    }
    
    public double r() {
        return this.r;
    }
    
    @Override
    public Element newInstance() {
        return new Circle();
    }
    
    @Override
    public void setBounds() {
        final double x = this.cx - this.r;
        final double y = this.cy - this.r;
        final double width = 2.0 * this.r;
        final double height = 2.0 * this.r;
        this.bounds.setRect(x, y, width, height);
    }
    
    @Override
    public boolean load(final String expr) {
        final boolean okay = true;
        if (!super.load(expr)) {
            return false;
        }
        if (expr.contains(" cx=")) {
            final Double result = SVGParser.extractDouble(expr, " cx=");
            if (result == null) {
                return false;
            }
            this.cx = result;
        }
        if (expr.contains(" cy=")) {
            final Double result = SVGParser.extractDouble(expr, " cy=");
            if (result == null) {
                return false;
            }
            this.cy = result;
        }
        if (expr.contains(" r=")) {
            final Double result = SVGParser.extractDouble(expr, " r=");
            if (result == null) {
                return false;
            }
            this.r = result;
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.label()).append(": fill=").append(this.style.fill()).append(", stroke=").append(this.style.stroke()).append(", strokeWidth=").append(this.style.strokeWidth());
        sb.append(" : cx=").append(this.cx).append(", cy=").append(this.cy).append(", r=").append(this.r);
        return sb.toString();
    }
    
    @Override
    public void render(final Graphics2D g2d, final double x0, final double y0, final Color footprintColour, final Color fillColour, final Color strokeColour) {
    }
    
    @Override
    public Element newOne() {
        return new Circle();
    }
}
