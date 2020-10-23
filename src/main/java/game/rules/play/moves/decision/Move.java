// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.decision;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.intArray.IntArrayFunction;
import game.functions.intArray.math.Difference;
import game.functions.ints.IntFunction;
import game.functions.range.RangeFunction;
import game.functions.region.RegionFunction;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.*;
import game.rules.play.moves.nonDecision.effect.set.SetDirectionType;
import game.rules.play.moves.nonDecision.effect.set.SetNextPlayerType;
import game.rules.play.moves.nonDecision.effect.set.SetTrumpType;
import game.rules.play.moves.nonDecision.effect.set.direction.SetDirection;
import game.rules.play.moves.nonDecision.effect.set.nextPlayer.SetNextPlayer;
import game.rules.play.moves.nonDecision.effect.set.suit.SetTrumpSuit;
import game.rules.play.moves.nonDecision.effect.state.swap.SwapSitesType;
import game.rules.play.moves.nonDecision.effect.state.swap.sites.SwapPieces;
import game.types.board.SiteType;
import game.types.board.StepType;
import game.types.play.RoleType;
import game.types.play.WhenType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.moves.*;
import util.Context;

public final class Move extends Decision
{
    private static final long serialVersionUID = 1L;
    
    public static Moves construct(final MoveSwapType moveType, final SwapSitesType swapType, @Opt final IntFunction locA, @Opt final IntFunction locB, @Opt final Then then) {
        Moves moves = null;
        switch (moveType) {
            case Swap -> moves = new SwapPieces(locA, locB, then);
        }
        if (moves == null) {
            throw new IllegalArgumentException("Move(): A MoveSwapType is not implemented.");
        }
        moves.setDecision();
        return moves;
    }
    
    public static Moves construct(final MoveRemoveType moveType, @Opt final SiteType type, @Or final IntFunction locationFunction, @Or final RegionFunction regionFunction, @Opt @Name final WhenType at, @Opt @Name final IntFunction count, @Opt final Then then) {
        Moves moves = null;
        switch (moveType) {
            case Remove -> moves = new Remove(type, locationFunction, regionFunction, at, count, then);
        }
        if (moves == null) {
            throw new IllegalArgumentException("Move(): A MoveRemoveType is not implemented.");
        }
        moves.setDecision();
        return moves;
    }
    
    public static Moves construct(final MoveSetType moveType, final SetTrumpType setType, @Or final IntFunction suit, @Or final Difference suits, @Opt final Then then) {
        int numNonNull = 0;
        if (suit != null) {
            ++numNonNull;
        }
        if (suits != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Move(): With SetSuitType only one suit or suits parameter must be non-null.");
        }
        Moves moves = null;
        switch (setType) {
            case TrumpSuit -> moves = new SetTrumpSuit(suit, suits, then);
        }
        if (moves == null) {
            throw new IllegalArgumentException("Move(): A SetSuitType is not implemented.");
        }
        moves.setDecision();
        return moves;
    }
    
    public static Moves construct(final MoveSetType moveType, final SetNextPlayerType setType, @Or final Player who, @Or final IntArrayFunction nextPlayers, @Opt final Then then) {
        int numNonNull = 0;
        if (who != null) {
            ++numNonNull;
        }
        if (nextPlayers != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Move(): With SetPlayerType only one who or nextPlayers parameter can be non-null.");
        }
        Moves moves = null;
        switch (setType) {
            case NextPlayer -> moves = new SetNextPlayer(who, nextPlayers, then);
        }
        if (moves == null) {
            throw new IllegalArgumentException("Move(): A SetPlayerType is not implemented.");
        }
        moves.setDecision();
        return moves;
    }
    
    public static Moves construct(final MoveSetType moveType, final SetDirectionType setType, @Opt final To to, @Opt @Or final IntFunction[] directions, @Opt @Or final IntFunction direction, @Opt @Name final BooleanFunction previous, @Opt @Name final BooleanFunction next, @Opt final Then then) {
        int numNonNull = 0;
        if (directions != null) {
            ++numNonNull;
        }
        if (direction != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Move(): With SetDirectionType zero or one directions or direction parameter must be non-null.");
        }
        Moves moves = null;
        switch (setType) {
            case Direction -> moves = new SetDirection(to, directions, direction, previous, next, then);
        }
        if (moves == null) {
            throw new IllegalArgumentException("Move(): A SetDirectionType is not implemented.");
        }
        moves.setDecision();
        return moves;
    }
    
    public static Moves construct(final MoveStepType moveType, @Opt final From from, @Opt final Direction directions, final To to, @Opt @Name final Boolean stack, @Opt final Then then) {
        Moves moves = null;
        switch (moveType) {
            case Step -> moves = new Step(from, directions, to, stack, then);
        }
        if (moves == null) {
            throw new IllegalArgumentException("Move(): A MoveStepType is not implemented.");
        }
        moves.setDecision();
        return moves;
    }
    
    public static Moves construct(final MoveSlideType moveType, @Opt final From from, @Opt final String track, @Opt final Direction directions, @Opt final Between between, @Opt final To to, @Opt final Then then) {
        Moves moves = null;
        switch (moveType) {
            case Slide -> moves = new Slide(from, track, directions, between, to, then);
        }
        if (moves == null) {
            throw new IllegalArgumentException("Move(): A MoveSlideType is not implemented.");
        }
        moves.setDecision();
        return moves;
    }
    
