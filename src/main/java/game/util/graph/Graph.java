// 
// Decompiled by Procyon v0.5.36
// 

package game.util.graph;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.graph.BaseGraphFunction;
import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import math.MathRoutines;
import math.Point3D;
import math.Vector;
import util.Context;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class Graph extends BaseGraphFunction
{
    private static final long serialVersionUID = 1L;
    public static final SiteType[] siteTypes;
    private final List<Vertex> vertices;
    private final List<Edge> edges;
    private final List<Face> faces;
    private final List<Perimeter> perimeters;
    private final Trajectories trajectories;
    private final boolean[] duplicateCoordinates;
    
    public Graph(@Name final Float[][] vertices, @Opt @Name final Integer[][] edges) {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.faces = new ArrayList<>();
        this.perimeters = new ArrayList<>();
        this.trajectories = new Trajectories();
        this.duplicateCoordinates = new boolean[SiteType.values().length];
        this.setVertices(vertices);
        if (edges != null) {
            this.setEdges(edges);
        }
        this.assemble(false);
    }
    
    @Hide
    public Graph(final List<Vertex> vertices, final List<Edge> edges) {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.faces = new ArrayList<>();
        this.perimeters = new ArrayList<>();
        this.trajectories = new Trajectories();
        this.duplicateCoordinates = new boolean[SiteType.values().length];
        for (final Vertex vertex : vertices) {
            this.addVertex(vertex.pt());
        }
        for (final Vertex vertex : vertices) {
            if (vertex.pivot() != null) {
                final Vertex newVertex = this.vertices.get(vertex.id());
                final Vertex pivot = this.findVertex(vertex.pivot().pt2D(), 0.001);
                if (pivot == null) {}
                newVertex.setPivot(pivot);
            }
        }
        for (final Edge edge : edges) {
            final Edge newEdge = this.findOrAddEdge(edge.vertexA().id(), edge.vertexB().id());
            if (edge.tangentA() != null) {
                newEdge.setTangentA(new Vector(edge.tangentA()));
            }
            if (edge.tangentB() != null) {
                newEdge.setTangentB(new Vector(edge.tangentB()));
            }
        }
        this.assemble(false);
    }
    
    @Hide
    public Graph(final Graph other) {
        this.vertices = new ArrayList<>();
        edges = new ArrayList<>();
        this.faces = new ArrayList<>();
        this.perimeters = new ArrayList<>();
        this.trajectories = new Trajectories();
        this.duplicateCoordinates = new boolean[SiteType.values().length];
        this.deepCopy(other);
    }
    
    @Hide
    public Graph() {
        this.vertices = new ArrayList<>();
        edges = new ArrayList<>();
        this.faces = new ArrayList<>();
        this.perimeters = new ArrayList<>();
        this.trajectories = new Trajectories();
        this.duplicateCoordinates = new boolean[SiteType.values().length];
    }
    
    public List<Vertex> vertices() {
        return Collections.unmodifiableList(this.vertices);
    }
    
    public List<Edge> edges() {
        return Collections.unmodifiableList(edges);
    }
    
    public List<Face> faces() {
        return Collections.unmodifiableList(this.faces);
    }
    
    public List<Perimeter> perimeters() {
        return Collections.unmodifiableList(this.perimeters);
    }
    
    public Trajectories trajectories() {
        return this.trajectories;
    }
    
    public boolean duplicateCoordinates(final SiteType siteType) {
        return this.duplicateCoordinates[siteType.ordinal()];
    }
    
    public void setDuplicateCoordinates(final SiteType siteType) {
        this.duplicateCoordinates[siteType.ordinal()] = true;
    }
    
    public void setDim(final int[] array) {
        this.dim = array;
    }
    
    public void clearPerimeters() {
        this.perimeters.clear();
    }
    
    public void addPerimeter(final Perimeter perimeter) {
        this.perimeters.add(perimeter);
    }
    
    public void removePerimeter(final int n) {
        this.perimeters.remove(n);
    }
    
    public List<? extends GraphElement> elements(final SiteType type) {
        switch (type) {
            case Vertex -> {
                return this.vertices;
            }
            case Edge -> {
                return edges;
            }
            case Cell -> {
                return this.faces;
            }
            default -> {
                return null;
            }
        }
    }
    
    public GraphElement element(final SiteType type, final int id) {
        switch (type) {
            case Vertex -> {
                return this.vertices.get(id);
            }
            case Edge -> {
                return edges.get(id);
            }
            case Cell -> {
                return this.faces.get(id);
            }
            default -> {
                return null;
            }
        }
    }
    
    public static Graph createFromLists(final List<double[]> vertexList, final List<int[]> edgeList) {
        final Float[][] vertexArray = new Float[vertexList.size()][3];
        for (int v = 0; v < vertexList.size(); ++v) {
            final double[] values = vertexList.get(v);
            vertexArray[v][0] = (float)values[0];
            vertexArray[v][1] = (float)values[1];
            if (values.length > 2) {
                vertexArray[v][2] = (float)values[2];
            }
            else {
                vertexArray[v][2] = 0.0f;
            }
        }
        final Integer[][] edgeArray = new Integer[edgeList.size()][2];
        for (int e = 0; e < edgeList.size(); ++e) {
            edgeArray[e][0] = edgeList.get(e)[0];
            edgeArray[e][1] = edgeList.get(e)[1];
        }
        return new Graph(vertexArray, edgeArray);
    }
    
    public void clear() {
        this.faces.clear();
        edges.clear();
        this.vertices.clear();
    }
    
    public void clear(final SiteType siteType) {
        switch (siteType) {
            case Vertex -> {
                this.vertices.clear();
            }
            case Edge -> {
                edges.clear();
                for (final Vertex vertex : this.vertices()) {
                    vertex.clearEdges();
                }
            }
            case Cell -> {
                this.faces.clear();
                for (final Edge edge : edges()) {
                    edge.setLeft(null);
                    edge.setRight(null);
                }
                for (final Vertex vertex : this.vertices()) {
                    vertex.clearFaces();
                }
            }
        }
    }
    
    public void deepCopy(final Graph other) {
        this.clear();
        for (final Vertex otherVertex : other.vertices) {
            this.addVertex(otherVertex.pt.x(), otherVertex.pt.y(), otherVertex.pt.z());
        }
        for (final Edge otherEdge : other.edges) {
            this.findOrAddEdge(otherEdge.vertexA().id(), otherEdge.vertexB().id());
        }
        for (final Face otherFace : other.faces) {
            final int[] vids = new int[otherFace.vertices().size()];
            for (int v = 0; v < otherFace.vertices().size(); ++v) {
                vids[v] = otherFace.vertices().get(v).id();
            }
            this.findOrAddFace(vids);
        }
        this.perimeters.clear();
    }
    
    public void translate(final double dx, final double dy, final double dz) {
        for (final Vertex vertex : this.vertices) {
            final double xx = vertex.pt().x() + dx;
            final double yy = vertex.pt().y() + dy;
            final double zz = vertex.pt().z() + dz;
            vertex.pt().set(xx, yy, zz);
        }
        this.recalculateEdgeAndFacePositions();
    }
    
    public void scale(final double sx, final double sy, final double sz) {
        for (final Vertex vertex : this.vertices) {
            final double xx = vertex.pt().x() * sx;
            final double yy = vertex.pt().y() * sy;
            final double zz = vertex.pt().z() * sz;
            vertex.pt().set(xx, yy, zz);
        }
        this.recalculateEdgeAndFacePositions();
    }
    
    public void rotate(final double degrees) {
        final double theta = Math.toRadians(degrees);
        final Rectangle2D bounds = this.bounds();
        final double pivotX = bounds.getX() + bounds.getWidth() / 2.0;
        final double pivotY = bounds.getY() + bounds.getHeight() / 2.0;
        for (final Vertex vertex : this.vertices) {
            final double dx = vertex.pt().x() - pivotX;
            final double dy = vertex.pt().y() - pivotY;
            final double xx = pivotX + dx * Math.cos(theta) - dy * Math.sin(theta);
            final double yy = pivotY + dy * Math.cos(theta) + dx * Math.sin(theta);
            vertex.pt().set(xx, yy);
        }
        for (final Edge edge : edges) {
            if (edge.tangentA() != null) {
                edge.tangentA().rotate(theta);
            }
            if (edge.tangentB() != null) {
                edge.tangentB().rotate(theta);
            }
        }
        this.recalculateEdgeAndFacePositions();
    }
    
    public void skew(final double amount) {
        final Rectangle2D bounds = this.bounds();
        for (final Vertex vertex : this.vertices) {
            final double offset = (vertex.pt().y() - bounds.getMinY()) * amount;
            final double xx = vertex.pt().x() + offset;
            final double yy = vertex.pt().y();
            final double zz = vertex.pt().z();
            vertex.pt().set(xx, yy, zz);
        }
        for (final Edge edge : edges) {
            if (edge.tangentA() != null) {
                final double offset = (edge.tangentA().y() - bounds.getMinY()) * amount;
                final double xx = edge.tangentA().x() + offset;
                final double yy = edge.tangentA().y();
                final double zz = edge.tangentA().z();
                edge.tangentA().set(xx, yy, zz);
            }
            if (edge.tangentB() != null) {
                final double offset = (edge.tangentB().y() - bounds.getMinY()) * amount;
                final double xx = edge.tangentB().x() + offset;
                final double yy = edge.tangentB().y();
                final double zz = edge.tangentB().z();
                edge.tangentB().set(xx, yy, zz);
            }
        }
        this.recalculateEdgeAndFacePositions();
    }
    
    public boolean isRegular() {
        if (this.basis == BasisType.Square || this.basis == BasisType.Triangular || this.basis == BasisType.Hexagonal) {
            return true;
        }
        BasisType refBasis = null;
        if (!this.faces.isEmpty()) {
            refBasis = this.faces.get(0).basis();
        }
        else if (!edges.isEmpty()) {
            refBasis = edges.get(0).basis();
        }
        else if (!this.vertices.isEmpty()) {
            refBasis = this.vertices.get(0).basis();
        }
        if (refBasis == null || refBasis == BasisType.NoBasis) {
            return false;
        }
        for (final GraphElement ge : this.vertices) {
            if (ge.basis() != refBasis) {
                return false;
            }
        }
        for (final GraphElement ge : edges) {
            if (ge.basis() != refBasis) {
                return false;
            }
        }
        for (final GraphElement ge : this.faces) {
            if (ge.basis() != refBasis) {
                return false;
            }
        }
        return true;
    }
    
    public Vertex findVertex(final Point2D pt, final double tolerance) {
        return this.findVertex(pt.getX(), pt.getY(), 0.0, tolerance);
    }
    
    public Vertex findVertex(final Point3D pt, final double tolerance) {
        return this.findVertex(pt.x(), pt.y(), pt.z(), tolerance);
    }
    
    public Vertex findVertex(final Vertex vertex, final double tolerance) {
        return this.findVertex(vertex.pt.x(), vertex.pt.y(), vertex.pt.z(), tolerance);
    }
    
    public Vertex findVertex(final double x, final double y, final double tolerance) {
        for (final Vertex vertex : this.vertices) {
            if (vertex.coincident(x, y, 0.0, tolerance)) {
                return vertex;
            }
        }
        return null;
    }
    
    public Vertex findVertex(final double x, final double y, final double z, final double tolerance) {
        for (final Vertex vertex : this.vertices) {
            if (vertex.coincident(x, y, z, tolerance)) {
                return vertex;
            }
        }
        return null;
    }
    
    public Edge findEdge(final int idA, final int idB) {
        for (final Edge edge : edges) {
            if (edge.matches(idA, idB)) {
                return edge;
            }
        }
        return null;
    }
    
    public Edge findEdge(final int idA, final int idB, final boolean curved) {
        for (final Edge edge : edges) {
            if (edge.matches(idA, idB, curved)) {
                return edge;
            }
        }
        return null;
    }
    
    public Edge findEdge(final Vertex vertexA, final Vertex vertexB) {
        return this.findEdge(vertexA.id(), vertexB.id());
    }
    
    public Edge findEdge(final Vertex vertexA, final Vertex vertexB, final boolean curved) {
        return this.findEdge(vertexA.id(), vertexB.id(), curved);
    }
    
    public Edge findEdge(final double ax, final double ay, final double bx, final double by, final double tolerance) {
        final Vertex vertexA = this.findVertex(ax, ay, tolerance);
        if (vertexA == null) {
            return null;
        }
        final Vertex vertexB = this.findVertex(bx, by, tolerance);
        if (vertexB == null) {
            return null;
        }
        return this.findEdge(vertexA.id(), vertexB.id());
    }
    
    public Edge findEdge(final double ax, final double ay, final double az, final double bx, final double by, final double bz, final double tolerance) {
        final Vertex vertexA = this.findVertex(ax, ay, az, tolerance);
        if (vertexA == null) {
            return null;
        }
        final Vertex vertexB = this.findVertex(bx, by, bz, tolerance);
        if (vertexB == null) {
            return null;
        }
        return this.findEdge(vertexA.id(), vertexB.id());
    }
    
    public Face findFace(final int... vertIds) {
        for (final Face face : this.faces) {
            if (face.matches(vertIds)) {
                return face;
            }
        }
        return null;
    }
    
    public Face findFace(final List<Point3D> pts, final double tolerance) {
        final int[] vertIds = new int[pts.size()];
        for (int v = 0; v < pts.size(); ++v) {
            final Point3D pt = pts.get(v);
            final Vertex vertex = this.findVertex(pt.x(), pt.y(), pt.z(), tolerance);
            if (vertex == null) {
                return null;
            }
            vertIds[v] = vertex.id();
        }
        return this.findFace(vertIds);
    }
    
    public boolean containsEdge(final Vertex vertexA, final Vertex vertexB) {
        return this.findEdge(vertexA.id(), vertexB.id()) != null;
    }
    
    public boolean containsEdge(final int idA, final int idB) {
        return this.findEdge(idA, idB) != null;
    }
    
    public boolean containsFace(final int... vids) {
        return this.findFace(vids) != null;
    }
    
    public void addVertex(final Vertex vertex) {
        this.vertices.add(vertex);
    }
    
    public void addEdge(final Edge edge) {
        edges.add(edge);
    }
    
    public void addFace(final Face face) {
        this.faces.add(face);
    }
    
    public Vertex addVertex(final Point2D pt) {
        return this.addVertex(pt.getX(), pt.getY(), 0.0);
    }
    
    public Vertex addVertex(final Point3D pt) {
        return this.addVertex(pt.x(), pt.y(), pt.z());
    }
    
    public Vertex addVertex(final double x, final double y) {
        return this.addVertex(x, y, 0.0);
    }
    
    public Vertex addVertex(final double x, final double y, final double z) {
        final Vertex newVertex = new Vertex(this.vertices.size(), x, y, z);
        this.vertices.add(newVertex);
        return newVertex;
    }
    
    public Vertex findOrAddVertex(final double x, final double y, final double tolerance) {
        return this.findOrAddVertex(x, y, 0.0, tolerance);
    }
    
    public Vertex findOrAddVertex(final Point2D pt, final double tolerance) {
        return this.findOrAddVertex(pt.getX(), pt.getY(), 0.0, tolerance);
    }
    
    public Vertex findOrAddVertex(final double x, final double y, final double z, final double tolerance) {
        final Vertex existing = this.findVertex(x, y, z, tolerance);
        if (existing != null) {
            return existing;
        }
        return this.addVertex(x, y, z);
    }
    
    public Edge findOrAddEdge(final Vertex vertexA, final Vertex vertexB, final Vector tangentA, final Vector tangentB) {
        return this.findOrAddEdge(vertexA.id(), vertexB.id(), tangentA, tangentB);
    }
    
    public Edge findOrAddEdge(final Vertex vertexA, final Vertex vertexB) {
        return this.findOrAddEdge(vertexA.id(), vertexB.id());
    }
    
    public Edge findOrAddEdge(final int vertIdA, final int vertIdB) {
        if (vertIdA >= this.vertices.size() || vertIdB >= this.vertices.size()) {
            System.out.println("** Graph.addEdge(): Trying to add edge " + vertIdA + "-" + vertIdB + " but only " + this.vertices.size() + " vertices.");
            return null;
        }
        for (final Edge edge : edges) {
            if (edge.matches(vertIdA, vertIdB)) {
                return edge;
            }
        }
        return this.addEdge(vertIdA, vertIdB);
    }
    
    public Edge findOrAddEdge(final int vertIdA, final int vertIdB, final Vector tangentA, final Vector tangentB) {
        if (vertIdA >= this.vertices.size() || vertIdB >= this.vertices.size()) {
            System.out.println("** Graph.addEdge(): Trying to add edge " + vertIdA + "-" + vertIdB + " but only " + this.vertices.size() + " vertices.");
            return null;
        }
        for (final Edge edge : edges) {
            if (edge.matches(vertIdA, vertIdB)) {
                return edge;
            }
        }
        return this.addEdge(vertIdA, vertIdB, tangentA, tangentB);
    }
    
    public Edge addEdge(final Vertex vertexA, final Vertex vertexB) {
        return this.addEdge(vertexA.id(), vertexB.id());
    }
    
    public Edge addEdge(final int vertIdA, final int vertIdB) {
        if (vertIdA >= this.vertices.size() || vertIdB >= this.vertices.size()) {
            System.out.println("** Graph.addEdge(): Trying to add edge " + vertIdA + "-" + vertIdB + " but only " + this.vertices.size() + " vertices.");
            return null;
        }
        final Vertex vertexA = this.vertices.get(vertIdA);
        final Vertex vertexB = this.vertices.get(vertIdB);
        final Edge newEdge = new Edge(edges.size(), vertexA, vertexB);
        edges.add(newEdge);
        vertexA.addEdge(newEdge);
        vertexA.sortEdges();
        vertexB.addEdge(newEdge);
        vertexB.sortEdges();
        if (vertexA.basis() == vertexB.basis()) {
            newEdge.setBasis(vertexA.basis());
        }
        if (vertexA.shape() == vertexB.shape()) {
            newEdge.setShape(vertexA.shape());
        }
        return newEdge;
    }
    
    public Edge addEdge(final Vertex vertexA, final Vertex vertexB, final Vector tangentA, final Vector tangentB) {
        return this.addEdge(vertexA.id(), vertexB.id(), tangentA, tangentB);
    }
    
    public Edge addEdge(final int vertIdA, final int vertIdB, final Vector tangentA, final Vector tangentB) {
        if (vertIdA >= this.vertices.size() || vertIdB >= this.vertices.size()) {
            System.out.println("** Graph.addEdge(): Trying to add edge " + vertIdA + "-" + vertIdB + " but only " + this.vertices.size() + " vertices.");
            return null;
        }
        final Vertex vertexA = this.vertices.get(vertIdA);
        final Vertex vertexB = this.vertices.get(vertIdB);
        final Edge newEdge = new Edge(edges.size(), vertexA, vertexB);
        edges.add(newEdge);
        vertexA.addEdge(newEdge);
        vertexA.sortEdges();
        vertexB.addEdge(newEdge);
        vertexB.sortEdges();
        if (vertexA.basis() == vertexB.basis()) {
            newEdge.setBasis(vertexA.basis());
        }
        if (vertexA.shape() == vertexB.shape()) {
            newEdge.setShape(vertexA.shape());
        }
        newEdge.setTangentA(tangentA);
        newEdge.setTangentB(tangentB);
        return newEdge;
    }
    
    public void makeEdges() {
        for (final Vertex vertexA : this.vertices) {
            for (final Vertex vertexB : this.vertices) {
                if (vertexA.id() == vertexB.id()) {
                    continue;
                }
                final double dist = vertexA.pt().distance(vertexB.pt());
                if (Math.abs(dist - 1.0) >= 0.05) {
                    continue;
                }
                findOrAddEdge(vertexA, vertexB);
            }
        }
    }
    
    public Face findOrAddFace(final int... vertIds) {
        if (vertIds.length > this.vertices.size()) {
            return null;
        }
        for (final int vid : vertIds) {
            if (vid >= this.vertices.size()) {
                System.out.println("** Graph.addFace(): Vertex " + vid + " specified but only " + this.vertices.size() + " vertices.");
                return null;
            }
        }
        for (final Face face : this.faces) {
            if (face.matches(vertIds)) {
                return face;
            }
        }
        final Face newFace = new Face(this.faces.size());
        final BasisType refBasis = this.vertices.isEmpty() ? null : this.vertices.get(0).basis();
        final ShapeType refShape = this.vertices.isEmpty() ? null : this.vertices.get(0).shape();
        boolean allSameBasis = true;
        final boolean allSameShape = true;
        for (int v = 0; v < vertIds.length; ++v) {
            final int m = vertIds[v];
            final int n = vertIds[(v + 1) % vertIds.length];
            final Vertex vert = this.vertices.get(m);
            final Vertex next = this.vertices.get(n);
            final Edge edge = this.findEdge(vert.id(), next.id());
            if (edge == null) {
                System.out.println("** Graph.addFace(): Couldn't find edge between V" + m + " and V" + n + ".");
                return null;
            }
            if (v > 0 && vert.basis() != refBasis) {
                allSameBasis = false;
            }
            if (v > 0 && vert.shape() != refShape) {
                allSameBasis = false;
            }
            if (edge.vertexA().id() == vert.id()) {
                edge.setRight(newFace);
            }
            else {
                edge.setLeft(newFace);
            }
            newFace.addVertexAndEdge(vert, edge);
        }
        for (final Vertex vertex : newFace.vertices()) {
            vertex.addFace(newFace);
        }
        final BasisType basisF = allSameBasis ? refBasis : BasisType.NoBasis;
        final ShapeType shapeF = refShape;
        newFace.setTilingAndShape(basisF, shapeF);
        this.faces.add(newFace);
        return newFace;
    }
    
    public void makeFaces(final boolean checkCrossings) {
        final int MAX_FACE_SIDES = 32;
        if (!this.faces.isEmpty()) {
            this.clearFaces();
        }
        for (final Vertex vertexStart : this.vertices) {
            for (final Edge edgeStart : vertexStart.edges()) {
                final TIntArrayList vertIds = new TIntArrayList();
                vertIds.add(vertexStart.id());
                Vertex vert = vertexStart;
                Edge edge = edgeStart;
                boolean closed = false;
                while (vertIds.size() <= 32) {
                    final Vertex prev = vert;
                    final int n = vert.edgePosition(edge);
                    edge = vert.edges().get((n + 1) % vert.edges().size());
                    vert = edge.otherVertex(vert);
                    if (vert.id() == vertexStart.id()) {
                        closed = true;
                        break;
                    }
                    if (vertIds.contains(vert.id())) {
                        break;
                    }
                    vertIds.add(vert.id());
                    if (checkCrossings && this.isEdgeCrossing(prev, vert)) {
                        break;
                    }
                }
                if (closed && vertIds.size() >= 3) {
                    final List<Point2D> poly = new ArrayList<>();
                    for (int v = 0; v < vertIds.size(); ++v) {
                        final Vertex vertex = this.vertices.get(vertIds.getQuick(v));
                        poly.add(new Point2D.Double(vertex.pt.x(), vertex.pt.y()));
                    }
                    if (!MathRoutines.clockwise(poly)) {
                        continue;
                    }
                    final int[] vids = vertIds.toArray();
                    if (this.containsFace(vids)) {
                        continue;
                    }
                    this.findOrAddFace(vids);
                }
            }
        }
    }
    
    public void clearFaces() {
        for (final Edge edge : edges) {
            edge.setLeft(null);
            edge.setRight(null);
        }
        for (final Vertex vertex : this.vertices) {
            vertex.clearFaces();
        }
        this.faces.clear();
    }
    
    public void remove(final GraphElement element, final boolean removeOrphans) {
        switch (element.siteType()) {
            case Vertex -> this.removeVertex(element.id());
            case Edge -> this.removeEdge(element.id());
            case Cell -> this.removeFace(element.id(), removeOrphans);
        }
    }
    
    public void removeVertex(final Vertex vertex) {
        this.removeVertex(vertex.id());
    }
    
    public void removeVertex(final int vid) {
        if (vid >= this.vertices.size()) {
            System.out.println("Graph.removeVertex(): Index " + vid + " out of range.");
            return;
        }
        final Vertex vertex = this.vertices.get(vid);
        final BitSet facesToRemove = new BitSet();
        for (final Face face : this.faces) {
            if (face.contains(vertex)) {
                facesToRemove.set(face.id(), true);
            }
        }
        for (final Edge edge : edges) {
            if (edge.left() != null && facesToRemove.get(edge.left().id())) {
                edge.setLeft(null);
            }
            if (edge.right() != null && facesToRemove.get(edge.right().id())) {
                edge.setRight(null);
            }
        }
        for (final Vertex vertexF : this.vertices()) {
            for (int f = vertexF.faces().size() - 1; f >= 0; --f) {
                final Face face2 = vertexF.faces().get(f);
                if (facesToRemove.get(face2.id())) {
                    vertexF.faces().remove(f);
                }
            }
        }
        for (int fid = this.faces.size() - 1; fid >= 0; --fid) {
            if (facesToRemove.get(fid)) {
                this.faces.remove(fid);
            }
        }
        final BitSet edgesToRemove = new BitSet();
        for (final Edge edge2 : vertex.edges()) {
            edgesToRemove.set(edge2.id(), true);
        }
        for (final Vertex vertexE : this.vertices()) {
            for (int e = vertexE.edges().size() - 1; e >= 0; --e) {
                final Edge edge3 = vertexE.edges().get(e);
                if (edgesToRemove.get(edge3.id())) {
                    vertexE.removeEdge(e);
                }
            }
        }
        for (int eid = edges().size() - 1; eid >= 0; --eid) {
            if (edgesToRemove.get(eid)) {
                edges.remove(eid);
            }
        }
        this.vertices.remove(vid);
        for (int f2 = 0; f2 < this.faces.size(); ++f2) {
            this.faces.get(f2).setId(f2);
        }
        for (int e2 = 0; e2 < edges.size(); ++e2) {
            edges.get(e2).setId(e2);
        }
        for (int v = 0; v < this.vertices.size(); ++v) {
            this.vertices.get(v).setId(v);
        }
    }
    
    public void removeEdge(final int vidA, final int vidB) {
        final Edge edge = this.findEdge(vidA, vidB);
        if (edge != null) {
            this.removeEdge(edge.id());
        }
    }
    
    public void removeEdge(final int eid) {
        if (eid >= edges.size()) {
            System.out.println("Graph.removeEdge(): Index " + eid + " out of range.");
            return;
        }
        final Edge edge = edges.get(eid);
        for (int fid = this.faces.size() - 1; fid >= 0; --fid) {
            if (this.faces.get(fid).contains(edge)) {
                this.removeFace(fid, false);
            }
        }
        final Vertex[] endpoints = { (edge.vertexA().id() <= edge.vertexB().id()) ? edge.vertexA() : edge.vertexB(), (edge.vertexA().id() <= edge.vertexB().id()) ? edge.vertexB() : edge.vertexA() };
        for (int v = 0; v < 2; ++v) {
            final Vertex vertex = endpoints[v];
            for (int e = vertex.edges().size() - 1; e >= 0; --e) {
                if (vertex.edges().get(e).id() == edge.id()) {
                    vertex.removeEdge(e);
                }
            }
        }
        edges.remove(eid);
        for (int e2 = eid; e2 < edges.size(); ++e2) {
            edges.get(e2).decrementId();
        }
    }
    
    public void removeFace(final int fid, final boolean removeOrphans) {
        if (fid >= this.faces.size()) {
            System.out.println("Graph.removeFace(): Index " + fid + " out of range.");
            return;
        }
        final Face face = this.faces.get(fid);
        final BitSet edgesToRemove = new BitSet();
        for (final Edge edge : face.edges()) {
            if (edge.left() != null && edge.left().id() == fid) {
                edge.setLeft(null);
                if (removeOrphans && edge.right() == null) {
                    edgesToRemove.set(edge.id());
                }
            }
            if (edge.right() != null && edge.right().id() == fid) {
                edge.setRight(null);
                if (!removeOrphans || edge.left() != null) {
                    continue;
                }
                edgesToRemove.set(edge.id());
            }
        }
        for (final Vertex vertex : face.vertices()) {
            for (int n = vertex.faces().size() - 1; n >= 0; --n) {
                if (vertex.faces().get(n).id() == fid) {
                    vertex.removeFace(n);
                }
            }
        }
        this.faces.remove(fid);
        for (int f = fid; f < this.faces.size(); ++f) {
            this.faces.get(f).decrementId();
        }
        for (int e = edges.size() - 1; e >= 0; --e) {
            if (edgesToRemove.get(e)) {
                this.removeEdge(e);
            }
        }
    }
    
    public void setBasisAndShape(final BasisType bt, final ShapeType st) {
        for (final Vertex vertex : this.vertices) {
            if (vertex.basis() == null) {
                vertex.setBasis(bt);
            }
            if (vertex.shape() == null) {
                vertex.setShape(st);
            }
        }
        for (final Edge edge : edges) {
            if (edge.basis() == null) {
                edge.setBasis(bt);
            }
            if (edge.shape() == null) {
                edge.setShape(st);
            }
        }
        for (final Face face : this.faces) {
            if (face.basis() == null) {
                face.setBasis(bt);
            }
            if (face.shape() == null) {
                face.setShape(st);
            }
        }
        this.basis = bt;
        this.shape = st;
    }
    
    public void synchroniseIds() {
        for (int n = 0; n < this.vertices.size(); ++n) {
            this.vertices.get(n).setId(n);
        }
        for (int n = 0; n < edges.size(); ++n) {
            edges.get(n).setId(n);
        }
        for (int n = 0; n < this.faces.size(); ++n) {
            this.faces.get(n).setId(n);
        }
    }
    
    public void reorder() {
        this.reorder(SiteType.Vertex);
        this.reorder(SiteType.Edge);
        this.reorder(SiteType.Cell);
    }
    
    public void reorder(final SiteType type) {
        final List<? extends GraphElement> elements = this.elements(type);
        final List<ItemScore> rank = new ArrayList<>();
        for (int n = 0; n < elements.size(); ++n) {
            final GraphElement ge = elements.get(n);
            final double score = ge.pt().y() * 100.0 + ge.pt().x();
            rank.add(new ItemScore(n, score));
        }
        Collections.sort(rank);
        for (int es = 0; es < rank.size(); ++es) {
            final GraphElement ge = elements.get(rank.get(es).id());
            ge.setId(es);
            switch (type) {
                case Vertex -> this.vertices.add((Vertex) ge);
                case Edge -> edges.add((Edge) ge);
                case Cell -> this.faces.add((Face) ge);
            }
        }
        if (!rank.isEmpty()) {
            elements.subList(0, rank.size()).clear();
        }
        for (int n = 0; n < elements.size(); ++n) {
            elements.get(n).setId(n);
        }
    }
    
    private void setVertices(final Number[][] positions) {
        this.vertices.clear();
        if (positions != null) {
            for (final Number[] position : positions) {
                if (position.length == 2) {
                    this.addVertex(position[0].floatValue(), position[1].floatValue(), 0.0);
                } else {
                    this.addVertex(position[0].floatValue(), position[1].floatValue(), position[2].floatValue());
                }
            }
        }
    }
    
    private void setEdges(final Integer[][] pairs) {
        edges.clear();
        if (pairs != null) {
            for (Integer[] pair : pairs) {
                this.findOrAddEdge(pair[0], pair[1]);
            }
        }
    }
    
    private void assemble(final boolean checkCrossings) {
        this.linkEdgesToVertices();
        this.makeFaces(checkCrossings);
        this.linkFacesToVertices();
        this.setBasisAndShape(BasisType.NoBasis, ShapeType.NoShape);
    }
    
    public void linkEdgesToVertices() {
        for (final Vertex vertex : this.vertices) {
            vertex.clearEdges();
        }
        for (final Edge edge : edges) {
            this.vertices.get(edge.vertexA().id()).addEdge(edge);
            this.vertices.get(edge.vertexB().id()).addEdge(edge);
        }
        for (final Vertex vertex : this.vertices) {
            vertex.sortEdges();
        }
    }
    
    public void linkFacesToVertices() {
        for (final Vertex vertex : this.vertices) {
            vertex.clearFaces();
        }
        for (final Face face : this.faces) {
            for (final Vertex vertex2 : face.vertices()) {
                vertex2.addFace(face);
            }
        }
        for (final Vertex vertex : this.vertices) {
            vertex.sortFaces();
        }
    }
    
    public boolean isEdgeCrossing(final Vertex vertexA, final Vertex vertexB) {
        final double tolerance = 0.001;
        final Point2D ptA = vertexA.pt2D();
        final Point2D ptB = vertexB.pt2D();
        for (final Edge edge : edges) {
            if (edge.matches(vertexA, vertexB)) {
                return false;
            }
            final Point2D ptEA = edge.vertexA().pt2D();
            final Point2D ptEB = edge.vertexB().pt2D();
            if (ptA.distance(ptEA) < 0.001 || ptA.distance(ptEB) < 0.001 || ptB.distance(ptEA) < 0.001) {
                continue;
            }
            if (ptB.distance(ptEB) < 0.001) {
                continue;
            }
            if (MathRoutines.lineSegmentsIntersect(ptA.getX(), ptA.getY(), ptB.getX(), ptB.getY(), ptEA.getX(), ptEA.getY(), ptEB.getX(), ptEB.getY())) {
                return true;
            }
        }
        return false;
    }
    
    public void trim() {
        for (int eid = edges.size() - 1; eid >= 0; --eid) {
            final Edge edge = edges.get(eid);
            if (edge.vertexA().edges().size() == 1 || edge.vertexB().edges().size() == 1) {
                this.removeEdge(eid);
            }
        }
        final BitSet pivotIds = new BitSet();
        for (final Vertex vertex : this.vertices) {
            if (vertex.pivot() != null) {
                pivotIds.set(vertex.pivot().id());
            }
        }
        for (int vid = this.vertices.size() - 1; vid >= 0; --vid) {
            final Vertex vertex = this.vertices.get(vid);
            if (vertex.edges().isEmpty() && !pivotIds.get(vid)) {
                this.removeVertex(vid);
            }
        }
    }
    
    public void clearProperties() {
        for (final Vertex vertex : this.vertices) {
            vertex.properties().clear();
        }
        for (final Edge edge : edges) {
            edge.properties().clear();
        }
        for (final Face face : this.faces) {
            face.properties().clear();
        }
    }
    
    public void measure(final boolean boardless) {
        MeasureGraph.measure(this, false);
    }
    
    public double distance() {
        if (this.vertices.size() < 2) {
            return 0.0;
        }
        double avg = 0.0;
        int found = 0;
        for (final Vertex va : this.vertices) {
            for (final Vertex vb : this.vertices) {
                if (va.id() == vb.id()) {
                    continue;
                }
                final double dist = va.pt.distance(vb.pt);
                avg += dist;
                ++found;
            }
        }
        avg /= found;
        return avg;
    }
    
    public double variance() {
        if (this.vertices.size() < 2) {
            return 0.0;
        }
        final double avg = this.distance();
        double varn = 0.0;
        int found = 0;
        for (final Vertex va : this.vertices) {
            for (final Vertex vb : this.vertices) {
                if (va.id() == vb.id()) {
                    continue;
                }
                ++found;
                final double dist = va.pt.distance(vb.pt);
                varn += Math.abs(dist - avg);
            }
        }
        varn /= found;
        return varn;
    }
    
    public Rectangle2D bounds() {
        return bounds(this.vertices);
    }
    
    public static Rectangle2D bounds(final List<? extends GraphElement> elements) {
        final double limit = 1000000.0;
        double x0 = 1000000.0;
        double y0 = 1000000.0;
        double x2 = -1000000.0;
        double y2 = -1000000.0;
        for (final GraphElement ge : elements) {
            final double x3 = ge.pt.x();
            final double y3 = ge.pt.y();
            if (x3 < x0) {
                x0 = x3;
            }
            if (x3 > x2) {
                x2 = x3;
            }
            if (y3 < y0) {
                y0 = y3;
            }
            if (y3 > y2) {
                y2 = y3;
            }
        }
        if (x0 == 1000000.0 || y0 == 1000000.0) {
            return new Rectangle2D.Double(0.0, 0.0, 0.0, 0.0);
        }
        return new Rectangle2D.Double(x0, y0, x2 - x0, y2 - y0);
    }
    
    public void normalise() {
        final Rectangle2D bounds = bounds(this.elements(SiteType.Vertex));
        final double maxExtent = Math.max(bounds.getWidth(), bounds.getHeight());
        if (maxExtent == 0.0) {
            System.out.println("** Normalising graph with zero bounding box.");
            return;
        }
        final double scale = 1.0 / maxExtent;
        final double offX = -bounds.getX() + (maxExtent - bounds.getWidth()) / 2.0;
        final double offY = -bounds.getY() + (maxExtent - bounds.getHeight()) / 2.0;
        for (final Vertex vertex : this.vertices) {
            final double xx = (vertex.pt.x() + offX) * scale;
            final double yy = (vertex.pt.y() + offY) * scale;
            final double zz = (vertex.pt.z() + offY) * scale;
            vertex.pt.set(xx, yy, zz);
        }
    }
    
    public void recalculateEdgeAndFacePositions() {
        for (final Edge edge : edges) {
            edge.setMidpoint();
        }
        for (final Face face : this.faces) {
            face.setMidpoint();
        }
    }
    
    public static void removeDuplicateEdges(final List<Edge> edges) {
        for (int ea = 0; ea < edges.size(); ++ea) {
            final Edge edgeA = edges.get(ea);
            for (int eb = edges.size() - 1; eb > ea; --eb) {
                if (edgeA.matches(edges.get(eb))) {
                    for (int ec = eb + 1; ec < edges.size(); ++ec) {
                        edges.get(ec).decrementId();
                    }
                    edges.remove(eb);
                }
            }
        }
    }
    
    public double averageEdgeLength() {
        if (edges.isEmpty()) {
            return 0.0;
        }
        double avg = 0.0;
        for (final Edge edge : edges) {
            avg += edge.length();
        }
        return avg / edges.size();
    }
    
    public Point2D centroid() {
        if (this.vertices.isEmpty()) {
            return new Point2D.Double(0.0, 0.0);
        }
        double midX = 0.0;
        double midY = 0.0;
        for (final Vertex vertex : this.vertices) {
            midX += vertex.pt().x();
            midY += vertex.pt().y();
        }
        return new Point2D.Double(midX / this.vertices().size(), midY / this.vertices().size());
    }
    
    @Override
    public Graph eval(final Context context, final SiteType siteType) {
        return this;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.vertices.isEmpty()) {
            return "Graph has no vertices.";
        }
        sb.append("Graph basis: ").append(this.basis).append("\n");
        sb.append("Graph shape: ").append(this.shape).append("\n");
        sb.append("Graph is ").append(this.isRegular() ? "" : "not ").append("regular.\n");
        sb.append("Vertices:\n");
        for (final Vertex vertex : this.vertices) {
            sb.append("- V: ").append(vertex.toString()).append("\n");
        }
        if (edges.isEmpty()) {
            sb.append("No edges.");
        }
        else {
            sb.append("Edges:\n");
            for (final Edge edge : edges) {
                sb.append("- E: ").append(edge.toString()).append("\n");
            }
        }
        if (this.faces.isEmpty()) {
            sb.append("No faces.");
        }
        else {
            sb.append("Faces:\n");
            for (final Face face : this.faces) {
                sb.append("- F: ").append(face.toString()).append("\n");
            }
        }
        return sb.toString();
    }
    
    static {
        siteTypes = new SiteType[] { SiteType.Vertex, SiteType.Edge, SiteType.Cell };
    }
}
