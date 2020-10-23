// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.util;

import org.apache.batik.util.CleanerThread;
import org.w3c.dom.Element;

public class DocumentDescriptor
{
    protected static final int INITIAL_CAPACITY = 101;
    protected Entry[] table;
    protected int count;
    
    public DocumentDescriptor() {
        this.table = new Entry[101];
    }
    
    public int getNumberOfElements() {
        synchronized (this) {
            return this.count;
        }
    }
    
    public int getLocationLine(final Element elt) {
        synchronized (this) {
            final int hash = elt.hashCode() & Integer.MAX_VALUE;
            final int index = hash % this.table.length;
            for (Entry e = this.table[index]; e != null; e = e.next) {
                if (e.hash == hash) {
                    final Object o = e.get();
                    if (o == elt) {
                        return e.locationLine;
                    }
                }
            }
        }
        return 0;
    }
    
    public int getLocationColumn(final Element elt) {
        synchronized (this) {
            final int hash = elt.hashCode() & Integer.MAX_VALUE;
            final int index = hash % this.table.length;
            for (Entry e = this.table[index]; e != null; e = e.next) {
                if (e.hash == hash) {
                    final Object o = e.get();
                    if (o == elt) {
                        return e.locationColumn;
                    }
                }
            }
        }
        return 0;
    }
    
    public void setLocation(final Element elt, final int line, final int col) {
        synchronized (this) {
            final int hash = elt.hashCode() & Integer.MAX_VALUE;
            int index = hash % this.table.length;
            for (Entry e = this.table[index]; e != null; e = e.next) {
                if (e.hash == hash) {
                    final Object o = e.get();
                    if (o == elt) {
                        e.locationLine = line;
                    }
                }
            }
            final int len = this.table.length;
            if (this.count++ >= len - (len >> 2)) {
                this.rehash();
                index = hash % this.table.length;
            }
            final Entry e2 = new Entry(hash, elt, line, col, this.table[index]);
            this.table[index] = e2;
        }
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
    
    protected void removeEntry(final Entry e) {
        synchronized (this) {
            final int hash = e.hash;
            final int index = hash % this.table.length;
            Entry curr = this.table[index];
            Entry prev = null;
            while (curr != e) {
                prev = curr;
                curr = curr.next;
            }
            if (curr == null) {
                return;
            }
            if (prev == null) {
                this.table[index] = curr.next;
            }
            else {
                prev.next = curr.next;
            }
            --this.count;
        }
    }
    
    protected class Entry extends CleanerThread.WeakReferenceCleared
    {
        public int hash;
        public int locationLine;
        public int locationColumn;
        public Entry next;
        
        public Entry(final int hash, final Element element, final int locationLine, final int locationColumn, final Entry next) {
            super(element);
            this.hash = hash;
            this.locationLine = locationLine;
            this.locationColumn = locationColumn;
            this.next = next;
        }
        
        @Override
        public void cleared() {
            DocumentDescriptor.this.removeEntry(this);
        }
    }
}
