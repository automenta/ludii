// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region.math;

import annotations.Or;
import game.Game;
import game.functions.ints.IntFunction;
import game.functions.region.BaseRegionFunction;
import game.functions.region.RegionFunction;
import game.util.equipment.Region;
import util.Context;

public final class Difference extends BaseRegionFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction source;
    private final RegionFunction subtraction;
    private final IntFunction siteToRemove;
    private Region precomputedRegion;
    
    public Difference(final RegionFunction source, @Or final RegionFunction subtraction, @Or final IntFunction siteToRemove) {
        this.precomputedRegion = null;
        this.source = source;
        int numNonNull = 0;
        if (subtraction != null) {
            ++numNonNull;
        }
        if (siteToRemove != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("one parameter must be non-null.");
        }
        this.siteToRemove = siteToRemove;
        this.subtraction = subtraction;
    }
    
    @Override
    public Region eval(final Context context) {
        if (this.precomputedRegion != null) {
            return this.precomputedRegion;
        }
        final Region sites1 = new Region(this.source.eval(context));
        if (this.subtraction != null) {
            final Region sites2 = this.subtraction.eval(context);
            sites1.remove(sites2);
            return sites1;
        }
        final int site2 = this.siteToRemove.eval(context);
        sites1.remove(site2);
        return sites1;
    }
    
    @Override
    public boolean isStatic() {
        return (this.subtraction == null || this.subtraction.isStatic()) && (this.siteToRemove == null || this.siteToRemove.isStatic()) && this.source.isStatic();
    }
    
    @Override
    public String toString() {
        return "Difference(" + this.source + "," + this.subtraction + ")";
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flag = this.source.gameFlags(game);
        if (this.subtraction != null) {
            flag |= this.subtraction.gameFlags(game);
        }
        if (this.siteToRemove != null) {
            flag |= this.siteToRemove.gameFlags(game);
        }
        return flag;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.source.preprocess(game);
        if (this.subtraction != null) {
            this.subtraction.preprocess(game);
        }
        if (this.siteToRemove != null) {
            this.siteToRemove.preprocess(game);
        }
        if (this.isStatic()) {
            final Context context = new Context(game, null);
            final Region sites1 = new Region(this.source.eval(context));
            if (this.subtraction != null) {
                final Region sites2 = this.subtraction.eval(context);
                sites1.remove(sites2);
            }
            else {
                final int site2 = this.siteToRemove.eval(context);
                sites1.remove(site2);
            }
            this.precomputedRegion = sites1;
        }
    }
    
    public RegionFunction source() {
        return this.source;
    }
    
    public RegionFunction subtraction() {
        return this.subtraction;
    }
}
