// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.components.board;

import game.equipment.container.Container;
import topology.Cell;
import util.Context;
import util.SettingsColour;
import util.state.State;
import util.state.containerState.ContainerState;
import view.container.aspects.components.ContainerComponents;
import view.container.styles.board.Connect4Style;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class Connect4Components extends ContainerComponents
{
    int connect4Rows;
    final Connect4Style boardStyle;
    
    public Connect4Components(final Connect4Style containerStyle) {
        super(containerStyle);
        this.connect4Rows = 6;
        this.boardStyle = containerStyle;
    }
    
    @Override
    public void drawComponents(final Graphics2D g2d, final Context context) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        final List<Cell> cells = this.boardStyle.topology().cells();
        final Rectangle placement = this.boardStyle.placement();
        final String label = "Board";
        final Container equip = context.game().mapContainer().get("Board");
        if (cells.isEmpty()) {
            System.out.println("** Connect4Style.drawStyle(): Board has no cells.");
            return;
        }
        final int u = (int)((cells.get(1).centroid().getX() - cells.get(0).centroid().getX()) * placement.width);
        final int r = (int)(0.425 * u + 0.5);
        if (equip != null) {
            final State state = context.state();
            final ContainerState cs = state.containerStates()[0];
            for (int site = 0; site < this.boardStyle.topology().cells().size(); ++site) {
                final Point2D pixel = cells.get(site).centroid();
                for (int levelNumber = cs.sizeStackCell(site), level = 0; level < levelNumber; ++level) {
                    final int who = cs.whoCell(site, level);
                    if (who == 0) {
                        break;
                    }
                    final int cx = (int)(pixel.getX() * placement.width);
                    final int cy = (int)(pixel.getY() * placement.width + (this.connect4Rows - 1) * u - level * u + placement.y);
                    g2d.setColor(SettingsColour.playerColour(who, context));
                    g2d.fillArc(cx - r, cy - r, 2 * r, 2 * r, 0, 360);
                }
            }
        }
    }
}
