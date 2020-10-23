// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.size;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.size.connection.SizeGroup;
import game.functions.ints.size.connection.SizeTerritory;
import game.functions.ints.size.largePiece.SizeLargePiece;
import game.functions.ints.size.site.SizeStack;
import game.functions.region.RegionFunction;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.moves.Player;
import util.Context;

public final class Size extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    
    public static IntFunction construct(final SizeSiteType sizeType, @Opt final SiteType type, @Opt @Or @Name final RegionFunction in, @Opt @Or @Name final IntFunction at) {
        int numNonNull = 0;
        if (in != null) {
            ++numNonNull;
        }
        if (at != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Size(): With SizeSiteType zero or one 'in' or 'at' parameters must be non-null.");
        }
        switch (sizeType) {
            case Stack -> {
                return new SizeStack(type, in, at);
            }
            default -> {
                throw new IllegalArgumentException("Size(): A SizeSiteType is not implemented.");
            }
        }
    }
    
    public static IntFunction construct(final SizeLargePieceType sizeType, @Opt final SiteType type, @Or @Name final RegionFunction in, @Or @Name final IntFunction at) {
        int numNonNull = 0;
        if (in != null) {
            ++numNonNull;
        }
        if (at != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Size(): With SizeLargePiece one 'in' or 'at' parameters must be non-null.");
        }
        switch (sizeType) {
            case LargePiece -> {
                return new SizeLargePiece(type, in, at);
            }
            default -> {
                throw new IllegalArgumentException("Size(): A SizeLargePiece is not implemented.");
            }
        }
    }
    
    public static IntFunction construct(@Opt final SizeGroupType sizeType, @Opt final SiteType type, @Name final IntFunction at, @Opt final Direction directions, @Opt @Name final BooleanFunction If) {
        switch (sizeType) {
            case Group -> {
                return new SizeGroup(type, at, directions, If);
            }
            default -> {
                throw new IllegalArgumentException("Size(): A SizeGroupType is not implemented.");
            }
        }
    }
    
    public static IntFunction construct(@Opt final SizeTerritoryType sizeType, @Opt final SiteType type, @Or final RoleType role, @Or final Player player, @Opt final AbsoluteDirection direction) {
        int numNonNull = 0;
        if (role != null) {
            ++numNonNull;
        }
        if (player != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Size(): With SizeTerritoryType only one role or player parameter must be non-null.");
        }
        switch (sizeType) {
            case Territory -> {
                return new SizeTerritory(type, role, player, direction);
            }
            default -> {
                throw new IllegalArgumentException("Size(): A SizeTerritoryType is not implemented.");
            }
        }
    }
    
    private Size() {
    }
    
    @Override
    public int eval(final Context context) {
        throw new UnsupportedOperationException("Size.eval(): Should never be called directly.");
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
