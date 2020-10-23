// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine;

import org.apache.batik.css.engine.value.Value;

public class StyleDeclaration
{
    protected static final int INITIAL_LENGTH = 8;
    protected Value[] values;
    protected int[] indexes;
    protected boolean[] priorities;
    protected int count;
    
    public StyleDeclaration() {
        this.values = new Value[8];
        this.indexes = new int[8];
        this.priorities = new boolean[8];
    }
    
    public int size() {
        return this.count;
    }
    
    public Value getValue(final int idx) {
        return this.values[idx];
    }
    
    public int getIndex(final int idx) {
        return this.indexes[idx];
    }
    
    public boolean getPriority(final int idx) {
        return this.priorities[idx];
    }
    
    public void remove(final int idx) {
        --this.count;
        final int from = idx + 1;
        final int to = idx;
        final int nCopy = this.count - idx;
        System.arraycopy(this.values, from, this.values, to, nCopy);
        System.arraycopy(this.indexes, from, this.indexes, to, nCopy);
        System.arraycopy(this.priorities, from, this.priorities, to, nCopy);
        this.values[this.count] = null;
        this.indexes[this.count] = 0;
        this.priorities[this.count] = false;
    }
    
    public void put(final int idx, final Value v, final int i, final boolean prio) {
        this.values[idx] = v;
        this.indexes[idx] = i;
        this.priorities[idx] = prio;
    }
    
    public void append(final Value v, final int idx, final boolean prio) {
        if (this.values.length == this.count) {
            final Value[] newval = new Value[this.count * 2];
            final int[] newidx = new int[this.count * 2];
            final boolean[] newprio = new boolean[this.count * 2];
            System.arraycopy(this.values, 0, newval, 0, this.count);
            System.arraycopy(this.indexes, 0, newidx, 0, this.count);
            System.arraycopy(this.priorities, 0, newprio, 0, this.count);
            this.values = newval;
            this.indexes = newidx;
            this.priorities = newprio;
        }
        for (int i = 0; i < this.count; ++i) {
            if (this.indexes[i] == idx) {
                if (prio || this.priorities[i] == prio) {
                    this.values[i] = v;
                    this.priorities[i] = prio;
                }
                return;
            }
        }
        this.values[this.count] = v;
        this.indexes[this.count] = idx;
        this.priorities[this.count] = prio;
        ++this.count;
    }
    
    public String toString(final CSSEngine eng) {
        final StringBuffer sb = new StringBuffer(this.count * 8);
        for (int i = 0; i < this.count; ++i) {
            sb.append(eng.getPropertyName(this.indexes[i]));
            sb.append(": ");
            sb.append(this.values[i]);
            sb.append(";\n");
        }
        return sb.toString();
    }
}
