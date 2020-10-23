// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.related;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.functions.region.sites.Sites;
import game.types.board.RelationType;
import game.types.board.SiteType;
import topology.Cell;
import topology.Edge;
import topology.Topology;
import topology.Vertex;
import util.Context;

@Hide
public final class IsRelated extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    protected final IntFunction site;
    protected final RegionFunction region;
    private SiteType type;
    private final RelationType relationType;
    private Boolean precomputedBoolean;
    
    public IsRelated(final RelationType relationType, @Opt final SiteType type, final IntFunction siteA, @Or final IntFunction siteB, @Or final RegionFunction region) {
        int numNonNull = 0;
        if (siteA != null) {
            ++numNonNull;
        }
        if (siteB != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Only one Or parameter can be non-null.");
        }
        this.site = siteA;
        if (region != null) {
            this.region = region;
        }
        else {
            this.region = Sites.construct(new IntFunction[] { siteB });
        }
        this.type = type;
        this.relationType = relationType;
    }
    
    @Override
    public boolean eval(final Context context) {
        if (this.precomputedBoolean != null) {
            return this.precomputedBoolean;
        }
        final int[] sites = this.region.eval(context).sites();
        final int location = this.site.eval(context);
        final Topology graph = context.topology();
        if ((this.type == null && context.game().board().defaultSite() != SiteType.Vertex) || this.type == SiteType.Cell) {
            final Cell cellA = graph.cells().get(location);
            switch (this.relationType) {
                case Adjacent -> {
                    for (final int st : sites) {
                        final Cell cellB = graph.cells().get(st);
                        if (cellB.adjacent().contains(cellA)) {
                            return true;
                        }
                    }
                    break;
                }
                case Diagonal -> {
                    for (final int st : sites) {
                        final Cell cellB = graph.cells().get(st);
                        if (cellB.diagonal().contains(cellA)) {
                            return true;
                        }
                    }
                    break;
                }
                case All -> {
                    for (final int st : sites) {
                        final Cell cellB = graph.cells().get(st);
                        if (cellB.neighbours().contains(cellA)) {
                            return true;
                        }
                    }
                    break;
                }
                case OffDiagonal -> {
                    for (final int st : sites) {
                        final Cell cellB = graph.cells().get(st);
                        if (cellB.off().contains(cellA)) {
                            return true;
                        }
                    }
                    break;
                }
                case Orthogonal -> {
                    for (final int st : sites) {
                        final Cell cellB = graph.cells().get(st);
                        if (cellB.orthogonal().contains(cellA)) {
                            return true;
                        }
                    }
                    break;
                }
            }
        }
        else if ((this.type == null && context.game().board().defaultSite() != SiteType.Vertex) || this.type == SiteType.Vertex) {
            final Vertex vertexA = graph.vertices().get(location);
            switch (this.relationType) {
                case Adjacent -> {
                    for (final int st : sites) {
                        for (final Vertex v : vertexA.adjacent()) {
                            if (v.index() == st) {
                                return true;
                            }
                        }
                    }
                    break;
                }
                case Diagonal -> {
                    for (final int st : sites) {
                        for (final Vertex v : vertexA.diagonal()) {
                            if (v.index() == st) {
                                return true;
                            }
                        }
                    }
                    break;
                }
                case All -> {
                    for (final int st : sites) {
                        for (final Vertex v : vertexA.neighbours()) {
                            if (v.index() == st) {
                                return true;
                            }
                        }
                    }
                    break;
                }
                case OffDiagonal -> {
                    for (final int st : sites) {
                        for (final Vertex v : vertexA.off()) {
                            if (v.index() == st) {
                                return true;
                            }
                        }
                    }
                    break;
                }
                case Orthogonal -> {
                    for (final int st : sites) {
                        for (final Vertex v : vertexA.orthogonal()) {
                            if (v.index() == st) {
                                return true;
                            }
                        }
                    }
                    break;
                }
            }
        }
        else {
            final Edge edgeA = graph.edges().get(location);
            switch (this.relationType) {
                case Adjacent -> {
                    for (final int st : sites) {
                        for (final Edge edgeB : edgeA.vA().edges()) {
                            if (edgeB.index() == st) {
                                return true;
                            }
                        }
                        for (final Edge edgeB : edgeA.vB().edges()) {
                            if (edgeB.index() == st) {
                                return true;
                            }
                        }
                    }
                    break;
                }
                case Diagonal -> {
                    return false;
                }
                case All -> {
                    for (final int st : sites) {
                        for (final Edge edgeB : edgeA.vA().edges()) {
                            if (edgeB.index() == st) {
                                return true;
                            }
                        }
                        for (final Edge edgeB : edgeA.vB().edges()) {
                            if (edgeB.index() == st) {
                                return true;
                            }
                        }
                    }
                    break;
                }
                case OffDiagonal -> {
                    return false;
                }
                case Orthogonal -> {
                    for (final int st : sites) {
                        for (final Edge edgeB : edgeA.vA().edges()) {
                            if (edgeB.index() == st) {
                                return true;
                            }
                        }
                        for (final Edge edgeB : edgeA.vB().edges()) {
                            if (edgeB.index() == st) {
                                return true;
                            }
                        }
                    }
                    break;
                }
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Connected(" + this.site + "," + this.region + ")";
    }
    
    @Override
    public boolean isStatic() {
        return this.region.isStatic() && this.site.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.region.gameFlags(game) | this.site.gameFlags(game);
        if (this.type != null) {
            if (this.type == SiteType.Edge || this.type == SiteType.Vertex) {
                gameFlags |= 0x800000L;
            }
            if (this.type == SiteType.Edge) {
                gameFlags |= 0x4000000L;
            }
            if (this.type == SiteType.Vertex) {
                gameFlags |= 0x1000000L;
            }
            if (this.type == SiteType.Cell) {
                gameFlags |= 0x2000000L;
            }
        }
        else if (game.board().defaultSite() == SiteType.Vertex) {
            gameFlags |= 0x1000000L;
        }
        else {
            gameFlags |= 0x2000000L;
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.site.preprocess(game);
        this.region.preprocess(game);
        if (this.isStatic()) {
            this.precomputedBoolean = this.eval(new Context(game, null));
        }
    }
}
