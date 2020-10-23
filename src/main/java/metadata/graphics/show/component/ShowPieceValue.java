// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.show.component;

import annotations.Hide;
import annotations.Opt;
import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;
import metadata.graphics.util.ValueLocationType;

@Hide
public class ShowPieceValue implements GraphicsItem
{
    private final RoleType roleType;
    private final String pieceName;
    private final ValueLocationType location;
    
    public ShowPieceValue(@Opt final RoleType roleType, @Opt final String pieceName, @Opt final ValueLocationType location) {
        this.roleType = roleType;
        this.pieceName = pieceName;
        this.location = ((location == null) ? ValueLocationType.Corner : location);
    }
    
    public RoleType roleType() {
        return this.roleType;
    }
    
    public String pieceName() {
        return this.pieceName;
    }
    
    public ValueLocationType location() {
        return this.location;
    }
}
