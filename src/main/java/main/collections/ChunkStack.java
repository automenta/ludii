// 
// Decompiled by Procyon v0.5.36
// 

package main.collections;

import main.math.BitTwiddling;

import java.io.Serializable;

public final class ChunkStack implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static final int TYPE_DEFAULT_STATE = 0;
    public static final int TYPE_PLAYER_STATE = 1;
    public static final int TYPE_INDEX_STATE = 2;
    public static final int TYPE_INDEX_LOCAL_STATE = 3;
    protected final int type;
    protected final ChunkSet what;
    protected final ChunkSet who;
    protected final ChunkSet state;
    protected final ChunkSet rotation;
    protected final ChunkSet[] hidden;
    protected int size;
    
    public ChunkStack(final int numComponents, final int numPlayers, final int numStates, final int numRotation, final int type, final boolean hidden) {
        this.type = type;
        this.size = 0;
        final int chunkSizeWhat = BitTwiddling.nextPowerOf2(BitTwiddling.bitsRequired(numComponents));
        this.what = new ChunkSet(chunkSizeWhat, 1);
        final int chunkSizeWho = BitTwiddling.nextPowerOf2(BitTwiddling.bitsRequired(numPlayers + 1));
        if (type > 0) {
            this.who = new ChunkSet(chunkSizeWho, 1);
        }
        else {
            this.who = null;
        }
        final int chunkSizeState = BitTwiddling.nextPowerOf2(BitTwiddling.bitsRequired(numStates));
        if (type > 1) {
            this.state = new ChunkSet(chunkSizeState, 1);
        }
        else {
            this.state = null;
        }
        final int chunkSizeRotation = BitTwiddling.nextPowerOf2(BitTwiddling.bitsRequired(numRotation));
        if (type > 2) {
            this.rotation = new ChunkSet(chunkSizeRotation, 1);
        }
        else {
            this.rotation = null;
        }
        if (hidden) {
            final int chunkSizeHidden = BitTwiddling.nextPowerOf2(BitTwiddling.bitsRequired(3));
            this.hidden = new ChunkSet[numPlayers + 1];
            for (int i = 0; i < numPlayers + 1; ++i) {
                this.hidden[i] = new ChunkSet(chunkSizeHidden, 1);
            }
        }
        else {
            this.hidden = null;
        }
    }
    
    public ChunkStack(final ChunkStack other) {
        this.type = other.type;
        this.size = other.size;
        this.what = ((other.what == null) ? null : other.what.clone());
        this.who = ((other.who == null) ? null : other.who.clone());
        this.state = ((other.state == null) ? null : other.state.clone());
        this.rotation = ((other.rotation == null) ? null : other.rotation.clone());
        if (other.hidden == null) {
            this.hidden = null;
        }
        else {
            this.hidden = new ChunkSet[other.hidden.length];
            for (int i = 0; i < this.hidden.length; ++i) {
                if (other.hidden[i] != null) {
                    this.hidden[i] = other.hidden[i].clone();
                }
            }
        }
    }
    
    public ChunkSet whatChunkSet() {
        return this.what;
    }
    
    public ChunkSet whoChunkSet() {
        return this.who;
    }
    
    public ChunkSet stateChunkSet() {
        return this.state;
    }
    
    public ChunkSet rotationChunkSet() {
        return this.rotation;
    }
    
    public ChunkSet[] hiddenBitSet() {
        return this.hidden;
    }
    
    public int size() {
        return this.size;
    }
    
    public void incrementSize() {
        ++this.size;
    }
    
    public void decrementSize() {
        if (this.size > 0) {
            --this.size;
        }
    }
    
    public int state() {
        if (this.type >= 2 && this.size > 0) {
            return this.state.getChunk(this.size - 1);
        }
        return 0;
    }
    
    public int state(final int level) {
        if (this.type >= 2 && level < this.size) {
            return this.state.getChunk(level);
        }
        return 0;
    }
    
    public void setState(final int val) {
        if (this.type >= 2 && this.size > 0) {
            this.state.setChunk(this.size - 1, val);
        }
    }
    
    public void setState(final int val, final int level) {
        if (this.type >= 2 && level < this.size) {
            this.state.setChunk(level, val);
        }
    }
    
    public int rotation() {
        if (this.type >= 3 && this.size > 0) {
            return this.rotation.getChunk(this.size - 1);
        }
        return 0;
    }
    
    public int rotation(final int level) {
        if (this.type >= 3 && level < this.size) {
            return this.rotation.getChunk(level);
        }
        return 0;
    }
    
    public void setRotation(final int val) {
        if (this.type >= 3 && this.size > 0) {
            this.rotation.setChunk(this.size - 1, val);
        }
    }
    
    public void setRotation(final int val, final int level) {
        if (this.type >= 3 && level < this.size) {
            this.rotation.setChunk(level, val);
        }
    }
    
    public boolean isInvisible(final int pid) {
        return this.hidden != null && this.size > 0 && this.hidden[pid].getChunk(this.size - 1) == 2;
    }
    
    public boolean isVisible(final int pid) {
        return this.hidden != null && this.size > 0 && this.hidden[pid].getChunk(this.size - 1) == 0;
    }
    
    public boolean isMasked(final int pid) {
        return this.hidden != null && this.size > 0 && this.hidden[pid].getChunk(this.size - 1) == 1;
    }
    
    public boolean isInvisible(final int pid, final int level) {
        return this.hidden != null && level < this.size && this.hidden[pid].getChunk(level) == 2;
    }
    
    public boolean isVisible(final int pid, final int level) {
        return this.hidden != null && level < this.size && this.hidden[pid].getChunk(level) == 0;
    }
    
    public boolean isMasked(final int pid, final int level) {
        return this.hidden != null && level < this.size && this.hidden[pid].getChunk(level) == 1;
    }
    
    public void setInvisible(final int pid) {
        if (this.hidden != null && this.size > 0) {
            this.hidden[pid].setChunk(this.size - 1, 2);
        }
    }
    
    public void setVisible(final int pid) {
        if (this.hidden != null && this.size > 0) {
            this.hidden[pid].setChunk(this.size - 1, 0);
        }
    }
    
    public void setMasked(final int pid) {
        if (this.hidden != null && this.size > 0) {
            this.hidden[pid].setChunk(this.size - 1, 1);
        }
    }
    
    public void setInvisible(final int pid, final int level) {
        if (this.hidden != null && level < this.size) {
            this.hidden[pid].setChunk(level, 2);
        }
    }
    
    public void setVisible(final int pid, final int level) {
        if (this.hidden != null && level < this.size) {
            this.hidden[pid].setChunk(level, 0);
        }
    }
    
    public void setMasked(final int pid, final int level) {
        if (this.hidden != null && level < this.size) {
            this.hidden[pid].setChunk(level, 1);
        }
    }
    
    public int what() {
        if (this.size > 0) {
            return this.what.getChunk(this.size - 1);
        }
        return 0;
    }
    
    public int what(final int level) {
        if (level < this.size) {
            return this.what.getChunk(level);
        }
        return 0;
    }
    
    public void setWhat(final int val) {
        if (this.size > 0) {
            this.what.setChunk(this.size - 1, val);
        }
    }
    
    public void setWhat(final int val, final int level) {
        if (level < this.size) {
            this.what.setChunk(level, val);
        }
    }
    
    public int who() {
        if (this.size <= 0) {
            return 0;
        }
        if (this.type > 0) {
            return this.who.getChunk(this.size - 1);
        }
        return this.what.getChunk(this.size - 1);
    }
    
    public int who(final int level) {
        if (level >= this.size) {
            return 0;
        }
        if (this.type > 0) {
            return this.who.getChunk(level);
        }
        return this.what.getChunk(level);
    }
    
    public void setWho(final int val) {
        if (this.size > 0 && this.type > 0) {
            this.who.setChunk(this.size - 1, val);
        }
    }
    
    public void setWho(final int val, final int level) {
        if (level < this.size && this.type > 0) {
            this.who.setChunk(level, val);
        }
    }
    
    @Override
    public String toString() {
        String str = "";
        if (this.type < 3) {
            str = "ChunkStack(what at:= " + this.what() + ", who = " + this.who() + ")";
        }
        else {
            str = "ChunkStack(what at:= " + this.what() + ", who = " + this.who() + ", state = )";
        }
        return str;
    }
}
