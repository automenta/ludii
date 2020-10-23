// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.events;

import org.w3c.dom.events.EventListener;
import java.util.HashMap;
import org.apache.batik.dom.util.IntTable;

public class EventListenerList
{
    protected int n;
    protected Entry head;
    protected IntTable counts;
    protected Entry[] listeners;
    protected HashMap listenersNS;
    
    public EventListenerList() {
        this.counts = new IntTable();
        this.listenersNS = new HashMap();
    }
    
    public void addListener(final String namespaceURI, final Object group, final EventListener listener) {
        for (Entry e = this.head; e != null; e = e.next) {
            if (((namespaceURI != null && namespaceURI.equals(e.namespaceURI)) || (namespaceURI == null && e.namespaceURI == null)) && e.listener == listener) {
                return;
            }
        }
        this.head = new Entry(listener, namespaceURI, group, this.head);
        this.counts.inc(namespaceURI);
        ++this.n;
        this.listeners = null;
        this.listenersNS.remove(namespaceURI);
    }
    
    public void removeListener(final String namespaceURI, final EventListener listener) {
        if (this.head == null) {
            return;
        }
        if (this.head != null && ((namespaceURI != null && namespaceURI.equals(this.head.namespaceURI)) || (namespaceURI == null && this.head.namespaceURI == null)) && listener == this.head.listener) {
            this.head = this.head.next;
        }
        else {
            Entry prev = this.head;
            Entry e;
            for (e = this.head.next; e != null; e = e.next) {
                if (((namespaceURI != null && namespaceURI.equals(e.namespaceURI)) || (namespaceURI == null && e.namespaceURI == null)) && e.listener == listener) {
                    prev.next = e.next;
                    break;
                }
                prev = e;
            }
            if (e == null) {
                return;
            }
        }
        this.counts.dec(namespaceURI);
        --this.n;
        this.listeners = null;
        this.listenersNS.remove(namespaceURI);
    }
    
    public Entry[] getEventListeners() {
        if (this.listeners != null) {
            return this.listeners;
        }
        this.listeners = new Entry[this.n];
        int i = 0;
        for (Entry e = this.head; e != null; e = e.next) {
            this.listeners[i++] = e;
        }
        return this.listeners;
    }
    
    public Entry[] getEventListeners(final String namespaceURI) {
        if (namespaceURI == null) {
            return this.getEventListeners();
        }
        Entry[] ls = this.listenersNS.get(namespaceURI);
        if (ls != null) {
            return ls;
        }
        final int count = this.counts.get(namespaceURI);
        if (count == 0) {
            return null;
        }
        ls = new Entry[count];
        this.listenersNS.put(namespaceURI, ls);
        int i = 0;
        Entry e = this.head;
        while (i < count) {
            if (namespaceURI.equals(e.namespaceURI)) {
                ls[i++] = e;
            }
            e = e.next;
        }
        return ls;
    }
    
    public boolean hasEventListener(final String namespaceURI) {
        if (namespaceURI == null) {
            return this.n != 0;
        }
        return this.counts.get(namespaceURI) != 0;
    }
    
    public int size() {
        return this.n;
    }
    
    public static class Entry
    {
        protected EventListener listener;
        protected String namespaceURI;
        protected Object group;
        protected boolean mark;
        protected Entry next;
        
        public Entry(final EventListener listener, final String namespaceURI, final Object group, final Entry next) {
            this.listener = listener;
            this.namespaceURI = namespaceURI;
            this.group = group;
            this.next = next;
        }
        
        public EventListener getListener() {
            return this.listener;
        }
        
        public Object getGroup() {
            return this.group;
        }
        
        public String getNamespaceURI() {
            return this.namespaceURI;
        }
    }
}
