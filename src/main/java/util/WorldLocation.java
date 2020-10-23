// 
// Decompiled by Procyon v0.5.36
// 

package util;

import util.locations.Location;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class WorldLocation implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Location location;
    private final Point2D position;
    
    public WorldLocation(final Location location, final Point2D position) {
        this.location = location;
        this.position = position;
    }
    
    public Location location() {
        return this.location;
    }
    
    public Point2D position() {
        return this.position;
    }
}
