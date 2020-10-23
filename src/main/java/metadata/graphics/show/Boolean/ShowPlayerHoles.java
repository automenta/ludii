// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.show.Boolean;

import annotations.Hide;
import annotations.Opt;
import metadata.graphics.GraphicsItem;

@Hide
public class ShowPlayerHoles implements GraphicsItem
{
    private final boolean showPlayerHoles;
    
    public ShowPlayerHoles(@Opt final Boolean showPlayerHoles) {
        this.showPlayerHoles = (showPlayerHoles == null || showPlayerHoles);
    }
    
    public boolean showPlayerHoles() {
        return this.showPlayerHoles;
    }
}
