// 
// Decompiled by Procyon v0.5.36
// 

package metrics;

import game.Game;
import util.Trial;

public abstract class Metric
{
    private final String name;
    private final String notes;
    private final String credit;
    private final MetricType type;
    private final Range<Double, Double> range;
    
    public Metric(final String name, final String notes, final String credit, final MetricType type, final double min, final double max) {
        this.name = name;
        this.notes = notes;
        this.credit = credit;
        this.type = type;
        this.range = new Range<>(min, max);
    }
    
    public String name() {
        return this.name;
    }
    
    public String notes() {
        return this.notes;
    }
    
    public String credit() {
        return this.credit;
    }
    
    public MetricType type() {
        return this.type;
    }
    
    public double min() {
        return this.range.min().intValue();
    }
    
    public double max() {
        return this.range.max().intValue();
    }
    
    public abstract double apply(final Game p0, final String p1, final Trial... p2);
    
    public enum MetricType
    {
        OUTCOMES, 
        MOVES, 
        LUDEMES
    }
}
