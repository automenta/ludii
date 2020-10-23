// 
// Decompiled by Procyon v0.5.36
// 

package game.util.graph;

import game.util.directions.AbsoluteDirection;

import java.util.BitSet;
import java.util.List;

public class Step
{
    protected final GraphElement from;
    protected final GraphElement to;
    protected final BitSet directions;
    
    public Step(final GraphElement from, final GraphElement to) {
        this.directions = new BitSet();
        this.from = from;
        this.to = to;
    }
    
    public GraphElement from() {
        return this.from;
    }
    
    public GraphElement to() {
        return this.to;
    }
    
    public BitSet directions() {
        return this.directions;
    }
    
    public boolean in(final List<Step> list) {
        for (final Step step : list) {
            if (this.from.matches(step.from()) && this.to.matches(step.to())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean matches(final Step other) {
        return this.from.matches(other.from) && this.to.matches(other.to) && this.directions.equals(other.directions);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.from.label() + " => " + this.to.label());
        if (!this.directions.isEmpty()) {
            sb.append(" (");
            for (int d = this.directions.nextSetBit(0); d >= 0; d = this.directions.nextSetBit(d + 1)) {
                if (d > 0) {
                    sb.append(", ");
                }
                sb.append(AbsoluteDirection.values()[d]);
            }
            sb.append(")");
        }
        return sb.toString();
    }
}
