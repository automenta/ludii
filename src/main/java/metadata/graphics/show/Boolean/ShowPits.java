// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.show.Boolean;

import annotations.Hide;
import annotations.Opt;
import metadata.graphics.GraphicsItem;

@Hide
public class ShowPits implements GraphicsItem
{
    private final boolean showPits;
    
    public ShowPits(@Opt final Boolean showPits) {
        this.showPits = (showPits == null || showPits);
    }
    
    public boolean showPits() {
        return this.showPits;
    }
}
