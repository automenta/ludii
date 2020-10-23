// 
// Decompiled by Procyon v0.5.36
// 

package game.util.graph;

import game.types.board.BasisType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import main.math.Point3D;

import java.awt.geom.Point2D;
import java.util.List;

public abstract class GraphElement
{
    protected int id;
    protected Point3D pt;
    protected BasisType basis;
    protected ShapeType shape;
    protected Properties properties = new Properties();
    protected final Situation situation = new Situation();
    protected boolean flag;
    
    public GraphElement() {
        this.id = -1;
        this.basis = null;
        this.shape = null;
        this.flag = false;
    }
    
    public int id() {
        return this.id;
    }
    
    public void setId(final int newId) {
        this.id = newId;
    }
    
    public void decrementId() {
        --this.id;
    }
    
    public Point3D pt() {
        return this.pt;
    }
    
    public Point2D.Double pt2D() {
        return new Point2D.Double(this.pt.x(), this.pt.y());
    }
    
    public Properties properties() {
        return properties;
    }
    
    public Situation situation() {
        return situation;
    }
    
    public boolean flag() {
        return this.flag;
    }
    
    public void setFlag(final boolean value) {
        this.flag = value;
    }
    
    public abstract Vertex pivot();
    
    public BasisType basis() {
        return this.basis;
    }
    
    public void setBasis(final BasisType type) {
        this.basis = type;
    }
    
    public ShapeType shape() {
        return this.shape;
    }
    
    public void setShape(final ShapeType type) {
        this.shape = type;
    }
    
    public abstract SiteType siteType();
    
    public void setTilingAndShape(final BasisType basisIn, final ShapeType shapeIn) {
        this.basis = basisIn;
        this.shape = shapeIn;
    }
    
    public boolean matches(final GraphElement other) {
        return this.siteType() == other.siteType() && this.id == other.id;
    }
    
    public String label() {
        return this.siteType().toString().substring(0, 1) + this.id;
    }
    
    public abstract List<GraphElement> nbors();
    
    public abstract void stepsTo(final Steps steps);
}
