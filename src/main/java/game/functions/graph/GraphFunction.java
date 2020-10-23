// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.graph;

import game.types.board.SiteType;
import game.types.state.GameType;
import game.util.graph.Graph;
import util.Context;

public interface GraphFunction extends GameType
{
    Graph eval(final Context context, final SiteType siteType);
    
    int[] dim();
}
