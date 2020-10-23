// 
// Decompiled by Procyon v0.5.36
// 

package util.state;

import util.state.onTrack.OnTrackIndices;
import util.state.onTrack.OnTrackIndicesCOW;

public final class CopyOnWriteState extends State
{
    private static final long serialVersionUID = 1L;
    
    public CopyOnWriteState(final State other) {
        super(other);
    }
    
    @Override
    protected OnTrackIndices copyOnTrackIndices(final OnTrackIndices otherOnTrackIndices) {
        return (otherOnTrackIndices == null) ? null : new OnTrackIndicesCOW(otherOnTrackIndices);
    }
}
