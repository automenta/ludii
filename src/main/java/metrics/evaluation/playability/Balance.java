// 
// Decompiled by Procyon v0.5.36
// 

package metrics.evaluation.playability;

import game.Game;
import main.Status;
import metrics.Metric;
import util.Trial;

public class Balance extends Metric
{
    public Balance() {
        super("Balance", "Similarity between player win rates.", "Core Ludii metric.", MetricType.OUTCOMES, 0.0, 1.0);
    }
    
    @Override
    public double apply(final Game game, final String args, final Trial... trials) {
        final int numPlayers = game.players().count();
        final int[] wins = new int[numPlayers + 1];
        for (final Trial trial : trials) {
            final Status result = trial.status();
            final int[] array = wins;
            final int winner = result.winner();
            ++array[winner];
        }
        final double[] rate = new double[numPlayers + 1];
        for (int p = 1; p <= numPlayers; ++p) {
            rate[p] = wins[p] / (double)trials.length;
        }
        double maxDisc = 0.0;
        for (int pa = 1; pa <= numPlayers; ++pa) {
            for (int pb = pa + 1; pb <= numPlayers; ++pb) {
                final double disc = Math.abs(rate[pa] - rate[pb]);
                if (disc > maxDisc) {
                    maxDisc = disc;
                }
            }
        }
        return 1.0 - maxDisc;
    }
}
