// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.state;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.types.board.SiteType;
import util.Context;
import util.state.containerStackingState.BaseContainerStateStacking;
import util.state.containerState.ContainerState;

public final class State extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction locn;
    private final IntFunction level;
    private SiteType type;
    
    public State(@Opt final SiteType type, @Name final IntFunction at, @Opt @Name final IntFunction level) {
        this.locn = at;
        this.level = level;
        this.type = type;
    }
    
    @Override
    public int eval(final Context context) {
        final int loc = this.locn.eval(context);
        if (loc == -1) {
            return 0;
        }
        final int containerId = context.containerId()[loc];
        if (!context.game().isStacking() || containerId != 0) {
            final ContainerState cs = context.state().containerStates()[containerId];
            return cs.state(loc, this.type);
        }
        final BaseContainerStateStacking state = (BaseContainerStateStacking)context.state().containerStates()[containerId];
        if (this.level == null) {
            return state.state(loc, this.type);
        }
        return state.state(loc, this.level.eval(context), this.type);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long stateFlag = this.locn.gameFlags(game) | 0x2L;
        stateFlag |= SiteType.stateFlags(this.type);
        if (this.level != null) {
            stateFlag |= this.level.gameFlags(game);
        }
        return stateFlag;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.locn.preprocess(game);
        if (this.level != null) {
            this.level.preprocess(game);
        }
    }
}
