// 
// Decompiled by Procyon v0.5.36
// 

package distance;

import game.Game;
import main.StringRoutines;

import java.util.List;

public class Levenshtein implements DistanceMetric
{
    @Override
    public Score distance(final Game gameA, final Game gameB) {
        final int edits = StringRoutines.levenshteinDistance(gameA.description().expanded(), gameB.description().expanded());
        final int maxLength = Math.max(gameA.description().expanded().length(), gameB.description().expanded().length());
        final double score = edits / (double)maxLength;
        return new Score(score);
    }
    
    @Override
    public Score distance(final Game gameA, final List<Game> gameB, final int numberTrials, final int maxTurns, final double thinkTime, final String AIName) {
        return this.distance(gameA, gameB.get(0));
    }
}
