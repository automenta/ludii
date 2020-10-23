// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.player;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.region.BaseRegionFunction;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.equipment.Region;
import game.util.moves.Player;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public final class SitesInvisible extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private Region precomputedRegion;
    private final IntFunction index;
    
    public SitesInvisible(@Or @Opt final Player player, @Or @Opt final RoleType role, @Opt final SiteType type) {
        this.precomputedRegion = null;
        this.index = ((role != null) ? new Id(null, role) : ((player != null) ? player.index() : null));
        this.type = type;
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        if (this.index == null) {
            return new Region();
        }
        final int pid = this.index.eval(context);
        if (pid < 1 || pid > context.game().players().size()) {
            System.out.println("SitesInvisible: Bad player index.");
            return new Region();
        }
        final TIntArrayList sites = new TIntArrayList();
        for (int id = 0; id < context.containers().length; ++id) {
            final int from = context.sitesFrom()[id];
            final int size = context.containers()[id].numSites();
            final ContainerState cs = context.containerState(id);
            for (int site = from; site < from + size; ++site) {
                if (cs.isInvisible(site, pid, this.type)) {
                    sites.add(site);
                }
            }
        }
        return new Region(sites.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return this.index == null || this.index.isStatic();
    }
    
    @Override
    public String toString() {
        return "SitesInvisible()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 8L;
        if (this.index != null) {
            flags = this.index.gameFlags(game);
        }
        flags |= SiteType.stateFlags(this.type);
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        if (this.index != null) {
            this.index.preprocess(game);
        }
        if (this.isStatic()) {
            this.precomputedRegion = this.eval(new Context(game, null));
        }
    }
}
