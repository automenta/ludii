// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.heuristics.terms;

import collections.FVector;
import game.Game;
import game.equipment.component.Component;
import metadata.ai.AIItem;
import metadata.ai.heuristics.transformations.HeuristicTransformation;
import util.Context;

public abstract class HeuristicTerm implements AIItem
{
    protected float weight;
    protected final HeuristicTransformation transformation;
    
    public HeuristicTerm(final HeuristicTransformation transformation, final Float weight) {
        if (weight == null) {
            this.weight = 1.0f;
        }
        else {
            this.weight = weight;
        }
        this.transformation = transformation;
    }
    
    public abstract float computeValue(final Context context, final int player, final float absWeightThreshold);
    
    public void init(final Game game) {
    }
    
    public abstract FVector computeStateFeatureVector(final Context context, final int player);
    
    public abstract FVector paramsVector();
    
    public int updateParams(final Game game, final FVector newParams, final int startIdx) {
        final FVector internalParams = this.paramsVector();
        if (internalParams == null) {
            this.weight = newParams.get(startIdx);
            return startIdx + 1;
        }
        internalParams.copyFrom(newParams, startIdx, 0, internalParams.dim());
        this.weight = 1.0f;
        return startIdx + internalParams.dim();
    }
    
    public HeuristicTransformation transformation() {
        return this.transformation;
    }
    
    public float weight() {
        return this.weight;
    }
    
    protected static FVector pieceWeightsVector(final Game game, final String[] pieceWeightNames, final float[] gameAgnosticWeightsArray) {
        final Component[] components = game.equipment().components();
        final FVector pieceWeights = new FVector(components.length);
        for (int nameIdx = 0; nameIdx < pieceWeightNames.length; ++nameIdx) {
            final String s = pieceWeightNames[nameIdx].trim();
            for (int i = 1; i < components.length; ++i) {
                final String compName = components[i].name();
                if (compName.startsWith(s)) {
                    boolean match = true;
                    for (int j = s.length(); j < compName.length(); ++j) {
                        if (!Character.isDigit(compName.charAt(j))) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        pieceWeights.set(i, gameAgnosticWeightsArray[nameIdx]);
                    }
                }
            }
        }
        return pieceWeights;
    }
    
    protected static void updateGameAgnosticWeights(final Game game, final FVector pieceWeights, final String[] pieceWeightNames, final float[] gameAgnosticWeightsArray) {
        final Component[] components = game.equipment().components();
        for (int nameIdx = 0; nameIdx < pieceWeightNames.length; ++nameIdx) {
            final String s = pieceWeightNames[nameIdx].trim();
            for (int i = 1; i < components.length; ++i) {
                final String compName = components[i].name();
                if (compName.startsWith(s)) {
                    boolean match = true;
                    for (int j = s.length(); j < compName.length(); ++j) {
                        if (!Character.isDigit(compName.charAt(j))) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        gameAgnosticWeightsArray[nameIdx] = pieceWeights.get(i);
                    }
                }
            }
        }
    }
    
    public abstract String toStringThresholded(final float threshold);
}
