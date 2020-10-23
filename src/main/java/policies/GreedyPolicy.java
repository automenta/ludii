// 
// Decompiled by Procyon v0.5.36
// 

package policies;

import collections.FVector;
import collections.FastArrayList;
import features.FeatureSet;
import function_approx.BoostedLinearFunction;
import function_approx.LinearFunction;
import game.Game;
import game.rules.play.moves.Moves;
import gnu.trove.list.array.TIntArrayList;
import util.Context;
import util.Move;
import util.Trial;
import utils.ExperimentFileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GreedyPolicy extends Policy
{
    protected LinearFunction[] linearFunctions;
    protected FeatureSet[] featureSets;
    protected int playoutTurnLimit;
    
    public GreedyPolicy() {
        this.playoutTurnLimit = 200;
        this.linearFunctions = null;
        this.featureSets = null;
    }
    
    public GreedyPolicy(final LinearFunction[] linearFunctions, final FeatureSet[] featureSets) {
        this.playoutTurnLimit = 200;
        this.linearFunctions = linearFunctions;
        this.featureSets = featureSets;
    }
    
    @Override
    public FVector computeDistribution(final Context context, final FastArrayList<Move> actions, final boolean thresholded) {
        FeatureSet featureSet;
        if (this.featureSets.length == 1) {
            featureSet = this.featureSets[0];
        }
        else {
            featureSet = this.featureSets[context.state().mover()];
        }
        return this.computeDistribution(featureSet.computeSparseFeatureVectors(context, actions, thresholded), context.state().mover());
    }
    
    @Override
    public float computeLogit(final Context context, final Move move) {
        FeatureSet featureSet;
        if (this.featureSets.length == 1) {
            featureSet = this.featureSets[0];
        }
        else {
            featureSet = this.featureSets[context.state().mover()];
        }
        LinearFunction linearFunction;
        if (this.linearFunctions.length == 1) {
            linearFunction = this.linearFunctions[0];
        }
        else {
            linearFunction = this.linearFunctions[context.state().mover()];
        }
        final FastArrayList<Move> wrappedMove = new FastArrayList<>(1);
        wrappedMove.add(move);
        return linearFunction.predict(featureSet.computeSparseFeatureVectors(context, wrappedMove, true).get(0));
    }
    
    public float[] computeLogits(final List<TIntArrayList> sparseFeatureVectors, final int player) {
        final float[] logits = new float[sparseFeatureVectors.size()];
        LinearFunction linearFunction;
        if (this.linearFunctions.length == 1) {
            linearFunction = this.linearFunctions[0];
        }
        else {
            linearFunction = this.linearFunctions[player];
        }
        for (int i = 0; i < sparseFeatureVectors.size(); ++i) {
            logits[i] = linearFunction.predict(sparseFeatureVectors.get(i));
        }
        return logits;
    }
    
    public FVector computeDistribution(final List<TIntArrayList> sparseFeatureVectors, final int player) {
        final float[] logits = this.computeLogits(sparseFeatureVectors, player);
        float maxLogit = Float.NEGATIVE_INFINITY;
        final TIntArrayList maxLogitIndices = new TIntArrayList();
        for (int i = 0; i < logits.length; ++i) {
            final float logit = logits[i];
            if (logit > maxLogit) {
                maxLogit = logit;
                maxLogitIndices.reset();
                maxLogitIndices.add(i);
            }
            else if (logit == maxLogit) {
                maxLogitIndices.add(i);
            }
        }
        final float maxProb = 1.0f / maxLogitIndices.size();
        final FVector distribution = new FVector(logits.length);
        for (int j = 0; j < maxLogitIndices.size(); ++j) {
            distribution.set(maxLogitIndices.getQuick(j), maxProb);
        }
        return distribution;
    }
    
    @Override
    public Trial runPlayout(final Context context) {
        final FVector[] params = new FVector[this.linearFunctions.length];
        for (int i = 0; i < this.linearFunctions.length; ++i) {
            if (this.linearFunctions[i] == null) {
                params[i] = null;
            }
            else {
                params[i] = this.linearFunctions[i].effectiveParams();
            }
        }
        return context.game().playout(context, null, 1.0, this.featureSets, params, -1, this.playoutTurnLimit, -1.0f, ThreadLocalRandom.current());
    }
    
    @Override
    public boolean playoutSupportsGame(final Game game) {
        return this.supportsGame(game);
    }
    
    @Override
    public void customise(final String[] inputs) {
        String policyWeightsFilepath = null;
        boolean boosted = false;
        for (int i = 1; i < inputs.length; ++i) {
            final String input = inputs[i];
            if (input.toLowerCase().startsWith("policyweights=")) {
                policyWeightsFilepath = input.substring("policyweights=".length());
            }
            else if (input.toLowerCase().startsWith("playoutturnlimit=")) {
                this.playoutTurnLimit = Integer.parseInt(input.substring("playoutturnlimit=".length()));
            }
            else if (input.toLowerCase().startsWith("friendly_name=")) {
                this.friendlyName = input.substring("friendly_name=".length());
            }
            else if (input.toLowerCase().startsWith("boosted=") && input.toLowerCase().endsWith("true")) {
                boosted = true;
            }
        }
        if (policyWeightsFilepath != null) {
            final String parentDir = new File(policyWeightsFilepath).getParent();
            if (!new File(policyWeightsFilepath).exists()) {
                policyWeightsFilepath = ExperimentFileUtils.getLastFilepath(parentDir + "/PolicyWeights", "txt");
            }
            if (boosted) {
                this.linearFunctions = new LinearFunction[] { BoostedLinearFunction.boostedFromFile(policyWeightsFilepath, null) };
            }
            else {
                this.linearFunctions = new LinearFunction[] { LinearFunction.fromFile(policyWeightsFilepath) };
            }
            this.featureSets = new FeatureSet[this.linearFunctions.length];
            for (int j = 0; j < this.linearFunctions.length; ++j) {
                if (this.linearFunctions[j] != null) {
                    this.featureSets[j] = new FeatureSet(parentDir + File.separator + this.linearFunctions[j].featureSetFile());
                }
            }
        }
        else {
            System.err.println("Cannot construct Greedy Policy from: " + Arrays.toString(inputs));
        }
    }
    
    @Override
    public Move selectAction(final Game game, final Context context, final double maxSeconds, final int maxIterations, final int maxDepth) {
        final Moves actions = game.moves(context);
        FeatureSet featureSet;
        if (this.featureSets.length == 1) {
            featureSet = this.featureSets[0];
        }
        else {
            featureSet = this.featureSets[context.state().mover()];
        }
        return actions.moves().get(FVector.wrap(this.computeLogits(featureSet.computeSparseFeatureVectors(context, actions.moves(), true), context.state().mover())).argMaxRand());
    }
    
    public static GreedyPolicy fromLines(final String[] lines) {
        final GreedyPolicy policy = new GreedyPolicy();
        policy.customise(lines);
        return policy;
    }
    
    @Override
    public void initAI(final Game game, final int playerID) {
        if (this.featureSets.length == 1) {
            final int[] supportedPlayers = new int[game.players().count()];
            for (int i = 0; i < supportedPlayers.length; ++i) {
                supportedPlayers[i] = i + 1;
            }
            this.featureSets[0].instantiateFeatures(game, supportedPlayers, this.linearFunctions[0].effectiveParams());
        }
        else {
            for (int j = 1; j < this.featureSets.length; ++j) {
                if (!this.featureSets[j].hasInstantiatedFeatures(game, this.linearFunctions[j].effectiveParams())) {
                    this.featureSets[j].instantiateFeatures(game, new int[] { j }, this.linearFunctions[j].effectiveParams());
                }
            }
        }
    }
}
