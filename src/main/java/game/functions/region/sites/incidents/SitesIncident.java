// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.incidents;

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
import topology.Cell;
import topology.Edge;
import topology.Topology;
import topology.Vertex;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public final class SitesIncident extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final SiteType ofType;
    private final SiteType resultType;
    private final IntFunction indexFn;
    private final IntFunction ownerFn;
    private Region precomputedRegion;
    
    public SitesIncident(final SiteType resultType, @Name final SiteType of, @Name final IntFunction at, @Opt @Or @Name final Player owner, @Opt @Or final RoleType roleOwner) {
        this.precomputedRegion = null;
        this.indexFn = at;
        this.ofType = of;
        this.resultType = resultType;
        this.ownerFn = ((owner != null) ? owner.index() : ((roleOwner != null) ? new Id(null, roleOwner) : null));
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        switch (this.ofType) {
            case Vertex: {
                return this.evalVertex(context, this.indexFn.eval(context));
            }
            case Edge: {
                return this.evalEdge(context, this.indexFn.eval(context));
            }
            case Cell: {
                return this.evalCell(context, this.indexFn.eval(context));
            }
            default: {
                return new Region();
            }
        }
    }
    
    private Region evalCell(final Context context, final int index) {
        final Topology graph = context.topology();
        final TIntArrayList result = new TIntArrayList();
        if (index < 0 || index >= context.topology().cells().size()) {
            return new Region(result.toArray());
        }
        final Cell cell = graph.cells().get(index);
        switch (this.resultType) {
            case Cell: {
                for (final Edge edge : cell.edges()) {
                    for (final Cell cell2 : edge.cells()) {
                        if (cell2.index() != cell.index()) {
                            result.add(cell2.index());
                        }
                    }
                }
                break;
            }
            case Edge: {
                for (final Edge edge : cell.edges()) {
                    result.add(edge.index());
                }
                break;
            }
            case Vertex: {
                for (final Vertex vertex : cell.vertices()) {
                    result.add(vertex.index());
                }
                break;
            }
        }
        if (this.ownerFn == null) {
            return new Region(result.toArray());
        }
        final TIntArrayList resultOwner = new TIntArrayList();
        final int owner = this.ownerFn.eval(context);
        final ContainerState cs = context.containerState(0);
        for (int i = 0; i < result.size(); ++i) {
            final int site = result.get(i);
            final int who = cs.who(site, this.resultType);
            if ((who != 0 && owner == context.game().players().size()) || who == owner) {
                resultOwner.add(site);
            }
        }
        return new Region(resultOwner.toArray());
    }
    
    private Region evalEdge(final Context context, final int index) {
        final Topology graph = context.topology();
        final TIntArrayList result = new TIntArrayList();
        if (index < 0 || index >= context.topology().edges().size()) {
            return new Region(result.toArray());
        }
        final Edge edge = graph.edges().get(index);
        switch (this.resultType) {
            case Vertex: {
                result.add(edge.vA().index());
                result.add(edge.vB().index());
                break;
            }
            case Edge: {
                for (final Edge edge2 : edge.vA().edges()) {
                    if (edge2.index() != edge.index()) {
                        result.add(edge2.index());
                    }
                }
                for (final Edge edge2 : edge.vB().edges()) {
                    if (edge2.index() != edge.index()) {
                        result.add(edge2.index());
                    }
                }
                break;
            }
            case Cell: {
                for (final Cell face : edge.cells()) {
                    result.add(face.index());
                }
                break;
            }
        }
        if (this.ownerFn == null) {
            return new Region(result.toArray());
        }
        final TIntArrayList resultOwner = new TIntArrayList();
        final int owner = this.ownerFn.eval(context);
        final ContainerState cs = context.containerState(0);
        for (int i = 0; i < result.size(); ++i) {
            final int site = result.get(i);
            final int who = cs.who(site, this.resultType);
            if ((who != 0 && owner == context.game().players().size()) || who == owner) {
                resultOwner.add(site);
            }
        }
        return new Region(resultOwner.toArray());
    }
    
    private Region evalVertex(final Context context, final int index) {
        final Topology graph = context.topology();
        final TIntArrayList result = new TIntArrayList();
        if (index < 0 || index >= context.topology().vertices().size()) {
            return new Region(result.toArray());
        }
        final Vertex vertex = graph.vertices().get(index);
        switch (this.resultType) {
            case Cell: {
                for (final Cell cell : vertex.cells()) {
                    result.add(cell.index());
                }
                break;
            }
            case Edge: {
                for (final Edge edge : vertex.edges()) {
                    result.add(edge.index());
                }
                break;
            }
            case Vertex: {
                for (final Edge edge : vertex.edges()) {
                    for (final Cell vertex2 : edge.cells()) {
                        if (vertex2.index() != vertex.index()) {
                            result.add(vertex2.index());
                        }
                    }
                }
                break;
            }
        }
        if (this.ownerFn == null) {
            return new Region(result.toArray());
        }
        final TIntArrayList resultOwner = new TIntArrayList();
        final int owner = this.ownerFn.eval(context);
        final ContainerState cs = context.containerState(0);
        for (int i = 0; i < result.size(); ++i) {
            final int site = result.get(i);
            final int who = cs.who(site, this.resultType);
            if ((who != 0 && owner == context.game().players().size()) || who == owner) {
                resultOwner.add(site);
            }
        }
        return new Region(resultOwner.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return (this.ownerFn == null || this.ownerFn.isStatic()) && this.indexFn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.indexFn.gameFlags(game);
        gameFlags |= SiteType.stateFlags(this.ofType);
        gameFlags |= SiteType.stateFlags(this.resultType);
        if (this.ownerFn != null) {
            gameFlags |= this.ownerFn.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.indexFn.preprocess(game);
        if (this.ownerFn != null) {
            this.ownerFn.preprocess(game);
        }
        if (this.isStatic()) {
            this.precomputedRegion = this.eval(new Context(game, null));
        }
    }
}
