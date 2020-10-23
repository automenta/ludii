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
import util.action.BaseAction;
import util.action.state.ActionSetCount;

@Hide
public final class SetCount extends StartRule
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction region;
    private final Integer count;
    protected SiteType type;
    
    public SetCount(final Integer count, @Opt final SiteType type, @Or final IntFunction site, @Or final RegionFunction region) {
        if (site != null) {
            this.region = Sites.construct(new IntFunction[] { site });
        }
        else {
            this.region = region;
        }
        this.count = count;
        this.type = type;
    }
    
    @Override
    public void eval(final Context context) {
        if (context.components().length == 1) {
            System.err.println("Start Rule (set Count ...): At least a piece has to be defined to set the count of a site");
            return;
        }
        final int what = context.components()[context.components().length - 1].index();
        final int[] sites;
        final int[] locs = sites = this.region.eval(context).sites();
        for (final int loc : sites) {
            final BaseAction actionAtomic = new ActionSetCount(this.type, loc, what, this.count);
            actionAtomic.apply(context, true);
            context.trial().moves().add(new Move(actionAtomic));
            context.trial().addInitPlacement();
        }
    }
    
    @Override
    public boolean isStatic() {
        return this.region.isStatic();
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
        this.type = SiteType.use(this.type, game);
        this.region.preprocess(game);
    }
    
    @Override
    public String toString() {
        final String str = "(set " + this.region + " " + this.count + ")";
        return str;
    }
    
    @Override
    public int howManyPlace(final Game game) {
        this.region.preprocess(game);
        return this.region.eval(new Context(game, null)).sites().length;
    }
    
    @Override
    public int count() {
        return this.count;
    }
}
