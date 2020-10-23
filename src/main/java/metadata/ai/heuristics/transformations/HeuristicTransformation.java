// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.heuristics.transformations;

import metadata.ai.AIItem;
import util.Context;

public interface HeuristicTransformation extends AIItem
{
    float transform(final Context context, final float heuristicScore);
}
