// 
// Decompiled by Procyon v0.5.36
// 

package game.util.graph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class Perimeter
{
    private final List<GraphElement> elements;
    private final List<Point2D.Double> positions;
    private final List<GraphElement> inside;
    final BitSet on;
    final BitSet in;
    
    public Perimeter() {
        this.elements = new ArrayList<>();
        this.positions = new ArrayList<>();
        this.inside = new ArrayList<>();
        this.on = new BitSet();
        this.in = new BitSet();
    }
    
    public List<GraphElement> elements() {
        return Collections.unmodifiableList(this.elements);
    }
    
    public List<Point2D.Double> positions() {
        return Collections.unmodifiableList(this.positions);
    }
    
    public List<GraphElement> inside() {
        return Collections.unmodifiableList(this.inside);
    }
    
    public BitSet on() {
        return this.on;
    }
    
    public BitSet in() {
        return this.in;
    }
    
    public Point2D startPoint() {
        if (this.positions.isEmpty()) {
            return null;
        }
        return this.positions.get(0);
    }
    
    public void clear() {
        this.elements.clear();
        this.positions.clear();
        this.inside.clear();
        this.on.clear();
        this.in.clear();
    }
    
    public void add(final Vertex vertex) {
        this.elements.add(vertex);
        this.positions.add(vertex.pt2D());
        this.on.set(vertex.id(), true);
    }
    
    public void addInside(final Vertex vertex) {
        this.inside.add(vertex);
        this.in.set(vertex.id(), true);
    }
}
