// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.placement.Board;

import game.types.board.SiteType;
import topology.Cell;
import util.Context;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.util.List;

public class Connect4Placement extends BoardPlacement
{
    int connect4Rows;
    
    public Connect4Placement(final BoardStyle containerStyle) {
        super(containerStyle);
        this.connect4Rows = 6;
        this.containerScale = 1.0;
    }
    
    @Override
    public void setPlacement(final Context context, final Rectangle placement) {
        super.setPlacement(context, placement);
        this.setCellLocations(placement.width, this.topology().cells());
    }
    
    public void setCellLocations(final int pixels, final List<Cell> cells) {
        final int cols = this.topology().columns(SiteType.Cell).size();
        final int rows = this.connect4Rows;
        final int u = pixels / (cols + 1);
        final int x0 = pixels / 2 - (int)(0.5 * cols * u + 0.5);
        final int y0 = pixels / 2 - (int)(0.5 * rows * u + 0.5);
        for (int n = 0; n < cols; ++n) {
            final Cell cell = cells.get(n);
            final int row = 0;
            final int col = n;
            final int x2 = x0 + col * u + u / 2;
            final int y2 = y0 + 0 * u + u / 2;
            cell.setCentroid(x2 / (double)pixels, y2 / (double)pixels, 0.0);
            this.topology().cells().get(cell.index()).setCentroid(x2 / (double)pixels, y2 / (double)pixels, 0.0);
        }
    }
}
