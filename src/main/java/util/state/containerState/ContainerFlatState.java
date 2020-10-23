// 
// Decompiled by Procyon v0.5.36
// 

package util.state.containerState;

import game.Game;
import game.equipment.container.Container;
import game.types.board.SiteType;
import collections.ChunkSet;
import topology.Cell;
import util.state.State;
import util.zhash.HashedBitSet;
import util.zhash.HashedChunkSet;
import util.zhash.ZobristHashGenerator;

public class ContainerFlatState extends BaseContainerState
{
    private static final long serialVersionUID = 1L;
    protected HashedChunkSet[] hidden;
    protected final HashedBitSet playable;
    protected final HashedChunkSet who;
    protected final HashedChunkSet what;
    protected final HashedChunkSet count;
    protected final HashedChunkSet state;
    protected final HashedChunkSet rotation;
    protected final HashedChunkSet value;
    
    public ContainerFlatState(final ZobristHashGenerator generator, final Game game, final Container container, final int numSites, final int maxWhatVal, final int maxStateVal, final int maxCountVal, final int maxRotationVal) {
        super(game, container, numSites);
        final int numPlayers = game.players().count();
        if ((game.gameFlags() & 0x8L) == 0x0L) {
            this.hidden = null;
        }
        else {
            this.hidden = new HashedChunkSet[numPlayers + 1];
            final int numHiddenState = 3;
            for (int i = 1; i < numPlayers + 1; ++i) {
                this.hidden[i] = new HashedChunkSet(generator, 3, numSites);
            }
        }
        if (!game.isBoardless()) {
            this.playable = null;
        }
        else {
            this.playable = new HashedBitSet(generator, numSites);
        }
        this.who = new HashedChunkSet(generator, numPlayers + 1, numSites);
        this.what = ((maxWhatVal > 0) ? new HashedChunkSet(generator, maxWhatVal, numSites) : null);
        this.count = ((maxCountVal > 0) ? new HashedChunkSet(generator, maxCountVal, numSites) : null);
        this.state = ((maxStateVal > 0) ? new HashedChunkSet(generator, maxStateVal, numSites) : null);
        this.rotation = ((maxRotationVal > 0) ? new HashedChunkSet(generator, maxRotationVal, numSites) : null);
        this.value = ((game.hasDominoes() && container.index() == 0) ? new HashedChunkSet(generator, 10, numSites) : null);
    }
    
    public ContainerFlatState(final ContainerFlatState other) {
        super(other);
        this.who = ((other.who == null) ? null : other.who.clone());
        if (other.hidden != null) {
            this.hidden = new HashedChunkSet[other.hidden.length];
            for (int i = 1; i < other.hidden.length; ++i) {
                this.hidden[i] = ((other.hidden[i] == null) ? null : other.hidden[i].clone());
            }
        }
        this.playable = ((other.playable == null) ? null : other.playable.clone());
        this.what = ((other.what == null) ? null : other.what.clone());
        this.count = ((other.count == null) ? null : other.count.clone());
        this.state = ((other.state == null) ? null : other.state.clone());
        this.rotation = ((other.rotation == null) ? null : other.rotation.clone());
        this.value = ((other.value == null) ? null : other.value.clone());
    }
    
    @Override
    public ContainerFlatState deepClone() {
        return new ContainerFlatState(this);
    }
    
    @Override
    protected long calcCanonicalHash(final int[] siteRemap, final int[] edgeRemap, final int[] vertexRemap, final int[] playerRemap, final boolean whoOnly) {
        long hash = 0L;
        if (this.who != null) {
            hash ^= this.who.calculateHashAfterRemap(siteRemap, playerRemap);
        }
        if (!whoOnly) {
            if (this.what != null) {
                hash ^= this.what.calculateHashAfterRemap(siteRemap, null);
            }
            if (this.playable != null) {
                hash ^= this.playable.calculateHashAfterRemap(siteRemap, false);
            }
            if (this.count != null) {
                hash ^= this.count.calculateHashAfterRemap(siteRemap, null);
            }
            if (this.state != null) {
                hash ^= this.state.calculateHashAfterRemap(siteRemap, null);
            }
            if (this.rotation != null) {
                hash ^= this.rotation.calculateHashAfterRemap(siteRemap, null);
            }
            if (this.value != null) {
                hash ^= this.value.calculateHashAfterRemap(siteRemap, null);
            }
            if (this.hidden != null) {
                for (int whom = 1; whom < this.hidden.length; ++whom) {
                    hash ^= this.hidden[whom].calculateHashAfterRemap(siteRemap, playerRemap);
                }
            }
        }
        return hash;
    }
    
