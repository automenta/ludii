// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board.puzzle;

import game.types.board.SiteType;
import metadata.graphics.util.PuzzleHintType;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import topology.Cell;
import topology.Edge;
import topology.TopologyElement;
import util.Context;
import util.Move;
import util.action.puzzle.ActionSet;
import util.locations.FullLocation;
import util.locations.Location;
import util.state.State;
import util.state.containerState.ContainerState;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PuzzleDesign extends BoardDesign
{
    protected ArrayList<Integer> hintValues;
    protected ArrayList<Location> locationValues;
    protected boolean cornerHints;
    protected final int MAX_CANDIDATE_DISPLAY_NUMBER = 9;
    protected ArrayList<int[]> regions;
    protected ArrayList<int[]> hintRegions;
    protected PuzzleHintType hintType;
    
    public PuzzleDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
        this.hintValues = null;
        this.locationValues = new ArrayList<>();
        this.regions = new ArrayList<>();
        this.hintRegions = new ArrayList<>();
        this.hintType = PuzzleHintType.Default;
    }
    
    @Override
    public String createSVGImage(final Context context) {
        final SVGGraphics2D g2d = this.boardStyle.setSVGRenderingValues();
        final double boardLineThickness = this.boardStyle.cellRadiusPixels() / 15.0;
        this.checkeredBoard = context.game().metadata().graphics().checkeredBoard();
        this.straightLines = context.game().metadata().graphics().straightRingLines();
        final float swThick;
        final float swThin = swThick = (float)Math.max(1.0, boardLineThickness);
        this.setStrokesAndColours(context, new Color(120, 190, 240), new Color(120, 190, 240), new Color(210, 230, 255), new Color(210, 0, 0), new Color(0, 230, 0), new Color(0, 0, 255), new Color(0, 0, 0), swThin, swThick);
        this.drawGround(g2d, context, true);
        this.fillCells(g2d);
        this.drawInnerCellEdges(g2d, context);
        this.drawOuterCellEdges(g2d, context);
        this.setSymbols(context);
        this.drawSymbolCellEdges(g2d, context, this.strokeThick());
        this.drawSymbols(g2d);
        if (context.game().isDeductionPuzzle() && context.game().metadata().graphics().showRegionOwner()) {
            this.drawRegions(g2d, context, this.colorDecoration(), this.strokeThick, this.hintRegions);
        }
        this.drawGround(g2d, context, false);
        return g2d.getSVGDocument();
    }
    
    protected Integer findHintPosInRegion(final Integer[] regionCellsIndeces, final Context context, final boolean preferLeft) {
        if (regionCellsIndeces.length == 1) {
            return regionCellsIndeces[0];
        }
        Double highestRow = null;
        Double lowestCellIndex = null;
        Integer bestCellFound = null;
        for (final Integer cellIndex : regionCellsIndeces) {
            final Point2D posn = context.topology().getGraphElements(context.board().defaultSite()).get(cellIndex).centroid();
            final double cellX = posn.getX();
            final double cellY = posn.getY();
            if (highestRow == null && lowestCellIndex == null) {
                highestRow = posn.getY();
                lowestCellIndex = posn.getX();
                bestCellFound = cellIndex;
            }
            else if (lowestCellIndex != null && highestRow != null && (cellX <= lowestCellIndex & cellY >= highestRow.intValue())) {
                highestRow = posn.getY();
                lowestCellIndex = posn.getX();
                bestCellFound = cellIndex;
            }
            else if (lowestCellIndex != null && preferLeft && cellX < lowestCellIndex.intValue()) {
                highestRow = posn.getY();
                lowestCellIndex = posn.getX();
                bestCellFound = cellIndex;
            }
            else if (highestRow != null && !preferLeft && cellY > highestRow.intValue()) {
                highestRow = posn.getY();
                lowestCellIndex = posn.getX();
                bestCellFound = cellIndex;
            }
        }
        return bestCellFound;
    }
    
    protected void detectHints(final Context context) {
        this.hintValues = new ArrayList<>();
        this.hintType = context.game().metadata().graphics().hintType();
        if (context.game().rules().phases()[0].play().moves().isConstraintsMoves() && context.game().equipment().cellHints() != null) {
            for (int numHints = context.game().equipment().cellHints().length, i = 0; i < numHints; ++i) {
                this.locationValues.add(new FullLocation(this.findHintPosInRegion(context.game().equipment().cellsWithHints()[i], context, true), 0, SiteType.Cell));
                this.hintValues.add(context.game().equipment().cellHints()[i]);
            }
        }
        if (context.game().rules().phases()[0].play().moves().isConstraintsMoves() && context.game().equipment().vertexHints() != null) {
            for (int numHints = context.game().equipment().vertexHints().length, i = 0; i < numHints; ++i) {
                this.locationValues.add(new FullLocation(this.findHintPosInRegion(context.game().equipment().verticesWithHints()[i], context, true), 0, SiteType.Vertex));
                this.hintValues.add(context.game().equipment().vertexHints()[i]);
            }
        }
        if (context.game().rules().phases()[0].play().moves().isConstraintsMoves() && context.game().equipment().edgeHints() != null) {
            for (int numHints = context.game().equipment().edgeHints().length, i = 0; i < numHints; ++i) {
                this.locationValues.add(new FullLocation(this.findHintPosInRegion(context.game().equipment().edgesWithHints()[i], context, true), 0, SiteType.Edge));
                this.hintValues.add(context.game().equipment().edgeHints()[i]);
            }
        }
        for (int numHints = context.game().equipment().cellsWithHints().length, i = 0; i < numHints; ++i) {
            final int[] convertedArray = Arrays.stream(context.game().equipment().cellsWithHints()[i]).mapToInt(Integer::intValue).toArray();
            this.hintRegions.add(convertedArray);
        }
    }
    
    @Override
    public void drawPuzzleCandidates(final Graphics2D g2d, final Context context) {
        final Font oldFont = g2d.getFont();
        final int minPuzzleValue = context.board().getRange(context.board().defaultSite()).min();
        final int maxPuzzleValue = context.board().getRange(context.board().defaultSite()).max();
        final int valueRange = maxPuzzleValue - minPuzzleValue + 1;
        final int base = (int)Math.sqrt(Math.max(9, valueRange));
        final int bigFontSize = (int)(0.75 * this.boardStyle.placement().getHeight() / Math.max(9, valueRange) + 0.5);
        final int smallFontSize = (int)(0.25 * bigFontSize + 0.5);
        final Font smallFont = new Font(oldFont.getFontName(), 0, smallFontSize);
        final State state = context.state();
        final ContainerState cs = state.containerStates()[0];
        final double u = this.boardStyle.cellRadius() * this.boardStyle.placement().getHeight();
        final double off = 0.6 * u;
        g2d.setFont(smallFont);
        final Point2D.Double[] offsets = new Point2D.Double[valueRange];
        for (int n = 0; n < valueRange; ++n) {
            final int row = n / base;
            final int col = n % base;
            final double x = (col - 0.5 * (base - 1)) * off;
            final double y = (row - 0.5 * (base - 1)) * off;
            offsets[n] = new Point2D.Double(x, y);
        }
        for (int site = 0; site < this.topology().getGraphElements(context.board().defaultSite()).size(); ++site) {
            final TopologyElement vertex = this.topology().cells().get(site);
            final Point2D posn = vertex.centroid();
            final int cx = (int)(posn.getX() * this.boardStyle.placement().width + 0.5);
            final int cy = this.boardStyle.placement().height - (int)(posn.getY() * this.boardStyle.placement().height + 0.5);
            if (!cs.isResolved(site, context.board().defaultSite())) {
                for (int b = 0; b <= valueRange; ++b) {
                    ActionSet a = null;
                    a = new ActionSet(context.board().defaultSite(), site, b + minPuzzleValue);
                    a.setDecision(true);
                    final Move m = new Move(a);
                    m.setFromNonDecision(site);
                    m.setToNonDecision(site);
                    m.setEdgeMove(site);
                    m.setDecision(true);
                    if (context.game().moves(context).moves().contains(m)) {
                        if (cs.bit(site, b + minPuzzleValue, context.board().defaultSite())) {
                            final int tx = (int)(cx + offsets[b].getX() + 0.5) + this.boardStyle.placement().x;
                            final int ty = (int)(cy + offsets[b].getY() + 0.5) + this.boardStyle.placement().y;
                            final Point drawPosn = new Point(tx, ty);
                            this.boardStyle.drawPuzzleValue(b + minPuzzleValue, site, context, g2d, drawPosn, (int)(this.cellRadiusPixels() / base * 1.5));
                        }
                    }
                }
            }
        }
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
                    if (this.hintType == PuzzleHintType.TopLeft) {
                        final Font valueFont = new Font("Arial", 1, (int)(this.boardStyle.cellRadiusPixels() / 1.5));
                        g2d.setColor(Color.BLACK);
                        g2d.setFont(valueFont);
                        final Rectangle2D rect = g2d.getFont().getStringBounds(Integer.toString(this.hintValues.get(i)), g2d.getFontRenderContext());
                        g2d.drawString(this.hintValues.get(i).toString(), (int)(drawnPosn.x - this.boardStyle.cellRadiusPixels() / 1.3), (int)(drawnPosn.y - rect.getHeight() / 4.0));
                    }
                    else {
                        final Font valueFont = new Font("Arial", 1, this.boardStyle.cellRadiusPixels());
                        g2d.setColor(Color.BLACK);
                        g2d.setFont(valueFont);
                        final Rectangle2D rect = g2d.getFont().getStringBounds(Integer.toString(this.hintValues.get(i)), g2d.getFontRenderContext());
                        g2d.drawString(this.hintValues.get(i).toString(), (int)(drawnPosn.x - rect.getWidth() / 2.0), (int)(drawnPosn.y + rect.getHeight() / 4.0));
                    }
                }
            }
        }
    }
    
    protected void drawRegions(final Graphics2D g2d, final Context context, final Color borderColor, final BasicStroke stroke, final ArrayList<int[]> regionList) {
        final List<Cell> cells = this.topology().cells();
        final ArrayList<Edge> regionLines = new ArrayList<>();
        final ArrayList<Edge> outsideRegionLines = new ArrayList<>();
        for (final int[] region : regionList) {
            regionLines.clear();
            for (final int r : region) {
                outsideRegionLines.clear();
                regionLines.addAll(cells.get(r).edges());
            }
            for (final Edge edge1 : regionLines) {
                int numContains = 0;
                for (final Edge edge2 : regionLines) {
                    if (Math.abs(edge1.vA().centroid().getX() - edge2.vA().centroid().getX()) < 1.0E-4 && Math.abs(edge1.vB().centroid().getX() - edge2.vB().centroid().getX()) < 1.0E-4 && Math.abs(edge1.vA().centroid().getY() - edge2.vA().centroid().getY()) < 1.0E-4 && Math.abs(edge1.vB().centroid().getY() - edge2.vB().centroid().getY()) < 1.0E-4) {
                        ++numContains;
                    }
                    else {
                        if (Math.abs(edge1.vA().centroid().getX() - edge2.vB().centroid().getX()) >= 1.0E-4 || Math.abs(edge1.vB().centroid().getX() - edge2.vA().centroid().getX()) >= 1.0E-4 || Math.abs(edge1.vA().centroid().getY() - edge2.vB().centroid().getY()) >= 1.0E-4 || Math.abs(edge1.vB().centroid().getY() - edge2.vA().centroid().getY()) >= 1.0E-4) {
                            continue;
                        }
                        ++numContains;
                    }
                }
                if (numContains == 1) {
                    outsideRegionLines.add(edge1);
                }
            }
            this.drawEdges(g2d, context, borderColor, stroke, outsideRegionLines);
        }
    }
    
    public boolean cornerHints() {
        return this.cornerHints;
    }
    
    public void setCornerHints(final boolean cornerHints) {
        this.cornerHints = cornerHints;
    }
}
