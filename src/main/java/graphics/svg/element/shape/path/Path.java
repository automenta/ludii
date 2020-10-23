// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape.path;

import graphics.svg.SVGParser;
import graphics.svg.element.Element;
import graphics.svg.element.shape.Shape;
import main.math.MathRoutines;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Path extends Shape
{
    private final List<PathOp> ops;
    private final double pathLength = 0.0;
    
    public Path() {
        super("path");
        this.ops = new ArrayList<>();
    }
    
    public List<PathOp> ops() {
        return Collections.unmodifiableList(this.ops);
    }
    
    public double pathLength() {
        return 0.0;
    }
    
    @Override
    public Element newInstance() {
        return new Path();
    }
    
    @Override
    public void setBounds() {
        double x0 = 10000.0;
        double y0 = 10000.0;
        double x2 = -10000.0;
        double y2 = -10000.0;
        for (final PathOp op : this.ops) {
            final Rectangle2D.Double bound = (Rectangle2D.Double)op.bounds();
            if (bound == null) {
                continue;
            }
            if (bound.x < x0) {
                x0 = bound.x;
            }
            if (bound.y < y0) {
                y0 = bound.y;
            }
            final double x3 = bound.x + bound.width;
            final double y3 = bound.y + bound.height;
            if (x3 > x2) {
                x2 = x3;
            }
            if (y3 <= y2) {
                continue;
            }
            y2 = y3;
        }
        this.bounds.setRect(x0, y0, x2 - x0, y2 - y0);
    }
    
    @Override
    public boolean load(final String expr) {
        final boolean okay = true;
        if (!super.load(expr)) {
            return false;
        }
        this.ops.clear();
        if (expr.contains(" d=\"")) {
            final int pos = expr.indexOf(" d=\"");
            String str = SVGParser.extractStringAt(expr, pos + 3);
            if (str == null) {
                System.out.println("* Failed to extract string from: " + expr.substring(pos + 3));
                return false;
            }
            str = str.replaceAll("-", " -");
            PathOp prevOp = null;
            final Point2D.Double[] current = new Point2D.Double[2];
            while (!str.isEmpty()) {
                str = str.trim();
                PathOp op = prevOp;
                final char ch = str.charAt(0);
                if (Character.toLowerCase(ch) >= 'a' && Character.toLowerCase(ch) <= 'z') {
                    op = PathOpFactory.get().generate(ch);
                    if (op == null) {
                        System.out.println("* Couldn't find path op for leading char: " + str);
                        return false;
                    }
                    str = str.substring(1).trim();
                }
                else if (!SVGParser.isNumeric(ch)) {
                    System.out.println("* Non-numeric leading char: " + str);
                    return false;
                }
                final List<Double> values = new ArrayList<>();
                str = extractValues(str, op.expectedNumValues(), values);
                if (str == null) {
                    return true;
                }
                op.setValues(values, current);
                this.ops.add(op);
                prevOp = op;
            }
        }
        return true;
    }
    
    public static String extractValues(final String strIn, final int numExpected, final List<Double> values) {
        values.clear();
        String str = strIn;
        while (values.size() < numExpected) {
            str = str.trim();
            if (str.isEmpty()) {
                return null;
            }
            if (!SVGParser.isNumeric(str.charAt(0))) {
                return null;
            }
            if (str.charAt(0) == '0' && str.charAt(1) != '.') {
                values.add(0.0);
                str = str.substring(1);
            }
            else {
                String sub = "";
                int c;
                for (c = 0; c < str.length() && SVGParser.isNumeric(str.charAt(c)); ++c) {
                    sub += str.charAt(c);
                }
                Double result;
                try {
                    result = Double.parseDouble(sub);
                }
                catch (Exception e) {
                    System.out.println("* Error extracting Double from: " + sub);
                    return null;
                }
                values.add(result);
                str = str.substring(c);
            }
        }
        return str;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.label() + ": fill=" + this.style.fill() + ", stroke=" + this.style.stroke() + ", strokeWidth=" + this.style.strokeWidth());
        for (final PathOp op : this.ops) {
            sb.append("\n   " + op + (op.absolute() ? " *" : ""));
        }
        return sb.toString();
    }
    
    public static boolean isMoveTo(final PathOp op) {
        return Character.toLowerCase(op.label()) == 'm';
    }
    
    @Override
    public void render(final Graphics2D g2d, final double x0, final double y0, final Color footprintColour, final Color fillColour, final Color strokeColour) {
        if (footprintColour != null) {
            g2d.setPaint(footprintColour);
            int c = 0;
            while (c < this.ops.size()) {
                while (c < this.ops.size() && !this.ops.get(c).isMoveTo()) {
                    ++c;
                }
                if (c >= this.ops.size()) {
                    break;
                }
                final List<Point2D> run = this.nextRunFrom(c);
                if (MathRoutines.isClockwise(run)) {
                    final GeneralPath subPath = new GeneralPath(1);
                    do {
                        this.ops.get(c).apply(subPath, x0, y0);
                    } while (++c < this.ops.size() && !this.ops.get(c).isMoveTo());
                    subPath.closePath();
                    g2d.fill(subPath);
                }
                else {
                    ++c;
                    while (c < this.ops.size() && !this.ops.get(c).isMoveTo()) {
                        ++c;
                    }
                }
                if (c >= this.ops.size()) {
                    break;
                }
            }
        }
        if (fillColour != null) {
            final GeneralPath path = new GeneralPath(0);
            for (final PathOp op : this.ops) {
                op.apply(path, x0, y0);
            }
            g2d.setPaint(fillColour);
            g2d.fill(path);
        }
        if (strokeColour != null && this.style.strokeWidth() > 0.0) {
            final GeneralPath path = new GeneralPath(0);
            for (final PathOp op : this.ops) {
                op.apply(path, x0, y0);
            }
            final BasicStroke stroke = new BasicStroke((float)this.style.strokeWidth(), 1, 0);
            g2d.setPaint(strokeColour);
            g2d.setStroke(stroke);
            g2d.draw(path);
        }
    }
    
    List<Point2D> nextRunFrom(final int from) {
        final List<Point2D> pts = new ArrayList<>();
        int c = from;
        do {
            this.ops.get(c).getPoints(pts);
        } while (++c < this.ops.size() && !this.ops.get(c).isMoveTo());
        return pts;
    }
    
    @Override
    public Element newOne() {
        return new Path();
    }
}
