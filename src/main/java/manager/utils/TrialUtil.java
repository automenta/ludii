// 
// Decompiled by Procyon v0.5.36
// 

package manager.utils;

import manager.Manager;
import util.Context;

public class TrialUtil
{
    public static int getInstanceStartIndex(final Context context) {
        final int numInitialPlacementMoves = context.currentInstanceContext().trial().numInitialPlacementMoves();
        final int startIndex = context.trial().numMoves() - context.currentInstanceContext().trial().numMoves() + numInitialPlacementMoves;
        return startIndex;
    }
    
    public static int getInstanceEndIndex(final Context context) {
        if (Manager.savedTrial() == null) {
            return context.trial().numMoves();
        }
        if (context.isAMatch()) {
            int endOfInstance;
            for (endOfInstance = context.trial().moves().size(); endOfInstance < Manager.savedTrial().moves().size() && !Manager.savedTrial().moves().get(endOfInstance).containsNextInstance(); ++endOfInstance) {}
            return endOfInstance;
        }
        return Manager.savedTrial().numMoves();
    }
}
