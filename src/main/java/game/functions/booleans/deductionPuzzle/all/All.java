// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.deductionPuzzle.all;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.types.board.SiteType;
import util.Context;

public class All extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    
    public static BooleanFunction construct(final AllPuzzleType allType, @Opt final SiteType elementType, @Opt final RegionFunction region, @Opt @Or @Name final IntFunction except, @Opt @Or @Name final IntFunction[] excepts) {
        int numNonNull = 0;
        if (except != null) {
            ++numNonNull;
        }
        if (excepts != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("All(): With AllPuzzleType zero or one except or excepts parameter must be non-null.");
        }
        switch (allType) {
            case Different -> {
                return new AllDifferent(elementType, region, except, excepts);
            }
            default -> throw new IllegalArgumentException("All(): A AllPuzzleType is not implemented.");
        }
    }
    
    private All() {
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
    public boolean eval(final Context context) {
        throw new UnsupportedOperationException("All.eval(): Should never be called directly.");
    }
}
