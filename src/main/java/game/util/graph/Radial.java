// 
// Decompiled by Procyon v0.5.36
// 

package game.util.graph;

import game.util.directions.AbsoluteDirection;
import main.math.MathRoutines;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Radial
{
    private final GraphElement[] steps;
    private final AbsoluteDirection direction;
    private List<Radial> opposites;
    
    public Radial(final GraphElement[] steps, final AbsoluteDirection direction) {
        this.opposites = null;
        this.steps = steps;
        this.direction = direction;
    }
    
    public GraphElement[] steps() {
        return this.steps;
    }
    
    public AbsoluteDirection direction() {
        return this.direction;
    }
    
    public List<Radial> opposites() {
        if (this.opposites == null) {
            return null;
        }
        return Collections.unmodifiableList(this.opposites);
    }
    
    public GraphElement from() {
        return this.steps[0];
    }
    
    public GraphElement lastStep() {
        return this.steps[this.steps.length - 1];
    }
    
    public boolean matches(final Radial other) {
        return this.direction == other.direction && this.stepsMatch(other);
    }
    
    public boolean stepsMatch(final Radial other) {
        if (this.steps.length != other.steps.length) {
            return false;
        }
        for (int n = 0; n < this.steps.length; ++n) {
            if (!this.steps[n].matches(other.steps[n])) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isOppositeAngleTo(final Radial other) {
        final double threshold = 0.1;
        final GraphElement geA = this.steps[1];
        final GraphElement geB = this.steps[0];
        final GraphElement geC = other.steps[1];
        final Point2D ptA = geA.pt2D();
        final Point2D ptB = geB.pt2D();
        final Point2D ptC = geC.pt2D();
        final double diff = MathRoutines.angleDifference(ptA, ptB, ptC);
        return Math.abs(diff) < 0.1;
    }
    
    public void addOpposite(final Radial opp) {
        if (this.opposites == null) {
            (this.opposites = new ArrayList<>()).add(opp);
        }
        else {
            for (final Radial existingOpposite : this.opposites) {
                if (this.direction.specific() || opp.direction() == this.direction || opp.stepsMatch(existingOpposite)) {
                    return;
                }
            }
            this.opposites.add(opp);
        }
    }
    
    public void removeOppositeSubsets() {
        if (this.opposites == null) {
            return;
        }
        for (int o = this.opposites.size() - 1; o >= 0; --o) {
            final Radial oppositeO = this.opposites.get(o);
            for (int n = 0; n < this.opposites.size(); ++n) {
                if (n != o) {
                    if (oppositeO.isSubsetOf(this.opposites.get(n))) {
                        this.opposites.remove(o);
                        break;
                    }
                }
            }
        }
    }
    
    public boolean isSubsetOf(final Radial other) {
        if (this.steps.length > other.steps.length) {
            return false;
        }
        for (int n = 0; n < this.steps.length; ++n) {
            if (!this.steps[n].matches(other.steps[n])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int n = 0; n < this.steps.length; ++n) {
            if (n > 0) {
                sb.append("-");
            }
            sb.append(this.steps[n].label());
        }
        if (this.opposites != null) {
            sb.append(" [");
            for (int o = 0; o < this.opposites.size(); ++o) {
                final Radial opp = this.opposites.get(o);
                if (o > 0) {
                    sb.append(", ");
                }
                for (int n2 = 0; n2 < opp.steps.length; ++n2) {
                    if (n2 > 0) {
                        sb.append("-");
                    }
                    sb.append(opp.steps[n2].label());
                }
            }
            sb.append("]");
        }
        return sb.toString();
    }
}
