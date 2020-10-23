// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.util;

import metadata.graphics.GraphicsItem;

public enum PieceStackType implements GraphicsItem
{
    Default, 
    Ground, 
    Reverse, 
    Fan, 
    FanAlternating, 
    None, 
    Backgammon, 
    Count, 
    Ring;
    
    public PieceStackType getTypeFromValue(final int value) {
        for (final PieceStackType type : values()) {
            if (this.ordinal() == value) {
                return type;
            }
        }
        return null;
    }
    
    public boolean midStackSelectionValid() {
        return this == PieceStackType.Ground || this == PieceStackType.Ring || this == PieceStackType.Fan || this == PieceStackType.Backgammon;
    }
}
