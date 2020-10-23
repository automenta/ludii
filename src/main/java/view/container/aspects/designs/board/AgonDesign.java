// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import gnu.trove.list.array.TIntArrayList;
import topology.Cell;
import topology.Vertex;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.List;

public class AgonDesign extends BoardDesign
{
    public AgonDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    protected void fillCells(final Graphics2D g2d) {
        final List<Vertex> cells = this.topology().vertices();
        final Rectangle placement = this.boardStyle.placement();
        g2d.setColor(this.colorFillPhase0);
        g2d.setStroke(this.strokeThin);
        for (final Vertex cell : cells) {
            final GeneralPath path = new GeneralPath();
            for (int v = 0; v < cell.cells().size(); ++v) {
                if (path.getCurrentPoint() == null) {
                    final Cell prev = cell.cells().get(cell.cells().size() - 1);
                    final double x = prev.centroid().getX() * placement.width;
                    final double y = placement.width - 1 - prev.centroid().getY() * placement.width;
                    path.moveTo(x, y);
                }
                final Cell corner = cell.cells().get(v);
                final double x = corner.centroid().getX() * placement.width;
                final double y = placement.width - 1 - corner.centroid().getY() * placement.width;
                path.lineTo(x, y);
            }
            g2d.setColor(this.colorFillPhase0);
            final int[] redSpots = { 7, 8, 9, 10, 11, 19, 28, 38, 49, 59, 68, 76, 83, 82, 81, 80, 79, 71, 62, 52, 41, 31, 22, 14, 24, 25, 26, 36, 47, 57, 66, 65, 64, 54, 43, 33, 45 };
            if (TIntArrayList.wrap(redSpots).contains(cell.index())) {
                g2d.setColor(this.colorFillPhase1);
            }
            g2d.fill(path);
        }
    }
}
