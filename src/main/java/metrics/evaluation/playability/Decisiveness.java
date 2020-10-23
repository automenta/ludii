// 
// Decompiled by Procyon v0.5.36
// 

package metrics.evaluation.playability;

import game.Game;
import main.Status;
import metrics.Metric;
import util.Trial;

public class Decisiveness extends Metric
{
    public Decisiveness() {
        super("Decisiveness", "Tendency for games to not end in a draw.", "Core Ludii metric.", MetricType.OUTCOMES, 0.0, 1.0);
    }
    
    @Override
    public double apply(final Game game, final String args, final Trial... trials) {
        if (trials.length == 0) {
            return 0.0;
        }
        int draws = 0;
        for (final Trial trial : trials) {
            final Status result = trial.status();
            if (result.winner() == 0) {
                ++draws;
            }
        }
        final double completion = 1.0 - draws / (double)trials.length;
        return completion;
    }
}
