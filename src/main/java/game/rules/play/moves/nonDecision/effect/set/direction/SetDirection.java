// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.set.direction;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.context.From;
import game.rules.play.moves.BaseMoves;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.types.board.SiteType;
import game.util.moves.To;
import util.Context;
import util.Move;
import util.action.state.ActionSetRotation;

@Hide
public final class SetDirection extends Effect
{
    private static final long serialVersionUID = 1L;
    private final IntFunction siteFn;
    private final IntFunction[] directionsFn;
    private final BooleanFunction previous;
    private final BooleanFunction next;
    private SiteType type;
    
    public SetDirection(@Opt final To to, @Opt @Or final IntFunction[] directions, @Opt @Or final IntFunction direction, @Opt @Name final BooleanFunction previous, @Opt @Name final BooleanFunction next, @Opt final Then then) {
        super(then);
        int numNonNull = 0;
        if (directions != null) {
            ++numNonNull;
        }
        if (direction != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Zero or one Or parameter must be non-null.");
        }
        this.siteFn = ((to == null) ? new From(null) : ((to.loc() != null) ? to.loc() : new From(null)));
        if (directions != null) {
            this.directionsFn = directions;
        }
        else {
            this.directionsFn = (direction == null) ? null : new IntFunction[] { direction };
        }
        this.previous = ((previous == null) ? BooleanConstant.construct(true) : previous);
        this.next = ((next == null) ? BooleanConstant.construct(true) : next);
        this.type = ((to == null) ? null : to.type());
    }
    
    @Override
    public Moves eval(final Context context) {
        final BaseMoves moves = new BaseMoves(super.then());
        final int site = this.siteFn.eval(context);
        if (site == -1) {
            return moves;
        }
        if (this.directionsFn != null) {
            for (final IntFunction directionFn : this.directionsFn) {
                final int direction = directionFn.eval(context);
                final ActionSetRotation actionRotation = new ActionSetRotation(this.type, site, direction);
                if (this.isDecision()) {
                    actionRotation.setDecision(true);
                }
                final Move action = new Move(actionRotation);
                action.setFromNonDecision(site);
                action.setToNonDecision(site);
                action.setMover(context.state().mover());
                moves.moves().add(action);
            }
        }
        if (this.previous != null || this.next != null) {
            final int currentRotation = context.containerState(context.containerId()[site]).rotation(site, this.type);
            final int maxRotation = context.game().maximalRotationStates() - 1;
            if (this.previous != null && this.previous.eval(context)) {
                final int newRotation = (currentRotation > 0) ? (currentRotation - 1) : maxRotation;
                final ActionSetRotation actionRotation2 = new ActionSetRotation(this.type, site, newRotation);
                if (this.isDecision()) {
                    actionRotation2.setDecision(true);
                }
                final Move action2 = new Move(actionRotation2);
                action2.setFromNonDecision(site);
                action2.setToNonDecision(site);
                action2.setMover(context.state().mover());
                moves.moves().add(action2);
            }
            if (this.next != null && this.next.eval(context)) {
                final int newRotation = (currentRotation < maxRotation) ? (currentRotation + 1) : 0;
                final ActionSetRotation actionRotation2 = new ActionSetRotation(this.type, site, newRotation);
                if (this.isDecision()) {
                    actionRotation2.setDecision(true);
                }
                final Move action2 = new Move(actionRotation2);
                action2.setFromNonDecision(site);
                action2.setToNonDecision(site);
                action2.setMover(context.state().mover());
                moves.moves().add(action2);
            }
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
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = super.gameFlags(game) | this.siteFn.gameFlags(game) | this.previous.gameFlags(game) | this.next.gameFlags(game) | 0x20000L;
        if (this.directionsFn != null) {
            for (final IntFunction direction : this.directionsFn) {
                gameFlags |= direction.gameFlags(game);
            }
        }
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        boolean isStatic = this.siteFn.isStatic() | this.previous.isStatic() | this.next.isStatic();
        if (this.directionsFn != null) {
            for (final IntFunction direction : this.directionsFn) {
                isStatic |= direction.isStatic();
            }
        }
        return isStatic;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        super.preprocess(game);
        this.siteFn.preprocess(game);
        this.previous.preprocess(game);
        this.next.preprocess(game);
        if (this.directionsFn != null) {
            for (final IntFunction direction : this.directionsFn) {
                direction.preprocess(game);
            }
        }
    }
}
