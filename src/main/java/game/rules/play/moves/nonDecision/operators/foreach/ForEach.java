// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.operators.foreach;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import annotations.Or2;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.rules.play.moves.nonDecision.operators.foreach.die.ForEachDie;
import game.rules.play.moves.nonDecision.operators.foreach.direction.ForEachDirection;
import game.rules.play.moves.nonDecision.operators.foreach.group.ForEachGroup;
import game.rules.play.moves.nonDecision.operators.foreach.piece.ForEachPiece;
import game.rules.play.moves.nonDecision.operators.foreach.player.ForEachPlayer;
import game.rules.play.moves.nonDecision.operators.foreach.site.ForEachSite;
import game.rules.play.moves.nonDecision.operators.foreach.value.ForEachValue;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.directions.Direction;
import game.util.moves.Between;
import game.util.moves.From;
import game.util.moves.Player;
import game.util.moves.To;
import util.Context;

public final class ForEach extends Effect
{
    private static final long serialVersionUID = 1L;
    
    public static Moves construct(final ForEachGroupType forEachType, @Opt final SiteType type, @Opt final Direction directions, @Opt @Name final BooleanFunction If, final Moves moves, @Opt final Then then) {
        switch (forEachType) {
            case Group -> {
                return new ForEachGroup(type, directions, If, moves, then);
            }
            default -> {
                throw new IllegalArgumentException("ForEach(): A ForEachGroupType is not implemented.");
            }
        }
    }
    
    public static Moves construct(final ForEachDieType forEachType, @Opt final IntFunction handDiceIndex, @Opt @Name final BooleanFunction combined, @Opt @Name final BooleanFunction replayDouble, @Opt @Name final BooleanFunction If, final Moves moves, @Opt final Then then) {
        switch (forEachType) {
            case Die -> {
                return new ForEachDie(handDiceIndex, combined, replayDouble, If, moves, then);
            }
            default -> {
                throw new IllegalArgumentException("ForEach(): A ForEachDieType is not implemented.");
            }
        }
    }
    
    public static Moves construct(final ForEachDirectionType forEachType, @Opt final From from, @Opt final Direction directions, @Opt final Between between, @Or final To to, @Or final Moves moves, @Opt final Then then) {
        int numNonNull = 0;
        if (to != null) {
            ++numNonNull;
        }
        if (moves != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("ForEach(): With ForEachDirectionType one to, moves parameter must be non-null.");
        }
        switch (forEachType) {
            case Direction -> {
                return new ForEachDirection(from, directions, between, to, moves, then);
            }
            default -> {
                throw new IllegalArgumentException("ForEach(): A ForEachDirectionType is not implemented.");
            }
        }
    }
    
    public static Moves construct(final ForEachSiteType forEachType, final RegionFunction regionFn, final Moves generator, @Opt @Name final Moves noMoveYet, @Opt final Then then) {
        switch (forEachType) {
            case Site -> {
                return new ForEachSite(regionFn, generator, noMoveYet, then);
            }
            default -> {
                throw new IllegalArgumentException("ForEach(): A ForEachSiteType is not implemented.");
            }
        }
    }
    
    public static Moves construct(final ForEachValueType forEachType, @Name final IntFunction min, @Name final IntFunction max, final Moves generator, @Opt final Then then) {
        switch (forEachType) {
            case Value -> {
                return new ForEachValue(min, max, generator, then);
            }
            default -> {
                throw new IllegalArgumentException("ForEach(): A ForEachValueType is not implemented.");
            }
        }
    }
    
    public static Moves construct(final ForEachPieceType forEachType, @Opt @Or final String item, @Opt @Or final String[] items, @Opt @Name @Or2 final IntFunction container, @Opt @Or2 final String containerName, @Opt final Moves specificMoves, @Opt @Or2 final Player player, @Opt @Or2 final RoleType role, @Opt @Name final BooleanFunction top, @Opt final SiteType type, @Opt final Then then) {
        int numNonNull = 0;
        if (item != null) {
            ++numNonNull;
        }
        if (items != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("ForEach(): With ForEachPieceType zero or one item, items parameter must be non-null.");
        }
        int numNonNull2 = 0;
        if (container != null) {
            ++numNonNull2;
        }
        if (containerName != null) {
            ++numNonNull2;
        }
        if (numNonNull2 > 1) {
            throw new IllegalArgumentException("ForEach(): With ForEachPieceType zero or one container, containerName parameter must be non-null.");
        }
        int numNonNull3 = 0;
        if (player != null) {
            ++numNonNull3;
        }
        if (role != null) {
            ++numNonNull3;
        }
        if (numNonNull3 > 1) {
            throw new IllegalArgumentException("ForEach(): With ForEachPieceType zero or one player, role parameter must be non-null.");
        }
        switch (forEachType) {
            case Piece -> {
                return new ForEachPiece(item, items, container, containerName, specificMoves, player, role, top, type, then);
            }
            default -> {
                throw new IllegalArgumentException("ForEach(): A ForEachPieceType is not implemented.");
            }
        }
    }
    
    public static Moves construct(final ForEachPlayerType forEachType, @Opt @Name @Or2 final IntFunction container, @Opt @Or2 final String containerName, @Opt final Moves specificMoves, @Opt @Or2 final Player player, @Opt @Or2 final RoleType role, @Opt @Name final BooleanFunction top, @Opt final SiteType type, @Opt final Then then) {
        int numNonNull = 0;
        if (container != null) {
            ++numNonNull;
        }
        if (containerName != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("ForEach(): With ForEachPlayerType zero or one container, containerName parameter must be non-null.");
        }
        int numNonNull2 = 0;
        if (player != null) {
            ++numNonNull2;
        }
        if (role != null) {
            ++numNonNull2;
        }
        if (numNonNull2 > 1) {
            throw new IllegalArgumentException("ForEach(): With ForEachPieceType zero or one player, role parameter must be non-null.");
        }
        switch (forEachType) {
            case Player -> {
                return new ForEachPlayer(container, containerName, specificMoves, role, player, top, type, then);
            }
            default -> {
                throw new IllegalArgumentException("ForEach(): A ForEachPlayerType is not implemented.");
            }
        }
    }
    
    private ForEach() {
        super(null);
    }
    
    @Override
    public Moves eval(final Context context) {
        throw new UnsupportedOperationException("ForEach.eval(): Should never be called directly.");
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
    public boolean canMoveTo(final Context context, final int target) {
        throw new UnsupportedOperationException("ForEach.canMoveTo(): Should never be called directly.");
    }
}
