// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.board;

import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.other.Map;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.types.play.RoleType;
import util.Context;

public final class MapEntry extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final String name;
    private final IntFunction key;
    private int precomputedValue;
    
    public MapEntry(@Opt final String name, @Or final IntFunction key, @Or final RoleType keyRole) {
        this.precomputedValue = -1;
        int numNonNull = 0;
        if (key != null) {
            ++numNonNull;
        }
        if (keyRole != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Exactly one Or parameter must be non-null.");
        }
        this.name = name;
        this.key = ((key != null) ? key : new Id(null, keyRole));
    }
    
    @Override
    public int eval(final Context context) {
        if (this.precomputedValue != -1) {
            return this.precomputedValue;
        }
        final int keyValue = this.key.eval(context);
        for (final Map map : context.game().equipment().maps()) {
            if (this.name == null || map.name().equals(this.name)) {
                final int tempMapValue = map.to(keyValue);
                if (tempMapValue != -1 && tempMapValue != map.noEntryValue()) {
                    return tempMapValue;
                }
            }
        }
        return keyValue;
    }
    
    @Override
    public boolean isStatic() {
        return this.key.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.key.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.key.preprocess(game);
        if (this.isStatic()) {
            this.precomputedValue = this.eval(new Context(game, null));
        }
    }
}
