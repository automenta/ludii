// 
// Decompiled by Procyon v0.5.36
// 

package utils.data_structures.transposition_table.alphabeta;

import util.Move;

public interface AlphaBetaTTHandler
{
    final class ABTTData
    {
        public Move bestMove;
        public long fullHash;
        
        public ABTTData() {
            this.bestMove = null;
            this.fullHash = -1L;
        }
    }
}
