// 
// Decompiled by Procyon v0.5.36
// 

package metadata.info.database;

import metadata.info.InfoItem;

public class Publisher implements InfoItem
{
    private final String publisher;
    
    public Publisher(final String publisher) {
        this.publisher = publisher;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("    (publisher \"" + this.publisher + "\")\n");
        return sb.toString();
    }
    
    public String publisher() {
        return this.publisher;
    }
}
