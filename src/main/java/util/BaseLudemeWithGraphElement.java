// 
// Decompiled by Procyon v0.5.36
// 

package util;

import game.Game;
import game.types.board.SiteType;

public class BaseLudemeWithGraphElement extends BaseLudeme
{
    private SiteType type;
    
    public BaseLudemeWithGraphElement() {
        this.type = null;
    }
    
    public SiteType graphElementType() {
        return this.type;
    }
    
    public void setGraphElementType(final SiteType preferred, final Game game) {
        if (preferred == null) {
            this.type = game.board().defaultSite();
        }
        else {
            this.type = preferred;
        }
    }
}
