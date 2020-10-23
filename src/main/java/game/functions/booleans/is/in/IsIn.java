// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.is.in;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.booleans.BooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.context.To;
import game.functions.region.RegionFunction;
import util.Context;

@Hide
public final class IsIn extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction[] sites;
    private final RegionFunction region;
    
    public static BooleanFunction construct(@Opt @Or final IntFunction site, @Opt @Or final IntFunction[] sites, final RegionFunction region) {
        if (sites != null) {
            return new IsIn(sites, region);
        }
        return new InSingleSite((site == null) ? To.instance() : site, region);
    }
    
    private IsIn(final IntFunction[] sites, final RegionFunction region) {
        this.sites = sites;
        this.region = region;
    }
    
    @Override
    public boolean eval(final Context context) {
        if (this.sites != null) {
            for (final IntFunction site : this.sites) {
                final int location = site.eval(context);
                if (location == -1 || !this.region.eval(context).bitSet().get(location)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        if (this.sites.length == 1) {
            return "In(" + this.sites[0] + "," + this.region + ")";
        }
        String str = "In({";
        for (final IntFunction site : this.sites) {
            str = str + site + " ";
        }
        str = str + "}," + this.region + ")";
        return str;
    }
    
    @Override
    public boolean isStatic() {
        if (this.sites != null) {
            for (final IntFunction site : this.sites) {
                if (!site.isStatic()) {
                    return false;
                }
            }
        }
        return this.region.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        long gameFlags = this.region.gameFlags(game);
        if (this.sites != null) {
            for (final IntFunction site : this.sites) {
                gameFlags |= site.gameFlags(game);
            }
        }
        return gameFlags;
    }
    
    @Override
    public void preprocess(final Game game) {
        this.region.preprocess(game);
        if (this.sites != null) {
            for (final IntFunction site : this.sites) {
                site.preprocess(game);
            }
        }
    }
    
    public RegionFunction region() {
        return this.region;
    }
    
    public IntFunction[] site() {
        return this.sites;
    }
    
    private static class InSingleSite extends BaseBooleanFunction
    {
        private static final long serialVersionUID = 1L;
        protected final IntFunction siteFunc;
        protected final RegionFunction region;
        
        public InSingleSite(final IntFunction site, final RegionFunction region) {
            this.siteFunc = site;
            this.region = region;
        }
        
        @Override
        public boolean eval(final Context context) {
            final int location = this.siteFunc.eval(context);
            return location >= 0 && this.region.contains(context, location);
        }
        
        @Override
        public String toString() {
            return "In(" + this.siteFunc + "," + this.region + ")";
        }
        
        @Override
        public boolean isStatic() {
            return this.region.isStatic() && this.siteFunc.isStatic();
        }
        
        @Override
        public long gameFlags(final Game game) {
            long gameFlags = this.region.gameFlags(game);
            gameFlags |= this.siteFunc.gameFlags(game);
            return gameFlags;
        }
        
        @Override
        public void preprocess(final Game game) {
            this.region.preprocess(game);
            this.siteFunc.preprocess(game);
        }
    }
}
