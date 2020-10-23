// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.operators.logical;

import annotations.Opt;
import game.Game;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.NonDecision;
import game.rules.play.moves.nonDecision.effect.Then;
import game.rules.play.moves.nonDecision.operator.Operator;
import collections.FastArrayList;
import util.Context;
import util.Move;

public final class Append extends Operator
{
    private static final long serialVersionUID = 1L;
    private final Moves list;
    
    public Append(final NonDecision list, @Opt final Then then) {
        super(then);
        this.list = list;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves result = new BaseMoves(super.then());
        final FastArrayList<Move> evaluated = this.list.eval(context).moves();
        for (final Move m : evaluated) {
            m.setDecision(true);
        }
        if (evaluated.isEmpty()) {
            return result;
        }
        final Move newMove = new Move(evaluated);
        newMove.setMover(context.state().mover());
        result.moves().add(newMove);
        if (this.then() != null) {
            newMove.then().add(this.then().moves());
        }
        return result;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return super.gameFlags(game) | this.list.gameFlags(game);
    }
    
    @Override
    public boolean isStatic() {
        return super.isStatic() && this.list.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.list.preprocess(game);
    }
}
