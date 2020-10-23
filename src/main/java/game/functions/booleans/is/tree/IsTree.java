// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.tree;

import annotations.Hide;
import annotations.Or;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.ints.last.LastTo;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.moves.Player;
import topology.Edge;
import topology.Topology;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public class IsTree extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction who;
    
    public IsTree(@Or final Player who, @Or final RoleType role) {
        this.who = ((role != null) ? new Id(null, role) : who.index());
    }
    
    @Override
    public boolean eval(final Context context) {
        final int siteId = new LastTo(null).eval(context);
        if (siteId == -1) {
            return false;
        }
        final Topology graph = context.topology();
        final int cid = context.containerId()[0];
        final ContainerState state = context.state().containerStates()[cid];
        int whoSiteId = this.who.eval(context);
        final int totalVertices = graph.vertices().size();
        final int[] localParent = new int[totalVertices];
        if (whoSiteId == 0) {
            if (state.what(siteId, SiteType.Edge) == 0) {
                whoSiteId = 1;
            }
            else {
                whoSiteId = state.what(siteId, SiteType.Edge);
            }
        }
        for (int i = 0; i < totalVertices; ++i) {
            localParent[i] = i;
        }
        for (int k = graph.edges().size() - 1; k >= 0; --k) {
            if (state.what(k, SiteType.Edge) == whoSiteId) {
                final Edge kEdge = graph.edges().get(k);
                final int vARoot = this.find(kEdge.vA().index(), localParent);
                final int vBRoot = this.find(kEdge.vB().index(), localParent);
                if (vARoot == vBRoot) {
                    return false;
                }
                localParent[vARoot] = vBRoot;
            }
        }
        return true;
    }
    
    private int find(final int position, final int[] parent) {
        final int parentId = parent[position];
        if (parentId == position) {
            return position;
        }
        return this.find(parentId, parent);
    }
    
    @Override
    public String toString() {
        return "IsTree()";
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0x800000L | this.who.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.who.preprocess(game);
    }
}
