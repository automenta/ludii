// 
// Decompiled by Procyon v0.5.36
// 

package util.state.containerStackingState;

import game.Game;
import game.equipment.container.Container;
import game.types.board.SiteType;
import gnu.trove.list.array.TIntArrayList;
import main.collections.ChunkSet;
import main.collections.ListStack;
import util.state.State;
import util.zhash.ZobristHashGenerator;

public class ContainerStateCards extends BaseContainerStateStacking
{
    private static final long serialVersionUID = 1L;
    private ListStack[] listStacks;
    private final int type;
    
    public ContainerStateCards(final ZobristHashGenerator generator, final Game game, final Container container, final int type) {
        super(game, container, container.numSites());
        this.listStacks = new ListStack[container.numSites()];
        for (int i = 0; i < this.listStacks.length; ++i) {
            this.listStacks[i] = new ListStack(type);
        }
        this.type = type;
    }
    
    public ContainerStateCards(final ContainerStateCards other) {
        super(other);
        if (other.listStacks == null) {
            this.listStacks = null;
        }
        else {
            this.listStacks = new ListStack[other.listStacks.length];
            for (int i = 0; i < this.listStacks.length; ++i) {
                this.listStacks[i] = new ListStack(other.listStacks[i]);
            }
        }
        this.type = other.type;
    }
    
    @Override
    public ContainerStateCards deepClone() {
        return new ContainerStateCards(this);
    }
    
    @Override
    protected long calcCanonicalHash(final int[] siteRemap, final int[] edgeRemap, final int[] vertexRemap, final int[] playerRemap, final boolean whoOnly) {
        return 0L;
    }
    
    @Override
    public void reset(final State trialState, final Game game) {
        super.reset(trialState, game);
        this.listStacks = new ListStack[game.equipment().totalDefaultSites()];
        for (int i = 0; i < this.listStacks.length; ++i) {
            this.listStacks[i] = new ListStack(this.type);
        }
    }
    
    @Override
    public void addItem(final State trialState, final int site, final int what, final int who, final Game game) {
        this.listStacks[site - this.offset].incrementSize();
        this.listStacks[site - this.offset].setWhat(what);
        this.listStacks[site - this.offset].setWho(who);
        final TIntArrayList hidden = new TIntArrayList();
        hidden.add(0);
        for (int i = 0; i < game.players().count(); ++i) {
            hidden.add(2);
        }
        this.listStacks[site - this.offset].addHidden(hidden);
    }
    
    @Override
    public void addItem(final State trialState, final int site, final int what, final int who, final Game game, final boolean[] hidden, final boolean masked) {
        this.listStacks[site - this.offset].incrementSize();
        this.listStacks[site - this.offset].setWhat(what);
        this.listStacks[site - this.offset].setWho(who);
        final TIntArrayList hiddenList = new TIntArrayList();
        hiddenList.add(0);
        for (int i = 0; i < hidden.length; ++i) {
            if (hidden[i]) {
                if (masked) {
                    hiddenList.add(1);
                }
                else {
                    hiddenList.add(2);
                }
            }
            else {
                hiddenList.add(0);
            }
        }
        this.listStacks[site - this.offset].addHidden(hiddenList);
    }
    
    @Override
    public void insert(final State trialState, final int site, final int level, final int what, final int who, final Game game) {
        if (level < this.listStacks[site - this.offset].size()) {
            this.listStacks[site - this.offset].insertWhat(what, level);
            this.listStacks[site - this.offset].insertWho(who, level);
            final TIntArrayList hidden = new TIntArrayList();
            hidden.add(0);
            for (int i = 0; i < game.players().count(); ++i) {
                hidden.add(2);
            }
            this.listStacks[site - this.offset].insertHidden(hidden, level);
            this.listStacks[site - this.offset].incrementSize();
        }
    }
    
    @Override
    public void addItem(final State trialState, final int site, final int what, final int who, final int stateVal, final int rotationVal, final Game game) {
        this.listStacks[site - this.offset].incrementSize();
        this.listStacks[site - this.offset].setWhat(what);
        this.listStacks[site - this.offset].setWho(who);
        this.listStacks[site - this.offset].setState(stateVal);
        this.listStacks[site - this.offset].setRotation(rotationVal);
        final TIntArrayList hidden = new TIntArrayList();
        hidden.add(null);
        for (int i = 0; i < game.players().count(); ++i) {
            hidden.add(2);
        }
        this.listStacks[site - this.offset].addHidden(hidden);
    }
    
