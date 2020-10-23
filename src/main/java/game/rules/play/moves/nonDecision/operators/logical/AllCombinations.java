// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.operators.logical;

import annotations.Opt;
import game.Game;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Then;
import game.rules.play.moves.nonDecision.operator.Operator;
import main.collections.FastArrayList;
import util.Context;
import util.Move;

public final class AllCombinations extends Operator
{
    private static final long serialVersionUID = 1L;
    private final Moves listA;
    private final Moves listB;
    
    public AllCombinations(final Moves listA, final Moves listB, @Opt final Then then) {
        super(then);
        this.listA = listA;
        this.listB = listB;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves result = new BaseMoves(super.then());
        final FastArrayList<Move> ev1 = this.listA.eval(context).moves();
        final FastArrayList<Move> ev2 = this.listB.eval(context).moves();
        for (final Move m1 : ev1) {
            for (final Move m2 : ev2) {
                final Move newMove = new Move(m1, m2);
                if (this.then() != null) {
                    newMove.then().add(this.then().moves());
                }
                result.moves().add(newMove);
            }
        }
        return result;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.listA.gameFlags(game) | this.listB.gameFlags(game) | super.gameFlags(game);
    }
    
    @Override
    public boolean isStatic() {
        return this.listA.isStatic() && this.listB.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.listA.preprocess(game);
        this.listB.preprocess(game);
    }
}
