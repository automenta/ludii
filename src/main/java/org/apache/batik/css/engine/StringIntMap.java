// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine;

public class StringIntMap
{
    protected Entry[] table;
    protected int count;
    
    public StringIntMap(final int c) {
        this.table = new Entry[c - (c >> 2) + 1];
    }
    
    public int get(final String key) {
        final int hash = key.hashCode() & Integer.MAX_VALUE;
        final int index = hash % this.table.length;
        for (Entry e = this.table[index]; e != null; e = e.next) {
            if (e.hash == hash && e.key.equals(key)) {
                return e.value;
            }
        }
        return -1;
    }
    
    public void put(final String key, final int value) {
        final int hash = key.hashCode() & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        for (Entry e = this.table[index]; e != null; e = e.next) {
            if (e.hash == hash && e.key.equals(key)) {
                e.value = value;
                return;
            }
        }
        final int len = this.table.length;
        if (this.count++ >= len - (len >> 2)) {
            this.rehash();
            index = hash % this.table.length;
        }
        final Entry e2 = new Entry(hash, key, value, this.table[index]);
        this.table[index] = e2;
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
        public final int hash;
        public String key;
        public int value;
        public Entry next;
        
        public Entry(final int hash, final String key, final int value, final Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
