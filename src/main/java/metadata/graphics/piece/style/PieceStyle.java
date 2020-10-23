// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.piece.style;

import annotations.Hide;
import annotations.Opt;
import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;
import metadata.graphics.util.ComponentStyleType;

@Hide
public class PieceStyle implements GraphicsItem
{
    private final RoleType roleType;
    private final String pieceName;
    private final ComponentStyleType componentStyleType;
    
    public PieceStyle(@Opt final RoleType roleType, @Opt final String pieceName, final ComponentStyleType componentStyleType) {
        this.roleType = roleType;
        this.pieceName = pieceName;
        this.componentStyleType = componentStyleType;
    }
    
    public RoleType roleType() {
        return this.roleType;
    }
    
    public String pieceName() {
        return this.pieceName;
    }
    
    public ComponentStyleType componentStyleType() {
        return this.componentStyleType;
    }
}
