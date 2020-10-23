// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board.puzzle;

import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.Cell;
import topology.TopologyElement;
import util.Context;
import util.locations.FullLocation;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class KakuroDesign extends PuzzleDesign
{
    protected ArrayList<Boolean> kakuroDirection;
    
    public KakuroDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
        this.kakuroDirection = new ArrayList<>();
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.005f;
        final float swThin = (float)Math.max(1, (int)(0.005f * this.boardStyle.placement().width + 0.5));
        final float swThick = 2.0f * swThin;
        this.setStrokesAndColours(context, new Color(120, 190, 240), null, new Color(210, 230, 255), null, null, null, new Color(120, 190, 240), swThin, swThick);
        if (this.hintValues == null) {
            this.detectHints(context);
        }
        final TIntArrayList blackLocations = new TIntArrayList();
        final TIntArrayList varsConstraints = context.game().constraintVariables();
        for (final Cell c : context.board().topology().cells()) {
            if (varsConstraints.contains(c.index())) {
                blackLocations.add(c.index());
            }
        }
        this.fillCells(g2d, this.boardStyle.placement().width, this.colorFillPhase0, this.colorInner, this.strokeThin, blackLocations, Color.BLACK, true);
        this.drawInnerCellEdges(g2d, context);
        this.drawOuterCellEdges(g2d, context);
        return g2d.getSVGDocument();
    }
    
    @Override
    protected void detectHints(final Context context) {
        this.hintValues = new ArrayList<>();
        if (context.game().rules().phases()[0].play().moves().isConstraintsMoves() && context.game().equipment().cellHints() != null) {
            for (int numHints = context.game().equipment().cellHints().length, i = 0; i < numHints; ++i) {
                if (context.game().equipment().cellsWithHints()[i][0].equals(context.game().equipment().cellsWithHints()[i][1] - 1)) {
                    this.kakuroDirection.add(true);
                }
                else {
                    this.kakuroDirection.add(false);
                }
                this.locationValues.add(new FullLocation(this.findHintPosInRegion(context.game().equipment().cellsWithHints()[i], context, this.kakuroDirection.get(i)), 0, SiteType.Cell));
                this.hintValues.add(context.game().equipment().cellHints()[i]);
            }
        }
    }
    
    @Override
    public void drawPuzzleHints(final Graphics2D g2d, final Context context) {
        if (this.hintValues == null) {
            this.detectHints(context);
        }
        final Font valueFont = new Font("Arial", 1, this.boardStyle.cellRadiusPixels());
        g2d.setColor(Color.WHITE);
        g2d.setFont(valueFont);
        for (final TopologyElement graphElement : this.topology().getAllGraphElements()) {
            final SiteType type = graphElement.elementType();
            final int site = graphElement.index();
            final Point2D posn = graphElement.centroid();
            final Point drawnPosn = this.screenPosn(posn);
            for (int i = 0; i < this.hintValues.size(); ++i) {
                if (this.locationValues.get(i).site() == site && this.locationValues.get(i).siteType() == type) {
                    int maxHintvalue = 0;
                    for (int j = 0; j < this.hintValues.size(); ++j) {
                        if (this.hintValues.get(i) != null && this.hintValues.get(i) > maxHintvalue) {
                            maxHintvalue = this.hintValues.get(i);
                        }
                    }
                    if (maxHintvalue > 9) {
                        g2d.setFont(new Font("Arial", 1, (int)(this.boardStyle.cellRadiusPixels() / 1.5)));
                    }
                    final Rectangle2D rect = g2d.getFont().getStringBounds(Integer.toString(this.hintValues.get(i)), g2d.getFontRenderContext());
                    if (this.kakuroDirection.get(i)) {
                        g2d.drawString(this.hintValues.get(i).toString(), (int)(drawnPosn.x - rect.getWidth() / 2.0 - this.cellRadiusPixels() * 1.5), (int)(drawnPosn.y + rect.getHeight() / 4.0 - this.cellRadiusPixels() * 0.3));
                    }
                    else {
                        g2d.drawString(this.hintValues.get(i).toString(), (int)(drawnPosn.x - rect.getWidth() / 2.0 - this.cellRadiusPixels() * 0.5), (int)(drawnPosn.y + rect.getHeight() / 4.0 - this.cellRadiusPixels() * 1.5));
                    }
                }
            }
        }
    }
}
