// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.container;

import game.equipment.Item;
import game.equipment.container.board.Track;
import game.types.board.SiteType;
import game.types.play.RoleType;
import metadata.graphics.util.ContainerStyleType;
import metadata.graphics.util.ControllerType;
import topology.Cell;
import topology.Edge;
import topology.Topology;
import topology.Vertex;
import util.ItemType;
import util.symmetry.SymmetryUtils;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Container extends Item implements Serializable, Cloneable
{
    private static final long serialVersionUID = 1L;
    private static final double SYMMETRY_ACCURACY = 1.0E-6;
    protected Topology topology;
    protected int numSites;
    protected List<Track> tracks;
    protected Track[][] ownedTracks;
    protected ContainerStyleType style;
    protected ControllerType controller;
    protected SiteType defaultSite;
    
    public Container(final String label, final int index, final RoleType role) {
        super(label, index, role);
        this.topology = new Topology();
        this.numSites = 0;
        this.tracks = new ArrayList<>();
        this.defaultSite = SiteType.Cell;
        this.setType(ItemType.Container);
    }
    
    protected Container(final Container other) {
        super(other);
        this.topology = new Topology();
        this.numSites = 0;
        this.tracks = new ArrayList<>();
        this.defaultSite = SiteType.Cell;
        this.topology = other.topology;
        this.numSites = other.numSites;
        this.tracks = other.tracks;
        this.style = other.style;
        this.controller = other.controller;
        this.defaultSite = other.defaultSite;
        this.style = other.style;
        this.controller = other.controller;
    }
    
    public abstract void createTopology(final int beginIndex, final int numEdges);
    
    public SiteType defaultSite() {
        return this.defaultSite;
    }
    
    public int numSites() {
        if (this.defaultSite != SiteType.Cell) {
            return this.topology.getGraphElements(this.defaultSite).size();
        }
        return this.numSites;
    }
    
    public boolean isHand() {
        return false;
    }
    
    public boolean isDice() {
        return false;
    }
    
    public boolean isDeck() {
        return false;
    }
    
    public boolean isBoardless() {
        return false;
    }
    
    public Container clone() {
        Container c;
        try {
            c = (Container)super.clone();
            c.setName(this.name());
            c.setIndex(this.index());
            c.setNumSites(this.numSites);
        }
        catch (CloneNotSupportedException e) {
            throw new Error();
        }
        return c;
    }
    
    public void setNumSites(final int numSites) {
        this.numSites = numSites;
    }
    
    public List<Track> tracks() {
        return Collections.unmodifiableList(this.tracks);
    }
    
    public Topology topology() {
        return this.topology;
    }
    
    public ContainerStyleType style() {
        return this.style;
    }
    
    public void setStyle(final ContainerStyleType st) {
        this.style = st;
    }
    
    public ControllerType controller() {
        return this.controller;
    }
    
    public void setController(final ControllerType controller) {
        this.controller = controller;
    }
    
    public Track[] ownedTracks(final int owner) {
        if (owner < this.ownedTracks.length) {
            return this.ownedTracks[owner];
        }
        return new Track[0];
    }
    
    public void setOwnedTrack(final Track[][] ownedTracks) {
        this.ownedTracks = ownedTracks;
    }
    
    public static void createSymmetries(final Topology topo) {
        createSymmetries(topo, 24);
        if (topo.cellReflectionSymmetries().length == 0 && topo.cellRotationSymmetries().length == 0) {
            createSymmetries(topo, 5);
        }
        if (topo.cellReflectionSymmetries().length == 0 && topo.cellRotationSymmetries().length == 0) {
            createSymmetries(topo, 7);
        }
    }
    
    private static void createSymmetries(final Topology topology, final int symmetries) {
        final List<Cell> cells = topology.cells();
        final List<Edge> edges = topology.edges();
        final List<Vertex> vertices = topology.vertices();
        final Point2D origin1 = topology.centrePoint();
        Point2D origin2 = new Point2D.Double(0.5, 0.5);
        if (origin1.equals(origin2)) {
            origin2 = null;
        }
        final int[][] cellRotations = new int[symmetries][];
        final int[][] edgeRotations = new int[symmetries][];
        final int[][] vertexRotations = new int[symmetries][];
        int rotCount = 0;
        for (int turns = 0; turns < symmetries; ++turns) {
            int[] cRots = calcCellRotation(cells, origin1, turns, symmetries);
            int[] eRots = calcEdgeRotation(edges, origin1, turns, symmetries);
            int[] vRots = calcVertexRotation(vertices, origin1, turns, symmetries);
            if (origin2 != null && (!SymmetryUtils.isBijective(cRots) || !SymmetryUtils.isBijective(eRots) || !SymmetryUtils.isBijective(vRots))) {
                cRots = calcCellRotation(cells, origin1, turns, symmetries);
                eRots = calcEdgeRotation(edges, origin1, turns, symmetries);
                vRots = calcVertexRotation(vertices, origin1, turns, symmetries);
            }
            if (SymmetryUtils.isBijective(cRots) && SymmetryUtils.isBijective(eRots) && SymmetryUtils.isBijective(vRots)) {
                cellRotations[rotCount] = cRots;
                edgeRotations[rotCount] = eRots;
                vertexRotations[rotCount] = vRots;
                ++rotCount;
            }
        }
        topology.setCellRotationSymmetries(Arrays.copyOf(cellRotations, rotCount));
        topology.setEdgeRotationSymmetries(Arrays.copyOf(edgeRotations, rotCount));
        topology.setVertexRotationSymmetries(Arrays.copyOf(vertexRotations, rotCount));
        final int[][] cellReflections = new int[symmetries][];
        final int[][] edgeReflections = new int[symmetries][];
        final int[][] vertexReflections = new int[symmetries][];
        int refCount = 0;
        for (int turns = 0; turns < symmetries; ++turns) {
            int[] cRefs = calcCellReflection(cells, origin1, turns, symmetries);
            int[] eRefs = calcEdgeReflection(edges, origin1, turns, symmetries);
            int[] vRefs = calcVertexReflection(vertices, origin1, turns, symmetries);
            if (origin2 != null && (!SymmetryUtils.isBijective(cRefs) || !SymmetryUtils.isBijective(eRefs) || !SymmetryUtils.isBijective(vRefs))) {
                cRefs = calcCellReflection(cells, origin1, turns, symmetries);
                eRefs = calcEdgeReflection(edges, origin1, turns, symmetries);
                vRefs = calcVertexReflection(vertices, origin1, turns, symmetries);
            }
            if (SymmetryUtils.isBijective(cRefs) && SymmetryUtils.isBijective(eRefs) && SymmetryUtils.isBijective(vRefs)) {
                cellReflections[refCount] = cRefs;
                edgeReflections[refCount] = eRefs;
                vertexReflections[refCount] = vRefs;
                ++refCount;
            }
        }
        topology.setCellReflectionSymmetries(Arrays.copyOf(cellReflections, refCount));
        topology.setEdgeReflectionSymmetries(Arrays.copyOf(edgeReflections, refCount));
        topology.setVertexReflectionSymmetries(Arrays.copyOf(vertexReflections, refCount));
    }
    
    private static int[] calcCellRotation(final List<Cell> cells, final Point2D origin, final int turns, final int symmetries) {
        final int[] rots = new int[cells.size()];
        for (int cell = 0; cell < cells.size(); ++cell) {
            final Point2D start = cells.get(cell).centroid();
            final Point2D end = SymmetryUtils.rotateAroundPoint(origin, start, turns, symmetries);
            rots[cell] = findMatchingCell(cells, end);
            if (rots[cell] == -1) {
                break;
            }
        }
        return rots;
    }
    
    private static int[] calcEdgeRotation(final List<Edge> edges, final Point2D origin, final int turns, final int symmetries) {
        final int[] rots = new int[edges.size()];
        for (int edge = 0; edge < edges.size(); ++edge) {
            final Point2D pt1 = edges.get(edge).vA().centroid();
            final Point2D pt2 = edges.get(edge).vB().centroid();
            final Point2D end1 = SymmetryUtils.rotateAroundPoint(origin, pt1, turns, symmetries);
            final Point2D end2 = SymmetryUtils.rotateAroundPoint(origin, pt2, turns, symmetries);
            rots[edge] = findMatchingEdge(edges, end1, end2);
            if (rots[edge] == -1) {
                break;
            }
        }
        return rots;
    }
    
    private static int[] calcVertexRotation(final List<Vertex> vertices, final Point2D origin, final int turns, final int symmetries) {
        final int[] rots = new int[vertices.size()];
        for (int vertexIndex = 0; vertexIndex < vertices.size(); ++vertexIndex) {
            final Point2D start = vertices.get(vertexIndex).centroid();
            final Point2D end = SymmetryUtils.rotateAroundPoint(origin, start, turns, symmetries);
            rots[vertexIndex] = findMatchingVertex(vertices, end);
            if (rots[vertexIndex] == -1) {
                break;
            }
        }
        return rots;
    }
    
    private static int findMatchingVertex(final List<Vertex> vertices, final Point2D end) {
        for (int vertex = 0; vertex < vertices.size(); ++vertex) {
            if (SymmetryUtils.closeEnough(end, vertices.get(vertex).centroid(), 1.0E-6)) {
                return vertex;
            }
        }
        return -1;
    }
    
    private static int findMatchingEdge(final List<Edge> edges, final Point2D pos1, final Point2D pos2) {
        for (int edgeIndex = 0; edgeIndex < edges.size(); ++edgeIndex) {
            final Edge edge = edges.get(edgeIndex);
            final Point2D ptA = edge.vA().centroid();
            final Point2D ptB = edge.vB().centroid();
            if ((SymmetryUtils.closeEnough(pos1, ptA, 1.0E-6) && SymmetryUtils.closeEnough(pos2, ptB, 1.0E-6)) || (SymmetryUtils.closeEnough(pos1, ptB, 1.0E-6) && SymmetryUtils.closeEnough(pos2, ptA, 1.0E-6))) {
                return edgeIndex;
            }
        }
        return -1;
    }
    
    private static int findMatchingCell(final List<Cell> cells, final Point2D pos) {
        for (int cell = 0; cell < cells.size(); ++cell) {
            if (SymmetryUtils.closeEnough(pos, cells.get(cell).centroid(), 1.0E-6)) {
                return cell;
            }
        }
        return -1;
    }
    
    private static int[] calcCellReflection(final List<Cell> cells, final Point2D origin, final int turns, final int symmetries) {
        final int[] refs = new int[cells.size()];
        for (int cell = 0; cell < cells.size(); ++cell) {
            final Point2D start = cells.get(cell).centroid();
            final Point2D end = SymmetryUtils.reflectAroundLine(origin, start, turns, symmetries);
            refs[cell] = findMatchingCell(cells, end);
            if (refs[cell] == -1) {
                break;
            }
        }
        return refs;
    }
    
    private static int[] calcEdgeReflection(final List<Edge> edges, final Point2D origin, final int turns, final int symmetries) {
        final int[] refs = new int[edges.size()];
        for (int edgeIndex = 0; edgeIndex < edges.size(); ++edgeIndex) {
            final Point2D p1 = edges.get(edgeIndex).vA().centroid();
            final Point2D p2 = edges.get(edgeIndex).vB().centroid();
            final Point2D end1 = SymmetryUtils.reflectAroundLine(origin, p1, turns, symmetries);
            final Point2D end2 = SymmetryUtils.reflectAroundLine(origin, p2, turns, symmetries);
            refs[edgeIndex] = findMatchingEdge(edges, end1, end2);
            if (refs[edgeIndex] == -1) {
                break;
            }
        }
        return refs;
    }
    
    private static int[] calcVertexReflection(final List<Vertex> vertices, final Point2D origin, final int turns, final int symmetries) {
        final int[] refs = new int[vertices.size()];
        for (int vertex = 0; vertex < vertices.size(); ++vertex) {
            final Point2D start = vertices.get(vertex).centroid();
            final Point2D end = SymmetryUtils.reflectAroundLine(origin, start, turns, symmetries);
            refs[vertex] = findMatchingVertex(vertices, end);
            if (refs[vertex] == -1) {
                break;
            }
        }
        return refs;
    }
}
