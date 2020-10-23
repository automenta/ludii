// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.intArray.sizes;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.intArray.BaseIntArrayFunction;
import game.functions.intArray.IntArrayFunction;
import game.functions.intArray.sizes.group.SizesGroup;
import game.functions.ints.IntFunction;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.directions.Direction;
import util.Context;

public final class Sizes extends BaseIntArrayFunction
{
    private static final long serialVersionUID = 1L;
    
    public static IntArrayFunction construct(final SizesGroupType sizesType, @Opt final SiteType type, @Opt final Direction directions, @Opt @Or final RoleType role, @Opt @Or @Name final IntFunction of, @Opt @Or @Name final BooleanFunction If, @Opt @Name final IntFunction min) {
        int numNonNull = 0;
        if (role != null) {
            ++numNonNull;
        }
        if (of != null) {
            ++numNonNull;
        }
        if (If != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Sizes(): With SizesGroupType zero or one 'role' or 'of' or 'If' parameters must be non-null.");
        }
        switch (sizesType) {
            case Group: {
                return new SizesGroup(type, directions, role, of, If, min);
            }
            default: {
                throw new IllegalArgumentException("Sizes(): A SizeGroupType is not implemented.");
            }
        }
    }
    
    private Sizes() {
    }
    
    @Override
    public int[] eval(final Context context) {
        throw new UnsupportedOperationException("Sizes.eval(): Should never be called directly.");
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
}
