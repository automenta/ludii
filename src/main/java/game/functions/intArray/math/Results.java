// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.intArray.math;

import annotations.Name;
import annotations.Or;
import annotations.Or2;
import game.Game;
import game.functions.intArray.BaseIntArrayFunction;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.functions.region.sites.custom.SitesCustom;
import gnu.trove.list.array.TIntArrayList;
import util.Context;

public final class Results extends BaseIntArrayFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction regionFromFn;
    private final RegionFunction regionToFn;
    private final IntFunction functionFn;
    
    public Results(@Name @Or final IntFunction from, @Name @Or final RegionFunction From, @Name @Or2 final IntFunction to, @Name @Or2 final RegionFunction To, final IntFunction function) {
        int numNonNull = 0;
        if (from != null) {
            ++numNonNull;
        }
        if (From != null) {
            ++numNonNull;
        }
        if (numNonNull != 1) {
            throw new IllegalArgumentException("Only one Or parameter must be non-null.");
        }
        int numNonNull2 = 0;
        if (to != null) {
            ++numNonNull2;
        }
        if (To != null) {
            ++numNonNull2;
        }
        if (numNonNull2 != 1) {
            throw new IllegalArgumentException("Only one Or2 parameter must be non-null.");
        }
        this.functionFn = function;
        this.regionFromFn = ((From != null) ? From : new SitesCustom(new IntFunction[] { from }));
        this.regionToFn = ((To != null) ? To : new SitesCustom(new IntFunction[] { to }));
    }
    
    @Override
    public int[] eval(final Context context) {
        final TIntArrayList resultList = new TIntArrayList();
        final int[] sitesFrom = this.regionFromFn.eval(context).sites();
        final int originFrom = context.from();
        final int originTo = context.to();
        for (final int from : sitesFrom) {
            context.setFrom(from);
            final int[] sites;
            final int[] sitesTo = sites = this.regionToFn.eval(context).sites();
            for (final int to : sites) {
                context.setTo(to);
                resultList.add(this.functionFn.eval(context));
            }
        }
        context.setFrom(originFrom);
        context.setTo(originTo);
        return resultList.toArray();
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flag = 0L;
        flag |= this.regionFromFn.gameFlags(game);
        flag |= this.regionToFn.gameFlags(game);
        flag |= this.functionFn.gameFlags(game);
        return flag;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.regionFromFn.preprocess(game);
        this.regionToFn.preprocess(game);
        this.functionFn.preprocess(game);
    }
}
