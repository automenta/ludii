// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.show.Boolean;

import annotations.Hide;
import annotations.Opt;
import metadata.graphics.GraphicsItem;

@Hide
public class ShowEdgeDirections implements GraphicsItem
{
    private final boolean showEdgeDirections;
    
    public ShowEdgeDirections(@Opt final Boolean showEdgeDirections) {
        this.showEdgeDirections = (showEdgeDirections == null || showEdgeDirections);
    }
    
    public boolean showEdgeDirections() {
        return this.showEdgeDirections;
    }
}
