// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.experiments;

import game.Game;
import gnu.trove.list.array.TIntArrayList;
import main.collections.ListUtils;
import manager.Manager;
import manager.ai.AIDetails;
import manager.referee.Referee;
import manager.utils.SettingsManager;
import util.AI;
import util.Context;
import utils.AIUtils;
import utils.experiments.ResultsSummary;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

public class EvalAIsThread extends Thread
{
    protected final EvalAIsThreadRunnable runnable;
    
    public static EvalAIsThread construct(final Referee ref, final List<AI> aiPlayers) {
        final EvalAIsThreadRunnable runnable = new EvalAIsThreadRunnable(ref, aiPlayers);
        return new EvalAIsThread(runnable);
    }
    
    protected EvalAIsThread(final EvalAIsThreadRunnable runnable) {
        super(runnable);
        this.runnable = runnable;
    }
    
    private static class EvalAIsThreadRunnable implements Runnable
    {
        protected final Referee ref;
        protected final List<AI> aiPlayers;
        
        public EvalAIsThreadRunnable(final Referee ref, final List<AI> aiPlayers) {
            this.ref = ref;
            this.aiPlayers = aiPlayers;
        }
        
        @Override
        public void run() {
            final int maxNumGames = 100;
            final Game game = this.ref.context().game();
            final int numPlayers = game.players().count();
            final List<String> agentStrings = new ArrayList<>(numPlayers);
            for (int i = 0; i < numPlayers; ++i) {
                final AI ai = this.aiPlayers.get(i + 1);
                final int playerIdx = i + 1;
                if (ai == null) {
                    try {
                        EventQueue.invokeAndWait(() -> Manager.app.addTextToAnalysisPanel("Cannot run evaluation; Player " + playerIdx + " is not AI.\n"));
                    }
                    catch (InvocationTargetException | InterruptedException ex3) {
                        ex3.printStackTrace();
                    }
                    return;
                }
                if (!ai.supportsGame(game)) {
                    try {
                        EventQueue.invokeAndWait(() -> Manager.app.addTextToAnalysisPanel("Cannot run evaluation; " + ai.friendlyName + " does not support this game.\n"));
                    }
                    catch (InvocationTargetException | InterruptedException ex4) {
                        ex4.printStackTrace();
                    }
                    return;
                }
                agentStrings.add(Manager.aiSelected()[i + 1].name());
            }
            final Context context = this.ref.context();
            final List<TIntArrayList> aiListPermutations = ListUtils.generatePermutations(TIntArrayList.wrap(IntStream.range(1, numPlayers + 1).toArray()));
            final ResultsSummary resultsSummary = new ResultsSummary(game, agentStrings);
            final Timer updateGuiTimer = new Timer();
            try {
                for (int gameCounter = 0; gameCounter < 100; ++gameCounter) {
                    final List<AI> currentAIList = new ArrayList<>(numPlayers);
                    final int currentAIsPermutation = gameCounter % aiListPermutations.size();
                    final TIntArrayList currentPlayersPermutation = aiListPermutations.get(currentAIsPermutation);
                    currentAIList.add(null);
                    for (int j = 0; j < currentPlayersPermutation.size(); ++j) {
                        final AI ai2 = this.aiPlayers.get(currentPlayersPermutation.getQuick(j));
                        currentAIList.add(ai2);
                    }
                    game.start(context);
                    updateGuiTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Manager.app.repaint();
                        }
                    }, 40L, 40L);
                    for (int p = 1; p < currentAIList.size(); ++p) {
                        currentAIList.get(p).initAI(game, p);
                    }
                    while (!context.trial().over()) {
                        if (SettingsManager.agentsPaused) {
                            final int passedAtGameCounter = gameCounter;
                            EventQueue.invokeLater(() -> Manager.app.addTextToAnalysisPanel("Evaluation interrupted after completing " + passedAtGameCounter + " games."));
                            return;
                        }
                        context.model().startNewStep(context, currentAIList, AIDetails.convertToThinkTimeArray(Manager.aiSelected()), -1, -1, 0.3, false, false, false, null, null);
                        Manager.setLiveAIs(context.model().getLiveAIs());
                        while (!context.model().isReady()) {
                            Thread.sleep(100L);
                        }
                        Manager.setLiveAIs(null);
                    }
                    final double[] utilities = AIUtils.agentUtilities(context);
                    final int numMovesPlayed = context.trial().moves().size() - context.trial().numInitialPlacementMoves();
                    final int[] agentPermutation = new int[currentPlayersPermutation.size() + 1];
                    currentPlayersPermutation.toArray(agentPermutation, 0, 1, currentPlayersPermutation.size());
                    for (int p2 = 1; p2 < agentPermutation.length; ++p2) {
                        final int[] array = agentPermutation;
                        final int n = p2;
                        --array[n];
                    }
                    resultsSummary.recordResults(agentPermutation, utilities, numMovesPlayed);
                    Manager.app.addTextToAnalysisPanel(resultsSummary.generateIntermediateSummary());
                    Manager.app.addTextToAnalysisPanel("\n");
                    for (int p2 = 1; p2 < currentAIList.size(); ++p2) {
                        currentAIList.get(p2).closeAI();
                    }
                    Thread.sleep(1000L);
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            finally {
                updateGuiTimer.cancel();
                updateGuiTimer.purge();
                Manager.setLiveAIs(null);
                Manager.app.repaint();
            }
        }
    }
}
