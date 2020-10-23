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
import game.functions.region.RegionFunction;
import game.types.board.RegionTypeStatic;
import game.types.board.SiteType;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public class SameParity extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction region;
    private final String nameRegion;
    private final SiteType type;
    
    public SameParity(@Opt final SiteType type, @Opt @Or final RegionFunction region, @Opt @Or final String nameRegion) {
        this.region = region;
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
        if (region != null) {
            this.regionConstraint = region;
            this.nameRegion = null;
        }
        else {
            this.areaConstraint = RegionTypeStatic.Regions;
            this.nameRegion = nameRegion;
        }
        this.type = ((type == null) ? SiteType.Cell : type);
    }
    
    @Override
    public boolean eval(final Context context) {
        final ContainerState ps = context.state().containerStates()[0];
        if (this.region != null) {
            final int[] sites = this.region.eval(context).sites();
            boolean allAssigned = true;
            int evenCount = 0;
            int unevenCount = 0;
            for (final int site : sites) {
                if (ps.isResolved(site, this.type)) {
                    if ((ps.what(site, this.type) & 0x1) == 0x0) {
                        ++evenCount;
                    }
                    else {
                        ++unevenCount;
                    }
                }
                else {
                    allAssigned = false;
                }
            }
            return !allAssigned || evenCount == unevenCount || evenCount + 1 == unevenCount || evenCount == 1 + unevenCount;
        }
        else {
            final Regions[] regions2;
            final Regions[] regions = regions2 = context.game().equipment().regions();
            for (final Regions rgn : regions2) {
                if ((this.nameRegion == null || (rgn.name() != null && rgn.name().equals(this.nameRegion))) && rgn.regionTypes() != null) {
                    final RegionTypeStatic[] regionTypes;
                    final RegionTypeStatic[] areas = regionTypes = rgn.regionTypes();
                    for (final RegionTypeStatic area : regionTypes) {
                        final Integer[][] convertStaticRegionOnLocs;
                        final Integer[][] regionsList = convertStaticRegionOnLocs = rgn.convertStaticRegionOnLocs(area, context);
                        for (final Integer[] locs : convertStaticRegionOnLocs) {
                            boolean allAssigned2 = true;
                            int evenCount2 = 0;
                            int unevenCount2 = 0;
                            for (final Integer loc : locs) {
                                if (ps.isResolved(loc, this.type)) {
                                    if ((ps.what(loc, this.type) & 0x1) == 0x0) {
                                        ++evenCount2;
                                    }
                                    else {
                                        ++unevenCount2;
                                    }
                                }
                                else {
                                    allAssigned2 = false;
                                }
                            }
                            if (allAssigned2 && evenCount2 != unevenCount2 && evenCount2 + 1 != unevenCount2 && evenCount2 != 1 + unevenCount2) {
                                return false;
                            }
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
        str = str + "SameParity(" + this.region + ")";
        return str;
    }
    
    @Override
    public boolean isStatic() {
        return this.region != null && this.region.isStatic();
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.region != null) {
            this.region.preprocess(game);
        }
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 128L;
        if (this.region != null) {
            flags |= this.region.gameFlags(game);
        }
        return flags;
    }
    
    public RegionFunction region() {
        return this.region;
    }
    
    public String nameRegion() {
        return this.nameRegion;
    }
}
