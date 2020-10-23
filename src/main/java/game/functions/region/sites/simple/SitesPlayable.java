// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.simple;

import annotations.Hide;
import game.Game;
import game.functions.booleans.is.in.IsIn;
import game.functions.ints.IntFunction;
import game.functions.ints.context.To;
import game.functions.region.BaseRegionFunction;
import game.functions.region.RegionConstant;
import game.functions.region.RegionFunction;
import game.functions.region.sites.Sites;
import game.functions.region.sites.SitesSimpleType;
import game.functions.region.sites.around.SitesAround;
import game.functions.region.sites.index.SitesEmpty;
import game.types.board.SiteType;
import game.util.equipment.Region;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public final class SitesPlayable extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public Region eval(final Context context) {
        final TIntArrayList sites = new TIntArrayList();
        if (context.game().isBoardless()) {
            final ContainerState cs = context.state().containerStates()[0];
            for (int numSite = context.game().equipment().containers()[0].numSites(), index = 0; index < numSite; ++index) {
                if (cs.isPlayable(index)) {
                    sites.add(index);
                }
            }
            return new Region(sites.toArray());
        }
        if (context.trial().moveNumber() == 0) {
            return Sites.construct(SitesSimpleType.Centre, null).eval(context);
        }
        final TIntArrayList occupiedSite = new TIntArrayList();
        for (int i = 0; i < context.containers()[0].numSites(); ++i) {
            if (!context.containerState(0).isEmpty(i, SiteType.Cell)) {
                occupiedSite.add(i);
            }
        }
        final RegionFunction occupiedRegion = new RegionConstant(new Region(occupiedSite.toArray()));
        return new SitesAround(null, null, occupiedRegion, null, null, null, IsIn.construct(null, new IntFunction[] { To.instance() }, SitesEmpty.construct(null, null)), null).eval(context);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public String toString() {
        return "Playable()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
