// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.largePiece;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.Game;
import game.equipment.component.Component;
import game.functions.ints.IntFunction;
import game.functions.region.BaseRegionFunction;
import game.types.board.SiteType;
import game.util.equipment.Region;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public final class SitesLargePiece extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction at;
    
    public SitesLargePiece(@Opt final SiteType type, @Name final IntFunction at) {
        this.at = at;
        this.type = type;
    }
    
    @Override
    public Region eval(final Context context) {
        final int site = this.at.eval(context);
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final TIntArrayList sitesOccupied = new TIntArrayList();
        if (site >= context.board().topology().getGraphElements(realType).size()) {
            return new Region(sitesOccupied.toArray());
        }
        final ContainerState cs = context.containerState(0);
        final int what = cs.what(site, realType);
        if (what == 0) {
            return new Region(sitesOccupied.toArray());
        }
        sitesOccupied.add(what);
        final Component piece = context.components()[what];
        if (!piece.isLargePiece()) {
            return new Region(sitesOccupied.toArray());
        }
        final int localState = cs.state(site, this.type);
        final TIntArrayList locs = piece.locs(context, site, localState, context.topology());
        for (int j = 0; j < locs.size(); ++j) {
            if (!sitesOccupied.contains(locs.get(j))) {
                sitesOccupied.add(locs.get(j));
            }
        }
        return new Region(sitesOccupied.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        gameFlags |= SiteType.stateFlags(this.type);
        gameFlags |= this.at.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.at.preprocess(game);
    }
}
