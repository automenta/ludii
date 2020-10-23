// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.heuristics.terms;

import annotations.Name;
import annotations.Opt;
import game.Game;
import main.collections.FVector;
import metadata.ai.heuristics.transformations.HeuristicTransformation;
import util.Context;

public class MobilitySimple extends HeuristicTerm
{
    public MobilitySimple(@Name @Opt final HeuristicTransformation transformation, @Name @Opt final Float weight) {
        super(transformation, weight);
    }
    
    @Override
    public float computeValue(final Context context, final int player, final float absWeightThreshold) {
        if (context.state().mover() == player) {
            return (float)context.game().moves(context).count();
        }
        return 0.0f;
    }
    
    @Override
    public FVector computeStateFeatureVector(final Context context, final int player) {
        final FVector featureVector = new FVector(1);
        featureVector.set(0, this.computeValue(context, player, -1.0f));
        return featureVector;
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
        sb.append("(mobilitySimple");
        if (this.transformation != null) {
            sb.append(" transformation:" + this.transformation.toString());
        }
        if (this.weight != 1.0f) {
            sb.append(" weight:" + this.weight);
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
            sb.append("(mobilitySimple");
            if (this.transformation != null) {
                sb.append(" transformation:" + this.transformation.toString());
            }
            if (this.weight != 1.0f) {
                sb.append(" weight:" + this.weight);
            }
            sb.append(")");
            return sb.toString();
        }
        return null;
    }
}
