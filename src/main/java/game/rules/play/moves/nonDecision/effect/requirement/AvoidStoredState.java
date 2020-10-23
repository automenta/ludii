// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.requirement;

import annotations.Opt;
import game.Game;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import util.Context;
import util.Move;
import util.TempContext;

public final class AvoidStoredState extends Effect
{
    private static final long serialVersionUID = 1L;
    private final Moves moves;
    
    public AvoidStoredState(final Moves moves, @Opt final Then then) {
        super(then);
        this.moves = moves;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves returnMoves = new BaseMoves(super.then());
        final Moves movesToEval = this.moves.eval(context);
        final long stateToCompare = context.state().storedState();
        for (int i = 0; i < movesToEval.moves().size(); ++i) {
            final Move m = movesToEval.moves().get(i);
            final Context newContext = new TempContext(context);
            m.apply(newContext, true);
            if (newContext.state().stateHash() != stateToCompare) {
                returnMoves.moves().add(m);
            }
        }
        return returnMoves;
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long gameFlags = this.moves.gameFlags(game) | 0x40000000L | super.gameFlags(game);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        final boolean isStatic = this.moves.isStatic();
        return isStatic;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.moves.preprocess(game);
    }
    
    @Override
    public String toEnglish() {
        return "NotSameState";
    }
}