    @Override
    public void setSite(final State trialState, final int site, final int whoVal, final int whatVal, final int countVal, final int stateVal, final int rotationVal, final int valueVal, final SiteType type) {
        final boolean wasEmpty = this.isEmpty(site, type);
        if (whoVal != -1) {
            this.listStacks[site - this.offset].setWho(whoVal);
        }
        if (whatVal != -1) {
            this.listStacks[site - this.offset].setWhat(whatVal);
        }
        if (stateVal != -1) {
            this.listStacks[site - this.offset].setState(stateVal);
        }
        if (rotationVal != -1) {
            this.listStacks[site - this.offset].setRotation(rotationVal);
        }
        final boolean isEmpty = this.isEmpty(site, type);
        if (wasEmpty == isEmpty) {
            return;
        }
        if (isEmpty) {
            this.addToEmpty(site);
        }
        else {
            this.removeFromEmpty(site - this.offset);
        }
    }
    
    @Override
    public void setSite(final State trialState, final int site, final int level, final int whoVal, final int whatVal, final int countVal, final int stateVal, final int rotationVal, final int valueVal) {
        final boolean wasEmpty = this.isEmpty(site, SiteType.Cell);
        if (whoVal != -1) {
            this.listStacks[site - this.offset].setWho(whoVal, level);
        }
        if (whatVal != -1) {
            this.listStacks[site - this.offset].setWhat(whatVal, level);
        }
        if (stateVal != -1) {
            this.listStacks[site - this.offset].setState(stateVal, level);
        }
        if (rotationVal != -1) {
            this.listStacks[site - this.offset].setRotation(rotationVal, level);
        }
        final boolean isEmpty = this.isEmpty(site, SiteType.Cell);
        if (wasEmpty == isEmpty) {
            return;
        }
        if (isEmpty) {
            this.addToEmpty(site);
        }
        else {
            this.removeFromEmpty(site - this.offset);
        }
    }
    
    @Override
    public boolean isOccupied(final int site) {
        return this.listStacks[site - this.offset] != null && this.listStacks[site - this.offset].what() != 0;
    }
    
    @Override
    public int whoCell(final int site) {
        if (this.listStacks[site - this.offset] == null) {
            return 0;
        }
        return this.listStacks[site - this.offset].who();
    }
    
    @Override
    public int whoCell(final int site, final int level) {
        if (this.listStacks[site - this.offset] == null) {
            return 0;
        }
        return this.listStacks[site - this.offset].who(level);
    }
    
    @Override
    public int whatCell(final int site) {
        if (this.listStacks[site - this.offset] == null) {
            return 0;
        }
        return this.listStacks[site - this.offset].what();
    }
    
    @Override
    public int stateCell(final int site) {
        if (this.listStacks[site - this.offset] == null) {
            return 0;
        }
        return this.listStacks[site - this.offset].state();
    }
    
    @Override
    public int rotationCell(final int site) {
        if (this.listStacks[site - this.offset] == null) {
            return 0;
        }
        return this.listStacks[site - this.offset].rotation();
    }
    
    @Override
    public boolean isInvisibleCell(final int site, final int who) {
        return this.listStacks[site - this.offset] != null && this.listStacks[site - this.offset].isInvisible(who);
    }
    
    @Override
    public boolean isMaskedCell(final int site, final int who) {
        return this.listStacks[site - this.offset] != null && this.listStacks[site - this.offset].isMasked(who);
    }
    
    @Override
    public boolean isVisibleCell(final int site, final int who) {
        return this.listStacks[site - this.offset] != null && !this.listStacks[site - this.offset].isVisible(who);
    }
    
    @Override
    public int whatCell(final int site, final int level) {
        if (this.listStacks[site - this.offset] == null) {
            return 0;
        }
        return this.listStacks[site - this.offset].what(level);
    }
    
    @Override
    public int stateCell(final int site, final int level) {
        if (this.listStacks[site - this.offset] == null) {
            return 0;
        }
        return this.listStacks[site - this.offset].state(level);
    }
    
    @Override
    public int rotationCell(final int site, final int level) {
        if (this.listStacks[site - this.offset] == null) {
            return 0;
        }
        return this.listStacks[site - this.offset].rotation(level);
    }
    
