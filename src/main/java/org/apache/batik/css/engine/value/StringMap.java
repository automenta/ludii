// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value;

public class StringMap
{
    protected static final int INITIAL_CAPACITY = 11;
    protected Entry[] table;
    protected int count;
    
    public StringMap() {
        this.table = new Entry[11];
    }
    
    public StringMap(final StringMap t) {
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
    
    public Object get(final String key) {
        final int hash = key.hashCode() & Integer.MAX_VALUE;
        final int index = hash % this.table.length;
        for (Entry e = this.table[index]; e != null; e = e.next) {
            if (e.hash == hash && e.key == key) {
                return e.value;
            }
        }
        return null;
    }
    
    public Object put(final String key, final Object value) {
        final int hash = key.hashCode() & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        for (Entry e = this.table[index]; e != null; e = e.next) {
            if (e.hash == hash && e.key == key) {
                final Object old = e.value;
                e.value = value;
                return old;
            }
        }
        final int len = this.table.length;
        if (this.count++ >= len - (len >> 2)) {
            this.rehash();
            index = hash % this.table.length;
        }
        final Entry e2 = new Entry(hash, key, value, this.table[index]);
        this.table[index] = e2;
        return null;
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
    
    protected static class Entry
    {
        public int hash;
        public String key;
        public Object value;
        public Entry next;
        
        public Entry(final int hash, final String key, final Object value, final Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
