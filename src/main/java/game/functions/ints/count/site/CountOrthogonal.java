// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.count.site;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or2;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.last.LastTo;
import game.functions.region.RegionFunction;
import game.functions.region.sites.Sites;
import game.types.board.SiteType;
import util.Context;

@Hide
public final class CountOrthogonal extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private Integer preComputedInteger;
    private final RegionFunction region;
    private SiteType type;
    
    public CountOrthogonal(@Opt final SiteType type, @Opt @Or2 @Name final RegionFunction in, @Opt @Or2 @Name final IntFunction at) {
        this.preComputedInteger = null;
        this.region = ((in != null) ? in : ((at != null) ? Sites.construct(new IntFunction[] { at }) : Sites.construct(new IntFunction[] { new LastTo(null) })));
        this.type = type;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.preComputedInteger != null) {
            return this.preComputedInteger;
        }
        final SiteType realSiteType = (this.type != null) ? this.type : context.board().defaultSite();
        final int[] sites = this.region.eval(context).sites();
        switch (realSiteType) {
            case Cell: {
                if (sites[0] < context.topology().cells().size()) {
                    return context.topology().cells().get(sites[0]).orthogonal().size();
                }
                break;
            }
            case Edge: {
                if (sites[0] < context.topology().edges().size()) {
                    return context.topology().edges().get(sites[0]).vA().edges().size() + context.topology().edges().get(sites[0]).vB().edges().size();
                }
                break;
            }
            case Vertex: {
                if (sites[0] < context.topology().vertices().size()) {
                    return context.topology().vertices().get(sites[0]).orthogonal().size();
                }
                break;
            }
        }
        return -1;
    }
    
    @Override
    public boolean isStatic() {
        return this.region.isStatic();
    }
    
    @Override
    public String toString() {
        return "Orthogonal()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.region.gameFlags(game);
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.region.preprocess(game);
        if (this.isStatic()) {
            this.preComputedInteger = this.eval(new Context(game, null));
        }
    }
}
