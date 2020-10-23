// 
// Decompiled by Procyon v0.5.36
// 

package util.locations;

import game.types.board.SiteType;

import java.io.Serializable;

public abstract class Location implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public abstract Location copy();
    
    public abstract int site();
    
    public abstract int level();
    
    public abstract SiteType siteType();
    
    public abstract void decrementLevel();
    
    public abstract void incrementLevel();
    
    public boolean equalsLoc(final Location other) {
        return this.site() == other.site() && this.level() == other.level() && this.siteType() == other.siteType();
    }
    
    @Override
    public String toString() {
        return "Location(site:" + this.site() + " level: " + this.level() + " siteType: " + this.siteType() + ")";
    }
}
