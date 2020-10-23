// 
// Decompiled by Procyon v0.5.36
// 

package search.mcts.playout;

import game.Game;
import org.json.JSONObject;
import policies.GreedyPolicy;
import policies.softmax.SoftmaxPolicy;
import util.Context;
import util.Trial;

import java.util.Arrays;

public interface PlayoutStrategy
{
    Trial runPlayout(final Context p0);
    
    boolean playoutSupportsGame(final Game p0);
    
    void customise(final String[] p0);
    
    static PlayoutStrategy fromJson(final JSONObject json) {
        final PlayoutStrategy playout = null;
        final String strategy = json.getString("strategy");
        if (strategy.equalsIgnoreCase("Random")) {
            return new RandomPlayout(200);
        }
        return playout;
    }
    
    public static PlayoutStrategy constructPlayoutStrategy(final String[] inputs) {
        PlayoutStrategy playout = null;
        if (inputs[0].endsWith("random") || inputs[0].endsWith("randomplayout")) {
            playout = new RandomPlayout();
            playout.customise(inputs);
        }
        else if (inputs[0].endsWith("softmax") || inputs[0].endsWith("softmaxplayout")) {
            playout = new SoftmaxPolicy();
            playout.customise(inputs);
        }
        else if (inputs[0].endsWith("greedy")) {
            playout = new GreedyPolicy();
            playout.customise(inputs);
        }
        else {
            System.err.println("Unknown play-out strategy: " + Arrays.toString(inputs));
        }
        return playout;
    }
}
