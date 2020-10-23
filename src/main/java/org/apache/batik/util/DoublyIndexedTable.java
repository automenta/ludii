// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.util.NoSuchElementException;
import java.util.Iterator;

public class DoublyIndexedTable
{
    protected int initialCapacity;
    protected Entry[] table;
    protected int count;
    
    public DoublyIndexedTable() {
        this(16);
    }
    
    public DoublyIndexedTable(final int c) {
        this.initialCapacity = c;
        this.table = new Entry[c];
    }
    
    public DoublyIndexedTable(final DoublyIndexedTable other) {
        this.initialCapacity = other.initialCapacity;
        this.table = new Entry[other.table.length];
        for (int i = 0; i < other.table.length; ++i) {
            Entry newE = null;
            for (Entry e = other.table[i]; e != null; e = e.next) {
                newE = new Entry(e.hash, e.key1, e.key2, e.value, newE);
            }
            this.table[i] = newE;
        }
        this.count = other.count;
    }
    
    public int size() {
        return this.count;
    }
    
    public Object put(final Object o1, final Object o2, final Object value) {
        final int hash = this.hashCode(o1, o2) & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        for (Entry e = this.table[index]; e != null; e = e.next) {
            if (e.hash == hash && e.match(o1, o2)) {
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
        final Entry e2 = new Entry(hash, o1, o2, value, this.table[index]);
        this.table[index] = e2;
        return null;
    }
    
    public Object get(final Object o1, final Object o2) {
        final int hash = this.hashCode(o1, o2) & Integer.MAX_VALUE;
        final int index = hash % this.table.length;
        for (Entry e = this.table[index]; e != null; e = e.next) {
            if (e.hash == hash && e.match(o1, o2)) {
                return e.value;
            }
        }
        return null;
    }
    
    public Object remove(final Object o1, final Object o2) {
        final int hash = this.hashCode(o1, o2) & Integer.MAX_VALUE;
        final int index = hash % this.table.length;
        Entry e = this.table[index];
        if (e == null) {
            return null;
        }
        if (e.hash == hash && e.match(o1, o2)) {
            this.table[index] = e.next;
            --this.count;
            return e.value;
        }
        Entry prev = e;
        for (e = e.next; e != null; e = e.next) {
            if (e.hash == hash && e.match(o1, o2)) {
                prev.next = e.next;
                --this.count;
                return e.value;
            }
            prev = e;
        }
        return null;
    }
    
    public Object[] getValuesArray() {
        final Object[] values = new Object[this.count];
        int i = 0;
        for (Entry e : this.table) {
            final Entry aTable = e;
            while (e != null) {
                values[i++] = e.value;
                e = e.next;
            }
        }
        return values;
    }
    
    public void clear() {
        this.table = new Entry[this.initialCapacity];
        this.count = 0;
    }
    
    public Iterator iterator() {
        return new TableIterator();
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
    
    public static class Entry
    {
        protected int hash;
        protected Object key1;
        protected Object key2;
        protected Object value;
        protected Entry next;
        
        public Entry(final int hash, final Object key1, final Object key2, final Object value, final Entry next) {
            this.hash = hash;
            this.key1 = key1;
            this.key2 = key2;
            this.value = value;
            this.next = next;
        }
        
        public Object getKey1() {
            return this.key1;
        }
        
        public Object getKey2() {
            return this.key2;
        }
        
        public Object getValue() {
            return this.value;
        }
        
        protected boolean match(final Object o1, final Object o2) {
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
    
    protected class TableIterator implements Iterator
    {
        private int nextIndex;
        private Entry nextEntry;
        private boolean finished;
        
        public TableIterator() {
            while (this.nextIndex < DoublyIndexedTable.this.table.length) {
                this.nextEntry = DoublyIndexedTable.this.table[this.nextIndex];
                if (this.nextEntry != null) {
                    break;
                }
                ++this.nextIndex;
            }
            this.finished = (this.nextEntry == null);
        }
        
        @Override
        public boolean hasNext() {
            return !this.finished;
        }
        
        @Override
        public Object next() {
            if (this.finished) {
                throw new NoSuchElementException();
            }
            final Entry ret = this.nextEntry;
            this.findNext();
            return ret;
        }
        
        protected void findNext() {
            this.nextEntry = this.nextEntry.next;
            if (this.nextEntry == null) {
                ++this.nextIndex;
                while (this.nextIndex < DoublyIndexedTable.this.table.length) {
                    this.nextEntry = DoublyIndexedTable.this.table[this.nextIndex];
                    if (this.nextEntry != null) {
                        break;
                    }
                    ++this.nextIndex;
                }
            }
            this.finished = (this.nextEntry == null);
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
