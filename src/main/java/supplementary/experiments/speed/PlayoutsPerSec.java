// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.experiments.speed;

import collections.FVector;
import features.FeatureSet;
import game.Game;
import main.CommandLineArgParse;
import main.FileHandling;
import policies.softmax.SoftmaxFromMetadata;
import util.Context;
import util.GameLoader;
import util.Trial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public final class PlayoutsPerSec
{
    private List<String> gameNames;
    private List<String> excludeDirs;
    private List<String> gameOptions;
    private int warmingUpSecs;
    private int measureSecs;
    private int playoutActionCap;
    private boolean useFeatures;
    private float autoPlayThreshold;
    
    private PlayoutsPerSec() {
    }
    
    public void startExperiment() {
        final List<String> gameNamesToTest = new ArrayList<>();
        if (this.gameNames.get(0).equalsIgnoreCase("all")) {
            for (int i = 0; i < this.excludeDirs.size(); ++i) {
                this.excludeDirs.set(i, this.excludeDirs.get(i).toLowerCase());
            }
            final String[] listGames;
            final String[] allGameNames = listGames = FileHandling.listGames();
            for (final String gameName : listGames) {
                final String name = gameName.replaceAll(Pattern.quote("\\"), "/");
                final String[] nameParts = name.split(Pattern.quote("/"));
                boolean exclude = false;
                for (final String part : nameParts) {
                    if (this.excludeDirs.contains(part.toLowerCase()) || part.equals("plex") || part.equals("bad") || part.equals("bad_playout") || part.equals("wip") || part.equals("test")) {
                        exclude = true;
                        break;
                    }
                }
                if (!exclude) {
                    gameNamesToTest.add(name);
                }
            }
        }
        else {
            final String[] allGameNames = FileHandling.listGames();
            for (String gameName2 : this.gameNames) {
                gameName2 = gameName2.replaceAll(Pattern.quote("\\"), "/");
                for (String name2 : allGameNames) {
                    name2 = name2.replaceAll(Pattern.quote("\\"), "/");
                    if (name2.endsWith(gameName2)) {
                        gameNamesToTest.add(name2);
                    }
                }
            }
        }
        System.out.println("Starting timings for games: " + gameNamesToTest);
        System.out.println();
        System.out.println("Using " + this.warmingUpSecs + " warming-up seconds per game.");
        System.out.println("Measuring results over " + this.measureSecs + " seconds per game.");
        System.out.println();
        for (final String gameName3 : gameNamesToTest) {
            final Game game = GameLoader.loadGameFromName(gameName3, this.gameOptions);
            FeatureSet[] featureSets;
            FVector[] weights;
            if (this.useFeatures) {
                final SoftmaxFromMetadata softmax = new SoftmaxFromMetadata();
                softmax.initAI(game, -1);
                if (softmax.featureSets().length > 0) {
                    featureSets = softmax.featureSets();
                    weights = new FVector[softmax.linearFunctions().length];
                    for (int j = 0; j < softmax.linearFunctions().length; ++j) {
                        if (softmax.linearFunctions()[j] != null) {
                            weights[j] = softmax.linearFunctions()[j].effectiveParams();
                        }
                    }
                }
                else {
                    featureSets = null;
                    weights = null;
                }
            }
            else {
                featureSets = null;
                weights = null;
            }
            final Trial trial = new Trial(game);
            final Context context = new Context(game, trial);
            long stopAt = 0L;
            long start = System.nanoTime();
            for (double abortAt = start + this.warmingUpSecs * 1.0E9; stopAt < abortAt; stopAt = System.nanoTime()) {
                game.start(context);
                game.playout(context, null, 1.0, featureSets, weights, -1, this.playoutActionCap, this.autoPlayThreshold, ThreadLocalRandom.current());
            }
            System.gc();
            stopAt = 0L;
            start = System.nanoTime();
            final double abortAt = start + this.measureSecs * 1.0E9;
            int playouts = 0;
            long numDecisions = 0L;
            while (stopAt < abortAt) {
                game.start(context);
                final Trial endTrial = game.playout(context, null, 1.0, featureSets, weights, -1, this.playoutActionCap, this.autoPlayThreshold, ThreadLocalRandom.current());
                numDecisions += endTrial.numMoves() - endTrial.numInitialPlacementMoves();
                stopAt = System.nanoTime();
                ++playouts;
            }
            final double secs = (stopAt - start) / 1.0E9;
            final double rate = playouts / secs;
            final double decisionsPerPlayout = numDecisions / (double)playouts;
            System.out.println(game.name() + "\t-\t" + rate + " p/s\t-\t" + decisionsPerPlayout + " decisions per playout\n");
        }
    }
    
    public static void main(final String[] args) {
        final CommandLineArgParse argParse = new CommandLineArgParse(true, "Measure playouts per second for one or more games.");
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--games").help("Names of the games to play. Each should end with \".lud\". Use \"all\" to run all games we can find. Runs all games by default.").withDefault(Collections.singletonList("all")).withNumVals("+").withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--exclude-dirs").help("List of game directories to exclude from experiment.").withDefault(Collections.singletonList("puzzle")).withNumVals("*").withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--game-options").help("Game Options to load.").withDefault(new ArrayList(0)).withNumVals("*").withType(CommandLineArgParse.OptionTypes.String));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--warming-up-secs", "--warming-up").help("Number of seconds of warming up (per game).").withDefault(10).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--measure-secs").help("Number of seconds over which we measure playouts (per game).").withDefault(30).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--playout-action-cap").help("Maximum number of actions to execute per playout (-1 for no cap).").withDefault(-1).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Int));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--use-features").help("Use features inside playouts (making them no longer uniformly random)").withNumVals(0).withType(CommandLineArgParse.OptionTypes.Boolean));
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--auto-play-threshold").help("Auto-play threshold for features").withDefault(-1.0f).withNumVals(1).withType(CommandLineArgParse.OptionTypes.Float));
        if (!argParse.parseArguments(args)) {
            return;
        }
        final PlayoutsPerSec experiment = new PlayoutsPerSec();
        experiment.gameNames = (List<String>)argParse.getValue("--games");
        experiment.excludeDirs = (List<String>)argParse.getValue("--exclude-dirs");
        experiment.gameOptions = (List<String>)argParse.getValue("--game-options");
        experiment.warmingUpSecs = argParse.getValueInt("--warming-up-secs");
        experiment.measureSecs = argParse.getValueInt("--measure-secs");
        experiment.playoutActionCap = argParse.getValueInt("--playout-action-cap");
        experiment.useFeatures = argParse.getValueBool("--use-features");
        experiment.autoPlayThreshold = argParse.getValueFloat("--auto-play-threshold");
        experiment.startExperiment();
    }
}
