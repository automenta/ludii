// 
// Decompiled by Procyon v0.5.36
// 

package metadata.info.database;

import metadata.info.InfoItem;

public class Rules implements InfoItem
{
    private final String rules;
    
    public Rules(final String rules) {
        this.rules = rules;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("    (rules \"" + this.rules + "\")\n");
        return sb.toString();
    }
    
    public String rules() {
        return this.rules;
    }
}
