// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.region.colour;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.functions.region.RegionFunction;
import game.types.board.SiteType;
import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;
import metadata.graphics.util.BoardGraphicsType;
import metadata.graphics.util.colour.Colour;

@Hide
public class RegionColour implements GraphicsItem
{
    private final String region;
    private final Integer[] sites;
    private final RegionFunction regionFunction;
    private final SiteType graphElementType;
    private final Colour colour;
    private final RoleType roleType;
    private final BoardGraphicsType boardGraphicsType;
    
    public RegionColour(@Opt final String region, @Opt final RoleType roleType, @Opt final SiteType graphElementType, @Opt @Or final Integer[] sites, @Opt @Or final Integer site, @Opt final RegionFunction regionFunction, @Opt final BoardGraphicsType boardGraphicsType, @Opt final Colour colour) {
        int numNonNull = 0;
        if (sites != null) {
            ++numNonNull;
        }
        if (site != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Only one of @Or should be different to null");
        }
        this.region = region;
        this.sites = (sites != null) ? sites : ((site != null) ? new Integer[] { site } : null);
        this.regionFunction = regionFunction;
        this.colour = colour;
        this.roleType = roleType;
        this.boardGraphicsType = boardGraphicsType;
        this.graphElementType = ((graphElementType == null) ? SiteType.Cell : graphElementType);
    }
    
    public SiteType graphElementType() {
        return this.graphElementType;
    }
    
    public String region() {
        return this.region;
    }
    
    public Integer[] sites() {
        return this.sites;
    }
    
    public Colour colour() {
        return this.colour;
    }
    
    public RegionFunction regionFunction() {
        return this.regionFunction;
    }
    
    public RoleType roleType() {
        return this.roleType;
    }
    
    public BoardGraphicsType boardGraphicsType() {
        return this.boardGraphicsType;
    }
}
