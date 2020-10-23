// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.util;

public class TriplyIndexedTable
{
    protected static final int INITIAL_CAPACITY = 11;
    protected Entry[] table;
    protected int count;
    
    public TriplyIndexedTable() {
        this.table = new Entry[11];
    }
    
    public TriplyIndexedTable(final int c) {
        this.table = new Entry[c];
    }
    
    public int size() {
        return this.count;
    }
    
    public Object put(final Object o1, final Object o2, final Object o3, final Object value) {
        final int hash = this.hashCode(o1, o2, o3) & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        for (Entry e = this.table[index]; e != null; e = e.next) {
            if (e.hash == hash && e.match(o1, o2, o3)) {
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
        final Entry e2 = new Entry(hash, o1, o2, o3, value, this.table[index]);
        this.table[index] = e2;
        return null;
    }
    
    public Object get(final Object o1, final Object o2, final Object o3) {
        final int hash = this.hashCode(o1, o2, o3) & Integer.MAX_VALUE;
        final int index = hash % this.table.length;
        for (Entry e = this.table[index]; e != null; e = e.next) {
            if (e.hash == hash && e.match(o1, o2, o3)) {
                return e.value;
            }
        }
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
    
    protected int hashCode(final Object o1, final Object o2, final Object o3) {
        return ((o1 == null) ? 0 : o1.hashCode()) ^ ((o2 == null) ? 0 : o2.hashCode()) ^ ((o3 == null) ? 0 : o3.hashCode());
    }
    
    protected static class Entry
    {
        public int hash;
        public Object key1;
        public Object key2;
        public Object key3;
        public Object value;
        public Entry next;
        
        public Entry(final int hash, final Object key1, final Object key2, final Object key3, final Object value, final Entry next) {
            this.hash = hash;
            this.key1 = key1;
            this.key2 = key2;
            this.key3 = key3;
            this.value = value;
            this.next = next;
        }
        
        public boolean match(final Object o1, final Object o2, final Object o3) {
            if (this.key1 != null) {
                if (!this.key1.equals(o1)) {
                    return false;
                }
            }
            else if (o1 != null) {
                return false;
            }
            if (this.key2 != null) {
                if (!this.key2.equals(o2)) {
                    return false;
                }
            }
            else if (o2 != null) {
                return false;
            }
            if (this.key3 != null) {
                return this.key3.equals(o3);
            }
            return o3 == null;
        }
    }
}
