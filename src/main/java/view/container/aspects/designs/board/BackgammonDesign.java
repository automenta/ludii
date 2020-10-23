// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.Context;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.Board.BackgammonPlacement;
import view.container.styles.board.BackgammonStyle;

import java.awt.*;
import java.awt.geom.GeneralPath;

public class BackgammonDesign extends BoardDesign
{
    BackgammonStyle backgammonStyle;
    BackgammonPlacement backgammonPlacement;
    private final Color[] boardColours;
    
    public BackgammonDesign(final BackgammonStyle boardStyle, final BackgammonPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
        this.boardColours = new Color[] { new Color(225, 182, 130), new Color(116, 58, 41), new Color(140, 75, 45), new Color(185, 130, 85) };
        this.backgammonStyle = boardStyle;
        this.backgammonPlacement = boardPlacement;
    }
    
    @Override
    public String createSVGImage(final Context context) {
        this.boardPlacement.customiseGraphElementLocations(context);
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        this.setStrokesAndColours(context, new Color(120, 190, 240), new Color(125, 75, 0), new Color(210, 230, 255), null, null, null, new Color(0, 0, 0), Math.max(1, (int)(0.0025 * this.boardStyle.placement().width + 0.5)), (int)(2.0 * Math.max(1, (int)(0.0025 * this.boardStyle.placement().width + 0.5))));
        this.drawBackgammonBoard(g2d);
        return g2d.getSVGDocument();
    }
    
    void drawBackgammonBoard(final Graphics2D g2d) {
        final Point pt0 = this.screenPosn(this.topology().vertices().get(0).centroid());
        final Point pt2 = this.screenPosn(this.topology().vertices().get(1).centroid());
        final int unit;
        final int off = unit = pt2.x - pt0.x;
        final Point ptD = this.screenPosn(this.topology().vertices().get(this.backgammonPlacement.homeSize() - 1).centroid());
        final Point ptF = this.screenPosn(this.topology().vertices().get(2 * this.backgammonPlacement.homeSize()).centroid());
        final Point ptC = this.screenPosn(this.topology().vertices().get(2 * this.backgammonPlacement.homeSize() + 1).centroid());
        final Point ptE = this.screenPosn(this.topology().vertices().get(3 * this.backgammonPlacement.homeSize() + 2).centroid());
        final int pr = (int)(off * 0.5);
        final int border = (int)(off * 0.5);
        final int cx = ptC.x - pr;
        final int cy = ptC.y - pr;
        final int ex = ptE.x - pr;
        final int ey = ptE.y - pr;
        final int dx = ptD.x + pr;
        final int dy = ptD.y + pr;
        final int fx = ptF.x + pr;
        final int fy = ptF.y + pr;
        final int ax = cx - border;
        final int ay = cy - border;
        final int bx = fx + border;
        final int by = fy + border;
        g2d.setColor(this.boardColours[2]);
        g2d.fillRect(ax, ay, Math.abs(bx - ax), Math.abs(by - ay));
        g2d.setColor(this.boardColours[3]);
        g2d.fillRect(cx, cy, Math.abs(dx - cx), Math.abs(dy - cy));
        g2d.fillRect(ex, ey, Math.abs(fx - ex), Math.abs(fy - ey));
        final GeneralPath pathD = new GeneralPath();
        final GeneralPath pathL = new GeneralPath();
        final int halfSize = this.topology().vertices().size() / 2;
        int counter = 0;
        for (int n = 0; n < halfSize; ++n) {
            if (n != this.backgammonPlacement.homeSize()) {
                if (n != 3 * this.backgammonPlacement.homeSize() + 1) {
                    ++counter;
                    final int tx0 = cx + n % halfSize * unit;
                    final int ty0 = cy;
                    final int ty2 = ty0 + (int)(4.5 * unit + 0.5);
                    final int bx2 = cx + n % halfSize * unit;
                    final int by2 = dy;
                    final int by3 = by2 - (int)(4.5 * unit + 0.5);
                    if (counter % 2 == 0) {
                        pathD.moveTo(tx0, ty0);
                        pathD.lineTo((tx0 + unit), ty0);
                        pathD.lineTo(tx0 + 0.5 * unit, ty2);
                        pathD.closePath();
                        pathL.moveTo(bx2, by2);
                        pathL.lineTo((bx2 + unit), by2);
                        pathL.lineTo(bx2 + 0.5 * unit, by3);
                        pathL.closePath();
                    }
                    else {
                        pathL.moveTo(tx0, ty0);
                        pathL.lineTo((tx0 + unit), ty0);
                        pathL.lineTo(tx0 + 0.5 * unit, ty2);
                        pathL.closePath();
                        pathD.moveTo(bx2, by2);
                        pathD.lineTo((bx2 + unit), by2);
                        pathD.lineTo(bx2 + 0.5 * unit, by3);
                        pathD.closePath();
                    }
                }
            }
        }
        g2d.setColor(this.boardColours[0]);
        g2d.fill(pathL);
        g2d.setColor(this.boardColours[1]);
        g2d.fill(pathD);
    }
}
