// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.Context;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;

public class UltimateTicTacToeDesign extends BoardDesign
{
    public UltimateTicTacToeDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swThin = (float)Math.max(1.0, 0.005 * this.boardStyle.placement().width + 0.5);
        final float swThick = 2.0f * swThin;
        this.setStrokesAndColours(context, new Color(50, 150, 255), null, new Color(180, 230, 255), new Color(0, 175, 0), new Color(230, 50, 20), new Color(0, 100, 200), null, swThin, swThick);
        this.drawBoard(g2d);
        return g2d.getSVGDocument();
    }
    
    protected void drawBoard(final Graphics2D g2d) {
        final int dots = (int)(0.9 * this.boardStyle.container().topology().cells().size());
        final int dim = (int)Math.sqrt(dots);
        final Point ptMid = this.screenPosn(this.topology().cells().get(dots / 2).centroid());
        final Point pt0 = this.screenPosn(this.topology().cells().get(0).centroid());
        final Point pt2 = this.screenPosn(this.topology().cells().get(1).centroid());
        final int unit = Math.abs(pt2.x - pt0.x);
        g2d.setColor(new Color(200, 220, 255));
        g2d.setStroke(this.strokeThin);
        final int x0 = ptMid.x - 5 * unit + unit / 2;
        final int y0 = ptMid.y - 5 * unit + unit / 2;
        final double off = 0.15;
        for (int n = 1; n < dim; ++n) {
            int ax = x0 + unit * n;
            int ay = y0 + unit * 0;
            int bx = ax;
            int by = y0 + (int)(unit * 2.85);
            g2d.drawLine(ax, ay, bx, by);
            ax = x0 + unit * n;
            ay = y0 + (int)(unit * 3.15);
            bx = ax;
            by = y0 + (int)(unit * 5.85);
            g2d.drawLine(ax, ay, bx, by);
            ax = x0 + unit * n;
            ay = y0 + (int)(unit * 6.15);
            bx = ax;
            by = y0 + unit * dim;
            g2d.drawLine(ax, ay, bx, by);
            ax = x0 + unit * 0;
            ay = y0 + unit * n;
            bx = x0 + (int)(unit * 2.85);
            by = ay;
            g2d.drawLine(ax, ay, bx, by);
            ax = x0 + (int)(unit * 3.15);
            ay = y0 + unit * n;
            bx = x0 + (int)(unit * 5.85);
            by = ay;
            g2d.drawLine(ax, ay, bx, by);
            ax = x0 + (int)(unit * 6.15);
            ay = y0 + unit * n;
            bx = x0 + unit * dim;
            by = ay;
            g2d.drawLine(ax, ay, bx, by);
        }
        g2d.setColor(new Color(20, 100, 200));
        g2d.setStroke(this.strokeThick());
        for (int n = 3; n < dim; n += 3) {
            int ax = x0 + n * unit;
            int ay = y0 + 0 * unit;
            int bx = x0 + n * unit;
            int by = y0 + dim * unit;
            g2d.drawLine(ax, ay, bx, by);
            ax = x0 + 0 * unit;
            ay = y0 + n * unit;
            bx = x0 + dim * unit;
            by = y0 + n * unit;
            g2d.drawLine(ax, ay, bx, by);
        }
    }
}
