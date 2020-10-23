// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.Context;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.Board.TablePlacement;
import view.container.styles.board.TableStyle;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class TableDesign extends BoardDesign
{
    TableStyle tableStyle;
    TablePlacement tablePlacement;
    private final Color[] boardColours;
    
    public TableDesign(final TableStyle boardStyle, final TablePlacement boardPlacement) {
        super(boardStyle, boardPlacement);
        this.boardColours = new Color[] { new Color(153, 76, 0), new Color(223, 178, 110) };
        this.tableStyle = boardStyle;
        this.tablePlacement = boardPlacement;
    }
    
    @Override
    public String createSVGImage(final Context context) {
        this.boardPlacement.customiseGraphElementLocations(context);
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        this.setStrokesAndColours(context, new Color(120, 190, 240), new Color(125, 75, 0), new Color(210, 230, 255), null, null, null, new Color(0, 0, 0), (float)Math.max(1, (int)(0.0025 * this.boardStyle.placement().width + 0.5)), (float)(int)(2.0 * Math.max(1, (int)(0.0025 * this.boardStyle.placement().width + 0.5))));
        this.drawTableBoard(g2d);
        return g2d.getSVGDocument();
    }
    
    void drawTableBoard(final Graphics2D g2d) {
        final Point pt0 = this.screenPosn(this.topology().vertices().get(0).centroid());
        final Point pt2 = this.screenPosn(this.topology().vertices().get(1).centroid());
        final int unit;
        final int off = unit = pt2.x - pt0.x;
        final Point ptBottomLeftRight = this.screenPosn(this.topology().vertices().get(this.tablePlacement.homeSize() - 1).centroid());
        final Point ptBottomRightLeft = this.screenPosn(this.topology().vertices().get(this.tablePlacement.homeSize()).centroid());
        final Point ptBottomRightRight = this.screenPosn(this.topology().vertices().get(this.tablePlacement.homeSize() * 2 - 1).centroid());
        final Point ptTopLeftLeft = this.screenPosn(this.topology().vertices().get(this.tablePlacement.homeSize() * 2).centroid());
        final Point ptTopRightLeft = this.screenPosn(this.topology().vertices().get(this.tablePlacement.homeSize() * 3).centroid());
        final int pr = (int)(unit * 0.5);
        final int borderX = (int)(unit * 0.2);
        final int borderY = unit * 0;
        final int diameterCircle = unit;
        final int gapYCircle = (int)(diameterCircle * 0.7);
        final int topLeftLeftX = ptTopLeftLeft.x - pr;
        final int topLeftLeftY = ptTopLeftLeft.y - pr;
        final int topRightLeftX = ptTopRightLeft.x - pr;
        final int topRightLeftY = ptTopRightLeft.y - pr;
        final int bottomLeftRightX = ptBottomLeftRight.x + pr;
        final int bottomLeftRightY = ptBottomLeftRight.y + pr;
        final int bottomRightLeftX = ptBottomRightLeft.x + pr;
        final int bottomRightRightX = ptBottomRightRight.x + pr;
        final int bottomRightRightY = ptBottomRightRight.y + pr;
        final int topLeftBorderX = topLeftLeftX - borderX;
        final int topLeftBorderY = topLeftLeftY - borderY;
        final int bottomRightBorderX = bottomRightRightX + borderX;
        final int bottomRightBorderY = bottomRightRightY + borderY;
        g2d.setColor(this.boardColours[1]);
        g2d.fillRect(topLeftBorderX, topLeftBorderY, Math.abs(bottomRightBorderX - topLeftBorderX), Math.abs(bottomRightBorderY - topLeftBorderY));
        g2d.setColor(this.boardColours[0]);
        g2d.fillRect(topLeftLeftX, topLeftLeftY + gapYCircle, Math.abs(bottomLeftRightX - topLeftLeftX), Math.abs(bottomLeftRightY - gapYCircle - (topLeftLeftY + gapYCircle)));
        g2d.fillRect(topRightLeftX, topRightLeftY + gapYCircle, Math.abs(bottomRightRightX - topRightLeftX), Math.abs(bottomRightRightY - gapYCircle - (topRightLeftY + gapYCircle)));
        final int bottomMiddleY = bottomLeftRightY - (int)(Math.abs(topRightLeftY - bottomLeftRightY) * 0.65);
        final int sizeXMiddle = Math.abs(bottomLeftRightX - bottomRightLeftX);
        final int sizeYMiddle = (int)Math.abs((bottomRightRightY - topLeftLeftY) * 0.35);
        g2d.fillRect(bottomLeftRightX, bottomMiddleY, sizeXMiddle, sizeYMiddle);
        g2d.setColor(this.boardColours[1]);
        final double offErrorMiddleCircle = 1.025;
        final Ellipse2D.Double topMiddleCircle = new Ellipse2D.Double(bottomLeftRightX, bottomLeftRightY - (int)(Math.abs(topRightLeftY - bottomLeftRightY) * 0.7), diameterCircle * 1.025, diameterCircle * 1.025);
        g2d.fill(topMiddleCircle);
        final Ellipse2D.Double bottomMiddleCircle = new Ellipse2D.Double(bottomLeftRightX, bottomLeftRightY - (int)(Math.abs(topRightLeftY - bottomLeftRightY) * 0.35), diameterCircle * 1.025, diameterCircle * 1.025);
        g2d.fill(bottomMiddleCircle);
        g2d.setColor(this.boardColours[0]);
        final double offErrorCircle = 0.99;
        final int halfSize = this.topology().vertices().size() / 2;
        for (int n = 0; n < halfSize; ++n) {
            final Point ptVertex = this.screenPosn(this.topology().vertices().get(n).centroid());
            final Ellipse2D.Double circle = new Ellipse2D.Double(ptVertex.x - pr, ptVertex.y - gapYCircle, diameterCircle * 0.99, diameterCircle * 0.99);
            g2d.fill(circle);
        }
        for (int n = halfSize; n < halfSize * 2; ++n) {
            final Point ptVertex = this.screenPosn(this.topology().vertices().get(n).centroid());
            final Ellipse2D.Double circle = new Ellipse2D.Double(ptVertex.x - pr, ptVertex.y - gapYCircle / 2, diameterCircle * 0.99, diameterCircle * 0.99);
            g2d.fill(circle);
        }
    }
}
