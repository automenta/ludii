// 
// Decompiled by Procyon v0.5.36
// 

package policies.softmax;

import collections.FVector;
import collections.FastArrayList;
import features.FeatureSet;
import features.features.Feature;
import function_approx.BoostedLinearFunction;
import function_approx.LinearFunction;
import game.Game;
import game.rules.play.moves.Moves;
import game.types.play.RoleType;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import metadata.ai.features.Features;
import metadata.ai.misc.Pair;
import policies.Policy;
import util.Context;
import util.Move;
import util.Trial;
import utils.ExperimentFileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SoftmaxPolicy extends Policy
{
    protected LinearFunction[] linearFunctions;
    protected FeatureSet[] featureSets;
    protected int playoutActionLimit;
    protected int playoutTurnLimit;
    protected float autoPlayThreshold;
    
    public SoftmaxPolicy() {
        this.playoutActionLimit = -1;
        this.playoutTurnLimit = -1;
        this.autoPlayThreshold = -1.0f;
        this.linearFunctions = null;
        this.featureSets = null;
    }
    
    public SoftmaxPolicy(final LinearFunction[] linearFunctions, final FeatureSet[] featureSets) {
        this.playoutActionLimit = -1;
        this.playoutTurnLimit = -1;
        this.autoPlayThreshold = -1.0f;
        this.linearFunctions = linearFunctions;
        this.featureSets = Arrays.copyOf(featureSets, featureSets.length);
    }
    
    public SoftmaxPolicy(final LinearFunction[] linearFunctions, final FeatureSet[] featureSets, final int playoutActionLimit) {
        this.playoutActionLimit = -1;
        this.playoutTurnLimit = -1;
        this.autoPlayThreshold = -1.0f;
        this.linearFunctions = linearFunctions;
        this.featureSets = Arrays.copyOf(featureSets, featureSets.length);
        this.playoutActionLimit = playoutActionLimit;
    }
    
    public SoftmaxPolicy(final Features features) {
        this.playoutActionLimit = -1;
        this.playoutTurnLimit = -1;
        this.autoPlayThreshold = -1.0f;
        final List<FeatureSet> featureSetsList = new ArrayList<>();
        final List<LinearFunction> linFuncs = new ArrayList<>();
        for (final metadata.ai.features.FeatureSet featureSet : features.featureSets()) {
            if (featureSet.role() == RoleType.Shared) {
                addFeatureSetWeights(0, featureSet.featureStrings(), featureSet.featureWeights(), featureSetsList, linFuncs);
            }
            else {
                addFeatureSetWeights(featureSet.role().owner(), featureSet.featureStrings(), featureSet.featureWeights(), featureSetsList, linFuncs);
            }
        }
        this.featureSets = featureSetsList.toArray(new FeatureSet[0]);
        this.linearFunctions = linFuncs.toArray(new LinearFunction[0]);
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
    
    public FVector computeDistribution(final List<TIntArrayList> sparseFeatureVectors, final int player) {
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
        final FVector distribution = FVector.wrap(logits);
        distribution.softmax();
        return distribution;
    }
    
    public FVector computeDistributionErrors(final FVector estimatedDistribution, final FVector targetDistribution) {
        final FVector errors = estimatedDistribution.copy();
        errors.subtract(targetDistribution);
        return errors;
    }
    
    public FVector computeParamGradients(final FVector errors, final List<TIntArrayList> sparseFeatureVectors, final int player) {
        LinearFunction linearFunction;
        if (this.linearFunctions.length == 1) {
            linearFunction = this.linearFunctions[0];
        }
        else {
            linearFunction = this.linearFunctions[player];
        }
        final FVector grads = new FVector(linearFunction.trainableParams().dim());
        for (int numActions = errors.dim(), i = 0; i < numActions; ++i) {
            final float error = errors.get(i);
            final TIntArrayList sparseFeatureVector = sparseFeatureVectors.get(i);
            for (int j = 0; j < sparseFeatureVector.size(); ++j) {
                final int featureIdx = sparseFeatureVector.getQuick(j);
                grads.addToEntry(featureIdx, error);
            }
        }
        return grads;
    }
    
    public int selectActionFromDistribution(final FVector distribution) {
        return distribution.sampleFromDistribution();
    }
    
    public void updateFeatureSets(final FeatureSet[] newFeatureSets) {
        for (int i = 0; i < this.featureSets.length; ++i) {
            if (newFeatureSets[i] != null) {
                for (int numExtraFeatures = newFeatureSets[i].getNumFeatures() - this.featureSets[i].getNumFeatures(), j = 0; j < numExtraFeatures; ++j) {
                    this.linearFunctions[i].setTheta(this.linearFunctions[i].trainableParams().append(0.0f));
                }
                this.featureSets[i] = newFeatureSets[i];
            }
        }
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
        return context.game().playout(context, null, 1.0, this.featureSets, params, this.playoutActionLimit, this.playoutTurnLimit, this.autoPlayThreshold, ThreadLocalRandom.current());
    }
    
    @Override
    public boolean playoutSupportsGame(final Game game) {
        return this.supportsGame(game);
    }
    
    @Override
    public void customise(final String[] inputs) {
        final List<String> policyWeightsFilepaths = new ArrayList<>();
        boolean boosted = false;
        for (int i = 1; i < inputs.length; ++i) {
            final String input = inputs[i];
            if (input.toLowerCase().startsWith("policyweights=")) {
                if (!policyWeightsFilepaths.isEmpty()) {
                    policyWeightsFilepaths.clear();
                }
                policyWeightsFilepaths.add(input.substring("policyweights=".length()));
            }
            else if (input.toLowerCase().startsWith("policyweights")) {
                for (int p = 1; p <= 16; ++p) {
                    if (input.toLowerCase().startsWith("policyweights" + p + "=")) {
                        while (policyWeightsFilepaths.size() <= p) {
                            policyWeightsFilepaths.add(null);
                        }
                        policyWeightsFilepaths.set(p, input.substring("policyweightsX=".length()));
                    }
                }
            }
            else if (input.toLowerCase().startsWith("playoutactionlimit=")) {
                this.playoutActionLimit = Integer.parseInt(input.substring("playoutactionlimit=".length()));
            }
            else if (input.toLowerCase().startsWith("playoutturnlimit=")) {
                this.playoutTurnLimit = Integer.parseInt(input.substring("playoutturnlimit=".length()));
            }
            else if (input.toLowerCase().startsWith("friendly_name=")) {
                this.friendlyName = input.substring("friendly_name=".length());
            }
            else if (input.toLowerCase().startsWith("boosted=")) {
                if (input.toLowerCase().endsWith("true")) {
                    boosted = true;
                }
            }
            else if (input.toLowerCase().startsWith("auto_play_threshold=")) {
                this.autoPlayThreshold = Float.parseFloat(input.substring("auto_play_threshold=".length()));
            }
        }
        if (!policyWeightsFilepaths.isEmpty()) {
            this.linearFunctions = new LinearFunction[policyWeightsFilepaths.size()];
            this.featureSets = new FeatureSet[this.linearFunctions.length];
            for (int i = 0; i < policyWeightsFilepaths.size(); ++i) {
                String policyWeightsFilepath = policyWeightsFilepaths.get(i);
                if (policyWeightsFilepath != null) {
                    final String parentDir = new File(policyWeightsFilepath).getParent();
                    if (!new File(policyWeightsFilepath).exists()) {
                        policyWeightsFilepath = ExperimentFileUtils.getLastFilepath(parentDir + "/PolicyWeightsCE_P" + i, "txt");
                    }
                    if (boosted) {
                        this.linearFunctions[i] = BoostedLinearFunction.boostedFromFile(policyWeightsFilepath, null);
                    }
                    else {
                        this.linearFunctions[i] = LinearFunction.fromFile(policyWeightsFilepath);
                    }
                    this.featureSets[i] = new FeatureSet(parentDir + File.separator + this.linearFunctions[i].featureSetFile());
                }
            }
        }
        else {
            System.err.println("Cannot construct Softmax Policy from: " + Arrays.toString(inputs));
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
        return actions.moves().get(this.selectActionFromDistribution(this.computeDistribution(featureSet.computeSparseFeatureVectors(context, actions.moves(), true), context.state().mover())));
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
    
    public LinearFunction linearFunction(final int player) {
        if (this.linearFunctions.length == 1) {
            return this.linearFunctions[0];
        }
        return this.linearFunctions[player];
    }
    
    public LinearFunction[] linearFunctions() {
        return this.linearFunctions;
    }
    
    public FeatureSet[] featureSets() {
        return this.featureSets;
    }
    
    public Features generateFeaturesMetadata() {
        Features features;
        if (this.featureSets.length == 1) {
            final FeatureSet featureSet = this.featureSets[0];
            final LinearFunction linFunc = this.linearFunctions[0];
            final Pair[] pairs = new Pair[featureSet.features().length];
            for (int i = 0; i < pairs.length; ++i) {
                pairs[i] = new Pair(featureSet.features()[i].toString(), linFunc.effectiveParams().get(i));
            }
            features = new Features(new metadata.ai.features.FeatureSet(RoleType.Shared, pairs));
        }
        else {
            final metadata.ai.features.FeatureSet[] metadataFeatureSets = new metadata.ai.features.FeatureSet[this.featureSets.length - 1];
            for (int p = 1; p < this.featureSets.length; ++p) {
                final FeatureSet featureSet2 = this.featureSets[p];
                final LinearFunction linFunc2 = this.linearFunctions[p];
                final Pair[] pairs2 = new Pair[featureSet2.features().length];
                for (int j = 0; j < pairs2.length; ++j) {
                    pairs2[j] = new Pair(featureSet2.features()[j].toString(), linFunc2.effectiveParams().get(j));
                }
                metadataFeatureSets[p - 1] = new metadata.ai.features.FeatureSet(RoleType.roleForPlayerId(p), pairs2);
            }
            features = new Features(metadataFeatureSets);
        }
        return features;
    }
    
    public static SoftmaxPolicy fromLines(final String[] lines) {
        SoftmaxPolicy policy = null;
        for (final String line : lines) {
            if (line.equalsIgnoreCase("features=from_metadata")) {
                policy = new SoftmaxFromMetadata();
                break;
            }
        }
        if (policy == null) {
            policy = new SoftmaxPolicy();
        }
        policy.customise(lines);
        return policy;
    }
    
    public static SoftmaxPolicy fromFile(final File weightsFile) {
        final SoftmaxPolicy policy = new SoftmaxPolicy();
        boolean boosted = false;
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(weightsFile.getAbsolutePath()), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            String lastLine = null;
            while (line != null) {
                lastLine = line;
                line = reader.readLine();
            }
            if (!lastLine.startsWith("FeatureSet=")) {
                boosted = true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        policy.customise(new String[] { "softmax", "policyweights=" + weightsFile.getAbsolutePath(), "boosted=" + boosted });
        return policy;
    }
    
    protected static void addFeatureSetWeights(final int playerIdx, final String[] featureStrings, final float[] featureWeights, final List<FeatureSet> outFeatureSets, final List<LinearFunction> outLinFuncs) {
        while (outFeatureSets.size() <= playerIdx) {
            outFeatureSets.add(null);
        }
        while (outLinFuncs.size() <= playerIdx) {
            outLinFuncs.add(null);
        }
        final List<Feature> features = new ArrayList<>();
        final TFloatArrayList weights = new TFloatArrayList();
        for (int i = 0; i < featureStrings.length; ++i) {
            features.add(Feature.fromString(featureStrings[i]));
            weights.add(featureWeights[i]);
        }
        outFeatureSets.set(playerIdx, new FeatureSet(features));
        outLinFuncs.set(playerIdx, new LinearFunction(new FVector(weights.toArray())));
    }
}
