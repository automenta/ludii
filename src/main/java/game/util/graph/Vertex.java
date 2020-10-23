// 
// Decompiled by Procyon v0.5.36
// 

package game.util.graph;

import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import math.MathRoutines;
import math.Point3D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vertex extends GraphElement
{
    private final List<Edge> edges;
    private final List<Face> faces;
    private Vertex pivot;
    
    public Vertex(final int id, final double x, final double y) {
        this.edges = new ArrayList<>();
        this.faces = new ArrayList<>();
        this.id = id;
        this.pt = new Point3D(x, y);
    }
    
    public Vertex(final int id, final double x, final double y, final double z) {
        this.edges = new ArrayList<>();
        this.faces = new ArrayList<>();
        this.id = id;
        this.pt = new Point3D(x, y, z);
    }
    
    public Vertex(final int id, final Point3D pt) {
        this.edges = new ArrayList<>();
        this.faces = new ArrayList<>();
        this.pivot = null;
        this.id = id;
        this.pt = new Point3D(pt);
    }
    
    public Vertex(final int id, final Point2D pt) {
        this.edges = new ArrayList<>();
        this.faces = new ArrayList<>();
        this.pivot = null;
        this.id = id;
        this.pt = new Point3D(pt.getX(), pt.getY());
    }
    
    public List<Edge> edges() {
        return Collections.unmodifiableList(this.edges);
    }
    
    public List<Face> faces() {
        return this.faces;
    }
    
    @Override
    public Vertex pivot() {
        return this.pivot;
    }
    
    public void setPivot(final Vertex vertex) {
        this.pivot = vertex;
    }
    
    public void clearEdges() {
        this.edges.clear();
    }
    
    public void addEdge(final Edge edge) {
        this.edges.add(edge);
    }
    
    public void removeEdge(final int n) {
        this.edges.remove(n);
    }
    
    public void clearFaces() {
        this.faces.clear();
    }
    
    public void addFace(final Face face) {
        this.faces.add(face);
    }
    
    public void removeFace(final int n) {
        this.faces.remove(n);
    }
    
    @Override
    public SiteType siteType() {
        return SiteType.Vertex;
    }
    
    public int edgePosition(final Edge edge) {
        for (int n = 0; n < this.edges.size(); ++n) {
            if (this.edges.get(n).matches(edge)) {
                return n;
            }
        }
        return -1;
    }
    
    public int facePosition(final Face face) {
        for (int n = 0; n < this.faces.size(); ++n) {
            if (this.faces.get(n).id() == face.id()) {
                return n;
            }
        }
        return -1;
    }
    
    public Edge incidentEdge(final Vertex other) {
        for (final Edge edge : this.edges) {
            if (edge.matches(this, other)) {
                return edge;
            }
        }
        return null;
    }
    
    public boolean coincident(final Vertex other, final double tolerance) {
        return this.coincident(other.pt.x(), other.pt.y(), other.pt.z(), tolerance);
    }
    
    public boolean coincident(final double x, final double y, final double z, final double tolerance) {
        final double error = Math.abs(x - this.pt.x()) + Math.abs(y - this.pt.y()) + Math.abs(z - this.pt.z());
        return error < tolerance;
    }
    
    public Vertex edgeAwayFrom(final Face face) {
        for (final Edge edge : this.edges) {
            if (!face.contains(edge)) {
                return edge.otherVertex(this.id);
            }
        }
        return null;
    }
    
    void sortEdges() {
        this.edges.sort((a, b) -> {
            final Vertex va = a.otherVertex(Vertex.this.id());
            final Vertex vb = b.otherVertex(Vertex.this.id());
            final double dirnA = Math.atan2(va.pt.y() - Vertex.this.pt.y(), va.pt.x() - Vertex.this.pt.x());
            final double dirnB = Math.atan2(vb.pt.y() - Vertex.this.pt.y(), vb.pt.x() - Vertex.this.pt.x());
            if (dirnA == dirnB) {
                return 0;
            }
            return (dirnA < dirnB) ? -1 : 1;
        });
    }
    
    void sortFaces() {
        this.faces.sort((a, b) -> {
            final double dirnA = Math.atan2(a.pt.y() - Vertex.this.pt.y(), a.pt.x() - Vertex.this.pt.x());
            final double dirnB = Math.atan2(b.pt.y() - Vertex.this.pt.y(), b.pt.x() - Vertex.this.pt.x());
            if (dirnA == dirnB) {
                return 0;
            }
            return (dirnA < dirnB) ? -1 : 1;
        });
    }
    
    @Override
    public List<GraphElement> nbors() {
        final List<GraphElement> nbors = new ArrayList<>();
        for (final Edge edge : this.edges) {
            nbors.add(edge.otherVertex(this.id));
        }
        return nbors;
    }
    
    @Override
    public void stepsTo(final Steps steps2) {
        for (final Edge edge : this.edges) {
            final Vertex to = edge.otherVertex(this.id);
            final Step newStep = new Step(this, to);
            newStep.directions().set(AbsoluteDirection.Orthogonal.ordinal());
            newStep.directions().set(AbsoluteDirection.Adjacent.ordinal());
            newStep.directions().set(AbsoluteDirection.All.ordinal());
            steps2.add(newStep);
        }
        for (final Face face : this.faces) {
            if (face.vertices().size() < 4) {
                continue;
            }
            double bestDist = 1000000.0;
            Vertex bestTo = null;
            for (final Vertex to2 : face.vertices()) {
                if (to2.id() == this.id) {
                    continue;
                }
                final double dist = MathRoutines.distanceToLine(face.pt2D(), this.pt2D(), to2.pt2D());
                if (dist >= bestDist) {
                    continue;
                }
                bestDist = dist;
                bestTo = to2;
            }
            final Step newStep2 = new Step(this, bestTo);
            newStep2.directions().set(AbsoluteDirection.Diagonal.ordinal());
            newStep2.directions().set(AbsoluteDirection.All.ordinal());
            steps2.add(newStep2);
        }
        for (final Edge edge : this.edges) {
            final Step newStep3 = new Step(this, edge);
            newStep3.directions().set(AbsoluteDirection.Orthogonal.ordinal());
            newStep3.directions().set(AbsoluteDirection.Adjacent.ordinal());
            newStep3.directions().set(AbsoluteDirection.All.ordinal());
            steps2.add(newStep3);
        }
        for (final Face face : this.faces) {
            final Step newStep3 = new Step(this, face);
            newStep3.directions().set(AbsoluteDirection.Orthogonal.ordinal());
            newStep3.directions().set(AbsoluteDirection.Adjacent.ordinal());
            newStep3.directions().set(AbsoluteDirection.All.ordinal());
            steps2.add(newStep3);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Vertex[").append(this.id).append("]: (");
        if (this.pt.x() == (int)this.pt.x() && this.pt.y() == (int)this.pt.y()) {
            sb.append(String.format("%d,%d", (int)this.pt.x(), (int)this.pt.y()));
        }
        else {
            sb.append(String.format("%.3f,%.3f", this.pt.x(), this.pt.y()));
        }
        if (this.pt.z() == (int)this.pt.z()) {
            sb.append(String.format(",%d", (int)this.pt.z()));
        }
        else {
            sb.append(String.format(",%.3f", this.pt.z()));
        }
        sb.append(")");
        if (this.pivot != null) {
            sb.append(" pivot=").append(this.pivot.id());
        }
        sb.append(" [");
        for (int e = 0; e < this.edges.size(); ++e) {
            if (e > 0) {
                sb.append(" ");
            }
            sb.append(this.edges.get(e).id());
        }
        sb.append("]");
        sb.append(" ").append(this.properties);
        sb.append(" \"").append(situation.label()).append("\"");
        return sb.toString();
    }
}
