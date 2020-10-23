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
import util.BaseLudeme;
import util.Context;
import util.state.containerStackingState.BaseContainerStateStacking;
import util.state.containerState.ContainerState;

public final class Who extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction loc;
    private final IntFunction level;
    private SiteType type;
    
    public static IntFunction construct(@Opt final SiteType type, @Name final IntFunction at, @Opt @Name final IntFunction level) {
        if (level == null || (level.isStatic() && level.eval(null) == -1)) {
            return new WhoNoLevel(at, type);
        }
        return new Who(type, at, level);
    }
    
    private Who(@Opt final SiteType type, @Name final IntFunction at, @Opt @Name final IntFunction level) {
        this.loc = at;
        this.level = ((level == null) ? new IntConstant(-1) : level);
        this.type = type;
    }
    
    @Override
    public final int eval(final Context context) {
        final int location = this.loc.eval(context);
        if (location == -1) {
            return 0;
        }
        final int containerId = context.containerId()[location];
        if ((context.game().gameFlags() & 0x10L) == 0x0L) {
            final ContainerState cs = context.state().containerStates()[containerId];
            return cs.who(this.loc.eval(context), this.type);
        }
        final BaseContainerStateStacking state = (BaseContainerStateStacking)context.state().containerStates()[containerId];
        if (this.level.eval(context) == -1) {
            return state.who(this.loc.eval(context), this.type);
        }
        return state.who(this.loc.eval(context), this.level.eval(context), this.type);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.loc.gameFlags(game) | this.level.gameFlags(game);
        if (this.type != null) {
            if (this.type.equals(SiteType.Edge) || this.type.equals(SiteType.Vertex)) {
                gameFlags |= 0x800000L;
            }
            if (this.type.equals(SiteType.Edge)) {
                gameFlags |= 0x4000000L;
            }
            if (this.type.equals(SiteType.Vertex)) {
                gameFlags |= 0x1000000L;
            }
            if (this.type.equals(SiteType.Cell)) {
                gameFlags |= 0x2000000L;
            }
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.loc.preprocess(game);
        this.level.preprocess(game);
    }
    
    public IntFunction site() {
        return this.loc;
    }
    
    public static class WhoNoLevel extends BaseLudeme implements IntFunction
    {
        private static final long serialVersionUID = 1L;
        protected final IntFunction site;
        private SiteType type;
        
        public WhoNoLevel(final IntFunction site, @Opt final SiteType type) {
            this.site = site;
            this.type = type;
        }
        
        @Override
        public final int eval(final Context context) {
            final int location = this.site.eval(context);
            if (location < 0) {
                return 0;
            }
            final int containerId = context.containerId()[location];
            final ContainerState cs = context.state().containerStates()[containerId];
            return cs.who(location, this.type);
        }
        
        @Override
        public boolean isStatic() {
            return false;
        }
        
        @Override
        public long gameFlags(final Game game) {
            long gameFlags = this.site.gameFlags(game);
            gameFlags |= SiteType.stateFlags(this.type);
            return gameFlags;
        }
        
        @Override
        public void preprocess(final Game game) {
            if (this.type == null) {
                this.type = game.board().defaultSite();
            }
            this.site.preprocess(game);
        }
        
        @Override
        public boolean isHint() {
            return false;
        }
    }
}
