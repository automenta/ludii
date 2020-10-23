// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import game.types.board.SiteType;
import metadata.graphics.util.MetadataImageInfo;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.Context;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.util.ArrayList;

public class GoDesign extends BoardDesign
{
    public GoDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.002f;
        final float swThick;
        final float swThin = swThick = Math.max(0.5f, 0.002f * this.boardStyle.placement().width);
        final Color colourInner = new Color(160, 140, 100);
        final Color colourOuter = new Color(0, 0, 0);
        final Color colourFill = new Color(255, 230, 150);
        final Color colourDot = new Color(130, 120, 90);
        this.setStrokesAndColours(context, colourInner, colourOuter, colourFill, null, null, null, colourDot, swThin, swThick);
        this.setSymbols(context);
        this.fillCells(g2d);
        this.drawInnerCellEdges(g2d, context);
        this.drawOuterCellEdges(g2d, context);
        final ArrayList<Integer> symbolLocations = new ArrayList<>();
        final int boardCellsWidth = this.topology().columns(context.board().defaultSite()).size();
        final int boardCellsHeight = this.topology().rows(context.board().defaultSite()).size();
        if (boardCellsWidth > 13) {
            symbolLocations.add(boardCellsWidth * 3 + 3);
            symbolLocations.add(boardCellsWidth * 3 + boardCellsWidth / 2);
            symbolLocations.add(boardCellsWidth * 3 + boardCellsWidth - 4);
            symbolLocations.add(boardCellsWidth * (boardCellsHeight - 1) / 2 + 3);
            symbolLocations.add(boardCellsWidth * (boardCellsHeight - 1) / 2 + boardCellsWidth / 2);
            symbolLocations.add(boardCellsWidth * (boardCellsHeight - 1) / 2 + boardCellsWidth - 4);
            symbolLocations.add(boardCellsWidth * (boardCellsHeight - 4) + 3);
            symbolLocations.add(boardCellsWidth * (boardCellsHeight - 4) + boardCellsWidth / 2);
            symbolLocations.add(boardCellsWidth * (boardCellsHeight - 4) + boardCellsWidth - 4);
        }
        else if (boardCellsWidth > 9) {
            symbolLocations.add(boardCellsWidth * 3 + 3);
            symbolLocations.add(boardCellsWidth * 3 + boardCellsWidth - 4);
            symbolLocations.add(boardCellsWidth * (boardCellsHeight - 1) / 2 + boardCellsWidth / 2);
            symbolLocations.add(boardCellsWidth * (boardCellsHeight - 4) + 3);
            symbolLocations.add(boardCellsWidth * (boardCellsHeight - 4) + boardCellsWidth - 4);
        }
        else {
            symbolLocations.add(boardCellsWidth * 2 + 2);
            symbolLocations.add(boardCellsWidth * 2 + boardCellsWidth - 3);
            symbolLocations.add(boardCellsWidth * (boardCellsHeight - 1) / 2 + boardCellsWidth / 2);
            symbolLocations.add(boardCellsWidth * (boardCellsHeight - 3) + 2);
            symbolLocations.add(boardCellsWidth * (boardCellsHeight - 3) + boardCellsWidth - 3);
        }
        for (final int i : symbolLocations) {
            this.symbols.add(new MetadataImageInfo(i, SiteType.Vertex, "dot", 0.3f));
        }
        this.drawSymbols(g2d);
        return g2d.getSVGDocument();
    }
}
