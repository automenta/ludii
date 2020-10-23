// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import game.types.board.SiteType;
import graphics.svg.SVGtoImage;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.Cell;
import topology.Vertex;
import util.ContainerUtil;
import util.Context;
import util.ImageUtil;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class ScriptaDesign extends BoardDesign
{
    public ScriptaDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        this.setCellLocations(this.topology().vertices());
        ContainerUtil.normaliseGraphElements(this.topology());
        ContainerUtil.centerGraphElements(this.topology());
        this.boardPlacement.calculateAverageCellRadius();
        this.boardPlacement.resetPlacement(context);
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.003f;
        final float swThin = Math.max(1, (int)(0.003f * this.boardStyle.placement().width + 0.5));
        final float swThick = 2.0f * swThin;
        this.setStrokesAndColours(context, new Color(100, 100, 100), new Color(0, 0, 0), null, null, null, null, new Color(0, 0, 0), swThin, swThick);
        this.drawSymbols(g2d);
        this.drawScriptaSymbols(g2d);
        this.drawBoardOutline(g2d);
        final double vertexRadius = 0.01 * this.boardStyle.placement().width;
        this.drawVertices(g2d, context, vertexRadius);
        return g2d.getSVGDocument();
    }
    
    public List<Vertex> setCellLocations(final List<Vertex> cells) {
        double x = 0.0;
        double y = 0.0;
        final int columnNumber = 12;
        for (final Vertex cell : cells) {
            if (cell.index() % 12 < 6) {
                x = cell.index() % 12 * this.boardStyle.cellRadius() * 2.0;
                y = cell.centroid().getY();
            }
            else {
                x = cell.index() % 12 * this.boardStyle.cellRadius() * 2.0 + this.boardStyle.cellRadius() * 2.0;
                y = cell.centroid().getY();
            }
            this.topology().vertices().get(cell.index()).setCentroid(x, y, 0.0);
        }
        return cells;
    }
    
    @Override
    public void drawBoardOutline(final SVGGraphics2D g2d) {
        final List<Vertex> cells = this.topology().vertices();
        g2d.setStroke(this.strokeThick());
        double minX = 9999.0;
        double minY = 9999.0;
        double maxX = -9999.0;
        double maxY = -9999.0;
        final GeneralPath path = new GeneralPath();
        for (final Vertex cell : cells) {
            for (int v = 0; v < cell.cells().size(); ++v) {
                final Cell corner = cell.cells().get(v);
                final Point posn = this.boardStyle.screenPosn(corner.centroid());
                final int x = posn.x;
                final int y = posn.y;
                if (minX > x) {
                    minX = x;
                }
                if (minY > y) {
                    minY = y;
                }
                if (maxX < x) {
                    maxX = x;
                }
                if (maxY < y) {
                    maxY = y;
                }
            }
            g2d.setColor(this.colorOuter);
        }
        final int OuterBufferDistance = (int)(this.boardStyle.cellRadiusPixels() * 2.1);
        path.moveTo(minX - OuterBufferDistance, minY - OuterBufferDistance);
        path.lineTo(minX - OuterBufferDistance, maxY + OuterBufferDistance);
        path.lineTo(maxX + OuterBufferDistance, maxY + OuterBufferDistance);
        path.lineTo(maxX + OuterBufferDistance, minY - OuterBufferDistance);
        path.lineTo(minX - OuterBufferDistance, minY - OuterBufferDistance);
        path.closePath();
        g2d.draw(path);
    }
    
    public void drawScriptaSymbols(final Graphics2D g2d) {
        final List<Vertex> cells = this.boardStyle.topology().vertices();
        final int[] dim = { this.boardStyle.topology().rows(SiteType.Vertex).size(), this.boardStyle.topology().columns(SiteType.Vertex).size() };
        final int maxDim = Math.max(dim[0], dim[1]);
        final double u = 1.0 / (maxDim + 2);
        final int imgSz = (int)(1.0 * u * this.boardStyle.placement().width);
        final Rectangle2D flowerRect = SVGtoImage.getBounds("/svg/misc/flower.svg", (int)(imgSz * 0.9));
        final Rectangle2D flowerHalf1Rect = SVGtoImage.getBounds("/svg/misc/flowerHalf1.svg", (int)(imgSz * 0.9));
        final Rectangle2D flowerHalf2Rect = SVGtoImage.getBounds("/svg/misc/flowerHalf2.svg", (int)(imgSz * 0.9));
        final Color edgeColour = Color.black;
        final Color fillColour = this.colorDecoration();
        for (final Vertex cell : cells) {
            final Vertex v = this.boardStyle.topology().vertices().get(cell.index());
            if (cell.index() == 5) {
                ImageUtil.drawImageAtPosn(g2d, "/svg/misc/flowerHalf1.svg", this.boardStyle.screenPosn(v.centroid()).x + this.boardStyle.cellRadiusPixels() * 2, this.boardStyle.screenPosn(v.centroid()).y + this.boardStyle.cellRadiusPixels() / 4, flowerHalf1Rect, edgeColour, fillColour);
            }
            else if (cell.index() == 17) {
                ImageUtil.drawImageAtPosn(g2d, "/svg/misc/flower.svg", this.boardStyle.screenPosn(v.centroid()).x + this.boardStyle.cellRadiusPixels() * 2, this.boardStyle.screenPosn(v.centroid()).y, flowerRect, edgeColour, fillColour);
            }
            else {
                if (cell.index() != 29) {
                    continue;
                }
                ImageUtil.drawImageAtPosn(g2d, "/svg/misc/flowerHalf2.svg", this.boardStyle.screenPosn(v.centroid()).x + this.boardStyle.cellRadiusPixels() * 2, this.boardStyle.screenPosn(v.centroid()).y - this.boardStyle.cellRadiusPixels() / 4, flowerHalf2Rect, edgeColour, fillColour);
            }
        }
    }
}
