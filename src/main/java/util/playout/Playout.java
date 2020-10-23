// 
// Decompiled by Procyon v0.5.36
// 

package util.playout;

import collections.FVector;
import util.AI;
import util.Context;
import util.FeatureSetInterface;
import util.Trial;

import java.util.List;
import java.util.Random;

public abstract class Playout
{
    public abstract Trial playout(final Context context, final List<AI> ais, final double thinkingTime, final FeatureSetInterface[] featureSets, final FVector[] weights, final int maxNumBiasedActions, final int maxNumPlayoutActions, final float autoPlayThreshold, final Random random);
    
    public abstract boolean callsGameMoves();
}
