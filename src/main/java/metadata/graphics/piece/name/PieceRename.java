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
public class PieceRename implements GraphicsItem
{
    private final RoleType roleType;
    private final String piece;
    private final String nameReplacement;
    private final Integer state;
    
    public PieceRename(@Opt final RoleType roleType, @Opt @Name final String piece, @Opt @Name final Integer state, final String nameReplacement) {
        this.roleType = roleType;
        this.piece = piece;
        this.state = state;
        this.nameReplacement = nameReplacement;
    }
    
    public RoleType roleType() {
        return this.roleType;
    }
    
    public Integer state() {
        return this.state;
    }
    
    public String piece() {
        return this.piece;
    }
    
    public String nameReplacement() {
        return this.nameReplacement;
    }
}
