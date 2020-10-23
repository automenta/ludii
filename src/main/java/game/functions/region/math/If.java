// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.math;

import annotations.Opt;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.region.BaseRegionFunction;
import game.functions.region.RegionConstant;
import game.functions.region.RegionFunction;
import game.util.equipment.Region;
import util.Context;

public final class If extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final BooleanFunction condition;
    private final RegionFunction ok;
    private final RegionFunction notOk;
    
    public If(final BooleanFunction cond, final RegionFunction ok, @Opt final RegionFunction notOk) {
        this.condition = cond;
        this.ok = ok;
        this.notOk = ((notOk == null) ? new RegionConstant(new Region()) : notOk);
    }
    
    @Override
    public final Region eval(final Context context) {
        if (this.condition.eval(context)) {
            return this.ok.eval(context);
        }
        return this.notOk.eval(context);
    }
    
    @Override
    public boolean isStatic() {
        return this.condition.isStatic() && this.ok.isStatic() && this.notOk.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.condition.gameFlags(game) | this.ok.gameFlags(game) | this.notOk.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.condition.preprocess(game);
        this.ok.preprocess(game);
        this.notOk.preprocess(game);
    }
}
