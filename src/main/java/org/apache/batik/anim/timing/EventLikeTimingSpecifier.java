// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

import org.w3c.dom.events.Event;

public abstract class EventLikeTimingSpecifier extends OffsetTimingSpecifier
{
    public EventLikeTimingSpecifier(final TimedElement owner, final boolean isBegin, final float offset) {
        super(owner, isBegin, offset);
    }
    
    @Override
    public boolean isEventCondition() {
        return true;
    }
    
    public abstract void resolve(final Event p0);
}
