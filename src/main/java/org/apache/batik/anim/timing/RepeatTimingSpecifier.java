// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

import org.w3c.dom.smil.TimeEvent;
import org.w3c.dom.events.Event;

public class RepeatTimingSpecifier extends EventbaseTimingSpecifier
{
    protected int repeatIteration;
    protected boolean repeatIterationSpecified;
    
    public RepeatTimingSpecifier(final TimedElement owner, final boolean isBegin, final float offset, final String syncbaseID) {
        super(owner, isBegin, offset, syncbaseID, owner.getRoot().getRepeatEventName());
    }
    
    public RepeatTimingSpecifier(final TimedElement owner, final boolean isBegin, final float offset, final String syncbaseID, final int repeatIteration) {
        super(owner, isBegin, offset, syncbaseID, owner.getRoot().getRepeatEventName());
        this.repeatIteration = repeatIteration;
        this.repeatIterationSpecified = true;
    }
    
    @Override
    public String toString() {
        return ((this.eventbaseID == null) ? "" : (this.eventbaseID + ".")) + "repeat" + (this.repeatIterationSpecified ? ("(" + this.repeatIteration + ")") : "") + ((this.offset != 0.0f) ? super.toString() : "");
    }
    
    @Override
    public void handleEvent(final Event e) {
        final TimeEvent evt = (TimeEvent)e;
        if (!this.repeatIterationSpecified || evt.getDetail() == this.repeatIteration) {
            super.handleEvent(e);
        }
    }
}
