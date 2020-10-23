// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.spi;

import org.apache.batik.util.Service;
import java.util.Collections;
import java.util.Collection;
import java.util.ListIterator;
import java.io.StreamCorruptedException;
import java.io.IOException;
import java.util.Iterator;
import java.io.BufferedInputStream;
import java.io.InputStream;
import org.apache.batik.ext.awt.image.renderable.ProfileRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent;
import org.apache.batik.util.ParsedURL;
import java.util.LinkedList;
import org.apache.batik.ext.awt.image.URLImageCache;
import java.util.List;

public class ImageTagRegistry implements ErrorConstants
{
    List entries;
    List extensions;
    List mimeTypes;
    URLImageCache rawCache;
    URLImageCache imgCache;
    static ImageTagRegistry registry;
    static BrokenLinkProvider defaultProvider;
    static BrokenLinkProvider brokenLinkProvider;
    
    public ImageTagRegistry() {
        this(null, null);
    }
    
    public ImageTagRegistry(URLImageCache rawCache, URLImageCache imgCache) {
        this.entries = new LinkedList();
        this.extensions = null;
        this.mimeTypes = null;
        if (rawCache == null) {
            rawCache = new URLImageCache();
        }
        if (imgCache == null) {
            imgCache = new URLImageCache();
        }
        this.rawCache = rawCache;
        this.imgCache = imgCache;
    }
    
    public void flushCache() {
        this.rawCache.flush();
        this.imgCache.flush();
    }
    
    public void flushImage(final ParsedURL purl) {
        this.rawCache.clear(purl);
        this.imgCache.clear(purl);
    }
    
    public Filter checkCache(final ParsedURL purl, final ICCColorSpaceWithIntent colorSpace) {
        final boolean needRawData = colorSpace != null;
        Filter ret = null;
        URLImageCache cache;
        if (needRawData) {
            cache = this.rawCache;
        }
        else {
            cache = this.imgCache;
        }
        ret = cache.request(purl);
        if (ret == null) {
            cache.clear(purl);
            return null;
        }
        if (colorSpace != null) {
            ret = new ProfileRable(ret, colorSpace);
        }
        return ret;
    }
    
    public Filter readURL(final ParsedURL purl) {
        return this.readURL(null, purl, null, true, true);
    }
    
    public Filter readURL(final ParsedURL purl, final ICCColorSpaceWithIntent colorSpace) {
        return this.readURL(null, purl, colorSpace, true, true);
    }
    
    public Filter readURL(InputStream is, final ParsedURL purl, final ICCColorSpaceWithIntent colorSpace, final boolean allowOpenStream, final boolean returnBrokenLink) {
        if (is != null && !is.markSupported()) {
            is = new BufferedInputStream(is);
        }
        final boolean needRawData = colorSpace != null;
        Filter ret = null;
        URLImageCache cache = null;
        if (purl != null) {
            if (needRawData) {
                cache = this.rawCache;
            }
            else {
                cache = this.imgCache;
            }
            ret = cache.request(purl);
            if (ret != null) {
                if (colorSpace != null) {
                    ret = new ProfileRable(ret, colorSpace);
                }
                return ret;
            }
        }
        boolean openFailed = false;
        final List mimeTypes = this.getRegisteredMimeTypes();
        for (final RegistryEntry re : this.entries) {
            if (re instanceof URLRegistryEntry) {
                if (purl == null) {
                    continue;
                }
                if (!allowOpenStream) {
                    continue;
                }
                final URLRegistryEntry ure = (URLRegistryEntry)re;
                if (!ure.isCompatibleURL(purl)) {
                    continue;
                }
                ret = ure.handleURL(purl, needRawData);
                if (ret != null) {
                    break;
                }
                continue;
            }
            else {
                if (!(re instanceof StreamRegistryEntry)) {
                    continue;
                }
                final StreamRegistryEntry sre = (StreamRegistryEntry)re;
                if (openFailed) {
                    continue;
                }
                try {
                    if (is == null) {
                        if (purl == null || !allowOpenStream) {
                            break;
                        }
                        try {
                            is = purl.openStream(mimeTypes.iterator());
                        }
                        catch (IOException ioe) {
                            openFailed = true;
                            continue;
                        }
                        if (!is.markSupported()) {
                            is = new BufferedInputStream(is);
                        }
                    }
                    if (!sre.isCompatibleStream(is)) {
                        continue;
                    }
                    ret = sre.handleStream(is, purl, needRawData);
                    if (ret != null) {
                        break;
                    }
                    continue;
                }
                catch (StreamCorruptedException sce) {
                    is = null;
                }
            }
        }
        if (cache != null) {
            cache.put(purl, ret);
        }
        if (ret == null) {
            if (!returnBrokenLink) {
                return null;
            }
            if (openFailed) {
                return getBrokenLinkImage(this, "url.unreachable", null);
            }
            return getBrokenLinkImage(this, "url.uninterpretable", null);
        }
        else {
            if (BrokenLinkProvider.hasBrokenLinkProperty(ret)) {
                return returnBrokenLink ? ret : null;
            }
            if (colorSpace != null) {
                ret = new ProfileRable(ret, colorSpace);
            }
            return ret;
        }
    }
    
