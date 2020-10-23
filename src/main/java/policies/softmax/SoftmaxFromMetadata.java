// 
// Decompiled by Procyon v0.5.36
// 

package policies.softmax;

import features.FeatureSet;
import function_approx.LinearFunction;
import game.Game;
import game.types.play.RoleType;
import metadata.ai.features.Features;

import java.util.ArrayList;
import java.util.List;

public class SoftmaxFromMetadata extends SoftmaxPolicy
{
    public SoftmaxFromMetadata() {
        this.friendlyName = "Softmax Policy (features from Game metadata)";
    }
    
    @Override
    public void initAI(final Game game, final int playerID) {
        final List<FeatureSet> featureSetsList = new ArrayList<>();
        final List<LinearFunction> linFuncs = new ArrayList<>();
        final Features featuresMetadata = game.metadata().ai().features();
        for (final metadata.ai.features.FeatureSet featureSet : featuresMetadata.featureSets()) {
            if (featureSet.role() == RoleType.Shared) {
                SoftmaxPolicy.addFeatureSetWeights(0, featureSet.featureStrings(), featureSet.featureWeights(), featureSetsList, linFuncs);
            }
            else {
                SoftmaxPolicy.addFeatureSetWeights(featureSet.role().owner(), featureSet.featureStrings(), featureSet.featureWeights(), featureSetsList, linFuncs);
            }
        }
        this.featureSets = featureSetsList.toArray(new FeatureSet[0]);
        this.linearFunctions = linFuncs.toArray(new LinearFunction[0]);
        this.playoutActionLimit = 200;
        super.initAI(game, playerID);
    }
    
    @Override
    public boolean supportsGame(final Game game) {
        return game.metadata().ai() != null && game.metadata().ai().features() != null;
    }
}
