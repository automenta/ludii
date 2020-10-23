// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.lang.ref.SoftReference;
import java.lang.ref.ReferenceQueue;

public class SoftDoublyIndexedTable
{
    protected static final int INITIAL_CAPACITY = 11;
    protected Entry[] table;
    protected int count;
    protected ReferenceQueue referenceQueue;
    
    public SoftDoublyIndexedTable() {
        this.referenceQueue = new ReferenceQueue();
        this.table = new Entry[11];
    }
    
    public SoftDoublyIndexedTable(final int c) {
        this.referenceQueue = new ReferenceQueue();
        this.table = new Entry[c];
    }
    
    public int size() {
        return this.count;
    }
    
    public Object get(final Object o1, final Object o2) {
        final int hash = this.hashCode(o1, o2) & Integer.MAX_VALUE;
        final int index = hash % this.table.length;
        for (Entry e = this.table[index]; e != null; e = e.next) {
            if (e.hash == hash && e.match(o1, o2)) {
                return e.get();
            }
        }
        return null;
    }
    
    public Object put(final Object o1, final Object o2, final Object value) {
        this.removeClearedEntries();
        final int hash = this.hashCode(o1, o2) & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e = this.table[index];
        if (e != null) {
            if (e.hash == hash && e.match(o1, o2)) {
                final Object old = e.get();
                this.table[index] = new Entry(hash, o1, o2, value, e.next);
                return old;
            }
            Entry o3 = e;
            for (e = e.next; e != null; e = e.next) {
                if (e.hash == hash && e.match(o1, o2)) {
                    final Object old2 = e.get();
                    e = new Entry(hash, o1, o2, value, e.next);
                    o3.next = e;
                    return old2;
                }
                o3 = e;
            }
        }
        final int len = this.table.length;
        if (this.count++ >= len - (len >> 2)) {
            this.rehash();
            index = hash % this.table.length;
        }
        this.table[index] = new Entry(hash, o1, o2, value, this.table[index]);
        return null;
    }
    
    public void clear() {
        this.table = new Entry[11];
        this.count = 0;
        this.referenceQueue = new ReferenceQueue();
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
    
    protected int hashCode(final Object o1, final Object o2) {
        final int result = (o1 == null) ? 0 : o1.hashCode();
        return result ^ ((o2 == null) ? 0 : o2.hashCode());
    }
    
    protected void removeClearedEntries() {
        Entry e;
        while ((e = (Entry)this.referenceQueue.poll()) != null) {
            final int index = e.hash % this.table.length;
            Entry t = this.table[index];
            if (t == e) {
                this.table[index] = e.next;
            }
            else {
                while (t != null) {
                    final Entry c = t.next;
                    if (c == e) {
                        t.next = e.next;
                        break;
                    }
                    t = c;
                }
            }
            --this.count;
        }
    }
    
    protected class Entry extends SoftReference
    {
        public int hash;
        public Object key1;
        public Object key2;
        public Entry next;
        
        public Entry(final int hash, final Object key1, final Object key2, final Object value, final Entry next) {
            super(value, SoftDoublyIndexedTable.this.referenceQueue);
            this.hash = hash;
            this.key1 = key1;
            this.key2 = key2;
            this.next = next;
        }
        
        public boolean match(final Object o1, final Object o2) {
            if (this.key1 != null) {
                if (!this.key1.equals(o1)) {
                    return false;
                }
            }
            else if (o1 != null) {
                return false;
            }
            if (this.key2 != null) {
                return this.key2.equals(o2);
            }
            return o2 == null;
        }
    }
}
