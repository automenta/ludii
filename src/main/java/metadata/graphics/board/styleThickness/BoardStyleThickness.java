// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.board.styleThickness;

import annotations.Hide;
import metadata.graphics.GraphicsItem;
import metadata.graphics.util.BoardGraphicsType;

@Hide
public class BoardStyleThickness implements GraphicsItem
{
    private final BoardGraphicsType boardGraphicsType;
    private final float thickness;
    
    public BoardStyleThickness(final BoardGraphicsType boardGraphicsType, final Float thickness) {
        this.boardGraphicsType = boardGraphicsType;
        this.thickness = thickness;
    }
    
    public BoardGraphicsType boardGraphicsType() {
        return this.boardGraphicsType;
    }
    
    public float thickness() {
        return this.thickness;
    }
}
