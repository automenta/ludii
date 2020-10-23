// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.count;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.count.component.CountPieces;
import game.functions.ints.count.component.CountPips;
import game.functions.ints.count.groups.CountGroups;
import game.functions.ints.count.liberties.CountLiberties;
import game.functions.ints.count.simple.*;
import game.functions.ints.count.site.*;
import game.functions.ints.count.steps.CountSteps;
import game.functions.ints.count.stepsOnTrack.CountStepsOnTrack;
import game.functions.region.RegionFunction;
import game.rules.play.moves.nonDecision.effect.Step;
import game.types.board.RelationType;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.directions.Direction;
import game.util.moves.Player;
import util.Context;

public final class Count extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    
    public static IntFunction construct(final CountSimpleType countType, @Opt final SiteType type) {
        switch (countType) {
            case Active: {
                return new CountActive();
            }
            case Cells: {
                return new CountCells();
            }
            case Columns: {
                return new CountColumns(type);
            }
            case Edges: {
                return new CountEdges();
            }
            case Moves: {
                return new CountMoves();
            }
            case MovesThisTurn: {
                return new CountMovesThisTurn();
            }
            case Phases: {
                return new CountPhases();
            }
            case Players: {
                return new CountPlayers();
            }
            case Rows: {
                return new CountRows(type);
            }
            case Trials: {
                return new CountTrials();
            }
            case Turns: {
                return new CountTurns();
            }
            case Vertices: {
                return new CountVertices();
            }
            default: {
                throw new IllegalArgumentException("Count(): A CountSimpleType is not implemented.");
            }
        }
    }
    
    public static IntFunction construct(@Opt final CountSiteType countType, @Opt final SiteType type, @Opt @Or @Name final RegionFunction in, @Opt @Or @Name final IntFunction at, @Opt @Or final String name) {
        int numNonNull = 0;
        if (in != null) {
            ++numNonNull;
        }
        if (at != null) {
            ++numNonNull;
        }
        if (name != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Count(): With CountSiteType zero or one 'in', 'at' or 'name' parameters must be non-null.");
        }
        if (countType == null) {
            return new CountNumber(type, in, at);
        }
        switch (countType) {
            case Adjacent: {
                return new CountAdjacent(type, in, at);
            }
            case Diagonal: {
                return new CountDiagonal(type, in, at);
            }
            case Neighbours: {
                return new CountNeighbours(type, in, at);
            }
            case Off: {
                return new CountOff(type, in, at);
            }
            case Orthogonal: {
                return new CountOrthogonal(type, in, at);
            }
            case Sites: {
                return new CountSites(in, at, name);
            }
            default: {
                throw new IllegalArgumentException("Count(): A CountSiteType is not implemented.");
            }
        }
    }
    
    public static IntFunction construct(final CountComponentType countType, @Opt final SiteType type, @Opt @Or final RoleType role, @Opt @Or @Name final IntFunction of, @Opt final String name, @Opt @Name final RegionFunction in) {
        int numNonNull = 0;
        if (role != null) {
            ++numNonNull;
        }
        if (of != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Count(): With CountComponentType zero or one 'role' or 'of' parameters must be non-null.");
        }
        switch (countType) {
            case Pieces: {
                return new CountPieces(type, role, of, name, in);
            }
            case Pips: {
                return new CountPips(role, of);
            }
            default: {
                throw new IllegalArgumentException("Count(): A CountComponentType is not implemented.");
            }
        }
    }
    
    public static IntFunction construct(final CountGroupsType countType, @Opt final SiteType type, @Opt final Direction directions, @Opt @Or final RoleType role, @Opt @Or @Name final IntFunction of, @Opt @Or @Name final BooleanFunction If, @Opt @Name final IntFunction min) {
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
            throw new IllegalArgumentException("Count(): With CountGroupsType zero or one 'role' or 'of' or 'If' parameters must be non-null.");
        }
        switch (countType) {
            case Groups: {
                return new CountGroups(type, directions, role, of, If, min);
            }
            default: {
                throw new IllegalArgumentException("Count(): A CountGroupsType is not implemented.");
            }
        }
    }
    
    public static IntFunction construct(final CountLibertiesType countType, @Opt final SiteType type, @Opt @Name final IntFunction at, @Opt final Direction directions, @Opt @Name final BooleanFunction If) {
        switch (countType) {
            case Liberties: {
                return new CountLiberties(type, at, directions, If);
            }
            default: {
                throw new IllegalArgumentException("Count(): A CountLibertiesType is not implemented.");
            }
        }
    }
    
    public static IntFunction construct(final CountStepsType countType, @Opt final SiteType type, @Opt final RelationType relation, @Opt final Step stepMove, final IntFunction site1, final IntFunction site2) {
        switch (countType) {
            case Steps: {
                return new CountSteps(type, relation, stepMove, site1, site2);
            }
            default: {
                throw new IllegalArgumentException("Count(): A CountStepsType is not implemented.");
            }
        }
    }
    
    public static IntFunction construct(final CountStepsOnTrackType countType, @Opt @Or final RoleType role, @Opt @Or final Player player, @Opt @Or final String name, @Opt final IntFunction site1, @Opt final IntFunction site2) {
        switch (countType) {
            case StepsOnTrack: {
                return new CountStepsOnTrack(role, player, name, site1, site2);
            }
            default: {
                throw new IllegalArgumentException("Count(): A CountStepsOnTrackType is not implemented.");
            }
        }
    }
    
    private Count() {
    }
    
    @Override
    public int eval(final Context context) {
        throw new UnsupportedOperationException("Count.eval(): Should never be called directly.");
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
