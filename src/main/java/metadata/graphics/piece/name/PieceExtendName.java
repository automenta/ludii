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
public class PieceExtendName implements GraphicsItem
{
    private final RoleType roleType;
    private final String piece;
    private final Integer state;
    private final String nameExtension;
    
    public PieceExtendName(@Opt final RoleType roleType, @Opt @Name final String piece, @Opt @Name final Integer state, final String nameExtension) {
        this.roleType = roleType;
        this.piece = piece;
        this.state = state;
        this.nameExtension = nameExtension;
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
    
    public String nameExtension() {
        return this.nameExtension;
    }
}
