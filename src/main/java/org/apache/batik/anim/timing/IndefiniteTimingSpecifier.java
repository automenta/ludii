// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

public class IndefiniteTimingSpecifier extends TimingSpecifier
{
    public IndefiniteTimingSpecifier(final TimedElement owner, final boolean isBegin) {
        super(owner, isBegin);
    }
    
    @Override
    public String toString() {
        return "indefinite";
    }
    
    @Override
    public void initialize() {
        if (!this.isBegin) {
            final InstanceTime instance = new InstanceTime(this, Float.POSITIVE_INFINITY, false);
            this.owner.addInstanceTime(instance, this.isBegin);
        }
    }
    
    @Override
    public boolean isEventCondition() {
        return false;
    }
}
