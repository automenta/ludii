// 
// Decompiled by Procyon v0.5.36
// 

package util.locations;

import game.types.board.SiteType;

public final class FullLocation extends Location
{
    private static final long serialVersionUID = 1L;
    private final int site;
    private int level;
    private final SiteType siteType;
    
    public FullLocation(final int site, final int level, final SiteType siteType) {
        this.level = 0;
        this.site = site;
        this.level = level;
        this.siteType = siteType;
    }
    
    public FullLocation(final int site, final int level) {
        this.level = 0;
        this.site = site;
        this.level = level;
        this.siteType = SiteType.Cell;
    }
    
    public FullLocation(final int site) {
        this.level = 0;
        this.site = site;
        this.level = 0;
        this.siteType = SiteType.Cell;
    }
    
    public FullLocation(final int site, final SiteType siteType) {
        this.level = 0;
        this.site = site;
        this.level = 0;
        this.siteType = siteType;
    }
    
    private FullLocation(final FullLocation other) {
        this.level = 0;
        this.site = other.site;
        this.level = other.level;
        this.siteType = other.siteType;
    }
    
    @Override
    public Location copy() {
        return new FullLocation(this);
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
        return this.siteType;
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
