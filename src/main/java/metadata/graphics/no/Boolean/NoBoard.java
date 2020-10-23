// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.no.Boolean;

import annotations.Hide;
import annotations.Opt;
import metadata.graphics.GraphicsItem;

@Hide
public class NoBoard implements GraphicsItem
{
    private final boolean boardHidden;
    
    public NoBoard(@Opt final Boolean boardHidden) {
        this.boardHidden = (boardHidden == null || boardHidden);
    }
    
    public boolean boardHidden() {
        return this.boardHidden;
    }
}
