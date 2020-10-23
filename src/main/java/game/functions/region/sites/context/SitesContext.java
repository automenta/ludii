// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.context;

import annotations.Hide;
import game.Game;
import game.functions.region.BaseRegionFunction;
import game.util.equipment.Region;
import util.Context;

@Hide
public final class SitesContext extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public Region eval(final Context context) {
        return context.region();
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
    }
}
