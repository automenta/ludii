// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board.puzzle;

import metadata.graphics.util.PuzzleHintType;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.Cell;
import topology.Edge;
import util.Context;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class SudokuDesign extends PuzzleDesign
{
    public SudokuDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final float swRatio = 0.005f;
        final float swThin = (float)Math.max(1, (int)(0.005f * this.boardStyle.placement().width + 0.5));
        final float swThick = 2.0f * swThin;
        this.setStrokesAndColours(context, new Color(120, 190, 240), new Color(120, 190, 240), new Color(210, 230, 255), null, null, null, new Color(200, 50, 200), swThin, swThick);
        this.detectHints(context);
        this.fillCells(g2d, this.boardStyle.placement().width, this.colorFillPhase0, this.colorInner, this.strokeThin, null, null, false);
        this.drawInnerCellEdges(g2d, context);
        this.drawOuterCellEdges(g2d, context);
        this.drawGridEdges(g2d, this.colorOuter, this.strokeThick());
        final float[] dash1 = { 6.0f };
        final BasicStroke dashed = new BasicStroke(this.strokeThin.getLineWidth(), 0, 0, 5.0f, dash1, 0.0f);
        super.drawRegions(g2d, context, this.colorDecoration(), dashed, this.hintRegions);
        return g2d.getSVGDocument();
    }
    
    @Override
    protected void detectHints(final Context context) {
        super.detectHints(context);
        this.hintType = PuzzleHintType.TopLeft;
    }
    
    protected void drawGridEdges(final Graphics2D g2d, final Color borderColor, final BasicStroke stroke) {
        final List<Cell> cells = this.topology().cells();
        g2d.setColor(borderColor);
        g2d.setStroke(stroke);
        final List<Edge> sudokuEdges = new ArrayList<>();
        final GeneralPath path = new GeneralPath();
        final double boardDimension = Math.sqrt(cells.size());
        final int lineInterval = (int)Math.sqrt(boardDimension);
        for (final Cell cell : cells) {
            for (final Edge edge : cell.edges()) {
                final int columnValue = cell.index() + 1;
                if (columnValue % lineInterval == 0 && columnValue % boardDimension != 0.0 && edge.vA().centroid().getX() > cell.centroid().getX() && edge.vB().centroid().getX() > cell.centroid().getX()) {
                    sudokuEdges.add(edge);
                }
                final int rowLength = (int)Math.sqrt(cells.size());
                final int rowValue = cell.index() / rowLength;
                if (rowValue % lineInterval == lineInterval - 1 && rowValue % (boardDimension - 1.0) != 0.0 && edge.vA().centroid().getY() > cell.centroid().getY() && edge.vB().centroid().getY() > cell.centroid().getY()) {
                    sudokuEdges.add(edge);
                }
            }
        }
        while (sudokuEdges.size() > 0) {
            Edge currentEdge = sudokuEdges.get(0);
            boolean nextEdgeFound = true;
            final Point2D va = currentEdge.vA().centroid();
            Point2D vb = currentEdge.vB().centroid();
            final Point vAPosn = this.screenPosn(va);
            Point vBPosn = this.screenPosn(vb);
            path.moveTo((float)vAPosn.x, (float)vAPosn.y);
            while (nextEdgeFound) {
                nextEdgeFound = false;
                path.lineTo((float)vBPosn.x, (float)vBPosn.y);
                sudokuEdges.remove(currentEdge);
                for (final Edge nextEdge : sudokuEdges) {
                    if (Math.abs(vb.getX() - nextEdge.vA().centroid().getX()) < 1.0E-4 && Math.abs(vb.getY() - nextEdge.vA().centroid().getY()) < 1.0E-4) {
                        nextEdgeFound = true;
                        currentEdge = nextEdge;
                        vb = currentEdge.vB().centroid();
                        vBPosn = this.screenPosn(vb);
                        break;
                    }
                    if (Math.abs(vb.getX() - nextEdge.vB().centroid().getX()) < 1.0E-4 && Math.abs(vb.getY() - nextEdge.vB().centroid().getY()) < 1.0E-4) {
                        nextEdgeFound = true;
                        currentEdge = nextEdge;
                        vb = currentEdge.vA().centroid();
                        vBPosn = this.screenPosn(vb);
                        break;
                    }
                }
            }
        }
        g2d.draw(path);
    }
}
