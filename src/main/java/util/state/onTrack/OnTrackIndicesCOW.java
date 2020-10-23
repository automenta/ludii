// 
// Decompiled by Procyon v0.5.36
// 

package util.state.onTrack;

import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnTrackIndicesCOW extends OnTrackIndices
{
    private final boolean[] copiedOnTrackIndices;
    
    public OnTrackIndicesCOW(final OnTrackIndices other) {
        super(Arrays.copyOf(other.onTrackIndices, other.onTrackIndices.length), other.locToIndex);
        this.copiedOnTrackIndices = new boolean[this.onTrackIndices.length];
    }
    
    @Override
    public void add(final int trackIdx, final int what, final int count, final int index) {
        this.ensureDeepCopy(trackIdx);
        super.add(trackIdx, what, count, index);
    }
    
    @Override
    public void remove(final int trackIdx, final int what, final int count, final int index) {
        this.ensureDeepCopy(trackIdx);
        super.remove(trackIdx, what, count, index);
    }
    
    public void ensureDeepCopy(final int trackIdx) {
        if (!this.copiedOnTrackIndices[trackIdx]) {
            final List<TIntArrayList> otherOnTracks = this.onTrackIndices[trackIdx];
            final List<TIntArrayList> onTracks = new ArrayList<>(otherOnTracks.size());
            for (final TIntArrayList whatOtherOnTrack : otherOnTracks) {
                onTracks.add(new TIntArrayList(whatOtherOnTrack));
            }
            this.onTrackIndices[trackIdx] = onTracks;
            this.copiedOnTrackIndices[trackIdx] = true;
        }
    }
}
