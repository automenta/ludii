// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves;

import annotations.Opt;
import collections.FastArrayList;
import game.Game;
import game.rules.play.moves.nonDecision.effect.Then;
import game.types.state.GameType;
import util.BaseLudeme;
import util.Context;
import util.Move;

import java.util.Iterator;

public abstract class Moves extends BaseLudeme implements GameType
{
    private static final long serialVersionUID = 1L;
    private final FastArrayList<Move> moves;
    private Then then;
    private boolean isDecision;
    private boolean applyAfterAllMoves;
    
    public Moves(@Opt final Then then) {
        this.moves = new FastArrayList<>(10);
        this.isDecision = false;
        this.applyAfterAllMoves = false;
        this.then = then;
    }
    
    public FastArrayList<Move> moves() {
        return this.moves;
    }
    
    public Then then() {
        return this.then;
    }
    
    public void setThen(final Then then) {
        this.then = then;
    }
    
    public int count() {
        return this.moves.size();
    }
    
    public Move get(final int n) {
        return this.moves.get(n);
    }
    
    public abstract Moves eval(final Context context);
    
    public boolean canMoveTo(final Context context, final int target) {
        final Iterator<Move> it = this.movesIterator(context);
        while (it.hasNext()) {
            if (it.next().toNonDecision() == target) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.then != null) {
            this.then.moves().preprocess(game);
        }
    }
    
    @Override
    public long gameFlags(final Game game) {
        long result = 0L;
        if (this.then != null) {
            result |= this.then.moves().gameFlags(game);
        }
        return result;
    }
    
    @Override
    public boolean isStatic() {
        return this.then != null && this.then.moves().isStatic();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Move m : this.moves) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(m.toString());
        }
        if (this.then != null) {
            sb.append(this.then);
        }
        return sb.toString();
    }
    
    @Override
    public String toEnglish() {
        return this.getClass().getSimpleName();
    }
    
    public boolean isConstraintsMoves() {
        return false;
    }
    
    public boolean isDecision() {
        return this.isDecision;
    }
    
    public void setDecision() {
        this.isDecision = true;
    }
    
    public boolean applyAfterAllMoves() {
        return this.applyAfterAllMoves;
    }
    
    public void setApplyAfterAllMoves(final boolean value) {
        this.applyAfterAllMoves = value;
    }
    
    public Iterator<Move> movesIterator(final Context context) {
        return this.eval(context).moves().iterator();
    }
    
    public boolean canMove(final Context context) {
        final Iterator<Move> moveIterator = this.movesIterator(context);
        while (moveIterator.hasNext()) {
            final Move next = moveIterator.next();
            if (!Game.satisfiesStateComparison(context, next)) {
                continue;
            }
            return true;
        }
        return false;
    }
}
