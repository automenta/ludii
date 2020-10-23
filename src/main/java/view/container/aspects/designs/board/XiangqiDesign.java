// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import game.types.board.SiteType;
import graphics.svg.SVGtoImage;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.Vertex;
import util.Context;
import util.ImageUtil;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class XiangqiDesign extends BoardDesign
{
    public XiangqiDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.005f;
        final float swThick;
        final float swThin = swThick = Math.max(1, (int)(0.005f * this.boardStyle.placement().width + 0.5));
        this.setStrokesAndColours(context, new Color(100, 75, 50), new Color(100, 75, 50), new Color(255, 230, 130), null, null, null, new Color(0, 0, 0), swThin, swThick);
        this.fillCells(g2d);
        this.drawInnerCellEdges(g2d, context);
        this.drawSymbols(g2d);
        this.drawXiangqiSymbols(g2d);
        this.drawOuterCellEdges(g2d, context);
        return g2d.getSVGDocument();
    }
    
    @Override
    protected void drawInnerCellEdges(final Graphics2D g2d, final Context context) {
        g2d.setStroke(this.strokeThin);
        g2d.setColor(this.colorInner);
        final GeneralPath path = new GeneralPath();
        for (final Vertex vA : this.topology().vertices()) {
            for (final Vertex vB : vA.orthogonal()) {
                final Point2D va = vA.centroid();
                final Point2D vb = vB.centroid();
                if ((va.getY() < 0.5 || vb.getY() > 0.5) && (va.getY() > 0.5 || vb.getY() < 0.5)) {
                    final Point vaWorld = this.boardStyle.screenPosn(vA.centroid());
                    final Point vbWorld = this.boardStyle.screenPosn(vB.centroid());
                    path.moveTo(vaWorld.x, vaWorld.y);
                    path.lineTo(vbWorld.x, vbWorld.y);
                }
            }
        }
        Point screenPosn = this.boardStyle.screenPosn(this.topology().vertices().get(3).centroid());
        path.moveTo(screenPosn.x, screenPosn.y);
        screenPosn = this.boardStyle.screenPosn(this.topology().vertices().get(23).centroid());
        path.lineTo(screenPosn.x, screenPosn.y);
        screenPosn = this.boardStyle.screenPosn(this.topology().vertices().get(5).centroid());
        path.moveTo(screenPosn.x, screenPosn.y);
        screenPosn = this.boardStyle.screenPosn(this.topology().vertices().get(21).centroid());
        path.lineTo(screenPosn.x, screenPosn.y);
        screenPosn = this.boardStyle.screenPosn(this.topology().vertices().get(86).centroid());
        path.moveTo(screenPosn.x, screenPosn.y);
        screenPosn = this.boardStyle.screenPosn(this.topology().vertices().get(66).centroid());
        path.lineTo(screenPosn.x, screenPosn.y);
        screenPosn = this.boardStyle.screenPosn(this.topology().vertices().get(84).centroid());
        path.moveTo(screenPosn.x, screenPosn.y);
        screenPosn = this.boardStyle.screenPosn(this.topology().vertices().get(68).centroid());
        path.lineTo(screenPosn.x, screenPosn.y);
        g2d.draw(path);
    }
    
    public void drawXiangqiSymbols(final Graphics2D g2d) {
        final int imgSz = this.boardPlacement.cellRadiusPixels() * 2;
        final int boardVertexWidth = this.topology().columns(SiteType.Vertex).size();
        final ArrayList<Integer> symbolLocations = new ArrayList<>();
        symbolLocations.add(boardVertexWidth * 2 + 1);
        symbolLocations.add(boardVertexWidth * 2 + 7);
        symbolLocations.add(boardVertexWidth * 3 + 2);
        symbolLocations.add(boardVertexWidth * 3 + 4);
        symbolLocations.add(boardVertexWidth * 3 + 6);
        symbolLocations.add(boardVertexWidth * 6 + 2);
        symbolLocations.add(boardVertexWidth * 6 + 4);
        symbolLocations.add(boardVertexWidth * 6 + 6);
        symbolLocations.add(boardVertexWidth * 7 + 1);
        symbolLocations.add(boardVertexWidth * 7 + 7);
        final Rectangle2D symbolRect = SVGtoImage.getBounds("/svg/xiangqi/symbol.svg", (int)(imgSz * 0.75));
        final Rectangle2D leftSymbolRect = SVGtoImage.getBounds("/svg/xiangqi/symbol_left.svg", (int)(imgSz * 0.75));
        final Rectangle2D rightSymbolRect = SVGtoImage.getBounds("/svg/xiangqi/symbol_right.svg", (int)(imgSz * 0.75));
        final Color edgeColour = Color.black;
        final Color fillColour = this.colorDecoration();
        for (final Vertex v : this.boardStyle.topology().vertices()) {
            if (v.index() == boardVertexWidth * 3 || v.index() == boardVertexWidth * 6) {
                final Point drawPosn = this.boardStyle.screenPosn(v.centroid());
                ImageUtil.drawImageAtPosn(g2d, "/svg/xiangqi/symbol_left.svg", drawPosn.x + imgSz / 4, drawPosn.y, leftSymbolRect, edgeColour, fillColour);
            }
            if (v.index() == boardVertexWidth * 3 + 8 || v.index() == boardVertexWidth * 6 + 8) {
                final Point drawPosn = this.boardStyle.screenPosn(v.centroid());
                ImageUtil.drawImageAtPosn(g2d, "/svg/xiangqi/symbol_right.svg", drawPosn.x - imgSz / 4, drawPosn.y, rightSymbolRect, edgeColour, fillColour);
            }
            if (symbolLocations.contains(v.index())) {
                final Point drawPosn = this.boardStyle.screenPosn(v.centroid());
                ImageUtil.drawImageAtPosn(g2d, "/svg/xiangqi/symbol.svg", drawPosn.x, drawPosn.y, symbolRect, edgeColour, fillColour);
            }
        }
    }
}
