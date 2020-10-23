// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect;

import annotations.Opt;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.context.To;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.types.board.SiteType;
import game.util.moves.Flips;
import util.Context;
import util.Move;
import util.action.BaseAction;
import util.action.state.ActionSetState;

public final class Flip extends Effect
{
    private static final long serialVersionUID = 1L;
    protected final IntFunction locFn;
    private SiteType type;
    
    public Flip(@Opt final SiteType type, @Opt final IntFunction loc, @Opt final Then then) {
        super(then);
        this.locFn = ((loc == null) ? To.instance() : loc);
        this.type = type;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        final int loc = this.locFn.eval(context);
        if (loc == -1) {
            return moves;
        }
        final int currentState = context.containerState(context.containerId()[loc]).state(loc, this.type);
        final int whatValue = context.containerState(context.containerId()[loc]).what(loc, this.type);
        if (whatValue == 0) {
            return moves;
        }
        final Flips flips = context.components()[whatValue].getFlips();
        if (flips == null) {
            return moves;
        }
        final int newState = flips.flipValue(currentState);
        final BaseAction action = new ActionSetState(this.type, loc, newState);
        final Move m = new Move(action);
        moves.moves().add(m);
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        return moves;
    }
    
    @Override
    public String toString() {
        return "Flip(" + this.locFn + ")";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0x2L | this.locFn.gameFlags(game) | super.gameFlags(game);
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return this.locFn.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        super.preprocess(game);
        this.locFn.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "Flip";
    }
}
