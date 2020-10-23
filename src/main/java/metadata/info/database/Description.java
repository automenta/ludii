// 
// Decompiled by Procyon v0.5.36
// 

package metadata.info.database;

import metadata.info.InfoItem;

public class Description implements InfoItem
{
    private final String description;
    
    public Description(final String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("    (description \"" + this.description + "\")\n");
        return sb.toString();
    }
    
    public String description() {
        return this.description;
    }
}
