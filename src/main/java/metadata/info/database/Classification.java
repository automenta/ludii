// 
// Decompiled by Procyon v0.5.36
// 

package metadata.info.database;

import metadata.info.InfoItem;

public class Classification implements InfoItem
{
    private final String classification;
    
    public Classification(final String classification) {
        this.classification = classification;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("    (classification \"" + this.classification + "\")\n");
        return sb.toString();
    }
    
    public String classification() {
        return this.classification;
    }
}
