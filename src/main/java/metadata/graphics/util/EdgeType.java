// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.util;

import metadata.graphics.GraphicsItem;

public enum EdgeType implements GraphicsItem
{
    All, 
    Inner, 
    Outer, 
    Interlayer;
    
    public boolean supersetOf(final EdgeType eA) {
        return this.equals(eA) || this.equals(EdgeType.All);
    }
}
