// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.edge;

import annotations.Hide;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import util.Context;

@Hide
public final class IsCrossing extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction edge1Fn;
    private final IntFunction edge2Fn;
    private Boolean precomputedBoolean;
    
    public IsCrossing(final IntFunction edge1, final IntFunction edge2) {
        this.edge1Fn = edge1;
        this.edge2Fn = edge2;
    }
    
    @Override
    public boolean eval(final Context context) {
        if (this.precomputedBoolean != null) {
            return this.precomputedBoolean;
        }
        final int edge1 = this.edge1Fn.eval(context);
        final int edge2 = this.edge2Fn.eval(context);
        return edge1 >= 0 && edge2 >= 0 && edge1 < context.topology().edges().size() && edge2 < context.topology().edges().size() && context.topology().edges().get(edge1).doesCross(edge2);
    }
    
    @Override
    public boolean isStatic() {
        return this.edge1Fn.isStatic() && this.edge2Fn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.edge1Fn.gameFlags(game) | this.edge2Fn.gameFlags(game) | 0x4000000L | 0x800000L;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.edge1Fn.preprocess(game);
        this.edge2Fn.preprocess(game);
        if (this.isStatic()) {
            this.precomputedBoolean = this.eval(new Context(game, null));
        }
    }
}
