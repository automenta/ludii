// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.SoftReferenceCache;

public class URLImageCache extends SoftReferenceCache
{
    static URLImageCache theCache;
    
    public static URLImageCache getDefaultCache() {
        return URLImageCache.theCache;
    }
    
    public synchronized boolean isPresent(final ParsedURL purl) {
        return super.isPresentImpl(purl);
    }
    
    public synchronized boolean isDone(final ParsedURL purl) {
        return super.isDoneImpl(purl);
    }
    
    public synchronized Filter request(final ParsedURL purl) {
        return (Filter)super.requestImpl(purl);
    }
    
    public synchronized void clear(final ParsedURL purl) {
        super.clearImpl(purl);
    }
    
    public synchronized void put(final ParsedURL purl, final Filter filt) {
        super.putImpl(purl, filt);
    }
    
    static {
        URLImageCache.theCache = new URLImageCache();
    }
}
