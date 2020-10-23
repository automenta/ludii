// 
// Decompiled by Procyon v0.5.36
// 

package topology;

import game.types.board.RelationType;
import game.types.board.SiteType;
import game.util.directions.DirectionFacing;
import game.util.graph.Properties;
import main.math.Point3D;
import main.math.RCL;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public abstract class TopologyElement
{
    protected int index;
    protected Point3D centroid;
    protected RCL coord;
    protected String label;
    private int cost;
    private int phase;
    private final List<DirectionFacing> supportedDirections;
    private final List<DirectionFacing> supportedOrthogonalDirections;
    private final List<DirectionFacing> supportedDiagonalDirections;
    private final List<DirectionFacing> supportedAdjacentDirections;
    private final List<DirectionFacing> supportedOffDirections;
    protected Properties properties;
    private final List<List<TopologyElement>> sitesAtDistance;
    protected TopologyElement[] sortedOrthos;
    
    public TopologyElement() {
        this.coord = new RCL();
        this.label = "?";
        this.supportedDirections = new ArrayList<>();
        this.supportedOrthogonalDirections = new ArrayList<>();
        this.supportedDiagonalDirections = new ArrayList<>();
        this.supportedAdjacentDirections = new ArrayList<>();
        this.supportedOffDirections = new ArrayList<>();
        this.properties = new Properties();
        this.sitesAtDistance = new ArrayList<>();
        this.sortedOrthos = null;
    }
    
    public abstract SiteType elementType();
    
    public Point2D centroid() {
        return new Point2D.Double(this.centroid.x(), this.centroid.y());
    }
    
    public Point3D centroid3D() {
        return this.centroid;
    }
    
    public void setCentroid(final double centroidX, final double centroidY, final double centroidZ) {
        this.centroid = new Point3D(centroidX, centroidY, centroidZ);
    }
    
    public int index() {
        return this.index;
    }
    
    public int phase() {
        return this.phase;
    }
    
    public void setPhase(final int phase) {
        this.phase = phase;
    }
    
    public void setIndex(final int index) {
        this.index = index;
    }
    
    public String label() {
        return this.label;
    }
    
    public int row() {
        return this.coord.row();
    }
    
    public void setRow(final int r) {
        this.coord.setRow(r);
    }
    
    public void setColumn(final int c) {
        this.coord.setColumn(c);
    }
    
    public void setLayer(final int l) {
        this.coord.setLayer(l);
    }
    
    public int col() {
        return this.coord.column();
    }
    
    public int layer() {
        return this.coord.layer();
    }
    
    public void setLabel(final String label) {
        this.label = label;
    }
    
    public int cost() {
        return this.cost;
    }
    
    public void setCost(final int cost) {
        this.cost = cost;
    }
    
    public void setCoord(final int row, final int col, final int level) {
        this.coord.set(row, col, level);
    }
    
    public Properties properties() {
        return this.properties;
    }
    
    public void setSortedOrthos(final TopologyElement[] sortedOrthos) {
        this.sortedOrthos = sortedOrthos;
    }
    
    public TopologyElement[] sortedOrthos() {
        return this.sortedOrthos;
    }
    
    public void setProperties(final Properties properties) {
        this.properties = properties;
    }
    
    public abstract List<? extends TopologyElement> orthogonal();
    
    public abstract List<? extends TopologyElement> diagonal();
    
    public abstract List<? extends TopologyElement> off();
    
    public abstract List<? extends TopologyElement> neighbours();
    
    public abstract List<? extends TopologyElement> adjacent();
    
    public List<DirectionFacing> supportedDirections(final RelationType relationType) {
        switch (relationType) {
            case Adjacent: {
                return this.supportedAdjacentDirections;
            }
            case Diagonal: {
                return this.supportedDiagonalDirections;
            }
            case All: {
                return this.supportedDirections;
            }
            case OffDiagonal: {
                return this.supportedOffDirections;
            }
            case Orthogonal: {
                return this.supportedOrthogonalDirections;
            }
            default: {
                return this.supportedDirections;
            }
        }
    }
    
    public List<List<TopologyElement>> sitesAtDistance() {
        return this.sitesAtDistance;
    }
    
    public List<DirectionFacing> supportedDirections() {
        return this.supportedDirections;
    }
    
    public List<DirectionFacing> supportedOrthogonalDirections() {
        return this.supportedOrthogonalDirections;
    }
    
    public List<DirectionFacing> supportedOffDirections() {
        return this.supportedOffDirections;
    }
    
    public List<DirectionFacing> supportedDiagonalDirections() {
        return this.supportedDiagonalDirections;
    }
    
    public List<DirectionFacing> supportedAdjacentDirections() {
        return this.supportedAdjacentDirections;
    }
}
