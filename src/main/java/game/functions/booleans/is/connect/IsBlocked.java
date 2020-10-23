// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.connect;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.other.Regions;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
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
import util.state.containerState.ContainerState;

import java.util.ArrayList;
import java.util.List;

@Hide
public final class IsBlocked extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction[] regionsToConnectFn;
    private final IntFunction roleFunc;
    private final Regions staticRegions;
    private final IntFunction number;
    private SiteType type;
    private final DirectionsFunction dirnChoice;
    private List<TIntArrayList> precomputedSitesRegions;
    private List<List<TIntArrayList>> precomputedOwnedRegions;
    
    public IsBlocked(@Opt final SiteType type, @Opt final IntFunction number, @Opt final Direction directions, @Or final RegionFunction[] regions, @Or final RoleType role, @Or final RegionTypeStatic regionType) {
        this.number = number;
        this.regionsToConnectFn = regions;
        this.roleFunc = ((role == null) ? null : new Id(null, role));
        this.staticRegions = ((regionType == null) ? null : new Regions(null, null, null, null, null, regionType, null, null));
        this.type = type;
        this.dirnChoice = ((directions != null) ? directions.directionsFunctions() : new Directions(AbsoluteDirection.Adjacent, null));
    }
    
    @Override
    public boolean eval(final Context context) {
        final Game game = context.game();
        final Topology topology = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final ContainerState cs = context.containerState(0);
        final int who = this.roleFunc.eval(context);
        final int playerRegion = (this.roleFunc == null) ? -1 : this.roleFunc.eval(context);
        List<TIntArrayList> sitesRegions;
        if (this.precomputedSitesRegions == null) {
            final RegionFunction[] regionsToConnect = this.regionsToConnectFn;
            sitesRegions = new ArrayList<>();
            if (regionsToConnect != null) {
                for (final RegionFunction regionToConnect : regionsToConnect) {
                    sitesRegions.add(new TIntArrayList(regionToConnect.eval(context).sites()));
                }
            }
            else if (this.staticRegions != null) {
                final Integer[][] convertStaticRegionOnLocs;
                final Integer[][] regionSets = convertStaticRegionOnLocs = this.staticRegions.convertStaticRegionOnLocs(this.staticRegions.regionTypes()[0], context);
                for (final Integer[] region : convertStaticRegionOnLocs) {
                    final TIntArrayList regionToAdd = new TIntArrayList();
                    for (final Integer site : region) {
                        regionToAdd.add(site);
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
                                sitesRegions.add(new TIntArrayList(r.eval(context).sites()));
                            }
                        }
                        else {
                            final TIntArrayList bitSet = new TIntArrayList();
                            for (final int site2 : region2.sites()) {
                                bitSet.add(site2);
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
        final TIntArrayList originalRegion = sitesRegions.get(0);
        for (int i = 0; i < originalRegion.size(); ++i) {
            final ArrayList<TIntArrayList> othersRegionToConnect = new ArrayList<>(sitesRegions);
            othersRegionToConnect.remove(0);
            final int from = originalRegion.get(i);
            final TIntArrayList groupSites = new TIntArrayList();
            if (cs.who(from, realType) == who || cs.what(from, realType) == 0) {
                groupSites.add(from);
            }
            int numRegionConnected = 0;
            if (++numRegionConnected == numRegionToConnect) {
                return false;
            }
            if (!groupSites.isEmpty()) {
                final TIntArrayList sitesExplored = new TIntArrayList();
                int j = 0;
                while (sitesExplored.size() != groupSites.size()) {
                    final int site2 = groupSites.get(j);
                    final TopologyElement siteElement = topology.getGraphElements(realType).get(site2);
                    final List<AbsoluteDirection> directions = this.dirnChoice.convertToAbsolute(realType, siteElement, null, null, null, context);
                    for (final AbsoluteDirection direction : directions) {
                        final List<Step> steps = topology.trajectories().steps(realType, siteElement.index(), realType, direction);
                        for (final Step step : steps) {
                            final int to = step.to().id();
                            if (groupSites.contains(to)) {
                                continue;
                            }
                            if (who != cs.who(to, realType) && cs.what(to, realType) != 0) {
                                continue;
                            }
                            groupSites.add(to);
                            for (int k = othersRegionToConnect.size() - 1; k >= 0; --k) {
                                final TIntArrayList regionToConnect2 = othersRegionToConnect.get(k);
                                if (regionToConnect2.contains(to)) {
                                    ++numRegionConnected;
                                    othersRegionToConnect.remove(k);
                                }
                                if (numRegionConnected == numRegionToConnect) {
                                    return false;
                                }
                            }
                        }
                    }
                    sitesExplored.add(site2);
                    ++j;
                }
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        String str = "";
        str += "IsBlocked";
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
        if (this.number != null) {
            this.number.preprocess(game);
        }
        if (this.regionsToConnectFn != null) {
            boolean allStatic = true;
            for (final RegionFunction regionFunc : this.regionsToConnectFn) {
                if (regionFunc != null && !regionFunc.isStatic()) {
                    allStatic = false;
                    break;
                }
            }
            if (allStatic) {
                this.precomputedSitesRegions = new ArrayList<>();
                for (final RegionFunction regionToConnect : this.regionsToConnectFn) {
                    if (regionToConnect != null) {
                        this.precomputedSitesRegions.add(new TIntArrayList(regionToConnect.eval(new Context(game, new Trial(game))).sites()));
                    }
                }
            }
        }
        else if (this.staticRegions != null) {
            this.precomputedSitesRegions = new ArrayList<>();
            final Integer[][] convertStaticRegionOnLocs;
            final Integer[][] regionSets = convertStaticRegionOnLocs = this.staticRegions.convertStaticRegionOnLocs(this.staticRegions.regionTypes()[0], new Context(game, new Trial(game)));
            for (final Integer[] region : convertStaticRegionOnLocs) {
                final TIntArrayList regionToAdd = new TIntArrayList();
                for (final Integer site : region) {
                    regionToAdd.add(site);
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
                            final TIntArrayList sitesToConnect = new TIntArrayList(r.eval(new Context(game, new Trial(game))).sites());
                            this.precomputedOwnedRegions.get(region2.owner()).add(sitesToConnect);
                        }
                    }
                    else {
                        final TIntArrayList sitesToConnect2 = new TIntArrayList();
                        for (final int site2 : region2.sites()) {
                            sitesToConnect2.add(site2);
                        }
                        this.precomputedOwnedRegions.get(region2.owner()).add(sitesToConnect2);
                    }
                }
            }
        }
    }
}
