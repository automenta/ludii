// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class TranscodingHints extends HashMap
{
    public TranscodingHints() {
        this(null);
    }
    
    public TranscodingHints(final Map init) {
        super(7);
        if (init != null) {
            this.putAll(init);
        }
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return super.containsKey(key);
    }
    
    @Override
    public Object get(final Object key) {
        return super.get(key);
    }
    
    @Override
    public Object put(final Object key, final Object value) {
        if (!((Key)key).isCompatibleValue(value)) {
            throw new IllegalArgumentException(value + " incompatible with " + key);
        }
        return super.put(key, value);
    }
    
    @Override
    public Object remove(final Object key) {
        return super.remove(key);
    }
    
    public void putAll(final TranscodingHints hints) {
        super.putAll(hints);
    }
    
    @Override
    public void putAll(final Map m) {
        if (m instanceof TranscodingHints) {
            this.putAll((TranscodingHints)m);
        }
        else {
            for (final Object o : m.entrySet()) {
                final Map.Entry entry = (Map.Entry)o;
                this.put(entry.getKey(), entry.getValue());
            }
        }
    }
    
    public abstract static class Key
    {
        protected Key() {
        }
        
        public abstract boolean isCompatibleValue(final Object p0);
    }
}
