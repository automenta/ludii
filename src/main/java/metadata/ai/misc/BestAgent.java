// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.misc;

import main.StringRoutines;
import metadata.ai.AIItem;

public final class BestAgent implements AIItem
{
    private final String agent;
    
    public BestAgent(final String agent) {
        this.agent = agent;
    }
    
    public String agent() {
        return this.agent;
    }
    
    @Override
    public String toString() {
        return "(bestAgent " + StringRoutines.quote(this.agent) + ")";
    }
}
