// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.heuristics.transformations;

import util.Context;

public class DivNumInitPlacement implements HeuristicTransformation
{
    @Override
    public float transform(final Context context, final float heuristicScore) {
        final int numInitPlacement = Math.max(1, context.trial().numInitPlacement());
        return heuristicScore / numInitPlacement;
    }
    
    @Override
    public String toString() {
        return "(divNumInitPlacement)";
    }
}
