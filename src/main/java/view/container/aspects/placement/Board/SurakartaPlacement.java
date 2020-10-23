// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.placement.Board;

import game.types.board.SiteType;
import util.Context;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;

public class SurakartaPlacement extends BoardPlacement
{
    public SurakartaPlacement(final BoardStyle containerStyle) {
        super(containerStyle);
    }
    
    @Override
    public void setPlacement(final Context context, final Rectangle placement) {
        final int rows = this.boardStyle.container().topology().rows(SiteType.Vertex).size() - 1;
        final int cols = this.boardStyle.container().topology().columns(SiteType.Vertex).size() - 1;
        final double maxDim = Math.max(rows, cols);
        int extra = Math.min(rows, cols) / 2;
        final int numLoops = this.container().tracks().size() / 2;
        if (numLoops >= extra) {
            ++extra;
        }
        final double fullDim = maxDim + extra * 2;
        switch (this.topology().graph().basis()) {
            case Square -> {
                this.containerScale = 1.1 * maxDim / fullDim;
                break;
            }
            case Triangular -> {
                this.containerScale = 0.9 * maxDim / fullDim;
                break;
            }
            default -> {
                System.out.println("** Board type " + this.topology().graph().basis() + " not supported for Surkarta.");
                break;
            }
        }
        this.setUnscaledPlacement(placement);
        this.placement = new Rectangle((int)(placement.getX() + placement.getWidth() * (1.0 - this.containerScale) / 2.0), (int)(placement.getY() + placement.getHeight() * (1.0 - this.containerScale) / 2.0), (int)(placement.getWidth() * this.containerScale), (int)(placement.getHeight() * this.containerScale));
        this.setCellRadiusPixels((int)(this.cellRadius() * placement.width * this.containerScale));
    }
}
