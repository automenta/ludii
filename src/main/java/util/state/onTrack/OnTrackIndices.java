// 
// Decompiled by Procyon v0.5.36
// 

package util.state.onTrack;

import game.equipment.container.board.Track;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

public class OnTrackIndices
{
    protected final List<TIntArrayList>[] onTrackIndices;
    protected final TIntObjectMap<TIntArrayList>[] locToIndex;
    
    public OnTrackIndices(final List<Track> tracks, final int numWhat) {
        this.onTrackIndices = (List<TIntArrayList>[])new List[tracks.size()];
        this.locToIndex = (TIntObjectMap<TIntArrayList>[])new TIntObjectMap[tracks.size()];
        for (int trackIdx = 0; trackIdx < tracks.size(); ++trackIdx) {
            final Track track = tracks.get(trackIdx);
            final int size = track.elems().length;
            final List<TIntArrayList> onTracks = new ArrayList<>();
            for (int i = 0; i < numWhat; ++i) {
                final TIntArrayList indicesTrack = new TIntArrayList();
                for (int j = 0; j < size; ++j) {
                    indicesTrack.add(0);
                }
                onTracks.add(indicesTrack);
            }
            this.onTrackIndices[trackIdx] = onTracks;
            final TIntObjectMap<TIntArrayList> locToIndexTrack = new TIntObjectHashMap<>();
            for (int k = 0; k < size; ++k) {
                final int site = track.elems()[k].site;
                if (locToIndexTrack.get(site) == null) {
                    locToIndexTrack.put(site, new TIntArrayList());
                }
                locToIndexTrack.get(site).add(k);
            }
            this.locToIndex[trackIdx] = locToIndexTrack;
        }
    }
    
    public OnTrackIndices(final OnTrackIndices other) {
        final List<TIntArrayList>[] otherOnTrackIndices = other.onTrackIndices;
        this.onTrackIndices = (List<TIntArrayList>[])new List[otherOnTrackIndices.length];
        for (int i = 0; i < otherOnTrackIndices.length; ++i) {
            final List<TIntArrayList> otherOnTracks = otherOnTrackIndices[i];
            final List<TIntArrayList> onTracks = new ArrayList<>(otherOnTracks.size());
            for (final TIntArrayList whatOtherOnTrack : otherOnTracks) {
                onTracks.add(new TIntArrayList(whatOtherOnTrack));
            }
            this.onTrackIndices[i] = onTracks;
        }
        this.locToIndex = other.locToIndex;
    }
    
    protected OnTrackIndices(final List<TIntArrayList>[] onTrackIndices, final TIntObjectMap<TIntArrayList>[] locToIndex) {
        this.onTrackIndices = onTrackIndices;
        this.locToIndex = locToIndex;
    }
    
    public List<TIntArrayList> whats(final int trackIdx) {
        return this.onTrackIndices[trackIdx];
    }
    
    public TIntArrayList whats(final int trackIdx, final int what) {
        return this.onTrackIndices[trackIdx].get(what);
    }
    
    public int whats(final int trackIdx, final int what, final int index) {
        return this.onTrackIndices[trackIdx].get(what).getQuick(index);
    }
    
    public void add(final int trackIdx, final int what, final int count, final int index) {
        final int currentCount = this.onTrackIndices[trackIdx].get(what).getQuick(index);
        this.onTrackIndices[trackIdx].get(what).setQuick(index, currentCount + count);
    }
    
    public void remove(final int trackIdx, final int what, final int count, final int index) {
        final int currentCount = this.onTrackIndices[trackIdx].get(what).getQuick(index);
        this.onTrackIndices[trackIdx].get(what).setQuick(index, currentCount - count);
    }
    
    public TIntArrayList indicesWithWhat(final int trackIdx, final int what) {
        final TIntArrayList indicesWithThatComponent = new TIntArrayList();
        final TIntArrayList indicesOnTrack = this.onTrackIndices[trackIdx].get(what);
        for (int i = 0; i < indicesOnTrack.size(); ++i) {
            if (indicesOnTrack.getQuick(i) != 0) {
                indicesWithThatComponent.add(i);
            }
        }
        return indicesWithThatComponent;
    }
    
    public TIntObjectMap<TIntArrayList> locToIndex(final int trackIdx) {
        return this.locToIndex[trackIdx];
    }
    
    public TIntArrayList locToIndex(final int trackIdx, final int site) {
        final TIntArrayList indices = this.locToIndex[trackIdx].get(site);
        if (indices == null) {
            return new TIntArrayList();
        }
        return indices;
    }
    
    public TIntArrayList locToIndexFrom(final int trackIdx, final int site, final int from) {
        final TIntArrayList indices = this.locToIndex[trackIdx].get(site);
        if (indices == null) {
            return new TIntArrayList();
        }
        final TIntArrayList indicesToReturn = new TIntArrayList();
        for (int i = 0; i < indices.size(); ++i) {
            if (indices.get(i) > from) {
                indicesToReturn.add(indices.get(i));
            }
        }
        return indicesToReturn;
    }
    
    @Override
    public String toString() {
        String str = "OnTrackIndices:\n";
        for (int i = 0; i < this.onTrackIndices.length; ++i) {
            str = str + "Track: " + i + "\n";
            final List<TIntArrayList> whatOnTracks = this.onTrackIndices[i];
            for (int what = 0; what < whatOnTracks.size(); ++what) {
                final TIntArrayList onTracks = whatOnTracks.get(what);
                for (int j = 0; j < onTracks.size(); ++j) {
                    if (onTracks.get(j) > 0) {
                        str = str + "Component " + what + " at index " + i + " count = " + onTracks.get(j) + "\n";
                    }
                }
            }
            str += "\n";
        }
        return str;
    }
}
