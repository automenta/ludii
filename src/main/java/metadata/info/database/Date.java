// 
// Decompiled by Procyon v0.5.36
// 

package metadata.info.database;

import metadata.info.InfoItem;

public class Date implements InfoItem
{
    private final String date;
    
    public Date(final String date) {
        this.date = date;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("    (date \"").append(this.date).append("\")\n");
        return sb.toString();
    }
    
    public String date() {
        return this.date;
    }
}
