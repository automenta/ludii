// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.piece.rotate;

import annotations.Hide;
import annotations.Name;
import annotations.Opt;
import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;

@Hide
public class PieceRotate implements GraphicsItem
{
    private final RoleType roleType;
    private final String pieceName;
    private final int degrees;
    
    public PieceRotate(@Opt final RoleType roleType, @Opt final String pieceName, @Name final Integer degrees) {
        this.roleType = roleType;
        this.pieceName = pieceName;
        this.degrees = degrees;
    }
    
    public RoleType roleType() {
        return this.roleType;
    }
    
    public String pieceName() {
        return this.pieceName;
    }
    
    public int degrees() {
        return this.degrees;
    }
}
