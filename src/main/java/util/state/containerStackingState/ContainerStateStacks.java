// 
// Decompiled by Procyon v0.5.36
// 

package util.state.containerStackingState;

import game.Game;
import game.equipment.container.Container;
import game.types.board.SiteType;
import collections.ChunkSet;
import topology.Cell;
import util.state.State;
import util.zhash.*;

import java.util.Arrays;

public class ContainerStateStacks extends BaseContainerStateStacking
{
    private static final long serialVersionUID = 1L;
    private final HashedChunkStack[] chunkStacks;
    private final HashedChunkSet[] hidden;
    private final HashedBitSet playable;
    private final long[][][] chunkStacksWhatHash;
    private final long[][][] chunkStacksWhoHash;
    private final long[][][] chunkStacksStateHash;
    private final long[][][] chunkStacksRotationHash;
    private final long[][] chunkStacksSizeHash;
    protected final int type;
    public final int numComponents;
    public final int numPlayers;
    public final int numStates;
    public final int numRotation;
    private final boolean hiddenInfo;
    
    public ContainerStateStacks(final ZobristHashGenerator generator, final Game game, final Container container, final int type) {
        super(game, container, container.numSites());
        final int numSites = container.topology().cells().size();
        this.chunkStacks = new HashedChunkStack[numSites];
        this.numComponents = game.numComponents();
        this.numPlayers = game.players().count();
        this.numStates = game.maximalLocalStates();
        this.numRotation = game.maximalRotationStates();
        final int maxValWhat = this.numComponents;
        final int maxValWho = this.numPlayers + 1;
        final int maxValState = this.numStates;
        final int maxValRotation = this.numRotation;
        this.chunkStacksWhatHash = ZobristHashUtilities.getSequence(generator, numSites, 32, maxValWhat + 1);
        this.chunkStacksWhoHash = ZobristHashUtilities.getSequence(generator, numSites, 32, maxValWho + 1);
        this.chunkStacksStateHash = ZobristHashUtilities.getSequence(generator, numSites, 32, maxValState + 1);
        this.chunkStacksRotationHash = ZobristHashUtilities.getSequence(generator, numSites, 32, maxValRotation + 1);
        this.chunkStacksSizeHash = ZobristHashUtilities.getSequence(generator, numSites, 32);
        this.type = type;
        if ((game.gameFlags() & 0x8L) == 0x0L) {
            this.hidden = null;
            this.hiddenInfo = false;
        }
        else {
            this.hidden = new HashedChunkSet[numSites];
            this.hiddenInfo = true;
        }
        if (game.isBoardless() && container.index() == 0) {
            this.playable = new HashedBitSet(generator, numSites);
        }
        else {
            this.playable = null;
        }
    }
    
    public ContainerStateStacks(final ContainerStateStacks other) {
        super(other);
        this.numComponents = other.numComponents;
        this.numPlayers = other.numPlayers;
        this.numStates = other.numStates;
        this.numRotation = other.numRotation;
        this.playable = ((other.playable == null) ? null : other.playable.clone());
        if (other.hidden == null) {
            this.hidden = null;
        }
        else {
            this.hidden = new HashedChunkSet[other.hidden.length];
            for (int i = 0; i < this.hidden.length; ++i) {
                if (other.hidden[i] != null) {
                    this.hidden[i] = other.hidden[i].clone();
                }
            }
        }
        if (other.chunkStacks == null) {
            this.chunkStacks = null;
        }
        else {
            this.chunkStacks = new HashedChunkStack[other.chunkStacks.length];
            for (int i = 0; i < other.chunkStacks.length; ++i) {
                final HashedChunkStack otherChunkStack = other.chunkStacks[i];
                if (otherChunkStack != null) {
                    this.chunkStacks[i] = otherChunkStack.clone();
                }
            }
        }
        this.chunkStacksWhatHash = other.chunkStacksWhatHash;
        this.chunkStacksWhoHash = other.chunkStacksWhoHash;
        this.chunkStacksStateHash = other.chunkStacksStateHash;
        this.chunkStacksRotationHash = other.chunkStacksRotationHash;
        this.chunkStacksSizeHash = other.chunkStacksSizeHash;
        this.type = other.type;
        this.hiddenInfo = other.hiddenInfo;
    }
    
