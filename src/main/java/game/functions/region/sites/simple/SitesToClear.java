// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.simple;

import annotations.Hide;
import game.Game;
import game.functions.region.BaseRegionFunction;
import game.util.equipment.Region;
import util.Context;

@Hide
public final class SitesToClear extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public Region eval(final Context context) {
        return context.state().regionToRemove();
    }
    
    @Override
    public boolean contains(final Context context, final int location) {
        return context.state().piecesToRemove().get(location);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public String toString() {
        return "ToClear()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 1073774592L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
