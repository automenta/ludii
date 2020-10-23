// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

import java.util.Iterator;
import java.util.LinkedList;

public class Interval
{
    protected float begin;
    protected float end;
    protected InstanceTime beginInstanceTime;
    protected InstanceTime endInstanceTime;
    protected LinkedList beginDependents;
    protected LinkedList endDependents;
    
    public Interval(final float begin, final float end, final InstanceTime beginInstanceTime, final InstanceTime endInstanceTime) {
        this.beginDependents = new LinkedList();
        this.endDependents = new LinkedList();
        this.begin = begin;
        this.end = end;
        this.beginInstanceTime = beginInstanceTime;
        this.endInstanceTime = endInstanceTime;
    }
    
    @Override
    public String toString() {
        return TimedElement.toString(this.begin) + ".." + TimedElement.toString(this.end);
    }
    
    public float getBegin() {
        return this.begin;
    }
    
    public float getEnd() {
        return this.end;
    }
    
    public InstanceTime getBeginInstanceTime() {
        return this.beginInstanceTime;
    }
    
    public InstanceTime getEndInstanceTime() {
        return this.endInstanceTime;
    }
    
    void addDependent(final InstanceTime dependent, final boolean forBegin) {
        if (forBegin) {
            this.beginDependents.add(dependent);
        }
        else {
            this.endDependents.add(dependent);
        }
    }
    
    void removeDependent(final InstanceTime dependent, final boolean forBegin) {
        if (forBegin) {
            this.beginDependents.remove(dependent);
        }
        else {
            this.endDependents.remove(dependent);
        }
    }
    
    float setBegin(final float begin) {
        float minTime = Float.POSITIVE_INFINITY;
        this.begin = begin;
        for (final Object beginDependent : this.beginDependents) {
            final InstanceTime it = (InstanceTime)beginDependent;
            final float t = it.dependentUpdate(begin);
            if (t < minTime) {
                minTime = t;
            }
        }
        return minTime;
    }
    
    float setEnd(final float end, final InstanceTime endInstanceTime) {
        float minTime = Float.POSITIVE_INFINITY;
        this.end = end;
        this.endInstanceTime = endInstanceTime;
        for (final Object endDependent : this.endDependents) {
            final InstanceTime it = (InstanceTime)endDependent;
            final float t = it.dependentUpdate(end);
            if (t < minTime) {
                minTime = t;
            }
        }
        return minTime;
    }
}
