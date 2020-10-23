// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.board;

import annotations.Opt;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.types.board.SiteType;
import topology.SiteFinder;
import topology.TopologyElement;
import util.Context;

public final class Coord extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final String coord;
    private SiteType type;
    private int precomputedValue;
    
    public Coord(@Opt final SiteType type, final String coordinate) {
        this.precomputedValue = -1;
        this.coord = coordinate;
        this.type = type;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.precomputedValue != -1) {
            return this.precomputedValue;
        }
        final TopologyElement element = SiteFinder.find(context.board(), this.coord, this.type);
        if (element == null) {
            return -1;
        }
        return element.index();
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        flags |= SiteType.stateFlags(this.type);
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.precomputedValue = this.eval(new Context(game, null));
    }
    
    @Override
    public String toString() {
        return "Coord(" + this.coord + ")";
    }
}
