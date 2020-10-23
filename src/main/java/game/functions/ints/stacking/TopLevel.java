// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.stacking;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.types.board.SiteType;
import util.Context;
import util.state.containerState.ContainerState;

public final class TopLevel extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction locn;
    private SiteType type;
    
    public TopLevel(@Opt final SiteType type, @Name final IntFunction at) {
        this.locn = at;
        this.type = type;
    }
    
    @Override
    public int eval(final Context context) {
        if (!context.game().isStacking()) {
            return 0;
        }
        final int loc = this.locn.eval(context);
        if (loc == -1) {
            return 0;
        }
        final ContainerState cs = context.state().containerStates()[context.containerId()[loc]];
        final int sizeStack = cs.sizeStack(loc, this.type);
        if (sizeStack != 0) {
            return cs.sizeStack(loc, this.type) - 1;
        }
        return 0;
    }
    
    @Override
    public boolean isStatic() {
        return this.locn.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.locn.gameFlags(game) | 0x10L;
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.locn.preprocess(game);
    }
    
    @Override
    public String toString() {
        return "Top(" + this.locn + ")";
    }
}
