// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board.graph;

import game.types.board.RelationType;
import metadata.graphics.util.BoardGraphicsType;
import metadata.graphics.util.EdgeInfoGUI;
import metadata.graphics.util.EdgeType;
import metadata.graphics.util.LineStyle;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.Edge;
import util.Context;
import util.StrokeUtil;
import view.container.aspects.designs.board.puzzle.PuzzleDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;
import view.container.styles.board.graph.GraphStyle;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

public class GraphDesign extends PuzzleDesign
{
    protected boolean drawOrthogonalEdges;
    protected boolean drawDiagonalEdges;
    protected boolean drawOffEdges;
    protected boolean drawOrthogonalConnections;
    protected boolean drawDiagonalConnections;
    protected boolean drawOffConnections;
    
    public GraphDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement, final boolean drawOrthogonals, final boolean drawDiagonals) {
        super(boardStyle, boardPlacement);
        this.drawOffEdges = false;
        this.drawOrthogonalConnections = false;
        this.drawDiagonalConnections = false;
        this.drawOffConnections = false;
        this.drawOrthogonalEdges = drawOrthogonals;
        this.drawDiagonalEdges = drawDiagonals;
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        double vr = 0.1 * this.cellRadiusPixels();
        if (vr < 4.0) {
            vr = 4.0;
        }
        if (vr > 6.0) {
            vr = 6.0;
        }
        ((GraphStyle)this.boardStyle).setBaseVertexRadius(vr * context.game().metadata().graphics().boardThickness(BoardGraphicsType.Vertices));
        ((GraphStyle)this.boardStyle).setBaseLineWidth(0.5 * vr);
        final float swThick;
        final float swThin = swThick = (float)((GraphStyle)this.boardStyle).baseLineWidth();
        this.straightLines = context.game().metadata().graphics().straightRingLines();
        final Color decorationColour = new Color(200, 200, 200);
        this.setStrokesAndColours(context, ((GraphStyle)this.boardStyle).baseGraphColour(), null, null, null, null, null, decorationColour, swThin, swThick);
        this.drawGround(g2d, context, true);
        this.detectHints(context);
        this.drawEdge(g2d, context, this.colorInner, this.strokeThin, EdgeType.Inner, RelationType.Orthogonal, false, this.drawOrthogonalEdges);
        this.drawEdge(g2d, context, this.colorOuter, this.strokeThick(), EdgeType.Outer, RelationType.Orthogonal, false, this.drawOrthogonalEdges);
        this.drawEdge(g2d, context, this.colorInner, StrokeUtil.getDottedStroke(this.strokeThin.getLineWidth()), EdgeType.All, RelationType.Diagonal, false, this.drawDiagonalEdges);
        this.drawEdge(g2d, context, this.colorInner, this.strokeThin, EdgeType.Inner, RelationType.Orthogonal, true, this.drawOrthogonalConnections);
        this.drawEdge(g2d, context, this.colorInner, StrokeUtil.getDottedStroke(this.strokeThin.getLineWidth()), EdgeType.All, RelationType.Diagonal, true, this.drawDiagonalConnections);
        this.drawEdge(g2d, context, this.colorInner, StrokeUtil.getDashedStroke(this.strokeThin.getLineWidth()), EdgeType.All, RelationType.OffDiagonal, true, this.drawOffConnections);
        if (context.metadata().graphics().showEdgeDirections()) {
            for (final Edge edge : this.topology().edges()) {
                this.drawArrowHeads(g2d, this.strokeThin, edge);
            }
        }
        this.drawVertices(g2d, context, ((GraphStyle)this.boardStyle).baseVertexRadius());
        this.setSymbols(context);
        this.drawSymbols(g2d);
        if (context.game().isDeductionPuzzle() && context.game().metadata().graphics().showRegionOwner()) {
            this.drawRegions(g2d, context, this.colorDecoration(), this.strokeThick, this.hintRegions);
        }
        this.drawGround(g2d, context, false);
        return g2d.getSVGDocument();
    }
    
    protected void drawEdge(final Graphics2D g2d, final Context context, final Color defaultLineColour, final Stroke defaultLineStroke, final EdgeType edgeType, final RelationType relationType, final boolean connection, final boolean alwaysDraw) {
        Color lineColour = defaultLineColour;
        Stroke lineStroke = defaultLineStroke;
        final EdgeInfoGUI edgeInfoGUI = context.game().metadata().graphics().drawEdge(edgeType, relationType, connection);
        if (edgeInfoGUI != null) {
            if (edgeInfoGUI.getStyle() == LineStyle.Hidden) {
                return;
            }
            if (edgeInfoGUI.getColour() != null) {
                lineColour = edgeInfoGUI.getColour();
            }
            if (edgeInfoGUI.getStyle() != null) {
                lineStroke = StrokeUtil.getStrokeFromStyle(edgeInfoGUI.getStyle(), this.strokeThin, this.strokeThick());
            }
        }
        if (alwaysDraw || edgeInfoGUI != null) {
            if (relationType.supersetOf(RelationType.Orthogonal)) {
                if (connection) {
                    this.drawOrthogonalConnections(g2d, context, lineColour, lineStroke);
                }
                else {
                    if (edgeType.supersetOf(EdgeType.Inner)) {
                        this.drawInnerCellEdges(g2d, context, lineColour, lineStroke);
                    }
                    if (edgeType.supersetOf(EdgeType.Outer)) {
                        this.drawOuterCellEdges(g2d, context, lineColour, lineStroke);
                    }
                }
            }
            if (relationType.supersetOf(RelationType.Diagonal)) {
                if (connection) {
                    this.drawDiagonalConnections(g2d, context, lineColour, lineStroke);
                }
                else {
                    this.drawDiagonalEdges(g2d, context, lineColour, lineStroke);
                }
            }
            if (relationType.supersetOf(RelationType.OffDiagonal) && connection) {
                this.drawOffDiagonalConnections(g2d, context, lineColour, lineStroke);
            }
        }
    }
    
    protected void drawArrowHeads(final Graphics2D g2d, final BasicStroke stroke, final Edge edge) {
        final Point drawPosnA = this.screenPosn(edge.vA().centroid());
        final Point drawPosnB = this.screenPosn(edge.vB().centroid());
        if (edge.toB()) {
            drawArrowHead(g2d, new Line2D.Double(drawPosnA, drawPosnB), stroke);
        }
        if (edge.toA()) {
            drawArrowHead(g2d, new Line2D.Double(drawPosnB, drawPosnA), stroke);
        }
    }
    
    protected static void drawArrowHead(final Graphics2D g2d, final Line2D line, final BasicStroke stroke) {
        final int strokeWidth = (int)stroke.getLineWidth() * 4;
        final Polygon arrowHead = new Polygon();
        arrowHead.addPoint(0, 0);
        arrowHead.addPoint((int)(-strokeWidth / 1.5), -strokeWidth);
        arrowHead.addPoint((int)(strokeWidth / 1.5), -strokeWidth);
        final AffineTransform tx = new AffineTransform();
        tx.setToIdentity();
        final double angle = Math.atan2(line.getY2() - line.getY1(), line.getX2() - line.getX1());
        tx.translate(line.getX2(), line.getY2());
        tx.rotate(angle - 1.5707963267948966);
        final Graphics2D g = (Graphics2D)g2d.create();
        g.setTransform(tx);
        g.fill(arrowHead);
        g.dispose();
    }
}
