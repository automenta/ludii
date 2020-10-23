// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.math;

import annotations.Alias;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import util.Context;

@Alias(alias = "%")
public final class Mod extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction value;
    private final IntFunction modulus;
    private int precomputedValue;
    
    public Mod(final IntFunction value, final IntFunction modulo) {
        this.precomputedValue = -1;
        this.value = value;
        this.modulus = modulo;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.precomputedValue != -1) {
            return this.precomputedValue;
        }
        return this.value.eval(context) % this.modulus.eval(context);
    }
    
    @Override
    public boolean isStatic() {
        return this.value.isStatic() && this.modulus.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.value.gameFlags(game) | this.modulus.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.value.preprocess(game);
        this.modulus.preprocess(game);
        if (this.isStatic()) {
            this.precomputedValue = this.eval(new Context(game, null));
        }
    }
}
