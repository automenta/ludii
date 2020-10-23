// 
// Decompiled by Procyon v0.5.36
// 

package util.state.owned;

import game.Game;
import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import util.locations.FullLocation;
import util.locations.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class FullOwned implements Owned, Serializable
{
    private static final long serialVersionUID = 1L;
    protected final List<FullLocation>[][] locations;
    protected final OwnedIndexMapper indexMapper;
    
    public FullOwned(final Game game) {
        this.indexMapper = new OwnedIndexMapper(game);
        this.locations = (List<FullLocation>[][])new List[game.players().size() + 1][];
        for (int p = 0; p <= game.players().size(); ++p) {
            this.locations[p] = (List<FullLocation>[])new List[this.indexMapper.numValidIndices(p)];
            for (int i = 0; i < this.locations[p].length; ++i) {
                this.locations[p][i] = new ArrayList<>();
            }
        }
    }
    
    private FullOwned(final FullOwned other) {
        this.indexMapper = other.indexMapper;
        this.locations = (List<FullLocation>[][])new List[other.locations.length][];
        for (int p = 0; p < other.locations.length; ++p) {
            this.locations[p] = (List<FullLocation>[])new List[other.locations[p].length];
            for (int i = 0; i < other.locations[p].length; ++i) {
                final List<FullLocation> otherPositionsComp = other.locations[p][i];
                final List<FullLocation> newPositionsComp = new ArrayList<>(otherPositionsComp.size());
                for (FullLocation fullLocation : otherPositionsComp) {
                    newPositionsComp.add((FullLocation) fullLocation.copy());
                }
                this.locations[p][i] = newPositionsComp;
            }
        }
    }
    
    @Override
    public Owned copy() {
        return new FullOwned(this);
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
        final TIntArrayList levels = new TIntArrayList();
        final int compIndex = this.indexMapper.compIndex(playerId, componentId);
        if (compIndex >= 0) {
            final List<FullLocation> locs = this.locations[playerId][compIndex];
            for (final Location pos : locs) {
                if (pos.site() == site) {
                    levels.add(pos.level());
                }
            }
        }
        return levels;
    }
    
    @Override
    public TIntArrayList sites(final int playerId, final int componentId) {
        final TIntArrayList sites = new TIntArrayList();
        final int compIndex = this.indexMapper.compIndex(playerId, componentId);
        if (compIndex >= 0) {
            final List<FullLocation> locs = this.locations[playerId][compIndex];
            for (final Location loc : locs) {
                if (!sites.contains(loc.site())) {
                    sites.add(loc.site());
                }
            }
        }
        return sites;
    }
    
    @Override
    public TIntArrayList sites(final int playerId) {
        final TIntArrayList sites = new TIntArrayList();
        for (int i = 0; i < this.locations[playerId].length; ++i) {
            final List<FullLocation> locs = this.locations[playerId][i];
            for (final FullLocation loc : locs) {
                if (!sites.contains(loc.site())) {
                    sites.add(loc.site());
                }
            }
        }
        return sites;
    }
    
    @Override
    public TIntArrayList sitesOnTop(final int playerId) {
        final TIntArrayList sites = new TIntArrayList();
        for (int i = 0; i < this.locations[playerId].length; ++i) {
            final List<FullLocation> locs = this.locations[playerId][i];
            for (final FullLocation loc : locs) {
                if (!sites.contains(loc.site())) {
                    sites.add(loc.site());
                }
            }
        }
        return sites;
    }
    
    @Override
    public List<FullLocation> positions(final int playerId, final int componentId) {
        return this.locations[playerId][this.indexMapper.compIndex(playerId, componentId)];
    }
    
    @Override
    public List<FullLocation>[] positions(final int playerId) {
        return this.locations[playerId];
    }
    
    @Override
    public void remove(final int playerId, final int componentId, final int pieceLoc) {
        final List<FullLocation> compPositions = this.locations[playerId][this.indexMapper.compIndex(playerId, componentId)];
        int i = 0;
        while (i < compPositions.size()) {
            if (compPositions.get(i).site() == pieceLoc) {
                compPositions.remove(i);
            }
            else {
                ++i;
            }
        }
    }
    
    @Override
    public void remove(final int playerId, final int componentId, final int pieceLoc, final int level) {
        final List<FullLocation> locs = this.locations[playerId][this.indexMapper.compIndex(playerId, componentId)];
        for (int i = 0; i < locs.size(); ++i) {
            if (locs.get(i).site() == pieceLoc && locs.get(i).level() == level) {
                locs.remove(i);
                --i;
            }
        }
        for (int idPlayer = 1; idPlayer < this.locations.length; ++idPlayer) {
            for (int j = 0; j < this.locations[idPlayer].length; ++j) {
                for (int idPos = 0; idPos < this.locations[idPlayer][j].size(); ++idPos) {
                    final int sitePos = this.locations[idPlayer][j].get(idPos).site();
                    final int levelPos = this.locations[idPlayer][j].get(idPos).level();
                    if (sitePos == pieceLoc && levelPos > level) {
                        this.locations[idPlayer][j].get(idPos).decrementLevel();
                    }
                }
            }
        }
    }
    
    @Override
    public void add(final int playerId, final int componentId, final int pieceLoc, final SiteType type) {
        this.locations[playerId][this.indexMapper.compIndex(playerId, componentId)].add(new FullLocation(pieceLoc, type));
    }
    
    @Override
    public void add(final int playerId, final int componentId, final int pieceLoc, final int level, final SiteType type) {
        this.locations[playerId][this.indexMapper.compIndex(playerId, componentId)].add(new FullLocation(pieceLoc, level, type));
    }
}
