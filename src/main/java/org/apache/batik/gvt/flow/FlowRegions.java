// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.flow;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Arrays;
import org.apache.batik.ext.awt.geom.Segment;
import java.util.List;
import org.apache.batik.ext.awt.geom.SegmentList;
import java.awt.Shape;

public class FlowRegions
{
    Shape flowShape;
    SegmentList sl;
    SegmentList.SplitResults sr;
    List validRanges;
    int currentRange;
    double currentY;
    double lineHeight;
    
    public FlowRegions(final Shape s) {
        this(s, s.getBounds2D().getY());
    }
    
    public FlowRegions(final Shape s, final double startY) {
        this.flowShape = s;
        this.sl = new SegmentList(s);
        this.currentY = startY - 1.0;
        this.gotoY(startY);
    }
    
    public double getCurrentY() {
        return this.currentY;
    }
    
    public double getLineHeight() {
        return this.lineHeight;
    }
    
    public boolean gotoY(final double y) {
        if (y < this.currentY) {
            throw new IllegalArgumentException("New Y can not be lower than old Y\nOld Y: " + this.currentY + " New Y: " + y);
        }
        if (y == this.currentY) {
            return false;
        }
        this.sr = this.sl.split(y);
        this.sl = this.sr.getBelow();
        this.sr = null;
        this.currentY = y;
        if (this.sl == null) {
            return true;
        }
        this.newLineHeight(this.lineHeight);
        return false;
    }
    
    public void newLineHeight(final double lineHeight) {
        this.lineHeight = lineHeight;
        this.sr = this.sl.split(this.currentY + lineHeight);
        if (this.sr.getAbove() != null) {
            this.sortRow(this.sr.getAbove());
        }
        this.currentRange = 0;
    }
    
    public int getNumRangeOnLine() {
        if (this.validRanges == null) {
            return 0;
        }
        return this.validRanges.size();
    }
    
    public void resetRange() {
        this.currentRange = 0;
    }
    
    public double[] nextRange() {
        if (this.currentRange >= this.validRanges.size()) {
            return null;
        }
        return this.validRanges.get(this.currentRange++);
    }
    
    public void endLine() {
        this.sl = this.sr.getBelow();
        this.sr = null;
        this.currentY += this.lineHeight;
    }
    
    public boolean newLine() {
        return this.newLine(this.lineHeight);
    }
    
    public boolean newLine(final double lineHeight) {
        if (this.sr != null) {
            this.sl = this.sr.getBelow();
        }
        this.sr = null;
        if (this.sl == null) {
            return false;
        }
        this.currentY += this.lineHeight;
        this.newLineHeight(lineHeight);
        return true;
    }
    
    public boolean newLineAt(final double y, final double lineHeight) {
        if (this.sr != null) {
            this.sl = this.sr.getBelow();
        }
        this.sr = null;
        if (this.sl == null) {
            return false;
        }
        this.currentY = y;
        this.newLineHeight(lineHeight);
        return true;
    }
    
    public boolean done() {
        return this.sl == null;
    }
    
    public void sortRow(final SegmentList sl) {
        final Transition[] segs = new Transition[sl.size() * 2];
        final Iterator iter = sl.iterator();
        int i = 0;
        while (iter.hasNext()) {
            final Segment seg = iter.next();
            segs[i++] = new Transition(seg.minX(), true);
            segs[i++] = new Transition(seg.maxX(), false);
        }
        Arrays.sort(segs, TransitionComp.COMP);
        this.validRanges = new ArrayList();
        int count = 1;
        double openStart = 0.0;
        for (i = 1; i < segs.length; ++i) {
            final Transition t = segs[i];
            if (t.up) {
                if (count == 0) {
                    final double cx = (openStart + t.loc) / 2.0;
                    final double cy = this.currentY + this.lineHeight / 2.0;
                    if (this.flowShape.contains(cx, cy)) {
                        this.validRanges.add(new double[] { openStart, t.loc });
                    }
                }
                ++count;
            }
            else if (--count == 0) {
                openStart = t.loc;
            }
        }
    }
    
    static class Transition
    {
        public double loc;
        public boolean up;
        
        public Transition(final double loc, final boolean up) {
            this.loc = loc;
            this.up = up;
        }
    }
    
    static class TransitionComp implements Comparator
    {
        public static Comparator COMP;
        
        @Override
        public int compare(final Object o1, final Object o2) {
            final Transition t1 = (Transition)o1;
            final Transition t2 = (Transition)o2;
            if (t1.loc < t2.loc) {
                return -1;
            }
            if (t1.loc > t2.loc) {
                return 1;
            }
            if (t1.up) {
                if (t2.up) {
                    return 0;
                }
                return -1;
            }
            else {
                if (t2.up) {
                    return 1;
                }
                return 0;
            }
        }
        
        @Override
        public boolean equals(final Object comp) {
            return this == comp;
        }
        
        static {
            TransitionComp.COMP = new TransitionComp();
        }
    }
}
