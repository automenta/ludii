// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.state;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.types.board.SiteType;
import util.Context;
import util.state.containerStackingState.BaseContainerStateStacking;
import util.state.containerState.ContainerState;

public final class What extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction loc;
    private final IntFunction level;
    private SiteType type;
    
    public What(@Opt final SiteType type, @Name final IntFunction at, @Opt @Name final IntFunction level) {
        this.loc = at;
        this.level = ((level == null) ? new IntConstant(0) : level);
        this.type = type;
    }
    
    @Override
    public int eval(final Context context) {
        final int siteIdx = this.loc.eval(context);
        if (siteIdx == -1) {
            return 0;
        }
        final int containerId = context.containerId()[siteIdx];
        if (!context.game().isStacking()) {
            final ContainerState cs = context.state().containerStates()[containerId];
            return cs.what(siteIdx, this.type);
        }
        final BaseContainerStateStacking state = (BaseContainerStateStacking)context.state().containerStates()[containerId];
        if (this.level.eval(context) == -1) {
            return state.what(siteIdx, this.type);
        }
        return state.what(siteIdx, this.level.eval(context), this.type);
    }
    
    public IntFunction loc() {
        return this.loc;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.loc.gameFlags(game) | this.level.gameFlags(game);
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.loc.preprocess(game);
        this.level.preprocess(game);
    }
    
    @Override
    public String toString() {
        String str = "(What ";
        str += this.loc;
        str = str + "," + this.level;
        str += ")";
        return str;
    }
}
