// 
// Decompiled by Procyon v0.5.36
// 

package game.util.graph;

import game.functions.dim.DimFunction;
import main.math.Polygon;
import util.BaseLudeme;

public class Poly extends BaseLudeme
{
    private final Polygon polygon;
    
    public Poly(final Float[][] pts) {
        this.polygon = new Polygon(pts);
    }
    
    public Poly(final DimFunction[][] pts) {
        final Float[][] floatPts = new Float[pts.length][];
        for (int i = 0; i < pts.length; ++i) {
            final DimFunction[] yPoint = pts[i];
            floatPts[i] = new Float[yPoint.length];
            for (int j = 0; j < pts[i].length; ++j) {
                floatPts[i][j] = Float.valueOf(pts[i][j].eval());
            }
        }
        this.polygon = new Polygon(floatPts);
    }
    
    public Polygon polygon() {
        return this.polygon;
    }
}
