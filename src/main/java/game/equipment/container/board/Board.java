// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.container.board;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import annotations.Or2;
import game.equipment.container.Container;
import game.functions.graph.GraphFunction;
import game.functions.range.Range;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.equipment.Values;
import game.util.graph.Edge;
import game.util.graph.Face;
import game.util.graph.Graph;
import game.util.graph.Vertex;
import main.math.Vector;
import metadata.graphics.util.ContainerStyleType;
import topology.Cell;
import topology.Topology;

public class Board extends Container
{
    private static final long serialVersionUID = 1L;
    protected Graph graph;
    private final GraphFunction graphFunction;
    private Range edgeRange;
    private Range cellRange;
    private Range vertexRange;
    
    public Board(final GraphFunction graphFn, @Opt @Or final Track track, @Opt @Or final Track[] tracks, @Opt @Or2 final Values values, @Opt @Or2 final Values[] valuesArray, @Opt @Name final SiteType use) {
        super("Board", -1, RoleType.Neutral);
        this.graph = null;
        int numNonNull = 0;
        if (track != null) {
            ++numNonNull;
        }
        if (tracks != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Board: Only one of `track' or `tracks' can be non-null.");
        }
        int valuesNonNull = 0;
        if (values != null) {
            ++valuesNonNull;
        }
        if (valuesArray != null) {
            ++valuesNonNull;
        }
        if (valuesNonNull > 1) {
            throw new IllegalArgumentException("Board(): Only one of `values' or `valuesArray' parameter can be non-null.");
        }
        this.defaultSite = ((use == null) ? SiteType.Cell : use);
        this.graphFunction = graphFn;
        if (valuesNonNull == 1) {
            final Values[] array;
            final Values[] valuesLocal = array = ((valuesArray != null) ? valuesArray : new Values[] { values });
            for (final Values valuesGraphElement : array) {
                switch (valuesGraphElement.type()) {
                    case Cell: {
                        this.cellRange = valuesGraphElement.range();
                        break;
                    }
                    case Edge: {
                        this.edgeRange = valuesGraphElement.range();
                        break;
                    }
                    case Vertex: {
                        this.vertexRange = valuesGraphElement.range();
                        break;
                    }
                }
            }
            if (this.vertexRange == null) {
                this.vertexRange = new Range(0, 0);
            }
            if (this.edgeRange == null) {
                this.edgeRange = new Range(0, 0);
            }
            if (this.cellRange == null) {
                this.cellRange = new Range(0, 0);
            }
            this.style = ContainerStyleType.Puzzle;
        }
        else {
            if (tracks != null) {
                for (final Track t : tracks) {
                    this.tracks.add(t);
                }
            }
            else if (track != null) {
                this.tracks.add(track);
            }
            if (this.defaultSite == SiteType.Vertex || this.defaultSite == SiteType.Edge) {
                this.style = ContainerStyleType.Graph;
            }
            else {
                this.style = ContainerStyleType.Board;
            }
        }
    }
    
    public Graph graph() {
        return this.graph;
    }
    
    public void init(final SiteType siteType, final boolean boardless) {
        (this.graph = this.graphFunction.eval(null, siteType)).measure(boardless);
        this.graph.trajectories().create(this.graph);
        this.graph.setDim(this.graphFunction.dim());
        this.topology.setGraph(this.graph);
    }
    
    @Override
    public void createTopology(final int beginIndex, final int numEdges) {
        this.init(this.defaultSite, this.isBoardless());
        this.topology.setPerimeter(this.graph.perimeters());
        this.topology.setTrajectories(this.graph.trajectories());
        for (int i = 0; i < this.graph.vertices().size(); ++i) {
            final Vertex graphVertex = this.graph.vertices().get(i);
            final double x = graphVertex.pt().x();
            final double y = graphVertex.pt().y();
            final double z = graphVertex.pt().z();
            final topology.Vertex vertex = new topology.Vertex(i, x, y, z);
            vertex.setProperties(graphVertex.properties());
            vertex.setRow(graphVertex.situation().rcl().row());
            vertex.setColumn(graphVertex.situation().rcl().column());
            vertex.setLayer(graphVertex.situation().rcl().layer());
            vertex.setLabel(graphVertex.situation().label());
            this.topology.vertices().add(vertex);
        }
        for (int i = 0; i < this.graph.vertices().size(); ++i) {
            final Vertex vertex2 = this.graph.vertices().get(i);
            if (vertex2.pivot() != null) {
                this.topology.vertices().get(i).setPivot(this.topology.vertices().get(vertex2.pivot().id()));
            }
        }
        for (int i = 0; i < this.graph.edges().size(); ++i) {
            final Edge graphEdge = this.graph.edges().get(i);
            final topology.Vertex vA = this.topology.vertices().get(graphEdge.vertexA().id());
            final topology.Vertex vB = this.topology.vertices().get(graphEdge.vertexB().id());
            final topology.Edge edge = new topology.Edge(i, vA, vB);
            edge.setProperties(graphEdge.properties());
            edge.setRow(graphEdge.situation().rcl().row());
            edge.setColumn(graphEdge.situation().rcl().column());
            edge.setLayer(graphEdge.situation().rcl().layer());
            edge.setLabel(graphEdge.situation().label());
            if (graphEdge.tangentA() != null) {
                edge.setTangentA(new Vector(graphEdge.tangentA()));
            }
            if (graphEdge.tangentB() != null) {
                edge.setTangentB(new Vector(graphEdge.tangentB()));
            }
            this.topology.edges().add(edge);
            vA.edges().add(edge);
            vB.edges().add(edge);
        }
        for (final Face face : this.graph.faces()) {
            final Cell cell = new Cell(face.id(), face.pt().x(), face.pt().y(), face.pt().z());
            cell.setProperties(face.properties());
            cell.setRow(face.situation().rcl().row());
            cell.setColumn(face.situation().rcl().column());
            cell.setLayer(face.situation().rcl().layer());
            cell.setLabel(face.situation().label());
            this.topology.cells().add(cell);
            for (final Vertex v : face.vertices()) {
                final topology.Vertex vertex3 = this.topology().vertices().get(v.id());
                cell.vertices().add(vertex3);
                vertex3.cells().add(cell);
            }
            for (final Edge e : face.edges()) {
                final topology.Edge edge2 = this.topology().edges().get(e.id());
                cell.edges().add(edge2);
                edge2.cells().add(cell);
            }
        }
        this.numSites = this.topology.cells().size();
        this.topology.computeNumEdgeIfRegular();
    }
    
    public void setTopology(final Topology topo) {
        this.topology = topo;
    }
    
    public Range vertexRange() {
        return this.vertexRange;
    }
    
    public Range edgeRange() {
        return this.edgeRange;
    }
    
    public Range cellRange() {
        return this.cellRange;
    }
    
    public Range getRange(final SiteType type) {
        switch (type) {
            case Vertex: {
                return this.vertexRange();
            }
            case Cell: {
                return this.cellRange();
            }
            case Edge: {
                return this.edgeRange();
            }
            default: {
                return null;
            }
        }
    }
}
