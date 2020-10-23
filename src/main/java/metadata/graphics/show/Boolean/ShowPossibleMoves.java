// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.show.Boolean;

import annotations.Hide;
import annotations.Opt;
import metadata.graphics.GraphicsItem;

@Hide
public class ShowPossibleMoves implements GraphicsItem
{
    private final boolean showPossibleMoves;
    
    public ShowPossibleMoves(@Opt final Boolean showPossibleMoves) {
        this.showPossibleMoves = (showPossibleMoves == null || showPossibleMoves);
    }
    
    public boolean showPossibleMoves() {
        return this.showPossibleMoves;
    }
}
