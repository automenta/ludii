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
import util.action.graph.ActionSetPhase;

@Hide
public final class SetPhase extends StartRule
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction region;
    private final int phase;
    private final SiteType type;
    
    public SetPhase(final Integer phase, @Opt final SiteType type, @Or final IntFunction site, @Or final RegionFunction region) {
        this.type = type;
        if (site != null) {
            this.region = Sites.construct(new IntFunction[] { site });
        }
        else {
            this.region = region;
        }
        this.phase = phase;
        if (phase > 3) {
            System.out.println("** Phase " + phase + " can't be drawn.");
        }
    }
    
    @Override
    public void eval(final Context context) {
        final int[] sites;
        final int[] locs = sites = this.region.eval(context).sites();
        for (final int loc : sites) {
            final ActionSetPhase actionSetPhase = new ActionSetPhase(this.type, loc, this.phase);
            actionSetPhase.apply(context, true);
            context.trial().moves().add(new Move(actionSetPhase));
            context.trial().addInitPlacement();
        }
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return SiteType.stateFlags(this.type);
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    @Override
    public String toString() {
        final String str = "(SetPhase)";
        return "(SetPhase)";
    }
}
