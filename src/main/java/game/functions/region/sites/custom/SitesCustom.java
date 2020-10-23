// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.sites.custom;

import annotations.Hide;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.region.BaseRegionFunction;
import game.util.equipment.Region;
import util.Context;

@Hide
public final class SitesCustom extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private Region precomputedRegion;
    private final IntFunction[] sitesFn;
    
    public SitesCustom(final IntFunction[] sites) {
        this.precomputedRegion = null;
        this.sitesFn = sites;
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        final int[] sites = new int[this.sitesFn.length];
        for (int i = 0; i < this.sitesFn.length; ++i) {
            if (this.sitesFn[i] != null) {
                final int site = this.sitesFn[i].eval(context);
                if (site > -1) {
                    sites[i] = this.sitesFn[i].eval(context);
                }
            }
        }
        return new Region(sites);
    }
    
    @Override
    public boolean contains(final Context context, final int location) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion.contains(location);
        }
        for (int i = 0; i < this.sitesFn.length; ++i) {
            if (this.sitesFn[i] != null && location == this.sitesFn[i].eval(context)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isStatic() {
        for (final IntFunction site : this.sitesFn) {
            if (!site.isStatic()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "CustomSites()";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 0L;
        for (final IntFunction site : this.sitesFn) {
            flags |= site.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        for (final IntFunction site : this.sitesFn) {
            site.preprocess(game);
        }
        if (this.isStatic()) {
            this.precomputedRegion = this.eval(new Context(game, null));
        }
    }
}
