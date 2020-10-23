// 
// Decompiled by Procyon v0.5.36
// 

package metadata.info.database;

import metadata.info.InfoItem;

public class Credit implements InfoItem
{
    private final String credit;
    
    public Credit(final String credit) {
        this.credit = credit;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("    (credit \"" + this.credit + "\")\n");
        return sb.toString();
    }
    
    public String credit() {
        return this.credit;
    }
}
