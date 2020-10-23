// 
// Decompiled by Procyon v0.5.36
// 

package util.state.owned;

import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import util.locations.Location;

import java.util.List;

public interface Owned
{
    Owned copy();
    
    int mapCompIndex(final int playerId, final int componentId);
    
    int reverseMap(final int playerId, final int mappedIndex);
    
    TIntArrayList levels(final int playerId, final int componentId, final int site);
    
    TIntArrayList sites(final int playerId, final int componentId);
    
    TIntArrayList sites(final int playerId);
    
    TIntArrayList sitesOnTop(final int playerId);
    
    List<? extends Location> positions(final int playerId, final int componentId);
    
    List<? extends Location>[] positions(final int playerId);
    
    void remove(final int playerId, final int componentId, final int pieceLoc);
    
    void remove(final int playerId, final int componentId, final int pieceLoc, final int level);
    
    void add(final int playerId, final int componentId, final int pieceLoc, final SiteType type);
    
    void add(final int playerId, final int componentId, final int pieceLoc, final int level, final SiteType type);
}
