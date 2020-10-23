// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region;

import annotations.Hide;
import game.Game;
import game.types.board.SiteType;
import util.BaseLudeme;
import util.Context;

@Hide
public abstract class BaseRegionFunction extends BaseLudeme implements RegionFunction
{
    private static final long serialVersionUID = 1L;
    protected SiteType type;
    
    @Override
    public boolean contains(final Context context, final int location) {
        return this.eval(context).contains(location);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public SiteType type(final Game game) {
        return (this.type != null) ? this.type : game.board().defaultSite();
    }
}
