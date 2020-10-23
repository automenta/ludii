// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.no;

import annotations.Hide;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.board.Id;
import game.types.play.RoleType;
import util.Context;
import util.state.State;

@Hide
public final class NoMoves extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final RoleType playerFn;
    private static ThreadLocal<Boolean> autoFail;
    
    public NoMoves(final RoleType playerFn) {
        this.playerFn = playerFn;
    }
    
    @Override
    public boolean eval(final Context context) {
        if (this.playerFn == RoleType.Next) {
            if (NoMoves.autoFail.get()) {
                return false;
            }
            final State state = context.state();
            final int currentPrevious = state.prev();
            final int currentMover = state.mover();
            final int nextMover = state.next();
            if (!context.trial().over() && context.active()) {
                context.setMoverAndImpliedPrevAndNext(state.next());
                state.setPrev(currentMover);
            }
            NoMoves.autoFail.set(Boolean.TRUE);
            context.game().computeStalemated(context);
            NoMoves.autoFail.set(Boolean.FALSE);
            state.setPrev(currentPrevious);
            state.setMover(currentMover);
            state.setNext(nextMover);
        }
        return context.state().isStalemated(new Id(null, this.playerFn).eval(context));
    }
    
    @Override
    public String toString() {
        return "Stalemated(" + this.playerFn + ")";
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    @Override
    public boolean autoFails() {
        return NoMoves.autoFail.get();
    }
    
    static {
        NoMoves.autoFail = ThreadLocal.withInitial(() -> Boolean.FALSE);
    }
}
