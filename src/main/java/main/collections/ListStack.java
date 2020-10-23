// 
// Decompiled by Procyon v0.5.36
// 

package main.collections;

import gnu.trove.list.array.TIntArrayList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ListStack implements Serializable
{
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
    
    public ListStack(final int type) {
        this.type = type;
        this.size = 0;
        this.what = new TIntArrayList();
        this.hidden = new ArrayList<>();
        if (type > 1) {
            this.who = new TIntArrayList();
        }
        else {
            this.who = null;
        }
        if (type > 2) {
            this.state = new TIntArrayList();
        }
        else {
            this.state = null;
        }
        if (type > 3) {
            this.rotation = new TIntArrayList();
        }
        else {
            this.rotation = null;
        }
    }
    
    public ListStack(final ListStack other) {
        this.type = other.type;
        this.size = other.size;
        this.what = ((other.what == null) ? null : new TIntArrayList(other.what));
        this.who = ((other.who == null) ? null : new TIntArrayList(other.who));
        this.state = ((other.state == null) ? null : new TIntArrayList(other.state));
        this.rotation = ((other.rotation == null) ? null : new TIntArrayList(other.rotation));
        this.hidden = ((other.hidden == null) ? null : new ArrayList<>(other.hidden));
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
    
    public int state(final int level) {
        if (this.type > 2 && level < this.size && level < this.state.size()) {
            return this.state.getQuick(level);
        }
        return 0;
    }
    
    public void setState(final int val) {
        if (this.type > 2) {
            this.state.add(val);
        }
    }
    
    public void setState(final int val, final int level) {
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
    
    public int rotation(final int level) {
        if (this.type > 3 && level < this.size && level < this.rotation.size()) {
            return this.rotation.getQuick(level);
        }
        return 0;
    }
    
    public void setRotation(final int val) {
        if (this.type > 3) {
            this.rotation.add(val);
        }
    }
    
    public void setRotation(final int val, final int level) {
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
    
    public int what(final int level) {
        if (level < this.size && level < this.what.size()) {
            return this.what.getQuick(level);
        }
        return 0;
    }
    
    public void setWhat(final int val) {
        this.what.add(val);
    }
    
    public void setWhat(final int val, final int level) {
        if (level < this.size) {
            this.what.set(level, val);
        }
    }
    
    public void insertWhat(final int val, final int level) {
        if (level < this.size) {
            this.what.insert(level, val);
        }
    }
    
    public int who() {
        if (this.size <= 0 || this.size - 1 >= this.who.size()) {
            return 0;
        }
        if (this.type > 0) {
            return this.who.getQuick(this.size - 1);
        }
        return this.what.getQuick(this.size - 1);
    }
    
    public int who(final int level) {
        if (level >= this.size || level >= this.what.size()) {
            return 0;
        }
        if (this.type > 0) {
            return this.who.getQuick(level);
        }
        return this.what.getQuick(level);
    }
    
    public void setWho(final int val) {
        if (this.type > 0) {
            this.who.add(val);
        }
    }
    
    public void setWho(final int val, final int level) {
        if (level < this.size && this.type > 0) {
            this.who.set(level, val);
        }
    }
    
    public void insertWho(final int val, final int level) {
        if (level < this.size && this.type > 0) {
            this.who.insert(level, val);
        }
    }
    
    public boolean isInvisible(final int player) {
        return this.size > 0 && this.size - 1 < this.hidden.size() && this.hidden.get(this.size - 1).getQuick(player) == 2;
    }
    
    public boolean isVisible(final int player) {
        return this.size > 0 && this.size - 1 < this.hidden.size() && this.hidden.get(this.size - 1).getQuick(player) == 0;
    }
    
    public boolean isMasked(final int player) {
        return this.size > 0 && this.size - 1 < this.hidden.size() && this.hidden.get(this.size - 1).getQuick(player) == 1;
    }
    
    public TIntArrayList hiddenArray(final int level) {
        if (level < this.size && level < this.hidden.size()) {
            return this.hidden.get(level);
        }
        return null;
    }
    
    public boolean isInvisible(final int level, final int player) {
        return level <= this.size && level < this.hidden.size() && this.hidden.get(level).getQuick(player) == 2;
    }
    
    public boolean isVisible(final int level, final int player) {
        return level <= this.size && level < this.hidden.size() && this.hidden.get(level).getQuick(player) == 0;
    }
    
    public boolean isMasked(final int level, final int player) {
        return level <= this.size && level < this.hidden.size() && this.hidden.get(level).getQuick(player) == 1;
    }
    
    public void addHidden(final TIntArrayList hiddenInfo) {
        this.hidden.add(hiddenInfo);
    }
    
    public void setHidden(final TIntArrayList hiddenInfo, final int level) {
        if (level < this.size && level < this.hidden.size()) {
            this.hidden.set(level, hiddenInfo);
        }
    }
    
    public void insertHidden(final TIntArrayList hiddenInfo, final int level) {
        if (level < this.size && level < this.hidden.size()) {
            this.hidden.add(level, hiddenInfo);
        }
    }
    
    public void setInvisible(final int player) {
        if (this.size > 0 && this.size - 1 < this.hidden.size()) {
            this.hidden.get(this.size - 1).set(player, 2);
        }
    }
    
    public void setMasked(final int player) {
        if (this.size > 0 && this.size - 1 < this.hidden.size()) {
            this.hidden.get(this.size - 1).set(player, 1);
        }
    }
    
    public void setVisible(final int player) {
        if (this.size > 0 && this.size - 1 < this.hidden.size()) {
            this.hidden.get(this.size - 1).set(player, 0);
        }
    }
    
    public void setInvisible(final int level, final int player) {
        if (level < this.size && level < this.hidden.size()) {
            this.hidden.get(level).set(player, 2);
        }
    }
    
    public void setMasked(final int level, final int player) {
        if (level < this.size && level < this.hidden.size()) {
            this.hidden.get(level).set(player, 1);
        }
    }
    
    public void setVisible(final int level, final int player) {
        if (level < this.size && level < this.hidden.size()) {
            this.hidden.get(level).set(player, 0);
        }
    }
    
    @Override
    public String toString() {
        String str = "";
        if (this.type < 3) {
            str = "ListStack(what at:= " + this.what + ", who = " + this.who + ")";
        }
        else {
            str = "ListStack(what at:= " + this.what + ", who = " + this.who + ", state = )";
        }
        return str;
    }
}
