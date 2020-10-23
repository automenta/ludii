// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.css.sac;

import java.io.InputStream;
import java.io.Reader;

public class InputSource
{
    private String uri;
    private InputStream byteStream;
    private String encoding;
    private Reader characterStream;
    private String title;
    private String media;
    
    public InputSource() {
    }
    
    public InputSource(final String uri) {
        this.setURI(uri);
    }
    
    public InputSource(final Reader characterStream) {
        this.setCharacterStream(characterStream);
    }
    
    public void setURI(final String uri) {
        this.uri = uri;
    }
    
    public String getURI() {
        return this.uri;
    }
    
    public void setByteStream(final InputStream byteStream) {
        this.byteStream = byteStream;
    }
    
    public InputStream getByteStream() {
        return this.byteStream;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public void setCharacterStream(final Reader characterStream) {
        this.characterStream = characterStream;
    }
    
    public Reader getCharacterStream() {
        return this.characterStream;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setMedia(final String media) {
        this.media = media;
    }
    
    public String getMedia() {
        if (this.media == null) {
            return "all";
        }
        return this.media;
    }
}
