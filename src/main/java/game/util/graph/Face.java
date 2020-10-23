// 
// Decompiled by Procyon v0.5.36
// 

package game.util.graph;

import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import main.math.MathRoutines;
import main.math.Point3D;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class Face extends GraphElement
{
    private final List<Vertex> vertices;
    private final List<Edge> edges;
    
    public Face(final int id) {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.id = id;
    }
    
    public List<Vertex> vertices() {
        return Collections.unmodifiableList(this.vertices);
    }
    
    public List<Edge> edges() {
        return Collections.unmodifiableList(this.edges);
    }
    
    @Override
    public Vertex pivot() {
        for (final Vertex vertex : this.vertices) {
            if (vertex.pivot() != null) {
                return vertex.pivot();
            }
        }
        return null;
    }
    
    @Override
    public SiteType siteType() {
        return SiteType.Cell;
    }
    
    public void addVertexAndEdge(final Vertex vertex, final Edge edge) {
        this.vertices.add(vertex);
        this.edges.add(edge);
        this.setMidpoint();
    }
    
    public boolean matches(final int... vids) {
        final int numSides = this.vertices.size();
        if (vids.length != numSides) {
            return false;
        }
        int start;
        for (start = 0; start < numSides && this.vertices.get(start).id() != vids[0]; ++start) {}
        if (start >= numSides) {
            return false;
        }
        int n;
        for (n = 0; n < numSides; ++n) {
            final int nn = (start + n) % numSides;
            if (this.vertices.get(nn).id() != vids[n]) {
                break;
            }
        }
        if (n >= numSides) {
            return true;
        }
        for (n = 0; n < numSides; ++n) {
            final int nn = (start - n + numSides) % numSides;
            if (this.vertices.get(nn).id() != vids[n]) {
                break;
            }
        }
        return n >= numSides;
    }
    
    public void setMidpoint() {
        double xx = 0.0;
        double yy = 0.0;
        double zz = 0.0;
        if (!this.vertices.isEmpty()) {
            for (final Vertex vertex : this.vertices) {
                xx += vertex.pt().x();
                yy += vertex.pt().y();
                zz += vertex.pt().z();
            }
            xx /= this.vertices.size();
            yy /= this.vertices.size();
            zz /= this.vertices.size();
        }
        this.pt = new Point3D(xx, yy, zz);
    }
    
    public boolean contains(final Vertex vertexIn) {
        for (final Vertex vertex : this.vertices()) {
            if (vertex.id() == vertexIn.id()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean contains(final Edge edgeIn) {
        for (final Edge edge : this.edges()) {
            if (edge.id() == edgeIn.id()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<GraphElement> nbors() {
        final List<GraphElement> nbors = new ArrayList<>();
        for (final Edge edge : this.edges()) {
            final Face nbor = edge.otherFace(this.id);
            if (nbor != null) {
                nbors.add(nbor);
            }
        }
        return nbors;
    }
    
    @Override
    public void stepsTo(final Steps steps) {
        final BitSet usedFaces = new BitSet();
        usedFaces.set(this.id, true);
        for (final Edge edge : this.edges) {
            final Face other = edge.otherFace(this.id);
            if (other == null) {
                continue;
            }
            usedFaces.set(other.id(), true);
            final Step newStep = new Step(this, other);
            newStep.directions().set(AbsoluteDirection.Orthogonal.ordinal());
            newStep.directions().set(AbsoluteDirection.Adjacent.ordinal());
            newStep.directions().set(AbsoluteDirection.All.ordinal());
            steps.add(newStep);
        }
        for (final Vertex vertex : this.vertices) {
            double bestDistance = 1000000.0;
            Face diagonal = null;
            for (final Face other2 : vertex.faces()) {
                if (usedFaces.get(other2.id())) {
                    continue;
                }
                final double dist = MathRoutines.distanceToLine(vertex.pt2D(), this.pt2D(), other2.pt2D());
                if (dist >= bestDistance) {
                    continue;
                }
                bestDistance = dist;
                diagonal = other2;
            }
            if (diagonal == null) {
                continue;
            }
            usedFaces.set(diagonal.id(), true);
            final Step newStep2 = new Step(this, diagonal);
            newStep2.directions().set(AbsoluteDirection.Diagonal.ordinal());
            newStep2.directions().set(AbsoluteDirection.Adjacent.ordinal());
            newStep2.directions().set(AbsoluteDirection.All.ordinal());
            steps.add(newStep2);
        }
        for (final Vertex vertex : this.vertices) {
            for (final Face other3 : vertex.faces()) {
                if (usedFaces.get(other3.id())) {
                    continue;
                }
                usedFaces.set(other3.id(), true);
                final Step newStep3 = new Step(this, other3);
                final BitSet d = newStep3.directions();
                d.set(AbsoluteDirection.OffDiagonal.ordinal());
                d.set(AbsoluteDirection.Adjacent.ordinal());
                d.set(AbsoluteDirection.All.ordinal());
                steps.add(newStep3);
            }
        }
        for (final Vertex vertex : this.vertices) {
            if (vertex.edges().size() != 3) {
                continue;
            }
            final Vertex otherVertex = vertex.edgeAwayFrom(this);
            if (otherVertex == null) {
                System.out.println("** Null otherVertex in Face non-adjacent diagonals test.");
            }
            else {
                for (final Face otherFace : otherVertex.faces()) {
                    if (usedFaces.get(otherFace.id())) {
                        continue;
                    }
                    final double distV = MathRoutines.distanceToLine(vertex.pt2D(), this.pt2D(), otherFace.pt2D());
                    final double distOV = MathRoutines.distanceToLine(otherVertex.pt2D(), this.pt2D(), otherFace.pt2D());
                    final double distAB = MathRoutines.distance(this.pt2D(), otherFace.pt2D());
                    final double error = (distV + distOV) / distAB;
                    if (error > 0.1) {
                        continue;
                    }
                    usedFaces.set(otherFace.id(), true);
                    final Step newStep4 = new Step(this, otherFace);
                    newStep4.directions().set(AbsoluteDirection.Diagonal.ordinal());
                    newStep4.directions().set(AbsoluteDirection.All.ordinal());
                    steps.add(newStep4);
                }
            }
        }
        for (final Vertex vertex : this.vertices) {
            final Step newStep5 = new Step(this, vertex);
            newStep5.directions().set(AbsoluteDirection.Orthogonal.ordinal());
            newStep5.directions().set(AbsoluteDirection.Adjacent.ordinal());
            newStep5.directions().set(AbsoluteDirection.All.ordinal());
            steps.add(newStep5);
        }
        for (final Edge edge : this.edges) {
            final Step newStep5 = new Step(this, edge);
            newStep5.directions().set(AbsoluteDirection.Orthogonal.ordinal());
            newStep5.directions().set(AbsoluteDirection.Adjacent.ordinal());
            newStep5.directions().set(AbsoluteDirection.All.ordinal());
            steps.add(newStep5);
        }
    }
    
    public double momentum() {
        double Txx = 0.0;
        double Tyy = 0.0;
        double Txy = 0.0;
        for (final Vertex vertex : this.vertices()) {
            final double x = vertex.pt().x() - this.pt.x();
            final double y = vertex.pt().y() - this.pt.y();
            Txx += x * x;
            Tyy += y * y;
            Txy += x * y;
        }
        System.out.println("\nTxx=" + Txx + ", Tyy=" + Tyy + ", Txy=" + Txy + ".");
        final double sinTheta = (Tyy - Txx + Math.sqrt((Tyy - Txx) * (Tyy - Txx) + 4.0 * Txy * Txy)) / Math.sqrt(8.0 * Txy * Txy + 2.0 * (Tyy - Txx) * (Tyy - Txx) + 2.0 * Math.abs(Tyy - Txx) * Math.sqrt((Tyy - Txx) * (Tyy - Txx) + 4.0 * Txy * Txy));
        System.out.println("sinTheta=" + sinTheta);
        final double cosTheta = 2.0 * Txy / Math.sqrt(8.0 * Txy * Txy + 2.0 * (Tyy - Txx) * (Tyy - Txx) + 2.0 * Math.abs(Tyy - Txx) * Math.sqrt((Tyy - Txx) * (Tyy - Txx) + 4.0 * Txy * Txy));
        System.out.println("cosTheta=" + cosTheta);
        final double theta = Math.acos(cosTheta);
        return theta;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Face[" + this.id + "]:");
        for (final Vertex vertex : this.vertices) {
            sb.append(" " + vertex.id());
        }
        sb.append(" " + properties);
        sb.append(" \"" + situation.label() + "\"");
        return sb.toString();
    }
}
