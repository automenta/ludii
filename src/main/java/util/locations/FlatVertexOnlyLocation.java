// 
// Decompiled by Procyon v0.5.36
// 

package util.locations;

import game.types.board.SiteType;

public final class FlatVertexOnlyLocation extends Location
{
    private static final long serialVersionUID = 1L;
    private final int site;
    
    public FlatVertexOnlyLocation(final int site) {
        this.site = site;
    }
    
    private FlatVertexOnlyLocation(final FlatVertexOnlyLocation other) {
        this.site = other.site;
    }
    
    @Override
    public Location copy() {
        return new FlatVertexOnlyLocation(this);
    }
    
    @Override
    public int site() {
        return this.site;
    }
    
    @Override
    public int level() {
        return 0;
    }
    
    @Override
    public SiteType siteType() {
        return SiteType.Vertex;
    }
    
    @Override
    public void decrementLevel() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void incrementLevel() {
        throw new UnsupportedOperationException();
    }
}
