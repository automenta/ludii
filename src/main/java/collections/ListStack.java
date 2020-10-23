/*
 * Decompiled with CFR 0.150.
 */
package collections;

import gnu.trove.list.array.TIntArrayList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ListStack
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int TYPE_DEFAULT_STATE = 0;
    public static final int TYPE_PLAYER_STATE = 1;
    public static final int TYPE_INDEX_STATE = 2;
    public static final int TYPE_INDEX_ROTATION = 4;
    public static final int TYPE_INDEX_LOCAL_STATE = 3;
    protected final int type;
    protected final TIntArrayList what;
    protected final TIntArrayList who;
    protected final TIntArrayList state;
    protected final TIntArrayList rotation;
    protected final List<TIntArrayList> hidden;
    protected int size;

    public ListStack(int type) {
        this.type = type;
        this.size = 0;
        this.what = new TIntArrayList();
        this.hidden = new ArrayList<>();
        this.who = type > 1 ? new TIntArrayList() : null;
        this.state = type > 2 ? new TIntArrayList() : null;
        this.rotation = type > 3 ? new TIntArrayList() : null;
    }

    public ListStack(ListStack other) {
        this.type = other.type;
        this.size = other.size;
        this.what = other.what == null ? null : new TIntArrayList(other.what);
        this.who = other.who == null ? null : new TIntArrayList(other.who);
        this.state = other.state == null ? null : new TIntArrayList(other.state);
        this.rotation = other.rotation == null ? null : new TIntArrayList(other.rotation);
        this.hidden = other.hidden == null ? null : new ArrayList<>(other.hidden);
    }

    public TIntArrayList whatChunkSet() {
        return this.what;
    }

    public TIntArrayList whoChunkSet() {
        return this.who;
    }

    public TIntArrayList stateChunkSet() {
        return this.state;
    }

    public TIntArrayList rotationChunkSet() {
        return this.rotation;
    }

    public List<TIntArrayList> hiddenChunkSet() {
        if (this.hidden == null) {
            return null;
        }
        return Collections.unmodifiableList(this.hidden);
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
            this.what.removeAt(this.what.size() - 1);
            if (this.who != null) {
                this.who.removeAt(this.who.size() - 1);
                if (this.state != null) {
                    this.state.removeAt(this.state.size() - 1);
                }
            }
            this.hidden.remove(this.hidden.size() - 1);
        }
    }

    public int state() {
        if (this.type > 2 && this.size > 0) {
            return this.state.getQuick(this.size - 1);
        }
        return 0;
    }

    public int state(int level) {
        if (this.type > 2 && level < this.size && level < this.state.size()) {
            return this.state.getQuick(level);
        }
        return 0;
    }

    public void setState(int val) {
        if (this.type > 2) {
            this.state.add(val);
        }
    }

    public void setState(int val, int level) {
        if (this.type > 2 && level < this.size) {
            this.state.set(level, val);
        }
    }

    public int rotation() {
        if (this.type > 3 && this.size > 0) {
            return this.rotation.getQuick(this.size - 1);
        }
        return 0;
    }

    public int rotation(int level) {
        if (this.type > 3 && level < this.size && level < this.rotation.size()) {
            return this.rotation.getQuick(level);
        }
        return 0;
    }

    public void setRotation(int val) {
        if (this.type > 3) {
            this.rotation.add(val);
        }
    }

    public void setRotation(int val, int level) {
        if (this.type > 3 && level < this.size) {
            this.rotation.set(level, val);
        }
    }

    public int what() {
        if (this.size > 0 && this.size - 1 < this.what.size()) {
            return this.what.getQuick(this.size - 1);
        }
        return 0;
    }

    public int what(int level) {
        if (level < this.size && level < this.what.size()) {
            return this.what.getQuick(level);
        }
        return 0;
    }

    public void setWhat(int val) {
        this.what.add(val);
    }

    public void setWhat(int val, int level) {
        if (level < this.size) {
            this.what.set(level, val);
        }
    }

    public void insertWhat(int val, int level) {
        if (level < this.size) {
            this.what.insert(level, val);
        }
    }

    public int who() {
        if (this.size > 0 && this.size - 1 < this.who.size()) {
            if (this.type > 0) {
                return this.who.getQuick(this.size - 1);
            }
            return this.what.getQuick(this.size - 1);
        }
        return 0;
    }

    public int who(int level) {
        if (level < this.size && level < this.what.size()) {
            if (this.type > 0) {
                return this.who.getQuick(level);
            }
            return this.what.getQuick(level);
        }
        return 0;
    }

    public void setWho(int val) {
        if (this.type > 0) {
            this.who.add(val);
        }
    }

    public void setWho(int val, int level) {
        if (level < this.size && this.type > 0) {
            this.who.set(level, val);
        }
    }

    public void insertWho(int val, int level) {
        if (level < this.size && this.type > 0) {
            this.who.insert(level, val);
        }
    }

    public boolean isInvisible(int player) {
        if (this.size > 0 && this.size - 1 < this.hidden.size()) {
            return this.hidden.get(this.size - 1).getQuick(player) == 2;
        }
        return false;
    }

    public boolean isVisible(int player) {
        if (this.size > 0 && this.size - 1 < this.hidden.size()) {
            return this.hidden.get(this.size - 1).getQuick(player) == 0;
        }
        return false;
    }

    public boolean isMasked(int player) {
        if (this.size > 0 && this.size - 1 < this.hidden.size()) {
            return this.hidden.get(this.size - 1).getQuick(player) == 1;
        }
        return false;
    }

    public TIntArrayList hiddenArray(int level) {
        if (level < this.size && level < this.hidden.size()) {
            return this.hidden.get(level);
        }
        return null;
    }

    public boolean isInvisible(int level, int player) {
        if (level <= this.size && level < this.hidden.size()) {
            return this.hidden.get(level).getQuick(player) == 2;
        }
        return false;
    }

    public boolean isVisible(int level, int player) {
        if (level <= this.size && level < this.hidden.size()) {
            return this.hidden.get(level).getQuick(player) == 0;
        }
        return false;
    }

    public boolean isMasked(int level, int player) {
        if (level <= this.size && level < this.hidden.size()) {
            return this.hidden.get(level).getQuick(player) == 1;
        }
        return false;
    }

    public void addHidden(TIntArrayList hiddenInfo) {
        this.hidden.add(hiddenInfo);
    }

    public void setHidden(TIntArrayList hiddenInfo, int level) {
        if (level < this.size && level < this.hidden.size()) {
            this.hidden.set(level, hiddenInfo);
        }
    }

    public void insertHidden(TIntArrayList hiddenInfo, int level) {
        if (level < this.size && level < this.hidden.size()) {
            this.hidden.add(level, hiddenInfo);
        }
    }

    public void setInvisible(int player) {
        if (this.size > 0 && this.size - 1 < this.hidden.size()) {
            this.hidden.get(this.size - 1).set(player, 2);
        }
    }

    public void setMasked(int player) {
        if (this.size > 0 && this.size - 1 < this.hidden.size()) {
            this.hidden.get(this.size - 1).set(player, 1);
        }
    }

    public void setVisible(int player) {
        if (this.size > 0 && this.size - 1 < this.hidden.size()) {
            this.hidden.get(this.size - 1).set(player, 0);
        }
    }

    public void setInvisible(int level, int player) {
        if (level < this.size && level < this.hidden.size()) {
            this.hidden.get(level).set(player, 2);
        }
    }

    public void setMasked(int level, int player) {
        if (level < this.size && level < this.hidden.size()) {
            this.hidden.get(level).set(player, 1);
        }
    }

    public void setVisible(int level, int player) {
        if (level < this.size && level < this.hidden.size()) {
            this.hidden.get(level).set(player, 0);
        }
    }

    public String toString() {
        String str = "";
        str = this.type < 3 ? "ListStack(what at:= " + this.what + ", who = " + this.who + ")" : "ListStack(what at:= " + this.what + ", who = " + this.who + ", state = )";
        return str;
    }
}