    @Override
    public void reset(final State trialState, final Game game) {
        super.reset(trialState, game);
        if (this.who != null) {
            this.who.clear(trialState);
        }
        if (this.what != null) {
            this.what.clear(trialState);
        }
        if (this.count != null) {
            this.count.clear(trialState);
        }
        if (this.state != null) {
            this.state.clear(trialState);
        }
        if (this.rotation != null) {
            this.rotation.clear(trialState);
        }
        if (this.value != null) {
            this.value.clear(trialState);
        }
    }
    
    @Override
    public boolean isInvisibleCell(final int site, final int whoH) {
        return this.hidden != null && this.hidden[whoH].getChunk(site - this.offset) == 2;
    }
    
    @Override
    public boolean isMaskedCell(final int site, final int whoH) {
        return this.hidden != null && this.hidden[whoH].getChunk(site - this.offset) == 1;
    }
    
    @Override
    public boolean isVisibleCell(final int site, final int whoH) {
        return this.hidden == null || this.hidden[whoH].getChunk(site - this.offset) == 0;
    }
    
    @Override
    public void setInvisibleCell(final State trialState, final int site, final int who) {
        if (this.hidden == null) {
            throw new RuntimeException("Tried to set invisible information a cell in game with no hidden state.");
        }
        this.hidden[who].setChunk(trialState, site - this.offset, 2);
    }
    
    @Override
    public void setVisibleCell(final State trialState, final int site, final int who) {
        if (this.hidden == null) {
            throw new RuntimeException("Tried to set visible information a cell in game with no hidden state.");
        }
        this.hidden[who].setChunk(trialState, site - this.offset, 0);
    }
    
    @Override
    public void setMaskedCell(final State trialState, final int site, final int who) {
        if (this.hidden == null) {
            throw new RuntimeException("Tried to set masked information a cell in game with no hidden state.");
        }
        this.hidden[who].setChunk(trialState, site - this.offset, 1);
    }
    
    @Override
    public boolean isPlayable(final int site) {
        return this.playable == null || this.playable.get(site - this.offset);
    }
    
    @Override
    public void setPlayable(final State trialState, final int site, final boolean on) {
        if (this.playable != null) {
            this.playable.set(trialState, site, on);
        }
    }
    
    @Override
    public boolean isOccupied(final int site) {
        return this.countCell(site) != 0;
    }
    