    public static Moves construct(final MoveShootType moveType, final Piece what, @Opt final From from, @Opt final AbsoluteDirection dirn, @Opt final Between between, @Opt final To to, @Opt final Then then) {
        Moves moves = null;
        switch (moveType) {
            case Shoot -> moves = new Shoot(what, from, dirn, between, to, then);
        }
        if (moves == null) {
            throw new IllegalArgumentException("Move(): A MoveShootType is not implemented.");
        }
        moves.setDecision();
        return moves;
    }
    
    public static Moves construct(final MoveSelectType moveType, final From from, @Opt final To to, @Opt final Then then) {
        Moves moves = null;
        switch (moveType) {
            case Select -> moves = new Select(from, to, then);
        }
        if (moves == null) {
            throw new IllegalArgumentException("Move(): A MoveSelectType is not implemented.");
        }
        moves.setDecision();
        return moves;
    }
    
    public static Moves construct(final MoveMessageType moveType, @Or final String message, @Or final String[] messages, @Opt final Then then) {
        Moves moves = null;
        switch (moveType) {
            case Propose -> moves = new Propose(message, messages, then);
            case Vote -> moves = new Vote(message, messages, then);
        }
        if (moves == null) {
            throw new IllegalArgumentException("Move(): A MoveProposeType is not implemented.");
        }
        moves.setDecision();
        return moves;
    }
    
    public static Moves construct(final MovePromoteType moveType, @Opt final SiteType type, @Opt final IntFunction locationFn, final Piece what, @Opt @Or final Player who, @Opt @Or final RoleType role, @Opt final Then then) {
        Moves moves = null;
        switch (moveType) {
            case Promote -> moves = new Promote(type, locationFn, what, who, role, then);
        }
        if (moves == null) {
            throw new IllegalArgumentException("Move(): A MovePromotionType is not implemented.");
        }
        moves.setDecision();
        return moves;
    }
    
    public static Moves construct(final MoveSimpleType moveType, @Opt final Then then) {
        Moves moves = null;
        switch (moveType) {
            case Pass -> moves = new Pass(then);
            case PlayCard -> moves = new PlayCard(then);
        }
        if (moves == null) {
            throw new IllegalArgumentException("Move(): A MoveSimpleType is not implemented.");
        }
        moves.setDecision();
        return moves;
    }
    
    public static Moves construct(final MoveLeapType moveType, @Opt final From from, final StepType[][] walk, @Opt @Name final BooleanFunction forward, @Opt @Name final BooleanFunction rotations, final To to, @Opt final Then then) {
        Moves moves = null;
        switch (moveType) {
            case Leap -> moves = new Leap(from, walk, forward, rotations, to, then);
        }
        if (moves == null) {
            throw new IllegalArgumentException("Move(): A MoveLeapType is not implemented.");
        }
        moves.setDecision();
        return moves;
    }
    
    public static Moves construct(final MoveHopType moveType, @Opt final From from, @Opt final Direction directions, @Opt final Between between, final To to, @Opt @Name final Boolean stack, @Opt final Then then) {
        Moves moves = null;
        switch (moveType) {
            case Hop -> moves = new Hop(from, directions, between, to, stack, then);
        }
        if (moves == null) {
            throw new IllegalArgumentException("Move(): A MoveHopType is not implemented.");
        }
        moves.setDecision();
        return moves;
    }
    
    public static Moves construct(final From from, final To to, @Opt @Name final IntFunction count, @Opt @Name final BooleanFunction copy, @Opt @Name final Boolean stack, @Opt final RoleType mover, @Opt final Then then) {
        final Moves moves = new FromTo(from, to, count, copy, stack, mover, then);
        moves.setDecision();
        return moves;
    }
    
    public static Moves construct(final MoveBetType moveType, @Or final Player who, @Or final RoleType role, final RangeFunction range, @Opt final Then then) {
        Moves moves = null;
        switch (moveType) {
            case Bet -> moves = new Bet(who, role, range, then);
        }
        if (moves == null) {
            throw new IllegalArgumentException("Move(): A MoveBetType is not implemented.");
        }
        moves.setDecision();
        return moves;
    }
    
    public static Moves construct(final MoveSiteType moveType, @Opt final Piece what, final To to, @Opt @Name final IntFunction count, @Opt @Name final Boolean stack, @Opt final Then then) {
        Moves moves = null;
        switch (moveType) {
            case Add -> moves = new Add(what, to, count, stack, then);
            case Claim -> moves = new Claim(what, to, then);
        }
        if (moves == null) {
            throw new IllegalArgumentException("Move(): A MoveAddType is not implemented.");
        }
        moves.setDecision();
        return moves;
    }
    
    private Move() {
        super(null);
    }
    
    @Override
    public Moves eval(final Context context) {
        throw new UnsupportedOperationException("Move.eval(): Should never be called directly.");
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
        throw new UnsupportedOperationException("Move.canMoveTo(): Should never be called directly.");
    }
}