    @Override
    public boolean isInvisibleCell(final int site, final int level, final int who) {
        return this.listStacks[site - this.offset] != null && this.listStacks[site - this.offset].isInvisible(level, who);
    }
    
    @Override
    public boolean isVisibleCell(final int site, final int level, final int who) {
        return this.listStacks[site - this.offset] != null && this.listStacks[site - this.offset].isVisible(level, who);
    }
    
    @Override
    public boolean isMaskedCell(final int site, final int level, final int who) {
        return this.listStacks[site - this.offset] != null && this.listStacks[site - this.offset].isMasked(level, who);
    }
    
    @Override
    public void setInvisibleCell(final State trialState, final int site, final int who) {
        if (this.listStacks[site - this.offset] == null) {
            return;
        }
        this.listStacks[site - this.offset].setInvisible(who);
    }
    
    @Override
    public void setVisibleCell(final State state, final int site, final int who) {
        if (this.listStacks[site - this.offset] == null) {
            return;
        }
        this.listStacks[site - this.offset].setVisible(who);
    }
    
    @Override
    public void setMaskedCell(final State state, final int site, final int who) {
        if (this.listStacks[site - this.offset] == null) {
            return;
        }
        this.listStacks[site - this.offset].setMasked(who);
    }
    
    @Override
    public void setInvisibleCell(final State trialState, final int site, final int level, final int who) {
        if (this.listStacks[site - this.offset] == null) {
            return;
        }
        this.listStacks[site - this.offset].setInvisible(level, who);
    }
    
    @Override
    public void setVisibleCell(final State trialState, final int site, final int level, final int who) {
        if (this.listStacks[site - this.offset] == null) {
            return;
        }
        this.listStacks[site - this.offset].setVisible(level, who);
    }
    
    @Override
    public void setMaskedCell(final State trialState, final int site, final int level, final int who) {
        if (this.listStacks[site - this.offset] == null) {
            return;
        }
        this.listStacks[site - this.offset].setMasked(level, who);
    }
    
    @Override
    public int remove(final State state, final int site, final SiteType graphElement) {
        if (this.listStacks[site - this.offset] == null) {
            return 0;
        }
        final int componentRemove = this.listStacks[site - this.offset].what();
        this.listStacks[site - this.offset].decrementSize();
        return componentRemove;
    }
    
    @Override
    public int remove(final State state, final int site, final int level, final SiteType graphElement) {
        if (this.listStacks[site - this.offset] == null) {
            return 0;
        }
        final int componentRemove = this.listStacks[site - this.offset].what(level);
        for (int i = level; i < this.sizeStackCell(site) - 1; ++i) {
            this.listStacks[site - this.offset].setWhat(this.listStacks[site - this.offset].what(i + 1), i);
            this.listStacks[site - this.offset].setWho(this.listStacks[site - this.offset].who(i + 1), i);
            this.listStacks[site - this.offset].setState(this.listStacks[site - this.offset].state(i + 1), i);
            this.listStacks[site - this.offset].setRotation(this.listStacks[site - this.offset].rotation(i + 1), i);
            this.listStacks[site - this.offset].setHidden(this.listStacks[site - this.offset].hiddenArray(i + 1), i);
        }
        this.listStacks[site - this.offset].setWhat(0);
        this.listStacks[site - this.offset].setWho(0);
        this.listStacks[site - this.offset].addHidden(null);
        this.listStacks[site - this.offset].decrementSize();
        return componentRemove;
    }
    
    @Override
    public int remove(final State state, final int site, final int level) {
        if (this.listStacks[site - this.offset] == null) {
            return 0;
        }
        final int componentRemove = this.listStacks[site - this.offset].what(level);
        for (int i = level; i < this.sizeStackCell(site) - 1; ++i) {
            this.listStacks[site - this.offset].setWhat(this.listStacks[site - this.offset].what(i + 1), i);
            this.listStacks[site - this.offset].setWho(this.listStacks[site - this.offset].who(i + 1), i);
            this.listStacks[site - this.offset].setState(this.listStacks[site - this.offset].state(i + 1), i);
            this.listStacks[site - this.offset].setRotation(this.listStacks[site - this.offset].rotation(i + 1), i);
            this.listStacks[site - this.offset].setHidden(this.listStacks[site - this.offset].hiddenArray(i + 1), i);
        }
        this.listStacks[site - this.offset].setWhat(0);
        this.listStacks[site - this.offset].setWho(0);
        this.listStacks[site - this.offset].addHidden(null);
        this.listStacks[site - this.offset].decrementSize();
        return componentRemove;
    }
    
