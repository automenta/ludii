// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.spi;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRegistryEntry implements RegistryEntry, ErrorConstants
{
    String name;
    float priority;
    List exts;
    List mimeTypes;
    
    public AbstractRegistryEntry(final String name, final float priority, final String[] exts, final String[] mimeTypes) {
        this.name = name;
        this.priority = priority;
        this.exts = new ArrayList(exts.length);
        for (final String ext : exts) {
            this.exts.add(ext);
        }
        this.exts = Collections.unmodifiableList((List<?>)this.exts);
        this.mimeTypes = new ArrayList(mimeTypes.length);
        for (final String mimeType : mimeTypes) {
            this.mimeTypes.add(mimeType);
        }
        this.mimeTypes = Collections.unmodifiableList((List<?>)this.mimeTypes);
    }
    
    public AbstractRegistryEntry(final String name, final float priority, final String ext, final String mimeType) {
        this.name = name;
        this.priority = priority;
        (this.exts = new ArrayList(1)).add(ext);
        this.exts = Collections.unmodifiableList((List<?>)this.exts);
        (this.mimeTypes = new ArrayList(1)).add(mimeType);
        this.mimeTypes = Collections.unmodifiableList((List<?>)this.mimeTypes);
    }
    
    @Override
    public String getFormatName() {
        return this.name;
    }
    
    @Override
    public List getStandardExtensions() {
        return this.exts;
    }
    
    @Override
    public List getMimeTypes() {
        return this.mimeTypes;
    }
    
    @Override
    public float getPriority() {
        return this.priority;
    }
}
