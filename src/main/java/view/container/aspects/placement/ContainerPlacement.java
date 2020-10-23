// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.placement;

import game.equipment.container.Container;
import game.types.board.SiteType;
import main.math.MathRoutines;
import topology.Cell;
import topology.Edge;
import topology.Topology;
import topology.Vertex;
import util.Context;
import util.GraphUtil;
import view.container.BaseContainerStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class ContainerPlacement
{
    protected BaseContainerStyle containerStyle;
    public double containerScale;
    protected double cellRadius;
    private int cellRadiusPixels;
    protected Rectangle placement;
    private Rectangle unscaledPlacement;
    
    public ContainerPlacement(final BaseContainerStyle containerStyle) {
        this.containerScale = 1.0;
        this.cellRadius = 0.0;
        this.containerStyle = containerStyle;
        this.calculateAverageCellRadius();
    }
    
    public void setPlacement(final Context context, final Rectangle placement) {
        this.setUnscaledPlacement(placement);
    }
    
    public void calculateAverageCellRadius() {
        double min = 1.0;
        if (this.container().defaultSite() == SiteType.Cell) {
            final List<Cell> cells = this.topology().cells();
            if (cells.size() > 0) {
                for (final Cell cell : cells) {
                    final double acc = GraphUtil.calculateCellRadius(cell);
                    if (acc < min) {
                        min = acc;
                    }
                }
            }
        }
        else {
            final List<Vertex> vertices = this.topology().vertices();
            if (vertices.size() > 0) {
                for (final Vertex vertex : vertices) {
                    for (final Vertex v : vertex.neighbours()) {
                        final double dist = MathRoutines.distance(v.centroid(), vertex.centroid()) * 0.5;
                        if (dist > 0.0 && dist < min) {
                            min = dist;
                        }
                    }
                }
            }
        }
        if (min != 0.0 && min != 1.0) {
            this.setCellRadius(min);
        }
        else {
            min = 1.0;
            for (int i = 0; i < this.topology().vertices().size(); ++i) {
                final Point2D.Double vi = new Point2D.Double(this.topology().vertices().get(i).centroid().getX(), this.topology().vertices().get(i).centroid().getY());
                for (int j = i + 1; j < this.topology().vertices().size(); ++j) {
                    final Point2D.Double vj = new Point2D.Double(this.topology().vertices().get(j).centroid().getX(), this.topology().vertices().get(j).centroid().getY());
                    final double dx = vi.x - vj.x;
                    final double dy = vi.y - vj.y;
                    final double dist2 = Math.sqrt(dx * dx + dy * dy);
                    if (min > dist2) {
                        min = dist2;
                    }
                }
            }
            this.setCellRadius(min / 2.0);
        }
    }
    
    public Point screenPosn(final Point2D posn) {
        try {
            final Point screenPosn = new Point();
            screenPosn.x = (int)(this.placement.x + posn.getX() * this.placement.width);
            screenPosn.y = (int)(this.placement.getY() * 2.0 + this.placement.height - (this.placement.y + posn.getY() * this.placement.height));
            return screenPosn;
        }
        catch (Exception e) {
            return new Point((int)posn.getX(), (int)posn.getY());
        }
    }
    
    public double cellRadius() {
        return this.cellRadius;
    }
    
    public int cellRadiusPixels() {
        return this.cellRadiusPixels;
    }
    
    public final Rectangle placement() {
        return this.placement;
    }
    
    public void setCellRadius(final double cellRadius) {
        this.cellRadius = cellRadius;
    }
    
    public Rectangle unscaledPlacement() {
        return this.unscaledPlacement;
    }
    
    public void setUnscaledPlacement(final Rectangle unscaledPlacement) {
        this.unscaledPlacement = unscaledPlacement;
    }
    
    public final double containerScale() {
        return this.containerScale;
    }
    
    public Topology topology() {
        return this.containerStyle.topology();
    }
    
    public Container container() {
        return this.containerStyle.container();
    }
    
    public void setCellRadiusPixels(final int cellRadiusPixels) {
        this.cellRadiusPixels = cellRadiusPixels;
    }
    
    public double containerZoom() {
        return 1.0;
    }
    
    public List<Cell> drawnCells() {
        return this.topology().cells();
    }
    
    public List<Edge> drawnEdges() {
        return this.topology().edges();
    }
    
    public List<Vertex> drawnVertices() {
        return this.topology().vertices();
    }
}
