// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

public class OffsetTimingSpecifier extends TimingSpecifier
{
    protected float offset;
    
    public OffsetTimingSpecifier(final TimedElement owner, final boolean isBegin, final float offset) {
        super(owner, isBegin);
        this.offset = offset;
    }
    
    @Override
    public String toString() {
        return ((this.offset >= 0.0f) ? "+" : "") + this.offset;
    }
    
    @Override
    public void initialize() {
        final InstanceTime instance = new InstanceTime(this, this.offset, false);
        this.owner.addInstanceTime(instance, this.isBegin);
    }
    
    @Override
    public boolean isEventCondition() {
        return false;
    }
}
