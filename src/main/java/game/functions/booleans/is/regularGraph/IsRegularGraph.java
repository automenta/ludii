// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.regularGraph;

import annotations.*;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.booleans.BooleanConstant;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntConstant;
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

import java.util.BitSet;

@Hide
public class IsRegularGraph extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction who;
    private final IntFunction kParameter;
    private final BooleanFunction oddFn;
    private final BooleanFunction evenFn;
    
    public IsRegularGraph(@Or final Player who, @Or final RoleType role, @Opt @Or2 @Name final IntFunction k, @Opt @Or2 @Name final BooleanFunction odd, @Opt @Or2 @Name final BooleanFunction even) {
        this.who = ((who != null) ? who.index() : new Id(null, role));
        this.kParameter = ((k == null) ? new IntConstant(0) : k);
        this.oddFn = ((odd == null) ? BooleanConstant.construct(false) : odd);
        this.evenFn = ((even == null) ? BooleanConstant.construct(false) : even);
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
        final boolean oddFlag = this.oddFn.eval(context);
        final boolean evenFlag = this.evenFn.eval(context);
        final int kValue = this.kParameter.eval(context);
        final int totalVertices = graph.vertices().size();
        final int totalEdges = graph.edges().size();
        final BitSet[] degreeInfo = new BitSet[totalVertices];
        if (whoSiteId == 0) {
            if (state.what(siteId, SiteType.Edge) == 0) {
                whoSiteId = 1;
            }
            else {
                whoSiteId = state.what(siteId, SiteType.Edge);
            }
        }
        for (int i = 0; i < totalVertices; ++i) {
            degreeInfo[i] = new BitSet(totalEdges);
        }
        for (int k = 0; k < totalEdges; ++k) {
            final Edge kEdge = graph.edges().get(k);
            if (state.what(kEdge.index(), SiteType.Edge) == whoSiteId) {
                final int vA = kEdge.vA().index();
                final int vB = kEdge.vB().index();
                degreeInfo[vA].set(vB);
                degreeInfo[vB].set(vA);
            }
        }
        int deg;
        if ((deg = kValue) == 0) {
            for (int j = 0; j < totalVertices; ++j) {
                if (degreeInfo[j].cardinality() != 0) {
                    deg = degreeInfo[j].cardinality();
                    break;
                }
            }
        }
        for (int j = 0; j < totalVertices; ++j) {
            if (deg != degreeInfo[j].cardinality()) {
                return false;
            }
        }
        if (oddFlag) {
            return deg % 2 == 1;
        }
        return !evenFlag || deg % 2 == 0;
    }
    
    @Override
    public String toString() {
        String str = "";
        str += "IsRegularGraph( )";
        return str;
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
        this.who.preprocess(game);
    }
}
