// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.board;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.types.board.SiteType;
import topology.Topology;
import topology.TopologyElement;
import util.Context;

public final class Phase extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction indexFn;
    private SiteType type;
    
    public Phase(@Opt final SiteType type, @Name final IntFunction of) {
        this.indexFn = of;
        this.type = type;
    }
    
    @Override
    public int eval(final Context context) {
        final int index = this.indexFn.eval(context);
        if (context.containerId()[index] != 0) {
            return -1;
        }
        final Topology graph = context.topology();
        final SiteType realType = (this.type != null) ? this.type : context.game().board().defaultSite();
        final TopologyElement element = graph.getGraphElements(realType).get(index);
        return element.phase();
    }
    
    @Override
    public boolean isStatic() {
        return this.indexFn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long stateFlag = this.indexFn.gameFlags(game);
        stateFlag |= SiteType.stateFlags(this.type);
        return stateFlag;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.indexFn.preprocess(game);
    }
}
