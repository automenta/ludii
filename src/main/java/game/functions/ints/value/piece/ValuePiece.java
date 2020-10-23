// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.value.piece;

import annotations.Hide;
import annotations.Name;
import game.Game;
import game.equipment.component.Component;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import util.Context;

@Hide
public final class ValuePiece extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction component;
    private int precomputedValue;
    
    public ValuePiece(@Name final IntFunction of) {
        this.precomputedValue = -1;
        this.component = of;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.precomputedValue != -1) {
            return this.precomputedValue;
        }
        final Component comp = context.components()[this.component.eval(context)];
        if (comp == null) {
            return 0;
        }
        return comp.getValue();
    }
    
    public IntFunction component() {
        return this.component;
    }
    
    @Override
    public boolean isStatic() {
        return this.component.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.component.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.component.preprocess(game);
        if (this.isStatic()) {
            this.precomputedValue = this.eval(new Context(game, null));
        }
    }
}
