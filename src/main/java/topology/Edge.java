// 
// Decompiled by Procyon v0.5.36
// 

package topology;

import game.types.board.RelationType;
import game.types.board.SiteType;
import math.Point3D;
import math.Vector;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public final class Edge extends TopologyElement implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Vertex[] vertices;
    private final List<Cell> cells;
    private final List<Edge> adjacent;
    private Vector tangentA;
    private Vector tangentB;
    private BitSet doesCross;
    
    public Edge(final Vertex v0, final Vertex v1) {
        this.vertices = new Vertex[2];
        this.cells = new ArrayList<>();
        this.adjacent = new ArrayList<>();
        this.tangentA = null;
        this.tangentB = null;
        this.doesCross = new BitSet();
        this.index = -1;
        this.vertices[0] = v0;
        this.vertices[1] = v1;
        final double x = (this.vertices[0].centroid3D().x() + this.vertices[1].centroid3D().x()) / 2.0;
        final double y = (this.vertices[0].centroid3D().y() + this.vertices[1].centroid3D().y()) / 2.0;
        final double z = (this.vertices[0].centroid3D().z() + this.vertices[1].centroid3D().z()) / 2.0;
        this.centroid = new Point3D(x, y, z);
    }
    
    public Edge(final int index, final Vertex v0, final Vertex v1) {
        this.vertices = new Vertex[2];
        this.cells = new ArrayList<>();
        this.adjacent = new ArrayList<>();
        this.tangentA = null;
        this.tangentB = null;
        this.doesCross = new BitSet();
        this.index = index;
        this.vertices[0] = v0;
        this.vertices[1] = v1;
        final double x = (this.vertices[0].centroid3D().x() + this.vertices[1].centroid3D().x()) / 2.0;
        final double y = (this.vertices[0].centroid3D().y() + this.vertices[1].centroid3D().y()) / 2.0;
        final double z = (this.vertices[0].centroid3D().z() + this.vertices[1].centroid3D().z()) / 2.0;
        this.centroid = new Point3D(x, y, z);
    }
    
    public void setDoesCross(final BitSet doesCross) {
        this.doesCross = doesCross;
    }
    
    public void setDoesCross(final int indexEdge) {
        this.doesCross.set(indexEdge);
    }
    
    public boolean doesCross(final int edge) {
        return edge >= 0 && edge < this.doesCross.size() && this.doesCross.get(edge);
    }
    
    public Vertex vertex(final int which) {
        return this.vertices[which];
    }
    
    public Vertex vA() {
        return this.vertices[0];
    }
    
    public Vertex vB() {
        return this.vertices[1];
    }
    
    public Vertex otherVertex(final Vertex v) {
        if (this.vertices[0] == v) {
            return this.vertices[1];
        }
        if (this.vertices[1] == v) {
            return this.vertices[0];
        }
        return null;
    }
    
    public Vector tangentA() {
        return this.tangentA;
    }
    
    public void setTangentA(final Vector vec) {
        this.tangentA = vec;
    }
    
    public Vector tangentB() {
        return this.tangentB;
    }
    
    public void setTangentB(final Vector vec) {
        this.tangentB = vec;
    }
    
    public boolean toA() {
        return false;
    }
    
    public boolean toB() {
        return true;
    }
    
    public RelationType type() {
        if (this.vA().orthogonal().contains(this.vB())) {
            return RelationType.Orthogonal;
        }
        if (this.vA().diagonal().contains(this.vB())) {
            return RelationType.Diagonal;
        }
        return RelationType.OffDiagonal;
    }
    
    public List<Cell> cells() {
        return this.cells;
    }
    
    public List<Vertex> vertices() {
        return Arrays.asList(this.vertices);
    }
    
    public boolean containsVertex(final int indexV) {
        return indexV == this.vA().index() || this.vB().index() == indexV;
    }
    
    public boolean matches(final Vertex va, final Vertex vb) {
        return (va.index() == this.vertices[0].index() && vb.index() == this.vertices[1].index()) || (va.index() == this.vertices[1].index() && vb.index() == this.vertices[0].index());
    }
    
    public boolean matches(final Point2D pa, final Point2D pb) {
        return (pa == this.vertices[0].centroid() && pb == this.vertices[1].centroid()) || (pa == this.vertices[1].centroid() && pb == this.vertices[0].centroid());
    }
    
    @Override
    public String toString() {
        final String str = "Edge(" + this.vertices[0].index() + "-" + this.vertices[1].index() + ")";
        return str;
    }
    
    public void optimiseMemory() {
        ((ArrayList)this.cells).trimToSize();
    }
    
    @Override
    public SiteType elementType() {
        return SiteType.Edge;
    }
    
    @Override
    public String label() {
        return String.valueOf(this.index);
    }
    
    @Override
    public List<Edge> orthogonal() {
        return this.adjacent;
    }
    
    @Override
    public List<Edge> diagonal() {
        return new ArrayList<>();
    }
    
    @Override
    public List<Edge> off() {
        return new ArrayList<>();
    }
    
    @Override
    public List<Edge> adjacent() {
        return this.adjacent;
    }
    
    @Override
    public List<Edge> neighbours() {
        return this.adjacent;
    }
}
