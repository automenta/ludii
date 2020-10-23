// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.random;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.Game;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.region.BaseRegionFunction;
import game.functions.region.RegionFunction;
import game.functions.region.sites.index.SitesEmpty;
import game.types.board.SiteType;
import game.util.equipment.Region;
import gnu.trove.list.array.TIntArrayList;
import util.Context;

@Hide
public final class SitesRandom extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction region;
    private final IntFunction numSitesFn;
    
    public SitesRandom(@Opt final RegionFunction region, @Opt @Name final IntFunction num) {
        this.region = ((region == null) ? SitesEmpty.construct(SiteType.Cell, new IntConstant(0)) : region);
        this.numSitesFn = ((num == null) ? new IntConstant(1) : num);
    }
    
    @Override
    public Region eval(final Context context) {
        final TIntArrayList sites = new TIntArrayList();
        final int[] regionSites = this.region.eval(context).sites();
        final int numSites = this.numSitesFn.eval(context);
        while (sites.size() != numSites && regionSites.length != 0) {
            final int site = regionSites[context.rng().nextInt(regionSites.length)];
            if (!sites.contains(site)) {
                sites.add(site);
            }
        }
        return new Region(sites.toArray());
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public String toString() {
        return "SitesRandom()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long flags = this.region.gameFlags(game) | this.numSitesFn.gameFlags(game);
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.region.preprocess(game);
        this.numSitesFn.preprocess(game);
    }
}
