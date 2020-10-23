// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.set.site;

import annotations.Hide;
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
import util.action.state.ActionSetCount;
import util.state.containerState.ContainerState;

@Hide
public final class SetCount extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction locationFunction;
    private final IntFunction newCount;
    protected SiteType type;
    
    public SetCount(@Opt final SiteType type, final IntFunction locationFunction, final IntFunction newCount, @Opt final Then then) {
        super(then);
        this.locationFunction = locationFunction;
        this.newCount = newCount;
        this.type = type;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int loc = this.locationFunction.eval(context);
        final int count = this.newCount.eval(context);
        final ContainerState cs = context.containerState(context.containerId()[loc]);
        final int what = cs.what(loc, this.type);
        final ActionSetCount action = new ActionSetCount(this.type, loc, what, count);
        final Move move = new Move(action);
        moves.moves().add(move);
        if (this.then() != null) {
            move.then().add(this.then().moves());
        }
        return moves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game);
        gameFlags |= SiteType.stateFlags(this.type);
        gameFlags |= this.locationFunction.gameFlags(game);
        gameFlags |= this.newCount.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return this.locationFunction.isStatic() && this.newCount.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        super.preprocess(game);
        this.locationFunction.preprocess(game);
        this.newCount.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "SetCount";
    }
}
