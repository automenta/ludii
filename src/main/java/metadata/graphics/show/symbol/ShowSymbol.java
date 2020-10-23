// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.show.symbol;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.functions.region.RegionFunction;
import game.types.board.SiteType;
import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;
import metadata.graphics.util.BoardGraphicsType;
import metadata.graphics.util.colour.Colour;

@Hide
public class ShowSymbol implements GraphicsItem
{
    private final String imageName;
    private final String region;
    private final SiteType graphElementType;
    private final Integer[] sites;
    private final RegionFunction regionFunction;
    private final RoleType roleType;
    private final float scale;
    private final Colour fillColour;
    private final Colour edgeColour;
    private final BoardGraphicsType boardGraphicsType;
    private final int rotation;
    
    public ShowSymbol(final String imageName, @Opt final String region, @Opt final RoleType roleType, @Opt final SiteType graphElementType, @Opt @Or final Integer[] sites, @Opt @Or final Integer site, @Opt final RegionFunction regionFunction, @Opt final BoardGraphicsType boardGraphicsType, @Opt @Name final Colour fillColour, @Opt @Name final Colour edgeColour, @Opt @Name final Float scale, @Opt @Name final Integer rotation) {
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
        this.imageName = imageName;
        this.region = region;
        this.graphElementType = ((graphElementType == null) ? SiteType.Cell : graphElementType);
        this.sites = (sites != null) ? sites : ((site != null) ? new Integer[] { site } : null);
        this.regionFunction = regionFunction;
        this.boardGraphicsType = boardGraphicsType;
        this.fillColour = fillColour;
        this.edgeColour = edgeColour;
        this.scale = ((scale == null) ? 1.0f : scale);
        this.rotation = ((rotation == null) ? 0 : rotation);
        this.roleType = roleType;
    }
    
    public SiteType graphElementType() {
        return this.graphElementType;
    }
    
    public Integer[] sites() {
        return this.sites;
    }
    
    public String imageName() {
        return this.imageName;
    }
    
    public float scale() {
        return this.scale;
    }
    
    public Colour fillColour() {
        return this.fillColour;
    }
    
    public Colour edgeColour() {
        return this.edgeColour;
    }
    
    public BoardGraphicsType boardGraphicsType() {
        return this.boardGraphicsType;
    }
    
    public String region() {
        return this.region;
    }
    
    public RegionFunction regionFunction() {
        return this.regionFunction;
    }
    
    public int rotation() {
        return this.rotation;
    }
    
    public RoleType roleType() {
        return this.roleType;
    }
}
