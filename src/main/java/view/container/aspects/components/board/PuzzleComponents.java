// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.components.board;

import bridge.Bridge;
import game.equipment.component.Piece;
import game.rules.start.StartRule;
import game.rules.start.deductionPuzzle.Set;
import game.types.play.RoleType;
import gnu.trove.list.array.TIntArrayList;
import topology.TopologyElement;
import util.Context;
import util.ImageInfo;
import util.state.State;
import util.state.containerState.ContainerState;
import view.component.BaseComponentStyle;
import view.component.custom.PieceStyle;
import view.container.aspects.components.ContainerComponents;
import view.container.aspects.designs.board.puzzle.PuzzleDesign;
import view.container.styles.board.puzzle.PuzzleStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.BitSet;

public class PuzzleComponents extends ContainerComponents
{
    protected TIntArrayList initialValues;
    final PuzzleStyle puzzleStyle;
    PuzzleDesign puzzleDesign;
    
    public PuzzleComponents(final PuzzleStyle containerStyle, final PuzzleDesign containerDesign) {
        super(containerStyle);
        this.initialValues = new TIntArrayList();
        this.puzzleStyle = containerStyle;
        this.puzzleDesign = containerDesign;
    }
    
    @Override
    public void drawComponents(final Graphics2D g2d, final Context context) {
        if (this.initialValues.size() == 0 && context.game().rules().start() != null) {
            final StartRule[] rules;
            final StartRule[] startRules = rules = context.game().rules().start().rules();
            for (final StartRule startRule : rules) {
                if (startRule.isSet()) {
                    final Set setRule = (Set)startRule;
                    for (final Integer site : setRule.vars()) {
                        this.initialValues.add(site);
                    }
                }
            }
        }
        final State state = context.state();
        final ContainerState cs = state.containerStates()[0];
        for (int site2 = 0; site2 < this.puzzleStyle.topology().getGraphElements(context.board().defaultSite()).size(); ++site2) {
            final TopologyElement element = this.puzzleStyle.topology().getGraphElements(context.board().defaultSite()).get(site2);
            final Point2D posn = element.centroid();
            final Point drawPosn = this.puzzleStyle.screenPosn(posn);
            final BitSet values = cs.values(context.board().defaultSite(), site2);
            if (cs.isResolved(site2, context.board().defaultSite())) {
                final int value = values.nextSetBit(0);
                final int dim = this.puzzleStyle.topology().rows(context.board().defaultSite()).size();
                final int bigFontSize = (int)(0.75 * this.puzzleStyle.placement().getHeight() / dim + 0.5);
                final Font bigFont = new Font("Arial", 1, bigFontSize);
                g2d.setFont(bigFont);
                if (this.initialValues.contains(site2)) {
                    g2d.setColor(Color.BLACK);
                }
                else {
                    g2d.setColor(new Color(139, 0, 0));
                }
                final int pieceSize = (int)(this.puzzleStyle.cellRadiusPixels() * 2 * this.pieceScale() * this.puzzleStyle.containerScale());
                this.drawPuzzleValue(value, site2, context, g2d, drawPosn, pieceSize);
            }
        }
    }
    
    @Override
    public void drawPuzzleValue(final int value, final int site, final Context context, final Graphics2D g2d, final Point drawPosn, final int imageSize) {
        final metadata.graphics.Graphics metadataGraphics = context.game().metadata().graphics();
        final String name = metadataGraphics.pieceNameReplacement(1, String.valueOf(value), context, 0);
        if (name != null) {
            final Piece component = new Piece(name, RoleType.P1, null, 0, null, null);
            component.create(context.game());
            component.setIndex(value);
            final BaseComponentStyle componentStyle = new PieceStyle(component);
            componentStyle.renderImageSVG(context, imageSize, 0, false, 0);
            Bridge.graphicsRenderer().drawSVG(g2d, componentStyle.getImageSVG(0), new ImageInfo(drawPosn, site, 0, context.board().defaultSite(), component, 0, 0.0, 0, 1, imageSize, 1), componentStyle);
        }
        else {
            final String str = "" + value;
            final Rectangle bounds = g2d.getFontMetrics().getStringBounds(str, g2d).getBounds();
            g2d.drawString(str, drawPosn.x - bounds.width / 2, drawPosn.y + bounds.height / 3);
        }
    }
}
