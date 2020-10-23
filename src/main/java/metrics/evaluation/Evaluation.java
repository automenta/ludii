// 
// Decompiled by Procyon v0.5.36
// 

package metrics.evaluation;

import game.Game;
import metrics.Metric;
import metrics.evaluation.playability.Balance;
import metrics.evaluation.playability.Decisiveness;
import metrics.evaluation.playability.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Evaluation
{
    private final Game game;
    private final List<Metric> metrics;
    
    public Evaluation(final Game game) {
        (this.metrics = new ArrayList<>()).add(new Balance());
        this.metrics.add(new Decisiveness());
        this.metrics.add(new Duration());
        this.game = game;
    }
    
    public List<Metric> metrics() {
        return Collections.unmodifiableList(this.metrics);
    }
    
    public static String report() {
        final StringBuilder sb = new StringBuilder();
        return sb.toString();
    }
}
