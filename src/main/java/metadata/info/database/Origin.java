// 
// Decompiled by Procyon v0.5.36
// 

package metadata.info.database;

import metadata.info.InfoItem;

public class Origin implements InfoItem
{
    private final String origin;
    
    public Origin(final String origin) {
        this.origin = origin;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("    (origin \"").append(this.origin).append("\")\n");
        return sb.toString();
    }
    
    public String origin() {
        return this.origin;
    }
}
