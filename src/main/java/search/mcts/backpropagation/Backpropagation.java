// 
// Decompiled by Procyon v0.5.36
// 

package search.mcts.backpropagation;

import search.mcts.nodes.BaseNode;
import util.Context;
import util.Move;

import java.util.ArrayList;
import java.util.List;

public final class Backpropagation
{
    public final int backpropFlags;
    public static final int GRAVE_STATS = 1;
    
    public Backpropagation(final int backpropFlags) {
        this.backpropFlags = backpropFlags;
    }
    
    public void update(final BaseNode startNode, final Context context, final double[] utilities, final int numPlayoutMoves) {
        BaseNode node = startNode;
        final boolean updateGRAVE = (this.backpropFlags & 0x1) != 0x0;
        final List<BaseNode.MoveKey> moveKeysAMAF = new ArrayList<>();
        final List<Move> trialMoves = context.trial().moves();
        final int numTrialMoves = trialMoves.size();
        int movesIdxAMAF = numTrialMoves - 1;
        if (updateGRAVE) {
            while (movesIdxAMAF >= numTrialMoves - numPlayoutMoves) {
                moveKeysAMAF.add(new BaseNode.MoveKey(trialMoves.get(movesIdxAMAF), movesIdxAMAF));
                --movesIdxAMAF;
            }
        }
        while (node != null) {
            node.update(utilities);
            if (updateGRAVE) {
                for (final BaseNode.MoveKey moveKey : moveKeysAMAF) {
                    final BaseNode.NodeStatistics orCreateGraveStatsEntry;
                    final BaseNode.NodeStatistics graveStats = orCreateGraveStatsEntry = node.getOrCreateGraveStatsEntry(moveKey);
                    ++orCreateGraveStatsEntry.visitCount;
                    final BaseNode.NodeStatistics nodeStatistics = graveStats;
                    nodeStatistics.accumulatedScore += utilities[context.state().playerToAgent(moveKey.move.mover())];
                }
                if (movesIdxAMAF >= 0) {
                    moveKeysAMAF.add(new BaseNode.MoveKey(trialMoves.get(movesIdxAMAF), movesIdxAMAF));
                    --movesIdxAMAF;
                }
            }
            node = node.parent();
        }
    }
}
