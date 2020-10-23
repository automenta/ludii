// 
// Decompiled by Procyon v0.5.36
// 

package util;

import collections.FVector;
import collections.FastArrayList;
import gnu.trove.list.array.TIntArrayList;
import util.state.State;

import java.util.List;

public abstract class FeatureSetInterface
{
    public List<TIntArrayList> computeSparseFeatureVectors(final Context context, final FastArrayList<Move> actions, final boolean thresholded) {
        return this.computeSparseFeatureVectors(context.state(), context.trial().lastMove(), actions, thresholded);
    }
    
    public abstract List<TIntArrayList> computeSparseFeatureVectors(final State state, final Move lastDecisionMove, final FastArrayList<Move> actions, final boolean thresholded);
    
    public abstract TIntArrayList getActiveFeatureIndices(final State state, final int lastFrom, final int lastTo, final int from, final int to, final int player, final boolean thresholded);
    
    public abstract float computeLogitFastReturn(final State state, final int lastFrom, final int lastTo, final int from, final int to, final float autoPlayThreshold, final FVector weightVector, final int player, final boolean thresholded);
}
