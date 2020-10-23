// 
// Decompiled by Procyon v0.5.36
// 

package topology;

import game.types.board.SiteType;
import math.Point3D;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Cell extends TopologyElement implements Serializable
{
    private static final long serialVersionUID = 1L;
    private List<Vertex> vertices;
    private final List<Edge> edges;
    private List<Cell> orthogonal;
    private List<Cell> diagonal;
    private List<Cell> off;
    private final List<Cell> adjacent;
    private final List<Cell> neighbours;
    
    public Cell(final int index, final double x, final double y, final double z) {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.orthogonal = new ArrayList<>();
        this.diagonal = new ArrayList<>();
        this.off = new ArrayList<>();
        this.adjacent = new ArrayList<>();
        this.neighbours = new ArrayList<>();
        this.index = index;
        this.label = String.valueOf(index);
        this.centroid = new Point3D(x, y, z);
    }
    
    @Override
    public List<Cell> orthogonal() {
        return this.orthogonal;
    }
    
    public void setOrthogonal(final List<Cell> orthogonal) {
        this.orthogonal = orthogonal;
    }
    
    @Override
    public List<Cell> diagonal() {
        return this.diagonal;
    }
    
    public void setDiagonal(final List<Cell> diagonal) {
        this.diagonal = diagonal;
    }
    
    @Override
    public List<Cell> neighbours() {
        return this.neighbours;
    }
    
    @Override
    public List<Cell> off() {
        return this.off;
    }
    
    public void setOff(final List<Cell> off) {
        this.off = off;
    }
    
    @Override
    public List<Cell> adjacent() {
        return this.adjacent;
    }
    
    public boolean matchCoord(final int x, final int y) {
        return this.coord.row() == x && this.coord.column() == y;
    }
    
    public boolean matches(final double x, final double y) {
        final double dx = x - this.centroid().getX();
        final double dy = y - this.centroid().getY();
        return Math.abs(dx) < 1.0E-4 && Math.abs(dy) < 1.0E-4;
    }
    
    public boolean matches(final Cell other) {
        final double dx = other.centroid().getX() - this.centroid().getX();
        final double dy = other.centroid().getY() - this.centroid().getY();
        return Math.abs(dx) < 1.0E-4 && Math.abs(dy) < 1.0E-4;
    }
    
    @Override
    public String toString() {
        final String str = "Cell: " + this.index;
        return str;
    }
    
    @Override
    public boolean equals(final Object o) {
        final Cell cell = (Cell)o;
        return cell != null && this.index == cell.index;
    }
    
    @Override
    public int hashCode() {
        return this.index;
    }
    
    public List<Vertex> vertices() {
        return this.vertices;
    }
    
    public void setVertices(final List<Vertex> v) {
        this.vertices = v;
    }
    
    public List<Edge> edges() {
        return this.edges;
    }
    
    public void optimiseMemory() {
        ((ArrayList)this.vertices).trimToSize();
        ((ArrayList)this.edges).trimToSize();
        ((ArrayList)this.orthogonal).trimToSize();
        ((ArrayList)this.diagonal).trimToSize();
        ((ArrayList)this.off).trimToSize();
        ((ArrayList)this.adjacent).trimToSize();
        ((ArrayList)this.neighbours).trimToSize();
    }
    
    @Override
    public SiteType elementType() {
        return SiteType.Cell;
    }
}
