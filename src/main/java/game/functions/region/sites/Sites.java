// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import annotations.Or2;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.region.BaseRegionFunction;
import game.functions.region.RegionFunction;
import game.functions.region.sites.around.SitesAround;
import game.functions.region.sites.between.SitesBetween;
import game.functions.region.sites.context.SitesContext;
import game.functions.region.sites.coords.SitesCoords;
import game.functions.region.sites.crossing.SitesCrossing;
import game.functions.region.sites.custom.SitesCustom;
import game.functions.region.sites.direction.SitesDirection;
import game.functions.region.sites.distance.SitesDistance;
import game.functions.region.sites.edges.*;
import game.functions.region.sites.group.SitesGroup;
import game.functions.region.sites.incidents.SitesIncident;
import game.functions.region.sites.index.*;
import game.functions.region.sites.largePiece.SitesLargePiece;
import game.functions.region.sites.lineOfSight.LineOfSightType;
import game.functions.region.sites.lineOfSight.SitesLineOfSight;
import game.functions.region.sites.moves.SitesFrom;
import game.functions.region.sites.moves.SitesTo;
import game.functions.region.sites.occupied.SitesOccupied;
import game.functions.region.sites.piece.SitesStart;
import game.functions.region.sites.player.*;
import game.functions.region.sites.random.SitesRandom;
import game.functions.region.sites.side.SitesSide;
import game.functions.region.sites.simple.*;
import game.functions.region.sites.walk.SitesWalk;
import game.rules.play.moves.Moves;
import game.rules.play.moves.nonDecision.NonDecision;
import game.types.board.RegionTypeDynamic;
import game.types.board.RelationType;
import game.types.board.SiteType;
import game.types.board.StepType;
import game.types.play.RoleType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.CompassDirection;
import game.util.directions.Direction;
import game.util.equipment.Region;
import game.util.moves.Piece;
import game.util.moves.Player;
import main.StringRoutines;
import util.Context;

