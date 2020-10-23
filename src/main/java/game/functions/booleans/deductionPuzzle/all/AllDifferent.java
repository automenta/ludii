// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.deductionPuzzle.all;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.Game;
import game.equipment.other.Regions;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.types.board.RegionTypeStatic;
import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.state.containerState.ContainerState;

@Hide
public class AllDifferent extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction region;
    private final RegionTypeStatic typeRegion;
    private final IntFunction[] exceptions;
    private final SiteType type;
    
    public AllDifferent(@Opt final SiteType elementType, @Opt final RegionFunction region, @Opt @Name @Or final IntFunction except, @Opt @Name @Or final IntFunction[] excepts) {
        this.region = region;
        this.typeRegion = ((region == null) ? RegionTypeStatic.Regions : null);
        if (region != null) {
            this.regionConstraint = region;
        }
        else {
            this.areaConstraint = this.typeRegion;
        }
        if (excepts != null) {
            this.exceptions = excepts;
        }
        else if (except != null) {
            (this.exceptions = new IntFunction[1])[0] = except;
        }
        else {
            this.exceptions = new IntFunction[0];
        }
        this.type = elementType;
    }
    
    @Override
    public boolean eval(final Context context) {
        final SiteType realType = (this.type == null) ? context.board().defaultSite() : this.type;
        final ContainerState cs = context.state().containerStates()[0];
        final TIntArrayList excepts = new TIntArrayList();
        for (final IntFunction exception : this.exceptions) {
            excepts.add(exception.eval(context));
        }
        if (this.typeRegion == null) {
            final TIntArrayList history = new TIntArrayList();
            final int[] sites = this.region.eval(context).sites();
            if (sites.length == 0) {
                return true;
            }
            for (final int site : sites) {
                if (cs.isResolved(site, realType)) {
                    final int what = cs.what(site, realType);
                    if (what == 0 && !excepts.contains(what)) {
                        return false;
                    }
                    if (!excepts.contains(what)) {
                        if (history.contains(what)) {
                            return false;
                        }
                        history.add(what);
                    }
                }
            }
        }
        else if (this.typeRegion == RegionTypeStatic.Regions) {
            final Regions[] regions2;
            final Regions[] regions = regions2 = context.game().equipment().regions();
            for (final Regions rgn : regions2) {
                if (rgn.regionTypes() != null) {
                    final RegionTypeStatic[] regionTypes;
                    final RegionTypeStatic[] areas = regionTypes = rgn.regionTypes();
                    for (final RegionTypeStatic area : regionTypes) {
                        final Integer[][] convertStaticRegionOnLocs;
                        final Integer[][] regionsList = convertStaticRegionOnLocs = rgn.convertStaticRegionOnLocs(area, context);
                        for (final Integer[] locs : convertStaticRegionOnLocs) {
                            final TIntArrayList history2 = new TIntArrayList();
                            if (area != RegionTypeStatic.AllDirections || cs.what(locs[0], realType) != 0) {
                                for (final Integer loc : locs) {
                                    if (loc != null) {
                                        if (cs.isResolved(loc, realType)) {
                                            final int what2 = cs.what(loc, realType);
                                            if (what2 == 0 && !excepts.contains(what2)) {
                                                return false;
                                            }
                                            if (!excepts.contains(what2)) {
                                                if (history2.contains(what2)) {
                                                    return false;
                                                }
                                                history2.add(what2);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else if (rgn.region() != null) {
                    final RegionFunction[] region;
                    final RegionFunction[] regionsFunctions = region = rgn.region();
                    for (final RegionFunction regionFunction : region) {
                        final int[] locs2 = regionFunction.eval(context).sites();
                        final TIntArrayList history3 = new TIntArrayList();
                        for (final int loc2 : locs2) {
                            if (cs.isResolved(loc2, realType)) {
                                final int what3 = cs.what(loc2, realType);
                                if (what3 == 0 && !excepts.contains(what3)) {
                                    return false;
                                }
                                if (!excepts.contains(what3)) {
                                    if (history3.contains(what3)) {
                                        return false;
                                    }
                                    history3.add(what3);
                                }
                            }
                        }
                    }
                }
                else if (rgn.sites() != null) {
                    final TIntArrayList history4 = new TIntArrayList();
                    for (final int loc3 : rgn.sites()) {
                        if (cs.isResolved(loc3, realType)) {
                            final int what4 = cs.what(loc3, realType);
                            if (what4 == 0 && !excepts.contains(what4)) {
                                return false;
                            }
                            if (!excepts.contains(what4)) {
                                if (history4.contains(what4)) {
                                    return false;
                                }
                                history4.add(what4);
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
        if (this.region != null) {
            str = str + "AllDifferent(" + this.region + ")";
        }
        else {
            str = str + "AllDifferent(" + this.typeRegion.toString() + ")";
        }
        return str;
    }
    
    @Override
    public boolean isStatic() {
        if (this.region != null && !this.region.isStatic()) {
            return false;
        }
        for (final IntFunction fn : this.exceptions) {
            if (!fn.isStatic()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public long gameFlags(final Game game) {
        long flags = 128L;
        if (this.region != null) {
            flags |= this.region.gameFlags(game);
        }
        for (final IntFunction fn : this.exceptions) {
            flags |= fn.gameFlags(game);
        }
        return flags;
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.region != null) {
            this.region.preprocess(game);
        }
        for (final IntFunction fn : this.exceptions) {
            fn.preprocess(game);
        }
    }
    
    public RegionFunction region() {
        return this.region;
    }
    
    public RegionTypeStatic area() {
        return this.typeRegion;
    }
    
    public IntFunction[] exceptions() {
        return this.exceptions;
    }
}
