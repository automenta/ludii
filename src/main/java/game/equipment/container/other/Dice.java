// 
// Decompiled by Procyon v0.5.36
// 

package game.equipment.container.other;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
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

public final class Dice extends Container
{
    private static final long serialVersionUID = 1L;
    private final int numFaces;
    private final Integer start;
    private final Integer[][] faces;
    private final Integer[] biased;
    protected int numLocs;
    
    public Dice(@Opt @Name final Integer d, @Or @Opt @Name final Integer[] faces, @Or @Opt @Name final Integer[][] facesByDie, @Or @Opt @Name final Integer from, @Opt final RoleType role, @Name final Integer num, @Opt @Name final Integer[] biased) {
        super(null, -1, (role == null) ? RoleType.Shared : role);
        final String className = this.getClass().toString();
        final String containerName = className.substring(className.lastIndexOf(46) + 1);
        final RoleType realRole = (role == null) ? RoleType.Shared : role;
        if (realRole.owner() > 0 && realRole.owner() <= 16) {
            if (this.name() == null) {
                this.setName(containerName + realRole.owner());
            }
        }
        else if (realRole == RoleType.Neutral) {
            if (this.name() == null) {
                this.setName(containerName + realRole.owner());
            }
        }
        else if (realRole == RoleType.Shared && this.name() == null) {
            this.setName(containerName + realRole.owner());
        }
        this.numLocs = num;
        this.style = ContainerStyleType.Hand;
        this.numFaces = ((d != null) ? d : ((faces != null) ? faces.length : ((facesByDie != null) ? facesByDie[0].length : 6)));
        int numNonNull = 0;
        if (from != null) {
            ++numNonNull;
        }
        if (faces != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Zero or one Or parameter must be non-null.");
        }
        this.start = ((faces != null || facesByDie != null) ? null : ((from == null) ? 1 : from));
        if (facesByDie != null) {
            this.faces = facesByDie;
        }
        else if (faces != null) {
            final Integer[][] sameFacesByDie = new Integer[this.numLocs][faces.length];
            for (int i = 0; i < this.numLocs; ++i) {
                sameFacesByDie[i] = faces;
            }
            this.faces = sameFacesByDie;
        }
        else {
            final Integer[][] sequentialFaces = new Integer[this.numLocs][this.numFaces];
            for (int i = 0; i < this.numLocs; ++i) {
                for (int j = 0; j < this.numFaces; ++j) {
                    sequentialFaces[i][j] = this.start + j;
                }
            }
            this.faces = sequentialFaces;
        }
        this.biased = biased;
        this.setType(ItemType.Dice);
    }
    
    protected Dice(final Dice other) {
        super(other);
        this.numFaces = other.numFaces;
        this.start = other.start;
        if (other.biased != null) {
            this.biased = new Integer[other.biased.length];
            System.arraycopy(other.biased, 0, this.biased, 0, other.biased.length);
        }
        else {
            this.biased = null;
        }
        if (other.faces != null) {
            this.faces = new Integer[other.faces.length][];
            for (int i = 0; i < other.faces.length; ++i) {
                System.arraycopy(other.faces[i], 0, this.faces[i], 0, other.faces[i].length);
            }
        }
        else {
            this.faces = null;
        }
    }
    
    @Override
    public Dice clone() {
        return new Dice(this);
    }
    
    public Integer[] getBiased() {
        return this.biased;
    }
    
    public Integer[][] getFaces() {
        return this.faces;
    }
    
    public Integer getStart() {
        return this.start;
    }
    
    public int getNumFaces() {
        return this.numFaces;
    }
    
    @Override
    public boolean isDice() {
        return true;
    }
    
    @Override
    public boolean isHand() {
        return true;
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
}
