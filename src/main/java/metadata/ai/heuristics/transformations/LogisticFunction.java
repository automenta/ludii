// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.heuristics.transformations;

import util.Context;

public class LogisticFunction implements HeuristicTransformation
{
    @Override
    public float transform(final Context context, final float heuristicScore) {
        return (float)(1.0 / (1.0 + Math.exp(heuristicScore)));
    }
    
    @Override
    public String toString() {
        return "(logisticFunction)";
    }
}