    @Override
    protected long calcCanonicalHash(final int[] siteRemap, final int[] edgeRemap, final int[] vertexRemap, final int[] playerRemap, final boolean whoOnly) {
        long hash = 0L;
        if (this.offset != 0) {
            return 0L;
        }
        for (int pos = 0; pos < this.chunkStacks.length && pos < siteRemap.length; ++pos) {
            final int newPos = siteRemap[pos];
            if (this.chunkStacks[pos] != null) {
                hash ^= this.chunkStacks[pos].remapHashTo(this.chunkStacksWhatHash[newPos], this.chunkStacksWhoHash[newPos], this.chunkStacksStateHash[newPos], this.chunkStacksRotationHash[newPos], this.chunkStacksSizeHash[newPos], whoOnly);
            }
        }
        return hash;
    }
    
    @Override
    public void reset(final State trialState, final Game game) {
        super.reset(trialState, game);
        for (final HashedChunkStack set : this.chunkStacks) {
            if (set != null) {
                trialState.updateStateHash(set.calcHash());
            }
        }
        Arrays.fill(this.chunkStacks, null);
        if (this.hidden != null) {
            for (final HashedChunkSet set2 : this.hidden) {
                if (set2 != null) {
                    set2.clear(trialState);
                }
            }
        }
    }
    
    private void verifyPresent(final int site) {
        if (this.chunkStacks[site - this.offset] != null) {
            return;
        }
        this.chunkStacks[site - this.offset] = new HashedChunkStack(this.numComponents, this.numPlayers, this.numStates, this.numRotation, this.type, this.hiddenInfo, this.chunkStacksWhatHash[site - this.offset], this.chunkStacksWhoHash[site - this.offset], this.chunkStacksStateHash[site - this.offset], this.chunkStacksRotationHash[site - this.offset], this.chunkStacksSizeHash[site - this.offset]);
    }
    
    @Override
    public void addItem(final State trialState, final int site, final int what, final int who, final Game game) {
        this.verifyPresent(site);
        this.chunkStacks[site - this.offset].incrementSize(trialState);
        this.chunkStacks[site - this.offset].setWhat(trialState, what);
        this.chunkStacks[site - this.offset].setWho(trialState, who);
        if (this.playable != null) {
            this.setPlayable(trialState, site - this.offset, false);
            final Cell cell = this.container().topology().cells().get(site);
            for (final Cell vNbors : cell.adjacent()) {
                if (!this.isOccupied(vNbors.index())) {
                    this.setPlayable(trialState, vNbors.index(), true);
                }
            }
        }
    }
    
    @Override
    public void addItem(final State trialState, final int site, final int what, final int who, final Game game, final boolean[] hide, final boolean masked) {
        this.verifyPresent(site);
        this.chunkStacks[site - this.offset].incrementSize(trialState);
        this.chunkStacks[site - this.offset].setWhat(trialState, what);
        this.chunkStacks[site - this.offset].setWho(trialState, who);
        if (hide != null) {
            for (int i = 0; i < hide.length; ++i) {
                if (hide[i]) {
                    if (masked) {
                        this.setMaskedCell(trialState, site, i);
                    }
                    else {
                        this.setInvisibleCell(trialState, site, i);
                    }
                }
                else {
                    this.setVisibleCell(trialState, site, i);
                }
            }
        }
        if (this.playable != null) {
            this.setPlayable(trialState, site, false);
            final Cell cell = this.container().topology().cells().get(site);
            for (final Cell vNbors : cell.adjacent()) {
                if (!this.isOccupied(vNbors.index())) {
                    this.setPlayable(trialState, vNbors.index(), true);
                }
            }
        }
    }
    
    @Override
    public void addItem(final State trialState, final int site, final int what, final int who, final int stateVal, final int rotationVal, final Game game) {
        this.verifyPresent(site);
        this.chunkStacks[site - this.offset].incrementSize(trialState);
        this.chunkStacks[site - this.offset].setWhat(trialState, what);
        this.chunkStacks[site - this.offset].setWho(trialState, who);
        this.chunkStacks[site - this.offset].setState(trialState, stateVal);
        this.chunkStacks[site - this.offset].setRotation(trialState, rotationVal);
        if (this.playable != null) {
            this.setPlayable(trialState, site, false);
            final Cell cell = this.container().topology().cells().get(site);
            for (final Cell vNbors : cell.adjacent()) {
                if (!this.isOccupied(vNbors.index())) {
                    this.setPlayable(trialState, vNbors.index(), true);
                }
            }
        }
    }
    
