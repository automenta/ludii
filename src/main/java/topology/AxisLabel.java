// 
// Decompiled by Procyon v0.5.36
// 

package topology;

import java.awt.geom.Point2D;
import java.io.Serializable;

public final class AxisLabel implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String label;
    private final Point2D.Double posn;
    
    public AxisLabel(final String label, final double x, final double y) {
        this.label = "?";
        this.posn = new Point2D.Double(0.0, 0.0);
        this.label = label;
        this.posn.setLocation(x, y);
    }
    
    public String label() {
        return this.label;
    }
    
    public Point2D.Double posn() {
        return this.posn;
    }
}
