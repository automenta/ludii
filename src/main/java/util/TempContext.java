// 
// Decompiled by Procyon v0.5.36
// 

package util;

import util.state.CopyOnWriteState;
import util.state.State;

public final class TempContext extends Context
{
    public TempContext(final Context other) {
        super(other);
    }
    
    @Override
    protected State copyState(final State otherState) {
        return (otherState == null) ? null : new CopyOnWriteState(otherState);
    }
}
