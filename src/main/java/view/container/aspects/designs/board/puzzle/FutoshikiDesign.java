// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board.puzzle;

import game.types.board.SiteType;
import topology.Edge;
import topology.TopologyElement;
import topology.Vertex;
import util.Context;
import util.locations.FullLocation;
import view.container.aspects.designs.board.graph.GraphDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class FutoshikiDesign extends GraphDesign
{
    ArrayList<Integer> arrowDirections;
    
    public FutoshikiDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement, false, false);
        this.arrowDirections = new ArrayList<>();
    }
    
    @Override
    protected void detectHints(final Context context) {
        this.hintValues = new ArrayList<>();
        if (context.game().rules().phases()[0].play().moves().isConstraintsMoves() && context.game().equipment().vertexHints() != null) {
            for (int numHints = context.game().equipment().vertexHints().length, i = 0; i < numHints; ++i) {
                this.locationValues.add(new FullLocation(this.findHintPosInRegion(context.game().equipment().verticesWithHints()[i], context, false), 0, SiteType.Edge));
                this.hintValues.add(context.game().equipment().vertexHints()[i]);
            }
        }
    }
    
    @Override
    protected Integer findHintPosInRegion(final Integer[] regionCellsIndeces, final Context context, final boolean preferLeft) {
        if (regionCellsIndeces.length == 1) {
            return regionCellsIndeces[0];
        }
        int bestEdgeFoundIndex = 0;
        final Point2D posnA = context.topology().getGraphElements(context.board().defaultSite()).get(regionCellsIndeces[0]).centroid();
        final Point2D posnB = context.topology().getGraphElements(context.board().defaultSite()).get(regionCellsIndeces[1]).centroid();
        final Point2D midPoint = new Point2D.Double((posnA.getX() + posnB.getX()) / 2.0, (posnA.getY() + posnB.getY()) / 2.0);
        double lowestDistance = 9.9999999E7;
        for (final Edge e : context.board().topology().edges()) {
            final double edgeDistance = Math.hypot(midPoint.getX() - e.centroid().getX(), midPoint.getY() - e.centroid().getY());
            if (edgeDistance < lowestDistance) {
                lowestDistance = edgeDistance;
                bestEdgeFoundIndex = e.index();
            }
        }
        if (Math.abs(posnA.getX() - posnB.getX()) < Math.abs(posnA.getY() - posnB.getY())) {
            if (posnA.getY() < posnB.getY()) {
                this.arrowDirections.add(1);
            }
            else {
                this.arrowDirections.add(3);
            }
        }
        else if (posnA.getX() < posnB.getX()) {
            this.arrowDirections.add(0);
        }
        else {
            this.arrowDirections.add(2);
        }
        return bestEdgeFoundIndex;
    }
    
    @Override
    public void drawPuzzleHints(final Graphics2D g2d, final Context context) {
        if (this.hintValues == null) {
            this.detectHints(context);
        }
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
                            break;
                        }
                    }
                    Font valueFont = new Font("Arial", 1, this.boardStyle.cellRadiusPixels());
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(valueFont);
                    Rectangle2D rect = g2d.getFont().getStringBounds("^", g2d.getFontRenderContext());
                    if (this.arrowDirections.get(i) == 0) {
                        rect = g2d.getFont().getStringBounds("<", g2d.getFontRenderContext());
                        g2d.drawString("<", (int)(drawnPosn.x - rect.getWidth() / 2.0), (int)(drawnPosn.y + rect.getHeight() / 3.0));
                    }
                    else if (this.arrowDirections.get(i) == 1) {
                        rect = g2d.getFont().getStringBounds("^", g2d.getFontRenderContext());
                        g2d.drawString("^", (int)(drawnPosn.x - rect.getWidth() / 2.0), (int)(drawnPosn.y + rect.getHeight() / 2.0));
                    }
                    else if (this.arrowDirections.get(i) == 2) {
                        rect = g2d.getFont().getStringBounds(">", g2d.getFontRenderContext());
                        g2d.drawString(">", (int)(drawnPosn.x - rect.getWidth() / 2.0), (int)(drawnPosn.y + rect.getHeight() / 3.0));
                    }
                    else if (this.arrowDirections.get(i) == 3) {
                        valueFont = new Font("Arial", 1, -this.boardStyle.cellRadiusPixels());
                        g2d.setFont(valueFont);
                        g2d.drawString("^", (int)(drawnPosn.x + rect.getWidth() / 2.0), (int)(drawnPosn.y - rect.getHeight() / 2.0));
                    }
                }
            }
        }
    }
    
    @Override
    protected void drawVertices(final Graphics2D g2d, final Context context, final double radius) {
        for (final Vertex vertex : this.topology().vertices()) {
            final int squareSize = (int)(this.boardStyle.cellRadiusPixels() * 1.2);
            g2d.setColor(this.colorOuter);
            g2d.setStroke(new BasicStroke(this.strokeThick.getLineWidth(), 1, 0));
            final Point pt = this.boardStyle.screenPosn(vertex.centroid());
            g2d.drawRect(pt.x - squareSize / 2, pt.y - squareSize / 2, squareSize, squareSize);
        }
    }
}
