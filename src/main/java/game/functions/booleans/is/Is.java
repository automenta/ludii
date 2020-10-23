// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import annotations.Or2;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.booleans.BooleanFunction;
import game.functions.booleans.is.component.IsThreatened;
import game.functions.booleans.is.component.IsWithin;
import game.functions.booleans.is.connect.IsBlocked;
import game.functions.booleans.is.connect.IsConnected;
import game.functions.booleans.is.edge.IsCrossing;
import game.functions.booleans.is.graph.IsLastFrom;
import game.functions.booleans.is.graph.IsLastTo;
import game.functions.booleans.is.in.IsIn;
import game.functions.booleans.is.indexPlayer.IsInvisible;
import game.functions.booleans.is.indexPlayer.IsMasked;
import game.functions.booleans.is.indexPlayer.IsVisible;
import game.functions.booleans.is.integer.*;
import game.functions.booleans.is.line.IsLine;
import game.functions.booleans.is.loop.IsLoop;
import game.functions.booleans.is.path.IsPath;
import game.functions.booleans.is.pattern.IsPattern;
import game.functions.booleans.is.player.*;
import game.functions.booleans.is.regularGraph.IsRegularGraph;
import game.functions.booleans.is.related.IsRelated;
import game.functions.booleans.is.repeat.IsRepeat;
import game.functions.booleans.is.simple.IsCycle;
import game.functions.booleans.is.simple.IsFull;
import game.functions.booleans.is.simple.IsPending;
import game.functions.booleans.is.site.IsEmpty;
import game.functions.booleans.is.site.IsOccupied;
import game.functions.booleans.is.string.IsDecided;
import game.functions.booleans.is.string.IsProposed;
import game.functions.booleans.is.target.IsTarget;
import game.functions.booleans.is.tree.IsCaterpillarTree;
import game.functions.booleans.is.tree.IsSpanningTree;
import game.functions.booleans.is.tree.IsTree;
import game.functions.booleans.is.tree.IsTreeCentre;
import game.functions.booleans.is.triggered.IsTriggered;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.rules.play.moves.Moves;
import game.types.board.RegionTypeStatic;
import game.types.board.RelationType;
import game.types.board.SiteType;
import game.types.board.StepType;
import game.types.play.RepetitionType;
import game.types.play.RoleType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.moves.Player;
import util.Context;

