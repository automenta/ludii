// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.experiments.eval;

import main.CommandLineArgParse;
import supplementary.experiments.EvalGamesSet;
import util.AI;
import utils.AIFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EvalAgents
{
    public String gameName;
    public List<String> gameOptions;
    public int numGames;
    public int gameLengthCap;
    public double thinkingTime;
    public int iterationLimit;
    public int depthLimit;
    public boolean rotateAgents;
    public int warmingUpSecs;
    public boolean roundToNextPermutationsDivisor;
    public List<String> agentStrings;
    public File outDir;
    public boolean outputSummary;
    public boolean outputAlphaRankData;
    public boolean printOut;
    protected boolean useGUI;
    protected int maxWallTime;
    
    public EvalAgents() {
    }
    
    public EvalAgents(final boolean useGUI) {
        this.useGUI = useGUI;
    }
    
    public EvalAgents(final boolean useGUI, final int maxWallTime) {
        this.useGUI = useGUI;
        this.maxWallTime = maxWallTime;
    }
    
    public void startExperiment() {
        final List<AI> ais = new ArrayList<>(this.agentStrings.size());
        for (final String agent : this.agentStrings) {
            ais.add(AIFactory.createAI(agent));
        }
        final EvalGamesSet gamesSet = new EvalGamesSet(this.useGUI, this.maxWallTime).setGameName(this.gameName).setGameOptions(this.gameOptions).setAgents(ais).setNumGames(this.numGames).setGameLengthCap(this.gameLengthCap).setMaxSeconds(this.thinkingTime).setMaxIterations(this.iterationLimit).setMaxSearchDepth(this.depthLimit).setRotateAgents(this.rotateAgents).setWarmingUpSecs(this.warmingUpSecs).setRoundToNextPermutationsDivisor(this.roundToNextPermutationsDivisor).setOutDir(this.outDir).setOutputAlphaRankData(this.outputAlphaRankData).setOutputSummary(this.outputSummary).setPrintOut(this.printOut);
        gamesSet.startGames();
    }
    
    public static void main(final String[] args) {
        final CommandLineArgParse argParse = new CommandLineArgParse(true, "Evaluate playing strength of different agents against each other.");
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--game").help("Name of the game to play. Should end with \".lud\".").withDefault("Amazons.lud").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--game-options").help("Game Options to load.").withDefault(new ArrayList(0)).withNumVals("*").withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--agents").help("Agents which should be evaluated").withDefault(Arrays.asList("UCT", "Biased MCTS")).withNumVals("+").withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("-n", "--num-games", "--num-eval-games").help("Number of training games to run.").withDefault(200).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--game-length-cap", "--max-num-actions").help("Maximum number of actions that may be taken before a game is terminated as a draw (-1 for no limit).").withDefault(-1).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--thinking-time", "--time", "--seconds").help("Max allowed thinking time per move (in seconds).").withDefault(1.0).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Double));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--iteration-limit", "--iterations").help("Max allowed number of MCTS iterations per move.").withDefault(-1).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--depth-limit").help("Max allowed search depth per move (for e.g. alpha-beta).").withDefault(-1).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--no-rotate-agents").help("Don't rotate through possible assignments of agents to Player IDs.").withType(CommandLineArgParse.OptionTypes.Boolean).withNumVals(0));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--warming-up-secs").help("Number of seconds for which to warm up JVM.").withType(CommandLineArgParse.OptionTypes.Int).withNumVals(1).withDefault(60));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--round-to-next-permutations-divisor").help("Increase number of games to play to next number that can be divided by number of permutations of agents.").withType(CommandLineArgParse.OptionTypes.Boolean).withNumVals(0));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--out-dir", "--output-directory").help("Filepath for output directory").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--output-summary").help("Output summary of results.").withType(CommandLineArgParse.OptionTypes.Boolean).withNumVals(0));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--output-alpha-rank-data").help("Output data for alpha-rank.").withType(CommandLineArgParse.OptionTypes.Boolean).withNumVals(0));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--no-print-out").help("Suppress general prints to System.out.").withType(CommandLineArgParse.OptionTypes.Boolean).withNumVals(0));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--useGUI").help("Whether to create a small GUI that can be used to manually interrupt training run. False by default."));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--max-wall-time").help("Max wall time in minutes (or -1 for no limit).").withDefault(-1).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        if (!argParse.parseArguments(args)) {
            return;
        }
        final EvalAgents eval = new EvalAgents(argParse.getValueBool("--useGUI"), argParse.getValueInt("--max-wall-time"));
        eval.gameName = argParse.getValueString("--game");
        eval.gameOptions = (List<String>)argParse.getValue("--game-options");
        eval.agentStrings = (List<String>)argParse.getValue("--agents");
        eval.numGames = argParse.getValueInt("-n");
        eval.gameLengthCap = argParse.getValueInt("--game-length-cap");
        eval.thinkingTime = argParse.getValueDouble("--thinking-time");
        eval.iterationLimit = argParse.getValueInt("--iteration-limit");
        eval.depthLimit = argParse.getValueInt("--depth-limit");
        eval.rotateAgents = !argParse.getValueBool("--no-rotate-agents");
        eval.warmingUpSecs = argParse.getValueInt("--warming-up-secs");
        eval.roundToNextPermutationsDivisor = argParse.getValueBool("--round-to-next-permutations-divisor");
        final String outDirFilepath = argParse.getValueString("--out-dir");
        if (outDirFilepath != null) {
            eval.outDir = new File(outDirFilepath);
        }
        else {
            eval.outDir = null;
        }
        eval.outputSummary = argParse.getValueBool("--output-summary");
        eval.outputAlphaRankData = argParse.getValueBool("--output-alpha-rank-data");
        eval.printOut = !argParse.getValueBool("--no-print-out");
        eval.startExperiment();
    }
}
