// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.show.Boolean;

import annotations.Hide;
import annotations.Opt;
import metadata.graphics.GraphicsItem;

@Hide
public class ShowCost implements GraphicsItem
{
    private final boolean showCost;
    
    public ShowCost(@Opt final Boolean showCost) {
        this.showCost = (showCost == null || showCost);
    }
    
    public boolean showCost() {
        return this.showCost;
    }
}
