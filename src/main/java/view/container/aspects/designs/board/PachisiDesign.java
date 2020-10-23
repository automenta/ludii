// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

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
import java.util.BitSet;

public class PachisiDesign extends BoardDesign
{
    private final BitSet crosses;
    
    public PachisiDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
        (this.crosses = new BitSet()).set(63);
        this.crosses.set(67);
        this.crosses.set(65);
        this.crosses.set(84);
        this.crosses.set(82);
        this.crosses.set(86);
        this.crosses.set(3);
        this.crosses.set(23);
        this.crosses.set(38);
        this.crosses.set(15);
        this.crosses.set(30);
        this.crosses.set(47);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.005f;
        final float swThin = (float)Math.max(1, (int)(0.005f * this.boardStyle.placement().width + 0.5));
        final float swThick = 2.0f * swThin;
        this.setStrokesAndColours(context, new Color(200, 200, 200), null, null, null, null, null, new Color(140, 140, 140), swThin, swThick);
        this.drawPachisiBoard(g2d);
        return g2d.getSVGDocument();
    }
    
    void drawPachisiBoard(final Graphics2D g2d) {
        final Point pt0 = this.screenPosn(this.topology().vertices().get(0).centroid());
        final Point pt2 = this.screenPosn(this.topology().vertices().get(1).centroid());
        final int unit = pt2.x - pt0.x;
        final Color lightColour = new Color(180, 70, 0);
        final Color darkColour = new Color(150, 60, 0);
        final Color edgeColour = new Color(220, 190, 50);
        g2d.setColor(edgeColour);
        final BasicStroke strokeA = new BasicStroke(4.0f, 1, 1);
        g2d.setStroke(strokeA);
        for (final Cell cell : this.topology().cells()) {
            final GeneralPath path = new GeneralPath();
            for (int n = 0; n < cell.vertices().size(); ++n) {
                final Vertex vertex = cell.vertices().get((n + 1) % cell.vertices().size());
                final Point pt3 = this.screenPosn(vertex.centroid());
                if (n == 0) {
                    path.moveTo((float)pt3.x, (float)pt3.y);
                }
                else {
                    path.lineTo((float)pt3.x, (float)pt3.y);
                }
            }
            path.closePath();
            if (cell.properties().get(4L)) {
                g2d.setColor(lightColour);
            }
            else {
                g2d.setColor(darkColour);
            }
            g2d.fill(path);
            g2d.setColor(edgeColour);
            g2d.draw(path);
        }
        final double r = 0.45 * unit;
        final BasicStroke strokeX = new BasicStroke((float)(r * 0.2), 0, 0);
        g2d.setStroke(strokeX);
        for (int cid = 0; cid < this.topology().cells().size(); ++cid) {
            final Cell cell2 = this.topology().cells().get(cid);
            if (cell2.vertices().size() >= 4) {
                final Point ptA = this.screenPosn(cell2.vertices().get(0).centroid());
                final Point ptB = this.screenPosn(cell2.vertices().get(1).centroid());
                final Point ptC = this.screenPosn(cell2.vertices().get(2).centroid());
                final Point ptD = this.screenPosn(cell2.vertices().get(3).centroid());
                if (this.crosses.get(cid)) {
                    g2d.setColor(edgeColour);
                    Shape line = new Line2D.Double(ptA.getX(), ptA.getY(), ptC.getX(), ptC.getY());
                    g2d.draw(line);
                    line = new Line2D.Double(ptB.getX(), ptB.getY(), ptD.getX(), ptD.getY());
                    g2d.draw(line);
                }
            }
        }
    }
}
