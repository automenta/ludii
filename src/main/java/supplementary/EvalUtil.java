// 
// Decompiled by Procyon v0.5.36
// 

package supplementary;

import analysis.Complexity;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import manager.Manager;
import manager.utils.SettingsManager;
import search.pns.ProofNumberSearch;

import java.awt.*;

public class EvalUtil
{
    public static void estimateGameTreeComplexity(final boolean forceNoStateRepetitionRule) {
        if (!Manager.ref().context().game().isDeductionPuzzle()) {

            final Thread thread = new Thread(() -> {
                final double numSecs = 30.0;
                final TObjectDoubleHashMap<String> results;
                final double avgNumDecisions;
                final double avgTrialBranchingFactor;
                final double power;
                final int numTrials;
                results = Complexity.estimateGameTreeComplexity(Manager.savedLudName(), SettingsManager.userSelections, 30.0, forceNoStateRepetitionRule);
                avgNumDecisions = results.get("Avg Num Decisions");
                avgTrialBranchingFactor = results.get("Avg Trial Branching Factor");
                power = results.get("Estimated Complexity Power");
                numTrials = (int)results.get("Num Trials");
                EventQueue.invokeLater(() -> {
                    Manager.app.addTextToAnalysisPanel("Avg. number of decisions per trial = " + avgNumDecisions + ".\n");
                    Manager.app.addTextToAnalysisPanel("Avg. branching factor per trial = " + avgTrialBranchingFactor + ".\n");
                    Manager.app.addTextToAnalysisPanel("Estimated game-tree complexity ~= 10^" + (int)Math.ceil(power) + ".\n");
                    Manager.app.addTextToAnalysisPanel("Statistics collected over " + numTrials + " random trials.\n");
                    Manager.app.setTemporaryMessage("");
                });
            });
            Manager.app.selectAnalysisTab();
            Manager.app.setTemporaryMessage("Estimate Game Tree Complexity is starting. This will take a bit over 30 seconds.\n");
            thread.setDaemon(true);
            thread.start();
        }
        else {
            Manager.app.setVolatileMessage("Estimate Game Tree Complexity is disabled for deduction puzzles.\n");
        }
    }
    
    public static void proveState(final ProofNumberSearch.ProofGoals proofGoal) {
        final Thread thread = new Thread(() -> {
            final ProofNumberSearch pns;
            pns = new ProofNumberSearch(proofGoal);
            if (!pns.supportsGame(Manager.ref().context().game())) {
                System.err.println("PNS doesn't support this game!");
            }
            else {
                pns.initIfNeeded(Manager.ref().context().game(), Manager.ref().context().state().mover());
                pns.selectAction(Manager.ref().context().game(), Manager.ref().context(), 1.0, -1, -1);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
    
    public static void estimateGameLength() {
        if (!Manager.ref().context().game().isDeductionPuzzle()) {

            final Thread thread = new Thread(() -> {
                final double numSecs = 30.0;
                final TObjectDoubleHashMap<String> results;
                final double avgNumDecisions;
                final double avgNumPlayerSwitches;
                final int numTrials;

                results = Complexity.estimateGameLength(Manager.ref().context().game(), 30.0);
                avgNumDecisions = results.get("Avg Num Decisions");
                avgNumPlayerSwitches = results.get("Avg Num Player Switches");
                numTrials = (int)results.get("Num Trials");
                EventQueue.invokeLater(() -> {
                    Manager.app.addTextToAnalysisPanel("Avg. number of decisions per trial = " + avgNumDecisions + ".\n");
                    Manager.app.addTextToAnalysisPanel("Avg. number of player switches per trial = " + avgNumPlayerSwitches + ".\n");
                    Manager.app.addTextToAnalysisPanel("Statistics collected over " + numTrials + " random trials.\n");
                    Manager.app.setTemporaryMessage("");
                });
            });
            Manager.app.selectAnalysisTab();
            Manager.app.setTemporaryMessage("Estimate Game Length is starting. This will take a bit over 30 seconds.\n");
            thread.setDaemon(true);
            thread.start();
        }
        else {
            Manager.app.setVolatileMessage("Estimate Game Length is disabled for deduction puzzles.\n");
        }
    }
    
    public static void estimateBranchingFactor() {
        if (!Manager.ref().context().game().isDeductionPuzzle()) {

            final Thread thread = new Thread(() -> {
                final double numSecs = 30.0;
                final TObjectDoubleHashMap<String> results;
                final double avgTrialBranchingFactor;
                final double avgStateBranchingFactor;
                final int numTrials;
                results = Complexity.estimateBranchingFactor(Manager.savedLudName(), SettingsManager.userSelections, 30.0);
                avgTrialBranchingFactor = results.get("Avg Trial Branching Factor");
                avgStateBranchingFactor = results.get("Avg State Branching Factor");
                numTrials = (int)results.get("Num Trials");
                EventQueue.invokeLater(() -> {
                    Manager.app.addTextToAnalysisPanel("Avg. branching factor per trial = " + avgTrialBranchingFactor + ".\n");
                    Manager.app.addTextToAnalysisPanel("Avg. branching factor per state = " + avgStateBranchingFactor + ".\n");
                    Manager.app.addTextToAnalysisPanel("Statistics collected over " + numTrials + " random trials.\n");
                    Manager.app.setTemporaryMessage("");
                });
            });
            Manager.app.selectAnalysisTab();
            Manager.app.setTemporaryMessage("Estimate Branching Factor is starting. This will take a bit over 30 seconds.\n");
            thread.setDaemon(true);
            thread.start();
        }
        else {
            Manager.app.setVolatileMessage("Estimate Branching Factor is disabled for deduction puzzles.\n");
        }
    }
}
