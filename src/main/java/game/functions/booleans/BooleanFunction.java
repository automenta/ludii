// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans;

import game.functions.ints.IntFunction;
import game.functions.region.RegionFunction;
import game.types.board.RegionTypeStatic;
import game.types.state.GameType;
import util.Context;
import util.locations.Location;

import java.util.List;

public interface BooleanFunction extends GameType
{
    boolean eval(final Context context);
    
    boolean autoFails();
    
    boolean autoSucceeds();
    
    RegionFunction regionConstraint();
    
    IntFunction[] locsConstraint();
    
    RegionTypeStatic staticRegion();
    
    List<Location> satisfyingSites(final Context context);
}
