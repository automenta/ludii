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
public final class CountNumber extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction region;
    private final SiteType type;
    
    public CountNumber(@Opt final SiteType type, @Opt @Or2 @Name final RegionFunction in, @Opt @Or2 @Name final IntFunction at) {
        this.region = ((in != null) ? in : ((at != null) ? Sites.construct(new IntFunction[] { at }) : Sites.construct(new IntFunction[] { new LastTo(null) })));
        this.type = type;
    }
    
    @Override
    public int eval(final Context context) {
        final SiteType realSiteType = (this.type != null) ? this.type : context.board().defaultSite();
        int count = 0;
        final int[] sites = this.region.eval(context).sites();
        if (context.game().isStacking()) {
            for (final int siteI : sites) {
                count += context.state().containerStates()[context.containerId()[siteI]].sizeStack(siteI, realSiteType);
            }
        }
        else {
            for (final int siteI : sites) {
                count += context.state().containerStates()[context.containerId()[siteI]].count(siteI, realSiteType);
            }
        }
        return count;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public String toString() {
        return "Dim()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 4L;
        gameFlags |= SiteType.stateFlags(this.type);
        gameFlags |= this.region.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.region.preprocess(game);
    }
}