    @Override
    public void setSite(final State trialState, final int site, final int whoVal, final int whatVal, final int countVal, final int stateVal, final int rotationVal, final int valueVal, final SiteType type) {
        final boolean wasEmpty = !this.isOccupied(site);
        if (whoVal != -1) {
            this.who.setChunk(trialState, site - this.offset, whoVal);
        }
        if (whatVal != -1) {
            this.defaultIfNull(this.what).setChunk(trialState, site - this.offset, whatVal);
        }
        if (countVal != -1) {
            if (this.count != null) {
                this.count.setChunk(trialState, site - this.offset, Math.max(countVal, 0));
            }
            else if (this.count == null && countVal > 1) {
                throw new UnsupportedOperationException("This game does not support counts, but a count > 1 has been set. countVal=" + countVal);
            }
        }
        if (stateVal != -1) {
            if (this.state != null) {
                this.state.setChunk(trialState, site - this.offset, stateVal);
            }
            else if (stateVal != 0) {
                throw new UnsupportedOperationException("This game does not support states, but a state has been set. stateVal=" + stateVal);
            }
        }
        if (rotationVal != -1) {
            if (this.rotation != null) {
                this.rotation.setChunk(trialState, site - this.offset, rotationVal);
            }
            else if (rotationVal != 0) {
                throw new UnsupportedOperationException("This game does not support rotations, but a rotation has been set. rotationVal=" + rotationVal);
            }
        }
        final boolean isEmpty = !this.isOccupied(site);
        if (wasEmpty == isEmpty) {
            return;
        }
        if (isEmpty) {
            this.addToEmpty(site);
            if (this.playable != null && valueVal == -1) {
                this.checkPlayable(trialState, site);
                final Cell v = this.container().topology().cells().get(site - this.offset);
                for (final Cell vNbors : v.adjacent()) {
                    this.checkPlayable(trialState, vNbors.index());
                }
            }
        }
        else {
            this.removeFromEmpty(site);
            if (this.playable != null && valueVal == -1) {
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
        if (this.isOccupied(site)) {
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
    public int whoCell(final int site) {
        return this.who.getChunk(site - this.offset);
    }
    
    @Override
    public int whatCell(final int site) {
        if (this.what == null) {
            return this.whoCell(site);
        }
        return this.what.getChunk(site - this.offset);
    }
    
    @Override
    public int stateCell(final int site) {
        if (this.state == null) {
            return 0;
        }
        return this.state.getChunk(site - this.offset);
    }
    
    @Override
    public int rotationCell(final int site) {
        if (this.rotation == null) {
            return 0;
        }
        return this.rotation.getChunk(site - this.offset);
    }
    
    @Override
    public int value(final int site) {
        if (this.value == null) {
            return 0;
        }
        return this.value.getChunk(site - this.offset);
    }
    
    @Override
    public int countCell(final int site) {
        if (this.count != null) {
            return this.count.getChunk(site - this.offset);
        }
        if (this.who.getChunk(site - this.offset) != 0 || (this.what != null && this.what.getChunk(site - this.offset) != 0)) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public int remove(final State trialState, final int site, final SiteType type) {
        final int whatIdx = this.what(site, type);
        this.setSite(trialState, site, 0, 0, 0, 0, 0, 0, type);
        return whatIdx;
    }
    
    @Override
    public int remove(final State trialState, final int site, final int level, final SiteType type) {
        return this.remove(trialState, site, type);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + ((this.hidden == null) ? 0 : this.hidden.hashCode());
        result = 31 * result + ((this.who == null) ? 0 : this.who.hashCode());
        result = 31 * result + ((this.playable == null) ? 0 : this.playable.hashCode());
        result = 31 * result + ((this.count == null) ? 0 : this.count.hashCode());
        result = 31 * result + ((this.what == null) ? 0 : this.what.hashCode());
        result = 31 * result + ((this.state == null) ? 0 : this.state.hashCode());
        result = 31 * result + ((this.rotation == null) ? 0 : this.rotation.hashCode());
        result = 31 * result + ((this.value == null) ? 0 : this.value.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ContainerFlatState)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final ContainerFlatState other = (ContainerFlatState)obj;
        if (this.hidden != null) {
            for (int i = 1; i < this.hidden.length; ++i) {
                if (!chunkSetsEqual(this.hidden[i], other.hidden[i])) {
                    return false;
                }
            }
        }
        return chunkSetsEqual(this.who, other.who) && bitSetsEqual(this.playable, other.playable) && chunkSetsEqual(this.count, other.count) && chunkSetsEqual(this.what, other.what) && chunkSetsEqual(this.state, other.state) && chunkSetsEqual(this.rotation, other.rotation) && chunkSetsEqual(this.value, other.value);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ContainerState type = " + this.getClass() + "\n");
        if (this.emptyChunkSetCell() != null) {
            sb.append("Empty = " + this.emptyChunkSetCell().toChunkString() + "\n");
        }
        if (this.who != null) {
            sb.append("Who = " + this.cloneWhoCell().toChunkString() + "\n");
        }
        if (this.what != null) {
            sb.append("What" + this.cloneWhatCell().toChunkString() + "\n");
        }
        if (this.state != null) {
            sb.append("State = " + this.state.internalStateCopy().toChunkString() + "\n");
        }
        if (this.rotation != null) {
            sb.append("Rotation = " + this.rotation.internalStateCopy().toChunkString() + "\n");
        }
        if (this.value != null) {
            sb.append("value = " + this.value.internalStateCopy().toChunkString() + "\n");
        }
        if (this.count != null) {
            sb.append("Count = " + this.count.internalStateCopy().toChunkString() + "\n");
        }
        if (this.playable != null) {
            sb.append("Playable = " + this.playable.internalStateCopy().toString() + "\n");
        }
        if (this.hidden != null) {
            for (int i = 1; i < this.hidden.length; ++i) {
                sb.append("Hidden for  player " + i + " = " + this.hidden[i].internalStateCopy().toChunkString() + "\n");
            }
        }
        return sb.toString();
    }
    
    private static boolean chunkSetsEqual(final HashedChunkSet thisSet, final HashedChunkSet otherSet) {
        if (thisSet == null) {
            return otherSet == null;
        }
        return thisSet.equals(otherSet);
    }
    
    private static boolean bitSetsEqual(final HashedBitSet thisSet, final HashedBitSet otherSet) {
        if (thisSet == null) {
            return otherSet == null;
        }
        return thisSet.equals(otherSet);
    }
    
    @Override
    public int sizeStackCell(final int site) {
        if (this.whatCell(site) != 0) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public int sizeStackEdge(final int site) {
        if (this.whatEdge(site) != 0) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public int sizeStackVertex(final int site) {
        if (this.whatVertex(site) != 0) {
            return 1;
        }
        return 0;
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
    public boolean isInvisibleEdge(final int site, final int whoId) {
        return false;
    }
    
    @Override
    public boolean isVisibleEdge(final int site, final int whoId) {
        return true;
    }
    
    @Override
    public boolean isMaskedEdge(final int site, final int whoId) {
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
    public boolean isInvisibleVertex(final int site, final int whoId) {
        return false;
    }
    
    @Override
    public boolean isVisibleVertex(final int site, final int whoId) {
        return true;
    }
    
    @Override
    public boolean isMaskedVertex(final int site, final int whoId) {
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
    public void setValue(final State trialState, final int site, final int valueVal) {
        if (valueVal != -1) {
            if (this.value != null) {
                this.value.setChunk(trialState, site - this.offset, valueVal);
            }
            else if (valueVal != 0) {
                throw new UnsupportedOperationException("This game does not support dominoes, but a value has been set. valueVal=" + valueVal);
            }
        }
    }
    
    @Override
    public void setCount(final State trialState, final int site, final int countVal) {
        if (countVal != -1) {
            if (this.count != null) {
                this.count.setChunk(trialState, site - this.offset, Math.max(countVal, 0));
            }
            else if (this.count == null && countVal > 1) {
                throw new UnsupportedOperationException("This game does not support counts, but a count > 1 has been set. countVal=" + countVal);
            }
        }
    }
    
    @Override
    public void addItem(final State trialState, final int site, final int whatItem, final int whoItem, final Game game) {
    }
    
    @Override
    public void insert(final State trialState, final int site, final int level, final int whatItem, final int whoItem, final Game game) {
    }
    
    @Override
    public void addItem(final State trialState, final int site, final int whatItem, final int whoItem, final int stateVal, final int rotationVal, final Game game) {
    }
    
    @Override
    public void addItem(final State trialState, final int site, final int whatItem, final int whoItem, final Game game, final boolean[] hiddenItem, final boolean masked) {
    }
    
    @Override
    public void removeStack(final State trialState, final int site) {
    }
    
    @Override
    public int whoCell(final int site, final int level) {
        return this.whoCell(site);
    }
    
    @Override
    public int whatCell(final int site, final int level) {
        return this.whatCell(site);
    }
    
    @Override
    public int stateCell(final int site, final int level) {
        return this.stateCell(site);
    }
    
    @Override
    public int rotationCell(final int site, final int level) {
        return this.rotationCell(site);
    }
    
    @Override
    public boolean isInvisibleCell(final int site, final int level, final int owner) {
        return this.isInvisibleCell(site, owner);
    }
    
    @Override
    public boolean isVisibleCell(final int site, final int level, final int owner) {
        return this.isVisibleCell(site, owner);
    }
    
    @Override
    public boolean isMaskedCell(final int site, final int level, final int owner) {
        return this.isMaskedCell(site, owner);
    }
    
    @Override
    public void setInvisibleCell(final State trialState, final int site, final int level, final int who) {
        this.setInvisibleCell(trialState, site, who);
    }
    
    @Override
    public void setVisibleCell(final State trialState, final int site, final int level, final int who) {
        this.setVisibleCell(trialState, site, who);
    }
    
    @Override
    public void setMaskedCell(final State trialState, final int site, final int level, final int who) {
        this.setMaskedCell(trialState, site, who);
    }
    
    @Override
    public int remove(final State trialState, final int site, final int level) {
        return this.remove(trialState, site, SiteType.Cell);
    }
    
    @Override
    public void setSite(final State trialState, final int site, final int level, final int whoVal, final int whatVal, final int countVal, final int stateVal, final int rotationVal, final int valueVal) {
        this.setSite(trialState, site, whoVal, whatVal, countVal, stateVal, rotationVal, valueVal, SiteType.Cell);
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
    public void addToEmpty(final int site, final SiteType graphType) {
        this.addToEmpty(site);
    }
    
    @Override
    public void removeFromEmpty(final int site, final SiteType graphType) {
        this.removeFromEmpty(site);
    }
    
    protected final HashedChunkSet defaultIfNull(final HashedChunkSet preferred) {
        if (preferred != null) {
            return preferred;
        }
        return this.who;
    }
    
    @Override
    public ChunkSet emptyChunkSetCell() {
        return this.empty.bitSet();
    }
    
    @Override
    public ChunkSet emptyChunkSetVertex() {
        return null;
    }
    
    @Override
    public ChunkSet emptyChunkSetEdge() {
        return null;
    }
    
    @Override
    public int numChunksWhoCell() {
        return this.who.numChunks();
    }
    
    @Override
    public int numChunksWhoVertex() {
        return -1;
    }
    
    @Override
    public int numChunksWhoEdge() {
        return -1;
    }
    
    @Override
    public int chunkSizeWhoCell() {
        return this.who.chunkSize();
    }
    
    @Override
    public int chunkSizeWhoVertex() {
        return -1;
    }
    
    @Override
    public int chunkSizeWhoEdge() {
        return -1;
    }
    
    @Override
    public int numChunksWhatCell() {
        return this.defaultIfNull(this.what).numChunks();
    }
    
    @Override
    public int numChunksWhatVertex() {
        return -1;
    }
    
    @Override
    public int numChunksWhatEdge() {
        return -1;
    }
    
    @Override
    public int chunkSizeWhatCell() {
        return this.defaultIfNull(this.what).chunkSize();
    }
    
    @Override
    public int chunkSizeWhatVertex() {
        return -1;
    }
    
    @Override
    public int chunkSizeWhatEdge() {
        return -1;
    }
    
    @Override
    public boolean matchesWhoCell(final ChunkSet mask, final ChunkSet pattern) {
        return this.who.matches(mask, pattern);
    }
    
    @Override
    public boolean matchesWhoVertex(final ChunkSet mask, final ChunkSet pattern) {
        return false;
    }
    
    @Override
    public boolean matchesWhoEdge(final ChunkSet mask, final ChunkSet pattern) {
        return false;
    }
    
    @Override
    public boolean violatesNotWhoCell(final ChunkSet mask, final ChunkSet pattern) {
        return this.who.violatesNot(mask, pattern);
    }
    
    @Override
    public boolean violatesNotWhoVertex(final ChunkSet mask, final ChunkSet pattern) {
        return false;
    }
    
    @Override
    public boolean violatesNotWhoEdge(final ChunkSet mask, final ChunkSet pattern) {
        return false;
    }
    
    @Override
    public boolean violatesNotWhoCell(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        return this.who.violatesNot(mask, pattern, startWord);
    }
    
    @Override
    public boolean violatesNotWhoVertex(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        return false;
    }
    
    @Override
    public boolean violatesNotWhoEdge(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        return false;
    }
    
    @Override
    public boolean matchesWhatCell(final ChunkSet mask, final ChunkSet pattern) {
        return this.defaultIfNull(this.what).matches(mask, pattern);
    }
    
    @Override
    public boolean matchesWhatVertex(final ChunkSet mask, final ChunkSet pattern) {
        return false;
    }
    
    @Override
    public boolean matchesWhatEdge(final ChunkSet mask, final ChunkSet pattern) {
        return false;
    }
    
    @Override
    public boolean violatesNotWhatCell(final ChunkSet mask, final ChunkSet pattern) {
        return this.defaultIfNull(this.what).violatesNot(mask, pattern);
    }
    
    @Override
    public boolean violatesNotWhatVertex(final ChunkSet mask, final ChunkSet pattern) {
        return false;
    }
    
    @Override
    public boolean violatesNotWhatEdge(final ChunkSet mask, final ChunkSet pattern) {
        return false;
    }
    
    @Override
    public boolean violatesNotWhatCell(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        return this.defaultIfNull(this.what).violatesNot(mask, pattern, startWord);
    }
    
    @Override
    public boolean violatesNotWhatVertex(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        return false;
    }
    
    @Override
    public boolean violatesNotWhatEdge(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        return false;
    }
    
    @Override
    public ChunkSet cloneWhoCell() {
        return this.who.internalStateCopy();
    }
    
    @Override
    public ChunkSet cloneWhoVertex() {
        return null;
    }
    
    @Override
    public ChunkSet cloneWhoEdge() {
        return null;
    }
    
    @Override
    public ChunkSet cloneWhatCell() {
        return this.defaultIfNull(this.what).internalStateCopy();
    }
    
    @Override
    public ChunkSet cloneWhatVertex() {
        return null;
    }
    
    @Override
    public ChunkSet cloneWhatEdge() {
        return null;
    }
}
