// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.experiments.eval;

import game.Game;
import gnu.trove.list.array.TIntArrayList;
import language.compiler.Compiler;
import main.CommandLineArgParse;
import main.FileHandling;
import main.StringRoutines;
import collections.ListUtils;
import grammar.Report;
import metadata.ai.features.Features;
import metadata.ai.heuristics.Heuristics;
import metadata.ai.misc.BestAgent;
import policies.softmax.SoftmaxPolicy;
import search.mcts.MCTS;
import search.minimax.AlphaBetaSearch;
import util.AI;
import util.Context;
import util.GameLoader;
import util.Trial;
import util.model.Model;
import utils.AIFactory;
import utils.AIUtils;
import utils.experiments.InterruptableExperiment;
import utils.experiments.ResultsSummary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class EvalGate
{
    public String gameName;
    public List<String> gameOptions;
    public int numGames;
    public int gameLengthCap;
    public double thinkingTime;
    public int warmingUpSecs;
    public String evalAgent;
    public List<String> evalFeatureWeightsFilepaths;
    public String evalHeuristicsFilepath;
    public File bestAgentsDataDir;
    public String gateAgentType;
    protected boolean useGUI;
    protected int maxWallTime;
    
    private EvalGate(final boolean useGUI, final int maxWallTime) {
        this.useGUI = useGUI;
        this.maxWallTime = maxWallTime;
    }
    
    private AI createEvalAI() {
        if (this.evalAgent.equals("Alpha-Beta")) {
            return AIFactory.createAI("algorithm=Alpha-Beta;heuristics=" + this.evalHeuristicsFilepath);
        }
        if (this.evalAgent.equals("BiasedMCTS")) {
            final StringBuilder playoutSb = new StringBuilder();
            playoutSb.append("playout=softmax");
            for (int p = 1; p <= this.evalFeatureWeightsFilepaths.size(); ++p) {
                playoutSb.append(",policyweights" + p + "=" + this.evalFeatureWeightsFilepaths.get(p - 1));
            }
            final String agentStr = StringRoutines.join(";", "algorithm=MCTS", "selection=ag0selection", playoutSb.toString(), "final_move=robustchild", "tree_reuse=true", "learned_selection_policy=playout", "friendly_name=BiasedMCTS");
            return AIFactory.createAI(agentStr);
        }
        if (this.evalAgent.equals("BiasedMCTSUniformPlayouts")) {
            final StringBuilder policySb = new StringBuilder();
            policySb.append("learned_selection_policy=softmax");
            for (int p = 1; p <= this.evalFeatureWeightsFilepaths.size(); ++p) {
                policySb.append(",policyweights" + p + "=" + this.evalFeatureWeightsFilepaths.get(p - 1));
            }
            final String agentStr = StringRoutines.join(";", "algorithm=MCTS", "selection=ag0selection", "playout=random", "final_move=robustchild", "tree_reuse=true", policySb.toString(), "friendly_name=BiasedMCTSUniformPlayouts");
            return AIFactory.createAI(agentStr);
        }
        System.err.println("Can't build eval AI: " + this.evalAgent);
        return null;
    }
    
    private AI createGateAI() {
        final String bestAgentDataDirFilepath = this.bestAgentsDataDir.getAbsolutePath().replaceAll(Pattern.quote("\\"), "/");
        final Report report = new Report();
        try {
            if (this.gateAgentType.equals("BestAgent")) {
                final BestAgent bestAgent = (BestAgent)Compiler.compileObject(FileHandling.loadTextContentsFromFile(bestAgentDataDirFilepath + "/BestAgent.txt"), "metadata.ai.misc.BestAgent", report);
                if (bestAgent.agent().equals("AlphaBeta")) {
                    return new AlphaBetaSearch(bestAgentDataDirFilepath + "/BestHeuristics.txt");
                }
                if (bestAgent.agent().equals("AlphaBetaMetadata")) {
                    return new AlphaBetaSearch();
                }
                if (bestAgent.agent().equals("UCT")) {
                    return AIFactory.createAI("UCT");
                }
                if (bestAgent.agent().equals("MC-GRAVE")) {
                    return AIFactory.createAI("MC-GRAVE");
                }
                if (bestAgent.agent().equals("Biased MCTS")) {
                    final Features features = (Features)Compiler.compileObject(FileHandling.loadTextContentsFromFile(bestAgentDataDirFilepath + "/BestFeatures.txt"), "metadata.ai.features.Features", report);
                    return MCTS.createBiasedMCTS(features, true);
                }
                if (bestAgent.agent().equals("Biased MCTS (Uniform Playouts)")) {
                    final Features features = (Features)Compiler.compileObject(FileHandling.loadTextContentsFromFile(bestAgentDataDirFilepath + "/BestFeatures.txt"), "metadata.ai.features.Features", report);
                    return MCTS.createBiasedMCTS(features, false);
                }
                System.err.println("Unrecognised best agent: " + bestAgent.agent());
            }
            else {
                if (this.gateAgentType.equals("Alpha-Beta")) {
                    return new AlphaBetaSearch(bestAgentDataDirFilepath + "/BestHeuristics.txt");
                }
                if (this.gateAgentType.equals("BiasedMCTS")) {
                    final Features features2 = (Features)Compiler.compileObject(FileHandling.loadTextContentsFromFile(bestAgentDataDirFilepath + "/BestFeatures.txt"), "metadata.ai.features.Features", report);
                    if (this.evalAgent.equals("BiasedMCTS")) {
                        return MCTS.createBiasedMCTS(features2, true);
                    }
                    if (this.evalAgent.equals("BiasedMCTSUniformPlayouts")) {
                        return MCTS.createBiasedMCTS(features2, false);
                    }
                    System.err.println("Trying to use Biased MCTS gate when evaluating something other than Biased MCTS!");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("Failed to build gate AI: " + this.gateAgentType);
        return null;
    }
    
    public void startExperiment() {
        final Game game = GameLoader.loadGameFromName(this.gameName, this.gameOptions);
        final int numPlayers = game.players().count();
        if (this.gameLengthCap >= 0) {
            game.setMaxTurns(Math.min(this.gameLengthCap, game.getMaxTurnLimit()));
        }
        final Trial trial = new Trial(game);
        final Context context = new Context(game, trial);
        final List<AI> ais = new ArrayList<>((numPlayers % 2 == 0) ? numPlayers : (numPlayers + 1));
        for (int i = 0; i < numPlayers; i += 2) {
            final AI evalAI = this.createEvalAI();
            evalAI.friendlyName = "EvalAI";
            final AI gateAI = this.createGateAI();
            gateAI.friendlyName = "GateAI";
            ais.add(evalAI);
            ais.add(gateAI);
        }
        final InterruptableExperiment experiment = new InterruptableExperiment(this.useGUI, this.maxWallTime) {
            @Override
            public void runExperiment() {
                int numGamesToPlay = EvalGate.this.numGames;
                List<TIntArrayList> aiListPermutations = new ArrayList<>();
                aiListPermutations = ListUtils.generatePermutations(TIntArrayList.wrap(IntStream.range(0, numPlayers).toArray()));
                if (numGamesToPlay % aiListPermutations.size() != 0) {
                    numGamesToPlay += numGamesToPlay % aiListPermutations.size();
                }
                long stopAt = 0L;
                final long start = System.nanoTime();
                for (double abortAt = start + EvalGate.this.warmingUpSecs * 1.0E9; stopAt < abortAt; stopAt = System.nanoTime()) {
                    game.start(context);
                    game.playout(context, null, 1.0, null, null, -1, -1, -1.0f, ThreadLocalRandom.current());
                }
                System.gc();
                final List<String> agentStrings = new ArrayList<>();
                for (final AI ai : ais) {
                    agentStrings.add(ai.friendlyName);
                }
                final ResultsSummary resultsSummary = new ResultsSummary(game, agentStrings);
                for (int gameCounter = 0; gameCounter < numGamesToPlay; ++gameCounter) {
                    this.checkWallTime(0.05);
                    if (this.interrupted) {
                        break;
                    }
                    final List<AI> currentAIList = new ArrayList<>(numPlayers);
                    final int currentAIsPermutation = gameCounter % aiListPermutations.size();
                    final TIntArrayList currentPlayersPermutation = aiListPermutations.get(currentAIsPermutation);
                    currentAIList.add(null);
                    for (int i = 0; i < currentPlayersPermutation.size(); ++i) {
                        currentAIList.add(ais.get(currentPlayersPermutation.getQuick(i) % ais.size()));
                    }
                    game.start(context);
                    for (int p = 1; p < currentAIList.size(); ++p) {
                        currentAIList.get(p).initAI(game, p);
                    }
                    final Model model = context.model();
                    while (!context.trial().over() && !this.interrupted) {
                        model.startNewStep(context, currentAIList, EvalGate.this.thinkingTime, -1, -1, 0.0);
                    }
                    if (context.trial().over()) {
                        final double[] utilities = AIUtils.agentUtilities(context);
                        final int numMovesPlayed = context.trial().moves().size() - context.trial().numInitialPlacementMoves();
                        final int[] agentPermutation = new int[currentPlayersPermutation.size() + 1];
                        currentPlayersPermutation.toArray(agentPermutation, 0, 1, currentPlayersPermutation.size());
                        resultsSummary.recordResults(agentPermutation, utilities, numMovesPlayed);
                    }
                }
                final double avgEvalScore = resultsSummary.avgScoreForAgentName("EvalAI");
                final double avgGateScore = resultsSummary.avgScoreForAgentName("GateAI");
                System.out.println("----------------------------------");
                System.out.println("Eval Agent = " + EvalGate.this.evalAgent);
                System.out.println("Gate Agent = " + EvalGate.this.gateAgentType);
                System.out.println();
                System.out.println("Eval Agent Score = " + avgEvalScore);
                System.out.println("Gate Agent Score = " + avgGateScore);
                System.out.println("----------------------------------");
                if (avgEvalScore > avgGateScore) {
                    boolean writeBestAgent = false;
                    boolean writeFeatures = false;
                    boolean writeHeuristics = false;
                    switch (EvalGate.this.gateAgentType) {
                        case "BestAgent":
                            writeBestAgent = true;
                            if (EvalGate.this.evalAgent.equals("Alpha-Beta")) {
                                writeHeuristics = true;
                            } else if (EvalGate.this.evalAgent.contains("BiasedMCTS")) {
                                writeFeatures = true;
                            } else {
                                System.err.println("Eval agent is neiter Alpha-Beta nor a variant of BiasedMCTS");
                            }
                            break;
                        case "Alpha-Beta":
                            if (EvalGate.this.evalAgent.equals("Alpha-Beta")) {
                                writeHeuristics = true;
                            } else {
                                System.err.println("evalAgent = " + EvalGate.this.evalAgent + " against gateAgentType = " + EvalGate.this.gateAgentType);
                            }
                            break;
                        case "BiasedMCTS":
                            if (EvalGate.this.evalAgent.contains("BiasedMCTS")) {
                                writeFeatures = true;
                            } else {
                                System.err.println("evalAgent = " + EvalGate.this.evalAgent + " against gateAgentType = " + EvalGate.this.gateAgentType);
                            }
                            break;
                        default:
                            System.err.println("Unrecognised gate agent type: " + EvalGate.this.gateAgentType);
                            break;
                    }
                    final String bestAgentsDataDirPath = EvalGate.this.bestAgentsDataDir.getAbsolutePath().replaceAll(Pattern.quote("\\"), "/");
                    if (writeBestAgent) {
                        final File bestAgentFile = new File(bestAgentsDataDirPath + "/BestAgent.txt");
                        try (final PrintWriter writer = new PrintWriter(bestAgentFile)) {
                            BestAgent bestAgent = switch (EvalGate.this.evalAgent) {
                                case "Alpha-Beta" -> new BestAgent("AlphaBeta");
                                case "BiasedMCTS" -> new BestAgent("Biased MCTS");
                                case "BiasedMCTSUniformPlayouts" -> new BestAgent("Biased MCTS (Uniform Playouts)");
                                default -> null;
                            };
                            System.out.println("Writing new best agent: " + EvalGate.this.evalAgent);
                            writer.println(bestAgent.toString());
                        }
                        catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    if (writeHeuristics) {
                        final File bestHeuristicsFile = new File(bestAgentsDataDirPath + "/BestHeuristics.txt");
                        try (final PrintWriter writer = new PrintWriter(bestHeuristicsFile)) {
                            final String heuristicsStr = FileHandling.loadTextContentsFromFile(EvalGate.this.evalHeuristicsFilepath);
                            final Heuristics heuristics = (Heuristics)Compiler.compileObject(heuristicsStr, "metadata.ai.heuristics.Heuristics", new Report());
                            System.out.println("writing new best heuristics");
                            writer.println(heuristics.toString());
                        }
                        catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    if (writeFeatures) {
                        final File bestFeaturesFile = new File(bestAgentsDataDirPath + "/BestFeatures.txt");
                        final StringBuilder playoutSb = new StringBuilder();
                        playoutSb.append("playout=softmax");
                        for (int p2 = 1; p2 <= EvalGate.this.evalFeatureWeightsFilepaths.size(); ++p2) {
                            playoutSb.append(",policyweights" + p2 + "=" + EvalGate.this.evalFeatureWeightsFilepaths.get(p2 - 1));
                        }
                        final String agentStr = StringRoutines.join(";", "algorithm=MCTS", "selection=ag0selection", playoutSb.toString(), "final_move=robustchild", "tree_reuse=true", "learned_selection_policy=playout", "friendly_name=BiasedMCTS");
                        final MCTS mcts = (MCTS)AIFactory.createAI(agentStr);
                        final SoftmaxPolicy softmax = (SoftmaxPolicy)mcts.playoutStrategy();
                        final Features features = softmax.generateFeaturesMetadata();
                        try (final PrintWriter writer2 = new PrintWriter(bestFeaturesFile)) {
                            System.out.println("writing new best features");
                            writer2.println(features.toString());
                        }
                        catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                }
            }
        };
    }
    
    public static void main(final String[] args) {
        final CommandLineArgParse argParse = new CommandLineArgParse(true, "Gating experiment to test if a newly-trained agent outperforms current best.");
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--game").help("Name of the game to play. Should end with \".lud\".").withDefault("Amazons.lud").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--game-options").help("Game Options to load.").withDefault(new ArrayList(0)).withNumVals("*").withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--eval-agent").help("Agent to be evaluated.").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String).withLegalVals("Alpha-Beta", "BiasedMCTS", "BiasedMCTSUniformPlayouts").setRequired());
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--eval-feature-weights-filepaths").help("Filepaths for feature weights to be evaluated.").withNumVals("*").withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--eval-heuristics-filepath").help("Filepath for heuristics to be evaluated.").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("-n", "--num-games", "--num-eval-games").help("Number of training games to run.").withDefault(200).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--game-length-cap", "--max-num-actions").help("Maximum number of actions that may be taken before a game is terminated as a draw (-1 for no limit).").withDefault(-1).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--thinking-time", "--time", "--seconds").help("Max allowed thinking time per move (in seconds).").withDefault(1.0).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Double));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--warming-up-secs").help("Number of seconds for which to warm up JVM.").withType(CommandLineArgParse.OptionTypes.Int).withNumVals(1).withDefault(60));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--best-agents-data-dir").help("Filepath for directory containing data on best agents").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String).setRequired());
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--gate-agent-type").help("Type of gate agent against which we wish to evaluate.").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String).setRequired().withLegalVals("BestAgent", "Alpha-Beta", "BiasedMCTS"));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--useGUI").help("Whether to create a small GUI that can be used to manually interrupt training run. False by default."));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--max-wall-time").help("Max wall time in minutes (or -1 for no limit).").withDefault(-1).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        if (!argParse.parseArguments(args)) {
            return;
        }
        final EvalGate eval = new EvalGate(argParse.getValueBool("--useGUI"), argParse.getValueInt("--max-wall-time"));
        eval.gameName = argParse.getValueString("--game");
        eval.gameOptions = (List<String>)argParse.getValue("--game-options");
        eval.evalAgent = argParse.getValueString("--eval-agent");
        eval.evalFeatureWeightsFilepaths = (List<String>)argParse.getValue("--eval-feature-weights-filepaths");
        eval.evalHeuristicsFilepath = argParse.getValueString("--eval-heuristics-filepath");
        eval.numGames = argParse.getValueInt("-n");
        eval.gameLengthCap = argParse.getValueInt("--game-length-cap");
        eval.thinkingTime = argParse.getValueDouble("--thinking-time");
        eval.warmingUpSecs = argParse.getValueInt("--warming-up-secs");
        eval.bestAgentsDataDir = new File(argParse.getValueString("--best-agents-data-dir"));
        eval.gateAgentType = argParse.getValueString("--gate-agent-type");
        eval.startExperiment();
    }
}
