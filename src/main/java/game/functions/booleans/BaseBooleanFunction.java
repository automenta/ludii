// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans;

import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.types.board.RegionTypeStatic;
import util.BaseLudeme;
import util.Context;
import util.locations.Location;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseBooleanFunction extends BaseLudeme implements BooleanFunction
{
    private static final long serialVersionUID = 1L;
    public RegionFunction regionConstraint;
    public IntFunction[] locsConstraint;
    public RegionTypeStatic areaConstraint;
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public boolean autoFails() {
        return false;
    }
    
    @Override
    public boolean autoSucceeds() {
        return false;
    }
    
    @Override
    public RegionFunction regionConstraint() {
        return this.regionConstraint;
    }
    
    @Override
    public IntFunction[] locsConstraint() {
        return this.locsConstraint;
    }
    
    @Override
    public RegionTypeStatic staticRegion() {
        return this.areaConstraint;
    }
    
    @Override
    public List<Location> satisfyingSites(final Context context) {
        return new ArrayList<>();
    }
}
