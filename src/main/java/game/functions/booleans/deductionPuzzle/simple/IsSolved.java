// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.deductionPuzzle.simple;

import annotations.Hide;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.booleans.BooleanFunction;
import game.rules.play.moves.nonDecision.effect.Satisfy;
import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public final class IsSolved extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public boolean eval(final Context context) {
        final ContainerState ps = context.state().containerStates()[0];
        final TIntArrayList varsConstraints = context.game().constraintVariables();
        final BooleanFunction[] constraints = ((Satisfy)context.game().rules().phases()[0].play().moves()).constraints();
        final TIntArrayList notAssignedVars = new TIntArrayList();
        final SiteType type = context.board().defaultSite();
        for (int i = 0; i < varsConstraints.size(); ++i) {
            final int var = varsConstraints.getQuick(i);
            if (!ps.isResolved(var, type)) {
                notAssignedVars.add(var);
            }
        }
        final Context newContext = new Context(context);
        for (int j = 0; j < notAssignedVars.size(); ++j) {
            newContext.state().containerStates()[0].set(notAssignedVars.getQuick(j), 0, type);
        }
        boolean constraintOK = true;
        if (constraints != null) {
            for (final BooleanFunction constraint : constraints) {
                if (!constraint.eval(newContext)) {
                    constraintOK = false;
                    break;
                }
            }
        }
        return constraintOK;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 128L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
