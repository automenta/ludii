// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.requirement.max.moves;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import util.Context;
import util.Move;
import util.TempContext;

@Hide
public final class MaxMoves extends Effect
{
    private static final long serialVersionUID = 1L;
    private final Moves moves;
    
    public MaxMoves(final Moves moves, @Opt final Then then) {
        super(then);
        this.moves = moves;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves returnMoves = new BaseMoves(super.then());
        final Moves movesToEval = this.moves.eval(context);
        final int[] replayCount = new int[movesToEval.moves().size()];
        for (int i = 0; i < movesToEval.moves().size(); ++i) {
            final Move m = movesToEval.moves().get(i);
            replayCount[i] = this.getReplayCount(context, m, 0);
        }
        int max = 0;
        for (final int count : replayCount) {
            if (count > max) {
                max = count;
            }
        }
        for (int j = 0; j < movesToEval.moves().size(); ++j) {
            if (replayCount[j] == max) {
                returnMoves.moves().add(movesToEval.moves().get(j));
            }
        }
        return returnMoves;
    }
    
    private int getReplayCount(final Context context, final Move m, final int count) {
        final Context newContext = new TempContext(context);
        newContext.game().apply(newContext, m);
        if (newContext.state().prev() != newContext.state().mover()) {
            return count;
        }
        final Moves legalMoves = newContext.game().moves(newContext);
        final int[] replayCount = new int[legalMoves.moves().size()];
        for (int i = 0; i < legalMoves.moves().size(); ++i) {
            final Move newMove = legalMoves.moves().get(i);
            replayCount[i] = this.getReplayCount(newContext, newMove, count + 1);
        }
        int max = 0;
        for (final int nbReplay : replayCount) {
            if (nbReplay > max) {
                max = nbReplay;
            }
        }
        return max;
    }
    
    @Override
    public boolean canMove(final Context context) {
        return this.moves.canMove(context);
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long gameFlags = this.moves.gameFlags(game) | super.gameFlags(game);
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
        return "MaxMove";
    }
}
