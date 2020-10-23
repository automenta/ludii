// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import game.types.board.SiteType;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.TopologyElement;
import topology.Vertex;
import util.Context;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.List;

public class ShibumiDesign extends BoardDesign
{
    public ShibumiDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.005f;
        final float swThin = Math.max(1, (int)(0.005f * this.boardStyle.placement().width + 0.5));
        final float swThick = 2.0f * swThin;
        this.setStrokesAndColours(context, null, null, null, null, new Color(66, 165, 245), new Color(255, 255, 255), null, swThin, swThick);
        this.drawBoardOutline(g2d);
        return g2d.getSVGDocument();
    }
    
    @Override
    public void drawBoardOutline(final SVGGraphics2D g2d) {
        final List<TopologyElement> corners = this.topology().corners(SiteType.Vertex);
        final int rO = (int)(this.boardStyle.cellRadiusPixels() * 1.1);
        final int rI = (int)(this.boardStyle.cellRadiusPixels() * 0.75);
        final Point[] pts = { this.screenPosn(corners.get(0).centroid()), this.screenPosn(corners.get(2).centroid()), this.screenPosn(corners.get(3).centroid()), this.screenPosn(corners.get(1).centroid()) };
        final GeneralPath path = new GeneralPath(0);
        path.moveTo(pts[3].x, (pts[3].y + rO));
        path.quadTo((pts[3].x + rO), (pts[3].y + rO), (pts[3].x + rO), pts[3].y);
        path.lineTo((pts[2].x + rO), pts[2].y);
        path.quadTo((pts[2].x + rO), (pts[2].y - rO), pts[2].x, (pts[2].y - rO));
        path.lineTo(pts[1].x, (pts[1].y - rO));
        path.quadTo((pts[1].x - rO), (pts[1].y - rO), (pts[1].x - rO), pts[1].y);
        path.lineTo((pts[0].x - rO), pts[0].y);
        path.quadTo((pts[0].x - rO), (pts[0].y + rO), pts[0].x, (pts[0].y + rO));
        path.closePath();
        for (final Vertex vertex : this.topology().vertices()) {
            if (vertex.layer() == 0) {
                final Point pt = this.screenPosn(vertex.centroid());
                final Shape shape = new Ellipse2D.Double(pt.x - rI, pt.y - rI, 2 * rI, 2 * rI);
                path.append(shape, false);
            }
        }
        g2d.setColor(new Color(60, 120, 200));
        final AffineTransform atDown = new AffineTransform();
        atDown.translate(0.0, rI / 4);
        path.transform(atDown);
        g2d.fill(path);
        g2d.setColor(new Color(80, 170, 255));
        final AffineTransform atUp = new AffineTransform();
        atUp.translate(0.0, -rI / 4);
        path.transform(atUp);
        g2d.fill(path);
    }
}
