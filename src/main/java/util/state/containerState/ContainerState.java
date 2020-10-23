// 
// Decompiled by Procyon v0.5.36
// 

package util.state.containerState;

import collections.ChunkSet;
import game.Game;
import game.equipment.container.Container;
import game.types.board.SiteType;
import game.util.directions.AbsoluteDirection;
import game.util.equipment.Region;
import util.Sites;
import util.UnionInfoD;
import util.state.State;
import util.symmetry.SymmetryValidator;

import java.io.Serializable;
import java.util.BitSet;

public interface ContainerState extends Serializable
{
    void reset(final State trialState, final Game game);
    
    int remove(final State state, final int site, final SiteType type);
    
    int remove(final State state, final int site, final int level, final SiteType type);
    
    UnionInfoD[] unionInfoAdjacent();
    
    UnionInfoD[] unionInfoBlockingAdjacent();
    
    UnionInfoD[] unionInfoOrthogonal();
    
    UnionInfoD[] unionInfoBlockingOrthogonal();
    
    UnionInfoD[] unionInfo(final AbsoluteDirection dir);
    
    UnionInfoD[] unionInfoBlocking(final AbsoluteDirection dir);
    
    ContainerState deepClone();
    
    long canonicalHash(final SymmetryValidator validator, final State state, final boolean whoOnly);
    
    Sites emptySites();
    
    int numEmpty();
    
    boolean isEmpty(final int site, final SiteType type);
    
    boolean isEmptyCell(final int cell);
    
    boolean isEmptyEdge(final int edge);
    
    boolean isEmptyVertex(final int vertex);
    
    Region emptyRegion(final SiteType type);
    
    void addToEmpty(final int site);
    
    void addToEmpty(final int site, final SiteType type);
    
    void removeFromEmpty(final int site, final SiteType type);
    
    void removeFromEmpty(final int site);
    
    Container container();
    
    void setContainer(final Container cont);
    
    String nameFromFile();
    
    void setPlayable(final State trialState, final int site, final boolean on);
    
    void setSite(final State trialState, final int site, final int who, final int what, final int count, final int state, final int rotation, final int value, final SiteType type);
    
    int whoCell(final int site);
    
    int whatCell(final int site);
    
    int countCell(final int site);
    
    int stateCell(final int site);
    
    int rotationCell(final int site);
    
    int value(final int site);
    
    boolean isInvisibleCell(final int site, final int who);
    
    boolean isMaskedCell(final int site, final int who);
    
    boolean isVisibleCell(final int site, final int who);
    
    void setInvisibleCell(final State state, final int site, final int who);
    
    void setVisibleCell(final State state, final int site, final int who);
    
    void setMaskedCell(final State state, final int site, final int who);
    
    boolean isPlayable(final int site);
    
    boolean isOccupied(final int site);
    
    int sizeStackCell(final int site);
    
    int whatEdge(final int site);
    
    int whoEdge(final int site);
    
    int countEdge(final int site);
    
    int stateEdge(final int site);
    
    int rotationEdge(final int site);
    
    boolean isInvisibleEdge(final int site, final int who);
    
    boolean isVisibleEdge(final int site, final int who);
    
    boolean isMaskedEdge(final int site, final int who);
    
    void setInvisibleEdge(final State state, final int site, final int who);
    
    void setVisibleEdge(final State state, final int site, final int who);
    
    void setMaskedEdge(final State state, final int site, final int who);
    
    int sizeStackEdge(final int site);
    
    int whatVertex(final int site);
    
    int whoVertex(final int site);
    
    int countVertex(final int site);
    
    int stateVertex(final int site);
    
    int rotationVertex(final int site);
    
    int sizeStackVertex(final int site);
    
    boolean isInvisibleVertex(final int site, final int who);
    
    boolean isVisibleVertex(final int site, final int who);
    
    boolean isMaskedVertex(final int site, final int who);
    
    void setInvisibleVertex(final State state, final int site, final int who);
    
    void setVisibleVertex(final State state, final int site, final int who);
    
    void setMaskedVertex(final State state, final int site, final int who);
    
    int what(final int site, final SiteType graphElementType);
    
    int who(final int site, final SiteType graphElementType);
    
    int count(final int site, final SiteType graphElementType);
    
    int sizeStack(final int site, final SiteType graphElementType);
    
