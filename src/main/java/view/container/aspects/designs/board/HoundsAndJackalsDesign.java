// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.Vertex;
import util.Context;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.util.BitSet;

public class HoundsAndJackalsDesign extends BoardDesign
{
    private final BitSet specialDots;
    
    public HoundsAndJackalsDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
        (this.specialDots = new BitSet()).set(0);
        this.specialDots.set(5);
        this.specialDots.set(7);
        this.specialDots.set(9);
        this.specialDots.set(14);
        this.specialDots.set(19);
        this.specialDots.set(24);
        this.specialDots.set(29);
        this.specialDots.set(34);
        this.specialDots.set(36);
        this.specialDots.set(38);
        this.specialDots.set(43);
        this.specialDots.set(48);
        this.specialDots.set(53);
        this.specialDots.set(58);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.005f;
        final float swThin = Math.max(1, (int)(0.005f * this.boardStyle.placement().width + 0.5));
        final float swThick = 2.0f * swThin;
        this.setStrokesAndColours(context, new Color(200, 200, 200), null, null, null, null, null, new Color(140, 140, 140), swThin, swThick);
        this.drawHoundsAndJackalsBoard(g2d);
        return g2d.getSVGDocument();
    }
    
    void drawHoundsAndJackalsBoard(final Graphics2D g2d) {
        GeneralPath path = new GeneralPath();
        final Point pt0 = this.screenPosn(this.topology().vertices().get(0).centroid());
        final Point pt2 = this.screenPosn(this.topology().vertices().get(1).centroid());
        final int unit = pt2.y - pt0.y;
        final Point ptA = this.screenPosn(this.topology().vertices().get(10).centroid());
        final Point ptB = this.screenPosn(this.topology().vertices().get(23).centroid());
        final Point ptE = this.screenPosn(this.topology().vertices().get(58).centroid());
        final Point ptH = this.screenPosn(this.topology().vertices().get(52).centroid());
        final Point ptI = this.screenPosn(this.topology().vertices().get(39).centroid());
        final int border = (int)(0.9 * unit);
        int ax = ptA.x - border;
        int ay = ptA.y + border;
        int bx = ax;
        int by = ptB.y;
        int cx = ax;
        int cy = by - 3 * unit;
        final int ex = ptE.x;
        final int ey = ptE.y - border;
        int dx = ex - 1 * unit;
        int dy = ey;
        final int fx = ex + 1 * unit;
        final int fy = ey;
        final int hx = ptH.x + border;
        final int hy = ptH.y;
        final int gx = hx;
        final int gy = hy - 3 * unit;
        final int ix = hx;
        final int iy = ptI.y + border;
        path.moveTo(ax, ay);
        path.lineTo(bx, by);
        path.curveTo(cx, cy, dx, dy, ex, ey);
        path.curveTo(fx, fy, gx, gy, hx, hy);
        path.lineTo(ix, iy);
        path.closePath();
        g2d.setColor(new Color(255, 240, 220));
        g2d.fill(path);
        final BasicStroke strokeB = new BasicStroke(0.5f, 0, 0);
        g2d.setStroke(strokeB);
        g2d.setColor(new Color(127, 120, 110));
        g2d.draw(path);
        final int rO = (int)(0.15 * unit);
        final int rI = rO / 2;
        final float sw = 0.03f * unit;
        final BasicStroke strokeD = new BasicStroke(sw, 0, 0);
        g2d.setStroke(strokeD);
        final Color dotColour = new Color(190, 150, 100);
        g2d.setColor(dotColour);
        for (int vid = 0; vid < this.topology().vertices().size(); ++vid) {
            final Vertex vertex = this.topology().vertices().get(vid);
            final Point pt3 = this.screenPosn(vertex.centroid());
            final Shape arcO = new Arc2D.Double(pt3.x - rO, pt3.y - rO, 2 * rO + 1, 2 * rO + 1, 0.0, 360.0, 0);
            g2d.draw(arcO);
            if (this.specialDots.get(vid)) {
                final Shape arcI = new Arc2D.Double(pt3.x - rI, pt3.y - rI, 2 * rI + 1, 2 * rI + 1, 0.0, 360.0, 0);
                g2d.draw(arcI);
            }
        }
        final BasicStroke strokeC = new BasicStroke(2.0f * sw, 1, 1);
        g2d.setStroke(strokeC);
        final Point pt4 = this.screenPosn(this.topology().vertices().get(5).centroid());
        final Point pt5 = this.screenPosn(this.topology().vertices().get(7).centroid());
        final Point pt6 = this.screenPosn(this.topology().vertices().get(9).centroid());
        final Point pt7 = this.screenPosn(this.topology().vertices().get(19).centroid());
        final Point pt8 = this.screenPosn(this.topology().vertices().get(34).centroid());
        final Point pt9 = this.screenPosn(this.topology().vertices().get(36).centroid());
        final Point pt10 = this.screenPosn(this.topology().vertices().get(38).centroid());
        final Point pt11 = this.screenPosn(this.topology().vertices().get(48).centroid());
        final int d1 = (int)(0.333 * unit);
        final int d2 = (int)(1.333 * unit);
        ax = pt6.x - d1;
        ay = pt6.y;
        bx = pt6.x - d2;
        by = pt6.y;
        cx = pt5.x - d2;
        cy = pt5.y;
        dx = pt5.x - d1;
        dy = pt5.y;
        path = new GeneralPath();
        path.moveTo(ax, ay);
        path.curveTo(bx, by, cx, cy, dx, dy);
        g2d.draw(path);
        ax = pt10.x + d1;
        ay = pt10.y;
        bx = pt10.x + d2;
        by = pt10.y;
        cx = pt9.x + d2;
        cy = pt9.y;
        dx = pt9.x + d1;
        dy = pt9.y;
        path = new GeneralPath();
        path.moveTo(ax, ay);
        path.curveTo(bx, by, cx, cy, dx, dy);
        g2d.draw(path);
        ax = pt4.x - d1;
        ay = pt4.y;
        bx = pt4.x - d2;
        by = pt4.y;
        cx = pt7.x + d2;
        cy = pt7.y;
        dx = pt7.x + d1;
        dy = pt7.y;
        path = new GeneralPath();
        path.moveTo(ax, ay);
        path.curveTo(bx, by, cx, cy, dx, dy);
        g2d.draw(path);
        ax = pt8.x + d1;
        ay = pt8.y;
        bx = pt8.x + d2;
        by = pt8.y;
        cx = pt11.x - d2;
        cy = pt11.y;
        dx = pt11.x - d1;
        dy = pt11.y;
        path = new GeneralPath();
        path.moveTo(ax, ay);
        path.curveTo(bx, by, cx, cy, dx, dy);
        g2d.draw(path);
    }
}
