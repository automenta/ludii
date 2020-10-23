// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai;

import annotations.Opt;
import metadata.MetadataItem;
import metadata.ai.features.Features;
import metadata.ai.heuristics.Heuristics;
import metadata.ai.misc.BestAgent;

public class Ai implements MetadataItem
{
    private final BestAgent bestAgent;
    private final Heuristics heuristics;
    private final Features features;
    
    public Ai(@Opt final BestAgent bestAgent, @Opt final Heuristics heuristics, @Opt final Features features) {
        this.bestAgent = bestAgent;
        this.heuristics = heuristics;
        this.features = features;
    }
    
    public BestAgent bestAgent() {
        return this.bestAgent;
    }
    
    public Heuristics heuristics() {
        return this.heuristics;
    }
    
    public Features features() {
        return this.features;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("    (ai\n");
        if (this.bestAgent != null) {
            sb.append("        ").append(this.bestAgent.toString()).append("\n");
        }
        if (this.heuristics != null) {
            sb.append("        ").append(this.heuristics.toString()).append("\n");
        }
        if (this.features != null) {
            sb.append("        ").append(this.features.toString()).append("\n");
        }
        sb.append("    )\n");
        return sb.toString();
    }
}
