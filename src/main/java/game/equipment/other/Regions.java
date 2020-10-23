// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.other;

import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.Item;
import game.functions.region.RegionFunction;
import game.types.board.RegionTypeStatic;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.DirectionFacing;
import game.util.equipment.Region;
import game.util.graph.Radial;
import gnu.trove.list.array.TIntArrayList;
import topology.Cell;
import topology.Topology;
import topology.TopologyElement;
import util.Context;
import util.ItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Regions extends Item
{
    private final int[] sites;
    private final RegionFunction[] region;
    private final RegionTypeStatic[] regionType;
    private final String hintRegionName;
    
    public Regions(@Opt final String name, @Opt final RoleType role, @Or final Integer[] sites, @Or final RegionFunction regionFn, @Or final RegionFunction[] regionsFn, @Or final RegionTypeStatic staticRegion, @Or final RegionTypeStatic[] staticRegions, @Opt final String hintRegionLabel) {
        super((name == null) ? ("Region" + ((role == null) ? RoleType.P1 : role)) : name, -1, RoleType.Neutral);
        int numNonNull = 0;
        if (sites != null) {
            ++numNonNull;
        }
        if (regionFn != null) {
            ++numNonNull;
        }
        if (regionsFn != null) {
            ++numNonNull;
        }
        if (staticRegion != null) {
            ++numNonNull;
        }
        if (staticRegions != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Exactly one Or parameter must be non-null.");
        }
        if (role != null) {
            this.setRole(role);
        }
        else {
            this.setRole(RoleType.Neutral);
        }
        if (sites != null) {
            this.sites = new int[sites.length];
            for (int i = 0; i < sites.length; ++i) {
                this.sites[i] = sites[i];
            }
        }
        else {
            this.sites = null;
        }
        this.region = ((regionFn != null) ? new RegionFunction[] { regionFn } : regionsFn);
        this.regionType = ((staticRegion != null) ? new RegionTypeStatic[] { staticRegion } : staticRegions);
        this.hintRegionName = hintRegionLabel;
        this.setType(ItemType.Regions);
    }
    
    public Integer[][] convertStaticRegionOnLocs(final RegionTypeStatic type, final Context context) {
        Integer[][] regions = null;
        final Topology graph = context.topology();
        final SiteType defaultType = context.board().defaultSite();
        switch (type) {
            case Corners: {
                regions = new Integer[graph.corners(defaultType).size()][1];
                for (int c = 0; c < graph.corners(defaultType).size(); ++c) {
                    final TopologyElement corner = graph.corners(defaultType).get(c);
                    regions[c][0] = corner.index();
                }
                break;
            }
            case Sides: {
                regions = new Integer[graph.sides(defaultType).size()][];
                int indexSide = 0;
                for (final Map.Entry<DirectionFacing, List<TopologyElement>> entry : graph.sides(defaultType).entrySet()) {
                    regions[indexSide] = new Integer[entry.getValue().size()];
                    for (int j = 0; j < entry.getValue().size(); ++j) {
                        final TopologyElement element = entry.getValue().get(j);
                        regions[indexSide][j] = element.index();
                    }
                    ++indexSide;
                }
                break;
            }
            case SidesNoCorners: {
                final TIntArrayList corners = new TIntArrayList();
                for (int c2 = 0; c2 < graph.corners(defaultType).size(); ++c2) {
                    final TopologyElement corner2 = graph.corners(defaultType).get(c2);
                    corners.add(corner2.index());
                }
                regions = new Integer[graph.sides(defaultType).size()][];
                int indexSideNoCorners = 0;
                for (final Map.Entry<DirectionFacing, List<TopologyElement>> entry2 : graph.sides(defaultType).entrySet()) {
                    final List<Integer> sideNoCorner = new ArrayList<>();
                    regions[indexSideNoCorners] = new Integer[entry2.getValue().size()];
                    for (int i = 0; i < entry2.getValue().size(); ++i) {
                        final TopologyElement element2 = entry2.getValue().get(i);
                        if (!corners.contains(element2.index())) {
                            sideNoCorner.add(element2.index());
                        }
                    }
                    regions[indexSideNoCorners] = new Integer[sideNoCorner.size()];
                    for (int i = 0; i < sideNoCorner.size(); ++i) {
                        regions[indexSideNoCorners][i] = sideNoCorner.get(i);
                    }
                    ++indexSideNoCorners;
                }
                break;
            }
            case AllSites: {
                regions = new Integer[1][graph.getGraphElements(defaultType).size()];
                for (int k = 0; k < graph.getGraphElements(defaultType).size(); ++k) {
                    regions[0][k] = k;
                }
                break;
            }
            case Columns: {
                regions = new Integer[graph.columns(defaultType).size()][];
                for (int k = 0; k < graph.columns(defaultType).size(); ++k) {
                    final List<TopologyElement> col = graph.columns(defaultType).get(k);
                    regions[k] = new Integer[col.size()];
                    for (int l = 0; l < col.size(); ++l) {
                        regions[k][l] = col.get(l).index();
                    }
                }
                break;
            }
            case Rows: {
                regions = new Integer[graph.rows(defaultType).size()][];
                for (int k = 0; k < graph.rows(defaultType).size(); ++k) {
                    final List<TopologyElement> row = graph.rows(defaultType).get(k);
                    regions[k] = new Integer[row.size()];
                    for (int l = 0; l < row.size(); ++l) {
                        regions[k][l] = row.get(l).index();
                    }
                }
                break;
            }
            case Diagonals: {
                regions = new Integer[graph.diagonals(defaultType).size()][];
                for (int k = 0; k < graph.diagonals(defaultType).size(); ++k) {
                    final List<TopologyElement> diag = graph.diagonals(defaultType).get(k);
                    regions[k] = new Integer[diag.size()];
                    for (int l = 0; l < diag.size(); ++l) {
                        regions[k][l] = diag.get(l).index();
                    }
                }
                break;
            }
            case Layers: {
                regions = new Integer[graph.layers(defaultType).size()][];
                for (int k = 0; k < graph.layers(defaultType).size(); ++k) {
                    final List<TopologyElement> diag = graph.layers(defaultType).get(k);
                    regions[k] = new Integer[diag.size()];
                    for (int l = 0; l < diag.size(); ++l) {
                        regions[k][l] = diag.get(l).index();
                    }
                }
                break;
            }
            case HintRegions: {
                if (this.hintRegionName == null) {
                    return context.game().equipment().cellsWithHints();
                }
                if (context.game().equipment().verticesWithHints().length != 0) {
                    return context.game().equipment().verticesWithHints();
                }
                if (context.game().equipment().cellsWithHints().length != 0) {
                    return context.game().equipment().cellsWithHints();
                }
                if (context.game().equipment().edgesWithHints().length != 0) {
                    return context.game().equipment().edgesWithHints();
                }
                break;
            }
            case AllDirections: {
                regions = new Integer[graph.getGraphElements(defaultType).size()][];
                for (final TopologyElement element : graph.getGraphElements(defaultType)) {
                    final List<Radial> radials = graph.trajectories().radials(defaultType, element.index(), AbsoluteDirection.All);
                    final TIntArrayList locs = new TIntArrayList();
                    locs.add(element.index());
                    for (final Radial radial : radials) {
                        for (int toIdx = 1; toIdx < radial.steps().length; ++toIdx) {
                            final int to = radial.steps()[toIdx].id();
                            if (!locs.contains(to)) {
                                locs.add(to);
                            }
                        }
                    }
                    regions[element.index()] = new Integer[locs.size()];
                    for (int index = 0; index < locs.size(); ++index) {
                        regions[element.index()][index] = locs.getQuick(index);
                    }
                }
                break;
            }
            case SubGrids: {
                final int sizeSubGrids = (int)Math.sqrt(Math.sqrt(graph.cells().size()));
                regions = new Integer[sizeSubGrids * sizeSubGrids][sizeSubGrids * sizeSubGrids];
                int indexRegion = 0;
                for (int rowSubGrid = 0; rowSubGrid < sizeSubGrids; ++rowSubGrid) {
                    for (int colSubGrid = 0; colSubGrid < sizeSubGrids; ++colSubGrid) {
                        int indexOnTheRegion = 0;
                        for (final Cell vertex : context.board().topology().cells()) {
                            final int col2 = vertex.col();
                            final int row2 = vertex.row();
                            if (row2 >= rowSubGrid * sizeSubGrids && row2 < (rowSubGrid + 1) * sizeSubGrids && col2 >= colSubGrid * sizeSubGrids && col2 < (colSubGrid + 1) * sizeSubGrids) {
                                regions[indexRegion][indexOnTheRegion] = vertex.index();
                                ++indexOnTheRegion;
                            }
                        }
                        ++indexRegion;
                    }
                }
                break;
            }
            case Regions:
            case Vertices:
            case Touching: {
                final ArrayList<ArrayList<TopologyElement>> touchingRegions = new ArrayList<>();
                for (final TopologyElement element2 : graph.getGraphElements(defaultType)) {
                    for (final TopologyElement vElement : element2.adjacent()) {
                        final ArrayList<TopologyElement> touchingRegion = new ArrayList<>();
                        touchingRegion.add(element2);
                        touchingRegion.add(vElement);
                        touchingRegions.add(touchingRegion);
                    }
                }
                regions = new Integer[touchingRegions.size()][2];
                for (int m = 0; m < touchingRegions.size(); ++m) {
                    for (int j2 = 0; j2 < 2; ++j2) {
                        regions[m][j2] = touchingRegions.get(m).get(j2).index();
                    }
                }
                break;
            }
        }
        return regions;
    }
    
    public int[] sites() {
        return this.sites;
    }
    
    public RegionFunction[] region() {
        return this.region;
    }
    
    public RegionTypeStatic[] regionTypes() {
        return this.regionType;
    }
    
    public int[] eval(final Context context) {
        if (this.region == null) {
            return this.sites;
        }
        final List<TIntArrayList> siteLists = new ArrayList<>();
        int totalNumSites = 0;
        for (final RegionFunction regionFn : this.region) {
            final TIntArrayList wrapped = TIntArrayList.wrap(regionFn.eval(context).sites());
            siteLists.add(wrapped);
            totalNumSites += wrapped.size();
        }
        final int[] toReturn = new int[totalNumSites];
        int startIdx = 0;
        for (final TIntArrayList wrapped2 : siteLists) {
            wrapped2.toArray(toReturn, 0, startIdx, wrapped2.size());
            startIdx += wrapped2.size();
        }
        if (siteLists.size() > 1) {
            return new Region(toReturn).sites();
        }
        return toReturn;
    }
    
    public boolean contains(final Context context, final int location) {
        if (this.region != null) {
            for (final RegionFunction regionFn : this.region) {
                if (regionFn.contains(context, location)) {
                    return true;
                }
            }
            return false;
        }
        for (final int site : this.sites) {
            if (site == location) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isStatic() {
        if (this.region != null) {
            for (final RegionFunction regionFn : this.region) {
                if (!regionFn.isStatic()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void preprocess(final Game game) {
        if (this.region() != null) {
            for (final RegionFunction regionFunction : this.region()) {
                regionFunction.preprocess(game);
            }
        }
    }
}
