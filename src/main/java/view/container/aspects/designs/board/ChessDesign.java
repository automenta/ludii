// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.Context;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;

public class ChessDesign extends BoardDesign
{
    public ChessDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        this.checkeredBoard = true;
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.005f;
        final float swThin = (float)Math.max(1, (int)(0.005f * this.boardStyle.placement().width + 0.5));
        final float swThick = 1.0f * swThin;
        this.setStrokesAndColours(context, new Color(0, 0, 0), new Color(150, 75, 0), new Color(200, 150, 75), new Color(250, 221, 144), new Color(223, 178, 110), new Color(255, 240, 200), null, swThin, swThick);
        this.drawGround(g2d, context, true);
        this.setSymbols(context);
        this.fillCells(g2d);
        this.drawOuterCellEdges(g2d, context);
        this.drawSymbols(g2d);
        this.drawGround(g2d, context, false);
        return g2d.getSVGDocument();
    }
}
