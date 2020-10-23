// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.set.site;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.ints.IntFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.types.board.SiteType;
import util.Context;
import util.Move;
import util.action.BaseAction;
import util.action.state.ActionSetState;

@Hide
public final class SetState extends Effect
{
    private static final long serialVersionUID = 1L;
    protected final IntFunction siteFn;
    protected final IntFunction state;
    private SiteType type;
    
    public SetState(@Opt final SiteType type, @Name final IntFunction site, final IntFunction state, @Opt final Then then) {
        super(then);
        this.siteFn = site;
        this.state = state;
        this.type = type;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves result = new BaseMoves(super.then());
        final BaseAction action = new ActionSetState(this.type, this.siteFn.eval(context), this.state.eval(context));
        final Move move = new Move(action);
        result.moves().add(move);
        return result;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0x2L | this.siteFn.gameFlags(game) | this.state.gameFlags(game) | super.gameFlags(game);
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return this.siteFn.isStatic() && this.state.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        super.preprocess(game);
        this.siteFn.preprocess(game);
        this.state.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "SetState";
    }
    
    @Override
    public String toString() {
        return "SetState [siteFn=" + this.siteFn + ", state=" + this.state + "then=" + this.then() + "]";
    }
}
