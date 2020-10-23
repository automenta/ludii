// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.font;

import java.lang.ref.SoftReference;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.lang.ref.ReferenceQueue;

public class AWTGlyphGeometryCache
{
    protected static final int INITIAL_CAPACITY = 71;
    protected Entry[] table;
    protected int count;
    protected ReferenceQueue referenceQueue;
    
    public AWTGlyphGeometryCache() {
        this.referenceQueue = new ReferenceQueue();
        this.table = new Entry[71];
    }
    
    public AWTGlyphGeometryCache(final int c) {
        this.referenceQueue = new ReferenceQueue();
        this.table = new Entry[c];
    }
    
    public int size() {
        return this.count;
    }
    
    public Value get(final char c) {
        final int hash = this.hashCode(c) & Integer.MAX_VALUE;
        final int index = hash % this.table.length;
        for (Entry e = this.table[index]; e != null; e = e.next) {
            if (e.hash == hash && e.match(c)) {
                return e.get();
            }
        }
        return null;
    }
    
    public Value put(final char c, final Value value) {
        this.removeClearedEntries();
        final int hash = this.hashCode(c) & Integer.MAX_VALUE;
        int index = hash % this.table.length;
        Entry e = this.table[index];
        if (e != null) {
            if (e.hash == hash && e.match(c)) {
                final Object old = e.get();
                this.table[index] = new Entry(hash, c, value, e.next);
                return (Value)old;
            }
            Entry o = e;
            for (e = e.next; e != null; e = e.next) {
                if (e.hash == hash && e.match(c)) {
                    final Object old2 = e.get();
                    e = new Entry(hash, c, value, e.next);
                    o.next = e;
                    return (Value)old2;
                }
                o = e;
            }
        }
        final int len = this.table.length;
        if (this.count++ >= len - (len >> 2)) {
            this.rehash();
            index = hash % this.table.length;
        }
        this.table[index] = new Entry(hash, c, value, this.table[index]);
        return null;
    }
    
    public void clear() {
        this.table = new Entry[71];
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
    
    protected int hashCode(final char c) {
        return c;
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
    
    public static class Value
    {
        protected Shape outline;
        protected Rectangle2D gmB;
        protected Rectangle2D outlineBounds;
        
        public Value(final Shape outline, final Rectangle2D gmB) {
            this.outline = outline;
            this.outlineBounds = outline.getBounds2D();
            this.gmB = gmB;
        }
        
        public Shape getOutline() {
            return this.outline;
        }
        
        public Rectangle2D getBounds2D() {
            return this.gmB;
        }
        
        public Rectangle2D getOutlineBounds2D() {
            return this.outlineBounds;
        }
    }
    
    protected class Entry extends SoftReference
    {
        public int hash;
        public char c;
        public Entry next;
        
        public Entry(final int hash, final char c, final Value value, final Entry next) {
            super(value, AWTGlyphGeometryCache.this.referenceQueue);
            this.hash = hash;
            this.c = c;
            this.next = next;
        }
        
        public boolean match(final char o2) {
            return this.c == o2;
        }
    }
}
