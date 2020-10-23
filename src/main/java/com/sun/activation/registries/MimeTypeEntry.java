// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.activation.registries;

public class MimeTypeEntry
{
    private final String type;
    private final String extension;
    
    public MimeTypeEntry(final String mime_type, final String file_ext) {
        this.type = mime_type;
        this.extension = file_ext;
    }
    
    public String getMIMEType() {
        return this.type;
    }
    
    public String getFileExtension() {
        return this.extension;
    }
    
    public String toString() {
        return "MIMETypeEntry: " + this.type + ", " + this.extension;
    }
}
