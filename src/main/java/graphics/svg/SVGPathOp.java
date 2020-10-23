// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SVGPathOp
{
    private final PathOpType type;
    private final boolean absolute;
    private final List<Point2D.Double> pts;
    private double xAxisRotation;
    private double largeArcSweep;
    private int sweepFlag;
    
    public SVGPathOp(final PathOpType type, final boolean absolute, final String[] subs) {
        this.pts = new ArrayList<>();
        this.xAxisRotation = 0.0;
        this.largeArcSweep = 0.0;
        this.sweepFlag = 0;
        this.type = type;
        this.absolute = absolute;
        this.parseNumbers(subs);
    }
    
    public PathOpType type() {
        return this.type;
    }
    
    public boolean absolute() {
        return this.absolute;
    }
    
    public List<Point2D.Double> pts() {
        return Collections.unmodifiableList(this.pts);
    }
    
    public double xAxisRotation() {
        return this.xAxisRotation;
    }
    
    public double largeArcSweep() {
        return this.largeArcSweep;
    }
    
    public int sweepFlag() {
        return this.sweepFlag;
    }
    
    boolean parseNumbers(final String[] subs) {
        if (subs == null) {
            return true;
        }
        if (subs.length % 2 != 0 && subs.length != 7) {
            System.out.println("** Odd number of substrings.");
            return false;
        }
        for (int s = 0; s < subs.length; s += 2) {
            double da = -1.0;
            double db = -1.0;
            final String str = subs[s].trim();
            try {
                da = Double.parseDouble(str);
            }
            catch (Exception e) {
                System.out.println("** '" + str + "' is not a double (x, " + s + ").");
                return false;
            }
            if (s < subs.length - 1) {
                final String str2 = subs[s + 1].trim();
                try {
                    db = Double.parseDouble(str2);
                }
                catch (Exception e2) {
                    System.out.println("** '" + str2 + "' is not a double (y, " + s + ").");
                    return false;
                }
            }
            this.pts.add(new Point2D.Double(da, db));
        }
        if (subs.length == 7) {
            this.xAxisRotation = this.pts.get(2).x;
            this.largeArcSweep = this.pts.get(2).y;
            this.sweepFlag = (int)this.pts.get(3).x;
            this.pts.remove(2);
        }
        return true;
    }
    
    public enum PathOpType
    {
        ArcTo, 
        MoveTo, 
        LineTo, 
        HLineTo, 
        VLineTo, 
        CurveTo, 
        QuadraticTo, 
        ShortCurveTo, 
        ShortQuadraticTo, 
        ClosePath
    }
}
