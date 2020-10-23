// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape;

import graphics.svg.SVGParser;
import graphics.svg.element.Element;

import java.awt.*;

public class Ellipse extends Shape
{
    private double cx;
    private double cy;
    private double rx;
    private double ry;
    
    public Ellipse() {
        super("ellipse");
        this.cx = 0.0;
        this.cy = 0.0;
        this.rx = 0.0;
        this.ry = 0.0;
    }
    
    public double cx() {
        return this.cx;
    }
    
    public double cy() {
        return this.cy;
    }
    
    public double rx() {
        return this.rx;
    }
    
    public double ry() {
        return this.ry;
    }
    
    @Override
    public Element newInstance() {
        return new Ellipse();
    }
    
    @Override
    public void setBounds() {
        final double x = this.cx - this.rx;
        final double y = this.cy - this.ry;
        final double width = 2.0 * this.rx;
        final double height = 2.0 * this.ry;
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
        if (expr.contains(" rx=")) {
            final Double result = SVGParser.extractDouble(expr, " rx=");
            if (result == null) {
                return false;
            }
            this.rx = result;
        }
        if (expr.contains(" ry=")) {
            final Double result = SVGParser.extractDouble(expr, " ry=");
            if (result == null) {
                return false;
            }
            this.ry = result;
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.label()).append(": fill=").append(this.style.fill()).append(", stroke=").append(this.style.stroke()).append(", strokeWidth=").append(this.style.strokeWidth());
        sb.append(" : cx=").append(this.cx).append(", cy=").append(this.cy).append(", rx=").append(this.rx).append(", ry=").append(this.ry);
        return sb.toString();
    }
    
    @Override
    public void render(final Graphics2D g2d, final double x0, final double y0, final Color footprintColour, final Color fillColour, final Color strokeColour) {
    }
    
    @Override
    public Element newOne() {
        return new Ellipse();
    }
}
