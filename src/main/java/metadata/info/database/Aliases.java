// 
// Decompiled by Procyon v0.5.36
// 

package metadata.info.database;

import metadata.info.InfoItem;

public class Aliases implements InfoItem
{
    private final String[] aliases;
    
    public Aliases(final String[] aliases) {
        this.aliases = aliases;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("    (aliases \"" + this.aliases + "\")\n");
        return sb.toString();
    }
    
    public String[] aliases() {
        return this.aliases;
    }
}
