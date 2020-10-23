// 
// Decompiled by Procyon v0.5.36
// 

package util.state.owned;

import game.Game;
import game.equipment.component.Component;

import java.io.Serializable;
import java.util.Arrays;

public final class OwnedIndexMapper implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final int[][] mappedIndices;
    private final int[][] reverseMap;
    
    public OwnedIndexMapper(final Game game) {
        final Component[] components = game.equipment().components();
        final int fullPlayersDim = game.players().count() + 2;
        final int fullCompsDim = components.length;
        this.mappedIndices = new int[fullPlayersDim][fullCompsDim];
        this.reverseMap = new int[fullPlayersDim][];
        for (int p = 0; p < fullPlayersDim; ++p) {
            int nextIndex = 0;
            Arrays.fill(this.mappedIndices[p], -1);
            for (int e = 0; e < fullCompsDim; ++e) {
                final Component comp = components[e];
                if (comp != null && comp.owner() == p) {
                    this.mappedIndices[p][e] = nextIndex++;
                }
            }
            this.reverseMap[p] = new int[nextIndex];
            for (int i = 0; i < this.mappedIndices[p].length; ++i) {
                if (this.mappedIndices[p][i] >= 0) {
                    this.reverseMap[p][this.mappedIndices[p][i]] = i;
                }
            }
        }
    }
    
    public final int compIndex(final int playerIdx, final int origCompIdx) {
        return this.mappedIndices[playerIdx][origCompIdx];
    }
    
    public final int[] playerCompIndices(final int playerIdx) {
        return this.mappedIndices[playerIdx];
    }
    
    public final int numValidIndices(final int playerIdx) {
        return this.reverseMap[playerIdx].length;
    }
    
    public final int reverseMap(final int playerIdx, final int mappedIndex) {
        return this.reverseMap[playerIdx][mappedIndex];
    }
}
