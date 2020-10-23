// 
// Decompiled by Procyon v0.5.36
// 

package game;

import collections.FVector;
import game.rules.play.moves.Moves;
import util.*;

import java.util.List;
import java.util.Random;

public interface API
{
    void create();
    
    void start(final Context context);
    
    Moves moves(final Context context);
    
    Move apply(final Context context, final Move move);
    
    Trial playout(final Context context, final List<AI> ais, final double thinkingTime, final FeatureSetInterface[] featureSets, final FVector[] weights, final int maxNumBiasedActions, final int maxNumPlayoutActions, final float autoPlayThreshold, final Random random);
}
