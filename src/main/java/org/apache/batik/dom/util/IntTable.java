// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.util;

import java.io.Serializable;

public class IntTable implements Serializable
{
    protected static final int INITIAL_CAPACITY = 11;
    protected Entry[] table;
    protected int count;
    
    public IntTable() {
        this.table = new Entry[11];
    }
    
    public IntTable(final int c) {
        this.table = new Entry[c];
    }
    
    public IntTable(final IntTable t) {
        this.count = t.count;
        this.table = new Entry[t.table.length];
        for (int i = 0; i < this.table.length; ++i) {
            Entry e = t.table[i];
            Entry n = null;
            if (e != null) {
                n = new Entry(e.hash, e.key, e.value, null);
                this.table[i] = n;
                for (e = e.next; e != null; e = e.next) {
                    n.next = new Entry(e.hash, e.key, e.value, null);
                    n = n.next;
                }
            }
        }
    }
    
    public int size() {
        return this.count;
    }
    
    protected Entry find(final Object key) {
        return null;
    }
    
    public int get(final Object key) {
        final int hash = (key == null) ? 0 : (key.hashCode() & Integer.MAX_VALUE);
        final int index = hash % this.table.length;
        for (Entry e = this.table[index]; e != null; e = e.next) {
            if (e.hash == hash && ((e.key == null && key == null) || (e.key != null && e.key.equals(key)))) {
                return e.value;
            }
        }
        return 0;
    }
    
    public int put(final Object key, final int value) {
        final int hash = (key == null) ? 0 : (key.hashCode() & Integer.MAX_VALUE);
        int index = hash % this.table.length;
        for (Entry e = this.table[index]; e != null; e = e.next) {
            if (e.hash == hash && ((e.key == null && key == null) || (e.key != null && e.key.equals(key)))) {
                final int old = e.value;
                e.value = value;
                return old;
            }
        }
        final int len = this.table.length;
        if (this.count++ >= len - (len >> 2)) {
            this.rehash();
            index = hash % this.table.length;
        }
        this.table[index] = new Entry(hash, key, value, this.table[index]);
        return 0;
    }
    
    public int inc(final Object key) {
        final int hash = (key == null) ? 0 : (key.hashCode() & Integer.MAX_VALUE);
        int index = hash % this.table.length;
        for (Entry e = this.table[index]; e != null; e = e.next) {
            if (e.hash == hash && ((e.key == null && key == null) || (e.key != null && e.key.equals(key)))) {
                return e.value++;
            }
        }
        final int len = this.table.length;
        if (this.count++ >= len - (len >> 2)) {
            this.rehash();
            index = hash % this.table.length;
        }
        this.table[index] = new Entry(hash, key, 1, this.table[index]);
        return 0;
    }
    
    public int dec(final Object key) {
        final int hash = (key == null) ? 0 : (key.hashCode() & Integer.MAX_VALUE);
        int index = hash % this.table.length;
        for (Entry e = this.table[index]; e != null; e = e.next) {
            if (e.hash == hash && ((e.key == null && key == null) || (e.key != null && e.key.equals(key)))) {
                return e.value--;
            }
        }
        final int len = this.table.length;
        if (this.count++ >= len - (len >> 2)) {
            this.rehash();
            index = hash % this.table.length;
        }
        this.table[index] = new Entry(hash, key, -1, this.table[index]);
        return 0;
    }
    
    public int remove(final Object key) {
        final int hash = (key == null) ? 0 : (key.hashCode() & Integer.MAX_VALUE);
        final int index = hash % this.table.length;
        Entry p = null;
        for (Entry e = this.table[index]; e != null; e = e.next) {
            if (e.hash == hash && ((e.key == null && key == null) || (e.key != null && e.key.equals(key)))) {
                final int result = e.value;
                if (p == null) {
                    this.table[index] = e.next;
                }
                else {
                    p.next = e.next;
                }
                --this.count;
                return result;
            }
            p = e;
        }
        return 0;
    }
    
    public void clear() {
        for (int i = 0; i < this.table.length; ++i) {
            this.table[i] = null;
        }
        this.count = 0;
    }
    
    protected void rehash() {
        final Entry[] oldTable = this.table;
        this.table = new Entry[oldTable.length * 2 + 1];
        for (int i = oldTable.length - 1; i >= 0; --i) {
            Entry e;
            int index;
            for (Entry old = oldTable[i]; old != null; old = old.next, index = e.hash % this.table.length, e.next = this.table[index], this.table[index] = e) {
                e = old;
            }
        }
    }
    
    protected static class Entry implements Serializable
    {
        public int hash;
        public Object key;
        public int value;
        public Entry next;
        
        public Entry(final int hash, final Object key, final int value, final Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
