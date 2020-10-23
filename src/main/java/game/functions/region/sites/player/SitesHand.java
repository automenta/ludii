// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.player;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.container.Container;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.region.BaseRegionFunction;
import game.types.play.RoleType;
import game.util.equipment.Region;
import game.util.moves.Player;
import util.Context;

@Hide
public final class SitesHand extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private Region precomputedRegion;
    private final IntFunction index;
    private final RoleType role;
    
    public SitesHand(@Or @Opt final Player player, @Or @Opt final RoleType role) {
        this.precomputedRegion = null;
        this.index = ((role != null) ? new Id(null, role) : ((player != null) ? player.index() : null));
        this.role = role;
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
            System.out.println("** Bad player index.");
            return new Region();
        }
        for (int id = 0; id < context.containers().length; ++id) {
            final Container c = context.containers()[id];
            if (c.isHand() && (this.role == RoleType.Shared || c.owner() == pid)) {
                final int[] sites = new int[context.game().equipment().containers()[id].numSites()];
                for (int i = 0; i < sites.length; ++i) {
                    sites[i] = context.game().equipment().sitesFrom()[id] + i;
                }
                return new Region(sites);
            }
        }
        return new Region();
    }
    
    @Override
    public boolean isStatic() {
        return this.index == null || this.index.isStatic();
    }
    
    @Override
    public String toString() {
        return "Hand()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        if (this.index != null) {
            flags = this.index.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.index != null) {
            this.index.preprocess(game);
        }
        if (this.isStatic()) {
            this.precomputedRegion = this.eval(new Context(game, null));
        }
    }
}
