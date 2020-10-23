// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.components.board;

import game.equipment.container.Container;
import game.equipment.container.board.Board;
import game.equipment.container.board.custom.MancalaBoard;
import game.equipment.other.Map;
import game.types.board.SiteType;
import game.types.board.StoreType;
import graphics.ImageProcessing;
import math.MathRoutines;
import topology.Topology;
import util.Context;
import util.SettingsColour;
import util.state.State;
import util.state.containerState.ContainerState;
import view.container.aspects.components.ContainerComponents;
import view.container.styles.BoardStyle;
import view.container.styles.board.MancalaStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class MancalaComponents extends ContainerComponents
{
    final BoardStyle boardStyle;
    private final Color seedColour;
    private final Point2D.Double[][] offsets;
    
    public MancalaComponents(final MancalaStyle containerStyle) {
        super(containerStyle);
        this.seedColour = new Color(255, 255, 230);
        this.offsets = new Point2D.Double[][] { new Point2D.Double[0], { new Point2D.Double(0.0, 0.0) }, { new Point2D.Double(-1.0, 0.0), new Point2D.Double(1.0, 0.0) }, { new Point2D.Double(-1.0, -0.8), new Point2D.Double(1.0, -0.8), new Point2D.Double(0.0, 1.0) }, { new Point2D.Double(-1.0, -1.0), new Point2D.Double(1.0, -1.0), new Point2D.Double(-1.0, 1.0), new Point2D.Double(1.0, 1.0) }, { new Point2D.Double(-1.0, -1.0), new Point2D.Double(1.0, -1.0), new Point2D.Double(-1.0, 1.0), new Point2D.Double(1.0, 1.0), new Point2D.Double(0.0, 0.0) } };
        this.boardStyle = containerStyle;
    }
    
    @Override
    public void drawComponents(final Graphics2D g2d, final Context context) {
        final Rectangle placement = this.boardStyle.placement();
        final int cellRadiusPixels = this.boardStyle.cellRadiusPixels();
        final int indexHoleBL;
        final boolean withStore = (indexHoleBL = ((!(context.board() instanceof MancalaBoard) || ((MancalaBoard) context.board()).storeType() != StoreType.None) ? 1 : 0)) != 0;
        final Point ptA = this.boardStyle.screenPosn(this.boardStyle.topology().vertices().get(indexHoleBL).centroid());
        final Point ptB = this.boardStyle.screenPosn(this.boardStyle.topology().vertices().get(indexHoleBL + 1).centroid());
        final double unit = MathRoutines.distance(ptA, ptB);
        final int seedRadius = Math.max(1, (int)(0.19 * unit));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        final Board board = context.board();
        final Topology graph = board.topology();
        final String label = "Board";
        final Container equip = context.game().mapContainer().get("Board");
        final Color shadeBase = this.seedColour;
        final Color shadeDark = MathRoutines.shade(shadeBase, 0.75);
        final Color shadeLight = MathRoutines.shade(shadeBase, 1.5);
        if (equip == null) {
            return;
        }
        final State state = context.state();
        final ContainerState cs = state.containerStates()[0];
        for (int site = 0; site < graph.vertices().size(); ++site) {
            final Point pt = this.boardStyle.screenPosn(graph.vertices().get(site).centroid());
            final int count = cs.count(site, SiteType.Vertex);
            final int cx = pt.x;
            final int cy = pt.y;
            final int swRing = (int)(this.boardStyle.cellRadius() * placement.width / 10.0);
            final BasicStroke strokeRink = new BasicStroke(swRing, 0, 0);
            g2d.setStroke(strokeRink);
            if (context.game().metadata().graphics().showPlayerHoles()) {
                for (int i = 0; i < context.players().size(); ++i) {
                    if (state.getValue(i) == site) {
                        final int r = cellRadiusPixels;
                        g2d.setColor(SettingsColour.playerColour(i, context));
                        g2d.drawArc(cx - r, cy - r, 2 * r, 2 * r, 0, 360);
                    }
                }
            }
            if (context.game().metadata().graphics().showPits() && context.game().equipment().maps().length != 0) {
                final Map map = context.game().equipment().maps()[0];
                for (int p = 1; p < context.players().size(); ++p) {
                    final int ownedSite = map.to(p);
                    if (ownedSite == site) {
                        final int r2 = cellRadiusPixels;
                        g2d.setColor(SettingsColour.playerColour(p, context));
                        g2d.drawArc(cx - r2, cy - r2, 2 * r2, 2 * r2, 0, 360);
                    }
                }
            }
            if (count > 0) {
                for (int group = Math.min(count, this.offsets.length - 1), s = 0; s < this.offsets[group].length; ++s) {
                    final Point2D.Double off = this.offsets[group][s];
                    final int x = cx + (int)(off.x * seedRadius + 0.5) - seedRadius + 1;
                    final int y = cy - (int)(off.y * seedRadius + 0.5) - seedRadius + 1;
                    ImageProcessing.ballImage(g2d, x, y, seedRadius, new Color(255, 255, 230));
                }
                if (count > 5) {
                    final Font oldFont = g2d.getFont();
                    final Font font = new Font(oldFont.getFontName(), 1, (int)(0.45 * this.boardStyle.cellRadius() * placement.width));
                    g2d.setFont(font);
                    final String str = Integer.toString(count);
                    final Rectangle2D bounds = font.getStringBounds(str, g2d.getFontRenderContext());
                    final int tx = cx - (int)(0.5 * bounds.getWidth() + 0.5);
                    final int ty = cy + (int)(0.5 * bounds.getHeight() + 0.5) - 4;
                    g2d.setColor(Color.black);
                    g2d.drawString(str, tx, ty - 1);
                    g2d.setColor(shadeLight);
                    g2d.drawString(str, tx, ty + 1);
                    g2d.setColor(shadeDark);
                    g2d.drawString(str, tx, ty);
                    g2d.setFont(oldFont);
                }
            }
        }
    }
}