    @Override
    public void removeStack(final State state, final int site) {
        if (this.listStacks[site - this.offset] == null) {
            return;
        }
        for (int previousSize = this.listStacks[site - this.offset].size(), i = 0; i < previousSize; ++i) {
            this.listStacks[site - this.offset].decrementSize();
        }
    }
    
    @Override
    public int countCell(final int site) {
        if (this.listStacks[site - this.offset] == null) {
            return 0;
        }
        return 1;
    }
    
    @Override
    public int sizeStackCell(final int site) {
        if (this.listStacks[site - this.offset] == null) {
            return 0;
        }
        return this.listStacks[site - this.offset].size();
    }
    
    @Override
    public boolean isPlayable(final int site) {
        return this.listStacks[site - this.offset] != null;
    }
    
    @Override
    public int whoEdge(final int edge) {
        return 0;
    }
    
    @Override
    public int whoVertex(final int vertex) {
        return 0;
    }
    
    @Override
    public int whatEdge(final int site) {
        return 0;
    }
    
    @Override
    public int countEdge(final int site) {
        return 0;
    }
    
    @Override
    public int stateEdge(final int site) {
        return 0;
    }
    
    @Override
    public int rotationEdge(final int site) {
        return 0;
    }
    
    @Override
    public boolean isInvisibleEdge(final int site, final int who) {
        return false;
    }
    
    @Override
    public boolean isVisibleEdge(final int site, final int who) {
        return false;
    }
    
    @Override
    public boolean isMaskedEdge(final int site, final int who) {
        return false;
    }
    
    @Override
    public void setInvisibleEdge(final State state, final int site, final int who) {
    }
    
    @Override
    public void setVisibleEdge(final State state, final int site, final int who) {
    }
    
    @Override
    public void setMaskedEdge(final State state, final int site, final int who) {
    }
    
    @Override
    public int whatVertex(final int site) {
        return 0;
    }
    
    @Override
    public int countVertex(final int site) {
        return 0;
    }
    
    @Override
    public int stateVertex(final int site) {
        return 0;
    }
    
    @Override
    public int rotationVertex(final int site) {
        return 0;
    }
    
    @Override
    public boolean isInvisibleVertex(final int site, final int who) {
        return false;
    }
    
    @Override
    public boolean isVisibleVertex(final int site, final int who) {
        return true;
    }
    
    @Override
    public boolean isMaskedVertex(final int site, final int who) {
        return false;
    }
    
    @Override
    public void setInvisibleVertex(final State state, final int site, final int who) {
    }
    
    @Override
    public void setVisibleVertex(final State state, final int site, final int who) {
    }
    
    @Override
    public void setMaskedVertex(final State state, final int site, final int who) {
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
    public int value(final int site) {
        return 0;
    }
    
    @Override
    public void setValue(final State trialState, final int site, final int valueVal) {
    }
    
    @Override
    public void setCount(final State trialState, final int site, final int countVal) {
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
    
    @Override
    public int whoVertex(final int site, final int level) {
        return 0;
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
    public boolean isInvisibleVertex(final int site, final int level, final int who) {
        return false;
    }
    
    @Override
    public boolean isVisibleVertex(final int site, final int level, final int who) {
        return false;
    }
    
    @Override
    public boolean isMaskedVertex(final int site, final int level, final int who) {
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
    public int whoEdge(final int site, final int level) {
        return 0;
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
    public boolean isInvisibleEdge(final int site, final int level, final int who) {
        return false;
    }
    
    @Override
    public boolean isVisibleEdge(final int site, final int level, final int who) {
        return false;
    }
    
    @Override
    public boolean isMaskedEdge(final int site, final int level, final int who) {
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
    public int sizeStackVertex(final int site) {
        return 0;
    }
    
    @Override
    public int sizeStackEdge(final int site) {
        return 0;
    }
    
    @Override
    public void addToEmpty(final int site, final SiteType graphType) {
        this.addToEmpty(site);
    }
    
    @Override
    public void removeFromEmpty(final int site, final SiteType graphType) {
        this.removeFromEmpty(site);
    }
}
