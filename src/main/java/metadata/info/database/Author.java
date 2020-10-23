// 
// Decompiled by Procyon v0.5.36
// 

package metadata.info.database;

import metadata.info.InfoItem;

public class Author implements InfoItem
{
    private final String author;
    
    public Author(final String author) {
        this.author = author;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("    (author \"").append(this.author).append("\")\n");
        return sb.toString();
    }
    
    public String author() {
        return this.author;
    }
}
