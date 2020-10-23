// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.context;

import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import topology.Topology;
import util.ContainerId;
import util.Context;

public final class Edge extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction vAFn;
    private final IntFunction vBFn;
    private int precomputedValue;
    
    public Edge(final IntFunction vA, final IntFunction vB) {
        this.precomputedValue = -1;
        this.vAFn = vA;
        this.vBFn = vB;
    }
    
    public Edge() {
        this.precomputedValue = -1;
        this.vAFn = null;
        this.vBFn = null;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.precomputedValue != -1) {
            return this.precomputedValue;
        }
        if (this.vAFn == null || this.vBFn == null) {
            return context.edge();
        }
        final int va = this.vAFn.eval(context);
        final int vb = this.vBFn.eval(context);
        final int cid = new ContainerId(null, null, null, null, this.vAFn).eval(context);
        final Topology graph = context.containers()[cid].topology();
        final topology.Edge edge = graph.findEdge(graph.vertices().get(va), graph.vertices().get(vb));
        if (edge != null) {
            return edge.index();
        }
        return -1;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.vAFn != null && this.vAFn.isStatic() && this.vBFn != null && this.vBFn.isStatic()) {
            this.precomputedValue = this.eval(new Context(game, null));
        }
    }
    
    @Override
    public String toString() {
        return "Edge()";
    }
}
