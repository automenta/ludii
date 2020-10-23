// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.board.colour;

import annotations.Hide;
import metadata.graphics.GraphicsItem;
import metadata.graphics.util.BoardGraphicsType;
import metadata.graphics.util.colour.Colour;

@Hide
public class BoardColour implements GraphicsItem
{
    private final BoardGraphicsType boardGraphicsType;
    private final Colour colour;
    
    public BoardColour(final BoardGraphicsType boardGraphicsType, final Colour colour) {
        this.boardGraphicsType = boardGraphicsType;
        this.colour = colour;
    }
    
    public BoardGraphicsType boardGraphicsType() {
        return this.boardGraphicsType;
    }
    
    public Colour colour() {
        return this.colour;
    }
}
