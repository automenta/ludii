// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.heuristics.transformations;

import util.Context;

public class Tanh implements HeuristicTransformation
{
    @Override
    public float transform(final Context context, final float heuristicScore) {
        return (float)Math.tanh(heuristicScore);
    }
    
    @Override
    public String toString() {
        return "(tanh)";
    }
}
