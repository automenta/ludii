// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.heuristics.terms;

import annotations.Name;
import annotations.Opt;
import collections.FVector;
import game.Game;
import metadata.ai.heuristics.transformations.HeuristicTransformation;
import util.Context;

public class CurrentMoverHeuristic extends HeuristicTerm
{
    public CurrentMoverHeuristic(@Name @Opt final HeuristicTransformation transformation, @Name @Opt final Float weight) {
        super(transformation, weight);
    }
    
    @Override
    public float computeValue(final Context context, final int player, final float absWeightThreshold) {
        if (context.state().mover() == player) {
            return 1.0f;
        }
        return 0.0f;
    }
    
    @Override
    public FVector computeStateFeatureVector(final Context context, final int player) {
        if (context.state().mover() == player) {
            return FVector.ones(1);
        }
        return FVector.zeros(1);
    }
    
    @Override
    public FVector paramsVector() {
        return null;
    }
    
    public static boolean isApplicableToGame(final Game game) {
        return game.isAlternatingMoveGame();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(currentMoverHeuristic");
        if (this.transformation != null) {
            sb.append(" transformation:").append(this.transformation.toString());
        }
        if (this.weight != 1.0f) {
            sb.append(" weight:").append(this.weight);
        }
        sb.append(")");
        return sb.toString();
    }
    
    @Override
    public String toStringThresholded(final float threshold) {
        boolean shouldPrint = false;
        if (Math.abs(this.weight) >= threshold) {
            shouldPrint = true;
        }
        if (shouldPrint) {
            final StringBuilder sb = new StringBuilder();
            sb.append("(currentMoverHeuristic");
            if (this.transformation != null) {
                sb.append(" transformation:").append(this.transformation.toString());
            }
            if (this.weight != 1.0f) {
                sb.append(" weight:").append(this.weight);
            }
            sb.append(")");
            return sb.toString();
        }
        return null;
    }
}
