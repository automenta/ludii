// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.experiments;

import game.Game;
import manager.Manager;
import metrics.Metric;
import util.AI;
import util.Context;
import util.GameLoader;
import util.Trial;
import utils.AIUtils;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class EvalGamesThread extends Thread
{
    protected final EvalGamesThreadRunnable runnable;
    
    public static EvalGamesThread construct(final String gameName, final List<String> gameOptions, final List<AI> aiPlayers, final int numGames, final double thinkingTime, final int maxNumTurns, final List<Metric> metricsToEvaluate, final ArrayList<Double> weights) {
        final EvalGamesThreadRunnable runnable = new EvalGamesThreadRunnable(gameName, gameOptions, aiPlayers, numGames, thinkingTime, maxNumTurns, metricsToEvaluate, weights);
        return new EvalGamesThread(runnable);
    }
    
    protected EvalGamesThread(final EvalGamesThreadRunnable runnable) {
        super(runnable);
        this.runnable = runnable;
    }
    
    private static class EvalGamesThreadRunnable implements Runnable
    {
        protected final Game game;
        protected final List<AI> aiPlayers;
        protected final int numGames;
        protected final double[] thinkingTime;
        protected final List<Metric> metricsToEvaluate;
        protected final ArrayList<Double> weights;
        
        public EvalGamesThreadRunnable(final String gameName, final List<String> gameOptions, final List<AI> aiPlayers, final int numGames, final double thinkingTime, final int maxNumTurns, final List<Metric> metricsToEvaluate, final ArrayList<Double> weights) {
            (this.game = GameLoader.loadGameFromName(gameName + ".lud", gameOptions)).setMaxTurns(maxNumTurns);
            this.aiPlayers = aiPlayers;
            this.numGames = numGames;
            this.thinkingTime = new double[aiPlayers.size()];
            this.metricsToEvaluate = metricsToEvaluate;
            this.weights = weights;
            for (int p = 1; p < aiPlayers.size(); ++p) {
                this.thinkingTime[p] = thinkingTime;
            }
        }
        
        @Override
        public void run() {
            for (int numPlayers = this.game.players().count(), i = 0; i < numPlayers; ++i) {
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
                if (!ai.supportsGame(this.game)) {
                    try {
                        EventQueue.invokeAndWait(() -> Manager.app.addTextToAnalysisPanel("Cannot run evaluation; " + ai.friendlyName + " does not support this game.\n"));
                    }
                    catch (InvocationTargetException | InterruptedException ex4) {
                        ex4.printStackTrace();
                    }
                    return;
                }
            }
            Manager.app.addTextToAnalysisPanel("Please don't touch anything until complete!\nRunning analysis.");
            final List<Trial> allStoredTrials = new ArrayList<>();
            final double[] sumScores = new double[this.game.players().count() + 1];
            int numDraws = 0;
            int numTimeouts = 0;
            long sumNumMoves = 0L;
            final Context context = new Context(this.game, new Trial(this.game));
            try {
                for (int gameCounter = 0; gameCounter < this.numGames; ++gameCounter) {
                    this.game.start(context);
                    for (int p = 1; p <= this.game.players().count(); ++p) {
                        this.aiPlayers.get(p).initAI(this.game, p);
                    }
                    while (!context.trial().over()) {
                        context.model().startNewStep(context, this.aiPlayers, this.thinkingTime, -1, -1, 0.0, true, false, false, null, null);
                        while (!context.model().isReady()) {
                            Thread.sleep(100L);
                        }
                    }
                    final double[] utils = AIUtils.agentUtilities(context);
                    for (int p2 = 1; p2 <= this.game.players().count(); ++p2) {
                        final double[] array = sumScores;
                        final int n = p2;
                        array[n] += (utils[p2] + 1.0) / 2.0;
                    }
                    if (context.trial().status().winner() == 0) {
                        ++numDraws;
                    }
                    if (context.state().numTurn() >= this.game.getMaxTurnLimit() * this.game.players().count() || context.trial().numMoves() - context.trial().numInitialPlacementMoves() >= this.game.getMaxMoveLimit()) {
                        ++numTimeouts;
                    }
                    sumNumMoves += context.trial().numMoves() - context.trial().numInitialPlacementMoves();
                    Manager.app.addTextToAnalysisPanel(".");
                    allStoredTrials.add(new Trial(context.trial()));
                    for (int p2 = 1; p2 < this.aiPlayers.size(); ++p2) {
                        this.aiPlayers.get(p2).closeAI();
                    }
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            final DecimalFormat df = new DecimalFormat("#.##");
            final String drawPercentage = df.format(numDraws * 100.0 / this.numGames) + "%";
            final String timeoutPercentage = df.format(numTimeouts * 100.0 / this.numGames) + "%";
            Manager.app.addTextToAnalysisPanel("\n\nAgent type: " + this.aiPlayers.get(0).friendlyName);
            Manager.app.addTextToAnalysisPanel("\nDraw likelihood: " + drawPercentage);
            Manager.app.addTextToAnalysisPanel("\nTimeout likelihood: " + timeoutPercentage);
            Manager.app.addTextToAnalysisPanel("\nAverage number of moves per game: " + df.format(sumNumMoves / (double)this.numGames));
            for (int j = 1; j < sumScores.length; ++j) {
                Manager.app.addTextToAnalysisPanel("\nPlayer " + j + " win rate: " + df.format(sumScores[j] * 100.0 / this.numGames) + "%");
            }
            Manager.app.addTextToAnalysisPanel("\n\n");
            double finalScore = 0.0;
            for (int m = 0; m < this.metricsToEvaluate.size(); ++m) {
                final Metric metric = this.metricsToEvaluate.get(m);
                final double score = metric.apply(this.game, "", allStoredTrials.toArray(new Trial[allStoredTrials.size()]));
                final double weight = this.weights.get(m);
                Manager.app.addTextToAnalysisPanel(metric.name() + ": " + df.format(score) + " (weight: " + weight + ")\n");
                finalScore += score * weight;
            }
            Manager.app.addTextToAnalysisPanel("Final Score: " + df.format(finalScore) + "\n\n");
        }
    }
}
