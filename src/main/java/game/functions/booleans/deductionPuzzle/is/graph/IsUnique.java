// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.deductionPuzzle.is.graph;

import annotations.Hide;
import annotations.Opt;
import game.Game;
import game.equipment.other.Regions;
import game.functions.booleans.BaseBooleanFunction;
import game.types.board.RegionTypeStatic;
import game.types.board.SiteType;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public class IsUnique extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final SiteType type;
    
    public IsUnique(@Opt final SiteType elementType) {
        this.areaConstraint = RegionTypeStatic.Regions;
        this.type = ((elementType == null) ? SiteType.Cell : elementType);
    }
    
    @Override
    public boolean eval(final Context context) {
        final ContainerState ps = context.state().containerStates()[0];
        final Regions[] regions2;
        final Regions[] regions = regions2 = context.game().equipment().regions();
        for (final Regions region : regions2) {
            if (region.regionTypes() != null) {
                final RegionTypeStatic[] regionTypes2;
                final RegionTypeStatic[] regionTypes = regionTypes2 = region.regionTypes();
                for (final RegionTypeStatic regionType : regionTypes2) {
                    final Integer[][] regionsList = region.convertStaticRegionOnLocs(regionType, context);
                    for (int i = 0; i < regionsList.length; ++i) {
                        for (int j = i + 1; j < regionsList.length; ++j) {
                            final Integer[] set1 = regionsList[i];
                            final Integer[] set2 = regionsList[j];
                            if (this.regionAllAssigned(set1, ps) && this.regionAllAssigned(set2, ps)) {
                                boolean identical = true;
                                for (int index = 0; index < set1.length; ++index) {
                                    if (ps.what(set1[index], this.type) != ps.what(set2[index], this.type)) {
                                        identical = false;
                                        break;
                                    }
                                }
                                if (identical) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public boolean regionAllAssigned(final Integer[] region, final ContainerState ps) {
        for (final Integer loc : region) {
            if (!ps.isResolved(loc, this.type)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    @Override
    public long gameFlags(final Game game) {
        final long flags = 128L;
        return 128L;
    }
    
    @Override
    public String toString() {
        String str = "";
        str += "Unique()";
        return str;
    }
}
