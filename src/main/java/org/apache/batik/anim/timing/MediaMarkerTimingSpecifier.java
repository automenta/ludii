// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.anim.timing;

public class MediaMarkerTimingSpecifier extends TimingSpecifier
{
    protected String syncbaseID;
    protected TimedElement mediaElement;
    protected String markerName;
    protected InstanceTime instance;
    
    public MediaMarkerTimingSpecifier(final TimedElement owner, final boolean isBegin, final String syncbaseID, final String markerName) {
        super(owner, isBegin);
        this.syncbaseID = syncbaseID;
        this.markerName = markerName;
        this.mediaElement = owner.getTimedElementById(syncbaseID);
    }
    
    @Override
    public String toString() {
        return this.syncbaseID + ".marker(" + this.markerName + ")";
    }
    
    @Override
    public boolean isEventCondition() {
        return false;
    }
}
