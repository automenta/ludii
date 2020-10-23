// 
// Decompiled by Procyon v0.5.36
// 

package topology;

import game.types.board.SiteType;
import math.Point3D;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Vertex extends TopologyElement implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final List<Cell> cells;
    private final List<Edge> edges;
    private Vertex pivot;
    private final List<Vertex> orthogonal;
    private final List<Vertex> diagonal;
    private final List<Vertex> off;
    private final List<Vertex> adjacent;
    private final List<Vertex> neighbours;
    
    public Vertex(final int index, final double x, final double y, final double z) {
        this.cells = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.pivot = null;
        this.orthogonal = new ArrayList<>();
        this.diagonal = new ArrayList<>();
        this.off = new ArrayList<>();
        this.adjacent = new ArrayList<>();
        this.neighbours = new ArrayList<>();
        this.index = index;
        this.centroid = new Point3D(x, y, z);
    }
    
    public List<Cell> cells() {
        return this.cells;
    }
    
    public List<Edge> edges() {
        return this.edges;
    }
    
    public Vertex pivot() {
        return this.pivot;
    }
    
    public void setPivot(final Vertex vertex) {
        this.pivot = vertex;
    }
    
    @Override
    public String toString() {
        return "Vertex: " + this.index;
    }
    
    public void optimiseMemory() {
        ((ArrayList)this.cells).trimToSize();
        ((ArrayList)this.edges).trimToSize();
    }
    
    @Override
    public SiteType elementType() {
        return SiteType.Vertex;
    }
    
    @Override
    public List<Vertex> orthogonal() {
        return this.orthogonal;
    }
    
    @Override
    public List<Vertex> diagonal() {
        return this.diagonal;
    }
    
    @Override
    public List<Vertex> off() {
        return this.off;
    }
    
    @Override
    public List<Vertex> adjacent() {
        return this.adjacent;
    }
    
    @Override
    public List<Vertex> neighbours() {
        return this.neighbours;
    }
    
    public boolean neighbour(final int vid) {
        for (final Vertex v : this.diagonal) {
            if (v.index() == vid) {
                return true;
            }
        }
        for (final Vertex v : this.orthogonal) {
            if (v.index() == vid) {
                return true;
            }
        }
        return false;
    }
    
    public int orthogonalOutDegree() {
        return this.orthogonal.size();
    }
}
