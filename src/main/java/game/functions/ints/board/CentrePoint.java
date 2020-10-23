// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.board;

import annotations.Opt;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.types.board.SiteType;
import topology.Topology;
import util.Context;

public final class CentrePoint extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    protected SiteType type;
    private int precomputedInteger;
    
    public CentrePoint(@Opt final SiteType type) {
        this.precomputedInteger = -1;
        this.type = type;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.precomputedInteger != -1) {
            return this.precomputedInteger;
        }
        if (this.type != null && this.type.equals(SiteType.Edge)) {
            return -1;
        }
        final Topology graph = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        return graph.centre(realType).get(0).index();
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
        if (this.isStatic()) {
            this.precomputedInteger = this.eval(new Context(game, null));
        }
    }
    
    @Override
    public String toString() {
        return "Middle()";
    }
}
