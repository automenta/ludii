// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.index;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.region.BaseRegionFunction;
import game.functions.region.RegionFunction;
import game.types.board.SiteType;
import game.util.equipment.Region;
import util.Context;

@Hide
public final class SitesEmpty extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction containerFunction;
    
    public static RegionFunction construct(@Opt final SiteType type, @Opt final IntFunction cont) {
        if (cont == null || (cont.isStatic() && cont.eval(null) == 0)) {
            return new EmptyDefault(type);
        }
        return new SitesEmpty(type, cont);
    }
    
    private SitesEmpty(@Opt final SiteType type, @Opt final IntFunction cont) {
        this.containerFunction = ((cont == null) ? new IntConstant(0) : cont);
        this.type = type;
    }
    
    @Override
    public Region eval(final Context context) {
        final int container = this.containerFunction.eval(context);
        return context.state().containerStates()[container].emptyRegion((this.type != null) ? this.type : context.board().defaultSite());
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public String toString() {
        if (this.type == null) {
            return "Null type in Empty.";
        }
        if (this.type == SiteType.Cell) {
            return "Empty(" + this.containerFunction + ")";
        }
        if (this.type == SiteType.Edge) {
            return "EmptyEdge(" + this.containerFunction + ")";
        }
        return "EmptyVertex(" + this.containerFunction + ")";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.containerFunction.gameFlags(game);
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.containerFunction.preprocess(game);
    }
    
    public IntFunction containerFunction() {
        return this.containerFunction;
    }
    
    public SiteType type() {
        return this.type;
    }
    
    public static class EmptyDefault extends BaseRegionFunction
    {
        private static final long serialVersionUID = 1L;
        
        EmptyDefault(final SiteType type) {
            this.type = type;
        }
        
        @Override
        public Region eval(final Context context) {
            return context.state().containerStates()[0].emptyRegion(this.type);
        }
        
        @Override
        public String toString() {
            return "Empty()";
        }
        
        @Override
        public boolean isStatic() {
            return false;
        }
        
        @Override
        public long gameFlags(final Game game) {
            return 0L;
        }
        
        @Override
        public void preprocess(final Game game) {
            this.type = SiteType.use(this.type, game);
        }
    }
}
