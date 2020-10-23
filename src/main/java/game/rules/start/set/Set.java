// 
// Decompiled by Procyon v0.5.36
// 

package game.rules.start.set;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.rules.Rule;
import game.rules.start.StartRule;
import game.rules.start.set.player.SetAmount;
import game.rules.start.set.player.SetScore;
import game.rules.start.set.players.SetTeam;
import game.rules.start.set.simple.SetAllInvisible;
import game.rules.start.set.sites.SetCost;
import game.rules.start.set.sites.SetCount;
import game.rules.start.set.sites.SetPhase;
import game.rules.start.set.sites.SetSite;
import game.types.board.SiteType;
import game.types.play.RoleType;
import util.Context;

public final class Set extends StartRule
{
    private static final long serialVersionUID = 1L;
    
    public static Rule construct(final RoleType role, @Opt final SiteType type, @Opt final IntFunction loc, @Opt @Name final String coord, @Opt @Name final RoleType[] invisibleTo, @Opt @Name final RoleType[] maskedTo) {
        return new SetSite(role, type, loc, coord, invisibleTo, maskedTo);
    }
    
    public static Rule construct(final RoleType role, @Opt final SiteType type, @Opt final IntFunction[] locs, @Opt final RegionFunction region, @Opt final String[] coords, @Opt @Name final RoleType[] invisibleTo) {
        return new SetSite(role, type, locs, region, coords, invisibleTo);
    }
    
    public static Rule construct(final SetStartSitesType startType, final Integer value, @Opt final SiteType type, @Or @Name final IntFunction at, @Or @Name final RegionFunction to) {
        int numNonNull = 0;
        if (to != null) {
            ++numNonNull;
        }
        if (at != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Set(): With SetStartSitesType Exactly one region or site parameter must be non-null.");
        }
        switch (startType) {
            case Count -> {
                return new SetCount(value, type, at, to);
            }
            case Cost -> {
                return new SetCost(value, type, at, to);
            }
            case Phase -> {
                return new SetPhase(value, type, at, to);
            }
            default -> {
                throw new IllegalArgumentException("Set(): A SetStartSitesType is not implemented.");
            }
        }
    }
    
    public static Rule construct(final SetStartPlayerType startType, @Opt final RoleType role, final IntFunction value) {
        switch (startType) {
            case Amount -> {
                return new SetAmount(role, value);
            }
            case Score -> {
                return new SetScore(role, value);
            }
            default -> {
                throw new IllegalArgumentException("Set(): A SetStartPlayerType is not implemented.");
            }
        }
    }
    
    public static Rule construct(final SetStartPlayersType startType, final IntFunction value, final RoleType[] roles) {
        switch (startType) {
            case Team -> {
                return new SetTeam(value, roles);
            }
            default -> {
                throw new IllegalArgumentException("Set(): A SetStartPlayersType is not implemented.");
            }
        }
    }
    
    public static Rule construct(final SetStartGraphType startType, @Opt final SiteType type) {
        switch (startType) {
            case AllInvisible -> {
                return new SetAllInvisible(type);
            }
            default -> {
                throw new IllegalArgumentException("Set(): A SetStartGraphType is not implemented.");
            }
        }
    }
    
    private Set() {
    }
    
    @Override
    public void eval(final Context context) {
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
}
