// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape;

import graphics.svg.SVGParser;
import graphics.svg.element.Element;

import java.awt.*;

public class Rect extends Shape
{
    private double x;
    private double y;
    private double width;
    private double height;
    private double rx;
    private double ry;
    
    public Rect() {
        super("rect");
        this.x = 0.0;
        this.y = 0.0;
        this.width = 0.0;
        this.height = 0.0;
        this.rx = 0.0;
        this.ry = 0.0;
    }
    
    public double x() {
        return this.x;
    }
    
    public double y() {
        return this.y;
    }
    
    public double width() {
        return this.width;
    }
    
    public double height() {
        return this.height;
    }
    
    public double rx() {
        return this.rx;
    }
    
    public double ry() {
        return this.ry;
    }
    
    @Override
    public Element newInstance() {
        return new Rect();
    }
    
    @Override
    public void setBounds() {
        this.bounds.setRect(this.x, this.y, this.width, this.height);
    }
    
    @Override
    public boolean load(final String expr) {
        final boolean okay = true;
        if (!super.load(expr)) {
            return false;
        }
        if (expr.contains(" x=")) {
            final Double result = SVGParser.extractDouble(expr, " x=");
            if (result == null) {
                return false;
            }
            this.x = result;
        }
        if (expr.contains(" y=")) {
            final Double result = SVGParser.extractDouble(expr, " y=");
            if (result == null) {
                return false;
            }
            this.y = result;
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
        if (expr.contains(" width=")) {
            final Double result = SVGParser.extractDouble(expr, " width=");
            if (result == null) {
                return false;
            }
            this.width = result;
        }
        if (expr.contains(" height=")) {
            final Double result = SVGParser.extractDouble(expr, " height=");
            if (result == null) {
                return false;
            }
            this.height = result;
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.label() + ": fill=" + this.style.fill() + ", stroke=" + this.style.stroke() + ", strokeWidth=" + this.style.strokeWidth());
        sb.append(" : x=" + this.x + ", y=" + this.y + ", rx=" + this.rx + ", ry=" + this.ry + ", width=" + this.width + ", height=" + this.height);
        return sb.toString();
    }
    
    @Override
    public void render(final Graphics2D g2d, final double x0, final double y0, final Color footprintColour, final Color fillColour, final Color strokeColour) {
    }
    
    @Override
    public Element newOne() {
        return new Rect();
    }
}
