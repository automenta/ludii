// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.integer;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.equipment.component.Component;
import game.equipment.component.tile.Path;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.last.LastTo;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.DirectionFacing;
import game.util.graph.Step;
import topology.Cell;
import topology.Topology;
import util.Context;
import util.state.containerState.ContainerState;

import java.util.Iterator;
import java.util.List;

@Hide
public final class IsSidesMatch extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction toFn;
    
    public IsSidesMatch(@Opt final IntFunction to) {
        this.toFn = ((to == null) ? new LastTo(null) : to);
    }
    
    @Override
    public boolean eval(final Context context) {
        final int to = this.toFn.eval(context);
        if (to == -1) {
            return false;
        }
        final int cid = context.containerId()[to];
        final Topology graph = context.containers()[cid].topology();
        final int ratioAdjOrtho = graph.supportedAdjacentDirections(SiteType.Cell).size() / graph.supportedOrthogonalDirections(SiteType.Cell).size();
        final ContainerState cs = context.containerState(cid);
        final int what = cs.whatCell(to);
        if (what == 0) {
            return false;
        }
        final Component component = context.components()[what];
        if (!component.isTile()) {
            return true;
        }
        final int numberEdges = graph.numEdges();
        int rotation = cs.rotation(to, SiteType.Cell) / ratioAdjOrtho;
        int[] terminus = component.terminus();
        final Integer numTerminus = component.numTerminus();
        final Path[] paths = component.paths();
        if (numTerminus != null) {
            terminus = new int[numberEdges];
            for (int i = 0; i < numberEdges; ++i) {
                terminus[i] = numTerminus;
            }
        }
        final int[][] coloredTerminus = new int[numberEdges][];
        for (int j = 0; j < numberEdges; ++j) {
            coloredTerminus[j] = new int[terminus[j]];
        }
        for (final Path path : paths) {
            final int side1 = path.side1();
            final int terminus2 = path.terminus1();
            final int side2 = path.side2();
            final int terminus3 = path.terminus2();
            final int colour = path.colour();
            coloredTerminus[side1][terminus2] = colour;
            coloredTerminus[side2][terminus3] = colour;
        }
        while (rotation != 0) {
            for (int j = 0; j < numberEdges - 1; ++j) {
                final int[] temp = coloredTerminus[j + 1];
                coloredTerminus[j + 1] = coloredTerminus[0];
                coloredTerminus[0] = temp;
            }
            --rotation;
        }
        final Cell toCell = graph.cells().get(to);
        for (final Cell vOrtho : toCell.orthogonal()) {
            final int whatOrtho = cs.what(vOrtho.index(), SiteType.Cell);
            if (whatOrtho == 0) {
                continue;
            }
            final Component compOrtho = context.components()[whatOrtho];
            if (!compOrtho.isTile()) {
                continue;
            }
            int rotationOrtho = cs.rotation(vOrtho.index(), SiteType.Cell) / ratioAdjOrtho;
            int[] terminusOrtho = compOrtho.terminus();
            final Integer numTerminusOrtho = compOrtho.numTerminus();
            final Path[] pathsOrtho = compOrtho.paths();
            if (numTerminusOrtho != null) {
                terminusOrtho = new int[numberEdges];
                for (int k = 0; k < numberEdges; ++k) {
                    terminusOrtho[k] = numTerminusOrtho;
                }
            }
            final int[][] coloredTerminusOrtho = new int[numberEdges][];
            for (int l = 0; l < numberEdges; ++l) {
                coloredTerminusOrtho[l] = new int[terminusOrtho[l]];
            }
            for (final Path path2 : pathsOrtho) {
                final int side3 = path2.side1();
                final int terminus4 = path2.terminus1();
                final int side4 = path2.side2();
                final int terminus5 = path2.terminus2();
                final int colour2 = path2.colour();
                coloredTerminusOrtho[side3][terminus4] = colour2;
                coloredTerminusOrtho[side4][terminus5] = colour2;
            }
            while (rotationOrtho != 0) {
                for (int l = 0; l < numberEdges - 1; ++l) {
                    final int[] temp2 = coloredTerminusOrtho[l + 1];
                    coloredTerminusOrtho[l + 1] = coloredTerminusOrtho[0];
                    coloredTerminusOrtho[0] = temp2;
                }
                --rotationOrtho;
            }
            List<DirectionFacing> directions;
            int indexSideTile;
            DirectionFacing direction;
            AbsoluteDirection absDirection;
            List<Step> steps;
            boolean found;
            Iterator<Step> iterator2;
            Step step;
            for (directions = graph.supportedOrthogonalDirections(SiteType.Cell), indexSideTile = 0; indexSideTile < directions.size(); ++indexSideTile) {
                direction = directions.get(indexSideTile);
                absDirection = direction.toAbsolute();
                steps = graph.trajectories().steps(SiteType.Cell, toCell.index(), SiteType.Cell, absDirection);
                found = false;
                iterator2 = steps.iterator();
                while (iterator2.hasNext()) {
                    step = iterator2.next();
                    if (step.to().id() == vOrtho.index()) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    break;
                }
            }
            final int[] toColor = coloredTerminus[indexSideTile];
            final int indexSideOrthoTile = (indexSideTile + graph.numEdges() / 2) % graph.numEdges();
            final int[] orthoColor = coloredTerminusOrtho[indexSideOrthoTile];
            for (int m = 0; m < toColor.length; ++m) {
                if (toColor[m] != orthoColor[orthoColor.length - (1 + m)]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.toFn.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.toFn.preprocess(game);
    }
}
