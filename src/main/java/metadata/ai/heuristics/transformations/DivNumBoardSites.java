// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.heuristics.transformations;

import util.Context;

public class DivNumBoardSites implements HeuristicTransformation
{
    @Override
    public float transform(final Context context, final float heuristicScore) {
        return heuristicScore / context.game().board().numSites();
    }
    
    @Override
    public String toString() {
        return "(divNumBoardSites)";
    }
}
