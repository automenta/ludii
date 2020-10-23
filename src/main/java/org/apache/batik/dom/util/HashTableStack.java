// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.util;

import java.util.HashMap;

public class HashTableStack
{
    protected Link current;
    
    public HashTableStack() {
        this.current = new Link(null);
    }
    
    public void push() {
        final Link current = this.current;
        ++current.pushCount;
    }
    
    public void pop() {
        if (this.current.pushCount-- == 0) {
            this.current = this.current.next;
        }
    }
    
    public String put(final String s, final String v) {
        if (this.current.pushCount != 0) {
            final Link current = this.current;
            --current.pushCount;
            this.current = new Link(this.current);
        }
        if (s.length() == 0) {
            this.current.defaultStr = v;
        }
        return this.current.table.put(s, v);
    }
    
    public String get(final String s) {
        if (s.length() == 0) {
            return this.current.defaultStr;
        }
        for (Link l = this.current; l != null; l = l.next) {
            final String uri = l.table.get(s);
            if (uri != null) {
                return uri;
            }
        }
        return null;
    }
    
    protected static class Link
    {
        public HashMap table;
        public Link next;
        public String defaultStr;
        public int pushCount;
        
        public Link(final Link n) {
            this.pushCount = 0;
            this.table = new HashMap();
            this.next = n;
            if (this.next != null) {
                this.defaultStr = this.next.defaultStr;
            }
        }
    }
}
