// 
// Decompiled by Procyon v0.5.36
// 

package utils;

import game.Game;
import main.collections.FastArrayList;
import main.collections.StringPair;
import org.apache.commons.rng.core.RandomProviderDefaultState;
import util.AI;
import util.Context;
import util.Move;
import util.Trial;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AIUtils
{
    private AIUtils() {
    }
    
    public static AI defaultAiForGame(final Game game) {
        return new LudiiAI();
    }
    
    public static FastArrayList<Move> extractMovesForMover(final FastArrayList<Move> allMoves, final int mover) {
        final FastArrayList<Move> moves = new FastArrayList<>(allMoves.size());
        for (final Move move : allMoves) {
            if (move.mover() == mover) {
                moves.add(move);
            }
        }
        return moves;
    }
    
    public static double rankToUtil(final double rank, final int numPlayers) {
        if (numPlayers == 1) {
            return 2.0 * rank - 1.0;
        }
        return 1.0 - (rank - 1.0) * (2.0 / (numPlayers - 1));
    }
    
    public static double[] utilities(final Context context) {
        final double[] ranking = context.trial().ranking();
        final double[] utilities = new double[ranking.length];
        final int numPlayers = ranking.length - 1;
        for (int p = 1; p < ranking.length; ++p) {
            double rank = ranking[p];
            if (numPlayers > 1 && rank == 0.0) {
                rank = context.computeNextDrawRank();
            }
            utilities[p] = rankToUtil(rank, numPlayers);
        }
        return utilities;
    }
    
    public static double[] agentUtilities(final Context context) {
        final double[] utils = utilities(context);
        final double[] agentUtils = new double[utils.length];
        for (int p = 1; p < utils.length; ++p) {
            agentUtils[p] = utils[context.state().playerToAgent(p)];
        }
        return agentUtils;
    }
    
    public static boolean isAIMetadata(final StringPair pair) {
        final String key = pair.key();
        return key.startsWith("BestAgent") || key.startsWith("AIMetadataGameNameCheck") || isFeaturesMetadata(pair) || isHeuristicsMetadata(pair);
    }
    
    public static boolean isFeaturesMetadata(final StringPair pair) {
        final String key = pair.key();
        return key.startsWith("Features");
    }
    
    public static boolean isHeuristicsMetadata(final StringPair pair) {
        final String key = pair.key();
        return key.startsWith("DivNumBoardCells") || key.startsWith("DivNumInitPlacement") || key.startsWith("Logistic") || key.startsWith("Tanh") || key.startsWith("CentreProximity") || key.startsWith("CornerProximity") || key.startsWith("CurrentMoverHeuristic") || key.startsWith("Intercept") || key.startsWith("LineCompletionHeuristic") || key.startsWith("Material") || key.startsWith("MobilitySimple") || key.startsWith("OpponentPieceProximity") || key.startsWith("OwnRegionsCount") || key.startsWith("PlayerRegionsProximity") || key.startsWith("PlayerSiteMapCount") || key.startsWith("RegionProximity") || key.startsWith("Score") || key.startsWith("SidesProximity");
    }
    
    public static List<StringPair> extractFeaturesMetadata(final Game game, final List<String> gameOptions, final List<StringPair> metadata) {
        final List<StringPair> relevantFeaturesMetadata = new ArrayList<>();
        for (final StringPair pair : metadata) {
            if (isFeaturesMetadata(pair)) {
                final String key = pair.key();
                final String[] keySplit = key.split(Pattern.quote(":"));
                boolean allOptionsMatch = true;
                if (keySplit.length > 1) {
                    final String[] metadataOptions = keySplit[1].split(Pattern.quote(";"));
                    for (int i = 0; i < metadataOptions.length; ++i) {
                        if (!gameOptions.contains(metadataOptions[i])) {
                            allOptionsMatch = false;
                            break;
                        }
                    }
                }
                if (!allOptionsMatch) {
                    continue;
                }
                relevantFeaturesMetadata.add(pair);
            }
        }
        return relevantFeaturesMetadata;
    }
    
    public static List<StringPair> extractHeuristicsMetadata(final Game game, final List<String> gameOptions, final List<StringPair> metadata) {
        final List<StringPair> relevantHeuristicsMetadata = new ArrayList<>();
        for (final StringPair pair : metadata) {
            if (isHeuristicsMetadata(pair)) {
                final String key = pair.key();
                final String[] keySplit = key.split(Pattern.quote(":"));
                boolean allOptionsMatch = true;
                if (keySplit.length > 1) {
                    final String[] metadataOptions = keySplit[1].split(Pattern.quote(";"));
                    for (int i = 0; i < metadataOptions.length; ++i) {
                        if (!gameOptions.contains(metadataOptions[i])) {
                            allOptionsMatch = false;
                            break;
                        }
                    }
                }
                if (!allOptionsMatch) {
                    continue;
                }
                relevantHeuristicsMetadata.add(pair);
            }
        }
        return relevantHeuristicsMetadata;
    }
    
    public static void saveHeuristicScores(final Trial origTrial, final Context origContext, final RandomProviderDefaultState gameStartRNGState, final File file) {
        System.err.println("saveHeuristicScores() currently not implemented");
    }
}
