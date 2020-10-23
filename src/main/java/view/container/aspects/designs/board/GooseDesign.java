// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import main.math.MathRoutines;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.Cell;
import util.Context;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.Line2D;

public class GooseDesign extends BoardDesign
{
    public GooseDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final Rectangle placement = this.boardStyle.placement();
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.002f;
        final float swThin = (float)Math.max(1, (int)(0.002f * placement.width + 0.5));
        final float swThick = 2.0f * swThin;
        this.setStrokesAndColours(context, new Color(200, 200, 200), null, null, null, null, null, new Color(140, 140, 140), swThin, swThick);
        final int r = (int)(MathRoutines.distance(this.topology().cells().get(0).centroid(), this.topology().cells().get(1).centroid()) * placement.width * 0.475 + 0.5);
        final Color fillColour = new Color(240, 240, 240);
        final Color borderColour = new Color(200, 200, 200);
        g2d.setStroke(new BasicStroke(swThin, 1, 1));
        g2d.setColor(borderColour);
        for (int vid = 0; vid < this.topology().cells().size() - 1; ++vid) {
            final Cell vertexA = this.topology().cells().get(vid);
            final Cell vertexB = this.topology().cells().get(vid + 1);
            final double ax = vertexA.centroid().getX() * placement.width;
            final double ay = placement.width - vertexA.centroid().getY() * placement.width;
            final double bx = vertexB.centroid().getX() * placement.width;
            final double by = placement.width - vertexB.centroid().getY() * placement.width;
            final Shape line = new Line2D.Double(ax, ay, bx, by);
            g2d.draw(line);
        }
        for (int vid = 0; vid < this.topology().cells().size(); ++vid) {
            final Cell vertexA = this.topology().cells().get(vid);
            final double ax2 = vertexA.centroid().getX() * placement.width;
            final double ay2 = placement.width - vertexA.centroid().getY() * placement.width;
            g2d.setColor(fillColour);
            g2d.fillArc((int)ax2 - r, (int)ay2 - r, 2 * r + 1, 2 * r + 1, 0, 360);
            g2d.setColor(borderColour);
            g2d.drawArc((int)ax2 - r, (int)ay2 - r, 2 * r + 1, 2 * r + 1, 0, 360);
        }
        return g2d.getSVGDocument();
    }
}
