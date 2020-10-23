// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.count.simple;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.types.board.SiteType;
import util.Context;

@Hide
public final class CountColumns extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private Integer preComputedInteger;
    private SiteType type;
    
    public CountColumns(@Opt final SiteType type) {
        this.preComputedInteger = null;
        this.type = type;
    }
    
    @Override
    public int eval(final Context context) {
        if (this.preComputedInteger != null) {
            return this.preComputedInteger;
        }
        final SiteType realSiteType = (this.type != null) ? this.type : context.board().defaultSite();
        return context.topology().columns(realSiteType).size();
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public String toString() {
        return "Columns()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = 0L;
        gameFlags |= SiteType.stateFlags(this.type);
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.type = SiteType.use(this.type, game);
        this.preComputedInteger = this.eval(new Context(game, null));
    }
}
