// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class SoftReferenceCache
{
    protected final Map map;
    private final boolean synchronous;
    
    protected SoftReferenceCache() {
        this(false);
    }
    
    protected SoftReferenceCache(final boolean synchronous) {
        this.map = new HashMap();
        this.synchronous = synchronous;
    }
    
    public synchronized void flush() {
        this.map.clear();
        this.notifyAll();
    }
    
    protected final synchronized boolean isPresentImpl(final Object key) {
        if (!this.map.containsKey(key)) {
            return false;
        }
        Object o = this.map.get(key);
        if (o == null) {
            return true;
        }
        final SoftReference sr = (SoftReference)o;
        o = sr.get();
        if (o != null) {
            return true;
        }
        this.clearImpl(key);
        return false;
    }
    
    protected final synchronized boolean isDoneImpl(final Object key) {
        Object o = this.map.get(key);
        if (o == null) {
            return false;
        }
        final SoftReference sr = (SoftReference)o;
        o = sr.get();
        if (o != null) {
            return true;
        }
        this.clearImpl(key);
        return false;
    }
    
    protected final synchronized Object requestImpl(final Object key) {
        if (this.map.containsKey(key)) {
            Object o;
            for (o = this.map.get(key); o == null; o = this.map.get(key)) {
                if (this.synchronous) {
                    return null;
                }
                try {
                    this.wait();
                }
                catch (InterruptedException ex) {}
                if (!this.map.containsKey(key)) {
                    break;
                }
            }
            if (o != null) {
                final SoftReference sr = (SoftReference)o;
                o = sr.get();
                if (o != null) {
                    return o;
                }
            }
        }
        this.map.put(key, null);
        return null;
    }
    
    protected final synchronized void clearImpl(final Object key) {
        this.map.remove(key);
        this.notifyAll();
    }
    
    protected final synchronized void putImpl(final Object key, final Object object) {
        if (this.map.containsKey(key)) {
            final SoftReference ref = new SoftRefKey(object, key);
            this.map.put(key, ref);
            this.notifyAll();
        }
    }
    
    class SoftRefKey extends CleanerThread.SoftReferenceCleared
    {
        Object key;
        
        public SoftRefKey(final Object o, final Object key) {
            super(o);
            this.key = key;
        }
        
        @Override
        public void cleared() {
            final SoftReferenceCache cache = SoftReferenceCache.this;
            if (cache == null) {
                return;
            }
            synchronized (cache) {
                if (!cache.map.containsKey(this.key)) {
                    return;
                }
                final Object o = cache.map.remove(this.key);
                if (this == o) {
                    cache.notifyAll();
                }
                else {
                    cache.map.put(this.key, o);
                }
            }
        }
    }
}
