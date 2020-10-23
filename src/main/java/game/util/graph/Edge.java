// 
// Decompiled by Procyon v0.5.36
// 

package game.util.graph;

import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import math.Point3D;
import math.Vector;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Edge extends GraphElement
{
    private Vertex vertexA;
    private Vertex vertexB;
    private Face left;
    private Face right;
    private Vector tangentA;
    private Vector tangentB;
    
    public Edge(final int id, final Vertex va, final Vertex vb) {
        this.vertexA = null;
        this.vertexB = null;
        this.left = null;
        this.right = null;
        this.tangentA = null;
        this.tangentB = null;
        this.id = id;
        this.vertexA = va;
        this.vertexB = vb;
        this.setMidpoint();
    }
    
    public Vertex vertexA() {
        return this.vertexA;
    }
    
    public void setVertexA(final Vertex vertA) {
        this.vertexA = vertA;
        this.setMidpoint();
    }
    
    public Vertex vertexB() {
        return this.vertexB;
    }
    
    public void setVertexB(final Vertex vertB) {
        this.vertexB = vertB;
        this.setMidpoint();
    }
    
    public Face left() {
        return this.left;
    }
    
    public void setLeft(final Face face) {
        this.left = face;
    }
    
    public Face right() {
        return this.right;
    }
    
    public void setRight(final Face face) {
        this.right = face;
    }
    
    public boolean curved() {
        return this.tangentA != null && this.tangentB != null;
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
    
    @Override
    public Vertex pivot() {
        if (this.vertexA.pivot() != null) {
            return this.vertexA.pivot();
        }
        return this.vertexB.pivot();
    }
    
    @Override
    public SiteType siteType() {
        return SiteType.Edge;
    }
    
    public boolean contains(final Vertex vertex) {
        return this.contains(vertex.id());
    }
    
    public boolean contains(final int vid) {
        return this.vertexA.id() == vid || this.vertexB.id() == vid;
    }
    
    public boolean isExterior() {
        return this.left == null || this.right == null;
    }
    
    public double length() {
        return this.vertexA.pt().distance(this.vertexB.pt());
    }
    
    public boolean matches(final Edge other) {
        return this.matches(other.vertexA.id(), other.vertexB.id());
    }
    
    public boolean matches(final Vertex vc, final Vertex vd) {
        return this.matches(vc.id(), vd.id());
    }
    
    public boolean matches(final int idA, final int idB) {
        return (this.vertexA.id() == idA && this.vertexB.id() == idB) || (this.vertexA.id() == idB && this.vertexB.id() == idA);
    }
    
    public boolean matches(final int idA, final int idB, final boolean curved) {
        return ((this.vertexA.id() == idA && this.vertexB.id() == idB) || (this.vertexA.id() == idB && this.vertexB.id() == idA)) && this.curved() == curved;
    }
    
    public boolean coincidentVertices(final Edge other, final double tolerance) {
        return (this.vertexA.coincident(other.vertexA, tolerance) && this.vertexB.coincident(other.vertexB, tolerance)) || (this.vertexA.coincident(other.vertexB, tolerance) && this.vertexB.coincident(other.vertexA, tolerance));
    }
    
    public boolean sharesVertex(final Edge other) {
        return this.sharesVertex(other.vertexA().id()) || this.sharesVertex(other.vertexB().id());
    }
    
    public boolean sharesVertex(final Vertex v) {
        return this.sharesVertex(v.id());
    }
    
    public boolean sharesVertex(final int vid) {
        return this.vertexA.id() == vid || this.vertexB.id() == vid;
    }
    
    public Vertex otherVertex(final int vid) {
        if (vid == this.vertexA.id()) {
            return this.vertexB;
        }
        if (vid == this.vertexB.id()) {
            return this.vertexA;
        }
        return null;
    }
    
    public Vertex otherVertex(final Vertex v) {
        return this.otherVertex(v.id());
    }
    
    public Face otherFace(final int fid) {
        if (this.left != null && this.left.id() == fid) {
            return this.right;
        }
        if (this.right != null && this.right.id() == fid) {
            return this.left;
        }
        return null;
    }
    
    public Face otherFace(final Face face) {
        return this.otherFace(face.id());
    }
    
    public void setMidpoint() {
        this.pt = new Point3D((this.vertexA.pt.x() + this.vertexB.pt.x()) * 0.5, (this.vertexA.pt.y() + this.vertexB.pt.y()) * 0.5, (this.vertexA.pt.z() + this.vertexB.pt.z()) * 0.5);
    }
    
    @Override
    public List<GraphElement> nbors() {
        final List<GraphElement> nbors = new ArrayList<>();
        for (final Edge edgeA : this.vertexA.edges()) {
            if (edgeA.id() != this.id) {
                nbors.add(edgeA);
            }
        }
        for (final Edge edgeB : this.vertexB.edges()) {
            if (edgeB.id() != this.id) {
                nbors.add(edgeB);
            }
        }
        return nbors;
    }
    
    @Override
    public void stepsTo(final Steps steps) {
        for (int v = 0; v < 2; ++v) {
            final Vertex vertex = (v == 0) ? this.vertexA : this.vertexB;
            for (final Edge other : vertex.edges()) {
                if (other.id == this.id) {
                    continue;
                }
                Step newStep = new Step(this, other);
                newStep.directions().set(AbsoluteDirection.Orthogonal.ordinal());
                newStep.directions().set(AbsoluteDirection.Adjacent.ordinal());
                newStep.directions().set(AbsoluteDirection.All.ordinal());
                steps.add(newStep);
            }
        }
        for (int v = 0; v < 2; ++v) {
            final Vertex vertex = (v == 0) ? this.vertexA : this.vertexB;
            final Step newStep2 = new Step(this, vertex);
            newStep2.directions().set(AbsoluteDirection.Orthogonal.ordinal());
            newStep2.directions().set(AbsoluteDirection.Adjacent.ordinal());
            newStep2.directions().set(AbsoluteDirection.All.ordinal());
            steps.add(newStep2);
        }
        final BitSet usedFaces = new BitSet();
        if (this.left != null) {
            usedFaces.set(this.left.id(), true);
            final Step newStep3 = new Step(this, this.left);
            newStep3.directions().set(AbsoluteDirection.Orthogonal.ordinal());
            newStep3.directions().set(AbsoluteDirection.Adjacent.ordinal());
            newStep3.directions().set(AbsoluteDirection.All.ordinal());
            steps.add(newStep3);
        }
        if (this.right != null) {
            usedFaces.set(this.right.id(), true);
            final Step newStep3 = new Step(this, this.right);
            newStep3.directions().set(AbsoluteDirection.Orthogonal.ordinal());
            newStep3.directions().set(AbsoluteDirection.Adjacent.ordinal());
            newStep3.directions().set(AbsoluteDirection.All.ordinal());
            steps.add(newStep3);
        }
        for (int v2 = 0; v2 < 2; ++v2) {
            final Vertex vertex2 = (v2 == 0) ? this.vertexA : this.vertexB;
            for (final Face face : vertex2.faces()) {
                if (usedFaces.get(face.id())) {
                    continue;
                }
                usedFaces.set(face.id(), true);
                final Step newStep4 = new Step(this, face);
                newStep4.directions().set(AbsoluteDirection.Orthogonal.ordinal());
                newStep4.directions().set(AbsoluteDirection.Adjacent.ordinal());
                newStep4.directions().set(AbsoluteDirection.All.ordinal());
                steps.add(newStep4);
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Edge[").append(this.id).append("]: ").append(this.vertexA.id()).append(" => ").append(this.vertexB.id());
        if (this.left != null) {
            sb.append(" L=").append(this.left.id());
        }
        if (this.right != null) {
            sb.append(" R=").append(this.right.id());
        }
        sb.append(" ").append(this.properties);
        if (this.tangentA != null) {
            sb.append(" ").append(this.tangentA);
        }
        if (this.tangentB != null) {
            sb.append(" ").append(this.tangentB);
        }
        sb.append(" \"").append(this.situation.label()).append("\"");
        return sb.toString();
    }
}
