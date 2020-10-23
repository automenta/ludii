// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.requirement;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import collections.FastArrayList;
import util.Context;
import util.Move;
import util.TempContext;

import java.util.Iterator;

public final class Do extends Effect
{
    private static final long serialVersionUID = 1L;
    final Moves prior;
    final Moves next;
    final BooleanFunction ifAfterwards;
    
    public Do(final Moves prior, @Opt @Name final Moves next, @Opt @Name final BooleanFunction ifAfterwards, @Opt final Then then) {
        super(then);
        this.next = next;
        this.prior = prior;
        this.ifAfterwards = ifAfterwards;
    }
    
    @Override
    public Moves eval(final Context context) {
        final Moves result = new BaseMoves(super.then());
        if (this.next != null) {
            final Context newContext = new TempContext(context);
            final Moves preMoves = this.generateAndApplyPreMoves(context, newContext);
            result.moves().addAll(this.next.eval(newContext).moves());
            prependPreMoves(preMoves, result, context);
        }
        if (this.ifAfterwards != null) {
            final Moves movesAfterIf = new BaseMoves(super.then());
            final FastArrayList<Move> toCheck = this.prior.eval(context).moves();
            if (this.ifAfterwards.autoSucceeds()) {
                movesAfterIf.moves().addAll(toCheck);
            }
            else {
                for (final Move m : toCheck) {
                    if (this.movePassesCond(m, context)) {
                        movesAfterIf.moves().add(m);
                    }
                }
            }
            if (this.then() != null) {
                for (int j = 0; j < movesAfterIf.moves().size(); ++j) {
                    movesAfterIf.moves().get(j).then().add(this.then().moves());
                }
            }
            return movesAfterIf;
        }
        if (this.then() != null) {
            for (int i = 0; i < result.moves().size(); ++i) {
                result.moves().get(i).then().add(this.then().moves());
            }
        }
        return result;
    }
    
    public final Moves generateAndApplyPreMoves(final Context genContext, final Context applyContext) {
        final Moves preMoves = new BaseMoves(null);
        for (final Move m : this.prior.eval(genContext).moves()) {
            final Move appliedMove = (Move)m.apply(applyContext, false);
            preMoves.moves().add(appliedMove);
        }
        return preMoves;
    }
    
    public static void prependPreMoves(final Moves preMoves, final Moves result, final Context context) {
        int insertIndex = 0;
        for (final Move preM : preMoves.moves()) {
            for (final Move move : result.moves()) {
                move.actions().addAll(insertIndex, preM.actions());
            }
            insertIndex = preM.actions().size();
        }
        if (result.moves().isEmpty() && context.game().hasHandDice()) {
            final Move passMove = Game.createPassMove(context);
            for (final Move preM2 : preMoves.moves()) {
                passMove.actions().addAll(0, preM2.actions());
            }
            result.moves().add(passMove);
        }
    }
    
    @Override
    public Iterator<Move> movesIterator(final Context context) {
        if (this.next == null && this.ifAfterwards != null) {
            return new Iterator<>() {
                protected final Iterator<Move> itr = Do.this.prior.movesIterator(context);
                protected Move nextMove = this.computeNextMove();

                @Override
                public boolean hasNext() {
                    return this.nextMove != null;
                }

                @Override
                public Move next() {
                    final Move ret = this.nextMove;
                    this.nextMove = this.computeNextMove();
                    if (Do.this.then() != null) {
                        ret.then().add(Do.this.then().moves());
                    }
                    return ret;
                }

                private Move computeNextMove() {
                    while (this.itr.hasNext()) {
                        final Move nextC = this.itr.next();
                        if (Do.this.ifAfterwards.autoSucceeds() || Do.this.movePassesCond(nextC, context)) {
                            return nextC;
                        }
                    }
                    return null;
                }
            };
        }
        return super.movesIterator(context);
    }
    
    public boolean movePassesCond(final Move m, final Context context) {
        final Context newContext = new TempContext(context);
        m.apply(newContext, true);
        if (newContext.state().mover() == newContext.state().next()) {
            newContext.state().setNext(newContext.state().prev());
        }
        return this.ifAfterwards.eval(newContext);
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.prior.gameFlags(game) | super.gameFlags(game);
        if (this.next != null) {
            gameFlags |= this.next.gameFlags(game);
        }
        if (this.ifAfterwards != null) {
            gameFlags |= this.ifAfterwards.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
        super.preprocess(game);
        this.prior.preprocess(game);
        if (this.next != null) {
            this.next.preprocess(game);
        }
        if (this.ifAfterwards != null) {
            this.ifAfterwards.preprocess(game);
        }
    }
    
    public Moves prior() {
        return this.prior;
    }
    
    public Moves after() {
        return this.next;
    }
    
    public BooleanFunction ifAfter() {
        return this.ifAfterwards;
    }
    
    @Override
    public String toEnglish() {
        return "Prior";
    }
    
    @Override
    public boolean canMoveTo(final Context context, final int target) {
        if (this.next != null) {
            return this.next.canMoveTo(context, target);
        }
        if (this.ifAfterwards == null) {
            return false;
        }
        if (this.ifAfterwards.autoSucceeds()) {
            return this.prior.canMoveTo(context, target);
        }
        final Iterator<Move> movesIterator = this.movesIterator(context);
        while (movesIterator.hasNext()) {
            final Move m = movesIterator.next();
            if (m.toNonDecision() == target) {
                return true;
            }
        }
        return false;
    }
}
