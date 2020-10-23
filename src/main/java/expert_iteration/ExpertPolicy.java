// 
// Decompiled by Procyon v0.5.36
// 

package expert_iteration;

import collections.FVector;
import collections.FastArrayList;
import util.AI;
import util.Move;

public abstract class ExpertPolicy extends AI
{
    public abstract FastArrayList<Move> lastSearchRootMoves();
    
    public abstract FVector computeExpertPolicy(final double p0);
    
    public abstract ExItExperience generateExItExperience();
}
