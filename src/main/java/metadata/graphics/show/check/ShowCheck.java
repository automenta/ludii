// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.show.check;

import annotations.Hide;
import annotations.Opt;
import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;

@Hide
public class ShowCheck implements GraphicsItem
{
    private final RoleType roleType;
    private final String pieceName;
    
    public ShowCheck(@Opt final RoleType roleType, @Opt final String pieceName) {
        this.roleType = roleType;
        this.pieceName = pieceName;
    }
    
    public RoleType roleType() {
        return this.roleType;
    }
    
    public String pieceName() {
        return this.pieceName;
    }
}
