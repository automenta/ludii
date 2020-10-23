// 
// Decompiled by Procyon v0.5.36
// 

package graphics.svg.element.shape.path;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.List;

public class Close extends PathOp
{
    public Close() {
        super('Z');
    }
    
    @Override
    public PathOp newInstance() {
        return new Close();
    }
    
    @Override
    public boolean load(final String expr) {
        this.label = expr.charAt(0);
        return true;
    }
    
    @Override
    public int expectedNumValues() {
        return 0;
    }
    
    @Override
    public void setValues(final List<Double> values, final Point2D[] current) {
        current[1] = (current[0] = null);
    }
    
    @Override
    public void getPoints(final List<Point2D> pts) {
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.label);
        return sb.toString();
    }
    
    @Override
    public void apply(final GeneralPath path, final double x0, final double y0) {
        path.closePath();
    }
}
