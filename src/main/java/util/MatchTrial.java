// 
// Decompiled by Procyon v0.5.36
// 

package util;

import java.util.ArrayList;
import java.util.List;

public final class MatchTrial
{
    protected final List<Trial> trials;
    
    public MatchTrial(final Trial trial) {
        (this.trials = new ArrayList<>()).add(trial);
    }
    
    public List<Trial> trials() {
        return this.trials;
    }
    
    public void clear() {
        this.trials.clear();
    }
    
    public Trial currentTrial() {
        return this.trials.isEmpty() ? null : this.trials.get(this.trials.size() - 1);
    }
}
