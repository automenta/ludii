// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.util;

import metadata.MetadataItem;

public enum BoardGraphicsType implements MetadataItem
{
    InnerEdges(0), 
    OuterEdges(1), 
    Phase0(2), 
    Phase1(3), 
    Phase2(4), 
    Phase3(5), 
    Symbols(6), 
    Vertices(7), 
    OuterVertices(8);
    
    private final int value;
    
    BoardGraphicsType(final int value) {
        this.value = value;
    }
    
    public int value() {
        return this.value;
    }
    
    public static BoardGraphicsType getTypeFromValue(final int value) {
        for (final BoardGraphicsType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
