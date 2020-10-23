// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.piece.name;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;

@Hide
public class PieceAddStateToName implements GraphicsItem
{
    private final RoleType roleType;
    private final String piece;
    private final Integer state;
    
    public PieceAddStateToName(@Opt final RoleType roleType, @Opt @Name final String piece, @Opt @Name final Integer state) {
        this.roleType = roleType;
        this.piece = piece;
        this.state = state;
    }
    
    public Integer state() {
        return this.state;
    }
    
    public RoleType roleType() {
        return this.roleType;
    }
    
    public String piece() {
        return this.piece;
    }
}
