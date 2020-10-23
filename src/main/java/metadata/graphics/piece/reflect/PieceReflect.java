// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.piece.reflect;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;

@Hide
public class PieceReflect implements GraphicsItem
{
    private final RoleType roleType;
    private final String pieceName;
    private final Boolean vertical;
    private final Boolean horizontal;
    
    public PieceReflect(@Opt final RoleType roleType, @Opt final String pieceName, @Opt @Name final Boolean vertical, @Opt @Name final Boolean horizontal) {
        this.roleType = roleType;
        this.pieceName = pieceName;
        this.vertical = vertical;
        this.horizontal = horizontal;
    }
    
    public RoleType roleType() {
        return this.roleType;
    }
    
    public String pieceName() {
        return this.pieceName;
    }
    
    public Boolean vertical() {
        return this.vertical;
    }
    
    public Boolean horizontal() {
        return this.horizontal;
    }
}
