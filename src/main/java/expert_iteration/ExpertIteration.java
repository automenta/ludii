// 
// Decompiled by Procyon v0.5.36
// 

package expert_iteration;

import features.FeatureSet;
import features.FeatureUtils;
import features.elements.FeatureElement;
import features.elements.RelativeFeatureElement;
import features.features.Feature;
import features.generation.AtomicFeatureGenerator;
import features.instances.FeatureInstance;
import features.patterns.Pattern;
import function_approx.BoostedLinearFunction;
import function_approx.LinearFunction;
import game.Game;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import language.compiler.Compiler;
import main.CommandLineArgParse;
import main.FileHandling;
import main.collections.FVector;
import main.collections.FastArrayList;
import main.grammar.Report;
import metadata.ai.features.Features;
import metadata.ai.heuristics.Heuristics;
import metadata.ai.misc.BestAgent;
import optimisers.Optimiser;
import optimisers.OptimiserFactory;
import policies.softmax.SoftmaxPolicy;
import search.mcts.MCTS;
import search.mcts.finalmoveselection.RobustChild;
import search.mcts.selection.AG0Selection;
import search.minimax.AlphaBetaSearch;
import util.*;
import utils.AIFactory;
import utils.AIUtils;
import utils.ExperimentFileUtils;
import utils.ExponentialMovingAverage;
import utils.data_structures.experience_buffers.ExperienceBuffer;
import utils.data_structures.experience_buffers.PrioritizedReplayBuffer;
import utils.data_structures.experience_buffers.UniformExperienceBuffer;
import utils.experiments.InterruptableExperiment;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class ExpertIteration
{
    private static final String gameCheckpointFormat = "%s_%05d.%s";
    private static final String weightUpdateCheckpointFormat = "%s_%08d.%s";
    protected String gameName;
    public List<String> gameOptions;
    protected String expertAI;
    protected String bestAgentsDataDir;
    protected int numTrainingGames;
    public int gameLengthCap;
    protected double thinkingTime;
    protected int iterationLimit;
    protected int depthLimit;
    protected int addFeatureEvery;
    protected int batchSize;
    protected int experienceBufferSize;
    protected int updateWeightsEvery;
    protected boolean noGrowFeatureSet;
    protected boolean trainTSPG;
    protected String crossEntropyOptimiserConfig;
    protected String ceExploreOptimiserConfig;
    protected String tspgOptimiserConfig;
    protected String valueOptimiserConfig;
    protected int combiningFeatureInstanceThreshold;
    protected boolean importanceSamplingEpisodeDurations;
    protected boolean prioritizedExperienceReplay;
    protected boolean ceExplore;
    protected float ceExploreMix;
    protected double ceExploreGamma;
    protected boolean ceExploreUniform;
    protected boolean noCEExploreIS;
    protected boolean weightedImportanceSampling;
    protected boolean noValueLearning;
    protected int maxNumBiasedPlayoutActions;
    protected boolean noPruneInitFeatures;
    protected int pruneInitFeaturesThreshold;
    protected int numPruningGames;
    protected int maxNumPruningSeconds;
    protected File outDir;
    protected CheckpointTypes checkpointType;
    protected int checkpointFrequency;
    protected boolean noLogging;
    protected boolean useGUI;
    protected int maxWallTime;
    
    public ExpertIteration() {
    }
    
    public ExpertIteration(final boolean useGUI) {
        this.useGUI = useGUI;
    }
    
    public ExpertIteration(final boolean useGUI, final int maxWallTime) {
        this.useGUI = useGUI;
        this.maxWallTime = maxWallTime;
    }
    
    public void startExperiment() {
        try (final PrintWriter logWriter = this.createLogWriter()) {
            this.startTraining(logWriter);
        }
    }
    
    private void startTraining(final PrintWriter logWriter) {
        final Game game = GameLoader.loadGameFromName(this.gameName, this.gameOptions);
        final int numPlayers = game.players().count();
        if (this.gameLengthCap >= 0) {
            game.setMaxTurns(Math.min(this.gameLengthCap, game.getMaxTurnLimit()));
        }
        final InterruptableExperiment experiment = new InterruptableExperiment(true) {
            protected long lastCheckpoint;
            protected String[] currentFeatureSetFilenames;
            protected String[] currentPolicyWeightsCEFilenames;
            protected String[] currentPolicyWeightsTSPGFilenames;
            protected String[] currentPolicyWeightsCEEFilenames;
            protected String currentValueFunctionFilename;
            protected String[] currentExperienceBufferFilenames;
            protected String[] currentGameDurationTrackerFilenames;
            protected String[] currentOptimiserCEFilenames;
            protected String[] currentOptimiserTSPGFilenames;
            protected String[] currentOptimiserCEEFilenames;
            protected String currentOptimiserValueFilename;
            
            private void initMembers() {
                this.lastCheckpoint = Long.MAX_VALUE;
                this.currentFeatureSetFilenames = new String[numPlayers + 1];
                this.currentPolicyWeightsCEFilenames = new String[numPlayers + 1];
                this.currentPolicyWeightsTSPGFilenames = new String[numPlayers + 1];
                this.currentPolicyWeightsCEEFilenames = new String[numPlayers + 1];
                this.currentValueFunctionFilename = null;
                this.currentExperienceBufferFilenames = new String[numPlayers + 1];
                this.currentGameDurationTrackerFilenames = new String[numPlayers + 1];
                this.currentOptimiserCEFilenames = new String[numPlayers + 1];
                this.currentOptimiserTSPGFilenames = new String[numPlayers + 1];
                this.currentOptimiserCEEFilenames = new String[numPlayers + 1];
                this.currentOptimiserValueFilename = null;
            }
            
            @Override
            public void runExperiment() {
                if (ExpertIteration.this.outDir == null) {
                    System.err.println("Warning: we're not writing any output files for this run!");
                }
                else if (!ExpertIteration.this.outDir.exists()) {
                    ExpertIteration.this.outDir.mkdirs();
                }
                this.initMembers();
                FeatureSet[] featureSets = this.prepareFeatureSets();
                final LinearFunction[] crossEntropyFunctions = this.prepareCrossEntropyFunctions(featureSets);
                final LinearFunction[] tspgFunctions = this.prepareTSPGFunctions(featureSets, crossEntropyFunctions);
                final LinearFunction[] ceExploreFunctions = this.prepareCEExploreFunctions(featureSets);
                final SoftmaxPolicy cePolicy = new SoftmaxPolicy(crossEntropyFunctions, featureSets, ExpertIteration.this.maxNumBiasedPlayoutActions);
                final SoftmaxPolicy tspgPolicy = new SoftmaxPolicy(tspgFunctions, featureSets, ExpertIteration.this.maxNumBiasedPlayoutActions);
                final SoftmaxPolicy ceExplorePolicy = new SoftmaxPolicy(ceExploreFunctions, featureSets);
                final Heuristics valueFunction = this.prepareValueFunction();
                final Optimiser[] ceOptimisers = this.prepareCrossEntropyOptimisers();
                final Optimiser[] tspgOptimisers = this.prepareTSPGOptimisers();
                final Optimiser[] ceExploreOptimisers = this.prepareCEExploreOptimisers();
                final Optimiser valueFunctionOptimiser = this.prepareValueFunctionOptimiser();
                final Trial trial = new Trial(game);
                final Context context = new Context(game, trial);
                final List<ExpertPolicy> experts = new ArrayList<>(numPlayers + 1);
                experts.add(null);
                final SoftmaxPolicy playoutPolicy = cePolicy;
                for (int p = 1; p <= numPlayers; ++p) {
                    final Report report = new Report();
                    AI ai = null;
                    Label_0743: {
                        if (ExpertIteration.this.expertAI.equals("BEST_AGENT")) {
                            try {
                                final BestAgent bestAgent = (BestAgent)Compiler.compileObject(FileHandling.loadTextContentsFromFile(ExpertIteration.this.bestAgentsDataDir + "/BestAgent.txt"), "metadata.ai.misc.BestAgent", report);
                                if (bestAgent.agent().equals("AlphaBeta")) {
                                    ai = new AlphaBetaSearch(ExpertIteration.this.bestAgentsDataDir + "/BestHeuristics.txt");
                                }
                                else if (bestAgent.agent().equals("AlphaBetaMetadata")) {
                                    ai = new AlphaBetaSearch();
                                }
                                else if (bestAgent.agent().equals("UCT")) {
                                    ai = AIFactory.createAI("UCT");
                                }
                                else if (bestAgent.agent().equals("MC-GRAVE")) {
                                    ai = AIFactory.createAI("MC-GRAVE");
                                }
                                else if (bestAgent.agent().equals("Biased MCTS")) {
                                    final Features features = (Features)Compiler.compileObject(FileHandling.loadTextContentsFromFile(ExpertIteration.this.bestAgentsDataDir + "/BestFeatures.txt"), "metadata.ai.features.Features", report);
                                    ai = MCTS.createBiasedMCTS(features, true);
                                }
                                else if (bestAgent.agent().equals("Biased MCTS (Uniform Playouts)")) {
                                    final Features features = (Features)Compiler.compileObject(FileHandling.loadTextContentsFromFile(ExpertIteration.this.bestAgentsDataDir + "/BestFeatures.txt"), "metadata.ai.features.Features", report);
                                    ai = MCTS.createBiasedMCTS(features, false);
                                }
                                else {
                                    if (!bestAgent.agent().equals("Random")) {
                                        System.err.println("Unrecognised best agent: " + bestAgent.agent());
                                        return;
                                    }
                                    ai = MCTS.createUCT();
                                }
                                break Label_0743;
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                        if (ExpertIteration.this.expertAI.equals("FROM_METADATA")) {
                            ai = AIFactory.fromMetadata(game);
                            if (ai == null) {
                                System.err.println("AI from metadata is null!");
                                return;
                            }
                            if (!(ai instanceof ExpertPolicy)) {
                                System.err.println("AI from metadata is not an expert policy!");
                                return;
                            }
                        }
                        else {
                            if (!ExpertIteration.this.expertAI.equals("Biased MCTS")) {
                                System.err.println("Cannot recognise expert AI: " + ExpertIteration.this.expertAI);
                                return;
                            }
                            final MCTS mcts = new MCTS(new AG0Selection(), playoutPolicy, new RobustChild());
                            mcts.setLearnedSelectionPolicy(cePolicy);
                            mcts.friendlyName = "Biased MCTS";
                            ai = mcts;
                        }
                    }
                    if (ai instanceof MCTS) {
                        ((MCTS)ai).setPreserveRootNode(true);
                    }
                    else if (ExpertIteration.this.trainTSPG) {
                        System.err.println("A non-MCTS expert cannot be used for training the TSPG objective!");
                        return;
                    }
                    experts.add((ExpertPolicy)ai);
                }
                final ExperienceBuffer[] experienceBuffers = this.prepareExperienceBuffers(ExpertIteration.this.prioritizedExperienceReplay);
                final ExponentialMovingAverage[] avgGameDurations = this.prepareGameDurationTrackers();
                long actionCounter = 0L;
                long weightsUpdateCounter = (ExpertIteration.this.checkpointType == CheckpointTypes.WeightUpdate) ? this.lastCheckpoint : 0L;
                int gameCounter = 0;
                if (ExpertIteration.this.checkpointType == CheckpointTypes.Game && this.lastCheckpoint >= 0L) {
                    gameCounter = (int)this.lastCheckpoint;
                    final ExpertIteration this$0 = ExpertIteration.this;
                    this$0.numTrainingGames += (int)this.lastCheckpoint;
                }
                while (gameCounter < ExpertIteration.this.numTrainingGames) {
                    this.checkWallTime(0.05);
                    if (this.interrupted) {
                        this.logLine(logWriter, "interrupting experiment...");
                        break;
                    }
                    this.saveCheckpoints(gameCounter, weightsUpdateCounter, featureSets, crossEntropyFunctions, tspgFunctions, ceExploreFunctions, valueFunction, experienceBuffers, ceOptimisers, tspgOptimisers, ceExploreOptimisers, valueFunctionOptimiser, avgGameDurations, false);
                    final FeatureSet[] expandedFeatureSets = new FeatureSet[numPlayers + 1];
                    if (!ExpertIteration.this.noGrowFeatureSet && gameCounter > 0 && gameCounter % ExpertIteration.this.addFeatureEvery == 0) {
                        for (int p2 = 1; p2 <= numPlayers; ++p2) {
                            final ExItExperience[] batch = experienceBuffers[p2].sampleExperienceBatchUniformly(ExpertIteration.this.batchSize);
                            if (batch.length > 0) {
                                final long startTime = System.currentTimeMillis();
                                final FeatureSet expandedFeatureSet = this.expandFeatureSetCorrelationBased(batch, featureSets[p2], cePolicy, game, ExpertIteration.this.combiningFeatureInstanceThreshold);
                                if (expandedFeatureSet != null) {
                                    (expandedFeatureSets[p2] = expandedFeatureSet).instantiateFeatures(game, new int[] { p2 }, null);
                                }
                                else {
                                    expandedFeatureSets[p2] = featureSets[p2];
                                }
                                this.logLine(logWriter, "Expanded feature set in " + (System.currentTimeMillis() - startTime) + " ms for P" + p2 + ".");
                            }
                            else {
                                expandedFeatureSets[p2] = featureSets[p2];
                            }
                        }
                        cePolicy.updateFeatureSets(expandedFeatureSets);
                        if (ExpertIteration.this.trainTSPG) {
                            tspgPolicy.updateFeatureSets(expandedFeatureSets);
                        }
                        if (ExpertIteration.this.ceExplore) {
                            ceExplorePolicy.updateFeatureSets(expandedFeatureSets);
                        }
                        featureSets = expandedFeatureSets;
                    }
                    this.logLine(logWriter, "starting game " + (gameCounter + 1));
                    game.start(context);
                    final List<List<ExItExperience>> gameExperienceSamples = new ArrayList<>(numPlayers + 1);
                    gameExperienceSamples.add(null);
                    for (int p3 = 1; p3 < experts.size(); ++p3) {
                        experts.get(p3).initAI(game, p3);
                        gameExperienceSamples.add(new ArrayList<>());
                    }
                    double ceExploreCurrISWeight = 1.0;
                    final List<FVector> ceExploreGradientVectors = new ArrayList<>();
                    final TIntArrayList ceExploreMovers = new TIntArrayList();
                    final TFloatArrayList ceExploreRewards = new TFloatArrayList();
                    while (!context.trial().over()) {
                        if (this.interrupted) {
                            this.logLine(logWriter, "interrupting experiment...");
                            break;
                        }
                        final int mover = context.state().mover();
                        final ExpertPolicy expert = experts.get(context.state().playerToAgent(mover));
                        expert.selectAction(game, new Context(context), ExpertIteration.this.thinkingTime, ExpertIteration.this.iterationLimit, ExpertIteration.this.depthLimit);
                        final FastArrayList<Move> legalMoves = new FastArrayList<>();
                        for (final Move legalMove : expert.lastSearchRootMoves()) {
                            legalMoves.add(legalMove);
                        }
                        final FVector expertDistribution = expert.computeExpertPolicy(1.0);
                        Move move;
                        if (ExpertIteration.this.ceExplore) {
                            final FVector ceExploreDistribution = ceExplorePolicy.computeDistribution(context, legalMoves, false);
                            final FVector mixedDistribution = expertDistribution.copy();
                            mixedDistribution.mult(1.0f - ExpertIteration.this.ceExploreMix);
                            mixedDistribution.addScaled(ceExploreDistribution, ExpertIteration.this.ceExploreMix);
                            final int moveIdx = mixedDistribution.sampleProportionally();
                            move = legalMoves.get(moveIdx);
                            ceExploreCurrISWeight *= expertDistribution.get(moveIdx) / mixedDistribution.get(moveIdx);
                            final List<TIntArrayList> sparseFeatureVectors = featureSets[mover].computeSparseFeatureVectors(context, legalMoves, false);
                            final FVector gradLog = new FVector(ceExplorePolicy.linearFunction(mover).trainableParams().dim());
                            for (int i = 0; i < sparseFeatureVectors.size(); ++i) {
                                final TIntArrayList featureVector = sparseFeatureVectors.get(i);
                                for (int j = 0; j < featureVector.size(); ++j) {
                                    gradLog.addToEntry(featureVector.getQuick(j), -1.0f * ceExploreDistribution.get(i));
                                }
                            }
                            final TIntArrayList featureVector2 = sparseFeatureVectors.get(moveIdx);
                            for (int k = 0; k < featureVector2.size(); ++k) {
                                gradLog.addToEntry(featureVector2.getQuick(k), 1.0f);
                            }
                            ceExploreGradientVectors.add(gradLog);
                            final FVector learnedDistribution = cePolicy.computeDistribution(sparseFeatureVectors, mover);
                            final FVector errors = expertDistribution.copy();
                            errors.subtract(learnedDistribution);
                            errors.abs();
                            ceExploreRewards.add(errors.sum());
                            ceExploreMovers.add(mover);
                        }
                        else {
                            final int moveIdx2 = expertDistribution.sampleProportionally();
                            move = legalMoves.get(moveIdx2);
                        }
                        final ExItExperience newExperience = expert.generateExItExperience();
                        if (valueFunction != null) {
                            newExperience.setStateFeatureVector(valueFunction.computeStateFeatureVector(context, mover));
                        }
                        gameExperienceSamples.get(mover).add(newExperience);
                        if (ExpertIteration.this.ceExplore) {
                            newExperience.setWeightCEExplore((float)ceExploreCurrISWeight);
                        }
                        game.apply(context, move);
                        ++actionCounter;
                        if (actionCounter % ExpertIteration.this.updateWeightsEvery != 0L) {
                            continue;
                        }
                        for (int p4 = 1; p4 <= numPlayers; ++p4) {
                            final ExItExperience[] batch2 = experienceBuffers[p4].sampleExperienceBatch(ExpertIteration.this.batchSize);
                            if (batch2.length != 0) {
                                final List<FVector> gradientsCE = new ArrayList<>(batch2.length);
                                final List<FVector> gradientsTSPG = new ArrayList<>(batch2.length);
                                final List<FVector> gradientsCEExplore = new ArrayList<>(batch2.length);
                                final List<FVector> gradientsValueFunction = new ArrayList<>(batch2.length);
                                final int[] indices = new int[batch2.length];
                                final float[] priorities = new float[batch2.length];
                                double sumImportanceSamplingWeights = 0.0;
                                for (int idx = 0; idx < batch2.length; ++idx) {
                                    final ExItExperience sample = batch2[idx];
                                    final List<TIntArrayList> sparseFeatureVectors2 = featureSets[p4].computeSparseFeatureVectors(sample.state().state(), sample.state().lastDecisionMove(), sample.moves(), false);
                                    final FVector apprenticePolicy = cePolicy.computeDistribution(sparseFeatureVectors2, sample.state().state().mover());
                                    final FVector expertPolicy = sample.expertDistribution();
                                    final FVector errors2 = cePolicy.computeDistributionErrors(apprenticePolicy, expertPolicy);
                                    if (sample.state().state().mover() != p4) {
                                        System.err.println("Sample's mover not equal to p!");
                                    }
                                    final FVector ceGradients = cePolicy.computeParamGradients(errors2, sparseFeatureVectors2, sample.state().state().mover());
                                    FVector valueGradients = null;
                                    if (valueFunction != null) {
                                        final FVector valueFunctionParams = valueFunction.paramsVector();
                                        final float predictedValue = (float)Math.tanh(valueFunctionParams.dot(sample.stateFeatureVector()));
                                        final float gameOutcome = (float)sample.playerOutcomes()[sample.state().state().mover()];
                                        final float valueError = predictedValue - gameOutcome;
                                        valueGradients = new FVector(valueFunctionParams.dim());
                                        final float gradDivFeature = 2.0f * valueError * (1.0f - predictedValue * predictedValue);
                                        for (int l = 0; l < valueGradients.dim(); ++l) {
                                            valueGradients.set(l, gradDivFeature * sample.stateFeatureVector().get(l));
                                        }
                                    }
                                    double importanceSamplingWeight = 1.0;
                                    if (ExpertIteration.this.importanceSamplingEpisodeDurations) {
                                        importanceSamplingWeight *= avgGameDurations[p4].movingAvg() / sample.episodeDuration();
                                    }
                                    if (ExpertIteration.this.prioritizedExperienceReplay) {
                                        final FVector absErrors = errors2.copy();
                                        absErrors.abs();
                                        priorities[idx] = Math.max(0.05f, absErrors.sum());
                                        importanceSamplingWeight *= sample.weightPER();
                                        indices[idx] = sample.bufferIdx();
                                    }
                                    if (ExpertIteration.this.ceExplore && !ExpertIteration.this.noCEExploreIS) {
                                        float ceExploreWeight = sample.weightCEExplore();
                                        if (ceExploreWeight < 0.1f) {
                                            ceExploreWeight = 0.1f;
                                        }
                                        else if (ceExploreWeight > 2.0f) {
                                            ceExploreWeight = 2.0f;
                                        }
                                        importanceSamplingWeight *= ceExploreWeight;
                                    }
                                    sumImportanceSamplingWeights += importanceSamplingWeight;
                                    ceGradients.mult((float)importanceSamplingWeight);
                                    gradientsCE.add(ceGradients);
                                    if (valueGradients != null) {
                                        valueGradients.mult((float)importanceSamplingWeight);
                                        gradientsValueFunction.add(valueGradients);
                                    }
                                    if (ExpertIteration.this.trainTSPG) {
                                        final FVector pi = tspgPolicy.computeDistribution(sparseFeatureVectors2, sample.state().state().mover());
                                        final FVector expertQs = sample.expertValueEstimates();
                                        final FVector grads = new FVector(tspgFunctions[p4].trainableParams().dim());
                                        for (int l = 0; l < sample.moves().size(); ++l) {
                                            final float expertQ = expertQs.get(l);
                                            final float pi_sa = pi.get(l);
                                            for (int m = 0; m < sample.moves().size(); ++m) {
                                                final TIntArrayList activeFeatures = sparseFeatureVectors2.get(m);
                                                for (int k2 = 0; k2 < activeFeatures.size(); ++k2) {
                                                    final int feature = activeFeatures.getQuick(k2);
                                                    if (l == m) {
                                                        grads.addToEntry(feature, expertQ * pi_sa * (1.0f - pi_sa));
                                                    }
                                                    else {
                                                        grads.addToEntry(feature, expertQ * pi_sa * (0.0f - pi.get(m)));
                                                    }
                                                }
                                            }
                                        }
                                        gradientsTSPG.add(grads);
                                    }
                                }
                                FVector meanGradientsValue = null;
                                FVector meanGradientsCE;
                                if (ExpertIteration.this.weightedImportanceSampling) {
                                    meanGradientsCE = gradientsCE.get(0).copy();
                                    for (int i2 = 1; i2 < gradientsCE.size(); ++i2) {
                                        meanGradientsCE.add(gradientsCE.get(i2));
                                    }
                                    meanGradientsCE.div((float)sumImportanceSamplingWeights);
                                    if (!gradientsValueFunction.isEmpty()) {
                                        meanGradientsValue = gradientsValueFunction.get(0).copy();
                                        for (int i2 = 1; i2 < gradientsValueFunction.size(); ++i2) {
                                            meanGradientsValue.add(gradientsValueFunction.get(i2));
                                        }
                                        meanGradientsValue.div((float)sumImportanceSamplingWeights);
                                    }
                                }
                                else {
                                    meanGradientsCE = FVector.mean(gradientsCE);
                                    if (!gradientsValueFunction.isEmpty()) {
                                        meanGradientsValue = FVector.mean(gradientsValueFunction);
                                    }
                                }
                                ceOptimisers[p4].minimiseObjective(crossEntropyFunctions[p4].trainableParams(), meanGradientsCE);
                                if (meanGradientsValue != null) {
                                    final FVector valueFunctionParams2 = valueFunction.paramsVector();
                                    valueFunctionOptimiser.minimiseObjective(valueFunctionParams2, meanGradientsValue);
                                    valueFunction.updateParams(game, valueFunctionParams2, 0);
                                }
                                if (ExpertIteration.this.trainTSPG) {
                                    final FVector meanGradientsTSPG = FVector.mean(gradientsTSPG);
                                    tspgOptimisers[p4].maximiseObjective(tspgFunctions[p4].trainableParams(), meanGradientsTSPG);
                                }
                                if (ExpertIteration.this.prioritizedExperienceReplay) {
                                    final PrioritizedReplayBuffer buffer = (PrioritizedReplayBuffer)experienceBuffers[p4];
                                    buffer.setPriorities(indices, priorities);
                                }
                            }
                        }
                        ++weightsUpdateCounter;
                    }
                    if (!this.interrupted) {
                        for (int p5 = 1; p5 <= numPlayers; ++p5) {
                            Collections.shuffle(gameExperienceSamples.get(p5), ThreadLocalRandom.current());
                            final int gameDuration = gameExperienceSamples.get(p5).size();
                            avgGameDurations[p5].observe(gameDuration);
                            final double[] playerOutcomes = AIUtils.agentUtilities(context);
                            for (final ExItExperience experience : gameExperienceSamples.get(p5)) {
                                experience.setEpisodeDuration(gameDuration);
                                experience.setPlayerOutcomes(playerOutcomes);
                                experienceBuffers[p5].add(experience);
                            }
                        }
                        if (ExpertIteration.this.ceExplore && !ExpertIteration.this.ceExploreUniform) {
                            final List<List<FVector>> gradVectorsPerPlayer = new ArrayList<>(numPlayers + 1);
                            gradVectorsPerPlayer.add(null);
                            for (int p6 = 1; p6 <= numPlayers; ++p6) {
                                gradVectorsPerPlayer.add(new ArrayList<>());
                            }
                            for (int t = 0; t < ceExploreGradientVectors.size(); ++t) {
                                final FVector gradLog2 = ceExploreGradientVectors.get(t);
                                float returns = 0.0f;
                                for (int tt = t + 1; tt < ceExploreRewards.size(); ++tt) {
                                    returns += (float)(Math.pow(ExpertIteration.this.ceExploreGamma, tt - (t + 1)) * ceExploreRewards.getQuick(tt));
                                }
                                gradLog2.mult(returns);
                                gradVectorsPerPlayer.get(ceExploreMovers.getQuick(t)).add(gradLog2);
                            }
                            for (int p6 = 1; p6 <= numPlayers; ++p6) {
                                if (gradVectorsPerPlayer.get(p6).size() > 0) {
                                    final FVector meanGradientsCEExplore = FVector.mean(gradVectorsPerPlayer.get(p6));
                                    ceExploreOptimisers[p6].minimiseObjective(ceExploreFunctions[p6].trainableParams(), meanGradientsCEExplore);
                                }
                            }
                        }
                    }
                    if (context.trial().over()) {
                        this.logLine(logWriter, "Finished running game " + (gameCounter + 1));
                    }
                    ++gameCounter;
                }
                this.saveCheckpoints(gameCounter + 1, weightsUpdateCounter, featureSets, crossEntropyFunctions, tspgFunctions, ceExploreFunctions, valueFunction, experienceBuffers, ceOptimisers, tspgOptimisers, ceExploreOptimisers, valueFunctionOptimiser, avgGameDurations, true);
            }
            
            private Optimiser[] prepareCrossEntropyOptimisers() {
                final Optimiser[] optimisers = new Optimiser[numPlayers + 1];
                for (int p = 1; p <= numPlayers; ++p) {
                    Optimiser optimiser = null;
                    this.currentOptimiserCEFilenames[p] = this.getFilenameLastCheckpoint("OptimiserCE_P" + p, "opt");
                    this.lastCheckpoint = Math.min(this.lastCheckpoint, this.extractCheckpointFromFilename(this.currentOptimiserCEFilenames[p], "OptimiserCE_P" + p, "opt"));
                    if (this.currentOptimiserCEFilenames[p] == null) {
                        optimiser = OptimiserFactory.createOptimiser(ExpertIteration.this.crossEntropyOptimiserConfig);
                        this.logLine(logWriter, "starting with new optimiser for Cross-Entropy");
                    }
                    else {
                        try (final ObjectInputStream reader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentOptimiserCEFilenames[p])))) {
                            optimiser = (Optimiser)reader.readObject();
                        }
                        catch (IOException | ClassNotFoundException ex2) {
                            ex2.printStackTrace();
                        }
                        this.logLine(logWriter, "continuing with CE optimiser loaded from " + this.currentOptimiserCEFilenames[p]);
                    }
                    optimisers[p] = optimiser;
                }
                return optimisers;
            }
            
            private Optimiser[] prepareTSPGOptimisers() {
                final Optimiser[] optimisers = new Optimiser[numPlayers + 1];
                if (ExpertIteration.this.trainTSPG) {
                    for (int p = 1; p <= numPlayers; ++p) {
                        Optimiser optimiser = null;
                        this.currentOptimiserTSPGFilenames[p] = this.getFilenameLastCheckpoint("OptimiserTSPG_P" + p, "opt");
                        this.lastCheckpoint = Math.min(this.lastCheckpoint, this.extractCheckpointFromFilename(this.currentOptimiserTSPGFilenames[p], "OptimiserTSPG_P" + p, "opt"));
                        if (this.currentOptimiserTSPGFilenames[p] == null) {
                            optimiser = OptimiserFactory.createOptimiser(ExpertIteration.this.tspgOptimiserConfig);
                            this.logLine(logWriter, "starting with new optimiser for TSPG");
                        }
                        else {
                            try (final ObjectInputStream reader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentOptimiserTSPGFilenames[p])))) {
                                optimiser = (Optimiser)reader.readObject();
                            }
                            catch (IOException | ClassNotFoundException ex2) {
                                ex2.printStackTrace();
                            }
                            this.logLine(logWriter, "continuing with TSPG optimiser loaded from " + this.currentOptimiserTSPGFilenames[p]);
                        }
                        optimisers[p] = optimiser;
                    }
                }
                return optimisers;
            }
            
            private Optimiser[] prepareCEExploreOptimisers() {
                final Optimiser[] optimisers = new Optimiser[numPlayers + 1];
                for (int p = 1; p <= numPlayers; ++p) {
                    Optimiser optimiser = null;
                    this.currentOptimiserCEEFilenames[p] = this.getFilenameLastCheckpoint("OptimiserCEE_P" + p, "opt");
                    this.lastCheckpoint = Math.min(this.lastCheckpoint, this.extractCheckpointFromFilename(this.currentOptimiserCEEFilenames[p], "OptimiserCEE_P" + p, "opt"));
                    if (this.currentOptimiserCEEFilenames[p] == null) {
                        optimiser = OptimiserFactory.createOptimiser(ExpertIteration.this.ceExploreOptimiserConfig);
                        this.logLine(logWriter, "starting with new optimiser for CEE");
                    }
                    else {
                        try (final ObjectInputStream reader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentOptimiserCEEFilenames[p])))) {
                            optimiser = (Optimiser)reader.readObject();
                        }
                        catch (IOException | ClassNotFoundException ex2) {
                            ex2.printStackTrace();
                        }
                        this.logLine(logWriter, "continuing with CEE optimiser loaded from " + this.currentOptimiserCEEFilenames[p]);
                    }
                    optimisers[p] = optimiser;
                }
                return optimisers;
            }
            
            private Optimiser prepareValueFunctionOptimiser() {
                final Optimiser[] optimisers = new Optimiser[numPlayers + 1];
                Optimiser optimiser = null;
                this.currentOptimiserValueFilename = this.getFilenameLastCheckpoint("OptimiserValue", "opt");
                this.lastCheckpoint = Math.min(this.lastCheckpoint, this.extractCheckpointFromFilename(this.currentOptimiserValueFilename, "OptimiserValue", "opt"));
                if (this.currentOptimiserValueFilename == null) {
                    optimiser = OptimiserFactory.createOptimiser(ExpertIteration.this.valueOptimiserConfig);
                    this.logLine(logWriter, "starting with new optimiser for Value function");
                }
                else {
                    try (final ObjectInputStream reader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentOptimiserValueFilename)))) {
                        optimiser = (Optimiser)reader.readObject();
                    }
                    catch (IOException | ClassNotFoundException ex2) {
                        ex2.printStackTrace();
                    }
                    this.logLine(logWriter, "continuing with Value function optimiser loaded from " + this.currentOptimiserValueFilename);
                }
                return optimiser;
            }
            
            private ExperienceBuffer[] prepareExperienceBuffers(final boolean prio) {
                final ExperienceBuffer[] experienceBuffers = new ExperienceBuffer[numPlayers + 1];
                for (int p = 1; p <= numPlayers; ++p) {
                    this.currentExperienceBufferFilenames[p] = this.getFilenameLastCheckpoint("ExperienceBuffer_P" + p, "buf");
                    this.lastCheckpoint = Math.min(this.lastCheckpoint, this.extractCheckpointFromFilename(this.currentExperienceBufferFilenames[p], "ExperienceBuffer_P" + p, "buf"));
                    ExperienceBuffer experienceBuffer;
                    if (this.currentExperienceBufferFilenames[p] == null) {
                        if (prio) {
                            experienceBuffer = new PrioritizedReplayBuffer(ExpertIteration.this.experienceBufferSize);
                        }
                        else {
                            experienceBuffer = new UniformExperienceBuffer(ExpertIteration.this.experienceBufferSize);
                        }
                        this.logLine(logWriter, "starting with empty experience buffer");
                    }
                    else {
                        experienceBuffer = prio ? PrioritizedReplayBuffer.fromFile(game, ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentExperienceBufferFilenames[p]) : UniformExperienceBuffer.fromFile(game, ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentExperienceBufferFilenames[p]);
                        this.logLine(logWriter, "continuing with experience buffer loaded from " + this.currentExperienceBufferFilenames[p]);
                    }
                    experienceBuffers[p] = experienceBuffer;
                }
                return experienceBuffers;
            }
            
            private ExponentialMovingAverage[] prepareGameDurationTrackers() {
                final ExponentialMovingAverage[] trackers = new ExponentialMovingAverage[numPlayers + 1];
                for (int p = 1; p <= numPlayers; ++p) {
                    ExponentialMovingAverage tracker = null;
                    this.currentGameDurationTrackerFilenames[p] = this.getFilenameLastCheckpoint("GameDurationTracker_P" + p, "bin");
                    this.lastCheckpoint = Math.min(this.lastCheckpoint, this.extractCheckpointFromFilename(this.currentGameDurationTrackerFilenames[p], "GameDurationTracker_P" + p, "bin"));
                    if (this.currentGameDurationTrackerFilenames[p] == null) {
                        tracker = new ExponentialMovingAverage();
                        this.logLine(logWriter, "starting with new tracker for average game duration");
                    }
                    else {
                        try (final ObjectInputStream reader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentGameDurationTrackerFilenames[p])))) {
                            tracker = (ExponentialMovingAverage)reader.readObject();
                        }
                        catch (IOException | ClassNotFoundException ex2) {
                            ex2.printStackTrace();
                        }
                        this.logLine(logWriter, "continuing with average game duration tracker loaded from " + this.currentGameDurationTrackerFilenames[p]);
                    }
                    trackers[p] = tracker;
                }
                return trackers;
            }
            
            private LinearFunction[] prepareCrossEntropyFunctions(final FeatureSet[] featureSets) {
                final LinearFunction[] linearFunctions = new LinearFunction[numPlayers + 1];
                for (int p = 1; p <= numPlayers; ++p) {
                    this.currentPolicyWeightsCEFilenames[p] = this.getFilenameLastCheckpoint("PolicyWeightsCE_P" + p, "txt");
                    this.lastCheckpoint = Math.min(this.lastCheckpoint, this.extractCheckpointFromFilename(this.currentPolicyWeightsCEFilenames[p], "PolicyWeightsCE_P" + p, "txt"));
                    LinearFunction linearFunction;
                    if (this.currentPolicyWeightsCEFilenames[p] == null) {
                        linearFunction = new LinearFunction(new FVector(featureSets[p].getNumFeatures()));
                        this.logLine(logWriter, "starting with new 0-weights linear function for Cross-Entropy");
                    }
                    else {
                        linearFunction = LinearFunction.fromFile(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentPolicyWeightsCEFilenames[p]);
                        this.logLine(logWriter, "continuing with Selection policy weights loaded from " + this.currentPolicyWeightsCEFilenames[p]);
                        try {
                            String featureSetFilepath = new File(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentPolicyWeightsCEFilenames[p]).getParent();
                            featureSetFilepath = featureSetFilepath + File.separator + linearFunction.featureSetFile();
                            if (!new File(featureSetFilepath).getCanonicalPath().equals(new File(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentFeatureSetFilenames[p]).getCanonicalPath())) {
                                System.err.println("Warning: policy weights were saved for feature set " + featureSetFilepath + ", but we are now using " + this.currentFeatureSetFilenames[p]);
                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    linearFunctions[p] = linearFunction;
                }
                return linearFunctions;
            }
            
            private LinearFunction[] prepareTSPGFunctions(final FeatureSet[] featureSets, final LinearFunction[] crossEntropyFunctions) {
                final LinearFunction[] linearFunctions = new LinearFunction[numPlayers + 1];
                if (ExpertIteration.this.trainTSPG) {
                    for (int p = 1; p <= numPlayers; ++p) {
                        this.currentPolicyWeightsTSPGFilenames[p] = this.getFilenameLastCheckpoint("PolicyWeightsTSPG_P" + p, "txt");
                        this.lastCheckpoint = Math.min(this.lastCheckpoint, this.extractCheckpointFromFilename(this.currentPolicyWeightsTSPGFilenames[p], "PolicyWeightsTSPG_P" + p, "txt"));
                        LinearFunction linearFunction;
                        if (this.currentPolicyWeightsTSPGFilenames[p] == null) {
                            linearFunction = new BoostedLinearFunction(new FVector(featureSets[p].getNumFeatures()), crossEntropyFunctions[p]);
                            this.logLine(logWriter, "starting with new 0-weights linear function for TSPG");
                        }
                        else {
                            linearFunction = BoostedLinearFunction.boostedFromFile(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentPolicyWeightsTSPGFilenames[p], crossEntropyFunctions[p]);
                            this.logLine(logWriter, "continuing with Selection policy weights loaded from " + this.currentPolicyWeightsTSPGFilenames[p]);
                            try {
                                String featureSetFilepath = new File(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentPolicyWeightsTSPGFilenames[p]).getParent();
                                featureSetFilepath = featureSetFilepath + File.separator + linearFunction.featureSetFile();
                                if (!new File(featureSetFilepath).getCanonicalPath().equals(new File(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentFeatureSetFilenames[p]).getCanonicalPath())) {
                                    System.err.println("Warning: policy weights were saved for feature set " + featureSetFilepath + ", but we are now using " + this.currentFeatureSetFilenames[p]);
                                }
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        linearFunctions[p] = linearFunction;
                    }
                }
                return linearFunctions;
            }
            
            private LinearFunction[] prepareCEExploreFunctions(final FeatureSet[] featureSets) {
                final LinearFunction[] linearFunctions = new LinearFunction[numPlayers + 1];
                for (int p = 1; p <= numPlayers; ++p) {
                    this.currentPolicyWeightsCEEFilenames[p] = this.getFilenameLastCheckpoint("PolicyWeightsCEE_P" + p, "txt");
                    this.lastCheckpoint = Math.min(this.lastCheckpoint, this.extractCheckpointFromFilename(this.currentPolicyWeightsCEEFilenames[p], "PolicyWeightsCEE_P" + p, "txt"));
                    LinearFunction linearFunction;
                    if (this.currentPolicyWeightsCEEFilenames[p] == null) {
                        linearFunction = new LinearFunction(new FVector(featureSets[p].getNumFeatures()));
                        this.logLine(logWriter, "starting with new 0-weights linear function for Cross-Entropy Exploration");
                    }
                    else {
                        linearFunction = LinearFunction.fromFile(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentPolicyWeightsCEEFilenames[p]);
                        this.logLine(logWriter, "continuing with Selection policy weights loaded from " + this.currentPolicyWeightsCEEFilenames[p]);
                        try {
                            String featureSetFilepath = new File(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentPolicyWeightsCEEFilenames[p]).getParent();
                            featureSetFilepath = featureSetFilepath + File.separator + linearFunction.featureSetFile();
                            if (!new File(featureSetFilepath).getCanonicalPath().equals(new File(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentFeatureSetFilenames[p]).getCanonicalPath())) {
                                System.err.println("Warning: CE Exploration policy weights were saved for feature set " + featureSetFilepath + ", but we are now using " + this.currentFeatureSetFilenames[p]);
                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    linearFunctions[p] = linearFunction;
                }
                return linearFunctions;
            }
            
            private Heuristics prepareValueFunction() {
                if (ExpertIteration.this.noValueLearning) {
                    return null;
                }
                Heuristics valueFunction = null;
                this.currentValueFunctionFilename = this.getFilenameLastCheckpoint("ValueFunction", "txt");
                this.lastCheckpoint = Math.min(this.lastCheckpoint, this.extractCheckpointFromFilename(this.currentValueFunctionFilename, "ValueFunction", "txt"));
                final Report report = new Report();
                if (this.currentValueFunctionFilename == null) {
                    if (ExpertIteration.this.bestAgentsDataDir != null) {
                        try {
                            final String descr = FileHandling.loadTextContentsFromFile(ExpertIteration.this.bestAgentsDataDir + "/BestHeuristics.txt");
                            valueFunction = (Heuristics)Compiler.compileObject(descr, "metadata.ai.heuristics.Heuristics", report);
                            valueFunction.init(game);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        valueFunction = game.metadata().ai().heuristics();
                        valueFunction.init(game);
                        this.logLine(logWriter, "starting with new initial value function from .lud metadata");
                    }
                }
                else {
                    try {
                        final String descr = FileHandling.loadTextContentsFromFile(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentValueFunctionFilename);
                        valueFunction = (Heuristics)Compiler.compileObject(descr, "metadata.ai.heuristics.Heuristics", report);
                        valueFunction.init(game);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.logLine(logWriter, "continuing with value function from " + ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentValueFunctionFilename);
                }
                return valueFunction;
            }
            
            private FeatureSet[] prepareFeatureSets() {
                final FeatureSet[] featureSets = new FeatureSet[numPlayers + 1];
                final TIntArrayList newlyCreated = new TIntArrayList();
                for (int p = 1; p <= numPlayers; ++p) {
                    this.currentFeatureSetFilenames[p] = this.getFilenameLastCheckpoint("FeatureSet_P" + p, "fs");
                    this.lastCheckpoint = Math.min(this.lastCheckpoint, this.extractCheckpointFromFilename(this.currentFeatureSetFilenames[p], "FeatureSet_P" + p, "fs"));
                    FeatureSet featureSet;
                    if (this.currentFeatureSetFilenames[p] == null) {
                        final AtomicFeatureGenerator atomicFeatures = new AtomicFeatureGenerator(game, 2, 4);
                        featureSet = new FeatureSet(atomicFeatures.getFeatures());
                        newlyCreated.add(p);
                        this.logLine(logWriter, "starting with new initial feature set for Player " + p);
                        this.logLine(logWriter, "num atomic features = " + featureSet.getNumFeatures());
                    }
                    else {
                        featureSet = new FeatureSet(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentFeatureSetFilenames[p]);
                        this.logLine(logWriter, "continuing with feature set loaded from " + ExpertIteration.this.outDir.getAbsolutePath() + File.separator + this.currentFeatureSetFilenames[p] + " for Player " + p);
                    }
                    if (featureSet.getNumFeatures() == 0) {
                        System.err.println("ERROR: Feature Set has 0 features!");
                        this.logLine(logWriter, "Training with 0 features makes no sense, interrupting experiment.");
                        this.interrupted = true;
                    }
                    featureSet.instantiateFeatures(game, new int[] { p }, null);
                    featureSets[p] = featureSet;
                }
                if (newlyCreated.size() > 0) {
                    final long[][][] frequencies = new long[numPlayers + 1][][];
                    for (int p2 = 1; p2 <= numPlayers; ++p2) {
                        final int numAtomicFeatures = featureSets[p2].getNumFeatures();
                        frequencies[p2] = new long[numAtomicFeatures][numAtomicFeatures];
                    }
                    final Trial trial = new Trial(game);
                    final Context context = new Context(game, trial);
                    final long pruningGamesStartTime = System.currentTimeMillis();
                    final long endTime = pruningGamesStartTime + ExpertIteration.this.maxNumPruningSeconds * 1000L;
                    for (int gameCounter = 0; gameCounter < ExpertIteration.this.numPruningGames && System.currentTimeMillis() <= endTime; ++gameCounter) {
                        game.start(context);
                        int numActions = 0;
                        while (!context.trial().over()) {
                            final FastArrayList<Move> legal = game.moves(context).moves();
                            final int mover = context.state().mover();
                            if (newlyCreated.contains(mover)) {
                                final FeatureSet featureSet2 = featureSets[mover];
                                final List<TIntArrayList> sparseFeatureVectors = featureSet2.computeSparseFeatureVectors(context, legal, false);
                                for (final TIntArrayList sparse : sparseFeatureVectors) {
                                    for (int i = 0; i < sparse.size(); ++i) {
                                        final int firstFeature = sparse.getQuick(i);
                                        final long[] array = frequencies[mover][firstFeature];
                                        final int n = firstFeature;
                                        ++array[n];
                                        for (int j = i + 1; j < sparse.size(); ++j) {
                                            final int secondFeature = sparse.getQuick(j);
                                            final long[] array2 = frequencies[mover][firstFeature];
                                            final int n2 = secondFeature;
                                            ++array2[n2];
                                            final long[] array3 = frequencies[mover][secondFeature];
                                            final int n3 = firstFeature;
                                            ++array3[n3];
                                        }
                                    }
                                }
                            }
                            final int r = ThreadLocalRandom.current().nextInt(legal.size());
                            game.apply(context, legal.get(r));
                            ++numActions;
                        }
                    }
                    for (int f = 0; f < newlyCreated.size(); ++f) {
                        final int p3 = newlyCreated.getQuick(f);
                        final TIntArrayList featuresToRemove = new TIntArrayList();
                        final FeatureSet featureSet3 = featureSets[p3];
                        final int numAtomicFeatures2 = featureSet3.getNumFeatures();
                        for (int k = 0; k < numAtomicFeatures2; ++k) {
                            if (!featuresToRemove.contains(k)) {
                                final long soloCount = frequencies[p3][k][k];
                                if (soloCount >= ExpertIteration.this.pruneInitFeaturesThreshold) {
                                    for (int l = k + 1; l < numAtomicFeatures2; ++l) {
                                        if (!featuresToRemove.contains(l)) {
                                            if (soloCount == frequencies[p3][k][l] && soloCount == frequencies[p3][l][l]) {
                                                final Feature firstFeature2 = featureSet3.features()[k];
                                                final Feature secondFeature2 = featureSet3.features()[l];
                                                final Pattern a = firstFeature2.pattern();
                                                final Pattern b = secondFeature2.pattern();
                                                boolean keepFirst = true;
                                                if (b.featureElements().size() < a.featureElements().size()) {
                                                    keepFirst = false;
                                                }
                                                else {
                                                    int sumWalkLengthsA = 0;
                                                    for (final FeatureElement el : a.featureElements()) {
                                                        if (el instanceof RelativeFeatureElement) {
                                                            final RelativeFeatureElement rel = (RelativeFeatureElement)el;
                                                            sumWalkLengthsA += rel.walk().steps().size();
                                                        }
                                                    }
                                                    int sumWalkLengthsB = 0;
                                                    for (final FeatureElement el2 : b.featureElements()) {
                                                        if (el2 instanceof RelativeFeatureElement) {
                                                            final RelativeFeatureElement rel2 = (RelativeFeatureElement)el2;
                                                            sumWalkLengthsB += rel2.walk().steps().size();
                                                        }
                                                    }
                                                    if (sumWalkLengthsB < sumWalkLengthsA) {
                                                        keepFirst = false;
                                                    }
                                                }
                                                if (keepFirst) {
                                                    featuresToRemove.add(l);
                                                }
                                                else {
                                                    featuresToRemove.add(k);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        final List<Feature> keepFeatures = new ArrayList<>();
                        for (int m = 0; m < numAtomicFeatures2; ++m) {
                            if (!featuresToRemove.contains(m)) {
                                keepFeatures.add(featureSet3.features()[m]);
                            }
                        }
                        final FeatureSet newFeatureSet = new FeatureSet(keepFeatures);
                        newFeatureSet.instantiateFeatures(game, new int[] { p3 }, null);
                        featureSets[p3] = newFeatureSet;
                        this.logLine(logWriter, "Finished pruning atomic feature set for Player " + p3);
                        this.logLine(logWriter, "Num atomic features after pruning = " + newFeatureSet.getNumFeatures());
                    }
                }
                return featureSets;
            }
            
            public FeatureSet expandFeatureSetCorrelationBased(final ExItExperience[] batch, final FeatureSet featureSet, final SoftmaxPolicy crossEntropyPolicy, final Game g, final int featureDiscoveryMaxNumFeatureInstances) {
                int numCases = 0;
                final TObjectIntHashMap<CombinableFeatureInstancePair> featurePairActivations = new TObjectIntHashMap<>(10, 0.5f, 0);
                final TObjectDoubleHashMap<CombinableFeatureInstancePair> errorSums = new TObjectDoubleHashMap<>(10, 0.5f, 0.0);
                double sumErrors = 0.0;
                double sumSquaredErrors = 0.0;
                final HashMap<CombinableFeatureInstancePair, Double> selectionErrorSums = new HashMap<>();
                final double sumSelectionErrors = 0.0;
                final double sumSquaredSelectionErrors = 0.0;
                final HashMap<CombinableFeatureInstancePair, Double> playoutErrorSums = new HashMap<>();
                final double sumPlayoutErrors = 0.0;
                final double sumSquaredPlayoutErrors = 0.0;
                for (final ExItExperience sample : batch) {
                    final List<TIntArrayList> sparseFeatureVectors = featureSet.computeSparseFeatureVectors(sample.state().state(), sample.state().lastDecisionMove(), sample.moves(), false);
                    final FVector errors = crossEntropyPolicy.computeDistributionErrors(crossEntropyPolicy.computeDistribution(sparseFeatureVectors, sample.state().state().mover()), sample.expertDistribution());
                    final Set<Feature> existingFeatures = new HashSet<>((int) Math.ceil(featureSet.getNumFeatures() / 0.75f), 0.75f);
                    for (final Feature feature : featureSet.features()) {
                        existingFeatures.add(feature);
                    }
                    for (int a = 0; a < sample.moves().size(); ++a) {
                        ++numCases;
                        final Set<CombinableFeatureInstancePair> observedCasePairs = new HashSet<>(256, 0.75f);
                        List<FeatureInstance> activeInstances = featureSet.getActiveFeatureInstances(sample.state().state(), FeatureUtils.fromPos(sample.state().lastDecisionMove()), FeatureUtils.toPos(sample.state().lastDecisionMove()), FeatureUtils.fromPos(sample.moves().get(a)), FeatureUtils.toPos(sample.moves().get(a)), sample.moves().get(a).mover());
                        if (activeInstances.size() > featureDiscoveryMaxNumFeatureInstances) {
                            activeInstances.sort((instanceA, instanceB) -> {
                                final int featureIdxA = instanceA.feature().featureSetIndex();
                                final int featureIdxB = instanceB.feature().featureSetIndex();
                                final float absWeightA = Math.abs(crossEntropyPolicy.linearFunction(sample.state().state().mover()).effectiveParams().get(featureIdxA));
                                final float absWeightB = Math.abs(crossEntropyPolicy.linearFunction(sample.state().state().mover()).effectiveParams().get(featureIdxB));
                                if (absWeightA == absWeightB) {
                                    return 0;
                                }
                                if (absWeightA > absWeightB) {
                                    return -1;
                                }
                                return 1;
                            });
                            activeInstances = activeInstances.subList(0, featureDiscoveryMaxNumFeatureInstances);
                        }
                        final int numActiveInstances = activeInstances.size();
                        final float error = errors.get(a);
                        sumErrors += error;
                        sumSquaredErrors += error * error;
                        for (int i = 0; i < numActiveInstances; ++i) {
                            final FeatureInstance instanceI = activeInstances.get(i);
                            final CombinableFeatureInstancePair combinedSelf = new CombinableFeatureInstancePair( instanceI, instanceI);
                            if (!observedCasePairs.contains(combinedSelf)) {
                                featurePairActivations.put(combinedSelf, featurePairActivations.get(combinedSelf) + 1);
                                errorSums.put(combinedSelf, errorSums.get(combinedSelf) + error);
                                observedCasePairs.add(combinedSelf);
                            }
                            for (int j = i + 1; j < numActiveInstances; ++j) {
                                final FeatureInstance instanceJ = activeInstances.get(j);
                                final CombinableFeatureInstancePair combined = new CombinableFeatureInstancePair( instanceI, instanceJ);
                                if (!existingFeatures.contains(combined.combinedFeature) && !observedCasePairs.contains(combined)) {
                                    featurePairActivations.put(combined, featurePairActivations.get(combined) + 1);
                                    errorSums.put(combined, errorSums.get(combined) + error);
                                    observedCasePairs.add(combined);
                                }
                            }
                        }
                    }
                }
                if (sumErrors == 0.0 || sumSquaredErrors == 0.0) {
                    return null;
                }
                final List<ScoredPair> scoredPairs = new ArrayList<>(featurePairActivations.size());
                double bestScore = Double.NEGATIVE_INFINITY;
                int bestPairIdx = -1;
                for (final CombinableFeatureInstancePair pair : featurePairActivations.keySet()) {
                    if (!pair.a.equals(pair.b)) {
                        final int actsI = featurePairActivations.get(new CombinableFeatureInstancePair(pair.a, pair.a));
                        final int actsJ = featurePairActivations.get(new CombinableFeatureInstancePair(pair.b, pair.b));
                        final int pairActs = featurePairActivations.get(pair);
                        if (pairActs == numCases) {
                            continue;
                        }
                        if (actsI == numCases) {
                            continue;
                        }
                        if (actsJ == numCases) {
                            continue;
                        }
                        final double pairErrorSum = errorSums.get(pair);
                        final double errorCorr = (numCases * pairErrorSum - pairActs * sumErrors) / (Math.sqrt(pairActs * (numCases - pairActs)) * Math.sqrt(numCases * sumSquaredErrors - sumErrors * sumErrors));
                        final double featureCorrI = pairActs * (numCases - actsI) / (Math.sqrt(pairActs * (numCases - pairActs)) * Math.sqrt(actsI * (numCases - actsI)));
                        final double featureCorrJ = pairActs * (numCases - actsJ) / (Math.sqrt(pairActs * (numCases - pairActs)) * Math.sqrt(actsJ * (numCases - actsJ)));
                        final double worstFeatureCorr = Math.max(Math.abs(featureCorrI), Math.abs(featureCorrJ));
                        final double score = Math.abs(errorCorr) * (1.0 - worstFeatureCorr);
                        if (Double.isNaN(score)) {
                            continue;
                        }
                        scoredPairs.add(new ScoredPair(pair, score));
                        if (score <= bestScore) {
                            continue;
                        }
                        bestScore = score;
                        bestPairIdx = scoredPairs.size() - 1;
                    }
                }
                while (scoredPairs.size() > 0) {
                    final ScoredPair bestPair = scoredPairs.remove(bestPairIdx);
                    final FeatureSet newFeatureSet = featureSet.createExpandedFeatureSet(g, bestPair.pair.a, bestPair.pair.b);
                    if (newFeatureSet != null) {
                        final int actsI = featurePairActivations.get(new CombinableFeatureInstancePair( bestPair.pair.a, bestPair.pair.a));
                        final int actsJ = featurePairActivations.get(new CombinableFeatureInstancePair( bestPair.pair.b, bestPair.pair.b));
                        final int pairActs = featurePairActivations.get(new CombinableFeatureInstancePair( bestPair.pair.a, bestPair.pair.b));
                        final double pairErrorSum = errorSums.get(new CombinableFeatureInstancePair( bestPair.pair.a, bestPair.pair.b));
                        final double errorCorr = (numCases * pairErrorSum - pairActs * sumErrors) / (Math.sqrt(numCases * pairActs - pairActs * pairActs) * Math.sqrt(numCases * sumSquaredErrors - sumErrors * sumErrors));
                        final double featureCorrI = (numCases * pairActs - pairActs * actsI) / (Math.sqrt(numCases * pairActs - pairActs * pairActs) * Math.sqrt(numCases * actsI - actsI * actsI));
                        final double featureCorrJ = (numCases * pairActs - pairActs * actsJ) / (Math.sqrt(numCases * pairActs - pairActs * pairActs) * Math.sqrt(numCases * actsJ - actsJ * actsJ));
                        this.logLine(logWriter, "New feature added!");
                        this.logLine(logWriter, "new feature = " + newFeatureSet.features()[newFeatureSet.getNumFeatures() - 1]);
                        this.logLine(logWriter, "active feature A = " + bestPair.pair.a.feature());
                        this.logLine(logWriter, "rot A = " + bestPair.pair.a.rotation());
                        this.logLine(logWriter, "ref A = " + bestPair.pair.a.reflection());
                        this.logLine(logWriter, "anchor A = " + bestPair.pair.a.anchorSite());
                        this.logLine(logWriter, "active feature B = " + bestPair.pair.b.feature());
                        this.logLine(logWriter, "rot B = " + bestPair.pair.b.rotation());
                        this.logLine(logWriter, "ref B = " + bestPair.pair.b.reflection());
                        this.logLine(logWriter, "anchor B = " + bestPair.pair.b.anchorSite());
                        this.logLine(logWriter, "score = " + bestPair.score);
                        this.logLine(logWriter, "correlation with errors = " + errorCorr);
                        this.logLine(logWriter, "correlation with first constituent = " + featureCorrI);
                        this.logLine(logWriter, "correlation with second constituent = " + featureCorrJ);
                        this.logLine(logWriter, "observed pair of instances " + pairActs + " times");
                        this.logLine(logWriter, "observed first constituent " + actsI + " times");
                        this.logLine(logWriter, "observed second constituent " + actsJ + " times");
                        return newFeatureSet;
                    }
                    bestScore = Double.NEGATIVE_INFINITY;
                    bestPairIdx = -1;
                    for (int k = 0; k < scoredPairs.size(); ++k) {
                        if (scoredPairs.get(k).score > bestScore) {
                            bestScore = scoredPairs.get(k).score;
                            bestPairIdx = k;
                        }
                    }
                }
                return null;
            }
            
            private long computeNextCheckpoint() {
                if (this.lastCheckpoint < 0L) {
                    return 0L;
                }
                return this.lastCheckpoint + ExpertIteration.this.checkpointFrequency;
            }
            
            private String createCheckpointFilename(final String baseFilename, final long checkpoint, final String extension) {
                final String format = (ExpertIteration.this.checkpointType == CheckpointTypes.Game) ? "%s_%05d.%s" : "%s_%08d.%s";
                return String.format(format, baseFilename, checkpoint, extension);
            }
            
            private int extractCheckpointFromFilename(final String filename, final String baseFilename, final String extension) {
                if (filename == null) {
                    return -1;
                }
                final String checkpoint = filename.substring((baseFilename + "_").length(), filename.length() - ("." + extension).length());
                return Integer.parseInt(checkpoint);
            }
            
            private String getFilenameLastCheckpoint(final String baseFilename, final String extension) {
                if (ExpertIteration.this.outDir == null) {
                    return null;
                }
                final String[] filenames = ExpertIteration.this.outDir.list();
                int maxCheckpoint = -1;
                for (final String filename : filenames) {
                    if (filename.startsWith(baseFilename + "_") && filename.endsWith("." + extension)) {
                        final int checkpoint = this.extractCheckpointFromFilename(filename, baseFilename, extension);
                        if (checkpoint > maxCheckpoint) {
                            maxCheckpoint = checkpoint;
                        }
                    }
                }
                if (maxCheckpoint < 0) {
                    return null;
                }
                return this.createCheckpointFilename(baseFilename, maxCheckpoint, extension);
            }
            
            private void saveCheckpoints(final int gameCounter, final long weightsUpdateCounter, final FeatureSet[] featureSets, final LinearFunction[] crossEntropyFunctions, final LinearFunction[] tspgFunctions, final LinearFunction[] ceExploreFunctions, final Heuristics valueFunction, final ExperienceBuffer[] experienceBuffers, final Optimiser[] ceOptimisers, final Optimiser[] tspgOptimisers, final Optimiser[] ceeOptimisers, final Optimiser valueFunctionOptimiser, final ExponentialMovingAverage[] avgGameDurations, final boolean forced) {
                long nextCheckpoint = this.computeNextCheckpoint();
                if (ExpertIteration.this.checkpointType == CheckpointTypes.Game) {
                    if (!forced && gameCounter < nextCheckpoint) {
                        return;
                    }
                    nextCheckpoint = gameCounter;
                }
                else if (ExpertIteration.this.checkpointType == CheckpointTypes.WeightUpdate) {
                    if (!forced && weightsUpdateCounter < nextCheckpoint) {
                        return;
                    }
                    nextCheckpoint = weightsUpdateCounter;
                }
                for (int p = 1; p <= numPlayers; ++p) {
                    final String featureSetFilename = this.createCheckpointFilename("FeatureSet_P" + p, nextCheckpoint, "fs");
                    featureSets[p].toFile(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + featureSetFilename);
                    this.currentFeatureSetFilenames[p] = featureSetFilename;
                    final String ceWeightsFilename = this.createCheckpointFilename("PolicyWeightsCE_P" + p, nextCheckpoint, "txt");
                    crossEntropyFunctions[p].writeToFile(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + ceWeightsFilename, new String[] { this.currentFeatureSetFilenames[p] });
                    this.currentPolicyWeightsCEFilenames[p] = ceWeightsFilename;
                    if (ExpertIteration.this.trainTSPG) {
                        final String tspgWeightsFilename = this.createCheckpointFilename("PolicyWeightsTSPG_P" + p, nextCheckpoint, "txt");
                        tspgFunctions[p].writeToFile(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + tspgWeightsFilename, new String[] { this.currentFeatureSetFilenames[p] });
                        this.currentPolicyWeightsTSPGFilenames[p] = tspgWeightsFilename;
                    }
                    if (ExpertIteration.this.ceExplore && !ExpertIteration.this.ceExploreUniform) {
                        final String ceExploreWeightsFilename = this.createCheckpointFilename("PolicyWeightsCEE_P" + p, nextCheckpoint, "txt");
                        ceExploreFunctions[p].writeToFile(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + ceExploreWeightsFilename, new String[] { this.currentFeatureSetFilenames[p] });
                        this.currentPolicyWeightsCEEFilenames[p] = ceExploreWeightsFilename;
                    }
                    if (valueFunction != null) {
                        final String valueFunctionFilename = this.createCheckpointFilename("ValueFunction", nextCheckpoint, "txt");
                        valueFunction.toFile(game, ExpertIteration.this.outDir.getAbsolutePath() + File.separator + valueFunctionFilename);
                    }
                    if (forced) {
                        final String experienceBufferFilename = this.createCheckpointFilename("ExperienceBuffer_P" + p, nextCheckpoint, "buf");
                        experienceBuffers[p].writeToFile(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + experienceBufferFilename);
                        final String ceOptimiserFilename = this.createCheckpointFilename("OptimiserCE_P" + p, nextCheckpoint, "opt");
                        ceOptimisers[p].writeToFile(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + ceOptimiserFilename);
                        this.currentOptimiserCEFilenames[p] = ceOptimiserFilename;
                        if (ExpertIteration.this.trainTSPG) {
                            final String tspgOptimiserFilename = this.createCheckpointFilename("OptimiserTSPG_P" + p, nextCheckpoint, "opt");
                            tspgOptimisers[p].writeToFile(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + tspgOptimiserFilename);
                            this.currentOptimiserTSPGFilenames[p] = tspgOptimiserFilename;
                        }
                        if (ExpertIteration.this.ceExplore && !ExpertIteration.this.ceExploreUniform) {
                            final String ceExploreOptimiserFilename = this.createCheckpointFilename("OptimiserCEE_P" + p, nextCheckpoint, "opt");
                            ceeOptimisers[p].writeToFile(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + ceExploreOptimiserFilename);
                            this.currentOptimiserCEEFilenames[p] = ceExploreOptimiserFilename;
                        }
                        final String gameDurationTrackerFilename = this.createCheckpointFilename("GameDurationTracker_P" + p, nextCheckpoint, "bin");
                        avgGameDurations[p].writeToFile(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + gameDurationTrackerFilename);
                        this.currentGameDurationTrackerFilenames[p] = gameDurationTrackerFilename;
                    }
                }
                if (forced) {
                    final String valueOptimiserFilename = this.createCheckpointFilename("OptimiserValue", nextCheckpoint, "opt");
                    valueFunctionOptimiser.writeToFile(ExpertIteration.this.outDir.getAbsolutePath() + File.separator + valueOptimiserFilename);
                    this.currentOptimiserValueFilename = valueOptimiserFilename;
                }
                this.lastCheckpoint = nextCheckpoint;
            }
            
            @Override
            public void logLine(final PrintWriter log, final String line) {
                if (!ExpertIteration.this.noLogging) {
                    super.logLine(log, line);
                }
            }
            
            final class ScoredPair
            {
                public final CombinableFeatureInstancePair pair;// = ExpertIteration.this.useGUI;
                public final double score;// = ExpertIteration.this.maxWallTime;

                public ScoredPair(CombinableFeatureInstancePair pair, double score) {
                    this.pair = pair; this.score= score;
                }
            }
            
            final class CombinableFeatureInstancePair
            {
                public final FeatureInstance a;// = ExpertIteration.this.maxWallTime;
                public final FeatureInstance b;// = numPlayers;
                protected final Feature combinedFeature;
                private int cachedHash;
                
                public CombinableFeatureInstancePair(final FeatureInstance a, final FeatureInstance b) {
                    Game g = game;
                    this.a = a; this.b = b;
                    this.cachedHash = Integer.MIN_VALUE;
                    if (a.feature().featureSetIndex() < b.feature().featureSetIndex()) {
                        this.combinedFeature = Feature.combineFeatures(g, a, b);
                    }
                    else if (b.feature().featureSetIndex() < a.feature().featureSetIndex()) {
                        this.combinedFeature = Feature.combineFeatures(g, b, a);
                    }
                    else if (a.reflection() > b.reflection()) {
                        this.combinedFeature = Feature.combineFeatures(g, a, b);
                    }
                    else if (b.reflection() > a.reflection()) {
                        this.combinedFeature = Feature.combineFeatures(g, b, a);
                    }
                    else if (a.rotation() < b.rotation()) {
                        this.combinedFeature = Feature.combineFeatures(g, a, b);
                    }
                    else if (b.rotation() < a.rotation()) {
                        this.combinedFeature = Feature.combineFeatures(g, b, a);
                    }
                    else if (a.anchorSite() < b.anchorSite()) {
                        this.combinedFeature = Feature.combineFeatures(g, a, b);
                    }
                    else if (b.anchorSite() < a.anchorSite()) {
                        this.combinedFeature = Feature.combineFeatures(g, b, a);
                    }
                    else {
                        this.combinedFeature = Feature.combineFeatures(g, a, b);
                    }
                }
                
                @Override
                public boolean equals(final Object other) {
                    return other instanceof CombinableFeatureInstancePair && this.combinedFeature.equals(((CombinableFeatureInstancePair)other).combinedFeature);
                }
                
                @Override
                public int hashCode() {
                    if (this.cachedHash == Integer.MIN_VALUE) {
                        this.cachedHash = this.combinedFeature.hashCode();
                    }
                    return this.cachedHash;
                }
                
                @Override
                public String toString() {
                    return this.combinedFeature + " (from " + this.a + " and " + this.b + ")";
                }
            }
        };
    }
    
    private PrintWriter createLogWriter() {
        if (this.outDir != null && !this.noLogging) {
            final String nextLogFilepath = ExperimentFileUtils.getNextFilepath(this.outDir.getAbsolutePath() + File.separator + "ExIt", "log");
            new File(nextLogFilepath).getParentFile().mkdirs();
            try {
                return new PrintWriter(nextLogFilepath, StandardCharsets.UTF_8);
            }
            catch (IOException ex2) {
                ex2.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    public static void main(final String[] args) {
        final CommandLineArgParse argParse = new CommandLineArgParse(true, "Execute a training run from self-play using Expert Iteration.");
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--game").help("Name of the game to play. Should end with \".lud\".").withDefault("board/space/blocking/Amazons.lud").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--game-options").help("Game Options to load.").withDefault(new ArrayList(0)).withNumVals("*").withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--expert-ai").help("Type of AI to use as expert.").withDefault("BEST_AGENT").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String).withLegalVals("BEST_AGENT", "FROM_METADATA", "Biased MCTS"));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--best-agents-data-dir").help("Filepath for directory with best agents data for this game (+ options).").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("-n", "--num-games", "--num-training-games").help("Number of training games to run.").withDefault(200).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--game-length-cap", "--max-num-actions").help("Maximum number of actions that may be taken before a game is terminated as a draw (-1 for no limit).").withDefault(-1).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--thinking-time", "--time", "--seconds").help("Max allowed thinking time per move (in seconds).").withDefault(1.0).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Double));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--iteration-limit", "--iterations").help("Max allowed number of MCTS iterations per move.").withDefault(-1).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--depth-limit").help("Search depth limit (e.g. for Alpha-Beta experts).").withDefault(-1).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--add-feature-every").help("After this many training games, we add a new feature.").withDefault(1).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--batch-size").help("Max size of minibatches in training.").withDefault(30).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--buffer-size", "--experience-buffer-size").help("Max size of the experience buffer.").withDefault(2500).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--update-weights-every").help("After this many moves (decision points) in training games, we update weights.").withDefault(1).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--no-grow-features", "--no-grow-featureset", "--no-grow-feature-set").help("If true, we'll not grow feature set (but still train weights).").withType(CommandLineArgParse.OptionTypes.Boolean));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--train-tspg").help("If true, we'll train a policy on TSPG objective (see COG paper).").withType(CommandLineArgParse.OptionTypes.Boolean));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--ce-optimiser", "--cross-entropy-optimiser").help("Optimiser to use for policy trained on Cross-Entropy loss.").withDefault("RMSProp").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--cee-optimiser", "--cross-entropy-exploration-optimiser").help("Optimiser to use for training Cross-Entropy Exploration policy.").withDefault("RMSProp").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--tspg-optimiser").help("Optimiser to use for policy trained on TSPG objective (see COG paper).").withDefault("RMSProp").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--value-optimiser").help("Optimiser to use for value function optimisation.").withDefault("RMSProp").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--combining-feature-instance-threshold").help("At most this number of feature instances will be taken into account when combining features.").withDefault(75).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--is-episode-durations").help("If true, we'll use importance sampling weights based on episode durations for CE-loss.").withType(CommandLineArgParse.OptionTypes.Boolean));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--prioritized-experience-replay", "--per").help("If true, we'll use prioritized experience replay").withType(CommandLineArgParse.OptionTypes.Boolean));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--ce-explore").help("If true, we'll use extra exploration based on cross-entropy losses").withType(CommandLineArgParse.OptionTypes.Boolean));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--ce-explore-mix").help("Proportion of exploration policy in our behaviour mix").withDefault(0.1f).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Float));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--ce-explore-gamma").help("Discount factor gamma for rewards awarded to CE Explore policy").withDefault(0.99).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Double));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--ce-explore-uniform").help("If true, our CE Explore policy will not be trained, but remain completely uniform").withType(CommandLineArgParse.OptionTypes.Boolean));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--no-ce-explore-is").help("If true, we ignore importance sampling when doing CE Exploration").withType(CommandLineArgParse.OptionTypes.Boolean));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--wis", "--weighted-importance-sampling").help("If true, we use Weighted Importance Sampling instead of Ordinary Importance Sampling for any of the above").withType(CommandLineArgParse.OptionTypes.Boolean));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--no-value-learning").help("If true, we don't do any value function learning.").withType(CommandLineArgParse.OptionTypes.Boolean));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--max-biased-playout-actions", "--max-num-biased-playout-actions").help("Maximum number of actions per playout which we'll bias using features (-1 for no limit).").withDefault(-1).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--no-prune-init-features").help("If true, we will keep full atomic feature set and not prune anything.").withType(CommandLineArgParse.OptionTypes.Boolean));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--prune-init-features-threshold").help("Will only consider pruning features if they have been active at least this many times.").withDefault(50).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--num-pruning-games").help("Number of random games to play out for determining features to prune.").withDefault(1500).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--max-pruning-seconds").help("Maximum number of seconds to spend on random games for pruning initial featureset.").withDefault(60).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--checkpoint-type", "--checkpoints").help("When do we store checkpoints of trained weights?").withDefault(CheckpointTypes.Game.toString()).withNumVals(1).withType(CommandLineArgParse.OptionTypes.String).withLegalVals(Arrays.stream(CheckpointTypes.values()).map((Function<? super CheckpointTypes, ?>)Object::toString).toArray()));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--checkpoint-freq", "--checkpoint-frequency").help("Frequency of checkpoint updates").withDefault(1).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--out-dir", "--output-directory").help("Filepath for output directory").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--no-logging").help("If true, we don't write a bunch of messages to a log file.").withType(CommandLineArgParse.OptionTypes.Boolean));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--useGUI").help("Whether to create a small GUI that can be used to manually interrupt training run. False by default."));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--max-wall-time").help("Max wall time in minutes (or -1 for no limit).").withDefault(-1).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        if (!argParse.parseArguments(args)) {
            return;
        }
        final ExpertIteration exIt = new ExpertIteration(argParse.getValueBool("--useGUI"), argParse.getValueInt("--max-wall-time"));
        exIt.gameName = argParse.getValueString("--game");
        exIt.gameOptions = (List<String>)argParse.getValue("--game-options");
        exIt.expertAI = argParse.getValueString("--expert-ai");
        exIt.bestAgentsDataDir = argParse.getValueString("--best-agents-data-dir");
        exIt.numTrainingGames = argParse.getValueInt("-n");
        exIt.gameLengthCap = argParse.getValueInt("--game-length-cap");
        exIt.thinkingTime = argParse.getValueDouble("--thinking-time");
        exIt.iterationLimit = argParse.getValueInt("--iteration-limit");
        exIt.depthLimit = argParse.getValueInt("--depth-limit");
        exIt.addFeatureEvery = argParse.getValueInt("--add-feature-every");
        exIt.batchSize = argParse.getValueInt("--batch-size");
        exIt.experienceBufferSize = argParse.getValueInt("--buffer-size");
        exIt.updateWeightsEvery = argParse.getValueInt("--update-weights-every");
        exIt.noGrowFeatureSet = argParse.getValueBool("--no-grow-features");
        exIt.trainTSPG = argParse.getValueBool("--train-tspg");
        exIt.crossEntropyOptimiserConfig = argParse.getValueString("--ce-optimiser");
        exIt.ceExploreOptimiserConfig = argParse.getValueString("--cee-optimiser");
        exIt.tspgOptimiserConfig = argParse.getValueString("--tspg-optimiser");
        exIt.valueOptimiserConfig = argParse.getValueString("--value-optimiser");
        exIt.combiningFeatureInstanceThreshold = argParse.getValueInt("--combining-feature-instance-threshold");
        exIt.importanceSamplingEpisodeDurations = argParse.getValueBool("--is-episode-durations");
        exIt.prioritizedExperienceReplay = argParse.getValueBool("--prioritized-experience-replay");
        exIt.ceExplore = argParse.getValueBool("--ce-explore");
        exIt.ceExploreMix = argParse.getValueFloat("--ce-explore-mix");
        exIt.ceExploreGamma = argParse.getValueDouble("--ce-explore-gamma");
        exIt.ceExploreUniform = argParse.getValueBool("--ce-explore-uniform");
        exIt.noCEExploreIS = argParse.getValueBool("--no-ce-explore-is");
        exIt.weightedImportanceSampling = argParse.getValueBool("--wis");
        exIt.noValueLearning = argParse.getValueBool("--no-value-learning");
        exIt.maxNumBiasedPlayoutActions = argParse.getValueInt("--max-num-biased-playout-actions");
        exIt.noPruneInitFeatures = argParse.getValueBool("--no-prune-init-features");
        exIt.pruneInitFeaturesThreshold = argParse.getValueInt("--prune-init-features-threshold");
        exIt.numPruningGames = argParse.getValueInt("--num-pruning-games");
        exIt.maxNumPruningSeconds = argParse.getValueInt("--max-pruning-seconds");
        exIt.checkpointType = CheckpointTypes.valueOf(argParse.getValueString("--checkpoint-type"));
        exIt.checkpointFrequency = argParse.getValueInt("--checkpoint-freq");
        final String outDirFilepath = argParse.getValueString("--out-dir");
        if (outDirFilepath != null) {
            exIt.outDir = new File(outDirFilepath);
        }
        else {
            exIt.outDir = null;
        }
        exIt.noLogging = argParse.getValueBool("--no-logging");
        exIt.startExperiment();
    }
    
    public enum CheckpointTypes
    {
        Game, 
        WeightUpdate
    }
}
