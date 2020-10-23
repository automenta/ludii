// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.deductionPuzzle.is;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.booleans.BooleanFunction;
import game.functions.booleans.deductionPuzzle.is.graph.IsUnique;
import game.functions.booleans.deductionPuzzle.is.regionResult.IsCount;
import game.functions.booleans.deductionPuzzle.is.regionResult.IsSum;
import game.functions.booleans.deductionPuzzle.simple.IsSolved;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.types.board.SiteType;
import util.Context;

public class Is extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    
    public static BooleanFunction construct(final IsPuzzleSimpleType isType) {
        switch (isType) {
            case Solved -> {
                return new IsSolved();
            }
            default -> {
                throw new IllegalArgumentException("Is(): A IsPuzzleSimpleType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsPuzzleGraphType isType, @Opt final SiteType elementType) {
        switch (isType) {
            case Unique -> {
                return new IsUnique(elementType);
            }
            default -> {
                throw new IllegalArgumentException("Is(): A IsPuzzleGraphType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsPuzzleRegionResultType isType, @Opt final SiteType type, @Opt final RegionFunction region, @Opt @Name final IntFunction of, @Opt final String nameRegion, final IntFunction result) {
        switch (isType) {
            case Count -> {
                return new IsCount(type, region, of, result);
            }
            case Sum -> {
                return new IsSum(type, region, nameRegion, result);
            }
            default -> {
                throw new IllegalArgumentException("Is(): A IsPuzzleRegionResultType is not implemented.");
            }
        }
    }
    
    private Is() {
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
        throw new UnsupportedOperationException("Is.eval(): Should never be called directly.");
    }
}
