// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.board;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.functions.region.sites.Sites;
import game.types.board.SiteType;
import topology.Topology;
import util.Context;

public final class Cost extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private int precomputedInteger;
    private final RegionFunction region;
    private final SiteType type;
    
    public Cost(@Opt final SiteType type, @Or @Name final IntFunction at, @Or @Name final RegionFunction in) {
        this.precomputedInteger = -1;
        this.type = ((type == null) ? SiteType.Cell : type);
        this.region = ((in != null) ? in : Sites.construct(new IntFunction[] { at }));
    }
    
    @Override
    public int eval(final Context context) {
        if (this.precomputedInteger != -1) {
            return this.precomputedInteger;
        }
        final int[] sites = this.region.eval(context).sites();
        final Topology graph = context.topology();
        int sum = 0;
        for (final int site : sites) {
            if (this.type == SiteType.Vertex) {
                sum += graph.vertices().get(site).cost();
            }
            else if (this.type == SiteType.Cell) {
                sum += graph.cells().get(site).cost();
            }
            else if (this.type == SiteType.Edge) {
                sum += graph.edges().get(site).cost();
            }
        }
        return sum;
    }
    
    @Override
    public boolean isStatic() {
        return this.region.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.region.gameFlags(game);
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.region.preprocess(game);
        if (this.isStatic()) {
            this.precomputedInteger = this.eval(new Context(game, null));
        }
    }
    
    @Override
    public String toString() {
        return "Cost()";
    }
}
