// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.crossing;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.region.BaseRegionFunction;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.equipment.Region;
import game.util.moves.Player;
import gnu.trove.list.array.TIntArrayList;
import main.math.MathRoutines;
import topology.Edge;
import topology.Topology;
import topology.Vertex;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public final class SitesCrossing extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction startLocationFn;
    private final IntFunction roleFunc;
    
    public SitesCrossing(@Name final IntFunction at, @Opt @Or final Player who, @Opt @Or final RoleType role) {
        this.startLocationFn = at;
        this.roleFunc = ((role != null) ? new Id(null, role) : who.index());
    }
    
    @Override
    public Region eval(final Context context) {
        final int from = this.startLocationFn.eval(context);
        if (from == -1) {
            return new Region(new TIntArrayList().toArray());
        }
        final Topology graph = context.topology();
        final TIntArrayList groupItems = new TIntArrayList();
        final ContainerState state = context.state().containerStates()[0];
        final int numPlayers = context.game().players().count();
        final int whoSiteId = this.roleFunc.eval(context);
        int player = 0;
        if (whoSiteId == 0) {
            if (!context.game().isGraphGame()) {
                return null;
            }
            player = this.roleFunc.eval(context);
        }
        else {
            player = whoSiteId;
        }
        final Edge kEdge = graph.edges().get(from);
        final int vA = kEdge.vA().index();
        final int vB = kEdge.vB().index();
        final Vertex a = graph.vertices().get(vA);
        final Vertex b = graph.vertices().get(vB);
        final double a0x = a.centroid().getX();
        final double a0y = a.centroid().getY();
        final double a1x = b.centroid().getX();
        final double a1y = b.centroid().getY();
        for (int k = 0; k < graph.edges().size(); ++k) {
            if (((whoSiteId == numPlayers + 1 && state.what(k, SiteType.Edge) != 0) || (player < numPlayers + 1 && state.who(k, SiteType.Edge) == whoSiteId)) && from != k) {
                final Edge kEdgek = graph.edges().get(k);
                final int vAk = kEdgek.vA().index();
                final int vBk = kEdgek.vB().index();
                final Vertex c = graph.vertices().get(vAk);
                final Vertex d = graph.vertices().get(vBk);
                final double b0x = c.centroid().getX();
                final double b0y = c.centroid().getY();
                final double b1x = d.centroid().getX();
                final double b1y = d.centroid().getY();
                if (MathRoutines.isCrossing(a0x, a0y, a1x, a1y, b0x, b0y, b1x, b1y)) {
                    groupItems.add(k);
                }
            }
        }
        return new Region(groupItems.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        gameFlags |= SiteType.stateFlags(this.type);
        gameFlags |= this.startLocationFn.gameFlags(game);
        if (this.roleFunc != null) {
            gameFlags |= this.roleFunc.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.startLocationFn.preprocess(game);
        if (this.roleFunc != null) {
            this.roleFunc.preprocess(game);
        }
    }
}
