// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.util;

import metadata.MetadataItem;

public enum ComponentStyleType implements MetadataItem
{
    Piece, 
    Tile, 
    Card, 
    Die, 
    Domino, 
    LargePiece, 
    ExtendedShogi, 
    ExtendedXiangqi, 
    NativeAmericanDice;
    
    public static ComponentStyleType fromName(final String name) {
        try {
            return valueOf(name);
        }
        catch (Exception e) {
            return ComponentStyleType.Piece;
        }
    }
}
