// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.loop;

import annotations.*;
import game.Game;
import game.equipment.component.Component;
import game.equipment.component.tile.Path;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.ints.last.LastTo;
import game.functions.ints.state.Mover;
import game.functions.region.RegionFunction;
import game.functions.region.sites.simple.SitesLastTo;
import game.types.board.RelationType;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.directions.RelativeDirection;
import game.util.equipment.Region;
import game.util.graph.Radial;
import game.util.graph.Step;
import gnu.trove.list.array.TIntArrayList;
import topology.Cell;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.locations.FullLocation;
import util.locations.Location;
import util.state.containerState.ContainerState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Hide
public final class IsLoop extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction[] rolesArray;
    private final IntFunction startFn;
    private final RegionFunction regionStartFn;
    private SiteType type;
    private final DirectionsFunction dirnChoice;
    private final boolean tilePath;
    private final IntFunction colourFn;
    TIntArrayList outerIndices;
    
    public IsLoop(@Opt final SiteType type, @Or @Opt @Name final RoleType surround, @Or @Opt final RoleType[] surroundList, @Opt final Direction directions, @Opt final IntFunction colour, @Or2 @Opt final IntFunction start, @Or2 @Opt final RegionFunction regionStart, @Opt @Name final Boolean path) {
        int numNonNull = 0;
        if (surround != null) {
            ++numNonNull;
        }
        if (surroundList != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Zero or one Or parameter can be non-null.");
        }
        int numNonNull2 = 0;
        if (start != null) {
            ++numNonNull2;
        }
        if (regionStart != null) {
            ++numNonNull2;
        }
        if (numNonNull2 > 1) {
            throw new IllegalArgumentException("Zero or one Or2 parameter can be non-null.");
        }
        this.colourFn = ((colour == null) ? new Mover() : colour);
        this.startFn = ((start == null) ? new LastTo(null) : start);
        this.regionStartFn = ((regionStart == null) ? ((start == null) ? new SitesLastTo() : null) : regionStart);
        this.tilePath = (path != null && path);
        if (surround != null) {
            this.rolesArray = new IntFunction[] { new Id(null, surround) };
        }
        else if (surroundList != null) {
            this.rolesArray = new IntFunction[surroundList.length];
            for (int i = 0; i < surroundList.length; ++i) {
                this.rolesArray[i] = new Id(null, surroundList[i]);
            }
        }
        else {
            this.rolesArray = null;
        }
        this.type = type;
        this.dirnChoice = ((directions != null) ? directions.directionsFunctions() : new Directions(AbsoluteDirection.Adjacent, null));
    }
    
    @Override
    public boolean eval(final Context context) {
        final int from = this.startFn.eval(context);
        if (from < 0) {
            return false;
        }
        final Topology topology = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        if (from >= topology.getGraphElements(realType).size()) {
            return false;
        }
        final ContainerState cs = context.containerState(0);
        final int what = cs.what(from, realType);
        if (what <= 0) {
            return false;
        }
        if (context.components()[what].isTile() && this.tilePath) {
            return this.evalTilePath(context);
        }
        final TIntArrayList ownersOfEnclosedSite = (this.rolesArray == null) ? null : new TIntArrayList();
        if (this.rolesArray != null) {
            for (IntFunction intFunction : this.rolesArray) {
                ownersOfEnclosedSite.add(intFunction.eval(context));
            }
        }
        final TIntArrayList aroundSites = new TIntArrayList();
        final TopologyElement startElement = topology.getGraphElements(realType).get(from);
        final List<AbsoluteDirection> directionsFromStart = new Directions(AbsoluteDirection.Adjacent, null).convertToAbsolute(realType, startElement, null, null, null, context);
        for (final AbsoluteDirection direction : directionsFromStart) {
            final List<Step> steps = topology.trajectories().steps(realType, startElement.index(), realType, direction);
            for (final Step step : steps) {
                final int to = step.to().id();
                final int whatTo = cs.what(to, realType);
                if (ownersOfEnclosedSite != null) {
                    final int whoTo = cs.who(to, realType);
                    if (!ownersOfEnclosedSite.contains(whoTo) || this.outerIndices.contains(to)) {
                        continue;
                    }
                }
                else {
                    if (whatTo == what || this.outerIndices.contains(to)) {
                        continue;
                    }
                }
                aroundSites.add(to);
            }
        }
        final TIntArrayList sitesToCheckInPreviousGroup = new TIntArrayList();
        for (int indexSite = aroundSites.size() - 1; indexSite >= 0; --indexSite) {
            final int origin = aroundSites.get(indexSite);
            if (!sitesToCheckInPreviousGroup.contains(origin)) {
                final TIntArrayList groupSites = new TIntArrayList();
                groupSites.add(origin);
                boolean continueSearch = true;
                final TIntArrayList sitesExplored = new TIntArrayList();
                int j = 0;
                while (sitesExplored.size() != groupSites.size()) {
                    final int site = groupSites.get(j);
                    final TopologyElement siteElement = topology.getGraphElements(realType).get(site);
                    final List<AbsoluteDirection> directions = new Directions(AbsoluteDirection.Orthogonal, null).convertToAbsolute(realType, siteElement, null, null, null, context);
                    for (final AbsoluteDirection direction2 : directions) {
                        final List<Radial> radials = topology.trajectories().radials(this.type, siteElement.index(), direction2);
                        for (final Radial radial : radials) {
                            for (int toIdx = 1; toIdx < radial.steps().length; ++toIdx) {
                                final int to2 = radial.steps()[toIdx].id();
                                if (!groupSites.contains(to2)) {
                                    if (what == cs.what(to2, realType)) {
                                        break;
                                    }
                                    if (ownersOfEnclosedSite != null) {
                                        final int whoTo2 = cs.who(to2, realType);
                                        if (ownersOfEnclosedSite.contains(whoTo2) && !this.outerIndices.contains(to2)) {
                                            groupSites.add(to2);
                                            sitesToCheckInPreviousGroup.add(to2);
                                        }
                                    }
                                    else {
                                        groupSites.add(to2);
                                        sitesToCheckInPreviousGroup.add(to2);
                                    }
                                    if (this.outerIndices.contains(to2)) {
                                        continueSearch = false;
                                        break;
                                    }
                                }
                            }
                            if (!continueSearch) {
                                break;
                            }
                        }
                        if (!continueSearch) {
                            break;
                        }
                    }
                    if (!continueSearch) {
                        break;
                    }
                    sitesExplored.add(site);
                    ++j;
                }
                if (continueSearch) {
                    final TIntArrayList loop = new TIntArrayList();
                    for (int indexGroup = 0; indexGroup < groupSites.size(); ++indexGroup) {
                        final int siteGroup = groupSites.get(indexGroup);
                        final TopologyElement element = topology.getGraphElements(realType).get(siteGroup);
                        final List<AbsoluteDirection> directionsElement = new Directions(AbsoluteDirection.Orthogonal, null).convertToAbsolute(realType, element, null, null, null, context);
                        for (final AbsoluteDirection direction3 : directionsElement) {
                            final List<Step> steps2 = topology.trajectories().steps(realType, element.index(), realType, direction3);
                            for (final Step step2 : steps2) {
                                final int to3 = step2.to().id();
                                if (!groupSites.contains(to3) && !loop.contains(to3)) {
                                    loop.add(to3);
                                }
                            }
                        }
                    }
                    boolean loopFound = false;
                    int previousIndice = 0;
                    int indexSiteLoop = 0;
                    final TIntArrayList exploredLoop = new TIntArrayList();
                    while (!loopFound) {
                        if (loop.isEmpty()) {
                            break;
                        }
                        final int siteLoop = loop.get(indexSiteLoop);
                        final int whatElement = cs.what(siteLoop, realType);
                        if (whatElement != what) {
                            loop.remove(siteLoop);
                            indexSiteLoop = previousIndice;
                        }
                        else {
                            final TopologyElement element2 = topology.getGraphElements(realType).get(siteLoop);
                            final List<AbsoluteDirection> directionsElement2 = this.dirnChoice.convertToAbsolute(realType, element2, null, null, null, context);
                            int newSite = -1;
                            for (final AbsoluteDirection direction4 : directionsElement2) {
                                final List<Step> steps3 = topology.trajectories().steps(realType, element2.index(), realType, direction4);
                                for (final Step step3 : steps3) {
                                    final int to4 = step3.to().id();
                                    final int whatTo2 = cs.what(to4, realType);
                                    if (loop.contains(to4) && whatTo2 == what) {
                                        newSite = to4;
                                        break;
                                    }
                                }
                                if (newSite != -1) {
                                    break;
                                }
                            }
                            if (newSite == -1) {
                                loop.remove(siteLoop);
                                exploredLoop.remove(siteLoop);
                                indexSiteLoop = previousIndice;
                            }
                            else {
                                exploredLoop.add(siteLoop);
                                if (exploredLoop.size() == loop.size()) {
                                    loopFound = true;
                                    break;
                                }
                                previousIndice = indexSiteLoop;
                                indexSiteLoop = loop.indexOf(newSite);
                            }
                        }
                    }
                    if (loopFound) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean evalTilePath(final Context context) {
        int[] regionToCheck;
        if (this.regionStartFn != null) {
            final Region region = this.regionStartFn.eval(context);
            regionToCheck = region.sites();
        }
        else {
            regionToCheck = new int[] { this.startFn.eval(context) };
        }
        final DirectionsFunction directionFunction = new Directions(RelativeDirection.Forward, null, RelationType.Orthogonal, null);
        for (final int from : regionToCheck) {
            final int colourLoop = this.colourFn.eval(context);
            final int cid = context.containerId()[from];
            final Topology graph = context.topology();
            final ContainerState cs = context.containerState(cid);
            final int ratioAdjOrtho = context.topology().numEdges();
            final TIntArrayList tileConnected = new TIntArrayList();
            final TIntArrayList originTileConnected = new TIntArrayList();
            tileConnected.add(from);
            originTileConnected.add(from);
            for (int index = 0; index < tileConnected.size(); ++index) {
                final int site = tileConnected.getQuick(index);
                final Cell cell = graph.cells().get(site);
                final int what = cs.what(site, SiteType.Cell);
                final Component component = context.components()[what];
                final int rotation = cs.rotation(site, SiteType.Cell) * 2 / ratioAdjOrtho;
                final Path[] paths = Arrays.copyOf(component.paths(), component.paths().length);
                for (final Path path : paths) {
                    if (path.colour() == colourLoop) {
                        final List<AbsoluteDirection> directionsStep1 = directionFunction.convertToAbsolute(SiteType.Cell, cell, null, null, path.side1(rotation, graph.numEdges()), context);
                        final AbsoluteDirection directionSide1 = directionsStep1.get(0);
                        final List<Step> stepsSide1 = graph.trajectories().steps(SiteType.Cell, cell.index(), SiteType.Cell, directionSide1);
                        if (!stepsSide1.isEmpty()) {
                            final int site1Connected = stepsSide1.get(0).to().id();
                            if (originTileConnected.getQuick(index) != site1Connected && site1Connected == from) {
                                return true;
                            }
                            final int whatSide1 = cs.whatCell(site1Connected);
                            if (originTileConnected.getQuick(index) != site1Connected && whatSide1 != 0 && context.components()[whatSide1].isTile()) {
                                tileConnected.add(site1Connected);
                                originTileConnected.add(site);
                            }
                        }
                        final List<AbsoluteDirection> directionsSide2 = directionFunction.convertToAbsolute(SiteType.Cell, cell, null, null, path.side2(rotation, graph.numEdges()), context);
                        final AbsoluteDirection directionSide2 = directionsSide2.get(0);
                        final List<Step> stepsSide2 = graph.trajectories().steps(SiteType.Cell, cell.index(), SiteType.Cell, directionSide2);
                        if (!stepsSide2.isEmpty()) {
                            final int site2Connected = stepsSide2.get(0).to().id();
                            if (originTileConnected.getQuick(index) != site2Connected && site2Connected == from) {
                                return true;
                            }
                            final int whatSide2 = cs.whatCell(site2Connected);
                            if (originTileConnected.getQuick(index) != site2Connected && whatSide2 != 0 && context.components()[whatSide2].isTile()) {
                                tileConnected.add(site2Connected);
                                originTileConnected.add(site);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        String str = "";
        str += "IsLoop()";
        return str;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        gameFlags |= SiteType.stateFlags(this.type);
        if (this.colourFn != null) {
            gameFlags |= this.colourFn.gameFlags(game);
        }
        if (this.regionStartFn != null) {
            gameFlags |= this.regionStartFn.gameFlags(game);
        }
        if (this.startFn != null) {
            gameFlags |= this.startFn.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        final List<TopologyElement> outerElements = game.board().topology().outer(this.type);
        this.outerIndices = new TIntArrayList();
        for (final TopologyElement element : outerElements) {
            this.outerIndices.add(element.index());
        }
        if (this.colourFn != null) {
            this.colourFn.preprocess(game);
        }
        if (this.rolesArray != null) {
            for (final IntFunction role : this.rolesArray) {
                role.preprocess(game);
            }
        }
        if (this.startFn != null) {
            this.startFn.preprocess(game);
        }
        if (this.regionStartFn != null) {
            this.regionStartFn.preprocess(game);
        }
    }
    
    @Override
    public List<Location> satisfyingSites(final Context context) {
        if (!this.eval(context)) {
            return new ArrayList<>();
        }
        final List<Location> winningSites = new ArrayList<>();
        final int from = this.startFn.eval(context);
        if (from < 0) {
            return new ArrayList<>();
        }
        final Topology topology = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        if (from >= topology.getGraphElements(realType).size()) {
            return new ArrayList<>();
        }
        final ContainerState cs = context.containerState(0);
        final int what = cs.what(from, realType);
        if (what <= 0) {
            return new ArrayList<>();
        }
        if (context.components()[what].isTile() && this.tilePath) {
            return new ArrayList<>();
        }
        final TIntArrayList ownersOfEnclosedSite = (this.rolesArray == null) ? null : new TIntArrayList();
        if (this.rolesArray != null) {
            for (IntFunction intFunction : this.rolesArray) {
                ownersOfEnclosedSite.add(intFunction.eval(context));
            }
        }
        final TIntArrayList aroundSites = new TIntArrayList();
        final TopologyElement startElement = topology.getGraphElements(realType).get(from);
        final List<AbsoluteDirection> directionsFromStart = new Directions(AbsoluteDirection.Adjacent, null).convertToAbsolute(realType, startElement, null, null, null, context);
        for (final AbsoluteDirection direction : directionsFromStart) {
            final List<Step> steps = topology.trajectories().steps(realType, startElement.index(), realType, direction);
            for (final Step step : steps) {
                final int to = step.to().id();
                final int whatTo = cs.what(to, realType);
                if (ownersOfEnclosedSite != null) {
                    final int whoTo = cs.who(to, realType);
                    if (!ownersOfEnclosedSite.contains(whoTo) || this.outerIndices.contains(to)) {
                        continue;
                    }
                }
                else {
                    if (whatTo == what || this.outerIndices.contains(to)) {
                        continue;
                    }
                }
                aroundSites.add(to);
            }
        }
        final TIntArrayList sitesToCheckInPreviousGroup = new TIntArrayList();
        for (int indexSite = aroundSites.size() - 1; indexSite >= 0; --indexSite) {
            final int origin = aroundSites.get(indexSite);
            if (!sitesToCheckInPreviousGroup.contains(origin)) {
                final TIntArrayList groupSites = new TIntArrayList();
                groupSites.add(origin);
                boolean continueSearch = true;
                final TIntArrayList sitesExplored = new TIntArrayList();
                int j = 0;
                while (sitesExplored.size() != groupSites.size()) {
                    final int site = groupSites.get(j);
                    final TopologyElement siteElement = topology.getGraphElements(realType).get(site);
                    final List<AbsoluteDirection> directions = new Directions(AbsoluteDirection.Orthogonal, null).convertToAbsolute(realType, siteElement, null, null, null, context);
                    for (final AbsoluteDirection direction2 : directions) {
                        final List<Radial> radials = topology.trajectories().radials(this.type, siteElement.index(), direction2);
                        for (final Radial radial : radials) {
                            for (int toIdx = 1; toIdx < radial.steps().length; ++toIdx) {
                                final int to2 = radial.steps()[toIdx].id();
                                if (!groupSites.contains(to2)) {
                                    if (what == cs.what(to2, realType)) {
                                        break;
                                    }
                                    if (ownersOfEnclosedSite != null) {
                                        final int whoTo2 = cs.who(to2, realType);
                                        if (ownersOfEnclosedSite.contains(whoTo2) && !this.outerIndices.contains(to2)) {
                                            groupSites.add(to2);
                                            sitesToCheckInPreviousGroup.add(to2);
                                        }
                                    }
                                    else {
                                        groupSites.add(to2);
                                        sitesToCheckInPreviousGroup.add(to2);
                                    }
                                    if (this.outerIndices.contains(to2)) {
                                        continueSearch = false;
                                        break;
                                    }
                                }
                            }
                            if (!continueSearch) {
                                break;
                            }
                        }
                        if (!continueSearch) {
                            break;
                        }
                    }
                    if (!continueSearch) {
                        break;
                    }
                    sitesExplored.add(site);
                    ++j;
                }
                if (continueSearch) {
                    final TIntArrayList loop = new TIntArrayList();
                    for (int indexGroup = 0; indexGroup < groupSites.size(); ++indexGroup) {
                        final int siteGroup = groupSites.get(indexGroup);
                        final TopologyElement element = topology.getGraphElements(realType).get(siteGroup);
                        final List<AbsoluteDirection> directionsElement = new Directions(AbsoluteDirection.Orthogonal, null).convertToAbsolute(realType, element, null, null, null, context);
                        for (final AbsoluteDirection direction3 : directionsElement) {
                            final List<Step> steps2 = topology.trajectories().steps(realType, element.index(), realType, direction3);
                            for (final Step step2 : steps2) {
                                final int to3 = step2.to().id();
                                if (!groupSites.contains(to3) && !loop.contains(to3)) {
                                    loop.add(to3);
                                }
                            }
                        }
                    }
                    for (int indexWin = 0; indexWin < loop.size(); ++indexWin) {
                        winningSites.add(new FullLocation(loop.get(indexWin), 0, realType));
                    }
                    return winningSites;
                }
            }
        }
        return new ArrayList<>();
    }
}
