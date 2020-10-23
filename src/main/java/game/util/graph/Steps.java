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

public class Steps
{
    private final SiteType siteType;
    private final int id;
    private final List<Step> steps;
    private List<Step>[] inDirection;
    private List<Step>[] toSiteType;
    private List<Step>[][] toSiteTypeInDirection;
    private final BitSet totalDirections;
    
    public Steps(final SiteType siteType, final int id) {
        this.steps = new ArrayList<>();
        this.totalDirections = new BitSet();
        this.siteType = siteType;
        this.id = id;
        this.allocate();
    }
    
    public List<Step> steps() {
        return Collections.unmodifiableList(this.steps);
    }
    
    public List<Step> toSiteType(final SiteType toType) {
        return Collections.unmodifiableList(this.toSiteType[toType.ordinal()]);
    }
    
    public List<Step> inDirection(final AbsoluteDirection dirn) {
        return Collections.unmodifiableList(this.inDirection[dirn.ordinal()]);
    }
    
    public List<Step> toSiteTypeInDirection(final SiteType toType, final AbsoluteDirection dirn) {
        return Collections.unmodifiableList(this.toSiteTypeInDirection[toType.ordinal()][dirn.ordinal()]);
    }
    
    public BitSet totalDirections() {
        return this.totalDirections;
    }
    
    public void clearInDirection(final AbsoluteDirection dirn) {
        this.inDirection[dirn.ordinal()].clear();
    }
    
    public void addInDirection(final AbsoluteDirection dirn, final Step step) {
        this.inDirection[dirn.ordinal()].add(step);
    }
    
    public void addToSiteTypeInDirection(final SiteType toType, final AbsoluteDirection dirn, final Step step) {
        final List<Step> stepsList = this.toSiteTypeInDirection[toType.ordinal()][dirn.ordinal()];
        for (final Step existing : stepsList) {
            if (step.matches(existing)) {
                return;
            }
        }
        stepsList.add(step);
    }
    
    public void allocate() {
        final int numSiteTypes = SiteType.values().length;
        final int numDirections = AbsoluteDirection.values().length;
        this.toSiteType = (List<Step>[])new ArrayList[numSiteTypes];
        for (int st = 0; st < numSiteTypes; ++st) {
            this.toSiteType[st] = new ArrayList<>();
        }
        this.inDirection = (List<Step>[])new ArrayList[numDirections];
        for (int dirn = 0; dirn < numDirections; ++dirn) {
            this.inDirection[dirn] = new ArrayList<>();
        }
        this.toSiteTypeInDirection = (List<Step>[][])new ArrayList[numSiteTypes][numDirections];
        for (int st = 0; st < numSiteTypes; ++st) {
            for (int dirn2 = 0; dirn2 < numDirections; ++dirn2) {
                this.toSiteTypeInDirection[st][dirn2] = new ArrayList<>();
            }
        }
    }
    
    void add(final Step step) {
        for (final Step existing : this.steps) {
            if (existing.from().matches(step.from()) && existing.to().matches(step.to())) {
                existing.directions().or(step.directions());
                return;
            }
        }
        steps.add(step);
        final int toSiteTypeId = step.to().siteType().ordinal();
        toSiteType[toSiteTypeId].add(step);
        for (int dirn = step.directions().nextSetBit(0); dirn >= 0; dirn = step.directions().nextSetBit(dirn + 1)) {
            inDirection[dirn].add(step);
            toSiteTypeInDirection[toSiteTypeId][dirn].add(step);
        }
        this.totalDirections.or(step.directions());
    }
    
    public void sort() {
        final int numSiteTypes = SiteType.values().length;
        final int numDirections = AbsoluteDirection.values().length;
        sort(this.steps);
        for (int dirn = 0; dirn < numDirections; ++dirn) {
            sort(this.inDirection[dirn]);
        }
        for (int st = 0; st < numSiteTypes; ++st) {
            sort(this.toSiteType[st]);
        }
        for (int st = 0; st < numSiteTypes; ++st) {
            for (int dirn2 = 0; dirn2 < numDirections; ++dirn2) {
                sort(this.toSiteTypeInDirection[st][dirn2]);
            }
        }
    }
    
    public static void sort(final List<Step> list) {
        final List<ItemScore> rank = new ArrayList<>();
        for (int n = 0; n < list.size(); ++n) {
            final Step step = list.get(n);
            final double theta = MathRoutines.angle(step.from().pt2D(), step.to().pt2D());
            double score;
            for (score = 1.5707963267948966 - theta + 1.0E-4; score < 0.0; score += 6.283185307179586) {}
            rank.add(new ItemScore(n, score));
        }
        Collections.sort(rank);
        for (ItemScore itemScore : rank) {
            list.add(list.get(itemScore.id()));
        }
        if (!rank.isEmpty()) {
            list.subList(0, rank.size()).clear();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Steps from " + this.siteType + " " + this.id + ":\n");
        for (final Step step : this.steps) {
            sb.append("- " + step.toString() + "\n");
        }
        return sb.toString();
    }
}
