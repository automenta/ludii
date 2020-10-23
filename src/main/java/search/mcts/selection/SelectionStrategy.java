// 
// Decompiled by Procyon v0.5.36
// 

package search.mcts.selection;

import org.json.JSONObject;
import search.mcts.nodes.BaseNode;

public interface SelectionStrategy
{
    int select(final BaseNode p0);
    
    int backpropFlags();
    
    void customise(final String[] p0);
    
    static SelectionStrategy fromJson(final JSONObject json) {
        final SelectionStrategy selection = null;
        final String strategy = json.getString("strategy");
        if (strategy.equalsIgnoreCase("UCB1")) {
            return new UCB1();
        }
        return selection;
    }
}
