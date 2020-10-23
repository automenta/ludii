// 
// Decompiled by Procyon v0.5.36
// 

package util.state.puzzleState;

import game.Game;
import game.equipment.container.Container;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.equipment.Region;
import main.collections.ChunkSet;
import util.Sites;
import util.UnionInfoD;
import util.state.State;
import util.state.containerState.ContainerState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class BaseContainerStateDeductionPuzzles implements ContainerState
{
    private static final long serialVersionUID = 1L;
    private transient Container container;
    private transient String nameFromFile;
    protected final int offset;
    private final Region empty;
    
    public BaseContainerStateDeductionPuzzles(final Game game, final Container container, final int numSites) {
        this.nameFromFile = null;
        this.container = container;
        this.empty = new Region(numSites);
        this.offset = game.equipment().sitesFrom()[container.index()];
    }
    
    public BaseContainerStateDeductionPuzzles(final BaseContainerStateDeductionPuzzles other) {
        this.nameFromFile = null;
        this.container = other.container;
        this.empty = new Region(other.empty);
        this.offset = other.offset;
    }
    
    @Override
    public void reset(final State trialState, final Game game) {
        final int numSites = this.container.numSites();
        this.empty.set(numSites);
    }
    
    @Override
    public String nameFromFile() {
        return this.nameFromFile;
    }
    
    @Override
    public Container container() {
        return this.container;
    }
    
    @Override
    public void setContainer(final Container cont) {
        this.container = cont;
    }
    
    @Override
    public Sites emptySites() {
        return new Sites(this.empty.sites());
    }
    
    @Override
    public int numEmpty() {
        return this.empty.count();
    }
    
    @Override
    public Region emptyRegion(final SiteType type) {
        return this.empty;
    }
    
    @Override
    public void addToEmpty(final int site) {
        this.empty.add(site - this.offset);
    }
    
    @Override
    public void removeFromEmpty(final int site) {
        this.empty.remove(site - this.offset);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(this.container.name());
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.nameFromFile = in.readUTF();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.empty.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof BaseContainerStateDeductionPuzzles)) {
            return false;
        }
        final BaseContainerStateDeductionPuzzles other = (BaseContainerStateDeductionPuzzles)obj;
        return this.empty.equals(other.empty);
    }
    
    @Override
    public int stateCell(final int site) {
        return 0;
    }
    
    @Override
    public int rotationCell(final int site) {
        return 0;
    }
    
    @Override
    public boolean isInvisibleCell(final int site, final int who) {
        throw new UnsupportedOperationException("Not supported for puzzles");
    }
    
    @Override
    public boolean isVisibleCell(final int site, final int who) {
        return true;
    }
    
    @Override
    public boolean isMaskedCell(final int site, final int who) {
        throw new UnsupportedOperationException("Not supported for puzzles");
    }
    
    @Override
    public void setInvisibleCell(final State state, final int site, final int who) {
        throw new UnsupportedOperationException("Not supported for puzzles");
    }
    
    @Override
    public void setVisibleCell(final State state, final int site, final int who) {
        throw new UnsupportedOperationException("Not supported for puzzles");
    }
    
    @Override
    public void setMaskedCell(final State state, final int site, final int who) {
        throw new UnsupportedOperationException("Not supported for puzzles");
    }
    
    @Override
    public boolean isPlayable(final int site) {
        throw new UnsupportedOperationException("Not supported for puzzles");
    }
    
    @Override
    public boolean isOccupied(final int site) {
        throw new UnsupportedOperationException("Not supported for puzzles");
    }
    
    @Override
    public void setSite(final State trialState, final int site, final int who, final int what, final int count, final int state, final int rotation, final int valueVal, final SiteType type) {
        throw new UnsupportedOperationException("Not supported for puzzles");
    }
    
    public abstract int numberEdge(final int var);
    
    public abstract void resetVariable(final SiteType type, final int var, final int numValues);
    
    @Override
    public boolean isResolved(final int var, final SiteType type) {
        if (type == SiteType.Cell) {
            return this.isResolvedCell(var);
        }
        if (type == SiteType.Vertex) {
            return this.isResolvedVerts(var);
        }
        return this.isResolvedEdges(var);
    }
    
    @Override
    public boolean bit(final int var, final int value, final SiteType type) {
        if (type == SiteType.Cell) {
            return this.bitCell(var, value);
        }
        if (type == SiteType.Vertex) {
            return this.bitVert(var, value);
        }
        return this.bitEdge(var, value);
    }
    
    @Override
    public void set(final int var, final int value, final SiteType type) {
        if (type == SiteType.Cell) {
            this.setCell(var, value);
        }
        else if (type == SiteType.Vertex) {
            this.setVert(var, value);
        }
        else {
            this.setEdge(var, value);
        }
    }
    
    public abstract boolean bitVert(final int var, final int value);
    
    public abstract void setVert(final int var, final int value);
    
    public abstract void toggleVerts(final int var, final int value);
    
    @Override
    public abstract int whatVertex(final int var);
    
    @Override
    public abstract int whatEdge(final int var);
    
    public abstract boolean bitEdge(final int var, final int value);
    
    public abstract void setEdge(final int var, final int value);
    
    public abstract void toggleEdges(final int var, final int value);
    
    public abstract boolean bitCell(final int var, final int value);
    
    public abstract void setCell(final int var, final int value);
    
    public abstract void toggleCells(final int var, final int value);
    
    @Override
    public UnionInfoD[] unionInfoAdjacent() {
        return null;
    }
    
    @Override
    public UnionInfoD[] unionInfoBlockingAdjacent() {
        return null;
    }
    
    @Override
    public UnionInfoD[] unionInfoOrthogonal() {
        return null;
    }
    
    @Override
    public UnionInfoD[] unionInfoBlockingOrthogonal() {
        return null;
    }
    
    @Override
    public UnionInfoD[] unionInfo(final AbsoluteDirection dir) {
        return null;
    }
    
    @Override
    public UnionInfoD[] unionInfoBlocking(final AbsoluteDirection dir) {
        return null;
    }
    
    @Override
    public int what(final int site, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.whatCell(site);
        }
        if (graphElementType == SiteType.Edge) {
            return this.whatEdge(site);
        }
        return this.whatVertex(site);
    }
    
    @Override
    public int who(final int site, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.whoCell(site);
        }
        if (graphElementType == SiteType.Edge) {
            return this.whoEdge(site);
        }
        return this.whoVertex(site);
    }
    
    @Override
    public int count(final int site, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.countCell(site);
        }
        if (graphElementType == SiteType.Edge) {
            return this.countEdge(site);
        }
        return this.countVertex(site);
    }
    
    @Override
    public int sizeStack(final int site, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.sizeStackCell(site);
        }
        if (graphElementType == SiteType.Edge) {
            return (this.whatEdge(site) != 0) ? 1 : 0;
        }
        return (this.whatVertex(site) != 0) ? 1 : 0;
    }
    
    @Override
    public boolean isInvisible(final int site, final int owner, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.isInvisibleCell(site, owner);
        }
        if (graphElementType == SiteType.Edge) {
            return this.isInvisibleEdge(site, owner);
        }
        return this.isInvisibleVertex(site, owner);
    }
    
    @Override
    public boolean isVisible(final int site, final int owner, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.isVisibleCell(site, owner);
        }
        if (graphElementType == SiteType.Edge) {
            return this.isVisibleEdge(site, owner);
        }
        return this.isVisibleVertex(site, owner);
    }
    
    @Override
    public boolean isMasked(final int site, final int owner, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.isMaskedCell(site, owner);
        }
        if (graphElementType == SiteType.Edge) {
            return this.isMaskedEdge(site, owner);
        }
        return this.isMaskedVertex(site, owner);
    }
    
    @Override
    public int state(final int site, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.stateCell(site);
        }
        if (graphElementType == SiteType.Edge) {
            return this.stateEdge(site);
        }
        return this.stateVertex(site);
    }
    
    @Override
    public int rotation(final int site, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.rotationCell(site);
        }
        if (graphElementType == SiteType.Edge) {
            return this.rotationEdge(site);
        }
        return this.rotationVertex(site);
    }
    
    @Override
    public int what(final int site, final int level, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.whatCell(site, level);
        }
        if (graphElementType == SiteType.Edge) {
            return this.whatEdge(site);
        }
        return this.whatVertex(site);
    }
    
    @Override
    public int who(final int site, final int level, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.whoCell(site, level);
        }
        if (graphElementType == SiteType.Edge) {
            return this.whoEdge(site);
        }
        return this.whoVertex(site);
    }
    
    @Override
    public boolean isInvisible(final int site, final int level, final int owner, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.isInvisibleCell(site, level, owner);
        }
        if (graphElementType == SiteType.Edge) {
            return this.isInvisibleEdge(site, owner);
        }
        return this.isInvisibleVertex(site, owner);
    }
    
    @Override
    public boolean isVisible(final int site, final int level, final int owner, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.isVisibleCell(site, level, owner);
        }
        if (graphElementType == SiteType.Edge) {
            return this.isVisibleEdge(site, owner);
        }
        return this.isVisibleVertex(site, owner);
    }
    
    @Override
    public boolean isMasked(final int site, final int level, final int owner, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.isMaskedCell(site, level, owner);
        }
        if (graphElementType == SiteType.Edge) {
            return this.isMaskedEdge(site, owner);
        }
        return this.isMaskedVertex(site, owner);
    }
    
    @Override
    public int state(final int site, final int level, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.stateCell(site, level);
        }
        if (graphElementType == SiteType.Edge) {
            return this.stateEdge(site);
        }
        return this.stateVertex(site);
    }
    
    @Override
    public int rotation(final int site, final int level, final SiteType graphElementType) {
        if (graphElementType == SiteType.Cell || this.container().index() != 0 || graphElementType == null) {
            return this.rotationCell(site, level);
        }
        if (graphElementType == SiteType.Edge) {
            return this.rotationEdge(site);
        }
        return this.rotationVertex(site);
    }
    
    @Override
    public int whatVertex(final int site, final int level) {
        return 0;
    }
    
    @Override
    public int stateVertex(final int site, final int level) {
        return 0;
    }
    
    @Override
    public int rotationVertex(final int site, final int level) {
        return 0;
    }
    
    @Override
    public boolean isInvisibleVertex(final int site, final int level, final int w) {
        return false;
    }
    
    @Override
    public boolean isVisibleVertex(final int site, final int level, final int w) {
        return false;
    }
    
    @Override
    public boolean isMaskedVertex(final int site, final int level, final int w) {
        return false;
    }
    
    @Override
    public void setInvisibleVertex(final State trialState, final int site, final int level, final int who) {
    }
    
    @Override
    public void setMaskedVertex(final State trialState, final int site, final int level, final int who) {
    }
    
    @Override
    public void setVisibleVertex(final State trialState, final int site, final int level, final int who) {
    }
    
    @Override
    public int whatEdge(final int site, final int level) {
        return 0;
    }
    
    @Override
    public int stateEdge(final int site, final int level) {
        return 0;
    }
    
    @Override
    public int rotationEdge(final int site, final int level) {
        return 0;
    }
    
    @Override
    public boolean isInvisibleEdge(final int site, final int level, final int w) {
        return false;
    }
    
    @Override
    public boolean isVisibleEdge(final int site, final int level, final int w) {
        return false;
    }
    
    @Override
    public boolean isMaskedEdge(final int site, final int level, final int w) {
        return false;
    }
    
    @Override
    public void setInvisibleEdge(final State trialState, final int site, final int level, final int who) {
    }
    
    @Override
    public void setMaskedEdge(final State trialState, final int site, final int level, final int who) {
    }
    
    @Override
    public void setVisibleEdge(final State trialState, final int site, final int level, final int who) {
    }
    
    @Override
    public void addItemVertex(final State trialState, final int site, final int whatValue, final int whoId, final Game game) {
    }
    
    @Override
    public void insertVertex(final State trialState, final int site, final int level, final int whatValue, final int whoId, final Game game) {
    }
    
    @Override
    public void addItemVertex(final State trialState, final int site, final int whatValue, final int whoId, final int stateVal, final int rotationVal, final Game game) {
    }
    
    @Override
    public void addItemVertex(final State trialState, final int site, final int whatValue, final int whoId, final Game game, final boolean[] hiddenValues, final boolean masked) {
    }
    
    @Override
    public void removeStackVertex(final State trialState, final int site) {
    }
    
    @Override
    public void addItemEdge(final State trialState, final int site, final int whatValue, final int whoId, final Game game) {
    }
    
    @Override
    public void insertEdge(final State trialState, final int site, final int level, final int whatValue, final int whoId, final Game game) {
    }
    
    @Override
    public void addItemEdge(final State trialState, final int site, final int whatValue, final int whoId, final int stateVal, final int rotationVal, final Game game) {
    }
    
    @Override
    public void addItemEdge(final State trialState, final int site, final int whatValue, final int whoId, final Game game, final boolean[] hiddenValues, final boolean masked) {
    }
    
    @Override
    public void removeStackEdge(final State trialState, final int site) {
    }
    
    @Override
    public void addItemGeneric(final State trialState, final int site, final int whatValue, final int whoId, final Game game, final SiteType graphElementType) {
    }
    
    @Override
    public void addItemGeneric(final State trialState, final int site, final int whatValue, final int whoId, final int stateVal, final int rotationVal, final Game game, final SiteType graphElementType) {
    }
    
    @Override
    public void addItemGeneric(final State trialState, final int site, final int whatValue, final int whoId, final Game game, final boolean[] hiddenValues, final boolean masked, final SiteType graphElementType) {
    }
    
    @Override
    public void removeStackGeneric(final State trialState, final int site, final SiteType graphElementType) {
    }
    
    @Override
    public boolean isEmpty(final int site, final SiteType type) {
        if (type == SiteType.Cell || this.container().index() != 0 || type == null) {
            return this.isEmptyCell(site);
        }
        if (type.equals(SiteType.Edge)) {
            return this.isEmptyEdge(site);
        }
        return this.isEmptyVertex(site);
    }
    
    @Override
    public boolean isEmptyVertex(final int vertex) {
        return true;
    }
    
    @Override
    public boolean isEmptyEdge(final int edge) {
        return true;
    }
    
    @Override
    public boolean isEmptyCell(final int site) {
        return this.empty.contains(site - this.offset);
    }
    
    @Override
    public int whoEdge(final int site, final int level) {
        return this.whoEdge(site);
    }
    
    @Override
    public int whoEdge(final int site) {
        return (this.whatEdge(site) != 0) ? 1 : 0;
    }
    
    @Override
    public int countEdge(final int site) {
        return (this.whatEdge(site) != 0) ? 1 : 0;
    }
    
    @Override
    public int whoVertex(final int site, final int level) {
        return this.whoVertex(site);
    }
    
    @Override
    public int whoVertex(final int site) {
        return (this.whatVertex(site) != 0) ? 1 : 0;
    }
    
    @Override
    public int countVertex(final int site) {
        return (this.whatVertex(site) != 0) ? 1 : 0;
    }
    
    @Override
    public int whoCell(final int site, final int level) {
        return this.whoCell(site);
    }
    
    @Override
    public int whoCell(final int site) {
        return (this.whatCell(site) != 0) ? 1 : 0;
    }
    
    @Override
    public int countCell(final int site) {
        return (this.whatCell(site) != 0) ? 1 : 0;
    }
    
    @Override
    public ChunkSet emptyChunkSetCell() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public ChunkSet emptyChunkSetVertex() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public ChunkSet emptyChunkSetEdge() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public int numChunksWhoVertex() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public int numChunksWhoCell() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public int numChunksWhoEdge() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public int chunkSizeWhoVertex() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public int chunkSizeWhoCell() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public int chunkSizeWhoEdge() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public int numChunksWhatVertex() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public int numChunksWhatCell() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public int numChunksWhatEdge() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public int chunkSizeWhatVertex() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public int chunkSizeWhatCell() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public int chunkSizeWhatEdge() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean matchesWhoVertex(final ChunkSet mask, final ChunkSet pattern) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean matchesWhoCell(final ChunkSet mask, final ChunkSet pattern) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean matchesWhoEdge(final ChunkSet mask, final ChunkSet pattern) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean matchesWhatVertex(final ChunkSet mask, final ChunkSet pattern) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean matchesWhatCell(final ChunkSet mask, final ChunkSet pattern) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean matchesWhatEdge(final ChunkSet mask, final ChunkSet pattern) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean violatesNotWhoVertex(final ChunkSet mask, final ChunkSet pattern) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean violatesNotWhoCell(final ChunkSet mask, final ChunkSet pattern) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean violatesNotWhoEdge(final ChunkSet mask, final ChunkSet pattern) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean violatesNotWhatVertex(final ChunkSet mask, final ChunkSet pattern) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean violatesNotWhatCell(final ChunkSet mask, final ChunkSet pattern) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean violatesNotWhatEdge(final ChunkSet mask, final ChunkSet pattern) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean violatesNotWhoVertex(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean violatesNotWhoCell(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean violatesNotWhoEdge(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean violatesNotWhatVertex(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean violatesNotWhatCell(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public boolean violatesNotWhatEdge(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public ChunkSet cloneWhoVertex() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public ChunkSet cloneWhoCell() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public ChunkSet cloneWhoEdge() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public ChunkSet cloneWhatVertex() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public ChunkSet cloneWhatCell() {
        throw new UnsupportedOperationException("TODO");
    }
    
    @Override
    public ChunkSet cloneWhatEdge() {
        throw new UnsupportedOperationException("TODO");
    }
}
