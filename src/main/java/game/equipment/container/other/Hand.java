// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.container.other;

import annotations.Name;
import annotations.Opt;
import game.equipment.container.Container;
import game.functions.dim.DimConstant;
import game.functions.graph.generators.basis.hex.RectangleOnHex;
import game.functions.graph.generators.basis.square.RectangleOnSquare;
import game.functions.graph.generators.basis.tri.RectangleOnTri;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.graph.Face;
import game.util.graph.Graph;
import game.util.graph.Vertex;
import metadata.graphics.util.ContainerStyleType;
import topology.Cell;
import topology.Topology;
import util.ItemType;

public class Hand extends Container
{
    private static final long serialVersionUID = 1L;
    protected int numLocs;
    
    public Hand(final RoleType role, @Opt @Name final Integer size) {
        super(null, -1, role);
        final String className = this.getClass().toString();
        final String containerName = className.substring(className.lastIndexOf(46) + 1);
        if (role.owner() > 0 && role.owner() <= 16) {
            if (this.name() == null) {
                this.setName(containerName + role.owner());
            }
        }
        else if (role == RoleType.Neutral) {
            if (this.name() == null) {
                this.setName(containerName + role.owner());
            }
        }
        else if (role == RoleType.Shared && this.name() == null) {
            this.setName(containerName + role.owner());
        }
        this.numLocs = ((size == null) ? 1 : size);
        this.style = ContainerStyleType.Hand;
        this.setType(ItemType.Hand);
    }
    
    protected Hand(final Hand other) {
        super(other);
        this.numLocs = other.numLocs;
    }
    
    @Override
    public void createTopology(final int beginIndex, final int numEdge) {
        final double unit = 1.0 / this.numLocs;
        this.topology = new Topology();
        final int realNumEdge = (numEdge == -1) ? 4 : numEdge;
        Graph graph = null;
        if (realNumEdge == 6) {
            graph = new RectangleOnHex(new DimConstant(1), new DimConstant(this.numLocs)).eval(null, SiteType.Cell);
        }
        else if (realNumEdge == 3) {
            graph = new RectangleOnTri(new DimConstant(1), new DimConstant(this.numLocs)).eval(null, SiteType.Cell);
        }
        else {
            graph = new RectangleOnSquare(new DimConstant(1), new DimConstant(this.numLocs), null, null).eval(null, SiteType.Cell);
        }
        for (int i = 0; i < graph.faces().size(); ++i) {
            final Face face = graph.faces().get(i);
            final Cell cell = new Cell(face.id() + beginIndex, face.pt().x() + i * unit, face.pt().y(), face.pt().z());
            cell.setCoord(cell.row(), cell.col(), 0);
            cell.setCentroid(face.pt().x(), face.pt().y(), 0.0);
            this.topology.cells().add(cell);
            for (final Vertex v : face.vertices()) {
                final double x = v.pt().x();
                final double y = v.pt().y();
                final double z = v.pt().z();
                final topology.Vertex vertex = new topology.Vertex(-1, x, y, z);
                cell.vertices().add(vertex);
            }
        }
        this.numSites = this.topology.cells().size();
    }
    
    public int numLocs() {
        return this.numLocs;
    }
    
    @Override
    public Hand clone() {
        return new Hand(this);
    }
    
    @Override
    public boolean isHand() {
        return true;
    }
}
