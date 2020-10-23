// 
// Decompiled by Procyon v0.5.36
// 

package policies;

import main.collections.FVector;
import main.collections.FastArrayList;
import search.mcts.playout.PlayoutStrategy;
import util.AI;
import util.Context;
import util.Move;

public abstract class Policy extends AI implements PlayoutStrategy
{
    public abstract FVector computeDistribution(final Context p0, final FastArrayList<Move> p1, final boolean p2);
    
    public abstract float computeLogit(final Context p0, final Move p1);
}
