// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import game.types.board.SiteType;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.Context;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.board.Connect4Style;

import java.awt.*;

public class Connect4Design extends BoardDesign
{
    int connect4Rows;
    Connect4Style connect4Style;
    
    public Connect4Design(final Connect4Style boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
        this.connect4Rows = 6;
        this.connect4Style = boardStyle;
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        this.setStrokesAndColours(context, new Color(120, 190, 240), new Color(125, 75, 0), new Color(210, 230, 255), null, null, null, new Color(0, 0, 0), (float)Math.max(1, (int)(0.0025 * this.boardStyle.placement().width + 0.5)), (float)(int)(2.0 * Math.max(1, (int)(0.0025 * this.boardStyle.placement().width + 0.5))));
        this.drawConnect4Board(g2d);
        this.topology().vertices().clear();
        this.topology().edges().clear();
        return g2d.getSVGDocument();
    }
    
    void drawConnect4Board(final Graphics2D g2d) {
        final int cols = this.topology().columns(SiteType.Cell).size();
        final int rows = this.connect4Rows;
        final int u = this.boardStyle.placement().width / (cols + 1);
        final int r = (int)(0.425 * u + 0.5);
        final int x0 = this.boardStyle.placement().width / 2 - (int)(0.5 * cols * u + 0.5);
        final int y0 = this.boardStyle.placement().width / 2 - (int)(0.5 * rows * u + 0.5);
        final int expand = (int)(0.1 * u);
        g2d.setColor(new Color(0, 100, 200));
        final int corner = u / 4;
        g2d.fillRoundRect(x0 - expand, y0 - expand, cols * u + 2 * expand, rows * u + 2 * expand, corner, corner);
        g2d.setColor(Color.white);
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                final int cx = x0 + col * u + u / 2;
                final int cy = y0 + row * u + u / 2;
                g2d.fillArc(cx - r, cy - r, 2 * r, 2 * r, 0, 360);
            }
        }
    }
    
    @Override
    public boolean ignorePieceSelectionLimit() {
        return true;
    }
}
