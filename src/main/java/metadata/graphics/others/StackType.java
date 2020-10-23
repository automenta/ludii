// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.others;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.types.board.SiteType;
import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;
import metadata.graphics.util.PieceStackType;

public class StackType implements GraphicsItem
{
    private final RoleType roleType;
    private final String name;
    private final Integer index;
    private final SiteType graphElementType;
    private final Integer[] sites;
    private final PieceStackType stackType;
    private final double scale;
    private final Integer state;
    
    public StackType(@Opt final RoleType roleType, @Opt final String name, @Opt final Integer index, @Opt final SiteType graphElementType, @Opt @Or @Name final Integer[] sites, @Opt @Or @Name final Integer site, @Opt @Name final Integer state, final PieceStackType stackType, @Opt final Float scale) {
        this.roleType = roleType;
        this.name = name;
        this.index = index;
        this.graphElementType = graphElementType;
        this.sites = (sites != null) ? sites : ((site != null) ? new Integer[] { site } : null);
        this.state = state;
        this.stackType = stackType;
        this.scale = (scale == null) ? 1.0 : scale;
    }
    
    public RoleType roleType() {
        return this.roleType;
    }
    
    public String name() {
        return this.name;
    }
    
    public SiteType graphElementType() {
        return this.graphElementType;
    }
    
    public Integer index() {
        return this.index;
    }
    
    public Integer[] sites() {
        return this.sites;
    }
    
    public PieceStackType stackType() {
        return this.stackType;
    }
    
    public double scale() {
        return this.scale;
    }
    
    public Integer state() {
        return this.state;
    }
}
