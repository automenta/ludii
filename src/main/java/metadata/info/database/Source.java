// 
// Decompiled by Procyon v0.5.36
// 

package metadata.info.database;

import metadata.info.InfoItem;

public class Source implements InfoItem
{
    private final String source;
    
    public Source(final String source) {
        this.source = source;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("    (source \"").append(this.source).append("\")\n");
        return sb.toString();
    }
    
    public String source() {
        return this.source;
    }
}
