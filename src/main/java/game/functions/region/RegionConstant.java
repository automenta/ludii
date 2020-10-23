// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region;

import annotations.Anon;
import game.Game;
import game.util.equipment.Region;
import util.Context;

public final class RegionConstant extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final Region region;
    
    public RegionConstant(@Anon final Region region) {
        this.region = region;
    }
    
    @Override
    public Region eval(final Context context) {
        return this.region;
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
