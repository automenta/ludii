// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine;

import org.apache.batik.util.ParsedURL;

public class ImportRule extends MediaRule
{
    public static final short TYPE = 2;
    protected ParsedURL uri;
    
    @Override
    public short getType() {
        return 2;
    }
    
    public void setURI(final ParsedURL u) {
        this.uri = u;
    }
    
    public ParsedURL getURI() {
        return this.uri;
    }
    
    @Override
    public String toString(final CSSEngine eng) {
        final StringBuffer sb = new StringBuffer();
        sb.append("@import \"");
        sb.append(this.uri);
        sb.append("\"");
        if (this.mediaList != null) {
            for (int i = 0; i < this.mediaList.getLength(); ++i) {
                sb.append(' ');
                sb.append(this.mediaList.item(i));
            }
        }
        sb.append(";\n");
        return sb.toString();
    }
}
