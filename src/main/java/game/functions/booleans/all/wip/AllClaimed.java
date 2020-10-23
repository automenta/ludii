// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.all.wip;

import annotations.Hide;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.last.LastTo;
import game.functions.region.RegionFunction;
import game.types.board.SiteType;
import game.util.equipment.Region;
import topology.Edge;
import topology.Topology;
import topology.Vertex;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public final class AllClaimed extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final SiteType indexType;
    private final RegionFunction regionFn;
    
    public AllClaimed(final SiteType type, final RegionFunction region) {
        this.indexType = type;
        this.regionFn = region;
    }
    
    @Override
    public boolean eval(final Context context) {
        final int siteId = new LastTo(null).eval(context);
        if (siteId == -1) {
            return false;
        }
        final Topology graph = context.topology();
        final ContainerState state = context.state().containerStates()[0];
        final int whoSiteId = state.who(siteId, this.indexType);
        final Region sites = this.regionFn.eval(context);
        switch (this.indexType) {
            case Edge: {
                for (int site = sites.bitSet().nextSetBit(0); site >= 0; site = sites.bitSet().nextSetBit(site + 1)) {
                    int count = 0;
                    final Vertex vertex = graph.vertices().get(site);
                    for (final Edge edge : vertex.edges()) {
                        if (state.who(edge.index(), this.indexType) == whoSiteId) {
                            ++count;
                        }
                    }
                    if (count == vertex.edges().size()) {
                        return true;
                    }
                }
                break;
            }
            case Vertex: {
                for (int site = sites.bitSet().nextSetBit(0); site >= 0; site = sites.bitSet().nextSetBit(site + 1)) {
                    final Edge edge2 = graph.edges().get(site);
                    if (state.who(edge2.vA().index(), this.indexType) == whoSiteId && state.who(edge2.vB().index(), this.indexType) == whoSiteId) {
                        return true;
                    }
                }
                break;
            }
            case Cell: {
                for (int site = sites.bitSet().nextSetBit(0); site >= 0; site = sites.bitSet().nextSetBit(site + 1)) {
                    final Edge edge2 = graph.edges().get(site);
                    if (state.who(edge2.vA().index(), this.indexType) == whoSiteId && state.who(edge2.vB().index(), this.indexType) == whoSiteId) {
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "AllClaimed()";
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 8388608L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
