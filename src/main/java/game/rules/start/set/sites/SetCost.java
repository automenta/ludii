// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start.set.sites;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.functions.region.sites.Sites;
import game.rules.start.StartRule;
import game.types.board.SiteType;
import util.Context;
import util.Move;
import util.action.graph.ActionSetCost;

@Hide
public final class SetCost extends StartRule
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction region;
    private final int cost;
    private SiteType type;
    
    public SetCost(final Integer cost, @Opt final SiteType type, @Or final IntFunction site, @Or final RegionFunction region) {
        this.type = type;
        if (site != null) {
            this.region = Sites.construct(new IntFunction[] { site });
        }
        else {
            this.region = region;
        }
        this.cost = cost;
    }
    
    @Override
    public void eval(final Context context) {
        final int[] sites;
        final int[] locs = sites = this.region.eval(context).sites();
        for (final int loc : sites) {
            final ActionSetCost actionSetCost = new ActionSetCost(this.type, loc, this.cost);
            actionSetCost.apply(context, true);
            context.trial().moves().add(new Move(actionSetCost));
            context.trial().addInitPlacement();
        }
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
    }
    
    @Override
    public String toString() {
        final String str = "(setCost)";
        return "(setCost)";
    }
}
