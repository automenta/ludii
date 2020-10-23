// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.color;

import org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent;
import org.apache.batik.util.SoftReferenceCache;

public class NamedProfileCache extends SoftReferenceCache
{
    static NamedProfileCache theCache;
    
    public static NamedProfileCache getDefaultCache() {
        return NamedProfileCache.theCache;
    }
    
    public NamedProfileCache() {
        super(true);
    }
    
    public synchronized boolean isPresent(final String profileName) {
        return super.isPresentImpl(profileName);
    }
    
    public synchronized boolean isDone(final String profileName) {
        return super.isDoneImpl(profileName);
    }
    
    public synchronized ICCColorSpaceWithIntent request(final String profileName) {
        return (ICCColorSpaceWithIntent)super.requestImpl(profileName);
    }
    
    public synchronized void clear(final String profileName) {
        super.clearImpl(profileName);
    }
    
    public synchronized void put(final String profileName, final ICCColorSpaceWithIntent bi) {
        super.putImpl(profileName, bi);
    }
    
    static {
        NamedProfileCache.theCache = new NamedProfileCache();
    }
}
