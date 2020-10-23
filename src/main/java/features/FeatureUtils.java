// 
// Decompiled by Procyon v0.5.36
// 

package features;

import util.Move;

public class FeatureUtils
{
    private FeatureUtils() {
    }
    
    public static int fromPos(final Move move) {
        if (move == null || move.isPass()) {
            return -1;
        }
        int fromPos = move.fromNonDecision();
        if (fromPos == toPos(move)) {
            fromPos = -1;
        }
        return fromPos;
    }
    
    public static int toPos(final Move move) {
        if (move == null || move.isPass()) {
            return -1;
        }
        return move.toNonDecision();
    }
}
