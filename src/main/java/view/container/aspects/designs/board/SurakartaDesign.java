// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import game.equipment.container.board.Track;
import game.types.board.SiteType;
import math.MathRoutines;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.Edge;
import util.Context;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.GeneralPath;

public class SurakartaDesign extends BoardDesign
{
    private final Color[] loopColours;
    
    public SurakartaDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
        this.loopColours = new Color[] { new Color(0, 175, 0), new Color(230, 50, 20), new Color(0, 100, 200), new Color(150, 150, 0), new Color(150, 0, 150), new Color(0, 150, 150) };
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swThin = (float)Math.max(1.0, 0.005 * this.boardStyle.placement().width + 0.5);
        final float swThick = 2.0f * swThin;
        this.setStrokesAndColours(context, new Color(50, 150, 255), null, new Color(180, 230, 255), new Color(0, 175, 0), new Color(230, 50, 20), new Color(0, 100, 200), null, swThin, swThick);
        this.drawBoard(g2d);
        return g2d.getSVGDocument();
    }
    
    protected void drawBoard(final Graphics2D g2d) {
        switch (this.topology().graph().basis()) {
            case Square -> this.drawBoardSquare(g2d);
            case Triangular -> this.drawBoardTriangular(g2d);
            default -> System.out.println("** Board type " + this.topology().graph().basis() + " not supported for Surkarta.");
        }
    }
    
    protected void drawBoardSquare(final Graphics2D g2d) {
        final int rows = this.boardStyle.container().topology().rows(SiteType.Vertex).size();
        final int cols = this.boardStyle.container().topology().columns(SiteType.Vertex).size();
        final Point ptSW = this.screenPosn(this.topology().vertices().get(0).centroid());
        final Point ptNW = this.screenPosn(this.topology().vertices().get(rows * cols - cols).centroid());
        final Point ptNE = this.screenPosn(this.topology().vertices().get(rows * cols - 1).centroid());
        final Point ptSE = this.screenPosn(this.topology().vertices().get(cols - 1).centroid());
        g2d.setColor(this.colorFillPhase0);
        final GeneralPath border = new GeneralPath();
        border.moveTo(ptSW.x, ptSW.y);
        border.lineTo(ptNW.x, ptNW.y);
        border.lineTo(ptNE.x, ptNE.y);
        border.lineTo(ptSE.x, ptSE.y);
        border.closePath();
        g2d.fill(border);
        g2d.setStroke(this.strokeThin);
        g2d.setColor(this.colorInner);
        for (final Edge edge : this.topology().edges()) {
            final Point ptA = this.screenPosn(edge.vA().centroid());
            final Point ptB = this.screenPosn(edge.vB().centroid());
            g2d.drawLine(ptA.x, ptA.y, ptB.x, ptB.y);
        }
        g2d.draw(border);
        g2d.setStroke(this.strokeThick());
        for (int t = 0; t < this.boardStyle.container().tracks().size(); t += 2) {
            g2d.setColor(this.loopColours[t / 2 % this.boardStyle.container().tracks().size() % this.loopColours.length]);
            final Track track = this.boardStyle.container().tracks().get(t);
            for (int e = 0; e < track.elems().length; ++e) {
                final Track.Elem elemM = track.elems()[e];
                final Track.Elem elemN = track.elems()[(e + 1) % track.elems().length];
                final Point ptM = this.screenPosn(this.topology().vertices().get(elemM.site).centroid());
                final Point ptN = this.screenPosn(this.topology().vertices().get(elemN.site).centroid());
                if (elemM.bump > 0) {
                    final int rowM = elemM.site / cols;
                    final int colM = elemM.site % cols;
                    final int rowN = elemN.site / cols;
                    final int colN = elemN.site % cols;
                    if ((rowM == 0 || rowN == 0) && (colM == 0 || colN == 0)) {
                        final int r = (int)(MathRoutines.distance(ptM, ptSW) + 0.5);
                        g2d.drawArc(ptSW.x - r, ptSW.y - r, 2 * r, 2 * r, 90, 270);
                    }
                    else if ((rowM == rows - 1 || rowN == rows - 1) && (colM == 0 || colN == 0)) {
                        final int r = (int)(MathRoutines.distance(ptM, ptNW) + 0.5);
                        g2d.drawArc(ptNW.x - r, ptNW.y - r, 2 * r, 2 * r, 0, 270);
                    }
                    else if ((rowM == rows - 1 || rowN == rows - 1) && (colM == cols - 1 || colN == cols - 1)) {
                        final int r = (int)(MathRoutines.distance(ptM, ptNE) + 0.5);
                        g2d.drawArc(ptNE.x - r, ptNE.y - r, 2 * r, 2 * r, 270, 270);
                    }
                    else if ((rowM == 0 || rowN == 0) && (colM == cols - 1 || colN == cols - 1)) {
                        final int r = (int)(MathRoutines.distance(ptM, ptSE) + 0.5);
                        g2d.drawArc(ptSE.x - r, ptSE.y - r, 2 * r, 2 * r, 180, 270);
                    }
                }
                else {
                    g2d.drawLine(ptM.x, ptM.y, ptN.x, ptN.y);
                }
            }
        }
    }
    
    protected void drawBoardTriangular(final Graphics2D g2d) {
        final int rows = this.boardStyle.container().topology().rows(SiteType.Vertex).size();
        final Point ptSW = this.screenPosn(this.topology().vertices().get(0).centroid());
        final Point ptTop = this.screenPosn(this.topology().vertices().get(this.topology().vertices().size() - 1).centroid());
        final Point ptSE = this.screenPosn(this.topology().vertices().get(rows - 1).centroid());
        g2d.setColor(this.colorFillPhase0);
        final GeneralPath border = new GeneralPath();
        border.moveTo(ptSW.x, ptSW.y);
        border.lineTo(ptTop.x, ptTop.y);
        border.lineTo(ptSE.x, ptSE.y);
        border.closePath();
        g2d.fill(border);
        g2d.setStroke(this.strokeThin);
        g2d.setColor(this.colorInner);
        for (final Edge edge : this.topology().edges()) {
            final Point ptA = this.screenPosn(edge.vA().centroid());
            final Point ptB = this.screenPosn(edge.vB().centroid());
            g2d.drawLine(ptA.x, ptA.y, ptB.x, ptB.y);
        }
        g2d.draw(border);
        g2d.setStroke(this.strokeThick());
        for (int t = 0; t < this.boardStyle.container().tracks().size(); t += 2) {
            g2d.setColor(this.loopColours[t / 2 % this.boardStyle.container().tracks().size() % this.loopColours.length]);
            final Track track = this.boardStyle.container().tracks().get(t);
            for (int e = 0; e < track.elems().length; ++e) {
                final Track.Elem elemM = track.elems()[e];
                final Track.Elem elemN = track.elems()[(e + 1) % track.elems().length];
                final Point ptM = this.screenPosn(this.topology().vertices().get(elemM.site).centroid());
                final Point ptN = this.screenPosn(this.topology().vertices().get(elemN.site).centroid());
                if (elemM.bump > 0) {
                    final int diff = elemN.site - elemM.site;
                    if (diff > 0 && diff <= rows / 2) {
                        final Point ptRef = ptTop;
                        final int r = (int)(MathRoutines.distance(ptM, ptRef) * Math.sqrt(3.0) / 2.0 + 0.5);
                        final int ax = ptRef.x + (int)(r * Math.cos(Math.toRadians(210.0)) - 0.5);
                        final int ay = ptRef.y - (int)(r * Math.sin(Math.toRadians(210.0)) + 0.5);
                        g2d.drawLine(ptM.x, ptM.y, ax, ay);
                        g2d.drawArc(ptRef.x - r, ptRef.y - r, 2 * r, 2 * r, 330, 240);
                        final int r2 = (int)(MathRoutines.distance(ptM, ptRef) + 0.5);
                        final int bx = ptRef.x + (int)(r * Math.cos(Math.toRadians(330.0)) + 0.5);
                        final int by = ptRef.y - (int)(r * Math.sin(Math.toRadians(330.0)) + 0.5);
                        final int cx = ptRef.x + (int)(r2 * Math.cos(Math.toRadians(300.0)) + 0.5);
                        final int cy = ptRef.y - (int)(r2 * Math.sin(Math.toRadians(300.0)) + 0.5);
                        g2d.drawLine(bx, by, cx, cy);
                    }
                    else if (diff >= rows / 2) {
                        final Point ptRef = ptSW;
                        final int r = (int)(MathRoutines.distance(ptM, ptRef) * Math.sqrt(3.0) / 2.0 + 0.5);
                        final int ax = ptRef.x + (int)(r * Math.cos(Math.toRadians(330.0)) + 0.5);
                        final int ay = ptRef.y - (int)(r * Math.sin(Math.toRadians(330.0)) - 0.5);
                        g2d.drawLine(ptM.x, ptM.y, ax, ay);
                        g2d.drawArc(ptRef.x - r, ptRef.y - r, 2 * r, 2 * r, 90, 240);
                        final int r2 = (int)(MathRoutines.distance(ptM, ptRef) + 0.5);
                        final int bx = ptRef.x + (int)(r * Math.cos(Math.toRadians(90.0)) + 0.5);
                        final int by = ptRef.y - (int)(r * Math.sin(Math.toRadians(90.0)) + 0.5);
                        final int cx = ptRef.x + (int)(r2 * Math.cos(Math.toRadians(60.0)) + 0.5);
                        final int cy = ptRef.y - (int)(r2 * Math.sin(Math.toRadians(60.0)) + 0.5);
                        g2d.drawLine(bx, by, cx, cy);
                    }
                    else if (diff < -rows / 2) {
                        final Point ptRef = ptSE;
                        final int r = (int)(MathRoutines.distance(ptM, ptRef) * Math.sqrt(3.0) / 2.0 + 0.5);
                        final int ax = ptRef.x + (int)(r * Math.cos(Math.toRadians(90.0)) + 0.5);
                        final int ay = ptRef.y - (int)(r * Math.sin(Math.toRadians(90.0)) + 0.5);
                        g2d.drawLine(ptM.x, ptM.y, ax, ay);
                        g2d.drawArc(ptRef.x - r, ptRef.y - r, 2 * r, 2 * r, 210, 240);
                        final int r2 = (int)(MathRoutines.distance(ptM, ptRef) + 0.5);
                        final int bx = ptRef.x + (int)(r * Math.cos(Math.toRadians(210.0)) + 0.5);
                        final int by = ptRef.y - (int)(r * Math.sin(Math.toRadians(210.0)) + 0.5);
                        final int cx = ptRef.x + (int)(r2 * Math.cos(Math.toRadians(180.0)) + 0.5);
                        final int cy = ptRef.y - (int)(r2 * Math.sin(Math.toRadians(180.0)) + 0.5);
                        g2d.drawLine(bx, by, cx, cy);
                    }
                }
                else {
                    g2d.drawLine(ptM.x, ptM.y, ptN.x, ptN.y);
                }
            }
        }
    }
}
