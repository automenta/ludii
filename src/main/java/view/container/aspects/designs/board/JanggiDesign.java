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
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public class JanggiDesign extends BoardDesign
{
    public JanggiDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.005f;
        final float swThick;
        final float swThin = swThick = Math.max(1, (int)(0.005f * this.boardStyle.placement().width + 0.5));
        this.setStrokesAndColours(context, new Color(100, 75, 50), new Color(100, 75, 50), new Color(255, 165, 0), null, null, null, new Color(0, 0, 0), swThin, swThick);
        this.drawBoardOutline(g2d);
        this.drawInnerCellEdges(g2d, context);
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
                    final Point vaWorld = this.screenPosn(vA.centroid());
                    final Point vbWorld = this.screenPosn(vB.centroid());
                    path.moveTo(vaWorld.x, vaWorld.y);
                    path.lineTo(vbWorld.x, vbWorld.y);
                }
            }
        }
        Point screenPosn = this.screenPosn(this.topology().vertices().get(3).centroid());
        path.moveTo(screenPosn.x, screenPosn.y);
        screenPosn = this.screenPosn(this.topology().vertices().get(23).centroid());
        path.lineTo(screenPosn.x, screenPosn.y);
        screenPosn = this.screenPosn(this.topology().vertices().get(5).centroid());
        path.moveTo(screenPosn.x, screenPosn.y);
        screenPosn = this.screenPosn(this.topology().vertices().get(21).centroid());
        path.lineTo(screenPosn.x, screenPosn.y);
        screenPosn = this.screenPosn(this.topology().vertices().get(86).centroid());
        path.moveTo(screenPosn.x, screenPosn.y);
        screenPosn = this.screenPosn(this.topology().vertices().get(66).centroid());
        path.lineTo(screenPosn.x, screenPosn.y);
        screenPosn = this.screenPosn(this.topology().vertices().get(84).centroid());
        path.moveTo(screenPosn.x, screenPosn.y);
        screenPosn = this.screenPosn(this.topology().vertices().get(68).centroid());
        path.lineTo(screenPosn.x, screenPosn.y);
        g2d.draw(path);
    }
}
