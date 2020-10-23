// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.region;

import annotations.Opt;
import annotations.Or;
import game.functions.region.RegionFunction;
import game.types.board.SiteType;
import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;
import metadata.graphics.region.colour.RegionColour;
import metadata.graphics.util.BoardGraphicsType;
import metadata.graphics.util.colour.Colour;

public class Region implements GraphicsItem
{
    public static GraphicsItem construct(final RegionColourType regionType, @Opt final String region, @Opt final RoleType roleType, @Opt final SiteType graphElementType, @Opt @Or final Integer[] sites, @Opt @Or final Integer site, @Opt final RegionFunction regionFunction, @Opt final BoardGraphicsType boardGraphicsType, @Opt final Colour colour) {
        switch (regionType) {
            case Colour -> {
                return new RegionColour(region, roleType, graphElementType, sites, site, regionFunction, boardGraphicsType, colour);
            }
            default -> {
                throw new IllegalArgumentException("Region(): A RegionColourType is not implemented.");
            }
        }
    }
    
    private Region() {
    }
}
