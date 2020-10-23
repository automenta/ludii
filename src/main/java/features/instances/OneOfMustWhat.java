// 
// Decompiled by Procyon v0.5.36
// 

package features.instances;

import collections.ChunkSet;
import game.types.board.SiteType;
import util.state.State;
import util.state.containerState.ContainerState;

public final class OneOfMustWhat implements BitwiseTest
{
    protected final ChunkSet mustWhats;
    protected final ChunkSet mustWhatsMask;
    protected final int firstUsedWord;
    protected final SiteType graphElementType;
    
    public OneOfMustWhat(final ChunkSet mustWhats, final ChunkSet mustWhatsMask, final SiteType graphElementType) {
        this.mustWhats = mustWhats;
        this.mustWhatsMask = mustWhatsMask;
        this.graphElementType = graphElementType;
        this.firstUsedWord = mustWhatsMask.nextSetBit(0) / 64;
    }
    
    @Override
    public final boolean matches(final State state) {
        final ContainerState container = state.containerStates()[0];
        switch (this.graphElementType) {
            case Cell -> {
                return container.violatesNotWhatCell(this.mustWhatsMask, this.mustWhats, this.firstUsedWord);
            }
            case Vertex -> {
                return container.violatesNotWhatVertex(this.mustWhatsMask, this.mustWhats, this.firstUsedWord);
            }
            case Edge -> {
                return container.violatesNotWhatEdge(this.mustWhatsMask, this.mustWhats, this.firstUsedWord);
            }
            default -> {
                return false;
            }
        }
    }
    
    @Override
    public final boolean hasNoTests() {
        return false;
    }
    
    @Override
    public final boolean onlyRequiresSingleMustEmpty() {
        return false;
    }
    
    @Override
    public final boolean onlyRequiresSingleMustWho() {
        return true;
    }
    
    @Override
    public final boolean onlyRequiresSingleMustWhat() {
        return false;
    }
    
    @Override
    public final SiteType graphElementType() {
        return this.graphElementType;
    }
    
    public final ChunkSet mustWhats() {
        return this.mustWhats;
    }
    
    public final ChunkSet mustWhatsMask() {
        return this.mustWhatsMask;
    }
    
    @Override
    public String toString() {
        String requirementsStr = "";
        for (int i = 0; i < this.mustWhats.numChunks(); ++i) {
            if (this.mustWhatsMask.getChunk(i) != 0) {
                requirementsStr = requirementsStr + i + " must contain " + this.mustWhats.getChunk(i) + ", ";
            }
        }
        return String.format("One of these what-conditions must hold: [%s]", requirementsStr);
    }
}
