// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import game.types.board.SiteType;
import metadata.graphics.util.MetadataImageInfo;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.TopologyElement;
import util.Context;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.util.ArrayList;

public class TaflDesign extends BoardDesign
{
    public TaflDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.003f;
        final float swThin = Math.max(1, (int)(0.003f * this.boardStyle.placement().width + 0.5));
        final float swThick = 2.0f * swThin;
        this.setStrokesAndColours(context, new Color(220, 170, 70), new Color(175, 125, 75), new Color(250, 200, 100), null, null, null, new Color(0, 0, 0), swThin, swThick);
        this.setSymbols(context);
        this.fillCells(g2d);
        this.drawInnerCellEdges(g2d, context);
        final ArrayList<Integer> symbolLocations = new ArrayList<>();
        for (final TopologyElement v : this.topology().centre(SiteType.Cell)) {
            symbolLocations.add(v.index());
        }
        for (final int i : symbolLocations) {
            if (this.topology().cells().get(i).vertices().size() % 3 == 0) {
                this.symbols.add(new MetadataImageInfo(i, SiteType.Cell, "knotTriangle", 0.8f));
            }
            else {
                this.symbols.add(new MetadataImageInfo(i, SiteType.Cell, "knotSquare", 0.9f));
            }
        }
        this.drawSymbols(g2d);
        this.drawOuterCellEdges(g2d, context);
        return g2d.getSVGDocument();
    }
}
