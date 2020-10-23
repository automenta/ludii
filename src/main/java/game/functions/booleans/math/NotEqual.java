// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans.math;

import annotations.Alias;
import annotations.Or;
import game.Game;
import game.functions.booleans.BaseBooleanFunction;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.functions.region.RegionFunction;
import game.types.play.RoleType;
import game.util.equipment.Region;
import gnu.trove.list.array.TIntArrayList;
import util.Context;

@Alias(alias = "!=")
public final class NotEqual extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private final IntFunction valueA;
    private final IntFunction valueB;
    private final RegionFunction regionA;
    private final RegionFunction regionB;
    private Boolean precomputedBoolean;
    
    public NotEqual(final IntFunction valueA, @Or final IntFunction valueB, @Or final RoleType roleB) {
        int numNonNull2 = 0;
        if (valueB != null) {
            ++numNonNull2;
        }
        if (roleB != null) {
            ++numNonNull2;
        }
        if (numNonNull2 != 1) {
            throw new IllegalArgumentException("Only one Or2 should be non-null.");
        }
        this.valueA = valueA;
        this.valueB = ((valueB != null) ? valueB : new Id(null, roleB));
        this.regionA = null;
        this.regionB = null;
    }
    
    public NotEqual(final RegionFunction regionA, final RegionFunction regionB) {
        this.valueA = null;
        this.valueB = null;
        this.regionA = regionA;
        this.regionB = regionB;
    }
    
    @Override
    public boolean eval(final Context context) {
        if (this.precomputedBoolean != null) {
            return this.precomputedBoolean;
        }
        if (this.regionA == null) {
            return this.valueA.eval(context) != this.valueB.eval(context);
        }
        final Region rA = this.regionA.eval(context);
        final Region rB = this.regionB.eval(context);
        final TIntArrayList listA = new TIntArrayList(rA.sites());
        final TIntArrayList listB = new TIntArrayList(rB.sites());
        if (listA.size() != listB.size()) {
            return true;
        }
        for (int i = 0; i < listA.size(); ++i) {
            final int siteA = listA.getQuick(i);
            if (!listB.contains(siteA)) {
                return true;
            }
        }
        return false;
    }
    
    public IntFunction valueA() {
        return this.valueA;
    }
    
    public IntFunction valueB() {
        return this.valueB;
    }
    
    @Override
    public String toString() {
        String str = "";
        if (this.regionA == null) {
            str = str + "NotEqual(" + this.valueA + ", " + this.valueB + ")";
        }
        else {
            str = str + "NotEqual(" + this.regionA + ", " + this.regionB + ")";
        }
        return str;
    }
    
    @Override
    public boolean isStatic() {
        if (this.regionA == null) {
            return this.valueA.isStatic() && this.valueB.isStatic();
        }
        return this.regionA.isStatic() && this.regionB.isStatic();
    }
    
    @Override
    public long gameFlags(final Game game) {
        if (this.regionA == null) {
            return this.valueA.gameFlags(game) | this.valueB.gameFlags(game);
        }
        return this.regionA.gameFlags(game) | this.regionB.gameFlags(game);
    }
    
    @Override
    public void preprocess(final Game game) {
        if (this.regionA == null) {
            this.valueA.preprocess(game);
            this.valueB.preprocess(game);
        }
        else {
            this.regionA.preprocess(game);
            this.regionB.preprocess(game);
        }
        if (this.isStatic()) {
            this.precomputedBoolean = this.eval(new Context(game, null));
        }
    }
}
