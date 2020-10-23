// 
// Decompiled by Procyon v0.5.36
// 

package metrics.evaluation.playability;

import game.Game;
import metrics.Metric;
import util.Trial;

public class Duration extends Metric
{
    private double minTurn;
    private double maxTurn;
    
    public Duration() {
        super("Duration", "Average number or turns in a game.", "Core Ludii metric.", MetricType.OUTCOMES, 0.0, 1.0);
        this.minTurn = 0.0;
        this.maxTurn = 1000.0;
    }
    
    @Override
    public double apply(final Game game, final String args, final Trial... trials) {
        if (trials.length == 0) {
            return 0.0;
        }
        double tally = 0.0;
        for (final Trial trial : trials) {
            final int numTurns = trial.numberOfTurns();
            double score = 1.0;
            if (numTurns < this.minTurn) {
                score = numTurns / this.minTurn;
            }
            else if (numTurns > this.maxTurn) {
                score = 1.0 - Math.min(1.0, (numTurns - this.maxTurn) / this.maxTurn);
            }
            tally += score;
        }
        return tally / trials.length;
    }
    
    public void setMinTurn(final double minTurn) {
        this.minTurn = minTurn;
    }
    
    public void setMaxTurn(final double maxTurn) {
        this.maxTurn = maxTurn;
    }
}
