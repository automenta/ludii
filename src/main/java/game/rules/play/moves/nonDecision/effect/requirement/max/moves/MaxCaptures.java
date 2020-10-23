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
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.Move;
import util.action.Action;
import util.action.move.ActionRemove;

import java.util.List;

@Hide
public final class MaxCaptures extends Effect
{
    private static final long serialVersionUID = 1L;
    private final Moves moves;
    
    public MaxCaptures(final Moves moves, @Opt final Then then) {
        super(then);
        this.moves = moves;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves returnMoves = new BaseMoves(super.then());
        final Moves movesToEval = this.moves.eval(context);
        final TIntArrayList numCaptureByMove = new TIntArrayList();
        for (final Move m : movesToEval.moves()) {
            int numCapture = 0;
            final List<Action> actions = m.getAllActions(context);
            for (final Action action : actions) {
                if (action instanceof ActionRemove) {
                    ++numCapture;
                }
            }
            numCaptureByMove.add(numCapture);
        }
        int maxCapture = 0;
        for (int i = 0; i < numCaptureByMove.size(); ++i) {
            if (numCaptureByMove.getQuick(i) > maxCapture) {
                maxCapture = numCaptureByMove.getQuick(i);
            }
        }
        for (int i = 0; i < numCaptureByMove.size(); ++i) {
            if (numCaptureByMove.getQuick(i) == maxCapture) {
                returnMoves.moves().add(movesToEval.get(i));
            }
        }
        if (this.then() != null) {
            for (int j = 0; j < this.moves.moves().size(); ++j) {
                this.moves.moves().get(j).then().add(this.then().moves());
            }
        }
        return returnMoves;
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
        return "MaxCaptures";
    }
}
