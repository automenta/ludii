// 
// Decompiled by Procyon v0.5.36
// 

package search.minimax;

import expert_iteration.ExItExperience;
import expert_iteration.ExpertPolicy;
import game.Game;
import language.compiler.Compiler;
import main.FileHandling;
import main.collections.FVector;
import main.collections.FastArrayList;
import main.grammar.Report;
import metadata.ai.Ai;
import metadata.ai.heuristics.Heuristics;
import metadata.ai.heuristics.terms.HeuristicTerm;
import metadata.ai.heuristics.terms.Material;
import metadata.ai.heuristics.terms.MobilitySimple;
import util.Context;
import util.Move;
import util.Trial;
import util.state.State;
import utils.AIUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AlphaBetaSearch extends ExpertPolicy
{
    private static final float ALPHA_INIT = -1000000.0f;
    private static final float BETA_INIT = 1000000.0f;
    private static final float PARANOID_OPP_WIN_SCORE = 10000.0f;
    public static final float ABS_HEURISTIC_WEIGHT_THRESHOLD = 0.01f;
    private Heuristics heuristicValueFunction;
    private final boolean heuristicsFromMetadata;
    protected double autoPlaySeconds;
    protected float estimatedRootScore;
    protected float maxHeuristicEval;
    protected float minHeuristicEval;
    protected String analysisReport;
    protected FastArrayList<Move> currentRootMoves;
    protected Move lastReturnedMove;
    protected Context lastSearchedRootContext;
    protected FVector rootValueEstimates;
    protected int numPlayersInGame;
    protected boolean provedWin;
    protected float rootAlphaInit;
    protected float rootBetaInit;
    protected FastArrayList<Move> sortedRootMoves;
    protected boolean searchedFullTree;
    
    public static AlphaBetaSearch createAlphaBeta() {
        return new AlphaBetaSearch();
    }
    
    public AlphaBetaSearch() {
        this.heuristicValueFunction = null;
        this.autoPlaySeconds = 0.0;
        this.estimatedRootScore = 0.0f;
        this.maxHeuristicEval = 0.0f;
        this.minHeuristicEval = 0.0f;
        this.analysisReport = null;
        this.currentRootMoves = null;
        this.lastReturnedMove = null;
        this.lastSearchedRootContext = null;
        this.rootValueEstimates = null;
        this.numPlayersInGame = 0;
        this.provedWin = false;
        this.rootAlphaInit = -1000000.0f;
        this.rootBetaInit = 1000000.0f;
        this.sortedRootMoves = null;
        this.searchedFullTree = false;
        this.friendlyName = "Alpha-Beta";
        this.heuristicsFromMetadata = true;
    }
    
    public AlphaBetaSearch(final String heuristicsFilepath) throws IOException {
        this.heuristicValueFunction = null;
        this.autoPlaySeconds = 0.0;
        this.estimatedRootScore = 0.0f;
        this.maxHeuristicEval = 0.0f;
        this.minHeuristicEval = 0.0f;
        this.analysisReport = null;
        this.currentRootMoves = null;
        this.lastReturnedMove = null;
        this.lastSearchedRootContext = null;
        this.rootValueEstimates = null;
        this.numPlayersInGame = 0;
        this.provedWin = false;
        this.rootAlphaInit = -1000000.0f;
        this.rootBetaInit = 1000000.0f;
        this.sortedRootMoves = null;
        this.searchedFullTree = false;
        this.friendlyName = "Alpha-Beta";
        final String heuristicsStr = FileHandling.loadTextContentsFromFile(heuristicsFilepath);
        this.heuristicValueFunction = (Heuristics)Compiler.compileObject(heuristicsStr, "metadata.ai.heuristics.Heuristics", new Report());
        this.heuristicsFromMetadata = false;
    }
    
    @Override
    public Move selectAction(final Game game, final Context context, final double maxSeconds, final int maxIterations, final int maxDepth) {
        this.provedWin = false;
        final int depthLimit = (maxDepth > 0) ? maxDepth : Integer.MAX_VALUE;
        this.lastSearchedRootContext = context;
        if (maxSeconds > 0.0) {
            final long startTime = System.currentTimeMillis();
            final long stopTime = startTime + (long)(maxSeconds * 1000.0);
            this.lastReturnedMove = this.iterativeDeepening(game, context, maxSeconds, depthLimit, 1);
            final long currentTime = System.currentTimeMillis();
            if (game.players().count() > 2 && currentTime < stopTime) {
                if (this.provedWin) {
                    return this.lastReturnedMove;
                }
                this.lastReturnedMove = this.iterativeDeepeningMaxN(game, context, (stopTime - currentTime) / 1000.0, depthLimit, 1);
            }
            return this.lastReturnedMove;
        }
        return this.lastReturnedMove = this.iterativeDeepening(game, context, maxSeconds, depthLimit, depthLimit);
    }
    
    public Move iterativeDeepening(final Game game, final Context context, final double maxSeconds, final int maxDepth, final int startDepth) {
        final long startTime = System.currentTimeMillis();
        long stopTime = (maxSeconds > 0.0) ? (startTime + (long)(maxSeconds * 1000.0)) : Long.MAX_VALUE;
        final int numPlayers = game.players().count();
        this.currentRootMoves = new FastArrayList<>(game.moves(context).moves());
        final FastArrayList<Move> tempMovesList = new FastArrayList<>(this.currentRootMoves);
        this.sortedRootMoves = new FastArrayList<>(this.currentRootMoves.size());
        while (!tempMovesList.isEmpty()) {
            this.sortedRootMoves.add(tempMovesList.removeSwap(ThreadLocalRandom.current().nextInt(tempMovesList.size())));
        }
        final int numRootMoves = this.sortedRootMoves.size();
        final List<ScoredMove> scoredMoves = new ArrayList<>(this.sortedRootMoves.size());
        if (numRootMoves == 1 && this.autoPlaySeconds >= 0.0 && this.autoPlaySeconds < maxSeconds) {
            stopTime = startTime + (long)(this.autoPlaySeconds * 1000.0);
        }
        this.rootValueEstimates = new FVector(this.currentRootMoves.size());
        final FVector moveScores = new FVector(numRootMoves);
        int searchDepth = startDepth - 1;
        final int maximisingPlayer = context.state().playerToAgent(context.state().mover());
        Move bestMoveCompleteSearch = this.sortedRootMoves.get(0);
        if (numPlayers > 2) {
            this.rootAlphaInit = (float)AIUtils.rankToUtil(context.computeNextLossRank(), numPlayers) * 1000000.0f;
            this.rootBetaInit = (float)AIUtils.rankToUtil(context.computeNextWinRank(), numPlayers) * 1000000.0f;
        }
        else {
            this.rootAlphaInit = -1000000.0f;
            this.rootBetaInit = 1000000.0f;
        }
        while (searchDepth < maxDepth) {
            ++searchDepth;
            this.searchedFullTree = true;
            float score = this.rootAlphaInit;
            float alpha = this.rootAlphaInit;
            final float beta = this.rootBetaInit;
            Move bestMove = this.sortedRootMoves.get(0);
            for (int i = 0; i < numRootMoves; ++i) {
                final Context copyContext = new Context(context);
                final Move m = this.sortedRootMoves.get(i);
                game.apply(copyContext, m);
                final float value = this.alphaBeta(copyContext, searchDepth - 1, alpha, beta, maximisingPlayer, stopTime);
                if (System.currentTimeMillis() >= stopTime || this.wantsInterrupt) {
                    bestMove = null;
                    break;
                }
                final int origMoveIdx = this.currentRootMoves.indexOf(m);
                if (origMoveIdx >= 0) {
                    this.rootValueEstimates.set(origMoveIdx, (float)this.scoreToValueEst(value, this.rootAlphaInit, this.rootBetaInit));
                }
                moveScores.set(i, value);
                if (value > score) {
                    score = value;
                    bestMove = m;
                }
                if (score > alpha) {
                    alpha = score;
                }
                if (alpha >= beta) {
                    break;
                }
            }
            if (bestMove != null) {
                this.estimatedRootScore = score;
                if (score == this.rootBetaInit) {
                    this.analysisReport = this.friendlyName + " found a proven win at depth " + searchDepth + ".";
                    this.provedWin = true;
                    return bestMove;
                }
                if (score == this.rootAlphaInit) {
                    this.analysisReport = this.friendlyName + " found a proven loss at depth " + searchDepth + ".";
                    return bestMoveCompleteSearch;
                }
                if (this.searchedFullTree) {
                    this.analysisReport = this.friendlyName + " completed search of depth " + searchDepth + " (no proven win or loss).";
                    return bestMove;
                }
                bestMoveCompleteSearch = bestMove;
            }
            else {
                --searchDepth;
            }
            if (System.currentTimeMillis() >= stopTime || this.wantsInterrupt) {
                this.analysisReport = this.friendlyName + " completed search of depth " + searchDepth + ".";
                return bestMoveCompleteSearch;
            }
            scoredMoves.clear();
            for (int i = 0; i < numRootMoves; ++i) {
                scoredMoves.add(new ScoredMove(this.sortedRootMoves.get(i), moveScores.get(i)));
            }
            Collections.sort(scoredMoves);
            this.sortedRootMoves.clear();
            for (int i = 0; i < numRootMoves; ++i) {
                this.sortedRootMoves.add(scoredMoves.get(i).move);
            }
            moveScores.fill(0, numRootMoves, 0.0f);
        }
        this.analysisReport = this.friendlyName + " completed search of depth " + searchDepth + ".";
        return bestMoveCompleteSearch;
    }
    
    public float alphaBeta(final Context context, final int depth, final float inAlpha, final float inBeta, final int maximisingPlayer, final long stopTime) {
        final Trial trial = context.trial();
        final State state = context.state();
        if (trial.over() || !context.active(maximisingPlayer)) {
            return (float)AIUtils.agentUtilities(context)[maximisingPlayer] * 1000000.0f;
        }
        if (depth == 0) {
            this.searchedFullTree = false;
            float heuristicScore = this.heuristicValueFunction.computeValue(context, maximisingPlayer, 0.01f);
            for (final int opp : this.opponents(maximisingPlayer)) {
                if (context.active(opp)) {
                    heuristicScore -= this.heuristicValueFunction.computeValue(context, opp, 0.01f);
                }
                else if (context.winners().contains(opp)) {
                    heuristicScore -= 10000.0f;
                }
            }
            if (state.playerToAgent(maximisingPlayer) != maximisingPlayer) {
                heuristicScore = -heuristicScore;
            }
            this.minHeuristicEval = Math.min(this.minHeuristicEval, heuristicScore);
            this.maxHeuristicEval = Math.max(this.maxHeuristicEval, heuristicScore);
            return heuristicScore;
        }
        final Game game = context.game();
        final int mover = state.playerToAgent(state.mover());
        final FastArrayList<Move> legalMoves = game.moves(context).moves();
        final int numLegalMoves = legalMoves.size();
        float alpha = inAlpha;
        float beta = inBeta;
        final int numPlayers = game.players().count();
        if (numPlayers > 2) {
            alpha = Math.max(alpha, (float)AIUtils.rankToUtil(context.computeNextLossRank(), numPlayers) * 1000000.0f);
            beta = Math.min(beta, (float)AIUtils.rankToUtil(context.computeNextWinRank(), numPlayers) * 1000000.0f);
        }
        if (mover == maximisingPlayer) {
            float score = -1000000.0f;
            for (int i = 0; i < numLegalMoves; ++i) {
                final Context copyContext = new Context(context);
                final Move m = legalMoves.get(i);
                game.apply(copyContext, m);
                final float value = this.alphaBeta(copyContext, depth - 1, alpha, beta, maximisingPlayer, stopTime);
                if (System.currentTimeMillis() >= stopTime || this.wantsInterrupt) {
                    return 0.0f;
                }
                if (value > score) {
                    score = value;
                }
                if (score > alpha) {
                    alpha = score;
                }
                if (alpha >= beta) {
                    break;
                }
            }
            return score;
        }
        float score = 1000000.0f;
        for (int i = 0; i < numLegalMoves; ++i) {
            final Context copyContext = new Context(context);
            final Move m = legalMoves.get(i);
            game.apply(copyContext, m);
            final float value = this.alphaBeta(copyContext, depth - 1, alpha, beta, maximisingPlayer, stopTime);
            if (System.currentTimeMillis() >= stopTime || this.wantsInterrupt) {
                return 0.0f;
            }
            if (value < score) {
                score = value;
            }
            if (score < beta) {
                beta = score;
            }
            if (alpha >= beta) {
                break;
            }
        }
        return score;
    }
    
    public Move iterativeDeepeningMaxN(final Game game, final Context context, final double maxSeconds, final int maxDepth, final int startDepth) {
        final long startTime = System.currentTimeMillis();
        long stopTime = (maxSeconds > 0.0) ? (startTime + (long)(maxSeconds * 1000.0)) : Long.MAX_VALUE;
        final int numRootMoves = this.sortedRootMoves.size();
        final List<ScoredMove> scoredMoves = new ArrayList<>(this.sortedRootMoves.size());
        if (numRootMoves == 1 && this.autoPlaySeconds >= 0.0 && this.autoPlaySeconds < maxSeconds) {
            stopTime = startTime + (long)(this.autoPlaySeconds * 1000.0);
        }
        this.rootValueEstimates = new FVector(this.currentRootMoves.size());
        final FVector moveScores = new FVector(numRootMoves);
        int searchDepth = startDepth - 1;
        final int maximisingPlayer = context.state().mover();
        final int numPlayers = game.players().count();
        Move bestMoveCompleteSearch = this.sortedRootMoves.get(0);
        this.rootAlphaInit = (float)AIUtils.rankToUtil(context.computeNextLossRank(), numPlayers) * 1000000.0f;
        this.rootBetaInit = (float)AIUtils.rankToUtil(context.computeNextWinRank(), numPlayers) * 1000000.0f;
        while (searchDepth < maxDepth) {
            ++searchDepth;
            this.searchedFullTree = true;
            float score = -1000000.0f;
            Move bestMove = this.sortedRootMoves.get(0);
            for (int i = 0; i < numRootMoves; ++i) {
                final Context copyContext = new Context(context);
                final Move m = this.sortedRootMoves.get(i);
                game.apply(copyContext, m);
                final float[] values = this.maxN(copyContext, searchDepth - 1, maximisingPlayer, this.rootAlphaInit, this.rootBetaInit, numPlayers, stopTime);
                if (System.currentTimeMillis() >= stopTime || this.wantsInterrupt) {
                    bestMove = null;
                    break;
                }
                final int origMoveIdx = this.currentRootMoves.indexOf(m);
                if (origMoveIdx >= 0) {
                    this.rootValueEstimates.set(origMoveIdx, (float)this.scoreToValueEst(values[maximisingPlayer], this.rootAlphaInit, this.rootBetaInit));
                }
                moveScores.set(i, values[maximisingPlayer]);
                if (values[maximisingPlayer] > score) {
                    score = values[maximisingPlayer];
                    bestMove = m;
                }
                if (score >= this.rootBetaInit) {
                    break;
                }
            }
            if (bestMove != null) {
                this.estimatedRootScore = score;
                if (score == this.rootBetaInit) {
                    this.analysisReport = this.analysisReport + " (subsequent Max^n found proven win at depth " + searchDepth + ")";
                    this.provedWin = true;
                    return bestMove;
                }
                if (score == this.rootAlphaInit) {
                    this.analysisReport = this.analysisReport + " (subsequent Max^n found proven loss at depth " + searchDepth + ")";
                    return bestMoveCompleteSearch;
                }
                if (this.searchedFullTree) {
                    this.analysisReport = this.analysisReport + " (subsequent Max^n completed search of depth " + searchDepth + " (no proven win or loss))";
                    return bestMove;
                }
                bestMoveCompleteSearch = bestMove;
            }
            else {
                --searchDepth;
            }
            if (System.currentTimeMillis() >= stopTime || this.wantsInterrupt) {
                this.analysisReport = this.analysisReport + " (subsequent Max^n completed search of depth " + searchDepth + ")";
                return bestMoveCompleteSearch;
            }
            scoredMoves.clear();
            for (int i = 0; i < numRootMoves; ++i) {
                scoredMoves.add(new ScoredMove(this.sortedRootMoves.get(i), moveScores.get(i)));
            }
            Collections.sort(scoredMoves);
            this.sortedRootMoves.clear();
            for (int i = 0; i < numRootMoves; ++i) {
                this.sortedRootMoves.add(scoredMoves.get(i).move);
            }
            moveScores.fill(0, numRootMoves, 0.0f);
        }
        this.analysisReport = this.analysisReport + " (subsequent Max^n completed search of depth " + searchDepth + ")";
        return bestMoveCompleteSearch;
    }
    
    public float[] maxN(final Context context, final int depth, final int maximisingPlayer, final float inAlpha, final float inBeta, final int numPlayers, final long stopTime) {
        final Trial trial = context.trial();
        final State state = context.state();
        if (trial.over()) {
            final double[] utils = AIUtils.utilities(context);
            final float[] toReturn = new float[utils.length];
            for (int p = 1; p < utils.length; ++p) {
                toReturn[p] = (float)utils[p] * 1000000.0f;
                if (toReturn[p] != inAlpha && toReturn[p] != inBeta) {
                    this.minHeuristicEval = Math.min(this.minHeuristicEval, toReturn[p]);
                    this.maxHeuristicEval = Math.max(this.maxHeuristicEval, toReturn[p]);
                }
            }
            return toReturn;
        }
        if (depth == 0) {
            this.searchedFullTree = false;
            final float[] playerScores = new float[numPlayers + 1];
            final double[] utils2 = (context.numActive() == numPlayers) ? null : AIUtils.utilities(context);
            for (int p = 1; p <= numPlayers; ++p) {
                if (context.active(p)) {
                    playerScores[p] = this.heuristicValueFunction.computeValue(context, p, 0.01f);
                }
                else {
                    playerScores[p] = (float)utils2[p] * 1000000.0f;
                }
            }
            final float oppScoreMultiplier = 1.0f / numPlayers;
            final float[] toReturn2 = new float[numPlayers + 1];
            for (int p2 = 1; p2 <= numPlayers; ++p2) {
                for (int other = 1; other <= numPlayers; ++other) {
                    if (other == p2) {
                        final float[] array = toReturn2;
                        final int n = p2;
                        array[n] += playerScores[other];
                    }
                    else {
                        final float[] array2 = toReturn2;
                        final int n2 = p2;
                        array2[n2] -= oppScoreMultiplier * playerScores[other];
                    }
                }
                this.minHeuristicEval = Math.min(this.minHeuristicEval, toReturn2[p2]);
                this.maxHeuristicEval = Math.max(this.maxHeuristicEval, toReturn2[p2]);
            }
            return toReturn2;
        }
        final Game game = context.game();
        final int mover = state.mover();
        final FastArrayList<Move> legalMoves = game.moves(context).moves();
        final float alpha = Math.max(inAlpha, (float)AIUtils.rankToUtil(context.computeNextLossRank(), numPlayers) * 1000000.0f);
        final float beta = Math.min(inBeta, (float)AIUtils.rankToUtil(context.computeNextWinRank(), numPlayers) * 1000000.0f);
        final int numLegalMoves = legalMoves.size();
        float[] returnScores = new float[numPlayers + 1];
        Arrays.fill(returnScores, -1000000.0f);
        float score = -1000000.0f;
        float maximisingPlayerTieBreaker = 1000000.0f;
        for (int i = 0; i < numLegalMoves; ++i) {
            final Context copyContext = new Context(context);
            final Move m = legalMoves.get(i);
            game.apply(copyContext, m);
            final float[] values = this.maxN(copyContext, depth - 1, maximisingPlayer, alpha, beta, numPlayers, stopTime);
            if (System.currentTimeMillis() >= stopTime || this.wantsInterrupt) {
                return null;
            }
            if (values[mover] > score) {
                score = values[mover];
                returnScores = values;
                maximisingPlayerTieBreaker = values[maximisingPlayer];
            }
            else if (values[mover] == score && mover != maximisingPlayer && values[maximisingPlayer] < maximisingPlayerTieBreaker) {
                returnScores = values;
                maximisingPlayerTieBreaker = values[maximisingPlayer];
            }
            if (score >= beta) {
                break;
            }
        }
        return returnScores;
    }
    
    public int[] opponents(final int player) {
        final int[] opponents = new int[this.numPlayersInGame - 1];
        int idx = 0;
        for (int p = 1; p <= this.numPlayersInGame; ++p) {
            if (p != player) {
                opponents[idx++] = p;
            }
        }
        return opponents;
    }
    
    public double scoreToValueEst(final float score, final float alpha, final float beta) {
        if (score == alpha) {
            return -1.0;
        }
        if (score == beta) {
            return 1.0;
        }
        return -0.8 + 1.6 * ((score - this.minHeuristicEval) / (this.maxHeuristicEval - this.minHeuristicEval));
    }
    
    @Override
    public void initAI(final Game game, final int playerID) {
        if (this.heuristicsFromMetadata) {
            final Ai aiMetadata = game.metadata().ai();
            if (aiMetadata != null && aiMetadata.heuristics() != null) {
                this.heuristicValueFunction = aiMetadata.heuristics();
            }
            else {
                this.heuristicValueFunction = new Heuristics(new HeuristicTerm[] { new Material(null, 1.0f, null), new MobilitySimple(null, 0.001f) });
            }
        }
        if (this.heuristicValueFunction != null) {
            this.heuristicValueFunction.init(game);
        }
        this.estimatedRootScore = 0.0f;
        this.maxHeuristicEval = 0.0f;
        this.minHeuristicEval = 0.0f;
        this.analysisReport = null;
        this.currentRootMoves = null;
        this.rootValueEstimates = null;
        this.lastSearchedRootContext = null;
        this.lastReturnedMove = null;
        this.numPlayersInGame = game.players().count();
    }
    
    @Override
    public boolean supportsGame(final Game game) {
        return game.players().count() > 1 && !game.isStochasticGame() && !game.hiddenInformation() && game.isAlternatingMoveGame();
    }
    
    @Override
    public double estimateValue() {
        return this.scoreToValueEst(this.estimatedRootScore, this.rootAlphaInit, this.rootBetaInit);
    }
    
    @Override
    public String generateAnalysisReport() {
        return this.analysisReport;
    }
    
    @Override
    public AIVisualisationData aiVisualisationData() {
        if (this.currentRootMoves == null || this.rootValueEstimates == null) {
            return null;
        }
        final FVector aiDistribution = this.rootValueEstimates.copy();
        aiDistribution.subtract(aiDistribution.min());
        return new AIVisualisationData(aiDistribution, this.rootValueEstimates, this.currentRootMoves);
    }
    
    @Override
    public FastArrayList<Move> lastSearchRootMoves() {
        final FastArrayList<Move> moves = new FastArrayList<>(this.currentRootMoves.size());
        for (final Move move : this.currentRootMoves) {
            moves.add(move);
        }
        return moves;
    }
    
    @Override
    public FVector computeExpertPolicy(final double tau) {
        final FVector distribution = FVector.zeros(this.currentRootMoves.size());
        distribution.set(this.currentRootMoves.indexOf(this.lastReturnedMove), 1.0f);
        distribution.softmax();
        return distribution;
    }
    
    @Override
    public ExItExperience generateExItExperience() {
        return new ExItExperience(new ExItExperience.ExItExperienceState(this.lastSearchedRootContext), this.currentRootMoves, this.computeExpertPolicy(1.0), FVector.zeros(this.currentRootMoves.size()));
    }
    
    public static AlphaBetaSearch fromLines(final String[] lines) {
        String friendlyName = "Alpha-Beta";
        String heuristicsFilepath = null;
        for (final String line : lines) {
            final String[] lineParts = line.split(",");
            if (lineParts[0].toLowerCase().startsWith("heuristics=")) {
                heuristicsFilepath = lineParts[0].substring("heuristics=".length());
            }
            else if (lineParts[0].toLowerCase().startsWith("friendly_name=")) {
                friendlyName = lineParts[0].substring("friendly_name=".length());
            }
        }
        AlphaBetaSearch alphaBeta = null;
        if (heuristicsFilepath != null) {
            try {
                alphaBeta = new AlphaBetaSearch(heuristicsFilepath);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (alphaBeta == null) {
            alphaBeta = new AlphaBetaSearch();
        }
        alphaBeta.friendlyName = friendlyName;
        return alphaBeta;
    }
    
    private class ScoredMove implements Comparable<ScoredMove>
    {
        public final Move move;
        public final float score;
        
        public ScoredMove(final Move move, final float score) {
            this.move = move;
            this.score = score;
        }
        
        @Override
        public int compareTo(final ScoredMove other) {
            final float delta = other.score - this.score;
            if (delta < 0.0f) {
                return -1;
            }
            if (delta > 0.0f) {
                return 1;
            }
            return 0;
        }
    }
}
