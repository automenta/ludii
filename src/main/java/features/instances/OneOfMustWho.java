// 
// Decompiled by Procyon v0.5.36
// 

package features.instances;

import game.types.board.SiteType;
import main.collections.ChunkSet;
import util.state.State;
import util.state.containerState.ContainerState;

public final class OneOfMustWho implements BitwiseTest
{
    protected final ChunkSet mustWhos;
    protected final ChunkSet mustWhosMask;
    protected final int firstUsedWord;
    protected final SiteType graphElementType;
    
    public OneOfMustWho(final ChunkSet mustWhos, final ChunkSet mustWhosMask, final SiteType graphElementType) {
        this.mustWhos = mustWhos;
        this.mustWhosMask = mustWhosMask;
        this.graphElementType = graphElementType;
        this.firstUsedWord = mustWhosMask.nextSetBit(0) / 64;
    }
    
    @Override
    public final boolean matches(final State state) {
        final ContainerState container = state.containerStates()[0];
        switch (this.graphElementType) {
            case Cell: {
                return container.violatesNotWhoCell(this.mustWhosMask, this.mustWhos, this.firstUsedWord);
            }
            case Vertex: {
                return container.violatesNotWhoVertex(this.mustWhosMask, this.mustWhos, this.firstUsedWord);
            }
            case Edge: {
                return container.violatesNotWhoEdge(this.mustWhosMask, this.mustWhos, this.firstUsedWord);
            }
            default: {
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
    
    public final ChunkSet mustWhos() {
        return this.mustWhos;
    }
    
    public final ChunkSet mustWhosMask() {
        return this.mustWhosMask;
    }
    
    @Override
    public String toString() {
        String requirementsStr = "";
        for (int i = 0; i < this.mustWhos.numChunks(); ++i) {
            if (this.mustWhosMask.getChunk(i) != 0) {
                requirementsStr = requirementsStr + i + " must belong to " + this.mustWhos.getChunk(i) + ", ";
            }
        }
        return String.format("One of these who-conditions must hold: [%s]", requirementsStr);
    }
}
