// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import game.Game;
import game.equipment.other.Map;
import main.math.MathRoutines;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.Cell;
import topology.Vertex;
import util.Context;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

public class SnakesAndLaddersDesign extends BoardDesign
{
    public SnakesAndLaddersDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.005f;
        final float swThin = (float)Math.max(1, (int)(0.005f * this.boardStyle.placement().width + 0.5));
        final float swThick = (float)Math.max(2, (int)(0.002 * this.boardStyle.placement().width + 0.5));
        final Color shade0 = new Color(210, 240, 255);
        final Color shade2 = new Color(190, 220, 255);
        final Color shadeEdge = MathRoutines.shade(shade0, 0.25);
        this.setStrokesAndColours(context, null, null, shade0, shade2, null, null, shadeEdge, swThin, swThick);
        this.fillCells(g2d);
        this.drawSnakesAndLadders(g2d, context.game());
        this.drawOuterCellEdges(g2d, context);
        return g2d.getSVGDocument();
    }
    
    private void drawSnakesAndLadders(final Graphics2D g2d, final Game game) {
        for (final Map map : game.equipment().maps()) {
            for (int n = 0; n < map.map().size(); ++n) {
                final int from = map.map().keys()[n];
                final int to = map.map().values()[n];
                if (from > to) {
                    this.drawSnake(g2d, from, to);
                }
            }
        }
        for (final Map map : game.equipment().maps()) {
            for (int n = 0; n < map.map().size(); ++n) {
                final int from = map.map().keys()[n];
                final int to = map.map().values()[n];
                if (from < to) {
                    this.drawLadder(g2d, from, to);
                }
            }
        }
    }
    
    private void drawLadder(final Graphics2D g2d, final int from, final int to) {
        final List<Cell> cells = this.topology().cells();
        final double clip = 0.5 * this.boardStyle.cellRadius() * this.boardStyle.placement().width;
        final Cell cellA = cells.get(from);
        final Cell cellB = cells.get(to);
        final Point pixelA = this.screenPosn(cellA.centroid());
        final Point pixelB = this.screenPosn(cellB.centroid());
        final double angle = Math.atan2(pixelB.y - pixelA.y, pixelB.x - pixelA.x);
        final Point2D.Double ptA = new Point2D.Double(pixelA.x + clip * Math.cos(angle), pixelA.y + clip * Math.sin(angle));
        final Point2D.Double ptB = new Point2D.Double(pixelB.x + clip * Math.cos(angle + 3.141592653589793), pixelB.y + clip * Math.sin(angle + 3.141592653589793));
        final double width = 0.3 * this.boardStyle.cellRadius() * this.boardStyle.placement().width;
        final double l0x = ptA.x + width * Math.cos(angle + 1.5707963267948966);
        final double l0y = ptA.y + width * Math.sin(angle + 1.5707963267948966);
        final double l1x = ptB.x + width * Math.cos(angle + 1.5707963267948966);
        final double l1y = ptB.y + width * Math.sin(angle + 1.5707963267948966);
        final double r0x = ptA.x + width * Math.cos(angle - 1.5707963267948966);
        final double r0y = ptA.y + width * Math.sin(angle - 1.5707963267948966);
        final double r1x = ptB.x + width * Math.cos(angle - 1.5707963267948966);
        final double r1y = ptB.y + width * Math.sin(angle - 1.5707963267948966);
        final double length = MathRoutines.distance(ptA, ptB);
        final int numRungs = (int)(0.75 * length / width);
        final BasicStroke stroke = new BasicStroke((float)(0.125 * this.boardStyle.cellRadius() * this.boardStyle.placement().width), 0, 0);
        g2d.setStroke(stroke);
        g2d.setColor(new Color(255, 127, 0));
        for (int r = 1; r < numRungs - 1; ++r) {
            final double t = r / (double)(numRungs - 1);
            final double rungLx = l0x + t * (l1x - l0x);
            final double rungLy = l0y + t * (l1y - l0y);
            final double rungRx = r0x + t * (r1x - r0x);
            final double rungRy = r0y + t * (r1y - r0y);
            final Shape rung = new Line2D.Double(rungLx, rungLy, rungRx, rungRy);
            g2d.draw(rung);
        }
        final Shape left = new Line2D.Double(l0x, l0y, l1x, l1y);
        final Shape right = new Line2D.Double(r0x, r0y, r1x, r1y);
        g2d.draw(left);
        g2d.draw(right);
    }
    
    private void drawSnake(final Graphics2D g2d, final int from, final int to) {
        final List<Cell> cells = this.topology().cells();
        final double u = this.boardStyle.cellRadius() * this.boardStyle.placement().width;
        final double clipTail = 0.5 * u;
        final double clipHead = 0.75 * u;
        final Cell cellA = cells.get(from);
        final Cell cellB = cells.get(to);
        final Point pixelA = this.screenPosn(cellA.centroid());
        final Point pixelB = this.screenPosn(cellB.centroid());
        final double angle = Math.atan2(pixelB.y - pixelA.y, pixelB.x - pixelA.x);
        final Point2D.Double ptA = new Point2D.Double(pixelA.x + clipTail * Math.cos(angle), pixelA.y + clipTail * Math.sin(angle));
        final Point2D.Double ptB = new Point2D.Double(pixelB.x + clipHead * Math.cos(angle + 3.141592653589793), pixelB.y + clipHead * Math.sin(angle + 3.141592653589793));
        final double offI = 0.2 * u;
        final double offO = 0.6 * u;
        final double length = MathRoutines.distance(ptA, ptB);
        final int numBends = 4 + (int)(0.5 * length / u);
        final Point2D.Double[][] cps = new Point2D.Double[numBends + 1][2];
        cps[0][0] = ptA;
        cps[0][1] = ptA;
        cps[numBends - 1][0] = ptB;
        cps[numBends - 1][1] = ptB;
        for (int b = 1; b < numBends - 1; ++b) {
            final double t = b / (double)(numBends - 1);
            final double tx = ptA.x + t * (ptB.x - ptA.x);
            final double ty = ptA.y + t * (ptB.y - ptA.y);
            if (b % 2 == 0) {
                cps[b][0] = new Point2D.Double(tx + offI * Math.cos(angle + 1.5707963267948966), ty + offI * Math.sin(angle + 1.5707963267948966));
                cps[b][1] = new Point2D.Double(tx + offO * Math.cos(angle + 1.5707963267948966), ty + offO * Math.sin(angle + 1.5707963267948966));
            }
            else {
                cps[b][1] = new Point2D.Double(tx + offI * Math.cos(angle - 1.5707963267948966), ty + offI * Math.sin(angle - 1.5707963267948966));
                cps[b][0] = new Point2D.Double(tx + offO * Math.cos(angle - 1.5707963267948966), ty + offO * Math.sin(angle - 1.5707963267948966));
            }
        }
        final GeneralPath path = new GeneralPath();
        path.moveTo(ptA.x, ptA.y);
        final double off = 0.6;
        for (int b2 = 0; b2 < numBends - 2; ++b2) {
            final double b0x = cps[b2][0].x;
            final double b0y = cps[b2][0].y;
            final double b1x = cps[b2 + 1][0].x;
            final double b1y = cps[b2 + 1][0].y;
            final double b2x = cps[b2 + 2][0].x;
            final double b2y = cps[b2 + 2][0].y;
            final double ax = (b0x + b1x) / 2.0;
            final double ay = (b0y + b1y) / 2.0;
            final double dx = (b1x + b2x) / 2.0;
            final double dy = (b1y + b2y) / 2.0;
            final double bx = ax + 0.6 * (b1x - ax);
            final double by = ay + 0.6 * (b1y - ay);
            final double cx = dx + 0.6 * (b1x - dx);
            final double cy = dy + 0.6 * (b1y - dy);
            path.curveTo(bx, by, cx, cy, dx, dy);
        }
        path.lineTo(ptB.x, ptB.y);
        for (int b2 = numBends - 3; b2 >= 0; --b2) {
            final double b0x = cps[b2 + 2][1].x;
            final double b0y = cps[b2 + 2][1].y;
            final double b1x = cps[b2 + 1][1].x;
            final double b1y = cps[b2 + 1][1].y;
            final double b2x = cps[b2 + 0][1].x;
            final double b2y = cps[b2 + 0][1].y;
            final double ax = (b0x + b1x) / 2.0;
            final double ay = (b0y + b1y) / 2.0;
            final double dx = (b1x + b2x) / 2.0;
            final double dy = (b1y + b2y) / 2.0;
            final double bx = ax + 0.6 * (b1x - ax);
            final double by = ay + 0.6 * (b1y - ay);
            final double cx = dx + 0.6 * (b1x - dx);
            final double cy = dy + 0.6 * (b1y - dy);
            path.curveTo(bx, by, cx, cy, dx, dy);
        }
        path.closePath();
        g2d.setColor(new Color(0, 127, 0));
        g2d.fill(path);
        final BasicStroke stroke = new BasicStroke(0.5f, 0, 0);
        g2d.setStroke(stroke);
        g2d.setColor(new Color(0, 0, 0));
        g2d.draw(path);
    }
    
    @Override
    protected void fillCells(final Graphics2D g2d) {
        final List<Cell> cells = this.topology().cells();
        final int fontSize = (int)(0.85 * this.boardStyle.cellRadius() * this.boardStyle.placement().width + 0.5);
        final Font font = new Font("Arial", 0, fontSize);
        g2d.setFont(font);
        g2d.setStroke(this.strokeThin);
        for (final Cell cell : cells) {
            final GeneralPath path = new GeneralPath();
            for (int v = 0; v < cell.vertices().size(); ++v) {
                if (path.getCurrentPoint() == null) {
                    final Vertex prev = cell.vertices().get(cell.vertices().size() - 1);
                    final Point prevPosn = this.screenPosn(prev.centroid());
                    path.moveTo((float)prevPosn.x, (float)prevPosn.y);
                }
                final Vertex corner = cell.vertices().get(v);
                final Point cornerPosn = this.screenPosn(corner.centroid());
                path.lineTo((float)cornerPosn.x, (float)cornerPosn.y);
            }
            if ((cell.col() + cell.row()) % 2 == 0) {
                g2d.setColor(this.colorFillPhase1);
            }
            else {
                g2d.setColor(this.colorFillPhase0);
            }
            g2d.fill(path);
        }
        g2d.setColor(Color.white);
        for (final Cell cell : cells) {
            String cellNumber = "";
            if (cell.row() % 2 == 0) {
                cellNumber = String.format("%d", cell.index() + 1);
            }
            else {
                cellNumber = String.format("%d", cell.row() * 10 + 10 - cell.col());
            }
            final Rectangle bounds = g2d.getFontMetrics().getStringBounds(cellNumber, g2d).getBounds();
            final Point pt = this.screenPosn(cell.centroid());
            g2d.drawString(cellNumber, pt.x - (int)(0.5 * bounds.getWidth()), pt.y + (int)(0.3 * bounds.getHeight()));
        }
    }
}
