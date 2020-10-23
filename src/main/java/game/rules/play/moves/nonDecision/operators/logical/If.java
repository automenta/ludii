// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.operators.logical;

import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Then;
import game.rules.play.moves.nonDecision.operator.Operator;
import util.Context;
import util.Move;

import java.util.Iterator;

public final class If extends Operator
{
    private static final long serialVersionUID = 1L;
    final BooleanFunction cond;
    final Moves list;
    final Moves elseList;
    
    public If(final BooleanFunction cond, final Moves list, @Opt final Moves elseList, @Opt final Then then) {
        super(then);
        this.cond = cond;
        this.list = list;
        this.elseList = elseList;
    }
    
    @Override
    public Iterator<Move> movesIterator(final Context context) {
        return new Iterator<>() {
            protected final Iterator<Move> itr = this.computeItr();

            @Override
            public boolean hasNext() {
                return this.itr != null && this.itr.hasNext();
            }

            @Override
            public Move next() {
                final Move next = this.itr.next();
                if (If.this.then() != null) {
                    next.then().add(If.this.then().moves());
                }
                return next;
            }

            private Iterator<Move> computeItr() {
                if (If.this.cond.eval(context)) {
                    return If.this.list.movesIterator(context);
                }
                if (If.this.elseList != null) {
                    return If.this.elseList.movesIterator(context);
                }
                return null;
            }
        };
    }
    
    @Override
    public Moves eval(final Context context) {
        if (this.cond.eval(context)) {
            final Moves moves = this.list.eval(context);
            if (this.then() != null) {
                for (int j = 0; j < moves.moves().size(); ++j) {
                    moves.moves().get(j).then().add(this.then().moves());
                }
            }
            return moves;
        }
        if (this.elseList != null) {
            final Moves moves = this.elseList.eval(context);
            if (this.then() != null) {
                for (int j = 0; j < moves.moves().size(); ++j) {
                    moves.moves().get(j).then().add(this.then().moves());
                }
            }
            return moves;
        }
        final Moves moves = new BaseMoves(super.then());
        if (this.then() != null) {
            for (int j = 0; j < moves.moves().size(); ++j) {
                moves.moves().get(j).then().add(this.then().moves());
            }
        }
        return new BaseMoves(super.then());
    }
    
    @Override
    public boolean canMove(final Context context) {
        if (this.cond.eval(context)) {
            return this.list.canMove(context);
        }
        return this.elseList != null && this.elseList.canMove(context);
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        if (this.cond.eval(context)) {
            return this.list.canMoveTo(context, target);
        }
        return this.elseList != null && this.elseList.canMoveTo(context, target);
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = super.gameFlags(game);
        if (this.cond != null) {
            flags |= this.cond.gameFlags(game);
        }
        if (this.list != null) {
            flags |= this.list.gameFlags(game);
        }
        if (this.elseList != null) {
            flags |= this.elseList.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public boolean isStatic() {
        return (this.cond == null || this.cond.isStatic()) && (this.list == null || this.list.isStatic()) && (this.elseList == null || this.elseList.isStatic());
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        if (this.cond != null) {
            this.cond.preprocess(game);
        }
        if (this.list != null) {
            this.list.preprocess(game);
        }
        if (this.elseList != null) {
            this.elseList.preprocess(game);
        }
    }
    
    public BooleanFunction cond() {
        return this.cond;
    }
    
    public Moves list() {
        return this.list;
    }
    
    public Moves elseList() {
        return this.elseList;
    }
    
    @Override
    public String toEnglish() {
        return "If";
    }
}
