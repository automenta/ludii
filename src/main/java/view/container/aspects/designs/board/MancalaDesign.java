// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import game.equipment.container.board.custom.MancalaBoard;
import game.types.board.StoreType;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.Vertex;
import util.Context;
import view.container.aspects.designs.BoardDesign;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.*;

public class MancalaDesign extends BoardDesign
{
    public MancalaDesign(final BoardStyle boardStyle) {
        super(boardStyle, null);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final int swThin = Math.max(1, (int)(0.001 * this.boardStyle.placement().width + 0.5));
        final int swThick = 2 * swThin;
        this.setStrokesAndColours(context, null, new Color(125, 75, 0), new Color(255, 220, 100), null, null, null, new Color(127, 100, 50), swThin, swThick);
        final Rectangle2D bounds = context.board().graph().bounds();
        final int numColumns = (context.board() instanceof MancalaBoard) ? ((MancalaBoard)context.board()).numColumns() : ((int)(bounds.getWidth() - 0.5));
        final int numRows = (context.board() instanceof MancalaBoard) ? ((MancalaBoard)context.board()).numRows() : ((int)(bounds.getHeight() + 0.5) + 1);
        final boolean withStore = !(context.board() instanceof MancalaBoard) || ((MancalaBoard) context.board()).storeType() != StoreType.None;
        this.drawMancalaBoard(g2d, numRows, numColumns, withStore);
        return g2d.getSVGDocument();
    }
    
    void drawMancalaBoard(final Graphics2D g2d, final int rows, final int cols, final boolean withStore) {
        final int indexHoleBL = withStore ? 1 : 0;
        final int indexHoleTR = withStore ? (rows * cols) : (rows * cols - 1);
        final int indexHoleBR = withStore ? cols : (cols - 1);
        final int indexHoleTL = indexHoleBR + 1 + (rows - 2) * cols;
        final Point pt1 = this.screenPosn(this.topology().vertices().get(indexHoleBL).centroid());
        final Point pt2 = this.screenPosn(this.topology().vertices().get(indexHoleBL + 1).centroid());
        final int dx = pt2.x - pt1.x;
        final double radius = 0.666 * dx;
        final Point2D ptBL = this.topology().vertices().get(indexHoleBL).centroid();
        final Point2D ptTR = this.topology().vertices().get(indexHoleTR).centroid();
        final Point2D ptBR = this.topology().vertices().get(indexHoleBR).centroid();
        final Point2D ptTL = this.topology().vertices().get(indexHoleTL).centroid();
        final Point2D ptL = this.topology().vertices().get(0).centroid();
        final Point2D ptR = withStore ? this.topology().vertices().get(rows * cols + 1).centroid() : this.topology().vertices().get(0).centroid();
        final int angleForStorage = 120 / rows;
        final int angleForCorners = rows * 15;
        final GeneralPath boardShape = new GeneralPath();
        Point pt3 = this.screenPosn(withStore ? ptL : ptBL);
        if (withStore) {
            boardShape.append(new Arc2D.Double(pt3.x - radius, pt3.y - radius, 2.0 * radius, 2.0 * radius, 180 - angleForStorage, 2 * angleForStorage, 0), true);
            pt3 = this.screenPosn(ptBL);
            boardShape.append(new Arc2D.Double(pt3.x - radius, pt3.y - radius, 2.0 * radius, 2.0 * radius, 270 - angleForCorners, angleForCorners, 0), true);
            pt3 = this.screenPosn(ptBR);
            boardShape.append(new Arc2D.Double(pt3.x - radius, pt3.y - radius, 2.0 * radius, 2.0 * radius, 270.0, angleForCorners, 0), true);
            pt3 = this.screenPosn(ptR);
            boardShape.append(new Arc2D.Double(pt3.x - radius, pt3.y - radius, 2.0 * radius, 2.0 * radius, 360 - angleForStorage, 2 * angleForStorage, 0), true);
            pt3 = this.screenPosn(ptTR);
            boardShape.append(new Arc2D.Double(pt3.x - radius, pt3.y - radius, 2.0 * radius, 2.0 * radius, 90 - angleForCorners, angleForCorners, 0), true);
            pt3 = this.screenPosn(ptTL);
            boardShape.append(new Arc2D.Double(pt3.x - radius, pt3.y - radius, 2.0 * radius, 2.0 * radius, 90.0, angleForCorners, 0), true);
            boardShape.closePath();
            g2d.setColor(this.colorFillPhase0);
            g2d.fill(boardShape);
            g2d.setColor(this.colorOuter);
            g2d.setStroke(this.strokeThick());
            g2d.draw(boardShape);
        }
        else {
            pt3 = this.screenPosn(ptTL);
            final double width = this.screenPosn(ptBR).x - this.screenPosn(ptBL).x + 2.0 * radius;
            final double height = this.screenPosn(ptBR).y - this.screenPosn(ptTR).y + 2.0 * radius;
            final int angle = (rows < 30) ? (rows * 15) : (rows * 10);
            final RoundRectangle2D shape = new RoundRectangle2D.Double(pt3.x - radius, pt3.y - radius, width, height, angle, angle);
            g2d.setColor(this.colorFillPhase0);
            g2d.fill(shape);
            g2d.setColor(this.colorOuter);
            g2d.setStroke(this.strokeThick());
            g2d.draw(shape);
        }
        final int fillR = this.colorFillPhase0.getRed();
        final int fillG = this.colorFillPhase0.getGreen();
        final int fillB = this.colorFillPhase0.getBlue();
        final float[] hsv = new float[3];
        Color.RGBtoHSB(fillR, fillG, fillB, hsv);
        final Color dark = new Color(Color.HSBtoRGB(hsv[0], hsv[1], 0.75f * hsv[2]));
        final Color darker = new Color(Color.HSBtoRGB(hsv[0], hsv[1], 0.5f * hsv[2]));
        g2d.setStroke(this.strokeThin);
        final int r = (int)(0.45 * dx);
        for (final Vertex vertex : this.topology().vertices()) {
            pt3 = this.screenPosn(vertex.centroid());
            this.drawPit(g2d, pt3.x, pt3.y, r, null, dark, darker);
        }
    }
    
    void drawPit(final Graphics2D g2d, final int x, final int y, final int r, final Color lines, final Color dark, final Color darker) {
        final int rr = (int)(0.85 * r);
        g2d.setColor(darker);
        g2d.fillArc(x - r, y - r, 2 * r, 2 * r, 0, 360);
        g2d.setColor(dark);
        g2d.fillArc(x - r, y - r, 2 * r, 2 * r, 180, 180);
        g2d.fillArc(x - r, y - rr, 2 * r, 2 * rr, 0, 360);
        if (lines != null) {
            g2d.setColor(lines);
            g2d.drawArc(x - r, y - r, 2 * r, 2 * r, 0, 360);
        }
    }
}
