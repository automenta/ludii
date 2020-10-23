// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.placement.Board;

import game.equipment.component.Component;
import topology.Cell;
import topology.Edge;
import topology.Vertex;
import util.Context;
import util.state.State;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class BoardlessPlacement extends BoardPlacement
{
    private State currentState;
    private long currentStateHash;
    private List<Cell> zoomedCells;
    private List<Edge> zoomedEdges;
    private List<Vertex> zoomedVertices;
    protected double zoom;
    
    public BoardlessPlacement(final BoardStyle containerStyle) {
        super(containerStyle);
        this.currentStateHash = -1L;
        this.zoom = 1.0;
    }
    
    @Override
    public List<Cell> drawnCells() {
        final List<Cell> drawnCells = new ArrayList<>();
        for (final Cell vOri : this.topology().cells()) {
            if (this.currentState.containerStates()[0].isPlayable(vOri.index()) || this.currentState.containerStates()[0].isOccupied(vOri.index())) {
                final Cell newCell = this.zoomedCells.get(vOri.index());
                final ArrayList<Vertex> newVertices = new ArrayList<>();
                for (final Vertex v : vOri.vertices()) {
                    newVertices.add(this.zoomedVertices.get(v.index()));
                }
                newCell.setVertices(newVertices);
                drawnCells.add(newCell);
            }
            else {
                final Cell newCell = new Cell(vOri.index(), -1.0, -1.0, -1.0);
                drawnCells.add(newCell);
            }
        }
        return drawnCells;
    }
    
    @Override
    public List<Edge> drawnEdges() {
        final List<Edge> drawnEdges = new ArrayList<>();
        for (final Edge eOri : this.topology().edges()) {
            for (final Cell c : eOri.cells()) {
                if (this.currentState.containerStates()[0].isPlayable(c.index()) || this.currentState.containerStates()[0].isOccupied(c.index())) {
                    final Edge e = this.zoomedEdges.get(eOri.index());
                    drawnEdges.add(e);
                }
            }
        }
        return drawnEdges;
    }
    
    @Override
    public List<Vertex> drawnVertices() {
        final List<Vertex> drawnVertices = new ArrayList<>();
        for (final Vertex fOri : this.topology().vertices()) {
            for (final Cell v : fOri.cells()) {
                if (this.currentState.containerStates()[0].isPlayable(v.index()) || this.currentState.containerStates()[0].isOccupied(v.index())) {
                    drawnVertices.add(this.zoomedVertices.get(fOri.index()));
                    break;
                }
            }
        }
        return drawnVertices;
    }
    
    public static Point2D.Double applyZoomToPoint(final Point2D point, final double zoomAmount, final Point2D.Double centerPoint) {
        final Point2D.Double zoomedPoint = new Point2D.Double();
        zoomedPoint.x = 0.5 + (point.getX() - centerPoint.x) * zoomAmount;
        zoomedPoint.y = 0.5 + (point.getY() - centerPoint.y) * zoomAmount;
        return zoomedPoint;
    }
    
    public double setZoomedLocations(final Context context) {
        int numberOccupiedCells = 0;
        double minX = 99999.0;
        double minY = 99999.0;
        double maxX = -99999.0;
        double maxY = -99999.0;
        if (this.currentState != null) {
            for (final Cell vertex : this.topology().cells()) {
                if (this.currentState.containerStates()[0].isPlayable(vertex.index()) || this.currentState.containerStates()[0].isOccupied(vertex.index())) {
                    ++numberOccupiedCells;
                    if (vertex.centroid().getX() < minX) {
                        minX = vertex.centroid().getX();
                    }
                    if (vertex.centroid().getX() > maxX) {
                        maxX = vertex.centroid().getX();
                    }
                    if (vertex.centroid().getY() < minY) {
                        minY = vertex.centroid().getY();
                    }
                    if (vertex.centroid().getY() <= maxY) {
                        continue;
                    }
                    maxY = vertex.centroid().getY();
                }
            }
        }
        minX -= this.cellRadius();
        minY -= this.cellRadius();
        maxX += this.cellRadius();
        maxY += this.cellRadius();
        double newZoom = 1.0;
        double centerPointX = 0.5;
        double centerPointY = 0.5;
        if (numberOccupiedCells > 0) {
            final double boardZoomX = 1.0 / (maxX - minX);
            final double boardZoomY = 1.0 / (maxY - minY);
            newZoom = Math.min(boardZoomX, boardZoomY);
            centerPointX = (maxX + minX) / 2.0;
            centerPointY = (maxY + minY) / 2.0;
        }
        int largestPieceWalk = 1;
        for (final Component component : context.components()) {
            if (component != null && component.isLargePiece()) {
                final int stepsForward = component.maxStepsForward();
                if (largestPieceWalk < stepsForward) {
                    largestPieceWalk = stepsForward;
                }
            }
        }
        final double maxZoom = 10.0 / largestPieceWalk;
        if (newZoom > maxZoom) {
            newZoom = maxZoom;
        }
        final List<Cell> graphCells = this.topology().cells();
        final List<Edge> graphEdges = this.topology().edges();
        final List<Vertex> graphVertices = this.topology().vertices();
        if (this.zoomedVertices == null || this.zoomedEdges == null || this.zoomedCells == null) {
            this.zoomedCells = new ArrayList<>(this.topology().cells().size());
            this.zoomedEdges = new ArrayList<>(this.topology().edges().size());
            this.zoomedVertices = new ArrayList<>(this.topology().vertices().size());
        }
        else {
            this.zoomedCells.clear();
            this.zoomedEdges.clear();
            this.zoomedVertices.clear();
        }
        for (int i = 0; i < graphCells.size(); ++i) {
            final Cell cell = graphCells.get(i);
            final Point2D.Double zoomedPoint = applyZoomToPoint(cell.centroid(), newZoom, new Point2D.Double(centerPointX, centerPointY));
            final Cell zoomedCell = new Cell(i, zoomedPoint.x, zoomedPoint.y, 0.0);
            zoomedCell.setOrthogonal(cell.orthogonal());
            zoomedCell.setDiagonal(cell.diagonal());
            zoomedCell.setOff(cell.off());
            this.zoomedCells.add(zoomedCell);
        }
        for (int i = 0; i < graphEdges.size(); ++i) {
            final Edge edge = graphEdges.get(i);
            final Point2D.Double zoomedPointA = applyZoomToPoint(edge.vA().centroid(), newZoom, new Point2D.Double(centerPointX, centerPointY));
            final Point2D.Double zoomedPointB = applyZoomToPoint(edge.vB().centroid(), newZoom, new Point2D.Double(centerPointX, centerPointY));
            final Edge zoomedEdge = new Edge(i, new Vertex(edge.vA().index(), zoomedPointA.x, zoomedPointA.y, 0.0), new Vertex(edge.vB().index(), zoomedPointB.x, zoomedPointB.y, 0.0));
            this.zoomedEdges.add(zoomedEdge);
        }
        for (int i = 0; i < graphVertices.size(); ++i) {
            final Vertex vertex2 = graphVertices.get(i);
            final Point2D.Double zoomedPoint = applyZoomToPoint(vertex2.centroid(), newZoom, new Point2D.Double(centerPointX, centerPointY));
            final Vertex zoomedVertex = new Vertex(i, zoomedPoint.x, zoomedPoint.y, 0.0);
            this.zoomedVertices.add(zoomedVertex);
        }
        return newZoom;
    }
    
    public void calculateZoom(final Context context) {
        this.currentState = context.state();
        if (this.currentStateHash != this.currentState.stateHash()) {
            this.currentStateHash = this.currentState.stateHash();
            this.zoom = this.setZoomedLocations(context);
        }
    }
    
    @Override
    public double containerZoom() {
        return this.zoom;
    }
    
    public void updateZoomImage(final Context context) {
        this.calculateZoom(context);
        this.setCellRadiusPixels((int)(this.cellRadius() * this.placement().width * this.containerZoom()));
    }
}