public class Is extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    
    public static BooleanFunction construct(final IsRepeatType isType, @Opt final RepetitionType repetitionType) {
        switch (isType) {
            case Repeat: {
                return new IsRepeat(repetitionType);
            }
            default: {
                throw new IllegalArgumentException("Is(): An IsRepeatType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsPatternType isType, final StepType[] walk, @Opt final SiteType type, @Opt @Name final IntFunction from, @Or @Opt @Name final IntFunction what, @Or @Opt @Name final IntFunction[] whats) {
        int numNonNull = 0;
        if (what != null) {
            ++numNonNull;
        }
        if (whats != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Only one parameter between what and whats can be non-null.");
        }
        switch (isType) {
            case Pattern: {
                return new IsPattern(walk, type, from, what, whats);
            }
            default: {
                throw new IllegalArgumentException("Is(): An IsPatternType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsTreeType isType, @Or final Player who, @Or final RoleType role) {
        switch (isType) {
            case Tree: {
                return new IsTree(who, role);
            }
            case SpanningTree: {
                return new IsSpanningTree(who, role);
            }
            case CaterpillarTree: {
                return new IsCaterpillarTree(who, role);
            }
            case TreeCentre: {
                return new IsTreeCentre(who, role);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsTreeType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsRegularGraphType isType, @Or final Player who, @Or final RoleType role, @Opt @Or2 @Name final IntFunction k, @Opt @Or2 @Name final BooleanFunction odd, @Opt @Or2 @Name final BooleanFunction even) {
        int numNonNull1 = 0;
        if (who != null) {
            ++numNonNull1;
        }
        if (role != null) {
            ++numNonNull1;
        }
        if (numNonNull1 != 1) {
            throw new IllegalArgumentException("Is(): with IsRegularGraphType one of who or role has to be non-null.");
        }
        numNonNull1 = 0;
        if (k != null) {
            ++numNonNull1;
        }
        if (odd != null) {
            ++numNonNull1;
        }
        if (even != null) {
            ++numNonNull1;
        }
        if (numNonNull1 > 1) {
            throw new IllegalArgumentException("Is(): with IsRegularGraphType only one of k, odd, even has to be non-null.");
        }
        switch (isType) {
            case RegularGraph: {
                return new IsRegularGraph(who, role, k, odd, even);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsRegularGraphType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsPlayerType isType, @Or final IntFunction index, @Or final RoleType role) {
        int numNonNull = 0;
        if (index != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Is(): with IsPlayerType only one of index, role has to be non-null.");
        }
        switch (isType) {
            case Enemy: {
                return new IsEnemy(index, role);
            }
            case Friend: {
                return new IsFriend(index, role);
            }
            case Mover: {
                return new IsMover(index, role);
            }
            case Next: {
                return new IsNext(index, role);
            }
            case Prev: {
                return new IsPrev(index, role);
            }
            case Active: {
                return new IsActive(index, role);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsPlayerType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsTriggeredType isType, final String event, @Or final IntFunction index, @Or final RoleType role) {
        int numNonNull = 0;
        if (index != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Is(): with IsTriggeredType only one of index, role has to be non-null.");
        }
        switch (isType) {
            case Triggered: {
                return new IsTriggered(event, index, role);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsTriggeredType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsSimpleType isType) {
        switch (isType) {
            case Cycle: {
                return new IsCycle();
            }
            case Pending: {
                return new IsPending();
            }
            case Full: {
                return new IsFull();
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsSimpleType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsEdgeType isType, final IntFunction edge1, final IntFunction edge2) {
        switch (isType) {
            case Crossing: {
                return new IsCrossing(edge1, edge2);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsEdgeType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsStringType isType, final String string) {
        switch (isType) {
            case Decided: {
                return new IsDecided(string);
            }
            case Proposed: {
                return new IsProposed(string);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsStringType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsGraphType isType, final SiteType type) {
        switch (isType) {
            case LastFrom: {
                return new IsLastFrom(type);
            }
            case LastTo: {
                return new IsLastTo(type);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsGraphType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsIndexPlayerType isType, @Opt final SiteType type, final IntFunction locn, @Or final IntFunction indexPlayer, @Or final RoleType role) {
        switch (isType) {
            case Invisible: {
                return new IsInvisible(type, locn, indexPlayer, role);
            }
            case Masked: {
                return new IsMasked(type, locn, indexPlayer, role);
            }
            case Visible: {
                return new IsVisible(type, locn, indexPlayer, role);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsIndexPlayerType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsIntegerType isType, @Opt final IntFunction value) {
        switch (isType) {
            case Even: {
                return new IsEven(value);
            }
            case Odd: {
                return new IsOdd(value);
            }
            case Flat: {
                return new IsFlat(value);
            }
            case PipsMatch: {
                return new IsPipsMatch(value);
            }
            case SidesMatch: {
                return new IsSidesMatch(value);
            }
            case Visited: {
                return new IsVisited(value);
            }
            case AnyDie: {
                return new IsAnyDie(value);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsValueType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsComponentType isType, @Opt final IntFunction what, @Opt final SiteType type, @Opt @Or @Name final IntFunction at, @Opt @Or @Name final RegionFunction in, @Opt final Moves specificMoves) {
        int numNonNull = 0;
        if (at != null) {
            ++numNonNull;
        }
        if (in != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Is(): With IsComponentType only one 'site' or 'sites' parameter must be non-null.");
        }
        switch (isType) {
            case Threatened: {
                return new IsThreatened(what, type, at, in, specificMoves);
            }
            case Within: {
                return new IsWithin(what, type, at, in);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsComponentType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsRelationType isType, final RelationType relationType, @Opt final SiteType type, final IntFunction siteA, @Or final IntFunction siteB, @Or final RegionFunction region) {
        int numNonNull = 0;
        if (siteA != null) {
            ++numNonNull;
        }
        if (siteB != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Is(): With IsRelationType oly one siteB or region parameter can be non-null.");
        }
        switch (isType) {
            case Related: {
                return new IsRelated(relationType, type, siteA, siteB, region);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsRelationType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsTargetType isType, @Opt @Or final IntFunction containerIdFn, @Opt @Or final String containerName, final Integer[] configuration, @Opt @Or final Integer specificSite, @Opt @Or final Integer[] specificSites) {
        int numNonNull = 0;
        if (containerIdFn != null) {
            ++numNonNull;
        }
        if (containerName != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Is(): wtih IsTargetType eero or one containerId or containerName parameter must be non-null.");
        }
        int numNonNullA = 0;
        if (specificSite != null) {
            ++numNonNullA;
        }
        if (specificSites != null) {
            ++numNonNullA;
        }
        if (numNonNullA > 1) {
            throw new IllegalArgumentException("Is(): wtih IsTargetType zero or one specificSite or specificSites parameter must be non-null.");
        }
        switch (isType) {
            case Target: {
                return new IsTarget(containerIdFn, containerName, configuration, specificSite, specificSites);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsTargetType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsConnectType isType, @Opt final IntFunction number, @Opt final SiteType type, @Opt @Name final IntFunction at, @Opt final Direction directions, @Or final RegionFunction[] regions, @Or final RoleType role, @Or final RegionTypeStatic regionType) {
        int numNonNull = 0;
        if (regions != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (regionType != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Is(): wtih IsConnectType Exactly one regions, role or regionType parameter must be non-null.");
        }
        switch (isType) {
            case Blocked: {
                return new IsBlocked(type, number, directions, regions, role, regionType);
            }
            case Connected: {
                return new IsConnected(number, type, at, directions, regions, role, regionType);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsConnectType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsLineType isType, @Opt final SiteType type, final IntFunction length, @Opt final AbsoluteDirection dirn, @Or2 @Opt @Name final IntFunction through, @Or2 @Opt @Name final RegionFunction throughAny, @Or @Opt final RoleType who, @Or @Opt @Name final IntFunction what, @Or @Opt @Name final IntFunction[] whats, @Opt @Name final BooleanFunction exact, @Opt @Name final BooleanFunction If, @Opt @Name final BooleanFunction byLevel) {
        int numNonNull = 0;
        if (what != null) {
            ++numNonNull;
        }
        if (whats != null) {
            ++numNonNull;
        }
        if (who != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Is(): With IsLineType zero or one what, whats or who parameter can be non-null.");
        }
        int numNonNull2 = 0;
        if (through != null) {
            ++numNonNull2;
        }
        if (throughAny != null) {
            ++numNonNull2;
        }
        if (numNonNull2 > 1) {
            throw new IllegalArgumentException("Is(): With IsLineType zero or one through or throughAny parameter can be non-null.");
        }
        switch (isType) {
            case Line: {
                return new IsLine(type, length, dirn, through, throughAny, who, what, whats, exact, If, byLevel);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsLineType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsLoopType isType, @Opt final SiteType type, @Or @Opt @Name final RoleType surround, @Or @Opt final RoleType[] surroundList, @Opt final Direction directions, @Opt final IntFunction colour, @Or2 @Opt final IntFunction start, @Or2 @Opt final RegionFunction regionStart, @Opt @Name final Boolean path) {
        int numNonNull = 0;
        if (surround != null) {
            ++numNonNull;
        }
        if (surroundList != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Is(): With IsLoopType zero or one surround or surroundList parameter can be non-null.");
        }
        int numNonNull2 = 0;
        if (start != null) {
            ++numNonNull2;
        }
        if (regionStart != null) {
            ++numNonNull2;
        }
        if (numNonNull2 > 1) {
            throw new IllegalArgumentException("Is(): With IsLoopType zero or one start or regionStart parameter can be non-null.");
        }
        switch (isType) {
            case Loop: {
                return new IsLoop(type, surround, surroundList, directions, colour, start, regionStart, path);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsLoopType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsPathType isType, final SiteType type, @Or final Player who, @Or final RoleType role, @Or2 @Opt @Name final IntFunction length, @Or2 @Opt @Name final IntFunction maxLimit, @Opt @Name final Boolean closed) {
        int numNonNull = 0;
        if (who != null) {
            ++numNonNull;
        }
        if (role != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Is(): With IsPathType Only one who or role parameter must be non-null.");
        }
        int numNonNull2 = 0;
        if (length != null) {
            ++numNonNull2;
        }
        if (maxLimit != null) {
            ++numNonNull2;
        }
        if (numNonNull2 != 1) {
            throw new IllegalArgumentException("With IsPathType Only one length or range parameter must be non-null.");
        }
        switch (isType) {
            case Path: {
                return new IsPath(type, who, role, length, maxLimit, closed);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsPathType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsSiteType isType, @Opt final SiteType type, final IntFunction at) {
        switch (isType) {
            case Empty: {
                return new IsEmpty(type, at);
            }
            case Occupied: {
                return new IsOccupied(type, at);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsSiteType is not implemented.");
            }
        }
    }
    
    public static BooleanFunction construct(final IsInType isType, @Opt @Or final IntFunction site, @Opt @Or final IntFunction[] sites, final RegionFunction region) {
        int numNonNull = 0;
        if (site != null) {
            ++numNonNull;
        }
        if (sites != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Is(): With IsInType Only one site or sites parameter can be non-null.");
        }
        switch (isType) {
            case In: {
                return IsIn.construct(site, sites, region);
            }
            default: {
                throw new IllegalArgumentException("Is(): A IsInType is not implemented.");
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
