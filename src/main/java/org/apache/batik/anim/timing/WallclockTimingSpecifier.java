// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

import java.util.Calendar;

public class WallclockTimingSpecifier extends TimingSpecifier
{
    protected Calendar time;
    protected InstanceTime instance;
    
    public WallclockTimingSpecifier(final TimedElement owner, final boolean isBegin, final Calendar time) {
        super(owner, isBegin);
        this.time = time;
    }
    
    @Override
    public String toString() {
        return "wallclock(" + this.time.toString() + ")";
    }
    
    @Override
    public void initialize() {
        final float t = this.owner.getRoot().convertWallclockTime(this.time);
        this.instance = new InstanceTime(this, t, false);
        this.owner.addInstanceTime(this.instance, this.isBegin);
    }
    
    @Override
    public boolean isEventCondition() {
        return false;
    }
}
