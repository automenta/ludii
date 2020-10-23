// 
// Decompiled by Procyon v0.5.36
// 

package analysis;

import game.Game;
import game.types.play.RepetitionType;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import language.compiler.Compiler;
import grammar.Description;
import grammar.Report;
import options.UserSelections;
import util.Context;
import util.Trial;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class Complexity
{
    public static TObjectDoubleHashMap<String> estimateBranchingFactor(final String gameResource, final UserSelections userSelections, final double numSeconds) {
        String desc = "";
        try (final BufferedReader rdr = new BufferedReader(new InputStreamReader(Complexity.class.getResourceAsStream(gameResource)))) {
            String line;
            while ((line = rdr.readLine()) != null) {
                desc = desc + line + "\n";
            }
        }
        catch (Exception e) {
            try (final BufferedReader rdr2 = new BufferedReader(new InputStreamReader(new FileInputStream(gameResource), StandardCharsets.UTF_8))) {
                String line2;
                while ((line2 = rdr2.readLine()) != null) {
                    desc = desc + line2 + "\n";
                }
            }
            catch (Exception e2) {
                e.printStackTrace();
            }
        }
        final Game game = Compiler.compile(new Description(desc), userSelections, new Report(), false);
        game.disableMemorylessPlayouts();
        final Trial trial = new Trial(game);
        final Context context = new Context(game, trial);
        trial.storeLegalMovesHistorySizes();
        System.gc();
        long stopAt = 0L;
        final long start = System.nanoTime();
        final long abortAt = start + (long)Math.ceil(numSeconds * 1.0E9);
        int numTrials = 0;
        long numStates = 0L;
        long sumBranchingFactors = 0L;
        double sumAvgTrialBranchingFactors = 0.0;
        while (stopAt < abortAt) {
            game.start(context);
            final Trial endTrial = game.playout(context, null, 1.0, null, null, -1, -1, -1.0f, ThreadLocalRandom.current());
            final int numDecisions = endTrial.numMoves() - endTrial.numInitialPlacementMoves();
            long trialSumBranchingFactors = 0L;
            final TIntArrayList branchingFactors = endTrial.auxilTrialData().legalMovesHistorySizes();
            for (int i = 0; i < branchingFactors.size(); ++i) {
                trialSumBranchingFactors += branchingFactors.getQuick(i);
            }
            numStates += branchingFactors.size();
            sumBranchingFactors += trialSumBranchingFactors;
            sumAvgTrialBranchingFactors += trialSumBranchingFactors / (double)numDecisions;
            ++numTrials;
            stopAt = System.nanoTime();
        }
        final TObjectDoubleHashMap<String> map = new TObjectDoubleHashMap<>();
        map.put("Avg Trial Branching Factor", sumAvgTrialBranchingFactors / numTrials);
        map.put("Avg State Branching Factor", sumBranchingFactors / (double)numStates);
        map.put("Num Trials", numTrials);
        return map;
    }
    
    public static TObjectDoubleHashMap<String> estimateGameLength(final Game game, final double numSeconds) {
        final Trial trial = new Trial(game);
        final Context context = new Context(game, trial);
        System.gc();
        long stopAt = 0L;
        final long start = System.nanoTime();
        final long abortAt = start + (long)Math.ceil(numSeconds * 1.0E9);
        int numTrials = 0;
        long numDecisions = 0L;
        long numPlayerSwitches = 0L;
        while (stopAt < abortAt) {
            game.start(context);
            final Trial endTrial = game.playout(context, null, 1.0, null, null, -1, -1, -1.0f, ThreadLocalRandom.current());
            numDecisions += endTrial.numMoves() - endTrial.numInitialPlacementMoves();
            numPlayerSwitches += context.state().numTurn() - 1;
            ++numTrials;
            stopAt = System.nanoTime();
        }
        final TObjectDoubleHashMap<String> map = new TObjectDoubleHashMap<>();
        map.put("Avg Num Decisions", numDecisions / (double)numTrials);
        map.put("Avg Num Player Switches", numPlayerSwitches / (double)numTrials);
        map.put("Num Trials", numTrials);
        return map;
    }
    
    public static TObjectDoubleHashMap<String> estimateGameTreeComplexity(final String gameResource, final UserSelections userSelections, final double numSeconds, final boolean forceNoStateRepetitionRule) {
        String desc = "";
        try (final BufferedReader rdr = new BufferedReader(new InputStreamReader(Complexity.class.getResourceAsStream(gameResource)))) {
            String line;
            while ((line = rdr.readLine()) != null) {
                desc = desc + line + "\n";
            }
        }
        catch (Exception e) {
            try (final BufferedReader rdr2 = new BufferedReader(new InputStreamReader(new FileInputStream(gameResource), StandardCharsets.UTF_8))) {
                String line2;
                while ((line2 = rdr2.readLine()) != null) {
                    desc = desc + line2 + "\n";
                }
            }
            catch (Exception e2) {
                e.printStackTrace();
            }
        }
        final Game game = Compiler.compile(new Description(desc), userSelections, new Report(), false);
        game.disableMemorylessPlayouts();
        if (forceNoStateRepetitionRule) {
            game.setRepetitionType(RepetitionType.InGame);
        }
        final Trial trial = new Trial(game);
        final Context context = new Context(game, trial);
        trial.storeLegalMovesHistorySizes();
        System.gc();
        long stopAt = 0L;
        final long start = System.nanoTime();
        final long abortAt = start + (long)Math.ceil(numSeconds * 1.0E9);
        int numTrials = 0;
        long sumNumDecisions = 0L;
        double sumAvgTrialBranchingFactors = 0.0;
        while (stopAt < abortAt) {
            game.start(context);
            final Trial endTrial = game.playout(context, null, 1.0, null, null, -1, -1, -1.0f, ThreadLocalRandom.current());
            final int numDecisions = endTrial.numMoves() - endTrial.numInitialPlacementMoves();
            long trialSumBranchingFactors = 0L;
            final TIntArrayList branchingFactors = endTrial.auxilTrialData().legalMovesHistorySizes();
            for (int i = 0; i < branchingFactors.size(); ++i) {
                trialSumBranchingFactors += branchingFactors.getQuick(i);
            }
            sumAvgTrialBranchingFactors += trialSumBranchingFactors / (double)numDecisions;
            sumNumDecisions += numDecisions;
            ++numTrials;
            stopAt = System.nanoTime();
        }
        final TObjectDoubleHashMap<String> map = new TObjectDoubleHashMap<>();
        final double d = sumNumDecisions / (double)numTrials;
        final double b = sumAvgTrialBranchingFactors / numTrials;
        map.put("Avg Num Decisions", d);
        map.put("Avg Trial Branching Factor", b);
        map.put("Estimated Complexity Power", d * Math.log10(b));
        map.put("Num Trials", numTrials);
        return map;
    }
}
