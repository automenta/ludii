/*
 * Decompiled with CFR 0.150.
 */
package collections;

import math.BitTwiddling;

import java.io.Serializable;

public final class ChunkStack
implements Serializable {
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

    public ChunkStack(int numComponents, int numPlayers, int numStates, int numRotation, int type, boolean hidden) {
        this.type = type;
        this.size = 0;
        int chunkSizeWhat = BitTwiddling.nextPowerOf2(BitTwiddling.bitsRequired(numComponents));
        this.what = new ChunkSet(chunkSizeWhat, 1);
        int chunkSizeWho = BitTwiddling.nextPowerOf2(BitTwiddling.bitsRequired(numPlayers + 1));
        this.who = type > 0 ? new ChunkSet(chunkSizeWho, 1) : null;
        int chunkSizeState = BitTwiddling.nextPowerOf2(BitTwiddling.bitsRequired(numStates));
        this.state = type > 1 ? new ChunkSet(chunkSizeState, 1) : null;
        int chunkSizeRotation = BitTwiddling.nextPowerOf2(BitTwiddling.bitsRequired(numRotation));
        this.rotation = type > 2 ? new ChunkSet(chunkSizeRotation, 1) : null;
        if (hidden) {
            int chunkSizeHidden = BitTwiddling.nextPowerOf2(BitTwiddling.bitsRequired(3));
            this.hidden = new ChunkSet[numPlayers + 1];
            for (int i = 0; i < numPlayers + 1; ++i) {
                this.hidden[i] = new ChunkSet(chunkSizeHidden, 1);
            }
        } else {
            this.hidden = null;
        }
    }

    public ChunkStack(ChunkStack other) {
        this.type = other.type;
        this.size = other.size;
        this.what = other.what == null ? null : other.what.clone();
        this.who = other.who == null ? null : other.who.clone();
        this.state = other.state == null ? null : other.state.clone();
        ChunkSet chunkSet = this.rotation = other.rotation == null ? null : other.rotation.clone();
        if (other.hidden == null) {
            this.hidden = null;
        } else {
            this.hidden = new ChunkSet[other.hidden.length];
            for (int i = 0; i < this.hidden.length; ++i) {
                if (other.hidden[i] == null) continue;
                this.hidden[i] = other.hidden[i].clone();
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

    public int state(int level) {
        if (this.type >= 2 && level < this.size) {
            return this.state.getChunk(level);
        }
        return 0;
    }

    public void setState(int val) {
        if (this.type >= 2 && this.size > 0) {
            this.state.setChunk(this.size - 1, val);
        }
    }

    public void setState(int val, int level) {
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

    public int rotation(int level) {
        if (this.type >= 3 && level < this.size) {
            return this.rotation.getChunk(level);
        }
        return 0;
    }

    public void setRotation(int val) {
        if (this.type >= 3 && this.size > 0) {
            this.rotation.setChunk(this.size - 1, val);
        }
    }

    public void setRotation(int val, int level) {
        if (this.type >= 3 && level < this.size) {
            this.rotation.setChunk(level, val);
        }
    }

    public boolean isInvisible(int pid) {
        if (this.hidden != null && this.size > 0) {
            return this.hidden[pid].getChunk(this.size - 1) == 2;
        }
        return false;
    }

    public boolean isVisible(int pid) {
        if (this.hidden != null && this.size > 0) {
            return this.hidden[pid].getChunk(this.size - 1) == 0;
        }
        return false;
    }

    public boolean isMasked(int pid) {
        if (this.hidden != null && this.size > 0) {
            return this.hidden[pid].getChunk(this.size - 1) == 1;
        }
        return false;
    }

    public boolean isInvisible(int pid, int level) {
        if (this.hidden != null && level < this.size) {
            return this.hidden[pid].getChunk(level) == 2;
        }
        return false;
    }

    public boolean isVisible(int pid, int level) {
        if (this.hidden != null && level < this.size) {
            return this.hidden[pid].getChunk(level) == 0;
        }
        return false;
    }

    public boolean isMasked(int pid, int level) {
        if (this.hidden != null && level < this.size) {
            return this.hidden[pid].getChunk(level) == 1;
        }
        return false;
    }

    public void setInvisible(int pid) {
        if (this.hidden != null && this.size > 0) {
            this.hidden[pid].setChunk(this.size - 1, 2);
        }
    }

    public void setVisible(int pid) {
        if (this.hidden != null && this.size > 0) {
            this.hidden[pid].setChunk(this.size - 1, 0);
        }
    }

    public void setMasked(int pid) {
        if (this.hidden != null && this.size > 0) {
            this.hidden[pid].setChunk(this.size - 1, 1);
        }
    }

    public void setInvisible(int pid, int level) {
        if (this.hidden != null && level < this.size) {
            this.hidden[pid].setChunk(level, 2);
        }
    }

    public void setVisible(int pid, int level) {
        if (this.hidden != null && level < this.size) {
            this.hidden[pid].setChunk(level, 0);
        }
    }

    public void setMasked(int pid, int level) {
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

    public int what(int level) {
        if (level < this.size) {
            return this.what.getChunk(level);
        }
        return 0;
    }

    public void setWhat(int val) {
        if (this.size > 0) {
            this.what.setChunk(this.size - 1, val);
        }
    }

    public void setWhat(int val, int level) {
        if (level < this.size) {
            this.what.setChunk(level, val);
        }
    }

    public int who() {
        if (this.size > 0) {
            if (this.type > 0) {
                return this.who.getChunk(this.size - 1);
            }
            return this.what.getChunk(this.size - 1);
        }
        return 0;
    }

    public int who(int level) {
        if (level < this.size) {
            if (this.type > 0) {
                return this.who.getChunk(level);
            }
            return this.what.getChunk(level);
        }
        return 0;
    }

    public void setWho(int val) {
        if (this.size > 0 && this.type > 0) {
            this.who.setChunk(this.size - 1, val);
        }
    }

    public void setWho(int val, int level) {
        if (level < this.size && this.type > 0) {
            this.who.setChunk(level, val);
        }
    }

    public String toString() {
        String str = "";
        str = this.type < 3 ? "ChunkStack(what at:= " + this.what() + ", who = " + this.who() + ")" : "ChunkStack(what at:= " + this.what() + ", who = " + this.who() + ", state = )";
        return str;
    }
}

