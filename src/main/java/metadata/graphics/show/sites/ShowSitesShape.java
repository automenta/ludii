// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.show.sites;

import annotations.Hide;
import game.types.board.ShapeType;
import metadata.graphics.GraphicsItem;

@Hide
public class ShowSitesShape implements GraphicsItem
{
    private final ShapeType shape;
    
    public ShowSitesShape(final ShapeType shape) {
        this.shape = shape;
    }
    
    public ShapeType shape() {
        return this.shape;
    }
}
