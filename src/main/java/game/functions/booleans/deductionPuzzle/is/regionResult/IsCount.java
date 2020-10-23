// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.deductionPuzzle.is.regionResult;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.types.board.SiteType;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public class IsCount extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction region;
    private final IntFunction whatFn;
    private final IntFunction resultFn;
    private final SiteType type;
    
    public IsCount(@Opt final SiteType type, @Opt final RegionFunction region, @Opt final IntFunction what, final IntFunction result) {
        this.region = region;
        this.whatFn = ((what == null) ? new IntConstant(1) : what);
        this.resultFn = result;
        this.type = type;
    }
    
    @Override
    public boolean eval(final Context context) {
        if (this.region == null) {
            return false;
        }
        final SiteType realType = (this.type == null) ? context.board().defaultSite() : this.type;
        final ContainerState ps = context.state().containerStates()[0];
        final int what = this.whatFn.eval(context);
        final int result = this.resultFn.eval(context);
        final int[] sites = this.region.eval(context).sites();
        boolean assigned = true;
        int currentCount = 0;
        for (final int site : sites) {
            if (ps.isResolved(site, realType)) {
                final int whatSite = ps.what(site, realType);
                if (whatSite == what) {
                    ++currentCount;
                }
            }
            else {
                assigned = false;
            }
        }
        return (!assigned || currentCount == result) && currentCount <= result;
    }
    
    @Override
    public boolean isStatic() {
        return this.region.isStatic() && this.whatFn.isStatic() && this.resultFn.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        this.region.preprocess(game);
        this.whatFn.preprocess(game);
        this.resultFn.preprocess(game);
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 128L;
        flags |= this.region.gameFlags(game);
        flags |= this.whatFn.gameFlags(game);
        flags |= this.resultFn.gameFlags(game);
        return flags;
    }
    
    public RegionFunction region() {
        return this.region;
    }
    
    public IntFunction result() {
        return this.resultFn;
    }
    
    public IntFunction what() {
        return this.whatFn;
    }
    
    @Override
    public String toString() {
        String str = "";
        str = str + "Count(" + this.region + ") = " + this.resultFn;
        return str;
    }
}