    @Override
    public void insert(final State trialState, final int site, final int level, final int what, final int who, final Game game) {
        this.verifyPresent(site);
        final int size = this.chunkStacks[site - this.offset].size();
        if (level == size) {
            this.addItem(trialState, site, what, who, game);
        }
        else if (level < size) {
            this.chunkStacks[site - this.offset].incrementSize(trialState);
            for (int i = size - 1; i >= level; --i) {
                final int whatLevel = this.chunkStacks[site - this.offset].what(i);
                this.chunkStacks[site - this.offset].setWhat(trialState, whatLevel, i + 1);
                final int whoLevel = this.chunkStacks[site - this.offset].who(i);
                this.chunkStacks[site - this.offset].setWho(trialState, whoLevel, i + 1);
                final int rotationLevel = this.chunkStacks[site - this.offset].rotation(i);
                this.chunkStacks[site - this.offset].setRotation(trialState, rotationLevel, i + 1);
                final int stateLevel = this.chunkStacks[site - this.offset].state(i);
                this.chunkStacks[site - this.offset].setState(trialState, stateLevel, i + 1);
            }
            this.chunkStacks[site - this.offset].setWhat(trialState, what, level);
            this.chunkStacks[site - this.offset].setWho(trialState, who, level);
            this.chunkStacks[site - this.offset].setState(trialState, 0, level);
            this.chunkStacks[site - this.offset].setRotation(trialState, 0, level);
        }
    }
    
    @Override
    public int whoCell(final int site) {
        if (this.chunkStacks[site - this.offset] == null) {
            return 0;
        }
        return this.chunkStacks[site - this.offset].who();
    }
    
    @Override
    public int whoCell(final int site, final int level) {
        if (this.chunkStacks[site - this.offset] == null) {
            return 0;
        }
        return this.chunkStacks[site - this.offset].who(level);
    }
    
    @Override
    public int whatCell(final int site) {
        if (this.chunkStacks[site - this.offset] == null) {
            return 0;
        }
        return this.chunkStacks[site - this.offset].what();
    }
    
    @Override
    public int whatCell(final int site, final int level) {
        if (this.chunkStacks[site - this.offset] == null) {
            return 0;
        }
        return this.chunkStacks[site - this.offset].what(level);
    }
    
