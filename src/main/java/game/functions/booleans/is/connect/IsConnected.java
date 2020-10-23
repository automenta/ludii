// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.connect;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import collections.ChunkSet;
import game.Game;
import game.equipment.other.Regions;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.ints.last.LastTo;
import game.functions.region.RegionFunction;
import game.types.board.RegionTypeStatic;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.Direction;
import game.util.graph.Step;
import gnu.trove.list.array.TIntArrayList;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.Trial;
import util.locations.FullLocation;
import util.locations.Location;
import util.state.containerState.ContainerState;

import java.util.ArrayList;
import java.util.List;

@Hide
public final class IsConnected extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction[] regionsToConnectFn;
    private final IntFunction roleFunc;
    private final Regions staticRegions;
    private final IntFunction number;
    private SiteType type;
    private final IntFunction startLocationFn;
    private final DirectionsFunction dirnChoice;
    private List<ChunkSet> precomputedSitesRegions;
    private List<List<ChunkSet>> precomputedOwnedRegions;
    
    public IsConnected(@Opt final IntFunction number, @Opt final SiteType type, @Opt @Name final IntFunction at, @Opt final Direction directions, @Or final RegionFunction[] regions, @Or final RoleType role, @Or final RegionTypeStatic regionType) {
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
            throw new IllegalArgumentException("Exactly one Or parameter must be non-null.");
        }
        this.startLocationFn = ((at == null) ? new LastTo(null) : at);
        this.regionsToConnectFn = regions;
        this.roleFunc = ((role == null) ? null : new Id(null, role));
        this.staticRegions = ((regionType == null) ? null : new Regions(null, null, null, null, null, regionType, null, null));
        this.type = type;
        this.number = number;
        this.dirnChoice = ((directions != null) ? directions.directionsFunctions() : new Directions(AbsoluteDirection.Adjacent, null));
    }
    
    @Override
    public boolean eval(final Context context) {
        final Game game = context.game();
        final int from = this.startLocationFn.eval(context);
        if (from < 0) {
            return false;
        }
        final Topology topology = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final List<? extends TopologyElement> realTypeElements = topology.getGraphElements(realType);
        if (from >= realTypeElements.size()) {
            return false;
        }
        final ContainerState cs = context.containerState(0);
        final int what = cs.what(from, realType);
        if (what <= 0) {
            return false;
        }
        final int playerRegion = (this.roleFunc == null) ? -1 : this.roleFunc.eval(context);
        List<ChunkSet> sitesRegions;
        if (this.precomputedSitesRegions == null) {
            final RegionFunction[] regionsToConnect = this.regionsToConnectFn;
            sitesRegions = new ArrayList<>();
            if (regionsToConnect != null) {
                for (final RegionFunction regionToConnect : regionsToConnect) {
                    sitesRegions.add(regionToConnect.eval(context).bitSet());
                }
            }
            else if (this.staticRegions != null) {
                final Integer[][] convertStaticRegionOnLocs;
                final Integer[][] regionSets = convertStaticRegionOnLocs = this.staticRegions.convertStaticRegionOnLocs(this.staticRegions.regionTypes()[0], context);
                for (final Integer[] region : convertStaticRegionOnLocs) {
                    final ChunkSet regionToAdd = new ChunkSet();
                    for (final Integer site : region) {
                        regionToAdd.set(site);
                    }
                    if (!regionToAdd.isEmpty()) {
                        sitesRegions.add(regionToAdd);
                    }
                }
            }
            else if (this.precomputedOwnedRegions != null) {
                sitesRegions.addAll(this.precomputedOwnedRegions.get(playerRegion));
            }
            else {
                for (final Regions region2 : game.equipment().regions()) {
                    if (region2.owner() == playerRegion) {
                        if (region2.region() != null) {
                            for (final RegionFunction r : region2.region()) {
                                sitesRegions.add(r.eval(context).bitSet());
                            }
                        }
                        else {
                            final ChunkSet bitSet = new ChunkSet();
                            for (final int site2 : region2.sites()) {
                                bitSet.set(site2);
                            }
                            sitesRegions.add(bitSet);
                        }
                    }
                }
            }
        }
        else {
            sitesRegions = new ArrayList<>(this.precomputedSitesRegions);
        }
        final int numRegionToConnect = (this.number != null) ? this.number.eval(context) : sitesRegions.size();
        final TIntArrayList groupSites = new TIntArrayList();
        if (cs.who(from, realType) == playerRegion || playerRegion == -1) {
            groupSites.add(from);
        }
        int numRegionConnected = 0;
        for (int j = sitesRegions.size() - 1; j >= 0; --j) {
            final ChunkSet regionToConnect2 = sitesRegions.get(j);
            if (regionToConnect2.get(from)) {
                ++numRegionConnected;
                sitesRegions.remove(j);
            }
            if (numRegionConnected == numRegionToConnect) {
                return true;
            }
        }
        if (!groupSites.isEmpty()) {
            final boolean[] inGroupSites = new boolean[realTypeElements.size()];
            inGroupSites[from] = true;
            for (int i = 0; i < groupSites.size(); ++i) {
                final int site3 = groupSites.getQuick(i);
                final TopologyElement siteElement = realTypeElements.get(site3);
                final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(realType, siteElement, null, null, null, context);
                for (final AbsoluteDirection direction : directions) {
                    final List<Step> steps = topology.trajectories().steps(realType, siteElement.index(), realType, direction);
                    for (final Step step : steps) {
                        final int to = step.to().id();
                        if (inGroupSites[to]) {
                            continue;
                        }
                        if (what != cs.what(to, realType)) {
                            continue;
                        }
                        for (int k = sitesRegions.size() - 1; k >= 0; --k) {
                            final ChunkSet regionToConnect3 = sitesRegions.get(k);
                            if (regionToConnect3.get(to)) {
                                ++numRegionConnected;
                                sitesRegions.remove(k);
                            }
                            if (numRegionConnected == numRegionToConnect) {
                                return true;
                            }
                        }
                        groupSites.add(to);
                        inGroupSites[to] = true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        String str = "";
        str = str + "IsConnected (" + this.regionsToConnectFn + ")";
        return str;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        if (this.regionsToConnectFn != null) {
            for (final RegionFunction regionFunc : this.regionsToConnectFn) {
                if (regionFunc != null) {
                    flags |= regionFunc.gameFlags(game);
                }
            }
        }
        flags |= SiteType.stateFlags(this.type);
        if (this.number != null) {
            flags |= this.number.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        if (this.regionsToConnectFn != null) {
            for (final RegionFunction regionFunc : this.regionsToConnectFn) {
                if (regionFunc != null) {
                    regionFunc.preprocess(game);
                }
            }
        }
        if (this.number != null) {
            this.number.preprocess(game);
        }
        if (this.regionsToConnectFn != null) {
            boolean allStatic = true;
            for (final RegionFunction regionFunc2 : this.regionsToConnectFn) {
                if (regionFunc2 != null && !regionFunc2.isStatic()) {
                    allStatic = false;
                    break;
                }
            }
            if (allStatic) {
                this.precomputedSitesRegions = new ArrayList<>();
                for (final RegionFunction regionToConnect : this.regionsToConnectFn) {
                    if (regionToConnect != null) {
                        this.precomputedSitesRegions.add(regionToConnect.eval(new Context(game, new Trial(game))).bitSet());
                    }
                }
            }
        }
        else if (this.staticRegions != null) {
            this.precomputedSitesRegions = new ArrayList<>();
            final Integer[][] convertStaticRegionOnLocs;
            final Integer[][] regionSets = convertStaticRegionOnLocs = this.staticRegions.convertStaticRegionOnLocs(this.staticRegions.regionTypes()[0], new Context(game, new Trial(game)));
            for (final Integer[] region : convertStaticRegionOnLocs) {
                final ChunkSet regionToAdd = new ChunkSet();
                for (final Integer site : region) {
                    regionToAdd.set(site);
                }
                if (!regionToAdd.isEmpty()) {
                    this.precomputedSitesRegions.add(regionToAdd);
                }
            }
        }
        else if (this.roleFunc != null) {
            boolean allStatic = true;
            for (final Regions region2 : game.equipment().regions()) {
                if (!region2.isStatic()) {
                    allStatic = false;
                    break;
                }
            }
            if (allStatic) {
                this.precomputedOwnedRegions = new ArrayList<>();
                for (int i = 0; i < game.players().size(); ++i) {
                    this.precomputedOwnedRegions.add(new ArrayList<>());
                }
                for (final Regions region2 : game.equipment().regions()) {
                    if (region2.region() != null) {
                        for (final RegionFunction r : region2.region()) {
                            final ChunkSet sitesToConnect = r.eval(new Context(game, new Trial(game))).bitSet();
                            this.precomputedOwnedRegions.get(region2.owner()).add(sitesToConnect);
                        }
                    }
                    else {
                        final ChunkSet sitesToConnect2 = new ChunkSet();
                        for (final int site2 : region2.sites()) {
                            sitesToConnect2.set(site2);
                        }
                        this.precomputedOwnedRegions.get(region2.owner()).add(sitesToConnect2);
                    }
                }
            }
        }
    }
    
    @Override
    public List<Location> satisfyingSites(final Context context) {
        if (!this.eval(context)) {
            return new ArrayList<>();
        }
        final List<Location> winningSites = new ArrayList<>();
        final Game game = context.game();
        final int from = (this.startLocationFn == null) ? context.trial().lastMove().toNonDecision() : this.startLocationFn.eval(context);
        if (from < 0) {
            return new ArrayList<>();
        }
        final Topology topology = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        if (from >= topology.getGraphElements(realType).size()) {
            return new ArrayList<>();
        }
        final ContainerState cs = context.containerState(0);
        final int what = cs.what(from, this.type);
        if (what <= 0) {
            return new ArrayList<>();
        }
        final int playerRegion = (this.roleFunc == null) ? -1 : this.roleFunc.eval(context);
        List<ChunkSet> sitesRegions;
        if (this.precomputedSitesRegions == null) {
            final RegionFunction[] regionsToConnect = this.regionsToConnectFn;
            sitesRegions = new ArrayList<>();
            if (regionsToConnect != null) {
                for (final RegionFunction regionToConnect : regionsToConnect) {
                    sitesRegions.add(regionToConnect.eval(context).bitSet());
                }
            }
            else if (this.staticRegions != null) {
                final Integer[][] convertStaticRegionOnLocs;
                final Integer[][] regionSets = convertStaticRegionOnLocs = this.staticRegions.convertStaticRegionOnLocs(this.staticRegions.regionTypes()[0], context);
                for (final Integer[] region : convertStaticRegionOnLocs) {
                    final ChunkSet regionToAdd = new ChunkSet();
                    for (final Integer site : region) {
                        regionToAdd.set(site);
                    }
                    if (!regionToAdd.isEmpty()) {
                        sitesRegions.add(regionToAdd);
                    }
                }
            }
            else if (this.precomputedOwnedRegions != null) {
                sitesRegions.addAll(this.precomputedOwnedRegions.get(playerRegion));
            }
            else {
                for (final Regions region2 : game.equipment().regions()) {
                    if (region2.owner() == playerRegion) {
                        if (region2.region() != null) {
                            for (final RegionFunction r : region2.region()) {
                                sitesRegions.add(r.eval(context).bitSet());
                            }
                        }
                        else {
                            final ChunkSet bitSet = new ChunkSet();
                            for (final int site2 : region2.sites()) {
                                bitSet.set(site2);
                            }
                            sitesRegions.add(bitSet);
                        }
                    }
                }
            }
        }
        else {
            sitesRegions = new ArrayList<>(this.precomputedSitesRegions);
        }
        final int numRegionToConnect = (this.number != null) ? this.number.eval(context) : sitesRegions.size();
        final TIntArrayList groupSites = new TIntArrayList();
        if (cs.who(from, realType) == playerRegion || playerRegion == -1) {
            groupSites.add(from);
        }
        winningSites.add(new FullLocation(from, 0, realType));
        int numRegionConnected = 0;
        for (int j = sitesRegions.size() - 1; j >= 0; --j) {
            final ChunkSet regionToConnect2 = sitesRegions.get(j);
            if (regionToConnect2.get(from)) {
                ++numRegionConnected;
                sitesRegions.remove(j);
            }
            if (numRegionConnected == numRegionToConnect) {
                return this.filterWinningSites(context, winningSites);
            }
        }
        if (!groupSites.isEmpty()) {
            final TIntArrayList sitesExplored = new TIntArrayList();
            int i = 0;
            while (sitesExplored.size() != groupSites.size()) {
                final int site3 = groupSites.get(i);
                final TopologyElement siteElement = topology.getGraphElements(realType).get(site3);
                final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(realType, siteElement, null, null, null, context);
                for (final AbsoluteDirection direction : directions) {
                    final List<Step> steps = topology.trajectories().steps(realType, siteElement.index(), realType, direction);
                    for (final Step step : steps) {
                        final int to = step.to().id();
                        if (groupSites.contains(to)) {
                            continue;
                        }
                        if (what != cs.what(to, realType)) {
                            continue;
                        }
                        groupSites.add(to);
                        winningSites.add(new FullLocation(to, 0, realType));
                        for (int k = sitesRegions.size() - 1; k >= 0; --k) {
                            final ChunkSet regionToConnect3 = sitesRegions.get(k);
                            if (regionToConnect3.get(to)) {
                                ++numRegionConnected;
                                sitesRegions.remove(k);
                            }
                            if (numRegionConnected == numRegionToConnect) {
                                return this.filterWinningSites(context, winningSites);
                            }
                        }
                    }
                }
                sitesExplored.add(site3);
                ++i;
            }
        }
        return new ArrayList<>();
    }
    
    public List<Location> filterWinningSites(final Context context, final List<Location> winningGroup) {
        final Game game = context.game();
        final Topology topology = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final int playerRegion = (this.roleFunc == null) ? -1 : this.roleFunc.eval(context);
        List<ChunkSet> sitesRegions;
        if (this.precomputedSitesRegions == null) {
            final RegionFunction[] regionsToConnect = this.regionsToConnectFn;
            sitesRegions = new ArrayList<>();
            if (regionsToConnect != null) {
                for (final RegionFunction regionToConnect : regionsToConnect) {
                    sitesRegions.add(regionToConnect.eval(context).bitSet());
                }
            }
            else if (this.staticRegions != null) {
                final Integer[][] convertStaticRegionOnLocs;
                final Integer[][] regionSets = convertStaticRegionOnLocs = this.staticRegions.convertStaticRegionOnLocs(this.staticRegions.regionTypes()[0], context);
                for (final Integer[] region : convertStaticRegionOnLocs) {
                    final ChunkSet regionToAdd = new ChunkSet();
                    for (final Integer site : region) {
                        regionToAdd.set(site);
                    }
                    if (!regionToAdd.isEmpty()) {
                        sitesRegions.add(regionToAdd);
                    }
                }
            }
            else if (this.precomputedOwnedRegions != null) {
                sitesRegions.addAll(this.precomputedOwnedRegions.get(playerRegion));
            }
            else {
                for (final Regions region2 : game.equipment().regions()) {
                    if (region2.owner() == playerRegion) {
                        if (region2.region() != null) {
                            for (final RegionFunction r : region2.region()) {
                                sitesRegions.add(r.eval(context).bitSet());
                            }
                        }
                        else {
                            final ChunkSet bitSet = new ChunkSet();
                            for (final int site2 : region2.sites()) {
                                bitSet.set(site2);
                            }
                            sitesRegions.add(bitSet);
                        }
                    }
                }
            }
        }
        else {
            sitesRegions = new ArrayList<>(this.precomputedSitesRegions);
        }
        final int numRegionToConnect = (this.number != null) ? this.number.eval(context) : sitesRegions.size();
        final List<Location> minimumGroup = new ArrayList<>(winningGroup);
        for (int i = minimumGroup.size() - 1; i >= 0; --i) {
            final TIntArrayList groupMinusI = new TIntArrayList();
            for (int j = 0; j < minimumGroup.size(); ++j) {
                if (j != i) {
                    groupMinusI.add(minimumGroup.get(j).site());
                }
            }
            final int startGroup = groupMinusI.get(0);
            final TIntArrayList groupSites = new TIntArrayList();
            groupSites.add(startGroup);
            if (!groupSites.isEmpty()) {
                final TIntArrayList sitesExplored = new TIntArrayList();
                int k = 0;
                while (sitesExplored.size() != groupSites.size()) {
                    final int site3 = groupSites.get(k);
                    final TopologyElement siteElement = topology.getGraphElements(realType).get(site3);
                    final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(realType, siteElement, null, null, null, context);
                    for (final AbsoluteDirection direction : directions) {
                        final List<Step> steps = topology.trajectories().steps(realType, siteElement.index(), realType, direction);
                        for (final Step step : steps) {
                            final int to = step.to().id();
                            if (groupSites.contains(to)) {
                                continue;
                            }
                            if (!groupMinusI.contains(to)) {
                                continue;
                            }
                            groupSites.add(to);
                        }
                    }
                    sitesExplored.add(site3);
                    ++k;
                }
            }
            final boolean oneSingleGroup = groupSites.size() == groupMinusI.size();
            int numRegionConnected = 0;
            for (int l = sitesRegions.size() - 1; l >= 0; --l) {
                final ChunkSet regionToConnect2 = sitesRegions.get(l);
                for (int siteToCheck = regionToConnect2.nextSetBit(0); siteToCheck >= 0; siteToCheck = regionToConnect2.nextSetBit(siteToCheck + 1)) {
                    if (groupMinusI.contains(siteToCheck)) {
                        ++numRegionConnected;
                        break;
                    }
                }
                if (numRegionConnected == numRegionToConnect) {
                    break;
                }
            }
            if (oneSingleGroup && numRegionConnected == numRegionToConnect) {
                minimumGroup.remove(i);
            }
        }
        return minimumGroup;
    }
}
