// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.filter;

import annotations.Name;
import game.Game;
import game.functions.booleans.BooleanFunction;
import game.functions.region.BaseRegionFunction;
import game.functions.region.RegionFunction;
import game.util.equipment.Region;
import gnu.trove.list.array.TIntArrayList;
import util.Context;

public final class ForEach extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction region;
    private final BooleanFunction condition;
    
    public ForEach(final RegionFunction region, @Name final BooleanFunction If) {
        this.region = region;
        this.condition = If;
    }
    
    @Override
    public final Region eval(final Context context) {
        final TIntArrayList originalSites = new TIntArrayList(this.region.eval(context).sites());
        final TIntArrayList returnSites = new TIntArrayList();
        for (int i = 0; i < originalSites.size(); ++i) {
            final int site = originalSites.getQuick(i);
            context.setValue("site", site);
            if (this.condition.eval(context)) {
                returnSites.add(site);
            }
        }
        if (context.from() == 33 && context.to() == 34) {
            System.out.println("from is " + context.from());
            System.out.println("to is " + context.to());
            System.out.println(returnSites);
        }
        return new Region(returnSites.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return this.condition.isStatic() && this.region.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        return this.condition.gameFlags(game) | this.region.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        this.condition.preprocess(game);
        this.region.preprocess(game);
    }
}
