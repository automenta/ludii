// 
// Decompiled by Procyon v0.5.36
// 

package util.state.owned;

import game.Game;
import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import main.collections.FastTIntArrayList;
import util.locations.FlatCellOnlyLocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class FlatCellOnlyOwned implements Owned, Serializable
{
    private static final long serialVersionUID = 1L;
    protected final FastTIntArrayList[][] locations;
    protected final OwnedIndexMapper indexMapper;
    
    public FlatCellOnlyOwned(final Game game) {
        this.indexMapper = new OwnedIndexMapper(game);
        this.locations = new FastTIntArrayList[game.players().size() + 1][];
        for (int p = 0; p <= game.players().size(); ++p) {
            this.locations[p] = new FastTIntArrayList[this.indexMapper.numValidIndices(p)];
            for (int i = 0; i < this.locations[p].length; ++i) {
                this.locations[p][i] = new FastTIntArrayList();
            }
        }
    }
    
    private FlatCellOnlyOwned(final FlatCellOnlyOwned other) {
        this.indexMapper = other.indexMapper;
        this.locations = new FastTIntArrayList[other.locations.length][];
        for (int p = 0; p < other.locations.length; ++p) {
            this.locations[p] = new FastTIntArrayList[other.locations[p].length];
            for (int i = 0; i < other.locations[p].length; ++i) {
                this.locations[p][i] = new FastTIntArrayList(other.locations[p][i]);
            }
        }
    }
    
    @Override
    public FlatCellOnlyOwned copy() {
        return new FlatCellOnlyOwned(this);
    }
    
    @Override
    public int mapCompIndex(final int playerId, final int componentId) {
        return this.indexMapper.compIndex(playerId, componentId);
    }
    
    @Override
    public int reverseMap(final int playerId, final int mappedIndex) {
        return this.indexMapper.reverseMap(playerId, mappedIndex);
    }
    
    @Override
    public TIntArrayList levels(final int playerId, final int componentId, final int site) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public FastTIntArrayList sites(final int playerId, final int componentId) {
        final int mappedIdx = this.indexMapper.compIndex(playerId, componentId);
        if (mappedIdx >= 0) {
            return new FastTIntArrayList(this.locations[playerId][mappedIdx]);
        }
        return new FastTIntArrayList();
    }
    
    @Override
    public TIntArrayList sites(final int playerId) {
        final TIntArrayList sites = new TIntArrayList();
        for (int i = 0; i < this.locations[playerId].length; ++i) {
            sites.addAll(this.locations[playerId][i]);
        }
        return sites;
    }
    
    @Override
    public TIntArrayList sitesOnTop(final int playerId) {
        return this.sites(playerId);
    }
    
    @Override
    public List<FlatCellOnlyLocation> positions(final int playerId, final int componentId) {
        final TIntArrayList sites = this.locations[playerId][this.indexMapper.compIndex(playerId, componentId)];
        final List<FlatCellOnlyLocation> locs = new ArrayList<>(sites.size());
        for (int i = 0; i < sites.size(); ++i) {
            locs.add(new FlatCellOnlyLocation(sites.getQuick(i)));
        }
        return locs;
    }
    
    @Override
    public List<FlatCellOnlyLocation>[] positions(final int playerId) {
        final TIntArrayList[] playerSites = this.locations[playerId];
        final List<FlatCellOnlyLocation>[] playerLocs = (List<FlatCellOnlyLocation>[])new List[playerSites.length];
        for (int i = 0; i < playerSites.length; ++i) {
            final TIntArrayList sites = this.locations[playerId][i];
            if (sites == null) {
                playerLocs[i] = null;
            }
            else {
                final List<FlatCellOnlyLocation> locs = new ArrayList<>(sites.size());
                for (int j = 0; j < sites.size(); ++j) {
                    locs.add(new FlatCellOnlyLocation(sites.getQuick(j)));
                }
                playerLocs[i] = locs;
            }
        }
        return playerLocs;
    }
    
    @Override
    public void remove(final int playerId, final int componentId, final int pieceLoc) {
        final FastTIntArrayList compPositions = this.locations[playerId][this.indexMapper.compIndex(playerId, componentId)];
        final int idx = compPositions.indexOf(pieceLoc);
        if (idx >= 0) {
            final int lastIdx = compPositions.size() - 1;
            compPositions.set(idx, compPositions.getQuick(lastIdx));
            compPositions.removeAt(lastIdx);
        }
    }
    
    @Override
    public void remove(final int playerId, final int componentId, final int pieceLoc, final int level) {
        assert level == 0;
        this.remove(playerId, componentId, pieceLoc);
    }
    
    @Override
    public void add(final int playerId, final int componentId, final int pieceLoc, final SiteType type) {
        assert type == SiteType.Cell;
        this.locations[playerId][this.indexMapper.compIndex(playerId, componentId)].add(pieceLoc);
    }
    
    @Override
    public void add(final int playerId, final int componentId, final int pieceLoc, final int level, final SiteType type) {
        assert type == SiteType.Cell;
        assert level == 0;
        this.add(playerId, componentId, pieceLoc, type);
    }
}
