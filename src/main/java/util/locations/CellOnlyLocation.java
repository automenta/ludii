// 
// Decompiled by Procyon v0.5.36
// 

package util.locations;

import game.types.board.SiteType;

public final class CellOnlyLocation extends Location
{
    private static final long serialVersionUID = 1L;
    private final int site;
    private int level;
    
    public CellOnlyLocation(final int site, final int level) {
        this.level = 0;
        this.site = site;
        this.level = level;
    }
    
    public CellOnlyLocation(final int site) {
        this.level = 0;
        this.site = site;
        this.level = 0;
    }
    
    private CellOnlyLocation(final CellOnlyLocation other) {
        this.level = 0;
        this.site = other.site;
        this.level = other.level;
    }
    
    @Override
    public Location copy() {
        return new CellOnlyLocation(this);
    }
    
    @Override
    public int site() {
        return this.site;
    }
    
    @Override
    public int level() {
        return this.level;
    }
    
    @Override
    public SiteType siteType() {
        return SiteType.Cell;
    }
    
    @Override
    public void decrementLevel() {
        --this.level;
    }
    
    @Override
    public void incrementLevel() {
        ++this.level;
    }
}