    public Filter readStream(final InputStream is) {
        return this.readStream(is, null);
    }
    
    public Filter readStream(InputStream is, final ICCColorSpaceWithIntent colorSpace) {
        if (!is.markSupported()) {
            is = new BufferedInputStream(is);
        }
        final boolean needRawData = colorSpace != null;
        Filter ret = null;
        for (final Object entry : this.entries) {
            final RegistryEntry re = (RegistryEntry)entry;
            if (!(re instanceof StreamRegistryEntry)) {
                continue;
            }
            final StreamRegistryEntry sre = (StreamRegistryEntry)re;
            try {
                if (!sre.isCompatibleStream(is)) {
                    continue;
                }
                ret = sre.handleStream(is, null, needRawData);
                if (ret != null) {
                    break;
                }
                continue;
            }
            catch (StreamCorruptedException sce) {
                break;
            }
        }
        if (ret == null) {
            return getBrokenLinkImage(this, "stream.unreadable", null);
        }
        if (colorSpace != null && !BrokenLinkProvider.hasBrokenLinkProperty(ret)) {
            ret = new ProfileRable(ret, colorSpace);
        }
        return ret;
    }
    
    public synchronized void register(final RegistryEntry newRE) {
        final float priority = newRE.getPriority();
        final ListIterator li = this.entries.listIterator();
        while (li.hasNext()) {
            final RegistryEntry re = li.next();
            if (re.getPriority() > priority) {
                li.previous();
                li.add(newRE);
                return;
            }
        }
        li.add(newRE);
        this.extensions = null;
        this.mimeTypes = null;
    }
    
    public synchronized List getRegisteredExtensions() {
        if (this.extensions != null) {
            return this.extensions;
        }
        this.extensions = new LinkedList();
        for (final Object entry : this.entries) {
            final RegistryEntry re = (RegistryEntry)entry;
            this.extensions.addAll(re.getStandardExtensions());
        }
        return this.extensions = Collections.unmodifiableList((List<?>)this.extensions);
    }
    
    public synchronized List getRegisteredMimeTypes() {
        if (this.mimeTypes != null) {
            return this.mimeTypes;
        }
        this.mimeTypes = new LinkedList();
        for (final Object entry : this.entries) {
            final RegistryEntry re = (RegistryEntry)entry;
            this.mimeTypes.addAll(re.getMimeTypes());
        }
        return this.mimeTypes = Collections.unmodifiableList((List<?>)this.mimeTypes);
    }
    
    public static synchronized ImageTagRegistry getRegistry() {
        if (ImageTagRegistry.registry != null) {
            return ImageTagRegistry.registry;
        }
        (ImageTagRegistry.registry = new ImageTagRegistry()).register(new JDKRegistryEntry());
        final Iterator iter = Service.providers(RegistryEntry.class);
        while (iter.hasNext()) {
            final RegistryEntry re = iter.next();
            ImageTagRegistry.registry.register(re);
        }
        return ImageTagRegistry.registry;
    }
    
    public static synchronized Filter getBrokenLinkImage(final Object base, final String code, final Object[] params) {
        Filter ret = null;
        if (ImageTagRegistry.brokenLinkProvider != null) {
            ret = ImageTagRegistry.brokenLinkProvider.getBrokenLinkImage(base, code, params);
        }
        if (ret == null) {
            ret = ImageTagRegistry.defaultProvider.getBrokenLinkImage(base, code, params);
        }
        return ret;
    }
    
    public static synchronized void setBrokenLinkProvider(final BrokenLinkProvider provider) {
        ImageTagRegistry.brokenLinkProvider = provider;
    }
    
    static {
        ImageTagRegistry.registry = null;
        ImageTagRegistry.defaultProvider = new DefaultBrokenLinkProvider();
        ImageTagRegistry.brokenLinkProvider = null;
    }
}
