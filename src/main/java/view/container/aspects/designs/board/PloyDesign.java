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

public class PloyDesign extends BoardDesign
{
    public PloyDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        this.setStrokesAndColours(context, new Color(102, 0, 153), null, new Color(153, 0, 204), null, null, null, null, (float)(int)(0.01 * this.boardStyle.placement().width + 0.5), (float)(int)(0.01 * this.boardStyle.placement().width + 0.5));
        this.drawBoardOutline(g2d);
        this.drawInnerCellEdges(g2d, context);
        this.drawOuterCellEdges(g2d, context);
        this.drawDiagonalEdges(g2d, context);
        final double vertexRadius = 0.03 * this.boardStyle.placement().width;
        this.drawVertices(g2d, context, vertexRadius);
        return g2d.getSVGDocument();
    }
}
