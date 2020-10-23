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
public final class SitesHint extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public Region eval(final Context context) {
        return context.hintRegion().eval(context);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public String toString() {
        return "Hint()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 128L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
}