    boolean isInvisible(final int site, final int who, final SiteType graphElementType);
    
    boolean isVisible(final int site, final int who, final SiteType graphElementType);
    
    boolean isMasked(final int site, final int who, final SiteType graphElementType);
    
    int state(final int site, final SiteType graphElementType);
    
    int rotation(final int site, final SiteType graphElementType);
    
    int what(final int site, final int level, final SiteType graphElementType);
    
    int who(final int site, final int level, final SiteType graphElementType);
    
    boolean isInvisible(final int site, final int level, final int who, final SiteType graphElementType);
    
    boolean isVisible(final int site, final int level, final int who, final SiteType graphElementType);
    
    boolean isMasked(final int site, final int level, final int who, final SiteType graphElementType);
    
    int state(final int site, final int level, final SiteType graphElementType);
    
    int rotation(final int site, final int level, final SiteType graphElementType);
    
    void setValue(final State state, final int site, final int value);
    
    void addItemGeneric(final State trialState, final int site, final int what, final int who, final Game game, final SiteType graphElementType);
    
    void addItemGeneric(final State trialState, final int site, final int what, final int who, final int stateVal, final int rotationVal, final Game game, final SiteType graphElementType);
    
    void addItemGeneric(final State trialState, final int site, final int what, final int who, final Game game, final boolean[] hidden, final boolean masked, final SiteType graphElementType);
    
    void removeStackGeneric(final State state, final int site, final SiteType graphElementType);
    
    void setCount(final State state, final int site, final int count);
    
    void addItem(final State trialState, final int site, final int what, final int who, final Game game);
    
    void insert(final State trialState, final int site, final int level, final int what, final int who, final Game game);
    
    void addItem(final State trialState, final int site, final int what, final int who, final int stateVal, final int rotationVal, final Game game);
    
    void addItem(final State trialState, final int site, final int what, final int who, final Game game, final boolean[] hidden, final boolean masked);
    
    void removeStack(final State state, final int site);
    
    int whoCell(final int site, final int level);
    
    int whatCell(final int site, final int level);
    
    int stateCell(final int site, final int level);
    
    int rotationCell(final int site, final int level);
    
    int remove(final State state, final int site, final int level);
    
    void setSite(final State trialState, final int site, final int level, final int whoVal, final int whatVal, final int countVal, final int stateVal, final int rotationVal, final int valueVal);
    
    void addItemVertex(final State trialState, final int site, final int what, final int who, final Game game);
    
    void insertVertex(final State trialState, final int site, final int level, final int what, final int who, final Game game);
    
    void addItemVertex(final State trialState, final int site, final int what, final int who, final int stateVal, final int rotationVal, final Game game);
    
    void addItemVertex(final State trialState, final int site, final int what, final int who, final Game game, final boolean[] hidden, final boolean masked);
    
    void removeStackVertex(final State state, final int site);
    
    int whoVertex(final int site, final int level);
    
    int whatVertex(final int site, final int level);
    
    int stateVertex(final int site, final int level);
    
    int rotationVertex(final int site, final int level);
    
    void addItemEdge(final State trialState, final int site, final int what, final int who, final Game game);
    
    void insertEdge(final State trialState, final int site, final int level, final int what, final int who, final Game game);
    
    void addItemEdge(final State trialState, final int site, final int what, final int who, final int stateVal, final int rotationVal, final Game game);
    
    void addItemEdge(final State trialState, final int site, final int what, final int who, final Game game, final boolean[] hidden, final boolean masked);
    
    void removeStackEdge(final State state, final int site);
    
    int whoEdge(final int site, final int level);
    
    int whatEdge(final int site, final int level);
    
    int stateEdge(final int site, final int level);
    
    int rotationEdge(final int site, final int level);
    
    void setVisible(final State trialState, final int site, final int who, final SiteType graphElementType);
    
    void setInvisible(final State trialState, final int site, final int who, final SiteType graphElementType);
    
    void setMasked(final State trialState, final int site, final int who, final SiteType graphElementType);
    
    void setInvisibleCell(final State trialState, final int site, final int level, final int who);
    
    void setMaskedCell(final State trialState, final int site, final int level, final int who);
    
    void setVisibleCell(final State trialState, final int site, final int level, final int who);
    
