// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.math;

import game.Game;
import game.functions.region.BaseRegionFunction;
import game.functions.region.RegionFunction;
import game.util.equipment.Region;
import util.Context;

public class Union extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction region1;
    private final RegionFunction region2;
    private final RegionFunction[] regions;
    private Region precomputedRegion;
    
    public Union(final RegionFunction region1, final RegionFunction region2) {
        this.precomputedRegion = null;
        this.region1 = region1;
        this.region2 = region2;
        this.regions = null;
    }
    
    public Union(final RegionFunction[] regions) {
        this.precomputedRegion = null;
        this.region1 = null;
        this.region2 = null;
        this.regions = regions;
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        if (this.regions == null) {
            final Region sites1 = new Region(this.region1.eval(context));
            final Region sites2 = this.region2.eval(context);
            sites1.union(sites2);
            return sites1;
        }
        if (this.regions.length == 0) {
            return new Region();
        }
        final Region sites3 = new Region(this.regions[0].eval(context));
        for (int i = 1; i < this.regions.length; ++i) {
            sites3.union(new Region(this.regions[i].eval(context)));
        }
        return sites3;
    }
    
    @Override
    public long gameFlags(final Game game) {
        if (this.regions == null) {
            return this.region1.gameFlags(game) | this.region2.gameFlags(game);
        }
        long gameFlags = 0L;
        for (final RegionFunction region : this.regions) {
            gameFlags |= region.gameFlags(game);
        }
        return gameFlags;
    }
    
    @Override
    public boolean isStatic() {
        if (this.regions == null) {
            return this.region1.isStatic() && this.region2.isStatic();
        }
        for (final RegionFunction region : this.regions) {
            if (!region.isStatic()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.regions == null) {
            this.region1.preprocess(game);
            this.region2.preprocess(game);
        }
        else {
            for (final RegionFunction region : this.regions) {
                region.preprocess(game);
            }
        }
        if (this.isStatic()) {
            this.precomputedRegion = this.eval(new Context(game, null));
        }
    }
    
    public RegionFunction region1() {
        return this.region1;
    }
    
    public RegionFunction region2() {
        return this.region2;
    }
    
    public RegionFunction[] regions() {
        return this.regions;
    }
}
