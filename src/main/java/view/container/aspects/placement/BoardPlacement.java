// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.placement;

import game.types.board.SiteType;
import util.ContainerUtil;
import util.Context;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.Point2D;

public class BoardPlacement extends ContainerPlacement
{
    protected BoardStyle boardStyle;
    protected static final double DEFAULT_BOARD_SCALE = 0.8;
    
    public BoardPlacement(final BoardStyle containerStyle) {
        super(containerStyle);
        this.containerScale = 0.8;
        this.boardStyle = containerStyle;
    }
    
    public void customiseGraphElementLocations(final Context context) {
        ContainerUtil.normaliseGraphElements(this.topology());
        ContainerUtil.centerGraphElements(this.topology());
        this.calculateAverageCellRadius();
        this.resetPlacement(context);
    }
    
    public void setCustomPlacement(final Context context, final Rectangle placement, final Point2D boardCenter, final double scale) {
        this.setUnscaledPlacement(placement);
        this.containerScale = scale;
        this.placement = new Rectangle((int)(placement.getX() + placement.getWidth() * (1.0 - scale) * boardCenter.getX()), (int)(placement.getY() + placement.getHeight() * (1.0 - scale) * boardCenter.getY()), (int)(placement.getWidth() * scale), (int)(placement.getHeight() * scale));
        this.setCellRadiusPixels((int)(this.cellRadius() * this.placement.width));
    }
    
    @Override
    public void setPlacement(final Context context, final Rectangle placement) {
        if (context.board().defaultSite() == SiteType.Vertex) {
            this.containerScale = 0.8 - this.cellRadius();
        }
        this.setCustomPlacement(context, placement, new Point2D.Double(0.5, 0.5), this.containerScale);
    }
    
    public void resetPlacement(final Context context) {
        this.setPlacement(context, this.unscaledPlacement());
    }
}
