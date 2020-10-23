// 
// Decompiled by Procyon v0.5.36
// 

package topology;

import game.Game;
import game.equipment.container.Container;
import game.equipment.other.Regions;
import game.functions.region.RegionFunction;
import game.types.board.RelationType;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.directions.CompassDirection;
import game.util.directions.DirectionFacing;
import game.util.graph.Properties;
import game.util.graph.*;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import main.math.MathRoutines;
import util.Context;
import util.GraphUtilities;
import util.Trial;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

public class Topology implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final List<Cell> cells;
    private final List<Edge> edges;
    private final List<Vertex> vertices;
    private Graph graph;
    private int numEdges;
    private Trajectories trajectories;
    private final Map<SiteType, List<DirectionFacing>> supportedDirections;
    private final Map<SiteType, List<DirectionFacing>> supportedOrthogonalDirections;
    private final Map<SiteType, List<DirectionFacing>> supportedDiagonalDirections;
    private final Map<SiteType, List<DirectionFacing>> supportedAdjacentDirections;
    private final Map<SiteType, List<DirectionFacing>> supportedOffDirections;
    private final Map<SiteType, List<TopologyElement>> corners;
    private final Map<SiteType, List<TopologyElement>> cornersConvex;
    private final Map<SiteType, List<TopologyElement>> cornersConcave;
    private final Map<SiteType, List<TopologyElement>> major;
    private final Map<SiteType, List<TopologyElement>> minor;
    private final Map<SiteType, List<TopologyElement>> outer;
    private final Map<SiteType, List<TopologyElement>> perimeter;
    private final Map<SiteType, List<TopologyElement>> inner;
    private final Map<SiteType, List<TopologyElement>> interlayer;
    private final Map<SiteType, List<TopologyElement>> top;
    private final Map<SiteType, List<TopologyElement>> left;
    private final Map<SiteType, List<TopologyElement>> right;
    private final Map<SiteType, List<TopologyElement>> bottom;
    private final Map<SiteType, List<TopologyElement>> centre;
    private final Map<SiteType, List<List<TopologyElement>>> columns;
    private final Map<SiteType, List<List<TopologyElement>>> rows;
    private final Map<SiteType, List<List<TopologyElement>>> phases;
    private final Map<SiteType, Map<DirectionFacing, List<TopologyElement>>> sides;
    private final Map<SiteType, int[][]> distanceToOtherSite;
    private final Map<SiteType, int[]> distanceToCorners;
    private final Map<SiteType, int[]> distanceToSides;
    private final Map<SiteType, int[]> distanceToCentre;
    private final Map<SiteType, List<List<TopologyElement>>> layers;
    private final Map<SiteType, List<List<TopologyElement>>> diagonals;
    private final Map<SiteType, List<TopologyElement>> axials;
    private final Map<SiteType, List<TopologyElement>> horizontal;
    private final Map<SiteType, List<TopologyElement>> vertical;
    private final Map<SiteType, List<TopologyElement>> angled;
    private final Map<SiteType, List<TopologyElement>> slash;
    private final Map<SiteType, List<TopologyElement>> slosh;
    private final Map<SiteType, int[][]> distanceToRegions;
    private final Map<SiteType, TIntArrayList> connectivities;
    private List<Perimeter> perimeters;
    private int[][] cellRotationSymmetries;
    private int[][] cellReflectionSymmetries;
    private int[][] edgeRotationSymmetries;
    private int[][] edgeReflectionSymmetries;
    private int[][] vertexRotationSymmetries;
    private int[][] vertexReflectionSymmetries;
    
    public Topology() {
        this.cells = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.vertices = new ArrayList<>();
        this.graph = null;
        this.numEdges = -1;
        this.supportedDirections = new EnumMap<>(SiteType.class);
        this.supportedOrthogonalDirections = new EnumMap<>(SiteType.class);
        this.supportedDiagonalDirections = new EnumMap<>(SiteType.class);
        this.supportedAdjacentDirections = new EnumMap<>(SiteType.class);
        this.supportedOffDirections = new EnumMap<>(SiteType.class);
        this.corners = new EnumMap<>(SiteType.class);
        this.cornersConvex = new EnumMap<>(SiteType.class);
        this.cornersConcave = new EnumMap<>(SiteType.class);
        this.major = new EnumMap<>(SiteType.class);
        this.minor = new EnumMap<>(SiteType.class);
        this.outer = new EnumMap<>(SiteType.class);
        this.perimeter = new EnumMap<>(SiteType.class);
        this.inner = new EnumMap<>(SiteType.class);
        this.interlayer = new EnumMap<>(SiteType.class);
        this.top = new EnumMap<>(SiteType.class);
        this.left = new EnumMap<>(SiteType.class);
        this.right = new EnumMap<>(SiteType.class);
        this.bottom = new EnumMap<>(SiteType.class);
        this.centre = new EnumMap<>(SiteType.class);
        this.columns = new EnumMap<>(SiteType.class);
        this.rows = new EnumMap<>(SiteType.class);
        this.phases = new EnumMap<>(SiteType.class);
        this.sides = new EnumMap<>(SiteType.class);
        this.distanceToOtherSite = new EnumMap<>(SiteType.class);
        this.distanceToCorners = new EnumMap<>(SiteType.class);
        this.distanceToSides = new EnumMap<>(SiteType.class);
        this.distanceToCentre = new EnumMap<>(SiteType.class);
        this.layers = new EnumMap<>(SiteType.class);
        this.diagonals = new EnumMap<>(SiteType.class);
        this.axials = new EnumMap<>(SiteType.class);
        this.horizontal = new EnumMap<>(SiteType.class);
        this.vertical = new EnumMap<>(SiteType.class);
        this.angled = new EnumMap<>(SiteType.class);
        this.slash = new EnumMap<>(SiteType.class);
        this.slosh = new EnumMap<>(SiteType.class);
        this.distanceToRegions = new EnumMap<>(SiteType.class);
        this.connectivities = new EnumMap<>(SiteType.class);
        this.perimeters = new ArrayList<>();
        for (final SiteType type : SiteType.values()) {
            this.corners.put(type, new ArrayList<>());
            this.cornersConcave.put(type, new ArrayList<>());
            this.cornersConvex.put(type, new ArrayList<>());
            this.major.put(type, new ArrayList<>());
            this.minor.put(type, new ArrayList<>());
            this.outer.put(type, new ArrayList<>());
            this.perimeter.put(type, new ArrayList<>());
            this.inner.put(type, new ArrayList<>());
            this.interlayer.put(type, new ArrayList<>());
            this.top.put(type, new ArrayList<>());
            this.bottom.put(type, new ArrayList<>());
            this.left.put(type, new ArrayList<>());
            this.right.put(type, new ArrayList<>());
            this.centre.put(type, new ArrayList<>());
            this.phases.put(type, new ArrayList<>());
            for (int i = 0; i < 4; ++i) {
                this.phases.get(type).add(new ArrayList<>());
            }
            this.rows.put(type, new ArrayList<>());
            this.columns.put(type, new ArrayList<>());
            this.layers.put(type, new ArrayList<>());
            this.diagonals.put(type, new ArrayList<>());
            this.axials.put(type, new ArrayList<>());
            this.horizontal.put(type, new ArrayList<>());
            this.vertical.put(type, new ArrayList<>());
            this.angled.put(type, new ArrayList<>());
            this.slash.put(type, new ArrayList<>());
            this.slosh.put(type, new ArrayList<>());
            this.sides.put(type, new HashMap<>());
            for (final CompassDirection direction : CompassDirection.values()) {
                this.sides.get(type).put(direction, new ArrayList<>());
            }
            this.supportedDirections.put(type, new ArrayList<>());
            this.supportedOrthogonalDirections.put(type, new ArrayList<>());
            this.supportedDiagonalDirections.put(type, new ArrayList<>());
            this.supportedAdjacentDirections.put(type, new ArrayList<>());
            this.supportedOffDirections.put(type, new ArrayList<>());
        }
    }
    
    public Graph graph() {
        return this.graph;
    }
    
    public void setGraph(final Graph gr) {
        this.graph = gr;
    }
    
    public void setCellRotationSymmetries(final int[][] cellRotationSymmetries) {
        this.cellRotationSymmetries = cellRotationSymmetries;
    }
    
    public void setCellReflectionSymmetries(final int[][] cellReflectionSymmetries) {
        this.cellReflectionSymmetries = cellReflectionSymmetries;
    }
    
    public void setEdgeRotationSymmetries(final int[][] edgeRotationSymmetries) {
        this.edgeRotationSymmetries = edgeRotationSymmetries;
    }
    
    public void setEdgeReflectionSymmetries(final int[][] edgeReflectionSymmetries) {
        this.edgeReflectionSymmetries = edgeReflectionSymmetries;
    }
    
    public void setVertexRotationSymmetries(final int[][] vertexRotationSymmetries) {
        this.vertexRotationSymmetries = vertexRotationSymmetries;
    }
    
    public void setVertexReflectionSymmetries(final int[][] vertexReflectionSymmetries) {
        this.vertexReflectionSymmetries = vertexReflectionSymmetries;
    }
    
    public int[][] cellRotationSymmetries() {
        return this.cellRotationSymmetries;
    }
    
    public int[][] cellReflectionSymmetries() {
        return this.cellReflectionSymmetries;
    }
    
    public int[][] edgeRotationSymmetries() {
        return this.edgeRotationSymmetries;
    }
    
    public int[][] edgeReflectionSymmetries() {
        return this.edgeReflectionSymmetries;
    }
    
    public int[][] vertexRotationSymmetries() {
        return this.vertexRotationSymmetries;
    }
    
    public int[][] vertexReflectionSymmetries() {
        return this.vertexReflectionSymmetries;
    }
    
    public List<TopologyElement> corners(final SiteType type) {
        return this.corners.get(type);
    }
    
    public List<TopologyElement> cornersConcave(final SiteType type) {
        return this.cornersConcave.get(type);
    }
    
    public List<TopologyElement> cornersConvex(final SiteType type) {
        return this.cornersConvex.get(type);
    }
    
    public List<TopologyElement> major(final SiteType type) {
        return this.major.get(type);
    }
    
    public List<TopologyElement> minor(final SiteType type) {
        return this.minor.get(type);
    }
    
    public List<TopologyElement> outer(final SiteType type) {
        return this.outer.get(type);
    }
    
    public List<TopologyElement> perimeter(final SiteType type) {
        return this.perimeter.get(type);
    }
    
    public List<TopologyElement> top(final SiteType type) {
        return this.top.get(type);
    }
    
    public List<TopologyElement> bottom(final SiteType type) {
        return this.bottom.get(type);
    }
    
    public List<TopologyElement> left(final SiteType type) {
        return this.left.get(type);
    }
    
    public List<TopologyElement> right(final SiteType type) {
        return this.right.get(type);
    }
    
    public List<TopologyElement> centre(final SiteType type) {
        return this.centre.get(type);
    }
    
    public List<TopologyElement> axial(final SiteType type) {
        return this.axials.get(type);
    }
    
    public List<TopologyElement> horizontal(final SiteType type) {
        return this.horizontal.get(type);
    }
    
    public List<TopologyElement> vertical(final SiteType type) {
        return this.vertical.get(type);
    }
    
    public List<TopologyElement> angled(final SiteType type) {
        return this.angled.get(type);
    }
    
    public List<TopologyElement> slash(final SiteType type) {
        return this.slash.get(type);
    }
    
    public List<TopologyElement> slosh(final SiteType type) {
        return this.slosh.get(type);
    }
    
    public List<TopologyElement> inner(final SiteType type) {
        return this.inner.get(type);
    }
    
    public List<TopologyElement> interlayer(final SiteType type) {
        return this.interlayer.get(type);
    }
    
    public List<List<TopologyElement>> rows(final SiteType type) {
        return this.rows.get(type);
    }
    
    public List<List<TopologyElement>> columns(final SiteType type) {
        return this.columns.get(type);
    }
    
    public List<List<TopologyElement>> layers(final SiteType type) {
        return this.layers.get(type);
    }
    
    public List<List<TopologyElement>> diagonals(final SiteType type) {
        return this.diagonals.get(type);
    }
    
    public List<List<TopologyElement>> phases(final SiteType type) {
        return this.phases.get(type);
    }
    
    public Map<DirectionFacing, List<TopologyElement>> sides(final SiteType type) {
        return this.sides.get(type);
    }
    
    public List<Cell> cells() {
        return this.cells;
    }
    
    public List<Edge> edges() {
        return this.edges;
    }
    
    public List<Vertex> vertices() {
        return this.vertices;
    }
    
    public List<DirectionFacing> supportedDirections(final RelationType relationType, final SiteType type) {
        switch (relationType) {
            case Adjacent: {
                return this.supportedAdjacentDirections.get(type);
            }
            case Diagonal: {
                return this.supportedDiagonalDirections.get(type);
            }
            case All: {
                return this.supportedDirections.get(type);
            }
            case OffDiagonal: {
                return this.supportedOffDirections.get(type);
            }
            case Orthogonal: {
                return this.supportedOrthogonalDirections.get(type);
            }
            default: {
                return this.supportedDirections.get(type);
            }
        }
    }
    
    public List<DirectionFacing> supportedDirections(final SiteType type) {
        return this.supportedDirections.get(type);
    }
    
    public List<DirectionFacing> supportedOrthogonalDirections(final SiteType type) {
        return this.supportedOrthogonalDirections.get(type);
    }
    
    public List<DirectionFacing> supportedOffDirections(final SiteType type) {
        return this.supportedOffDirections.get(type);
    }
    
    public List<DirectionFacing> supportedDiagonalDirections(final SiteType type) {
        return this.supportedDiagonalDirections.get(type);
    }
    
    public List<DirectionFacing> supportedAdjacentDirections(final SiteType type) {
        return this.supportedAdjacentDirections.get(type);
    }
    
    public int[] distancesToCorners(final SiteType type) {
        return this.distanceToCorners.get(type);
    }
    
    public int[] distancesToSides(final SiteType type) {
        return this.distanceToSides.get(type);
    }
    
    public int[] distancesToCentre(final SiteType type) {
        return this.distanceToCentre.get(type);
    }
    
    public int[][] distancesToOtherSite(final SiteType type) {
        return this.distanceToOtherSite.get(type);
    }
    
    public int[][] distancesToRegions(final SiteType type) {
        return this.distanceToRegions.get(type);
    }
    
    public void setDistanceToCorners(final SiteType type, final int[] distanceToCorners) {
        this.distanceToCorners.put(type, distanceToCorners);
    }
    
    public void setDistanceToSides(final SiteType type, final int[] distanceToSides) {
        this.distanceToSides.put(type, distanceToSides);
    }
    
    public void setDistanceToCentre(final SiteType type, final int[] distanceToCentre) {
        this.distanceToCentre.put(type, distanceToCentre);
    }
    
    public int numEdges() {
        return this.numEdges;
    }
    
    public Cell findCell(final double x, final double y) {
        for (final Cell cell : this.cells) {
            if (cell.matches(x, y)) {
                return cell;
            }
        }
        return null;
    }
    
    public Edge findEdge(final Vertex va, final Vertex vb) {
        for (final Edge edge : this.edges) {
            if (edge.matches(va, vb)) {
                return edge;
            }
        }
        return null;
    }
    
    public Edge findEdge(final Point2D pa, final Point2D pb) {
        for (final Edge edge : this.edges) {
            if (edge.matches(pa, pb)) {
                return edge;
            }
        }
        return null;
    }
    
    public Edge midpointEdgeUsed(final Point2D midpoint) {
        for (final Edge edge : this.edges) {
            if (Math.abs(edge.centroid().getX() - midpoint.getX()) < 1.0E-4 && Math.abs(edge.centroid().getY() - midpoint.getY()) < 1.0E-4) {
                return edge;
            }
        }
        return null;
    }
    
    public Cell getCellWithCoords(final int row, final int col, final int level) {
        for (final Cell v : this.cells) {
            if (v.row() == row && v.col() == col && v.layer() == level) {
                return v;
            }
        }
        return null;
    }
    
    public Vertex getVertexWithCoords(final int row, final int col, final int level) {
        for (final Vertex v : this.vertices) {
            if (v.row() == row && v.col() == col && v.layer() == level) {
                return v;
            }
        }
        return null;
    }
    
    public TopologyElement getGraphElement(final SiteType graphElementType, final int index) {
        switch (graphElementType) {
            case Vertex: {
                return this.vertices().get(index);
            }
            case Edge: {
                return this.edges().get(index);
            }
            case Cell: {
                return this.cells().get(index);
            }
            default: {
                return null;
            }
        }
    }
    
    public List<? extends TopologyElement> getGraphElements(final SiteType graphElementType) {
        switch (graphElementType) {
            case Vertex: {
                return this.vertices();
            }
            case Edge: {
                return this.edges();
            }
            case Cell: {
                return this.cells();
            }
            default: {
                return null;
            }
        }
    }
    
    public int numSites(final SiteType type) {
        switch (type) {
            case Vertex: {
                return this.vertices().size();
            }
            case Edge: {
                return this.edges().size();
            }
            case Cell: {
                return this.cells().size();
            }
            default: {
                return -1;
            }
        }
    }
    
    public ArrayList<TopologyElement> getAllGraphElements() {
        final ArrayList<TopologyElement> allGraphElements = new ArrayList<>();
        allGraphElements.addAll(this.vertices());
        allGraphElements.addAll(this.edges());
        allGraphElements.addAll(this.cells());
        return allGraphElements;
    }
    
    public void preGenerateDistanceToEachElementToEachOther(final SiteType type, final RelationType relation) {
        final List<? extends TopologyElement> elements = this.getGraphElements(type);
        final int[][] distances = new int[elements.size()][elements.size()];
        for (int idElem = 0; idElem < elements.size(); ++idElem) {
            final TopologyElement element = elements.get(idElem);
            int currDist = 0;
            final TIntArrayList currList = new TIntArrayList();
            switch (relation) {
                case Adjacent: {
                    for (final TopologyElement elem : element.adjacent()) {
                        currList.add(elem.index());
                    }
                    break;
                }
                case All: {
                    for (final TopologyElement elem : element.neighbours()) {
                        currList.add(elem.index());
                    }
                    break;
                }
                case Diagonal: {
                    for (final TopologyElement elem : element.diagonal()) {
                        currList.add(elem.index());
                    }
                    break;
                }
                case OffDiagonal: {
                    for (final TopologyElement elem : element.off()) {
                        currList.add(elem.index());
                    }
                    break;
                }
                case Orthogonal: {
                    for (final TopologyElement elem : element.orthogonal()) {
                        currList.add(elem.index());
                    }
                    break;
                }
            }
            final TIntArrayList nextList = new TIntArrayList();
            while (!currList.isEmpty()) {
                ++currDist;
                for (int i = 0; i < currList.size(); ++i) {
                    final int idNeighbour = currList.get(i);
                    if (idNeighbour != idElem) {
                        if (distances[idElem][idNeighbour] <= 0) {
                            distances[idElem][idNeighbour] = currDist;
                            switch (relation) {
                                case Adjacent: {
                                    for (final TopologyElement elem2 : elements.get(idNeighbour).adjacent()) {
                                        if (!nextList.contains(elem2.index()) && !currList.contains(elem2.index())) {
                                            nextList.add(elem2.index());
                                        }
                                    }
                                    break;
                                }
                                case All: {
                                    for (final TopologyElement elem2 : elements.get(idNeighbour).neighbours()) {
                                        if (!nextList.contains(elem2.index()) && !currList.contains(elem2.index())) {
                                            nextList.add(elem2.index());
                                        }
                                    }
                                    break;
                                }
                                case Diagonal: {
                                    for (final TopologyElement elem2 : elements.get(idNeighbour).diagonal()) {
                                        if (!nextList.contains(elem2.index()) && !currList.contains(elem2.index())) {
                                            nextList.add(elem2.index());
                                        }
                                    }
                                    break;
                                }
                                case OffDiagonal: {
                                    for (final TopologyElement elem2 : elements.get(idNeighbour).off()) {
                                        if (!nextList.contains(elem2.index()) && !currList.contains(elem2.index())) {
                                            nextList.add(elem2.index());
                                        }
                                    }
                                    break;
                                }
                                case Orthogonal: {
                                    for (final TopologyElement elem2 : elements.get(idNeighbour).orthogonal()) {
                                        if (!nextList.contains(elem2.index()) && !currList.contains(elem2.index())) {
                                            nextList.add(elem2.index());
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                currList.clear();
                currList.addAll(nextList);
                nextList.clear();
            }
        }
        this.distanceToOtherSite.put(type, distances);
        for (int idElem = 0; idElem < elements.size(); ++idElem) {
            final TopologyElement element = elements.get(idElem);
            int maxDistance = 0;
            for (int idOtherElem = 0; idOtherElem < elements.size(); ++idOtherElem) {
                if (maxDistance < this.distancesToOtherSite(type)[idElem][idOtherElem]) {
                    ++maxDistance;
                }
            }
            final List<TopologyElement> distanceZero = new ArrayList<>();
            distanceZero.add(element);
            element.sitesAtDistance().add(distanceZero);
            for (int distance = 1; distance < maxDistance; ++distance) {
                final List<TopologyElement> sitesAtDistance = new ArrayList<>();
                for (int idOtherElem2 = 0; idOtherElem2 < elements.size(); ++idOtherElem2) {
                    if (this.distancesToOtherSite(type)[idElem][idOtherElem2] == distance) {
                        sitesAtDistance.add(elements.get(idOtherElem2));
                    }
                }
                element.sitesAtDistance().add(sitesAtDistance);
            }
        }
    }
    
    public void preGenerateDistanceToRegionsCells(final Game game, final Regions[] regions) {
        final int numCells = this.cells.size();
        if (numCells == 0) {
            return;
        }
        final int[][] distances = new int[regions.length][];
        final Context dummyContext = new Context(game, new Trial(game));
        for (int i = 0; i < regions.length; ++i) {
            distances[i] = null;
            final Regions region = regions[i];
            int[] regionSites = null;
            if (region.region() != null) {
                boolean allStatic = true;
                for (final RegionFunction regionFunc : region.region()) {
                    if (regionFunc.type(game) != SiteType.Cell) {
                        allStatic = false;
                    }
                    else {
                        if (!regionFunc.isStatic()) {
                            allStatic = false;
                            break;
                        }
                        if (regionSites == null) {
                            regionSites = regionFunc.eval(dummyContext).sites();
                        }
                        else {
                            final int[] toAppend = regionFunc.eval(dummyContext).sites();
                            regionSites = Arrays.copyOf(regionSites, regionSites.length + toAppend.length);
                            System.arraycopy(toAppend, 0, regionSites, regionSites.length - toAppend.length, toAppend.length);
                        }
                    }
                }
                if (!allStatic) {
                    continue;
                }
            }
            else {
                if (region.sites() == null) {
                    continue;
                }
                regionSites = region.sites();
            }
            distances[i] = new int[numCells];
            final boolean[] startingPoint = new boolean[numCells];
            for (int j = 0; j < regionSites.length; ++j) {
                final int regionSite = regionSites[j];
                if (regionSite < this.cells.size()) {
                    final Cell regionCell = this.cells.get(regionSite);
                    final boolean[] visited = new boolean[numCells];
                    int currDist = 0;
                    distances[i][regionSite] = currDist;
                    startingPoint[regionSite] = (visited[regionSite] = true);
                    final List<Cell> currNeighbourList = new ArrayList<>();
                    currNeighbourList.addAll(regionCell.adjacent());
                    final List<Cell> nextNeighbourList = new ArrayList<>();
                    while (!currNeighbourList.isEmpty()) {
                        ++currDist;
                        for (final Cell neighbour : currNeighbourList) {
                            final int idx = neighbour.index();
                            if (!visited[idx]) {
                                if (startingPoint[idx]) {
                                    continue;
                                }
                                if (distances[i][idx] > 0 && distances[i][idx] <= currDist) {
                                    continue;
                                }
                                distances[i][idx] = currDist;
                                nextNeighbourList.addAll(neighbour.neighbours());
                            }
                        }
                        currNeighbourList.clear();
                        currNeighbourList.addAll(nextNeighbourList);
                        nextNeighbourList.clear();
                    }
                }
            }
        }
        this.distanceToRegions.put(SiteType.Cell, distances);
    }
    
    public void preGenerateDistanceToRegionsVertices(final Game game, final Regions[] regions) {
        final int numVertices = this.vertices.size();
        final int[][] distances = new int[regions.length][];
        final Context dummyContext = new Context(game, new Trial(game));
        for (int i = 0; i < regions.length; ++i) {
            distances[i] = null;
            final Regions region = regions[i];
            int[] regionSites = null;
            if (region.region() != null) {
                boolean allStatic = true;
                for (final RegionFunction regionFunc : region.region()) {
                    if (regionFunc.type(game) != SiteType.Vertex) {
                        allStatic = false;
                    }
                    else {
                        if (!regionFunc.isStatic()) {
                            allStatic = false;
                            break;
                        }
                        if (regionSites == null) {
                            regionSites = regionFunc.eval(null).sites();
                        }
                        else {
                            final int[] toAppend = regionFunc.eval(dummyContext).sites();
                            regionSites = Arrays.copyOf(regionSites, regionSites.length + toAppend.length);
                            System.arraycopy(toAppend, 0, regionSites, regionSites.length - toAppend.length, toAppend.length);
                        }
                    }
                }
                if (!allStatic) {
                    continue;
                }
            }
            else {
                if (region.sites() == null) {
                    continue;
                }
                regionSites = region.sites();
            }
            distances[i] = new int[numVertices];
            final boolean[] startingPoint = new boolean[numVertices];
            for (int j = 0; j < regionSites.length; ++j) {
                final int regionSite = regionSites[j];
                final Vertex regionVertex = this.vertices.get(regionSite);
                final boolean[] visited = new boolean[numVertices];
                int currDist = 0;
                distances[i][regionSite] = currDist;
                startingPoint[regionSite] = (visited[regionSite] = true);
                final List<Vertex> currNeighbourList = new ArrayList<>();
                currNeighbourList.addAll(regionVertex.adjacent());
                final List<Vertex> nextNeighbourList = new ArrayList<>();
                while (!currNeighbourList.isEmpty()) {
                    ++currDist;
                    for (final Vertex neighbour : currNeighbourList) {
                        final int idx = neighbour.index();
                        if (!visited[idx]) {
                            if (startingPoint[idx]) {
                                continue;
                            }
                            if (distances[i][idx] > 0 && distances[i][idx] <= currDist) {
                                continue;
                            }
                            distances[i][idx] = currDist;
                            nextNeighbourList.addAll(neighbour.neighbours());
                        }
                    }
                    currNeighbourList.clear();
                    currNeighbourList.addAll(nextNeighbourList);
                    nextNeighbourList.clear();
                }
            }
        }
        this.distanceToRegions.put(SiteType.Vertex, distances);
    }
    
    public void preGenerateDistanceTables(final SiteType type) {
        this.preGenerateDistanceToPrecomputed(type, this.centre, this.distanceToCentre);
        this.preGenerateDistanceToPrecomputed(type, this.corners, this.distanceToCorners);
        this.preGenerateDistanceToPrecomputed(type, this.perimeter, this.distanceToSides);
    }
    
    private void preGenerateDistanceToPrecomputed(final SiteType type, final Map<SiteType, List<TopologyElement>> precomputed, final Map<SiteType, int[]> distancesMap) {
        final List<? extends TopologyElement> elements = this.getGraphElements(type);
        final int numElements = elements.size();
        if (numElements == 0) {
            return;
        }
        final int[] distances = new int[numElements];
        final boolean[] startingPoint = new boolean[numElements];
        for (final TopologyElement corner : precomputed.get(type)) {
            final boolean[] visited = new boolean[numElements];
            final int cornerIdx = corner.index();
            int currDist = 0;
            distances[cornerIdx] = currDist;
            startingPoint[cornerIdx] = (visited[cornerIdx] = true);
            final List<TopologyElement> currNeighbourList = new ArrayList<>();
            currNeighbourList.addAll(corner.adjacent());
            final List<TopologyElement> nextNeighbourList = new ArrayList<>();
            while (!currNeighbourList.isEmpty()) {
                ++currDist;
                for (final TopologyElement neighbour : currNeighbourList) {
                    final int idx = neighbour.index();
                    if (!visited[idx]) {
                        if (startingPoint[idx]) {
                            continue;
                        }
                        if (distances[idx] > 0 && distances[idx] <= currDist) {
                            continue;
                        }
                        distances[idx] = currDist;
                        nextNeighbourList.addAll(neighbour.neighbours());
                    }
                }
                currNeighbourList.clear();
                currNeighbourList.addAll(nextNeighbourList);
                nextNeighbourList.clear();
            }
        }
        distancesMap.put(type, distances);
    }
    
    public void optimiseMemory() {
        ((ArrayList)this.cells).trimToSize();
        ((ArrayList)this.edges).trimToSize();
        ((ArrayList)this.vertices).trimToSize();
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.corners.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.cornersConcave.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.cornersConvex.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.major.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.minor.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.outer.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.perimeter.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.inner.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.interlayer.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.top.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.bottom.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.left.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.right.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.centre.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<List<TopologyElement>>> list2 : this.phases.entrySet()) {
            ((ArrayList)list2.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<List<TopologyElement>>> list2 : this.rows.entrySet()) {
            ((ArrayList)list2.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<List<TopologyElement>>> list2 : this.columns.entrySet()) {
            ((ArrayList)list2.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<List<TopologyElement>>> list2 : this.layers.entrySet()) {
            ((ArrayList)list2.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<List<TopologyElement>>> list2 : this.diagonals.entrySet()) {
            ((ArrayList)list2.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.axials.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.horizontal.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.vertical.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.angled.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.slash.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Map.Entry<SiteType, List<TopologyElement>> list : this.slosh.entrySet()) {
            ((ArrayList)list.getValue()).trimToSize();
        }
        for (final Cell vertex : this.cells) {
            vertex.optimiseMemory();
        }
        for (final Edge edge : this.edges) {
            edge.optimiseMemory();
        }
        for (final Vertex face : this.vertices) {
            face.optimiseMemory();
        }
    }
    
    public TIntArrayList connectivities(final SiteType type) {
        return this.connectivities.get(type);
    }
    
    public TIntArrayList trueOrthoConnectivities(final Game game) {
        if (game.board().defaultSite() == SiteType.Vertex) {
            return this.connectivities.get(SiteType.Vertex);
        }
        return this.connectivities.get(SiteType.Cell);
    }
    
    public void pregenerateFeaturesData(final Game game, final Container container) {
        this.pregenerateFeaturesData(container, SiteType.Cell);
        this.pregenerateFeaturesData(container, SiteType.Vertex);
        if (game.board().defaultSite() == SiteType.Edge) {
            this.pregenerateFeaturesData(container, SiteType.Edge);
        }
    }
    
    public List<Perimeter> perimeters() {
        return this.perimeters;
    }
    
    public void setPerimeter(final List<Perimeter> perimeters) {
        this.perimeters = perimeters;
    }
    
    public void pregenerateFeaturesData(final Container container, final SiteType type) {
        final List<? extends TopologyElement> elements = this.getGraphElements(type);
        this.connectivities.put(type, new TIntArrayList());
        for (final TopologyElement element : elements) {
            final int elementIdx = element.index;
            final List<TopologyElement> sortedOrthos = new ArrayList<>();
            final List<Radial> radials = new ArrayList<>(this.trajectories().radials(type, elementIdx).inDirection(AbsoluteDirection.Orthogonal));
            for (int numRealRadials = radials.size(), i = 0; i < numRealRadials; ++i) {
                final Radial realRadial = radials.get(i);
                final TopologyElement neighbour = elements.get(realRadial.steps()[1].id());
                final List<Radial> neighbourRadials = this.trajectories().radials(type, neighbour.index).inDirection(AbsoluteDirection.All);
                for (final Radial neighbourRadial : neighbourRadials) {
                    final GraphElement[] path = neighbourRadial.steps();
                    if (path.length == 2 && path[1].id() == elementIdx) {
                        final double x = 2.0 * path[1].pt().x() - path[0].pt().x();
                        final double y = 2.0 * path[1].pt().y() - path[0].pt().y();
                        final double z = 2.0 * path[1].pt().z() - path[0].pt().z();
                        final Radial placeholder = new Radial(new GraphElement[] { path[1], new game.util.graph.Vertex(-1, x, y, z) }, AbsoluteDirection.Orthogonal);
                        radials.add(placeholder);
                    }
                }
            }
            Radials.sort(radials);
            for (final Radial radial : radials) {
                final GraphElement[] path2 = radial.steps();
                if (path2[1].id() == -1) {
                    sortedOrthos.add(null);
                }
                else {
                    sortedOrthos.add(elements.get(path2[1].id()));
                }
            }
            if (!this.connectivities.get(type).contains(sortedOrthos.size())) {
                this.connectivities.get(type).add(sortedOrthos.size());
            }
            element.setSortedOrthos(sortedOrthos.toArray(new TopologyElement[sortedOrthos.size()]));
        }
        this.connectivities.get(type).sort();
        this.connectivities.get(type).trimToSize();
    }
    
    public Point2D.Double centrePoint() {
        if (this.centre.get(SiteType.Cell).size() == 0) {
            return new Point2D.Double(0.5, 0.5);
        }
        double avgX = 0.0;
        double avgY = 0.0;
        for (final TopologyElement element : this.centre.get(SiteType.Cell)) {
            avgX += element.centroid().getX();
            avgY += element.centroid().getY();
        }
        avgX /= this.centre.get(SiteType.Cell).size();
        avgY /= this.centre.get(SiteType.Cell).size();
        return new Point2D.Double(avgX, avgY);
    }
    
    public void convertPropertiesToList(final SiteType type, final TopologyElement element) {
        final Properties properties = element.properties();
        if (properties.get(1L)) {
            this.inner(type).add(element);
        }
        if (properties.get(2L)) {
            this.outer(type).add(element);
        }
        if (properties.get(128L)) {
            this.interlayer(type).add(element);
        }
        if (properties.get(4L)) {
            this.perimeter(type).add(element);
        }
        if (properties.get(1024L)) {
            this.corners(type).add(element);
        }
        if (properties.get(4096L)) {
            this.cornersConcave(type).add(element);
        }
        if (properties.get(2048L)) {
            this.cornersConvex(type).add(element);
        }
        if (properties.get(16L)) {
            this.major(type).add(element);
        }
        if (properties.get(32L)) {
            this.minor(type).add(element);
        }
        if (properties.get(8L)) {
            this.centre(type).add(element);
        }
        if (properties.get(33554432L)) {
            this.left(type).add(element);
        }
        if (properties.get(134217728L)) {
            this.top(type).add(element);
        }
        if (properties.get(67108864L)) {
            this.right(type).add(element);
        }
        if (properties.get(268435456L)) {
            this.bottom(type).add(element);
        }
        if (properties.get(1073741824L)) {
            this.axial(type).add(element);
        }
        if (properties.get(17179869184L)) {
            this.slash(type).add(element);
        }
        if (properties.get(34359738368L)) {
            this.slosh(type).add(element);
        }
        if (properties.get(4294967296L)) {
            this.vertical(type).add(element);
        }
        if (properties.get(2147483648L)) {
            this.horizontal(type).add(element);
        }
        if (properties.get(8589934592L)) {
            this.angled(type).add(element);
        }
        if (properties.get(1048576L)) {
            this.phases(type).get(0).add(element);
            element.setPhase(0);
        }
        if (properties.get(2097152L)) {
            this.phases(type).get(1).add(element);
            element.setPhase(1);
        }
        if (properties.get(4194304L)) {
            this.phases(type).get(2).add(element);
            element.setPhase(2);
        }
        if (properties.get(8388608L)) {
            this.phases(type).get(3).add(element);
            element.setPhase(3);
        }
        if (properties.get(2199023255552L)) {
            this.sides.get(type).get(CompassDirection.E).add(element);
        }
        if (properties.get(8796093022208L)) {
            this.sides.get(type).get(CompassDirection.W).add(element);
        }
        if (properties.get(1099511627776L)) {
            this.sides.get(type).get(CompassDirection.N).add(element);
        }
        if (properties.get(4398046511104L)) {
            this.sides.get(type).get(CompassDirection.S).add(element);
        }
        if (properties.get(17592186044416L)) {
            this.sides.get(type).get(CompassDirection.NE).add(element);
        }
        if (properties.get(140737488355328L)) {
            this.sides.get(type).get(CompassDirection.NW).add(element);
        }
        if (properties.get(70368744177664L)) {
            this.sides.get(type).get(CompassDirection.SW).add(element);
        }
        if (properties.get(35184372088832L)) {
            this.sides.get(type).get(CompassDirection.SE).add(element);
        }
    }
    
    public void computeRelation(final SiteType type) {
        if (this.trajectories == null) {
            return;
        }
        final List<? extends TopologyElement> elements = this.getGraphElements(type);
        for (final TopologyElement element : elements) {
            final List<game.util.graph.Step> stepsNeighbours = trajectories.steps(type, element.index(), type, AbsoluteDirection.All);
            for (final Step step : stepsNeighbours) {
                GraphUtilities.addNeighbour(type, element, elements.get(step.to().id()));
            }
            final List<Step> stepsAdjacent = this.trajectories.steps(type, element.index(), type, AbsoluteDirection.Adjacent);
            for (final Step step2 : stepsAdjacent) {
                GraphUtilities.addAdjacent(type, element, elements.get(step2.to().id()));
            }
            final List<Step> stepsOrthogonal = this.trajectories.steps(type, element.index(), type, AbsoluteDirection.Orthogonal);
            for (final Step step3 : stepsOrthogonal) {
                GraphUtilities.addOrthogonal(type, element, elements.get(step3.to().id()));
            }
            final List<Step> stepsDiagonal = this.trajectories.steps(type, element.index(), type, AbsoluteDirection.Diagonal);
            for (final Step step4 : stepsDiagonal) {
                GraphUtilities.addDiagonal(type, element, elements.get(step4.to().id()));
            }
            final List<Step> stepsOff = this.trajectories.steps(type, element.index(), type, AbsoluteDirection.OffDiagonal);
            for (final Step step5 : stepsOff) {
                GraphUtilities.addOff(type, element, elements.get(step5.to().id()));
            }
        }
    }
    
    public void computeRows(final SiteType type, final boolean threeDimensions) {
        this.rows(type).clear();
        if (this.graph() == null || this.graph().duplicateCoordinates(type) || threeDimensions) {
            final TDoubleArrayList rowCentroids = new TDoubleArrayList();
            for (final TopologyElement element : this.getGraphElements(type)) {
                if (element.centroid3D().z() != 0.0) {
                    continue;
                }
                final double yElement = element.centroid3D().y();
                boolean found = false;
                for (int i = 0; i < rowCentroids.size(); ++i) {
                    if (Math.abs(yElement - rowCentroids.get(i)) < 0.001) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    continue;
                }
                rowCentroids.add(yElement);
            }
            rowCentroids.sort();
            for (int j = 0; j < rowCentroids.size(); ++j) {
                this.rows(type).add(new ArrayList<>());
                for (final TopologyElement element2 : this.getGraphElements(type)) {
                    if (Math.abs(element2.centroid3D().y() - rowCentroids.get(j)) < 0.001) {
                        this.rows(type).get(j).add(element2);
                        element2.setRow(j);
                    }
                }
            }
        }
        else {
            for (final TopologyElement element3 : this.getGraphElements(type)) {
                final int rowId = element3.row();
                if (this.rows(type).size() > rowId) {
                    this.rows(type).get(rowId).add(element3);
                }
                else {
                    while (this.rows(type).size() <= rowId) {
                        this.rows(type).add(new ArrayList<>());
                    }
                    this.rows(type).get(rowId).add(element3);
                }
            }
        }
    }
    
    public void computeColumns(final SiteType type, final boolean threeDimensions) {
        this.columns(type).clear();
        if (this.graph() == null || this.graph().duplicateCoordinates(type) || threeDimensions) {
            final TDoubleArrayList colCentroids = new TDoubleArrayList();
            for (final TopologyElement element : this.getGraphElements(type)) {
                if (element.centroid3D().z() != 0.0) {
                    continue;
                }
                final double xElement = element.centroid3D().x();
                boolean found = false;
                for (int i = 0; i < colCentroids.size(); ++i) {
                    if (Math.abs(xElement - colCentroids.get(i)) < 0.001) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    continue;
                }
                colCentroids.add(xElement);
            }
            colCentroids.sort();
            for (int j = 0; j < colCentroids.size(); ++j) {
                this.columns(type).add(new ArrayList<>());
                for (final TopologyElement element2 : this.getGraphElements(type)) {
                    if (Math.abs(element2.centroid3D().x() - colCentroids.get(j)) < 0.001) {
                        this.columns(type).get(j).add(element2);
                        element2.setColumn(j);
                    }
                }
            }
        }
        else {
            for (final TopologyElement element3 : this.getGraphElements(type)) {
                final int columnId = element3.col();
                if (this.columns(type).size() > columnId) {
                    this.columns(type).get(columnId).add(element3);
                }
                else {
                    while (this.columns(type).size() <= columnId) {
                        this.columns(type).add(new ArrayList<>());
                    }
                    this.columns(type).get(columnId).add(element3);
                }
            }
        }
    }
    
    public void computeLayers(final SiteType type) {
        this.layers(type).clear();
        if (this.graph() == null || this.graph().duplicateCoordinates(type)) {
            final TDoubleArrayList layerCentroids = new TDoubleArrayList();
            for (final TopologyElement element : this.getGraphElements(type)) {
                if (!layerCentroids.contains(element.centroid3D().z())) {
                    layerCentroids.add(element.centroid3D().z());
                }
            }
            layerCentroids.sort();
            for (int i = 0; i < layerCentroids.size(); ++i) {
                this.layers(type).add(new ArrayList<>());
                for (final TopologyElement element2 : this.getGraphElements(type)) {
                    if (element2.centroid3D().z() == layerCentroids.get(i)) {
                        this.layers(type).get(i).add(element2);
                        element2.setLayer(i);
                    }
                }
            }
        }
        else {
            for (final TopologyElement element3 : this.getGraphElements(type)) {
                final int layerId = element3.layer();
                if (this.layers(type).size() > layerId) {
                    this.layers(type).get(layerId).add(element3);
                }
                else {
                    while (this.layers(type).size() <= layerId) {
                        this.layers(type).add(new ArrayList<>());
                    }
                    this.layers(type).get(layerId).add(element3);
                }
            }
        }
    }
    
    public void computeCoordinates(final SiteType type) {
        if (this.graph() == null || this.graph().duplicateCoordinates(type)) {
            for (final TopologyElement element : this.getGraphElements(type)) {
                String columnString = "";
                if (element.col() >= 26) {
                    columnString = String.valueOf((char)(65 + element.col() / 26 - 1));
                }
                columnString += String.valueOf((char)(65 + element.col() % 26));
                final String rowString = String.valueOf(element.row() + 1);
                final String label = columnString + rowString;
                element.setLabel(label);
            }
        }
    }
    
    public Trajectories trajectories() {
        return this.trajectories;
    }
    
    public void setTrajectories(final Trajectories trajectories) {
        this.trajectories = trajectories;
    }
    
    public void computeSupportedDirection(final SiteType type) {
        if (this.trajectories == null) {
            return;
        }
        final List<? extends TopologyElement> elements = this.getGraphElements(type);
        final List<DirectionFacing> supportedDirection = this.supportedDirections.get(type);
        final List<DirectionFacing> supportedOrthogonalDirection = this.supportedOrthogonalDirections.get(type);
        final List<DirectionFacing> supportedDiagonalDirection = this.supportedDiagonalDirections.get(type);
        final List<DirectionFacing> supportedAdjacentDirection = this.supportedAdjacentDirections.get(type);
        final List<DirectionFacing> supportedOffDirection = this.supportedOffDirections.get(type);
        supportedDirection.clear();
        supportedOrthogonalDirection.clear();
        supportedDiagonalDirection.clear();
        supportedAdjacentDirection.clear();
        supportedOffDirection.clear();
        for (final TopologyElement element : elements) {
            final List<Step> steps = this.trajectories.steps(type, element.index(), type, AbsoluteDirection.All);
            final List<Step> stepsOrtho = this.trajectories.steps(type, element.index(), type, AbsoluteDirection.Orthogonal);
            final List<Step> stepsDiago = this.trajectories.steps(type, element.index(), type, AbsoluteDirection.Diagonal);
            final List<Step> stepsAdjacent = this.trajectories.steps(type, element.index(), type, AbsoluteDirection.Adjacent);
            final List<Step> stepsOff = this.trajectories.steps(type, element.index(), type, AbsoluteDirection.OffDiagonal);
            for (final Step step : steps) {
                for (int a = step.directions().nextSetBit(0); a >= 0; a = step.directions().nextSetBit(a + 1)) {
                    final AbsoluteDirection abs = AbsoluteDirection.values()[a];
                    final DirectionFacing direction = AbsoluteDirection.convert(abs);
                    if (direction != null) {
                        if (!supportedDirection.contains(direction)) {
                            supportedDirection.add(direction);
                        }
                        if (!element.supportedDirections().contains(direction)) {
                            element.supportedDirections().add(direction);
                        }
                    }
                }
            }
            for (final Step step : stepsOrtho) {
                for (int a = step.directions().nextSetBit(0); a >= 0; a = step.directions().nextSetBit(a + 1)) {
                    final AbsoluteDirection abs = AbsoluteDirection.values()[a];
                    final DirectionFacing direction = AbsoluteDirection.convert(abs);
                    if (direction != null) {
                        if (!supportedOrthogonalDirection.contains(direction)) {
                            supportedOrthogonalDirection.add(direction);
                        }
                        if (!element.supportedOrthogonalDirections().contains(direction)) {
                            element.supportedOrthogonalDirections().add(direction);
                        }
                    }
                }
            }
            for (final Step step : stepsDiago) {
                for (int a = step.directions().nextSetBit(0); a >= 0; a = step.directions().nextSetBit(a + 1)) {
                    final AbsoluteDirection abs = AbsoluteDirection.values()[a];
                    final DirectionFacing direction = AbsoluteDirection.convert(abs);
                    if (direction != null) {
                        if (!supportedDiagonalDirection.contains(direction)) {
                            supportedDiagonalDirection.add(direction);
                        }
                        if (!element.supportedDiagonalDirections().contains(direction)) {
                            element.supportedDiagonalDirections().add(direction);
                        }
                    }
                }
            }
            for (final Step step : stepsAdjacent) {
                for (int a = step.directions().nextSetBit(0); a >= 0; a = step.directions().nextSetBit(a + 1)) {
                    final AbsoluteDirection abs = AbsoluteDirection.values()[a];
                    final DirectionFacing direction = AbsoluteDirection.convert(abs);
                    if (direction != null) {
                        if (!supportedAdjacentDirection.contains(direction)) {
                            supportedAdjacentDirection.add(direction);
                        }
                        if (!element.supportedAdjacentDirections().contains(direction)) {
                            element.supportedAdjacentDirections().add(direction);
                        }
                    }
                }
            }
            for (final Step step : stepsOff) {
                for (int a = step.directions().nextSetBit(0); a >= 0; a = step.directions().nextSetBit(a + 1)) {
                    final AbsoluteDirection abs = AbsoluteDirection.values()[a];
                    final DirectionFacing direction = AbsoluteDirection.convert(abs);
                    if (direction != null) {
                        if (!supportedOffDirection.contains(direction)) {
                            supportedOffDirection.add(direction);
                        }
                        if (!element.supportedOffDirections().contains(direction)) {
                            element.supportedOffDirections().add(direction);
                        }
                    }
                }
            }
        }
        final Comparator<DirectionFacing> dirComparator = (d1, d2) -> d1.index() - d2.index();
        Collections.sort(supportedDirection, dirComparator);
        Collections.sort(supportedOrthogonalDirection, dirComparator);
        Collections.sort(supportedDiagonalDirection, dirComparator);
        Collections.sort(supportedAdjacentDirection, dirComparator);
        Collections.sort(supportedOffDirection, dirComparator);
    }
    
    public void computeDoesCross() {
        for (final Edge edge : this.edges()) {
            edge.setDoesCross(new BitSet(this.edges.size()));
        }
        for (int i = 0; i < this.edges.size(); ++i) {
            final Edge edge = this.edges.get(i);
            final double tolerance = 0.001;
            final Point2D ptA = edge.vA().centroid();
            final Point2D ptB = edge.vB().centroid();
            for (int j = i + 1; j < this.edges.size(); ++j) {
                final Edge otherEdge = this.edges.get(j);
                final Point2D ptEA = otherEdge.vA().centroid();
                final Point2D ptEB = otherEdge.vB().centroid();
                if (ptA.distance(ptEA) >= 0.001 && ptA.distance(ptEB) >= 0.001 && ptB.distance(ptEA) >= 0.001) {
                    if (ptB.distance(ptEB) >= 0.001) {
                        if (MathRoutines.lineSegmentsIntersect(ptA.getX(), ptA.getY(), ptB.getX(), ptB.getY(), ptEA.getX(), ptEA.getY(), ptEB.getX(), ptEB.getY())) {
                            otherEdge.setDoesCross(edge.index());
                            edge.setDoesCross(otherEdge.index());
                        }
                    }
                }
            }
        }
    }
    
    public boolean isRegular() {
        return this.graph.isRegular();
    }
    
    public void computeNumEdgeIfRegular() {
        if (this.isRegular() && this.cells.size() > 0) {
            this.numEdges = this.cells.get(0).edges().size();
        }
    }
}
