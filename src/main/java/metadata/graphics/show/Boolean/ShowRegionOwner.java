// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.show.Boolean;

import annotations.Hide;
import annotations.Opt;
import metadata.graphics.GraphicsItem;

@Hide
public class ShowRegionOwner implements GraphicsItem
{
    private final boolean show;
    
    public ShowRegionOwner(@Opt final Boolean show) {
        this.show = (show == null || show);
    }
    
    public boolean show() {
        return this.show;
    }
}
