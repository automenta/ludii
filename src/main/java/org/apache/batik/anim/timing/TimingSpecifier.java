// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

public abstract class TimingSpecifier
{
    protected TimedElement owner;
    protected boolean isBegin;
    
    protected TimingSpecifier(final TimedElement owner, final boolean isBegin) {
        this.owner = owner;
        this.isBegin = isBegin;
    }
    
    public TimedElement getOwner() {
        return this.owner;
    }
    
    public boolean isBegin() {
        return this.isBegin;
    }
    
    public void initialize() {
    }
    
    public void deinitialize() {
    }
    
    public abstract boolean isEventCondition();
    
    float newInterval(final Interval interval) {
        return Float.POSITIVE_INFINITY;
    }
    
    float removeInterval(final Interval interval) {
        return Float.POSITIVE_INFINITY;
    }
    
    float handleTimebaseUpdate(final InstanceTime instanceTime, final float newTime) {
        return Float.POSITIVE_INFINITY;
    }
}
