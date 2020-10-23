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

public class ShogiDesign extends BoardDesign
{
    public ShogiDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.004f;
        final float swThick;
        final float swThin = swThick = (float)Math.max(1, (int)(0.4f / this.topology().vertices().size() * this.boardStyle.placement().width + 0.5));
        this.setStrokesAndColours(context, new Color(100, 75, 50), new Color(100, 75, 50), new Color(255, 230, 130), null, null, null, new Color(0, 0, 0), swThin, swThick);
        this.fillCells(g2d);
        this.drawInnerCellEdges(g2d, context);
        final int boardCellsWidth = this.topology().columns(context.board().defaultSite()).size() + 1;
        final int boardCellsHeight = this.topology().rows(context.board().defaultSite()).size() + 1;
        int dotInwardsValueVertical;
        int dotInwardsValueHorizontal = dotInwardsValueVertical = boardCellsWidth / 3;
        if (this.topology().cells().size() == 1296) {
            dotInwardsValueVertical = 6;
            dotInwardsValueHorizontal = 7;
        }
        final ArrayList<Integer> symbolLocations = new ArrayList<>();
        symbolLocations.add(boardCellsWidth * dotInwardsValueVertical + dotInwardsValueHorizontal);
        symbolLocations.add(boardCellsWidth * dotInwardsValueVertical + boardCellsWidth - dotInwardsValueHorizontal - 1);
        symbolLocations.add(boardCellsWidth * (boardCellsHeight - dotInwardsValueVertical - 1) + dotInwardsValueHorizontal);
        symbolLocations.add(boardCellsWidth * (boardCellsHeight - dotInwardsValueVertical - 1) + boardCellsWidth - dotInwardsValueHorizontal - 1);
        for (final int i : symbolLocations) {
            this.symbols.add(new MetadataImageInfo(i, SiteType.Vertex, "dot", 0.2f));
        }
        this.drawSymbols(g2d);
        this.drawOuterCellEdges(g2d, context);
        return g2d.getSVGDocument();
    }
}
