// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.board.shape;

import annotations.Hide;
import game.types.board.ShapeType;
import metadata.graphics.GraphicsItem;

@Hide
public class BoardShape implements GraphicsItem
{
    private final ShapeType shape;
    
    public BoardShape(final ShapeType shape) {
        this.shape = shape;
    }
    
    public ShapeType shape() {
        return this.shape;
    }
}
