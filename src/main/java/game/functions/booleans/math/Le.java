// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.math;

import annotations.Alias;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.types.board.SiteType;
import util.Context;
import util.state.puzzleState.ContainerDeductionPuzzleState;

@Alias(alias = "<=")
public final class Le extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction valueA;
    private final IntFunction valueB;
    private Boolean precomputedBoolean;
    
    public Le(final IntFunction valueA, final IntFunction valueB) {
        this.valueA = valueA;
        this.valueB = valueB;
    }
    
    @Override
    public boolean eval(final Context context) {
        if (this.precomputedBoolean != null) {
            return this.precomputedBoolean;
        }
        if (!context.game().isDeductionPuzzle()) {
            return this.valueA.eval(context) <= this.valueB.eval(context);
        }
        final ContainerDeductionPuzzleState ps = (ContainerDeductionPuzzleState)context.state().containerStates()[0];
        final SiteType type = context.board().defaultSite();
        final int indexA = this.valueA.eval(context);
        final int indexB = this.valueB.eval(context);
        if (!ps.isResolved(indexA, type) || !ps.isResolved(indexB, type)) {
            return true;
        }
        final int vA = ps.what(indexA, type);
        final int vB = ps.what(indexB, type);
        return vA <= vB;
    }
    
    public IntFunction valueA() {
        return this.valueA;
    }
    
    public IntFunction valueB() {
        return this.valueB;
    }
    
    @Override
    public String toString() {
        String str = "";
        str = str + "LesserThanOrEqual(" + this.valueA + ", " + this.valueB + ")";
        return str;
    }
    
    @Override
    public boolean isStatic() {
        return this.valueA.isStatic() && this.valueB.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.valueA.gameFlags(game) | this.valueB.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.valueA.preprocess(game);
        this.valueB.preprocess(game);
        if (this.isStatic()) {
            this.precomputedBoolean = this.eval(new Context(game, null));
        }
    }
}
