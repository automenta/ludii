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
import game.types.board.ShapeType;
import game.types.board.SiteType;
import util.Context;

import java.util.Arrays;

@Hide
public class IsShape extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final RegionFunction region;
    private final String nameRegion;
    private final ShapeType shape;
    private final SiteType type;
    
    public IsShape(@Opt @Or final RegionFunction region, @Opt @Or final String nameRegion, final ShapeType shape, @Opt final SiteType type) {
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
        this.region = region;
        this.nameRegion = nameRegion;
        if (region != null) {
            this.regionConstraint = region;
        }
        else {
            this.areaConstraint = RegionTypeStatic.Regions;
        }
        this.shape = ((shape == null) ? ShapeType.Rectangle : shape);
        this.type = ((type == null) ? SiteType.Cell : type);
    }
    
    @Override
    public boolean eval(final Context context) {
        int numColumns = context.topology().columns(SiteType.Cell).size();
        if (this.type.equals(SiteType.Vertex)) {
            --numColumns;
        }
        if (this.region != null) {
            final int[] sites = this.region.eval(context).sites();
            Arrays.sort(sites);
            if (this.shape.equals(ShapeType.Rectangle) || this.shape.equals(ShapeType.Square)) {
                int startingRow = -1;
                int numRowSites = 0;
                int firstIndexOfRow = -1;
                int currentIndex = -1;
                int currentRow = -1;
                for (int i = 0; i < sites.length; ++i) {
                    final int iRow = (int)Math.ceil(sites[i] / numColumns);
                    if (i == 0) {
                        firstIndexOfRow = sites[i];
                        startingRow = iRow;
                        currentIndex = sites[i];
                        currentRow = iRow;
                    }
                    else if (iRow - currentRow == 1) {
                        currentIndex = sites[i];
                        if (sites[i] % numColumns != sites[0]) {
                            return false;
                        }
                        if (currentRow == startingRow) {
                            numRowSites = currentIndex - firstIndexOfRow;
                        }
                        else if (currentIndex - firstIndexOfRow != numRowSites) {
                            return false;
                        }
                        currentRow = iRow;
                        firstIndexOfRow = sites[i];
                    }
                    else {
                        if (sites[i] - currentIndex != 1) {
                            return false;
                        }
                        currentIndex = sites[i];
                    }
                }
            }
        }
        else {
            final Regions[] regions2;
            final Regions[] regions = regions2 = context.game().equipment().regions();
            for (final Regions reg : regions2) {
                if ((this.nameRegion == null || (reg.name() != null && reg.name().equals(this.nameRegion))) && reg.regionTypes() != null) {
                    final RegionTypeStatic[] regionTypes;
                    final RegionTypeStatic[] areas = regionTypes = reg.regionTypes();
                    for (final RegionTypeStatic area : regionTypes) {
                        final Integer[][] convertStaticRegionOnLocs;
                        final Integer[][] regionsList = convertStaticRegionOnLocs = reg.convertStaticRegionOnLocs(area, context);
                        for (final Integer[] locs : convertStaticRegionOnLocs) {
                            Arrays.sort(locs);
                            if (this.shape.equals(ShapeType.Rectangle) || this.shape.equals(ShapeType.Square)) {
                                int startingRow2 = -1;
                                int numRowSites2 = 0;
                                int firstIndexOfRow2 = -1;
                                int currentIndex2 = -1;
                                int currentRow2 = -1;
                                for (int j = 0; j < locs.length; ++j) {
                                    final int iRow2 = (int)Math.ceil(locs[j] / numColumns);
                                    if (j == 0) {
                                        firstIndexOfRow2 = locs[j];
                                        startingRow2 = iRow2;
                                        currentIndex2 = locs[j];
                                        currentRow2 = iRow2;
                                    }
                                    else if (iRow2 - currentRow2 == 1) {
                                        currentIndex2 = locs[j];
                                        if (locs[j] % numColumns != locs[0]) {
                                            return false;
                                        }
                                        if (currentRow2 == startingRow2) {
                                            numRowSites2 = currentIndex2 - firstIndexOfRow2;
                                        }
                                        else if (currentIndex2 - firstIndexOfRow2 != numRowSites2) {
                                            return false;
                                        }
                                        currentRow2 = iRow2;
                                        firstIndexOfRow2 = locs[j];
                                    }
                                    else {
                                        if (locs[j] - currentIndex2 != 1) {
                                            return false;
                                        }
                                        currentIndex2 = locs[j];
                                    }
                                }
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
        str = str + "Shape(" + this.region + ") = " + this.shape;
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
