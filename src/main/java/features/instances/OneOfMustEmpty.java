// 
// Decompiled by Procyon v0.5.36
// 

package features.instances;

import collections.ChunkSet;
import game.types.board.SiteType;
import util.state.State;
import util.state.containerState.ContainerState;

public final class OneOfMustEmpty implements BitwiseTest
{
    protected final ChunkSet mustEmpties;
    protected final int firstUsedWord;
    protected final SiteType graphElementType;
    
    public OneOfMustEmpty(final ChunkSet mustEmpties, final SiteType graphElementType) {
        this.mustEmpties = mustEmpties;
        this.graphElementType = graphElementType;
        this.firstUsedWord = mustEmpties.nextSetBit(0) / 64;
    }
    
    @Override
    public final boolean matches(final State state) {
        final ContainerState container = state.containerStates()[0];
        ChunkSet chunkSet = null;
        switch (this.graphElementType) {
            case Cell -> chunkSet = container.emptyChunkSetCell();
            case Vertex -> chunkSet = container.emptyChunkSetVertex();
            case Edge -> chunkSet = container.emptyChunkSetEdge();
            default -> chunkSet = null;
        }
        return chunkSet.violatesNot(this.mustEmpties, this.mustEmpties, this.firstUsedWord);
    }
    
    @Override
    public final boolean hasNoTests() {
        return false;
    }
    
    @Override
    public final boolean onlyRequiresSingleMustEmpty() {
        return true;
    }
    
    @Override
    public final boolean onlyRequiresSingleMustWho() {
        return false;
    }
    
    @Override
    public final boolean onlyRequiresSingleMustWhat() {
        return false;
    }
    
    @Override
    public final SiteType graphElementType() {
        return this.graphElementType;
    }
    
    public final ChunkSet mustEmpties() {
        return this.mustEmpties;
    }
    
    @Override
    public String toString() {
        String requirementsStr = "";
        for (int i = this.mustEmpties.nextSetBit(0); i >= 0; i = this.mustEmpties.nextSetBit(i + 1)) {
            requirementsStr = requirementsStr + i + ", ";
        }
        return String.format("One of these must be empty: [%s]", requirementsStr);
    }
}
