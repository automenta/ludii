// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board.puzzle;

import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.Context;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;

public class HashiDesign extends PuzzleDesign
{
    public HashiDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.003f;
        final float swThin = Math.max(1, (int)(0.003f * this.boardStyle.placement().width + 0.5));
        final float swThick = 2.0f * swThin;
        this.setStrokesAndColours(context, new Color(0, 0, 0), null, null, null, null, null, new Color(0, 0, 0), swThin, swThick);
        final double cellDistance = this.boardStyle.cellRadius();
        final double vertexRadius = 0.5 * this.boardStyle.placement().width * cellDistance;
        this.drawVertices(g2d, context, vertexRadius);
        return g2d.getSVGDocument();
    }
}
