// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.experiments;

import game.Game;
import gnu.trove.list.array.TIntArrayList;
import main.collections.ListUtils;
import util.AI;
import util.Context;
import util.GameLoader;
import util.Trial;
import util.model.Model;
import utils.AIUtils;
import utils.experiments.InterruptableExperiment;
import utils.experiments.ResultsSummary;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class EvalGamesSet
{
    protected String gameName;
    protected List<String> gameOptions;
    protected List<AI> agents;
    protected int numGames;
    protected int gameLengthCap;
    protected double maxSeconds;
    protected int maxIterations;
    protected int maxSearchDepth;
    protected boolean rotateAgents;
    protected int warmingUpSecs;
    protected boolean roundToNextPermutationsDivisor;
    protected boolean printOut;
    protected File outDir;
    protected boolean outputSummary;
    protected boolean outputAlphaRankData;
    protected boolean useGUI;
    protected int maxWallTime;
    
    public EvalGamesSet() {
        this.gameName = null;
        this.gameOptions = new ArrayList<>();
        this.agents = null;
        this.numGames = 100;
        this.gameLengthCap = -1;
        this.maxSeconds = 1.0;
        this.maxIterations = -1;
        this.maxSearchDepth = -1;
        this.rotateAgents = true;
        this.warmingUpSecs = 60;
        this.roundToNextPermutationsDivisor = false;
        this.printOut = true;
        this.outDir = null;
        this.outputSummary = true;
        this.outputAlphaRankData = false;
        this.useGUI = false;
        this.maxWallTime = -1;
    }
    
    public EvalGamesSet(final boolean useGUI) {
        this.gameName = null;
        this.gameOptions = new ArrayList<>();
        this.agents = null;
        this.numGames = 100;
        this.gameLengthCap = -1;
        this.maxSeconds = 1.0;
        this.maxIterations = -1;
        this.maxSearchDepth = -1;
        this.rotateAgents = true;
        this.warmingUpSecs = 60;
        this.roundToNextPermutationsDivisor = false;
        this.printOut = true;
        this.outDir = null;
        this.outputSummary = true;
        this.outputAlphaRankData = false;
        this.useGUI = false;
        this.maxWallTime = -1;
        this.useGUI = useGUI;
    }
    
    public EvalGamesSet(final boolean useGUI, final int maxWallTime) {
        this.gameName = null;
        this.gameOptions = new ArrayList<>();
        this.agents = null;
        this.numGames = 100;
        this.gameLengthCap = -1;
        this.maxSeconds = 1.0;
        this.maxIterations = -1;
        this.maxSearchDepth = -1;
        this.rotateAgents = true;
        this.warmingUpSecs = 60;
        this.roundToNextPermutationsDivisor = false;
        this.printOut = true;
        this.outDir = null;
        this.outputSummary = true;
        this.outputAlphaRankData = false;
        this.useGUI = false;
        this.maxWallTime = -1;
        this.useGUI = useGUI;
        this.maxWallTime = maxWallTime;
    }
    
    public void startGames() {
        final Game game = GameLoader.loadGameFromName(this.gameName, this.gameOptions);
        if (game == null) {
            System.err.println("Could not instantiate game. Aborting set of games. Game name = " + this.gameName + ".");
            return;
        }
        if (this.agents == null) {
            System.err.println("No list of agents provided. Aborting set of games.");
            return;
        }
        final int numPlayers = game.players().count();
        if (this.agents.size() != numPlayers) {
            System.err.println("Expected " + numPlayers + " agents, but received list of " + this.agents.size() + " agents. Aborting set of games.");
            return;
        }
        if (this.gameLengthCap >= 0) {
            game.setMaxTurns(Math.min(this.gameLengthCap, game.getMaxTurnLimit()));
        }
        final Trial trial = new Trial(game);
        final Context context = new Context(game, trial);
        final InterruptableExperiment experiment = new InterruptableExperiment(this.useGUI, this.maxWallTime) {
            @Override
            public void runExperiment() {
                int numGamesToPlay = EvalGamesSet.this.numGames;
                List<TIntArrayList> aiListPermutations = new ArrayList<>();
                if (EvalGamesSet.this.rotateAgents) {
                    aiListPermutations = ListUtils.generatePermutations(TIntArrayList.wrap(IntStream.range(0, numPlayers).toArray()));
                    if (numGamesToPlay % aiListPermutations.size() != 0) {
                        if (EvalGamesSet.this.roundToNextPermutationsDivisor) {
                            numGamesToPlay += numGamesToPlay % aiListPermutations.size();
                        }
                        else {
                            System.err.println(String.format("Warning: number of games to play (%d) is not divisible by the number of permutations of list of AIs (%d)", numGamesToPlay, aiListPermutations.size()));
                        }
                    }
                }
                else {
                    aiListPermutations.add(TIntArrayList.wrap(IntStream.range(0, numPlayers).toArray()));
                }
                if (EvalGamesSet.this.printOut) {
                    System.out.println("Warming up...");
                }
                long stopAt = 0L;
                final long start = System.nanoTime();
                for (double abortAt = start + EvalGamesSet.this.warmingUpSecs * 1.0E9; stopAt < abortAt; stopAt = System.nanoTime()) {
                    game.start(context);
                    game.playout(context, null, 1.0, null, null, -1, -1, -1.0f, ThreadLocalRandom.current());
                }
                System.gc();
                if (EvalGamesSet.this.printOut) {
                    System.out.println("Finished warming up!");
                }
                final List<String> agentStrings = new ArrayList<>();
                for (final AI ai : EvalGamesSet.this.agents) {
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
                        currentAIList.add(EvalGamesSet.this.agents.get(currentPlayersPermutation.getQuick(i) % EvalGamesSet.this.agents.size()));
                    }
                    game.start(context);
                    for (int p = 1; p < currentAIList.size(); ++p) {
                        currentAIList.get(p).initAI(game, p);
                    }
                    final Model model = context.model();
                    while (!context.trial().over() && !this.interrupted) {
                        model.startNewStep(context, currentAIList, EvalGamesSet.this.maxSeconds, EvalGamesSet.this.maxIterations, EvalGamesSet.this.maxSearchDepth, 0.0);
                    }
                    if (context.trial().over()) {
                        final double[] utilities = AIUtils.agentUtilities(context);
                        final int numMovesPlayed = context.trial().moves().size() - context.trial().numInitialPlacementMoves();
                        final int[] agentPermutation = new int[currentPlayersPermutation.size() + 1];
                        currentPlayersPermutation.toArray(agentPermutation, 0, 1, currentPlayersPermutation.size());
                        resultsSummary.recordResults(agentPermutation, utilities, numMovesPlayed);
                    }
                    if (EvalGamesSet.this.printOut && (gameCounter < 5 || gameCounter % 10 == 9)) {
                        System.out.print(resultsSummary.generateIntermediateSummary());
                    }
                }
                if (EvalGamesSet.this.outDir != null) {
                    if (EvalGamesSet.this.outputSummary) {
                        final File outFile = new File(EvalGamesSet.this.outDir + "/results.txt");
                        outFile.getParentFile().mkdirs();
                        try (final PrintWriter writer = new PrintWriter(outFile, StandardCharsets.UTF_8)) {
                            writer.write(resultsSummary.generateIntermediateSummary());
                        }
                        catch (IOException ex2) {
                            ex2.printStackTrace();
                        }
                    }
                    if (EvalGamesSet.this.outputAlphaRankData) {
                        final File outFile = new File(EvalGamesSet.this.outDir + "/alpha_rank_data.csv");
                        outFile.getParentFile().mkdirs();
                        resultsSummary.writeAlphaRankData(outFile);
                    }
                }
            }
        };
    }
    
    public EvalGamesSet setGameName(final String gameName) {
        this.gameName = gameName;
        return this;
    }
    
    public EvalGamesSet setGameOptions(final List<String> gameOptions) {
        this.gameOptions = gameOptions;
        return this;
    }
    
    public EvalGamesSet setAgents(final List<AI> agents) {
        this.agents = agents;
        return this;
    }
    
    public EvalGamesSet setNumGames(final int numGames) {
        this.numGames = numGames;
        return this;
    }
    
    public EvalGamesSet setGameLengthCap(final int gameLengthCap) {
        this.gameLengthCap = gameLengthCap;
        return this;
    }
    
    public EvalGamesSet setMaxSeconds(final double maxSeconds) {
        this.maxSeconds = maxSeconds;
        return this;
    }
    
    public EvalGamesSet setMaxIterations(final int maxIterations) {
        this.maxIterations = maxIterations;
        return this;
    }
    
    public EvalGamesSet setMaxSearchDepth(final int maxSearchDepth) {
        this.maxSearchDepth = maxSearchDepth;
        return this;
    }
    
    public EvalGamesSet setRotateAgents(final boolean rotateAgents) {
        this.rotateAgents = rotateAgents;
        return this;
    }
    
    public EvalGamesSet setWarmingUpSecs(final int warmingUpSecs) {
        this.warmingUpSecs = warmingUpSecs;
        return this;
    }
    
    public EvalGamesSet setRoundToNextPermutationsDivisor(final boolean roundToNextPermutationsDivisor) {
        this.roundToNextPermutationsDivisor = roundToNextPermutationsDivisor;
        return this;
    }
    
    public EvalGamesSet setOutDir(final File outDir) {
        this.outDir = outDir;
        return this;
    }
    
    public EvalGamesSet setOutputAlphaRankData(final boolean outputAlphaRankData) {
        this.outputAlphaRankData = outputAlphaRankData;
        return this;
    }
    
    public EvalGamesSet setOutputSummary(final boolean outputSummary) {
        this.outputSummary = outputSummary;
        return this;
    }
    
    public EvalGamesSet setPrintOut(final boolean printOut) {
        this.printOut = printOut;
        return this;
    }
}
