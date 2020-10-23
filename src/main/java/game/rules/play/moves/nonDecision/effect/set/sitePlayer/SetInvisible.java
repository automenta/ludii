// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.set.sitePlayer;

import annotations.*;
import game.Game;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.region.RegionFunction;
import game.functions.region.sites.Sites;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.moves.Player;
import util.Context;
import util.Move;
import util.action.Action;
import util.action.hidden.ActionSetInvisible;
import util.state.containerState.ContainerState;

import java.util.ArrayList;
import java.util.List;

@Hide
public final class SetInvisible extends Effect
{
    private static final long serialVersionUID = 1L;
    final RegionFunction regionFn;
    final IntFunction whoFn;
    final BooleanFunction stackFn;
    protected SiteType type;
    
    public SetInvisible(@Opt final SiteType type, @Or final IntFunction site, @Or final RegionFunction region, @Opt @Or2 final Player who, @Opt @Or2 final RoleType role, @Opt @Name final BooleanFunction stack, @Opt final Then then) {
        super(then);
        if (region != null) {
            this.regionFn = region;
        }
        else {
            this.regionFn = Sites.construct(new IntFunction[] { site });
        }
        this.whoFn = ((who != null) ? who.index() : new Id(null, role));
        this.stackFn = ((stack == null) ? BooleanConstant.construct(false) : stack);
        this.type = type;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves result = new BaseMoves(super.then());
        final int[] sites = this.regionFn.eval(context).sites();
        final int who = this.whoFn.eval(context);
        final boolean stack = this.stackFn.eval(context);
        final List<Action> actions = new ArrayList<>();
        if (stack) {
            for (final int site : sites) {
                final ContainerState cs = context.containerState(context.containerId()[site]);
                for (int sizeStack = cs.sizeStack(site, SiteType.Cell), lvl = 0; lvl < sizeStack; ++lvl) {
                    if (who != -1) {
                        actions.add(new ActionSetInvisible(this.type, site, lvl, who));
                    }
                    else {
                        for (int pid = 1; pid < context.game().players().size(); ++pid) {
                            actions.add(new ActionSetInvisible(this.type, site, lvl, pid));
                        }
                    }
                }
            }
        }
        else {
            for (final int site : sites) {
                actions.add(new ActionSetInvisible(this.type, site, -1, who));
            }
        }
        final Move move = new Move(actions);
        result.moves().add(move);
        if (this.then() != null) {
            for (int j = 0; j < result.moves().size(); ++j) {
                result.moves().get(j).then().add(this.then().moves());
            }
        }
        return result;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0x8L | this.regionFn.gameFlags(game) | super.gameFlags(game);
        gameFlags |= SiteType.stateFlags(this.type);
        if (this.whoFn != null) {
            gameFlags |= this.whoFn.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return (this.whoFn == null || this.whoFn.isStatic()) && this.regionFn.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        if (this.type == null) {
            this.type = game.board().defaultSite();
        }
        this.regionFn.preprocess(game);
        if (this.whoFn != null) {
            this.whoFn.preprocess(game);
        }
    }
}
