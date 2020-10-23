// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.misc;

import main.StringRoutines;
import metadata.MetadataItem;

public class Pair implements MetadataItem
{
    protected final String key;
    protected final float floatVal;
    
    public Pair(final String key, final Float floatVal) {
        this.key = key;
        this.floatVal = floatVal;
    }
    
    public final String key() {
        return this.key;
    }
    
    public final float floatVal() {
        return this.floatVal;
    }
    
    @Override
    public String toString() {
        return "(pair " + StringRoutines.quote(this.key) + " " + this.floatVal + ")";
    }
}
