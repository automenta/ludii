// 
// Decompiled by Procyon v0.5.36
// 

package util.zhash;

import collections.ChunkStack;
import util.state.State;

import java.io.Serializable;

public class HashedChunkStack implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final ChunkStack internalState;
    private final long[][] whatHash;
    private final long[][] whoHash;
    private final long[][] stateHash;
    private final long[][] rotationHash;
    private final long[] sizeHash;
    private long zhash;
    
    public HashedChunkStack(final int numComponents, final int numPlayers, final int numStates, final int numRotations, final int type, final boolean hidden, final long[][] whatHash, final long[][] whoHash, final long[][] stateHash, final long[][] rotationHash, final long[] sizeHash) {
        this.zhash = 0L;
        this.internalState = new ChunkStack(numComponents, numPlayers, numStates, numRotations, type, hidden);
        this.whatHash = whatHash;
        this.whoHash = whoHash;
        this.stateHash = stateHash;
        this.rotationHash = rotationHash;
        this.sizeHash = sizeHash;
    }
    
    private HashedChunkStack(final HashedChunkStack that) {
        this.zhash = 0L;
        this.internalState = new ChunkStack(that.internalState);
        this.whatHash = that.whatHash;
        this.whoHash = that.whoHash;
        this.stateHash = that.stateHash;
        this.rotationHash = that.rotationHash;
        this.sizeHash = that.sizeHash;
        this.zhash = that.zhash;
    }
    
    public long calcHash() {
        return this.zhash;
    }
    
    public long remapHashTo(final long[][] newWhatHash, final long[][] newWhoHash, final long[][] newStateHash, final long[][] newRotationHash, final long[] newSizeHash, final boolean whoOnly) {
        long hash = newSizeHash[this.internalState.size()];
        for (int level = 0; level < this.internalState.size(); ++level) {
            if (whoOnly) {
                hash ^= newWhoHash[level][this.internalState.who(level)];
            }
            else {
                hash ^= newStateHash[level][this.internalState.state(level)];
                hash ^= newRotationHash[level][this.internalState.rotation(level)];
                hash ^= newWhoHash[level][this.internalState.who(level)];
                hash ^= newWhatHash[level][this.internalState.what(level)];
            }
        }
        return hash;
    }
    
    public void setState(final State trialState, final int val, final int level) {
        if (level >= this.internalState.size()) {
            return;
        }
        long delta = this.stateHash[level][this.internalState.state(level)];
        this.internalState.setState(val, level);
        delta ^= this.stateHash[level][this.internalState.state(level)];
        trialState.updateStateHash(delta);
        this.zhash ^= delta;
    }
    
    public void setRotation(final State trialState, final int val, final int level) {
        if (level >= this.internalState.size()) {
            return;
        }
        long delta = this.rotationHash[level][this.internalState.rotation(level)];
        this.internalState.setRotation(val, level);
        delta ^= this.rotationHash[level][this.internalState.rotation(level)];
        trialState.updateStateHash(delta);
        this.zhash ^= delta;
    }
    
    public void setWho(final State trialState, final int val, final int level) {
        if (level >= this.internalState.size()) {
            return;
        }
        long delta = this.whoHash[level][this.internalState.who(level)];
        this.internalState.setWho(val, level);
        delta ^= this.whoHash[level][this.internalState.who(level)];
        trialState.updateStateHash(delta);
        this.zhash ^= delta;
    }
    
    public void setWhat(final State trialState, final int val, final int level) {
        if (level >= this.internalState.size()) {
            return;
        }
        long delta = this.whatHash[level][this.internalState.what(level)];
        this.internalState.setWhat(val, level);
        delta ^= this.whatHash[level][this.internalState.what(level)];
        trialState.updateStateHash(delta);
        this.zhash ^= delta;
    }
    
    public void setInvisible(final State trialState, final int who, final int level) {
        if (level >= this.internalState.size()) {
            return;
        }
        this.internalState.setInvisible(who, level);
    }
    
    public void setVisible(final State trialState, final int who, final int level) {
        if (level >= this.internalState.size()) {
            return;
        }
        this.internalState.setVisible(who, level);
    }
    
    public void setMasked(final State trialState, final int who, final int level) {
        if (level >= this.internalState.size()) {
            return;
        }
        this.internalState.setMasked(who, level);
    }
    
    public void decrementSize(final State trialState) {
        long delta = this.sizeHash[this.internalState.size()];
        this.internalState.decrementSize();
        delta ^= this.sizeHash[this.internalState.size()];
        trialState.updateStateHash(delta);
        this.zhash ^= delta;
    }
    
    public void incrementSize(final State trialState) {
        long delta = this.sizeHash[this.internalState.size()];
        this.internalState.incrementSize();
        delta ^= this.sizeHash[this.internalState.size()];
        trialState.updateStateHash(delta);
        this.zhash ^= delta;
    }
    
    public void setRotation(final State trialState, final int val) {
        final int size = this.internalState.size();
        if (size <= 0) {
            return;
        }
        this.setRotation(trialState, val, size - 1);
    }
    
    public void setState(final State trialState, final int val) {
        final int size = this.internalState.size();
        if (size <= 0) {
            return;
        }
        this.setState(trialState, val, size - 1);
    }
    
    public void setWho(final State trialState, final int val) {
        final int size = this.internalState.size();
        if (size <= 0) {
            return;
        }
        this.setWho(trialState, val, size - 1);
    }
    
    public void setWhat(final State trialState, final int val) {
        final int size = this.internalState.size();
        if (size <= 0) {
            return;
        }
        this.setWhat(trialState, val, size - 1);
    }
    
    public void setInvisible(final State trialState, final int who) {
        final int size = this.internalState.size();
        if (size <= 0) {
            return;
        }
        this.setInvisible(trialState, who, size - 1);
    }
    
    public void setVisible(final State trialState, final int who) {
        final int size = this.internalState.size();
        if (size <= 0) {
            return;
        }
        this.setVisible(trialState, who, size - 1);
    }
    
    public void setMasked(final State trialState, final int who) {
        final int size = this.internalState.size();
        if (size <= 0) {
            return;
        }
        this.setMasked(trialState, who, size - 1);
    }
    
    public HashedChunkStack clone() {
        return new HashedChunkStack(this);
    }
    
    public int size() {
        return this.internalState.size();
    }
    
    public int who() {
        return this.internalState.who();
    }
    
    public int who(final int level) {
        return this.internalState.who(level);
    }
    
    public int what() {
        return this.internalState.what();
    }
    
    public int what(final int level) {
        return this.internalState.what(level);
    }
    
    public boolean isInvisible(final int who) {
        return this.internalState.isInvisible(who);
    }
    
    public boolean isVisible(final int who) {
        return this.internalState.isVisible(who);
    }
    
    public boolean isMasked(final int who) {
        return this.internalState.isMasked(who);
    }
    
    public boolean isInvisible(final int who, final int level) {
        return this.internalState.isInvisible(who, level);
    }
    
    public boolean isVisible(final int who, final int level) {
        return this.internalState.isVisible(who, level);
    }
    
    public boolean isMasked(final int who, final int level) {
        return this.internalState.isMasked(who, level);
    }
    
    public int state() {
        return this.internalState.state();
    }
    
    public int state(final int level) {
        return this.internalState.state(level);
    }
    
    public int rotation() {
        return this.internalState.rotation();
    }
    
    public int rotation(final int level) {
        return this.internalState.rotation(level);
    }
}