    void setInvisibleVertex(final State trialState, final int site, final int level, final int who);
    
    void setMaskedVertex(final State trialState, final int site, final int level, final int who);
    
    void setVisibleVertex(final State trialState, final int site, final int level, final int who);
    
    void setInvisibleEdge(final State trialState, final int site, final int level, final int who);
    
    void setMaskedEdge(final State trialState, final int site, final int level, final int who);
    
    void setVisibleEdge(final State trialState, final int site, final int level, final int who);
    
    boolean isInvisibleCell(final int site, final int level, final int who);
    
    boolean isVisibleCell(final int site, final int level, final int who);
    
    boolean isMaskedCell(final int site, final int level, final int who);
    
    boolean isInvisibleVertex(final int site, final int level, final int who);
    
    boolean isVisibleVertex(final int site, final int level, final int who);
    
    boolean isMaskedVertex(final int site, final int level, final int who);
    
    boolean isInvisibleEdge(final int site, final int level, final int who);
    
    boolean isVisibleEdge(final int site, final int level, final int who);
    
    boolean isMaskedEdge(final int site, final int level, final int who);
    
    boolean bit(final int var, final int value, final SiteType type);
    
    boolean isResolved(final int var, final SiteType type);
    
    boolean isResolvedEdges(final int var);
    
    boolean isResolvedCell(final int var);
    
    boolean isResolvedVerts(final int var);
    
    void set(final int var, final int value, final SiteType type);
    
    BitSet values(final SiteType type, final int var);
    
    ChunkSet emptyChunkSetVertex();
    
    ChunkSet emptyChunkSetCell();
    
    ChunkSet emptyChunkSetEdge();
    
    int numChunksWhoVertex();
    
    int numChunksWhoCell();
    
    int numChunksWhoEdge();
    
    int chunkSizeWhoVertex();
    
    int chunkSizeWhoCell();
    
    int chunkSizeWhoEdge();
    
    int numChunksWhatVertex();
    
    int numChunksWhatCell();
    
    int numChunksWhatEdge();
    
    int chunkSizeWhatVertex();
    
    int chunkSizeWhatCell();
    
    int chunkSizeWhatEdge();
    
    boolean matchesWhoVertex(final ChunkSet mask, final ChunkSet pattern);
    
    boolean matchesWhoCell(final ChunkSet mask, final ChunkSet pattern);
    
    boolean matchesWhoEdge(final ChunkSet mask, final ChunkSet pattern);
    
    boolean matchesWhatVertex(final ChunkSet mask, final ChunkSet pattern);
    
    boolean matchesWhatCell(final ChunkSet mask, final ChunkSet pattern);
    
    boolean matchesWhatEdge(final ChunkSet mask, final ChunkSet pattern);
    
    boolean violatesNotWhoVertex(final ChunkSet mask, final ChunkSet pattern);
    
    boolean violatesNotWhoCell(final ChunkSet mask, final ChunkSet pattern);
    
    boolean violatesNotWhoEdge(final ChunkSet mask, final ChunkSet pattern);
    
    boolean violatesNotWhatVertex(final ChunkSet mask, final ChunkSet pattern);
    
    boolean violatesNotWhatCell(final ChunkSet mask, final ChunkSet pattern);
    
    boolean violatesNotWhatEdge(final ChunkSet mask, final ChunkSet pattern);
    
    boolean violatesNotWhoVertex(final ChunkSet mask, final ChunkSet pattern, final int startWord);
    
    boolean violatesNotWhoCell(final ChunkSet mask, final ChunkSet pattern, final int startWord);
    
    boolean violatesNotWhoEdge(final ChunkSet mask, final ChunkSet pattern, final int startWord);
    
    boolean violatesNotWhatVertex(final ChunkSet mask, final ChunkSet pattern, final int startWord);
    
    boolean violatesNotWhatCell(final ChunkSet mask, final ChunkSet pattern, final int startWord);
    
    boolean violatesNotWhatEdge(final ChunkSet mask, final ChunkSet pattern, final int startWord);
    
    ChunkSet cloneWhoVertex();
    
    ChunkSet cloneWhoCell();
    
    ChunkSet cloneWhoEdge();
    
    ChunkSet cloneWhatVertex();
    
    ChunkSet cloneWhatCell();
    
    ChunkSet cloneWhatEdge();
}
