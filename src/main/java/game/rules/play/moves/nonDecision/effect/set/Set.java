// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.play.moves.nonDecision.effect.set;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.intArray.IntArrayFunction;
import game.functions.intArray.math.Difference;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.effect.Effect;
import game.rules.play.moves.nonDecision.effect.Then;
import game.rules.play.moves.nonDecision.effect.set.direction.SetDirection;
import game.rules.play.moves.nonDecision.effect.set.nextPlayer.SetNextPlayer;
import game.rules.play.moves.nonDecision.effect.set.pending.SetPending;
import game.rules.play.moves.nonDecision.effect.set.player.SetScore;
import game.rules.play.moves.nonDecision.effect.set.player.SetValue;
import game.rules.play.moves.nonDecision.effect.set.site.SetCount;
import game.rules.play.moves.nonDecision.effect.set.site.SetState;
import game.rules.play.moves.nonDecision.effect.set.sitePlayer.SetInvisible;
import game.rules.play.moves.nonDecision.effect.set.sitePlayer.SetMasked;
import game.rules.play.moves.nonDecision.effect.set.sitePlayer.SetVisible;
import game.rules.play.moves.nonDecision.effect.set.suit.SetTrumpSuit;
import game.rules.play.moves.nonDecision.effect.set.value.SetCounter;
import game.rules.play.moves.nonDecision.effect.set.value.SetPot;
import game.rules.play.moves.nonDecision.effect.set.value.SetVar;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.moves.Player;
import game.util.moves.To;
import util.Context;

public final class Set extends Effect
{
    private static final long serialVersionUID = 1L;
    
    public static Moves construct(final SetTrumpType setType, @Or final IntFunction suit, @Or final Difference suits, @Opt final Then then) {
        int numNonNull = 0;
        if (suit != null) {
            ++numNonNull;
        }
        if (suits != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Set(): With SetSuitType only one suit or suits parameter must be non-null.");
        }
        switch (setType) {
            case TrumpSuit -> {
                return new SetTrumpSuit(suit, suits, then);
            }
            default -> {
                throw new IllegalArgumentException("Set(): A SetSuitType is not implemented.");
            }
        }
    }
    
    public static Moves construct(final SetNextPlayerType setType, @Or final Player who, @Or final IntArrayFunction nextPlayers, @Opt final Then then) {
        int numNonNull = 0;
        if (who != null) {
            ++numNonNull;
        }
        if (nextPlayers != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Set(): With SetPlayerType only one who or nextPlayers parameter can be non-null.");
        }
        switch (setType) {
            case NextPlayer -> {
                return new SetNextPlayer(who, nextPlayers, then);
            }
            default -> {
                throw new IllegalArgumentException("Set(): A SetPlayerType is not implemented.");
            }
        }
    }
    
    public static Moves construct(final SetDirectionType setType, @Opt final To to, @Opt @Or final IntFunction[] directions, @Opt @Or final IntFunction direction, @Opt @Name final BooleanFunction previous, @Opt @Name final BooleanFunction next, @Opt final Then then) {
        int numNonNull = 0;
        if (directions != null) {
            ++numNonNull;
        }
        if (direction != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Set(): With SetDirectionType zero or one directions or direction parameter must be non-null.");
        }
        switch (setType) {
            case Direction -> {
                return new SetDirection(to, directions, direction, previous, next, then);
            }
            default -> {
                throw new IllegalArgumentException("Set(): A SetDirectionType is not implemented.");
            }
        }
    }
    
    public static Moves construct(final SetPlayerType setType, @Or final Player player, @Or final RoleType role, final IntFunction value, @Opt final Then then) {
        int numNonNull = 0;
        if (player != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Set(): With SetType Only one player or role parameter m be non-null.");
        }
        switch (setType) {
            case Value -> {
                return new SetValue(player, role, value, then);
            }
            case Score -> {
                return new SetScore(player, role, value, then);
            }
            default -> {
                throw new IllegalArgumentException("Set(): A SetType is not implemented.");
            }
        }
    }
    
    public static Moves construct(final SetPendingType setType, @Opt @Or final IntFunction value, @Opt @Or final RegionFunction region, @Opt final Then then) {
        switch (setType) {
            case Pending -> {
                return new SetPending(value, region, then);
            }
            default -> {
                throw new IllegalArgumentException("Set(): A SetPendingType is not implemented.");
            }
        }
    }
    
    public static Moves construct(final SetValueType setType, @Opt final IntFunction newValue, @Opt final Then then) {
        switch (setType) {
            case Counter -> {
                return new SetCounter(newValue, then);
            }
            case Var -> {
                return new SetVar(newValue, then);
            }
            case Pot -> {
                return new SetPot(newValue, then);
            }
            default -> {
                throw new IllegalArgumentException("Set(): A SetIntegerType is not implemented.");
            }
        }
    }
    
    public static Moves construct(final SetSiteType setType, @Opt final SiteType type, @Name final IntFunction at, final IntFunction value, @Opt final Then then) {
        switch (setType) {
            case Count -> {
                return new SetCount(type, at, value, then);
            }
            case State -> {
                return new SetState(type, at, value, then);
            }
            default -> {
                throw new IllegalArgumentException("Set(): A SetSiteType is not implemented.");
            }
        }
    }
    
    public static Moves construct(final SetRegionType setType, @Opt final SiteType type, @Or final IntFunction site, @Or final RegionFunction region, @Opt @Name final IntFunction level, @Opt @Or final Player who, @Opt @Or final RoleType role, @Opt @Name final BooleanFunction stack, @Opt final Then then) {
        int numNonNull = 0;
        if (site != null) {
            ++numNonNull;
        }
        if (region != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Set(): With SetRegionPlayerType one Site or Region parameter has to be non-null.");
        }
        int numNonNull2 = 0;
        if (who != null) {
            ++numNonNull2;
        }
        if (role != null) {
            ++numNonNull2;
        }
        if (numNonNull2 > 1) {
            throw new IllegalArgumentException("Set(): With SetRegionPlayerType one who or role parameter can be non-null.");
        }
        switch (setType) {
            case Masked -> {
                return new SetMasked(type, site, region, who, role, stack, then);
            }
            case Visible -> {
                return new SetVisible(type, site, level, who, role, then);
            }
            case Invisible -> {
                return new SetInvisible(type, site, region, who, role, stack, then);
            }
            default -> {
                throw new IllegalArgumentException("Set(): A SetRegionPlayerType is not implemented.");
            }
        }
    }
    
    private Set() {
        super(null);
    }
    
    @Override
    public Moves eval(final Context context) {
        throw new UnsupportedOperationException("Set.eval(): Should never be called directly.");
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
        throw new UnsupportedOperationException("Set.canMoveTo(): Should never be called directly.");
    }
}
