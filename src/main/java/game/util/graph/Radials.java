// 
// Decompiled by Procyon v0.5.36
// 

package game.util.graph;

import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import math.MathRoutines;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class Radials
{
    private final SiteType siteType;
    private final int siteId;
    private final List<Radial> radials;
    private List<Radial>[] inDirection;
    private List<Radial>[] distinctInDirection;
    private final BitSet totalDirections;
    
    public Radials(final SiteType siteType, final int id) {
        this.radials = new ArrayList<>();
        this.totalDirections = new BitSet();
        this.siteType = siteType;
        this.siteId = id;
        this.allocate();
    }
    
    public List<Radial> radials() {
        return Collections.unmodifiableList(this.radials);
    }
    
    public List<Radial> inDirection(final AbsoluteDirection dirn) {
        return Collections.unmodifiableList(this.inDirection[dirn.ordinal()]);
    }
    
    public List<Radial> distinctInDirection(final AbsoluteDirection dirn) {
        return Collections.unmodifiableList(this.distinctInDirection[dirn.ordinal()]);
    }
    
    public BitSet totalDirections() {
        return this.totalDirections;
    }
    
    public void allocate() {
        final int numDirections = AbsoluteDirection.values().length;
        this.inDirection = (List<Radial>[])new ArrayList[numDirections];
        this.distinctInDirection = (List<Radial>[])new ArrayList[numDirections];
        for (int dirn = 0; dirn < numDirections; ++dirn) {
            this.inDirection[dirn] = new ArrayList<>();
            this.distinctInDirection[dirn] = new ArrayList<>();
        }
    }
    
    public void addInDirection(final AbsoluteDirection dirn, final Radial radial) {
        this.inDirection[dirn.ordinal()].add(radial);
    }
    
    public void addDistinctInDirection(final AbsoluteDirection dirn, final Radial radial) {
        this.distinctInDirection[dirn.ordinal()].add(radial);
    }
    
    void addSafe(final Radial radial) {
        for (final Radial existing : this.radials) {
            if (existing.matches(radial)) {
                return;
            }
        }
        for (final Radial existing : this.radials) {
            if ((radial.direction() == AbsoluteDirection.CW && existing.direction() == AbsoluteDirection.CCW) || (radial.direction() == AbsoluteDirection.CCW && existing.direction() == AbsoluteDirection.CW) || (radial.direction() == AbsoluteDirection.In && existing.direction() == AbsoluteDirection.Out) || (radial.direction() == AbsoluteDirection.Out && existing.direction() == AbsoluteDirection.In) || (radial.isOppositeAngleTo(existing) && ((radial.direction().specific() && existing.direction().specific()) || radial.direction() == existing.direction()))) {
                radial.addOpposite(existing);
                existing.addOpposite(radial);
            }
        }
        boolean isDistinct = true;
        for (final Radial existing2 : this.inDirection[radial.direction().ordinal()]) {
            if (radial.stepsMatch(existing2) || radial.isOppositeAngleTo(existing2)) {
                isDistinct = false;
                break;
            }
            if (radial.opposites() == null) {
                continue;
            }
            for (final Radial existingOpposite : radial.opposites()) {
                if (radial.stepsMatch(existingOpposite)) {
                    isDistinct = false;
                    break;
                }
            }
        }
        this.radials.add(radial);
        this.inDirection[radial.direction().ordinal()].add(radial);
        this.totalDirections.set(radial.direction().ordinal());
        if (isDistinct) {
            this.distinctInDirection[radial.direction().ordinal()].add(radial);
        }
    }
    
    public void removeSubsetsInDirection(final AbsoluteDirection dirn) {
        final int dirnId = dirn.ordinal();
        for (int n = this.inDirection[dirnId].size() - 1; n >= 0; --n) {
            final Radial radial = this.inDirection[dirnId].get(n);
            for (int nn = 0; nn < this.inDirection[dirnId].size(); ++nn) {
                if (n != nn) {
                    if (radial.isSubsetOf(this.inDirection[dirnId].get(nn))) {
                        this.inDirection[dirnId].remove(n);
                        break;
                    }
                }
            }
        }
    }
    
    public void setDistinct() {
        for (int dirn = 0; dirn < AbsoluteDirection.values().length; ++dirn) {
            this.distinctInDirection[dirn].clear();
        }
        for (int dirn = 0; dirn < AbsoluteDirection.values().length; ++dirn) {
            for (final Radial radial : this.inDirection[dirn]) {
                boolean isDistinct = true;
                for (final Radial existing : this.distinctInDirection[dirn]) {
                    if (radial == existing) {
                        continue;
                    }
                    if (existing.opposites() == null) {
                        continue;
                    }
                    for (final Radial opp : existing.opposites()) {
                        if (radial.stepsMatch(opp)) {
                            isDistinct = false;
                            break;
                        }
                    }
                }
                if (isDistinct) {
                    this.distinctInDirection[dirn].add(radial);
                }
            }
        }
    }
    
    public void sort() {
        sort(this.radials);
        for (int dirn = 0; dirn < AbsoluteDirection.values().length; ++dirn) {
            sort(this.inDirection[dirn]);
            sort(this.distinctInDirection[dirn]);
        }
    }
    
    public static void sort(final List<Radial> list) {
        final List<ItemScore> rank = new ArrayList<>();
        for (int n = 0; n < list.size(); ++n) {
            final Radial radial = list.get(n);
            final double theta = MathRoutines.angle(radial.steps()[0].pt2D(), radial.steps()[1].pt2D());
            double score;
            for (score = 1.5707963267948966 - theta + 1.0E-4; score < 0.0; score += 6.283185307179586) {}
            rank.add(new ItemScore(n, score));
        }
        Collections.sort(rank);
        final Radial[] orig = list.toArray(new Radial[0]);
        for (int r = 0; r < rank.size(); ++r) {
            list.set(r, orig[rank.get(r).id()]);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Radials from " + this.siteType + " " + this.siteId + ":\n");
        for (final AbsoluteDirection dirn : AbsoluteDirection.values()) {
            for (final Radial radial : this.inDirection[dirn.ordinal()]) {
                sb.append("- " + dirn + ": " + radial.toString());
                boolean isDistinct = false;
                for (final Radial dist : this.distinctInDirection[dirn.ordinal()]) {
                    if (dist.matches(radial)) {
                        isDistinct = true;
                        break;
                    }
                }
                if (isDistinct) {
                    sb.append("*");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
