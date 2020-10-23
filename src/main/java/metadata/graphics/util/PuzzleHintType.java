// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.util;

import metadata.graphics.GraphicsItem;

public enum PuzzleHintType implements GraphicsItem
{
    Default, 
    None, 
    TopLeft;
    
    public PuzzleHintType getTypeFromValue(final int value) {
        for (final PuzzleHintType type : values()) {
            if (this.ordinal() == value) {
                return type;
            }
        }
        return null;
    }
}
