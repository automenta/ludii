// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.TopologyElement;
import topology.Vertex;
import util.Context;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

public class LascaDesign extends BoardDesign
{
    public LascaDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.005f;
        final float swThin = Math.max(1, (int)(0.005f * this.boardStyle.placement().width + 0.5));
        final float swThick = 2.0f * swThin;
        this.setStrokesAndColours(context, null, null, new Color(200, 200, 200), null, null, new Color(255, 255, 255), null, swThin, swThick);
        final double vertexRadius = this.boardStyle.cellRadiusPixels() * 0.95;
        this.drawBoardOutline(g2d);
        this.drawVertices(g2d, context, vertexRadius);
        return g2d.getSVGDocument();
    }
    
    @Override
    protected void drawVertices(final Graphics2D g2d, final Context context, final double vertexRadius) {
        g2d.setColor(this.colorFillPhase3);
        for (final Vertex vertex : this.topology().vertices()) {
            final Point position = this.screenPosn(vertex.centroid());
            final Shape ellipseO = new Ellipse2D.Double(position.x - vertexRadius, position.y - vertexRadius, 2.0 * vertexRadius, 2.0 * vertexRadius);
            g2d.fill(ellipseO);
        }
    }
    
    @Override
    public void drawBoardOutline(final SVGGraphics2D g2d) {
        g2d.setStroke(this.strokeThin);
        double minX = 9999.0;
        double minY = 9999.0;
        double maxX = -9999.0;
        double maxY = -9999.0;
        final GeneralPath path = new GeneralPath();
        for (final TopologyElement cell : this.topology().vertices()) {
            final Point posn = this.screenPosn(cell.centroid());
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
        g2d.setColor(this.colorFillPhase0);
        final int OuterBufferDistance = (int)(this.cellRadiusPixels() * 1.1);
        path.moveTo(minX - OuterBufferDistance, minY - OuterBufferDistance);
        path.lineTo(minX - OuterBufferDistance, maxY + OuterBufferDistance);
        path.lineTo(maxX + OuterBufferDistance, maxY + OuterBufferDistance);
        path.lineTo(maxX + OuterBufferDistance, minY - OuterBufferDistance);
        path.lineTo(minX - OuterBufferDistance, minY - OuterBufferDistance);
        g2d.fill(path);
    }
}
