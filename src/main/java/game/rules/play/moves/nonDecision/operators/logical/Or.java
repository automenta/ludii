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
import util.Context;
import util.Move;

import java.util.Iterator;

public final class Or extends Operator
{
    private static final long serialVersionUID = 1L;
    final Moves[] list;
    
    public Or(final Moves movesA, final Moves movesB, @Opt final Then then) {
        super(then);
        (this.list = new Moves[2])[0] = movesA;
        this.list[1] = movesB;
    }
    
    public Or(final Moves[] list, @Opt final Then then) {
        super(then);
        this.list = list;
    }
    
    @Override
    public Iterator<Move> movesIterator(final Context context) {
        return new Iterator<>() {
            protected int listIdx = 0;
            protected Iterator<Move> itr = this.computeNextItr();

            @Override
            public boolean hasNext() {
                return this.itr != null;
            }

            @Override
            public Move next() {
                final Move next = this.itr.next();
                if (!this.itr.hasNext()) {
                    this.itr = this.computeNextItr();
                }
                if (Or.this.then() != null) {
                    next.then().add(Or.this.then().moves());
                }
                return next;
            }

            private Iterator<Move> computeNextItr() {
                while (Or.this.list.length > this.listIdx) {
                    final Iterator<Move> nextItr = Or.this.list[this.listIdx++].movesIterator(context);
                    if (nextItr.hasNext()) {
                        return nextItr;
                    }
                }
                return null;
            }
        };
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves moves = new BaseMoves(super.then());
        for (Moves value : this.list) {
            moves.moves().addAll(value.eval(context).moves());
        }
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        return moves;
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        for (final Moves moves : this.list) {
            if (moves.canMoveTo(context, target)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Or(" + this.list + ")";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.list[0].gameFlags(game) | super.gameFlags(game);
        for (final Moves moves : this.list) {
            gameFlags |= moves.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        boolean isStatic = this.list[0].isStatic();
        for (final Moves moves : this.list) {
            isStatic = (isStatic && moves.isStatic());
        }
        return isStatic;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        for (final Moves moves : this.list) {
            moves.preprocess(game);
        }
    }
    
    public Moves[] list() {
        return this.list;
    }
    
    @Override
    public String toEnglish() {
        return "Or";
    }
}