    @Override
    public void setSite(final State trialState, final int site, final int whoVal, final int whatVal, final int countVal, final int stateVal, final int rotationVal, final int valueVal, final SiteType type) {
        if (type == SiteType.Cell) {
            this.verifyPresent(site);
            final boolean wasEmpty = this.isEmpty(site, SiteType.Cell);
            if (whoVal != -1) {
                this.chunkStacks[site - this.offset].setWho(trialState, whoVal);
            }
            if (whatVal != -1) {
                this.chunkStacks[site - this.offset].setWhat(trialState, whatVal);
            }
            if (stateVal != -1) {
                this.chunkStacks[site - this.offset].setState(trialState, stateVal);
            }
            if (rotationVal != -1) {
                this.chunkStacks[site - this.offset].setRotation(trialState, rotationVal);
            }
            final boolean isEmpty = this.isEmpty(site, SiteType.Cell);
            if (wasEmpty == isEmpty) {
                return;
            }
            if (isEmpty) {
                this.addToEmpty(site);
                if (this.playable != null) {
                    this.checkPlayable(trialState, site - this.offset);
                    final Cell v = this.container().topology().cells().get(site - this.offset);
                    for (final Cell vNbors : v.adjacent()) {
                        this.checkPlayable(trialState, vNbors.index());
                    }
                }
            }
            else {
                this.removeFromEmpty(site - this.offset);
                if (this.playable != null) {
                    this.setPlayable(trialState, site - this.offset, false);
                    final Cell v = this.container().topology().cells().get(site - this.offset);
                    for (final Cell vNbors : v.adjacent()) {
                        if (!this.isOccupied(vNbors.index())) {
                            this.setPlayable(trialState, vNbors.index(), true);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void setSite(final State trialState, final int site, final int level, final int whoVal, final int whatVal, final int countVal, final int stateVal, final int rotationVal, final int valueVal) {
        this.verifyPresent(site);
        final boolean wasEmpty = this.isEmpty(site, SiteType.Cell);
        if (whoVal != -1) {
            this.chunkStacks[site - this.offset].setWho(trialState, whoVal, level);
        }
        if (whatVal != -1) {
            this.chunkStacks[site - this.offset].setWhat(trialState, whatVal, level);
        }
        if (stateVal != -1) {
            this.chunkStacks[site - this.offset].setState(trialState, stateVal, level);
        }
        if (rotationVal != -1) {
            this.chunkStacks[site - this.offset].setRotation(trialState, rotationVal, level);
        }
        final boolean isEmpty = this.isEmpty(site, SiteType.Cell);
        if (wasEmpty == isEmpty) {
            return;
        }
        if (isEmpty) {
            this.addToEmpty(site);
            if (this.playable != null) {
                this.checkPlayable(trialState, site - this.offset);
                final Cell v = this.container().topology().cells().get(site - this.offset);
                for (final Cell vNbors : v.adjacent()) {
                    this.checkPlayable(trialState, vNbors.index());
                }
            }
        }
        else {
            this.removeFromEmpty(site - this.offset);
            if (this.playable != null) {
                this.setPlayable(trialState, site - this.offset, false);
                final Cell v = this.container().topology().cells().get(site - this.offset);
                for (final Cell vNbors : v.adjacent()) {
                    if (!this.isOccupied(vNbors.index())) {
                        this.setPlayable(trialState, vNbors.index(), true);
                    }
                }
            }
        }
    }
    
    private void checkPlayable(final State trialState, final int site) {
        if (this.isOccupied(site - this.offset)) {
            this.setPlayable(trialState, site - this.offset, false);
            return;
        }
        final Cell v = this.container().topology().cells().get(site - this.offset);
        for (final Cell vNbors : v.adjacent()) {
            if (this.isOccupied(vNbors.index())) {
                this.setPlayable(trialState, site - this.offset, true);
                return;
            }
        }
        this.setPlayable(trialState, site - this.offset, false);
    }
    
    @Override
    public boolean isOccupied(final int site) {
        return this.chunkStacks[site - this.offset] != null && this.chunkStacks[site - this.offset].what() != 0;
    }
    
    @Override
    public int stateCell(final int site) {
        if (this.chunkStacks[site - this.offset] == null) {
            return 0;
        }
        return this.chunkStacks[site - this.offset].state();
    }
    
    @Override
    public int stateCell(final int site, final int level) {
        if (this.chunkStacks[site - this.offset] == null) {
            return 0;
        }
        return this.chunkStacks[site - this.offset].state(level);
    }
    
    @Override
    public int rotationCell(final int site) {
        if (this.chunkStacks[site - this.offset] == null) {
            return 0;
        }
        return this.chunkStacks[site - this.offset].rotation();
    }
    
    @Override
    public int rotationCell(final int site, final int level) {
        if (this.chunkStacks[site - this.offset] == null) {
            return 0;
        }
        return this.chunkStacks[site - this.offset].rotation(level);
    }
    
    @Override
    public int remove(final State state, final int site, final SiteType graphElement) {
        if (this.chunkStacks[site - this.offset] == null) {
            return 0;
        }
        final int componentRemove = this.chunkStacks[site - this.offset].what();
        this.chunkStacks[site - this.offset].setWhat(state, 0);
        this.chunkStacks[site - this.offset].setWho(state, 0);
        this.chunkStacks[site - this.offset].setState(state, 0);
        this.chunkStacks[site - this.offset].setRotation(state, 0);
        this.chunkStacks[site - this.offset].decrementSize(state);
        return componentRemove;
    }
    
    @Override
    public int remove(final State state, final int site, final int level, final SiteType graphElement) {
        if (this.chunkStacks[site - this.offset] == null) {
            return 0;
        }
        final int componentRemove = this.chunkStacks[site - this.offset].what(level);
        for (int i = level; i < this.sizeStackCell(site) - 1; ++i) {
            this.chunkStacks[site - this.offset].setWhat(state, this.chunkStacks[site - this.offset].what(i + 1), i);
            this.chunkStacks[site - this.offset].setWho(state, this.chunkStacks[site - this.offset].who(i + 1), i);
            this.chunkStacks[site - this.offset].setState(state, this.chunkStacks[site - this.offset].state(i + 1), i);
            this.chunkStacks[site - this.offset].setRotation(state, this.chunkStacks[site - this.offset].rotation(i + 1), i);
        }
        this.chunkStacks[site - this.offset].setWhat(state, 0);
        this.chunkStacks[site - this.offset].setWho(state, 0);
        this.chunkStacks[site - this.offset].decrementSize(state);
        return componentRemove;
    }
    
    @Override
    public int remove(final State state, final int site, final int level) {
        if (this.chunkStacks[site - this.offset] == null) {
            return 0;
        }
        final int componentRemove = this.chunkStacks[site - this.offset].what(level);
        for (int i = level; i < this.sizeStackCell(site) - 1; ++i) {
            this.chunkStacks[site - this.offset].setWhat(state, this.chunkStacks[site - this.offset].what(i + 1), i);
            this.chunkStacks[site - this.offset].setWho(state, this.chunkStacks[site - this.offset].who(i + 1), i);
            this.chunkStacks[site - this.offset].setState(state, this.chunkStacks[site - this.offset].state(i + 1), i);
            this.chunkStacks[site - this.offset].setRotation(state, this.chunkStacks[site - this.offset].rotation(i + 1), i);
        }
        this.chunkStacks[site - this.offset].setWhat(state, 0);
        this.chunkStacks[site - this.offset].setWho(state, 0);
        this.chunkStacks[site - this.offset].decrementSize(state);
        return componentRemove;
    }
    
    @Override
    public void removeStack(final State state, final int site) {
        if (this.chunkStacks[site - this.offset] == null) {
            return;
        }
        state.updateStateHash(this.chunkStacks[site - this.offset].calcHash());
        this.chunkStacks[site - this.offset] = null;
    }
    
    @Override
    public int countCell(final int site) {
        if (this.chunkStacks[site - this.offset] == null) {
            return 0;
        }
        return 1;
    }
    
    @Override
    public int sizeStackCell(final int site) {
        if (this.chunkStacks[site - this.offset] == null) {
            return 0;
        }
        return this.chunkStacks[site - this.offset].size();
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
    public ContainerStateStacks deepClone() {
        return new ContainerStateStacks(this);
    }
    
    @Override
    public boolean isInvisibleCell(final int site, final int who) {
        return this.chunkStacks[site - this.offset] != null && this.chunkStacks[site - this.offset].isInvisible(who);
    }
    
    @Override
    public boolean isMaskedCell(final int site, final int who) {
        return this.chunkStacks[site - this.offset] != null && this.chunkStacks[site - this.offset].isMasked(who);
    }
    
    @Override
    public boolean isVisibleCell(final int site, final int who) {
        return this.chunkStacks[site - this.offset] != null && !this.chunkStacks[site - this.offset].isVisible(who);
    }
    
    @Override
    public void setInvisibleCell(final State trialState, final int site, final int who) {
        if (this.hiddenInfo) {
            this.chunkStacks[site - this.offset].setInvisible(trialState, who);
        }
    }
    
    @Override
    public void setVisibleCell(final State state, final int site, final int who) {
        if (this.hiddenInfo) {
            this.chunkStacks[site - this.offset].setVisible(state, who);
        }
    }
    
    @Override
    public void setMaskedCell(final State state, final int site, final int who) {
        if (this.hiddenInfo) {
            this.chunkStacks[site - this.offset].setMasked(state, who);
        }
    }
    
    @Override
    public boolean isInvisibleCell(final int site, final int level, final int who) {
        return this.chunkStacks[site - this.offset] != null && this.chunkStacks[site - this.offset].isInvisible(who, level);
    }
    
    @Override
    public boolean isVisibleCell(final int site, final int level, final int who) {
        return this.chunkStacks[site - this.offset] == null || this.chunkStacks[site - this.offset].isVisible(who, level);
    }
    
    @Override
    public boolean isMaskedCell(final int site, final int level, final int who) {
        return this.chunkStacks[site - this.offset] != null && this.chunkStacks[site - this.offset].isMasked(who, level);
    }
    
    @Override
    public void setInvisibleCell(final State trialState, final int site, final int level, final int who) {
        if (this.hiddenInfo) {
            this.chunkStacks[site - this.offset].setInvisible(trialState, who, level);
        }
    }
    
    @Override
    public void setVisibleCell(final State trialState, final int site, final int level, final int who) {
        if (this.hiddenInfo) {
            this.chunkStacks[site - this.offset].setVisible(trialState, who, level);
        }
    }
    
    @Override
    public void setMaskedCell(final State trialState, final int site, final int level, final int who) {
        if (this.hiddenInfo) {
            this.chunkStacks[site - this.offset].setMasked(trialState, who, level);
        }
    }
    
    @Override
    public boolean isPlayable(final int site) {
        if (this.playable == null) {
            throw new RuntimeException("Tried to access playable bitset in non-boardless game.");
        }
        return this.playable.get(site - this.offset);
    }
    
    @Override
    public void setPlayable(final State trialState, final int site, final boolean on) {
        this.playable.set(trialState, site - this.offset, on);
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
        return true;
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
    public int value(final int site) {
        return 0;
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
    public void setValue(final State trialState, final int site, final int valueVal) {
    }
    
    @Override
    public void setCount(final State trialState, final int site, final int countVal) {
    }
    
    @Override
    public void addToEmpty(final int site, final SiteType graphType) {
        this.addToEmpty(site);
    }
    
    @Override
    public void removeFromEmpty(final int site, final SiteType graphType) {
        this.removeFromEmpty(site);
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
