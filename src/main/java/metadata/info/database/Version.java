// 
// Decompiled by Procyon v0.5.36
// 

package metadata.info.database;

import metadata.info.InfoItem;

public class Version implements InfoItem
{
    private final String version;
    
    public Version(final String version) {
        this.version = version;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("    (version \"" + this.version + "\")\n");
        return sb.toString();
    }
    
    public String version() {
        return this.version;
    }
}