public final class Sites extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    
    public static RegionFunction construct() {
        return new SitesContext();
    }
    
    public static RegionFunction construct(final SitesBetweenType regionType, @Opt final Direction directions, @Opt final SiteType type, @Name final IntFunction from, @Opt @Name final BooleanFunction fromIncluded, @Name final IntFunction to, @Opt @Name final BooleanFunction toIncluded, @Opt @Name final BooleanFunction cond) {
        switch (regionType) {
            case Between -> {
                return new SitesBetween(directions, type, from, fromIncluded, to, toIncluded, cond);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesBetweenType is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(final SitesLargePieceType regionType, @Opt final SiteType type, @Name final IntFunction at) {
        switch (regionType) {
            case LargePiece -> {
                return new SitesLargePiece(type, at);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesLargePiece is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(final SitesRandomType regionType, @Opt final RegionFunction region, @Opt @Name final IntFunction num) {
        switch (regionType) {
            case Random -> {
                return new SitesRandom(region, num);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesRandomType is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(final SitesCrossingType regionType, @Name final IntFunction at, @Opt @Or final Player who, @Opt @Or final RoleType role) {
        int numNonNull = 0;
        if (who != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Sites(): With SitesCrossingType only one who or role parameter must be non-null.");
        }
        switch (regionType) {
            case Crossing -> {
                return new SitesCrossing(at, who, role);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesCrossingType is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(final SitesGroupType regionType, @Opt final SiteType type, @Name final IntFunction at, @Opt final Direction directions, @Opt @Name final BooleanFunction If) {
        switch (regionType) {
            case Group -> {
                return new SitesGroup(type, at, directions, If);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesGroupType is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(final SitesEdgeType regionType) {
        switch (regionType) {
            case Axial -> {
                return new SitesAxial();
            }
            case Horizontal -> {
                return new SitesHorizontal();
            }
            case Vertical -> {
                return new SitesVertical();
            }
            case Angled -> {
                return new SitesAngled();
            }
            case Slash -> {
                return new SitesSlash();
            }
            case Slosh -> {
                return new SitesSlosh();
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesEdgeType is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(final SitesSimpleType regionType, @Opt final SiteType elementType) {
        switch (regionType) {
            case Board -> {
                return new SitesBoard(elementType);
            }
            case Bottom -> {
                return new SitesBottom(elementType);
            }
            case Corners -> {
                return new SitesCorners(elementType);
            }
            case ConcaveCorners -> {
                return new SitesConcaveCorners(elementType);
            }
            case ConvexCorners -> {
                return new SitesConvexCorners(elementType);
            }
            case Hint -> {
                return new SitesHint();
            }
            case Inner -> {
                return new SitesInner(elementType);
            }
            case Left -> {
                return new SitesLeft(elementType);
            }
            case LineOfPlay -> {
                return new SitesLineOfPlay();
            }
            case Major -> {
                return new SitesMajor(elementType);
            }
            case Minor -> {
                return new SitesMinor(elementType);
            }
            case Outer -> {
                return new SitesOuter(elementType);
            }
            case Right -> {
                return new SitesRight(elementType);
            }
            case ToClear -> {
                return new SitesToClear();
            }
            case Top -> {
                return new SitesTop(elementType);
            }
            case Pending -> {
                return new SitesPending();
            }
            case Playable -> {
                return new SitesPlayable();
            }
            case LastTo -> {
                return new SitesLastTo();
            }
            case LastFrom -> {
                return new SitesLastFrom();
            }
            case Centre -> {
                return new SitesCentre(elementType);
            }
            case Perimeter -> {
                return new SitesPerimeter(elementType);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesSimpleType is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(@Opt final SiteType elementType, final String[] coords) {
        return new SitesCoords(elementType, coords);
    }
    
    public static RegionFunction construct(final SitesMoveType moveType, final Moves moves) {
        switch (moveType) {
            case From -> {
                return new SitesFrom(moves);
            }
            case To -> {
                return new SitesTo(moves);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesMoveType is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(final IntFunction[] sites) {
        return new SitesCustom(sites);
    }
    
    public static RegionFunction construct(@Opt final SiteType elementType, @Opt final IntFunction index, final StepType[][] possibleSteps, @Opt @Name final BooleanFunction rotations) {
        return new SitesWalk(elementType, index, possibleSteps, rotations);
    }
    
    public static RegionFunction construct(final SitesIndexType regionType, @Opt final SiteType elementType, @Opt final IntFunction index) {
        switch (regionType) {
            case Cell -> {
                return new SitesCell(elementType, index);
            }
            case Column -> {
                return new SitesColumn(elementType, index);
            }
            case Edge -> {
                return new SitesEdge(elementType, index);
            }
            case Phase -> {
                return new SitesPhase(elementType, index);
            }
            case Row -> {
                return new SitesRow(elementType, index);
            }
            case State -> {
                return new SitesState(elementType, index);
            }
            case Empty -> {
                return SitesEmpty.construct(elementType, index);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesIndexType is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(final SitesSideType regionType, @Opt final SiteType elementType, @Opt @Or final Player player, @Opt @Or final RoleType role, @Opt @Or final CompassDirection direction) {
        int numNonNull = 0;
        if (player != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (direction != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Sites(): A SitesDirectionType only one of index, role, direction can be non-null.");
        }
        switch (regionType) {
            case Side -> {
                return new SitesSide(elementType, player, role, direction);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesPlayerType is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(final SitesDistanceType regionType, @Opt final SiteType elementType, @Opt final RelationType relation, @Name final IntFunction from, final IntFunction distance) {
        switch (regionType) {
            case Distance -> {
                return new SitesDistance(elementType, relation, from, distance);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesDistanceType is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(@Opt @Or final Player player, @Opt @Or final RoleType role, @Opt final SiteType siteType, @Opt final String name) {
        if (StringRoutines.isCoordinate(name)) {
            int numNonNull = 0;
            if (player != null) {
                ++numNonNull;
            }
            if (role != null) {
                ++numNonNull;
            }
            if (numNonNull != 0) {
                throw new IllegalArgumentException("Sites(): index and role has to be null to specify a region of a single coordinate.");
            }
            return new SitesCoords(siteType, new String[] { name });
        }
        else {
            int numNonNull = 0;
            if (player != null) {
                ++numNonNull;
            }
            if (role != null) {
                ++numNonNull;
            }
            if (numNonNull > 1) {
                throw new IllegalArgumentException("Sites(): only one of index, role can be non-null.");
            }
            return new SitesEquipmentRegion(player, role, name);
        }
    }
    
    public static RegionFunction construct(final SitesPlayerType regionType, @Opt final SiteType elementType, @Opt @Or final Player pid, @Opt @Or final RoleType role, @Opt final NonDecision moves, @Opt final String name) {
        int numNonNull = 0;
        if (pid != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Sites(): A SitesPlayerType only one of pid, role can be non-null.");
        }
        switch (regionType) {
            case Hand -> {
                return new SitesHand(pid, role);
            }
            case Track -> {
                return new SitesTrack(pid, role, name);
            }
            case Winning -> {
                return new SitesWinning(pid, role, moves);
            }
            case Invisible -> {
                return new SitesInvisible(pid, role, elementType);
            }
            case Masked -> {
                return new SitesMasked(pid, role, elementType);
            }
            case Visible -> {
                return new SitesVisible(pid, role, elementType);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesPlayerType is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(final SitesPieceType regionType, final Piece pid) {
        switch (regionType) {
            case Start -> {
                return new SitesStart(pid);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesPlayerType is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(final SitesOccupiedType regionType, @Or @Name final Player by, @Or @Name final RoleType By, @Opt @Or2 @Name final IntFunction container, @Opt @Or2 @Name final String Container, @Opt @Or @Name final IntFunction component, @Opt @Or @Name final String Component, @Opt @Or @Name final String[] components, @Opt @Name final Boolean top, @Opt final SiteType type) {
        int numNonNull = 0;
        if (by != null) {
            ++numNonNull;
        }
        if (By != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Sites(): With SitesOccupiedType exactly one who or role parameter must be non-null.");
        }
        int numNonNull2 = 0;
        if (container != null) {
            ++numNonNull2;
        }
        if (Container != null) {
            ++numNonNull2;
        }
        if (numNonNull2 > 1) {
            throw new IllegalArgumentException("Sites(): With SitesOccupiedType zero or one container or Container parameter must be non-null.");
        }
        int numNonNull3 = 0;
        if (Component != null) {
            ++numNonNull3;
        }
        if (component != null) {
            ++numNonNull3;
        }
        if (components != null) {
            ++numNonNull3;
        }
        if (numNonNull3 > 1) {
            throw new IllegalArgumentException("Sites(): With SitesOccupiedType zero or one Component or component or components parameter must be non-null.");
        }
        switch (regionType) {
            case Occupied -> {
                return new SitesOccupied(by, By, container, Container, component, Component, components, top, type);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesOccupiedType is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(final SitesIncidentType regionType, final SiteType resultType, @Name final SiteType of, @Name final IntFunction at, @Opt @Or @Name final Player owner, @Opt @Or final RoleType roleOwner) {
        int numNonNull = 0;
        if (owner != null) {
            ++numNonNull;
        }
        if (roleOwner != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Sites(): With SitesIncidentType Zero or one owner or roleOwner parameter can be non-null.");
        }
        switch (regionType) {
            case Incident -> {
                return new SitesIncident(resultType, of, at, owner, roleOwner);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesIncidentType is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(final SitesAroundType regionType, @Opt final SiteType typeLoc, @Or final IntFunction where, @Or final RegionFunction regionWhere, @Opt final RegionTypeDynamic type, @Opt @Name final IntFunction distance, @Opt final AbsoluteDirection directions, @Opt @Name final BooleanFunction If, @Opt @Name final BooleanFunction includeSelf) {
        int numNonNull = 0;
        if (where != null) {
            ++numNonNull;
        }
        if (regionWhere != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Sites(): With SitesAroundType one where or regionWhere parameter must be non-null.");
        }
        switch (regionType) {
            case Around -> {
                return new SitesAround(typeLoc, where, regionWhere, type, distance, directions, If, includeSelf);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesAroundType is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(final SitesDirectionType regionType, @Or @Name final IntFunction from, @Or @Name final RegionFunction From, @Opt final Direction directions, @Opt @Name final BooleanFunction included, @Opt @Name final BooleanFunction stop, @Opt @Name final BooleanFunction stopIncluded, @Opt @Name final IntFunction distance, @Opt final SiteType type) {
        int numNonNull = 0;
        if (from != null) {
            ++numNonNull;
        }
        if (From != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Sites(): With SitesDirectionType one from or From parameter must be non-null.");
        }
        switch (regionType) {
            case Direction -> {
                return new SitesDirection(from, From, directions, included, stop, stopIncluded, distance, type);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesDirectionType is not implemented.");
            }
        }
    }
    
    public static RegionFunction construct(final SitesLineOfSightType regionType, @Opt final LineOfSightType typeLoS, @Opt final SiteType typeLoc, @Opt @Name final IntFunction at, @Opt final Direction directions) {
        switch (regionType) {
            case LineOfSight -> {
                return new SitesLineOfSight(typeLoS, typeLoc, at, directions);
            }
            default -> {
                throw new IllegalArgumentException("Sites(): A SitesAroundType is not implemented.");
            }
        }
    }
    
    private Sites() {
    }
    
    @Override
    public Region eval(final Context context) {
        return null;
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
