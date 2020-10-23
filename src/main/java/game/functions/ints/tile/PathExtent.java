// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.tile;

import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.component.Component;
import game.equipment.component.tile.Path;
import game.functions.directions.Directions;
import game.functions.directions.DirectionsFunction;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.last.LastTo;
import game.functions.ints.state.Mover;
import game.functions.region.RegionFunction;
import game.functions.region.sites.simple.SitesLastTo;
import game.types.board.RelationType;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.RelativeDirection;
import game.util.equipment.Region;
import game.util.graph.Step;
import gnu.trove.list.array.TIntArrayList;
import topology.Cell;
import topology.Topology;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.Arrays;
import java.util.List;

public final class PathExtent extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction colourFn;
    private final IntFunction startFn;
    private final RegionFunction regionStartFn;
    
    public PathExtent(@Opt final IntFunction colour, @Or @Opt final IntFunction start, @Or @Opt final RegionFunction regionStart) {
        int numNonNull = 0;
        if (start != null) {
            ++numNonNull;
        }
        if (regionStart != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Zero or one Or parameter can be non-null.");
        }
        this.colourFn = ((colour == null) ? new Mover() : colour);
        this.startFn = ((start == null) ? new LastTo(null) : start);
        this.regionStartFn = ((regionStart == null) ? ((start == null) ? new SitesLastTo() : null) : regionStart);
    }
    
    @Override
    public int eval(final Context context) {
        int[] regionToCheck;
        if (this.regionStartFn != null) {
            final Region region = this.regionStartFn.eval(context);
            regionToCheck = region.sites();
        }
        else {
            regionToCheck = new int[] { this.startFn.eval(context) };
        }
        int maxExtent = 0;
        for (final int from : regionToCheck) {
            if (from == -1) {
                return maxExtent;
            }
            final int colourLoop = this.colourFn.eval(context);
            final Topology graph = context.topology();
            final Cell fromCell = graph.cells().get(from);
            final int fromRow = fromCell.row();
            final int fromCol = fromCell.col();
            final int cid = context.containerId()[from];
            final ContainerState cs = context.state().containerStates()[cid];
            final int whatSideId = cs.what(from, SiteType.Cell);
            if (whatSideId == 0 || !context.components()[whatSideId].isTile()) {
                return maxExtent;
            }
            final DirectionsFunction directionFunction = new Directions(RelativeDirection.Forward, null, RelationType.Orthogonal, null);
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
                            final Cell cell1Connected = graph.cells().get(site1Connected);
                            final int rowCell1 = cell1Connected.row();
                            final int colCell1 = cell1Connected.col();
                            final int drow = Math.abs(rowCell1 - fromRow);
                            final int dcol = Math.abs(colCell1 - fromCol);
                            if (drow > maxExtent) {
                                maxExtent = drow;
                            }
                            if (dcol > maxExtent) {
                                maxExtent = dcol;
                            }
                            final int whatSide1 = cs.what(site1Connected, SiteType.Cell);
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
                            final Cell cell2Connected = graph.cells().get(site2Connected);
                            final int rowCell2 = cell2Connected.row();
                            final int colCell2 = cell2Connected.col();
                            final int drow2 = Math.abs(rowCell2 - fromRow);
                            final int dcol2 = Math.abs(colCell2 - fromCol);
                            if (drow2 > maxExtent) {
                                maxExtent = drow2;
                            }
                            if (dcol2 > maxExtent) {
                                maxExtent = dcol2;
                            }
                            final int whatSide2 = cs.what(site2Connected, SiteType.Cell);
                            if (originTileConnected.getQuick(index) != site2Connected && whatSide2 != 0 && context.components()[whatSide2].isTile()) {
                                tileConnected.add(site2Connected);
                                originTileConnected.add(site);
                            }
                        }
                    }
                }
            }
        }
        return maxExtent;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.colourFn.gameFlags(game) | this.startFn.gameFlags(game);
        if (this.regionStartFn != null) {
            gameFlags |= this.regionStartFn.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.colourFn.preprocess(game);
        if (this.startFn != null) {
            this.startFn.preprocess(game);
        }
        if (this.regionStartFn != null) {
            this.regionStartFn.preprocess(game);
        }
    }
}
