// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.deductionPuzzle.is.wip;

import annotations.Hide;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.other.Regions;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.types.board.RegionTypeStatic;
import game.types.board.SiteType;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public class IsProduct extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction region;
    private final String nameRegion;
    private final IntFunction resultFn;
    private final SiteType type;
    
    public IsProduct(@Opt final SiteType elementType, @Opt @Or final RegionFunction region, @Opt @Or final String nameRegion, final IntFunction result) {
        int numNonNull = 0;
        if (region != null) {
            ++numNonNull;
        }
        if (nameRegion != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Zero or one Or parameter must be non-null.");
        }
        if ((this.region = region) != null) {
            this.regionConstraint = region;
            this.nameRegion = null;
        }
        else {
            this.areaConstraint = RegionTypeStatic.Regions;
            this.nameRegion = nameRegion;
        }
        this.resultFn = result;
        this.type = ((elementType == null) ? SiteType.Cell : elementType);
    }
    
    @Override
    public boolean eval(final Context context) {
        final ContainerState ps = context.state().containerStates()[0];
        if (this.region != null) {
            final int result = this.resultFn.eval(context);
            final int[] sites = this.region.eval(context).sites();
            boolean allAssigned = true;
            int currentProduct = 1;
            for (final int site : sites) {
                if (ps.isResolved(site, this.type)) {
                    currentProduct *= ps.what(site, this.type);
                }
                else {
                    allAssigned = false;
                }
            }
            return (!allAssigned || currentProduct == result) && currentProduct <= result;
        }
        else {
            int result = this.resultFn.eval(context);
            final Regions[] regions = context.game().equipment().regions();
            final Integer[] cellHint = context.game().equipment().cellHints();
            for (final Regions rgn : regions) {
                if ((this.nameRegion == null || (rgn.name() != null && rgn.name().equals(this.nameRegion))) && rgn.regionTypes() != null) {
                    final RegionTypeStatic[] regionTypes;
                    final RegionTypeStatic[] areas = regionTypes = rgn.regionTypes();
                    for (final RegionTypeStatic area : regionTypes) {
                        final Integer[][] regionsList = rgn.convertStaticRegionOnLocs(area, context);
                        int indexRegion = 0;
                        for (final Integer[] locs : regionsList) {
                            if (this.resultFn.isHint()) {
                                context.setHint(cellHint[indexRegion]);
                                result = this.resultFn.eval(context);
                            }
                            boolean allAssigned2 = true;
                            int currentProduct2 = 1;
                            for (final Integer loc : locs) {
                                if (ps.isResolved(loc, this.type)) {
                                    currentProduct2 *= ps.what(loc, this.type);
                                }
                                else {
                                    allAssigned2 = false;
                                }
                            }
                            if ((allAssigned2 && currentProduct2 != result) || currentProduct2 > result) {
                                return false;
                            }
                            ++indexRegion;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        String str = "";
        str = str + "Mult(" + this.region + ") = " + this.resultFn;
        return str;
    }
    
    @Override
    public boolean isStatic() {
        return (this.region == null || this.region.isStatic()) && this.resultFn.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        this.resultFn.preprocess(game);
        if (this.region != null) {
            this.region.preprocess(game);
        }
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 128L;
        flags |= this.resultFn.gameFlags(game);
        if (this.region != null) {
            flags |= this.region.gameFlags(game);
        }
        return flags;
    }
    
    public RegionFunction region() {
        return this.region;
    }
    
    public IntFunction result() {
        return this.resultFn;
    }
    
    public String nameRegion() {
        return this.nameRegion;
    }
}
