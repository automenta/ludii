// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.region;

import game.Game;
import game.types.board.SiteType;
import game.types.state.GameType;
import game.util.equipment.Region;
import util.Context;

public interface RegionFunction extends GameType
{
    Region eval(final Context context);
    
    boolean contains(final Context context, final int location);
    
    SiteType type(final Game game);
}
