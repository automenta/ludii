// 
// Decompiled by Procyon v0.5.36
// 

package features.graph_search;

import features.Walk;
import topology.TopologyElement;

import java.util.List;

public class Path
{
    protected final List<TopologyElement> sites;
    protected final Walk walk;
    
    public Path(final List<TopologyElement> sites, final Walk walk) {
        this.sites = sites;
        this.walk = walk;
    }
    
    public TopologyElement destination() {
        return this.sites.get(this.sites.size() - 1);
    }
    
    public TopologyElement start() {
        return this.sites.get(0);
    }
    
    public List<TopologyElement> sites() {
        return this.sites;
    }
    
    public Walk walk() {
        return this.walk;
    }
}
