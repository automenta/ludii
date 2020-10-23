// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.side;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.region.BaseRegionFunction;
import game.types.board.SiteType;
import game.types.play.RoleType;
import game.util.directions.CompassDirection;
import game.util.directions.DirectionFacing;
import game.util.equipment.Region;
import game.util.moves.Player;
import gnu.trove.list.array.TIntArrayList;
import topology.Topology;
import topology.TopologyElement;
import util.Context;

import java.util.List;

@Hide
public final class SitesSide extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private Region precomputedRegion;
    private final IntFunction index;
    private final RoleType role;
    private final DirectionFacing direction;
    
    public SitesSide(@Opt final SiteType elementType, @Or @Opt final Player player, @Or @Opt final RoleType role, @Or @Opt final CompassDirection direction) {
        this.precomputedRegion = null;
        this.type = elementType;
        this.direction = direction;
        this.index = ((role != null) ? new Id(null, role) : ((player != null) ? player.index() : null));
        this.role = role;
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        final boolean useCells = (this.type != null && this.type.equals(SiteType.Cell)) || (this.type == null && context.game().board().defaultSite() != SiteType.Vertex);
        final Topology graph = context.topology();
        if (this.role != null && this.role == RoleType.Shared) {
            return new Region(useCells ? graph.outer(SiteType.Cell) : graph.outer(SiteType.Vertex));
        }
        DirectionFacing dirn = this.direction;
        if (dirn == null && this.index != null) {
            final int pid = this.index.eval(context);
            if (pid < 1 || pid > context.game().players().count()) {
                System.out.println("** Bad player index.");
                return new Region();
            }
            final game.players.Player player = context.game().players().players().get(pid);
            dirn = player.direction();
        }
        if (dirn == null) {
            return new Region();
        }
        final TIntArrayList sites = new TIntArrayList();
        if (useCells) {
            final List<TopologyElement> side = graph.sides(SiteType.Cell).get(dirn);
            for (final TopologyElement v : side) {
                sites.add(v.index());
            }
        }
        else {
            final List<TopologyElement> side = graph.sides(SiteType.Vertex).get(dirn);
            for (final TopologyElement v : side) {
                sites.add(v.index());
            }
        }
        return new Region(sites.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return this.index == null || this.index.isStatic();
    }
    
    @Override
    public String toString() {
        return "Side()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        if (this.index != null) {
            flags = this.index.gameFlags(game);
        }
        flags |= SiteType.stateFlags(this.type);
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        if (this.index != null) {
            this.index.preprocess(game);
        }
        if (this.isStatic()) {
            if (this.type.equals(SiteType.Cell)) {
                this.precomputedRegion = new Region(game.equipment().containers()[0].topology().sides(SiteType.Cell).get(this.direction));
            }
            else {
                this.precomputedRegion = new Region(game.equipment().containers()[0].topology().sides(SiteType.Vertex).get(this.direction));
            }
        }
    }
}
