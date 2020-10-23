// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.deductionPuzzle.is.regionResult;

import annotations.Hide;
import annotations.Opt;
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
public class IsSum extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction region;
    private final IntFunction resultFn;
    private final SiteType type;
    private final String name;
    
    public IsSum(@Opt final SiteType elementType, @Opt final RegionFunction region, @Opt final String nameRegion, final IntFunction result) {
        this.region = region;
        this.resultFn = result;
        if (region != null) {
            this.regionConstraint = region;
        }
        else {
            this.areaConstraint = RegionTypeStatic.Regions;
        }
        this.type = ((elementType == null) ? SiteType.Cell : elementType);
        this.name = ((nameRegion == null) ? "" : nameRegion);
    }
    
    @Override
    public boolean eval(final Context context) {
        final ContainerState ps = context.state().containerStates()[0];
        if (this.region != null) {
            final int result = this.resultFn.eval(context);
            final int[] sites = this.region.eval(context).sites();
            boolean allAssigned = true;
            int currentSum = 0;
            for (final int site : sites) {
                if (ps.isResolved(site, this.type)) {
                    currentSum += ps.what(site, this.type);
                }
                else {
                    allAssigned = false;
                }
            }
            return (!allAssigned || currentSum == result) && currentSum <= result;
        }
        else {
            int result = this.resultFn.eval(context);
            final Regions[] regions = context.game().equipment().regions();
            Integer[] regionHint;
            if (this.type == SiteType.Cell) {
                regionHint = context.game().equipment().cellHints();
            }
            else if (this.type == SiteType.Vertex) {
                regionHint = context.game().equipment().vertexHints();
            }
            else {
                regionHint = context.game().equipment().edgeHints();
            }
            for (final Regions reg : regions) {
                if (reg.name().contains(this.name) && reg.regionTypes() != null) {
                    final RegionTypeStatic[] regionTypes;
                    final RegionTypeStatic[] areas = regionTypes = reg.regionTypes();
                    for (final RegionTypeStatic area : regionTypes) {
                        final Integer[][] regionsList = reg.convertStaticRegionOnLocs(area, context);
                        int indexRegion = 0;
                        for (final Integer[] locs : regionsList) {
                            if (this.resultFn.isHint()) {
                                context.setHint(regionHint[indexRegion]);
                                result = this.resultFn.eval(context);
                            }
                            boolean allAssigned2 = true;
                            int currentSum2 = 0;
                            for (final Integer loc : locs) {
                                if (ps.isResolved(loc, this.type)) {
                                    currentSum2 += ps.what(loc, this.type);
                                }
                                else {
                                    allAssigned2 = false;
                                }
                            }
                            if ((allAssigned2 && currentSum2 != result) || currentSum2 > result) {
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
        str = str + "Sum(" + this.region + ") = " + this.resultFn;
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
}
