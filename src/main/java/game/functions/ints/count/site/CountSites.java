// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.ints.count.site;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.ints.BaseIntFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.last.LastTo;
import game.functions.region.RegionFunction;
import game.functions.region.sites.Sites;
import util.ContainerId;
import util.Context;

@Hide
public final class CountSites extends BaseIntFunction
{
    private static final long serialVersionUID = 1L;
    private Integer preComputedInteger;
    private final RegionFunction region;
    private final ContainerId containerId;
    
    public CountSites(@Opt @Or @Name final RegionFunction in, @Opt @Or @Name final IntFunction at, @Opt @Or final String name) {
        this.preComputedInteger = null;
        this.region = ((in != null) ? in : ((at != null) ? Sites.construct(new IntFunction[] { at }) : Sites.construct(new IntFunction[] { new LastTo(null) })));
        this.containerId = ((name == null && at == null) ? null : new ContainerId(at, name, null, null, null));
    }
    
    @Override
    public int eval(final Context context) {
        if (this.preComputedInteger != null) {
            return this.preComputedInteger;
        }
        if (this.containerId != null) {
            final int cid = this.containerId.eval(context);
            return context.containers()[cid].numSites();
        }
        return this.region.eval(context).count();
    }
    
    @Override
    public boolean isStatic() {
        return this.containerId != null || this.region.isStatic();
    }
    
    @Override
    public String toString() {
        return "Sites()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.region.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.region.preprocess(game);
        if (this.isStatic()) {
            this.preComputedInteger = this.eval(new Context(game, null));
        }
    }
}
