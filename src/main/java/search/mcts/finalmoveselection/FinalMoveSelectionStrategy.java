// 
// Decompiled by Procyon v0.5.36
// 

package search.mcts.finalmoveselection;

import org.json.JSONObject;
import search.mcts.nodes.BaseNode;
import util.Move;

public interface FinalMoveSelectionStrategy
{
    Move selectMove(final BaseNode p0);
    
    void customize(final String[] p0);
    
    static FinalMoveSelectionStrategy fromJson(final JSONObject json) {
        final FinalMoveSelectionStrategy selection = null;
        final String strategy = json.getString("strategy");
        if (strategy.equalsIgnoreCase("RobustChild")) {
            return new RobustChild();
        }
        return selection;
    }
}
