// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.experiments.scripts;

import collections.ListUtils;
import game.Game;
import main.CommandLineArgParse;
import main.FileHandling;
import main.StringRoutines;
import main.UnixPrintWriter;
import options.Option;
import search.mcts.MCTS;
import search.minimax.AlphaBetaSearch;
import util.GameLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GenerateGatingScripts
{
    private static final String MEM_PER_CPU = "5120";
    private static final String JVM_MEM = "4096";
    private static final int MAX_WALL_TIME = 4000;
    private static final int MAX_JOBS_PER_BATCH = 800;
    private static final AlphaBetaSearch dummyAlphaBeta;
    private static final MCTS dummyUCT;
    
    private GenerateGatingScripts() {
    }
    
    private static void generateScripts(final CommandLineArgParse argParse) {
        final List<String> jobScriptNames = new ArrayList<>();
        final String[] allGameNames = FileHandling.listGames();
        String scriptsDirPath = argParse.getValueString("--scripts-dir");
        scriptsDirPath = scriptsDirPath.replaceAll(Pattern.quote("\\"), "/");
        if (!(!scriptsDirPath.isEmpty() && scriptsDirPath.charAt(scriptsDirPath.length() - 1) == '/')) {
            scriptsDirPath += "/";
        }
        final File scriptsDir = new File(scriptsDirPath);
        if (!scriptsDir.exists()) {
            scriptsDir.mkdirs();
        }
        String trainingOutDirPath = argParse.getValueString("--training-out-dir");
        trainingOutDirPath = trainingOutDirPath.replaceAll(Pattern.quote("\\"), "/");
        if (!(!trainingOutDirPath.isEmpty() && trainingOutDirPath.charAt(trainingOutDirPath.length() - 1) == '/')) {
            trainingOutDirPath += "/";
        }
        final File trainingOutDir = new File(trainingOutDirPath);
        String bestAgentsDataDirPath = argParse.getValueString("--best-agents-data-dir");
        bestAgentsDataDirPath = bestAgentsDataDirPath.replaceAll(Pattern.quote("\\"), "/");
        if (!(!bestAgentsDataDirPath.isEmpty() && bestAgentsDataDirPath.charAt(bestAgentsDataDirPath.length() - 1) == '/')) {
            bestAgentsDataDirPath += "/";
        }
        final File bestAgentsDataDir = new File(bestAgentsDataDirPath);
        if (!bestAgentsDataDir.exists()) {
            bestAgentsDataDir.mkdirs();
        }
        final File[] listFiles;
        final File[] gameOutDirs = listFiles = trainingOutDir.listFiles();
        for (final File gameOutDir : listFiles) {
            if (gameOutDir.isDirectory()) {
                final String gameDirName = gameOutDir.getName();
                final File[] files = gameOutDir.listFiles();
                boolean noOptions = false;
                for (final File file : files) {
                    if (file.getName().contains("ExperienceBuffer") || file.getName().contains("FeatureSet")) {
                        noOptions = true;
                        break;
                    }
                }
                if (noOptions) {
                    generateScripts(gameOutDir, gameDirName, null, scriptsDir, bestAgentsDataDir, allGameNames, jobScriptNames, argParse);
                }
                else {
                    for (final File file : files) {
                        generateScripts(file, gameDirName, file.getName(), scriptsDir, bestAgentsDataDir, allGameNames, jobScriptNames, argParse);
                    }
                }
            }
        }
        final List<List<String>> jobScriptsLists = new ArrayList<>();
        List<String> remainingJobScriptNames = jobScriptNames;
        while (!remainingJobScriptNames.isEmpty()) {
            if (remainingJobScriptNames.size() > 800) {
                final List<String> subList = new ArrayList<>();
                for (int i = 0; i < 800; ++i) {
                    subList.add(remainingJobScriptNames.get(i));
                }
                jobScriptsLists.add(subList);
                remainingJobScriptNames = remainingJobScriptNames.subList(800, remainingJobScriptNames.size());
            }
            else {
                jobScriptsLists.add(remainingJobScriptNames);
                remainingJobScriptNames = new ArrayList<>();
            }
        }
        for (int j = 0; j < jobScriptsLists.size(); ++j) {
            try (final PrintWriter writer = new UnixPrintWriter(new File(scriptsDir + "/SubmitJobs_Part" + j + ".sh"), "UTF-8")) {
                for (final String jobScriptName : jobScriptsLists.get(j)) {
                    writer.println("sbatch " + jobScriptName);
                }
            }
            catch (FileNotFoundException | UnsupportedEncodingException ex2) {
                ex2.printStackTrace();
            }
        }
    }
    
    private static void generateScripts(final File trainingOutDir, final String gameDirName, final String optionsStr, final File scriptsDir, final File bestAgentsDataDir, final String[] allGameNames, final List<String> outJobScriptNames, final CommandLineArgParse argParse) {
        String gameName = "";
        for (final String name : allGameNames) {
            final String[] gameNameSplit = name.replaceAll(Pattern.quote("\\"), "/").split(Pattern.quote("/"));
            final String cleanGameName = StringRoutines.cleanGameName(gameNameSplit[gameNameSplit.length - 1]);
            if (gameDirName.equals(cleanGameName)) {
                gameName = name;
                break;
            }
        }
        if (gameName.isEmpty()) {
            System.err.println("Can't recognise game: " + gameDirName);
            return;
        }
        List<String> optionsToCompile = new ArrayList<>();
        Game game;
        if (optionsStr == null) {
            game = GameLoader.loadGameFromName(gameName, new ArrayList<>());
        }
        else {
            final Game gameNoOptions = GameLoader.loadGameFromName(gameName, new ArrayList<>());
            final List<List<String>> optionCategories = new ArrayList<>();
            for (int o = 0; o < gameNoOptions.description().gameOptions().numCategories(); ++o) {
                final List<Option> options = gameNoOptions.description().gameOptions().categories().get(o).options();
                final List<String> optionCategory = new ArrayList<>();
                for (final Option option : options) {
                    final String categoryStr = StringRoutines.join("/", option.menuHeadings().toArray(new String[0]));
                    if (!categoryStr.contains("Board Size/") && !categoryStr.contains("Rows/") && !categoryStr.contains("Columns/")) {
                        optionCategory.add(categoryStr);
                    }
                }
                if (!optionCategory.isEmpty()) {
                    optionCategories.add(optionCategory);
                }
            }
            final List<List<String>> optionCombinations = ListUtils.generateTuples(optionCategories);
            for (final List<String> optionCombination : optionCombinations) {
                final String optionCombinationString = StringRoutines.join("-", optionCombination).replaceAll(Pattern.quote(" "), "").replaceAll(Pattern.quote("/"), "_").replaceAll(Pattern.quote("("), "_").replaceAll(Pattern.quote(")"), "_").replaceAll(Pattern.quote(","), "_");
                if (optionsStr.equals(optionCombinationString)) {
                    optionsToCompile = optionCombination;
                    break;
                }
            }
            if (optionsToCompile == null) {
                System.err.println("Couldn't find options to compile!");
                return;
            }
            game = GameLoader.loadGameFromName(gameName, optionsToCompile);
        }
        final int numPlayers = game.players().count();
        File bestAgentsDataDirForGame;
        if (optionsStr != null) {
            bestAgentsDataDirForGame = new File(bestAgentsDataDir.getAbsolutePath() + "/" + gameDirName + "/" + optionsStr);
        }
        else {
            bestAgentsDataDirForGame = new File(bestAgentsDataDir.getAbsolutePath() + "/" + gameDirName);
        }
        final List<String> agentsToEval = new ArrayList<>();
        final List<List<String>> evalFeatureWeightFilepaths = new ArrayList<>();
        final List<String> evalHeuristicsFilepaths = new ArrayList<>();
        final List<List<String>> gateAgentTypes = new ArrayList<>();
        final File[] trainingOutFiles = trainingOutDir.listFiles();
        if (trainingOutFiles == null || trainingOutFiles.length == 0) {
            System.err.println("No training out files for: " + trainingOutDir.getAbsolutePath());
            return;
        }
        File latestValueFunctionFile = null;
        int latestValueFunctionCheckpoint = 0;
        final File[] latestPolicyWeightFiles = new File[numPlayers + 1];
        final int[] latestPolicyWeightCheckpoints = new int[numPlayers + 1];
        boolean foundPoliceWeights = false;
        for (final File file : trainingOutFiles) {
            String filename = file.getName();
            filename = filename.substring(0, filename.lastIndexOf(46));
            final String[] filenameSplit = filename.split(Pattern.quote("_"));
            if (filename.startsWith("PolicyWeightsCE_P")) {
                final int checkpoint = Integer.parseInt(filenameSplit[2]);
                final int p = Integer.parseInt(filenameSplit[1].substring(1));
                if (checkpoint > latestPolicyWeightCheckpoints[p]) {
                    foundPoliceWeights = true;
                    latestPolicyWeightFiles[p] = file;
                    latestPolicyWeightCheckpoints[p] = checkpoint;
                }
            }
            else if (filename.startsWith("ValueFunction")) {
                final int checkpoint = Integer.parseInt(filenameSplit[1]);
                if (checkpoint > latestValueFunctionCheckpoint) {
                    latestValueFunctionFile = file;
                    latestValueFunctionCheckpoint = checkpoint;
                }
            }
        }
        int numMatchups = 0;
        if (GenerateGatingScripts.dummyAlphaBeta.supportsGame(game) && latestValueFunctionCheckpoint > 0) {
            agentsToEval.add("Alpha-Beta");
            evalFeatureWeightFilepaths.add(new ArrayList<>());
            evalHeuristicsFilepaths.add(latestValueFunctionFile.getAbsolutePath());
            final List<String> gateAgents = new ArrayList<>();
            gateAgents.add("Alpha-Beta");
            gateAgents.add("BestAgent");
            gateAgentTypes.add(gateAgents);
            numMatchups += 2;
        }
        if (GenerateGatingScripts.dummyUCT.supportsGame(game) && foundPoliceWeights) {
            final List<String> policyWeightFiles = new ArrayList<>();
            for (int p2 = 1; p2 < latestPolicyWeightFiles.length; ++p2) {
                policyWeightFiles.add(latestPolicyWeightFiles[p2].toString());
            }
            agentsToEval.add("BiasedMCTS");
            evalFeatureWeightFilepaths.add(policyWeightFiles);
            evalHeuristicsFilepaths.add(null);
            List<String> gateAgents2 = new ArrayList<>();
            if (new File(bestAgentsDataDirForGame.getAbsolutePath() + "/BestFeatures.txt").exists()) {
                gateAgents2.add("BiasedMCTS");
                ++numMatchups;
            }
            gateAgents2.add("BestAgent");
            ++numMatchups;
            gateAgentTypes.add(gateAgents2);
            agentsToEval.add("BiasedMCTSUniformPlayouts");
            evalFeatureWeightFilepaths.add(policyWeightFiles);
            evalHeuristicsFilepaths.add(null);
            gateAgents2 = new ArrayList<>();
            if (new File(bestAgentsDataDirForGame.getAbsolutePath() + "/BestFeatures.txt").exists()) {
                gateAgents2.add("BiasedMCTS");
                ++numMatchups;
            }
            gateAgents2.add("BestAgent");
            ++numMatchups;
            gateAgentTypes.add(gateAgents2);
        }
        final String userName = argParse.getValueString("--user-name");
        String optionsStrFilenames;
        if (optionsStr == null) {
            optionsStrFilenames = "";
        }
        else {
            optionsStrFilenames = "_" + optionsStr;
        }
        final List<String> quotedOptionStrings = new ArrayList<>(optionsToCompile.size());
        for (final String s : optionsToCompile) {
            quotedOptionStrings.add(StringRoutines.quote(s));
        }
        final List<String> javaCalls = new ArrayList<>();
        for (int evalAgentIdx = 0; evalAgentIdx < agentsToEval.size(); ++evalAgentIdx) {
            final String agentToEval = agentsToEval.get(evalAgentIdx);
            final List<String> featureWeightFilepaths = evalFeatureWeightFilepaths.get(evalAgentIdx);
            final String heuristicFilepath = evalHeuristicsFilepaths.get(evalAgentIdx);
            final List<String> gateAgents3 = gateAgentTypes.get(evalAgentIdx);
            for (final String gateAgent : gateAgents3) {
                String javaCall = StringRoutines.join(" ", "java", "-Xms4096M", "-Xmx4096M", "-XX:+HeapDumpOnOutOfMemoryError", "-da", "-dsa", "-XX:+UseStringDeduplication", "-jar", StringRoutines.quote("/home/" + userName + "/Gating/Ludii.jar"), "--eval-gate", "--game", StringRoutines.quote(gameName), "--eval-agent", StringRoutines.quote(agentToEval), "-n 30", "--game-length-cap 800", "--thinking-time 1", "--best-agents-data-dir", StringRoutines.quote(bestAgentsDataDirForGame.getAbsolutePath()), "--gate-agent-type", gateAgent, "--max-wall-time", String.valueOf(Math.max(1000, 4000 / numMatchups)));
                if (!quotedOptionStrings.isEmpty()) {
                    javaCall = javaCall + " --game-options " + StringRoutines.join(" ", quotedOptionStrings);
                }
                if (!featureWeightFilepaths.isEmpty()) {
                    javaCall = javaCall + " --eval-feature-weights-filepaths " + StringRoutines.join(" ", featureWeightFilepaths);
                }
                if (heuristicFilepath != null) {
                    javaCall = javaCall + " --eval-heuristics-filepath " + heuristicFilepath;
                }
                javaCalls.add(javaCall);
            }
        }
        final String jobScriptFilename = "Gating_" + gameDirName + optionsStrFilenames + ".sh";
        try (final PrintWriter writer = new UnixPrintWriter(new File(scriptsDir + "/" + jobScriptFilename), "UTF-8")) {
            writer.println("#!/usr/local_rwth/bin/zsh");
            writer.println("#SBATCH -J Gating_" + gameDirName + optionsStrFilenames);
            writer.println("#SBATCH -o /work/" + userName + "/Gating/Out" + gameDirName + optionsStrFilenames + "_%J.out");
            writer.println("#SBATCH -e /work/" + userName + "/Gating/Err" + gameDirName + optionsStrFilenames + "_%J.err");
            writer.println("#SBATCH -t 4000");
            writer.println("#SBATCH --mem-per-cpu=5120");
            writer.println("#SBATCH -A " + argParse.getValueString("--project"));
            writer.println("unset JAVA_TOOL_OPTIONS");
            for (final String javaCall2 : javaCalls) {
                writer.println(javaCall2);
            }
            outJobScriptNames.add(jobScriptFilename);
        }
        catch (FileNotFoundException | UnsupportedEncodingException ex2) {
            ex2.printStackTrace();
        }
    }
    
    public static void main(final String[] args) {
        final CommandLineArgParse argParse = new CommandLineArgParse(true, "Generates gating scripts for cluster to evaluate which trained agents outperform current default agents.");
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--project").help("Project for which to submit the job on cluster.").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String).setRequired());
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--user-name").help("Username on the cluster.").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String).setRequired());
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--scripts-dir").help("Directory in which to store generated scripts.").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String).setRequired());
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--training-out-dir").help("Base output directory that contains all the results from training.").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String).setRequired());
        argParse.addOption(new CommandLineArgParse.ArgOption().withNames("--best-agents-data-dir").help("Base directory in which we store data about the best agents per game.").withNumVals(1).withType(CommandLineArgParse.OptionTypes.String).setRequired());
        if (!argParse.parseArguments(args)) {
            return;
        }
        generateScripts(argParse);
    }
    
    static {
        dummyAlphaBeta = new AlphaBetaSearch();
        dummyUCT = MCTS.createUCT();
    }
}
