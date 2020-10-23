// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

import java.util.HashMap;

public class SyncbaseTimingSpecifier extends OffsetTimingSpecifier
{
    protected String syncbaseID;
    protected TimedElement syncbaseElement;
    protected boolean syncBegin;
    protected HashMap instances;
    
    public SyncbaseTimingSpecifier(final TimedElement owner, final boolean isBegin, final float offset, final String syncbaseID, final boolean syncBegin) {
        super(owner, isBegin, offset);
        this.instances = new HashMap();
        this.syncbaseID = syncbaseID;
        this.syncBegin = syncBegin;
        (this.syncbaseElement = owner.getTimedElementById(syncbaseID)).addDependent(this, syncBegin);
    }
    
    @Override
    public String toString() {
        return this.syncbaseID + "." + (this.syncBegin ? "begin" : "end") + ((this.offset != 0.0f) ? super.toString() : "");
    }
    
    @Override
    public void initialize() {
    }
    
    @Override
    public boolean isEventCondition() {
        return false;
    }
    
    @Override
    float newInterval(final Interval interval) {
        if (this.owner.hasPropagated) {
            return Float.POSITIVE_INFINITY;
        }
        final InstanceTime instance = new InstanceTime(this, (this.syncBegin ? interval.getBegin() : interval.getEnd()) + this.offset, true);
        this.instances.put(interval, instance);
        interval.addDependent(instance, this.syncBegin);
        return this.owner.addInstanceTime(instance, this.isBegin);
    }
    
    @Override
    float removeInterval(final Interval interval) {
        if (this.owner.hasPropagated) {
            return Float.POSITIVE_INFINITY;
        }
        final InstanceTime instance = this.instances.get(interval);
        interval.removeDependent(instance, this.syncBegin);
        return this.owner.removeInstanceTime(instance, this.isBegin);
    }
    
    @Override
    float handleTimebaseUpdate(final InstanceTime instanceTime, final float newTime) {
        if (this.owner.hasPropagated) {
            return Float.POSITIVE_INFINITY;
        }
        return this.owner.instanceTimeChanged(instanceTime, this.isBegin);
    }
}
