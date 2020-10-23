// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.index;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.region.BaseRegionFunction;
import game.types.board.SiteType;
import game.util.equipment.Region;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public final class SitesState extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction stateValue;
    
    public SitesState(@Opt final SiteType elementType, final IntFunction stateValue) {
        this.stateValue = stateValue;
        this.type = elementType;
    }
    
    @Override
    public Region eval(final Context context) {
        final TIntArrayList sites = new TIntArrayList();
        final int stateId = this.stateValue.eval(context);
        final ContainerState cs = context.containerState(0);
        for (int sitesTo = context.containers()[0].numSites(), site = 0; site < sitesTo; ++site) {
            if (cs.state(site, this.type) == stateId) {
                sites.add(site);
            }
        }
        return new Region(sites.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        flags |= this.stateValue.gameFlags(game);
        flags |= SiteType.stateFlags(this.type);
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.stateValue.preprocess(game);
    }
}
