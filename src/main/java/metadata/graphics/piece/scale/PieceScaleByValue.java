// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.piece.scale;

import annotations.Hide;
import annotations.Opt;
import metadata.graphics.GraphicsItem;

@Hide
public class PieceScaleByValue implements GraphicsItem
{
    private final boolean scalePiecesByValue;
    
    public PieceScaleByValue(@Opt final Boolean scalePiecesByValue) {
        this.scalePiecesByValue = (scalePiecesByValue == null || scalePiecesByValue);
    }
    
    public boolean scalePiecesByValue() {
        return this.scalePiecesByValue;
    }
}
